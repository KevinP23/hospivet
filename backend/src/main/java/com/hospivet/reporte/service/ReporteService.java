package com.hospivet.reporte.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hospivet.cita.mapper.CitaMapper;
import com.hospivet.cita.model.Cita;
import com.hospivet.cliente.mapper.ClienteMapper;
import com.hospivet.cliente.model.Cliente;
import com.hospivet.common.exception.BusinessException;
import com.hospivet.factura.mapper.DetalleFacturaMapper;
import com.hospivet.factura.mapper.FacturaMapper;
import com.hospivet.factura.model.DetalleFactura;
import com.hospivet.factura.model.Factura;
import com.hospivet.inventario.mapper.ProductoMapper;
import com.hospivet.inventario.model.Producto;
import com.hospivet.reporte.dto.ReporteIngresoFila;
import com.hospivet.reporte.dto.ReporteIngresosResponse;
import com.hospivet.reporte.dto.ReporteInventarioFila;
import com.hospivet.reporte.dto.ReporteProductoFila;
import com.hospivet.reporte.dto.ReporteServicioFila;
import com.hospivet.servicio.mapper.ServicioMapper;
import com.hospivet.servicio.model.Servicio;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * R6 Gestión de reportes (SD-13 a SD-16). Solo lee datos: la postcondición de los cuatro casos de uso
 * exige mostrar el reporte sin alterar los registros. Decisiones del plan R06 (D-19): datos de toda la
 * veterinaria (N-02), solo productos activos (N-07) y facturas no anuladas (N-08).
 */
@Service
@Transactional(readOnly = true)
public class ReporteService {

    public static final String DISPONIBLE = "DISPONIBLE";
    public static final String AGOTADO = "AGOTADO";
    private static final String DATOS_INVALIDOS = "Datos inválidos";

    private final ProductoMapper productoMapper;
    private final FacturaMapper facturaMapper;
    private final DetalleFacturaMapper detalleMapper;
    private final ClienteMapper clienteMapper;
    private final CitaMapper citaMapper;
    private final ServicioMapper servicioMapper;

    public ReporteService(ProductoMapper productoMapper, FacturaMapper facturaMapper,
                          DetalleFacturaMapper detalleMapper, ClienteMapper clienteMapper,
                          CitaMapper citaMapper, ServicioMapper servicioMapper) {
        this.productoMapper = productoMapper;
        this.facturaMapper = facturaMapper;
        this.detalleMapper = detalleMapper;
        this.clienteMapper = clienteMapper;
        this.citaMapper = citaMapper;
        this.servicioMapper = servicioMapper;
    }

    /** SD-13 reporteInventario(): productos y stock; Valor en Bs = stock actual × precio. */
    public List<ReporteInventarioFila> inventario() {
        return productosActivos().stream()
                .map(producto -> new ReporteInventarioFila(
                        producto.getNombre(),
                        producto.getCategoria(),
                        producto.getStockActual(),
                        producto.getStockMinimo(),
                        producto.getPrecio().multiply(BigDecimal.valueOf(producto.getStockActual()))
                                .setScale(2, RoundingMode.HALF_UP)))
                .toList();
    }

    /** SD-14 reporteIngresos(desde, hasta): facturas del periodo por fecha y total de ingresos. */
    public ReporteIngresosResponse ingresos(LocalDate desde, LocalDate hasta) {
        validarPeriodo(desde, hasta);
        List<Factura> facturas = facturaMapper.selectList(Wrappers.<Factura>lambdaQuery()
                .ge(Factura::getFechaHora, inicio(desde))
                .lt(Factura::getFechaHora, finExclusivo(hasta))
                .ne(Factura::getEstado, "ANULADA")
                .orderByAsc(Factura::getFechaHora)
                .orderByAsc(Factura::getIdFactura));
        if (facturas.isEmpty()) {
            return new ReporteIngresosResponse(List.of(), BigDecimal.ZERO.setScale(2));
        }

        List<Long> ids = facturas.stream().map(Factura::getIdFactura).toList();
        Map<Long, List<DetalleFactura>> detalle = detalleMapper.selectList(Wrappers.<DetalleFactura>lambdaQuery()
                        .in(DetalleFactura::getIdFactura, ids)
                        .orderByAsc(DetalleFactura::getIdDetalle))
                .stream()
                .collect(Collectors.groupingBy(DetalleFactura::getIdFactura));
        Map<Long, Cliente> clientes = clienteMapper.selectByIds(
                        facturas.stream().map(Factura::getIdCliente).distinct().toList())
                .stream()
                .collect(Collectors.toMap(Cliente::getIdCliente, Function.identity()));

        List<ReporteIngresoFila> filas = facturas.stream()
                .map(factura -> new ReporteIngresoFila(
                        factura.getFechaHora().toLocalDate(),
                        nombreCliente(clientes.get(factura.getIdCliente())),
                        concepto(detalle.getOrDefault(factura.getIdFactura(), List.of())),
                        factura.getTotal().setScale(2, RoundingMode.HALF_UP)))
                .toList();
        BigDecimal total = filas.stream().map(ReporteIngresoFila::monto)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        return new ReporteIngresosResponse(filas, total);
    }

