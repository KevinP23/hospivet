package com.hospivet.auth.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hospivet.auth.dto.RecuperacionResponse;
import com.hospivet.auth.mapper.UsuarioMapper;
import com.hospivet.auth.model.Usuario;
import com.hospivet.cliente.mapper.ClienteMapper;
import com.hospivet.cliente.model.Cliente;
import com.hospivet.common.exception.BusinessException;
import com.hospivet.veterinario.mapper.VeterinarioMapper;
import com.hospivet.veterinario.model.Veterinario;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 3.1.2 Recuperar contraseña (R1.2, SD-02). Decisiones del plan (D-20): identificación por usuario o correo
 * (N-01), contraseña temporal sin vencimiento (N-02) y, si el correo no sale, nada cambia (N-03).
 * D-21 (R10, N-01 A): también atiende a clientes (por usuario, CI o correo); si no tienen cuenta, se crea aquí
 * con un usuario de nombre + inicial del apellido + dígitos del CI (por ejemplo, "mariav4562").
 */
@Service
public class RecuperacionService {

    public static final String USUARIO_NO_REGISTRADO = "Usuario no registrado";
    public static final String RECUPERACION_SOLICITADA = "Recuperación solicitada";
    /** N-03 A: la misma alerta general que la interfaz muestra ante cualquier error del servidor. */
    public static final String NO_COMPLETADA = "No fue posible completar la operación";

    private static final Logger log = LoggerFactory.getLogger(RecuperacionService.class);
    /** Sin 0/O ni 1/l/I para que la contraseña se lea sin confusiones en el correo. */
    private static final String ALFABETO = "ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
    /** Observación del responsable (2026-11-25): 8 caracteres, más fácil de copiar que los 10 iniciales. */
    private static final int LONGITUD = 8;

    private final UsuarioMapper usuarioMapper;
    private final VeterinarioMapper veterinarioMapper;
    private final ClienteMapper clienteMapper;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<JavaMailSender> mailSender;
    private final String remitente;
    private final SecureRandom random = new SecureRandom();

    public RecuperacionService(UsuarioMapper usuarioMapper, VeterinarioMapper veterinarioMapper,
                               ClienteMapper clienteMapper, PasswordEncoder passwordEncoder,
                               ObjectProvider<JavaMailSender> mailSender,
                               @Value("${hospivet.mail.remitente:}") String remitente) {
        this.usuarioMapper = usuarioMapper;
        this.veterinarioMapper = veterinarioMapper;
        this.clienteMapper = clienteMapper;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    /**
     * SD-02 recuperarContrasena(identidad). La cuenta del cliente (si se crea), el nuevo hash y el envío del
     * correo van en una sola transacción: si el envío falla, todo se deshace y no se modifican las credenciales.
     */
    @Transactional
    public RecuperacionResponse recuperarContrasena(String identidad) {
        Destinatario destinatario = buscarPorIdentidad(identidad);
        if (destinatario == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, USUARIO_NO_REGISTRADO,
                    Map.of("identidad", USUARIO_NO_REGISTRADO));
        }
        JavaMailSender sender = mailSender.getIfAvailable();
        if (!StringUtils.hasText(destinatario.correo()) || sender == null || !StringUtils.hasText(remitente)) {
            log.warn("Recuperación no completada para {}: falta el correo registrado o el servidor SMTP",
                    destinatario.nombreUsuario());
            throw new BusinessException(HttpStatus.BAD_GATEWAY, NO_COMPLETADA);
        }

        String temporal = generarTemporal();
        Usuario usuario = destinatario.cliente() == null ? destinatario.usuario()
                : cuentaDelCliente(destinatario.cliente(), destinatario.usuario());
        usuario.setClaveHash(passwordEncoder.encode(temporal));
        usuarioMapper.updateById(usuario);
        enviar(sender, destinatario, usuario.getNombreUsuario(), temporal);
        return new RecuperacionResponse(RECUPERACION_SOLICITADA);
    }

    /**
     * N-01 A de R1.2: nombre de usuario exacto o correo del veterinario sin distinguir mayúsculas.
     * N-01 A de R10: usuario del cliente, CI exacto o correo del cliente activo.
     */
    private Destinatario buscarPorIdentidad(String identidad) {
        if (!StringUtils.hasText(identidad)) {
            return null;
        }
        String valor = identidad.trim();

        Usuario usuario = usuarioMapper.selectOne(Wrappers.<Usuario>lambdaQuery()
                .eq(Usuario::getNombreUsuario, valor));
        if (usuario == null) {
            Veterinario porCorreo = veterinarioMapper.selectOne(Wrappers.<Veterinario>lambdaQuery()
                    .apply("LOWER(correo) = LOWER({0})", valor));
            usuario = porCorreo == null ? null : usuarioMapper.selectById(porCorreo.getIdVeterinario());
        }
        if (usuario != null && Boolean.TRUE.equals(usuario.getActivo()) && "VETERINARIO".equals(usuario.getRol())) {
            Veterinario veterinario = veterinarioMapper.selectById(usuario.getIdUsuario());
            return veterinario == null ? null : new Destinatario(usuario, null, veterinario.getNombre(),
                    veterinario.getApellido(), veterinario.getCorreo(), usuario.getNombreUsuario());
        }
        if (usuario != null && Boolean.TRUE.equals(usuario.getActivo()) && "CLIENTE".equals(usuario.getRol())) {
            Cliente propio = clienteMapper.selectOne(Wrappers.<Cliente>lambdaQuery()
                    .eq(Cliente::getIdUsuario, usuario.getIdUsuario())
                    .eq(Cliente::getActivo, true));
            return propio == null ? null : new Destinatario(usuario, propio, propio.getNombre(),
                    propio.getApellido(), propio.getCorreo(), usuario.getNombreUsuario());
        }

        Cliente cliente = clienteMapper.selectOne(Wrappers.<Cliente>lambdaQuery()
                .eq(Cliente::getActivo, true)
                .and(condicion -> condicion.eq(Cliente::getCi, valor)
                        .or().apply("LOWER(correo) = LOWER({0})", valor))
                .last("LIMIT 1"));
        if (cliente == null) {
            return null;
        }
        Usuario cuenta = cliente.getIdUsuario() == null ? null : usuarioMapper.selectById(cliente.getIdUsuario());
        if (cuenta != null && !Boolean.TRUE.equals(cuenta.getActivo())) {
            return null;
        }
        return new Destinatario(cuenta, cliente, cliente.getNombre(), cliente.getApellido(), cliente.getCorreo(),
                cuenta == null ? cliente.getCi() : cuenta.getNombreUsuario());
    }

