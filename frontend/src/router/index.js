import { createRouter, createWebHistory } from 'vue-router'
import { authState, refreshSession } from '../api/auth'
import LoginView from '../views/auth/LoginView.vue'
import RecuperarContrasenaView from '../views/auth/RecuperarContrasenaView.vue'
import MainLayout from '../layouts/MainLayout.vue'
import ClientesView from '../views/clientes/ClientesView.vue'
import MascotasView from '../views/mascotas/MascotasView.vue'
import InventarioView from '../views/inventario/InventarioView.vue'
import CitasView from '../views/citas/CitasView.vue'
import AtencionView from '../views/atencion/AtencionView.vue'
import PromocionesView from '../views/promociones/PromocionesView.vue'
import FacturacionView from '../views/facturacion/FacturacionView.vue'
import MenuPrincipalView from '../views/menu/MenuPrincipalView.vue'
import ReportesView from '../views/reportes/ReportesView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'login', component: LoginView },
    // R1.2: pública, como el inicio de sesión (3.1.2 es extend de Iniciar sesión).
    { path: '/recuperar-contrasena', name: 'recuperar-contrasena', component: RecuperarContrasenaView },
    {
      path: '/',
      component: MainLayout,
      meta: { requiresAuth: true },
      children: [
        { path: '', name: 'menu', component: MenuPrincipalView },
        { path: 'clientes', name: 'clientes', component: ClientesView },
        { path: 'mascotas', name: 'mascotas', component: MascotasView },
        { path: 'inventario', name: 'inventario', component: InventarioView },
        { path: 'citas', name: 'citas', component: CitasView },
        { path: 'atencion', name: 'atencion', component: AtencionView },
        { path: 'promociones', name: 'promociones', component: PromocionesView },
        { path: 'facturacion', name: 'facturacion', component: FacturacionView },
        // R06: Reportes abre el reporte de inventario; su campo Tipo de reporte lleva a los demás (N-01 B).
        { path: 'reportes', name: 'reportes', component: ReportesView, meta: { reporte: 'inventario' } },
        { path: 'reportes/ingresos', name: 'reportes-ingresos', component: ReportesView, meta: { reporte: 'ingresos' } },
        { path: 'reportes/servicios', name: 'reportes-servicios', component: ReportesView, meta: { reporte: 'servicios' } },
        { path: 'reportes/productos', name: 'reportes-productos', component: ReportesView, meta: { reporte: 'productos' } },
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
  if ((to.name === 'login' || to.name === 'recuperar-contrasena') && authState.user) {
    return { name: 'menu' }
  }
  return true
})

export default router

