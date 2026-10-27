<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listarClientes } from '../../api/clientes'
import { listarServicios } from '../../api/citas'
import { listarProductos } from '../../api/inventario'
import { listarPromociones } from '../../api/promociones'
import {
  actualizarCorreoCliente,
  consultarFactura,
  editarFactura,
  enviarFactura,
  generarFactura,
  imprimirFactura,
  listarFacturas,
  listarImpresoras,
} from '../../api/facturas'
import { apiFieldErrors, apiMessage } from '../../api/http'
import { hoyLocal } from '../../utils/fechas'

const mode = ref('listado')
const loading = ref(false)
const saving = ref(false)
const errorMessage = ref('')
const fieldErrors = ref({})

const facturas = ref([])
const clientes = ref([])
const servicios = ref([])
const productos = ref([])
const promociones = ref([])

const estados = { EMITIDA: 'Emitida', ANULADA: 'Anulada', PAGADA: 'Pagada' }
const money = (value) => new Intl.NumberFormat('es-BO', { style: 'currency', currency: 'BOB' }).format(Number(value || 0))
const redondear = (value) => Math.round(Number(value) * 100) / 100

function formatDate(value) {
  if (!value) return ''
  return new Intl.DateTimeFormat('es-BO', { day: '2-digit', month: '2-digit', year: 'numeric' })
    .format(new Date(`${value.slice(0, 10)}T00:00:00`))
}

function resetMessages() {
  errorMessage.value = ''
  fieldErrors.value = {}
}

async function cargarFacturas() {
  facturas.value = await listarFacturas()
}

// N-01: listado de facturas con acceso a las cinco operaciones.
async function abrirListado() {
  resetMessages()
  mode.value = 'listado'
  loading.value = true
  try {
    await cargarFacturas()
  } catch (error) {
    errorMessage.value = apiMessage(error, 'No fue posible cargar las facturas')
  } finally {
    loading.value = false
  }
}

// R7.3 Generar factura (SD-19)
const generar = reactive({ idCliente: null, item: null, idPromocion: null, detalle: [], noEncontrado: false })

const catalogo = computed(() => [
  ...servicios.value.map((servicio) => ({ clave: `SERVICIO-${servicio.idServicio}`, tipo: 'SERVICIO', id: servicio.idServicio, nombre: servicio.nombre, precio: Number(servicio.precio) })),
  ...productos.value.map((producto) => ({ clave: `PRODUCTO-${producto.idProducto}`, tipo: 'PRODUCTO', id: producto.idProducto, nombre: producto.nombre, precio: Number(producto.precio) })),
])

// N-02: agregar el mismo ítem suma 1 a la cantidad; el precio es el del catálogo.
function agregarAlDetalle() {
  resetMessages()
  generar.noEncontrado = false
  const item = catalogo.value.find((elemento) => elemento.clave === generar.item)
  if (!item) return
  const existente = generar.detalle.find((linea) => linea.clave === item.clave)
  if (existente) existente.cantidad += 1
  else generar.detalle.push({ ...item, cantidad: 1 })
  generar.item = null
}

const subtotalDe = (lineas) => redondear(lineas.reduce((total, linea) => total + linea.precio * linea.cantidad, 0))

// N-04: promociones publicadas, vigentes hoy y asociadas a algún ítem del detalle (o sin ítems asociados).
const promocionesVigentes = computed(() => {
  const hoy = hoyLocal()
  return promociones.value.filter((promocion) => {
    if (promocion.estado !== 'PUBLICADA' || promocion.fechaInicio > hoy || promocion.fechaFin < hoy) return false
    if (!promocion.idsServicio.length && !promocion.idsProducto.length) return true
    return generar.detalle.some((linea) => aplicaA(promocion, linea))
  })
})

function aplicaA(promocion, linea) {
  return linea.tipo === 'SERVICIO' ? promocion.idsServicio.includes(linea.id) : promocion.idsProducto.includes(linea.id)
}

