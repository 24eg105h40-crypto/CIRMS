import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { itemService } from '../services/itemService';
import { notificationService } from '../services/notificationService';
import { PlusCircle, Search, FileText, Bell, CheckCircle, ArrowRight, Loader2 } from 'lucide-react';

export default function StudentDashboard() {
  const { user } = useAuth();
  const [stats, setStats] = useState({
    lostCount: 0,
    foundCount: 0,
    pendingClaims: 0,
    recoveredCount: 0,
  });
  const [unreadNotifs, setUnreadNotifs] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, [user]);

  const fetchStats = async () => {
    if (!user?.id) return;
    setLoading(true);
    try {
      const [statsRes, notifsRes] = await Promise.allSettled([
        itemService.getMyStats(),
        notificationService.getUnreadCount(user.id),
      ]);

      if (statsRes.status === 'fulfilled') {
        const s = statsRes.value?.data || {};
        setStats({
          lostCount: s.lostCount ?? 0,
          foundCount: s.foundCount ?? 0,
          pendingClaims: s.pendingClaims ?? 0,
          recoveredCount: s.recoveredCount ?? 0,
        });
      }

      if (notifsRes.status === 'fulfilled') {
        setUnreadNotifs(notifsRes.value?.data ?? 0);
      }
    } catch (err) {
      console.error('Dashboard stats error:', err);
    } finally {
      setLoading(false);
    }
  };

  const KpiCard = ({ label, value, color }) => (
    <div className="cirms-card" style={{ position: 'relative', overflow: 'hidden' }}>
      <div style={{
        position: 'absolute', top: 0, left: 0, bottom: 0, width: 4,
        background: color, borderRadius: 'var(--radius-lg) 0 0 var(--radius-lg)'
      }} />
      <span style={{ fontSize: '0.82rem', color: 'var(--text-muted)', fontWeight: 600, display: 'block', marginBottom: '0.5rem' }}>
        {label}
      </span>
      {loading ? (
        <Loader2 size={24} color={color} style={{ animation: 'spin 1s linear infinite' }} />
      ) : (
        <div style={{ fontSize: '2.25rem', fontWeight: 800, color }}>{value}</div>
      )}
    </div>
  );

  return (
    <div className="container" style={{ padding: '3rem 1rem' }}>
      {/* Welcome Banner */}
      <div className="cirms-card" style={{
        background: 'linear-gradient(135deg, #1d4ed8 0%, #0f172a 100%)',
        color: '#ffffff',
        padding: '2.5rem',
        marginBottom: '2rem',
        borderRadius: 'var(--radius-lg)',
        position: 'relative',
        overflow: 'hidden',
      }}>
        {/* decorative circles */}
        <div style={{ position: 'absolute', top: -30, right: -30, width: 200, height: 200, borderRadius: '50%', background: 'rgba(255,255,255,0.05)', pointerEvents: 'none' }} />
        <div style={{ position: 'absolute', bottom: -50, right: 80, width: 150, height: 150, borderRadius: '50%', background: 'rgba(255,255,255,0.04)', pointerEvents: 'none' }} />

        <h1 style={{ color: '#fff', fontSize: '2rem', marginBottom: '0.5rem', position: 'relative' }}>
          Welcome back, {user?.fullName?.split(' ')[0] || 'Student'}!
        </h1>
        <p style={{ color: '#93c5fd', fontSize: '0.95rem', maxWidth: 560, position: 'relative' }}>
          Manage your reported items, track active claims, and review real-time recovery updates all in one place.
        </p>

        <div style={{ display: 'flex', gap: '0.75rem', marginTop: '1.75rem', flexWrap: 'wrap', position: 'relative' }}>
          <Link to="/report/lost" className="btn" style={{ background: '#ffffff', color: '#1e40af', fontWeight: 700, gap: '6px', display: 'flex', alignItems: 'center' }}>
            <PlusCircle size={16} /> Report Lost Item
          </Link>
          <Link to="/report/found" className="btn" style={{ background: '#10b981', color: '#ffffff', fontWeight: 700, gap: '6px', display: 'flex', alignItems: 'center' }}>
            <PlusCircle size={16} /> Report Found Item
          </Link>
          <Link to="/search" className="btn btn-outline" style={{ background: 'rgba(255,255,255,0.1)', color: '#fff', borderColor: 'rgba(255,255,255,0.25)', gap: '6px', display: 'flex', alignItems: 'center' }}>
            <Search size={16} /> Search Items
          </Link>
        </div>
      </div>

      {/* KPI Stats Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1.25rem', marginBottom: '2.5rem' }}>
        <KpiCard label="My Lost Reports" value={stats.lostCount} color="#ef4444" />
        <KpiCard label="My Found Reports" value={stats.foundCount} color="#10b981" />
        <KpiCard label="Pending Claims" value={stats.pendingClaims} color="#f59e0b" />
        <KpiCard label="Recovered Items" value={stats.recoveredCount} color="#2563eb" />
      </div>

      {/* Quick Navigation Cards */}
      <h2 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '1rem' }}>Quick Actions</h2>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))', gap: '1.25rem' }}>
        <Link to="/my-reports" className="cirms-card" style={{ display: 'flex', alignItems: 'center', gap: '1rem', transition: 'transform 0.2s', textDecoration: 'none' }}
          onMouseEnter={e => e.currentTarget.style.transform = 'translateY(-2px)'}
          onMouseLeave={e => e.currentTarget.style.transform = 'translateY(0)'}
        >
          <div style={{ background: 'var(--primary-light)', padding: '12px', borderRadius: '12px', color: 'var(--primary-color)', flexShrink: 0 }}>
            <FileText size={24} />
          </div>
          <div style={{ flex: 1 }}>
            <h4 style={{ fontSize: '1rem', marginBottom: '0.2rem', fontWeight: 700 }}>My Reports</h4>
            <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>View & manage your lost/found items</p>
          </div>
          <ArrowRight size={16} color="var(--text-muted)" />
        </Link>

        <Link to="/my-claims" className="cirms-card" style={{ display: 'flex', alignItems: 'center', gap: '1rem', transition: 'transform 0.2s', textDecoration: 'none' }}
          onMouseEnter={e => e.currentTarget.style.transform = 'translateY(-2px)'}
          onMouseLeave={e => e.currentTarget.style.transform = 'translateY(0)'}
        >
          <div style={{ background: '#fef3c7', padding: '12px', borderRadius: '12px', color: '#b45309', flexShrink: 0 }}>
            <CheckCircle size={24} />
          </div>
          <div style={{ flex: 1 }}>
            <h4 style={{ fontSize: '1rem', marginBottom: '0.2rem', fontWeight: 700 }}>My Claims</h4>
            <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Track ownership verification status</p>
          </div>
          <ArrowRight size={16} color="var(--text-muted)" />
        </Link>

        <Link to="/notifications" className="cirms-card" style={{ display: 'flex', alignItems: 'center', gap: '1rem', transition: 'transform 0.2s', textDecoration: 'none', position: 'relative' }}
          onMouseEnter={e => e.currentTarget.style.transform = 'translateY(-2px)'}
          onMouseLeave={e => e.currentTarget.style.transform = 'translateY(0)'}
        >
          <div style={{ background: '#e0e7ff', padding: '12px', borderRadius: '12px', color: '#4338ca', flexShrink: 0, position: 'relative' }}>
            <Bell size={24} />
            {unreadNotifs > 0 && (
              <span style={{
                position: 'absolute', top: -4, right: -4,
                background: '#ef4444', color: '#fff',
                width: 18, height: 18, borderRadius: '50%',
                fontSize: '0.65rem', fontWeight: 700,
                display: 'flex', alignItems: 'center', justifyContent: 'center'
              }}>{unreadNotifs > 9 ? '9+' : unreadNotifs}</span>
            )}
          </div>
          <div style={{ flex: 1 }}>
            <h4 style={{ fontSize: '1rem', marginBottom: '0.2rem', fontWeight: 700 }}>Notifications</h4>
            <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
              {unreadNotifs > 0 ? `${unreadNotifs} unread notification${unreadNotifs > 1 ? 's' : ''}` : 'Item matches and claim decisions'}
            </p>
          </div>
          <ArrowRight size={16} color="var(--text-muted)" />
        </Link>

        <Link to="/matches" className="cirms-card" style={{ display: 'flex', alignItems: 'center', gap: '1rem', transition: 'transform 0.2s', textDecoration: 'none' }}
          onMouseEnter={e => e.currentTarget.style.transform = 'translateY(-2px)'}
          onMouseLeave={e => e.currentTarget.style.transform = 'translateY(0)'}
        >
          <div style={{ background: 'linear-gradient(135deg, #eff6ff, #f5f3ff)', padding: '12px', borderRadius: '12px', color: '#2563eb', flexShrink: 0 }}>
            <span style={{ fontSize: '1.25rem' }}>⚡</span>
          </div>
          <div style={{ flex: 1 }}>
            <h4 style={{ fontSize: '1rem', marginBottom: '0.2rem', fontWeight: 700 }}>Smart Matches</h4>
            <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>AI-scored potential item matches</p>
          </div>
          <ArrowRight size={16} color="var(--text-muted)" />
        </Link>
      </div>

      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </div>
  );
}
