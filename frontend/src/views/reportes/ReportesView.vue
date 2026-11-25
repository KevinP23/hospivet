<script setup>
// R06 Gestión de reportes: interfaces 3.1.13 a 3.1.16 de la Iteración 5, tal cual los mockups (plan R06, D-19).
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { apiMessage } from '../../api/http'
import { listarCategorias } from '../../api/inventario'
import { reporteIngresos, reporteInventario, reporteProductos, reporteServicios } from '../../api/reportes'

const route = useRoute()
const router = useRouter()

// N-01 B: el campo Tipo de reporte solo existe en Reporte de inventario y cambia a la interfaz elegida.
const tipos = [
  { value: 'inventario', label: 'Inventario', ruta: 'reportes' },
  { value: 'ingresos', label: 'Ingresos', ruta: 'reportes-ingresos' },
  { value: 'servicios', label: 'Servicios', ruta: 'reportes-servicios' },
  { value: 'productos', label: 'Productos', ruta: 'reportes-productos' },
]
const tipo = computed(() => route.meta.reporte || 'inventario')

function cambiarTipo(valor) {
  const destino = tipos.find((item) => item.value === valor)
  if (destino) router.push({ name: destino.ruta })
}

// Formatos de los mockups: 01/09/2026, 1.050,00 y Bs 830,00.
const numero = new Intl.NumberFormat('es-BO', { minimumFractionDigits: 2, maximumFractionDigits: 2, useGrouping: 'always' })
const importe = (valor) => numero.format(Number(valor || 0))
const bolivianos = (valor) => `Bs ${importe(valor)}`

function formatDate(value) {
  if (!value) return ''
  const [anio, mes, dia] = value.slice(0, 10).split('-')
  return `${dia}/${mes}/${anio}`
}

const dosDigitos = (valor) => String(valor).padStart(2, '0')
const aTexto = (fecha) => `${fecha.getFullYear()}-${dosDigitos(fecha.getMonth() + 1)}-${dosDigitos(fecha.getDate())}`

// N-06 C: el periodo empieza con el mes actual, no se puede vaciar y no admite un rango invertido.
function mesActual() {
  const hoy = new Date()
  return {
    desde: aTexto(new Date(hoy.getFullYear(), hoy.getMonth(), 1)),
    hasta: aTexto(new Date(hoy.getFullYear(), hoy.getMonth() + 1, 0)),
  }
}

const inicioDelDia = (texto) => new Date(`${texto}T00:00:00`).getTime()
const posteriorA = (limite) => (fecha) => Boolean(limite) && fecha.getTime() > inicioDelDia(limite)
const anteriorA = (limite) => (fecha) => Boolean(limite) && fecha.getTime() < inicioDelDia(limite)

function nuevoEstado(extra = {}) {
  return reactive({ cargando: false, generado: false, error: '', filas: [], ...extra })
}

const inventario = nuevoEstado()
const ingresos = nuevoEstado({ ...mesActual(), total: 0 })
const servicios = nuevoEstado(mesActual())
// Element Plus trata '' como "sin selección", por eso "Todas las categorías" usa su propio valor.
const TODAS = 'TODAS'
const productos = nuevoEstado({ categoria: TODAS, estado: 'TODOS' })
const categorias = ref([])

// N-05 B: los estados del mockup.
const estadosProducto = [
  { value: 'TODOS', label: 'Todos' },
  { value: 'DISPONIBLE', label: 'Disponible' },
  { value: 'AGOTADO', label: 'Agotado' },
]
const estadoLabel = (valor) => estadosProducto.find((item) => item.value === valor)?.label || valor

async function generar(estado, consulta, alRecibir) {
  estado.cargando = true
  estado.error = ''
  try {
    alRecibir(await consulta())
    estado.generado = true
  } catch (error) {
    estado.error = apiMessage(error)
  } finally {
    estado.cargando = false
  }
}

const generarInventario = () => generar(inventario, reporteInventario, (data) => {
  inventario.filas = data
})

const generarIngresos = () => generar(ingresos, () => reporteIngresos(ingresos.desde, ingresos.hasta), (data) => {
  ingresos.filas = data.filas
  ingresos.total = data.total
})

