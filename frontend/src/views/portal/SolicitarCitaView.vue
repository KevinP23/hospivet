<script setup>
// Interfaz Solicitar cita del cliente (3.1.30, R10.1), tal cual el mockup. Decisiones del plan R10: D-21.
import { onMounted, reactive, ref, watch } from 'vue'
import { apiMessage } from '../../api/http'
import { horariosDisponibles, misMascotas, serviciosDisponibles, solicitarCita } from '../../api/portal'
import { hoyLocal } from '../../utils/fechas'

const mascotas = ref([])
const servicios = ref([])
const horarios = ref([])
const form = reactive({ idMascota: null, idServicio: null, fecha: '', hora: '' })
const loading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

// N-03 A: no se eligen días pasados ni domingos.
function diaNoDisponible(fecha) {
  const inicioDeHoy = new Date()
  inicioDeHoy.setHours(0, 0, 0, 0)
  return fecha.getTime() < inicioDeHoy.getTime() || fecha.getDay() === 0
}

function primerDiaDeAtencion() {
  const hoy = new Date(`${hoyLocal()}T00:00:00`)
  if (hoy.getDay() === 0) hoy.setDate(hoy.getDate() + 1)
  const dosDigitos = (valor) => String(valor).padStart(2, '0')
  return `${hoy.getFullYear()}-${dosDigitos(hoy.getMonth() + 1)}-${dosDigitos(hoy.getDate())}`
}

async function cargarHorarios() {
  form.hora = ''
  if (!form.fecha || !form.idServicio) {
    horarios.value = []
    return
  }
  try {
    horarios.value = await horariosDisponibles(form.fecha, form.idServicio)
  } catch (error) {
    horarios.value = []
    errorMessage.value = apiMessage(error)
  }
}

watch(() => [form.fecha, form.idServicio], () => {
  successMessage.value = ''
  cargarHorarios()
})

function elegirHora(horario) {
  if (!horario.disponible) return
  form.hora = horario.hora
}

async function confirmar() {
  // Sin todos los datos elegidos el botón no hace nada (no se agregan mensajes que no están en el PDF).
  if (!form.idMascota || !form.idServicio || !form.fecha || !form.hora || loading.value) return
  errorMessage.value = ''
  successMessage.value = ''
  loading.value = true
  try {
    const data = await solicitarCita({ ...form })
    // N-06 A: "Cita solicitada" (SD-30); el horario pasa a ocupado.
    successMessage.value = data.mensaje
  } catch (error) {
    // R10.1.5: "Horario no disponible, seleccione otro"; se conservan mascota, servicio y fecha.
    errorMessage.value = apiMessage(error)
  } finally {
    loading.value = false
    await cargarHorarios()
  }
}

onMounted(async () => {
  try {
    ;[mascotas.value, servicios.value] = await Promise.all([misMascotas(), serviciosDisponibles()])
    form.idMascota = mascotas.value[0]?.idMascota ?? null
    form.idServicio = servicios.value[0]?.idServicio ?? null
    form.fecha = primerDiaDeAtencion()
  } catch (error) {
    errorMessage.value = apiMessage(error)
  }
})
</script>

<template>
  <section class="module-page">
    <header class="module-heading">
      <h1>Solicitar cita del cliente</h1>
      <p>Referencias: R10.1, R10.1.1–6</p>
    </header>
    <div class="form-panel portal-panel" v-loading="loading">
      <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" class="portal-alert" />
      <el-alert v-if="successMessage" :title="successMessage" type="success" :closable="false" class="portal-alert" />
      <el-form label-position="top" @submit.prevent>
        <div class="form-columns">
          <el-form-item label="Mascota">
            <el-select v-model="form.idMascota" class="full-width">
              <el-option v-for="mascota in mascotas" :key="mascota.idMascota" :label="mascota.nombre" :value="mascota.idMascota" />
            </el-select>
          </el-form-item>
          <el-form-item label="Servicio">
            <el-select v-model="form.idServicio" class="full-width">
              <el-option v-for="servicio in servicios" :key="servicio.idServicio" :label="servicio.nombre" :value="servicio.idServicio" />
            </el-select>
          </el-form-item>
          <el-form-item label="Seleccionar fecha">
            <el-date-picker v-model="form.fecha" type="date" format="DD/MM/YYYY" value-format="YYYY-MM-DD"
              :clearable="false" :disabled-date="diaNoDisponible" class="full-width" />
          </el-form-item>
        </div>
        <el-form-item label="Horarios disponibles">
          <div class="slot-list">
            <button
              v-for="horario in horarios"
              :key="horario.hora"
              type="button"
              class="slot"
              :class="{ selected: form.hora === horario.hora, busy: !horario.disponible }"
              :disabled="!horario.disponible"
              @click="elegirHora(horario)"
            >
              {{ horario.disponible ? horario.hora : `${horario.hora} ocupado` }}
            </button>
          </div>
        </el-form-item>
      </el-form>
      <el-button type="primary" @click="confirmar">Confirmar cita</el-button>
    </div>
  </section>
</template>

<style scoped>
.portal-panel {
  min-height: auto;
}

.portal-alert {
  margin-bottom: 18px;
}

.slot-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.slot {
  min-width: 120px;
  padding: 11px 16px;
  border: 1px solid var(--hv-border);
  border-radius: 8px;
  color: var(--hv-primary);
  background: #eaf0f8;
  font-size: 16px;
  font-weight: 800;
  cursor: pointer;
}

.slot.selected {
  border-color: var(--hv-primary);
  color: #fff;
  background: var(--hv-primary);
}

.slot.busy {
  color: #8a96a6;
  background: #f1f3f6;
  cursor: not-allowed;
}
</style>
