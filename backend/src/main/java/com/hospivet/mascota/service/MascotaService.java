package com.hospivet.mascota.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hospivet.cliente.service.ClienteService;
import com.hospivet.mascota.dto.MascotaCreateRequest;
import com.hospivet.mascota.dto.MascotaResponse;
import com.hospivet.mascota.mapper.MascotaMapper;
import com.hospivet.mascota.model.Mascota;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MascotaService {

    private final MascotaMapper mascotaMapper;
    private final ClienteService clienteService;

    public MascotaService(MascotaMapper mascotaMapper, ClienteService clienteService) {
        this.mascotaMapper = mascotaMapper;
        this.clienteService = clienteService;
    }

    public List<MascotaResponse> listar(Long idCliente) {
        LambdaQueryWrapper<Mascota> query = Wrappers.<Mascota>lambdaQuery()
                .eq(Mascota::getActivo, true);
        if (idCliente != null) {
            query.eq(Mascota::getIdCliente, idCliente);
        }
        query.orderByAsc(Mascota::getNombre);
        return mascotaMapper.selectList(query).stream().map(MascotaResponse::from).toList();
    }

    @Transactional
    public MascotaResponse registrar(MascotaCreateRequest request) {
        clienteService.requireActivo(request.idCliente());

        Mascota mascota = new Mascota();
        mascota.setIdCliente(request.idCliente());
        mascota.setNombre(request.nombre().trim());
        mascota.setEspecie(request.especie().trim());
        mascota.setRaza(blankToNull(request.raza()));
        mascota.setFechaNacimiento(request.fechaNacimiento());
        mascota.setPeso(request.peso());
        mascota.setSexo(blankToNull(request.sexo()));
        mascota.setObservaciones(blankToNull(request.observaciones()));
        mascota.setActivo(true);
        mascotaMapper.insert(mascota);
        return MascotaResponse.from(mascota);
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}

