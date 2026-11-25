import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import es from 'element-plus/es/locale/lang/es'
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

// Calendarios y textos de Element Plus en español (meses, días y botones).
createApp(App).use(ElementPlus, { locale: es }).use(router).mount('#app')

