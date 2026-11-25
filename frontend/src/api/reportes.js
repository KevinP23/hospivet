import { http } from './http'

// SD-13 reporteInventario()
export async function reporteInventario() {
  const { data } = await http.get('/reportes/inventario')
  return data
}

// SD-14 reporteIngresos(desde, hasta)
export async function reporteIngresos(desde, hasta) {
  const { data } = await http.get('/reportes/ingresos', { params: { desde, hasta } })
  return data
}

// SD-15 reporteServicios(periodo)
export async function reporteServicios(desde, hasta) {
  const { data } = await http.get('/reportes/servicios', { params: { desde, hasta } })
  return data
}

// SD-16 reporteProductos(criterio)
export async function reporteProductos(categoria, estado) {
  const { data } = await http.get('/reportes/productos', { params: { categoria, estado } })
  return data
}
