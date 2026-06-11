import { useEffect, useRef } from 'react'

export default function Modal({ isOpen, onClose, title, children, footer, width = '640px' }) {
  const overlayRef = useRef(null)

  useEffect(() => {
    if (!isOpen) return
    const handler = (e) => { if (e.key === 'Escape') onClose() }
    document.addEventListener('keydown', handler)
    return () => document.removeEventListener('keydown', handler)
  }, [isOpen, onClose])

  if (!isOpen) return null

  return (
    <div
      ref={overlayRef}
      className="pd-modal"
      onClick={(e) => { if (e.target === overlayRef.current) onClose() }}
    >
      <div className="pd-modal__panel" style={{ maxWidth: width }}>
        <div className="pd-modal__header">
          <h3 className="pd-modal__title">{title}</h3>
          <button className="pd-modal__close" onClick={onClose} aria-label="닫기">✕</button>
        </div>
        <div className="pd-modal__body">{children}</div>
        {footer && <div className="pd-modal__footer">{footer}</div>}
      </div>
    </div>
  )
}