const totalesGenerar = computed(() => {
  const subtotal = subtotalDe(generar.detalle)
  const promocion = promocionesVigentes.value.find((item) => item.idPromocion === generar.idPromocion)
  let descuento = 0
  if (promocion) {
    const sinItems = !promocion.idsServicio.length && !promocion.idsProducto.length
    const base = sinItems ? subtotal : subtotalDe(generar.detalle.filter((linea) => aplicaA(promocion, linea)))
    descuento = redondear((base * Number(promocion.porcentajeDescuento)) / 100)
  }
  return { subtotal, descuento, total: redondear(subtotal - descuento) }
})

async function confirmarFactura() {
  resetMessages()
  saving.value = true
  try {
    await generarFactura({
      idCliente: generar.idCliente,
      items: generar.detalle.map((linea) => ({ tipo: linea.tipo, id: linea.id, cantidad: linea.cantidad })),
      idPromocion: promocionesVigentes.value.some((item) => item.idPromocion === generar.idPromocion) ? generar.idPromocion : null,
    })
    ElMessage.success('Factura generada con total calculado')
    await abrirListado()
  } catch (error) {
    const message = apiMessage(error, 'Servicio o producto no encontrado')
    if (message === 'Servicio o producto no encontrado') {
      // Curso 2c: se cancela la operación y no se muestran filas ni totales ficticios.
      generar.detalle = []
      generar.noEncontrado = true
    } else {
      errorMessage.value = message
    }
  } finally {
    saving.value = false
  }
}

// R7.1 Revisar factura (SD-17)
const revisar = reactive({ numero: '', factura: null, noEncontrada: false })

async function buscarFacturaRevisar() {
  resetMessages()
  revisar.factura = null
  revisar.noEncontrada = false
  const id = Number(String(revisar.numero).trim())
  if (!Number.isInteger(id) || id <= 0) {
    revisar.noEncontrada = true
    return
  }
  loading.value = true
  try {
    revisar.factura = await consultarFactura(id)
  } catch (error) {
    if (error.response?.status === 404) revisar.noEncontrada = true
    else errorMessage.value = apiMessage(error, 'Factura no encontrada')
  } finally {
    loading.value = false
  }
}

// R7.2 Editar factura (SD-18). N-06: un selector elige la línea del detalle que editan los campos.
const editar = reactive({ numero: '', factura: null, idCliente: null, fecha: '', descuento: '', lineas: [], idLinea: null })
const lineaActual = computed(() => editar.lineas.find((linea) => linea.idDetalle === editar.idLinea) || null)

async function buscarFacturaEditar() {
  resetMessages()
  editar.factura = null
  const id = Number(String(editar.numero).trim())
  if (!Number.isInteger(id) || id <= 0) {
    errorMessage.value = 'Factura no encontrada'
    return
  }
  loading.value = true
  try {
    const factura = await consultarFactura(id)
    Object.assign(editar, {
      factura,
      idCliente: factura.idCliente,
      fecha: factura.fechaHora.slice(0, 10),
      descuento: String(Number(factura.descuento).toFixed(2)),
      lineas: factura.detalle.map((linea) => ({
        idDetalle: linea.idDetalle,
        descripcion: linea.descripcion,
        cantidad: String(linea.cantidad),
        precioUnitario: String(Number(linea.precioUnitario).toFixed(2)),
      })),
      idLinea: factura.detalle[0]?.idDetalle ?? null,
    })
  } catch (error) {
    errorMessage.value = apiMessage(error, 'Factura no encontrada')
  } finally {
    loading.value = false
  }
}

const esEnteroPositivo = (value) => /^\d+$/.test(String(value).trim()) && Number(value) > 0
const esMontoNoNegativo = (value) => /^\d+(\.\d{1,2})?$/.test(String(value).trim())

