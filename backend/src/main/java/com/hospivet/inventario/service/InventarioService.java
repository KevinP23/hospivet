package com.hospivet.inventario.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hospivet.auth.mapper.UsuarioMapper;
import com.hospivet.auth.model.Usuario;
import com.hospivet.common.exception.BusinessException;
import com.hospivet.inventario.dto.ProductoCreateRequest;
import com.hospivet.inventario.dto.ProductoResponse;
import com.hospivet.inventario.dto.StockUpdateRequest;
import com.hospivet.inventario.mapper.MovimientoInventarioMapper;
import com.hospivet.inventario.mapper.ProductoMapper;
import com.hospivet.inventario.model.MovimientoInventario;
import com.hospivet.inventario.model.Producto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class InventarioService {

    private final ProductoMapper productoMapper;
    private final MovimientoInventarioMapper movimientoMapper;
    private final UsuarioMapper usuarioMapper;

    public InventarioService(ProductoMapper productoMapper,
                             MovimientoInventarioMapper movimientoMapper,
                             UsuarioMapper usuarioMapper) {
        this.productoMapper = productoMapper;
        this.movimientoMapper = movimientoMapper;
        this.usuarioMapper = usuarioMapper;
    }

    public List<ProductoResponse> listar(String buscar, String categoria) {
        String criterio = normalize(buscar);
        String categoriaNormalizada = normalize(categoria);

        return productoMapper.selectList(Wrappers.<Producto>lambdaQuery()
                        .eq(Producto::getActivo, true)
                        .orderByAsc(Producto::getNombre))
                .stream()
                .filter(producto -> criterio == null
                        || normalize(producto.getNombre()).contains(criterio)
                        || ProductoResponse.codigo(producto.getIdProducto()).toLowerCase(Locale.ROOT)
                        .contains(criterio))
                .filter(producto -> categoriaNormalizada == null
                        || normalize(producto.getCategoria()).contains(categoriaNormalizada))
                .map(ProductoResponse::from)
                .toList();
    }

    @Transactional
    public ProductoResponse agregar(ProductoCreateRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.nombre().trim());
        producto.setCategoria(request.categoria().trim());
        producto.setDescripcion(blankToNull(request.descripcion()));
        producto.setPrecio(request.precio());
        producto.setStockActual(request.cantidadInicial());
        producto.setStockMinimo(request.stockMinimo());
        producto.setActivo(true);
        productoMapper.insert(producto);
        return ProductoResponse.from(producto);
    }

    @Transactional
    public ProductoResponse actualizarStock(Long idProducto,
                                             StockUpdateRequest request,
                                             String nombreUsuario) {
        Producto producto = requireActivo(idProducto);
        long cantidadAnterior = producto.getStockActual();
        long nuevaCantidad = request.nuevaCantidad();

        if (cantidadAnterior != nuevaCantidad) {
            producto.setStockActual(nuevaCantidad);
            productoMapper.updateById(producto);

            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setIdProducto(producto.getIdProducto());
            movimiento.setIdVeterinario(requireVeterinarioId(nombreUsuario));
            movimiento.setTipo("AJUSTE");
            movimiento.setCantidad(Math.abs(nuevaCantidad - cantidadAnterior));
            movimiento.setFechaHora(LocalDateTime.now());
            movimiento.setMotivo("Stock actualizado de %d a %d".formatted(cantidadAnterior, nuevaCantidad));
            movimientoMapper.insert(movimiento);
        }

        return ProductoResponse.from(producto);
    }

    @Transactional
    public void eliminar(Long idProducto) {
        Producto producto = requireActivo(idProducto);
        producto.setActivo(false);
        productoMapper.updateById(producto);
    }

    private Producto requireActivo(Long idProducto) {
        Producto producto = productoMapper.selectById(idProducto);
        if (producto == null || !Boolean.TRUE.equals(producto.getActivo())) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Producto no encontrado");
        }
        return producto;
    }

    private Long requireVeterinarioId(String nombreUsuario) {
        Usuario usuario = usuarioMapper.selectOne(Wrappers.<Usuario>lambdaQuery()
                .eq(Usuario::getNombreUsuario, nombreUsuario)
                .eq(Usuario::getActivo, true));
        if (usuario == null || !"VETERINARIO".equals(usuario.getRol())) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Veterinario no autorizado");
        }
        return usuario.getIdUsuario();
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim().toLowerCase(Locale.ROOT) : null;
    }
}
