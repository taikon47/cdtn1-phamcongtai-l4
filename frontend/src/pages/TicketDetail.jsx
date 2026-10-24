export default function TicketDetail({ ticket, onReceive }) {
  return (
    <section className="panel">
      <div className="panel-title">
        <div>
          <span className="eyebrow">MÀN HÌNH 02</span>
          <h2>Chi tiết phiếu</h2>
        </div>
      </div>

      {!ticket ? (
        <div className="state empty">
          Chọn một phiếu ở danh sách để xem chi tiết.
        </div>
      ) : (
        <>
          <div className="detail-grid">
            <div>
              <label>Mã phiếu</label>
              <strong>{ticket.ticketCode}</strong>
            </div>
            <div>
              <label>Trạng thái</label>
              <strong>{ticket.status}</strong>
            </div>
            <div>
              <label>Khách hàng</label>
              <strong>{ticket.customerName}</strong>
            </div>
            <div>
              <label>Thiết bị</label>
              <strong>{ticket.deviceName}</strong>
            </div>
            <div>
              <label>Kỹ năng yêu cầu</label>
              <strong>{ticket.requiredSkill}</strong>
            </div>
            <div>
              <label>Khu vực dịch vụ</label>
              <strong>{ticket.serviceArea}</strong>
            </div>
          </div>

          <div className="description">
            <label>Mô tả sự cố</label>
            <p>{ticket.issueDescription || "Không có mô tả."}</p>
          </div>

          <button className="primary" onClick={onReceive}>
            Tiếp nhận & phân công
          </button>
        </>
      )}
    </section>
  );
}
