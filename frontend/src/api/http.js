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

export function apiFieldErrors(error) {
  return error.response?.data?.fieldErrors || {}
}

