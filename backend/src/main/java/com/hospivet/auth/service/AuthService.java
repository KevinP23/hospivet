package com.hospivet.auth.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hospivet.auth.dto.AuthResponse;
import com.hospivet.auth.dto.LoginRequest;
import com.hospivet.auth.mapper.UsuarioMapper;
import com.hospivet.auth.model.Usuario;
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
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioMapper usuarioMapper, VeterinarioMapper veterinarioMapper,
                       PasswordEncoder passwordEncoder) {
        this.usuarioMapper = usuarioMapper;
        this.veterinarioMapper = veterinarioMapper;
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
        if (!"VETERINARIO".equals(usuario.getRol())) {
            throw new BusinessException(HttpStatus.FORBIDDEN,
                    "El acceso de esta entrega está habilitado solo para veterinarios");
        }

        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                usuario.getNombreUsuario(), null,
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol())));
        return new AuthenticatedUser(authentication, respuesta(usuario));
    }

    /** Datos de la sesión activa; COR-02 N-02: la cabecera muestra nombre y apellido del veterinario. */
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

    private AuthResponse respuesta(Usuario usuario) {
        Veterinario veterinario = veterinarioMapper.selectById(usuario.getIdUsuario());
        return new AuthResponse(usuario.getIdUsuario(), usuario.getNombreUsuario(), usuario.getRol(),
                veterinario == null ? null : veterinario.getNombre(),
                veterinario == null ? null : veterinario.getApellido());
    }

    public record AuthenticatedUser(Authentication authentication, AuthResponse response) {
    }
}
