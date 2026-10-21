import { reactive } from 'vue'
import { http, prepareCsrf } from './http'

export const authState = reactive({
  user: null,
  checked: false,
})

export async function login(credentials) {
  const { data } = await http.post('/auth/login', credentials)
  authState.user = data
  authState.checked = true
  return data
}

export async function refreshSession() {
  try {
    const { data } = await http.get('/auth/me')
    authState.user = data
  } catch {
    authState.user = null
  } finally {
    authState.checked = true
  }
  return authState.user
}

export async function logout() {
  await prepareCsrf()
  await http.post('/auth/logout')
  authState.user = null
  authState.checked = true
}

