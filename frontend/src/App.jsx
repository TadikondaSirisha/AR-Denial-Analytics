
import { useEffect, useState } from "react";
import axios from "axios";
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid,
  Tooltip, ResponsiveContainer
} from "recharts";
import {
  Activity, FileText, IndianRupee, AlertCircle,
  Search, ChevronLeft, ChevronRight, RefreshCw
} from "lucide-react";
import "./App.css";

const API_URL = "http://localhost:8080/api";

function formatCurrency(value) {
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
  }).format(value || 0);
}

function App() {
  const [payers, setPayers] = useState([]);
  const [reasons, setReasons] = useState([]);
  const [claims, setClaims] = useState([]);
  const [totalClaims, setTotalClaims] = useState(0);

  const [loading, setLoading] = useState(true);
  const [claimsLoading, setClaimsLoading] = useState(false);
  const [error, setError] = useState("");

  const [payer, setPayer] = useState("");
  const [reason, setReason] = useState("");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  const [filters, setFilters] = useState({});
  const [page, setPage] = useState(0);
  const pageSize = 10;

  useEffect(() => {
    async function loadAnalytics() {
      try {
        setLoading(true);
        const [payerResponse, reasonResponse] = await Promise.all([
          axios.get(`${API_URL}/analytics/payers`),
          axios.get(`${API_URL}/analytics/reasons`),
        ]);

        setPayers(payerResponse.data);
        setReasons(reasonResponse.data);
      } catch (err) {
        console.error(err);
        setError("Unable to load analytics. Please check the backend.");
      } finally {
        setLoading(false);
      }
    }

    loadAnalytics();
  }, []);

  useEffect(() => {
    async function loadClaims() {
      try {
        setClaimsLoading(true);

        const response = await axios.get(`${API_URL}/claims`, {
          params: {
            ...filters,
            page,
            size: pageSize,
          },
        });

        setClaims(response.data.content || []);
        setTotalClaims(response.data.totalElements || 0);
      } catch (err) {
        console.error(err);
        setError("Unable to load claims. Please check the backend.");
      } finally {
        setClaimsLoading(false);
      }
    }

    loadClaims();
  }, [filters, page]);

  const totalDeniedAmount = payers.reduce(
    (total, item) => total + Number(item.totalDeniedAmount || 0), 0
  );

  const totalDeniedClaims = payers.reduce(
    (total, item) => total + Number(item.deniedClaimCount || 0), 0
  );

  const handleApplyFilters = () => {
    if (startDate && endDate && startDate > endDate) {
      setError("Start date cannot be later than end date.");
      return;
    }

    setError("");
    setPage(0);
    setFilters({
      payer,
      reason,
      startDate,
      endDate,
    });
  };

  const handleResetFilters = () => {
    setPayer("");
    setReason("");
    setStartDate("");
    setEndDate("");
    setFilters({});
    setPage(0);
    setError("");
  };

  const totalPages = Math.ceil(totalClaims / pageSize);

  if (loading) {
    return <div className="loading-screen">Loading dashboard...</div>;
  }

  return (
    <div className="app-container">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-icon"><Activity size={23} /></div>
          <span>AR Analytics</span>
        </div>

        <div className="sidebar-label">WORKSPACE</div>
        <div className="nav-item active">
          <Activity size={18} />
          <span>Dashboard</span>
        </div>
        <div className="sidebar-note">
          Healthcare Revenue Cycle
          <br />
          Management
        </div>
        <div className="sidebar-footer">AR Denial Analytics v1.0</div>
      </aside>

      <main className="main-content">
        <header className="topbar">
          <div>
            <p className="breadcrumb">Workspace / Overview</p>
            <h1>Denial Analytics</h1>
          </div>
          <div className="live-status">
            <span className="status-dot"></span>
            Live data
          </div>
        </header>

        {error && (
          <div className="error-message">
            <AlertCircle size={18} />
            {error}
            <button onClick={() => setError("")}>Dismiss</button>
          </div>
        )}

        <section className="welcome-section">
          <div>
            <h2>Claims Overview</h2>
            <p>Monitor denied claims and understand the reasons behind them.</p>
          </div>
          <button className="refresh-button" onClick={() => window.location.reload()}>
            <RefreshCw size={16} /> Refresh
          </button>
        </section>

        <section className="stats-grid">
          <div className="stat-card">
            <div className="stat-icon purple"><FileText size={21} /></div>
            <p>Total Denied Claims</p>
            <h2>{totalDeniedClaims.toLocaleString("en-IN")}</h2>
            <span>Across all payers</span>
          </div>

          <div className="stat-card">
            <div className="stat-icon blue"><IndianRupee size={21} /></div>
            <p>Total Denied Amount</p>
            <h2>{formatCurrency(totalDeniedAmount)}</h2>
            <span>Combined billed amount</span>
          </div>

          <div className="stat-card">
            <div className="stat-icon orange"><Activity size={21} /></div>
            <p>Active Payers</p>
            <h2>{payers.length}</h2>
            <span>Payers with denied claims</span>
          </div>

          <div className="stat-card">
            <div className="stat-icon green"><AlertCircle size={21} /></div>
            <p>Denial Categories</p>
            <h2>{reasons.length}</h2>
            <span>Distinct denial reasons</span>
          </div>
        </section>

        <section className="analytics-grid">
          <div className="panel payer-panel">
            <div className="panel-heading">
              <div>
                <h3>Denials by Payer</h3>
                <p>Total denied amount grouped by insurance payer</p>
              </div>
            </div>

            <div className="chart-container">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={payers} margin={{ top: 10, right: 15, left: 5, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" vertical={false} />
                  <XAxis dataKey="payer" tick={{ fontSize: 11 }} />
                  <YAxis tick={{ fontSize: 11 }} />
                  <Tooltip formatter={(value) => formatCurrency(value)} />
                  <Bar
                    dataKey="totalDeniedAmount"
                    name="Denied Amount"
                    fill="#635bdb"
                    radius={[6, 6, 0, 0]}
                  />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>

          <div className="panel reason-panel">
            <div className="panel-heading">
              <div>
                <h3>Denial Reasons</h3>
                <p>Amount grouped by denial category</p>
              </div>
            </div>

            <div className="reason-list">
              {reasons.map((item) => (
                <div className="reason-item" key={item.denialReason}>
                  <div className="reason-info">
                    <span>{item.denialReason}</span>
                    <strong>{formatCurrency(item.totalDeniedAmount)}</strong>
                  </div>
                  <div className="progress-track">
                    <div
                      className="progress-fill"
                      style={{
                        width: `${Math.max(
                          0,
                          (Number(item.totalDeniedAmount) /
                            Math.max(...reasons.map(r => Number(r.totalDeniedAmount)), 1)) * 100
                        )}%`,
                      }}
                    />
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>

        <section className="panel claims-panel">
          <div className="panel-heading claims-heading">
            <div>
              <h3>Denied Claims</h3>
              <p>Search and filter denied healthcare claims</p>
            </div>
            <span className="record-count">{totalClaims} records</span>
          </div>

          <div className="filters">
            <div className="filter-field">
              <label>Payer</label>
              <select value={payer} onChange={(e) => setPayer(e.target.value)}>
                <option value="">All Payers</option>
                {payers.map((item) => (
                  <option key={item.payer} value={item.payer}>{item.payer}</option>
                ))}
              </select>
            </div>

            <div className="filter-field">
              <label>Denial Reason</label>
              <select value={reason} onChange={(e) => setReason(e.target.value)}>
                <option value="">All Reasons</option>
                {reasons.map((item) => (
                  <option key={item.denialReason} value={item.denialReason}>
                    {item.denialReason}
                  </option>
                ))}
              </select>
            </div>

            <div className="filter-field">
              <label>From Date</label>
              <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} />
            </div>

            <div className="filter-field">
              <label>To Date</label>
              <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} />
            </div>

            <button className="apply-button" onClick={handleApplyFilters}>
              <Search size={16} /> Apply
            </button>
            <button className="reset-button" onClick={handleResetFilters}>
              Reset
            </button>
          </div>

          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Claim ID</th>
                  <th>Payer</th>
                  <th>Provider</th>
                  <th>CPT Code</th>
                  <th>Denial Reason</th>
                  <th>Denied Date</th>
                  <th>Amount</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {claimsLoading ? (
                  <tr><td colSpan="8" className="empty-state">Loading claims...</td></tr>
                ) : claims.length === 0 ? (
                  <tr><td colSpan="8" className="empty-state">No denied claims found.</td></tr>
                ) : (
                  claims.map((claim) => (
                    <tr key={claim.id}>
                      <td className="claim-id">#{claim.id}</td>
                      <td>{claim.payer}</td>
                      <td>{claim.provider}</td>
                      <td>{claim.cptCode}</td>
                      <td>{claim.denialReason}</td>
                      <td>{claim.deniedAt}</td>
                      <td className="amount">{formatCurrency(claim.billedAmount)}</td>
                      <td><span className="status-badge">{claim.status}</span></td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>

          <div className="pagination">
            <span>
              Showing {totalClaims === 0 ? 0 : page * pageSize + 1}
              {" - "}
              {Math.min((page + 1) * pageSize, totalClaims)}
              {" of "}{totalClaims}
            </span>
            <div className="pagination-actions">
              <button
                disabled={page === 0 || claimsLoading}
                onClick={() => setPage((current) => current - 1)}
              >
                <ChevronLeft size={17} /> Previous
              </button>
              <span>Page {page + 1} of {Math.max(totalPages, 1)}</span>
              <button
                disabled={page + 1 >= totalPages || claimsLoading}
                onClick={() => setPage((current) => current + 1)}
              >
                Next <ChevronRight size={17} />
              </button>
            </div>
          </div>
        </section>

        <footer className="page-footer">
          AR Denial Analytics · Healthcare Claims Intelligence
        </footer>
      </main>
    </div>
  );
}

export default App;
