export const organizationsPageStyles = {
  main: { maxWidth: '980px', margin: '34px auto', padding: '0 24px 56px' },
  form: {
    background: 'var(--pd-surface-raised)',
    padding: '26px',
    borderRadius: '8px',
    boxShadow: 'var(--pd-shadow-sm)',
    border: '1px solid var(--pd-border-soft)',
    backdropFilter: 'blur(18px)',
    marginBottom: '24px',
    display: 'flex',
    flexDirection: 'column',
    gap: '8px'
  },
  formTitle: { fontSize: '17px', fontWeight: 900, marginBottom: '10px', color: 'var(--pd-heading)' },
  label: { fontSize: '13px', fontWeight: 800, color: 'var(--pd-text)' },
  input: {
    padding: '11px 12px',
    border: '1px solid var(--pd-border)',
    borderRadius: '8px',
    fontSize: '14px',
    outline: 'none',
    marginBottom: '4px'
  },
  error: { color: 'var(--pd-danger)', fontSize: '13px' },
  list: { display: 'flex', flexDirection: 'column', gap: '16px' },
  card: {
    background: 'var(--pd-surface-raised)',
    padding: '22px 24px',
    borderRadius: '8px',
    boxShadow: 'var(--pd-shadow-sm)',
    border: '1px solid var(--pd-border-soft)',
    backdropFilter: 'blur(18px)',
    display: 'flex',
    flexDirection: 'column',
    gap: '12px'
  },
  cardHeader: { display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
  orgName: { fontSize: '17px', fontWeight: 900, color: 'var(--pd-heading)' },
  orgId: { fontSize: '12px', color: 'var(--pd-muted-soft)' },
  memberForm: { display: 'flex', gap: '8px' },
  memberInput: {
    flex: 1,
    padding: '10px 12px',
    border: '1px solid var(--pd-border)',
    borderRadius: '8px',
    fontSize: '14px',
    outline: 'none'
  }
}
