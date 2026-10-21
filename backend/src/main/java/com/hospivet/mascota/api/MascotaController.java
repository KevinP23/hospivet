package com.hospivet.mascota.api;

import com.hospivet.mascota.dto.MascotaCreateRequest;
import com.hospivet.mascota.dto.MascotaResponse;
import com.hospivet.mascota.service.MascotaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {

    private final MascotaService mascotaService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @GetMapping
    public List<MascotaResponse> listar(
            @RequestParam(name = "clienteId", required = false) Long clienteId) {
        return mascotaService.listar(clienteId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MascotaResponse registrar(@Valid @RequestBody MascotaCreateRequest request) {
        return mascotaService.registrar(request);
    }
}