    /** D-21 (ajuste del 2026-11-25): usuario nombre + inicial del apellido + dígitos del CI, vinculado en cliente.id_usuario. */
    private Usuario cuentaDelCliente(Cliente cliente, Usuario existente) {
        if (existente != null) {
            return existente;
        }
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(nombreUsuarioLibre(cliente));
        usuario.setClaveHash(passwordEncoder.encode(generarTemporal()));
        usuario.setRol("CLIENTE");
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuarioMapper.insert(usuario);
        cliente.setIdUsuario(usuario.getIdUsuario());
        clienteMapper.updateById(cliente);
        return usuario;
    }

    /**
     * Usuario del cliente (pedido del responsable, 2026-11-25): primer nombre + inicial del primer apellido +
     * últimos 4 dígitos del CI, en minúsculas y sin tildes. "María Elena Vargas Ríos", CI 7134562 → "mariav4562".
     * Con el CI completo se arma la alternativa para cuando el usuario corto ya existe ("mariav7134562").
     */
    static String nombreUsuarioCorto(Cliente cliente) {
        String ci = cliente.getCi().replaceAll("[^0-9]", "");
        return prefijo(cliente) + (ci.length() <= 4 ? ci : ci.substring(ci.length() - 4));
    }

    static String nombreUsuarioLargo(Cliente cliente) {
        return prefijo(cliente) + cliente.getCi().replaceAll("[^0-9]", "");
    }

    private static String prefijo(Cliente cliente) {
        String nombre = primeraPalabra(cliente.getNombre());
        String apellido = primeraPalabra(cliente.getApellido());
        String texto = nombre + (apellido.isEmpty() ? "" : apellido.substring(0, 1));
        return texto.length() <= 30 ? texto : texto.substring(0, 30);
    }

    private static String primeraPalabra(String texto) {
        if (!StringUtils.hasText(texto)) {
            return "";
        }
        String sinTildes = java.text.Normalizer.normalize(texto.trim(), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(java.util.Locale.ROOT);
        return sinTildes.split("\\s+")[0].replaceAll("[^a-z0-9]", "");
    }

    private String nombreUsuarioLibre(Cliente cliente) {
        String corto = nombreUsuarioCorto(cliente);
        if (!existeUsuario(corto)) {
            return corto;
        }
        String largo = nombreUsuarioLargo(cliente);
        String candidato = largo;
        for (int numero = 2; existeUsuario(candidato); numero++) {
            candidato = largo + numero;
        }
        return candidato;
    }

    private boolean existeUsuario(String nombreUsuario) {
        return usuarioMapper.selectCount(Wrappers.<Usuario>lambdaQuery()
                .eq(Usuario::getNombreUsuario, nombreUsuario)) > 0;
    }

    private String generarTemporal() {
        StringBuilder texto = new StringBuilder(LONGITUD);
        for (int i = 0; i < LONGITUD; i++) {
            texto.append(ALFABETO.charAt(random.nextInt(ALFABETO.length())));
        }
        return texto.toString();
    }

    private void enviar(JavaMailSender sender, Destinatario destinatario, String nombreUsuario, String temporal) {
        try {
            MimeMessage mensaje = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(destinatario.correo());
            helper.setSubject("HOSPIVET · Contraseña temporal");
            helper.setText("""
                    Hola, %s %s:

                    Se solicitó recuperar la contraseña de HOSPIVET.

                    Usuario: %s
                    Contraseña temporal: %s

                    Use esta contraseña para iniciar sesión. Reemplaza a la contraseña anterior.
                    """.formatted(destinatario.nombre(), destinatario.apellido(), nombreUsuario, temporal), false);
            sender.send(mensaje);
        } catch (MessagingException | MailException exception) {
            log.warn("No se pudo enviar la contraseña temporal de {}", nombreUsuario, exception);
            throw new BusinessException(HttpStatus.BAD_GATEWAY, NO_COMPLETADA);
        }
    }

    /** Cuenta (puede no existir aún si es un cliente), datos para el saludo y correo de destino. */
    private record Destinatario(Usuario usuario, Cliente cliente, String nombre, String apellido, String correo,
                                String nombreUsuario) {
    }
}
