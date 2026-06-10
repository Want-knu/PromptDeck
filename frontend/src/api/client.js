let accessToken = null
let refreshPromise = null
const authListeners = new Set()

const USER_EMAIL_KEY = 'pd-user-email'

export function getUserEmail() {
  return localStorage.getItem(USER_EMAIL_KEY)
}

export function setUserEmail(email) {
  if (email) localStorage.setItem(USER_EMAIL_KEY, email)
  else localStorage.removeItem(USER_EMAIL_KEY)
}

const AUTH_URLS = new Set([
  '/api/auth/login',
  '/api/auth/signup',
  '/api/auth/logout',
  '/api/auth/refresh'
])

export function getAccessToken() {
  return accessToken
}

export function setAccessToken(token) {
  accessToken = token || null
  notifyAuthListeners()
}

export function clearAccessToken() {
  accessToken = null
  setUserEmail(null)
  notifyAuthListeners()
}

export function subscribeAuth(listener) {
  authListeners.add(listener)
  return () => authListeners.delete(listener)
}

export function authHeaders(extra = {}) {
  return {
    ...extra
  }
}

export async function apiFetch(url, options = {}) {
  const res = await fetchWithDefaults(url, options)

  if (res.status !== 401 || isAuthUrl(url)) {
    return res
  }

  const refreshed = await refreshAccessToken()
  if (!refreshed) {
    clearAccessToken()
    return res
  }

  return fetchWithDefaults(url, options)
}

export async function readJsonResponse(res, fallbackMessage) {
  const data = await parseJson(res)
  if (!res.ok) {
    throw new Error(data?.message || fallbackMessage)
  }
  return data
}

export async function apiGet(url, fallbackMessage = '조회 실패') {
  const res = await apiFetch(url, { headers: authHeaders() })
  const data = await readJsonResponse(res, fallbackMessage)
  return data.data
}

export async function apiPost(url, body, fallbackMessage = '요청 실패') {
  const res = await apiFetch(url, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(body)
  })
  const data = await readJsonResponse(res, fallbackMessage)
  return data.data
}

export async function apiPut(url, body, fallbackMessage = '수정 실패') {
  const res = await apiFetch(url, {
    method: 'PUT',
    headers: authHeaders(),
    body: JSON.stringify(body)
  })
  const data = await readJsonResponse(res, fallbackMessage)
  return data.data
}

export async function apiDelete(url, fallbackMessage = '삭제 실패') {
  const res = await apiFetch(url, {
    method: 'DELETE',
    headers: authHeaders()
  })
  if (!res.ok) {
    await readJsonResponse(res, fallbackMessage)
  }
}

export async function refreshAccessToken() {
  if (!refreshPromise) {
    refreshPromise = requestAccessTokenRefresh()
  }

  try {
    return await refreshPromise
  } finally {
    refreshPromise = null
  }
}

async function requestAccessTokenRefresh() {
  let res

  try {
    res = await fetchWithDefaults('/api/auth/refresh', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' }
    })
  } catch {
    clearAccessToken()
    return false
  }

  if (!res.ok) {
    clearAccessToken()
    return false
  }

  const data = await parseJson(res)
  const nextAccessToken = data?.data?.accessToken

  if (!nextAccessToken) {
    clearAccessToken()
    return false
  }

  setAccessToken(nextAccessToken)
  return true
}

function fetchWithDefaults(url, options = {}) {
  const headers = new Headers(options.headers || {})

  if (options.body && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json')
  }

  if (accessToken && !isAuthUrl(url)) {
    headers.set('Authorization', `Bearer ${accessToken}`)
  } else {
    headers.delete('Authorization')
  }

  return fetch(url, {
    ...options,
    headers,
    credentials: 'include'
  })
}

async function parseJson(res) {
  const text = await res.text()
  if (!text) return null

  try {
    return JSON.parse(text)
  } catch {
    return { message: text }
  }
}

function isAuthUrl(url) {
  return typeof url === 'string' && AUTH_URLS.has(url)
}

function notifyAuthListeners() {
  authListeners.forEach(listener => listener(Boolean(accessToken)))
}
