import { apiFetch, authHeaders, readJsonResponse } from './client'

const BASE = '/api/provider-executions'

export async function executeProvider(body) {
  const res = await apiFetch(BASE, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(body)
  })
  const data = await readJsonResponse(res, '실행 실패')
  return data.data
}

export async function previewProvider(body) {
  const res = await apiFetch(`${BASE}/preview`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(body)
  })
  const data = await readJsonResponse(res, '미리보기 실패')
  return data.data
}

export async function getExecutionHistory(organizationId) {
  const url = organizationId
    ? `${BASE}/history?organizationId=${organizationId}`
    : `${BASE}/history`
  const res = await apiFetch(url, { headers: authHeaders() })
  const data = await readJsonResponse(res, '조회 실패')
  return data.data
}
