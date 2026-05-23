import { apiDelete, apiGet, apiPost, apiPut } from './client'

const BASE = '/api/provider-keys'

export async function getProviderKeys() {
  return apiGet(BASE, '조회 실패')
}

export async function createProviderKey(providerType, apiKey, displayName) {
  return apiPost(BASE, { providerType, apiKey, displayName }, '등록 실패')
}

export async function updateProviderKey(id, body) {
  return apiPut(`${BASE}/${id}`, body, '수정 실패')
}

export async function deleteProviderKey(id) {
  return apiDelete(`${BASE}/${id}`, '삭제 실패')
}
