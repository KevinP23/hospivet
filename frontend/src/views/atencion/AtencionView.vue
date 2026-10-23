<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { listarClientes } from '../../api/clientes'
import { listarMascotas } from '../../api/mascotas'
import {
  consultarHistorial,
  listarAtenciones,
  listarCitasProgramadas,
  listarDiagnosticos,
  registrarDiagnostico,
  registrarReceta,
  registrarTratamiento,
} from '../../api/atenciones'
import { apiFieldErrors, apiMessage } from '../../api/http'

const router = useRouter()

// Navegación interna entre los cuatro casos de uso de R8 (sin datos ni reglas propias).
const operaciones = [
  { mode: 'historial', label: 'Consultar historial' },
  { mode: 'diagnostico', label: 'Registrar diagnóstico' },
  { mode: 'tratamiento', label: 'Registrar tratamiento' },
  { mode: 'receta', label: 'Registrar receta' },
]

const mode = ref('historial')
const loading = ref(false)
const saving = ref(false)
const errorMessage = ref('')
const fieldErrors = ref({})

const clientes = ref([])
const mascotas = ref([])
const atencionesMascota = ref([])
const citasProgramadas = ref([])
const diagnosticos = ref([])
const atenciones = ref([])

const mascotaLabel = (mascota) => {
  const cliente = clientes.value.find((item) => item.idCliente === mascota.idCliente)
  return cliente ? `${mascota.nombre} — ${cliente.nombre} ${cliente.apellido}` : mascota.nombre
}

function formatDate(value) {
  if (!value) return ''
  return new Intl.DateTimeFormat('es-BO').format(new Date(`${value.slice(0, 10)}T00:00:00`))
}

function resetMessages() {
  errorMessage.value = ''
  fieldErrors.value = {}
}

function showServerError(error, fallback) {
  errorMessage.value = apiMessage(error, fallback)
  fieldErrors.value = apiFieldErrors(error)
  const firstFieldMessage = Object.values(fieldErrors.value)[0]
  if (firstFieldMessage) errorMessage.value = firstFieldMessage
}

// R8.1 Consultar historial (SD-22)
const historial = reactive({ idMascota: null, filas: [], buscado: false })

async function buscarHistorial() {
  resetMessages()
  if (!historial.idMascota) return
  loading.value = true
  try {
    historial.filas = await consultarHistorial(historial.idMascota)
    historial.buscado = true
  } catch (error) {
    showServerError(error, 'No fue posible consultar el historial')
  } finally {
    loading.value = false
  }
}

// N-05: Volver a la ficha lleva a Registrar mascota.
function volverALaFicha() {
  router.push({ name: 'mascotas' })
}

// R8.2 Registrar diagnóstico (SD-23)
const diagnosticoFormRef = ref()
const emptyDiagnostico = () => ({ idMascota: null, atencion: null, descripcion: '' })
const diagnosticoForm = reactive(emptyDiagnostico())
const diagnosticoRules = {
  descripcion: [{ required: true, whitespace: true, message: 'Ingrese un diagnóstico', trigger: 'blur' }],
}

// N-01: la atención médica se elige entre las atenciones de la mascota o sus citas programadas.
const opcionesAtencion = computed(() => [
  ...atencionesMascota.value.map((atencion) => ({
    value: `A-${atencion.idAtencion}`,
    label: `${atencion.codigo} — ${formatDate(atencion.fechaHora)}`,
  })),
  ...citasProgramadas.value.map((cita) => ({
    value: `C-${cita.idCita}`,
    label: `${cita.codigo} — ${formatDate(cita.fechaHora)} ${cita.fechaHora.slice(11, 16)}`,
  })),
])

async function cargarAtencionesDeMascota() {
  diagnosticoForm.atencion = null
  atencionesMascota.value = []
  citasProgramadas.value = []
  if (!diagnosticoForm.idMascota) return
  const [loadedAtenciones, loadedCitas] = await Promise.all([
    listarAtenciones(diagnosticoForm.idMascota),
    listarCitasProgramadas(diagnosticoForm.idMascota),
  ])
  atencionesMascota.value = loadedAtenciones
  citasProgramadas.value = loadedCitas
}

