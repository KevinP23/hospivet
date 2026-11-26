<script setup>
// Portal Cliente (Iteración 5, 3.1.30 a 3.1.32): cabecera y navegación tal cual los mockups, en azul.
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { authState, logout } from '../api/auth'

const route = useRoute()
const router = useRouter()

const navigation = [
  { label: 'Reservar cita', name: 'portal-cita' },
  { label: 'Servicios', name: 'portal-servicios' },
  { label: 'Promociones', name: 'portal-promociones' },
]

const nombreVisible = computed(() => {
  const user = authState.user
  return [user?.nombre, user?.apellido].filter(Boolean).join(' ') || user?.usuario || 'Cliente'
})

async function handleProfileCommand(command) {
  if (command !== 'logout') return
  await logout()
  await router.push({ name: 'login' })
}
</script>

<template>
  <div class="shell portal-shell">
    <header class="app-header">
      <div class="brand-row">
        <router-link class="brand-lockup" :to="{ name: 'portal-cita' }" aria-label="HOSPIVET">
          <span class="brand-mark">HV</span>
          <strong>HOSPIVET · Portal Cliente</strong>
        </router-link>
        <el-dropdown trigger="click" @command="handleProfileCommand">
          <button class="profile-pill" type="button">{{ nombreVisible }} · Cliente</button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">Cerrar sesión</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <nav class="app-navigation" aria-label="Navegación del portal">
        <router-link
          v-for="item in navigation"
          :key="item.name"
          :to="{ name: item.name }"
          class="nav-item"
          :class="{ active: route.name === item.name }"
        >
          {{ item.label }}
        </router-link>
      </nav>
    </header>
    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<style>
/* Colores del Portal Cliente de los mockups (azul); el resto de la aplicación conserva el verde. */
.portal-shell {
  --hv-primary: #1f4f86;
  --hv-primary-dark: #173d69;
  --hv-nav: #e7eef7;
  --hv-border: #d3dce8;
  --hv-muted: #66758a;
  background: #f4f7fb;
}

.portal-shell .nav-item.active {
  color: var(--hv-primary);
}

.portal-shell .empty-appointments {
  color: var(--hv-primary-dark);
  background: #eaf0f8;
}
</style>
