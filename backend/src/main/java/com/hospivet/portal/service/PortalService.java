package com.hospivet.portal.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hospivet.auth.mapper.UsuarioMapper;
import com.hospivet.auth.model.Usuario;
import com.hospivet.cita.mapper.CitaMapper;
import com.hospivet.cita.model.Cita;
import com.hospivet.cita.service.CitaService;
import com.hospivet.cliente.mapper.ClienteMapper;
import com.hospivet.cliente.model.Cliente;
import com.hospivet.common.exception.BusinessException;
import com.hospivet.inventario.mapper.ProductoMapper;
import com.hospivet.inventario.model.Producto;
import com.hospivet.mascota.mapper.MascotaMapper;
import com.hospivet.mascota.model.Mascota;
import com.hospivet.portal.dto.CitaSolicitadaResponse;
import com.hospivet.portal.dto.HorarioResponse;
import com.hospivet.portal.dto.MascotaPortalResponse;
import com.hospivet.portal.dto.PromocionPortalResponse;
import com.hospivet.portal.dto.ServicioPortalResponse;
import com.hospivet.portal.dto.SolicitudCitaRequest;
import com.hospivet.promocion.mapper.PromocionMapper;
import com.hospivet.promocion.mapper.PromocionProductoMapper;
import com.hospivet.promocion.mapper.PromocionServicioMapper;
import com.hospivet.promocion.model.Promocion;
import com.hospivet.promocion.model.PromocionProducto;
import com.hospivet.promocion.model.PromocionServicio;
import com.hospivet.servicio.mapper.ServicioMapper;
import com.hospivet.servicio.model.Servicio;
import com.hospivet.servicio.service.ServicioService;
import com.hospivet.veterinario.mapper.VeterinarioMapper;
import com.hospivet.veterinario.service.VeterinarioSesionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * R10 Consulta de información (Cliente): SD-30 a SD-32. Todo se filtra por el cliente de la sesión; el cliente
 * solo consulta y registra sus propias citas (alcance 2.5). Decisiones del plan R10: D-21.
 */
@Service
public class PortalService {

    public static final String HORARIO_NO_DISPONIBLE = "Horario no disponible, seleccione otro";
    public static final String CITA_SOLICITADA = "Cita solicitada";
    /** N-05 A: el motivo es obligatorio (D-12) y la interfaz no lo pide. */
    public static final String MOTIVO = "Solicitada por el cliente";
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    private final UsuarioMapper usuarioMapper;
    private final ClienteMapper clienteMapper;
    private final MascotaMapper mascotaMapper;
    private final CitaMapper citaMapper;
    private final VeterinarioMapper veterinarioMapper;
    private final ServicioMapper servicioMapper;
    private final ProductoMapper productoMapper;
    private final PromocionMapper promocionMapper;
    private final PromocionServicioMapper promocionServicioMapper;
    private final PromocionProductoMapper promocionProductoMapper;
    private final ServicioService servicioService;
    private final CitaService citaService;
    private final VeterinarioSesionService veterinarioSesionService;
    private final HorarioAtencion horarioAtencion;

    public PortalService(UsuarioMapper usuarioMapper, ClienteMapper clienteMapper, MascotaMapper mascotaMapper,
                         CitaMapper citaMapper, VeterinarioMapper veterinarioMapper, ServicioMapper servicioMapper,
                         ProductoMapper productoMapper, PromocionMapper promocionMapper,
                         PromocionServicioMapper promocionServicioMapper,
                         PromocionProductoMapper promocionProductoMapper, ServicioService servicioService,
                         CitaService citaService, VeterinarioSesionService veterinarioSesionService,
                         HorarioAtencion horarioAtencion) {
        this.usuarioMapper = usuarioMapper;
        this.clienteMapper = clienteMapper;
        this.mascotaMapper = mascotaMapper;
        this.citaMapper = citaMapper;
        this.veterinarioMapper = veterinarioMapper;
        this.servicioMapper = servicioMapper;
        this.productoMapper = productoMapper;
        this.promocionMapper = promocionMapper;
        this.promocionServicioMapper = promocionServicioMapper;
        this.promocionProductoMapper = promocionProductoMapper;
        this.servicioService = servicioService;
        this.citaService = citaService;
        this.veterinarioSesionService = veterinarioSesionService;
        this.horarioAtencion = horarioAtencion;
    }

