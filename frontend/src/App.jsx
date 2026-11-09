import { useEffect, useState } from "react";
import ReactMarkdown from "react-markdown";
import "./App.css";

const API = import.meta.env.VITE_API_BASE_URL || "/api";

function App() {
  const [page, setPage] = useState("Dashboard");

  const [analytics, setAnalytics] = useState(null);
  const [payments, setPayments] = useState([]);
  const [courierRecords, setCourierRecords] = useState([]);
  const [paymentHistory, setPaymentHistory] = useState([]);
  const [courierHistory, setCourierHistory] = useState([]);
  const [returns, setReturns] = useState([]);
  const [importHistory, setImportHistory] = useState([]);
  const [selectedImportFile, setSelectedImportFile] = useState(null);
  const [importUploading, setImportUploading] = useState(false);
  const [importResult, setImportResult] = useState(null);
  const [importError, setImportError] = useState("");
  const [businessInsights, setBusinessInsights] = useState("");

  const [returnForm, setReturnForm] = useState({
    orderNumber: "",
    returnReference: "",
    expectedRefundAmount: "",
    returnReason: ""
  });
  const [returnSaving, setReturnSaving] = useState(false);

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [refreshKey, setRefreshKey] = useState(0);
  // Gemini AI analysis
const [aiLoadingOrder, setAiLoadingOrder] = useState("");
const [aiExplanation, setAiExplanation] = useState("");
const [aiError, setAiError] = useState("");

  // Dashboard analytics
  useEffect(() => {
    fetch(`${API}/dashboard/analytics`)
      .then((res) => {
        if (!res.ok) throw new Error("Dashboard request failed");
        return res.json();
      })
      .then(setAnalytics)
      .catch(console.error);
  }, []);

  // Payment reconciliation
  useEffect(() => {
    if (page !== "Payments") return;

    setLoading(true);
    setError("");

    fetch(`${API}/reconciliation/payments`)
      .then((res) => {
        if (!res.ok) throw new Error("Unable to load payment records");
        return res.json();
      })
      .then(setPayments)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [page, refreshKey]);

  // Returns and refunds
  useEffect(() => {
    if (page !== "Returns & Refunds") return;

    setLoading(true);
    setError("");

    fetch(`${API}/returns`)
      .then((res) => {
        if (!res.ok) throw new Error("Unable to load return records");
        return res.json();
      })
      .then(setReturns)
      .catch((err) => {
        setReturns([]);
        setError(err.message);
      })
      .finally(() => setLoading(false));
  }, [page, refreshKey]);

  // File import history
  useEffect(() => {
    if (page !== "File Imports") return;

    setLoading(true);
    setImportError("");

    fetch(`${API}/imports/history`)
      .then((res) => {
        if (!res.ok) throw new Error("Unable to load import history");
        return res.json();
      })
      .then((data) =>
        setImportHistory(Array.isArray(data) ? data : [])
      )
      .catch((err) => setImportError(err.message))
      .finally(() => setLoading(false));
  }, [page, refreshKey]);

  // Courier reconciliation
  useEffect(() => {
    if (page !== "Courier") return;

    setLoading(true);
    setError("");

    fetch(`${API}/reconciliation/courier`)
      .then((res) => {
        if (!res.ok) throw new Error("Unable to load courier reconciliation");
        return res.json();
      })
      .then(setCourierRecords)
      .catch((err) => {
        setCourierRecords([]);
        setError(err.message);
      })
      .finally(() => setLoading(false));
  }, [page, refreshKey]);

  // Reconciliation History
useEffect(() => {
  if (page !== "Reconciliation History") return;

  setLoading(true);
  setError("");

  Promise.all([
    fetch(`${API}/reconciliation/history`),
    fetch(`${API}/reconciliation/courier/history`)
  ])
    .then(async ([paymentResponse, courierResponse]) => {
      if (!paymentResponse.ok || !courierResponse.ok) {
        throw new Error("Unable to load reconciliation history");
      }

      const paymentData = await paymentResponse.json();
      const courierData = await courierResponse.json();

      setPaymentHistory(paymentData);
      setCourierHistory(courierData);
    })
    .catch((err) => setError(err.message))
    .finally(() => setLoading(false));
}, [page, refreshKey]);

  const navigation = [
    { name: "Dashboard", icon: "▦" },
    { name: "Payments", icon: "💳" },
    { name: "Courier", icon: "🚚" },
    { name: "Returns & Refunds", icon: "↩" },
    { name: "File Imports", icon: "📁" },
    { name: "Reconciliation History", icon: "📋" },
  ];

  const formatAmount = (amount) =>
    `₹${Number(amount || 0).toLocaleString("en-IN", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })}`;

  const formatWeight = (weight) =>
    `${Number(weight || 0).toFixed(3)} kg`;

  const refresh = () => setRefreshKey((prev) => prev + 1);

  
const generateBusinessInsights = async () => {
  setAiLoadingOrder("BUSINESS_INSIGHTS");
  setAiError("");
  setBusinessInsights("");

  try {
    const response = await fetch(`${API}/ai/explain`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        recordType: "BUSINESS",
        reference: "MERCHANT_DASHBOARD",
        expectedAmount: Number(analytics?.totalExpectedAmount || 0),
        actualAmount: Number(analytics?.totalReceivedAmount || 0),
        discrepancy: Number(analytics?.totalDiscrepancy || 0),
        additionalDetails: `
          Total Orders: ${analytics?.totalOrders ?? "Not available"}
          Courier Discrepancies: ${analytics?.courierDiscrepancies ?? "Not available"}
          Pending COD Settlement: ${analytics?.pendingCodSettlement ?? "Not available"}
          Total Returns: ${analytics?.totalReturns ?? "Not available"}
          Expected Refunds: ${analytics?.totalExpectedRefunds ?? "Not available"}
          Completed Refunds: ${analytics?.completedRefunds ?? "Not available"}
          Pending Refunds: ${analytics?.pendingRefunds ?? "Not available"}
        `,
      }),
    });

    if (!response.ok) {
      throw new Error("Unable to generate business insights.");
    }

    const data = await response.json();
    setBusinessInsights(data.explanation);
  } catch (err) {
    setAiError(err.message);
  } finally {
    setAiLoadingOrder("");
  }
};


  
const explainPayment = async (payment) => {
  setAiLoadingOrder(payment.orderNumber);
  setAiExplanation("");
  setAiError("");

  try {
    const response = await fetch(`${API}/ai/explain`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        recordType: "PAYMENT",
        reference: payment.orderNumber,
        expectedAmount: Number(payment.expectedAmount || 0),
        actualAmount: Number(payment.receivedAmount || 0),
        discrepancy: Number(payment.discrepancy || 0),
      }),
    });

    if (!response.ok) {
      throw new Error("Unable to generate AI explanation.");
    }

    const data = await response.json();
    setAiExplanation(data.explanation);
  } catch (err) {
    setAiError(err.message);
  } finally {
    setAiLoadingOrder("");
  }
};



