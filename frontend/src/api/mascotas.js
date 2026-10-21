import { http, prepareCsrf } from './http'

export async function listarMascotas(clienteId = null) {
  const { data } = await http.get('/mascotas', {
    params: clienteId ? { clienteId } : {},
  })
  return data
}

export async function registrarMascota(mascota) {
  await prepareCsrf()
  const { data } = await http.post('/mascotas', mascota)
  return data
}

