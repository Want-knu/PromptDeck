import { apiFetch, apiPost, clearAccessToken } from './client'

const BASE = '/api/auth'

export async function signup(email, password, name) {
  return apiPost(`${BASE}/signup`, { email, password, name }, '회원가입에 실패했습니다.')
}

export async function login(email, password) {
  return apiPost(`${BASE}/login`, { email, password }, '로그인에 실패했습니다.')
}

export async function logout() {
  await apiFetch(`${BASE}/logout`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' }
  })
  clearAccessToken()
}
