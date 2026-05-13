const BASE = '/api/provider-settings'

function authHeaders() {
  const token = localStorage.getItem('token')
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {})
  }
}

export async function getProviderSettings() {
  const res = await fetch(BASE, { headers: authHeaders() })
  const data = await res.json()
  if (!res.ok) throw new Error(data.message || '조회 실패')
  return data.data
}

export async function getProviderSetting(id) {
  const res = await fetch(`${BASE}/${id}`, { headers: authHeaders() })
  const data = await res.json()
  if (!res.ok) throw new Error(data.message || '조회 실패')
  return data.data
}

export async function createProviderSetting(body) {
  const res = await fetch(BASE, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(body)
  })
  const data = await res.json()
  if (!res.ok) throw new Error(data.message || '생성 실패')
  return data.data
}

export async function updateProviderSetting(id, body) {
  const res = await fetch(`${BASE}/${id}`, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify(body)
  })
  const data = await res.json()
  if (!res.ok) throw new Error(data.message || '수정 실패')
  return data.data
}

export async function deleteProviderSetting(id) {
  const res = await fetch(`${BASE}/${id}`, {
    method: 'DELETE',
    headers: authHeaders()
  })
  if (!res.ok) {
    const data = await res.json()
    throw new Error(data.message || '삭제 실패')
  }
}
