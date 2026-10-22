import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import { authState } from './api/auth'
import { configurarSesionVencida } from './api/http'
import './styles/main.css'

configurarSesionVencida(() => {
  if (!authState.user) return
  authState.user = null
  router.push({ name: 'login', query: { sesion: 'expirada' } })
})

createApp(App).use(ElementPlus).use(router).mount('#app')

