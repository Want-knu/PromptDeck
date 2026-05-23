export default function PageHeader({ title, description, eyebrow, actionLabel, onAction, actionDisabled }) {
  return (
    <div className="pd-page-header">
      <div className="pd-page-header__copy">
        {eyebrow && <p className="pd-page-header__eyebrow">{eyebrow}</p>}
        <h2 className="pd-page-header__title">{title}</h2>
        {description && <p className="pd-page-header__description">{description}</p>}
      </div>
      {actionLabel && (
        <button className="pd-btn pd-btn--primary" onClick={onAction} disabled={actionDisabled}>
          {actionLabel}
        </button>
      )}
    </div>
  )
}