    /**
     * SD-15 reporteServicios(periodo): citas ATENDIDAS del periodo agrupadas por servicio.
     * Ingreso en Bs = cantidad realizada × precio del servicio en el catálogo (N-03).
     */
    public List<ReporteServicioFila> servicios(LocalDate desde, LocalDate hasta) {
        validarPeriodo(desde, hasta);
        Map<Long, Long> cantidades = citaMapper.selectList(Wrappers.<Cita>lambdaQuery()
                        .eq(Cita::getEstado, "ATENDIDA")
                        .ge(Cita::getFechaHora, inicio(desde))
                        .lt(Cita::getFechaHora, finExclusivo(hasta)))
                .stream()
                .collect(Collectors.groupingBy(Cita::getIdServicio, Collectors.counting()));
        if (cantidades.isEmpty()) {
            return List.of();
        }

        return servicioMapper.selectByIds(cantidades.keySet()).stream()
                .map(servicio -> {
                    long cantidad = cantidades.get(servicio.getIdServicio());
                    return new ReporteServicioFila(
                            servicio.getNombre(),
                            cantidad,
                            servicio.getPrecio().multiply(BigDecimal.valueOf(cantidad))
                                    .setScale(2, RoundingMode.HALF_UP));
                })
                .sorted(Comparator.comparing(ReporteServicioFila::cantidad).reversed()
                        .thenComparing(ReporteServicioFila::servicio, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    /**
     * SD-16 reporteProductos(criterio): categoría y estado. Vacío o TODOS no filtran.
     * Estados de la interfaz (N-05): DISPONIBLE (stock mayor que 0) y AGOTADO (stock 0).
     */
    public List<ReporteProductoFila> productos(String categoria, String estado) {
        String estadoBuscado = normalizarEstado(estado);
        String categoriaBuscada = StringUtils.hasText(categoria) ? categoria.trim() : null;

        return productosActivos().stream()
                .filter(producto -> categoriaBuscada == null
                        || producto.getCategoria().trim().equalsIgnoreCase(categoriaBuscada))
                .map(producto -> new ReporteProductoFila(
                        producto.getNombre(),
                        producto.getCategoria(),
                        producto.getStockActual(),
                        producto.getStockActual() == 0 ? AGOTADO : DISPONIBLE))
                .filter(fila -> estadoBuscado == null || fila.estado().equals(estadoBuscado))
                .toList();
    }

    private List<Producto> productosActivos() {
        return productoMapper.selectList(Wrappers.<Producto>lambdaQuery()
                .eq(Producto::getActivo, true)
                .orderByAsc(Producto::getNombre));
    }

    private static String normalizarEstado(String estado) {
        if (!StringUtils.hasText(estado) || "TODOS".equalsIgnoreCase(estado.trim())) {
            return null;
        }
        String valor = estado.trim().toUpperCase(Locale.ROOT);
        if (!DISPONIBLE.equals(valor) && !AGOTADO.equals(valor)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, DATOS_INVALIDOS, Map.of("estado", "Estado inválido"));
        }
        return valor;
    }

    /** Precondición de 3.1.14 y 3.1.15: rango de fechas válido. La interfaz no permite armar otro (N-06). */
    private static void validarPeriodo(LocalDate desde, LocalDate hasta) {
        if (desde == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, DATOS_INVALIDOS, Map.of("desde", "Seleccione la fecha"));
        }
        if (hasta == null) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, DATOS_INVALIDOS, Map.of("hasta", "Seleccione la fecha"));
        }
        if (hasta.isBefore(desde)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, DATOS_INVALIDOS,
                    Map.of("hasta", "La fecha hasta no puede ser anterior a la fecha desde"));
        }
    }

    private static LocalDateTime inicio(LocalDate desde) {
        return desde.atStartOfDay();
    }

    private static LocalDateTime finExclusivo(LocalDate hasta) {
        return hasta.plusDays(1).atStartOfDay();
    }

    private static String nombreCliente(Cliente cliente) {
        return cliente == null ? "" : (cliente.getNombre() + " " + cliente.getApellido()).trim();
    }

    /** N-04: descripciones de las líneas de la factura, separadas por coma, en el orden del detalle. */
    private static String concepto(List<DetalleFactura> lineas) {
        return lineas.stream().map(DetalleFactura::getDescripcion).collect(Collectors.joining(", "));
    }
}
