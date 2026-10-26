import React, { useEffect, useState, useRef } from 'react';
import { Link } from 'react-router-dom';
import {
  Search, PlusCircle, ShieldCheck, Clock, Sparkles,
  ArrowRight, Package, MapPin, Zap, Users, TrendingUp
} from 'lucide-react';
import { itemService } from '../services/itemService';

/* ── Category emoji map ─────────────────────────────── */
const CAT_ICONS = {
  'Electronics':  '💻',
  'Accessories':  '👜',
  'Documents':    '📄',
  'Clothing':     '👕',
  'Keys':         '🔑',
  'Books':        '📚',
  'Bags':         '🎒',
  'Stationery':   '✏️',
  'Sports':       '⚽',
  'Jewellery':    '💍',
  'Mobile Phone': '📱',
  default:        '📦',
};

/* ── Animated counter ───────────────────────────────── */
function Counter({ target, suffix = '' }) {
  const [value, setValue] = useState(0);
  const ref = useRef(null);
  useEffect(() => {
    const observer = new IntersectionObserver(([entry]) => {
      if (!entry.isIntersecting) return;
      observer.disconnect();
      const duration = 1400;
      const start = performance.now();
      const animate = (now) => {
        const elapsed = now - start;
        const progress = Math.min(elapsed / duration, 1);
        const ease = 1 - Math.pow(1 - progress, 3); // cubic ease-out
        setValue(Math.round(target * ease));
        if (progress < 1) requestAnimationFrame(animate);
      };
      requestAnimationFrame(animate);
    }, { threshold: 0.5 });
    if (ref.current) observer.observe(ref.current);
    return () => observer.disconnect();
  }, [target]);
  return <span ref={ref}>{value}{suffix}</span>;
}

/* ── Step card component ────────────────────────────── */
function StepCard({ step, title, desc, delay }) {
  const colors = ['#2563eb', '#7c3aed', '#0891b2', '#059669', '#d97706'];
  const color = colors[(step - 1) % colors.length];
  return (
    <div
      className="cirms-card cirms-card--lift animate-fade-in-up"
      style={{
        padding: '2rem 1.5rem',
        textAlign: 'center',
        animationDelay: `${delay}ms`,
        position: 'relative',
        overflow: 'hidden',
        borderTop: `3px solid ${color}`,
      }}
    >
      <div style={{ position: 'absolute', top: -20, right: -20, width: 80, height: 80, borderRadius: '50%', background: color, opacity: 0.06 }} />
      <div style={{
        width: 52, height: 52, borderRadius: '50%',
        background: `${color}18`,
        color: color,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        margin: '0 auto 1.25rem',
        fontSize: '1.35rem', fontWeight: 800,
        border: `2px solid ${color}30`,
      }}>
        {step}
      </div>
      <h3 style={{ fontSize: '1.1rem', marginBottom: '0.5rem', color: '#0f172a' }}>{title}</h3>
      <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', lineHeight: 1.6 }}>{desc}</p>
    </div>
  );
}

/* ── Feature card ───────────────────────────────────── */
function FeatureCard({ icon, title, desc, gradient }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
      <div style={{
        width: 52, height: 52, borderRadius: '14px',
        background: gradient,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        color: '#fff', flexShrink: 0,
        boxShadow: '0 4px 14px rgba(255,255,255,0.15)',
      }}>
        {icon}
      </div>
      <div>
        <h3 style={{ color: '#fff', fontSize: '1.2rem', marginBottom: '0.5rem' }}>{title}</h3>
        <p style={{ color: '#cbd5e1', fontSize: '0.875rem', lineHeight: 1.7 }}>{desc}</p>
      </div>
    </div>
  );
}

