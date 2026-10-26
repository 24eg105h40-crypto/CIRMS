import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { itemService } from '../services/itemService';
import { claimService } from '../services/claimService';
import { matchingService } from '../services/matchingService';
import { useAuth } from '../context/AuthContext';
import { MapPin, Calendar, Tag, Package, ArrowLeft, X, ShieldCheck, Loader2, AlertCircle, CheckCircle, Zap, ChevronRight } from 'lucide-react';

export default function ItemDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAuth();
  const [item, setItem] = useState(null);
  const [loading, setLoading] = useState(true);

  // Claim modal state
  const [showClaimModal, setShowClaimModal] = useState(false);
  const [claimForm, setClaimForm] = useState({ reason: '', identifyingInformation: '' });
  const [claimErrors, setClaimErrors] = useState({});
  const [claimLoading, setClaimLoading] = useState(false);
  const [claimSuccess, setClaimSuccess] = useState(false);
  const [claimServerError, setClaimServerError] = useState('');

  // Matches state
  const [matches, setMatches] = useState([]);
  const [matchesLoading, setMatchesLoading] = useState(false);

  useEffect(() => {
    itemService.getItemById(id)
      .then((res) => {
        setItem(res.data);
        // Load matches after item is fetched (only for authenticated users)
        if (res.data) {
          setMatchesLoading(true);
          matchingService.getMatchesForItem(res.data.id, 5)
            .then(mr => setMatches(mr.data || []))
            .catch(() => setMatches([]))
            .finally(() => setMatchesLoading(false));
        }
      })
      .catch((err) => console.error(err))
      .finally(() => setLoading(false));
  }, [id]);

  const openClaim = () => {
    setClaimForm({ reason: '', identifyingInformation: '' });
    setClaimErrors({});
    setClaimServerError('');
    setClaimSuccess(false);
    setShowClaimModal(true);
  };

  const validateClaim = () => {
    const errs = {};
    if (!claimForm.reason.trim()) errs.reason = 'Please describe why this is yours.';
    else if (claimForm.reason.trim().length < 20) errs.reason = 'Please provide at least 20 characters.';
    if (!claimForm.identifyingInformation.trim()) errs.identifyingInformation = 'Identifying information is required.';
    else if (claimForm.identifyingInformation.trim().length < 15) errs.identifyingInformation = 'Please provide at least 15 characters.';
    return errs;
  };

  const handleSubmitClaim = async (e) => {
    e.preventDefault();
    const errs = validateClaim();
    if (Object.keys(errs).length > 0) { setClaimErrors(errs); return; }
    setClaimLoading(true);
    setClaimServerError('');
    try {
      await claimService.submitClaim({
        itemId: parseInt(id),
        reason: claimForm.reason.trim(),
        identifyingInformation: claimForm.identifyingInformation.trim(),
      });
      setClaimSuccess(true);
    } catch (err) {
      setClaimServerError(err.message || 'Failed to submit claim. Please try again.');
    } finally {
      setClaimLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="container" style={{ padding: '4rem 1rem', textAlign: 'center', color: 'var(--text-muted)' }}>
        <div style={{ width: 40, height: 40, border: '3px solid var(--border-color)', borderTopColor: 'var(--primary-color)', borderRadius: '50%', animation: 'spin 0.8s linear infinite', margin: '0 auto 1rem' }} />
        Loading item details...
      </div>
    );
  }

  if (!item) {
    return (
      <div className="container" style={{ padding: '4rem 1rem', textAlign: 'center' }}>
        <h2>Item Not Found</h2>
        <Link to="/search" className="btn btn-primary" style={{ marginTop: '1rem' }}>Back to Search</Link>
      </div>
    );
  }

  const isClaimable = item.itemType === 'FOUND' && item.status === 'FOUND';
  const isOwner = user && item.reporterName && item.reporterEmail === user.email;

  return (
    <div className="container" style={{ padding: '3rem 1rem', maxWidth: 900 }}>
      <Link to="/search" style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', color: 'var(--text-muted)', marginBottom: '1.5rem', fontSize: '0.9rem' }}>
        <ArrowLeft size={16} /> Back to Search
      </Link>

      <div className="cirms-card" style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '2rem', padding: '2rem' }}>
        {/* Image */}
        <div>
          <div style={{
            background: '#f8fafc',
            border: '1px solid var(--border-color)',
            borderRadius: 'var(--radius-md)',
            height: 340,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            overflow: 'hidden'
          }}>
            {item.imageUrl ? (
              <img src={item.imageUrl} alt={item.itemName} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
            ) : (
              <Package size={64} color="#94a3b8" />
            )}
          </div>
        </div>

        {/* Details */}
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.75rem', flexWrap: 'wrap' }}>
            <span className={`badge ${item.itemType === 'LOST' ? 'badge-lost' : 'badge-found'}`}>{item.itemType}</span>
            <span className="badge" style={{
              background: item.status === 'FOUND' ? '#ecfdf5' : item.status === 'LOST' ? '#fef2f2' : '#eff6ff',
              color: item.status === 'FOUND' ? '#059669' : item.status === 'LOST' ? '#dc2626' : '#2563eb',
            }}>{item.status?.replace('_', ' ')}</span>
          </div>

          <h1 style={{ fontSize: '2rem', marginBottom: '1rem' }}>{item.itemName}</h1>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', marginBottom: '1.5rem', fontSize: '0.9rem' }}>
            {item.categoryName && (
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <Tag size={16} color="var(--primary-color)" />
                <strong>Category:</strong> {item.categoryName}
              </div>
            )}
            {item.location && (
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <MapPin size={16} color="var(--primary-color)" />
                <strong>Location:</strong> {item.location}
              </div>
            )}
            {item.dateLostOrFound && (
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <Calendar size={16} color="var(--primary-color)" />
                <strong>Date:</strong> {item.dateLostOrFound} {item.timeLostOrFound && `(${item.timeLostOrFound})`}
              </div>
            )}
            {item.color && <div><strong>Color:</strong> {item.color}</div>}
            {item.brand && <div><strong>Brand:</strong> {item.brand}</div>}
          </div>

          {item.description && (
            <div style={{ borderTop: '1px solid var(--border-color)', paddingTop: '1rem', marginBottom: '1.5rem' }}>
              <h3 style={{ fontSize: '1rem', marginBottom: '0.5rem' }}>Description</h3>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', lineHeight: 1.6 }}>
                {item.description}
              </p>
            </div>
          )}

          {/* Claim Section */}
          {isClaimable && !isOwner && (
            <div style={{ background: '#f8fafc', padding: '1.25rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '0.5rem' }}>
                <ShieldCheck size={18} color="#2563eb" />
                <strong style={{ fontSize: '0.95rem' }}>Claim This Item</strong>
              </div>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '1rem' }}>
                Is this item yours? Submit a confidential claim with identifying proof.
              </p>
              {isAuthenticated ? (
                <button className="btn btn-primary" style={{ width: '100%', justifyContent: 'center' }} onClick={openClaim}>
                  Submit Ownership Claim
                </button>
              ) : (
                <Link to={`/login?redirect=/items/${id}`} className="btn btn-primary" style={{ width: '100%', display: 'block', textAlign: 'center' }}>
                  Sign In to Claim Item
                </Link>
              )}
            </div>
          )}

          {item.status === 'CLAIM_PENDING' && (
            <div style={{ background: '#fffbeb', padding: '1rem', borderRadius: 'var(--radius-md)', border: '1px solid #fde68a', fontSize: '0.88rem', color: '#92400e', display: 'flex', gap: '8px', alignItems: 'center' }}>
              <AlertCircle size={16} />
              This item has a claim under review by the admin.
            </div>
          )}

          {item.status === 'CLAIM_APPROVED' && (
            <div style={{ background: '#ecfdf5', padding: '1rem', borderRadius: 'var(--radius-md)', border: '1px solid #6ee7b7', fontSize: '0.88rem', color: '#065f46', display: 'flex', gap: '8px', alignItems: 'center' }}>
              <CheckCircle size={16} />
              This item has been recovered and returned to its owner.
            </div>
          )}
        </div>
      </div>

      {/* Possible Matches Section */}
      {(matchesLoading || matches.length > 0) && (
        <div style={{ marginTop: '2rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '1rem' }}>
            <div style={{ background: 'linear-gradient(135deg, #2563eb, #7c3aed)', padding: '6px', borderRadius: '8px', color: '#fff' }}>
              <Zap size={16} />
            </div>
            <h2 style={{ fontSize: '1.25rem', fontWeight: 700, margin: 0 }}>Possible Matches</h2>
            {matches.length > 0 && (
              <span style={{ fontSize: '0.78rem', fontWeight: 700, background: '#eff6ff', color: '#2563eb', padding: '2px 8px', borderRadius: '999px' }}>
                {matches.length} found
              </span>
            )}
          </div>

          {matchesLoading ? (
            <div style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '8px', justifyContent: 'center' }}>
              <Loader2 size={18} color="var(--primary-color)" style={{ animation: 'spin 1s linear infinite' }} />
              Running matching algorithm...
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {matches.map(match => {
                const score = match.score;
                const scoreColor = score >= 70 ? '#059669' : score >= 50 ? '#2563eb' : score >= 35 ? '#d97706' : '#6b7280';
                const scoreBg   = score >= 70 ? '#ecfdf5' : score >= 50 ? '#eff6ff' : score >= 35 ? '#fffbeb' : '#f9fafb';
                return (
                  <div key={match.matchedItemId} className="cirms-card" style={{ display: 'flex', alignItems: 'center', gap: '1rem', padding: '1.1rem', flexWrap: 'wrap' }}>
                    {/* Thumbnail */}
                    <div style={{ width: 56, height: 56, borderRadius: '8px', background: '#f1f5f9', flexShrink: 0, overflow: 'hidden', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                      {match.matchedItemImageUrl ? (
                        <img src={match.matchedItemImageUrl} alt={match.matchedItemName} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                      ) : <Package size={24} color="#94a3b8" />}
                    </div>

                    {/* Info */}
                    <div style={{ flex: 1, minWidth: 140 }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '6px', marginBottom: '3px', flexWrap: 'wrap' }}>
                        <span style={{ fontWeight: 700, fontSize: '0.9rem' }}>{match.matchedItemName}</span>
                        <span style={{ fontSize: '0.68rem', fontWeight: 700, padding: '1px 6px', borderRadius: '999px', background: match.matchedItemType === 'FOUND' ? '#ecfdf5' : '#fef2f2', color: match.matchedItemType === 'FOUND' ? '#059669' : '#dc2626' }}>{match.matchedItemType}</span>
                      </div>
                      <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>
                        {match.matchedItemLocation && <span style={{ marginRight: 8 }}>📍 {match.matchedItemLocation}</span>}
                        {match.matchedItemDate && <span>📅 {match.matchedItemDate}</span>}
                      </div>
                      <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)', marginTop: '2px', fontStyle: 'italic' }}>{match.matchSummary}</div>
                    </div>

                    {/* Score */}
                    <div style={{ textAlign: 'center', flexShrink: 0 }}>
                      <div style={{ fontSize: '1.4rem', fontWeight: 800, color: scoreColor, background: scoreBg, width: 52, height: 52, borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column', margin: '0 auto 4px' }}>
                        {score}
                      </div>
                      <span style={{ fontSize: '0.65rem', color: '#94a3b8' }}>/ 100</span>
                    </div>

                    <Link to={`/items/${match.matchedItemId}`} className="btn btn-outline" style={{ fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: '4px', whiteSpace: 'nowrap' }}>
                      View <ChevronRight size={13} />
                    </Link>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      )}

      {/* Claim Modal */}
      {showClaimModal && (
        <div style={{
          position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.55)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000, padding: '1rem'
        }}>
          <div className="cirms-card" style={{ maxWidth: 520, width: '100%', padding: '2rem', maxHeight: '90vh', overflowY: 'auto' }}>
            {claimSuccess ? (
              <div style={{ textAlign: 'center', padding: '1rem' }}>
                <div style={{ width: 60, height: 60, background: '#ecfdf5', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 1rem' }}>
                  <CheckCircle size={32} color="#10b981" />
                </div>
                <h3 style={{ marginBottom: '0.75rem' }}>Claim Submitted!</h3>
                <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '1.5rem' }}>
                  Your claim has been submitted successfully. An admin will review it and you'll receive a notification with the decision.
                </p>
                <div style={{ display: 'flex', gap: '0.75rem' }}>
                  <button className="btn btn-outline" style={{ flex: 1 }} onClick={() => setShowClaimModal(false)}>
                    Close
                  </button>
                  <Link to="/my-claims" className="btn btn-primary" style={{ flex: 1, textAlign: 'center' }}>
                    View My Claims
                  </Link>
                </div>
              </div>
            ) : (
              <>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
                  <h3 style={{ fontSize: '1.1rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <ShieldCheck size={20} color="#2563eb" /> Submit Ownership Claim
                  </h3>
                  <button onClick={() => setShowClaimModal(false)} style={{ background: 'none', border: 'none', cursor: 'pointer' }}>
                    <X size={20} color="var(--text-muted)" />
                  </button>
                </div>

                <div style={{ background: '#eff6ff', borderRadius: 'var(--radius-sm)', padding: '0.75rem 1rem', marginBottom: '1.5rem', fontSize: '0.85rem', color: '#1e40af' }}>
                  <strong>Item:</strong> {item.itemName} &nbsp;|&nbsp; <strong>Found at:</strong> {item.location}
                </div>

                {claimServerError && (
                  <div style={{ background: '#fef2f2', border: '1px solid #fecaca', borderRadius: 'var(--radius-sm)', padding: '0.75rem', marginBottom: '1rem', fontSize: '0.85rem', color: '#dc2626', display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <AlertCircle size={16} /> {claimServerError}
                  </div>
                )}

                <form onSubmit={handleSubmitClaim}>
                  <div style={{ marginBottom: '1.25rem' }}>
                    <label style={{ display: 'block', fontWeight: 600, marginBottom: '6px', fontSize: '0.9rem' }}>
                      Why is this item yours? <span style={{ color: '#ef4444' }}>*</span>
                    </label>
                    <textarea
                      value={claimForm.reason}
                      onChange={e => setClaimForm(f => ({ ...f, reason: e.target.value }))}
                      placeholder="Describe why you believe this is your item (e.g., I lost my black wallet near the library last Monday...)"
                      rows={3}
                      style={{
                        width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-sm)',
                        border: `1.5px solid ${claimErrors.reason ? '#ef4444' : 'var(--border-color)'}`,
                        resize: 'vertical', fontFamily: 'inherit', fontSize: '0.875rem', boxSizing: 'border-box',
                      }}
                    />
                    {claimErrors.reason && <p style={{ color: '#ef4444', fontSize: '0.8rem', marginTop: '4px' }}>{claimErrors.reason}</p>}
                  </div>

                  <div style={{ marginBottom: '1.5rem' }}>
                    <label style={{ display: 'block', fontWeight: 600, marginBottom: '6px', fontSize: '0.9rem' }}>
                      Identifying Information <span style={{ color: '#ef4444' }}>*</span>
                    </label>
                    <textarea
                      value={claimForm.identifyingInformation}
                      onChange={e => setClaimForm(f => ({ ...f, identifyingInformation: e.target.value }))}
                      placeholder="Provide specific details only the owner would know (e.g., my student ID inside, a sticker on the back, serial number: 12345...)"
                      rows={3}
                      style={{
                        width: '100%', padding: '0.75rem', borderRadius: 'var(--radius-sm)',
                        border: `1.5px solid ${claimErrors.identifyingInformation ? '#ef4444' : 'var(--border-color)'}`,
                        resize: 'vertical', fontFamily: 'inherit', fontSize: '0.875rem', boxSizing: 'border-box',
                      }}
                    />
                    {claimErrors.identifyingInformation && <p style={{ color: '#ef4444', fontSize: '0.8rem', marginTop: '4px' }}>{claimErrors.identifyingInformation}</p>}
                    <p style={{ fontSize: '0.78rem', color: 'var(--text-muted)', marginTop: '4px' }}>
                      This is confidential and only visible to admins. Be specific — vague claims are rejected.
                    </p>
                  </div>

                  <div style={{ display: 'flex', gap: '0.75rem' }}>
                    <button type="button" className="btn btn-outline" style={{ flex: 1 }} onClick={() => setShowClaimModal(false)}>
                      Cancel
                    </button>
                    <button type="submit" className="btn btn-primary" style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '6px' }} disabled={claimLoading}>
                      {claimLoading ? <Loader2 size={16} style={{ animation: 'spin 1s linear infinite' }} /> : <ShieldCheck size={16} />}
                      {claimLoading ? 'Submitting...' : 'Submit Claim'}
                    </button>
                  </div>
                </form>
              </>
            )}
          </div>
        </div>
      )}

      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </div>
  );
}
