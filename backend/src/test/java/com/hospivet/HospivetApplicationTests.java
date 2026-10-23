package com.hospivet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospivet.atencion.mapper.AtencionMedicaMapper;
import com.hospivet.atencion.mapper.DiagnosticoMapper;
import com.hospivet.atencion.mapper.RecetaMapper;
import com.hospivet.atencion.mapper.TratamientoMapper;
import com.hospivet.auth.mapper.UsuarioMapper;
import com.hospivet.promocion.mapper.PromocionMapper;
import com.hospivet.promocion.mapper.PromocionProductoMapper;
import com.hospivet.promocion.mapper.PromocionServicioMapper;
import com.hospivet.auth.model.Usuario;
import com.hospivet.cita.mapper.CitaMapper;
import com.hospivet.cliente.mapper.ClienteMapper;
import com.hospivet.inventario.mapper.MovimientoInventarioMapper;
import com.hospivet.inventario.mapper.ProductoMapper;
import com.hospivet.mascota.mapper.MascotaMapper;
import com.hospivet.servicio.mapper.ServicioMapper;
import com.hospivet.servicio.model.Servicio;
import com.hospivet.veterinario.mapper.VeterinarioMapper;
import com.hospivet.veterinario.model.Veterinario;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
class HospivetApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioMapper usuarioMapper;

    @Autowired
    private VeterinarioMapper veterinarioMapper;

    @Autowired
    private ClienteMapper clienteMapper;

    @Autowired
    private MascotaMapper mascotaMapper;

    @Autowired
    private ProductoMapper productoMapper;

    @Autowired
    private MovimientoInventarioMapper movimientoInventarioMapper;

    @Autowired
    private CitaMapper citaMapper;

    @Autowired
    private AtencionMedicaMapper atencionMedicaMapper;

    @Autowired
    private DiagnosticoMapper diagnosticoMapper;

    @Autowired
    private TratamientoMapper tratamientoMapper;

    @Autowired
    private RecetaMapper recetaMapper;

    @Autowired
    private PromocionMapper promocionMapper;

    @Autowired
    private PromocionServicioMapper promocionServicioMapper;

    @Autowired
    private PromocionProductoMapper promocionProductoMapper;

    @Autowired
    private ServicioMapper servicioMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long servicioInicialId;

    @BeforeEach
    void prepararVeterinario() {
        promocionServicioMapper.delete(null);
        promocionProductoMapper.delete(null);
        promocionMapper.delete(null);
        recetaMapper.delete(null);
        tratamientoMapper.delete(null);
        diagnosticoMapper.delete(null);
        atencionMedicaMapper.delete(null);
        citaMapper.delete(null);
        servicioMapper.delete(null);
        movimientoInventarioMapper.delete(null);
        productoMapper.delete(null);
        mascotaMapper.delete(null);
        clienteMapper.delete(null);
        veterinarioMapper.delete(null);
        usuarioMapper.delete(null);

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("veterinario");
        usuario.setClaveHash(passwordEncoder.encode("clave-segura"));
        usuario.setRol("VETERINARIO");
        usuario.setActivo(true);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuarioMapper.insert(usuario);

        Veterinario veterinario = new Veterinario();
        veterinario.setIdVeterinario(usuario.getIdUsuario());
        veterinario.setCi("1000001");
        veterinario.setNombre("Ana");
        veterinario.setApellido("Rojas");
        veterinarioMapper.insert(veterinario);

        Servicio servicio = new Servicio();
        servicio.setNombre("Consulta general");
        servicio.setDescripcion("Consulta veterinaria general");
        servicio.setPrecio(BigDecimal.ZERO);
        servicio.setDuracionMin(30);
        servicio.setActivo(true);
        servicioMapper.insert(servicio);
        servicioInicialId = servicio.getIdServicio();
    }

    @Test
    void completaFlujoLoginClienteYMascota() throws Exception {
        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"usuario":"veterinario","contrasena":"clave-segura"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("VETERINARIO"))
                .andReturn();

        HttpSession session = login.getRequest().getSession(false);
        assertThat(session).isNotNull();

        MvcResult cliente = mockMvc.perform(post("/api/clientes")
                        .session((org.springframework.mock.web.MockHttpSession) session)
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                  "ci":"7654321",
                                  "nombre":"Luis",
                                  "apellido":"Pérez",
                                  "telefono":"70000000",
                                  "correo":"luis@example.test",
                                  "direccion":"Zona Central"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ci").value("7654321"))
                .andReturn();

        JsonNode clienteJson = objectMapper.readTree(cliente.getResponse().getContentAsString());
        long idCliente = clienteJson.get("idCliente").asLong();

        mockMvc.perform(post("/api/mascotas")
                        .session((org.springframework.mock.web.MockHttpSession) session)
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                  "idCliente":%d,
                                  "nombre":"Luna",
                                  "especie":"Canino",
                                  "raza":"Mestiza",
                                  "fechaNacimiento":"2022-05-10",
                                  "peso":12.40,
                                  "sexo":"H",
                                  "observaciones":"Sin observaciones"
                                }
                                """.formatted(idCliente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCliente").value(idCliente))
                .andExpect(jsonPath("$.nombre").value("Luna"));

        assertThat(clienteMapper.selectCount(null)).isEqualTo(1);
        assertThat(mascotaMapper.selectCount(null)).isEqualTo(1);
    }

    @Test
    void rechazaCredencialesIncorrectasSinCrearSesionAutenticada() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"usuario":"veterinario","contrasena":"incorrecta"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Contraseña incorrecta"))
                .andReturn();

        assertThat(result.getRequest().getSession(false)).isNull();
    }

    @Test
    void verificaLaContrasenaAntesDelRol() throws Exception {
        Usuario cliente = new Usuario();
        cliente.setNombreUsuario("cliente");
        cliente.setClaveHash(passwordEncoder.encode("clave-cliente"));
        cliente.setRol("CLIENTE");
        cliente.setActivo(true);
        cliente.setFechaCreacion(LocalDateTime.now());
        usuarioMapper.insert(cliente);

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"usuario":"cliente","contrasena":"incorrecta"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Contraseña incorrecta"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"usuario":"cliente","contrasena":"clave-cliente"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void devuelveErroresPorCampoYNoRegistraClienteInvalido() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                .user("veterinario").roles("VETERINARIO"))
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {"ci":"ABC","nombre":"","apellido":"","telefono":""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Revise los datos del formulario"))
                .andExpect(jsonPath("$.fieldErrors.ci").value("CI inválido"))
                .andExpect(jsonPath("$.fieldErrors.nombre").exists())
                .andExpect(jsonPath("$.fieldErrors.apellido").exists())
                .andExpect(jsonPath("$.fieldErrors.telefono").exists());

        assertThat(clienteMapper.selectCount(null)).isZero();
    }

    @Test
    void completaFlujoDeInventarioConTrazabilidadDeStock() throws Exception {
        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"usuario":"veterinario","contrasena":"clave-segura"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        HttpSession session = login.getRequest().getSession(false);
        MvcResult producto = mockMvc.perform(post("/api/inventario/productos")
                        .session((org.springframework.mock.web.MockHttpSession) session)
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                  "nombre":"Amoxicilina 250 mg",
                                  "categoria":"Medicamento",
                                  "descripcion":"Caja de 12 cápsulas",
                                  "precio":25.00,
                                  "cantidadInicial":42,
                                  "stockMinimo":5
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"))
                .andReturn();

        long idProducto = objectMapper.readTree(producto.getResponse().getContentAsString())
                .get("idProducto").asLong();

        mockMvc.perform(patch("/api/inventario/productos/{idProducto}/stock", idProducto)
                        .session((org.springframework.mock.web.MockHttpSession) session)
                        .with(csrf())
                        .contentType("application/json")
                        .content("{\"nuevaCantidad\":5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockActual").value(5))
                .andExpect(jsonPath("$.estado").value("STOCK_BAJO"));

        mockMvc.perform(get("/api/inventario/productos")
                        .session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Amoxicilina 250 mg"));

        assertThat(movimientoInventarioMapper.selectCount(null)).isEqualTo(1);
        var movimiento = movimientoInventarioMapper.selectList(null).get(0);
        assertThat(movimiento.getStockAnterior()).isEqualTo(42);
        assertThat(movimiento.getStockResultante()).isEqualTo(5);
        assertThat(movimiento.getCantidad()).isEqualTo(37);

        mockMvc.perform(get("/api/inventario/categorias")
                        .session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Medicamento"));

        mockMvc.perform(delete("/api/inventario/productos/{idProducto}", idProducto)
                        .session((org.springframework.mock.web.MockHttpSession) session)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/inventario/productos")
                        .session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void completaRegistroModificacionConsultaYCancelacionDeCita() throws Exception {
        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"usuario":"veterinario","contrasena":"clave-segura"}
                                """))
                .andExpect(status().isOk())
                .andReturn();
        org.springframework.mock.web.MockHttpSession session =
                (org.springframework.mock.web.MockHttpSession) login.getRequest().getSession(false);

        MvcResult cliente = mockMvc.perform(post("/api/clientes")
                        .session(session).with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                  "ci":"8765432","nombre":"Ana","apellido":"Pérez",
                                  "telefono":"71234567","correo":"ana@example.test"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        long idCliente = objectMapper.readTree(cliente.getResponse().getContentAsString())
                .get("idCliente").asLong();

        MvcResult mascota = mockMvc.perform(post("/api/mascotas")
                        .session(session).with(csrf())
                        .contentType("application/json")
                        .content("""
                                {
                                  "idCliente":%d,"nombre":"Firulais","especie":"Canino",
                                  "raza":"Mestizo","fechaNacimiento":"2021-06-01",
                                  "peso":14.5,"sexo":"M"
                                }
                                """.formatted(idCliente)))
                .andExpect(status().isCreated())
                .andReturn();
        long idMascota = objectMapper.readTree(mascota.getResponse().getContentAsString())
                .get("idMascota").asLong();

        LocalDate dia = LocalDate.now().plusDays(7);
        String plantillaCita = """
                {
                  "idCliente":%d,"idMascota":%d,"idServicio":%d,
                  "fechaHora":"%sT%s:00","motivo":"Control preventivo"
                }
                """;
        String nuevaCita = plantillaCita.formatted(idCliente, idMascota, servicioInicialId, dia, "09:00");
        MvcResult creada = mockMvc.perform(post("/api/citas")
                        .session(session).with(csrf())
                        .contentType("application/json").content(nuevaCita))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PROGRAMADA"))
                .andExpect(jsonPath("$.cliente").value("Ana Pérez"))
                .andExpect(jsonPath("$.mascota").value("Firulais"))
                .andExpect(jsonPath("$.servicio").value("Consulta general"))
                .andReturn();
        long idCita = objectMapper.readTree(creada.getResponse().getContentAsString())
                .get("idCita").asLong();

        mockMvc.perform(post("/api/citas")
                        .session(session).with(csrf())
                        .contentType("application/json").content(nuevaCita))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Horario no disponible, seleccione otro"));

        // D-11: la consulta dura 30 minutos, por lo que 09:15 se solapa y 09:30 no.
        mockMvc.perform(post("/api/citas")
                        .session(session).with(csrf())
                        .contentType("application/json")
                        .content(plantillaCita.formatted(idCliente, idMascota, servicioInicialId, dia, "09:15")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Horario no disponible, seleccione otro"));

        // D-10: no se registran citas en el pasado.
        mockMvc.perform(post("/api/citas")
                        .session(session).with(csrf())
                        .contentType("application/json")
                        .content(plantillaCita.formatted(idCliente, idMascota, servicioInicialId,
                                LocalDate.now().minusDays(1), "09:00")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.fechaHora").value("Seleccione una fecha y hora futuras"));

        mockMvc.perform(put("/api/citas/{idCita}", idCita)
                        .session(session).with(csrf())
                        .contentType("application/json")
                        .content("""
                                {"idServicio":%d,"fechaHora":"%sT10:30:00"}
                                """.formatted(servicioInicialId, dia)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fechaHora").value(dia + "T10:30:00"));

        mockMvc.perform(post("/api/citas")
                        .session(session).with(csrf())
                        .contentType("application/json")
                        .content(plantillaCita.formatted(idCliente, idMascota, servicioInicialId, dia, "09:30")))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/citas").param("fecha", dia.toString()).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].codigo").value(CitaCode(idCita)))
                .andExpect(jsonPath("$[1].estado").value("PROGRAMADA"));

        mockMvc.perform(patch("/api/citas/{idCita}/cancelacion", idCita)
                        .session(session).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADA"));

        assertThat(citaMapper.selectCount(null)).isEqualTo(2);
    }

    @Test
    void registraAtencionMedicaYConsultaElHistorial() throws Exception {
        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"usuario\":\"veterinario\",\"contrasena\":\"clave-segura\"}"))
                .andExpect(status().isOk())
                .andReturn();
        org.springframework.mock.web.MockHttpSession session =
                (org.springframework.mock.web.MockHttpSession) login.getRequest().getSession(false);

        long idCliente = objectMapper.readTree(mockMvc.perform(post("/api/clientes")
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"ci\":\"5555555\",\"nombre\":\"Ana\",\"apellido\":\"Pérez\",\"telefono\":\"70000001\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString())
                .get("idCliente").asLong();
        long idMascota = objectMapper.readTree(mockMvc.perform(post("/api/mascotas")
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"idCliente\":%d,\"nombre\":\"Firulais\",\"especie\":\"Canino\",\"peso\":12.5}"
                                .formatted(idCliente)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString())
                .get("idMascota").asLong();
        long idCita = objectMapper.readTree(mockMvc.perform(post("/api/citas")
                        .session(session).with(csrf()).contentType("application/json")
                        .content(("{\"idCliente\":%d,\"idMascota\":%d,\"idServicio\":%d,"
                                + "\"fechaHora\":\"%sT09:00:00\",\"motivo\":\"Control clínico\"}")
                                .formatted(idCliente, idMascota, servicioInicialId, LocalDate.now().plusDays(3))))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString())
                .get("idCita").asLong();

        // R8.1.3: sin registros previos.
        mockMvc.perform(get("/api/atenciones/historial").param("mascotaId", String.valueOf(idMascota)).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        // R8.2.3: diagnóstico vacío.
        mockMvc.perform(post("/api/atenciones/diagnosticos")
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"idCita\":%d,\"descripcion\":\" \"}".formatted(idCita)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.descripcion").value("Ingrese un diagnóstico"));

        // N-01 y N-02: la atención nace de la cita y la cita queda ATENDIDA.
        JsonNode diagnostico = objectMapper.readTree(mockMvc.perform(post("/api/atenciones/diagnosticos")
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"idCita\":%d,\"descripcion\":\"Otitis leve\"}".formatted(idCita)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
        long idDiagnostico = diagnostico.get("idDiagnostico").asLong();
        long idAtencion = diagnostico.get("idAtencion").asLong();
        assertThat(citaMapper.selectById(idCita).getEstado()).isEqualTo("ATENDIDA");

        // R8.3.3: tratamiento vacío; diccionario 4.7.9: fecha de fin anterior a la de inicio.
        mockMvc.perform(post("/api/diagnosticos/{id}/tratamientos", idDiagnostico)
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"indicaciones\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.indicaciones").value("Ingrese un tratamiento"));
        mockMvc.perform(post("/api/diagnosticos/{id}/tratamientos", idDiagnostico)
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"indicaciones\":\"Gotas óticas 7 días\",\"fechaInicio\":\"2026-11-10\",\"fechaFin\":\"2026-11-05\"}"))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/diagnosticos/{id}/tratamientos", idDiagnostico)
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"indicaciones\":\"Gotas óticas 7 días\",\"duracion\":\"7 días\","
                                + "\"fechaInicio\":\"2026-11-10\",\"fechaFin\":\"2026-11-17\"}"))
                .andExpect(status().isCreated());

        // R8.4.3 y N-03: medicamento, dosis y frecuencia obligatorios con el mismo mensaje.
        mockMvc.perform(post("/api/atenciones/{id}/recetas", idAtencion)
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"medicamento\":\"Gotas óticas\",\"dosis\":\"3 gotas\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.frecuencia").value("Complete medicamento y dosis"));
        mockMvc.perform(post("/api/atenciones/{id}/recetas", idAtencion)
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"medicamento\":\"Gotas óticas\",\"dosis\":\"3 gotas\",\"frecuencia\":\"Cada 12 horas\"}"))
                .andExpect(status().isCreated());

        // R8.1.2 y N-06: historial con el motivo de la cita de origen.
        mockMvc.perform(get("/api/atenciones/historial").param("mascotaId", String.valueOf(idMascota)).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].atencion").value("Control clínico"))
                .andExpect(jsonPath("$[0].diagnosticos[0]").value("Otitis leve"))
                .andExpect(jsonPath("$[0].tratamientos[0]").value("Gotas óticas 7 días"))
                .andExpect(jsonPath("$[0].recetas[0]").value("Gotas óticas"));

        // La cita atendida ya no se ofrece para originar otra atención.
        mockMvc.perform(get("/api/atenciones/citas-programadas").param("mascotaId", String.valueOf(idMascota)).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void gestionaPromocionesSegunLaIteracion5() throws Exception {
        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"usuario\":\"veterinario\",\"contrasena\":\"clave-segura\"}"))
                .andExpect(status().isOk())
                .andReturn();
        org.springframework.mock.web.MockHttpSession session =
                (org.springframework.mock.web.MockHttpSession) login.getRequest().getSession(false);

        // R9.1.3: campos obligatorios.
        mockMvc.perform(post("/api/promociones")
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"titulo\":\"\",\"descripcion\":\"\",\"porcentajeDescuento\":20,"
                                + "\"fechaInicio\":\"2026-11-30\",\"fechaFin\":\"2026-12-31\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.titulo").value("Campo obligatorio"))
                .andExpect(jsonPath("$.fieldErrors.descripcion").value("Campo obligatorio"));

        // R9.1.5: la fecha de fin debe ser posterior a la de inicio.
        mockMvc.perform(post("/api/promociones")
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"titulo\":\"Vacunas\",\"descripcion\":\"Descuento\",\"porcentajeDescuento\":20,"
                                + "\"fechaInicio\":\"2026-11-30\",\"fechaFin\":\"2026-11-30\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La fecha de fin debe ser posterior a la de inicio"));

        // R9.1.6 y N-02: se guarda en BORRADOR con el servicio asociado.
        JsonNode creada = objectMapper.readTree(mockMvc.perform(post("/api/promociones")
                        .session(session).with(csrf()).contentType("application/json")
                        .content(("{\"titulo\":\"20 %% de descuento en vacunas\",\"descripcion\":\"Descuento para vacunación\","
                                + "\"porcentajeDescuento\":20,\"fechaInicio\":\"2026-11-30\",\"fechaFin\":\"2026-12-31\","
                                + "\"idsServicio\":[%d]}").formatted(servicioInicialId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("BORRADOR"))
                .andExpect(jsonPath("$.aplicaA[0]").value("Consulta general"))
                .andReturn().getResponse().getContentAsString());
        long idPromocion = creada.get("idPromocion").asLong();

        // R9.2.4 y N-03: Datos inválidos con el campo y la regla.
        mockMvc.perform(put("/api/promociones/{id}", idPromocion)
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"titulo\":\"Vacunas\",\"descripcion\":\"Descuento\",\"porcentajeDescuento\":20,"
                                + "\"fechaInicio\":\"2026-11-30\",\"fechaFin\":\"2026-11-29\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Datos inválidos"))
                .andExpect(jsonPath("$.fieldErrors.fechaFin").value("Fin posterior al inicio"));
        mockMvc.perform(put("/api/promociones/{id}", idPromocion)
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"titulo\":\"Vacunas\",\"descripcion\":\"Descuento\",\"porcentajeDescuento\":150,"
                                + "\"fechaInicio\":\"2026-11-30\",\"fechaFin\":\"2026-12-31\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.porcentajeDescuento").value("Descuento numérico entre 0 y 100"));
        mockMvc.perform(put("/api/promociones/{id}", idPromocion)
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"titulo\":\"Vacunas\",\"descripcion\":\"Descuento\",\"porcentajeDescuento\":20,"
                                + "\"fechaInicio\":\"2026-11-30\",\"fechaFin\":\"2026-12-31\",\"idsServicio\":[999999]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Datos inválidos"))
                .andExpect(jsonPath("$.fieldErrors.aplicaA").value("Los productos o servicios seleccionados deben estar registrados"));

        // R9.2.5: guardar cambios.
        mockMvc.perform(put("/api/promociones/{id}", idPromocion)
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"titulo\":\"20 %% de descuento en vacunas\",\"descripcion\":\"Descuento para vacunación\","
                                .replace("%%", "%") + "\"porcentajeDescuento\":25,\"fechaInicio\":\"2026-11-30\",\"fechaFin\":\"2026-12-31\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.porcentajeDescuento").value(25))
                .andExpect(jsonPath("$.aplicaA").isEmpty());

        // R9.3.3 y R9.3.2: publicar y ya publicada.
        mockMvc.perform(patch("/api/promociones/{id}/publicacion", idPromocion).session(session).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PUBLICADA"));
        mockMvc.perform(patch("/api/promociones/{id}/publicacion", idPromocion).session(session).with(csrf()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("La promoción ya está publicada"));

        // R9.4.4: eliminar con sus asociaciones.
        mockMvc.perform(delete("/api/promociones/{id}", idPromocion).session(session).with(csrf()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/promociones").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    private String CitaCode(long idCita) {
        return "CITA-%05d".formatted(idCita);
    }
}
