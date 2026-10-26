import React, { useState, useEffect, useRef } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { notificationService } from '../services/notificationService';
import {
  Shield, Bell, LogOut, Search, Home, LayoutDashboard,
  FileText, CheckSquare, Zap, Menu, X, User, PlusCircle
} from 'lucide-react';

export default function Navbar() {
  const { user, isAuthenticated, isAdmin, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [unreadCount, setUnreadCount] = useState(0);
  const [mobileOpen, setMobileOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const menuRef = useRef(null);

  // Fetch unread notifications
  useEffect(() => {
    if (isAuthenticated && user?.id) {
      notificationService.getUnreadCount(user.id)
        .then((res) => setUnreadCount(res.data || 0))
        .catch(() => setUnreadCount(0));
    }
  }, [isAuthenticated, user?.id, location.pathname]);

  // Close mobile menu on route change
  useEffect(() => {
    setMobileOpen(false);
  }, [location.pathname]);

  // Scrolled navbar shadow
  useEffect(() => {
    const handler = () => setScrolled(window.scrollY > 10);
    window.addEventListener('scroll', handler, { passive: true });
    return () => window.removeEventListener('scroll', handler);
  }, []);

  // Close on outside click
  useEffect(() => {
    const handler = (e) => {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setMobileOpen(false);
      }
    };
    if (mobileOpen) document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, [mobileOpen]);

  // Lock body scroll when mobile menu open
  useEffect(() => {
    document.body.style.overflow = mobileOpen ? 'hidden' : '';
    return () => { document.body.style.overflow = ''; };
  }, [mobileOpen]);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path) => location.pathname === path;

  const navLinkStyle = (path) => ({
    color: isActive(path) ? 'var(--primary-color)' : '#475569',
    fontWeight: isActive(path) ? 700 : 500,
    fontSize: '0.875rem',
    display: 'flex',
    alignItems: 'center',
    gap: '5px',
    padding: '0.35rem 0.5rem',
    borderRadius: 'var(--radius-sm)',
    transition: 'color 0.2s ease, background 0.2s ease',
    textDecoration: 'none',
    position: 'relative',
  });

  return (
    <>
      <nav
        ref={menuRef}
        style={{
          background: '#ffffff',
          borderBottom: scrolled ? '1px solid var(--border-color)' : '1px solid transparent',
          padding: '0 2rem',
          position: 'sticky',
          top: 0,
          zIndex: 'var(--z-sticky)',
          boxShadow: scrolled ? '0 2px 16px rgba(0,0,0,0.07)' : '0 1px 3px rgba(0,0,0,0.04)',
          transition: 'box-shadow 0.3s ease, border-color 0.3s ease',
          height: '64px',
          display: 'flex',
          alignItems: 'center',
        }}
      >
        <div style={{
          maxWidth: 1200,
          width: '100%',
          margin: '0 auto',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
        }}>
          {/* Logo */}
          <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', textDecoration: 'none' }}>
            <div style={{
              background: 'linear-gradient(135deg, #2563eb, #1d4ed8)',
              padding: '8px',
              borderRadius: '10px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#fff',
              boxShadow: '0 2px 8px rgba(37,99,235,0.3)',
            }}>
              <Shield size={20} />
            </div>
            <div>
              <span style={{ fontSize: '1.2rem', fontWeight: 800, color: '#0f172a', letterSpacing: '-0.03em', fontFamily: 'var(--heading-font)' }}>
                CIRMS
              </span>
              {isAdmin && (
                <span style={{
                  marginLeft: '6px',
                  background: '#fee2e2', color: '#b91c1c',
                  fontSize: '0.6rem', fontWeight: 700,
                  padding: '2px 5px', borderRadius: '4px',
                  letterSpacing: '0.05em',
                }}>
                  ADMIN
                </span>
              )}
            </div>
          </Link>

          {/* Desktop Nav */}
          <div className="hide-mobile" style={{ display: 'flex', alignItems: 'center', gap: '0.25rem' }}>
            <Link to="/" style={navLinkStyle('/')}>
              <Home size={15} /> Home
            </Link>
            <Link to="/search" style={navLinkStyle('/search')}>
              <Search size={15} /> Search
            </Link>

            {isAuthenticated ? (
              <>
                {isAdmin ? (
                  <Link to="/admin/dashboard" style={navLinkStyle('/admin/dashboard')}>
                    <LayoutDashboard size={15} /> Admin Dashboard
                  </Link>
                ) : (
                  <>
                    <Link to="/report/lost" style={navLinkStyle('/report/lost')}>
                      <PlusCircle size={15} /> Report Lost
                    </Link>
                    <Link to="/report/found" style={navLinkStyle('/report/found')}>
                      <PlusCircle size={15} /> Report Found
                    </Link>
                    <Link to="/my-reports" style={navLinkStyle('/my-reports')}>
                      <FileText size={15} /> My Reports
                    </Link>
                    <Link to="/my-claims" style={navLinkStyle('/my-claims')}>
                      <CheckSquare size={15} /> My Claims
                    </Link>
                    <Link to="/matches" style={{
                      ...navLinkStyle('/matches'),
                      background: isActive('/matches') ? 'var(--primary-light)' : 'linear-gradient(135deg, #eff6ff, #f5f3ff)',
                      color: '#2563eb',
                      fontWeight: 600,
                      padding: '0.35rem 0.7rem',
                    }}>
                      <Zap size={14} /> Matches
                    </Link>
                    <Link to="/dashboard" style={navLinkStyle('/dashboard')}>
                      <LayoutDashboard size={15} /> Dashboard
                    </Link>
                  </>
                )}
              </>
            ) : null}
          </div>

          {/* Right Actions (Desktop) */}
          <div className="hide-mobile" style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            {isAuthenticated ? (
              <>
                {/* Bell */}
                <Link to="/notifications" style={{ position: 'relative', color: '#64748b', display: 'flex', padding: '6px' }}>
                  <Bell size={20} />
                  {unreadCount > 0 && (
                    <span style={{
                      position: 'absolute', top: 2, right: 2,
                      background: '#ef4444', color: '#fff',
                      fontSize: '0.6rem', fontWeight: 700,
                      width: 15, height: 15, borderRadius: '50%',
                      display: 'flex', alignItems: 'center', justifyContent: 'center',
                      border: '1.5px solid #fff',
                      animation: 'pulse 2s ease-in-out infinite',
                    }}>
                      {unreadCount > 9 ? '9+' : unreadCount}
                    </span>
                  )}
                </Link>

                {/* Avatar / Name */}
                <Link to="/profile" style={{
                  display: 'flex', alignItems: 'center', gap: '8px',
                  color: '#334155', textDecoration: 'none',
                  padding: '6px 10px',
                  borderRadius: 'var(--radius-md)',
                  border: '1px solid var(--border-color)',
                  background: 'var(--bg-subtle)',
                  transition: 'background 0.2s ease',
                }}
                  onMouseEnter={e => e.currentTarget.style.background = '#e2e8f0'}
                  onMouseLeave={e => e.currentTarget.style.background = 'var(--bg-subtle)'}
                >
                  <div style={{
                    width: 28, height: 28, borderRadius: '50%',
                    background: 'linear-gradient(135deg, #2563eb, #7c3aed)',
                    color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center',
                    fontSize: '0.7rem', fontWeight: 700, flexShrink: 0,
                  }}>
                    {user?.fullName?.charAt(0)?.toUpperCase() || 'U'}
                  </div>
                  <span style={{ fontSize: '0.82rem', fontWeight: 600, maxWidth: 90, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                    {user?.fullName?.split(' ')[0] || 'User'}
                  </span>
                </Link>

                <button onClick={handleLogout} className="btn btn-outline btn-sm" style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                  <LogOut size={14} /> Logout
                </button>
              </>
            ) : (
              <>
                <Link to="/login" className="btn btn-outline btn-sm">Sign In</Link>
                <Link to="/register" className="btn btn-primary btn-sm">Register</Link>
              </>
            )}
          </div>

          {/* Mobile: Bell + Hamburger */}
          <div className="show-mobile" style={{ display: 'none', alignItems: 'center', gap: '0.5rem' }}>
            {isAuthenticated && (
              <Link to="/notifications" style={{ position: 'relative', color: '#64748b', display: 'flex', padding: '6px' }}>
                <Bell size={20} />
                {unreadCount > 0 && (
                  <span style={{
                    position: 'absolute', top: 2, right: 2,
                    background: '#ef4444', color: '#fff',
                    fontSize: '0.6rem', fontWeight: 700,
                    width: 15, height: 15, borderRadius: '50%',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    border: '1.5px solid #fff',
                  }}>
                    {unreadCount > 9 ? '9+' : unreadCount}
                  </span>
                )}
              </Link>
            )}
            <button
              onClick={() => setMobileOpen(v => !v)}
              style={{
                background: 'none', border: 'none', cursor: 'pointer',
                color: '#334155', padding: '6px', borderRadius: 'var(--radius-sm)',
                display: 'flex', alignItems: 'center',
              }}
              aria-label="Toggle menu"
            >
              {mobileOpen ? <X size={22} /> : <Menu size={22} />}
            </button>
          </div>
        </div>
      </nav>

      {/* Mobile Drawer Overlay */}
      {mobileOpen && (
        <div
          style={{
            position: 'fixed', inset: 0,
            background: 'rgba(0,0,0,0.4)',
            zIndex: 98,
            animation: 'fadeIn 0.2s ease',
          }}
          onClick={() => setMobileOpen(false)}
        />
      )}

      {/* Mobile Drawer */}
      <div
        style={{
          position: 'fixed',
          top: 0,
          right: 0,
          width: 'min(320px, 85vw)',
          height: '100dvh',
          background: '#ffffff',
          zIndex: 99,
          boxShadow: '-4px 0 24px rgba(0,0,0,0.12)',
          transform: mobileOpen ? 'translateX(0)' : 'translateX(100%)',
          transition: 'transform 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
          overflowY: 'auto',
          display: 'flex',
          flexDirection: 'column',
        }}
      >
        {/* Drawer Header */}
        <div style={{
          padding: '1.25rem 1.5rem',
          borderBottom: '1px solid var(--border-color)',
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <div style={{
              background: 'linear-gradient(135deg, #2563eb, #1d4ed8)',
              padding: '7px', borderRadius: '9px', color: '#fff',
            }}>
              <Shield size={18} />
            </div>
            <span style={{ fontFamily: 'var(--heading-font)', fontWeight: 800, fontSize: '1.1rem', color: '#0f172a' }}>CIRMS</span>
          </div>
          <button
            onClick={() => setMobileOpen(false)}
            style={{ background: 'none', border: 'none', cursor: 'pointer', color: '#64748b', padding: '4px' }}
          >
            <X size={20} />
          </button>
        </div>

        {/* Drawer User Info */}
        {isAuthenticated && (
          <div style={{
            padding: '1rem 1.5rem',
            background: 'linear-gradient(135deg, #eff6ff, #f5f3ff)',
            borderBottom: '1px solid var(--border-color)',
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
              <div style={{
                width: 40, height: 40, borderRadius: '50%',
                background: 'linear-gradient(135deg, #2563eb, #7c3aed)',
                color: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center',
                fontSize: '1rem', fontWeight: 700, flexShrink: 0,
              }}>
                {user?.fullName?.charAt(0)?.toUpperCase() || 'U'}
              </div>
              <div>
                <div style={{ fontWeight: 700, fontSize: '0.95rem', color: '#0f172a' }}>{user?.fullName}</div>
                <div style={{ fontSize: '0.78rem', color: '#64748b' }}>{user?.email}</div>
              </div>
            </div>
          </div>
        )}

        {/* Drawer Links */}
        <nav style={{ flex: 1, padding: '1rem 0' }}>
          {[
            { to: '/', icon: <Home size={17} />, label: 'Home' },
            { to: '/search', icon: <Search size={17} />, label: 'Search Items' },
            ...(!isAdmin && isAuthenticated ? [
              { to: '/report/lost', icon: <PlusCircle size={17} />, label: 'Report Lost Item' },
              { to: '/report/found', icon: <PlusCircle size={17} />, label: 'Report Found Item' },
              { to: '/my-reports', icon: <FileText size={17} />, label: 'My Reports' },
              { to: '/my-claims', icon: <CheckSquare size={17} />, label: 'My Claims' },
              { to: '/matches', icon: <Zap size={17} />, label: '⚡ Smart Matches' },
              { to: '/dashboard', icon: <LayoutDashboard size={17} />, label: 'Dashboard' },
            ] : []),
            ...(isAdmin ? [
              { to: '/admin/dashboard', icon: <LayoutDashboard size={17} />, label: 'Admin Dashboard' },
            ] : []),
            ...(isAuthenticated ? [
              { to: '/profile', icon: <User size={17} />, label: 'My Profile' },
            ] : []),
          ].map((item) => (
            <Link
              key={item.to}
              to={item.to}
              style={{
                display: 'flex', alignItems: 'center', gap: '0.75rem',
                padding: '0.8rem 1.5rem',
                color: isActive(item.to) ? 'var(--primary-color)' : '#475569',
                fontWeight: isActive(item.to) ? 700 : 500,
                fontSize: '0.9rem',
                background: isActive(item.to) ? 'var(--primary-light)' : 'transparent',
                borderLeft: isActive(item.to) ? '3px solid var(--primary-color)' : '3px solid transparent',
                transition: 'background 0.15s ease',
                textDecoration: 'none',
              }}
              onMouseEnter={e => { if (!isActive(item.to)) e.currentTarget.style.background = '#f8fafc'; }}
              onMouseLeave={e => { if (!isActive(item.to)) e.currentTarget.style.background = 'transparent'; }}
            >
              {item.icon}
              {item.label}
            </Link>
          ))}
        </nav>

        {/* Drawer Footer */}
        <div style={{ padding: '1rem 1.5rem', borderTop: '1px solid var(--border-color)' }}>
          {isAuthenticated ? (
            <button
              onClick={handleLogout}
              className="btn btn-outline"
              style={{ width: '100%', justifyContent: 'center', display: 'flex', alignItems: 'center', gap: '8px', color: '#ef4444', borderColor: '#fecaca' }}
            >
              <LogOut size={16} /> Sign Out
            </button>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <Link to="/login" className="btn btn-outline" style={{ width: '100%', justifyContent: 'center' }}>Sign In</Link>
              <Link to="/register" className="btn btn-primary" style={{ width: '100%', justifyContent: 'center' }}>Create Account</Link>
            </div>
          )}
        </div>
      </div>

      <style>{`
        @media (max-width: 768px) {
          .hide-mobile { display: none !important; }
          .show-mobile { display: flex !important; }
        }
        @media (min-width: 769px) {
          .hide-desktop { display: none !important; }
          .show-mobile  { display: none !important; }
        }
      `}</style>
    </>
  );
}