async function guardarDiagnostico() {
  resetMessages()
  const valid = await diagnosticoFormRef.value.validate().catch(() => false)
  if (!valid) {
    errorMessage.value = 'Ingrese un diagnóstico'
    return
  }
  const [tipo, id] = diagnosticoForm.atencion.split('-')
  saving.value = true
  try {
    await registrarDiagnostico({
      idAtencion: tipo === 'A' ? Number(id) : null,
      idCita: tipo === 'C' ? Number(id) : null,
      descripcion: diagnosticoForm.descripcion,
    })
    ElMessage.success('Diagnóstico guardado')
    diagnosticoForm.descripcion = ''
    diagnosticoFormRef.value.clearValidate()
    await cargarAtencionesDeMascota()
  } catch (error) {
    showServerError(error, 'No fue posible guardar el diagnóstico')
  } finally {
    saving.value = false
  }
}

// R8.3 Registrar tratamiento (SD-24)
const tratamientoFormRef = ref()
const emptyTratamiento = () => ({
  idDiagnostico: null, indicaciones: '', duracion: '', fechaInicio: '', fechaFin: '',
})
const tratamientoForm = reactive(emptyTratamiento())
const tratamientoRules = {
  indicaciones: [{ required: true, whitespace: true, message: 'Ingrese un tratamiento', trigger: 'blur' }],
}
const diagnosticoSeleccionado = computed(() =>
  diagnosticos.value.find((diagnostico) => diagnostico.idDiagnostico === tratamientoForm.idDiagnostico),
)

// N-04: la fecha de fin no puede ser anterior a la de inicio (diccionario 4.7.9).
function esAnteriorAlInicio(date) {
  if (!tratamientoForm.fechaInicio) return false
  return date.getTime() < new Date(`${tratamientoForm.fechaInicio}T00:00:00`).getTime()
}

function onFechaInicioChange() {
  if (tratamientoForm.fechaFin && tratamientoForm.fechaInicio && tratamientoForm.fechaFin < tratamientoForm.fechaInicio) {
    tratamientoForm.fechaFin = ''
  }
}

async function guardarTratamiento() {
  resetMessages()
  const valid = await tratamientoFormRef.value.validate().catch(() => false)
  if (!valid) {
    errorMessage.value = 'Ingrese un tratamiento'
    return
  }
  saving.value = true
  try {
    await registrarTratamiento(tratamientoForm.idDiagnostico, {
      indicaciones: tratamientoForm.indicaciones,
      duracion: tratamientoForm.duracion || null,
      fechaInicio: tratamientoForm.fechaInicio || null,
      fechaFin: tratamientoForm.fechaFin || null,
    })
    ElMessage.success('Tratamiento guardado')
    Object.assign(tratamientoForm, emptyTratamiento())
    tratamientoFormRef.value.clearValidate()
  } catch (error) {
    showServerError(error, 'No fue posible guardar el tratamiento')
  } finally {
    saving.value = false
  }
}

// R8.4 Registrar receta (SD-25)
const recetaFormRef = ref()
const emptyReceta = () => ({
  idAtencion: null, medicamento: '', dosis: '', frecuencia: '', duracion: '', indicaciones: '',
})
const recetaForm = reactive(emptyReceta())
const recetaRequired = [{ required: true, whitespace: true, message: 'Complete medicamento y dosis', trigger: 'blur' }]
// N-03: la frecuencia es obligatoria por el diccionario 4.7.10 y usa el mensaje de R8.4.3.
const recetaRules = { medicamento: recetaRequired, dosis: recetaRequired, frecuencia: recetaRequired }

async function guardarReceta() {
  resetMessages()
  const valid = await recetaFormRef.value.validate().catch(() => false)
  if (!valid) {
    errorMessage.value = 'Complete medicamento y dosis'
    return
  }
  saving.value = true
  try {
    await registrarReceta(recetaForm.idAtencion, {
      medicamento: recetaForm.medicamento,
      dosis: recetaForm.dosis,
      frecuencia: recetaForm.frecuencia,
      duracion: recetaForm.duracion || null,
      indicaciones: recetaForm.indicaciones || null,
    })
    ElMessage.success('Receta guardada')
    Object.assign(recetaForm, emptyReceta())
    recetaFormRef.value.clearValidate()
  } catch (error) {
    showServerError(error, 'No fue posible guardar la receta')
  } finally {
    saving.value = false
  }
}

async function openMode(nextMode) {
  mode.value = nextMode
  resetMessages()
  loading.value = true
  try {
    if (nextMode === 'tratamiento') diagnosticos.value = await listarDiagnosticos()
    if (nextMode === 'receta') atenciones.value = await listarAtenciones()
  } catch (error) {
    showServerError(error, 'No fue posible cargar los datos')
  } finally {
    loading.value = false
  }
}

