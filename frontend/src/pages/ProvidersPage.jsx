import { useCallback, useState } from 'react'
import Navbar from '../components/Navbar'
import { PageHeader, Button, LoadingSpinner, EmptyState, Modal } from '../components/ui'
import ProviderKeyCard from '../components/providers/ProviderKeyCard'
import ProviderKeyForm from '../components/providers/ProviderKeyForm'
import { getProviderKeys, createProviderKey, updateProviderKey, deleteProviderKey } from '../api/providers'
import { useProviderOptions } from '../hooks/useProviderOptions'
import { useAsyncList } from '../hooks/useAsyncList'
import {
  EMPTY_PROVIDER_KEY_FORM,
  buildProviderKeyUpdatePayload,
  createProviderKeyEditForm
} from '../utils/providerKeyForm'
import { providersPageStyles as styles } from '../styles/pageStyles/providersPageStyles'

export default function ProvidersPage() {
  const loadKeys = useCallback(() => getProviderKeys(), [])
  const { items: keys, setItems: setKeys, loading, error, setError, reload: fetchKeys } = useAsyncList(loadKeys)
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState(EMPTY_PROVIDER_KEY_FORM)
  const [editTarget, setEditTarget] = useState(null)
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState('')
  const [deleteTarget, setDeleteTarget] = useState(null)

  const { providerTypes } = useProviderOptions('OPENAI')

  async function handleCreate(e) {
    e.preventDefault()
    setFormError('')
    setSaving(true)
    try {
      if (editTarget) {
        await updateProviderKey(editTarget.id, buildProviderKeyUpdatePayload(editTarget, form))
      } else {
        await createProviderKey(form.providerType, form.apiKey, form.displayName)
      }
      resetForm()
      setShowForm(false)
      fetchKeys()
    } catch (err) {
      setFormError(err.message)
    } finally {
      setSaving(false)
    }
  }

  function openCreate() {
    resetForm()
    setShowForm(true)
  }

  function openEdit(key) {
    setEditTarget(key)
    setForm(createProviderKeyEditForm(key))
    setFormError('')
    setShowForm(true)
  }

  function resetForm() {
    setEditTarget(null)
    setForm(EMPTY_PROVIDER_KEY_FORM)
    setFormError('')
  }

  function updateForm(patch) {
    setForm(prev => ({ ...prev, ...patch }))
  }

  function toggleForm() {
    if (showForm) {
      resetForm()
      setShowForm(false)
      return
    }

    openCreate()
  }

  async function handleDelete() {
    if (!deleteTarget) return
    try {
      await deleteProviderKey(deleteTarget.id)
      setKeys(prev => prev.filter(k => k.id !== deleteTarget.id))
      setDeleteTarget(null)
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <>
      <Navbar />
      <main className="pd-page-enter" style={styles.main}>
        <PageHeader
          title="API Key 관리"
          eyebrow="Credentials"
          description="Provider별 API Key를 등록하고 실행 화면에서 사용할 인증 정보를 관리합니다."
          actionLabel={showForm ? '취소' : '새 API Key'}
          onAction={toggleForm}
        />

        {showForm && (
          <ProviderKeyForm
            form={form}
            providerTypes={providerTypes}
            editTarget={editTarget}
            saving={saving}
            error={formError}
            onChange={updateForm}
            onSubmit={handleCreate}
            styles={styles}
          />
        )}

        {loading && <LoadingSpinner />}
        {error && <p style={styles.error}>{error}</p>}

        {!loading && keys.length === 0 && (
          <EmptyState message="등록된 API Key가 없습니다. 위에서 추가해보세요." />
        )}

        <div style={styles.list}>
          {keys.map(k => (
            <ProviderKeyCard
              key={k.id}
              providerKey={k}
              onEdit={openEdit}
              onDelete={setDeleteTarget}
              styles={styles}
            />
          ))}
        </div>
        <Modal
          isOpen={Boolean(deleteTarget)}
          onClose={() => setDeleteTarget(null)}
          title="API Key 삭제"
          width="420px"
          footer={(
            <>
              <Button variant="outline" onClick={() => setDeleteTarget(null)}>취소</Button>
              <Button variant="danger" onClick={handleDelete}>삭제</Button>
            </>
          )}
        >
          <p style={styles.confirmText}>
            {deleteTarget ? `"${deleteTarget.displayName}" API Key를 삭제합니다.` : ''}
          </p>
        </Modal>
      </main>
    </>
  )
}
