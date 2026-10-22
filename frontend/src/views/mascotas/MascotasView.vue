<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listarClientes } from '../../api/clientes'
import { registrarMascota } from '../../api/mascotas'
import { apiFieldErrors, apiMessage } from '../../api/http'
import { hoyLocal } from '../../utils/fechas'

const formRef = ref()
const clientes = ref([])
const loading = ref(false)
const saving = ref(false)
const errorMessage = ref('')
const fieldErrors = ref({})

const emptyForm = () => ({
  idCliente: null,
  nombre: '',
  especie: '',
  raza: '',
  fechaNacimiento: '',
  peso: '',
  sexo: '',
  observaciones: '',
})
const form = reactive(emptyForm())

const rules = {
  idCliente: [{ required: true, message: 'Seleccione el cliente propietario', trigger: 'change' }],
  nombre: [{ required: true, message: 'Complete los campos obligatorios', trigger: 'blur' }],
  especie: [{ required: true, message: 'Complete los campos obligatorios', trigger: 'blur' }],
  peso: [
    { required: true, message: 'Complete los campos obligatorios', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value == null || Number(value) <= 0 || !/^\d{1,4}(\.\d{1,2})?$/.test(String(value).trim())) {
          callback(new Error('Edad o peso inválido'))
        }
        else callback()
      },
      trigger: 'blur',
    },
  ],
  fechaNacimiento: [
    {
      validator: (_rule, value, callback) => {
        if (value && value > hoyLocal()) callback(new Error('Edad o peso inválido'))
        else callback()
      },
      trigger: 'change',
    },
  ],
}

const calculatedAge = computed(() => {
  if (!form.fechaNacimiento) return ''
  const birthDate = new Date(`${form.fechaNacimiento}T00:00:00`)
  const currentDate = new Date()
  let months = (currentDate.getFullYear() - birthDate.getFullYear()) * 12
    + currentDate.getMonth() - birthDate.getMonth()
  if (currentDate.getDate() < birthDate.getDate()) months -= 1
  if (months < 0) return ''
  if (months < 12) return `${months} ${months === 1 ? 'mes' : 'meses'}`
  const age = Math.floor(months / 12)
  return `${age} ${age === 1 ? 'año' : 'años'}`
})

async function load() {
  loading.value = true
  try {
    clientes.value = await listarClientes()
  } catch (error) {
    errorMessage.value = apiMessage(error, 'No fue posible cargar los datos')
  } finally {
    loading.value = false
  }
}

async function submit() {
  errorMessage.value = ''
  fieldErrors.value = {}
  const validationErrors = await formRef.value.validate().then(() => null).catch((errors) => errors)
  if (validationErrors) {
    const invalidAgeOrWeight = ['fechaNacimiento', 'peso'].some((field) =>
      validationErrors[field]?.some((error) => error.message === 'Edad o peso inválido'),
    )
    errorMessage.value = invalidAgeOrWeight ? 'Edad o peso inválido' : 'Complete los campos obligatorios'
    return
  }

  saving.value = true
  try {
    const payload = {
      ...form,
      fechaNacimiento: form.fechaNacimiento || null,
      peso: Number(form.peso),
      sexo: form.sexo || null,
    }
    await registrarMascota(payload)
    ElMessage.success('Mascota registrada y vinculada al propietario')
    Object.assign(form, emptyForm())
    formRef.value.clearValidate()
  } catch (error) {
    errorMessage.value = apiMessage(error, 'No fue posible registrar la mascota')
    fieldErrors.value = apiFieldErrors(error)
  } finally {
    saving.value = false
  }
}

function cancel() {
  Object.assign(form, emptyForm())
  errorMessage.value = ''
  fieldErrors.value = {}
  formRef.value?.clearValidate()
}

onMounted(load)
</script>

<template>
  <section class="module-page">
    <header class="module-heading">
      <h1>Registrar mascota</h1>
      <p>Referencias: R3.1, R3.1.1–7</p>
    </header>

    <div class="form-panel" v-loading="loading">
      <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="submit">
        <div class="form-columns">
          <el-form-item label="Cliente propietario" prop="idCliente" :error="fieldErrors.idCliente">
            <el-select v-model="form.idCliente" filterable placeholder="Seleccione un cliente" class="full-width">
              <el-option
                v-for="cliente in clientes"
                :key="cliente.idCliente"
                :label="`${cliente.nombre} ${cliente.apellido} — CI ${cliente.ci}`"
                :value="cliente.idCliente"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="Nombre" prop="nombre" :error="fieldErrors.nombre">
            <el-input v-model="form.nombre" maxlength="60" />
          </el-form-item>
          <el-form-item label="Especie" prop="especie" :error="fieldErrors.especie">
            <el-input v-model="form.especie" maxlength="40" />
          </el-form-item>
          <el-form-item label="Raza" prop="raza" :error="fieldErrors.raza">
            <el-input v-model="form.raza" maxlength="60" />
          </el-form-item>
          <el-form-item label="Fecha de nacimiento" prop="fechaNacimiento" :error="fieldErrors.fechaNacimiento">
            <el-date-picker
              v-model="form.fechaNacimiento"
              type="date"
              format="DD/MM/YYYY"
              value-format="YYYY-MM-DD"
              :disabled-date="(date) => date.getTime() > Date.now()"
              class="full-width"
            />
          </el-form-item>
          <el-form-item label="Edad calculada">
            <el-input :model-value="calculatedAge" readonly placeholder="Se calcula con la fecha" />
          </el-form-item>
          <el-form-item label="Peso en kg" prop="peso" :error="fieldErrors.peso">
            <el-input v-model="form.peso" type="number" step="0.1" />
          </el-form-item>
          <el-form-item label="Sexo" prop="sexo" :error="fieldErrors.sexo">
            <el-select v-model="form.sexo" placeholder="Seleccione" class="full-width" clearable>
              <el-option label="Macho" value="M" />
              <el-option label="Hembra" value="H" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="Observaciones" prop="observaciones" :error="fieldErrors.observaciones">
          <el-input v-model="form.observaciones" maxlength="255" />
        </el-form-item>
        <div class="form-actions">
          <el-button type="primary" native-type="submit" :loading="saving">Guardar mascota</el-button>
          <el-button plain @click="cancel">Cancelar</el-button>
        </div>
      </el-form>
    </div>
  </section>
</template>

