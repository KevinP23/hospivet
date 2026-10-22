package com.hospivet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospivet.auth.mapper.UsuarioMapper;
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
    private ServicioMapper servicioMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long servicioInicialId;

    @BeforeEach
    void prepararVeterinario() {
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

    private String CitaCode(long idCita) {
        return "CITA-%05d".formatted(idCita);
    }
}
