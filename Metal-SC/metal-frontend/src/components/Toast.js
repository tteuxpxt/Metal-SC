import React, { createContext, useCallback, useContext, useMemo, useState } from 'react';
import { AlertTriangle, CheckCircle2, Info, X, XCircle } from 'lucide-react';

const ToastContext = createContext(null);

const TOAST_CONFIG = {
  success: {
    icon: CheckCircle2,
    duration: 4500,
    label: 'Sucesso'
  },
  error: {
    icon: XCircle,
    duration: 7000,
    label: 'Erro'
  },
  warning: {
    icon: AlertTriangle,
    duration: 6000,
    label: 'Atenção'
  },
  info: {
    icon: Info,
    duration: 5000,
    label: 'Informação'
  }
};

const ToastItem = ({ toast, onClose }) => {
  const config = TOAST_CONFIG[toast.type] || TOAST_CONFIG.info;
  const Icon = config.icon;

  return (
    <div className={`toast-item ${toast.type}`} role="status" aria-live="polite">
      <div className="toast-icon" aria-hidden="true">
        <Icon size={20} />
      </div>
      <div className="toast-content">
        <strong>{toast.title || config.label}</strong>
        <p>{toast.message}</p>
      </div>
      <button
        type="button"
        className="toast-close"
        onClick={() => onClose(toast.id)}
        aria-label="Fechar notificação"
      >
        <X size={16} />
      </button>
    </div>
  );
};

export const ToastProvider = ({ children }) => {
  const [toasts, setToasts] = useState([]);

  const removeToast = useCallback((id) => {
    setToasts((current) => current.filter((toast) => toast.id !== id));
  }, []);

  const showToast = useCallback((type, message, options = {}) => {
    if (!message) return null;

    const id = `${Date.now()}-${Math.random().toString(36).slice(2)}`;
    const config = TOAST_CONFIG[type] || TOAST_CONFIG.info;
    const duration = options.duration ?? config.duration;

    setToasts((current) => [
      ...current,
      {
        id,
        type,
        message,
        title: options.title
      }
    ]);

    if (duration > 0) {
      window.setTimeout(() => removeToast(id), duration);
    }

    return id;
  }, [removeToast]);

  const toast = useMemo(() => ({
    success: (message, options) => showToast('success', message, options),
    error: (message, options) => showToast('error', message, options),
    warning: (message, options) => showToast('warning', message, options),
    info: (message, options) => showToast('info', message, options),
    dismiss: removeToast
  }), [removeToast, showToast]);

  return (
    <ToastContext.Provider value={toast}>
      {children}
      <div className="toast-stack" aria-label="Notificações">
        {toasts.map((item) => (
          <ToastItem key={item.id} toast={item} onClose={removeToast} />
        ))}
      </div>
    </ToastContext.Provider>
  );
};

export const useToast = () => {
  const context = useContext(ToastContext);
  if (!context) throw new Error('useToast must be used within ToastProvider');
  return context;
};
