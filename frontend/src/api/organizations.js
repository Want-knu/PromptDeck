const BASE = '/api/organizations'

function authHeaders() {
  const token = localStorage.getItem('token')
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {})
  }
}

export async function getOrganizations() {
  const res = await fetch(BASE, { headers: authHeaders() })
  const data = await res.json()
  if (!res.ok) throw new Error(data.message || '조회 실패')
  return data.data
}

export async function createOrganization(name) {
  const res = await fetch(BASE, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ name })
  })
  const data = await res.json()
  if (!res.ok) throw new Error(data.message || '생성 실패')
  return data.data
}

export async function addOrganizationMember(organizationId, email) {
  const res = await fetch(`${BASE}/${organizationId}/members`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ email })
  })
  const data = await res.json()
  if (!res.ok) throw new Error(data.message || '멤버 추가 실패')
}
