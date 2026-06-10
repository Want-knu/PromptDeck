import { Button } from '../ui'

export default function OrganizationCreateForm({ name, creating, error, onNameChange, onSubmit, styles }) {
  return (
    <form className="pd-card pd-stagger-1" onSubmit={onSubmit} style={styles.form}>
      <h3 style={styles.formTitle}>새 조직 생성</h3>
      <label style={styles.label}>조직 이름</label>
      <input
        style={styles.input}
        type="text"
        placeholder="조직 이름을 입력하세요"
        value={name}
        onChange={e => onNameChange(e.target.value)}
        required
      />
      {error && <p style={styles.error}>{error}</p>}
      <Button type="submit" loading={creating}>생성</Button>
    </form>
  )
}
