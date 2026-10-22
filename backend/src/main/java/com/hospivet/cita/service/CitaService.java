package com.hospivet.cita.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hospivet.cita.dto.CitaCreateRequest;
import com.hospivet.cita.dto.CitaResponse;
import com.hospivet.cita.dto.CitaUpdateRequest;
import com.hospivet.cita.mapper.CitaMapper;
import com.hospivet.cita.model.Cita;
import com.hospivet.cliente.mapper.ClienteMapper;
import com.hospivet.cliente.model.Cliente;
import com.hospivet.cliente.service.ClienteService;
import com.hospivet.common.exception.BusinessException;
import com.hospivet.mascota.mapper.MascotaMapper;
import com.hospivet.mascota.model.Mascota;
import com.hospivet.servicio.mapper.ServicioMapper;
import com.hospivet.servicio.model.Servicio;
import com.hospivet.servicio.service.ServicioService;
import com.hospivet.veterinario.mapper.VeterinarioMapper;
import com.hospivet.veterinario.model.Veterinario;
import com.hospivet.veterinario.service.VeterinarioSesionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CitaService {

    private final CitaMapper citaMapper;
    private final ClienteMapper clienteMapper;
    private final MascotaMapper mascotaMapper;
    private final ServicioMapper servicioMapper;
    private final VeterinarioMapper veterinarioMapper;
    private final ClienteService clienteService;
    private final ServicioService servicioService;
    private final VeterinarioSesionService veterinarioSesionService;

    public CitaService(CitaMapper citaMapper, ClienteMapper clienteMapper, MascotaMapper mascotaMapper,
                       ServicioMapper servicioMapper, VeterinarioMapper veterinarioMapper,
                       ClienteService clienteService, ServicioService servicioService,
                       VeterinarioSesionService veterinarioSesionService) {
        this.citaMapper = citaMapper;
        this.clienteMapper = clienteMapper;
        this.mascotaMapper = mascotaMapper;
        this.servicioMapper = servicioMapper;
        this.veterinarioMapper = veterinarioMapper;
        this.clienteService = clienteService;
        this.servicioService = servicioService;
        this.veterinarioSesionService = veterinarioSesionService;
    }

    public List<CitaResponse> listar(LocalDate fecha, String nombreUsuario) {
        Long idVeterinario = veterinarioSesionService.requireIdVeterinario(nombreUsuario);
        LambdaQueryWrapper<Cita> query = Wrappers.<Cita>lambdaQuery()
                .eq(Cita::getIdVeterinario, idVeterinario);
        if (fecha != null) {
            query.ge(Cita::getFechaHora, fecha.atStartOfDay())
                    .le(Cita::getFechaHora, fecha.atTime(LocalTime.MAX));
        }
        query.orderByAsc(Cita::getFechaHora);
        return toResponses(citaMapper.selectList(query));
    }

    @Transactional
    public CitaResponse registrar(CitaCreateRequest request, String nombreUsuario) {
        Long idVeterinario = veterinarioSesionService.requireIdVeterinario(nombreUsuario);
        Cliente cliente = clienteService.requireActivo(request.idCliente());
        Mascota mascota = requireMascotaDelCliente(request.idMascota(), cliente.getIdCliente());
        Servicio servicio = servicioService.requireActivo(request.idServicio());
        veterinarioSesionService.bloquearAgenda(idVeterinario);
        validateAvailability(idVeterinario, request.fechaHora(), servicio, null);

        Cita cita = new Cita();
        cita.setIdCliente(cliente.getIdCliente());
        cita.setIdMascota(mascota.getIdMascota());
        cita.setIdVeterinario(idVeterinario);
        cita.setIdServicio(servicio.getIdServicio());
        cita.setFechaHora(request.fechaHora());
        cita.setEstado("PROGRAMADA");
        cita.setMotivo(request.motivo().trim());
        cita.setObservaciones(blankToNull(request.observaciones()));
        citaMapper.insert(cita);
        return toResponse(cita);
    }

    @Transactional
    public CitaResponse modificar(Long idCita, CitaUpdateRequest request, String nombreUsuario) {
        Long idVeterinario = veterinarioSesionService.requireIdVeterinario(nombreUsuario);
        Servicio servicio = servicioService.requireActivo(request.idServicio());
        veterinarioSesionService.bloquearAgenda(idVeterinario);
        Cita cita = requireProgramada(idCita, idVeterinario);
        validateAvailability(idVeterinario, request.fechaHora(), servicio, idCita);
        cita.setIdServicio(servicio.getIdServicio());
        cita.setFechaHora(request.fechaHora());
        citaMapper.updateById(cita);
        return toResponse(cita);
    }

    @Transactional
    public CitaResponse cancelar(Long idCita, String nombreUsuario) {
        Long idVeterinario = veterinarioSesionService.requireIdVeterinario(nombreUsuario);
        Cita cita = requireProgramada(idCita, idVeterinario);
        cita.setEstado("CANCELADA");
        citaMapper.updateById(cita);
        return toResponse(cita);
    }

    /**
     * Un horario está ocupado si el intervalo [inicio, inicio + duración del servicio) se solapa
     * con el de otra cita PROGRAMADA del mismo veterinario (decisión D-11).
     */
    private void validateAvailability(Long idVeterinario, LocalDateTime inicio, Servicio servicio, Long excludedId) {
        LocalDateTime fin = inicio.plusMinutes(servicio.getDuracionMin());
        LambdaQueryWrapper<Cita> query = Wrappers.<Cita>lambdaQuery()
                .eq(Cita::getIdVeterinario, idVeterinario)
                .eq(Cita::getEstado, "PROGRAMADA")
                .ge(Cita::getFechaHora, inicio.toLocalDate().minusDays(1).atStartOfDay())
                .lt(Cita::getFechaHora, fin);
        if (excludedId != null) query.ne(Cita::getIdCita, excludedId);

        List<Cita> cercanas = citaMapper.selectList(query);
        Map<Long, Servicio> servicios = byId(servicioMapper,
                cercanas.stream().map(Cita::getIdServicio).toList(), Servicio::getIdServicio);
        boolean ocupado = cercanas.stream().anyMatch(cita -> {
            LocalDateTime finExistente = cita.getFechaHora()
                    .plusMinutes(servicios.get(cita.getIdServicio()).getDuracionMin());
            return cita.getFechaHora().isBefore(fin) && inicio.isBefore(finExistente);
        });
        if (ocupado) {
            throw new BusinessException(HttpStatus.CONFLICT, "Horario no disponible, seleccione otro");
        }
    }

    private Cita requireProgramada(Long idCita, Long idVeterinario) {
        Cita cita = citaMapper.selectById(idCita);
        if (cita == null || !idVeterinario.equals(cita.getIdVeterinario())) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Cita no encontrada");
        }
        if (!"PROGRAMADA".equals(cita.getEstado())) {
            throw new BusinessException(HttpStatus.CONFLICT, "La cita ya no está programada");
        }
        return cita;
    }

    private Mascota requireMascotaDelCliente(Long idMascota, Long idCliente) {
        Mascota mascota = mascotaMapper.selectById(idMascota);
        if (mascota == null || !Boolean.TRUE.equals(mascota.getActivo())
                || !idCliente.equals(mascota.getIdCliente())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "La mascota no pertenece al cliente seleccionado");
        }
        return mascota;
    }

    private CitaResponse toResponse(Cita cita) {
        return toResponses(List.of(cita)).get(0);
    }

    /** Carga los datos relacionados con una consulta por tabla y sin exigir que sigan activos. */
    private List<CitaResponse> toResponses(List<Cita> citas) {
        if (citas.isEmpty()) return List.of();
        Map<Long, Cliente> clientes = byId(clienteMapper,
                citas.stream().map(Cita::getIdCliente).toList(), Cliente::getIdCliente);
        Map<Long, Mascota> mascotas = byId(mascotaMapper,
                citas.stream().map(Cita::getIdMascota).toList(), Mascota::getIdMascota);
        Map<Long, Veterinario> veterinarios = byId(veterinarioMapper,
                citas.stream().map(Cita::getIdVeterinario).toList(), Veterinario::getIdVeterinario);
        Map<Long, Servicio> servicios = byId(servicioMapper,
                citas.stream().map(Cita::getIdServicio).toList(), Servicio::getIdServicio);

        return citas.stream().map(cita -> {
            Cliente cliente = clientes.get(cita.getIdCliente());
            Mascota mascota = mascotas.get(cita.getIdMascota());
            Veterinario veterinario = veterinarios.get(cita.getIdVeterinario());
            Servicio servicio = servicios.get(cita.getIdServicio());
            return new CitaResponse(cita.getIdCita(), CitaResponse.codigo(cita.getIdCita()),
                    cliente.getIdCliente(), cliente.getNombre() + " " + cliente.getApellido(),
                    mascota.getIdMascota(), mascota.getNombre(), veterinario.getIdVeterinario(),
                    veterinario.getNombre() + " " + veterinario.getApellido(), servicio.getIdServicio(),
                    servicio.getNombre(), cita.getFechaHora(), cita.getEstado(), cita.getMotivo(),
                    cita.getObservaciones());
        }).toList();
    }

    private static <T> Map<Long, T> byId(BaseMapper<T> mapper, Collection<Long> ids, Function<T, Long> idOf) {
        if (ids.isEmpty()) return Map.of();
        return mapper.selectByIds(ids.stream().distinct().toList()).stream()
                .collect(Collectors.toMap(idOf, Function.identity()));
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
