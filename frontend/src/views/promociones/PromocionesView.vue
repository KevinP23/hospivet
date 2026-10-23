<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listarServicios } from '../../api/citas'
import { listarProductos } from '../../api/inventario'
import {
  crearPromocion,
  editarPromocion,
  eliminarPromocion,
  listarPromociones,
  obtenerPromocion,
  publicarPromocion,
} from '../../api/promociones'
import { apiFieldErrors, apiMessage } from '../../api/http'

const mode = ref('listado')
const loading = ref(false)
const saving = ref(false)
const errorMessage = ref('')
const fieldErrors = ref({})

const promociones = ref([])
const servicios = ref([])
const productos = ref([])

const estados = { BORRADOR: 'Borrador', PUBLICADA: 'Publicada', FINALIZADA: 'Finalizada' }

function formatDate(value) {
  if (!value) return ''
  return new Intl.DateTimeFormat('es-BO', { day: '2-digit', month: '2-digit', year: 'numeric' })
    .format(new Date(`${value.slice(0, 10)}T00:00:00`))
}

const vigencia = (promocion) => (promocion ? `${formatDate(promocion.fechaInicio)} a ${formatDate(promocion.fechaFin)}` : '')

function resetMessages() {
  errorMessage.value = ''
  fieldErrors.value = {}
}

async function cargarPromociones() {
  promociones.value = await listarPromociones()
}

// N-02: varios servicios y productos, opcional. Los valores llevan prefijo S- o P-.
const emptyForm = () => ({
  titulo: '', descripcion: '', porcentajeDescuento: null, aplicaA: [], fechaInicio: '', fechaFin: '',
})
const form = reactive(emptyForm())

function payload() {
  return {
    titulo: form.titulo,
    descripcion: form.descripcion,
    porcentajeDescuento: form.porcentajeDescuento,
    fechaInicio: form.fechaInicio || null,
    fechaFin: form.fechaFin || null,
    idsServicio: form.aplicaA.filter((value) => value.startsWith('S-')).map((value) => Number(value.slice(2))),
    idsProducto: form.aplicaA.filter((value) => value.startsWith('P-')).map((value) => Number(value.slice(2))),
  }
}

function camposVacios() {
  const vacios = {}
  if (!form.titulo.trim()) vacios.titulo = 'Campo obligatorio'
  if (!form.descripcion.trim()) vacios.descripcion = 'Campo obligatorio'
  if (form.porcentajeDescuento === null || form.porcentajeDescuento === undefined) vacios.porcentajeDescuento = 'Campo obligatorio'
  if (!form.fechaInicio) vacios.fechaInicio = 'Campo obligatorio'
  if (!form.fechaFin) vacios.fechaFin = 'Campo obligatorio'
  return vacios
}

const finNoPosterior = () => form.fechaInicio && form.fechaFin && form.fechaFin <= form.fechaInicio

// R9.1 Crear promoción (SD-26)
async function guardarPromocion() {
  resetMessages()
  const vacios = camposVacios()
  if (Object.keys(vacios).length) {
    errorMessage.value = 'Complete los campos obligatorios'
    fieldErrors.value = vacios
    return
  }
  if (finNoPosterior()) {
    errorMessage.value = 'La fecha de fin debe ser posterior a la de inicio'
    fieldErrors.value = { fechaFin: errorMessage.value }
    return
  }
  saving.value = true
  try {
    await crearPromocion(payload())
    ElMessage.success('Promoción creada')
    await abrirListado()
  } catch (error) {
    const message = apiMessage(error, 'Complete los campos obligatorios')
    fieldErrors.value = apiFieldErrors(error)
    const esFecha = message === 'La fecha de fin debe ser posterior a la de inicio'
    errorMessage.value = esFecha || !Object.keys(fieldErrors.value).length ? message : 'Complete los campos obligatorios'
  } finally {
    saving.value = false
  }
}

