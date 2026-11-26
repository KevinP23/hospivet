import { createRouter, createWebHistory } from 'vue-router'
import { authState, refreshSession } from '../api/auth'
import LoginView from '../views/auth/LoginView.vue'
import RecuperarContrasenaView from '../views/auth/RecuperarContrasenaView.vue'
import MainLayout from '../layouts/MainLayout.vue'
import PortalLayout from '../layouts/PortalLayout.vue'
import ClientesView from '../views/clientes/ClientesView.vue'
import MascotasView from '../views/mascotas/MascotasView.vue'
import InventarioView from '../views/inventario/InventarioView.vue'
import CitasView from '../views/citas/CitasView.vue'
import AtencionView from '../views/atencion/AtencionView.vue'
import PromocionesView from '../views/promociones/PromocionesView.vue'
import FacturacionView from '../views/facturacion/FacturacionView.vue'
import MenuPrincipalView from '../views/menu/MenuPrincipalView.vue'
import ReportesView from '../views/reportes/ReportesView.vue'
import SolicitarCitaView from '../views/portal/SolicitarCitaView.vue'
import ServiciosClienteView from '../views/portal/ServiciosClienteView.vue'
import PromocionesClienteView from '../views/portal/PromocionesClienteView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', name: 'login', component: LoginView },
    // R1.2: pública, como el inicio de sesión (3.1.2 es extend de Iniciar sesión).
    { path: '/recuperar-contrasena', name: 'recuperar-contrasena', component: RecuperarContrasenaView },
    {
      path: '/',
      component: MainLayout,
      meta: { requiresAuth: true, rol: 'VETERINARIO' },
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
    // R10 Portal Cliente (D-21): solo el rol CLIENTE.
    {
      path: '/portal',
      component: PortalLayout,
      meta: { requiresAuth: true, rol: 'CLIENTE' },
      children: [
        { path: '', redirect: { name: 'portal-cita' } },
        { path: 'cita', name: 'portal-cita', component: SolicitarCitaView },
        { path: 'servicios', name: 'portal-servicios', component: ServiciosClienteView },
        { path: 'promociones', name: 'portal-promociones', component: PromocionesClienteView },
      ],
    },
  ],
})

/** Pantalla inicial de cada rol: menú principal del veterinario o Reservar cita del cliente. */
export function inicioSegunRol(user) {
  return user?.rol === 'CLIENTE' ? { name: 'portal-cita' } : { name: 'menu' }
}

router.beforeEach(async (to) => {
  if (!authState.checked) {
    await refreshSession()
  }
  if (to.meta.requiresAuth && !authState.user) {
    return { name: 'login' }
  }
  if ((to.name === 'login' || to.name === 'recuperar-contrasena') && authState.user) {
    return inicioSegunRol(authState.user)
  }
  // Cada rol solo abre sus pantallas (alcance del actor Cliente, 2.5).
  const rolRequerido = [...to.matched].reverse().find((registro) => registro.meta.rol)?.meta.rol
  if (rolRequerido && authState.user && authState.user.rol !== rolRequerido) {
    return inicioSegunRol(authState.user)
  }
  return true
})

export default router
