import React from 'react';
import { AlertTriangle, RotateCcw, Home } from 'lucide-react';

export default class ErrorBoundary extends React.Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null, errorInfo: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, errorInfo) {
    this.setState({ errorInfo });
    console.error('Unhandled UI Error caught by ErrorBoundary:', error, errorInfo);
  }

  handleReset = () => {
    this.setState({ hasError: false, error: null, errorInfo: null });
    window.location.href = '/';
  };

  handleReload = () => {
    window.location.reload();
  };

  render() {
    if (this.state.hasError) {
      return (
        <div
          style={{
            minHeight: '80vh',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            padding: '2rem 1rem',
            background: 'var(--bg-gradient, #f8fafc)',
          }}
        >
          <div
            style={{
              maxWidth: '560px',
              width: '100%',
              background: '#ffffff',
              borderRadius: '16px',
              border: '1px solid #e2e8f0',
              boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1)',
              padding: '2.5rem 2rem',
              textAlign: 'center',
            }}
          >
            <div
              style={{
                width: '64px',
                height: '64px',
                borderRadius: '50%',
                background: '#fef2f2',
                color: '#ef4444',
                display: 'inline-flex',
                alignItems: 'center',
                justifyContent: 'center',
                marginBottom: '1.25rem',
              }}
            >
              <AlertTriangle size={32} />
            </div>

            <h2
              style={{
                fontSize: '1.5rem',
                fontWeight: 700,
                color: '#0f172a',
                marginBottom: '0.5rem',
              }}
            >
              Something went wrong
            </h2>

            <p
              style={{
                color: '#64748b',
                fontSize: '0.95rem',
                marginBottom: '1.75rem',
                lineHeight: 1.5,
              }}
            >
              An unexpected error occurred while rendering this view. Your session and data remain safe.
            </p>

            <div
              style={{
                display: 'flex',
                gap: '0.75rem',
                justifyContent: 'center',
                flexWrap: 'wrap',
              }}
            >
              <button
                onClick={this.handleReload}
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '0.5rem',
                  padding: '0.65rem 1.25rem',
                  borderRadius: '10px',
                  border: '1px solid #e2e8f0',
                  background: '#ffffff',
                  color: '#334155',
                  fontWeight: 600,
                  fontSize: '0.9rem',
                  cursor: 'pointer',
                  transition: 'all 0.15s ease',
                }}
              >
                <RotateCcw size={16} />
                Try Again
              </button>

              <button
                onClick={this.handleReset}
                style={{
                  display: 'inline-flex',
                  alignItems: 'center',
                  gap: '0.5rem',
                  padding: '0.65rem 1.25rem',
                  borderRadius: '10px',
                  border: 'none',
                  background: 'linear-gradient(135deg, #0284c7 0%, #2563eb 100%)',
                  color: '#ffffff',
                  fontWeight: 600,
                  fontSize: '0.9rem',
                  cursor: 'pointer',
                  boxShadow: '0 4px 6px -1px rgba(37, 99, 235, 0.3)',
                  transition: 'all 0.15s ease',
                }}
              >
                <Home size={16} />
                Back to Home
              </button>
            </div>

            {this.state.error && (
              <details
                style={{
                  marginTop: '1.75rem',
                  textAlign: 'left',
                  background: '#f8fafc',
                  padding: '0.75rem 1rem',
                  borderRadius: '8px',
                  border: '1px solid #e2e8f0',
                  fontSize: '0.8rem',
                  color: '#64748b',
                }}
              >
                <summary style={{ cursor: 'pointer', fontWeight: 600, color: '#475569' }}>
                  Technical error details
                </summary>
                <pre
                  style={{
                    marginTop: '0.5rem',
                    whiteSpace: 'pre-wrap',
                    wordBreak: 'break-word',
                    color: '#dc2626',
                    fontFamily: 'monospace',
                  }}
                >
                  {this.state.error.toString()}
                </pre>
              </details>
            )}
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
