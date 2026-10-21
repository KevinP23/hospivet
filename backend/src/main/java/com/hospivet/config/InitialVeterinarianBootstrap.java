package com.hospivet.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hospivet.auth.mapper.UsuarioMapper;
import com.hospivet.auth.model.Usuario;
import com.hospivet.veterinario.mapper.VeterinarioMapper;
import com.hospivet.veterinario.model.Veterinario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class InitialVeterinarianBootstrap implements ApplicationRunner {

    private final UsuarioMapper usuarioMapper;
    private final VeterinarioMapper veterinarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;
    private final String ci;
    private final String nombre;
    private final String apellido;
    private final String telefono;
    private final String correo;

    public InitialVeterinarianBootstrap(
            UsuarioMapper usuarioMapper,
            VeterinarioMapper veterinarioMapper,
            PasswordEncoder passwordEncoder,
            @Value("${hospivet.bootstrap.username:}") String username,
            @Value("${hospivet.bootstrap.password:}") String password,
            @Value("${hospivet.bootstrap.ci:}") String ci,
            @Value("${hospivet.bootstrap.nombre:}") String nombre,
            @Value("${hospivet.bootstrap.apellido:}") String apellido,
            @Value("${hospivet.bootstrap.telefono:}") String telefono,
            @Value("${hospivet.bootstrap.correo:}") String correo) {
        this.usuarioMapper = usuarioMapper;
        this.veterinarioMapper = veterinarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
        this.ci = ci;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<String> required = List.of(username, password, ci, nombre, apellido);
        boolean anyProvided = required.stream().anyMatch(StringUtils::hasText);
        if (!anyProvided) {
            return;
        }
        if (required.stream().anyMatch(value -> !StringUtils.hasText(value))) {
            throw new IllegalStateException(
                    "La cuenta inicial requiere usuario, contraseña, CI, nombre y apellido");
        }

        Long existing = usuarioMapper.selectCount(Wrappers.<Usuario>lambdaQuery()
                .eq(Usuario::getNombreUsuario, username));
        if (existing > 0) {
            return;
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(username);
        usuario.setClaveHash(passwordEncoder.encode(password));
        usuario.setRol("VETERINARIO");
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuarioMapper.insert(usuario);

        Veterinario veterinario = new Veterinario();
        veterinario.setIdVeterinario(usuario.getIdUsuario());
        veterinario.setCi(ci);
        veterinario.setNombre(nombre);
        veterinario.setApellido(apellido);
        veterinario.setTelefono(blankToNull(telefono));
        veterinario.setCorreo(blankToNull(correo));
        veterinarioMapper.insert(veterinario);
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}

