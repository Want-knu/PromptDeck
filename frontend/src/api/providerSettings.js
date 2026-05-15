import { apiFetch, authHeaders, readJsonResponse } from './client'

const BASE = '/api/provider-settings'

export async function getProviderSettings() {
  const res = await apiFetch(BASE, { headers: authHeaders() })
  const data = await readJsonResponse(res, '조회 실패')
  return data.data
}

export async function getProviderSettingOptions() {
  const res = await apiFetch(`${BASE}/options`, { headers: authHeaders() })
  const data = await readJsonResponse(res, '지원 옵션 조회 실패')
  return data.data
}

export async function getProviderSetting(id) {
  const res = await apiFetch(`${BASE}/${id}`, { headers: authHeaders() })
  const data = await readJsonResponse(res, '조회 실패')
  return data.data
}

export async function createProviderSetting(body) {
  const res = await apiFetch(BASE, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(body)
  })
  const data = await readJsonResponse(res, '생성 실패')
  return data.data
}

export async function updateProviderSetting(id, body) {
  const res = await apiFetch(`${BASE}/${id}`, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify(body)
  })
  const data = await readJsonResponse(res, '수정 실패')
  return data.data
}

export async function deleteProviderSetting(id) {
  const res = await apiFetch(`${BASE}/${id}`, {
    method: 'DELETE',
    headers: authHeaders()
  })
  if (!res.ok) {
    await readJsonResponse(res, '삭제 실패')
  }
}
