import { Badge, Button, Card } from '../ui'

export default function ProviderSettingCard({ setting, onEdit, onDelete, styles }) {
  return (
    <Card className="pd-stagger-1" style={styles.card}>
      <div style={styles.cardLeft}>
        <Badge>{setting.providerType}</Badge>
        <div>
          <p style={styles.cardTitle}>{setting.displayName}</p>
          <p style={styles.cardSub}>{setting.model}</p>
          {setting.providerType === 'CUSTOM' && (
            <p style={styles.cardSub}>{setting.method} · {setting.endpoint}</p>
          )}
        </div>
      </div>
      <div style={styles.cardActions}>
        <Button variant="outline" size="sm" onClick={() => onEdit(setting)}>수정</Button>
        <Button variant="danger" size="sm" onClick={() => onDelete(setting)}>삭제</Button>
      </div>
    </Card>
  )
}
