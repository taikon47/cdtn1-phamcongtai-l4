import { useEffect, useState } from "react";
import DataState from "../components/DataState";
import { api } from "../api/client";

export default function TicketReceive({ ticket, onCompleted }) {
  const [technicians, setTechnicians] = useState([]);
  const [technicianId, setTechnicianId] = useState("");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  useEffect(() => {
    let active = true;

    async function load() {
      setLoading(true);
      setError("");

      try {
        const data = await api.technicians();
        if (active) setTechnicians(data || []);
      } catch (e) {
        if (active) setError(e.message);
      } finally {
        if (active) setLoading(false);
      }
    }

    load();
    return () => {
      active = false;
    };
  }, []);

  async function submit(e) {
    e.preventDefault();

    if (!ticket) {
      setError("Vui lòng chọn phiếu bảo hành trước.");
      return;
    }

    if (!technicianId) {
      setError("Vui lòng chọn kỹ thuật viên.");
      return;
    }

    setSaving(true);
    setError("");
    setMessage("");

    try {
      await api.assign(ticket.ticketId, technicianId);
      setMessage(`Đã phân công thành công phiếu ${ticket.ticketCode}.`);
      setTechnicianId("");
      await onCompleted();
    } catch (e) {
      setError(e.message);
    } finally {
      setSaving(false);
    }
  }

  return (
    <section className="panel">
      <div className="panel-title">
        <div>
          <span className="eyebrow">MÀN HÌNH 03</span>
          <h2>Tiếp nhận & phân công</h2>
        </div>
      </div>

      {!ticket ? (
        <div className="state empty">
          Chưa có phiếu được chọn để tiếp nhận.
        </div>
      ) : (
        <DataState
          loading={loading}
          error={error && !saving ? error : ""}
          empty={!loading && !error && technicians.length === 0}
          emptyMessage="Không có kỹ thuật viên."
        >
          <form onSubmit={submit}>
            <div className="selected-ticket">
              <span>Phiếu đang xử lý</span>
              <strong>
                {ticket.ticketCode} — {ticket.deviceName}
              </strong>
              <small>
                Yêu cầu: {ticket.requiredSkill} · {ticket.serviceArea}
              </small>
            </div>

            <label htmlFor="technician">Kỹ thuật viên</label>
            <select
              id="technician"
              value={technicianId}
              onChange={(e) => {
                setTechnicianId(e.target.value);
                setError("");
              }}
            >
              <option value="">-- Chọn kỹ thuật viên --</option>
              {technicians.map((tech) => (
                <option
                  key={tech.technicianId}
                  value={tech.technicianId}
                >
                  {tech.fullName} — {tech.skill} — {tech.serviceArea}
                </option>
              ))}
            </select>

            {error && <div className="inline-error">{error}</div>}
            {message && <div className="success">{message}</div>}

            <button className="primary" type="submit" disabled={saving}>
              {saving ? "Đang phân công..." : "Xác nhận phân công"}
            </button>
          </form>
        </DataState>
      )}
    </section>
  );
}
