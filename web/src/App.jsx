import { useEffect, useState } from "react";
import { supabase, isSupabaseConfigured } from "./lib/supabase";
import {
  ArrowRight, ArrowUpRight, CheckCircle2, ChevronRight, CreditCard,
  Database, History, LogOut, Menu, ShieldCheck, Smartphone, Tv, Wifi,
  Zap, Wallet, X
} from "lucide-react";

const services = [
  { name: "Buy Airtime", detail: "Top up any Nigerian network", icon: Smartphone },
  { name: "Buy Data", detail: "Stay connected for less", icon: Wifi },
  { name: "Pay TV", detail: "Cable TV subscriptions", icon: Tv },
  { name: "Pay Electricity", detail: "Prepaid and postpaid bills", icon: Zap }
];

export default function App() {
  const [session, setSession] = useState(null);
  const [authMode, setAuthMode] = useState("signin");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [busy, setBusy] = useState(false);
  const [notice, setNotice] = useState("");
  const [balance, setBalance] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [mobileMenu, setMobileMenu] = useState(false);

  useEffect(() => {
    if (!supabase) return;
    supabase.auth.getSession().then(({ data }) => setSession(data.session));
    const { data: listener } = supabase.auth.onAuthStateChange((_event, next) => setSession(next));
    return () => listener.subscription.unsubscribe();
  }, []);

  useEffect(() => {
    if (!session || !supabase) return;
    let active = true;
    async function loadAccount() {
      const userId = session.user.id;
      const walletResult = await supabase.from("wallets").select("balance").eq("user_id", userId).maybeSingle();
      if (active) setBalance(walletResult.data?.balance ?? null);
      const txResult = await supabase.from("transactions").select("*").eq("user_id", userId).order("created_at", { ascending: false }).limit(8);
      if (active) setTransactions(txResult.data ?? []);
    }
    loadAccount();
    return () => { active = false; };
  }, [session]);

  async function handleAuth(event) {
    event.preventDefault();
    if (!supabase) { setNotice("Add your Supabase environment variables to connect account access."); return; }
    setBusy(true); setNotice("");
    const result = authMode === "signin"
      ? await supabase.auth.signInWithPassword({ email, password })
      : await supabase.auth.signUp({ email, password });
    setBusy(false);
    if (result.error) setNotice(result.error.message);
    else if (authMode === "signup" && !result.data.session) setNotice("Account created. Check your email to confirm your account.");
    else setNotice("Welcome to Great VTU.");
  }

  async function signOut() {
    await supabase?.auth.signOut();
    setSession(null); setBalance(null); setTransactions([]);
  }

  const money = (value) => new Intl.NumberFormat("en-NG", { style: "currency", currency: "NGN" }).format(Number(value || 0));

  return (
    <div className="app-shell">
      <header className="topbar">
        <a className="brand" href="#" onClick={() => setSession(null)}>
          <span className="brand-mark">G</span><span>GREAT<span className="brand-light">VTU</span></span>
        </a>
        <button className="menu-toggle" onClick={() => setMobileMenu(!mobileMenu)} aria-label="Toggle menu">{mobileMenu ? <X/> : <Menu/>}</button>
        <nav className={mobileMenu ? "nav open" : "nav"}>
          <a href="#services" onClick={() => setMobileMenu(false)}>Services</a>
          <a href="#how-it-works" onClick={() => setMobileMenu(false)}>How it works</a>
          {session ? <button className="nav-action" onClick={signOut}><LogOut size={16}/> Sign out</button> : <a className="nav-action" href="#account">Sign in <ArrowUpRight size={16}/></a>}
        </nav>
      </header>

      {session ? (
        <main className="dashboard">
          <div className="welcome-row"><div><p className="eyebrow">YOUR ACCOUNT</p><h1>Welcome back</h1><p className="muted">{session.user.email}</p></div><button className="outline-btn" onClick={signOut}><LogOut size={16}/> Sign out</button></div>
          <section className="balance-card">
            <div className="balance-top"><span><Wallet size={17}/> Wallet balance</span><span className="secure-tag"><ShieldCheck size={14}/> Secure account</span></div>
            <div className="balance-amount">{balance === null ? "—" : money(balance)}</div>
            <p>{balance === null ? "Wallet balance is not available yet. Check your Supabase wallets table and access policies." : "Available wallet balance"}</p>
            <div className="balance-foot"><span>Great VTU wallet</span><span>NGN · Nigeria</span></div>
          </section>
          <section id="services" className="section-block"><div className="section-heading"><div><p className="eyebrow">WHAT DO YOU NEED?</p><h2>Quick services</h2></div></div>
            <div className="service-grid">{services.map(({name,detail,icon:Icon}) => <button className="service-tile" key={name} onClick={() => setNotice("This service will be enabled after the VTU provider and verified payment backend are connected.")}><span className="service-icon"><Icon size={22}/></span><strong>{name}</strong><small>{detail}</small><ChevronRight className="tile-arrow" size={18}/></button>)}</div>
          </section>
          <section className="section-block"><div className="section-heading"><div><p className="eyebrow">YOUR ACTIVITY</p><h2>Recent transactions</h2></div><History size={20}/></div>
            <div className="transactions">{transactions.length ? transactions.map((tx) => <div className="transaction" key={tx.id}><span className="transaction-icon"><CreditCard size={18}/></span><div className="transaction-info"><strong>{tx.service_name || tx.type || "Transaction"}</strong><small>{tx.recipient || tx.reference || "—"}</small></div><div className="transaction-amount"><strong>{money(tx.amount)}</strong><small>{tx.status || "Pending"}</small></div></div>) : <p className="empty-state">No transaction records found yet.</p>}</div>
          </section>
          {notice && <div className="notice"><span>{notice}</span><button onClick={() => setNotice("")} aria-label="Dismiss"><X size={16}/></button></div>}
        </main>
      ) : (
        <>
          <main>
            <section className="hero">
              <div className="hero-copy"><div className="pill"><span className="live-dot"/> SIMPLE DIGITAL PAYMENTS</div><h1>Your everyday<br/><span>top-ups, simplified.</span></h1><p>Buy airtime, stay connected with data, and manage everyday bill payments from one convenient place.</p><div className="hero-actions"><a className="primary-btn" href="#account">Get started <ArrowRight size={18}/></a><a className="text-link" href="#services">Explore services <ChevronRight size={17}/></a></div><div className="trust-row"><span><ShieldCheck size={16}/> Account access</span><span><CheckCircle2 size={16}/> Clear activity</span></div></div>
              <div className="hero-visual"><div className="visual-glow"/><div className="phone-card"><div className="phone-head"><span className="mini-brand">G</span><span className="phone-dots">•••</span></div><p className="phone-label">WALLET BALANCE</p><div className="phone-balance">₦••,•••<span>.00</span></div><div className="phone-line"/><div className="phone-shortcuts"><span><Smartphone/>Airtime</span><span><Wifi/>Data</span><span><Zap/>Bills</span></div><div className="phone-activity"><span>Recent activity</span><b>••••••••</b></div></div><div className="float-chip"><span className="chip-check"><CheckCircle2 size={17}/></span><span><b>One simple hub</b><small>Your digital essentials</small></span></div></div>
            </section>
            <section id="services" className="public-services section-block"><div className="section-heading"><div><p className="eyebrow">ONE PLACE. EVERYDAY ESSENTIALS.</p><h2>Services made simple</h2></div><p className="section-intro">A clean, convenient way to manage the digital services you use every day.</p></div><div className="service-grid">{services.map(({name,detail,icon:Icon}) => <div className="service-tile" key={name}><span className="service-icon"><Icon size={22}/></span><strong>{name}</strong><small>{detail}</small><ArrowUpRight className="tile-arrow" size={18}/></div>)}</div></section>
            <section id="how-it-works" className="steps-section"><div className="section-heading"><div><p className="eyebrow">GET STARTED IN MINUTES</p><h2>Simple from the start</h2></div></div><div className="steps-grid"><div><span>01</span><h3>Create your account</h3><p>Register with your email and sign in securely.</p></div><div><span>02</span><h3>Access your dashboard</h3><p>View your wallet information and account activity.</p></div><div><span>03</span><h3>Choose a service</h3><p>Service checkout will open when verified provider payments are ready.</p></div></div></section>
            <section id="account" className="account-section"><div className="account-copy"><p className="eyebrow">GREAT VTU ACCOUNT</p><h2>Everything starts<br/>with your account.</h2><p>Sign in to access your Great VTU dashboard. New here? Create an account in a few steps.</p><div className="account-points"><span><ShieldCheck/> Supabase-powered authentication</span><span><Database/> Wallet and transaction data</span></div></div><div className="auth-card"><div className="auth-tabs"><button className={authMode==="signin"?"active":""} onClick={()=>{setAuthMode("signin");setNotice("");}}>Sign in</button><button className={authMode==="signup"?"active":""} onClick={()=>{setAuthMode("signup");setNotice("");}}>Create account</button></div><h3>{authMode==="signin"?"Welcome back":"Create your account"}</h3><p className="muted">{authMode==="signin"?"Enter your details to continue.":"Get started with Great VTU."}</p><form onSubmit={handleAuth}><label>Email address<input type="email" autoComplete="email" placeholder="you@example.com" value={email} onChange={e=>setEmail(e.target.value)} required/></label><label>Password<input type="password" autoComplete={authMode==="signin"?"current-password":"new-password"} placeholder="At least 6 characters" minLength={6} value={password} onChange={e=>setPassword(e.target.value)} required/></label><button className="primary-btn submit-btn" disabled={busy}>{busy?"Please wait…":authMode==="signin"?"Sign in":"Create account"} <ArrowRight size={17}/></button></form>{!isSupabaseConfigured && <p className="config-warning">Supabase connection needs to be configured before account sign-in can work.</p>}{notice && <p className="form-notice">{notice}</p>}<p className="auth-foot"><ShieldCheck size={14}/> Your account is protected by Supabase Auth.</p></div></section>
          </main>
        </>
      )}
      <footer className="footer"><a className="brand" href="#"><span className="brand-mark">G</span><span>GREAT<span className="brand-light">VTU</span></span></a><span>© {new Date().getFullYear()} Great VTU. Built by Great Media Enterprise.</span><span>Digital services, made simpler.</span></footer>
      {notice && session && <div className="notice"><span>{notice}</span><button onClick={() => setNotice("")} aria-label="Dismiss"><X size={16}/></button></div>}
    </div>
  );
}