const generarServicios = () => generar(servicios, () => reporteServicios(servicios.desde, servicios.hasta), (data) => {
  servicios.filas = data
})

const categoriaBuscada = () => (productos.categoria === TODAS ? '' : productos.categoria)

const generarProductos = () => generar(productos, () => reporteProductos(categoriaBuscada(), productos.estado), (data) => {
  productos.filas = data
})

onMounted(async () => {
  try {
    categorias.value = await listarCategorias()
  } catch {
    categorias.value = []
  }
})
</script>

<template>
  <section class="module-page">
    <!-- 3.1.13 Reporte de inventario (R6.1) -->
    <template v-if="tipo === 'inventario'">
      <header class="module-heading">
        <h1>Reporte de inventario</h1>
        <p>Referencias: R6.1, R6.1.1–4</p>
      </header>
      <div class="form-panel report-panel" v-loading="inventario.cargando">
        <el-alert v-if="inventario.error" :title="inventario.error" type="error" :closable="false" />
        <el-form label-position="top" @submit.prevent>
          <div class="form-columns">
            <el-form-item label="Tipo de reporte">
              <el-select :model-value="tipo" class="full-width" @update:model-value="cambiarTipo">
                <el-option v-for="item in tipos" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </div>
        </el-form>
        <div class="form-actions">
          <el-button type="primary" @click="generarInventario">Generar reporte</el-button>
        </div>
        <el-table :data="inventario.filas" class="inventory-table report-table" empty-text=" ">
          <el-table-column prop="producto" label="Producto" min-width="200" />
          <el-table-column prop="categoria" label="Categoría" min-width="170" />
          <el-table-column prop="stockActual" label="Stock actual" min-width="120" />
          <el-table-column prop="stockMinimo" label="Stock mínimo" min-width="120" />
          <el-table-column label="Valor en Bs" min-width="130">
            <template #default="scope">{{ importe(scope.row.valor) }}</template>
          </el-table-column>
          <template #empty>
            <div v-if="inventario.generado" class="empty-appointments">No hay datos disponibles para este reporte</div>
          </template>
        </el-table>
      </div>
    </template>

    <!-- 3.1.14 Reporte de ingresos (R6.2) -->
    <template v-else-if="tipo === 'ingresos'">
      <header class="module-heading">
        <h1>Reporte de ingresos</h1>
        <p>Referencias: R6.2, R6.2.1–4</p>
      </header>
      <div class="form-panel report-panel" v-loading="ingresos.cargando">
        <el-alert v-if="ingresos.error" :title="ingresos.error" type="error" :closable="false" />
        <el-form label-position="top" @submit.prevent>
          <div class="form-columns">
            <el-form-item label="Desde">
              <el-date-picker v-model="ingresos.desde" type="date" format="DD/MM/YYYY" value-format="YYYY-MM-DD"
                :clearable="false" :disabled-date="posteriorA(ingresos.hasta)" class="full-width" />
            </el-form-item>
            <el-form-item label="Hasta">
              <el-date-picker v-model="ingresos.hasta" type="date" format="DD/MM/YYYY" value-format="YYYY-MM-DD"
                :clearable="false" :disabled-date="anteriorA(ingresos.desde)" class="full-width" />
            </el-form-item>
          </div>
        </el-form>
        <div class="form-actions">
          <el-button type="primary" @click="generarIngresos">Generar reporte</el-button>
        </div>
        <el-table :data="ingresos.filas" class="inventory-table report-table" empty-text=" ">
          <el-table-column label="Fecha" min-width="120">
            <template #default="scope">{{ formatDate(scope.row.fecha) }}</template>
          </el-table-column>
          <el-table-column prop="cliente" label="Cliente" min-width="190" />
          <el-table-column prop="concepto" label="Concepto" min-width="260" />
          <el-table-column label="Monto en Bs" min-width="130">
            <template #default="scope">{{ importe(scope.row.monto) }}</template>
          </el-table-column>
          <template #empty>
            <div v-if="ingresos.generado" class="empty-appointments">No hay ingresos registrados en el periodo seleccionado</div>
          </template>
        </el-table>
        <p v-if="ingresos.filas.length" class="report-total">Total de ingresos del periodo: {{ bolivianos(ingresos.total) }}</p>
      </div>
    </template>

    <!-- 3.1.15 Reporte de servicios (R6.3) -->
    <template v-else-if="tipo === 'servicios'">
      <header class="module-heading">
        <h1>Reporte de servicios</h1>
        <p>Referencias: R6.3, R6.3.1–4</p>
      </header>
      <div class="form-panel report-panel" v-loading="servicios.cargando">
        <el-alert v-if="servicios.error" :title="servicios.error" type="error" :closable="false" />
        <el-form label-position="top" @submit.prevent>
          <div class="form-columns">
            <el-form-item label="Periodo desde">
              <el-date-picker v-model="servicios.desde" type="date" format="DD/MM/YYYY" value-format="YYYY-MM-DD"
                :clearable="false" :disabled-date="posteriorA(servicios.hasta)" class="full-width" />
            </el-form-item>
            <el-form-item label="Periodo hasta">
              <el-date-picker v-model="servicios.hasta" type="date" format="DD/MM/YYYY" value-format="YYYY-MM-DD"
                :clearable="false" :disabled-date="anteriorA(servicios.desde)" class="full-width" />
            </el-form-item>
          </div>
        </el-form>
        <div class="form-actions">
          <el-button type="primary" @click="generarServicios">Generar reporte</el-button>
        </div>
        <el-table :data="servicios.filas" class="inventory-table report-table" empty-text=" ">
          <el-table-column prop="servicio" label="Servicio" min-width="220" />
          <el-table-column prop="cantidad" label="Cantidad realizada" min-width="160" />
          <el-table-column label="Ingreso en Bs" min-width="150">
            <template #default="scope">{{ importe(scope.row.ingreso) }}</template>
          </el-table-column>
          <template #empty>
            <div v-if="servicios.generado" class="empty-appointments">No hay servicios registrados en el periodo seleccionado</div>
          </template>
        </el-table>
      </div>
    </template>

    <!-- 3.1.16 Reporte de productos (R6.4) -->
    <template v-else>
      <header class="module-heading">
        <h1>Reporte de productos</h1>
        <p>Referencias: R6.4, R6.4.1–4</p>
      </header>
      <div class="form-panel report-panel" v-loading="productos.cargando">
        <el-alert v-if="productos.error" :title="productos.error" type="error" :closable="false" />
        <el-form label-position="top" @submit.prevent>
          <div class="form-columns">
            <el-form-item label="Categoría">
              <el-select v-model="productos.categoria" class="full-width">
                <el-option label="Todas las categorías" :value="TODAS" />
                <el-option v-for="categoria in categorias" :key="categoria" :label="categoria" :value="categoria" />
              </el-select>
            </el-form-item>
            <el-form-item label="Estado">
              <el-select v-model="productos.estado" class="full-width">
                <el-option v-for="item in estadosProducto" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </div>
        </el-form>
        <div class="form-actions">
          <el-button type="primary" @click="generarProductos">Generar reporte</el-button>
        </div>
        <el-table :data="productos.filas" class="inventory-table report-table" empty-text=" ">
          <el-table-column prop="producto" label="Producto" min-width="220" />
          <el-table-column prop="categoria" label="Categoría" min-width="180" />
          <el-table-column prop="stock" label="Stock" min-width="110" />
          <el-table-column label="Estado" min-width="130">
            <template #default="scope">{{ estadoLabel(scope.row.estado) }}</template>
          </el-table-column>
          <template #empty>
            <div v-if="productos.generado" class="empty-appointments">No hay productos que cumplan el criterio seleccionado</div>
          </template>
        </el-table>
      </div>
    </template>
  </section>
</template>

<style scoped>
.report-panel {
  min-height: auto;
}

.report-table {
  margin-top: 22px;
}

.report-table :deep(.el-table__empty-block) {
  min-height: 0;
}

.report-table :deep(.el-table__empty-text) {
  width: 100%;
  padding: 10px 0;
  text-align: left;
  line-height: 1.4;
}

.report-total {
  margin: 18px 0 0;
  color: #176965;
  font-size: 19px;
  font-weight: 800;
}
</style>
