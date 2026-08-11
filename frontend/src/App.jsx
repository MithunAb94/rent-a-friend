import { useDeferredValue, useEffect, useState, startTransition } from "react";
import { api } from "./api";

const SESSION_KEY = "rent-a-friend-session";

const initialRegisterForm = {
  fullName: "",
  legalName: "",
  email: "",
  password: "",
  phoneNumber: "",
  city: "",
  state: "",
  country: "",
  latitude: "",
  longitude: "",
  locationConsentGranted: false,
  emotionalGoal: "",
  preferredSupportStyle: "Gentle listener",
  dateOfBirth: "",
  governmentIdType: "Passport",
  governmentIdLastFour: "",
  emergencyContactName: "",
  emergencyContactPhone: "",
  acceptedTerms: false,
  acceptedSafetyPolicy: false,
  acceptedPhysicalBoundaries: false
};

const initialLoginForm = {
  email: "",
  password: ""
};

const initialBookingForm = {
  category: "Just Need To Talk",
  sessionMode: "CHAT",
  preferredDate: "",
  preferredTime: "",
  durationMinutes: 60,
  notes: ""
};

function formatDate(value) {
  if (!value) {
    return "Not available";
  }

  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return date.toLocaleString();
}

function App() {
  const [categories, setCategories] = useState([]);
  const [listeners, setListeners] = useState([]);
  const [legalPolicy, setLegalPolicy] = useState(null);
  const [dashboard, setDashboard] = useState(null);
  const [session, setSession] = useState(() => {
    const stored = window.localStorage.getItem(SESSION_KEY);
    return stored ? JSON.parse(stored) : null;
  });
  const [authMode, setAuthMode] = useState("register");
  const [registerForm, setRegisterForm] = useState(initialRegisterForm);
  const [loginForm, setLoginForm] = useState(initialLoginForm);
  const [bookingForm, setBookingForm] = useState(initialBookingForm);
  const [selectedListener, setSelectedListener] = useState(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [categoryFilter, setCategoryFilter] = useState("All");
  const [cityFilter, setCityFilter] = useState("All");
  const [loading, setLoading] = useState(false);
  const [locating, setLocating] = useState(false);
  const [feedback, setFeedback] = useState("");
  const [error, setError] = useState("");

  const deferredSearch = useDeferredValue(searchTerm);

  useEffect(() => {
    loadCatalog();
  }, []);

  useEffect(() => {
    if (!session?.token) {
      return;
    }
    refreshDashboard(session.token);
  }, [session?.token]);

  async function loadCatalog() {
    try {
      const [categoryData, listenerData, policyData] = await Promise.all([
        api.getCategories(),
        api.getListeners(),
        api.getLegalPolicy()
      ]);
      setCategories(categoryData);
      setListeners(listenerData);
      setLegalPolicy(policyData);
      if (!selectedListener && listenerData.length > 0) {
        setSelectedListener(listenerData[0]);
      }
    } catch (requestError) {
      setError(requestError.message);
    }
  }

  async function refreshDashboard(token) {
    try {
      const data = await api.getDashboard(token);
      startTransition(() => {
        setDashboard(data);
        setSession((currentSession) => {
          if (!currentSession) {
            return currentSession;
          }
          const nextSession = { ...currentSession, user: data.user };
          window.localStorage.setItem(SESSION_KEY, JSON.stringify(nextSession));
          return nextSession;
        });
      });
    } catch (requestError) {
      setError(requestError.message);
      clearSession();
    }
  }

  function persistSession(nextSession) {
    setSession(nextSession);
    window.localStorage.setItem(SESSION_KEY, JSON.stringify(nextSession));
  }

  function clearSession() {
    setSession(null);
    setDashboard(null);
    window.localStorage.removeItem(SESSION_KEY);
  }

  function updateRegisterField(field, value) {
    setRegisterForm((currentForm) => ({
      ...currentForm,
      [field]: value
    }));
  }

  function handleLocationCapture() {
    if (!navigator.geolocation) {
      setError("Your browser does not support current-location capture.");
      return;
    }

    setLocating(true);
    setError("");
    setFeedback("");

    navigator.geolocation.getCurrentPosition(
      (position) => {
        const { latitude, longitude } = position.coords;
        setRegisterForm((currentForm) => ({
          ...currentForm,
          latitude: latitude.toFixed(6),
          longitude: longitude.toFixed(6),
          locationConsentGranted: true
        }));
        setLocating(false);
        setFeedback("Current location captured. We will store it only because you consented.");
      },
      () => {
        setLocating(false);
        setError("Location capture was blocked. You can still register with manual city, state, and country.");
      },
      {
        enableHighAccuracy: true,
        timeout: 10000
      }
    );
  }

  async function handleRegister(event) {
    event.preventDefault();
    setLoading(true);
    setError("");
    setFeedback("");
    try {
      const authResponse = await api.register({
        ...registerForm,
        latitude: registerForm.latitude ? Number(registerForm.latitude) : null,
        longitude: registerForm.longitude ? Number(registerForm.longitude) : null
      });
      persistSession(authResponse);
      setFeedback("Your account and verification submission are saved. Online support is ready while in-person review stays protected.");
      setRegisterForm(initialRegisterForm);
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleLogin(event) {
    event.preventDefault();
    setLoading(true);
    setError("");
    setFeedback("");
    try {
      const authResponse = await api.login(loginForm);
      persistSession(authResponse);
      setFeedback("Welcome back. Your support dashboard is refreshed.");
      setLoginForm(initialLoginForm);
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleBooking(event) {
    event.preventDefault();
    if (!session?.token || !activeListener) {
      setError("Please create an account before booking a session.");
      return;
    }

    setLoading(true);
    setError("");
    setFeedback("");
    try {
      await api.createBooking(session.token, {
        ...bookingForm,
        listenerId: activeListener.id,
        durationMinutes: Number(bookingForm.durationMinutes)
      });
      await refreshDashboard(session.token);
      setFeedback(`Session request sent to ${activeListener.displayName}.`);
      setBookingForm({
        ...initialBookingForm,
        category: bookingForm.category
      });
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  async function handleStatusChange(bookingId, status) {
    if (!session?.token) {
      return;
    }
    setLoading(true);
    setError("");
    setFeedback("");
    try {
      await api.updateBookingStatus(session.token, bookingId, status);
      await refreshDashboard(session.token);
      setFeedback(`Booking marked as ${status.toLowerCase()}.`);
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setLoading(false);
    }
  }

  const cities = ["All", ...new Set(listeners.map((listener) => listener.city))];

  const filteredListeners = listeners.filter((listener) => {
    const matchesSearch =
      listener.displayName.toLowerCase().includes(deferredSearch.toLowerCase()) ||
      listener.title.toLowerCase().includes(deferredSearch.toLowerCase()) ||
      listener.supportAreas.join(" ").toLowerCase().includes(deferredSearch.toLowerCase());
    const matchesCategory =
      categoryFilter === "All" || listener.supportAreas.includes(categoryFilter);
    const matchesCity = cityFilter === "All" || listener.city === cityFilter;
    return matchesSearch && matchesCategory && matchesCity;
  });

  const activeListener =
    filteredListeners.find((listener) => listener.id === selectedListener?.id) ||
    filteredListeners[0] ||
    selectedListener;

  const verificationStatus = dashboard?.user?.verificationStatus || session?.user?.verificationStatus;
  const inPersonLocked = verificationStatus !== "VERIFIED";

  return (
    <div className="app-shell">
      <div className="background-orb orb-one" />
      <div className="background-orb orb-two" />

      <header className="topbar">
        <div>
          <p className="eyebrow">Rent a Friend</p>
          <h1>A caring space to be heard, supported, and protected by clear boundaries.</h1>
        </div>
        <div className="topbar-actions">
          {session?.user ? (
            <>
              <div className="welcome-pill">
                <span>{session.user.fullName}</span>
                <small>
                  {session.user.city}, {session.user.state}
                </small>
              </div>
              <button className="secondary-button" onClick={clearSession}>
                Sign out
              </button>
            </>
          ) : (
            <button
              className="secondary-button"
              onClick={() => document.getElementById("auth-card")?.scrollIntoView({ behavior: "smooth" })}
            >
              Join now
            </button>
          )}
        </div>
      </header>

      <main className="layout">
        <section className="hero-card">
          <div className="hero-copy">
            <p className="eyebrow">Emotional support, with guardrails</p>
            <h2>Register with identity details, current location consent, and clear conduct rules before you connect.</h2>
            <p className="hero-text">
              Rent a Friend is built for listening, companionship conversation, and emotional support. It is not a
              sexual, escort, or physical intimacy platform.
            </p>
            <div className="hero-metrics">
              <div>
                <strong>{listeners.length}+</strong>
                <span>listener profiles</span>
              </div>
              <div>
                <strong>{categories.length}</strong>
                <span>support tracks</span>
              </div>
              <div>
                <strong>{legalPolicy?.version || "Protected"}</strong>
                <span>active policy version</span>
              </div>
            </div>
          </div>
          <div className="hero-panel">
            <p className="eyebrow">How it works</p>
            <div className="step-list">
              <article>
                <span>01</span>
                <h3>Register securely</h3>
                <p>We collect contact details, emergency backup, ID hints, and optional current coordinates with consent.</p>
              </article>
              <article>
                <span>02</span>
                <h3>Review and match</h3>
                <p>Users can browse listeners, while verification review protects higher-risk session types.</p>
              </article>
              <article>
                <span>03</span>
                <h3>Book safely</h3>
                <p>Sexual requests and unsafe physical expectations are blocked by policy and booking validation.</p>
              </article>
            </div>
          </div>
        </section>

        <section className="categories-grid">
          {categories.map((category) => (
            <article key={category.id} className="category-card">
              <p className="category-icon">{category.icon}</p>
              <h3>{category.name}</h3>
              <p>{category.description}</p>
            </article>
          ))}
        </section>

        {legalPolicy && (
          <section className="policy-card">
            <div className="policy-head">
              <div>
                <p className="eyebrow">Terms and Conditions</p>
                <h2>{legalPolicy.title}</h2>
                <p className="hero-text">{legalPolicy.summary}</p>
              </div>
              <span className="policy-version">{legalPolicy.version}</span>
            </div>
            <div className="policy-grid">
              <article>
                <h3>User requirements</h3>
                <ul>
                  {legalPolicy.userRequirements.map((item) => (
                    <li key={item}>{item}</li>
                  ))}
                </ul>
              </article>
              <article>
                <h3>Prohibited requests</h3>
                <ul>
                  {legalPolicy.prohibitedRequests.map((item) => (
                    <li key={item}>{item}</li>
                  ))}
                </ul>
              </article>
              <article>
                <h3>In-person rules</h3>
                <ul>
                  {legalPolicy.inPersonSafetyRules.map((item) => (
                    <li key={item}>{item}</li>
                  ))}
                </ul>
              </article>
            </div>
            <p className="policy-note">{legalPolicy.enforcementNote}</p>
          </section>
        )}

        <section className="content-grid">
          <div className="catalog-column">
            <div className="section-head">
              <div>
                <p className="eyebrow">Listener directory</p>
                <h2>Choose a voice that feels safe for you.</h2>
              </div>
              <div className="filters">
                <input
                  value={searchTerm}
                  onChange={(event) => setSearchTerm(event.target.value)}
                  placeholder="Search by name or need"
                />
                <select value={categoryFilter} onChange={(event) => setCategoryFilter(event.target.value)}>
                  <option value="All">All support types</option>
                  {categories.map((category) => (
                    <option key={category.id} value={category.name}>
                      {category.name}
                    </option>
                  ))}
                </select>
                <select value={cityFilter} onChange={(event) => setCityFilter(event.target.value)}>
                  {cities.map((city) => (
                    <option key={city} value={city}>
                      {city === "All" ? "All cities" : city}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div className="listener-grid">
              {filteredListeners.map((listener) => (
                <article
                  key={listener.id}
                  className={`listener-card ${activeListener?.id === listener.id ? "selected" : ""}`}
                  onClick={() => setSelectedListener(listener)}
                >
                  <img src={listener.imageUrl} alt={listener.displayName} />
                  <div>
                    <div className="listener-title-row">
                      <h3>{listener.displayName}</h3>
                      {listener.featured && <span className="tag feature-tag">Featured</span>}
                    </div>
                    <p className="listener-role">{listener.title}</p>
                    <div className="listener-meta">
                      <span>{listener.city}</span>
                      <span>{listener.rating} rating</span>
                      <span>${listener.hourlyRate}/hour</span>
                    </div>
                    <div className="tag-row">
                      {listener.supportAreas.map((area) => (
                        <span className="tag" key={`${listener.id}-${area}`}>
                          {area}
                        </span>
                      ))}
                    </div>
                    <p className="listener-response">{listener.responseTime}</p>
                  </div>
                </article>
              ))}
            </div>
          </div>

          <aside className="side-column">
            <section className="detail-card">
              {activeListener ? (
                <>
                  <img className="detail-image" src={activeListener.imageUrl} alt={activeListener.displayName} />
                  <p className="eyebrow">Selected listener</p>
                  <h2>{activeListener.displayName}</h2>
                  <p className="listener-role">{activeListener.title}</p>
                  <div className="detail-pill-row">
                    <span>{activeListener.city}</span>
                    <span>{activeListener.responseTime}</span>
                    <span>{activeListener.availabilityNote}</span>
                  </div>
                  <p className="detail-note">
                    Choose this listener if you want a warm and private space to be heard without judgment.
                  </p>
                </>
              ) : (
                <p>No listeners found for the selected filters.</p>
              )}
            </section>

            <section id="auth-card" className="panel-card">
              <div className="panel-head">
                <div>
                  <p className="eyebrow">Account</p>
                  <h2>{session?.user ? "Your support dashboard" : "Register or sign in"}</h2>
                </div>
                {!session?.user && (
                  <div className="mode-switch">
                    <button
                      className={authMode === "register" ? "active" : ""}
                      onClick={() => setAuthMode("register")}
                    >
                      Register
                    </button>
                    <button
                      className={authMode === "login" ? "active" : ""}
                      onClick={() => setAuthMode("login")}
                    >
                      Login
                    </button>
                  </div>
                )}
              </div>

              {error && <p className="message error">{error}</p>}
              {feedback && <p className="message success">{feedback}</p>}

              {!session?.user && authMode === "register" && (
                <form className="stack-form" onSubmit={handleRegister}>
                  <div className="form-grid">
                    <input
                      value={registerForm.fullName}
                      onChange={(event) => updateRegisterField("fullName", event.target.value)}
                      placeholder="Display name"
                      required
                    />
                    <input
                      value={registerForm.legalName}
                      onChange={(event) => updateRegisterField("legalName", event.target.value)}
                      placeholder="Legal name"
                      required
                    />
                    <input
                      type="email"
                      value={registerForm.email}
                      onChange={(event) => updateRegisterField("email", event.target.value)}
                      placeholder="Email"
                      required
                    />
                    <input
                      type="password"
                      value={registerForm.password}
                      onChange={(event) => updateRegisterField("password", event.target.value)}
                      placeholder="Password"
                      required
                    />
                    <input
                      value={registerForm.phoneNumber}
                      onChange={(event) => updateRegisterField("phoneNumber", event.target.value)}
                      placeholder="Phone number"
                      required
                    />
                    <label className="field">
                      <span>Date of birth</span>
                      <input
                          type="date"
                          value={registerForm.dateOfBirth}
                          onChange={(event) => updateRegisterField("dateOfBirth", event.target.value)}
                          required
                      />
                    </label>                    <input
                      value={registerForm.city}
                      onChange={(event) => updateRegisterField("city", event.target.value)}
                      placeholder="City"
                      required
                    />
                    <input
                      value={registerForm.state}
                      onChange={(event) => updateRegisterField("state", event.target.value)}
                      placeholder="State"
                      required
                    />
                    <input
                      value={registerForm.country}
                      onChange={(event) => updateRegisterField("country", event.target.value)}
                      placeholder="Country"
                      required
                    />
                    <select
                      value={registerForm.governmentIdType}
                      onChange={(event) => updateRegisterField("governmentIdType", event.target.value)}
                    >
                      <option>Passport</option>
                      <option>Driver License</option>
                      <option>National ID</option>
                    </select>
                    <input
                      value={registerForm.governmentIdLastFour}
                      onChange={(event) => updateRegisterField("governmentIdLastFour", event.target.value)}
                      placeholder="Government ID last 4 digits"
                      maxLength="4"
                      required
                    />
                    <input
                      value={registerForm.emergencyContactName}
                      onChange={(event) => updateRegisterField("emergencyContactName", event.target.value)}
                      placeholder="Emergency contact name"
                      required
                    />
                    <input
                      value={registerForm.emergencyContactPhone}
                      onChange={(event) => updateRegisterField("emergencyContactPhone", event.target.value)}
                      placeholder="Emergency contact phone"
                      required
                    />
                    <select
                      value={registerForm.preferredSupportStyle}
                      onChange={(event) => updateRegisterField("preferredSupportStyle", event.target.value)}
                    >
                      <option>Gentle listener</option>
                      <option>Practical motivator</option>
                      <option>Calm companion</option>
                      <option>Reflective conversationalist</option>
                    </select>
                  </div>

                  <textarea
                    value={registerForm.emotionalGoal}
                    onChange={(event) => updateRegisterField("emotionalGoal", event.target.value)}
                    placeholder="What kind of support do you need right now?"
                    rows="4"
                    required
                  />

                  <div className="location-card">
                    <div>
                      <h3>Current location capture</h3>
                      <p>
                        Use your device location to store exact coordinates for safer review and future in-person
                        approval. Manual city, state, and country are still required.
                      </p>
                    </div>
                    <button
                      type="button"
                      className="secondary-button"
                      onClick={handleLocationCapture}
                      disabled={locating}
                    >
                      {locating ? "Capturing location..." : "Use my current location"}
                    </button>
                    <div className="location-pills">
                      <span>{registerForm.latitude ? `Lat ${registerForm.latitude}` : "Latitude not captured"}</span>
                      <span>{registerForm.longitude ? `Lng ${registerForm.longitude}` : "Longitude not captured"}</span>
                    </div>
                  </div>

                  <div className="consent-box">
                    <label className="checkbox-row">
                      <input
                        type="checkbox"
                        checked={registerForm.locationConsentGranted}
                        onChange={(event) => updateRegisterField("locationConsentGranted", event.target.checked)}
                      />
                      <span>I consent to storing my current location if I choose to capture it for account safety review.</span>
                    </label>
                    <label className="checkbox-row">
                      <input
                        type="checkbox"
                        checked={registerForm.acceptedTerms}
                        onChange={(event) => updateRegisterField("acceptedTerms", event.target.checked)}
                      />
                      <span>I accept the platform terms and conditions and understand this is an emotional support service only.</span>
                    </label>
                    <label className="checkbox-row">
                      <input
                        type="checkbox"
                        checked={registerForm.acceptedSafetyPolicy}
                        onChange={(event) => updateRegisterField("acceptedSafetyPolicy", event.target.checked)}
                      />
                      <span>I accept the safety policy, including identity review and account monitoring for unsafe behavior.</span>
                    </label>
                    <label className="checkbox-row">
                      <input
                        type="checkbox"
                        checked={registerForm.acceptedPhysicalBoundaries}
                        onChange={(event) => updateRegisterField("acceptedPhysicalBoundaries", event.target.checked)}
                      />
                      <span>I understand that sex, nudity, escorting, kissing, touching, and hidden physical commitments are forbidden.</span>
                    </label>
                  </div>

                  <button className="primary-button" disabled={loading}>
                    {loading ? "Creating account..." : "Create verified account"}
                  </button>
                </form>
              )}

              {!session?.user && authMode === "login" && (
                <form className="stack-form" onSubmit={handleLogin}>
                  <input
                    type="email"
                    value={loginForm.email}
                    onChange={(event) => setLoginForm({ ...loginForm, email: event.target.value })}
                    placeholder="Email"
                    required
                  />
                  <input
                    type="password"
                    value={loginForm.password}
                    onChange={(event) => setLoginForm({ ...loginForm, password: event.target.value })}
                    placeholder="Password"
                    required
                  />
                  <button className="primary-button" disabled={loading}>
                    {loading ? "Signing in..." : "Sign in"}
                  </button>
                </form>
              )}

              {session?.user && dashboard && (
                <div className="dashboard-stack">
                  <div className="dashboard-profile">
                    <div className="profile-topline">
                      <div>
                        <h3>{dashboard.user.fullName}</h3>
                        <p>
                          {dashboard.user.city}, {dashboard.user.state}, {dashboard.user.country}
                        </p>
                      </div>
                      <span className={`status-pill ${dashboard.user.verificationStatus.toLowerCase()}`}>
                        {dashboard.user.verificationStatus}
                      </span>
                    </div>
                    <span>{dashboard.user.preferredSupportStyle}</span>
                    <p>{dashboard.user.emotionalGoal}</p>
                    <div className="profile-meta-grid">
                      <div>
                        <strong>Contact</strong>
                        <small>{dashboard.user.phoneNumber}</small>
                      </div>
                      <div>
                        <strong>ID</strong>
                        <small>
                          {dashboard.user.governmentIdType} {dashboard.user.maskedGovernmentId}
                        </small>
                      </div>
                      <div>
                        <strong>Emergency</strong>
                        <small>
                          {dashboard.user.emergencyContactName} {dashboard.user.emergencyContactPhone}
                        </small>
                      </div>
                      <div>
                        <strong>Terms version</strong>
                        <small>{dashboard.user.termsVersion}</small>
                      </div>
                    </div>
                    <p className="verification-note">
                      Verification submitted on {formatDate(dashboard.user.verificationSubmittedAt)}. In-person sessions
                      unlock only after approval.
                    </p>
                  </div>

                  <div className="stats-grid">
                    <article>
                      <strong>{dashboard.stats.totalSessions}</strong>
                      <span>Total requests</span>
                    </article>
                    <article>
                      <strong>{dashboard.stats.upcomingSessions}</strong>
                      <span>Upcoming</span>
                    </article>
                    <article>
                      <strong>{dashboard.stats.completedSessions}</strong>
                      <span>Completed</span>
                    </article>
                  </div>

                  <div className="safety-banner">
                    <strong>Safety notice</strong>
                    <p>
                      Requests involving sex, nudity, escorting, or hidden physical commitments are blocked. In-person
                      bookings require a verified account.
                    </p>
                  </div>

                  <form className="stack-form booking-form" onSubmit={handleBooking}>
                    <h3>Book with {activeListener?.displayName || "your selected listener"}</h3>
                    <select
                      value={bookingForm.category}
                      onChange={(event) => setBookingForm({ ...bookingForm, category: event.target.value })}
                    >
                      {categories.map((category) => (
                        <option key={category.id} value={category.name}>
                          {category.name}
                        </option>
                      ))}
                    </select>
                    <select
                      value={bookingForm.sessionMode}
                      onChange={(event) => setBookingForm({ ...bookingForm, sessionMode: event.target.value })}
                    >
                      <option value="CHAT">Chat</option>
                      <option value="AUDIO">Audio</option>
                      <option value="VIDEO">Video</option>
                      <option value="IN_PERSON" disabled={inPersonLocked}>
                        In person {inPersonLocked ? "(verification required)" : ""}
                      </option>
                    </select>
                    <label className="field">
                      <span>Booking date</span>
                      <input
                          type="date"
                          value={bookingForm.preferredDate}
                          onChange={(event) =>
                              setBookingForm({ ...bookingForm, preferredDate: event.target.value })
                          }
                          required
                      />
                    </label>
                    <label className="field">
                      <span>Booking time</span>
                      <input
                          type="time"
                          value={bookingForm.preferredTime}
                          onChange={(event) =>
                              setBookingForm({ ...bookingForm, preferredTime: event.target.value })
                          }
                          required
                      />
                    </label>
                    <select
                      value={bookingForm.durationMinutes}
                      onChange={(event) => setBookingForm({ ...bookingForm, durationMinutes: event.target.value })}
                    >
                      <option value="30">30 minutes</option>
                      <option value="45">45 minutes</option>
                      <option value="60">60 minutes</option>
                      <option value="90">90 minutes</option>
                    </select>
                    <textarea
                      rows="3"
                      value={bookingForm.notes}
                      onChange={(event) => setBookingForm({ ...bookingForm, notes: event.target.value })}
                      placeholder="Anything you want your listener to know before the session?"
                    />
                    <button className="primary-button" disabled={loading || !activeListener}>
                      {loading ? "Sending request..." : "Request session"}
                    </button>
                  </form>

                  <div className="booking-list">
                    <div className="panel-head compact">
                      <h3>Your bookings</h3>
                    </div>
                    {dashboard.bookings.length === 0 && (
                      <p className="empty-state">Your first booking will appear here.</p>
                    )}
                    {dashboard.bookings.map((booking) => (
                      <article className="booking-card" key={booking.id}>
                        <div>
                          <p className="booking-heading">{booking.listenerName}</p>
                          <p>
                            {booking.category} - {booking.sessionMode} - {booking.preferredDate} at {booking.preferredTime}
                          </p>
                          <small>{booking.durationMinutes} minutes</small>
                        </div>
                        <div className="booking-actions">
                          <span className={`status-pill ${booking.status.toLowerCase()}`}>{booking.status}</span>
                          {booking.status === "PENDING" && (
                            <button
                              className="secondary-button small"
                              onClick={() => handleStatusChange(booking.id, "CANCELLED")}
                            >
                              Cancel
                            </button>
                          )}
                          {booking.status === "CONFIRMED" && (
                            <button
                              className="secondary-button small"
                              onClick={() => handleStatusChange(booking.id, "COMPLETED")}
                            >
                              Mark complete
                            </button>
                          )}
                        </div>
                      </article>
                    ))}
                  </div>
                </div>
              )}
            </section>
          </aside>
        </section>
      </main>
    </div>
  );
}

export default App;