// N-07: alerta "Datos inválidos" y, bajo el campo, la regla con los textos de la interfaz.
function validarEdicion() {
  if (!editar.idCliente) return { idCliente: 'Cliente obligatorio' }
  if (!editar.fecha) return { fecha: 'Fecha válida' }
  for (const linea of editar.lineas) {
    const errores = {}
    if (!String(linea.descripcion).trim()) errores.descripcion = 'Descripción obligatoria'
    if (!esEnteroPositivo(linea.cantidad)) errores.cantidad = 'Cantidad mayor que 0'
    if (!esMontoNoNegativo(linea.precioUnitario)) errores.precioUnitario = 'Precio no negativo'
    if (Object.keys(errores).length) {
      editar.idLinea = linea.idDetalle
      return errores
    }
  }
  const subtotal = redondear(editar.lineas.reduce((total, linea) => total + Number(linea.precioUnitario) * Number(linea.cantidad), 0))
  if (!esMontoNoNegativo(editar.descuento) || Number(editar.descuento) > subtotal) {
    return { descuento: 'Descuento entre 0 y el subtotal' }
  }
  return null
}

async function guardarCambios() {
  resetMessages()
  const errores = validarEdicion()
  if (errores) {
    errorMessage.value = 'Datos inválidos'
    fieldErrors.value = errores
    return
  }
  saving.value = true
  try {
    await editarFactura(editar.factura.idFactura, {
      idCliente: editar.idCliente,
      fecha: editar.fecha,
      descuento: Number(editar.descuento),
      lineas: editar.lineas.map((linea) => ({
        idDetalle: linea.idDetalle,
        descripcion: linea.descripcion,
        cantidad: Number(linea.cantidad),
        precioUnitario: Number(linea.precioUnitario),
      })),
    })
    ElMessage.success('Factura actualizada')
    await buscarFacturaEditar()
  } catch (error) {
    errorMessage.value = 'Datos inválidos'
    fieldErrors.value = apiFieldErrors(error)
  } finally {
    saving.value = false
  }
}

// R7.4 Imprimir factura (SD-20). N-08 B: impresión directa en la impresora del equipo.
const imprimir = reactive({ idFactura: null, impresora: null, impresoras: [], vistaPrevia: null, fallo: false })

async function cargarImpresoras() {
  const { impresoras, predeterminada } = await listarImpresoras()
  imprimir.impresoras = impresoras
  if (!imprimir.impresora) imprimir.impresora = predeterminada || impresoras[0] || null
}

async function verVistaPrevia() {
  resetMessages()
  if (!imprimir.idFactura) return
  loading.value = true
  try {
    imprimir.vistaPrevia = await consultarFactura(imprimir.idFactura)
  } catch (error) {
    errorMessage.value = apiMessage(error, 'Factura no encontrada')
  } finally {
    loading.value = false
  }
}

async function enviarAImpresora() {
  resetMessages()
  saving.value = true
  try {
    if (!imprimir.vistaPrevia || imprimir.vistaPrevia.idFactura !== imprimir.idFactura) await verVistaPrevia()
    const { mensaje } = await imprimirFactura(imprimir.idFactura, imprimir.impresora)
    imprimir.fallo = false
    ElMessage.success(mensaje)
  } catch (error) {
    // Curso 2c: se conserva la vista previa y se puede reintentar.
    imprimir.fallo = true
    errorMessage.value = apiMessage(error, 'Error de impresión, verifique la impresora')
  } finally {
    saving.value = false
  }
}

// R7.5 Enviar factura (SD-21)
const enviar = reactive({ idFactura: null, medio: 'CORREO', correo: '' })
const facturaEnviar = computed(() => facturas.value.find((factura) => factura.idFactura === enviar.idFactura) || null)

function seleccionarFacturaEnviar() {
  resetMessages()
  enviar.correo = facturaEnviar.value?.correoCliente || ''
}

async function enviarAlCliente() {
  resetMessages()
  saving.value = true
  try {
    const { mensaje } = await enviarFactura(enviar.idFactura, enviar.medio)
    ElMessage.success(mensaje)
  } catch (error) {
    errorMessage.value = apiMessage(error, 'El cliente no tiene correo registrado')
    fieldErrors.value = apiFieldErrors(error)
  } finally {
    saving.value = false
  }
}

