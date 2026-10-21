import { http, prepareCsrf } from './http'

export async function listarCitas(fecha = '') {
  const { data } = await http.get('/citas', { params: fecha ? { fecha } : {} })
  return data
}

export async function listarServicios() {
  const { data } = await http.get('/servicios')
  return data
}

export async function registrarCita(cita) {
  await prepareCsrf()
  const { data } = await http.post('/citas', cita)
  return data
}

export async function modificarCita(idCita, cita) {
  await prepareCsrf()
  const { data } = await http.put(`/citas/${idCita}`, cita)
  return data
}

export async function cancelarCita(idCita) {
  await prepareCsrf()
  const { data } = await http.patch(`/citas/${idCita}/cancelacion`)
  return data
}
