
(() => {
  "use strict";

  const $ = s => document.querySelector(s);
  const $$ = s => [...document.querySelectorAll(s)];

  const state = {
    tickets: [],
    technicians: [],
    appointments: [],
    ticketStatus: "UNASSIGNED",
    assignTicket: null,
    toastTimer: null
  };

  const today = new Date();
  today.setMinutes(today.getMinutes() - today.getTimezoneOffset());

  $("#today-chip").textContent = new Intl.DateTimeFormat("vi-VN", {
    weekday: "short",
    day: "2-digit",
    month: "2-digit",
    year: "numeric"
  }).format(new Date());

  $("#appointment-date").min = today.toISOString().slice(0, 10);
  $("#appointment-date").value = today.toISOString().slice(0, 10);

  function escapeHtml(v) {
    return String(v ?? "").replace(/[&<>"']/g, c => ({
      "&": "&amp;",
      "<": "&lt;",
      ">": "&gt;",
      '"': "&quot;",
      "'": "&#39;"
    }[c]));
  }

  function idOf(x) {
    return x.appointmentId ?? x.ticketId ?? x.technicianId ?? x.id ?? "—";
  }

  function statusPill(value) {
    const s = String(value || "").toUpperCase();

    if (["CONFIRMED", "ASSIGNED", "AVAILABLE", "COMPLETED"].includes(s)) {
      return `<span class="pill success">${escapeHtml(statusLabel(s))}</span>`;
    }

    if (["PENDING", "UNASSIGNED"].includes(s)) {
      return `<span class="pill pending">${escapeHtml(statusLabel(s))}</span>`;
    }

    if (["BUSY", "CANCELLED", "UNAVAILABLE"].includes(s)) {
      return `<span class="pill danger">${escapeHtml(statusLabel(s))}</span>`;
    }

    return `<span class="pill neutral">${escapeHtml(statusLabel(s) || "Chưa rõ")}</span>`;
  }

  function statusLabel(s) {
    return ({
      UNASSIGNED: "Chờ phân công",
      ASSIGNED: "Đã phân công",
      PENDING: "Chờ xác nhận",
      CONFIRMED: "Đã xác nhận",
      AVAILABLE: "Sẵn sàng",
      BUSY: "Đang bận",
      UNAVAILABLE: "Không sẵn sàng",
      CANCELLED: "Đã hủy",
      COMPLETED: "Hoàn tất"
    })[s] || s;
  }

  function showToast(message) {
    const el = $("#toast");
    el.textContent = message;
    el.classList.add("show");
    clearTimeout(state.toastTimer);
    state.toastTimer = setTimeout(() => el.classList.remove("show"), 3600);
  }

  function showGlobalError(message) {
    const el = $("#global-alert");
    el.textContent = message;
    el.className = "alert error";
  }

  function clearGlobalError() {
    $("#global-alert").className = "alert hidden";
  }

  function setApiStatus(ok) {
    $("#api-status").textContent = ok ? "API đã kết nối" : "API chưa kết nối";
    $(".status-dot").style.background = ok ? "#43d7a0" : "#f16d7b";
  }

  function loading() {
    return '<div class="state loading"><span class="spinner"></span>Đang tải dữ liệu…</div>';
  }

  function empty(message) {
    return `<div class="state empty">${escapeHtml(message)}</div>`;
  }

  function errorState(message, retry) {
    return `<div class="state error">
      Không thể tải dữ liệu.<br>
      <small>${escapeHtml(message)}</small>
      <button class="btn btn-secondary retry-btn">Thử lại</button>
    </div>`;
  }

  function bindRetry(container, fn) {
    const b = container.querySelector(".retry-btn");
    if (b) b.addEventListener("click", fn);
  }

  function formatDate(v) {
    if (!v) return "—";

    const d = new Date(v);

    return Number.isNaN(d.getTime())
      ? String(v).replace("T", " ")
      : new Intl.DateTimeFormat("vi-VN", {
          day: "2-digit",
          month: "2-digit",
          year: "numeric",
          hour: "2-digit",
          minute: "2-digit"
        }).format(d);
  }

  function dateParts(v) {
    if (!v) return { day: "—", month: "" };

    const d = new Date(v);

    if (Number.isNaN(d.getTime())) {
      return {
        day: String(v).slice(8, 10) || "—",
        month: String(v).slice(5, 7) || ""
      };
    }

    return {
      day: String(d.getDate()).padStart(2, "0"),
      month: new Intl.DateTimeFormat("vi-VN", {
        month: "short"
      }).format(d)
    };
  }

  // =========================
  // PHIẾU BẢO HÀNH
  // =========================

  function renderTicketRows(tickets, target, compact = false) {
    if (!tickets.length) {
      target.innerHTML = empty("Chưa có phiếu phù hợp với bộ lọc.");
      return;
    }

    target.innerHTML = `<table>
      <thead>
        <tr>
          <th>Mã phiếu</th>
          <th>Khách hàng / Thiết bị</th>
          <th>Tay nghề</th>
          <th>Khu vực</th>
          <th>Trạng thái</th>
          ${compact ? "" : "<th>Thao tác</th>"}
        </tr>
      </thead>
      <tbody>
        ${tickets.map(t => `<tr>
          <td>
            <div class="primary-cell">${escapeHtml(t.ticketCode || ("PT-" + idOf(t)))}</div>
            <div class="secondary-cell">#${escapeHtml(idOf(t))}</div>
          </td>
          <td>
            <div class="primary-cell">${escapeHtml(t.customerName || "Chưa có tên")}</div>
            <div class="secondary-cell">${escapeHtml(t.deviceName || "Thiết bị")}</div>
          </td>
          <td>${escapeHtml(t.requiredSkill || "—")}</td>
          <td>${escapeHtml(t.serviceArea || "—")}</td>
          <td>${statusPill(t.status)}</td>
          ${compact ? "" : `<td>
            <button class="row-action assign-action"
              data-id="${escapeHtml(idOf(t))}"
              ${String(t.status).toUpperCase() !== "UNASSIGNED" ? "disabled" : ""}>
              Phân công
            </button>
          </td>`}
        </tr>`).join("")}
      </tbody>
    </table>`;

    target.querySelectorAll(".assign-action").forEach(b => {
      b.addEventListener("click", () => openAssign(Number(b.dataset.id)));
    });
  }

  async function loadTickets() {
    const target = $("#tickets-table");
    target.innerHTML = loading();
    $("#overview-tickets").innerHTML = loading();

    try {
      const status = state.ticketStatus;
      let data;

      if (status === "ALL") {
        const [u, a] = await Promise.all([
          ApiClient.tickets("UNASSIGNED"),
          ApiClient.tickets("ASSIGNED")
        ]);

        data = [...(u || []), ...(a || [])];
      } else {
        data = await ApiClient.tickets(status);
      }

      state.tickets = Array.isArray(data) ? data : [];

      const search = $("#ticket-search").value.trim().toLowerCase();

      const filtered = state.tickets.filter(t =>
        !search ||
        [
          t.ticketCode,
          t.customerName,
          t.deviceName,
          t.requiredSkill,
          t.serviceArea
        ].some(v => String(v || "").toLowerCase().includes(search))
      );

      renderTicketRows(filtered, target);

      // Luôn lấy phiếu ở cả hai trạng thái để hiển thị tổng quan.
      const overviewTickets = status === "ALL"
        ? state.tickets
        : (
            await Promise.all([
              ApiClient.tickets("UNASSIGNED"),
              ApiClient.tickets("ASSIGNED")
            ])
          ).flat();

      renderTicketRows(
        overviewTickets.slice(0, 5),
        $("#overview-tickets"),
        true
      );

      const unassignedForStats = status === "UNASSIGNED"
        ? state.tickets
        : status === "ALL"
          ? state.tickets.filter(
              t => String(t.status).toUpperCase() === "UNASSIGNED"
            )
          : await ApiClient.tickets("UNASSIGNED");

      const unassignedCount = Array.isArray(unassignedForStats)
        ? unassignedForStats.length
        : 0;

      $("#nav-ticket-count").textContent = unassignedCount;
      $("#stat-unassigned").textContent = unassignedCount;

      setApiStatus(true);
      clearGlobalError();
    } catch (e) {
      target.innerHTML = errorState(e.message, loadTickets);
      bindRetry(target, loadTickets);

      $("#overview-tickets").innerHTML = errorState(e.message, loadTickets);
      bindRetry($("#overview-tickets"), loadTickets);

      setApiStatus(false);
      showGlobalError(e.message);
    }
  }

  // =========================
  // KỸ THUẬT VIÊN
  // =========================

  function renderTechRows() {
    const target = $("#technicians-table");
    const q = $("#tech-search").value.trim().toLowerCase();
    const filter = $("#tech-availability").value;

    const rows = state.technicians.filter(t =>
      (
        !q ||
        [t.fullName, t.skill, t.serviceArea].some(
          v => String(v || "").toLowerCase().includes(q)
        )
      ) &&
      (
        filter === "ALL" ||
        String(t.availabilityStatus).toUpperCase() === filter
      )
    );

    if (!rows.length) {
      target.innerHTML = empty(
        state.technicians.length
          ? "Không có kỹ thuật viên phù hợp bộ lọc."
          : "Chưa có dữ liệu kỹ thuật viên."
      );
      return;
    }

    target.innerHTML = `<table>
      <thead>
        <tr>
          <th>Kỹ thuật viên</th>
          <th>Chuyên môn</th>
          <th>Khu vực</th>
          <th>Khối lượng</th>
          <th>Trạng thái</th>
          <th>Lịch</th>
        </tr>
      </thead>
      <tbody>
        ${rows.map(t => `<tr>
          <td>
            <div class="primary-cell">${escapeHtml(t.fullName || "Kỹ thuật viên")}</div>
            <div class="secondary-cell">KTV-${escapeHtml(idOf(t))}</div>
          </td>
          <td>${escapeHtml(t.skill || "—")}</td>
          <td>${escapeHtml(t.serviceArea || "—")}</td>
          <td>
            <strong>${escapeHtml(t.currentWorkload ?? 0)}</strong>
            <span class="secondary-cell">phiếu</span>
          </td>
          <td>${statusPill(t.availabilityStatus)}</td>
          <td>
            <button class="row-action schedule-action"
              data-id="${escapeHtml(idOf(t))}">
              Xem lịch
            </button>
          </td>
        </tr>`).join("")}
      </tbody>
    </table>`;

    target.querySelectorAll(".schedule-action").forEach(b => {
      b.addEventListener("click", () => viewSchedule(Number(b.dataset.id)));
    });
  }

  async function loadTechnicians() {
    const target = $("#technicians-table");
    target.innerHTML = loading();

    try {
      const data = await ApiClient.technicians();

      state.technicians = Array.isArray(data) ? data : [];

      renderTechRows();
      fillTechSelects();
      fillAppointmentTechFilter();

      $("#stat-techs").textContent = state.technicians.length;

      $("#stat-tech-sub").textContent =
        `${state.technicians.filter(
          t => String(t.availabilityStatus).toUpperCase() === "AVAILABLE"
        ).length} đang sẵn sàng`;

      setApiStatus(true);
      clearGlobalError();
    } catch (e) {
      target.innerHTML = errorState(e.message, loadTechnicians);
      bindRetry(target, loadTechnicians);

      setApiStatus(false);
      showGlobalError(e.message);
    }
  }

  function fillAppointmentTechFilter() {
    const select = $("#appointment-filter-tech");
    if (!select) return;
    const selected = select.value || "ALL";
    select.innerHTML = '<option value="ALL">Tất cả kỹ thuật viên</option>' +
      state.technicians.map(t =>
        `<option value="${escapeHtml(t.technicianId)}">KTV #${escapeHtml(t.technicianId)} · ${escapeHtml(t.fullName || t.name || "Kỹ thuật viên")}</option>`
      ).join("");
    if ([...select.options].some(option => option.value === selected)) {
      select.value = selected;
    }
  }

  function fillTechSelects() {
    const options = state.technicians.map(t =>
      `<option value="${escapeHtml(t.technicianId)}">
        ${escapeHtml(t.fullName)} · ${escapeHtml(t.skill)} ·
        ${escapeHtml(t.serviceArea)}
        (${escapeHtml(statusLabel(String(t.availabilityStatus || "").toUpperCase()))})
      </option>`
    ).join("");

    $("#assign-tech").innerHTML =
      '<option value="">Chọn kỹ thuật viên…</option>' + options;

    $("#appointment-tech").innerHTML =
      '<option value="">Chọn kỹ thuật viên…</option>' + options;
  }

  // =========================
  // DANH SÁCH PHIẾU TẠO LỊCH
  // =========================

  async function fillTicketSelect() {
    const select = $("#appointment-ticket");

    try {
      // Chỉ đưa phiếu đã phân công vào danh sách tạo lịch hẹn.
      const data = await ApiClient.tickets("ASSIGNED");
      const rows = Array.isArray(data) ? data : [];

      select.innerHTML =
        '<option value="">Chọn phiếu…</option>' +
        rows.map(t =>
          `<option value="${escapeHtml(t.ticketId)}">
            ${escapeHtml(t.ticketCode || ("PT-" + t.ticketId))}
            · ${escapeHtml(t.customerName || "Khách hàng")}
            · ${escapeHtml(t.deviceName || "Thiết bị")}
          </option>`
        ).join("");

      if (!rows.length) {
        select.innerHTML =
          '<option value="">Chưa có phiếu đã phân công</option>';
      }
    } catch (e) {
      select.innerHTML =
        '<option value="">Không tải được danh sách phiếu</option>';

      showToast("Lỗi tải phiếu bảo hành: " + e.message);
    }
  }

  // =========================
  // PHÂN CÔNG KỸ THUẬT VIÊN
  // =========================

  async function openAssign(ticketId) {
    const ticket = state.tickets.find(
      t => Number(t.ticketId) === ticketId
    );

    if (!ticket) {
      showToast("Không tìm thấy phiếu. Hãy tải lại danh sách.");
      return;
    }

    state.assignTicket = ticket;

    $("#assign-title").textContent =
      `Phân công ${ticket.ticketCode || ("#" + ticketId)}`;

    $("#assign-ticket-summary").textContent =
      `${ticket.customerName || "Khách hàng"} · ` +
      `${ticket.deviceName || "Thiết bị"} · ` +
      `Yêu cầu: ${ticket.requiredSkill || "—"} · ` +
      `Khu vực: ${ticket.serviceArea || "—"}`;

    const matched = state.technicians.filter(t =>
      String(t.availabilityStatus).toUpperCase() === "AVAILABLE" &&
      String(t.skill).toLowerCase() ===
        String(ticket.requiredSkill).toLowerCase() &&
      String(t.serviceArea).toLowerCase() ===
        String(ticket.serviceArea).toLowerCase()
    );

    $("#assign-tech").innerHTML =
      '<option value="">Chọn kỹ thuật viên phù hợp…</option>' +
      matched.map(t =>
        `<option value="${escapeHtml(t.technicianId)}">
          ${escapeHtml(t.fullName)} ·
          ${escapeHtml(t.skill)} ·
          ${escapeHtml(t.serviceArea)} ·
          tải ${escapeHtml(t.currentWorkload ?? 0)}
        </option>`
      ).join("");

    $("#assign-feedback").className = "feedback hidden";
    $("#assign-feedback").textContent = "";

    if (!matched.length) {
      $("#assign-feedback").className = "feedback bad";
      $("#assign-feedback").textContent =
        "Không có kỹ thuật viên nào thỏa mãn đồng thời tay nghề, khu vực và trạng thái sẵn sàng.";
    }

    $("#assign-dialog").showModal();
  }

  $("#cancel-assign").addEventListener("click", () => {
    $("#assign-dialog").close();
  });

  $("#confirm-assign").addEventListener("click", async () => {
    if (!state.assignTicket) return;

    const tech = $("#assign-tech").value;

    if (!tech) {
      $("#assign-feedback").className = "feedback bad";
      $("#assign-feedback").textContent =
        "Vui lòng chọn kỹ thuật viên phù hợp.";
      return;
    }

    const b = $("#confirm-assign");
    b.disabled = true;
    b.textContent = "Đang lưu…";

    try {
      await ApiClient.assign(state.assignTicket.ticketId, tech);

      $("#assign-dialog").close();
      showToast("Đã phân công kỹ thuật viên.");

      await Promise.all([
        loadTickets(),
        loadTechnicians()
      ]);

      await fillTicketSelect();
    } catch (e) {
      $("#assign-feedback").className = "feedback bad";
      $("#assign-feedback").textContent = e.message;
    } finally {
      b.disabled = false;
      b.textContent = "Xác nhận phân công";
    }
  });

  // =========================
  // LỊCH HẸN
  // =========================

  function filteredAppointments() {
    const date = $("#appointment-filter-date")?.value || "";
    const status = $("#appointment-filter-status")?.value || "ALL";
    const technician = $("#appointment-filter-tech")?.value || "ALL";

    return state.appointments.filter(a => {
      const start = String(a.startTime || "");
      const rowDate = start.includes("T") ? start.split("T")[0] : start.slice(0, 10);
      const rowStatus = String(a.status || "").toUpperCase();
      return (!date || rowDate === date)
        && (status === "ALL" || rowStatus === status)
        && (technician === "ALL" || String(a.technicianId) === technician);
    });
  }

  function refreshAppointmentTable() {
    renderAppointments($("#appointments-table"), filteredAppointments());
  }

  function renderAppointments(target, rows, compact = false) {
    if (!Array.isArray(rows) || rows.length === 0) {
      target.innerHTML = empty("Chưa có lịch hẹn được ghi nhận.");
      return;
    }

    // Hiển thị lịch hẹn rút gọn trên trang tổng quan.
    if (compact) {
      target.innerHTML = `<div class="mini-list">
        ${rows.slice(0, 5).map(a => {
          const d = dateParts(a.startTime);

          return `<div class="mini-row">
            <div class="mini-date">
              <strong>${escapeHtml(d.day)}</strong>
              <small>${escapeHtml(d.month)}</small>
            </div>
            <div class="mini-info">
              <strong>
                ${escapeHtml(
                  a.appointmentType === "RECEIVE"
                    ? "Nhận thiết bị"
                    : a.appointmentType === "RETURN"
                      ? "Trả thiết bị"
                      : a.appointmentType || "—"
                )}
                · Phiếu #${escapeHtml(a.ticketId)}
              </strong>
              <small>
                ${escapeHtml(formatDate(a.startTime))}
                · KTV #${escapeHtml(a.technicianId)}
              </small>
            </div>
            ${statusPill(a.status)}
          </div>`;
        }).join("")}
      </div>`;
      return;
    }

    // Bảng quản lý lịch hẹn đầy đủ.
    target.innerHTML = `<table>
      <thead>
        <tr>
          <th>Thời gian</th>
          <th>Phiếu</th>
          <th>Kỹ thuật viên</th>
          <th>Loại lịch</th>
          <th>Trạng thái</th>
          <th>Thao tác</th>
        </tr>
      </thead>
      <tbody>
        ${rows.map(a => {
          const status = String(a.status || "").toUpperCase();

          return `<tr>
            <td>
              <div class="primary-cell">
                ${escapeHtml(formatDate(a.startTime))}
              </div>
              <div class="secondary-cell">
                Đến ${escapeHtml(formatDate(a.endTime))}
              </div>
            </td>
            <td>#${escapeHtml(a.ticketId)}</td>
            <td>#${escapeHtml(a.technicianId)}</td>
            <td>${escapeHtml(
              a.appointmentType === "RECEIVE"
                ? "Nhận thiết bị"
                : a.appointmentType === "RETURN"
                  ? "Trả thiết bị"
                  : a.appointmentType || "—"
            )}</td>
            <td>${statusPill(status)}</td>
            <td class="appointment-actions">
              ${status === "PENDING"
                ? `<button type="button" class="row-action confirm-action" data-id="${escapeHtml(idOf(a))}">Xác nhận</button>`
                : ""}
              ${["PENDING", "CONFIRMED"].includes(status)
                ? `<button type="button" class="row-action cancel-action" data-id="${escapeHtml(idOf(a))}">Hủy lịch</button>`
                : ""}
              ${!["PENDING", "CONFIRMED"].includes(status) ? "—" : ""}
            </td>
          </tr>`;
        }).join("")}
      </tbody>
    </table>`;

    // Xử lý nút xác nhận lịch hẹn.
    target.querySelectorAll(".confirm-action").forEach(button => {
      button.addEventListener("click", async () => {
        const appointmentId = button.dataset.id;
        if (!appointmentId || button.disabled) return;

        const originalText = button.textContent.trim();
        button.disabled = true;
        button.textContent = "Đang xác nhận…";

        try {
          await ApiClient.confirmAppointment(appointmentId);
          showToast("Đã xác nhận lịch hẹn.");
          await loadAppointments();
        } catch (error) {
          showToast(error.message || "Không thể xác nhận lịch hẹn.");
          button.disabled = false;
          button.textContent = originalText;
        }
      });
    });

    target.querySelectorAll(".cancel-action").forEach(button => {
      button.addEventListener("click", async () => {
        const appointmentId = button.dataset.id;
        if (!appointmentId || button.disabled) return;
        if (!window.confirm("Bạn có chắc muốn hủy lịch hẹn này không?")) return;

        button.disabled = true;
        const originalText = button.textContent.trim();
        button.textContent = "Đang hủy…";

        try {
          await ApiClient.cancelAppointment(appointmentId);
          showToast("Đã hủy lịch hẹn.");
          await loadAppointments();
        } catch (error) {
          showToast(error.message || "Không thể hủy lịch hẹn.");
          button.disabled = false;
          button.textContent = originalText;
        }
      });
    });
  }

  async function loadAppointments() {
    const target = $("#appointments-table");

    target.innerHTML = loading();
    $("#overview-appointments").innerHTML = loading();

    try {
      const data = await ApiClient.appointments();

      state.appointments = Array.isArray(data) ? data : [];

      refreshAppointmentTable();
      renderAppointments(
        $("#overview-appointments"),
        state.appointments.filter(a => !["CANCELLED"].includes(String(a.status || "").toUpperCase())),
        true
      );

      fillAppointmentTechFilter();

      $("#stat-appointments").textContent =
        state.appointments.length;

      $("#stat-pending").textContent =
        state.appointments.filter(
          a => String(a.status).toUpperCase() === "PENDING"
        ).length;

      $("#stat-confirmed").textContent =
        state.appointments.filter(
          a => String(a.status).toUpperCase() === "CONFIRMED"
        ).length;

      const now = new Date();
      $("#stat-upcoming").textContent =
        state.appointments.filter(a =>
          !["CANCELLED", "COMPLETED"].includes(String(a.status || "").toUpperCase())
          && new Date(a.startTime) >= now
        ).length;

      setApiStatus(true);
      clearGlobalError();
    } catch (e) {
      target.innerHTML = errorState(e.message, loadAppointments);
      bindRetry(target, loadAppointments);

      $("#overview-appointments").innerHTML =
        errorState(e.message, loadAppointments);

      bindRetry($("#overview-appointments"), loadAppointments);

      setApiStatus(false);
      showGlobalError(e.message);
    }
  }

  // =========================
  // XEM LỊCH KỸ THUẬT VIÊN
  // =========================

  async function viewSchedule(id) {
    try {
      const data = await ApiClient.schedule(id);
      const rows = Array.isArray(data) ? data : [];

      const tech = state.technicians.find(
        t => Number(idOf(t)) === Number(id)
      );

      const previous = document.getElementById("schedule-modal");
      if (previous) previous.remove();

      const modal = document.createElement("div");
      modal.id = "schedule-modal";

      modal.innerHTML = `
        <div data-modal-backdrop style="
          position:fixed;
          inset:0;
          z-index:9999;
          background:rgba(15,29,54,.58);
          display:flex;
          align-items:center;
          justify-content:center;
          padding:20px;
          backdrop-filter:blur(4px)">

          <section role="dialog"
            aria-modal="true"
            aria-labelledby="schedule-modal-title"
            style="
              width:min(820px,100%);
              max-height:85vh;
              overflow:auto;
              background:#fff;
              border-radius:18px;
              box-shadow:0 24px 80px rgba(0,0,0,.22);
              color:#203451">

            <header style="
              padding:24px 26px;
              border-bottom:1px solid #edf0f6;
              display:flex;
              justify-content:space-between;
              align-items:center;
              gap:16px">

              <div>
                <div style="
                  font-size:11px;
                  letter-spacing:1.5px;
                  color:#647da6;
                  font-weight:700;
                  margin-bottom:8px">
                  SMART CRM / LỊCH LÀM VIỆC
                </div>

                <h2 id="schedule-modal-title" style="
                  font-size:23px;
                  margin:0 0 8px;
                  font-weight:750">
                  ${escapeHtml(tech ? tech.fullName : "Kỹ thuật viên")}
                </h2>

                <div style="font-size:13px;color:#71819a">
                  Mã kỹ thuật viên: KTV-${escapeHtml(id)}
                </div>
              </div>

              <button type="button"
                data-close-schedule
                aria-label="Đóng cửa sổ"
                style="
                  border:1px solid #e3e9f2;
                  background:#fff;
                  border-radius:10px;
                  width:40px;
                  height:40px;
                  font-size:24px;
                  color:#526782;
                  cursor:pointer;
                  flex-shrink:0">
                ×
              </button>
            </header>

            <div style="padding:24px 26px">
              <div style="
                display:flex;
                gap:12px;
                flex-wrap:wrap;
                margin-bottom:22px">

                <div style="
                  flex:1;
                  min-width:140px;
                  padding:16px;
                  background:#f4f7fd;
                  border-radius:12px">

                  <div style="
                    font-size:12px;
                    color:#72829c;
                    margin-bottom:7px">
                    Tổng lịch hẹn
                  </div>

                  <div style="font-size:27px;font-weight:750">
                    ${rows.length}
                  </div>
                </div>

                <div style="
                  flex:1;
                  min-width:140px;
                  padding:16px;
                  background:#eaf8f2;
                  border-radius:12px">

                  <div style="
                    font-size:12px;
                    color:#4e8874;
                    margin-bottom:7px">
                    Đã xác nhận
                  </div>

                  <div style="
                    font-size:27px;
                    font-weight:750;
                    color:#13845d">
                    ${rows.filter(
                      a => String(a.status).toUpperCase() === "CONFIRMED"
                    ).length}
                  </div>
                </div>
              </div>

              <h3 style="font-size:16px;margin:0 0 14px">
                Chi tiết lịch hẹn
              </h3>

              <div style="
                overflow-x:auto;
                border:1px solid #e9edf4;
                border-radius:12px">

                <table style="
                  width:100%;
                  border-collapse:collapse;
                  font-size:13px;
                  min-width:540px">

                  <thead style="background:#f6f8fc">
                    <tr>
                      <th style="padding:13px;text-align:left;color:#71819a">
                        THỜI GIAN
                      </th>
                      <th style="padding:13px;text-align:left;color:#71819a">
                        PHIẾU
                      </th>
                      <th style="padding:13px;text-align:left;color:#71819a">
                        LOẠI LỊCH
                      </th>
                      <th style="padding:13px;text-align:left;color:#71819a">
                        TRẠNG THÁI
                      </th>
                    </tr>
                  </thead>

                  <tbody>
                    ${rows.length ? rows.map(a => `
                      <tr style="border-top:1px solid #edf0f6">
                        <td style="padding:15px 13px;line-height:1.8">
                          <strong>${escapeHtml(formatDate(a.startTime))}</strong>
                          <div style="color:#7b8ba3">
                            Đến ${escapeHtml(formatDate(a.endTime))}
                          </div>
                        </td>

                        <td style="padding:15px 13px;font-weight:650">
                          #${escapeHtml(a.ticketId)}
                        </td>

                        <td style="padding:15px 13px">
                          ${escapeHtml(
                            a.appointmentType === "RECEIVE"
                              ? "Nhận thiết bị"
                              : a.appointmentType === "RETURN"
                                ? "Trả thiết bị"
                                : a.appointmentType || "—"
                          )}
                        </td>

                        <td style="padding:15px 13px">
                          ${statusPill(a.status)}
                        </td>
                      </tr>
                    `).join("") : `
                      <tr>
                        <td colspan="4" style="
                          padding:32px;
                          text-align:center;
                          color:#8492a8">
                          Kỹ thuật viên chưa có lịch hẹn.
                        </td>
                      </tr>
                    `}
                  </tbody>
                </table>
              </div>

              <div style="
                display:flex;
                justify-content:flex-end;
                margin-top:22px">

                <button type="button"
                  data-close-schedule
                  style="
                    background:#4169ed;
                    color:#fff;
                    border:0;
                    border-radius:10px;
                    padding:11px 22px;
                    font-weight:650;
                    cursor:pointer">
                  Đóng
                </button>
              </div>
            </div>
          </section>
        </div>`;

      document.body.appendChild(modal);

      const close = () => {
        modal.remove();
        document.removeEventListener("keydown", onKey);
      };

      const onKey = e => {
        if (e.key === "Escape") close();
      };

      modal.querySelectorAll("[data-close-schedule]").forEach(b => {
        b.addEventListener("click", close);
      });

      modal.querySelector("[data-modal-backdrop]").addEventListener(
        "click",
        e => {
          if (e.target === modal.querySelector("[data-modal-backdrop]")) {
            close();
          }
        }
      );

      document.addEventListener("keydown", onKey);
    } catch (e) {
      showToast(e.message);
    }
  }

  // =========================
  // TẠO LỊCH HẸN
  // =========================

  function appointmentPayload() {
    return {
      ticketId: Number($("#appointment-ticket").value),
      technicianId: Number($("#appointment-tech").value),
      appointmentType: $("#appointment-type").value,
      appointmentDate: $("#appointment-date").value,
      startTime: $("#appointment-start").value,
      endTime: $("#appointment-end").value
    };
  }

  function showAppointmentFeedback(message, type) {
    const el = $("#appointment-feedback");
    el.textContent = message;
    el.className = `feedback full ${type}`;
  }

  async function checkConflict() {
    const p = appointmentPayload();

    if (
      !p.ticketId ||
      !p.technicianId ||
      !p.appointmentDate ||
      !p.startTime ||
      !p.endTime
    ) {
      showAppointmentFeedback(
        "Vui lòng nhập đủ phiếu, kỹ thuật viên, ngày và khung giờ.",
        "bad"
      );
      return;
    }

    if (p.endTime <= p.startTime) {
      showAppointmentFeedback(
        "Giờ kết thúc phải sau giờ bắt đầu.",
        "bad"
      );
      return;
    }

    const b = $("#check-conflict");
    b.disabled = true;
    b.textContent = "Đang kiểm tra…";

    try {
      const r = await ApiClient.checkConflict(p);

      showAppointmentFeedback(
        r.message || (r.conflict ? "Có trùng lịch" : "Không trùng lịch"),
        r.conflict ? "bad" : "ok"
      );
    } catch (e) {
      showAppointmentFeedback(e.message, "bad");
    } finally {
      b.disabled = false;
      b.textContent = "Kiểm tra trùng lịch";
    }
  }

  $("#check-conflict").addEventListener("click", checkConflict);

  $("#appointment-form").addEventListener("submit", async e => {
    e.preventDefault();

    const p = appointmentPayload();

    if (!p.ticketId || !p.technicianId) {
      showAppointmentFeedback(
        "Vui lòng chọn phiếu và kỹ thuật viên.",
        "bad"
      );
      return;
    }

    if (p.endTime <= p.startTime) {
      showAppointmentFeedback(
        "Giờ kết thúc phải sau giờ bắt đầu.",
        "bad"
      );
      return;
    }

    const b = e.submitter;
    b.disabled = true;
    b.textContent = "Đang tạo…";

    try {
      const c = await ApiClient.checkConflict(p);

      if (c.conflict) {
        showAppointmentFeedback(
          c.message || "Khung giờ bị trùng.",
          "bad"
        );
        return;
      }

      await ApiClient.createAppointment(p);

      showAppointmentFeedback(
        "Tạo lịch hẹn thành công. Lịch đang chờ xác nhận.",
        "ok"
      );

      showToast("Đã tạo lịch hẹn.");
      await loadAppointments();
    } catch (err) {
      showAppointmentFeedback(err.message, "bad");
    } finally {
      b.disabled = false;
      b.textContent = "Tạo lịch hẹn";
    }
  });

  // =========================
  // CHUYỂN TRANG VÀ KHỞI ĐỘNG
  // =========================

  function showView(name) {
    $$(".view").forEach(v => {
      v.classList.toggle("active", v.id === `view-${name}`);
    });

    $$(".nav-item").forEach(b => {
      b.classList.toggle("active", b.dataset.view === name);
    });

    $("#page-title").textContent = ({
      overview: "Tổng quan điều phối",
      tickets: "Phiếu bảo hành",
      technicians: "Đội ngũ kỹ thuật viên",
      appointments: "Quản lý lịch hẹn"
    })[name] || "Smart CRM";

    if (name === "tickets") {
      loadTickets();
    }

    if (name === "technicians") {
      loadTechnicians();
    }

    if (name === "appointments") {
      Promise.all([
        loadAppointments(),
        loadTechnicians(),
        loadTickets()
      ]).then(fillTicketSelect);
    }
  }

  $$(".nav-item").forEach(b => {
    b.addEventListener("click", () => showView(b.dataset.view));
  });

  $$("[data-go]").forEach(b => {
    b.addEventListener("click", () => showView(b.dataset.go));
  });

  $("#ticket-status").addEventListener("change", () => {
    state.ticketStatus = $("#ticket-status").value;
    loadTickets().then(fillTicketSelect);
  });

  $("#ticket-search").addEventListener("input", () => {
    const q = $("#ticket-search").value.trim().toLowerCase();

    renderTicketRows(
      state.tickets.filter(t =>
        [
          t.ticketCode,
          t.customerName,
          t.deviceName,
          t.requiredSkill,
          t.serviceArea
        ].some(v => String(v || "").toLowerCase().includes(q))
      ),
      $("#tickets-table")
    );
  });

  $("#tech-search").addEventListener("input", renderTechRows);
  $("#tech-availability").addEventListener("change", renderTechRows);

  $("#reload-tickets").addEventListener("click", () => {
    loadTickets().then(fillTicketSelect);
  });

  $("#reload-techs").addEventListener("click", loadTechnicians);
  $("#reload-appointments").addEventListener("click", loadAppointments);
  $("#appointment-filter-date").addEventListener("change", refreshAppointmentTable);
  $("#appointment-filter-status").addEventListener("change", refreshAppointmentTable);
  $("#appointment-filter-tech").addEventListener("change", refreshAppointmentTable);
  $("#clear-appointment-filters").addEventListener("click", () => {
    $("#appointment-filter-date").value = "";
    $("#appointment-filter-status").value = "ALL";
    $("#appointment-filter-tech").value = "ALL";
    refreshAppointmentTable();
  });

  $("#refresh-all").addEventListener("click", () => {
    Promise.all([
      loadTickets(),
      loadTechnicians(),
      loadAppointments()
    ]).then(fillTicketSelect);
  });

  async function boot() {
    await Promise.all([
      loadTickets(),
      loadTechnicians(),
      loadAppointments()
    ]);

    fillTicketSelect();
  }

  boot();
})();
