import { http, prepareCsrf } from './http'

export async function listarPromociones() {
  const { data } = await http.get('/promociones')
  return data
}

export async function obtenerPromocion(idPromocion) {
  const { data } = await http.get(`/promociones/${idPromocion}`)
  return data
}

export async function crearPromocion(promocion) {
  await prepareCsrf()
  const { data } = await http.post('/promociones', promocion)
  return data
}

export async function editarPromocion(idPromocion, promocion) {
  await prepareCsrf()
  const { data } = await http.put(`/promociones/${idPromocion}`, promocion)
  return data
}

export async function publicarPromocion(idPromocion) {
  await prepareCsrf()
  const { data } = await http.patch(`/promociones/${idPromocion}/publicacion`)
  return data
}

export async function eliminarPromocion(idPromocion) {
  await prepareCsrf()
  await http.delete(`/promociones/${idPromocion}`)
}
