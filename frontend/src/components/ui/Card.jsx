export default function Card({ children, onClick, hoverable = false, style, className = '' }) {
  const classes = [
    'pd-card',
    hoverable ? 'pd-card--hoverable' : '',
    onClick ? 'pd-card--clickable' : '',
    className
  ].filter(Boolean).join(' ')

  return (
    <div
      className={classes}
      style={style}
      onClick={onClick}
      role={onClick ? 'button' : undefined}
      tabIndex={onClick ? 0 : undefined}
      onKeyDown={onClick ? (e) => { if (e.key === 'Enter') onClick(e) } : undefined}
    >
      {children}
    </div>
  )
}
