import { createRouter, createWebHistory } from 'vue-router'
import { authState, refreshSession } from '../api/auth'
import LoginView from '../views/auth/LoginView.vue'
import MainLayout from '../layouts/MainLayout.vue'
import ClientesView from '../views/clientes/ClientesView.vue'
import MascotasView from '../views/mascotas/MascotasView.vue'
import InventarioView from '../views/inventario/InventarioView.vue'
import CitasView from '../views/citas/CitasView.vue'
import AtencionView from '../views/atencion/AtencionView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'login', component: LoginView },
    {
      path: '/',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        { path: '', redirect: '/clientes' },
        { path: 'clientes', name: 'clientes', component: ClientesView },
        { path: 'mascotas', name: 'mascotas', component: MascotasView },
        { path: 'inventario', name: 'inventario', component: InventarioView },
        { path: 'citas', name: 'citas', component: CitasView },
        { path: 'atencion', name: 'atencion', component: AtencionView },
      ],
    },
  ],
})

router.beforeEach(async (to) => {
  if (!authState.checked) {
    await refreshSession()
  }
  if (to.meta.requiresAuth && !authState.user) {
    return { name: 'login' }
  }
  if (to.name === 'login' && authState.user) {
    return { name: 'clientes' }
  }
  return true
})

export default router

