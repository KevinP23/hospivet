<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { authState } from '../../api/auth'
import { listarClientes } from '../../api/clientes'
import { listarMascotas } from '../../api/mascotas'
import {
  cancelarCita,
  listarCitas,
  listarServicios,
  modificarCita,
  registrarCita,
} from '../../api/citas'
import { apiFieldErrors, apiMessage } from '../../api/http'
import { esDiaPasado, esFuturo, hoyLocal } from '../../utils/fechas'

const mode = ref('list')
const formRef = ref()
const citas = ref([])
const clientes = ref([])
const mascotas = ref([])
const servicios = ref([])
const selectedCita = ref(null)
const filterDate = ref(hoyLocal())
const loading = ref(false)
const saving = ref(false)
const errorMessage = ref('')
const fieldErrors = ref({})

const emptyForm = () => ({
  idCliente: null,
  idMascota: null,
  idServicio: null,
  fecha: '',
  hora: '',
  motivo: '',
  observaciones: '',
})
const form = reactive(emptyForm())

const rules = {
  idCliente: [{ required: true, message: 'Seleccione el cliente', trigger: 'change' }],
  idMascota: [{ required: true, message: 'Seleccione la mascota', trigger: 'change' }],
  idServicio: [{ required: true, message: 'Seleccione el servicio solicitado', trigger: 'change' }],
  fecha: [{ required: true, message: 'Seleccione fecha y hora', trigger: 'change' }],
  hora: [{ required: true, message: 'Seleccione fecha y hora', trigger: 'change' }],
  motivo: [{ required: true, message: 'Complete el motivo', trigger: 'blur' }],
}

const mascotasDelCliente = computed(() =>
  mascotas.value.filter((mascota) => mascota.idCliente === form.idCliente),
)

async function loadCatalogs() {
  const [loadedClients, loadedPets, loadedServices] = await Promise.all([
    listarClientes(),
    listarMascotas(),
    listarServicios(),
  ])
  clientes.value = loadedClients
  mascotas.value = loadedPets
  servicios.value = loadedServices
}

async function loadAppointments(useFilter = true) {
  loading.value = true
  errorMessage.value = ''
  try {
    citas.value = await listarCitas(useFilter ? filterDate.value : '')
  } catch (error) {
    errorMessage.value = apiMessage(error, 'No fue posible cargar las citas')
  } finally {
    loading.value = false
  }
}

async function initialize() {
  loading.value = true
  try {
    await loadCatalogs()
    citas.value = await listarCitas(filterDate.value)
  } catch (error) {
    errorMessage.value = apiMessage(error, 'No fue posible cargar los datos')
  } finally {
    loading.value = false
  }
}

function resetMessages() {
  errorMessage.value = ''
  fieldErrors.value = {}
  formRef.value?.clearValidate()
}

function resetForm() {
  Object.assign(form, emptyForm())
  if (servicios.value.length === 1) form.idServicio = servicios.value[0].idServicio
}

function openList() {
  mode.value = 'list'
  selectedCita.value = null
  resetMessages()
  loadAppointments(Boolean(filterDate.value))
}

function openCreate() {
  selectedCita.value = null
  resetForm()
  resetMessages()
  mode.value = 'create'
}

function onClientChange() {
  form.idMascota = null
}

function openEdit(cita) {
  selectedCita.value = cita
  Object.assign(form, {
    ...emptyForm(),
    idCliente: cita.idCliente,
    idMascota: cita.idMascota,
    idServicio: cita.idServicio,
    fecha: cita.fechaHora.slice(0, 10),
    hora: cita.fechaHora.slice(11, 16),
  })
  resetMessages()
  mode.value = 'edit'
}

function openCancel(cita) {
  selectedCita.value = cita
  resetMessages()
  mode.value = 'cancel'
}

function appointmentPayload(includeDetails = true) {
  const payload = {
    idServicio: form.idServicio,
    fechaHora: `${form.fecha}T${form.hora}:00`,
  }
  if (includeDetails) {
    payload.idCliente = form.idCliente
    payload.idMascota = form.idMascota
    payload.motivo = form.motivo
    payload.observaciones = form.observaciones || null
  }
  return payload
}

async function validateForm(properties = undefined) {
  const validationErrors = await formRef.value.validate(properties).then(() => null).catch((errors) => errors)
  if (validationErrors) {
    errorMessage.value = 'Complete los campos obligatorios'
    return false
  }
  return true
}

function requireFutureDateTime() {
  if (esFuturo(form.fecha, form.hora)) return true
  errorMessage.value = 'Seleccione una fecha y hora futuras'
  fieldErrors.value.hora = errorMessage.value
  return false
}

async function submitCreate() {
  resetMessages()
  if (!await validateForm()) return
  if (!requireFutureDateTime()) return
  saving.value = true
  try {
    await registrarCita(appointmentPayload(true))
    ElMessage.success('Cita registrada correctamente')
    openList()
  } catch (error) {
    errorMessage.value = apiMessage(error, 'No fue posible registrar la cita')
    fieldErrors.value = apiFieldErrors(error)
    if (fieldErrors.value.fechaHora) errorMessage.value = fieldErrors.value.fechaHora
    if (errorMessage.value === 'Horario no disponible, seleccione otro' || fieldErrors.value.fechaHora) {
      fieldErrors.value.hora = errorMessage.value
    }
  } finally {
    saving.value = false
  }
}

