export const providersPageStyles = {
  main: { maxWidth: '980px', margin: '34px auto', padding: '0 24px 56px' },
  keySummary: { display: 'flex', alignItems: 'center', gap: '10px', flexWrap: 'wrap' },
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
  confirmText: { color: 'var(--pd-text)', fontSize: '14px', lineHeight: 1.6 },
  list: { display: 'flex', flexDirection: 'column', gap: '14px' },
  keyCard: {
    background: 'var(--pd-surface-raised)',
    padding: '18px 20px',
    borderRadius: '8px',
    boxShadow: 'var(--pd-shadow-sm)',
    border: '1px solid var(--pd-border-soft)',
    backdropFilter: 'blur(18px)',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    flexWrap: 'wrap',
    gap: '10px'
  },
  keyName: { fontSize: '15px', fontWeight: 800, color: 'var(--pd-heading)' },
  keyMeta: { display: 'flex', alignItems: 'center', gap: '10px', flexWrap: 'wrap' },
  maskedKey: { fontSize: '13px', color: 'var(--pd-muted)', background: 'var(--pd-surface-muted)', padding: '6px 10px', borderRadius: '8px', border: '1px solid var(--pd-border-soft)' }
}
