<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  actualizarStock,
  agregarProducto,
  eliminarProducto,
  listarProductos,
} from '../../api/inventario'
import { apiFieldErrors, apiMessage } from '../../api/http'

const mode = ref('list')
const loading = ref(false)
const saving = ref(false)
const productos = ref([])
const errorMessage = ref('')
const fieldErrors = ref({})

const filtros = reactive({ buscar: '', categoria: '' })
const addFormRef = ref()
const addForm = reactive({
  nombre: '', categoria: '', descripcion: '', precio: '', cantidadInicial: '', stockMinimo: '',
})
const selectedProduct = ref(null)
const stockSearch = ref('')
const nuevaCantidad = ref('')

const categories = computed(() => [...new Set(productos.value.map((producto) => producto.categoria))].sort())

const addRules = {
  nombre: [{ required: true, message: 'Complete los campos obligatorios', trigger: 'blur' }],
  categoria: [{ required: true, message: 'Complete los campos obligatorios', trigger: 'blur' }],
  precio: [
    { required: true, message: 'Complete los campos obligatorios', trigger: 'blur' },
    { validator: validatePositive, trigger: 'blur' },
  ],
  cantidadInicial: [
    { required: true, message: 'Complete los campos obligatorios', trigger: 'blur' },
    { validator: validatePositiveInteger, trigger: 'blur' },
  ],
  stockMinimo: [
    { required: true, message: 'Complete los campos obligatorios', trigger: 'blur' },
    { validator: validateNonNegativeInteger, trigger: 'blur' },
  ],
}

function validatePositive(_rule, value, callback) {
  if (!Number.isFinite(Number(value)) || Number(value) <= 0) callback(new Error('Precio o cantidad inválidos'))
  else callback()
}

function validatePositiveInteger(_rule, value, callback) {
  if (!Number.isInteger(Number(value)) || Number(value) <= 0) callback(new Error('Precio o cantidad inválidos'))
  else callback()
}

function validateNonNegativeInteger(_rule, value, callback) {
  if (!Number.isInteger(Number(value)) || Number(value) < 0) callback(new Error('Precio o cantidad inválidos'))
  else callback()
}

function resetMessages() {
  errorMessage.value = ''
  fieldErrors.value = {}
}

async function load() {
  loading.value = true
  resetMessages()
  try {
    productos.value = await listarProductos({
      buscar: filtros.buscar || undefined,
      categoria: filtros.categoria || undefined,
    })
  } catch (error) {
    errorMessage.value = apiMessage(error, 'No fue posible consultar el inventario')
  } finally {
    loading.value = false
  }
}

function openList() {
  mode.value = 'list'
  selectedProduct.value = null
  stockSearch.value = ''
  nuevaCantidad.value = ''
  resetMessages()
  load()
}

function openAdd() {
  Object.assign(addForm, {
    nombre: '', categoria: '', descripcion: '', precio: '', cantidadInicial: '', stockMinimo: '',
  })
  addFormRef.value?.clearValidate()
  resetMessages()
  mode.value = 'add'
}

async function submitAdd() {
  resetMessages()
  const validationErrors = await addFormRef.value.validate().then(() => null).catch((errors) => errors)
  if (validationErrors) {
    const invalidNumber = ['precio', 'cantidadInicial', 'stockMinimo'].some((field) =>
      validationErrors[field]?.some((error) => error.message === 'Precio o cantidad inválidos'),
    )
    errorMessage.value = invalidNumber ? 'Precio o cantidad inválidos' : 'Complete los campos obligatorios'
    return
  }

  saving.value = true
  try {
    await agregarProducto({
      ...addForm,
      precio: Number(addForm.precio),
      cantidadInicial: Number(addForm.cantidadInicial),
      stockMinimo: Number(addForm.stockMinimo),
    })
    ElMessage.success('Producto guardado en el inventario')
    openList()
  } catch (error) {
    errorMessage.value = apiMessage(error, 'No fue posible guardar el producto')
    fieldErrors.value = apiFieldErrors(error)
  } finally {
    saving.value = false
  }
}

function openStock(producto = null) {
  selectedProduct.value = producto
  stockSearch.value = producto ? `${producto.codigo} — ${producto.nombre}` : ''
  nuevaCantidad.value = producto?.stockActual ?? ''
  resetMessages()
  mode.value = 'stock'
}