async function submitEdit() {
  resetMessages()
  const missingFields = []
  if (!form.idServicio) missingFields.push('idServicio')
  if (!form.fecha) missingFields.push('fecha')
  if (!form.hora) missingFields.push('hora')
  if (missingFields.length) {
    errorMessage.value = 'Complete los campos obligatorios'
    missingFields.forEach((field) => {
      fieldErrors.value[field] = field === 'idServicio'
        ? 'Seleccione el servicio solicitado'
        : 'Seleccione fecha y hora'
    })
    return
  }
  if (!requireFutureDateTime()) return
  saving.value = true
  try {
    await modificarCita(selectedCita.value.idCita, appointmentPayload(false))
    ElMessage.success('Cambios de la cita guardados')
    openList()
  } catch (error) {
    errorMessage.value = apiMessage(error, 'No fue posible modificar la cita')
    fieldErrors.value = apiFieldErrors(error)
    if (fieldErrors.value.fechaHora) errorMessage.value = fieldErrors.value.fechaHora
    if (errorMessage.value === 'Horario no disponible, seleccione otro' || fieldErrors.value.fechaHora) {
      fieldErrors.value.hora = errorMessage.value
    }
  } finally {
    saving.value = false
  }
}

async function confirmCancel() {
  saving.value = true
  resetMessages()
  try {
    await cancelarCita(selectedCita.value.idCita)
    ElMessage.success('Cita cancelada correctamente')
    openList()
  } catch (error) {
    errorMessage.value = apiMessage(error, 'No fue posible cancelar la cita')
  } finally {
    saving.value = false
  }
}

function formatDate(value) {
  return new Intl.DateTimeFormat('es-BO').format(new Date(`${value.slice(0, 10)}T00:00:00`))
}

function formatDateTime(value) {
  return `${formatDate(value)}, ${value.slice(11, 16)}`
}

function statusLabel(status) {
  return { PROGRAMADA: 'Programada', ATENDIDA: 'Atendida', CANCELADA: 'Cancelada' }[status] || status
}

onMounted(initialize)
</script>

