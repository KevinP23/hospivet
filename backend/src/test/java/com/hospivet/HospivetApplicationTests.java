package com.hospivet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospivet.atencion.mapper.AtencionMedicaMapper;
import com.hospivet.atencion.mapper.DiagnosticoMapper;
import com.hospivet.atencion.mapper.RecetaMapper;
import com.hospivet.atencion.mapper.TratamientoMapper;
import com.hospivet.auth.mapper.UsuarioMapper;
import com.hospivet.factura.mapper.DetalleFacturaMapper;
import com.hospivet.factura.mapper.FacturaMapper;
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
    private FacturaMapper facturaMapper;

    @Autowired
    private DetalleFacturaMapper detalleFacturaMapper;

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
        detalleFacturaMapper.delete(null);
        facturaMapper.delete(null);
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
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.apellido").value("Rojas"))
                .andReturn();

        HttpSession session = login.getRequest().getSession(false);
        assertThat(session).isNotNull();

        // COR-02 N-02: la sesión activa devuelve nombre y apellido para la cabecera.
        mockMvc.perform(get("/api/auth/me").session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario").value("veterinario"))
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.apellido").value("Rojas"));

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

    @Test
    void gestionaFacturacionSegunLaIteracion5() throws Exception {
        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"usuario\":\"veterinario\",\"contrasena\":\"clave-segura\"}"))
                .andExpect(status().isOk())
                .andReturn();
        org.springframework.mock.web.MockHttpSession session =
                (org.springframework.mock.web.MockHttpSession) login.getRequest().getSession(false);

        long conCorreo = objectMapper.readTree(mockMvc.perform(post("/api/clientes")
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"ci\":\"4444444\",\"nombre\":\"Ana\",\"apellido\":\"Pérez\",\"telefono\":\"70000002\","
                                + "\"correo\":\"ana@example.test\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).get("idCliente").asLong();
        long sinCorreo = objectMapper.readTree(mockMvc.perform(post("/api/clientes")
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"ci\":\"3333333\",\"nombre\":\"Luis\",\"apellido\":\"Rojas\",\"telefono\":\"70000003\"}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).get("idCliente").asLong();
        long idProducto = objectMapper.readTree(mockMvc.perform(post("/api/inventario/productos")
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"nombre\":\"Vacuna\",\"categoria\":\"Medicamento\",\"precio\":25.00,"
                                + "\"cantidadInicial\":10,\"stockMinimo\":2}"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).get("idProducto").asLong();
        long idPromocion = objectMapper.readTree(mockMvc.perform(post("/api/promociones")
                        .session(session).with(csrf()).contentType("application/json")
                        .content(("{\"titulo\":\"Vacunas\",\"descripcion\":\"Descuento\",\"porcentajeDescuento\":20,"
                                + "\"fechaInicio\":\"%s\",\"fechaFin\":\"%s\",\"idsProducto\":[%d]}")
                                .formatted(LocalDate.now().minusDays(1), LocalDate.now().plusDays(10), idProducto)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).get("idPromocion").asLong();
        mockMvc.perform(patch("/api/promociones/{id}/publicacion", idPromocion).session(session).with(csrf()))
                .andExpect(status().isOk());

        // R7.3.3: servicio o producto no encontrado.
        mockMvc.perform(post("/api/facturas")
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"idCliente\":%d,\"items\":[{\"tipo\":\"PRODUCTO\",\"id\":999999,\"cantidad\":1}]}"
                                .formatted(conCorreo)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Servicio o producto no encontrado"));

        // R7.3.4 a R7.3.6, N-02 y N-04: el mismo ítem suma cantidad y la promoción descuenta 20 % de sus ítems.
        long idFactura = objectMapper.readTree(mockMvc.perform(post("/api/facturas")
                        .session(session).with(csrf()).contentType("application/json")
                        .content(("{\"idCliente\":%d,\"idPromocion\":%d,\"items\":["
                                + "{\"tipo\":\"PRODUCTO\",\"id\":%d,\"cantidad\":1},"
                                + "{\"tipo\":\"PRODUCTO\",\"id\":%d,\"cantidad\":1}]}")
                                .formatted(conCorreo, idPromocion, idProducto, idProducto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.detalle[0].cantidad").value(2))
                .andExpect(jsonPath("$.subtotal").value(50.0))
                .andExpect(jsonPath("$.descuento").value(10.0))
                .andExpect(jsonPath("$.total").value(40.0))
                .andExpect(jsonPath("$.estado").value("EMITIDA"))
                .andReturn().getResponse().getContentAsString()).get("idFactura").asLong();

        // R7.1.2: factura no encontrada.
        mockMvc.perform(get("/api/facturas/{id}", 999999).session(session))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Factura no encontrada"));

        JsonNode factura = objectMapper.readTree(mockMvc.perform(get("/api/facturas/{id}", idFactura).session(session))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        long idDetalle = factura.get("detalle").get(0).get("idDetalle").asLong();

        // R7.2.3 y R7.2.4: datos inválidos con el campo y la regla.
        mockMvc.perform(put("/api/facturas/{id}", idFactura)
                        .session(session).with(csrf()).contentType("application/json")
                        .content(("{\"idCliente\":%d,\"fecha\":\"%s\",\"descuento\":0,\"lineas\":[{\"idDetalle\":%d,"
                                + "\"descripcion\":\"Vacuna\",\"cantidad\":0,\"precioUnitario\":25}]}")
                                .formatted(conCorreo, LocalDate.now(), idDetalle)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors['lineas[0].cantidad']").value("Cantidad mayor que 0"));
        mockMvc.perform(put("/api/facturas/{id}", idFactura)
                        .session(session).with(csrf()).contentType("application/json")
                        .content(("{\"idCliente\":%d,\"fecha\":\"%s\",\"descuento\":999,\"lineas\":[{\"idDetalle\":%d,"
                                + "\"descripcion\":\"Vacuna\",\"cantidad\":2,\"precioUnitario\":25}]}")
                                .formatted(conCorreo, LocalDate.now(), idDetalle)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Datos inválidos"))
                .andExpect(jsonPath("$.fieldErrors.descuento").value("Descuento entre 0 y el subtotal"));

        // R7.2.5: guardar cambios y recalcular.
        mockMvc.perform(put("/api/facturas/{id}", idFactura)
                        .session(session).with(csrf()).contentType("application/json")
                        .content(("{\"idCliente\":%d,\"fecha\":\"%s\",\"descuento\":5,\"lineas\":[{\"idDetalle\":%d,"
                                + "\"descripcion\":\"Vacuna\",\"cantidad\":3,\"precioUnitario\":25}]}")
                                .formatted(conCorreo, LocalDate.now(), idDetalle)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subtotal").value(75.0))
                .andExpect(jsonPath("$.total").value(70.0));

        // R7.4.3: error de impresión.
        mockMvc.perform(post("/api/facturas/{id}/impresion", idFactura)
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"impresora\":\"Impresora inexistente\"}"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.message").value("Error de impresión, verifique la impresora"));

        // R7.5.2: el cliente no tiene correo registrado.
        long facturaSinCorreo = objectMapper.readTree(mockMvc.perform(post("/api/facturas")
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"idCliente\":%d,\"items\":[{\"tipo\":\"PRODUCTO\",\"id\":%d,\"cantidad\":1}]}"
                                .formatted(sinCorreo, idProducto)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).get("idFactura").asLong();
        mockMvc.perform(post("/api/facturas/{id}/envio", facturaSinCorreo)
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"medio\":\"CORREO\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El cliente no tiene correo registrado"));

        // N-10: actualizar correo en el registro del cliente.
        mockMvc.perform(put("/api/facturas/{id}/correo-cliente", facturaSinCorreo)
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"correo\":\"luis@\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.correo").value("Correo inválido"));
        mockMvc.perform(put("/api/facturas/{id}/correo-cliente", facturaSinCorreo)
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"correo\":\"luis@example.test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correoCliente").value("luis@example.test"));

        // Sin servidor de correo configurado, el envío no se confirma.
        mockMvc.perform(post("/api/facturas/{id}/envio", facturaSinCorreo)
                        .session(session).with(csrf()).contentType("application/json")
                        .content("{\"medio\":\"CORREO\"}"))
                .andExpect(status().isBadGateway());
    }

    @Test
    void generaReportesSegunLaIteracion5SinModificarDatos() throws Exception {
        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"usuario\":\"veterinario\",\"contrasena\":\"clave-segura\"}"))
                .andExpect(status().isOk())
                .andReturn();
        org.springframework.mock.web.MockHttpSession session =
                (org.springframework.mock.web.MockHttpSession) login.getRequest().getSession(false);

        // R6.1.4, R6.2.4, R6.3.4 y R6.4.4: sin datos, listas vacías (la interfaz muestra el mensaje).
        mockMvc.perform(get("/api/reportes/inventario").session(session))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mockMvc.perform(get("/api/reportes/productos").session(session))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mockMvc.perform(get("/api/reportes/ingresos?desde=2026-09-01&hasta=2026-09-30").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filas.length()").value(0))
                .andExpect(jsonPath("$.total").value(0.0));
        mockMvc.perform(get("/api/reportes/servicios?desde=2026-09-01&hasta=2026-09-30").session(session))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));

        Long idVeterinario = veterinarioMapper.selectList(null).get(0).getIdVeterinario();
        Long amoxicilina = productoReporte("Amoxicilina 250 mg", "Medicamento", "25.00", 42, 5, true);
        productoReporte("Champú antipulgas", "Higiene", "45.00", 5, 5, true);
        productoReporte("Jeringa 5 ml", "Insumo", "2.00", 0, 10, true);
        productoReporte("Producto retirado", "Medicamento", "10.00", 3, 1, false);

        // SD-13: solo productos activos (N-07); Valor en Bs = stock actual × precio, como el mockup.
        mockMvc.perform(get("/api/reportes/inventario").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].producto").value("Amoxicilina 250 mg"))
                .andExpect(jsonPath("$[0].stockActual").value(42))
                .andExpect(jsonPath("$[0].stockMinimo").value(5))
                .andExpect(jsonPath("$[0].valor").value(1050.0))
                .andExpect(jsonPath("$[1].valor").value(225.0));

        // SD-16 y N-05: estados Disponible (stock > 0) y Agotado (stock 0); vacío o TODOS no filtran.
        mockMvc.perform(get("/api/reportes/productos?categoria=&estado=TODOS").session(session))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(3));
        mockMvc.perform(get("/api/reportes/productos?estado=AGOTADO").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].producto").value("Jeringa 5 ml"))
                .andExpect(jsonPath("$[0].estado").value("AGOTADO"));
        mockMvc.perform(get("/api/reportes/productos?categoria=Medicamento&estado=DISPONIBLE").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].stock").value(42));
        mockMvc.perform(get("/api/reportes/productos?categoria=Higiene&estado=AGOTADO").session(session))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
        mockMvc.perform(get("/api/reportes/productos?estado=VENCIDO").session(session))
                .andExpect(status().isBadRequest());

        // SD-14: facturas del periodo (sin las ANULADA, N-08), concepto con las líneas (N-04) y total.
        Long ana = clienteReporte("5000001", "Ana", "Pérez");
        Long carlos = clienteReporte("5000002", "Carlos", "Rojas");
        Long vacunacion = servicioReporte("Vacunación", "120.00");
        com.hospivet.servicio.model.Servicio consulta = servicioMapper.selectById(servicioInicialId);
        consulta.setPrecio(new BigDecimal("80.00"));
        servicioMapper.updateById(consulta);

        Long factura1 = facturaReporte(ana, idVeterinario, LocalDateTime.of(2026, 9, 10, 9, 30), "180.00", "EMITIDA");
        lineaServicioReporte(factura1, servicioInicialId, "Consulta general", "80.00");
        lineaServicioReporte(factura1, vacunacion, "Vacuna", "100.00");
        Long factura2 = facturaReporte(carlos, idVeterinario, LocalDateTime.of(2026, 9, 15, 16, 0), "650.00", "EMITIDA");
        lineaServicioReporte(factura2, servicioInicialId, "Cirugía menor", "650.00");
        Long anulada = facturaReporte(ana, idVeterinario, LocalDateTime.of(2026, 9, 20, 10, 0), "999.00", "ANULADA");
        lineaServicioReporte(anulada, servicioInicialId, "Anulada", "999.00");
        Long fueraDelPeriodo = facturaReporte(ana, idVeterinario, LocalDateTime.of(2026, 10, 1, 0, 0), "100.00", "EMITIDA");
        lineaServicioReporte(fueraDelPeriodo, servicioInicialId, "Octubre", "100.00");

        mockMvc.perform(get("/api/reportes/ingresos?desde=2026-09-01&hasta=2026-09-30").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.filas.length()").value(2))
                .andExpect(jsonPath("$.filas[0].fecha").value("2026-09-10"))
                .andExpect(jsonPath("$.filas[0].cliente").value("Ana Pérez"))
                .andExpect(jsonPath("$.filas[0].concepto").value("Consulta general, Vacuna"))
                .andExpect(jsonPath("$.filas[0].monto").value(180.0))
                .andExpect(jsonPath("$.filas[1].cliente").value("Carlos Rojas"))
                .andExpect(jsonPath("$.total").value(830.0));

        // Precondición de 3.1.14 y 3.1.15: rango de fechas válido (la interfaz no permite otro, N-06).
        mockMvc.perform(get("/api/reportes/ingresos?desde=2026-09-30&hasta=2026-09-01").session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.hasta").exists());
        mockMvc.perform(get("/api/reportes/servicios?hasta=2026-09-30").session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.desde").exists());

        // SD-15 y N-03: citas ATENDIDAS del periodo por servicio; ingreso = cantidad × precio del catálogo.
        Long mascota = mascotaReporte(ana);
        citaReporte(ana, mascota, idVeterinario, servicioInicialId, LocalDateTime.of(2026, 9, 2, 9, 0), "ATENDIDA");
        citaReporte(ana, mascota, idVeterinario, servicioInicialId, LocalDateTime.of(2026, 9, 9, 9, 0), "ATENDIDA");
        citaReporte(ana, mascota, idVeterinario, servicioInicialId, LocalDateTime.of(2026, 9, 30, 18, 0), "ATENDIDA");
        citaReporte(ana, mascota, idVeterinario, vacunacion, LocalDateTime.of(2026, 9, 16, 10, 0), "ATENDIDA");
        citaReporte(ana, mascota, idVeterinario, vacunacion, LocalDateTime.of(2026, 9, 17, 10, 0), "PROGRAMADA");
        citaReporte(ana, mascota, idVeterinario, vacunacion, LocalDateTime.of(2026, 9, 18, 10, 0), "CANCELADA");
        citaReporte(ana, mascota, idVeterinario, servicioInicialId, LocalDateTime.of(2026, 10, 1, 9, 0), "ATENDIDA");

        mockMvc.perform(get("/api/reportes/servicios?desde=2026-09-01&hasta=2026-09-30").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].servicio").value("Consulta general"))
                .andExpect(jsonPath("$[0].cantidad").value(3))
                .andExpect(jsonPath("$[0].ingreso").value(240.0))
                .andExpect(jsonPath("$[1].servicio").value("Vacunación"))
                .andExpect(jsonPath("$[1].cantidad").value(1))
                .andExpect(jsonPath("$[1].ingreso").value(120.0));

        // Postcondiciones de 3.1.13 a 3.1.16: los reportes no alteran los registros.
        assertThat(productoMapper.selectById(amoxicilina).getStockActual()).isEqualTo(42L);
        assertThat(facturaMapper.selectById(factura1).getTotal()).isEqualByComparingTo("180.00");
        assertThat(citaMapper.selectCount(null)).isEqualTo(7L);

        // Sin sesión no hay acceso a los reportes.
        mockMvc.perform(get("/api/reportes/inventario")).andExpect(status().isUnauthorized());
    }

    private Long productoReporte(String nombre, String categoria, String precio, long stock, long minimo, boolean activo) {
        com.hospivet.inventario.model.Producto producto = new com.hospivet.inventario.model.Producto();
        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setPrecio(new BigDecimal(precio));
        producto.setStockActual(stock);
        producto.setStockMinimo(minimo);
        producto.setActivo(activo);
        productoMapper.insert(producto);
        return producto.getIdProducto();
    }

    private Long clienteReporte(String ci, String nombre, String apellido) {
        com.hospivet.cliente.model.Cliente cliente = new com.hospivet.cliente.model.Cliente();
        cliente.setCi(ci);
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setTelefono("70000000");
        cliente.setActivo(true);
        clienteMapper.insert(cliente);
        return cliente.getIdCliente();
    }

    private Long servicioReporte(String nombre, String precio) {
        Servicio servicio = new Servicio();
        servicio.setNombre(nombre);
        servicio.setDescripcion(nombre);
        servicio.setPrecio(new BigDecimal(precio));
        servicio.setDuracionMin(20);
        servicio.setActivo(true);
        servicioMapper.insert(servicio);
        return servicio.getIdServicio();
    }

    private Long facturaReporte(Long idCliente, Long idVeterinario, LocalDateTime fecha, String total, String estado) {
        com.hospivet.factura.model.Factura factura = new com.hospivet.factura.model.Factura();
        factura.setIdCliente(idCliente);
        factura.setIdVeterinario(idVeterinario);
        factura.setFechaHora(fecha);
        factura.setSubtotal(new BigDecimal(total));
        factura.setDescuento(BigDecimal.ZERO);
        factura.setTotal(new BigDecimal(total));
        factura.setEstado(estado);
        facturaMapper.insert(factura);
        return factura.getIdFactura();
    }

    private void lineaServicioReporte(Long idFactura, Long idServicio, String descripcion, String precio) {
        com.hospivet.factura.model.DetalleFactura linea = new com.hospivet.factura.model.DetalleFactura();
        linea.setIdFactura(idFactura);
        linea.setIdServicio(idServicio);
        linea.setDescripcion(descripcion);
        linea.setCantidad(1L);
        linea.setPrecioUnitario(new BigDecimal(precio));
        linea.setSubtotal(new BigDecimal(precio));
        linea.setTipoItem("SERVICIO");
        detalleFacturaMapper.insert(linea);
    }

    private Long mascotaReporte(Long idCliente) {
        com.hospivet.mascota.model.Mascota mascota = new com.hospivet.mascota.model.Mascota();
        mascota.setIdCliente(idCliente);
        mascota.setNombre("Toby");
        mascota.setEspecie("Perro");
        mascota.setPeso(new BigDecimal("12.40"));
        mascota.setActivo(true);
        mascotaMapper.insert(mascota);
        return mascota.getIdMascota();
    }

    private void citaReporte(Long idCliente, Long idMascota, Long idVeterinario, Long idServicio,
                             LocalDateTime fecha, String estado) {
        com.hospivet.cita.model.Cita cita = new com.hospivet.cita.model.Cita();
        cita.setIdCliente(idCliente);
        cita.setIdMascota(idMascota);
        cita.setIdVeterinario(idVeterinario);
        cita.setIdServicio(idServicio);
        cita.setFechaHora(fecha);
        cita.setEstado(estado);
        cita.setMotivo("Control");
        citaMapper.insert(cita);
    }

    private String CitaCode(long idCita) {
        return "CITA-%05d".formatted(idCita);
    }
}
