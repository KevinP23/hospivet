import { http, prepareCsrf } from './http'

// R10 Consulta de información (Cliente). El cliente sale siempre de la sesión.
export async function misMascotas() {
  const { data } = await http.get('/portal/mascotas')
  return data
}

// R10.1.1 Ver horarios disponibles
export async function horariosDisponibles(fecha, idServicio) {
  const { data } = await http.get('/portal/citas/horarios', { params: { fecha, idServicio } })
  return data
}

// SD-30 registrar(fecha, horario, mascota)
export async function solicitarCita(solicitud) {
  await prepareCsrf()
  const { data } = await http.post('/portal/citas', solicitud)
  return data
}

// SD-31 Consultar servicios
export async function serviciosDisponibles() {
  const { data } = await http.get('/portal/servicios')
  return data
}

// SD-32 Ver promociones
export async function promocionesVigentes() {
  const { data } = await http.get('/portal/promociones')
  return data
}