const explainCourier = async (record) => {
  setAiLoadingOrder(record.trackingNumber);
  setAiExplanation("");
  setAiError("");

  const additionalDetails = `
    Reconciliation Status: ${record.status}

    Expected Weight: ${record.expectedWeight ?? "Not available"} kg
    Actual Weight: ${record.actualWeight ?? "Not available"} kg

    Expected Shipping Charge: ${record.expectedShippingCharge ?? "Not available"}
    Actual Shipping Charge: ${record.actualShippingCharge ?? "Not available"}
    Shipping Charge Discrepancy: ${record.shippingChargeDiscrepancy ?? "Not available"}

    Expected COD: ${record.expectedCodAmount ?? "Not available"}
    COD Collected: ${record.codCollected ?? "Not available"}
    COD Settled: ${record.codSettled ?? "Not available"}
    COD Settlement Discrepancy: ${record.codSettlementDiscrepancy ?? "Not available"}
  `;

  try {
    const response = await fetch(`${API}/ai/explain`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        recordType: "COURIER",
        reference: record.trackingNumber,
        expectedAmount: Number(record.expectedShippingCharge ?? 0),
        actualAmount: Number(record.actualShippingCharge ?? 0),
        discrepancy: Number(record.shippingChargeDiscrepancy ?? 0),
        additionalDetails,
      }),
    });

    if (!response.ok) {
      throw new Error("Unable to generate courier AI explanation.");
    }

    const data = await response.json();
    setAiExplanation(data.explanation);
  } catch (err) {
    setAiError(err.message);
  } finally {
    setAiLoadingOrder("");
  }
};



  const runPaymentReconciliation = async () => {
  try {
    setLoading(true);
    setError("");

    const response = await fetch(
      `${API}/reconciliation/payments/run`,
      { method: "POST" }
    );

    if (!response.ok) {
      throw new Error("Payment reconciliation failed");
    }

    alert("Payment reconciliation completed successfully!");
    refresh();
  } catch (err) {
    setError(err.message);
  } finally {
    setLoading(false);
  }
};

