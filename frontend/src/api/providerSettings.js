import { apiDelete, apiGet, apiPost, apiPut } from './client'

const BASE = '/api/provider-settings'

export async function getProviderSettings() {
  return apiGet(BASE, '조회 실패')
}

export async function getProviderSettingOptions() {
  return apiGet(`${BASE}/options`, '지원 옵션 조회 실패')
}

export async function getProviderSetting(id) {
  return apiGet(`${BASE}/${id}`, '조회 실패')
}

export async function createProviderSetting(body) {
  return apiPost(BASE, body, '생성 실패')
}

export async function updateProviderSetting(id, body) {
  return apiPut(`${BASE}/${id}`, body, '수정 실패')
}

export async function deleteProviderSetting(id) {
  return apiDelete(`${BASE}/${id}`, '삭제 실패')
}
