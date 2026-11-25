<script setup>
// Interfaz Recuperar contraseña (Iteración 5, 3.1.2, R1.2), tal cual el mockup. Decisiones del plan: D-20.
import { ref } from 'vue'
import { recuperarContrasena } from '../../api/auth'
import { apiMessage } from '../../api/http'
import AppHeader from '../../components/AppHeader.vue'

const identidad = ref('')
const loading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

async function enviar() {
  // N-05 A: con el campo vacío el botón no hace nada y no se agrega ningún mensaje.
  if (!identidad.value.trim() || loading.value) return

  errorMessage.value = ''
  successMessage.value = ''
  loading.value = true
  try {
    const data = await recuperarContrasena(identidad.value.trim())
    // N-04 A: "Recuperación solicitada" (SD-02, pasos 14 y 16) y el campo se limpia.
    successMessage.value = data.mensaje
    identidad.value = ''
  } catch (error) {
    // R1.2.3 "Usuario no registrado"; N-03 A: alerta general si el correo no se pudo enviar.
    errorMessage.value = apiMessage(error)
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
          <h1>Recuperar contraseña</h1>
          <p>Ingresa tu usuario o correo registrado</p>
        </div>

        <el-form label-position="top" @submit.prevent="enviar">
          <el-form-item label="Usuario o correo registrado">
            <el-input v-model="identidad" maxlength="100" autocomplete="username" @keyup.enter="enviar" />
          </el-form-item>
          <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" class="recovery-alert" />
          <el-alert v-if="successMessage" :title="successMessage" type="success" :closable="false" class="recovery-alert" />
          <el-button type="primary" native-type="submit" :loading="loading">Enviar contraseña temporal</el-button>
        </el-form>
        <p class="password-recovery">
          <router-link :to="{ name: 'login' }">Volver al inicio de sesión</router-link>
        </p>
      </section>
    </main>
  </div>
</template>

<style scoped>
.recovery-alert {
  margin-bottom: 18px;
}

.password-recovery a {
  color: inherit;
  text-decoration: none;
}

.password-recovery a:hover,
.password-recovery a:focus-visible {
  text-decoration: underline;
}
</style>
