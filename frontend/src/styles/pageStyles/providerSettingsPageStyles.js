export const providerSettingsPageStyles = {
  main: { maxWidth: '1040px', margin: '34px auto', padding: '0 24px 56px' },
  form: { background: 'var(--pd-surface-raised)', padding: '28px', borderRadius: '8px', boxShadow: 'var(--pd-shadow-sm)', border: '1px solid var(--pd-border-soft)', backdropFilter: 'blur(18px)', display: 'flex', flexDirection: 'column', gap: '14px' },
  input: { padding: '11px 12px', border: '1px solid var(--pd-border)', borderRadius: '8px', fontSize: '14px', outline: 'none' },
  inputLocked: { background: 'var(--pd-surface-muted)', color: 'var(--pd-muted)', cursor: 'not-allowed', borderColor: 'var(--pd-border-soft)' },
  textarea: { padding: '11px 12px', border: '1px solid var(--pd-border)', borderRadius: '8px', fontSize: '13px', fontFamily: 'monospace', outline: 'none', resize: 'vertical' },
  formActions: { display: 'flex', gap: '10px', marginTop: '8px' },
  error: { color: 'var(--pd-danger)', fontSize: '13px' },
  confirmText: { color: 'var(--pd-text)', fontSize: '14px', lineHeight: 1.6 },
  list: { display: 'flex', flexDirection: 'column', gap: '14px' },
  card: { background: 'var(--pd-surface-raised)', padding: '20px 22px', borderRadius: '8px', boxShadow: 'var(--pd-shadow-sm)', border: '1px solid var(--pd-border-soft)', backdropFilter: 'blur(18px)', display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '14px', flexWrap: 'wrap' },
  cardLeft: { display: 'flex', alignItems: 'flex-start', gap: '14px' },
  cardTitle: { fontSize: '16px', fontWeight: 900, marginBottom: '4px', color: 'var(--pd-heading)' },
  cardSub: { fontSize: '12px', color: 'var(--pd-muted)', marginBottom: '2px' },
  cardActions: { display: 'flex', gap: '8px' }
}
