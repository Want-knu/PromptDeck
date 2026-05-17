import { useState, useCallback, useEffect, useMemo } from 'react'
import {
  PROVIDER_MODELS as FALLBACK_MODELS,
  PROVIDER_PRESETS as FALLBACK_PRESETS,
  PROVIDER_TYPES as FALLBACK_TYPES,
  HTTP_METHODS as FALLBACK_METHODS,
  AUTH_TYPES as FALLBACK_AUTH_TYPES,
} from '../constants/providerOptions'
import { getProviderSettingOptions } from '../api/providerSettings'

const CUSTOM_DEFAULTS = {
  model: '',
  endpoint: '',
  method: 'POST',
  authType: 'BEARER',
  authHeaderName: '',
  authQueryParamName: '',
  responsePath: '',
  bodyTemplateJson: '',
}

/**
 * providerType 변경 시 관련 필드를 프리셋 기반으로 초기화하는 훅
 * 백엔드 GET /api/provider-settings/options 응답을 우선 사용, 실패 시 mock fallback
 */
export function useProviderOptions(initialProviderType = 'OPENAI') {
  const [providerType, setProviderType] = useState(initialProviderType)
  const [options, setOptions] = useState(null)       // API 응답 원본
  const [optionsReady, setOptionsReady] = useState(false)

  // 마운트 시 옵션 API 호출 (1회)
  useEffect(() => {
    let cancelled = false
    getProviderSettingOptions()
      .then(data => {
        if (!cancelled) setOptions(data)
      })
      .catch(() => {
        // API 실패 → options null 유지, fallback 사용
      })
      .finally(() => {
        if (!cancelled) setOptionsReady(true)
      })
    return () => { cancelled = true }
  }, [])

  // ── resolved values: API 우선, 없으면 mock fallback ──

  const providerTypes = useMemo(() => {
    if (options?.providerTypes?.length) return options.providerTypes
    return FALLBACK_TYPES
  }, [options])

  const httpMethods = useMemo(() => {
    if (options?.providers) {
      const set = new Set()
      Object.values(options.providers).forEach(p => p.methods?.forEach(m => set.add(m)))
      if (set.size) return [...set]
    }
    return FALLBACK_METHODS
  }, [options])

  const authTypes = useMemo(() => {
    if (options?.providers) {
      const set = new Set()
      Object.values(options.providers).forEach(p => p.authTypes?.forEach(a => set.add(a)))
      if (set.size) return [...set]
    }
    return FALLBACK_AUTH_TYPES
  }, [options])

  /** { OPENAI: ['gpt-4o', ...], ... } 형식 */
  const providerModels = useMemo(() => {
    if (!options?.providers) return FALLBACK_MODELS
    const map = {}
    for (const [type, opt] of Object.entries(options.providers)) {
      if (opt.models?.length) map[type] = opt.models
    }
    return Object.keys(map).length ? map : FALLBACK_MODELS
  }, [options])

  /** { OPENAI: { endpoint, method, authType, ... }, ... } 형식 */
  const providerPresets = useMemo(() => {
    if (!options?.providers) return FALLBACK_PRESETS
    const map = {}
    for (const [type, opt] of Object.entries(options.providers)) {
      if (opt.custom) continue // CUSTOM은 프리셋 제외
      map[type] = {
        endpoint: opt.defaultEndpoint ?? '',
        method: opt.defaultMethod ?? 'POST',
        authType: opt.defaultAuthType ?? 'BEARER',
        authHeaderName: opt.defaultAuthHeaderName ?? '',
        authQueryParamName: opt.defaultAuthQueryParamName ?? '',
        responsePath: opt.defaultResponsePath ?? '',
        bodyTemplateJson: opt.bodyTemplates?.[0]?.value ?? '',
      }
    }
    return Object.keys(map).length ? map : FALLBACK_PRESETS
  }, [options])

  // ── helpers ──

  const getDefaultsFor = useCallback((type) => {
    if (type === 'CUSTOM') {
      return { providerType: type, ...CUSTOM_DEFAULTS }
    }
    const preset = providerPresets[type] ?? {}
    const models = providerModels[type] ?? []
    return {
      providerType: type,
      model: models[0] ?? '',
      ...preset,
    }
  }, [providerModels, providerPresets])

  const changeProviderType = useCallback((newType, setFormFn) => {
    setProviderType(newType)
    const defaults = getDefaultsFor(newType)
    setFormFn(prev => ({
      ...prev,
      ...defaults,
      authHeaderName: defaults.authHeaderName ?? '',
      authQueryParamName: defaults.authQueryParamName ?? '',
    }))
  }, [getDefaultsFor])

  return {
    providerType,
    setProviderType,
    changeProviderType,
    getDefaultsFor,
    // options data (API 우선, fallback 포함)
    optionsReady,
    providerTypes,
    httpMethods,
    authTypes,
    providerModels,
    providerPresets,
  }
}
