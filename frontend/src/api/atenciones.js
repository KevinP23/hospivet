import { http, prepareCsrf } from './http'

export async function consultarHistorial(mascotaId) {
  const { data } = await http.get('/atenciones/historial', { params: { mascotaId } })
  return data
}

export async function listarAtenciones(mascotaId = null) {
  const { data } = await http.get('/atenciones', { params: mascotaId ? { mascotaId } : {} })
  return data
}

export async function listarCitasProgramadas(mascotaId) {
  const { data } = await http.get('/atenciones/citas-programadas', { params: { mascotaId } })
  return data
}

export async function listarDiagnosticos(mascotaId = null) {
  const { data } = await http.get('/atenciones/diagnosticos', { params: mascotaId ? { mascotaId } : {} })
  return data
}

export async function registrarDiagnostico(diagnostico) {
  await prepareCsrf()
  const { data } = await http.post('/atenciones/diagnosticos', diagnostico)
  return data
}

export async function registrarTratamiento(idDiagnostico, tratamiento) {
  await prepareCsrf()
  const { data } = await http.post(`/diagnosticos/${idDiagnostico}/tratamientos`, tratamiento)
  return data
}

export async function registrarReceta(idAtencion, receta) {
  await prepareCsrf()
  const { data } = await http.post(`/atenciones/${idAtencion}/recetas`, receta)
  return data
}
