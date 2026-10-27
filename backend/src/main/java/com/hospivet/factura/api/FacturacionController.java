package com.hospivet.factura.api;

import com.hospivet.factura.dto.CorreoClienteRequest;
import com.hospivet.factura.dto.EnvioRequest;
import com.hospivet.factura.dto.FacturaEditarRequest;
import com.hospivet.factura.dto.FacturaGenerarRequest;
import com.hospivet.factura.dto.FacturaResponse;
import com.hospivet.factura.dto.ImpresionRequest;
import com.hospivet.factura.dto.ImpresorasResponse;
import com.hospivet.factura.dto.MensajeResponse;
import com.hospivet.factura.service.FacturaDocumentoService;
import com.hospivet.factura.service.FacturaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Controlador de las secuencias SD-17 a SD-21 de la Iteración 5. */
@RestController
@RequestMapping("/api")
public class FacturacionController {

    private final FacturaService facturaService;
    private final FacturaDocumentoService documentoService;

    public FacturacionController(FacturaService facturaService, FacturaDocumentoService documentoService) {
        this.facturaService = facturaService;
        this.documentoService = documentoService;
    }

    @GetMapping("/facturas")
    public List<FacturaResponse> listar() {
        return facturaService.listar();
    }

    @GetMapping("/facturas/{idFactura}")
    public FacturaResponse consultarDetalle(@PathVariable Long idFactura) {
        return facturaService.consultarDetalle(idFactura);
    }

    @PostMapping("/facturas")
    @ResponseStatus(HttpStatus.CREATED)
    public FacturaResponse generar(@Valid @RequestBody FacturaGenerarRequest request, Authentication authentication) {
        return facturaService.generar(request, authentication.getName());
    }

    @PutMapping("/facturas/{idFactura}")
    public FacturaResponse editar(@PathVariable Long idFactura, @Valid @RequestBody FacturaEditarRequest request) {
        return facturaService.editar(idFactura, request);
    }

    @GetMapping("/impresoras")
    public ImpresorasResponse impresoras() {
        return documentoService.impresoras();
    }

    @PostMapping("/facturas/{idFactura}/impresion")
    public MensajeResponse imprimir(@PathVariable Long idFactura, @Valid @RequestBody ImpresionRequest request) {
        documentoService.imprimir(idFactura, request.impresora());
        return new MensajeResponse("Documento preparado para impresión");
    }

    @PostMapping("/facturas/{idFactura}/envio")
    public MensajeResponse enviar(@PathVariable Long idFactura, @Valid @RequestBody EnvioRequest request) {
        documentoService.enviar(idFactura, request.medio());
        return new MensajeResponse("Solicitud de envío confirmada por proveedor");
    }

    @PutMapping("/facturas/{idFactura}/correo-cliente")
    public FacturaResponse actualizarCorreo(@PathVariable Long idFactura,
                                            @Valid @RequestBody CorreoClienteRequest request) {
        return documentoService.actualizarCorreo(idFactura, request.correo());
    }
}
