import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { adminService } from '../services/adminService';
import {
  Users, Package, CheckCircle, AlertTriangle, ShieldCheck, Activity,
  XCircle, Eye, MessageSquare, Loader2, RefreshCw, X, ChevronDown, ChevronUp
} from 'lucide-react';

export default function AdminDashboard() {
  const [metrics, setMetrics] = useState(null);
  const [pendingClaims, setPendingClaims] = useState([]);
  const [allClaims, setAllClaims] = useState([]);
  const [loading, setLoading] = useState(true);
  const [claimLoading, setClaimLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('pending'); // 'pending' | 'all'
  const [reviewModal, setReviewModal] = useState(null); // { claim, action: 'approve'|'reject' }
  const [reviewComment, setReviewComment] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [expandedClaim, setExpandedClaim] = useState(null);

  useEffect(() => {
    fetchAll();
  }, []);

  const fetchAll = async () => {
    setLoading(true);
    setClaimLoading(true);
    setError('');
    try {
      const [statsRes, pendingRes, allRes] = await Promise.allSettled([
        adminService.getStats(),
        adminService.getPendingClaims(),
        adminService.getAllClaims(),
      ]);

      if (statsRes.status === 'fulfilled') setMetrics(statsRes.value?.data || null);
      if (pendingRes.status === 'fulfilled') setPendingClaims(pendingRes.value?.data || []);
      if (allRes.status === 'fulfilled') setAllClaims(allRes.value?.data || []);
      if (statsRes.status === 'rejected') setError('Failed to load dashboard stats.');
    } catch (err) {
      setError(err.message || 'Failed to load admin data.');
    } finally {
      setLoading(false);
      setClaimLoading(false);
    }
  };

  const handleReviewSubmit = async () => {
    if (!reviewModal) return;
    setSubmitting(true);
    try {
      const { claim, action } = reviewModal;
      if (action === 'approve') {
        await adminService.approveClaim(claim.id, reviewComment);
      } else {
        await adminService.rejectClaim(claim.id, reviewComment);
      }
      setReviewModal(null);
      setReviewComment('');
      fetchAll(); // refresh
    } catch (err) {
      alert(err.message || 'Operation failed.');
    } finally {
      setSubmitting(false);
    }
  };

  const StatCard = ({ label, value, color, icon: Icon }) => (
    <div className="cirms-card" style={{ position: 'relative', overflow: 'hidden' }}>
      <div style={{ position: 'absolute', top: 0, left: 0, bottom: 0, width: 4, background: color }} />
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div>
          <span style={{ fontSize: '0.82rem', color: 'var(--text-muted)', fontWeight: 600, display: 'block', marginBottom: '0.5rem' }}>{label}</span>
          {loading ? (
            <Loader2 size={22} color={color} style={{ animation: 'spin 1s linear infinite' }} />
          ) : (
            <div style={{ fontSize: '2.25rem', fontWeight: 800, color }}>{value ?? '—'}</div>
          )}
        </div>
        <div style={{ background: color + '18', padding: '10px', borderRadius: '10px' }}>
          <Icon size={22} color={color} />
        </div>
      </div>
    </div>
  );

  const STATUS_CFG = {
    PENDING:  { color: '#f59e0b', bg: '#fffbeb', label: 'Pending' },
    APPROVED: { color: '#10b981', bg: '#ecfdf5', label: 'Approved' },
    REJECTED: { color: '#ef4444', bg: '#fef2f2', label: 'Rejected' },
  };

  const displayClaims = activeTab === 'pending' ? pendingClaims : allClaims;

  return (
    <div className="container" style={{ padding: '3rem 1rem' }}>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '2rem', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <span style={{ fontSize: '0.75rem', fontWeight: 700, color: '#ef4444', background: '#fef2f2', padding: '3px 10px', borderRadius: '999px', marginBottom: '0.5rem', display: 'inline-block' }}>
            ADMINISTRATION
          </span>
          <h1 style={{ fontSize: '2rem', marginBottom: '0.25rem' }}>Campus Control Dashboard</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
            Real-time campus lost & found analytics and claim verification queue
          </p>
        </div>
        <button className="btn btn-outline" style={{ display: 'flex', alignItems: 'center', gap: '6px', fontSize: '0.85rem' }} onClick={fetchAll}>
          <RefreshCw size={15} /> Refresh
        </button>
      </div>

      {error && (
        <div style={{ background: '#fef2f2', border: '1px solid #fecaca', borderRadius: 'var(--radius-md)', padding: '1rem', marginBottom: '1.5rem', color: '#dc2626' }}>
          {error}
        </div>
      )}

      {/* KPI Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1.25rem', marginBottom: '3rem' }}>
        <StatCard label="Total Users" value={metrics?.totalUsers} color="#2563eb" icon={Users} />
        <StatCard label="Total Items" value={metrics?.totalItems} color="#6b7280" icon={Package} />
        <StatCard label="Lost Items" value={metrics?.totalLostItems} color="#ef4444" icon={Package} />
        <StatCard label="Found Items" value={metrics?.totalFoundItems} color="#10b981" icon={Package} />
        <StatCard label="Pending Claims" value={metrics?.pendingClaims} color="#f59e0b" icon={AlertTriangle} />
        <StatCard label="Approved Claims" value={metrics?.approvedClaims} color="#10b981" icon={CheckCircle} />
        <StatCard label="Rejected Claims" value={metrics?.rejectedClaims} color="#ef4444" icon={XCircle} />
        <StatCard label="Recovered Items" value={metrics?.recoveredItems} color="#8b5cf6" icon={ShieldCheck} />
      </div>

      {/* Claim Verification Section */}
      <div style={{ marginBottom: '2rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem', flexWrap: 'wrap', gap: '0.75rem' }}>
          <h2 style={{ fontSize: '1.4rem', fontWeight: 700 }}>Claim Verification Queue</h2>
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            {[
              { key: 'pending', label: `Pending (${pendingClaims.length})` },
              { key: 'all', label: `All (${allClaims.length})` },
            ].map(tab => (
              <button
                key={tab.key}
                onClick={() => setActiveTab(tab.key)}
                style={{
                  padding: '6px 14px', borderRadius: '999px', border: '1.5px solid',
                  borderColor: activeTab === tab.key ? 'var(--primary-color)' : 'var(--border-color)',
                  background: activeTab === tab.key ? 'var(--primary-color)' : 'transparent',
                  color: activeTab === tab.key ? '#fff' : 'var(--text-muted)',
                  fontWeight: 600, fontSize: '0.8rem', cursor: 'pointer', transition: 'all 0.2s',
                }}
              >
                {tab.label}
              </button>
            ))}
          </div>
        </div>

        {claimLoading ? (
          <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-muted)' }}>
            <Loader2 size={32} color="var(--primary-color)" style={{ animation: 'spin 1s linear infinite', marginBottom: '0.75rem' }} />
            <p>Loading claims...</p>
          </div>
        ) : displayClaims.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-muted)', background: 'var(--card-bg)', borderRadius: 'var(--radius-lg)', border: '1px solid var(--border-color)' }}>
            <CheckCircle size={48} color="#cbd5e1" style={{ marginBottom: '0.75rem' }} />
            <p>{activeTab === 'pending' ? 'No pending claims! All caught up.' : 'No claims found.'}</p>
          </div>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {displayClaims.map(claim => {
              const cfg = STATUS_CFG[claim.status] || STATUS_CFG.PENDING;
              const isExpanded = expandedClaim === claim.id;
              const isPending = claim.status === 'PENDING';
              return (
                <div key={claim.id} className="cirms-card" style={{ padding: '1.25rem' }}>
                  <div style={{ display: 'flex', alignItems: 'flex-start', gap: '1rem', flexWrap: 'wrap' }}>
                    {/* Content */}
                    <div style={{ flex: 1, minWidth: 240 }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '6px', flexWrap: 'wrap' }}>
                        <h4 style={{ fontSize: '0.95rem', fontWeight: 700 }}>
                          Claim #{claim.id} — {claim.itemName || `Item #${claim.itemId}`}
                        </h4>
                        <span style={{
                          fontSize: '0.72rem', fontWeight: 700, padding: '2px 8px', borderRadius: '999px',
                          background: cfg.bg, color: cfg.color
                        }}>{cfg.label}</span>
                      </div>
                      <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '4px' }}>
                        <strong>Claimant:</strong> {claim.claimantName} ({claim.claimantEmail})
                      </div>
                      <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                        <strong>Reason:</strong> {claim.reason}
                      </div>

                      {/* Expanded Details */}
                      {isExpanded && (
                        <div style={{ marginTop: '1rem', padding: '1rem', background: '#f8fafc', borderRadius: 'var(--radius-sm)', fontSize: '0.85rem' }}>
                          {claim.identifyingInformation && (
                            <div style={{ marginBottom: '0.75rem' }}>
                              <strong>Identifying Information:</strong>
                              <p style={{ color: 'var(--text-muted)', marginTop: '4px' }}>{claim.identifyingInformation}</p>
                            </div>
                          )}
                          {claim.adminComment && (
                            <div style={{ background: cfg.bg, padding: '0.75rem', borderRadius: 'var(--radius-sm)', color: cfg.color }}>
                              <strong>Admin Comment:</strong> {claim.adminComment}
                            </div>
                          )}
                          <div style={{ marginTop: '0.5rem', color: 'var(--text-muted)' }}>
                            <strong>Submitted:</strong> {claim.createdAt ? new Date(claim.createdAt).toLocaleString('en-IN') : 'N/A'}
                          </div>
                        </div>
                      )}
                    </div>

                    {/* Actions */}
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', alignItems: 'flex-end' }}>
                      <button
                        onClick={() => setExpandedClaim(isExpanded ? null : claim.id)}
                        style={{
                          padding: '5px 10px', borderRadius: 'var(--radius-sm)',
                          border: '1px solid var(--border-color)', background: 'transparent',
                          cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '4px',
                          fontSize: '0.78rem', color: 'var(--text-muted)',
                        }}
                      >
                        {isExpanded ? <ChevronUp size={14} /> : <ChevronDown size={14} />}
                        Details
                      </button>
                      <Link
                        to={`/items/${claim.itemId}`}
                        style={{
                          padding: '5px 10px', borderRadius: 'var(--radius-sm)',
                          border: '1px solid var(--border-color)', background: 'transparent',
                          cursor: 'pointer', display: 'flex', alignItems: 'center', gap: '4px',
                          fontSize: '0.78rem', color: 'var(--text-muted)', textDecoration: 'none',
                        }}
                      >
                        <Eye size={14} /> View Item
                      </Link>
                      {isPending && (
                        <>
                          <button
                            onClick={() => { setReviewModal({ claim, action: 'approve' }); setReviewComment(''); }}
                            style={{
                              padding: '6px 12px', borderRadius: 'var(--radius-sm)',
                              border: 'none', background: '#10b981', color: '#fff',
                              fontWeight: 600, fontSize: '0.8rem', cursor: 'pointer',
                              display: 'flex', alignItems: 'center', gap: '4px',
                            }}
                          >
                            <CheckCircle size={14} /> Approve
                          </button>
                          <button
                            onClick={() => { setReviewModal({ claim, action: 'reject' }); setReviewComment(''); }}
                            style={{
                              padding: '6px 12px', borderRadius: 'var(--radius-sm)',
                              border: 'none', background: '#ef4444', color: '#fff',
                              fontWeight: 600, fontSize: '0.8rem', cursor: 'pointer',
                              display: 'flex', alignItems: 'center', gap: '4px',
                            }}
                          >
                            <XCircle size={14} /> Reject
                          </button>
                        </>
                      )}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>

      {/* Admin Shortcuts */}
      <h2 style={{ fontSize: '1.25rem', fontWeight: 700, marginBottom: '1rem' }}>Management Portals</h2>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))', gap: '1.25rem' }}>
        <Link to="/search" className="cirms-card" style={{ display: 'flex', gap: '1rem', alignItems: 'flex-start', textDecoration: 'none' }}>
          <div style={{ background: '#eff6ff', padding: '10px', borderRadius: '10px', color: '#2563eb' }}>
            <Package size={22} />
          </div>
          <div>
            <h4 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '4px' }}>Item Catalog</h4>
            <p style={{ fontSize: '0.82rem', color: 'var(--text-muted)' }}>Browse and manage all campus items</p>
          </div>
        </Link>
        <Link to="/admin/users" className="cirms-card" style={{ display: 'flex', gap: '1rem', alignItems: 'flex-start', textDecoration: 'none' }}>
          <div style={{ background: '#f5f3ff', padding: '10px', borderRadius: '10px', color: '#7c3aed' }}>
            <Users size={22} />
          </div>
          <div>
            <h4 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '4px' }}>User Management</h4>
            <p style={{ fontSize: '0.82rem', color: 'var(--text-muted)' }}>View all registered students</p>
          </div>
        </Link>
      </div>

      {/* Review Modal */}
      {reviewModal && (
        <div style={{
          position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.55)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000, padding: '1rem'
        }}>
          <div className="cirms-card" style={{ maxWidth: 480, width: '100%', padding: '2rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
              <h3 style={{ fontSize: '1.1rem' }}>
                {reviewModal.action === 'approve' ? '✅ Approve Claim' : '❌ Reject Claim'} — #{reviewModal.claim.id}
              </h3>
              <button onClick={() => setReviewModal(null)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}>
                <X size={20} color="var(--text-muted)" />
              </button>
            </div>

            <div style={{ background: '#f8fafc', borderRadius: 'var(--radius-sm)', padding: '1rem', marginBottom: '1.25rem', fontSize: '0.85rem' }}>
              <div><strong>Item:</strong> {reviewModal.claim.itemName}</div>
              <div><strong>Claimant:</strong> {reviewModal.claim.claimantName}</div>
              <div><strong>Reason:</strong> {reviewModal.claim.reason}</div>
              {reviewModal.claim.identifyingInformation && (
                <div><strong>Identifying Info:</strong> {reviewModal.claim.identifyingInformation}</div>
              )}
            </div>

            <div style={{ marginBottom: '1.5rem' }}>
              <label style={{ display: 'block', fontWeight: 600, marginBottom: '6px', fontSize: '0.9rem' }}>
                Admin Comment <span style={{ color: 'var(--text-muted)', fontWeight: 400 }}>(optional)</span>
              </label>
              <textarea
                value={reviewComment}
                onChange={e => setReviewComment(e.target.value)}
                placeholder={reviewModal.action === 'approve' ? 'e.g. Verified with student ID proof...' : 'e.g. Insufficient identifying details provided...'}
                rows={3}
                style={{
                  width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-sm)',
                  border: '1.5px solid var(--border-color)', resize: 'vertical',
                  fontFamily: 'inherit', fontSize: '0.875rem', boxSizing: 'border-box',
                }}
              />
            </div>

            <div style={{ display: 'flex', gap: '0.75rem' }}>
              <button className="btn btn-outline" style={{ flex: 1 }} onClick={() => setReviewModal(null)}>
                Cancel
              </button>
              <button
                className="btn btn-primary"
                style={{
                  flex: 1,
                  background: reviewModal.action === 'approve' ? '#10b981' : '#ef4444',
                  borderColor: reviewModal.action === 'approve' ? '#10b981' : '#ef4444',
                  display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '6px',
                }}
                disabled={submitting}
                onClick={handleReviewSubmit}
              >
                {submitting ? <Loader2 size={16} style={{ animation: 'spin 1s linear infinite' }} /> : (
                  reviewModal.action === 'approve' ? <><CheckCircle size={15} /> Approve</> : <><XCircle size={15} /> Reject</>
                )}
              </button>
            </div>
          </div>
        </div>
      )}

      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </div>
  );
}