function findStockProduct() {
  resetMessages()
  const criterion = stockSearch.value.trim().toLocaleLowerCase()
  selectedProduct.value = productos.value.find((producto) =>
    producto.codigo.toLocaleLowerCase() === criterion
    || producto.nombre.toLocaleLowerCase() === criterion
    || `${producto.codigo} — ${producto.nombre}`.toLocaleLowerCase() === criterion,
  ) || null

  if (!selectedProduct.value) {
    nuevaCantidad.value = ''
    errorMessage.value = 'Producto no encontrado'
    return
  }
  nuevaCantidad.value = selectedProduct.value.stockActual
}

async function submitStock() {
  resetMessages()
  if (!selectedProduct.value) {
    errorMessage.value = 'Producto no encontrado'
    return
  }
  if (!Number.isInteger(Number(nuevaCantidad.value)) || Number(nuevaCantidad.value) < 0) {
    errorMessage.value = 'Cantidad inválida'
    return
  }

  saving.value = true
  try {
    await actualizarStock(selectedProduct.value.idProducto, Number(nuevaCantidad.value))
    ElMessage.success('Existencia actualizada correctamente')
    openList()
  } catch (error) {
    errorMessage.value = apiMessage(error, 'No fue posible actualizar el stock')
    fieldErrors.value = apiFieldErrors(error)
  } finally {
    saving.value = false
  }
}

function openDelete(producto) {
  selectedProduct.value = producto
  resetMessages()
  mode.value = 'delete'
}

async function confirmDelete() {
  if (!selectedProduct.value) return
  saving.value = true
  resetMessages()
  try {
    await eliminarProducto(selectedProduct.value.idProducto)
    ElMessage.success('Producto eliminado del inventario')
    openList()
  } catch (error) {
    errorMessage.value = apiMessage(error, 'No fue posible eliminar el producto')
  } finally {
    saving.value = false
  }
}

function money(value) {
  return new Intl.NumberFormat('es-BO', { style: 'currency', currency: 'BOB' }).format(value)
}

function statusLabel(status) {
  return { DISPONIBLE: 'Disponible', STOCK_BAJO: 'Stock bajo', AGOTADO: 'Agotado' }[status] || status
}

onMounted(load)
</script>

