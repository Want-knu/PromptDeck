const BASE = '/api/provider-keys'

function authHeaders() {
  const token = localStorage.getItem('token')
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {})
  }
}

export async function getProviderKeys() {
  const res = await fetch(BASE, { headers: authHeaders() })
  const data = await res.json()
  if (!res.ok) throw new Error(data.message || '조회 실패')
  return data.data
}

export async function createProviderKey(providerType, apiKey, displayName) {
  const res = await fetch(BASE, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify({ providerType, apiKey, displayName })
  })
  const data = await res.json()
  if (!res.ok) throw new Error(data.message || '등록 실패')
  return data.data
}

export async function deleteProviderKey(id) {
  const res = await fetch(`${BASE}/${id}`, {
    method: 'DELETE',
    headers: authHeaders()
  })
  if (!res.ok) {
    const data = await res.json()
    throw new Error(data.message || '삭제 실패')
  }
}
