package com.hospivet.promocion.api;

import com.hospivet.promocion.dto.PromocionRequest;
import com.hospivet.promocion.dto.PromocionResponse;
import com.hospivet.promocion.service.PromocionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Controlador de las secuencias SD-26 a SD-29 de la Iteración 5. */
@RestController
@RequestMapping("/api/promociones")
public class PromocionesController {

    private final PromocionService promocionService;

    public PromocionesController(PromocionService promocionService) {
        this.promocionService = promocionService;
    }

    @GetMapping
    public List<PromocionResponse> listar() {
        return promocionService.listar();
    }

    @GetMapping("/{idPromocion}")
    public PromocionResponse obtener(@PathVariable Long idPromocion) {
        return promocionService.obtener(idPromocion);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PromocionResponse crear(@Valid @RequestBody PromocionRequest request) {
        return promocionService.crear(request);
    }

    @PutMapping("/{idPromocion}")
    public PromocionResponse editar(@PathVariable Long idPromocion, @Valid @RequestBody PromocionRequest request) {
        return promocionService.editar(idPromocion, request);
    }

    @PatchMapping("/{idPromocion}/publicacion")
    public PromocionResponse publicar(@PathVariable Long idPromocion) {
        return promocionService.publicar(idPromocion);
    }

    @DeleteMapping("/{idPromocion}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long idPromocion) {
        promocionService.eliminar(idPromocion);
    }
}
