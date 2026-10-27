import { http, prepareCsrf } from './http'

export async function listarFacturas() {
  const { data } = await http.get('/facturas')
  return data
}

export async function consultarFactura(idFactura) {
  const { data } = await http.get(`/facturas/${idFactura}`)
  return data
}

export async function generarFactura(factura) {
  await prepareCsrf()
  const { data } = await http.post('/facturas', factura)
  return data
}

export async function editarFactura(idFactura, factura) {
  await prepareCsrf()
  const { data } = await http.put(`/facturas/${idFactura}`, factura)
  return data
}

export async function listarImpresoras() {
  const { data } = await http.get('/impresoras')
  return data
}

export async function imprimirFactura(idFactura, impresora) {
  await prepareCsrf()
  const { data } = await http.post(`/facturas/${idFactura}/impresion`, { impresora })
  return data
}

export async function enviarFactura(idFactura, medio) {
  await prepareCsrf()
  const { data } = await http.post(`/facturas/${idFactura}/envio`, { medio })
  return data
}

export async function actualizarCorreoCliente(idFactura, correo) {
  await prepareCsrf()
  const { data } = await http.put(`/facturas/${idFactura}/correo-cliente`, { correo })
  return data
}
