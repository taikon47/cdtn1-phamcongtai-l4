import DataState from "../components/DataState";

export default function TicketList({
  tickets,
  loading,
  error,
  selectedTicket,
  onSelect,
  onReload,
}) {
  return (
    <section className="panel">
      <div className="panel-title">
        <div>
          <span className="eyebrow">MÀN HÌNH 01</span>
          <h2>Danh sách phiếu bảo hành</h2>
        </div>
        <button className="secondary" onClick={onReload}>
          Tải lại
        </button>
      </div>

      <DataState
        loading={loading}
        error={error}
        empty={!loading && !error && tickets.length === 0}
        emptyMessage="Không có phiếu UNASSIGNED."
      >
        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Mã phiếu</th>
                <th>Khách hàng</th>
                <th>Thiết bị</th>
                <th>Kỹ năng</th>
                <th>Khu vực</th>
                <th>Trạng thái</th>
              </tr>
            </thead>
            <tbody>
              {tickets.map((ticket) => (
                <tr
                  key={ticket.ticketId}
                  className={
                    selectedTicket?.ticketId === ticket.ticketId
                      ? "selected"
                      : ""
                  }
                  onClick={() => onSelect(ticket)}
                >
                  <td><strong>{ticket.ticketCode}</strong></td>
                  <td>{ticket.customerName}</td>
                  <td>{ticket.deviceName}</td>
                  <td>{ticket.requiredSkill}</td>
                  <td>{ticket.serviceArea}</td>
                  <td>
                    <span className="badge">{ticket.status}</span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </DataState>
    </section>
  );
}
