import { http, prepareCsrf } from './http'

export async function listarClientes() {
  const { data } = await http.get('/clientes')
  return data
}

export async function registrarCliente(cliente) {
  await prepareCsrf()
  const { data } = await http.post('/clientes', cliente)
  return data
}

