package com.hospivet.promocion.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hospivet.common.exception.BusinessException;
import com.hospivet.inventario.mapper.ProductoMapper;
import com.hospivet.inventario.model.Producto;
import com.hospivet.promocion.dto.PromocionRequest;
import com.hospivet.promocion.dto.PromocionResponse;
import com.hospivet.promocion.mapper.PromocionMapper;
import com.hospivet.promocion.mapper.PromocionProductoMapper;
import com.hospivet.promocion.mapper.PromocionServicioMapper;
import com.hospivet.promocion.model.Promocion;
import com.hospivet.promocion.model.PromocionProducto;
import com.hospivet.promocion.model.PromocionServicio;
import com.hospivet.servicio.mapper.ServicioMapper;
import com.hospivet.servicio.model.Servicio;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PromocionService {

    private static final String SERVICIOS_O_PRODUCTOS_INVALIDOS =
            "Los productos o servicios seleccionados deben estar registrados";

    private final PromocionMapper promocionMapper;
    private final PromocionServicioMapper promocionServicioMapper;
    private final PromocionProductoMapper promocionProductoMapper;
    private final ServicioMapper servicioMapper;
    private final ProductoMapper productoMapper;

    public PromocionService(PromocionMapper promocionMapper, PromocionServicioMapper promocionServicioMapper,
                            PromocionProductoMapper promocionProductoMapper, ServicioMapper servicioMapper,
                            ProductoMapper productoMapper) {
        this.promocionMapper = promocionMapper;
        this.promocionServicioMapper = promocionServicioMapper;
        this.promocionProductoMapper = promocionProductoMapper;
        this.servicioMapper = servicioMapper;
        this.productoMapper = productoMapper;
    }

    /** Listado de N-01 y opciones de Buscar/Seleccionar promoción. */
    public List<PromocionResponse> listar() {
        List<Promocion> promociones = promocionMapper.selectList(Wrappers.<Promocion>lambdaQuery()
                .orderByDesc(Promocion::getIdPromocion));
        return promociones.stream().map(this::toResponse).toList();
    }

    public PromocionResponse obtener(Long idPromocion) {
        return toResponse(requirePromocion(idPromocion));
    }

    /** SD-26 crear(datosPromocion): queda en BORRADOR hasta publicarse. */
    @Transactional
    public PromocionResponse crear(PromocionRequest request) {
        if (!request.fechaFin().isAfter(request.fechaInicio())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La fecha de fin debe ser posterior a la de inicio",
                    Map.of("fechaFin", "La fecha de fin debe ser posterior a la de inicio"));
        }
        validarAsociaciones(request);

        Promocion promocion = new Promocion();
        aplicarDatos(promocion, request);
        promocion.setEstado("BORRADOR");
        promocionMapper.insert(promocion);
        guardarAsociaciones(promocion.getIdPromocion(), request);
        return toResponse(promocion);
    }

    /** SD-27 editar(idPromocion, datos): ante un error conserva la versión anterior. */
    @Transactional
    public PromocionResponse editar(Long idPromocion, PromocionRequest request) {
        Promocion promocion = requirePromocion(idPromocion);
        if (!request.fechaFin().isAfter(request.fechaInicio())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Datos inválidos",
                    Map.of("fechaFin", "Fin posterior al inicio"));
        }
        validarAsociaciones(request);

        aplicarDatos(promocion, request);
        promocionMapper.updateById(promocion);
        borrarAsociaciones(idPromocion);
        guardarAsociaciones(idPromocion, request);
        return toResponse(promocion);
    }

    /** SD-28 publicar(idPromocion). */
    @Transactional
    public PromocionResponse publicar(Long idPromocion) {
        Promocion promocion = requirePromocion(idPromocion);
        if ("PUBLICADA".equals(promocion.getEstado())) {
            throw new BusinessException(HttpStatus.CONFLICT, "La promoción ya está publicada");
        }
        promocion.setEstado("PUBLICADA");
        promocionMapper.updateById(promocion);
        return toResponse(promocion);
    }

    /** SD-29 eliminar(idPromocion, confirmar): la confirmación ocurre en la vista. */
    @Transactional
    public void eliminar(Long idPromocion) {
        requirePromocion(idPromocion);
        borrarAsociaciones(idPromocion);
        promocionMapper.deleteById(idPromocion);
    }

    private void aplicarDatos(Promocion promocion, PromocionRequest request) {
        promocion.setTitulo(request.titulo().trim());
        promocion.setDescripcion(request.descripcion().trim());
        promocion.setPorcentajeDescuento(request.porcentajeDescuento());
        promocion.setFechaInicio(request.fechaInicio());
        promocion.setFechaFin(request.fechaFin());
    }

    /** R9.2.3: si se selecciona un producto o servicio, verifica que exista. */
    private void validarAsociaciones(PromocionRequest request) {
        List<Long> idsServicio = ids(request.idsServicio());
        List<Long> idsProducto = ids(request.idsProducto());
        boolean serviciosValidos = idsServicio.isEmpty()
                || servicioMapper.selectByIds(idsServicio).size() == idsServicio.size();
        boolean productosValidos = idsProducto.isEmpty()
                || productoMapper.selectByIds(idsProducto).size() == idsProducto.size();
        if (!serviciosValidos || !productosValidos) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Datos inválidos",
                    Map.of("aplicaA", SERVICIOS_O_PRODUCTOS_INVALIDOS));
        }
    }

    private void guardarAsociaciones(Long idPromocion, PromocionRequest request) {
        ids(request.idsServicio()).forEach(idServicio ->
                promocionServicioMapper.insert(new PromocionServicio(idPromocion, idServicio)));
        ids(request.idsProducto()).forEach(idProducto ->
                promocionProductoMapper.insert(new PromocionProducto(idPromocion, idProducto)));
    }

    private void borrarAsociaciones(Long idPromocion) {
        promocionServicioMapper.delete(Wrappers.<PromocionServicio>lambdaQuery()
                .eq(PromocionServicio::getIdPromocion, idPromocion));
        promocionProductoMapper.delete(Wrappers.<PromocionProducto>lambdaQuery()
                .eq(PromocionProducto::getIdPromocion, idPromocion));
    }

    private Promocion requirePromocion(Long idPromocion) {
        Promocion promocion = promocionMapper.selectById(idPromocion);
        if (promocion == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Datos inválidos");
        }
        return promocion;
    }

    private PromocionResponse toResponse(Promocion promocion) {
        List<Long> idsServicio = promocionServicioMapper.selectList(Wrappers.<PromocionServicio>lambdaQuery()
                        .eq(PromocionServicio::getIdPromocion, promocion.getIdPromocion()))
                .stream().map(PromocionServicio::getIdServicio).toList();
        List<Long> idsProducto = promocionProductoMapper.selectList(Wrappers.<PromocionProducto>lambdaQuery()
                        .eq(PromocionProducto::getIdPromocion, promocion.getIdPromocion()))
                .stream().map(PromocionProducto::getIdProducto).toList();

        List<String> aplicaA = new ArrayList<>();
        if (!idsServicio.isEmpty()) {
            Map<Long, String> servicios = servicioMapper.selectByIds(idsServicio).stream()
                    .collect(Collectors.toMap(Servicio::getIdServicio, Servicio::getNombre));
            idsServicio.forEach(id -> aplicaA.add(servicios.get(id)));
        }
        if (!idsProducto.isEmpty()) {
            Map<Long, String> productos = productoMapper.selectByIds(idsProducto).stream()
                    .collect(Collectors.toMap(Producto::getIdProducto, Producto::getNombre));
            idsProducto.forEach(id -> aplicaA.add(productos.get(id)));
        }

        return new PromocionResponse(promocion.getIdPromocion(), PromocionResponse.codigo(promocion.getIdPromocion()),
                promocion.getTitulo(), promocion.getDescripcion(), promocion.getPorcentajeDescuento(),
                promocion.getFechaInicio(), promocion.getFechaFin(), promocion.getEstado(),
                idsServicio, idsProducto, aplicaA);
    }

    private static List<Long> ids(List<Long> values) {
        return values == null ? List.of() : values.stream().filter(id -> id != null).distinct().toList();
    }
}
