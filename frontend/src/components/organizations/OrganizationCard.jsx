import { Button, Card } from '../ui'

export default function OrganizationCard({
  organization,
  memberEmail,
  memberError,
  addingMember,
  onMemberEmailChange,
  onAddMember,
  styles
}) {
  return (
    <Card className="pd-stagger-1" style={styles.card}>
      <div style={styles.cardHeader}>
        <span style={styles.orgName}>{organization.name}</span>
        <span style={styles.orgId}>ID: {organization.id}</span>
      </div>

      <form onSubmit={e => onAddMember(e, organization.id)} style={styles.memberForm}>
        <input
          style={styles.memberInput}
          type="email"
          placeholder="멤버 이메일 입력"
          value={memberEmail ?? ''}
          onChange={e => onMemberEmailChange(organization.id, e.target.value)}
          required
        />
        <Button
          type="submit"
          variant="outline"
          size="sm"
          loading={addingMember}
        >
          멤버 추가
        </Button>
      </form>
      {memberError && (
        <p style={styles.error}>{memberError}</p>
      )}
    </Card>
  )
}