<template>
  <section class="module-page appointments-page">
    <template v-if="mode === 'list'">
      <header class="module-heading appointments-heading">
        <div>
          <h1>Consultar citas del veterinario</h1>
          <p>Referencias: R5.4, R5.4.1–7</p>
        </div>
        <el-button type="primary" @click="openCreate">Registrar cita</el-button>
      </header>

      <div class="form-panel appointments-panel" v-loading="loading">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <div class="appointment-filter">
          <el-form-item label="Filtrar por día">
            <el-date-picker v-model="filterDate" type="date" format="DD/MM/YYYY" value-format="YYYY-MM-DD" class="full-width" />
          </el-form-item>
        </div>
        <div class="form-actions appointment-toolbar">
          <el-button type="primary" @click="loadAppointments(true)">Filtrar citas</el-button>
          <el-button plain @click="filterDate = ''; loadAppointments(false)">Ver todas</el-button>
        </div>

        <div v-if="!loading && !errorMessage && citas.length === 0" class="empty-appointments">
          No hay citas registradas para la fecha seleccionada
        </div>
        <el-table v-else :data="citas" class="inventory-table appointment-table">
          <el-table-column label="Fecha" min-width="125">
            <template #default="scope">{{ formatDate(scope.row.fechaHora) }}</template>
          </el-table-column>
          <el-table-column label="Hora" width="90">
            <template #default="scope">{{ scope.row.fechaHora.slice(11, 16) }}</template>
          </el-table-column>
          <el-table-column prop="cliente" label="Cliente" min-width="160" />
          <el-table-column prop="mascota" label="Mascota" min-width="120" />
          <el-table-column prop="servicio" label="Servicio" min-width="150" />
          <el-table-column label="Estado" width="125">
            <template #default="scope">{{ statusLabel(scope.row.estado) }}</template>
          </el-table-column>
          <el-table-column label="Acciones" width="175" align="right">
            <template #default="scope">
              <el-button v-if="scope.row.estado === 'PROGRAMADA'" link class="table-action" @click="openEdit(scope.row)">Modificar</el-button>
              <el-button v-if="scope.row.estado === 'PROGRAMADA'" link class="table-action danger" @click="openCancel(scope.row)">Cancelar</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </template>

    <template v-else-if="mode === 'create'">
      <header class="module-heading">
        <h1>Registrar cita</h1>
        <p>Referencias: R5.1, R5.1.1–5</p>
      </header>
      <div class="form-panel">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="submitCreate">
          <div class="form-columns">
            <el-form-item label="Cliente" prop="idCliente" :error="fieldErrors.idCliente">
              <el-select v-model="form.idCliente" filterable class="full-width" placeholder="Seleccione un cliente" @change="onClientChange">
                <el-option v-for="cliente in clientes" :key="cliente.idCliente" :label="`${cliente.nombre} ${cliente.apellido}`" :value="cliente.idCliente" />
              </el-select>
            </el-form-item>
            <el-form-item label="Mascota" prop="idMascota" :error="fieldErrors.idMascota">
              <el-select v-model="form.idMascota" filterable class="full-width" placeholder="Seleccione una mascota" :disabled="!form.idCliente">
                <el-option v-for="mascota in mascotasDelCliente" :key="mascota.idMascota" :label="mascota.nombre" :value="mascota.idMascota" />
              </el-select>
            </el-form-item>
            <el-form-item label="Veterinario">
              <el-input :model-value="authState.user?.usuario || 'Veterinario de la sesión'" readonly />
            </el-form-item>
            <el-form-item label="Servicio solicitado" prop="idServicio" :error="fieldErrors.idServicio">
              <el-select v-model="form.idServicio" class="full-width" placeholder="Seleccione el servicio">
                <el-option v-for="servicio in servicios" :key="servicio.idServicio" :label="servicio.nombre" :value="servicio.idServicio" />
              </el-select>
            </el-form-item>
            <el-form-item label="Fecha" prop="fecha" :error="fieldErrors.fecha">
              <el-date-picker v-model="form.fecha" type="date" format="DD/MM/YYYY" value-format="YYYY-MM-DD" :disabled-date="esDiaPasado" class="full-width" />
            </el-form-item>
            <el-form-item label="Hora" prop="hora" :error="fieldErrors.hora">
              <el-time-picker v-model="form.hora" format="HH:mm" value-format="HH:mm" class="full-width" />
            </el-form-item>
            <el-form-item label="Motivo" prop="motivo" :error="fieldErrors.motivo">
              <el-input v-model="form.motivo" maxlength="255" />
            </el-form-item>
            <el-form-item label="Observaciones" prop="observaciones" :error="fieldErrors.observaciones">
              <el-input v-model="form.observaciones" maxlength="255" />
            </el-form-item>
          </div>
          <div class="form-actions">
            <el-button type="primary" native-type="submit" :loading="saving">Registrar cita</el-button>
            <el-button plain @click="openList">Cancelar</el-button>
          </div>
        </el-form>
      </div>
    </template>

    <template v-else-if="mode === 'edit'">
      <header class="module-heading">
        <h1>Modificar cita</h1>
        <p>Referencias: R5.2, R5.2.1–5</p>
      </header>
      <div class="form-panel">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="submitEdit">
          <div class="form-columns">
            <el-form-item label="Buscar cita registrada">
              <el-input :model-value="selectedCita.codigo" readonly />
            </el-form-item>
            <el-form-item label="Cliente y mascota">
              <el-input :model-value="`${selectedCita.cliente} — ${selectedCita.mascota}`" readonly />
            </el-form-item>
            <el-form-item label="Servicio" prop="idServicio" :error="fieldErrors.idServicio">
              <el-select v-model="form.idServicio" class="full-width">
                <el-option v-for="servicio in servicios" :key="servicio.idServicio" :label="servicio.nombre" :value="servicio.idServicio" />
              </el-select>
            </el-form-item>
            <el-form-item label="Fecha" prop="fecha" :error="fieldErrors.fecha">
              <el-date-picker v-model="form.fecha" type="date" format="DD/MM/YYYY" value-format="YYYY-MM-DD" :disabled-date="esDiaPasado" class="full-width" />
            </el-form-item>
            <el-form-item label="Hora" prop="hora" :error="fieldErrors.hora">
              <el-time-picker v-model="form.hora" format="HH:mm" value-format="HH:mm" class="full-width" />
            </el-form-item>
          </div>
          <div class="form-actions">
            <el-button type="primary" disabled>Buscar cita</el-button>
            <el-button plain native-type="submit" :loading="saving">Guardar cambios</el-button>
            <el-button plain @click="openList">Cancelar</el-button>
          </div>
        </el-form>
      </div>
    </template>

    <template v-else>
      <header class="module-heading">
        <h1>Cancelar cita</h1>
        <p>Referencias: R5.3, R5.3.1–4</p>
      </header>
      <div class="form-panel delete-panel">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <h2>¿Cancelar esta cita?</h2>
        <div class="form-columns">
          <el-form-item label="Buscar cita"><el-input :model-value="selectedCita.codigo" readonly /></el-form-item>
          <el-form-item label="Cliente y mascota"><el-input :model-value="`${selectedCita.cliente} — ${selectedCita.mascota}`" readonly /></el-form-item>
          <el-form-item label="Fecha y hora"><el-input :model-value="formatDateTime(selectedCita.fechaHora)" readonly /></el-form-item>
          <el-form-item label="Servicio"><el-input :model-value="selectedCita.servicio" readonly /></el-form-item>
        </div>
        <div class="form-actions">
          <el-button type="primary" :loading="saving" @click="confirmCancel">Sí, cancelar cita</el-button>
          <el-button plain @click="openList">Volver al listado</el-button>
        </div>
        <p class="form-help">Si no confirmas, la cita permanece programada.</p>
      </div>
    </template>
  </section>
</template>
