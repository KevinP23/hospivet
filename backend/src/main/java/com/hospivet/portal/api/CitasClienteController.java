package com.hospivet.portal.api;

import com.hospivet.portal.dto.CitaSolicitadaResponse;
import com.hospivet.portal.dto.HorarioResponse;
import com.hospivet.portal.dto.MascotaPortalResponse;
import com.hospivet.portal.dto.SolicitudCitaRequest;
import com.hospivet.portal.service.PortalService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/** CitasClienteController de SD-30 (3.1.30 Solicitar cita del cliente). Solo rol CLIENTE. */
@RestController
@RequestMapping("/api/portal")
public class CitasClienteController {

    private final PortalService portalService;

    public CitasClienteController(PortalService portalService) {
        this.portalService = portalService;
    }

    @GetMapping("/mascotas")
    public List<MascotaPortalResponse> mascotas(Authentication authentication) {
        return portalService.mascotas(authentication.getName());
    }

    /** R10.1.1 Ver horarios disponibles. */
    @GetMapping("/citas/horarios")
    public List<HorarioResponse> horarios(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Long idServicio,
            Authentication authentication) {
        return portalService.horarios(fecha, idServicio, authentication.getName());
    }

    /** SD-30 registrar(fecha, horario, mascota). */
    @PostMapping("/citas")
    @ResponseStatus(HttpStatus.CREATED)
    public CitaSolicitadaResponse registrar(@Valid @RequestBody SolicitudCitaRequest request,
                                            Authentication authentication) {
        return portalService.solicitarCita(request, authentication.getName());
    }
}
