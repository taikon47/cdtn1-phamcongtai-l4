const BASE = "/api/v1";

async function request(path, options = {}) {
  let response;

  try {
    response = await fetch(BASE + path, {
      ...options,
      headers: {
        Accept: "application/json",
        ...(options.body ? { "Content-Type": "application/json" } : {}),
        ...(options.headers || {}),
      },
    });
  } catch {
    throw new Error("Không kết nối được Spring Boot API.");
  }

  if (response.status === 204) return null;

  const type = response.headers.get("content-type") || "";
  const data = type.includes("application/json")
    ? await response.json().catch(() => null)
    : await response.text().catch(() => "");

  if (!response.ok) {
    const message =
      data && typeof data === "object"
        ? data.detail || data.message || data.error || data.title
        : data;

    throw new Error(message || `Yêu cầu thất bại (HTTP ${response.status})`);
  }

  return data;
}

export const api = {
  tickets: (status = "UNASSIGNED") =>
    request(`/warranty-tickets?status=${encodeURIComponent(status)}`),

  technicians: () => request("/technicians"),

  technician: (id) =>
    request(`/technicians/${encodeURIComponent(id)}`),

  schedule: (id) =>
    request(`/technicians/${encodeURIComponent(id)}/schedule`),

  appointments: () => request("/appointments"),

  assign: (ticketId, technicianId) =>
    request(`/warranty-tickets/${encodeURIComponent(ticketId)}/assignments`, {
      method: "POST",
      body: JSON.stringify({ technicianId: Number(technicianId) }),
    }),

  checkConflict: (body) =>
    request("/appointments/check-conflict", {
      method: "POST",
      body: JSON.stringify(body),
    }),

  createAppointment: (body) =>
    request("/appointments", {
      method: "POST",
      body: JSON.stringify(body),
    }),

  confirmAppointment: (id) =>
    request(`/appointments/${encodeURIComponent(id)}/confirm`, {
      method: "PATCH",
    }),

  cancelAppointment: (id) =>
    request(`/appointments/${encodeURIComponent(id)}/cancel`, {
      method: "PATCH",
    }),
};
