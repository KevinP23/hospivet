<script setup>
import { useRoute, useRouter } from 'vue-router'
import { authState, logout } from '../api/auth'

defineProps({
  authenticated: {
    type: Boolean,
    default: false,
  },
})

const route = useRoute()
const router = useRouter()

const navigation = [
  { label: 'Menú principal', path: '/', enabled: true },
  { label: 'Clientes', path: '/clientes', enabled: true },
  { label: 'Mascotas', path: '/mascotas', enabled: true },
  { label: 'Inventario', path: '/inventario', enabled: true },
  { label: 'Citas', path: '/citas', enabled: true },
  { label: 'Atención médica', path: '/atencion', enabled: true },
  { label: 'Facturación', path: '/facturacion', enabled: true },
  { label: 'Reportes', enabled: false },
  { label: 'Promociones', path: '/promociones', enabled: true },
]

function isActive(item) {
  if (!item.path) return false
  if (item.path === '/') return route.path === '/' || route.name === 'login'
  return route.path.startsWith(item.path)
}

async function handleProfileCommand(command) {
  if (command !== 'logout') return
  await logout()
  await router.push({ name: 'login' })
}
</script>

<template>
  <header class="app-header">
    <div class="brand-row">
      <router-link class="brand-lockup" to="/" aria-label="HOSPIVET">
        <span class="brand-mark">HV</span>
        <strong>HOSPIVET · Sistema de Gestión</strong>
      </router-link>

      <el-dropdown v-if="authenticated" trigger="click" @command="handleProfileCommand">
        <button class="profile-pill" type="button">
          {{ authState.user?.usuario || 'Veterinario' }} · Veterinaria
        </button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="logout">Cerrar sesión</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <span v-else class="profile-pill">Acceso veterinario</span>
    </div>

    <nav class="app-navigation" aria-label="Navegación principal">
      <template v-for="item in navigation" :key="item.label">
        <router-link
          v-if="item.enabled"
          :to="item.path"
          class="nav-item"
          :class="{ active: isActive(item) }"
        >
          {{ item.label }}
        </router-link>
        <span v-else class="nav-item disabled" :title="`${item.label} se implementará en otra iteración`">
          {{ item.label }}
        </span>
      </template>
    </nav>
  </header>
</template>
