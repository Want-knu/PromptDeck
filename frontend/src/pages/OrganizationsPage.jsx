import { useCallback, useState } from 'react'
import Navbar from '../components/Navbar'
import { PageHeader, LoadingSpinner, EmptyState } from '../components/ui'
import OrganizationCard from '../components/organizations/OrganizationCard'
import OrganizationCreateForm from '../components/organizations/OrganizationCreateForm'
import { getOrganizations, createOrganization, addOrganizationMember } from '../api/organizations'
import { useAsyncList } from '../hooks/useAsyncList'
import { organizationsPageStyles as styles } from '../styles/pageStyles/organizationsPageStyles'

export default function OrganizationsPage() {
  const loadOrganizations = useCallback(() => getOrganizations(), [])
  const { items: orgs, loading, error, reload: fetchOrgs } = useAsyncList(loadOrganizations)
  const [showCreateForm, setShowCreateForm] = useState(false)
  const [orgName, setOrgName] = useState('')
  const [creating, setCreating] = useState(false)
  const [createError, setCreateError] = useState('')
  const [memberForms, setMemberForms] = useState({})
  const [memberErrors, setMemberErrors] = useState({})
  const [memberAdding, setMemberAdding] = useState({})

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

  function setMemberEmail(orgId, email) {
    setMemberForms(prev => ({ ...prev, [orgId]: email }))
  }

  return (
    <>
      <Navbar />
      <main className="pd-page-enter" style={styles.main}>
        <PageHeader
          title="조직 관리"
          eyebrow="Workspace"
          description="공동으로 사용할 Provider 프로필과 실행 기록을 조직 단위로 관리합니다."
          actionLabel={showCreateForm ? '취소' : '새 조직'}
          onAction={() => setShowCreateForm(v => !v)}
        />

        {showCreateForm && (
          <OrganizationCreateForm
            name={orgName}
            creating={creating}
            error={createError}
            onNameChange={setOrgName}
            onSubmit={handleCreate}
            styles={styles}
          />
        )}

        {loading && <LoadingSpinner />}
        {error && <p style={styles.error}>{error}</p>}

        {!loading && orgs.length === 0 && (
          <EmptyState message="소속된 조직이 없습니다. 위에서 조직을 만들어보세요." />
        )}

        <div style={styles.list}>
          {orgs.map(org => (
            <OrganizationCard
              key={org.id}
              organization={org}
              memberEmail={memberForms[org.id]}
              memberError={memberErrors[org.id]}
              addingMember={memberAdding[org.id]}
              onMemberEmailChange={setMemberEmail}
              onAddMember={handleAddMember}
              styles={styles}
            />
          ))}
        </div>
      </main>
    </>
  )
}
