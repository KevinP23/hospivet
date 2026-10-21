package com.hospivet.inventario.api;

import com.hospivet.inventario.dto.ProductoCreateRequest;
import com.hospivet.inventario.dto.ProductoResponse;
import com.hospivet.inventario.dto.StockUpdateRequest;
import com.hospivet.inventario.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inventario/productos")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @GetMapping
    public List<ProductoResponse> listar(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) String categoria) {
        return inventarioService.listar(buscar, categoria);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse agregar(@Valid @RequestBody ProductoCreateRequest request) {
        return inventarioService.agregar(request);
    }

    @PatchMapping("/{idProducto}/stock")
    public ProductoResponse actualizarStock(@PathVariable Long idProducto,
                                             @Valid @RequestBody StockUpdateRequest request,
                                             Authentication authentication) {
        return inventarioService.actualizarStock(idProducto, request, authentication.getName());
    }

    @DeleteMapping("/{idProducto}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long idProducto) {
        inventarioService.eliminar(idProducto);
    }
}
