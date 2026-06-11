import { apiDelete, apiGet, apiPost, apiPut } from './client'

const BASE = '/api/provider-executions'

export async function executeProvider(body) {
  return apiPost(BASE, body, '실행 실패')
}

export async function previewProvider(body) {
  return apiPost(`${BASE}/preview`, body, '미리보기 실패')
}

export async function getExecutionHistory(organizationId) {
  const url = organizationId
    ? `${BASE}/history?organizationId=${organizationId}`
    : `${BASE}/history`
  return apiGet(url, '조회 실패')
}

export async function getExecutionPresets() {
  return apiGet('/api/provider-execution-presets', '프리셋 조회 실패')
}

export async function createExecutionPreset(body) {
  return apiPost('/api/provider-execution-presets', body, '프리셋 저장 실패')
}

export async function updateExecutionPreset(id, body) {
  return apiPut(`/api/provider-execution-presets/${id}`, body, '프리셋 수정 실패')
}

export async function deleteExecutionPreset(id) {
  return apiDelete(`/api/provider-execution-presets/${id}`, '프리셋 삭제 실패')
}
