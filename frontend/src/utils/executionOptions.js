export function resolveOptionSchemaJson(setting, settingOptions) {
  if (!setting) {
    return ''
  }

  if (setting.providerType === 'CUSTOM' && setting.optionSchemaJson) {
    return setting.optionSchemaJson
  }

  const providerOption = settingOptions?.providers?.[setting.providerType]
  return providerOption?.modelOptions?.[setting.model]?.optionSchemaJson
    ?? providerOption?.optionSchemas?.[0]?.value
    ?? ''
}

export function parseOptionSchema(schemaJson) {
  if (!schemaJson) {
    return {}
  }

  try {
    const parsed = JSON.parse(schemaJson)
    return parsed && typeof parsed === 'object' && !Array.isArray(parsed) ? parsed : {}
  } catch {
    return {}
  }
}

export function defaultOptionValues(schema) {
  return Object.fromEntries(
    Object.entries(schema).map(([name, config]) => [
      name,
      Object.prototype.hasOwnProperty.call(config, 'default') ? String(config.default) : ''
    ])
  )
}

export function stringifyOptionValues(values) {
  return Object.fromEntries(
    Object.entries(values).map(([name, value]) => {
      if (value == null) {
        return [name, '']
      }

      if (typeof value === 'object') {
        return [name, JSON.stringify(value, null, 2)]
      }

      return [name, String(value)]
    })
  )
}

export function castOptionValues(schema, values) {
  const result = {}

  Object.entries(schema).forEach(([name, config]) => {
    const raw = values[name]
    if (raw === '' || raw == null) {
      return
    }

    if (config.type === 'number') {
      const value = Number(raw)
      if (Number.isNaN(value)) {
        throw new Error(`${name} 옵션은 숫자여야 합니다.`)
      }
      validateRange(name, config, value)
      result[name] = value
      return
    }

    if (config.type === 'integer') {
      const value = Number(raw)
      if (!Number.isInteger(value)) {
        throw new Error(`${name} 옵션은 정수여야 합니다.`)
      }
      validateRange(name, config, value)
      result[name] = value
      return
    }

    if (config.type === 'boolean') {
      result[name] = raw === true || raw === 'true'
      return
    }

    if (config.type === 'array' || config.type === 'object') {
      result[name] = typeof raw === 'string' ? JSON.parse(raw) : raw
      return
    }

    result[name] = raw
  })

  return result
}

export function buildExecutionPayload({ form, optionSchema, optionValues, requiresProviderKey }) {
  const variables = parseVariablesJson(form.variables)
  const options = castOptionValues(optionSchema, optionValues)

  return {
    providerKeyId: requiresProviderKey && form.providerKeyId ? Number(form.providerKeyId) : undefined,
    providerSettingId: Number(form.providerSettingId),
    prompt: form.prompt || undefined,
    instructions: form.instructions || undefined,
    options: Object.keys(options).length ? options : undefined,
    variables: variables || undefined
  }
}

export function formatJsonString(value) {
  try {
    return JSON.stringify(JSON.parse(value), null, 2)
  } catch {
    return value
  }
}

function parseVariablesJson(value) {
  if (!value.trim()) {
    return undefined
  }

  try {
    return JSON.parse(value)
  } catch {
    throw new Error('변수 JSON 형식이 올바르지 않습니다.')
  }
}

function validateRange(name, config, value) {
  if (config.minimum != null && value < Number(config.minimum)) {
    throw new Error(`${name} 옵션은 ${config.minimum} 이상이어야 합니다.`)
  }

  if (config.maximum != null && value > Number(config.maximum)) {
    throw new Error(`${name} 옵션은 ${config.maximum} 이하여야 합니다.`)
  }
}
