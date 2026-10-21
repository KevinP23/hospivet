package com.hospivet.cita.api;

import com.hospivet.cita.dto.CitaCreateRequest;
import com.hospivet.cita.dto.CitaResponse;
import com.hospivet.cita.dto.CitaUpdateRequest;
import com.hospivet.cita.service.CitaService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private final CitaService citaService;

    public CitaController(CitaService citaService) {
        this.citaService = citaService;
    }

    @GetMapping
    public List<CitaResponse> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Authentication authentication) {
        return citaService.listar(fecha, authentication.getName());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CitaResponse registrar(@Valid @RequestBody CitaCreateRequest request,
                                  Authentication authentication) {
        return citaService.registrar(request, authentication.getName());
    }

    @PutMapping("/{idCita}")
    public CitaResponse modificar(@PathVariable Long idCita,
                                  @Valid @RequestBody CitaUpdateRequest request,
                                  Authentication authentication) {
        return citaService.modificar(idCita, request, authentication.getName());
    }

    @PatchMapping("/{idCita}/cancelacion")
    public CitaResponse cancelar(@PathVariable Long idCita, Authentication authentication) {
        return citaService.cancelar(idCita, authentication.getName());
    }
}
