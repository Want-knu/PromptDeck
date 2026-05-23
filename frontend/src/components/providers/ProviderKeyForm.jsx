import { Button } from '../ui'

export default function ProviderKeyForm({
  form,
  providerTypes,
  editTarget,
  saving,
  error,
  onChange,
  onSubmit,
  styles
}) {
  return (
    <form className="pd-card pd-stagger-1" onSubmit={onSubmit} style={styles.form}>
      <h3 style={styles.formTitle}>{editTarget ? 'API Key 수정' : '새 API Key 등록'}</h3>
      <label style={styles.label}>Provider</label>
      <select
        style={styles.input}
        value={form.providerType}
        onChange={e => onChange({ providerType: e.target.value })}
      >
        {providerTypes.map(type => <option key={type} value={type}>{type}</option>)}
      </select>
      <label style={styles.label}>표시 이름</label>
      <input
        style={styles.input}
        type="text"
        placeholder="My OpenAI Key"
        value={form.displayName}
        onChange={e => onChange({ displayName: e.target.value })}
        required
      />
      <label style={styles.label}>API Key</label>
      <input
        style={styles.input}
        type="password"
        placeholder={editTarget ? '새 API Key 입력' : 'sk-...'}
        value={form.apiKey}
        onChange={e => onChange({ apiKey: e.target.value })}
        required
      />
      {error && <p style={styles.error}>{error}</p>}
      <Button type="submit" loading={saving}>{editTarget ? '수정' : '저장'}</Button>
    </form>
  )
}
