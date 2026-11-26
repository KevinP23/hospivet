<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { registrarCliente } from '../../api/clientes'
import { apiFieldErrors, apiMessage } from '../../api/http'

const formRef = ref()
const saving = ref(false)
const errorMessage = ref('')
const fieldErrors = ref({})

const emptyForm = () => ({
  ci: '', nombre: '', apellido: '', telefono: '', correo: '', direccion: '',
})
const form = reactive(emptyForm())

// COR-04 D2: bajo el campo vacío va "Campo obligatorio"; la alerta dice "Complete los campos obligatorios" (pág. 65).
const CAMPO_OBLIGATORIO = 'Campo obligatorio'
const rules = {
  ci: [
    { required: true, message: CAMPO_OBLIGATORIO, trigger: 'blur' },
    { pattern: /^\d+$/, message: 'CI inválido', trigger: 'blur' },
  ],
  nombre: [{ required: true, message: CAMPO_OBLIGATORIO, trigger: 'blur' }],
  apellido: [{ required: true, message: CAMPO_OBLIGATORIO, trigger: 'blur' }],
  telefono: [{ required: true, message: CAMPO_OBLIGATORIO, trigger: 'blur' }],
  correo: [{ type: 'email', message: 'Correo inválido', trigger: 'blur' }],
}

async function submit() {
  errorMessage.value = ''
  fieldErrors.value = {}
  const validationErrors = await formRef.value.validate().then(() => null).catch((errors) => errors)
  if (validationErrors) {
    const invalidCi = validationErrors.ci?.some((error) => error.message === 'CI inválido')
    const invalidEmail = validationErrors.correo?.some((error) => error.message === 'Correo inválido')
    if (invalidCi) errorMessage.value = 'CI inválido'
    else if (invalidEmail && Object.keys(validationErrors).length === 1) errorMessage.value = 'Correo inválido'
    else errorMessage.value = 'Complete los campos obligatorios'
    return
  }

  saving.value = true
  try {
    await registrarCliente({ ...form })
    // COR-04 D3: texto de SD-03 (pasos 14 y 16).
    ElMessage.success('Cliente registrado')
    Object.assign(form, emptyForm())
    formRef.value.clearValidate()
  } catch (error) {
    errorMessage.value = apiMessage(error, 'No fue posible registrar el cliente')
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
</script>

<template>
  <section class="module-page">
    <header class="module-heading">
      <h1>Registrar cliente</h1>
      <p>Referencias: R2.1, R2.1.1–6</p>
    </header>

    <div class="form-panel">
      <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />

      <!-- COR-04 D1: sin asteriscos, como el mockup. -->
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" hide-required-asterisk @submit.prevent="submit">
        <div class="form-columns">
          <el-form-item label="Nombre" prop="nombre" :error="fieldErrors.nombre">
            <el-input v-model="form.nombre" maxlength="60" />
          </el-form-item>
          <el-form-item label="Apellido" prop="apellido" :error="fieldErrors.apellido">
            <el-input v-model="form.apellido" maxlength="60" />
          </el-form-item>
          <el-form-item label="Carnet de identidad" prop="ci" :error="fieldErrors.ci">
            <el-input v-model="form.ci" maxlength="15" />
          </el-form-item>
          <el-form-item label="Teléfono" prop="telefono" :error="fieldErrors.telefono">
            <el-input v-model="form.telefono" maxlength="20" />
          </el-form-item>
          <el-form-item label="Correo" prop="correo" :error="fieldErrors.correo">
            <el-input v-model="form.correo" maxlength="100" />
          </el-form-item>
          <el-form-item label="Dirección" prop="direccion" :error="fieldErrors.direccion">
            <el-input v-model="form.direccion" maxlength="150" />
          </el-form-item>
        </div>
        <div class="form-actions">
          <el-button type="primary" native-type="submit" :loading="saving">Guardar cliente</el-button>
          <el-button plain @click="cancel">Cancelar</el-button>
        </div>
      </el-form>
    </div>
  </section>
</template>

