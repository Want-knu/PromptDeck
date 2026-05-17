import { useState, useCallback } from 'react'
import {
  PROVIDER_MODELS,
  PROVIDER_PRESETS,
} from '../constants/providerOptions'

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
 */
export function useProviderOptions(initialProviderType = 'OPENAI') {
  const [providerType, setProviderType] = useState(initialProviderType)

  const getDefaultsFor = useCallback((type) => {
    if (type === 'CUSTOM') {
      return { providerType: type, ...CUSTOM_DEFAULTS }
    }
    const preset = PROVIDER_PRESETS[type] ?? {}
    const models = PROVIDER_MODELS[type] ?? []
    return {
      providerType: type,
      model: models[0] ?? '',
      ...preset,
    }
  }, [])

  const changeProviderType = useCallback((newType, setFormFn) => {
    setProviderType(newType)
    const defaults = getDefaultsFor(newType)
    setFormFn(prev => ({
      ...prev,
      ...defaults,
      // auth 관련 필드는 프리셋 기준으로 덮어쓰되, undefined는 빈 문자열로
      authHeaderName: defaults.authHeaderName ?? '',
      authQueryParamName: defaults.authQueryParamName ?? '',
    }))
  }, [getDefaultsFor])

  return {
    providerType,
    setProviderType,
    changeProviderType,
    getDefaultsFor,
  }
}
