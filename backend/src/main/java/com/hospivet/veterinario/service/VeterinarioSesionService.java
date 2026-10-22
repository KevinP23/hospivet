package com.hospivet.veterinario.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hospivet.auth.mapper.UsuarioMapper;
import com.hospivet.auth.model.Usuario;
import com.hospivet.common.exception.BusinessException;
import com.hospivet.veterinario.mapper.VeterinarioMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class VeterinarioSesionService {

    private final UsuarioMapper usuarioMapper;
    private final VeterinarioMapper veterinarioMapper;

    public VeterinarioSesionService(UsuarioMapper usuarioMapper, VeterinarioMapper veterinarioMapper) {
        this.usuarioMapper = usuarioMapper;
        this.veterinarioMapper = veterinarioMapper;
    }

    public Long requireIdVeterinario(String nombreUsuario) {
        Usuario usuario = usuarioMapper.selectOne(Wrappers.<Usuario>lambdaQuery()
                .eq(Usuario::getNombreUsuario, nombreUsuario)
                .eq(Usuario::getActivo, true));
        if (usuario == null || !"VETERINARIO".equals(usuario.getRol())
                || veterinarioMapper.selectById(usuario.getIdUsuario()) == null) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Veterinario no autorizado");
        }
        return usuario.getIdUsuario();
    }

    /** Serializa las operaciones de agenda del veterinario dentro de la transacción actual. */
    public void bloquearAgenda(Long idVeterinario) {
        veterinarioMapper.bloquearPorId(idVeterinario);
    }
}
