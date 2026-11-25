package com.hospivet.auth.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hospivet.auth.dto.RecuperacionResponse;
import com.hospivet.auth.mapper.UsuarioMapper;
import com.hospivet.auth.model.Usuario;
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

/**
 * 3.1.2 Recuperar contraseña (R1.2, SD-02). Decisiones del plan (D-20): identificación por usuario o correo
 * de un veterinario activo (N-01), contraseña temporal sin vencimiento (N-02) y, si el correo no sale,
 * la contraseña anterior se conserva (N-03).
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
    private static final int LONGITUD = 10;

    private final UsuarioMapper usuarioMapper;
    private final VeterinarioMapper veterinarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<JavaMailSender> mailSender;
    private final String remitente;
    private final SecureRandom random = new SecureRandom();

    public RecuperacionService(UsuarioMapper usuarioMapper, VeterinarioMapper veterinarioMapper,
                               PasswordEncoder passwordEncoder, ObjectProvider<JavaMailSender> mailSender,
                               @Value("${hospivet.mail.remitente:}") String remitente) {
        this.usuarioMapper = usuarioMapper;
        this.veterinarioMapper = veterinarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.remitente = remitente;
    }

    /**
     * SD-02 recuperarContrasena(identidad). El nuevo hash y el envío del correo van en una sola transacción:
     * si el envío falla, la transacción se deshace y no se modifican las credenciales.
     */
    @Transactional
    public RecuperacionResponse recuperarContrasena(String identidad) {
        Usuario usuario = buscarPorIdentidad(identidad);
        if (usuario == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, USUARIO_NO_REGISTRADO,
                    java.util.Map.of("identidad", USUARIO_NO_REGISTRADO));
        }
        Veterinario veterinario = veterinarioMapper.selectById(usuario.getIdUsuario());
        JavaMailSender sender = mailSender.getIfAvailable();
        if (veterinario == null || !StringUtils.hasText(veterinario.getCorreo())
                || sender == null || !StringUtils.hasText(remitente)) {
            log.warn("Recuperación no completada para {}: falta el correo del veterinario o el servidor SMTP",
                    usuario.getNombreUsuario());
            throw new BusinessException(HttpStatus.BAD_GATEWAY, NO_COMPLETADA);
        }

        String temporal = generarTemporal();
        usuario.setClaveHash(passwordEncoder.encode(temporal));
        usuarioMapper.updateById(usuario);
        enviar(sender, veterinario, usuario.getNombreUsuario(), temporal);
        return new RecuperacionResponse(RECUPERACION_SOLICITADA);
    }

    /** N-01 A: nombre de usuario exacto o correo del veterinario sin distinguir mayúsculas; solo veterinarios activos. */
    private Usuario buscarPorIdentidad(String identidad) {
        if (!StringUtils.hasText(identidad)) {
            return null;
        }
        String valor = identidad.trim();
        Usuario usuario = usuarioMapper.selectOne(Wrappers.<Usuario>lambdaQuery()
                .eq(Usuario::getNombreUsuario, valor));
        if (usuario == null) {
            Veterinario veterinario = veterinarioMapper.selectOne(Wrappers.<Veterinario>lambdaQuery()
                    .apply("LOWER(correo) = LOWER({0})", valor));
            usuario = veterinario == null ? null : usuarioMapper.selectById(veterinario.getIdVeterinario());
        }
        boolean valido = usuario != null && Boolean.TRUE.equals(usuario.getActivo())
                && "VETERINARIO".equals(usuario.getRol());
        return valido ? usuario : null;
    }

    private String generarTemporal() {
        StringBuilder texto = new StringBuilder(LONGITUD);
        for (int i = 0; i < LONGITUD; i++) {
            texto.append(ALFABETO.charAt(random.nextInt(ALFABETO.length())));
        }
        return texto.toString();
    }

    private void enviar(JavaMailSender sender, Veterinario veterinario, String nombreUsuario, String temporal) {
        try {
            MimeMessage mensaje = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(veterinario.getCorreo());
            helper.setSubject("HOSPIVET · Contraseña temporal");
            helper.setText("""
                    Hola, %s %s:

                    Se solicitó recuperar la contraseña de HOSPIVET.

                    Usuario: %s
                    Contraseña temporal: %s

                    Use esta contraseña para iniciar sesión. Reemplaza a la contraseña anterior.
                    """.formatted(veterinario.getNombre(), veterinario.getApellido(), nombreUsuario, temporal), false);
            sender.send(mensaje);
        } catch (MessagingException | MailException exception) {
            log.warn("No se pudo enviar la contraseña temporal de {}", nombreUsuario, exception);
            throw new BusinessException(HttpStatus.BAD_GATEWAY, NO_COMPLETADA);
        }
    }
}
