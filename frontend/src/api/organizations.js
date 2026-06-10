import { apiGet, apiPost } from './client'

const BASE = '/api/organizations'

export async function getOrganizations() {
  return apiGet(BASE, '조회 실패')
}

export async function createOrganization(name) {
  return apiPost(BASE, { name }, '생성 실패')
}

export async function addOrganizationMember(organizationId, email) {
  await apiPost(`${BASE}/${organizationId}/members`, { email }, '멤버 추가 실패')
}
