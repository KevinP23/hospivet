<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../../api/auth'
import { apiFieldErrors, apiMessage } from '../../api/http'
import AppHeader from '../../components/AppHeader.vue'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const errorMessage = ref('')
const fieldErrors = ref({})
const form = reactive({ usuario: '', contrasena: '' })

const rules = {
  usuario: [{ required: true, message: 'Ingrese el usuario', trigger: 'blur' }],
  contrasena: [{ required: true, message: 'Ingrese la contraseña', trigger: 'blur' }],
}

async function submit() {
  errorMessage.value = ''
  fieldErrors.value = {}
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await login({ ...form })
    await router.push({ name: 'clientes' })
  } catch (error) {
    errorMessage.value = apiMessage(error, 'No fue posible iniciar sesión')
    fieldErrors.value = apiFieldErrors(error)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-shell">
    <AppHeader />
    <main class="login-page">
      <section class="login-card">
        <div class="login-heading">
          <span class="brand-mark large">HV</span>
          <h1>Bienvenido de nuevo</h1>
          <p>Ingresa tus credenciales para continuar</p>
        </div>

        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />

        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="submit">
          <el-form-item label="Usuario" prop="usuario" :error="fieldErrors.usuario">
            <el-input v-model="form.usuario" maxlength="50" autocomplete="username" />
          </el-form-item>
          <el-form-item label="Contraseña" prop="contrasena" :error="fieldErrors.contrasena">
            <el-input
              v-model="form.contrasena"
              type="password"
              show-password
              autocomplete="current-password"
              @keyup.enter="submit"
            />
          </el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading">Ingresar</el-button>
        </el-form>
        <p class="password-recovery">¿Olvidaste tu contraseña? <span>Recuperar contraseña</span></p>
      </section>
    </main>
  </div>
</template>

