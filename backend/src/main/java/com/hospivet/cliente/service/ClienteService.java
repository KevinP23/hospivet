package com.hospivet.cliente.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hospivet.cliente.dto.ClienteCreateRequest;
import com.hospivet.cliente.dto.ClienteResponse;
import com.hospivet.cliente.mapper.ClienteMapper;
import com.hospivet.cliente.model.Cliente;
import com.hospivet.common.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteMapper clienteMapper;

    public ClienteService(ClienteMapper clienteMapper) {
        this.clienteMapper = clienteMapper;
    }

    public List<ClienteResponse> listarActivos() {
        return clienteMapper.selectList(Wrappers.<Cliente>lambdaQuery()
                        .eq(Cliente::getActivo, true)
                        .orderByAsc(Cliente::getApellido, Cliente::getNombre))
                .stream()
                .map(ClienteResponse::from)
                .toList();
    }

    @Transactional
    public ClienteResponse registrar(ClienteCreateRequest request) {
        Long duplicates = clienteMapper.selectCount(Wrappers.<Cliente>lambdaQuery()
                .eq(Cliente::getCi, request.ci().trim()));
        if (duplicates > 0) {
            throw new BusinessException(HttpStatus.CONFLICT, "El CI ya está registrado");
        }

        Cliente cliente = new Cliente();
        cliente.setCi(request.ci().trim());
        cliente.setNombre(request.nombre().trim());
        cliente.setApellido(request.apellido().trim());
        cliente.setTelefono(request.telefono().trim());
        cliente.setCorreo(blankToNull(request.correo()));
        cliente.setDireccion(blankToNull(request.direccion()));
        cliente.setActivo(true);
        clienteMapper.insert(cliente);
        return ClienteResponse.from(cliente);
    }

    public Cliente requireActivo(Long idCliente) {
        Cliente cliente = clienteMapper.selectById(idCliente);
        if (cliente == null || !Boolean.TRUE.equals(cliente.getActivo())) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Cliente propietario no encontrado");
        }
        return cliente;
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}

