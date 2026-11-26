package com.hospivet.auth.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hospivet.auth.dto.AuthResponse;
import com.hospivet.auth.dto.LoginRequest;
import com.hospivet.auth.mapper.UsuarioMapper;
import com.hospivet.auth.model.Usuario;
import com.hospivet.cliente.mapper.ClienteMapper;
import com.hospivet.cliente.model.Cliente;
import com.hospivet.common.exception.BusinessException;
import com.hospivet.veterinario.mapper.VeterinarioMapper;
import com.hospivet.veterinario.model.Veterinario;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UsuarioMapper usuarioMapper;
    private final VeterinarioMapper veterinarioMapper;
    private final ClienteMapper clienteMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioMapper usuarioMapper, VeterinarioMapper veterinarioMapper,
                       ClienteMapper clienteMapper, PasswordEncoder passwordEncoder) {
        this.usuarioMapper = usuarioMapper;
        this.veterinarioMapper = veterinarioMapper;
        this.clienteMapper = clienteMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthenticatedUser autenticar(LoginRequest request) {
        Usuario usuario = buscarUsuario(request.usuario());

        if (usuario == null || !Boolean.TRUE.equals(usuario.getActivo())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado");
        }
        if (!passwordEncoder.matches(request.contrasena(), usuario.getClaveHash())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Contraseña incorrecta");
        }
        // D-21 (reemplaza D-04): entran el veterinario y el cliente con su ficha activa (Portal Cliente, R10).
        boolean veterinario = "VETERINARIO".equals(usuario.getRol());
        boolean cliente = "CLIENTE".equals(usuario.getRol()) && clienteActivoDe(usuario) != null;
        if (!veterinario && !cliente) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Acceso no autorizado");
        }

        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                usuario.getNombreUsuario(), null,
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol())));
        return new AuthenticatedUser(authentication, respuesta(usuario));
    }

    /** Datos de la sesión activa; la cabecera muestra nombre y apellido (COR-02 N-02 y Portal Cliente). */
    public AuthResponse sesion(String nombreUsuario) {
        Usuario usuario = buscarUsuario(nombreUsuario);
        if (usuario == null) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado");
        }
        return respuesta(usuario);
    }

    private Usuario buscarUsuario(String nombreUsuario) {
        return usuarioMapper.selectOne(Wrappers.<Usuario>lambdaQuery()
                .eq(Usuario::getNombreUsuario, nombreUsuario));
    }

    private Cliente clienteActivoDe(Usuario usuario) {
        return clienteMapper.selectOne(Wrappers.<Cliente>lambdaQuery()
                .eq(Cliente::getIdUsuario, usuario.getIdUsuario())
                .eq(Cliente::getActivo, true));
    }

    private AuthResponse respuesta(Usuario usuario) {
        if ("CLIENTE".equals(usuario.getRol())) {
            Cliente cliente = clienteActivoDe(usuario);
            return new AuthResponse(usuario.getIdUsuario(), usuario.getNombreUsuario(), usuario.getRol(),
                    cliente == null ? null : cliente.getNombre(),
                    cliente == null ? null : cliente.getApellido());
        }
        Veterinario veterinario = veterinarioMapper.selectById(usuario.getIdUsuario());
        return new AuthResponse(usuario.getIdUsuario(), usuario.getNombreUsuario(), usuario.getRol(),
                veterinario == null ? null : veterinario.getNombre(),
                veterinario == null ? null : veterinario.getApellido());
    }

    public record AuthenticatedUser(Authentication authentication, AuthResponse response) {
    }
}