<template>
  <section class="module-page inventory-page">
    <template v-if="mode === 'list'">
      <header class="module-heading">
        <h1>Consultar inventario</h1>
        <p>Referencias: R4.1, R4.1.1–3</p>
      </header>

      <div class="form-panel inventory-panel">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <div class="inventory-search">
          <el-form-item label="Buscar producto">
            <el-input v-model="filtros.buscar" placeholder="Nombre o código" @keyup.enter="load" />
          </el-form-item>
          <el-form-item label="Categoría">
            <el-select v-model="filtros.categoria" clearable placeholder="Todas" class="full-width">
              <el-option v-for="category in categories" :key="category" :label="category" :value="category" />
            </el-select>
          </el-form-item>
        </div>
        <div class="form-actions inventory-toolbar">
          <el-button type="primary" @click="load">Buscar</el-button>
          <el-button plain @click="openAdd">Agregar producto</el-button>
        </div>

        <el-table
          :data="productos"
          v-loading="loading"
          class="inventory-table"
          empty-text="No hay productos registrados"
        >
          <el-table-column label="Producto" min-width="240">
            <template #default="scope">
              <strong>{{ scope.row.nombre }}</strong>
              <small>{{ scope.row.codigo }}</small>
            </template>
          </el-table-column>
          <el-table-column prop="categoria" label="Categoría" min-width="145" />
          <el-table-column label="Precio" width="140">
            <template #default="scope">{{ money(scope.row.precio) }}</template>
          </el-table-column>
          <el-table-column prop="stockActual" label="Existencia" width="130" />
          <el-table-column label="Estado" width="135">
            <template #default="scope">
              <span class="stock-status" :class="scope.row.estado.toLowerCase()">
                {{ statusLabel(scope.row.estado) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="Acciones" width="215" align="right">
            <template #default="scope">
              <el-button link class="table-action" @click="openStock(scope.row)">Stock</el-button>
              <el-button link class="table-action danger" @click="openDelete(scope.row)">Eliminar</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </template>

    <template v-else-if="mode === 'add'">
      <header class="module-heading">
        <h1>Agregar producto</h1>
        <p>Referencias: R4.2, R4.2.1–6</p>
      </header>
      <div class="form-panel">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-position="top" @submit.prevent="submitAdd">
          <div class="form-columns">
            <el-form-item label="Nombre del producto" prop="nombre" :error="fieldErrors.nombre">
              <el-input v-model="addForm.nombre" maxlength="100" />
            </el-form-item>
            <el-form-item label="Categoría" prop="categoria" :error="fieldErrors.categoria">
              <el-input v-model="addForm.categoria" maxlength="60" />
            </el-form-item>
            <el-form-item label="Descripción" prop="descripcion" :error="fieldErrors.descripcion">
              <el-input v-model="addForm.descripcion" maxlength="255" />
            </el-form-item>
            <el-form-item label="Precio en Bs" prop="precio" :error="fieldErrors.precio">
              <el-input v-model="addForm.precio" type="number" min="0" step="0.01" />
            </el-form-item>
            <el-form-item label="Cantidad inicial" prop="cantidadInicial" :error="fieldErrors.cantidadInicial">
              <el-input v-model="addForm.cantidadInicial" type="number" min="0" step="1" />
            </el-form-item>
            <el-form-item label="Stock mínimo" prop="stockMinimo" :error="fieldErrors.stockMinimo">
              <el-input v-model="addForm.stockMinimo" type="number" min="0" step="1" />
            </el-form-item>
          </div>
          <div class="form-actions">
            <el-button type="primary" native-type="submit" :loading="saving">Guardar producto</el-button>
            <el-button plain @click="openList">Cancelar</el-button>
          </div>
        </el-form>
      </div>
    </template>

    <template v-else-if="mode === 'stock'">
      <header class="module-heading">
        <h1>Actualizar stock</h1>
        <p>Referencias: R4.4, R4.4.1–7</p>
      </header>
      <div class="form-panel">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <div class="form-columns">
          <el-form-item label="Buscar producto">
            <el-input v-model="stockSearch" placeholder="Código o nombre" @keyup.enter="findStockProduct" />
          </el-form-item>
          <el-form-item label="Existencia actual">
            <el-input :model-value="selectedProduct ? `${selectedProduct.stockActual} unidades` : '—'" readonly />
          </el-form-item>
          <el-form-item label="Nueva cantidad" :error="fieldErrors.nuevaCantidad">
            <el-input v-model="nuevaCantidad" type="number" min="0" step="1" :disabled="!selectedProduct" />
          </el-form-item>
        </div>
        <div class="form-actions">
          <el-button type="primary" @click="findStockProduct">Buscar producto</el-button>
          <el-button v-if="selectedProduct" plain :loading="saving" @click="submitStock">Guardar cambios</el-button>
          <el-button plain @click="openList">Cancelar</el-button>
        </div>
        <p class="form-help">Ingresa una cantidad numérica no negativa.</p>
      </div>
    </template>

    <template v-else>
      <header class="module-heading">
        <h1>Eliminar producto</h1>
        <p>Referencias: R4.3, R4.3.1–4</p>
      </header>
      <div class="form-panel delete-panel">
        <el-alert v-if="errorMessage" :title="errorMessage" type="error" :closable="false" />
        <h2>¿Eliminar este producto del inventario?</h2>
        <div class="form-columns">
          <el-form-item label="Producto seleccionado">
            <el-input :model-value="selectedProduct?.nombre" readonly />
          </el-form-item>
          <el-form-item label="Existencia actual">
            <el-input :model-value="`${selectedProduct?.stockActual ?? 0} unidades`" readonly />
          </el-form-item>
        </div>
        <div class="form-actions">
          <el-button type="primary" :loading="saving" @click="confirmDelete">Sí, eliminar producto</el-button>
          <el-button plain @click="openList">Cancelar</el-button>
        </div>
        <p class="form-help">Si cancelas, el producto se conserva y vuelves al listado de inventario.</p>
      </div>
    </template>
  </section>
</template>
