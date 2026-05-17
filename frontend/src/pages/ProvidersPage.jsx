import { useState, useEffect } from 'react'
import Navbar from '../components/Navbar'
import { getProviderKeys, createProviderKey, deleteProviderKey } from '../api/providers'
import { PROVIDER_TYPES } from '../constants/providerOptions'

export default function ProvidersPage() {
  const [keys, setKeys] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [form, setForm] = useState({ providerType: 'OPENAI', apiKey: '', displayName: '' })
  const [saving, setSaving] = useState(false)
  const [formError, setFormError] = useState('')

  useEffect(() => {
    fetchKeys()
  }, [])

  async function fetchKeys() {
    setLoading(true)
    setError('')
    try {
      const data = await getProviderKeys()
      setKeys(data ?? [])
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  async function handleCreate(e) {
    e.preventDefault()
    setFormError('')
    setSaving(true)
    try {
      await createProviderKey(form.providerType, form.apiKey, form.displayName)
      setForm({ providerType: 'OPENAI', apiKey: '', displayName: '' })
      setShowForm(false)
      fetchKeys()
    } catch (err) {
      setFormError(err.message)
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(id) {
    if (!window.confirm('이 API Key를 삭제하시겠습니까?')) return
    try {
      await deleteProviderKey(id)
      setKeys(prev => prev.filter(k => k.id !== id))
    } catch (err) {
      alert(err.message)
    }
  }

  return (
    <>
      <Navbar />
      <main style={styles.main}>
        <div style={styles.header}>
          <h2 style={styles.heading}>Provider Key 관리</h2>
          <button style={styles.addBtn} onClick={() => setShowForm(v => !v)}>
            {showForm ? '취소' : '+ Key 추가'}
          </button>
        </div>

        {showForm && (
          <form onSubmit={handleCreate} style={styles.form}>
            <h3 style={styles.formTitle}>새 API Key 등록</h3>
            <label style={styles.label}>Provider</label>
            <select
              style={styles.input}
              value={form.providerType}
              onChange={e => setForm(p => ({ ...p, providerType: e.target.value }))}
            >
              {PROVIDER_TYPES.map(t => <option key={t} value={t}>{t}</option>)}
            </select>
            <label style={styles.label}>표시 이름</label>
            <input
              style={styles.input}
              type="text"
              placeholder="My OpenAI Key"
              value={form.displayName}
              onChange={e => setForm(p => ({ ...p, displayName: e.target.value }))}
              required
            />
            <label style={styles.label}>API Key</label>
            <input
              style={styles.input}
              type="password"
              placeholder="sk-..."
              value={form.apiKey}
              onChange={e => setForm(p => ({ ...p, apiKey: e.target.value }))}
              required
            />
            {formError && <p style={styles.error}>{formError}</p>}
            <button style={styles.submitBtn} type="submit" disabled={saving}>
              {saving ? '저장 중...' : '저장'}
            </button>
          </form>
        )}

        {loading && <p style={styles.info}>불러오는 중...</p>}
        {error && <p style={styles.error}>{error}</p>}

        {!loading && keys.length === 0 && (
          <p style={styles.info}>등록된 API Key가 없습니다. 위에서 추가해보세요.</p>
        )}

        <div style={styles.list}>
          {keys.map(k => (
            <div key={k.id} style={styles.keyCard}>
              <div>
                <span style={styles.badge}>{k.providerType}</span>
                <span style={styles.keyName}>{k.displayName}</span>
              </div>
              <div style={styles.keyMeta}>
                <code style={styles.maskedKey}>{k.maskedApiKey}</code>
                <button style={styles.deleteBtn} onClick={() => handleDelete(k.id)}>삭제</button>
              </div>
            </div>
          ))}
        </div>
      </main>
    </>
  )
}

const styles = {
  main: { maxWidth: '800px', margin: '40px auto', padding: '0 24px' },
  header: { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' },
  heading: { fontSize: '22px', fontWeight: 700 },
  addBtn: {
    padding: '8px 18px',
    background: '#4f46e5',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontWeight: 600
  },
  form: {
    background: '#fff',
    padding: '24px',
    borderRadius: '10px',
    boxShadow: '0 1px 8px rgba(0,0,0,0.07)',
    marginBottom: '24px',
    display: 'flex',
    flexDirection: 'column',
    gap: '8px'
  },
  formTitle: { fontSize: '16px', fontWeight: 700, marginBottom: '8px' },
  label: { fontSize: '13px', fontWeight: 600, color: '#374151' },
  input: {
    padding: '9px 12px',
    border: '1px solid #d1d5db',
    borderRadius: '6px',
    fontSize: '14px',
    outline: 'none',
    marginBottom: '4px'
  },
  submitBtn: {
    marginTop: '8px',
    padding: '10px',
    background: '#4f46e5',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    fontWeight: 600,
    cursor: 'pointer'
  },
  error: { color: '#ef4444', fontSize: '13px' },
  info: { color: '#6b7280', fontSize: '14px', marginTop: '24px' },
  list: { display: 'flex', flexDirection: 'column', gap: '12px' },
  keyCard: {
    background: '#fff',
    padding: '16px 20px',
    borderRadius: '10px',
    boxShadow: '0 1px 6px rgba(0,0,0,0.06)',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    flexWrap: 'wrap',
    gap: '10px'
  },
  badge: {
    display: 'inline-block',
    padding: '2px 10px',
    background: '#ede9fe',
    color: '#6d28d9',
    borderRadius: '20px',
    fontSize: '12px',
    fontWeight: 600,
    marginRight: '10px'
  },
  keyName: { fontSize: '15px', fontWeight: 600 },
  keyMeta: { display: 'flex', alignItems: 'center', gap: '16px' },
  maskedKey: { fontSize: '13px', color: '#6b7280', background: '#f3f4f6', padding: '3px 8px', borderRadius: '4px' },
  deleteBtn: {
    padding: '5px 12px',
    background: 'transparent',
    border: '1px solid #fca5a5',
    borderRadius: '6px',
    color: '#ef4444',
    cursor: 'pointer',
    fontSize: '13px'
  }
}
