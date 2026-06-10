export default function LoadingSpinner({ message = '불러오는 중...' }) {
  return (
    <div className="pd-spinner">
      <div className="pd-spinner__mark" />
      <p className="pd-spinner__message">{message}</p>
    </div>
  )
}
