export function createProviderSettingForm(providerType, providerPresets, providerModels) {
  const preset = providerPresets[providerType] ?? {}
  const models = providerModels[providerType] ?? []

  return {
    providerType,
    organizationId: '',
    displayName: '',
    model: models[0] ?? '',
    ...preset,
    headersJson: '',
    queryParamsJson: '',
    optionSchemaJson: '',
  }
}

export function createProviderSettingEditForm(setting) {
  return {
    providerType: setting.providerType,
    organizationId: setting.organizationId ? String(setting.organizationId) : '',
    displayName: setting.displayName,
    model: setting.model,
    endpoint: setting.endpoint,
    method: setting.method ?? 'POST',
    authType: setting.authType ?? 'BEARER',
    authHeaderName: setting.authHeaderName ?? '',
    authQueryParamName: setting.authQueryParamName ?? '',
    headersJson: setting.headersJson ?? '',
    queryParamsJson: setting.queryParamsJson ?? '',
    bodyTemplateJson: setting.bodyTemplateJson ?? '',
    optionSchemaJson: setting.optionSchemaJson ?? '',
    responsePath: setting.responsePath ?? ''
  }
}

export function buildProviderSettingPayload(form) {
  const isCustomProvider = form.providerType === 'CUSTOM'

  return {
    ...form,
    organizationId: form.organizationId ? Number(form.organizationId) : undefined,
    headersJson: isCustomProvider ? form.headersJson : undefined,
    queryParamsJson: isCustomProvider ? form.queryParamsJson : undefined,
    bodyTemplateJson: isCustomProvider ? form.bodyTemplateJson : undefined,
    authHeaderName: isCustomProvider && form.authType === 'HEADER' ? form.authHeaderName : undefined,
    authQueryParamName: isCustomProvider && form.authType === 'QUERY_PARAM' ? form.authQueryParamName : undefined,
    optionSchemaJson: isCustomProvider ? form.optionSchemaJson : undefined
  }
}
