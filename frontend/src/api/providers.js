import { apiFetch, authHeaders, readJsonResponse } from './client'

const BASE = '/api/provider-keys'

export async function getProviderKeys() {
  const res = await apiFetch(BASE, { headers: authHeaders() })
  const data = await readJsonResponse(res, '조회 실패')
  return data.data
}

export async function createProviderKey(providerType, apiKey, displayName) {
  const res = await apiFetch(BASE, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ providerType, apiKey, displayName })
  })
  const data = await readJsonResponse(res, '등록 실패')
  return data.data
}

export async function deleteProviderKey(id) {
  const res = await apiFetch(`${BASE}/${id}`, {
    method: 'DELETE',
    headers: authHeaders()
  })
  if (!res.ok) {
    await readJsonResponse(res, '삭제 실패')
  }
}