    /** Campo MASCOTA: solo las mascotas activas del cliente de la sesión (CA-09). */
    public List<MascotaPortalResponse> mascotas(String nombreUsuario) {
        Cliente cliente = requireCliente(nombreUsuario);
        return mascotaMapper.selectList(Wrappers.<Mascota>lambdaQuery()
                        .eq(Mascota::getIdCliente, cliente.getIdCliente())
                        .eq(Mascota::getActivo, true)
                        .orderByAsc(Mascota::getNombre))
                .stream().map(mascota -> new MascotaPortalResponse(mascota.getIdMascota(), mascota.getNombre()))
                .toList();
    }

    /**
     * R10.1.1: horarios del día (N-03). Un horario está libre si algún veterinario activo no tiene otra cita
     * PROGRAMADA que se solape (D-11, N-04). Hoy solo se muestran los horarios que aún no pasaron (D-10).
     */
    public List<HorarioResponse> horarios(LocalDate fecha, Long idServicio, String nombreUsuario) {
        requireCliente(nombreUsuario);
        if (fecha == null || idServicio == null) {
            return List.of();
        }
        Servicio servicio = servicioService.requireActivo(idServicio);
        List<Long> veterinarios = veterinariosActivos();
        LocalDateTime ahora = LocalDateTime.now();
        List<HorarioResponse> horarios = new ArrayList<>();
        for (LocalTime hora : horarioAtencion.horasDeInicio(fecha, servicio.getDuracionMin())) {
            LocalDateTime inicio = fecha.atTime(hora);
            if (!inicio.isAfter(ahora)) {
                continue;
            }
            boolean libre = veterinarios.stream()
                    .anyMatch(idVeterinario -> citaService.horarioLibre(idVeterinario, inicio, servicio, null));
            horarios.add(new HorarioResponse(hora.format(HORA), libre));
        }
        return horarios;
    }

    /**
     * SD-30 registrar(fecha, horario, mascota): valida que el horario siga disponible (R10.1.4) bloqueando la
     * agenda del veterinario (D-13) y registra la cita PROGRAMADA (R10.1.6). Si ya no está libre: R10.1.5.
     */
    @Transactional
    public CitaSolicitadaResponse solicitarCita(SolicitudCitaRequest request, String nombreUsuario) {
        Cliente cliente = requireCliente(nombreUsuario);
        Mascota mascota = mascotaMapper.selectById(request.idMascota());
        if (mascota == null || !Boolean.TRUE.equals(mascota.getActivo())
                || !cliente.getIdCliente().equals(mascota.getIdCliente())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "Datos inválidos",
                    Map.of("idMascota", "Seleccione la mascota"));
        }
        Servicio servicio = servicioService.requireActivo(request.idServicio());
        LocalTime hora = LocalTime.parse(request.hora(), HORA);
        LocalDateTime inicio = request.fecha().atTime(hora);
        boolean horarioDeAtencion = horarioAtencion.horasDeInicio(request.fecha(), servicio.getDuracionMin())
                .contains(hora);
        if (!horarioDeAtencion || !inicio.isAfter(LocalDateTime.now())) {
            throw new BusinessException(HttpStatus.CONFLICT, HORARIO_NO_DISPONIBLE);
        }

