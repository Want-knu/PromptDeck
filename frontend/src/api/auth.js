import { apiFetch, clearAccessToken, readJsonResponse } from './client'

const BASE = '/api/auth'

export async function signup(email, password, name) {
  const res = await apiFetch(`${BASE}/signup`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password, name })
  })
  return readJsonResponse(res, '회원가입에 실패했습니다.')
}

export async function login(email, password) {
  const res = await apiFetch(`${BASE}/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  })
  return readJsonResponse(res, '로그인에 실패했습니다.')
}

export async function logout() {
  await apiFetch(`${BASE}/logout`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' }
  })
  clearAccessToken()
}
