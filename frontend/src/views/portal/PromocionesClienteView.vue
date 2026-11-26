<script setup>
// Interfaz Ver promociones (Cliente) (3.1.32, R10.3), tal cual el mockup. N-08 A (D-21).
import { onMounted, ref } from 'vue'
import { apiMessage } from '../../api/http'
import { promocionesVigentes } from '../../api/portal'

const promociones = ref([])
const cargado = ref(false)
const loading = ref(false)
const errorMessage = ref('')
const marcada = ref(null)
const detalle = ref(null)

function formatDate(value) {
  if (!value) return ''
  const [anio, mes, dia] = value.slice(0, 10).split('-')
  return `${dia}/${mes}/${anio}`
}

// R10.3.4 Seleccionar promoción; R10.3.2 ver su detalle.
function verDetalle() {
  detalle.value = marcada.value
}

onMounted(async () => {
  loading.value = true
  try {
    promociones.value = await promocionesVigentes()
    // Como el mockup: la primera promoción aparece seleccionada con su detalle.
    marcada.value = promociones.value[0] ?? null
    detalle.value = marcada.value
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
      <h1>Promociones vigentes</h1>
      <p>Referencias: R10.3, R10.3.1–5</p>
    </header>
    <div class="form-panel promo-panel" v-loading="loading">
      <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
      <div v-if="cargado && !errorMessage && promociones.length === 0" class="empty-appointments">
        No hay promociones vigentes
      </div>
      <template v-else-if="promociones.length">
        <div class="promo-list">
          <button
            v-for="promocion in promociones"
            :key="promocion.idPromocion"
            type="button"
            class="promo-card"
            :class="{ selected: marcada?.idPromocion === promocion.idPromocion }"
            @click="marcada = promocion"
          >
            <strong>{{ promocion.titulo }}</strong>
            <span class="promo-text">{{ promocion.descripcion }}</span>
            <span class="promo-dates">Vigencia: {{ formatDate(promocion.fechaInicio) }} al {{ formatDate(promocion.fechaFin) }}</span>
          </button>
        </div>
        <el-button type="primary" @click="verDetalle">Ver detalle de la promoción</el-button>
        <div v-if="detalle" class="promo-detail">
          <strong>Detalle de la promoción</strong>
          <p>{{ detalle.detalle }}</p>
        </div>
      </template>
    </div>
  </section>
</template>

<style scoped>
.promo-panel {
  min-height: 420px;
}

.promo-list {
  display: grid;
  gap: 14px;
  margin-bottom: 20px;
}

.promo-card {
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

.promo-card.selected {
  border-color: var(--hv-primary);
  box-shadow: 0 0 0 1px var(--hv-primary) inset;
}

.promo-card strong {
  font-size: 19px;
}

.promo-text {
  color: var(--hv-text);
  font-size: 15px;
}

.promo-dates {
  color: var(--hv-primary);
  font-size: 15px;
  font-weight: 800;
}

.promo-detail {
  margin-top: 22px;
}

.promo-detail strong {
  font-size: 18px;
}

.promo-detail p {
  margin: 6px 0 0;
}
</style>
