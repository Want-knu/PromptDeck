import { useState, useEffect, useMemo } from 'react'
import Navbar from '../components/Navbar'
import { PageHeader, Card, Button, Badge, LoadingSpinner } from '../components/ui'
import { getProviderKeys } from '../api/providers'
import { getProviderSettings, getProviderSettingOptions } from '../api/providerSettings'
import { executeProvider, previewProvider } from '../api/executions'

export default function ExecutionPage() {
  const [keys, setKeys] = useState([])
  const [settings, setSettings] = useState([])
  const [settingOptions, setSettingOptions] = useState(null)
  const [loadingDeps, setLoadingDeps] = useState(true)

  const [form, setForm] = useState({
    providerKeyId: '',
    providerSettingId: '',
    prompt: '',
    instructions: '',
    variables: ''
  })
  const [optionValues, setOptionValues] = useState({})

  const [preview, setPreview] = useState(null)
  const [result, setResult] = useState(null)
  const [previewing, setPreviewing] = useState(false)
  const [executing, setExecuting] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    Promise.all([getProviderKeys(), getProviderSettings(), getProviderSettingOptions()])
      .then(([k, ps, options]) => {
        setKeys(k ?? [])
        setSettings(ps ?? [])
        setSettingOptions(options)
        if (ps?.length) setForm(p => ({ ...p, providerSettingId: String(ps[0].id) }))
      })
      .catch(err => setError(err.message))
      .finally(() => setLoadingDeps(false))
  }, [])

  const selectedSetting = settings.find(s => String(s.id) === form.providerSettingId)
  const requiresProviderKey = selectedSetting?.authType !== 'NONE'
  const filteredKeys = useMemo(() => (
    selectedSetting
      ? keys.filter(k => k.providerType === selectedSetting.providerType)
      : []
  ), [keys, selectedSetting])
  const optionSchema = parseOptionSchema(resolveOptionSchemaJson(selectedSetting, settingOptions))

  useEffect(() => {
    if (!selectedSetting) {
      if (form.providerKeyId) {
        setForm(p => ({ ...p, providerKeyId: '' }))
      }
      return
    }

    if (!requiresProviderKey) {
      if (form.providerKeyId) {
        setForm(p => ({ ...p, providerKeyId: '' }))
      }
      return
    }

    const selectedKeyMatches = filteredKeys.some(k => String(k.id) === form.providerKeyId)
    if (!selectedKeyMatches) {
      setForm(p => ({ ...p, providerKeyId: filteredKeys[0] ? String(filteredKeys[0].id) : '' }))
    }
  }, [selectedSetting, requiresProviderKey, filteredKeys, form.providerKeyId])

  useEffect(() => {
    setOptionValues(defaultOptionValues(optionSchema))
  }, [form.providerSettingId, selectedSetting?.model, selectedSetting?.optionSchemaJson, settingOptions])

  function buildPayload() {
    let variables
    if (form.variables.trim()) {
      try {
        variables = JSON.parse(form.variables)
      } catch {
        throw new Error('변수 JSON 형식이 올바르지 않습니다.')
      }
    }
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

  async function handlePreview() {
    setError('')
    setPreview(null)
    setResult(null)
    if (!form.prompt.trim()) {
      setError('프롬프트를 입력하세요.')
      return
    }
    setPreviewing(true)
    try {
      const payload = buildPayload()
      const data = await previewProvider(payload)
      setPreview(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setPreviewing(false)
    }
  }

  async function handleExecute() {
    setError('')
    setResult(null)
    setPreview(null)
    if (!form.prompt.trim()) {
      setError('프롬프트를 입력하세요.')
      return
    }
    setExecuting(true)
    try {
      const payload = buildPayload()
      const data = await executeProvider(payload)
      setResult(data)
    } catch (err) {
      setError(err.message)
    } finally {
      setExecuting(false)
    }
  }

  return (
    <>
      <Navbar />
      <main style={st.main}>
        <PageHeader title="요청 실행" />

        {loadingDeps ? (
          <LoadingSpinner />
        ) : (
          <div style={st.layout}>
            <Card style={st.panel}>
              <h3 style={st.subheading}>설정 선택</h3>

              <label style={st.label}>Provider 설정</label>
              {settings.length === 0 ? (
                <p style={st.warn}>등록된 Provider 설정이 없습니다. Provider 설정 관리에서 먼저 추가하세요.</p>
              ) : (
                <select style={st.select} value={form.providerSettingId} onChange={e => setForm(p => ({ ...p, providerSettingId: e.target.value }))}>
                  {settings.map(s => (
                    <option key={s.id} value={s.id}>{s.displayName} ({s.providerType} · {s.model})</option>
                  ))}
                </select>
              )}

              {selectedSetting && (
                <div style={st.settingInfo}>
                  <p style={st.settingInfoRow}><strong>엔드포인트:</strong> {selectedSetting.endpoint}</p>
                  <p style={st.settingInfoRow}><strong>메서드:</strong> {selectedSetting.method} · <strong>인증:</strong> {selectedSetting.authType}</p>
                  {selectedSetting.responsePath && (
                    <p style={st.settingInfoRow}><strong>응답 경로:</strong> {selectedSetting.responsePath}</p>
                  )}
                </div>
              )}

              <label style={st.label}>API Key</label>
              {!selectedSetting ? (
                <p style={st.info}>Provider 설정을 먼저 선택하세요.</p>
              ) : !requiresProviderKey ? (
                <p style={st.info}>선택한 설정은 인증이 없어 API Key 없이 실행됩니다.</p>
              ) : keys.length === 0 ? (
                <p style={st.warn}>등록된 API Key가 없습니다. Provider Key 관리에서 먼저 추가하세요.</p>
              ) : filteredKeys.length === 0 ? (
                <p style={st.warn}>{selectedSetting?.providerType} 의 API Key가 없습니다. {selectedSetting?.providerType}의 Key를 먼저 추가하세요.</p>
              ) : (
                <select style={st.select} value={form.providerKeyId} onChange={e => setForm(p => ({ ...p, providerKeyId: e.target.value }))}>
                  {filteredKeys.map(k => (
                    <option key={k.id} value={k.id}>{k.displayName} ({k.providerType})</option>
                  ))}
                </select>
              )}

              <label style={st.label}>프롬프트</label>
              <textarea
                style={st.textarea}
                rows={6}
                placeholder="LLM에 전달할 프롬프트를 입력하세요..."
                value={form.prompt}
                onChange={e => setForm(p => ({ ...p, prompt: e.target.value }))}
                required
              />

              <label style={st.label}>지시문</label>
              <textarea
                style={st.textarea}
                rows={3}
                placeholder="응답 스타일이나 제약 조건을 입력하세요."
                value={form.instructions}
                onChange={e => setForm(p => ({ ...p, instructions: e.target.value }))}
              />

              {Object.keys(optionSchema).length > 0 && (
                <div style={st.optionSection}>
                  <h3 style={st.optionTitle}>실행 옵션</h3>
                  <div style={st.optionGrid}>
                    {Object.entries(optionSchema).map(([name, schema]) => (
                      <OptionField
                        key={name}
                        name={name}
                        schema={schema}
                        value={optionValues[name]}
                        onChange={value => setOptionValues(prev => ({ ...prev, [name]: value }))}
                      />
                    ))}
                  </div>
                </div>
              )}

              <label style={st.label}>
                추가 변수 (JSON)
                <span style={st.hint}> — CUSTOM 템플릿이나 추가 치환값이 필요할 때만 사용</span>
              </label>
              <textarea
                style={st.textarea}
                rows={3}
                placeholder={'{"custom_variable": "value"}'}
                value={form.variables}
                onChange={e => setForm(p => ({ ...p, variables: e.target.value }))}
              />

              {error && <p style={st.error}>{error}</p>}

              <div style={st.btnRow}>
                <Button
                  variant="success"
                  onClick={handlePreview}
                  disabled={previewing || executing || !form.prompt.trim() || !form.providerSettingId || (requiresProviderKey && !form.providerKeyId)}
                  loading={previewing}
                  style={{ flex: 1 }}
                >
                  미리보기
                </Button>
                <Button
                  onClick={handleExecute}
                  disabled={previewing || executing || !form.prompt.trim() || !form.providerSettingId || (requiresProviderKey && !form.providerKeyId)}
                  loading={executing}
                  style={{ flex: 1 }}
                >
                  실행
                </Button>
              </div>
            </Card>

            <Card style={st.panel}>
              <h3 style={st.subheading}>결과</h3>

              {!preview && !result && (
                <p style={st.info}>미리보기 또는 실행 결과가 여기에 표시됩니다.</p>
              )}

              {preview && (
                <div>
                  <p style={st.resultLabel}>요청 미리보기</p>
                  <div style={st.resultMeta}>
                    <Badge variant="info">{preview.method}</Badge>
                    <code style={st.endpoint}>{preview.endpoint}</code>
                  </div>
                  {preview.headers && Object.keys(preview.headers).length > 0 && (
                    <>
                      <p style={st.sectionLabel}>헤더</p>
                      <pre style={st.pre}>{JSON.stringify(preview.headers, null, 2)}</pre>
                    </>
                  )}
                  {preview.body && (
                    <>
                      <p style={st.sectionLabel}>바디</p>
                      <pre style={st.pre}>{JSON.stringify(preview.body, null, 2)}</pre>
                    </>
                  )}
                </div>
              )}

              {result && (
                <div>
                  <div style={st.resultHeader}>
                    <p style={st.resultLabel}>실행 결과</p>
                    <Badge variant={result.success ? 'success' : 'error'}>
                      {result.statusCode} {result.success ? '성공' : '실패'}
                    </Badge>
                  </div>
                  <p style={st.sectionLabel}>{result.providerType} · {result.model}</p>

                  {result.parsedResponse && (
                    <>
                      <p style={st.sectionLabel}>파싱된 응답</p>
                      <div style={st.parsedBox}>{result.parsedResponse}</div>
                    </>
                  )}

                  {result.errorMessage && (
                    <>
                      <p style={st.sectionLabel}>에러 메시지</p>
                      <p style={st.error}>{result.errorMessage}</p>
                    </>
                  )}

                  <p style={st.sectionLabel}>원본 응답</p>
                  <pre style={{ ...st.pre, maxHeight: '300px', overflow: 'auto' }}>
                    {(() => { try { return JSON.stringify(JSON.parse(result.responseBody), null, 2) } catch { return result.responseBody } })()}
                  </pre>
                </div>
              )}
            </Card>
          </div>
        )}
      </main>
    </>
  )
}

function resolveOptionSchemaJson(setting, settingOptions) {
  if (!setting) {
    return ''
  }

  if (setting.optionSchemaJson) {
    return setting.optionSchemaJson
  }

  const providerOption = settingOptions?.providers?.[setting.providerType]
  return providerOption?.modelOptions?.[setting.model]?.optionSchemaJson
    ?? providerOption?.optionSchemas?.[0]?.value
    ?? ''
}

function parseOptionSchema(schemaJson) {
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

function defaultOptionValues(schema) {
  const defaults = {}
  Object.entries(schema).forEach(([name, config]) => {
    if (Object.prototype.hasOwnProperty.call(config, 'default')) {
      defaults[name] = String(config.default)
    } else {
      defaults[name] = ''
    }
  })
  return defaults
}

function castOptionValues(schema, values) {
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

function validateRange(name, config, value) {
  if (config.minimum != null && value < Number(config.minimum)) {
    throw new Error(`${name} 옵션은 ${config.minimum} 이상이어야 합니다.`)
  }

  if (config.maximum != null && value > Number(config.maximum)) {
    throw new Error(`${name} 옵션은 ${config.maximum} 이하여야 합니다.`)
  }
}

function OptionField({ name, schema, value, onChange }) {
  const commonProps = {
    id: `option-${name}`,
    value: value ?? '',
    onChange: e => onChange(e.target.value)
  }
  const isRangedNumber = (schema.type === 'number' || schema.type === 'integer')
    && schema.minimum != null
    && schema.maximum != null
  const step = schema.type === 'integer' ? 1 : resolveNumberStep(schema)

  return (
    <label style={st.optionField} htmlFor={`option-${name}`}>
      <span style={st.optionLabelRow}>
        <span>{name}</span>
        {isRangedNumber && (
          <span style={st.optionRange}>{schema.minimum} - {schema.maximum}</span>
        )}
      </span>
      {schema.enum?.length ? (
        <select style={st.select} {...commonProps}>
          <option value="">기본값</option>
          {schema.enum.map(option => (
            <option key={option} value={option}>{option}</option>
          ))}
        </select>
      ) : schema.type === 'boolean' ? (
        <select style={st.select} {...commonProps}>
          <option value="">기본값</option>
          <option value="true">true</option>
          <option value="false">false</option>
        </select>
      ) : isRangedNumber ? (
        <div style={st.rangeField}>
          <input
            id={`option-${name}`}
            style={st.range}
            type="range"
            min={schema.minimum}
            max={schema.maximum}
            step={step}
            value={value === '' || value == null ? schema.default ?? schema.minimum : value}
            onChange={e => onChange(e.target.value)}
          />
          <input
            style={st.rangeNumber}
            type="number"
            min={schema.minimum}
            max={schema.maximum}
            step={step}
            placeholder={schema.default != null ? String(schema.default) : ''}
            {...commonProps}
          />
        </div>
      ) : (
        <input
          style={st.input}
          type={schema.type === 'number' || schema.type === 'integer' ? 'number' : 'text'}
          min={schema.minimum}
          max={schema.maximum}
          step={step}
          placeholder={schema.default != null ? String(schema.default) : ''}
          {...commonProps}
        />
      )}
    </label>
  )
}

function resolveNumberStep(schema) {
  if (schema.step != null) {
    return schema.step
  }

  return 0.01
}

const st = {
  main: { maxWidth: '1100px', margin: '40px auto', padding: '0 24px' },
  heading: { fontSize: '22px', fontWeight: 700, marginBottom: '24px' },
  subheading: { fontSize: '16px', fontWeight: 700, marginBottom: '16px' },
  layout: { display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px', alignItems: 'start' },
  panel: { background: '#fff', borderRadius: '12px', padding: '24px', boxShadow: '0 1px 8px rgba(0,0,0,0.07)', display: 'flex', flexDirection: 'column', gap: '10px' },
  label: { fontSize: '13px', fontWeight: 600, color: '#374151' },
  hint: { fontWeight: 400, color: '#9ca3af' },
  select: { padding: '9px 12px', border: '1px solid #d1d5db', borderRadius: '6px', fontSize: '14px', outline: 'none' },
  input: { padding: '9px 12px', border: '1px solid #d1d5db', borderRadius: '6px', fontSize: '14px', outline: 'none' },
  textarea: { padding: '9px 12px', border: '1px solid #d1d5db', borderRadius: '6px', fontSize: '13px', fontFamily: 'inherit', outline: 'none', resize: 'vertical' },
  optionSection: { border: '1px solid #e5e7eb', borderRadius: '8px', padding: '12px', background: '#fafafa' },
  optionTitle: { fontSize: '13px', fontWeight: 700, marginBottom: '10px' },
  optionGrid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '10px' },
  optionField: { display: 'flex', flexDirection: 'column', gap: '5px' },
  optionLabel: { fontSize: '12px', fontWeight: 600, color: '#4b5563' },
  optionLabelRow: { display: 'flex', justifyContent: 'space-between', gap: '8px', alignItems: 'center', fontSize: '12px', fontWeight: 600, color: '#4b5563' },
  optionRange: { color: '#9ca3af', fontWeight: 500 },
  rangeField: { display: 'grid', gridTemplateColumns: '1fr 72px', gap: '8px', alignItems: 'center' },
  range: { width: '100%' },
  rangeNumber: { width: '72px', boxSizing: 'border-box', padding: '8px 6px', border: '1px solid #d1d5db', borderRadius: '6px', fontSize: '13px', outline: 'none' },
  settingInfo: { background: '#f8fafc', borderRadius: '8px', padding: '12px', border: '1px solid #e2e8f0' },
  settingInfoRow: { fontSize: '12px', color: '#4b5563', marginBottom: '4px' },
  btnRow: { display: 'flex', gap: '10px', marginTop: '4px' },
  previewBtn: { flex: 1, padding: '10px', background: '#f0fdf4', color: '#16a34a', border: '1px solid #86efac', borderRadius: '8px', fontWeight: 600, cursor: 'pointer', fontSize: '14px' },
  executeBtn: { flex: 1, padding: '10px', background: '#4f46e5', color: '#fff', border: 'none', borderRadius: '8px', fontWeight: 600, cursor: 'pointer', fontSize: '14px' },
  info: { color: '#6b7280', fontSize: '14px' },
  warn: { color: '#d97706', fontSize: '13px', background: '#fffbeb', padding: '10px', borderRadius: '6px', border: '1px solid #fde68a' },
  error: { color: '#ef4444', fontSize: '13px' },
  resultLabel: { fontSize: '15px', fontWeight: 700, marginBottom: '10px' },
  resultHeader: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '6px' },
  resultMeta: { display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '12px' },
  badge: { padding: '3px 10px', background: '#dbeafe', color: '#1d4ed8', borderRadius: '20px', fontSize: '12px', fontWeight: 600 },
  endpoint: { fontSize: '12px', color: '#374151', background: '#f3f4f6', padding: '4px 8px', borderRadius: '4px', wordBreak: 'break-all' },
  sectionLabel: { fontSize: '12px', fontWeight: 600, color: '#6b7280', marginBottom: '4px', marginTop: '10px' },
  pre: { background: '#1e293b', color: '#e2e8f0', borderRadius: '8px', padding: '14px', fontSize: '12px', fontFamily: 'monospace', overflowX: 'auto', whiteSpace: 'pre-wrap', wordBreak: 'break-all' },
  parsedBox: { background: '#f0fdf4', border: '1px solid #86efac', borderRadius: '8px', padding: '14px', fontSize: '14px', lineHeight: 1.7, whiteSpace: 'pre-wrap', wordBreak: 'break-word' },
  statusBadge: { padding: '4px 12px', borderRadius: '20px', fontSize: '12px', fontWeight: 700 },
  statusOk: { background: '#dcfce7', color: '#16a34a' },
  statusFail: { background: '#fee2e2', color: '#dc2626' }
}