export default function HomePage() {
  const [categories, setCategories] = useState([]);
  const [recentItems, setRecentItems] = useState([]);
  const [heroVisible, setHeroVisible] = useState(false);

  useEffect(() => {
    // Trigger hero animation
    setTimeout(() => setHeroVisible(true), 50);

    // Load categories
    itemService.getCategories()
      .then((res) => setCategories(res.data || []))
      .catch(() => {});

    // Load 6 most recent items for preview
    itemService.searchItems({ size: 6, page: 0 })
      .then((res) => setRecentItems(res.data?.content || []))
      .catch(() => {});
  }, []);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 0 }}>

      {/* ── Hero ─────────────────────────────────────────── */}
      <section style={{
        background: 'linear-gradient(145deg, #0f172a 0%, #1e293b 50%, #0c1a3a 100%)',
        color: '#ffffff',
        padding: '7rem 2rem 6rem',
        textAlign: 'center',
        position: 'relative',
        overflow: 'hidden',
      }}>
        {/* Animated decorative blobs */}
        <div style={{
          position: 'absolute', top: -60, left: -60, width: 400, height: 400,
          borderRadius: '50%', background: 'radial-gradient(circle, rgba(37,99,235,0.18) 0%, transparent 70%)',
          animation: 'float 6s ease-in-out infinite',
          pointerEvents: 'none',
        }} />
        <div style={{
          position: 'absolute', bottom: -80, right: -60, width: 350, height: 350,
          borderRadius: '50%', background: 'radial-gradient(circle, rgba(124,58,237,0.15) 0%, transparent 70%)',
          animation: 'float 7s ease-in-out infinite reverse',
          pointerEvents: 'none',
        }} />
        <div style={{
          position: 'absolute', top: '40%', left: '60%', width: 200, height: 200,
          borderRadius: '50%', background: 'radial-gradient(circle, rgba(6,182,212,0.10) 0%, transparent 70%)',
          animation: 'float 5s ease-in-out infinite',
          pointerEvents: 'none',
        }} />

        {/* Grid pattern overlay */}
        <div style={{
          position: 'absolute', inset: 0,
          backgroundImage: 'linear-gradient(rgba(255,255,255,0.02) 1px, transparent 1px), linear-gradient(90deg, rgba(255,255,255,0.02) 1px, transparent 1px)',
          backgroundSize: '48px 48px',
          pointerEvents: 'none',
        }} />

        <div style={{
          maxWidth: 820, margin: '0 auto', position: 'relative',
          opacity: heroVisible ? 1 : 0,
          transform: heroVisible ? 'translateY(0)' : 'translateY(20px)',
          transition: 'opacity 0.7s ease, transform 0.7s ease',
        }}>
          {/* Badge */}
          <div style={{
            display: 'inline-flex', alignItems: 'center', gap: '0.5rem',
            background: 'rgba(37, 99, 235, 0.2)',
            color: '#93c5fd',
            padding: '0.45rem 1.1rem',
            borderRadius: '9999px',
            fontSize: '0.82rem',
            fontWeight: 600,
            marginBottom: '1.75rem',
            border: '1px solid rgba(96, 165, 250, 0.3)',
            backdropFilter: 'blur(8px)',
          }}>
            <Sparkles size={14} />
            Campus Item Recovery & Management System
          </div>

          {/* Heading */}
          <h1 style={{
            fontSize: 'clamp(2.25rem, 5vw, 3.75rem)',
            fontWeight: 900,
            color: '#ffffff',
            marginBottom: '1.25rem',
            letterSpacing: '-0.04em',
            lineHeight: 1.12,
            fontFamily: 'var(--heading-font)',
          }}>
            Lost Something on Campus?<br />
            <span style={{
              background: 'linear-gradient(135deg, #38bdf8, #818cf8)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              backgroundClip: 'text',
            }}>
              Find It Faster with CIRMS.
            </span>
          </h1>

          {/* Subheading */}
          <p style={{
            fontSize: '1.1rem', color: '#94a3b8',
            marginBottom: '2.75rem', maxWidth: 600, margin: '0 auto 2.75rem',
            lineHeight: 1.75,
          }}>
            A centralized, verified lost & found network — connecting students, faculty, and campus security for rapid, secure item recovery.
          </p>

          {/* CTA Buttons */}
          <div style={{ display: 'flex', justifyContent: 'center', gap: '0.75rem', flexWrap: 'wrap' }}>
            <Link
              to="/report/lost"
              className="btn btn-primary btn-lg"
              style={{ boxShadow: '0 4px 20px rgba(37,99,235,0.4)' }}
            >
              <PlusCircle size={18} /> Report Lost Item
            </Link>
            <Link
              to="/report/found"
              className="btn btn-lg"
              style={{ background: '#10b981', color: '#fff', boxShadow: '0 4px 20px rgba(16,185,129,0.35)' }}
            >
              <PlusCircle size={18} /> Report Found Item
            </Link>
            <Link
              to="/search"
              className="btn btn-lg"
              style={{ background: 'rgba(255,255,255,0.08)', color: '#fff', borderColor: 'rgba(255,255,255,0.2)', backdropFilter: 'blur(8px)' }}
            >
              <Search size={18} /> Browse Items
            </Link>
          </div>
        </div>

        {/* Stats row */}
        <div style={{
          display: 'flex', justifyContent: 'center', gap: '3rem', flexWrap: 'wrap',
          marginTop: '4rem', position: 'relative',
          opacity: heroVisible ? 1 : 0,
          transition: 'opacity 0.8s ease 0.3s',
        }}>
          {[
            { icon: <Users size={18} />, value: 500, suffix: '+', label: 'Registered Students' },
            { icon: <Package size={18} />, value: 1200, suffix: '+', label: 'Items Catalogued' },
            { icon: <TrendingUp size={18} />, value: 92, suffix: '%', label: 'Recovery Rate' },
          ].map((stat) => (
            <div key={stat.label} style={{ textAlign: 'center' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '6px', justifyContent: 'center', color: '#60a5fa', marginBottom: '4px' }}>
                {stat.icon}
                <span style={{ fontSize: '2rem', fontWeight: 800, color: '#f1f5f9', fontFamily: 'var(--heading-font)' }}>
                  <Counter target={stat.value} suffix={stat.suffix} />
                </span>
              </div>
              <div style={{ fontSize: '0.8rem', color: '#64748b', fontWeight: 500 }}>{stat.label}</div>
            </div>
          ))}
        </div>
      </section>

      {/* ── How It Works ─────────────────────────────────── */}
      <section className="container" style={{ padding: '5rem 1.5rem' }}>
        <div style={{ textAlign: 'center', marginBottom: '3.5rem' }}>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', background: 'var(--primary-light)', color: 'var(--primary-color)', padding: '4px 14px', borderRadius: '9999px', fontSize: '0.78rem', fontWeight: 700, marginBottom: '1rem', letterSpacing: '0.04em', textTransform: 'uppercase' }}>
            Simple Process
          </div>
          <h2 style={{ fontSize: 'clamp(1.6rem, 3vw, 2.25rem)', marginBottom: '0.75rem' }}>How CIRMS Works</h2>
          <p style={{ color: 'var(--text-muted)', maxWidth: 520, margin: '0 auto' }}>
            A transparent 5-step recovery workflow trusted by the campus community
          </p>
        </div>

        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(190px, 1fr))',
          gap: '1.25rem',
        }}>
          {[
            { title: 'Report', desc: 'Submit item details with photos, location, and timestamps.' },
            { title: 'Search',  desc: 'Browse catalog with smart filters by category, location, and date.' },
            { title: 'Claim',   desc: 'Provide confidential proof of ownership for a found item.' },
            { title: 'Verify',  desc: 'Security admins inspect and validate ownership evidence.' },
            { title: 'Recover', desc: 'Collect your verified item at the campus security desk.' },
          ].map((item, i) => (
            <StepCard key={i} step={i + 1} {...item} delay={i * 80} />
          ))}
        </div>
      </section>

      {/* ── Recent Items Preview ──────────────────────────── */}
      {recentItems.length > 0 && (
        <section style={{ background: 'var(--bg-subtle)', padding: '4rem 0' }}>
          <div className="container">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2.5rem', flexWrap: 'wrap', gap: '1rem' }}>
              <div>
                <h2 style={{ fontSize: '1.75rem', marginBottom: '0.3rem' }}>Recently Reported</h2>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>The latest lost & found items from campus</p>
              </div>
              <Link to="/search" className="btn btn-outline" style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                View All <ArrowRight size={15} />
              </Link>
            </div>

            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(290px, 1fr))',
              gap: '1.25rem',
            }}>
              {recentItems.map((item, i) => (
                <Link
                  key={item.id}
                  to={`/items/${item.id}`}
                  className="cirms-card cirms-card--lift animate-fade-in-up"
                  style={{ textDecoration: 'none', animationDelay: `${i * 60}ms`, display: 'flex', flexDirection: 'column' }}
                >
                  {/* Image */}
                  <div style={{
                    height: 160, background: '#f1f5f9', borderRadius: 'var(--radius-sm)',
                    marginBottom: '1rem', overflow: 'hidden', position: 'relative',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                  }}>
                    {item.imageUrl
                      ? <img src={item.imageUrl} alt={item.itemName} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                      : <Package size={40} color="#cbd5e1" />
                    }
                    <span style={{
                      position: 'absolute', top: 8, right: 8,
                      fontSize: '0.68rem', fontWeight: 700, padding: '2px 8px',
                      borderRadius: '9999px',
                      background: item.itemType === 'LOST' ? '#fee2e2' : '#dcfce7',
                      color: item.itemType === 'LOST' ? '#b91c1c' : '#15803d',
                    }}>
                      {item.itemType}
                    </span>
                  </div>
                  {/* Info */}
                  <h4 style={{ fontSize: '0.95rem', fontWeight: 700, marginBottom: '0.25rem', color: '#0f172a' }}>{item.itemName}</h4>
                  <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)', display: 'flex', gap: '10px', marginTop: 'auto', paddingTop: '0.5rem' }}>
                    {item.location && <span style={{ display: 'flex', alignItems: 'center', gap: '3px' }}><MapPin size={11} />{item.location}</span>}
                    {item.dateLostOrFound && <span>{item.dateLostOrFound}</span>}
                  </div>
                </Link>
              ))}
            </div>
          </div>
        </section>
      )}

      {/* ── Categories ────────────────────────────────────── */}
      {categories.length > 0 && (
        <section className="container" style={{ padding: '4.5rem 1.5rem' }}>
          <div style={{ textAlign: 'center', marginBottom: '2.5rem' }}>
            <h2 style={{ fontSize: '1.75rem', marginBottom: '0.4rem' }}>Browse by Category</h2>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Find items sorted across common campus categories</p>
          </div>

          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.75rem', justifyContent: 'center' }}>
            {categories.map((cat, i) => {
              const emoji = CAT_ICONS[cat.name] || CAT_ICONS.default;
              return (
                <Link
                  key={cat.id}
                  to={`/search?category=${cat.id}`}
                  className="animate-fade-in"
                  style={{
                    background: '#ffffff',
                    border: '1.5px solid var(--border-color)',
                    padding: '0.6rem 1.25rem',
                    borderRadius: '9999px',
                    color: 'var(--text-secondary)',
                    fontWeight: 600,
                    fontSize: '0.875rem',
                    display: 'flex', alignItems: 'center', gap: '0.5rem',
                    boxShadow: 'var(--shadow-sm)',
                    transition: 'all 0.2s ease',
                    textDecoration: 'none',
                    animationDelay: `${i * 40}ms`,
                  }}
                  onMouseEnter={e => {
                    e.currentTarget.style.background = 'var(--primary-color)';
                    e.currentTarget.style.color = '#fff';
                    e.currentTarget.style.borderColor = 'var(--primary-color)';
                    e.currentTarget.style.transform = 'translateY(-2px)';
                    e.currentTarget.style.boxShadow = 'var(--shadow-blue)';
                  }}
                  onMouseLeave={e => {
                    e.currentTarget.style.background = '#ffffff';
                    e.currentTarget.style.color = 'var(--text-secondary)';
                    e.currentTarget.style.borderColor = 'var(--border-color)';
                    e.currentTarget.style.transform = 'translateY(0)';
                    e.currentTarget.style.boxShadow = 'var(--shadow-sm)';
                  }}
                >
                  <span>{emoji}</span> {cat.name}
                </Link>
              );
            })}
          </div>
        </section>
      )}

      {/* ── Features Banner ───────────────────────────────── */}
      <section style={{ padding: '4rem 2rem 5rem', background: 'var(--bg-subtle)' }}>
        <div className="container">
          <div style={{
            background: 'linear-gradient(145deg, #0f172a, #1e3a8a 40%, #0f172a)',
            borderRadius: 'var(--radius-xl)',
            padding: 'clamp(2rem, 4vw, 3.5rem)',
            position: 'relative',
            overflow: 'hidden',
          }}>
            {/* Decorative elements */}
            <div style={{ position: 'absolute', top: -40, right: -40, width: 300, height: 300, borderRadius: '50%', background: 'rgba(37,99,235,0.12)', pointerEvents: 'none' }} />
            <div style={{ position: 'absolute', bottom: -60, left: -40, width: 250, height: 250, borderRadius: '50%', background: 'rgba(124,58,237,0.10)', pointerEvents: 'none' }} />

            <div style={{ textAlign: 'center', marginBottom: '3rem', position: 'relative' }}>
              <h2 style={{ color: '#fff', fontSize: 'clamp(1.5rem, 3vw, 2rem)', marginBottom: '0.5rem' }}>
                Built for Campus Security & Speed
              </h2>
              <p style={{ color: '#94a3b8', fontSize: '0.9rem' }}>
                Every feature designed to reconnect people with their belongings — fast.
              </p>
            </div>

            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))',
              gap: '2.5rem',
              position: 'relative',
            }}>
              <FeatureCard
                icon={<ShieldCheck size={22} />}
                title="Verified Campus Security"
                desc="Confidential claimant verification safeguards sensitive items and prevents unauthorized handovers."
                gradient="linear-gradient(135deg, #2563eb, #1d4ed8)"
              />
              <FeatureCard
                icon={<Zap size={22} />}
                title="Smart Match Engine"
                desc="Multi-attribute matching automatically compares reports and flags high-confidence pairs instantly."
                gradient="linear-gradient(135deg, #7c3aed, #6d28d9)"
              />
              <FeatureCard
                icon={<Clock size={22} />}
                title="Real-time Notifications"
                desc="Instant updates when claims are reviewed, matches are flagged, or items are ready for collection."
                gradient="linear-gradient(135deg, #0891b2, #0e7490)"
              />
            </div>

            {/* CTA inside banner */}
            <div style={{ textAlign: 'center', marginTop: '3rem', position: 'relative' }}>
              <Link
                to="/register"
                className="btn btn-primary btn-lg"
                style={{ boxShadow: '0 4px 20px rgba(37,99,235,0.4)', display: 'inline-flex' }}
              >
                Get Started — It's Free <ArrowRight size={18} />
              </Link>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
