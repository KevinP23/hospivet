package com.hospivet.atencion.api;

import com.hospivet.atencion.dto.AtencionResponse;
import com.hospivet.atencion.dto.CitaProgramadaResponse;
import com.hospivet.atencion.dto.DiagnosticoCreateRequest;
import com.hospivet.atencion.dto.DiagnosticoResponse;
import com.hospivet.atencion.dto.HistorialResponse;
import com.hospivet.atencion.dto.RecetaCreateRequest;
import com.hospivet.atencion.dto.RecetaResponse;
import com.hospivet.atencion.dto.TratamientoCreateRequest;
import com.hospivet.atencion.dto.TratamientoResponse;
import com.hospivet.atencion.service.AtencionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Controlador de las secuencias SD-22 a SD-25 de la Iteración 5. */
@RestController
@RequestMapping("/api")
public class AtencionController {

    private final AtencionService atencionService;

    public AtencionController(AtencionService atencionService) {
        this.atencionService = atencionService;
    }

    @GetMapping("/atenciones/historial")
    public List<HistorialResponse> consultarHistorial(@RequestParam Long mascotaId) {
        return atencionService.consultarHistorial(mascotaId);
    }

    @GetMapping("/atenciones")
    public List<AtencionResponse> listarAtenciones(@RequestParam(required = false) Long mascotaId) {
        return atencionService.listarAtenciones(mascotaId);
    }

    @GetMapping("/atenciones/citas-programadas")
    public List<CitaProgramadaResponse> listarCitasProgramadas(@RequestParam Long mascotaId,
                                                               Authentication authentication) {
        return atencionService.listarCitasProgramadas(mascotaId, authentication.getName());
    }

    @GetMapping("/atenciones/diagnosticos")
    public List<DiagnosticoResponse> listarDiagnosticos(@RequestParam(required = false) Long mascotaId) {
        return atencionService.listarDiagnosticos(mascotaId);
    }

    @PostMapping("/atenciones/diagnosticos")
    @ResponseStatus(HttpStatus.CREATED)
    public DiagnosticoResponse registrarDiagnostico(@Valid @RequestBody DiagnosticoCreateRequest request,
                                                    Authentication authentication) {
        return atencionService.registrarDiagnostico(request, authentication.getName());
    }

    @PostMapping("/diagnosticos/{idDiagnostico}/tratamientos")
    @ResponseStatus(HttpStatus.CREATED)
    public TratamientoResponse registrarTratamiento(@PathVariable Long idDiagnostico,
                                                    @Valid @RequestBody TratamientoCreateRequest request) {
        return atencionService.registrarTratamiento(idDiagnostico, request);
    }

    @PostMapping("/atenciones/{idAtencion}/recetas")
    @ResponseStatus(HttpStatus.CREATED)
    public RecetaResponse registrarReceta(@PathVariable Long idAtencion,
                                          @Valid @RequestBody RecetaCreateRequest request) {
        return atencionService.registrarReceta(idAtencion, request);
    }
}