// R9.2 Editar promoción (SD-27). N-06: Buscar promoción es un selector por código o título.
const promocionBuscada = ref(null)
const promocionCargada = ref(null)

async function buscarPromocion() {
  resetMessages()
  if (!promocionBuscada.value) return
  loading.value = true
  try {
    const promocion = await obtenerPromocion(promocionBuscada.value)
    promocionCargada.value = promocion
    Object.assign(form, {
      titulo: promocion.titulo,
      descripcion: promocion.descripcion,
      porcentajeDescuento: Number(promocion.porcentajeDescuento),
      aplicaA: [
        ...promocion.idsServicio.map((id) => `S-${id}`),
        ...promocion.idsProducto.map((id) => `P-${id}`),
      ],
      fechaInicio: promocion.fechaInicio,
      fechaFin: promocion.fechaFin,
    })
  } catch (error) {
    errorMessage.value = apiMessage(error, 'Datos inválidos')
  } finally {
    loading.value = false
  }
}

// N-03: alerta "Datos inválidos" y, bajo el campo, la regla con los textos de la interfaz.
async function guardarCambios() {
  resetMessages()
  const invalidos = camposVacios()
  if (!invalidos.fechaFin && finNoPosterior()) invalidos.fechaFin = 'Fin posterior al inicio'
  if (Object.keys(invalidos).length) {
    errorMessage.value = 'Datos inválidos'
    fieldErrors.value = invalidos
    return
  }
  saving.value = true
  try {
    await editarPromocion(promocionCargada.value.idPromocion, payload())
    ElMessage.success('Promoción actualizada')
    await cargarPromociones()
    await buscarPromocion()
  } catch (error) {
    errorMessage.value = 'Datos inválidos'
    fieldErrors.value = apiFieldErrors(error)
  } finally {
    saving.value = false
  }
}

// R9.3 Publicar promoción (SD-28)
const promocionSeleccionada = ref(null)
const seleccionada = computed(() =>
  promociones.value.find((promocion) => promocion.idPromocion === promocionSeleccionada.value) || null,
)
const yaPublicada = computed(() => seleccionada.value?.estado === 'PUBLICADA')

async function publicar() {
  resetMessages()
  if (yaPublicada.value) {
    errorMessage.value = 'La promoción ya está publicada'
    return
  }
  saving.value = true
  try {
    await publicarPromocion(seleccionada.value.idPromocion)
    ElMessage.success('Promoción visible para clientes')
    await abrirListado()
  } catch (error) {
    errorMessage.value = apiMessage(error, 'La promoción ya está publicada')
    await cargarPromociones()
  } finally {
    saving.value = false
  }
}

// R9.4 Eliminar promoción (SD-29)
async function eliminar() {
  resetMessages()
  saving.value = true
  try {
    await eliminarPromocion(seleccionada.value.idPromocion)
    ElMessage.success('Promoción eliminada')
    await abrirListado()
  } catch (error) {
    errorMessage.value = apiMessage(error, 'No fue posible eliminar la promoción')
  } finally {
    saving.value = false
  }
}

async function cancelarEliminacion() {
  ElMessage.info('Operación cancelada; la promoción se mantiene')
  await abrirListado()
}

// N-01: listado de promociones con acceso a las cuatro operaciones.
async function abrirListado() {
  resetMessages()
  mode.value = 'listado'
  loading.value = true
  try {
    await cargarPromociones()
  } catch (error) {
    errorMessage.value = apiMessage(error, 'No fue posible cargar las promociones')
  } finally {
    loading.value = false
  }
}

function abrir(nextMode, promocion = null) {
  resetMessages()
  Object.assign(form, emptyForm())
  promocionCargada.value = null
  promocionBuscada.value = null
  promocionSeleccionada.value = promocion?.idPromocion ?? null
  mode.value = nextMode
  if (nextMode === 'editar' && promocion) {
    promocionBuscada.value = promocion.idPromocion
    buscarPromocion()
  }
}

