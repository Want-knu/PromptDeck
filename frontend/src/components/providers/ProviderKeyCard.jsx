import { Badge, Button, Card } from '../ui'

export default function ProviderKeyCard({ providerKey, onEdit, onDelete, styles }) {
  return (
    <Card className="pd-stagger-1" style={styles.keyCard}>
      <div style={styles.keySummary}>
        <Badge>{providerKey.providerType}</Badge>
        <span style={styles.keyName}>{providerKey.displayName}</span>
      </div>
      <div style={styles.keyMeta}>
        <code style={styles.maskedKey}>{providerKey.maskedApiKey}</code>
        <Button variant="secondary" size="sm" onClick={() => onEdit(providerKey)}>수정</Button>
        <Button variant="danger" size="sm" onClick={() => onDelete(providerKey)}>삭제</Button>
      </div>
    </Card>
  )
}
