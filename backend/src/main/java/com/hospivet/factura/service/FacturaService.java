package com.hospivet.factura.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hospivet.cliente.mapper.ClienteMapper;
import com.hospivet.cliente.model.Cliente;
import com.hospivet.common.exception.BusinessException;
import com.hospivet.factura.dto.DetalleResponse;
import com.hospivet.factura.dto.FacturaEditarRequest;
import com.hospivet.factura.dto.FacturaGenerarRequest;
import com.hospivet.factura.dto.FacturaResponse;
import com.hospivet.factura.dto.ItemRequest;
import com.hospivet.factura.dto.LineaRequest;
import com.hospivet.factura.mapper.DetalleFacturaMapper;
import com.hospivet.factura.mapper.FacturaMapper;
import com.hospivet.factura.model.DetalleFactura;
import com.hospivet.factura.model.Factura;
import com.hospivet.inventario.mapper.ProductoMapper;
import com.hospivet.inventario.model.Producto;
import com.hospivet.promocion.mapper.PromocionMapper;
import com.hospivet.promocion.mapper.PromocionProductoMapper;
import com.hospivet.promocion.mapper.PromocionServicioMapper;
import com.hospivet.promocion.model.Promocion;
import com.hospivet.promocion.model.PromocionProducto;
import com.hospivet.promocion.model.PromocionServicio;
import com.hospivet.servicio.mapper.ServicioMapper;
import com.hospivet.servicio.model.Servicio;
import com.hospivet.veterinario.service.VeterinarioSesionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FacturaService {

    private static final String NO_ENCONTRADA = "Factura no encontrada";
    private static final String ITEM_NO_ENCONTRADO = "Servicio o producto no encontrado";
    private static final String DATOS_INVALIDOS = "Datos inválidos";

    private final FacturaMapper facturaMapper;
    private final DetalleFacturaMapper detalleMapper;
    private final ClienteMapper clienteMapper;
    private final ServicioMapper servicioMapper;
    private final ProductoMapper productoMapper;
    private final PromocionMapper promocionMapper;
    private final PromocionServicioMapper promocionServicioMapper;
    private final PromocionProductoMapper promocionProductoMapper;
    private final VeterinarioSesionService veterinarioSesionService;

    public FacturaService(FacturaMapper facturaMapper, DetalleFacturaMapper detalleMapper,
                          ClienteMapper clienteMapper, ServicioMapper servicioMapper, ProductoMapper productoMapper,
                          PromocionMapper promocionMapper, PromocionServicioMapper promocionServicioMapper,
                          PromocionProductoMapper promocionProductoMapper,
                          VeterinarioSesionService veterinarioSesionService) {
        this.facturaMapper = facturaMapper;
        this.detalleMapper = detalleMapper;
        this.clienteMapper = clienteMapper;
        this.servicioMapper = servicioMapper;
        this.productoMapper = productoMapper;
        this.promocionMapper = promocionMapper;
        this.promocionServicioMapper = promocionServicioMapper;
        this.promocionProductoMapper = promocionProductoMapper;
        this.veterinarioSesionService = veterinarioSesionService;
    }

    /** Listado de N-01, la factura más reciente primero. */
    public List<FacturaResponse> listar() {
        List<Factura> facturas = facturaMapper.selectList(Wrappers.<Factura>lambdaQuery()
                .orderByDesc(Factura::getIdFactura));
        Map<Long, Cliente> clientes = clientesDe(facturas);
        return facturas.stream().map(factura -> toResponse(factura, clientes.get(factura.getIdCliente()), List.of()))
                .toList();
    }

    /** SD-17 consultarDetalle(idFactura). */
    public FacturaResponse consultarDetalle(Long idFactura) {
        Factura factura = requireFactura(idFactura);
        return toResponse(factura, clienteMapper.selectById(factura.getIdCliente()), detalleDe(idFactura));
    }

    /** SD-19 generar(cliente, items): cabecera y detalle en una sola transacción. */
    @Transactional
    public FacturaResponse generar(FacturaGenerarRequest request, String nombreUsuario) {
        Long idVeterinario = veterinarioSesionService.requireIdVeterinario(nombreUsuario);
        Cliente cliente = clienteMapper.selectById(request.idCliente());
        if (cliente == null || !Boolean.TRUE.equals(cliente.getActivo())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, DATOS_INVALIDOS);
        }

        List<DetalleFactura> lineas = construirLineas(request.items());
        BigDecimal subtotal = sumar(lineas);
        BigDecimal descuento = request.idPromocion() == null
                ? BigDecimal.ZERO.setScale(2)
                : descuentoDePromocion(request.idPromocion(), lineas, LocalDate.now());

        Factura factura = new Factura();
        factura.setIdCliente(cliente.getIdCliente());
        factura.setIdVeterinario(idVeterinario);
        factura.setFechaHora(LocalDateTime.now());
        factura.setSubtotal(subtotal);
        factura.setDescuento(descuento);
        factura.setTotal(subtotal.subtract(descuento));
        factura.setEstado("EMITIDA");
        facturaMapper.insert(factura);
        for (DetalleFactura linea : lineas) {
            linea.setIdFactura(factura.getIdFactura());
            detalleMapper.insert(linea);
        }
        return toResponse(factura, cliente, lineas);
    }

    /** SD-18 editar(idFactura, datos, items): valida, recalcula y guarda cabecera e ítems juntos. */
    @Transactional
    public FacturaResponse editar(Long idFactura, FacturaEditarRequest request) {
        Factura factura = requireFactura(idFactura);
        Cliente cliente = clienteMapper.selectById(request.idCliente());
        if (cliente == null) {
            throw invalido("idCliente", "Cliente obligatorio");
        }

        Map<Long, DetalleFactura> actuales = detalleDe(idFactura).stream()
                .collect(Collectors.toMap(DetalleFactura::getIdDetalle, Function.identity(),
                        (a, b) -> a, LinkedHashMap::new));
        for (LineaRequest cambio : request.lineas()) {
            DetalleFactura linea = actuales.get(cambio.idDetalle());
            if (linea == null) {
                throw invalido("lineas", "Producto o servicio válido");
            }
            linea.setDescripcion(cambio.descripcion().trim());
            linea.setCantidad(cambio.cantidad());
            linea.setPrecioUnitario(cambio.precioUnitario().setScale(2, RoundingMode.HALF_UP));
            linea.setSubtotal(linea.getPrecioUnitario().multiply(BigDecimal.valueOf(linea.getCantidad())));
        }
        for (DetalleFactura linea : actuales.values()) {
            boolean itemValido = "SERVICIO".equals(linea.getTipoItem())
                    ? servicioMapper.selectById(linea.getIdServicio()) != null
                    : productoMapper.selectById(linea.getIdProducto()) != null;
            if (!itemValido) {
                throw invalido("lineas", "Producto o servicio válido");
            }
        }

        BigDecimal subtotal = sumar(new ArrayList<>(actuales.values()));
        BigDecimal descuento = request.descuento().setScale(2, RoundingMode.HALF_UP);
        if (descuento.compareTo(subtotal) > 0) {
            throw invalido("descuento", "Descuento entre 0 y el subtotal");
        }

        factura.setIdCliente(cliente.getIdCliente());
        factura.setFechaHora(LocalDateTime.of(request.fecha(), factura.getFechaHora().toLocalTime()));
        factura.setSubtotal(subtotal);
        factura.setDescuento(descuento);
        factura.setTotal(subtotal.subtract(descuento));
        facturaMapper.updateById(factura);
        actuales.values().forEach(detalleMapper::updateById);
        return toResponse(factura, cliente, new ArrayList<>(actuales.values()));
    }

    public Factura requireFactura(Long idFactura) {
        Factura factura = idFactura == null ? null : facturaMapper.selectById(idFactura);
        if (factura == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, NO_ENCONTRADA);
        }
        return factura;
    }

    public List<DetalleFactura> detalleDe(Long idFactura) {
        return detalleMapper.selectList(Wrappers.<DetalleFactura>lambdaQuery()
                .eq(DetalleFactura::getIdFactura, idFactura)
                .orderByAsc(DetalleFactura::getIdDetalle));
    }

    /** R7.3.2 y N-02: el precio sale del catálogo; el mismo ítem agregado varias veces suma cantidad. */
    private List<DetalleFactura> construirLineas(List<ItemRequest> items) {
        Map<String, DetalleFactura> lineas = new LinkedHashMap<>();
        for (ItemRequest item : items) {
            String tipo = item.tipo().trim().toUpperCase();
            String clave = tipo + "-" + item.id();
            DetalleFactura existente = lineas.get(clave);
            if (existente != null) {
                existente.setCantidad(existente.getCantidad() + item.cantidad());
                existente.setSubtotal(existente.getPrecioUnitario().multiply(BigDecimal.valueOf(existente.getCantidad())));
                continue;
            }
            DetalleFactura linea = new DetalleFactura();
            if ("SERVICIO".equals(tipo)) {
                Servicio servicio = servicioMapper.selectById(item.id());
                if (servicio == null || !Boolean.TRUE.equals(servicio.getActivo())) {
                    throw new BusinessException(HttpStatus.NOT_FOUND, ITEM_NO_ENCONTRADO);
                }
                linea.setIdServicio(servicio.getIdServicio());
                linea.setDescripcion(recortar(servicio.getNombre()));
                linea.setPrecioUnitario(servicio.getPrecio());
            } else if ("PRODUCTO".equals(tipo)) {
                Producto producto = productoMapper.selectById(item.id());
                if (producto == null || !Boolean.TRUE.equals(producto.getActivo())) {
                    throw new BusinessException(HttpStatus.NOT_FOUND, ITEM_NO_ENCONTRADO);
                }
                linea.setIdProducto(producto.getIdProducto());
                linea.setDescripcion(recortar(producto.getNombre()));
                linea.setPrecioUnitario(producto.getPrecio());
            } else {
                throw new BusinessException(HttpStatus.NOT_FOUND, ITEM_NO_ENCONTRADO);
            }
            linea.setTipoItem(tipo);
            linea.setCantidad(item.cantidad());
            linea.setSubtotal(linea.getPrecioUnitario().multiply(BigDecimal.valueOf(item.cantidad())));
            lineas.put(clave, linea);
        }
        return new ArrayList<>(lineas.values());
    }

    /**
     * R7.3.5 y N-04: promoción PUBLICADA vigente en la fecha de emisión. El porcentaje se aplica a los ítems
     * asociados a la promoción; si la promoción no tiene ítems asociados, a todo el subtotal.
     */
    private BigDecimal descuentoDePromocion(Long idPromocion, List<DetalleFactura> lineas, LocalDate fecha) {
        Promocion promocion = promocionMapper.selectById(idPromocion);
        boolean vigente = promocion != null && "PUBLICADA".equals(promocion.getEstado())
                && !fecha.isBefore(promocion.getFechaInicio()) && !fecha.isAfter(promocion.getFechaFin());
        if (!vigente) {
            throw invalido("idPromocion", "Promoción vigente");
        }
        Set<Long> servicios = promocionServicioMapper.selectList(Wrappers.<PromocionServicio>lambdaQuery()
                        .eq(PromocionServicio::getIdPromocion, idPromocion))
                .stream().map(PromocionServicio::getIdServicio).collect(Collectors.toSet());
        Set<Long> productos = promocionProductoMapper.selectList(Wrappers.<PromocionProducto>lambdaQuery()
                        .eq(PromocionProducto::getIdPromocion, idPromocion))
                .stream().map(PromocionProducto::getIdProducto).collect(Collectors.toSet());

        List<DetalleFactura> aplicables = servicios.isEmpty() && productos.isEmpty() ? lineas
                : lineas.stream().filter(linea -> servicios.contains(linea.getIdServicio())
                        || productos.contains(linea.getIdProducto())).toList();
        if (aplicables.isEmpty()) {
            throw invalido("idPromocion", "Promoción vigente");
        }
        return sumar(aplicables).multiply(promocion.getPorcentajeDescuento())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private BusinessException invalido(String campo, String regla) {
        return new BusinessException(HttpStatus.BAD_REQUEST, DATOS_INVALIDOS, Map.of(campo, regla));
    }

    private static BigDecimal sumar(List<DetalleFactura> lineas) {
        return lineas.stream().map(DetalleFactura::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    }

    private static String recortar(String texto) {
        return texto.length() <= 150 ? texto : texto.substring(0, 150);
    }

    private Map<Long, Cliente> clientesDe(List<Factura> facturas) {
        List<Long> ids = facturas.stream().map(Factura::getIdCliente).distinct().toList();
        if (ids.isEmpty()) return Map.of();
        return clienteMapper.selectByIds(ids).stream()
                .collect(Collectors.toMap(Cliente::getIdCliente, Function.identity()));
    }

    public FacturaResponse toResponse(Factura factura, Cliente cliente, List<DetalleFactura> lineas) {
        List<DetalleResponse> detalle = lineas.stream().map(linea -> new DetalleResponse(linea.getIdDetalle(),
                linea.getTipoItem(), linea.getIdProducto(), linea.getIdServicio(), linea.getDescripcion(),
                linea.getCantidad(), linea.getPrecioUnitario(), linea.getSubtotal())).toList();
        return new FacturaResponse(factura.getIdFactura(), FacturaResponse.numero(factura.getIdFactura()),
                cliente.getIdCliente(), cliente.getNombre() + " " + cliente.getApellido(), cliente.getCorreo(),
                factura.getFechaHora(), factura.getSubtotal(), factura.getDescuento(), factura.getTotal(),
                factura.getEstado(), detalle);
    }
}
