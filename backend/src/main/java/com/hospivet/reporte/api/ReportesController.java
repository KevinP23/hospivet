package com.hospivet.reporte.api;

import com.hospivet.reporte.dto.ReporteIngresosResponse;
import com.hospivet.reporte.dto.ReporteInventarioFila;
import com.hospivet.reporte.dto.ReporteProductoFila;
import com.hospivet.reporte.dto.ReporteServicioFila;
import com.hospivet.reporte.service.ReporteService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/** Controlador de las secuencias SD-13 a SD-16 de la Iteración 5 (ReportesController). */
@RestController
@RequestMapping("/api/reportes")
public class ReportesController {

    private final ReporteService reporteService;

    public ReportesController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    /** SD-13 reporteInventario(). */
    @GetMapping("/inventario")
    public List<ReporteInventarioFila> reporteInventario() {
        return reporteService.inventario();
    }

    /** SD-14 reporteIngresos(desde, hasta). */
    @GetMapping("/ingresos")
    public ReporteIngresosResponse reporteIngresos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.ingresos(desde, hasta);
    }

    /** SD-15 reporteServicios(periodo). */
    @GetMapping("/servicios")
    public List<ReporteServicioFila> reporteServicios(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return reporteService.servicios(desde, hasta);
    }

    /** SD-16 reporteProductos(criterio). */
    @GetMapping("/productos")
    public List<ReporteProductoFila> reporteProductos(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String estado) {
        return reporteService.productos(categoria, estado);
    }
}
