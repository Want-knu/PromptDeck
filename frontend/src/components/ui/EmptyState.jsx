export default function EmptyState({ message = '데이터가 없습니다.', icon }) {
  return (
    <div className="pd-empty">
      {icon && <div className="pd-empty__icon">{icon}</div>}
      <p className="pd-empty__message">{message}</p>
    </div>
  )
}
