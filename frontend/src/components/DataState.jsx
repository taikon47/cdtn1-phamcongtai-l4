export default function DataState({
  loading,
  error,
  empty,
  emptyMessage = "Không có dữ liệu.",
  children,
}) {
  if (loading) {
    return <div className="state loading">Đang tải dữ liệu...</div>;
  }

  if (error) {
    return (
      <div className="state error">
        <strong>Không thể tải dữ liệu</strong>
        <span>{error}</span>
      </div>
    );
  }

  if (empty) {
    return <div className="state empty">{emptyMessage}</div>;
  }

  return children;
}
