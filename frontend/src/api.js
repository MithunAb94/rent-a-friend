const API_BASE = import.meta.env.VITE_API_BASE || "/api";

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(options.token ? { Authorization: `Bearer ${options.token}` } : {})
    },
    ...options,
    body: options.body ? JSON.stringify(options.body) : undefined
  });

  const payload = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw new Error(payload.message || "Request failed");
  }

  return payload;
}

export const api = {
  getCategories: () => request("/meta/categories"),
  getLegalPolicy: () => request("/meta/legal-policy"),
  getListeners: () => request("/listeners"),
  getDashboard: (token) => request("/dashboard", { token }),
  register: (body) => request("/auth/register", { method: "POST", body }),
  login: (body) => request("/auth/login", { method: "POST", body }),
  createBooking: (token, body) => request("/bookings", { method: "POST", token, body }),
  updateBookingStatus: (token, bookingId, status) =>
    request(`/bookings/${bookingId}/status`, {
      method: "PATCH",
      token,
      body: { status }
    })
};

