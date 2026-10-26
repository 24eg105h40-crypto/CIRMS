import React from 'react';
import { Link } from 'react-router-dom';
import { Shield, Zap, Search, PlusCircle, FileText, CheckSquare, Mail, ExternalLink } from 'lucide-react';

export default function Footer() {
  const year = new Date().getFullYear();

  return (
    <footer style={{
      background: 'linear-gradient(180deg, #0f172a 0%, #060f1f 100%)',
      color: '#94a3b8',
      padding: '4rem 2rem 0',
      marginTop: 'auto',
      borderTop: '1px solid rgba(255,255,255,0.06)',
    }}>
      <div style={{ maxWidth: 1200, margin: '0 auto' }}>
        {/* Top grid */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
          gap: '2.5rem',
          paddingBottom: '3rem',
        }}>
          {/* Brand */}
          <div style={{ maxWidth: 300 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', marginBottom: '1rem' }}>
              <div style={{
                background: 'linear-gradient(135deg, #2563eb, #1d4ed8)',
                padding: '8px', borderRadius: '10px', display: 'flex',
                boxShadow: '0 0 16px rgba(37,99,235,0.3)',
              }}>
                <Shield size={20} color="#fff" />
              </div>
              <span style={{ fontSize: '1.25rem', fontWeight: 800, color: '#fff', fontFamily: 'var(--heading-font)', letterSpacing: '-0.02em' }}>
                CIRMS
              </span>
            </div>
            <p style={{ fontSize: '0.855rem', lineHeight: 1.75, color: '#64748b' }}>
              Campus Item Recovery & Management System — making lost and found reporting transparent, verified, and fast for the entire campus community.
            </p>
            <div style={{ display: 'flex', alignItems: 'center', gap: '6px', marginTop: '1.25rem', fontSize: '0.78rem', color: '#475569' }}>
              <Mail size={13} />
              <span>security@campus.edu</span>
            </div>
          </div>

          {/* Quick Links */}
          <div>
            <h4 style={{ color: '#e2e8f0', fontSize: '0.875rem', fontWeight: 700, marginBottom: '1.1rem', textTransform: 'uppercase', letterSpacing: '0.06em' }}>
              Quick Links
            </h4>
            <ul style={{ listStyle: 'none', padding: 0, display: 'flex', flexDirection: 'column', gap: '0.65rem' }}>
              {[
                { to: '/search',        icon: <Search size={13} />,      label: 'Search Items' },
                { to: '/report/lost',   icon: <PlusCircle size={13} />,  label: 'Report Lost Item' },
                { to: '/report/found',  icon: <PlusCircle size={13} />,  label: 'Report Found Item' },
                { to: '/my-reports',    icon: <FileText size={13} />,    label: 'My Reports' },
                { to: '/my-claims',     icon: <CheckSquare size={13} />, label: 'My Claims' },
                { to: '/matches',       icon: <Zap size={13} />,         label: 'Smart Matches' },
              ].map((link) => (
                <li key={link.to}>
                  <Link
                    to={link.to}
                    style={{
                      color: '#64748b',
                      fontSize: '0.855rem',
                      display: 'flex',
                      alignItems: 'center',
                      gap: '7px',
                      transition: 'color 0.2s ease',
                      textDecoration: 'none',
                    }}
                    onMouseEnter={e => e.currentTarget.style.color = '#93c5fd'}
                    onMouseLeave={e => e.currentTarget.style.color = '#64748b'}
                  >
                    <span style={{ color: '#334155' }}>{link.icon}</span>
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Campus Guidelines */}
          <div>
            <h4 style={{ color: '#e2e8f0', fontSize: '0.875rem', fontWeight: 700, marginBottom: '1.1rem', textTransform: 'uppercase', letterSpacing: '0.06em' }}>
              Campus Guidelines
            </h4>
            <ul style={{ listStyle: 'none', padding: 0, display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {[
                'Found items must be turned over to the Security Office or Lost & Found Desk within 24 hours.',
                'Photo identification and verified proof are required before item handover.',
                'Unclaimed items held for 30 days before disposal per campus policy.',
                'False claims are subject to disciplinary action.',
              ].map((rule, i) => (
                <li key={i} style={{ display: 'flex', gap: '8px', fontSize: '0.835rem', color: '#64748b', lineHeight: 1.6 }}>
                  <span style={{ color: '#2563eb', fontWeight: 700, flexShrink: 0, marginTop: 1 }}>{i + 1}.</span>
                  {rule}
                </li>
              ))}
            </ul>
          </div>

          {/* Tech stack */}
          <div>
            <h4 style={{ color: '#e2e8f0', fontSize: '0.875rem', fontWeight: 700, marginBottom: '1.1rem', textTransform: 'uppercase', letterSpacing: '0.06em' }}>
              Technology
            </h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.6rem' }}>
              {[
                { name: 'Spring Boot 3.3', desc: 'REST API + Security' },
                { name: 'React 18 + Vite', desc: 'Frontend SPA' },
                { name: 'MySQL 8.0',       desc: 'Relational database' },
                { name: 'JWT Auth',        desc: 'Stateless security' },
                { name: 'Smart Matching',  desc: 'Multi-attribute scoring' },
              ].map((tech) => (
                <div key={tech.name} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span style={{ fontSize: '0.82rem', color: '#94a3b8', fontWeight: 500 }}>{tech.name}</span>
                  <span style={{ fontSize: '0.72rem', color: '#475569', background: '#1e293b', padding: '2px 8px', borderRadius: '4px' }}>{tech.desc}</span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Bottom bar */}
        <div style={{
          paddingTop: '1.5rem',
          paddingBottom: '1.5rem',
          borderTop: '1px solid rgba(255,255,255,0.06)',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: '0.75rem',
          fontSize: '0.78rem',
          color: '#475569',
        }}>
          <span>© {year} CIRMS — B.Tech Final-Year Project</span>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: '6px' }}>
              Built with
              <span style={{ color: '#ef4444' }}>♥</span>
              using Spring Boot &amp; React
            </span>
            <span style={{ color: '#1e293b' }}>|</span>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: '4px' }}>
              <span style={{ width: 7, height: 7, background: '#10b981', borderRadius: '50%', display: 'inline-block', boxShadow: '0 0 6px #10b981', animation: 'pulse 2s ease-in-out infinite' }} />
              System Online
            </span>
          </div>
        </div>
      </div>
    </footer>
  );
}
