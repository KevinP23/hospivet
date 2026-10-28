<script setup>
// Interfaz del menú principal después de iniciar sesión (Iteración 5, 3.1.1). COR-02.
const accesos = [
  // N-03: Clientes y mascotas abre Clientes; Mascotas sigue en la navegación.
  { titulo: 'Clientes y mascotas', descripcion: 'Registrar clientes y sus mascotas', path: '/clientes' },
  { titulo: 'Citas', descripcion: 'Registrar, consultar, modificar y cancelar citas', path: '/citas' },
  { titulo: 'Atención médica', descripcion: 'Historial, diagnósticos, tratamientos y recetas', path: '/atencion' },
  { titulo: 'Inventario', descripcion: 'Productos, existencias y movimientos', path: '/inventario' },
  { titulo: 'Facturación', descripcion: 'Revisar, editar, generar, imprimir y enviar', path: '/facturacion' },
  // N-01: visible y deshabilitado hasta construir R06, igual que en la navegación.
  { titulo: 'Reportes', descripcion: 'Inventario, ingresos, servicios y productos', path: null },
  { titulo: 'Promociones', descripcion: 'Crear, editar, publicar y eliminar', path: '/promociones' },
]
</script>

<template>
  <section class="module-page">
    <header class="module-heading">
      <h1>Menú principal</h1>
      <p>Selecciona el módulo con el que deseas trabajar</p>
    </header>
    <div class="menu-grid">
      <template v-for="acceso in accesos" :key="acceso.titulo">
        <router-link v-if="acceso.path" :to="acceso.path" class="menu-card">
          <span class="menu-mark">HV</span>
          <span class="menu-text">
            <strong>{{ acceso.titulo }}</strong>
            <span>{{ acceso.descripcion }}</span>
          </span>
        </router-link>
        <div v-else class="menu-card disabled" :title="`${acceso.titulo} se implementará en otra iteración`" aria-disabled="true">
          <span class="menu-mark">HV</span>
          <span class="menu-text">
            <strong>{{ acceso.titulo }}</strong>
            <span>{{ acceso.descripcion }}</span>
          </span>
        </div>
      </template>
    </div>
  </section>
</template>

<style scoped>
.menu-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 22px 32px;
}

.menu-card {
  display: flex;
  align-items: flex-start;
  gap: 19px;
  min-height: 108px;
  padding: 23px 19px;
  border: 1px solid var(--hv-border);
  border-radius: 12px;
  color: var(--hv-text);
  background: #fff;
  text-decoration: none;
}

a.menu-card:hover,
a.menu-card:focus-visible {
  border-color: var(--hv-primary);
}

.menu-card.disabled {
  cursor: not-allowed;
  opacity: 0.92;
}

.menu-mark {
  display: grid;
  width: 49px;
  height: 49px;
  place-items: center;
  flex: 0 0 auto;
  border-radius: 10px;
  color: var(--hv-primary-dark);
  background: var(--hv-nav);
  font-size: 17px;
  font-weight: 800;
}

.menu-text {
  display: grid;
  gap: 9px;
  padding-top: 2px;
}

.menu-text strong {
  font-size: 24px;
}

.menu-text span {
  color: var(--hv-muted);
  font-size: 16px;
}

@media (max-width: 680px) {
  .menu-grid {
    grid-template-columns: 1fr;
  }
}
</style>