// N-10: Actualizar correo guarda el correo escrito en el registro del cliente.
async function actualizarCorreo() {
  resetMessages()
  saving.value = true
  try {
    const factura = await actualizarCorreoCliente(enviar.idFactura, enviar.correo)
    await cargarFacturas()
    enviar.correo = factura.correoCliente
  } catch (error) {
    fieldErrors.value = apiFieldErrors(error)
    errorMessage.value = fieldErrors.value.correo || apiMessage(error, 'El cliente no tiene correo registrado')
  } finally {
    saving.value = false
  }
}

async function abrir(nextMode, factura = null) {
  resetMessages()
  mode.value = nextMode
  if (nextMode === 'generar') {
    Object.assign(generar, { idCliente: null, item: null, idPromocion: null, detalle: [], noEncontrado: false })
    promociones.value = await listarPromociones()
  }
  if (nextMode === 'revisar') {
    Object.assign(revisar, { numero: factura ? factura.numero : '', factura: null, noEncontrada: false })
    if (factura) await buscarFacturaRevisar()
  }
  if (nextMode === 'editar') {
    Object.assign(editar, { numero: factura ? factura.numero : '', factura: null, lineas: [], idLinea: null })
    if (factura) await buscarFacturaEditar()
  }
  if (nextMode === 'imprimir') {
    Object.assign(imprimir, { idFactura: factura?.idFactura ?? null, vistaPrevia: null, fallo: false })
    try {
      await cargarImpresoras()
    } catch (error) {
      errorMessage.value = apiMessage(error, 'Error de impresión, verifique la impresora')
    }
  }
  if (nextMode === 'enviar') {
    Object.assign(enviar, { idFactura: factura?.idFactura ?? null, medio: 'CORREO', correo: factura?.correoCliente || '' })
  }
}

