import { useState, useCallback, useEffect, useMemo } from 'react'
import {
  CUSTOM_PROVIDER_DEFAULTS,
  resolveAuthTypes,
  resolveHttpMethods,
  resolveProviderModels,
  resolveProviderPresets,
  resolveProviderTypes
} from '../utils/providerOptionsResolver'
import { getProviderSettingOptions } from '../api/providerSettings'

export function useProviderOptions(initialProviderType = 'OPENAI') {
  const [providerType, setProviderType] = useState(initialProviderType)
  const [options, setOptions] = useState(null)
  const [optionsReady, setOptionsReady] = useState(false)

  useEffect(() => {
    let cancelled = false
    getProviderSettingOptions()
      .then(data => {
        if (!cancelled) setOptions(data)
      })
      .catch(() => {})
      .finally(() => {
        if (!cancelled) setOptionsReady(true)
      })
    return () => { cancelled = true }
  }, [])

  const providerTypes = useMemo(() => resolveProviderTypes(options), [options])
  const httpMethods = useMemo(() => resolveHttpMethods(options), [options])
  const authTypes = useMemo(() => resolveAuthTypes(options), [options])
  const providerModels = useMemo(() => resolveProviderModels(options), [options])
  const providerPresets = useMemo(() => resolveProviderPresets(options), [options])

  const getDefaultsFor = useCallback((type) => {
    if (type === 'CUSTOM') {
      return { providerType: type, ...CUSTOM_PROVIDER_DEFAULTS }
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
    optionsReady,
    providerTypes,
    httpMethods,
    authTypes,
    providerModels,
    providerPresets,
  }
}
