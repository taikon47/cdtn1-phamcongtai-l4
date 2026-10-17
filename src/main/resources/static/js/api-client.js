
/* Centralized API client: all HTTP requests are routed through this module. */
(() => {
  "use strict";

  const BASE = "/api/v1";

  async function request(path, options = {}) {
    const init = {
      method: options.method || "GET",
      headers: {
        "Accept": "application/json",
        ...(options.body !== undefined
          ? { "Content-Type": "application/json" }
          : {}),
        ...(options.headers || {})
      },
      signal: options.signal
    };

    if (options.body !== undefined) {
      init.body = JSON.stringify(options.body);
    }

    let response;

    try {
      response = await fetch(BASE + path, init);
    } catch (e) {
      const error = new Error(
        "Không kết nối được máy chủ. Hãy kiểm tra ứng dụng Spring Boot rồi thử lại."
      );
      error.kind = "network";
      throw error;
    }

    if (response.status === 204) {
      return null;
    }

    const type = response.headers.get("content-type") || "";

    const data = type.includes("application/json")
      ? await response.json().catch(() => null)
      : await response.text().catch(() => "");

    if (!response.ok) {
      const message =
        data && typeof data === "object"
          ? (data.detail || data.message || data.error || data.title)
          : data;

      const error = new Error(
        message || `Yêu cầu thất bại (HTTP ${response.status})`
      );

      error.status = response.status;
      error.data = data;
      throw error;
    }

    return data;
  }

  window.ApiClient = Object.freeze({
    tickets: status =>
      request(
        "/warranty-tickets" +
        (status && status !== "ALL"
          ? "?status=" + encodeURIComponent(status)
          : "")
      ),

    technicians: () => request("/technicians"),

    schedule: id =>
      request(`/technicians/${encodeURIComponent(id)}/schedule`),

    appointments: () => request("/appointments"),

    assign: (ticketId, technicianId) =>
      request(
        `/warranty-tickets/${encodeURIComponent(ticketId)}/assignments`,
        {
          method: "POST",
          body: { technicianId: Number(technicianId) }
        }
      ),

    checkConflict: body =>
      request("/appointments/check-conflict", {
        method: "POST",
        body
      }),

    createAppointment: body =>
      request("/appointments", {
        method: "POST",
        body
      }),

    confirmAppointment: id =>
      request(
        `/appointments/${encodeURIComponent(id)}/confirm`,
        { method: "PATCH" }
      ),

    // API9: Hủy lịch hẹn
    cancelAppointment: id =>
      request(
        `/appointments/${encodeURIComponent(id)}/cancel`,
        { method: "PATCH" }
      )
  });
})();