        for (Long idVeterinario : veterinariosActivos()) {
            veterinarioSesionService.bloquearAgenda(idVeterinario);
            if (citaService.horarioLibre(idVeterinario, inicio, servicio, null)) {
                Cita cita = new Cita();
                cita.setIdCliente(cliente.getIdCliente());
                cita.setIdMascota(mascota.getIdMascota());
                cita.setIdVeterinario(idVeterinario);
                cita.setIdServicio(servicio.getIdServicio());
                cita.setFechaHora(inicio);
                cita.setEstado("PROGRAMADA");
                cita.setMotivo(MOTIVO);
                citaMapper.insert(cita);
                return new CitaSolicitadaResponse(cita.getIdCita(), CITA_SOLICITADA);
            }
        }
        throw new BusinessException(HttpStatus.CONFLICT, HORARIO_NO_DISPONIBLE);
    }

    /** SD-31: servicios activos con precio y descripción (R10.2.1–R10.2.4). */
    public List<ServicioPortalResponse> servicios() {
        return servicioService.listarActivos().stream()
                .map(servicio -> new ServicioPortalResponse(servicio.idServicio(), servicio.nombre(),
                        servicio.descripcion(), servicio.precio()))
                .toList();
    }

    /** SD-32: promociones PUBLICADAS vigentes hoy, con su detalle armado desde sus ítems (N-08 A). */
    public List<PromocionPortalResponse> promociones() {
        LocalDate hoy = LocalDate.now();
        return promocionMapper.selectList(Wrappers.<Promocion>lambdaQuery()
                        .eq(Promocion::getEstado, "PUBLICADA")
                        .le(Promocion::getFechaInicio, hoy)
                        .ge(Promocion::getFechaFin, hoy)
                        .orderByAsc(Promocion::getFechaFin)
                        .orderByAsc(Promocion::getTitulo))
                .stream()
                .map(promocion -> new PromocionPortalResponse(promocion.getIdPromocion(), promocion.getTitulo(),
                        promocion.getDescripcion(), promocion.getPorcentajeDescuento(), promocion.getFechaInicio(),
                        promocion.getFechaFin(), detalle(promocion)))
                .toList();
    }

    private String detalle(Promocion promocion) {
        List<String> items = new ArrayList<>();
        promocionServicioMapper.selectList(Wrappers.<PromocionServicio>lambdaQuery()
                        .eq(PromocionServicio::getIdPromocion, promocion.getIdPromocion()))
                .forEach(item -> {
                    Servicio servicio = servicioMapper.selectById(item.getIdServicio());
                    if (servicio != null) items.add(servicio.getNombre());
                });
        promocionProductoMapper.selectList(Wrappers.<PromocionProducto>lambdaQuery()
                        .eq(PromocionProducto::getIdPromocion, promocion.getIdPromocion()))
                .forEach(item -> {
                    Producto producto = productoMapper.selectById(item.getIdProducto());
                    if (producto != null) items.add(producto.getNombre());
                });
        String aplicable = items.isEmpty() ? "Aplicable a todos los servicios y productos."
                : "Aplicable a: " + String.join(", ", items) + ".";
        return aplicable + " Descuento: " + porcentaje(promocion.getPorcentajeDescuento()) + " %.";
    }

    private static String porcentaje(BigDecimal valor) {
        return valor.stripTrailingZeros().toPlainString().replace('.', ',');
    }

    /** N-04 A: veterinarios activos en orden; hoy la veterinaria tiene uno. */
    private List<Long> veterinariosActivos() {
        return usuarioMapper.selectList(Wrappers.<Usuario>lambdaQuery()
                        .eq(Usuario::getRol, "VETERINARIO")
                        .eq(Usuario::getActivo, true)
                        .orderByAsc(Usuario::getIdUsuario))
                .stream()
                .map(Usuario::getIdUsuario)
                .filter(id -> veterinarioMapper.selectById(id) != null)
                .toList();
    }

    /** El cliente sale siempre de la sesión, nunca de un dato enviado por la pantalla. */
    private Cliente requireCliente(String nombreUsuario) {
        Usuario usuario = usuarioMapper.selectOne(Wrappers.<Usuario>lambdaQuery()
                .eq(Usuario::getNombreUsuario, nombreUsuario)
                .eq(Usuario::getActivo, true));
        Cliente cliente = usuario == null || !"CLIENTE".equals(usuario.getRol()) ? null
                : clienteMapper.selectOne(Wrappers.<Cliente>lambdaQuery()
                .eq(Cliente::getIdUsuario, usuario.getIdUsuario())
                .eq(Cliente::getActivo, true));
        if (cliente == null) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Acceso no autorizado");
        }
        return cliente;
    }
}
