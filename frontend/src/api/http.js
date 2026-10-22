import axios from 'axios'

export const http = axios.create({
  baseURL: '/api',
  withCredentials: true,
  withXSRFToken: true,
  xsrfCookieName: 'XSRF-TOKEN',
  xsrfHeaderName: 'X-XSRF-TOKEN',
  headers: {
    'Content-Type': 'application/json',
  },
})

export async function prepareCsrf() {
  await http.get('/auth/csrf')
}

export function apiMessage(error, fallback = 'No fue posible completar la operación') {
  return error.response?.data?.message || fallback
}

// Ante una respuesta 401 fuera del inicio de sesión, la sesión del servidor ya no existe.
export function configurarSesionVencida(alVencer) {
  http.interceptors.response.use(
    (response) => response,
    (error) => {
      const url = error.config?.url || ''
      const esConsultaDeSesion = url.startsWith('/auth/login') || url.startsWith('/auth/me')
      if (error.response?.status === 401 && !esConsultaDeSesion) alVencer()
      return Promise.reject(error)
    },
  )
}

export function apiFieldErrors(error) {
  return error.response?.data?.fieldErrors || {}
}

