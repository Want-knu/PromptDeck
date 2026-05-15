import { apiFetch, authHeaders, readJsonResponse } from './client'

const BASE = '/api/organizations'

export async function getOrganizations() {
  const res = await apiFetch(BASE, { headers: authHeaders() })
  const data = await readJsonResponse(res, '조회 실패')
  return data.data
}

export async function createOrganization(name) {
  const res = await apiFetch(BASE, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ name })
  })
  const data = await readJsonResponse(res, '생성 실패')
  return data.data
}

export async function addOrganizationMember(organizationId, email) {
  const res = await apiFetch(`${BASE}/${organizationId}/members`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ email })
  })
  await readJsonResponse(res, '멤버 추가 실패')
}
