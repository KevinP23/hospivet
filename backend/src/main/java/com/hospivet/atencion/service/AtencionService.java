package com.hospivet.atencion.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hospivet.atencion.dto.AtencionResponse;
import com.hospivet.atencion.dto.CitaProgramadaResponse;
import com.hospivet.atencion.dto.DiagnosticoCreateRequest;
import com.hospivet.atencion.dto.DiagnosticoResponse;
import com.hospivet.atencion.dto.HistorialResponse;
import com.hospivet.atencion.dto.RecetaCreateRequest;
import com.hospivet.atencion.dto.RecetaResponse;
import com.hospivet.atencion.dto.TratamientoCreateRequest;
import com.hospivet.atencion.dto.TratamientoResponse;
import com.hospivet.atencion.mapper.AtencionMedicaMapper;
import com.hospivet.atencion.mapper.DiagnosticoMapper;
import com.hospivet.atencion.mapper.RecetaMapper;
import com.hospivet.atencion.mapper.TratamientoMapper;
import com.hospivet.atencion.model.AtencionMedica;
import com.hospivet.atencion.model.Diagnostico;
import com.hospivet.atencion.model.Receta;
import com.hospivet.atencion.model.Tratamiento;
import com.hospivet.cita.dto.CitaResponse;
import com.hospivet.cita.mapper.CitaMapper;
import com.hospivet.cita.model.Cita;
import com.hospivet.common.exception.BusinessException;
import com.hospivet.mascota.mapper.MascotaMapper;
import com.hospivet.mascota.model.Mascota;
import com.hospivet.veterinario.service.VeterinarioSesionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AtencionService {

    private final AtencionMedicaMapper atencionMapper;
    private final DiagnosticoMapper diagnosticoMapper;
    private final TratamientoMapper tratamientoMapper;
    private final RecetaMapper recetaMapper;
    private final CitaMapper citaMapper;
    private final MascotaMapper mascotaMapper;
    private final VeterinarioSesionService veterinarioSesionService;

    public AtencionService(AtencionMedicaMapper atencionMapper, DiagnosticoMapper diagnosticoMapper,
                           TratamientoMapper tratamientoMapper, RecetaMapper recetaMapper,
                           CitaMapper citaMapper, MascotaMapper mascotaMapper,
                           VeterinarioSesionService veterinarioSesionService) {
        this.atencionMapper = atencionMapper;
        this.diagnosticoMapper = diagnosticoMapper;
        this.tratamientoMapper = tratamientoMapper;
        this.recetaMapper = recetaMapper;
        this.citaMapper = citaMapper;
        this.mascotaMapper = mascotaMapper;
        this.veterinarioSesionService = veterinarioSesionService;
    }

    public List<AtencionResponse> listarAtenciones(Long idMascota) {
        LambdaQueryWrapper<AtencionMedica> query = Wrappers.<AtencionMedica>lambdaQuery()
                .orderByDesc(AtencionMedica::getFechaHora);
        if (idMascota != null) query.eq(AtencionMedica::getIdMascota, idMascota);
        List<AtencionMedica> atenciones = atencionMapper.selectList(query);
        Map<Long, Mascota> mascotas = byId(mascotaMapper,
                atenciones.stream().map(AtencionMedica::getIdMascota).toList(), Mascota::getIdMascota);
        return atenciones.stream().map(atencion -> toResponse(atencion, mascotas)).toList();
    }

    /** Citas programadas del veterinario de la sesión que pueden originar una atención (N-01). */
    public List<CitaProgramadaResponse> listarCitasProgramadas(Long idMascota, String nombreUsuario) {
        Long idVeterinario = veterinarioSesionService.requireIdVeterinario(nombreUsuario);
        return citaMapper.selectList(Wrappers.<Cita>lambdaQuery()
                        .eq(Cita::getIdVeterinario, idVeterinario)
                        .eq(Cita::getIdMascota, idMascota)
                        .eq(Cita::getEstado, "PROGRAMADA")
                        .orderByAsc(Cita::getFechaHora))
                .stream()
                .map(cita -> new CitaProgramadaResponse(cita.getIdCita(), CitaResponse.codigo(cita.getIdCita()),
                        cita.getFechaHora(), cita.getMotivo()))
                .toList();
    }

    public List<DiagnosticoResponse> listarDiagnosticos(Long idMascota) {
        List<AtencionMedica> atenciones = idMascota == null
                ? atencionMapper.selectList(null)
                : atencionMapper.selectList(Wrappers.<AtencionMedica>lambdaQuery()
                        .eq(AtencionMedica::getIdMascota, idMascota));
        if (atenciones.isEmpty()) return List.of();
        Map<Long, AtencionMedica> porId = atenciones.stream()
                .collect(Collectors.toMap(AtencionMedica::getIdAtencion, Function.identity()));
        Map<Long, Mascota> mascotas = byId(mascotaMapper,
                atenciones.stream().map(AtencionMedica::getIdMascota).toList(), Mascota::getIdMascota);
        return diagnosticoMapper.selectList(Wrappers.<Diagnostico>lambdaQuery()
                        .in(Diagnostico::getIdAtencion, porId.keySet())
                        .orderByDesc(Diagnostico::getFechaRegistro))
                .stream()
                .map(diagnostico -> toResponse(diagnostico, porId.get(diagnostico.getIdAtencion()), mascotas))
                .toList();
    }

    /** SD-23 registrar(descripcion, atencion). */
    @Transactional
    public DiagnosticoResponse registrarDiagnostico(DiagnosticoCreateRequest request, String nombreUsuario) {
        AtencionMedica atencion = request.idAtencion() != null
                ? requireAtencion(request.idAtencion())
                : atenderCita(request.idCita(), nombreUsuario);

        Diagnostico diagnostico = new Diagnostico();
        diagnostico.setIdAtencion(atencion.getIdAtencion());
        diagnostico.setDescripcion(request.descripcion().trim());
        diagnostico.setFechaRegistro(LocalDateTime.now());
        diagnosticoMapper.insert(diagnostico);
        Map<Long, Mascota> mascotas = byId(mascotaMapper, List.of(atencion.getIdMascota()), Mascota::getIdMascota);
        return toResponse(diagnostico, atencion, mascotas);
    }

    /** SD-24 registrar(indicaciones, diagnostico). */
    @Transactional
    public TratamientoResponse registrarTratamiento(Long idDiagnostico, TratamientoCreateRequest request) {
        if (diagnosticoMapper.selectById(idDiagnostico) == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Diagnóstico no encontrado");
        }
        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setIdDiagnostico(idDiagnostico);
        tratamiento.setIndicaciones(request.indicaciones().trim());
        tratamiento.setDuracion(blankToNull(request.duracion()));
        tratamiento.setFechaInicio(request.fechaInicio());
        tratamiento.setFechaFin(request.fechaFin());
        tratamientoMapper.insert(tratamiento);
        return new TratamientoResponse(tratamiento.getIdTratamiento(), idDiagnostico, tratamiento.getIndicaciones(),
                tratamiento.getDuracion(), tratamiento.getFechaInicio(), tratamiento.getFechaFin());
    }

    /** SD-25 registrar(medicamento, dosis, atencion). */
    @Transactional
    public RecetaResponse registrarReceta(Long idAtencion, RecetaCreateRequest request) {
        requireAtencion(idAtencion);
        Receta receta = new Receta();
        receta.setIdAtencion(idAtencion);
        receta.setMedicamento(request.medicamento().trim());
        receta.setDosis(request.dosis().trim());
        receta.setFrecuencia(request.frecuencia().trim());
        receta.setDuracion(blankToNull(request.duracion()));
        receta.setIndicaciones(blankToNull(request.indicaciones()));
        recetaMapper.insert(receta);
        return new RecetaResponse(receta.getIdReceta(), idAtencion, receta.getMedicamento(), receta.getDosis(),
                receta.getFrecuencia(), receta.getDuracion(), receta.getIndicaciones());
    }

    /** SD-22 consultarHistorial(idMascota): una consulta por tabla, la atención más reciente primero. */
    public List<HistorialResponse> consultarHistorial(Long idMascota) {
        List<AtencionMedica> atenciones = atencionMapper.selectList(Wrappers.<AtencionMedica>lambdaQuery()
                .eq(AtencionMedica::getIdMascota, idMascota)
                .orderByDesc(AtencionMedica::getFechaHora));
        if (atenciones.isEmpty()) return List.of();

        List<Long> idsAtencion = atenciones.stream().map(AtencionMedica::getIdAtencion).toList();
        List<Diagnostico> diagnosticos = diagnosticoMapper.selectList(Wrappers.<Diagnostico>lambdaQuery()
                .in(Diagnostico::getIdAtencion, idsAtencion)
                .orderByAsc(Diagnostico::getFechaRegistro));
        List<Tratamiento> tratamientos = diagnosticos.isEmpty() ? List.of()
                : tratamientoMapper.selectList(Wrappers.<Tratamiento>lambdaQuery()
                        .in(Tratamiento::getIdDiagnostico,
                                diagnosticos.stream().map(Diagnostico::getIdDiagnostico).toList())
                        .orderByAsc(Tratamiento::getIdTratamiento));
        List<Receta> recetas = recetaMapper.selectList(Wrappers.<Receta>lambdaQuery()
                .in(Receta::getIdAtencion, idsAtencion)
                .orderByAsc(Receta::getIdReceta));
        Map<Long, Cita> citas = byId(citaMapper, atenciones.stream()
                .map(AtencionMedica::getIdCita).filter(id -> id != null).toList(), Cita::getIdCita);

        Map<Long, Long> atencionDeDiagnostico = diagnosticos.stream()
                .collect(Collectors.toMap(Diagnostico::getIdDiagnostico, Diagnostico::getIdAtencion));
        Map<Long, List<String>> diagnosticosPorAtencion = diagnosticos.stream().collect(Collectors.groupingBy(
                Diagnostico::getIdAtencion, Collectors.mapping(Diagnostico::getDescripcion, Collectors.toList())));
        Map<Long, List<String>> tratamientosPorAtencion = tratamientos.stream().collect(Collectors.groupingBy(
                tratamiento -> atencionDeDiagnostico.get(tratamiento.getIdDiagnostico()),
                Collectors.mapping(Tratamiento::getIndicaciones, Collectors.toList())));
        Map<Long, List<String>> recetasPorAtencion = recetas.stream().collect(Collectors.groupingBy(
                Receta::getIdAtencion, Collectors.mapping(Receta::getMedicamento, Collectors.toList())));

        return atenciones.stream()
                .sorted(Comparator.comparing(AtencionMedica::getFechaHora).reversed())
                .map(atencion -> new HistorialResponse(
                        atencion.getIdAtencion(),
                        AtencionResponse.codigo(atencion.getIdAtencion()),
                        atencion.getFechaHora(),
                        // N-06: la columna Atención muestra el motivo de la cita de origen.
                        atencion.getIdCita() == null ? null : citas.get(atencion.getIdCita()).getMotivo(),
                        diagnosticosPorAtencion.getOrDefault(atencion.getIdAtencion(), List.of()),
                        tratamientosPorAtencion.getOrDefault(atencion.getIdAtencion(), List.of()),
                        recetasPorAtencion.getOrDefault(atencion.getIdAtencion(), List.of())))
                .toList();
    }

    /**
     * N-01 y N-02: la atención nace de una cita programada del veterinario de la sesión
     * y la cita pasa a ATENDIDA en la misma transacción.
     */
    private AtencionMedica atenderCita(Long idCita, String nombreUsuario) {
        Long idVeterinario = veterinarioSesionService.requireIdVeterinario(nombreUsuario);
        veterinarioSesionService.bloquearAgenda(idVeterinario);
        Cita cita = idCita == null ? null : citaMapper.selectById(idCita);
        if (cita == null || !idVeterinario.equals(cita.getIdVeterinario())) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Atención médica no encontrada");
        }
        if (!"PROGRAMADA".equals(cita.getEstado())) {
            throw new BusinessException(HttpStatus.CONFLICT, "La cita ya no está programada");
        }

        AtencionMedica atencion = new AtencionMedica();
        atencion.setIdCita(cita.getIdCita());
        atencion.setIdMascota(cita.getIdMascota());
        atencion.setIdVeterinario(idVeterinario);
        atencion.setFechaHora(LocalDateTime.now());
        atencionMapper.insert(atencion);

        cita.setEstado("ATENDIDA");
        citaMapper.updateById(cita);
        return atencion;
    }

    private AtencionMedica requireAtencion(Long idAtencion) {
        AtencionMedica atencion = idAtencion == null ? null : atencionMapper.selectById(idAtencion);
        if (atencion == null) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "Atención médica no encontrada");
        }
        return atencion;
    }

    private AtencionResponse toResponse(AtencionMedica atencion, Map<Long, Mascota> mascotas) {
        Mascota mascota = mascotas.get(atencion.getIdMascota());
        return new AtencionResponse(atencion.getIdAtencion(), AtencionResponse.codigo(atencion.getIdAtencion()),
                atencion.getIdMascota(), mascota.getNombre(), atencion.getFechaHora());
    }

    private DiagnosticoResponse toResponse(Diagnostico diagnostico, AtencionMedica atencion,
                                           Map<Long, Mascota> mascotas) {
        Mascota mascota = mascotas.get(atencion.getIdMascota());
        return new DiagnosticoResponse(diagnostico.getIdDiagnostico(),
                DiagnosticoResponse.codigo(diagnostico.getIdDiagnostico()), atencion.getIdAtencion(),
                atencion.getIdMascota(), mascota.getNombre(), diagnostico.getDescripcion(),
                diagnostico.getFechaRegistro());
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
