import { useState, useEffect } from 'react'
import Navbar from '../components/Navbar'
import { PageHeader, Button, LoadingSpinner, EmptyState, Modal } from '../components/ui'
import ProviderSettingCard from '../components/providerSettings/ProviderSettingCard'
import ProviderSettingForm from '../components/providerSettings/ProviderSettingForm'
import {
  getProviderSettings,
  createProviderSetting,
  updateProviderSetting,
  deleteProviderSetting
} from '../api/providerSettings'
import { getOrganizations } from '../api/organizations'
import { useProviderOptions } from '../hooks/useProviderOptions'
import {
  buildProviderSettingPayload,
  createProviderSettingEditForm,
  createProviderSettingForm
} from '../utils/providerSettingForm'
import { providerSettingsPageStyles as s } from '../styles/pageStyles/providerSettingsPageStyles'

export default function ProviderSettingsPage() {
  const [settings, setSettings] = useState([])
  const [organizations, setOrganizations] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [mode, setMode] = useState('list')
  const [form, setForm] = useState(null)
  const [editTarget, setEditTarget] = useState(null)
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState('')
  const [tab, setTab] = useState('basic')
  const [deleteTarget, setDeleteTarget] = useState(null)

  const {
    changeProviderType,
    optionsReady,
    providerTypes,
    httpMethods,
    authTypes,
    providerModels,
    providerPresets,
  } = useProviderOptions('OPENAI')

  useEffect(() => {
    if (optionsReady && form === null) {
      setForm(createProviderSettingForm('OPENAI', providerPresets, providerModels))
    }
  }, [optionsReady, providerPresets, providerModels, form])

  useEffect(() => { fetchSettings() }, [])

  async function fetchSettings() {
    setLoading(true)
    setError('')
    try {
      const [data, orgData] = await Promise.all([
        getProviderSettings(),
        getOrganizations()
      ])
      setSettings(data ?? [])
      setOrganizations(orgData ?? [])
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  function openCreate() {
    setForm(createProviderSettingForm('OPENAI', providerPresets, providerModels))
    setEditTarget(null)
    setFormError('')
    setTab('basic')
    setMode('create')
  }

  function openEdit(s) {
    setForm(createProviderSettingEditForm(s))
    setEditTarget(s)
    setFormError('')
    setTab('basic')
    setMode('edit')
  }

  function cancel() {
    setMode('list')
    setEditTarget(null)
  }

  function handleProviderTypeChange(e) {
    const nextProviderType = e.target.value
    changeProviderType(nextProviderType, setForm)
    if (nextProviderType !== 'CUSTOM') {
      setTab('basic')
    }
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setFormError('')
    setSaving(true)
    try {
      const payload = buildProviderSettingPayload(form)
      if (mode === 'create') {
        await createProviderSetting(payload)
      } else {
        const { organizationId, ...updatePayload } = payload
        await updateProviderSetting(editTarget.id, { ...updatePayload, version: editTarget.version })
      }
      setMode('list')
      fetchSettings()
    } catch (err) {
      setFormError(err.message)
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete() {
    if (!deleteTarget) return
    try {
      await deleteProviderSetting(deleteTarget.id)
      setSettings(prev => prev.filter(x => x.id !== deleteTarget.id))
      setDeleteTarget(null)
    } catch (err) {
      setError(err.message)
    }
  }

  const set = (key) => (e) => setForm(p => ({ ...p, [key]: e.target.value }))

  if (mode === 'create' || mode === 'edit') {
    if (form === null) {
      return (
        <>
          <Navbar />
          <main className="pd-page-enter" style={s.main}><LoadingSpinner message="옵션 불러오는 중..." /></main>
        </>
      )
    }

    const availableModels = providerModels[form.providerType] ?? []

    return (
      <>
        <Navbar />
        <main className="pd-page-enter" style={s.main}>
          <PageHeader
            title={mode === 'create' ? '새 Provider 프로필 추가' : 'Provider 프로필 수정'}
            eyebrow="Provider Profile"
            description="실행 화면에서 재사용할 Provider와 모델 조합을 구성합니다."
            actionLabel="목록으로"
            onAction={cancel}
          />

          <ProviderSettingForm
            mode={mode}
            form={form}
            tab={tab}
            saving={saving}
            error={formError}
            organizations={organizations}
            providerTypes={providerTypes}
            httpMethods={httpMethods}
            authTypes={authTypes}
            availableModels={availableModels}
            onTabChange={setTab}
            onProviderTypeChange={handleProviderTypeChange}
            onFieldChange={set}
            onSubmit={handleSubmit}
            onCancel={cancel}
            styles={s}
          />
        </main>
      </>
    )
  }

  return (
    <>
      <Navbar />
      <main className="pd-page-enter" style={s.main}>
        <PageHeader
          title="Provider 프로필"
          eyebrow="Models"
          description="Provider, 모델, CUSTOM 연결 방식을 프로필로 저장하고 실행 페이지에서 바로 선택합니다."
          actionLabel="새 프로필"
          onAction={openCreate}
        />

        {loading && <LoadingSpinner />}
        {error && <p style={s.error}>{error}</p>}
        {!loading && settings.length === 0 && (
          <EmptyState message="등록된 Provider 프로필이 없습니다. 위에서 추가해보세요." />
        )}

        <div style={s.list}>
          {settings.map(item => (
            <ProviderSettingCard
              key={item.id}
              setting={item}
              onEdit={openEdit}
              onDelete={setDeleteTarget}
              styles={s}
            />
          ))}
        </div>
        <Modal
          isOpen={Boolean(deleteTarget)}
          onClose={() => setDeleteTarget(null)}
          title="Provider 프로필 삭제"
          width="420px"
          footer={(
            <>
              <Button variant="outline" onClick={() => setDeleteTarget(null)}>취소</Button>
              <Button variant="danger" onClick={handleDelete}>삭제</Button>
            </>
          )}
        >
          <p style={s.confirmText}>
            {deleteTarget ? `"${deleteTarget.displayName}" 프로필을 삭제합니다.` : ''}
          </p>
        </Modal>
      </main>
    </>
  )
}