function cancelar() {
  resetMessages()
  if (mode.value === 'diagnostico') {
    Object.assign(diagnosticoForm, emptyDiagnostico())
    atencionesMascota.value = []
    citasProgramadas.value = []
    diagnosticoFormRef.value?.clearValidate()
  }
  if (mode.value === 'tratamiento') {
    Object.assign(tratamientoForm, emptyTratamiento())
    tratamientoFormRef.value?.clearValidate()
  }
  if (mode.value === 'receta') {
    Object.assign(recetaForm, emptyReceta())
    recetaFormRef.value?.clearValidate()
  }
}

onMounted(async () => {
  loading.value = true
  try {
    const [loadedClientes, loadedMascotas] = await Promise.all([listarClientes(), listarMascotas()])
    clientes.value = loadedClientes
    mascotas.value = loadedMascotas
  } catch (error) {
    showServerError(error, 'No fue posible cargar los datos')
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <section class="module-page">
    <div class="form-actions module-operations">
      <el-button
        v-for="operacion in operaciones"
        :key="operacion.mode"
        :type="mode === operacion.mode ? 'primary' : ''"
        :plain="mode !== operacion.mode"
        @click="openMode(operacion.mode)"
      >
        {{ operacion.label }}
      </el-button>
    </div>

    <template v-if="mode === 'historial'">
      <header class="module-heading">
        <h1>Consultar historial</h1>
        <p>Referencias: R8.1, R8.1.1–3</p>
      </header>
      <div class="form-panel" v-loading="loading">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <div class="form-columns">
          <el-form-item label="Buscar mascota">
            <el-select v-model="historial.idMascota" filterable class="full-width" placeholder="Seleccione una mascota">
              <el-option v-for="mascota in mascotas" :key="mascota.idMascota" :label="mascotaLabel(mascota)" :value="mascota.idMascota" />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-actions">
          <el-button type="primary" :disabled="!historial.idMascota" @click="buscarHistorial">Buscar historial</el-button>
          <el-button plain @click="volverALaFicha">Volver a la ficha</el-button>
        </div>
        <el-table v-if="historial.buscado" :data="historial.filas" class="inventory-table">
          <el-table-column label="Fecha" min-width="110">
            <template #default="scope">{{ formatDate(scope.row.fechaHora) }}</template>
          </el-table-column>
          <el-table-column prop="atencion" label="Atención" min-width="150" />
          <el-table-column label="Diagnóstico" min-width="170">
            <template #default="scope"><span class="multiline">{{ scope.row.diagnosticos.join('\n') }}</span></template>
          </el-table-column>
          <el-table-column label="Tratamiento" min-width="170">
            <template #default="scope"><span class="multiline">{{ scope.row.tratamientos.join('\n') }}</span></template>
          </el-table-column>
          <el-table-column label="Receta" min-width="150">
            <template #default="scope"><span class="multiline">{{ scope.row.recetas.join('\n') }}</span></template>
          </el-table-column>
          <template #empty>
            <div class="empty-appointments">Sin historial previo</div>
          </template>
        </el-table>
      </div>
    </template>

    <template v-else-if="mode === 'diagnostico'">
      <header class="module-heading">
        <h1>Registrar diagnóstico</h1>
        <p>Referencias: R8.2, R8.2.1–5</p>
      </header>
      <div class="form-panel" v-loading="loading">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <el-form ref="diagnosticoFormRef" :model="diagnosticoForm" :rules="diagnosticoRules" label-position="top" @submit.prevent="guardarDiagnostico">
          <div class="form-columns">
            <el-form-item label="Mascota">
              <el-select v-model="diagnosticoForm.idMascota" filterable class="full-width" placeholder="Seleccione una mascota" @change="cargarAtencionesDeMascota">
                <el-option v-for="mascota in mascotas" :key="mascota.idMascota" :label="mascotaLabel(mascota)" :value="mascota.idMascota" />
              </el-select>
            </el-form-item>
            <el-form-item label="Atención médica">
              <el-select v-model="diagnosticoForm.atencion" class="full-width" placeholder="Seleccione la atención" :disabled="!diagnosticoForm.idMascota">
                <el-option v-for="opcion in opcionesAtencion" :key="opcion.value" :label="opcion.label" :value="opcion.value" />
              </el-select>
            </el-form-item>
            <el-form-item label="Diagnóstico" prop="descripcion" :error="fieldErrors.descripcion">
              <el-input v-model="diagnosticoForm.descripcion" type="textarea" :rows="2" maxlength="1000" />
            </el-form-item>
          </div>
          <div class="form-actions">
            <el-button type="primary" native-type="submit" :loading="saving" :disabled="!diagnosticoForm.atencion">Guardar diagnóstico</el-button>
            <el-button plain @click="cancelar">Cancelar</el-button>
          </div>
        </el-form>
      </div>
    </template>

    <template v-else-if="mode === 'tratamiento'">
      <header class="module-heading">
        <h1>Registrar tratamiento</h1>
        <p>Referencias: R8.3, R8.3.1–5</p>
      </header>
      <div class="form-panel" v-loading="loading">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <el-form ref="tratamientoFormRef" :model="tratamientoForm" :rules="tratamientoRules" label-position="top" @submit.prevent="guardarTratamiento">
          <div class="form-columns">
            <el-form-item label="Diagnóstico asociado">
              <el-select v-model="tratamientoForm.idDiagnostico" filterable class="full-width" placeholder="Seleccione el diagnóstico">
                <el-option v-for="diagnostico in diagnosticos" :key="diagnostico.idDiagnostico" :label="`${diagnostico.codigo} — ${diagnostico.descripcion}`" :value="diagnostico.idDiagnostico" />
              </el-select>
            </el-form-item>
            <el-form-item label="Mascota">
              <el-input :model-value="diagnosticoSeleccionado?.mascota || ''" readonly />
            </el-form-item>
            <el-form-item label="Tratamiento indicado" prop="indicaciones" :error="fieldErrors.indicaciones">
              <el-input v-model="tratamientoForm.indicaciones" type="textarea" :rows="2" maxlength="1000" />
            </el-form-item>
            <el-form-item label="Duración" :error="fieldErrors.duracion">
              <el-input v-model="tratamientoForm.duracion" maxlength="60" />
            </el-form-item>
            <el-form-item label="Fecha de inicio">
              <el-date-picker v-model="tratamientoForm.fechaInicio" type="date" format="DD/MM/YYYY" value-format="YYYY-MM-DD" class="full-width" @change="onFechaInicioChange" />
            </el-form-item>
            <el-form-item label="Fecha de fin">
              <el-date-picker v-model="tratamientoForm.fechaFin" type="date" format="DD/MM/YYYY" value-format="YYYY-MM-DD" :disabled-date="esAnteriorAlInicio" class="full-width" />
            </el-form-item>
          </div>
          <div class="form-actions">
            <el-button type="primary" native-type="submit" :loading="saving" :disabled="!tratamientoForm.idDiagnostico">Guardar tratamiento</el-button>
            <el-button plain @click="cancelar">Cancelar</el-button>
          </div>
        </el-form>
      </div>
    </template>

    <template v-else>
      <header class="module-heading">
        <h1>Registrar receta</h1>
        <p>Referencias: R8.4, R8.4.1–4</p>
      </header>
      <div class="form-panel" v-loading="loading">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <el-form ref="recetaFormRef" :model="recetaForm" :rules="recetaRules" label-position="top" @submit.prevent="guardarReceta">
          <div class="form-columns">
            <el-form-item label="Atención y mascota">
              <el-select v-model="recetaForm.idAtencion" filterable class="full-width" placeholder="Seleccione la atención">
                <el-option v-for="atencion in atenciones" :key="atencion.idAtencion" :label="`${atencion.codigo} — ${atencion.mascota}`" :value="atencion.idAtencion" />
              </el-select>
            </el-form-item>
            <el-form-item label="Medicamento" prop="medicamento" :error="fieldErrors.medicamento">
              <el-input v-model="recetaForm.medicamento" maxlength="120" />
            </el-form-item>
            <el-form-item label="Dosis" prop="dosis" :error="fieldErrors.dosis">
              <el-input v-model="recetaForm.dosis" maxlength="60" />
            </el-form-item>
            <el-form-item label="Frecuencia" prop="frecuencia" :error="fieldErrors.frecuencia">
              <el-input v-model="recetaForm.frecuencia" maxlength="60" />
            </el-form-item>
            <el-form-item label="Duración" :error="fieldErrors.duracion">
              <el-input v-model="recetaForm.duracion" maxlength="60" />
            </el-form-item>
            <el-form-item label="Indicaciones" :error="fieldErrors.indicaciones">
              <el-input v-model="recetaForm.indicaciones" maxlength="255" />
            </el-form-item>
          </div>
          <div class="form-actions">
            <el-button type="primary" native-type="submit" :loading="saving" :disabled="!recetaForm.idAtencion">Guardar receta</el-button>
            <el-button plain @click="cancelar">Cancelar</el-button>
          </div>
        </el-form>
      </div>
    </template>
  </section>
</template>

<style scoped>
.multiline {
  white-space: pre-line;
}

.module-operations {
  margin-bottom: 18px;
}

/* El aviso "Sin historial previo" ocupa todo el ancho, como en la interfaz de la Iteración 5. */
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
