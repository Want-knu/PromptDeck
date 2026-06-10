export default function Badge({ children, variant = 'default', style, className = '' }) {
  return (
    <span className={`pd-badge pd-badge--${variant} ${className}`.trim()} style={style}>
      {children}
    </span>
  )
}
