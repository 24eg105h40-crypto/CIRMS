import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { claimService } from '../services/claimService';
import { Eye, Package, AlertCircle, Clock, CheckCircle, XCircle, MessageSquare } from 'lucide-react';

const STATUS_CONFIG = {
  PENDING:  { color: '#f59e0b', bg: '#fffbeb', icon: Clock, label: 'Pending Review' },
  APPROVED: { color: '#10b981', bg: '#ecfdf5', icon: CheckCircle, label: 'Approved' },
  REJECTED: { color: '#ef4444', bg: '#fef2f2', icon: XCircle, label: 'Rejected' },
};

export default function MyClaimsPage() {
  const [claims, setClaims] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filter, setFilter] = useState('ALL');

  useEffect(() => {
    fetchMyClaims();
  }, []);

  const fetchMyClaims = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await claimService.getMyClaims();
      setClaims(data.data || []);
    } catch (err) {
      setError(err.message || 'Failed to load your claims.');
    } finally {
      setLoading(false);
    }
  };

  const filtered = filter === 'ALL' ? claims : claims.filter(c => c.status === filter);

  return (
    <div className="container" style={{ padding: '3rem 1rem' }}>
      {/* Header */}
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '2rem', marginBottom: '0.25rem' }}>My Claims</h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.95rem' }}>
          Track the status of your ownership claim submissions
        </p>
      </div>

      {/* Filter Tabs */}
      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.5rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '1rem', flexWrap: 'wrap' }}>
        {['ALL', 'PENDING', 'APPROVED', 'REJECTED'].map(tab => {
          const count = tab === 'ALL' ? claims.length : claims.filter(c => c.status === tab).length;
          const cfg = STATUS_CONFIG[tab];
          return (
            <button
              key={tab}
              onClick={() => setFilter(tab)}
              style={{
                padding: '6px 16px',
                borderRadius: '999px',
                border: '1.5px solid',
                borderColor: filter === tab ? (cfg ? cfg.color : 'var(--primary-color)') : 'var(--border-color)',
                background: filter === tab ? (cfg ? cfg.bg : 'var(--primary-light)') : 'transparent',
                color: filter === tab ? (cfg ? cfg.color : 'var(--primary-color)') : 'var(--text-muted)',
                fontWeight: 600,
                fontSize: '0.85rem',
                cursor: 'pointer',
                transition: 'all 0.2s',
              }}
            >
              {tab === 'ALL' ? `All (${count})` : `${tab.charAt(0) + tab.slice(1).toLowerCase()} (${count})`}
            </button>
          );
        })}
      </div>

      {/* Error */}
      {error && (
        <div style={{ background: '#fef2f2', border: '1px solid #fecaca', borderRadius: 'var(--radius-md)', padding: '1rem', marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '8px', color: '#dc2626' }}>
          <AlertCircle size={18} /> {error}
        </div>
      )}

      {/* Loading */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: '4rem', color: 'var(--text-muted)' }}>
          <div style={{ width: 40, height: 40, border: '3px solid var(--border-color)', borderTopColor: 'var(--primary-color)', borderRadius: '50%', animation: 'spin 0.8s linear infinite', margin: '0 auto 1rem' }} />
          Loading your claims...
        </div>
      ) : filtered.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '5rem 2rem', color: 'var(--text-muted)' }}>
          <CheckCircle size={64} color="#cbd5e1" style={{ marginBottom: '1rem' }} />
          <h3 style={{ marginBottom: '0.5rem' }}>No {filter !== 'ALL' ? filter.toLowerCase() : ''} claims yet</h3>
          <p style={{ marginBottom: '1.5rem', fontSize: '0.9rem' }}>
            When you submit a claim for a found item, it will appear here.
          </p>
          <Link to="/search" className="btn btn-primary">Browse Found Items</Link>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {filtered.map(claim => {
            const cfg = STATUS_CONFIG[claim.status] || STATUS_CONFIG.PENDING;
            const StatusIcon = cfg.icon;
            return (
              <div key={claim.id} className="cirms-card" style={{ padding: '1.5rem' }}>
                <div style={{ display: 'flex', alignItems: 'flex-start', gap: '1.25rem', flexWrap: 'wrap' }}>
                  {/* Item Image */}
                  <div style={{
                    width: 72, height: 72, borderRadius: 'var(--radius-md)',
                    background: '#f1f5f9', flexShrink: 0,
                    overflow: 'hidden', display: 'flex', alignItems: 'center', justifyContent: 'center'
                  }}>
                    {claim.itemImageUrl ? (
                      <img src={claim.itemImageUrl} alt={claim.itemName} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                    ) : (
                      <Package size={28} color="#94a3b8" />
                    )}
                  </div>

                  {/* Claim Details */}
                  <div style={{ flex: 1 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '6px', flexWrap: 'wrap' }}>
                      <h3 style={{ fontSize: '1rem', fontWeight: 700 }}>{claim.itemName || `Item #${claim.itemId}`}</h3>
                      <span style={{
                        display: 'inline-flex', alignItems: 'center', gap: '4px',
                        fontSize: '0.75rem', fontWeight: 700, padding: '3px 10px', borderRadius: '999px',
                        background: cfg.bg, color: cfg.color
                      }}>
                        <StatusIcon size={12} /> {cfg.label}
                      </span>
                    </div>

                    <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '0.75rem', lineHeight: 1.5 }}>
                      <strong>Your Reason:</strong> {claim.reason}
                    </div>

                    {claim.identifyingInformation && (
                      <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '0.75rem', lineHeight: 1.5 }}>
                        <strong>Identifying Info:</strong> {claim.identifyingInformation}
                      </div>
                    )}

                    {claim.adminComment && (
                      <div style={{
                        background: cfg.bg, border: `1px solid ${cfg.color}33`,
                        borderRadius: 'var(--radius-sm)', padding: '0.75rem',
                        fontSize: '0.85rem', color: cfg.color, display: 'flex', alignItems: 'flex-start', gap: '8px'
                      }}>
                        <MessageSquare size={14} style={{ flexShrink: 0, marginTop: 2 }} />
                        <div><strong>Admin Comment:</strong> {claim.adminComment}</div>
                      </div>
                    )}

                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '0.75rem', flexWrap: 'wrap', gap: '0.5rem' }}>
                      <span style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>
                        Submitted: {claim.createdAt ? new Date(claim.createdAt).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' }) : 'N/A'}
                      </span>
                      <Link
                        to={`/items/${claim.itemId}`}
                        className="btn btn-outline"
                        style={{ padding: '5px 12px', fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: '4px' }}
                      >
                        <Eye size={13} /> View Item
                      </Link>
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </div>
  );
}