const runCourierReconciliation = async () => {
  try {
    setLoading(true);
    setError("");

    const response = await fetch(
      `${API}/reconciliation/courier/run`,
      { method: "POST" }
    );

    if (!response.ok) {
      throw new Error("Courier reconciliation failed");
    }

    alert("Courier reconciliation completed successfully!");
    refresh();
  } catch (err) {
    setError(err.message);
  } finally {
    setLoading(false);
  }
};

  const filteredPayments = payments.filter((payment) => {
    const matchesSearch = payment.orderNumber
      ?.toLowerCase()
      .includes(search.toLowerCase());

    const matchesStatus =
      statusFilter === "ALL" || payment.status === statusFilter;

    return matchesSearch && matchesStatus;
  });

  const filteredCourier = courierRecords.filter((record) => {
    const matchesSearch = record.trackingNumber
      ?.toLowerCase()
      .includes(search.toLowerCase());

    const matchesStatus =
      statusFilter === "ALL" || record.status === statusFilter;

    return matchesSearch && matchesStatus;
  });

  const filteredReturns = returns.filter((record) => {
    const query = search.toLowerCase();

    const matchesSearch =
      record.returnReference?.toLowerCase().includes(query) ||
      record.orderNumber?.toLowerCase().includes(query);

    const matchesStatus =
      statusFilter === "ALL" || record.returnStatus === statusFilter;

    return matchesSearch && matchesStatus;
  });

    const createReturn = async (event) => {
    event.preventDefault();
    setReturnSaving(true);
    setError("");

    try {
      const response = await fetch(`${API}/returns`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          ...returnForm,
          expectedRefundAmount: Number(returnForm.expectedRefundAmount),
          returnStatus: "REQUESTED",
          returnDate: new Date().toISOString().slice(0, 19),
        }),
      });

      if (!response.ok) {
        const message = await response.text();
        throw new Error(message || "Unable to create return request");
      }

      setReturnForm({
        orderNumber: "",
        returnReference: "",
        expectedRefundAmount: "",
        returnReason: ""
      });

      refresh();
    } catch (err) {
      setError(err.message);
    } finally {
      setReturnSaving(false);
    }
  };

  const updateReturnStatus = async (reference, status) => {
    setError("");

    try {
      const response = await fetch(
        `${API}/returns/${encodeURIComponent(reference)}/status?status=${encodeURIComponent(status)}`,
        { method: "PUT" }
      );

      if (!response.ok) {
        const message = await response.text();
        throw new Error(message || "Unable to update return status");
      }

      const updated = await response.json();

      setReturns((current) =>
        current.map((item) =>
          item.returnReference === reference ? updated : item
        )
      );
    } catch (err) {
      setError(err.message);
    }
  };

  // File upload
  const uploadImportFile = async (event) => {
    event.preventDefault();

    if (!selectedImportFile) {
      setImportError("Choose a CSV or Excel file first.");
      return;
    }

    const formData = new FormData();
    formData.append("file", selectedImportFile);

    setImportUploading(true);
    setImportError("");
    setImportResult(null);

    try {
      const response = await fetch(`${API}/imports/upload`, {
        method: "POST",
        body: formData
      });

      const data = await response.json().catch(() => ({}));

      if (!response.ok) {
        throw new Error(
          data.message || data.error || "File upload failed."
        );
      }

      setImportResult(data);
      setSelectedImportFile(null);

      const input = document.getElementById("reconx-import-file");
      if (input) input.value = "";

      const historyResponse = await fetch(`${API}/imports/history`);

      if (historyResponse.ok) {
        const historyData = await historyResponse.json();
        setImportHistory(
          Array.isArray(historyData) ? historyData : []
        );
      }
    } catch (err) {
      setImportError(err.message);
    } finally {
      setImportUploading(false);
    }
  };

  const statusClass = (status) => {
    switch (status) {
      case "MATCHED":
        return "matched";

      case "AMOUNT_MISMATCH":
      case "WEIGHT_MISMATCH":
      case "SHIPPING_CHARGE_MISMATCH":
      case "COD_COLLECTION_MISMATCH":
      case "COD_SETTLEMENT_MISMATCH":
        return "mismatch";

      case "PAYMENT_MISSING":
      case "COURIER_TRANSACTION_MISSING":
        return "missing";

      case "PAYMENT_FAILED":
      case "REJECTED":
        return "failed";

      case "APPROVED":
      case "PICKED_UP":
      case "RECEIVED":
      case "REFUND_PENDING":
        return "mismatch";

      case "REFUNDED":
        return "matched";

      case "MULTIPLE_DISCREPANCIES":
        return "failed";

      default:
        return "";
    }
  };

  // Dashboard
  const renderDashboard = () => (
    <>
      {!analytics ? (
        <div className="panel">
          Loading dashboard analytics...
        </div>
      ) : (
        <>
          <section className="stats-grid">
            {[
              {
                label: "Total Orders",
                value: analytics.totalOrders,
                icon: "📦",
              },
              {
                label: "Expected Revenue",
                value: formatAmount(analytics.totalExpectedAmount),
                icon: "💰",
              },
              {
                label: "Received Revenue",
                value: formatAmount(analytics.totalReceivedAmount),
                icon: "💳",
              },
              {
                label: "Total Discrepancy",
                value: formatAmount(analytics.totalDiscrepancy),
                icon: "⚠️",
              },
            ].map((item) => (
              <article className="stat-card" key={item.label}>
                <div className="stat-top">
                  <span>{item.label}</span>
                  <span className="stat-icon">{item.icon}</span>
                </div>
                <h2>{item.value}</h2>
              </article>
            ))}
          </section>

<section className="panel ai-business-panel">
  <div className="panel-heading">
    <div>
      <h3>✨ ReconX AI Business Insights</h3>
      <p>AI-powered analysis of your merchant financial performance.</p>
    </div>
    <button
      className="refresh-button"
      onClick={generateBusinessInsights}
      disabled={aiLoadingOrder === "BUSINESS_INSIGHTS"}
    >
      {aiLoadingOrder === "BUSINESS_INSIGHTS"
        ? "Analyzing..."
        : "Generate Insights"}
    </button>
  </div>

  {aiError && <p className="error-message">{aiError}</p>}

  {businessInsights && (
    <div className="ai-markdown">
      <ReactMarkdown>{businessInsights}</ReactMarkdown>
    </div>
  )}
</section>

<section className="payment-summary">
  <div className="mini-card">
    <span>Courier Discrepancies</span>
    <strong className="text-orange">
      {analytics.courierDiscrepancies}
    </strong>
  </div>

  <div className="mini-card">
    <span>Pending COD Settlement</span>
    <strong>{formatAmount(analytics.pendingCodSettlement)}</strong>
  </div>

  <div className="mini-card">
    <span>Total Returns</span>
    <strong>{analytics.totalReturns}</strong>
  </div>

  <div className="mini-card">
    <span>Expected Refunds</span>
    <strong>{formatAmount(analytics.totalExpectedRefunds)}</strong>
  </div>

  <div className="mini-card">
    <span>Completed Refunds</span>
    <strong className="text-green">
      {formatAmount(analytics.completedRefunds)}
    </strong>
  </div>

  <div className="mini-card">
    <span>Pending Refunds</span>
    <strong className="text-orange">
      {formatAmount(analytics.pendingRefunds)}
    </strong>
  </div>
</section>

<section className="content-grid">
  <article className="panel">
    <h3>Payment Reconciliation Overview</h3>
    <p className="muted">Payment status distribution</p>

    {[
      ["Matched", analytics.matchedPayments, "#10b981"],
      ["Mismatched", analytics.amountMismatches, "#f59e0b"],
      ["Missing", analytics.missingPayments, "#ef4444"],
      ["Failed", analytics.failedPayments, "#8b5cf6"]
    ].map(([label, value, color]) => (
      <div key={label} style={{ marginBottom: "16px" }}>
        <div style={{
          display: "flex",
          justifyContent: "space-between",
          marginBottom: "6px"
        }}>
          <span>{label}</span>
          <strong>{value}</strong>
        </div>
        <div style={{
          height: "9px",
          background: "#edf0f5",
          borderRadius: "10px",
          overflow: "hidden"
        }}>
          <div style={{
            width: `${analytics.totalOrders
              ? (value / analytics.totalOrders) * 100
              : 0}%`,
            height: "100%",
            background: color,
            borderRadius: "10px"
          }} />
        </div>
      </div>
    ))}
  </article>

  <article className="panel">
    <h3>Courier Reconciliation Overview</h3>
    <p className="muted">Shipment discrepancy summary</p>

    <div className="summary-grid">
      <div>
        <small>Discrepancies</small>
        <strong>{analytics.courierDiscrepancies}</strong>
      </div>
      <div>
        <small>Pending COD</small>
        <strong>{formatAmount(analytics.pendingCodSettlement)}</strong>
      </div>
      <div>
        <small>Matched Shipments</small>
        <strong>
          {Math.max(0, courierRecords.length - analytics.courierDiscrepancies)}
        </strong>
      </div>
    </div>
  </article>
</section>

          <section className="content-grid">
            <article className="panel">
              <div className="panel-heading">
                <div>
                  <h3>Payment Reconciliation</h3>
                  <p>Current payment matching performance</p>
                </div>
                <span className="pill">Live data</span>
              </div>

              <div className="rate">
                {Number(analytics.reconciliationRate).toFixed(1)}%
              </div>

              <p className="muted">
                Reconciliation success rate
              </p>

              <div className="progress-track">
                <div
                  className="progress-fill"
                  style={{
                    width: `${Math.min(
                      100,
                      Math.max(0, Number(analytics.reconciliationRate))
                    )}%`,
                  }}
                />
              </div>
            </article>

            <article className="panel">
              <div className="panel-heading">
                <div>
                  <h3>Payment Status</h3>
                  <p>Order reconciliation breakdown</p>
                </div>
              </div>

              {[
                ["Matched Payments", analytics.matchedPayments, "green"],
                ["Amount Mismatches", analytics.amountMismatches, "orange"],
                ["Missing Payments", analytics.missingPayments, "red"],
                ["Failed Payments", analytics.failedPayments, "purple"],
              ].map(([label, value, color]) => (
                <div className="status-row" key={label}>
                  <span>
                    <i className={`dot ${color}`} />
                    {label}
                  </span>
                  <strong>{value}</strong>
                </div>
              ))}
            </article>
          </section>

          <section className="panel bottom-panel">
            <div className="panel-heading">
              <div>
                <h3>Reconciliation Summary</h3>
                <p>Financial overview from your backend</p>
              </div>
            </div>

            <div className="summary-grid">
              <div>
                <small>Total Orders</small>
                <strong>{analytics.totalOrders}</strong>
              </div>

              <div>
                <small>Matched Payments</small>
                <strong>{analytics.matchedPayments}</strong>
              </div>

              <div>
                <small>Unresolved Payments</small>
                <strong>
                  {analytics.amountMismatches +
                    analytics.missingPayments +
                    analytics.failedPayments}
                </strong>
              </div>
            </div>
          </section>
        </>
      )}
    </>
  );

    // Payments
  const renderPayments = () => (
    <>
      <section className="page-intro">
        <div>
          <h2>Payment Reconciliation</h2>
          <p>
            Track gateway payments and identify financial discrepancies.
          </p>
        </div>

        <div style={{ display: "flex", gap: "10px" }}>
  <button
    className="refresh-button"
    onClick={runPaymentReconciliation}
    disabled={loading}
  >
    ▶ Run Reconciliation
  </button>

  <button className="refresh-button" onClick={refresh}>
    ↻ Refresh
  </button>
</div>
      </section>

      <section className="payment-summary">
        <div className="mini-card">
          <span>Total Records</span>
          <strong>{payments.length}</strong>
        </div>

        <div className="mini-card">
          <span>Matched</span>
          <strong className="text-green">
            {payments.filter((p) => p.status === "MATCHED").length}
          </strong>
        </div>

        <div className="mini-card">
          <span>Discrepancies</span>
          <strong className="text-orange">
            {payments.filter((p) => p.status === "AMOUNT_MISMATCH").length}
          </strong>
        </div>

        <div className="mini-card">
          <span>Missing / Failed</span>
          <strong className="text-red">
            {payments.filter((p) =>
              ["PAYMENT_MISSING", "PAYMENT_FAILED"].includes(p.status)
            ).length}
          </strong>
        </div>
      </section>

      {(aiLoadingOrder || aiExplanation || aiError) && (
  <section className="panel">
    <h3>✨ ReconX AI Analysis</h3>

    {aiLoadingOrder && (
      <p>Analyzing {aiLoadingOrder} with Gemini...</p>
    )}

    {aiError && (
      <p className="error-message">{aiError}</p>
    )}

    {aiExplanation && (
  <div className="ai-markdown">
    <ReactMarkdown>{aiExplanation}</ReactMarkdown>
  </div>
)}
  </section>
)}

      <section className="panel payments-panel">
        <div className="panel-heading">
          <div>
            <h3>Payment Transactions</h3>
            <p>Order-wise expected and received payment amounts</p>
          </div>
        </div>

        <div className="table-toolbar">
          <input
            type="text"
            placeholder="Search order number..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />

          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
          >
            <option value="ALL">All statuses</option>
            <option value="MATCHED">Matched</option>
            <option value="AMOUNT_MISMATCH">Amount mismatch</option>
            <option value="PAYMENT_MISSING">Payment missing</option>
            <option value="PAYMENT_FAILED">Payment failed</option>
          </select>
        </div>

        {loading && (
          <p className="message">Loading payment records...</p>
        )}

        {error && <div className="error-message">{error}</div>}

        {!loading && !error && (
          <div className="table-scroll">
            <table className="payments-table">
              <thead>
                <tr>
                  <th>Order Number</th>
                  <th>Expected Amount</th>
                  <th>Received Amount</th>
                  <th>Discrepancy</th>
                  <th>Status</th>
                  <th>AI Analysis</th>
                </tr>
              </thead>

              <tbody>
                {filteredPayments.map((payment, index) => (
                  <tr key={`${payment.orderNumber}-${index}`}>
                    <td className="order-number">
                      {payment.orderNumber}
                    </td>
                    <td>{formatAmount(payment.expectedAmount)}</td>
                    <td>{formatAmount(payment.receivedAmount)}</td>
                    <td className="discrepancy">
                      {formatAmount(payment.discrepancy)}
                    </td>
                    <td>
                      <span
                        className={`status-badge ${statusClass(payment.status)}`}
                      >
                        {payment.status.replaceAll("_", " ")}
                      </span>
                    </td>

                    <td>
  <button
  className="refresh-button"
  onClick={() => explainPayment(payment)}
  disabled={aiLoadingOrder === payment.orderNumber}
>
  {aiLoadingOrder === payment.orderNumber
    ? "Analyzing..."
    : "✨ Explain with AI"}
</button>
</td>

                  </tr>
                ))}

                {filteredPayments.length === 0 && (
                  <tr>
                    <td colSpan="6" className="empty-table">
                      No matching payment records found.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}

        <div className="table-footer">
          Showing {filteredPayments.length} of {payments.length} records
        </div>
      </section>
    </>
  );

  // Courier
  const renderCourier = () => {
    const matched = courierRecords.filter(
      (r) => r.status === "MATCHED"
    ).length;

    const missing = courierRecords.filter(
      (r) => r.status === "COURIER_TRANSACTION_MISSING"
    ).length;

    const discrepancies = courierRecords.length - matched - missing;

    const totalExtraShipping = courierRecords.reduce(
      (sum, r) =>
        sum + Math.max(0, Number(r.shippingChargeDiscrepancy || 0)),
      0
    );

    const totalCodPending = courierRecords.reduce(
      (sum, r) =>
        sum + Math.max(0, Number(r.codSettlementDiscrepancy || 0)),
      0
    );

    return (
      <>

      {(aiLoadingOrder || aiExplanation || aiError) && (
  <section className="panel">
    <h3>✨ ReconX AI Courier Analysis</h3>

    {aiLoadingOrder && (
      <p>Analyzing {aiLoadingOrder} with Gemini...</p>
    )}

    {aiError && (
      <p className="error-message">{aiError}</p>
    )}

    {aiExplanation && (
      <div className="ai-markdown">
        <ReactMarkdown>{aiExplanation}</ReactMarkdown>
      </div>
    )}
  </section>
)}

        <section className="page-intro">
          <div>
            <h2>Courier Reconciliation</h2>
            <p>
              Monitor shipment weights, shipping charges and COD settlements.
            </p>
          </div>

          <div style={{ display: "flex", gap: "10px" }}>
  <button
    className="refresh-button"
    onClick={runCourierReconciliation}
    disabled={loading}
  >
    ▶ Run Reconciliation
  </button>

  <button className="refresh-button" onClick={refresh}>
    ↻ Refresh
  </button>
</div>
        </section>

        <section className="payment-summary">
          <div className="mini-card">
            <span>Total Shipments</span>
            <strong>{courierRecords.length}</strong>
          </div>

          <div className="mini-card">
            <span>Matched</span>
            <strong className="text-green">{matched}</strong>
          </div>

          <div className="mini-card">
            <span>Discrepancies</span>
            <strong className="text-orange">{discrepancies}</strong>
          </div>

          <div className="mini-card">
            <span>Missing Transactions</span>
            <strong className="text-red">{missing}</strong>
          </div>
        </section>

        <section className="payment-summary">
          <div className="mini-card">
            <span>Extra Shipping Charges</span>
            <strong>{formatAmount(totalExtraShipping)}</strong>
          </div>

          <div className="mini-card">
            <span>COD Settlement Pending</span>
            <strong className="text-orange">
              {formatAmount(totalCodPending)}
            </strong>
          </div>
        </section>

        <section className="panel payments-panel">
          <div className="panel-heading">
            <div>
              <h3>Courier Reconciliation Records</h3>
              <p>
                Compare expected shipment values against courier transactions.
              </p>
            </div>
          </div>

          <div className="table-toolbar">
            <input
              type="text"
              placeholder="Search tracking number..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />

            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <option value="ALL">All statuses</option>
              <option value="MATCHED">Matched</option>
              <option value="WEIGHT_MISMATCH">Weight mismatch</option>
              <option value="SHIPPING_CHARGE_MISMATCH">
                Shipping charge mismatch
              </option>
              <option value="COD_COLLECTION_MISMATCH">
                COD collection mismatch
              </option>
              <option value="COD_SETTLEMENT_MISMATCH">
                COD settlement mismatch
              </option>
              <option value="MULTIPLE_DISCREPANCIES">
                Multiple discrepancies
              </option>
              <option value="COURIER_TRANSACTION_MISSING">
                Transaction missing
              </option>
            </select>
          </div>

          {loading && (
            <p className="message">Loading courier reconciliation...</p>
          )}

          {error && <div className="error-message">{error}</div>}

          {!loading && !error && (
            <div className="table-scroll">
              <table className="payments-table">
                <thead>
                  <tr>
                    <th>Tracking Number</th>
                    <th>Expected Weight</th>
                    <th>Actual Weight</th>
                    <th>Expected Shipping</th>
                    <th>Actual Shipping</th>
                    <th>COD Expected</th>
                    <th>COD Collected</th>
                    <th>COD Settled</th>
                    <th>Status</th>
                    <th>AI Analysis</th>
                  </tr>
                </thead>

                <tbody>
                  {filteredCourier.map((record, index) => (
                    <tr key={`${record.trackingNumber}-${index}`}>
                      <td className="order-number">
                        {record.trackingNumber}
                      </td>

                      <td>{formatWeight(record.expectedWeight)}</td>
                      <td>{formatWeight(record.actualWeight)}</td>

                      <td>{formatAmount(record.expectedShippingCharge)}</td>
                      <td>{formatAmount(record.actualShippingCharge)}</td>

                      <td>{formatAmount(record.expectedCodAmount)}</td>
                      <td>{formatAmount(record.codCollected)}</td>
                      <td>{formatAmount(record.codSettled)}</td>

                      <td>
                        <span
                          className={`status-badge ${statusClass(record.status)}`}
                        >
                          {record.status?.replaceAll("_", " ")}
                        </span>
                      </td>

                      <td>
  <button
    className="refresh-button"
    onClick={() => explainCourier(record)}
    disabled={aiLoadingOrder === record.trackingNumber}
  >
    {aiLoadingOrder === record.trackingNumber
      ? "Analyzing..."
      : "✨ Explain with AI"}
  </button>
</td>

                    </tr>
                  ))}

                  {filteredCourier.length === 0 && (
                    <tr>
                      <td colSpan="10" className="empty-table">
                        No courier reconciliation records found.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}

          <div className="table-footer">
            Showing {filteredCourier.length} of {courierRecords.length} records
          </div>
        </section>
      </>
    );
  };

    // Returns & Refunds
  const renderReturns = () => {
    const totalExpected = returns.reduce(
      (sum, item) => sum + Number(item.expectedRefundAmount || 0),
      0
    );

    const refunded = returns
      .filter((item) => item.returnStatus === "REFUNDED")
      .reduce(
        (sum, item) => sum + Number(item.expectedRefundAmount || 0),
        0
      );

    const pending = returns
      .filter((item) =>
        ["REFUND_PENDING", "RECEIVED"].includes(item.returnStatus)
      )
      .reduce(
        (sum, item) => sum + Number(item.expectedRefundAmount || 0),
        0
      );

    const statuses = [
      "REQUESTED",
      "APPROVED",
      "PICKED_UP",
      "RECEIVED",
      "REFUND_PENDING",
      "REFUNDED",
      "REJECTED"
    ];

    return (
      <>
        <section className="page-intro">
          <div>
            <h2>Returns & Refunds</h2>
            <p>Track returned orders, refund obligations and return progress.</p>
          </div>
          <button className="refresh-button" onClick={refresh}>
            ↻ Refresh
          </button>
        </section>

        <section className="payment-summary">
          <div className="mini-card">
            <span>Total Returns</span>
            <strong>{returns.length}</strong>
          </div>
          <div className="mini-card">
            <span>Expected Refunds</span>
            <strong>{formatAmount(totalExpected)}</strong>
          </div>
          <div className="mini-card">
            <span>Refunded</span>
            <strong className="text-green">{formatAmount(refunded)}</strong>
          </div>
          <div className="mini-card">
            <span>Refunds Pending</span>
            <strong className="text-orange">{formatAmount(pending)}</strong>
          </div>
        </section>

        <section className="panel payments-panel">
          <div className="panel-heading">
            <div>
              <h3>Create Return Request</h3>
              <p>Register a return against an existing order.</p>
            </div>
          </div>

          <form
            onSubmit={createReturn}
            className="table-toolbar"
            style={{ flexWrap: "wrap", alignItems: "center" }}
          >
            <input
              required
              placeholder="Order number"
              value={returnForm.orderNumber}
              onChange={(e) =>
                setReturnForm({
                  ...returnForm,
                  orderNumber: e.target.value
                })
              }
            />

            <input
              required
              placeholder="Return reference"
              value={returnForm.returnReference}
              onChange={(e) =>
                setReturnForm({
                  ...returnForm,
                  returnReference: e.target.value
                })
              }
            />

            <input
              required
              type="number"
              min="0.01"
              step="0.01"
              placeholder="Refund amount (₹)"
              value={returnForm.expectedRefundAmount}
              onChange={(e) =>
                setReturnForm({
                  ...returnForm,
                  expectedRefundAmount: e.target.value
                })
              }
            />

            <input
              placeholder="Return reason"
              value={returnForm.returnReason}
              onChange={(e) =>
                setReturnForm({
                  ...returnForm,
                  returnReason: e.target.value
                })
              }
            />

            <button
              className="refresh-button"
              type="submit"
              disabled={returnSaving}
            >
              {returnSaving ? "Saving..." : "+ Create Return"}
            </button>
          </form>
        </section>

        <section className="panel payments-panel">
          <div className="panel-heading">
            <div>
              <h3>Return Records</h3>
              <p>Review return progress and update workflow status.</p>
            </div>
          </div>

          <div className="table-toolbar">
            <input
              placeholder="Search return or order reference..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />

            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <option value="ALL">All statuses</option>
              {statuses.map((status) => (
                <option key={status} value={status}>
                  {status.replaceAll("_", " ")}
                </option>
              ))}
            </select>
          </div>

          {loading && <p className="message">Loading return records...</p>}
          {error && <div className="error-message">{error}</div>}

          {!loading && !error && (
            <div className="table-scroll">
              <table className="payments-table">
                <thead>
                  <tr>
                    <th>Return Reference</th>
                    <th>Order Number</th>
                    <th>Expected Refund</th>
                    <th>Reason</th>
                    <th>Return Date</th>
                    <th>Status</th>
                    <th>Update Status</th>
                  </tr>
                </thead>

                <tbody>
                  {filteredReturns.map((record) => (
                    <tr key={record.id ?? record.returnReference}>
                      <td className="order-number">
                        {record.returnReference}
                      </td>
                      <td>{record.orderNumber}</td>
                      <td>{formatAmount(record.expectedRefundAmount)}</td>
                      <td>{record.returnReason || "—"}</td>
                      <td>
                        {record.returnDate
                          ? new Date(record.returnDate).toLocaleDateString("en-IN")
                          : "—"}
                      </td>
                      <td>
                        <span
                          className={`status-badge ${statusClass(record.returnStatus)}`}
                        >
                          {record.returnStatus?.replaceAll("_", " ")}
                        </span>
                      </td>
                      <td>
                        <select
                          value={record.returnStatus}
                          onChange={(e) =>
                            updateReturnStatus(
                              record.returnReference,
                              e.target.value
                            )
                          }
                          aria-label={`Update ${record.returnReference} status`}
                        >
                          {statuses.map((status) => (
                            <option key={status} value={status}>
                              {status.replaceAll("_", " ")}
                            </option>
                          ))}
                        </select>
                      </td>
                    </tr>
                  ))}

                  {filteredReturns.length === 0 && (
                    <tr>
                      <td colSpan="7" className="empty-table">
                        No return records found.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          )}

          <div className="table-footer">
            Showing {filteredReturns.length} of {returns.length} records
          </div>
        </section>
      </>
    );
  };

  // File Imports
  const renderFileImports = () => (
    <>
      <section className="page-intro">
        <div>
          <h2>File Import Center</h2>
          <p>
            Upload merchant, payment gateway or courier CSV and Excel reports.
          </p>
        </div>

        <button className="refresh-button" onClick={refresh}>
          ↻ Refresh
        </button>
      </section>

      <section className="panel import-panel">
        <div className="panel-heading">
          <div>
            <h3>Upload Reconciliation File</h3>
            <p>Select a CSV or Excel file to import records.</p>
          </div>
        </div>

        <form onSubmit={uploadImportFile} className="import-form">
          <div className="import-dropzone">
            <div className="import-icon">📂</div>
            <h3>Choose your reconciliation file</h3>
            <p>Supported formats: CSV, XLS and XLSX</p>

            <input
  id="reconx-import-file"
  type="file"
  accept=".csv,.xls,.xlsx"
  onChange={(e) => {
    setSelectedImportFile(e.target.files?.[0] || null);
    setImportError("");
  }}
/>

            {selectedImportFile && (
              <p className="selected-file">
                Selected: {selectedImportFile.name}
              </p>
            )}
          </div>

          <button
            className="refresh-button import-submit"
            type="submit"
            disabled={importUploading}
          >
            {importUploading ? "Uploading..." : "Upload and Process"}
          </button>
        </form>

        {importError && (
          <div className="error-message">{importError}</div>
        )}

        {importResult && (
          <div className="import-result">
            <h3>Import Results</h3>

            <div className="import-result-grid">
              <div>
                <small>File Name</small>
                <strong>{importResult.fileName}</strong>
              </div>
              <div>
                <small>Total Records</small>
                <strong>{importResult.totalRecords}</strong>
              </div>
              <div>
                <small>Successful</small>
                <strong className="text-green">
                  {importResult.successfulRecords}
                </strong>
              </div>
              <div>
                <small>Failed</small>
                <strong className="text-red">
                  {importResult.failedRecords}
                </strong>
              </div>
              <div>
                <small>Status</small>
                <strong>
                  {importResult.status?.replaceAll("_", " ")}
                </strong>
              </div>
            </div>

            {importResult.errors?.length > 0 && (
              <div className="import-errors">
                <h4>Rows with errors</h4>
                <div className="table-scroll">
                  <table className="payments-table">
                    <thead>
                      <tr>
                        <th>Row Number</th>
                        <th>Error Message</th>
                      </tr>
                    </thead>
                    <tbody>
                      {importResult.errors.map((item, index) => (
                        <tr key={index}>
                          <td>{item.rowNumber}</td>
                          <td>{item.message}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}
          </div>
        )}
      </section>

      <section className="panel payments-panel">
        <div className="panel-heading">
          <div>
            <h3>Import History</h3>
            <p>Previously uploaded files and their processing results.</p>
          </div>
        </div>

        {loading && <p className="message">Loading import history...</p>}

        {importError && !importResult && (
          <div className="error-message">{importError}</div>
        )}

        {!loading && (
          <div className="table-scroll">
            <table className="payments-table">
              <thead>
                <tr>
                  <th>File Name</th>
                  <th>File Type</th>
                  <th>Total Records</th>
                  <th>Successful</th>
                  <th>Failed</th>
                  <th>Status</th>
                  <th>Imported At</th>
                </tr>
              </thead>

              <tbody>
                {importHistory.map((record) => (
                  <tr key={record.id}>
                    <td className="order-number">{record.fileName}</td>
                    <td>{record.fileType}</td>
                    <td>{record.totalRecords}</td>
                    <td className="text-green">
                      {record.successfulRecords}
                    </td>
                    <td className="text-red">{record.failedRecords}</td>
                    <td>
                      <span className="status-badge">
                        {record.status?.replaceAll("_", " ")}
                      </span>
                    </td>
                    <td>
                      {record.importedAt
                        ? new Date(record.importedAt).toLocaleString("en-IN")
                        : "—"}
                    </td>
                  </tr>
                ))}

                {importHistory.length === 0 && (
                  <tr>
                    <td colSpan="7" className="empty-table">
                      No import history found.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}

        <div className="table-footer">
          Total imports: {importHistory.length}
        </div>
      </section>
    </>
  );

  return (
    <div className="app">
      <aside className="sidebar">
        <h2 className="brand">
          Recon<span>X</span>
        </h2>

        <p className="brand-subtitle">RECONCILIATION PLATFORM</p>

        <nav>
          {navigation.map((item) => (
            <button
              key={item.name}
              className={`nav-link ${page === item.name ? "active" : ""}`}
              onClick={() => {
                setPage(item.name);
                setError("");
                setImportError("");
                setSearch("");
                setStatusFilter("ALL");

                window.history.replaceState(
                  null,
                  "",
                  item.name === "Dashboard"
                    ? "/"
                    : `/#${encodeURIComponent(
                        item.name.toLowerCase().replaceAll(" ", "-")
                      )}`
                );
              }}
            >
              <span>{item.icon}</span>
              {item.name}
            </button>
          ))}
        </nav>

        <div className="sidebar-bottom">
          <div className="avatar">M</div>
          <div>
            <strong>Merchant Account</strong>
            <small>Administrator</small>
          </div>
        </div>
      </aside>

      <main className="main-content">
        <header className="topbar">
          <div>
            <h1>
              {page === "Dashboard" ? "Dashboard Overview" : page}
            </h1>
            <p>
              {page === "Dashboard"
                ? "Monitor your business reconciliation activity."
                : "Manage and monitor your reconciliation operations."}
            </p>
          </div>

          <div className="topbar-right">
            <span className="status-dot" />
            System Online
          </div>
        </header>

        {page === "Dashboard" && renderDashboard()}
        {page === "Payments" && renderPayments()}
        {page === "Courier" && renderCourier()}
        {page === "Returns & Refunds" && renderReturns()}
        {page === "File Imports" && renderFileImports()}

        {page === "Reconciliation History" && (
  <>
    <section className="page-intro">
      <div>
        <h2>Reconciliation History</h2>
        <p>Review previous payment and courier reconciliation runs.</p>
      </div>

      <button className="refresh-button" onClick={refresh}>
        ↻ Refresh
      </button>
    </section>

    {loading && <p className="message">Loading history...</p>}
    {error && <div className="error-message">{error}</div>}

    {!loading && !error && (
      <>
        <section className="panel payments-panel">
          <div className="panel-heading">
            <div>
              <h3>Payment Reconciliation History</h3>
              <p>Previous order-wise payment reconciliation results.</p>
            </div>
            <span className="pill">{paymentHistory.length} Records</span>
          </div>

          <div className="table-scroll">
            <table className="payments-table">
              <thead>
                <tr>
                  <th>Order Number</th>
                  <th>Expected Amount</th>
                  <th>Received Amount</th>
                  <th>Discrepancy</th>
                  <th>Status</th>
                  <th>Reconciled At</th>
                </tr>
              </thead>
              <tbody>
                {paymentHistory.map((record) => (
                  <tr key={record.id}>
                    <td className="order-number">{record.orderNumber}</td>
                    <td>{formatAmount(record.expectedAmount)}</td>
                    <td>{formatAmount(record.receivedAmount)}</td>
                    <td>{formatAmount(record.discrepancy)}</td>
                    <td>
                      <span className={`status-badge ${statusClass(record.status)}`}>
                        {record.status?.replaceAll("_", " ")}
                      </span>
                    </td>
                    <td>
                      {record.reconciledAt
                        ? new Date(record.reconciledAt).toLocaleString("en-IN")
                        : "—"}
                    </td>
                  </tr>
                ))}

                {paymentHistory.length === 0 && (
                  <tr>
                    <td colSpan="6" className="empty-table">
                      No payment history found.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </section>

        <section className="panel payments-panel">
          <div className="panel-heading">
            <div>
              <h3>Courier Reconciliation History</h3>
              <p>Previous shipment-wise courier reconciliation results.</p>
            </div>
            <span className="pill">{courierHistory.length} Records</span>
          </div>

          <div className="table-scroll">
            <table className="payments-table">
              <thead>
                <tr>
                  <th>Tracking Number</th>
                  <th>Weight Difference</th>
                  <th>Shipping Difference</th>
                  <th>COD Collection Difference</th>
                  <th>COD Settlement Difference</th>
                  <th>Status</th>
                  <th>Reconciled At</th>
                </tr>
              </thead>
              <tbody>
                {courierHistory.map((record) => (
                  <tr key={record.id}>
                    <td className="order-number">{record.trackingNumber}</td>
                    <td>{formatWeight(record.weightDiscrepancy)}</td>
                    <td>{formatAmount(record.shippingChargeDiscrepancy)}</td>
                    <td>{formatAmount(record.codCollectionDiscrepancy)}</td>
                    <td>{formatAmount(record.codSettlementDiscrepancy)}</td>
                    <td>
                      <span className={`status-badge ${statusClass(record.status)}`}>
                        {record.status?.replaceAll("_", " ")}
                      </span>
                    </td>
                    <td>
                      {record.reconciledAt
                        ? new Date(record.reconciledAt).toLocaleString("en-IN")
                        : "—"}
                    </td>
                  </tr>
                ))}

                {courierHistory.length === 0 && (
                  <tr>
                    <td colSpan="7" className="empty-table">
                      No courier history found.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </section>
      </>
    )}
  </>
)}
      </main>
    </div>
  );
}

export default App;