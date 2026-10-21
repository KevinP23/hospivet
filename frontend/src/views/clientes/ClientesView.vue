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

const rules = {
  ci: [
    { required: true, message: 'Complete los campos obligatorios', trigger: 'blur' },
    { pattern: /^\d+$/, message: 'CI inválido', trigger: 'blur' },
  ],
  nombre: [{ required: true, message: 'Complete los campos obligatorios', trigger: 'blur' }],
  apellido: [{ required: true, message: 'Complete los campos obligatorios', trigger: 'blur' }],
  telefono: [{ required: true, message: 'Complete los campos obligatorios', trigger: 'blur' }],
}

async function submit() {
  errorMessage.value = ''
  fieldErrors.value = {}
  const validationErrors = await formRef.value.validate().then(() => null).catch((errors) => errors)
  if (validationErrors) {
    const invalidCi = validationErrors.ci?.some((error) => error.message === 'CI inválido')
    errorMessage.value = invalidCi ? 'CI inválido' : 'Complete los campos obligatorios'
    return
  }

  saving.value = true
  try {
    await registrarCliente({ ...form })
    ElMessage.success('Cliente registrado correctamente')
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

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="submit">
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

