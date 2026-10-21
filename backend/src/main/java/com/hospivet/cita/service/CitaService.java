package com.hospivet.cita.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hospivet.auth.mapper.UsuarioMapper;
import com.hospivet.auth.model.Usuario;
import com.hospivet.cita.dto.CitaCreateRequest;
import com.hospivet.cita.dto.CitaResponse;
import com.hospivet.cita.dto.CitaUpdateRequest;
import com.hospivet.cita.mapper.CitaMapper;
import com.hospivet.cita.model.Cita;
import com.hospivet.cliente.model.Cliente;
import com.hospivet.cliente.service.ClienteService;
import com.hospivet.common.exception.BusinessException;
import com.hospivet.mascota.mapper.MascotaMapper;
import com.hospivet.mascota.model.Mascota;
import com.hospivet.servicio.model.Servicio;
import com.hospivet.servicio.service.ServicioService;
import com.hospivet.veterinario.mapper.VeterinarioMapper;
import com.hospivet.veterinario.model.Veterinario;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class CitaService {

    private final CitaMapper citaMapper;
    private final UsuarioMapper usuarioMapper;
    private final VeterinarioMapper veterinarioMapper;
    private final MascotaMapper mascotaMapper;
    private final ClienteService clienteService;
    private final ServicioService servicioService;

    public CitaService(CitaMapper citaMapper, UsuarioMapper usuarioMapper,
                       VeterinarioMapper veterinarioMapper, MascotaMapper mascotaMapper,
                       ClienteService clienteService, ServicioService servicioService) {
        this.citaMapper = citaMapper;
        this.usuarioMapper = usuarioMapper;
        this.veterinarioMapper = veterinarioMapper;
        this.mascotaMapper = mascotaMapper;
        this.clienteService = clienteService;
        this.servicioService = servicioService;
    }

    public List<CitaResponse> listar(LocalDate fecha, String nombreUsuario) {
        Long idVeterinario = requireVeterinarioId(nombreUsuario);
        LambdaQueryWrapper<Cita> query = Wrappers.<Cita>lambdaQuery()
                .eq(Cita::getIdVeterinario, idVeterinario);
        if (fecha != null) {
            query.ge(Cita::getFechaHora, fecha.atStartOfDay())
                    .le(Cita::getFechaHora, fecha.atTime(LocalTime.MAX));
        }
        query.orderByAsc(Cita::getFechaHora);
        return citaMapper.selectList(query).stream().map(this::toResponse).toList();
    }

    @Transactional
    public CitaResponse registrar(CitaCreateRequest request, String nombreUsuario) {
        Long idVeterinario = requireVeterinarioId(nombreUsuario);
        Cliente cliente = clienteService.requireActivo(request.idCliente());
        Mascota mascota = requireMascotaDelCliente(request.idMascota(), cliente.getIdCliente());
        servicioService.requireActivo(request.idServicio());
        validateAvailability(idVeterinario, request.fechaHora(), null);

        Cita cita = new Cita();
        cita.setIdCliente(cliente.getIdCliente());
        cita.setIdMascota(mascota.getIdMascota());
        cita.setIdVeterinario(idVeterinario);
        cita.setIdServicio(request.idServicio());
        cita.setFechaHora(request.fechaHora());
        cita.setEstado("PROGRAMADA");
        cita.setMotivo(request.motivo().trim());
        cita.setObservaciones(blankToNull(request.observaciones()));
        citaMapper.insert(cita);
        return toResponse(cita);
    }

    @Transactional
    public CitaResponse modificar(Long idCita, CitaUpdateRequest request, String nombreUsuario) {
        Long idVeterinario = requireVeterinarioId(nombreUsuario);
        Cita cita = requireProgramada(idCita, idVeterinario);
        servicioService.requireActivo(request.idServicio());
        validateAvailability(idVeterinario, request.fechaHora(), idCita);
        cita.setIdServicio(request.idServicio());
        cita.setFechaHora(request.fechaHora());
        citaMapper.updateById(cita);
        return toResponse(cita);
    }

    @Transactional
    public CitaResponse cancelar(Long idCita, String nombreUsuario) {
        Long idVeterinario = requireVeterinarioId(nombreUsuario);
        Cita cita = requireProgramada(idCita, idVeterinario);
        cita.setEstado("CANCELADA");
        citaMapper.updateById(cita);
        return toResponse(cita);
    }

    private void validateAvailability(Long idVeterinario, LocalDateTime fechaHora, Long excludedId) {
        LambdaQueryWrapper<Cita> query = Wrappers.<Cita>lambdaQuery()
                .eq(Cita::getIdVeterinario, idVeterinario)
                .eq(Cita::getFechaHora, fechaHora)
                .eq(Cita::getEstado, "PROGRAMADA");
        if (excludedId != null) query.ne(Cita::getIdCita, excludedId);
        if (citaMapper.selectCount(query) > 0) {
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

    private Long requireVeterinarioId(String nombreUsuario) {
        Usuario usuario = usuarioMapper.selectOne(Wrappers.<Usuario>lambdaQuery()
                .eq(Usuario::getNombreUsuario, nombreUsuario)
                .eq(Usuario::getActivo, true));
        if (usuario == null || !"VETERINARIO".equals(usuario.getRol())
                || veterinarioMapper.selectById(usuario.getIdUsuario()) == null) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "Veterinario no autorizado");
        }
        return usuario.getIdUsuario();
    }

    private CitaResponse toResponse(Cita cita) {
        Cliente cliente = clienteService.requireActivo(cita.getIdCliente());
        Mascota mascota = mascotaMapper.selectById(cita.getIdMascota());
        Veterinario veterinario = veterinarioMapper.selectById(cita.getIdVeterinario());
        Servicio servicio = servicioService.requireActivo(cita.getIdServicio());
        return new CitaResponse(cita.getIdCita(), CitaResponse.codigo(cita.getIdCita()),
                cliente.getIdCliente(), cliente.getNombre() + " " + cliente.getApellido(),
                mascota.getIdMascota(), mascota.getNombre(), veterinario.getIdVeterinario(),
                veterinario.getNombre() + " " + veterinario.getApellido(), servicio.getIdServicio(),
                servicio.getNombre(), cita.getFechaHora(), cita.getEstado(), cita.getMotivo(),
                cita.getObservaciones());
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
