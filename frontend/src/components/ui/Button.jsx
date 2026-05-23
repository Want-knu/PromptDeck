export default function Button({ children, variant = 'primary', onClick, disabled, loading, type = 'button', style, size = 'md', className = '' }) {
  const classes = [
    'pd-btn',
    `pd-btn--${variant}`,
    size !== 'md' ? `pd-btn--${size}` : '',
    className
  ].filter(Boolean).join(' ')

  return (
    <button
      className={classes}
      type={type}
      style={style}
      onClick={onClick}
      disabled={disabled || loading}
    >
      {loading && <span className="pd-btn__spinner" />}
      {children}
    </button>
  )
}
