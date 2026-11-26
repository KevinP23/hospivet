<script setup>
// Interfaz Consultar servicios (Cliente) (3.1.31, R10.2), tal cual el mockup. N-07 A (D-21).
import { onMounted, ref } from 'vue'
import { apiMessage } from '../../api/http'
import { serviciosDisponibles } from '../../api/portal'

const servicios = ref([])
const cargado = ref(false)
const loading = ref(false)
const errorMessage = ref('')
const marcado = ref(null)
const detalle = ref(null)

const bolivianos = (valor) => new Intl.NumberFormat('es-BO', { style: 'currency', currency: 'BOB' }).format(Number(valor || 0))

// R10.2.2 Seleccionar servicio; R10.2.3–4 su precio y descripción.
function verSeleccionado() {
  detalle.value = marcado.value
}

onMounted(async () => {
  loading.value = true
  try {
    servicios.value = await serviciosDisponibles()
    // Como el mockup: el primer servicio aparece seleccionado con su detalle.
    marcado.value = servicios.value[0] ?? null
    detalle.value = marcado.value
  } catch (error) {
    errorMessage.value = apiMessage(error)
  } finally {
    cargado.value = true
    loading.value = false
  }
})
</script>

<template>
  <section class="module-page">
    <header class="module-heading">
      <h1>Servicios disponibles</h1>
      <p>Referencias: R10.2, R10.2.1–5</p>
    </header>
    <div class="form-panel catalog-panel" v-loading="loading">
      <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
      <div v-if="cargado && !errorMessage && servicios.length === 0" class="empty-appointments">
        No hay servicios disponibles
      </div>
      <template v-else-if="servicios.length">
        <div class="catalog-grid">
          <button
            v-for="servicio in servicios"
            :key="servicio.idServicio"
            type="button"
            class="catalog-card"
            :class="{ selected: marcado?.idServicio === servicio.idServicio }"
            @click="marcado = servicio"
          >
            <strong>{{ servicio.nombre }}</strong>
            <span class="catalog-text">{{ servicio.descripcion }}</span>
            <span class="catalog-price">{{ bolivianos(servicio.precio) }}</span>
          </button>
        </div>
        <div v-if="detalle" class="catalog-detail">
          <strong>{{ detalle.nombre }}</strong>
          <p>{{ detalle.descripcion }}</p>
        </div>
        <el-button type="primary" @click="verSeleccionado">Ver servicio seleccionado</el-button>
      </template>
    </div>
  </section>
</template>

<style scoped>
.catalog-panel {
  min-height: 420px;
}

.catalog-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.catalog-card {
  display: grid;
  gap: 8px;
  padding: 16px;
  border: 1px solid var(--hv-border);
  border-radius: 8px;
  color: var(--hv-text);
  background: #fff;
  font: inherit;
  text-align: left;
  cursor: pointer;
}

.catalog-card.selected {
  border-color: var(--hv-primary);
  box-shadow: 0 0 0 1px var(--hv-primary) inset;
}

.catalog-card strong {
  font-size: 18px;
}

.catalog-text {
  color: var(--hv-muted);
  font-size: 14px;
}

.catalog-price {
  color: var(--hv-primary);
  font-size: 18px;
  font-weight: 800;
}

.catalog-detail {
  margin: 22px 0 16px;
}

.catalog-detail strong {
  font-size: 18px;
}

.catalog-detail p {
  margin: 6px 0 0;
  color: var(--hv-text);
}

@media (max-width: 760px) {
  .catalog-grid {
    grid-template-columns: 1fr;
  }
}
</style>
