export default function FormField({ label, hint, children }) {
  return (
    <div className="pd-field">
      <div className="pd-field__label-row">
        <label className="pd-field__label">{label}</label>
        {hint && <span className="pd-field__hint">{hint}</span>}
      </div>
      {children}
    </div>
  )
}
