import { useState, useEffect } from 'react'
import Navbar from '../components/Navbar'
import { getOrganizations, createOrganization, addOrganizationMember } from '../api/organizations'

export default function OrganizationsPage() {
  const [orgs, setOrgs] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showCreateForm, setShowCreateForm] = useState(false)
  const [orgName, setOrgName] = useState('')
  const [creating, setCreating] = useState(false)
  const [createError, setCreateError] = useState('')
  const [memberForms, setMemberForms] = useState({})
  const [memberErrors, setMemberErrors] = useState({})
  const [memberAdding, setMemberAdding] = useState({})

  useEffect(() => {
    fetchOrgs()
  }, [])

  async function fetchOrgs() {
    setLoading(true)
    setError('')
    try {
      const data = await getOrganizations()
      setOrgs(data ?? [])
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  async function handleCreate(e) {
    e.preventDefault()
    setCreateError('')
    setCreating(true)
    try {
      await createOrganization(orgName)
      setOrgName('')
      setShowCreateForm(false)
      fetchOrgs()
    } catch (err) {
      setCreateError(err.message)
    } finally {
      setCreating(false)
    }
  }

  async function handleAddMember(e, orgId) {
    e.preventDefault()
    setMemberErrors(prev => ({ ...prev, [orgId]: '' }))
    setMemberAdding(prev => ({ ...prev, [orgId]: true }))
    try {
      await addOrganizationMember(orgId, memberForms[orgId] ?? '')
      setMemberForms(prev => ({ ...prev, [orgId]: '' }))
    } catch (err) {
      setMemberErrors(prev => ({ ...prev, [orgId]: err.message }))
    } finally {
      setMemberAdding(prev => ({ ...prev, [orgId]: false }))
    }
  }

  return (
    <>
      <Navbar />
      <main style={styles.main}>
        <div style={styles.header}>
          <h2 style={styles.heading}>조직 관리</h2>
          <button style={styles.addBtn} onClick={() => setShowCreateForm(v => !v)}>
            {showCreateForm ? '취소' : '+ 조직 만들기'}
          </button>
        </div>

        {showCreateForm && (
          <form onSubmit={handleCreate} style={styles.form}>
            <h3 style={styles.formTitle}>새 조직 생성</h3>
            <label style={styles.label}>조직 이름</label>
            <input
              style={styles.input}
              type="text"
              placeholder="조직 이름을 입력하세요"
              value={orgName}
              onChange={e => setOrgName(e.target.value)}
              required
            />
            {createError && <p style={styles.error}>{createError}</p>}
            <button style={styles.submitBtn} type="submit" disabled={creating}>
              {creating ? '생성 중...' : '생성'}
            </button>
          </form>
        )}

        {loading && <p style={styles.info}>불러오는 중...</p>}
        {error && <p style={styles.error}>{error}</p>}

        {!loading && orgs.length === 0 && (
          <p style={styles.info}>소속된 조직이 없습니다. 위에서 조직을 만들어보세요.</p>
        )}

        <div style={styles.list}>
          {orgs.map(org => (
            <div key={org.id} style={styles.card}>
              <div style={styles.cardHeader}>
                <span style={styles.orgName}>{org.name}</span>
                <span style={styles.orgId}>ID: {org.id}</span>
              </div>

              <form onSubmit={e => handleAddMember(e, org.id)} style={styles.memberForm}>
                <input
                  style={styles.memberInput}
                  type="email"
                  placeholder="멤버 이메일 입력"
                  value={memberForms[org.id] ?? ''}
                  onChange={e => setMemberForms(prev => ({ ...prev, [org.id]: e.target.value }))}
                  required
                />
                <button
                  style={styles.memberBtn}
                  type="submit"
                  disabled={memberAdding[org.id]}
                >
                  {memberAdding[org.id] ? '추가 중...' : '멤버 추가'}
                </button>
              </form>
              {memberErrors[org.id] && (
                <p style={styles.error}>{memberErrors[org.id]}</p>
              )}
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
  list: { display: 'flex', flexDirection: 'column', gap: '16px' },
  card: {
    background: '#fff',
    padding: '20px 24px',
    borderRadius: '10px',
    boxShadow: '0 1px 6px rgba(0,0,0,0.06)',
    display: 'flex',
    flexDirection: 'column',
    gap: '12px'
  },
  cardHeader: { display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
  orgName: { fontSize: '16px', fontWeight: 700 },
  orgId: { fontSize: '12px', color: '#9ca3af' },
  memberForm: { display: 'flex', gap: '8px' },
  memberInput: {
    flex: 1,
    padding: '8px 12px',
    border: '1px solid #d1d5db',
    borderRadius: '6px',
    fontSize: '14px',
    outline: 'none'
  },
  memberBtn: {
    padding: '8px 16px',
    background: '#ede9fe',
    color: '#6d28d9',
    border: 'none',
    borderRadius: '6px',
    cursor: 'pointer',
    fontWeight: 600,
    fontSize: '13px',
    whiteSpace: 'nowrap'
  }
}
