import {
  AUTH_TYPES as FALLBACK_AUTH_TYPES,
  HTTP_METHODS as FALLBACK_METHODS,
  PROVIDER_MODELS as FALLBACK_MODELS,
  PROVIDER_PRESETS as FALLBACK_PRESETS,
  PROVIDER_TYPES as FALLBACK_TYPES,
} from '../constants/providerOptions'

export const CUSTOM_PROVIDER_DEFAULTS = {
  model: '',
  endpoint: '',
  method: 'POST',
  authType: 'BEARER',
  authHeaderName: '',
  authQueryParamName: '',
  responsePath: '',
  bodyTemplateJson: '',
}

export function resolveProviderTypes(options) {
  return options?.providerTypes?.length ? options.providerTypes : FALLBACK_TYPES
}

export function resolveHttpMethods(options) {
  return collectProviderValues(options, 'methods', FALLBACK_METHODS)
}

export function resolveAuthTypes(options) {
  return collectProviderValues(options, 'authTypes', FALLBACK_AUTH_TYPES)
}

export function resolveProviderModels(options) {
  if (!options?.providers) {
    return FALLBACK_MODELS
  }

  const map = {}
  for (const [type, provider] of Object.entries(options.providers)) {
    if (provider.models?.length) {
      map[type] = provider.models
    }
  }

  return Object.keys(map).length ? map : FALLBACK_MODELS
}

export function resolveProviderPresets(options) {
  if (!options?.providers) {
    return FALLBACK_PRESETS
  }

  const map = {}
  for (const [type, provider] of Object.entries(options.providers)) {
    if (provider.custom) {
      continue
    }

    map[type] = {
      endpoint: provider.defaultEndpoint ?? '',
      method: provider.defaultMethod ?? 'POST',
      authType: provider.defaultAuthType ?? 'BEARER',
      authHeaderName: provider.defaultAuthHeaderName ?? '',
      authQueryParamName: provider.defaultAuthQueryParamName ?? '',
      responsePath: provider.defaultResponsePath ?? '',
      bodyTemplateJson: provider.bodyTemplates?.[0]?.value ?? '',
    }
  }

  return Object.keys(map).length ? map : FALLBACK_PRESETS
}

function collectProviderValues(options, key, fallback) {
  if (!options?.providers) {
    return fallback
  }

  const values = new Set()
  Object.values(options.providers).forEach(provider => {
    provider[key]?.forEach(value => values.add(value))
  })

  return values.size ? [...values] : fallback
}
