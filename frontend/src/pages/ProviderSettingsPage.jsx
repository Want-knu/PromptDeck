import { useState, useEffect } from 'react'
import Navbar from '../components/Navbar'
import FormField from '../components/ui/FormField'
import {
  getProviderSettings,
  createProviderSetting,
  updateProviderSetting,
  deleteProviderSetting
} from '../api/providerSettings'
import {
  PROVIDER_TYPES,
  HTTP_METHODS,
  AUTH_TYPES,
  PROVIDER_MODELS,
  PROVIDER_PRESETS,
} from '../constants/providerOptions'
import { useProviderOptions } from '../hooks/useProviderOptions'

const OPENAI_PRESET = PROVIDER_PRESETS.OPENAI

const EMPTY_FORM = {
  providerType: 'OPENAI',
  displayName: '',
  model: PROVIDER_MODELS.OPENAI[0],
  ...OPENAI_PRESET,
  headersJson: '',
  queryParamsJson: '',
  optionSchemaJson: '',
}

export default function ProviderSettingsPage() {
  const [settings, setSettings] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [mode, setMode] = useState('list') // 'list' | 'create' | 'edit'
  const [form, setForm] = useState(EMPTY_FORM)
  const [editTarget, setEditTarget] = useState(null)
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState('')
  const [tab, setTab] = useState('basic') // 'basic' | 'advanced'

  const { changeProviderType } = useProviderOptions('OPENAI')

  useEffect(() => { fetchSettings() }, [])

  async function fetchSettings() {
    setLoading(true)
    setError('')
    try {
      const data = await getProviderSettings()
      setSettings(data ?? [])
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  function openCreate() {
    setForm(EMPTY_FORM)
    setEditTarget(null)
    setFormError('')
    setTab('basic')
    setMode('create')
  }

  function openEdit(s) {
    setForm({
      providerType: s.providerType,
      displayName: s.displayName,
      model: s.model,
      endpoint: s.endpoint,
      method: s.method ?? 'POST',
      authType: s.authType ?? 'BEARER',
      authHeaderName: s.authHeaderName ?? '',
      authQueryParamName: s.authQueryParamName ?? '',
      headersJson: s.headersJson ?? '',
      queryParamsJson: s.queryParamsJson ?? '',
      bodyTemplateJson: s.bodyTemplateJson ?? '',
      optionSchemaJson: s.optionSchemaJson ?? '',
      responsePath: s.responsePath ?? ''
    })
    setEditTarget(s)
    setFormError('')
    setTab('basic')
    setMode('edit')
  }

  function cancel() {
    setMode('list')
    setEditTarget(null)
  }

  // providerType 변경 시 연관 필드를 프리셋으로 초기화
  function handleProviderTypeChange(e) {
    changeProviderType(e.target.value, setForm)
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setFormError('')
    setSaving(true)
    try {
      const payload = {
        ...form,
        authHeaderName: form.authType === 'HEADER' ? form.authHeaderName : undefined,
        authQueryParamName: form.authType === 'QUERY_PARAM' ? form.authQueryParamName : undefined
      }
      if (mode === 'create') {
        await createProviderSetting(payload)
      } else {
        await updateProviderSetting(editTarget.id, { ...payload, version: editTarget.version })
      }
      setMode('list')
      fetchSettings()
    } catch (err) {
      setFormError(err.message)
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(s) {
    if (!window.confirm(`"${s.displayName}" 설정을 삭제하시겠습니까?`)) return
    try {
      await deleteProviderSetting(s.id)
      setSettings(prev => prev.filter(x => x.id !== s.id))
    } catch (err) {
      alert(err.message)
    }
  }

  const set = (key) => (e) => setForm(p => ({ ...p, [key]: e.target.value }))

  if (mode === 'create' || mode === 'edit') {
    const isCustom = form.providerType === 'CUSTOM'
    const availableModels = PROVIDER_MODELS[form.providerType] ?? []

    return (
      <>
        <Navbar />
        <main style={s.main}>
          <div style={s.header}>
            <h2 style={s.heading}>{mode === 'create' ? '새 Provider 설정 추가' : 'Provider 설정 수정'}</h2>
            <button style={s.cancelBtn} onClick={cancel}>목록으로</button>
          </div>

          <div style={s.tabs}>
            {['basic', 'advanced'].map(t => (
              <button
                key={t}
                style={{ ...s.tab, ...(tab === t ? s.tabActive : {}) }}
                onClick={() => setTab(t)}
              >
                {t === 'basic' ? '기본 정보' : '고급 설정'}
              </button>
            ))}
          </div>

          <form onSubmit={handleSubmit} style={s.form}>
            {tab === 'basic' && (
              <>
                <FormField label="Provider 타입">
                  <select style={s.input} value={form.providerType} onChange={handleProviderTypeChange}>
                    {PROVIDER_TYPES.map(t => <option key={t}>{t}</option>)}
                  </select>
                </FormField>
                <FormField label="표시 이름">
                  <input style={s.input} required value={form.displayName} onChange={set('displayName')} placeholder="My GPT-4 Setting" />
                </FormField>
                <FormField label="모델명">
                  {isCustom ? (
                    <input style={s.input} required value={form.model} onChange={set('model')} placeholder="your-model-name" />
                  ) : (
                    <select style={s.input} value={form.model} onChange={set('model')}>
                      {availableModels.map(m => <option key={m}>{m}</option>)}
                    </select>
                  )}
                </FormField>
                <FormField label="엔드포인트 URL" hint={!isCustom ? '프리셋 자동완성' : undefined}>
                  {isCustom ? (
                    <input
                      style={s.input}
                      required
                      value={form.endpoint}
                      onChange={set('endpoint')}
                      placeholder="https://api.example.com/v1/chat"
                    />
                  ) : (
                    <select style={{ ...s.input, ...s.inputLocked }} value={form.endpoint} onChange={set('endpoint')} disabled>
                      <option value={form.endpoint}>{form.endpoint}</option>
                    </select>
                  )}
                </FormField>
                <FormField label="HTTP 메서드" hint={!isCustom ? '프리셋 자동완성' : undefined}>
                  <select style={{ ...s.input, ...(!isCustom ? s.inputLocked : {}) }} value={form.method} onChange={set('method')} disabled={!isCustom}>
                    {HTTP_METHODS.map(m => <option key={m}>{m}</option>)}
                  </select>
                </FormField>
                <FormField label="인증 방식" hint={!isCustom ? '프리셋 자동완성' : undefined}>
                  <select style={{ ...s.input, ...(!isCustom ? s.inputLocked : {}) }} value={form.authType} onChange={set('authType')} disabled={!isCustom}>
                    {AUTH_TYPES.map(a => <option key={a}>{a}</option>)}
                  </select>
                </FormField>
                {form.authType === 'HEADER' && (
                  <FormField label="인증 헤더명">
                    <input style={s.input} value={form.authHeaderName} onChange={set('authHeaderName')} placeholder="X-API-Key" />
                  </FormField>
                )}
                {form.authType === 'QUERY_PARAM' && (
                  <FormField label="인증 쿼리 파라미터명">
                    <input style={s.input} value={form.authQueryParamName} onChange={set('authQueryParamName')} placeholder="api_key" />
                  </FormField>
                )}
                <FormField label="응답 추출 경로 (JSONPath)" hint={!isCustom ? '프리셋 자동완성' : undefined}>
                  <input
                    style={{ ...s.input, ...(!isCustom ? s.inputLocked : {}) }}
                    value={form.responsePath}
                    onChange={set('responsePath')}
                    disabled={!isCustom}
                    placeholder={isCustom ? 'choices[0].message.content' : undefined}
                  />
                </FormField>
              </>
            )}

            {tab === 'advanced' && (
              <>
                <FormField label="헤더 JSON" hint={'예: {"X-Custom": "value"}'}>
                  <textarea style={s.textarea} value={form.headersJson} onChange={set('headersJson')} rows={3} />
                </FormField>
                <FormField label="쿼리 파라미터 JSON" hint={'예: {"version": "2024-01"}'}>
                  <textarea style={s.textarea} value={form.queryParamsJson} onChange={set('queryParamsJson')} rows={3} />
                </FormField>
                <FormField label="바디 템플릿 JSON" hint={!isCustom ? '프리셋 자동완성 — 수정하려면 CUSTOM 타입을 사용하세요' : '{{prompt}}, {{model}} 등 변수 사용 가능'}>
                  <textarea
                    style={{ ...s.textarea, ...(!isCustom ? s.inputLocked : {}) }}
                    value={form.bodyTemplateJson}
                    onChange={set('bodyTemplateJson')}
                    rows={6}
                    disabled={!isCustom}
                  />
                </FormField>
                <FormField label="옵션 스키마 JSON" hint="사용자 정의 옵션 필드 스키마">
                  <textarea style={s.textarea} value={form.optionSchemaJson} onChange={set('optionSchemaJson')} rows={4} />
                </FormField>
              </>
            )}

            {formError && <p style={s.error}>{formError}</p>}
            <div style={{ display: 'flex', gap: '10px', marginTop: '8px' }}>
              <button style={s.submitBtn} type="submit" disabled={saving}>
                {saving ? '저장 중...' : '저장'}
              </button>
              <button style={s.cancelBtn} type="button" onClick={cancel}>취소</button>
            </div>
          </form>
        </main>
      </>
    )
  }

  return (
    <>
      <Navbar />
      <main style={s.main}>
        <div style={s.header}>
          <h2 style={s.heading}>Provider 설정 관리</h2>
          <button style={s.addBtn} onClick={openCreate}>+ 설정 추가</button>
        </div>

        {loading && <p style={s.info}>불러오는 중...</p>}
        {error && <p style={s.error}>{error}</p>}
        {!loading && settings.length === 0 && (
          <p style={s.info}>등록된 Provider 설정이 없습니다. 위에서 추가해보세요.</p>
        )}

        <div style={s.list}>
          {settings.map(item => (
            <div key={item.id} style={s.card}>
              <div style={s.cardLeft}>
                <span style={s.badge}>{item.providerType}</span>
                <div>
                  <p style={s.cardTitle}>{item.displayName}</p>
                  <p style={s.cardSub}>{item.model} · {item.endpoint}</p>
                  <p style={s.cardSub}>인증: {item.authType} · 메서드: {item.method}</p>
                </div>
              </div>
              <div style={s.cardActions}>
                <button style={s.editBtn} onClick={() => openEdit(item)}>수정</button>
                <button style={s.deleteBtn} onClick={() => handleDelete(item)}>삭제</button>
              </div>
            </div>
          ))}
        </div>
      </main>
    </>
  )
}

const s = {
  main: { maxWidth: '860px', margin: '40px auto', padding: '0 24px' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' },
  heading: { fontSize: '22px', fontWeight: 700 },
  addBtn: { padding: '8px 18px', background: '#4f46e5', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer', fontWeight: 600 },
  tabs: { display: 'flex', gap: '4px', marginBottom: '20px' },
  tab: { padding: '8px 20px', border: '1px solid #d1d5db', borderRadius: '8px', background: '#f9fafb', cursor: 'pointer', fontSize: '14px', fontWeight: 500 },
  tabActive: { background: '#4f46e5', color: '#fff', borderColor: '#4f46e5' },
  form: { background: '#fff', padding: '28px', borderRadius: '12px', boxShadow: '0 1px 8px rgba(0,0,0,0.07)', display: 'flex', flexDirection: 'column', gap: '14px' },
  input: { padding: '9px 12px', border: '1px solid #d1d5db', borderRadius: '6px', fontSize: '14px', outline: 'none' },
  inputLocked: { background: '#f3f4f6', color: '#6b7280', cursor: 'not-allowed', borderColor: '#e5e7eb' },
  textarea: { padding: '9px 12px', border: '1px solid #d1d5db', borderRadius: '6px', fontSize: '13px', fontFamily: 'monospace', outline: 'none', resize: 'vertical' },
  submitBtn: { padding: '10px 24px', background: '#4f46e5', color: '#fff', border: 'none', borderRadius: '8px', fontWeight: 600, cursor: 'pointer' },
  cancelBtn: { padding: '8px 18px', background: 'transparent', border: '1px solid #d1d5db', borderRadius: '8px', cursor: 'pointer', fontWeight: 500 },
  error: { color: '#ef4444', fontSize: '13px' },
  info: { color: '#6b7280', fontSize: '14px', marginTop: '24px' },
  list: { display: 'flex', flexDirection: 'column', gap: '12px' },
  card: { background: '#fff', padding: '18px 20px', borderRadius: '10px', boxShadow: '0 1px 6px rgba(0,0,0,0.06)', display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '12px', flexWrap: 'wrap' },
  cardLeft: { display: 'flex', alignItems: 'flex-start', gap: '14px' },
  badge: { display: 'inline-block', padding: '3px 10px', background: '#ede9fe', color: '#6d28d9', borderRadius: '20px', fontSize: '12px', fontWeight: 600, whiteSpace: 'nowrap', marginTop: '2px' },
  cardTitle: { fontSize: '15px', fontWeight: 700, marginBottom: '3px' },
  cardSub: { fontSize: '12px', color: '#6b7280', marginBottom: '2px' },
  cardActions: { display: 'flex', gap: '8px' },
  editBtn: { padding: '5px 14px', background: 'transparent', border: '1px solid #93c5fd', borderRadius: '6px', color: '#3b82f6', cursor: 'pointer', fontSize: '13px' },
  deleteBtn: { padding: '5px 14px', background: 'transparent', border: '1px solid #fca5a5', borderRadius: '6px', color: '#ef4444', cursor: 'pointer', fontSize: '13px' }
}
