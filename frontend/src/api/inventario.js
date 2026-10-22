import { http, prepareCsrf } from './http'

export async function listarProductos(filtros = {}) {
  const { data } = await http.get('/inventario/productos', { params: filtros })
  return data
}

export async function listarCategorias() {
  const { data } = await http.get('/inventario/categorias')
  return data
}

export async function agregarProducto(producto) {
  await prepareCsrf()
  const { data } = await http.post('/inventario/productos', producto)
  return data
}

export async function actualizarStock(idProducto, nuevaCantidad) {
  await prepareCsrf()
  const { data } = await http.patch(`/inventario/productos/${idProducto}/stock`, { nuevaCantidad })
  return data
}

export async function eliminarProducto(idProducto) {
  await prepareCsrf()
  await http.delete(`/inventario/productos/${idProducto}`)
}
