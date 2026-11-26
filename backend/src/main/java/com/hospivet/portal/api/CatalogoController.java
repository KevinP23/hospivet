package com.hospivet.portal.api;

import com.hospivet.portal.dto.PromocionPortalResponse;
import com.hospivet.portal.dto.ServicioPortalResponse;
import com.hospivet.portal.service.PortalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** CatalogoController de SD-31 (Consultar servicios) y SD-32 (Ver promociones). Solo rol CLIENTE. */
@RestController
@RequestMapping("/api/portal")
public class CatalogoController {

    private final PortalService portalService;

    public CatalogoController(PortalService portalService) {
        this.portalService = portalService;
    }

    @GetMapping("/servicios")
    public List<ServicioPortalResponse> servicios() {
        return portalService.servicios();
    }

    @GetMapping("/promociones")
    public List<PromocionPortalResponse> promociones() {
        return portalService.promociones();
    }
}