onMounted(async () => {
  loading.value = true
  try {
    const [loadedServicios, loadedProductos] = await Promise.all([listarServicios(), listarProductos()])
    servicios.value = loadedServicios
    productos.value = loadedProductos
    await cargarPromociones()
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
          <h1>Gestión de promociones</h1>
          <p>Referencias: R9</p>
        </div>
        <el-button type="primary" @click="abrir('crear')">Crear promoción</el-button>
      </header>
      <div class="form-panel" v-loading="loading">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <el-table :data="promociones" class="inventory-table" empty-text=" ">
          <el-table-column prop="codigo" label="Código" width="130" />
          <el-table-column prop="titulo" label="Título" min-width="200" />
          <el-table-column label="Vigencia" min-width="200">
            <template #default="scope">{{ vigencia(scope.row) }}</template>
          </el-table-column>
          <el-table-column label="Estado" width="120">
            <template #default="scope">{{ estados[scope.row.estado] }}</template>
          </el-table-column>
          <el-table-column label="Acciones" width="240" align="right">
            <template #default="scope">
              <el-button link class="table-action" @click="abrir('editar', scope.row)">Editar</el-button>
              <el-button link class="table-action" @click="abrir('publicar', scope.row)">Publicar</el-button>
              <el-button link class="table-action danger" @click="abrir('eliminar', scope.row)">Eliminar</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </template>

    <template v-else-if="mode === 'crear' || mode === 'editar'">
      <header class="module-heading">
        <h1>{{ mode === 'crear' ? 'Crear promoción' : 'Editar promoción' }}</h1>
        <p>{{ mode === 'crear' ? 'Referencias: R9.1, R9.1.1–6' : 'Referencias: R9.2, R9.2.1–5' }}</p>
      </header>
      <div class="form-panel" v-loading="loading">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <el-form label-position="top" @submit.prevent>
          <div class="form-columns">
            <el-form-item v-if="mode === 'editar'" label="Buscar promoción">
              <el-select v-model="promocionBuscada" filterable class="full-width" placeholder="Código o título">
                <el-option v-for="promocion in promociones" :key="promocion.idPromocion" :label="`${promocion.codigo} — ${promocion.titulo}`" :value="promocion.idPromocion" />
              </el-select>
            </el-form-item>
            <el-form-item label="Título" :error="fieldErrors.titulo">
              <el-input v-model="form.titulo" maxlength="100" />
            </el-form-item>
            <el-form-item label="Descripción" :error="fieldErrors.descripcion">
              <el-input v-model="form.descripcion" maxlength="255" />
            </el-form-item>
            <el-form-item label="Porcentaje de descuento" :error="fieldErrors.porcentajeDescuento">
              <!-- N-04: el control solo admite valores entre 0 y 100. -->
              <el-input-number v-model="form.porcentajeDescuento" :min="0" :max="100" :precision="2" :controls="false" class="full-width" />
            </el-form-item>
            <el-form-item label="Aplicar a servicio o producto" :error="fieldErrors.aplicaA">
              <el-select v-model="form.aplicaA" multiple filterable class="full-width" placeholder="Seleccione">
                <el-option-group label="Servicios">
                  <el-option v-for="servicio in servicios" :key="`S-${servicio.idServicio}`" :label="servicio.nombre" :value="`S-${servicio.idServicio}`" />
                </el-option-group>
                <el-option-group label="Productos">
                  <el-option v-for="producto in productos" :key="`P-${producto.idProducto}`" :label="producto.nombre" :value="`P-${producto.idProducto}`" />
                </el-option-group>
              </el-select>
            </el-form-item>
            <el-form-item label="Fecha de inicio" :error="fieldErrors.fechaInicio">
              <el-date-picker v-model="form.fechaInicio" type="date" format="DD/MM/YYYY" value-format="YYYY-MM-DD" class="full-width" />
            </el-form-item>
            <el-form-item label="Fecha de fin" :error="fieldErrors.fechaFin">
              <el-date-picker v-model="form.fechaFin" type="date" format="DD/MM/YYYY" value-format="YYYY-MM-DD" class="full-width" />
            </el-form-item>
          </div>
          <div class="form-actions">
            <template v-if="mode === 'crear'">
              <el-button type="primary" :loading="saving" @click="guardarPromocion">Guardar promoción</el-button>
              <el-button plain @click="abrirListado">Cancelar</el-button>
            </template>
            <template v-else>
              <el-button type="primary" :disabled="!promocionBuscada" @click="buscarPromocion">Buscar promoción</el-button>
              <el-button plain :loading="saving" :disabled="!promocionCargada" @click="guardarCambios">Guardar cambios</el-button>
              <el-button plain @click="abrirListado">Cancelar</el-button>
            </template>
          </div>
          <p v-if="mode === 'editar'" class="form-help">
            Título y descripción obligatorios; descuento numérico entre 0 y 100. Los productos o servicios seleccionados deben estar registrados.
          </p>
        </el-form>
      </div>
    </template>

    <template v-else-if="mode === 'publicar'">
      <header class="module-heading">
        <h1>Publicar promoción</h1>
        <p>Referencias: R9.3, R9.3.1–3</p>
      </header>
      <div class="form-panel delete-panel" v-loading="loading">
        <el-alert v-if="yaPublicada || errorMessage" :title="yaPublicada ? 'La promoción ya está publicada' : errorMessage" type="error" :closable="false" />
        <h2 v-else>¿Publicar esta promoción para los clientes?</h2>
        <el-form label-position="top" @submit.prevent>
        <div class="form-columns">
          <el-form-item label="Seleccionar promoción">
            <el-select v-model="promocionSeleccionada" filterable class="full-width" placeholder="Seleccione una promoción" @change="resetMessages">
              <el-option v-for="promocion in promociones" :key="promocion.idPromocion" :label="promocion.titulo" :value="promocion.idPromocion" />
            </el-select>
          </el-form-item>
          <el-form-item label="Estado actual">
            <el-input :model-value="seleccionada ? estados[seleccionada.estado] : ''" readonly />
          </el-form-item>
          <el-form-item label="Vigencia">
            <el-input :model-value="vigencia(seleccionada)" readonly />
          </el-form-item>
        </div>
        </el-form>
        <div class="form-actions">
          <el-button v-if="!yaPublicada" type="primary" :loading="saving" :disabled="!seleccionada" @click="publicar">Sí, publicar promoción</el-button>
          <el-button :type="yaPublicada ? 'primary' : ''" :plain="!yaPublicada" @click="abrirListado">Volver al listado</el-button>
        </div>
        <p class="form-help">La promoción publicada será visible en el portal del cliente.</p>
      </div>
    </template>

    <template v-else>
      <header class="module-heading">
        <h1>Eliminar promoción</h1>
        <p>Referencias: R9.4, R9.4.1–4</p>
      </header>
      <div class="form-panel delete-panel" v-loading="loading">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <h2>¿Eliminar esta promoción del sistema?</h2>
        <el-form label-position="top" @submit.prevent>
        <div class="form-columns">
          <el-form-item label="Seleccionar promoción">
            <el-select v-model="promocionSeleccionada" filterable class="full-width" placeholder="Seleccione una promoción">
              <el-option v-for="promocion in promociones" :key="promocion.idPromocion" :label="promocion.titulo" :value="promocion.idPromocion" />
            </el-select>
          </el-form-item>
          <el-form-item label="Vigencia">
            <el-input :model-value="vigencia(seleccionada)" readonly />
          </el-form-item>
        </div>
        </el-form>
        <div class="form-actions">
          <el-button type="primary" :loading="saving" :disabled="!seleccionada" @click="eliminar">Sí, eliminar promoción</el-button>
          <el-button plain @click="cancelarEliminacion">Cancelar</el-button>
        </div>
        <p class="form-help">Si cancelas, la promoción se mantiene y vuelves al listado.</p>
      </div>
    </template>
  </section>
</template>