onMounted(async () => {
  loading.value = true
  try {
    const [loadedClientes, loadedServicios, loadedProductos] = await Promise.all([
      listarClientes(), listarServicios(), listarProductos(),
    ])
    clientes.value = loadedClientes
    servicios.value = loadedServicios
    productos.value = loadedProductos
    await cargarFacturas()
  } catch (error) {
    errorMessage.value = apiMessage(error, 'No fue posible cargar los datos')
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <section class="module-page">
    <template v-if="mode === 'listado'">
      <header class="module-heading appointments-heading">
        <div>
          <h1>Gestión de facturación</h1>
          <p>Referencias: R7</p>
        </div>
        <el-button type="primary" @click="abrir('generar')">Generar factura</el-button>
      </header>
      <div class="form-panel" v-loading="loading">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <el-table :data="facturas" class="inventory-table" empty-text=" ">
          <el-table-column prop="numero" label="Número" width="110" />
          <el-table-column prop="cliente" label="Cliente" min-width="160" />
          <el-table-column label="Fecha" width="120">
            <template #default="scope">{{ formatDate(scope.row.fechaHora) }}</template>
          </el-table-column>
          <el-table-column label="Total" width="130">
            <template #default="scope">{{ money(scope.row.total) }}</template>
          </el-table-column>
          <el-table-column label="Estado" width="110">
            <template #default="scope">{{ estados[scope.row.estado] }}</template>
          </el-table-column>
          <el-table-column label="Acciones" width="300" align="right">
            <template #default="scope">
              <el-button link class="table-action" @click="abrir('revisar', scope.row)">Revisar</el-button>
              <el-button link class="table-action" @click="abrir('editar', scope.row)">Editar</el-button>
              <el-button link class="table-action" @click="abrir('imprimir', scope.row)">Imprimir</el-button>
              <el-button link class="table-action" @click="abrir('enviar', scope.row)">Enviar</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </template>

    <template v-else-if="mode === 'generar'">
      <header class="module-heading">
        <h1>Generar factura</h1>
        <p>Referencias: R7.3, R7.3.1–6</p>
      </header>
      <div class="form-panel" v-loading="loading">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <el-form label-position="top" @submit.prevent>
          <div class="form-columns">
            <el-form-item label="Cliente">
              <el-select v-model="generar.idCliente" filterable class="full-width" placeholder="Seleccione un cliente">
                <el-option v-for="cliente in clientes" :key="cliente.idCliente" :label="`${cliente.nombre} ${cliente.apellido}`" :value="cliente.idCliente" />
              </el-select>
            </el-form-item>
            <el-form-item label="Agregar servicio o producto">
              <el-select v-model="generar.item" filterable class="full-width" placeholder="Seleccione">
                <el-option-group label="Servicios">
                  <el-option v-for="item in catalogo.filter((elemento) => elemento.tipo === 'SERVICIO')" :key="item.clave" :label="item.nombre" :value="item.clave" />
                </el-option-group>
                <el-option-group label="Productos">
                  <el-option v-for="item in catalogo.filter((elemento) => elemento.tipo === 'PRODUCTO')" :key="item.clave" :label="item.nombre" :value="item.clave" />
                </el-option-group>
              </el-select>
            </el-form-item>
            <el-form-item label="Promoción vigente">
              <el-select v-model="generar.idPromocion" clearable class="full-width" placeholder="Sin promoción">
                <el-option v-for="promocion in promocionesVigentes" :key="promocion.idPromocion" :label="promocion.titulo" :value="promocion.idPromocion" />
              </el-select>
            </el-form-item>
          </div>
        </el-form>
        <div class="form-actions">
          <el-button type="primary" :disabled="!generar.item" @click="agregarAlDetalle">Agregar al detalle</el-button>
          <el-button plain :loading="saving" :disabled="!generar.idCliente || !generar.detalle.length" @click="confirmarFactura">Confirmar y generar factura</el-button>
          <el-button plain @click="abrirListado">Cancelar</el-button>
        </div>
        <el-table :data="generar.detalle" class="inventory-table invoice-table" empty-text=" ">
          <el-table-column prop="nombre" label="Descripción" min-width="180" />
          <el-table-column prop="cantidad" label="Cantidad" width="110" />
          <el-table-column label="Precio unitario" width="150">
            <template #default="scope">{{ money(scope.row.precio) }}</template>
          </el-table-column>
          <el-table-column label="Subtotal" width="150">
            <template #default="scope">{{ money(scope.row.precio * scope.row.cantidad) }}</template>
          </el-table-column>
          <template #empty>
            <div v-if="generar.noEncontrado" class="empty-appointments">Servicio o producto no encontrado</div>
          </template>
        </el-table>
        <div v-if="generar.detalle.length" class="invoice-totals">
          <span>Subtotal: {{ money(totalesGenerar.subtotal) }}</span>
          <span>Descuento: {{ money(totalesGenerar.descuento) }}</span>
          <span>Total: {{ money(totalesGenerar.total) }}</span>
        </div>
      </div>
    </template>

    <template v-else-if="mode === 'revisar'">
      <header class="module-heading">
        <h1>Revisar factura</h1>
        <p>Referencias: R7.1, R7.1.1–3</p>
      </header>
      <div class="form-panel" v-loading="loading">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <el-form label-position="top" @submit.prevent="buscarFacturaRevisar">
          <div class="form-columns">
            <el-form-item label="Buscar factura registrada">
              <el-input v-model="revisar.numero" maxlength="10" placeholder="000124" @keyup.enter="buscarFacturaRevisar" />
            </el-form-item>
            <el-form-item label="Cliente">
              <el-input :model-value="revisar.factura?.cliente || ''" readonly />
            </el-form-item>
            <el-form-item label="Fecha de emisión">
              <el-input :model-value="formatDate(revisar.factura?.fechaHora)" readonly />
            </el-form-item>
            <el-form-item label="Estado">
              <el-input :model-value="revisar.factura ? estados[revisar.factura.estado] : ''" readonly />
            </el-form-item>
          </div>
        </el-form>
        <div class="form-actions">
          <el-button type="primary" @click="buscarFacturaRevisar">Buscar factura</el-button>
          <el-button plain @click="abrirListado">Volver al listado</el-button>
        </div>
        <el-table :data="revisar.factura?.detalle || []" class="inventory-table invoice-table" empty-text=" ">
          <el-table-column prop="descripcion" label="Descripción" min-width="180" />
          <el-table-column prop="cantidad" label="Cantidad" width="110" />
          <el-table-column label="Precio unitario" width="150">
            <template #default="scope">{{ money(scope.row.precioUnitario) }}</template>
          </el-table-column>
          <el-table-column label="Subtotal" width="150">
            <template #default="scope">{{ money(scope.row.subtotal) }}</template>
          </el-table-column>
          <template #empty>
            <div v-if="revisar.noEncontrada" class="empty-appointments">Factura no encontrada</div>
          </template>
        </el-table>
        <div v-if="revisar.factura" class="invoice-totals">
          <span>Subtotal: {{ money(revisar.factura.subtotal) }}</span>
          <span>Descuento: {{ money(revisar.factura.descuento) }}</span>
          <span>Total: {{ money(revisar.factura.total) }}</span>
        </div>
      </div>
    </template>

    <template v-else-if="mode === 'editar'">
      <header class="module-heading">
        <h1>Editar factura</h1>
        <p>Referencias: R7.2, R7.2.1–5</p>
      </header>
      <div class="form-panel" v-loading="loading">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <el-form label-position="top" @submit.prevent>
          <div class="form-columns">
            <el-form-item label="Buscar factura a editar">
              <el-input v-model="editar.numero" maxlength="10" placeholder="000124" @keyup.enter="buscarFacturaEditar" />
            </el-form-item>
            <el-form-item label="Cliente" :error="fieldErrors.idCliente">
              <el-select v-model="editar.idCliente" filterable class="full-width" :disabled="!editar.factura" placeholder="Seleccione un cliente">
                <el-option v-for="cliente in clientes" :key="cliente.idCliente" :label="`${cliente.nombre} ${cliente.apellido}`" :value="cliente.idCliente" />
              </el-select>
            </el-form-item>
            <el-form-item label="Fecha" :error="fieldErrors.fecha">
              <el-date-picker v-model="editar.fecha" type="date" format="DD/MM/YYYY" value-format="YYYY-MM-DD" class="full-width" :disabled="!editar.factura" />
            </el-form-item>
            <el-form-item label="Línea del detalle">
              <el-select v-model="editar.idLinea" class="full-width" :disabled="!editar.factura" placeholder="Seleccione la línea">
                <el-option v-for="linea in editar.lineas" :key="linea.idDetalle" :label="linea.descripcion || '—'" :value="linea.idDetalle" />
              </el-select>
            </el-form-item>
            <template v-if="lineaActual">
              <el-form-item label="Descripción del detalle" :error="fieldErrors.descripcion">
                <el-input v-model="lineaActual.descripcion" maxlength="150" />
              </el-form-item>
              <el-form-item label="Cantidad" :error="fieldErrors.cantidad">
                <el-input v-model="lineaActual.cantidad" maxlength="8" />
              </el-form-item>
              <el-form-item label="Precio unitario en Bs" :error="fieldErrors.precioUnitario">
                <el-input v-model="lineaActual.precioUnitario" maxlength="11" />
              </el-form-item>
            </template>
            <el-form-item label="Descuento en Bs" :error="fieldErrors.descuento">
              <el-input v-model="editar.descuento" maxlength="15" :disabled="!editar.factura" />
            </el-form-item>
          </div>
        </el-form>
        <div class="form-actions">
          <el-button type="primary" @click="buscarFacturaEditar">Buscar factura</el-button>
          <el-button plain :loading="saving" :disabled="!editar.factura" @click="guardarCambios">Guardar cambios</el-button>
          <el-button plain @click="abrirListado">Cancelar</el-button>
        </div>
        <p class="form-help">Cliente y descripción obligatorios; fecha válida; precio no negativo. Descuento entre 0 y el subtotal. El sistema recalcula el total.</p>
      </div>
    </template>

    <template v-else-if="mode === 'imprimir'">
      <header class="module-heading">
        <h1>Imprimir factura</h1>
        <p>Referencias: R7.4, R7.4.1–3</p>
      </header>
      <div class="form-panel" v-loading="loading">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <el-form label-position="top" @submit.prevent>
          <div class="form-columns">
            <el-form-item label="Seleccionar factura">
              <el-select v-model="imprimir.idFactura" filterable class="full-width" placeholder="Seleccione una factura" @change="imprimir.vistaPrevia = null">
                <el-option v-for="factura in facturas" :key="factura.idFactura" :label="`${factura.numero} — ${factura.cliente}`" :value="factura.idFactura" />
              </el-select>
            </el-form-item>
            <el-form-item label="Impresora">
              <el-select v-model="imprimir.impresora" class="full-width" placeholder="Seleccione la impresora">
                <el-option v-for="impresora in imprimir.impresoras" :key="impresora" :label="impresora" :value="impresora" />
              </el-select>
            </el-form-item>
          </div>
        </el-form>
        <div class="form-actions">
          <el-button type="primary" :disabled="!imprimir.idFactura" @click="verVistaPrevia">Vista previa</el-button>
          <el-button plain :loading="saving && !imprimir.fallo" :disabled="!imprimir.idFactura || !imprimir.impresora" @click="enviarAImpresora">Imprimir</el-button>
          <el-button plain :loading="saving && imprimir.fallo" :disabled="!imprimir.fallo" @click="enviarAImpresora">Reintentar</el-button>
        </div>
        <template v-if="imprimir.vistaPrevia">
          <el-table :data="imprimir.vistaPrevia.detalle" class="inventory-table invoice-table">
            <el-table-column prop="descripcion" label="Descripción" min-width="180" />
            <el-table-column prop="cantidad" label="Cantidad" width="110" />
            <el-table-column label="Precio unitario" width="150">
              <template #default="scope">{{ money(scope.row.precioUnitario) }}</template>
            </el-table-column>
            <el-table-column label="Subtotal" width="150">
              <template #default="scope">{{ money(scope.row.subtotal) }}</template>
            </el-table-column>
          </el-table>
          <div class="invoice-totals">
            <span>Total a pagar: {{ money(imprimir.vistaPrevia.total) }}</span>
          </div>
        </template>
      </div>
    </template>

    <template v-else>
      <header class="module-heading">
        <h1>Enviar factura</h1>
        <p>Referencias: R7.5, R7.5.1–3</p>
      </header>
      <div class="form-panel" v-loading="loading">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <el-form label-position="top" @submit.prevent>
          <div class="form-columns">
            <el-form-item label="Factura seleccionada">
              <el-select v-model="enviar.idFactura" filterable class="full-width" placeholder="Seleccione una factura" @change="seleccionarFacturaEnviar">
                <el-option v-for="factura in facturas" :key="factura.idFactura" :label="factura.numero" :value="factura.idFactura" />
              </el-select>
            </el-form-item>
            <el-form-item label="Cliente">
              <el-input :model-value="facturaEnviar?.cliente || ''" readonly />
            </el-form-item>
            <el-form-item label="Medio de envío" :error="fieldErrors.medio">
              <el-select v-model="enviar.medio" class="full-width">
                <el-option label="Correo electrónico" value="CORREO" />
              </el-select>
            </el-form-item>
            <el-form-item label="Correo del cliente" :error="fieldErrors.correo">
              <el-input v-model="enviar.correo" maxlength="100" :disabled="!enviar.idFactura" />
            </el-form-item>
          </div>
        </el-form>
        <div class="form-actions">
          <el-button type="primary" :loading="saving" :disabled="!enviar.idFactura" @click="enviarAlCliente">Enviar factura</el-button>
          <el-button plain :disabled="!enviar.idFactura" @click="actualizarCorreo">Actualizar correo</el-button>
          <el-button plain @click="abrirListado">Cancelar</el-button>
        </div>
      </div>
    </template>
  </section>
</template>

<style scoped>
.invoice-table {
  margin-top: 18px;
}

.invoice-totals {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 40px;
  margin-top: 18px;
  color: #176965;
  font-size: 21px;
  font-weight: 800;
}

:deep(.el-table__empty-block) {
  width: 100% !important;
  padding: 12px 0;
}

:deep(.el-table__empty-text) {
  width: 100%;
  line-height: normal;
  text-align: left;
}
</style>
