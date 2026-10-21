package com.hospivet.servicio.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hospivet.common.exception.BusinessException;
import com.hospivet.servicio.dto.ServicioResponse;
import com.hospivet.servicio.mapper.ServicioMapper;
import com.hospivet.servicio.model.Servicio;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioService {

    private final ServicioMapper servicioMapper;

    public ServicioService(ServicioMapper servicioMapper) {
        this.servicioMapper = servicioMapper;
    }

    public List<ServicioResponse> listarActivos() {
        return servicioMapper.selectList(Wrappers.<Servicio>lambdaQuery()
                        .eq(Servicio::getActivo, true)
                        .orderByAsc(Servicio::getNombre))
                .stream().map(ServicioResponse::from).toList();
    }

    public Servicio requireActivo(Long idServicio) {
        Servicio servicio = servicioMapper.selectById(idServicio);
        if (servicio == null || !Boolean.TRUE.equals(servicio.getActivo())) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Servicio no encontrado");
        }
        return servicio;
    }
}
