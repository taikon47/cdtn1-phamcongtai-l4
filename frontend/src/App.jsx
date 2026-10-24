import { useCallback, useEffect, useState } from "react";
import { api } from "./api/client";
import TicketList from "./pages/TicketList";
import TicketDetail from "./pages/TicketDetail";
import TicketReceive from "./pages/TicketReceive";
import "./App.css";

function App() {
  const [tickets, setTickets] = useState([]);
  const [selectedTicket, setSelectedTicket] = useState(null);
  const [screen, setScreen] = useState("list");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadTickets = useCallback(async () => {
    try {
      const data = await api.tickets("UNASSIGNED");
      setTickets(data || []);

      setSelectedTicket((current) => {
        if (!current) return null;
        return (data || []).find(
          (item) => item.ticketId === current.ticketId
        ) || null;
      });

      setError("");
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    // Initial synchronization with the Spring Boot API.
    // oxlint-disable-next-line react/set-state-in-effect
    loadTickets();
  }, [loadTickets]);

  function selectTicket(ticket) {
    setSelectedTicket(ticket);
    setScreen("detail");
  }

  async function completed() {
    await loadTickets();
  }

  return (
    <div className="app-shell">
      <header>
        <div>
          <span className="brand">MEKONG MOBILE</span>
          <h1>Smart CRM · Warranty Service</h1>
          <p>Quản lý tiếp nhận và phân công bảo hành</p>
        </div>
        <div className="api-status">
          <span></span>
          Spring Boot API · :8081
        </div>
      </header>

      <nav>
        <button
          className={screen === "list" ? "active" : ""}
          onClick={() => setScreen("list")}
        >
          1. Danh sách
        </button>

        <button
          className={screen === "detail" ? "active" : ""}
          onClick={() => setScreen("detail")}
        >
          2. Chi tiết
        </button>

        <button
          className={screen === "receive" ? "active" : ""}
          onClick={() => setScreen("receive")}
        >
          3. Tiếp nhận
        </button>
      </nav>

      <main>
        {screen === "list" && (
          <TicketList
            tickets={tickets}
            loading={loading}
            error={error}
            selectedTicket={selectedTicket}
            onSelect={selectTicket}
            onReload={loadTickets}
          />
        )}

        {screen === "detail" && (
          <TicketDetail
            ticket={selectedTicket}
            onReceive={() => setScreen("receive")}
          />
        )}

        {screen === "receive" && (
          <TicketReceive
            ticket={selectedTicket}
            onCompleted={completed}
          />
        )}
      </main>

      <footer>CDTN · Software Engineering · Sprint 2</footer>
    </div>
  );
}

export default App;

