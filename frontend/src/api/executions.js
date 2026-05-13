const BASE = '/api/provider-executions'

function authHeaders() {
  const token = localStorage.getItem('token')
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {})
  }
}

export async function executeProvider(body) {
  const res = await fetch(BASE, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(body)
  })
  const data = await res.json()
  if (!res.ok) throw new Error(data.message || '실행 실패')
  return data.data
}

export async function previewProvider(body) {
  const res = await fetch(`${BASE}/preview`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(body)
  })
  const data = await res.json()
  if (!res.ok) throw new Error(data.message || '미리보기 실패')
  return data.data
}

export async function getExecutionHistory(organizationId) {
  const url = organizationId
    ? `${BASE}/history?organizationId=${organizationId}`
    : `${BASE}/history`
  const res = await fetch(url, { headers: authHeaders() })
  const data = await res.json()
  if (!res.ok) throw new Error(data.message || '조회 실패')
  return data.data
}
