
import React, { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState } from 'react';
import {
  Bell,
  CheckCircle2,
  ChevronLeft,
  ChevronRight,
  Clock3,
  Edit2,
  Filter,
  Flag,
  ClipboardList,
  ImagePlus,
  LayoutDashboard,
  LogOut,
  Mail,
  MapPinned,
  MapPin,
  Menu,
  MessageSquare,
  Package,
  Phone,
  Plus,
  Search,
  ShieldCheck,
  ShoppingCart,
  Star,
  Store,
  Trash2,
  UploadCloud,
  User,
  Eye,
  EyeOff,
  X,
  Send,
  Check,
  XCircle,
  Tag,
  CheckCheck
} from 'lucide-react';
import './App.css';
import { ToastProvider, useToast } from './components/Toast';
import { ConfirmProvider, useConfirm } from './components/ConfirmDialog';
import metalScLogo from './assets/Metal-SC-removebg-preview.png'; 
import {
  createPedido,
  deletePeca,
  deleteAdminRevendedor,
  deleteAdminUsuario,
  fetchAdminRevendedores,
  fetchAdminDashboard,
  fetchAdminUsuarios,
  fetchPecas,
  fetchPecasByRevendedor,
  fetchPedidosByCliente,
  fetchPedidosByRevendedor,
  fetchComentariosPerfilByAlvo,
  fetchComentarioPerfilMedia,
  createComentarioPerfil,
  fetchPerfilById,
  deleteComentarioPerfil,
  updatePerfil,
  uploadUsuarioFoto,
  confirmarPagamentoPedido,
  cancelarPedido,
  informarPagamentoPedido,
  baixarTaxasRevendedor,
  ativarPremiumRevendedor,
  desativarPremiumRevendedor,
  fetchNegociacoesCliente,
  fetchNegociacoesRevendedor,
  fetchNegociacao,
  fetchNotificacoes,
  fetchNotificacoesContagem,
  enviarMensagemNegociacao,
  enviarContrapropostaNegociacao,
  aprovarNegociacao,
  recusarNegociacao,
  encerrarNegociacao,
  marcarNegociacaoLida,
  excluirNegociacao,
  denunciarNegociacao,
  fetchModeracaoAlertas,
  fetchModeracaoStats,
  atualizarModeracaoStatus,
  removerMensagemModeracao,
  suspenderUsuarioModeracao,
  loginUsuario,
  registerUsuario,
  savePeca,
  uploadPecaImagem,
  removePecaImagem,
  denunciarPecaImagem
} from './api';

const AppContext = createContext(null);

const useApp = () => {
  const context = useContext(AppContext);
  if (!context) throw new Error('useApp must be used within AppProvider');
  return context;
};

const formatPrice = (value) =>
  new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL'
  }).format(Number(value || 0));

const formatDate = (value) => {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '';
  return date.toLocaleDateString('pt-BR');
};

const formatDateTime = (value) => {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '';
  return date.toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' });
};

const STATUS_LABELS = {
  EM_NEGOCIACAO: 'Negociando',
  AGUARDANDO_RESPOSTA: 'Aguardando resposta',
  AGUARDANDO_APROVACAO_DUPLA: 'Aguardando aprovação',
  APROVADA: 'Aprovado',
  FECHADO: 'Fechado',
  CANCELADO: 'Cancelado',
  AGUARDANDO_NEGOCIACAO: 'Aguardando negociação',
  PAGAMENTO_LIBERADO: 'Pagamento liberado',
  PAGAMENTO_PENDENTE: 'Pagamento pendente',
  PAGAMENTO_INFORMADO_CLIENTE: 'Pagamento informado',
  PAGAMENTO_CONFIRMADO: 'Pagamento confirmado',
  EM_PREPARACAO: 'Em preparação',
  CONCLUIDO: 'Concluído',
  PENDENTE: 'Pendente',
  CONFIRMADO: 'Confirmado',
  EM_SEPARACAO: 'Em separação',
  ENVIADO: 'Enviado',
  ENTREGUE: 'Entregue',
  BLOQUEADO_AGUARDANDO_NEGOCIACAO: 'Aguardando negociação',
  ANALISADO: 'Analisado',
  RESOLVIDO: 'Resolvido'
};

const formatStatusLabel = (status) => {
  if (!status) return '';
  return STATUS_LABELS[status] || String(status).replaceAll('_', ' ').toLowerCase();
};

const getStatusPillClass = (status) => {
  const positive = ['APROVADA', 'FECHADO', 'PAGAMENTO_LIBERADO', 'PAGAMENTO_CONFIRMADO', 'CONCLUIDO', 'CONFIRMADO', 'ENTREGUE'];
  const negative = ['CANCELADO'];
  if (positive.includes(status)) return 'active';
  if (negative.includes(status)) return 'inactive';
  return 'neutral';
};

const MESSAGE_TYPE_META = {
  PROPOSTA: { label: 'Proposta', className: 'tag-proposta' },
  CONTRAPROPOSTA: { label: 'Contraproposta', className: 'tag-contraproposta' },
  ACEITE: { label: 'Aceite', className: 'tag-aceite' },
  RECUSA: { label: 'Recusa', className: 'tag-recusa' },
  APROVACAO: { label: 'Aprovação', className: 'tag-aprovacao' }
};

const getMessageTypeMeta = (tipo) => MESSAGE_TYPE_META[tipo] || null;

const getInitials = (name = '') => {
  const parts = String(name).trim().split(/\s+/).filter(Boolean);
  if (parts.length === 0) return '?';
  return parts.slice(0, 2).map((part) => part[0]).join('').toUpperCase();
};

const getPaymentStageLabel = (pedido) => {
  if (pedido.statusPagamento === 'PAGAMENTO_CONFIRMADO' || pedido.status === 'PAGAMENTO_CONFIRMADO') {
    return 'Confirmado';
  }
  if (pedido.status === 'PAGAMENTO_INFORMADO_CLIENTE') return 'Informado';
  if (pedido.status === 'PAGAMENTO_LIBERADO' || pedido.status === 'PAGAMENTO_PENDENTE') {
    return 'Aguardando';
  }
  return 'Pendente';
};

const getPedidoTotal = (pedido) => pedido.valorFinalNegociado || pedido.valorTotal;

const getPedidoTitle = (pedido) => {
  const itemNames = (pedido.itens || [])
    .map((item) => item.pecaNome)
    .filter(Boolean);

  if (itemNames.length === 1) return itemNames[0];
  if (itemNames.length > 1) {
    const remaining = itemNames.length - 1;
    return `${itemNames[0]} + ${remaining} ${remaining === 1 ? 'item' : 'itens'}`;
  }
  return `Pedido #${pedido.id?.substring(0, 8) || 'novo'}`;
};

const getNegotiationValue = (conversa) => {
  const value =
    conversa.valorFinalAcordado ??
    conversa.valorNegociado ??
    conversa.valorOriginal;
  return Number.isFinite(Number(value)) ? Number(value) : null;
};

const isClientPaymentAction = (pedido) =>
  pedido.status === 'PAGAMENTO_LIBERADO' || pedido.status === 'PAGAMENTO_PENDENTE';

const isClientApprovalAction = (pedido) =>
  Boolean(pedido.conversaId) &&
  !pedido.aprovacaoCliente &&
  ['AGUARDANDO_NEGOCIACAO', 'PENDENTE'].includes(pedido.status);

const getClientOrderPriority = (pedido) => {
  if (isClientPaymentAction(pedido)) return 0;
  if (isClientApprovalAction(pedido)) return 1;
  if (!['CONCLUIDO', 'ENTREGUE', 'CANCELADO'].includes(pedido.status)) return 2;
  return 3;
};

const toNumberOrNull = (value) => {
  const parsed = Number(value);
  return Number.isFinite(parsed) ? parsed : null;
};

const resolveImageUrl = (url) => {
  if (!url) return '';
  if (url.startsWith('http://') || url.startsWith('https://')) return url;
  const apiBase = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';
  const origin = apiBase.replace(/\/api\/?$/, '');
  return `${origin}${url.startsWith('/') ? '' : '/'}${url}`;
};

const AppProvider = ({ children }) => {
  const toast = useToast();
  const [user, setUser] = useState(null);
  const [authToken, setAuthToken] = useState(null);
  const [cart, setCart] = useState([]);
  const [hydrated, setHydrated] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCity, setSelectedCity] = useState('');
  const [currentPage, setCurrentPage] = useState('home');
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [selectedProfile, setSelectedProfile] = useState(null);
  const [selectedNegotiationId, setSelectedNegotiationId] = useState(null);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  useEffect(() => {
    const savedUser = localStorage.getItem('metal_user');
    const savedCart = localStorage.getItem('metal_cart');
    const savedAuth = localStorage.getItem('metal_auth');

    if (savedUser) {
      try {
        setUser(JSON.parse(savedUser));
      } catch (err) {
        console.error('Sessão salva inválida, removendo:', err);
        localStorage.removeItem('metal_user');
      }
    }

    if (savedCart) {
      try {
        const parsed = JSON.parse(savedCart);
        const normalized = Array.isArray(parsed)
          ? parsed.map((item) => {
              const maxEstoque = toNumberOrNull(item?.estoque);
              if (maxEstoque !== null && item.quantidade > maxEstoque) {
                return { ...item, quantidade: maxEstoque };
              }
              return item;
            })
          : [];
        setCart(normalized);
      } catch (err) {
        console.error('Carrinho salvo inválido, removendo:', err);
        localStorage.removeItem('metal_cart');
      }
    }

    if (savedAuth) setAuthToken(savedAuth);

    setHydrated(true);
  }, []);

  useEffect(() => {
    if (!hydrated) return;
    if (user) {
      localStorage.setItem('metal_user', JSON.stringify(user));
    } else {
      localStorage.removeItem('metal_user');
    }
  }, [user, hydrated]);

  useEffect(() => {
    if (!hydrated) return;
    if (authToken) {
      localStorage.setItem('metal_auth', authToken);
    } else {
      localStorage.removeItem('metal_auth');
    }
  }, [authToken, hydrated]);

  useEffect(() => {
    if (!hydrated) return;
    localStorage.setItem('metal_cart', JSON.stringify(cart));
  }, [cart, hydrated]);

  useEffect(() => {
    const handleUnauthorized = () => {
      setUser(null);
      setAuthToken(null);
      setCurrentPage('login');
    };

    window.addEventListener('metal:unauthorized', handleUnauthorized);
    return () => window.removeEventListener('metal:unauthorized', handleUnauthorized);
  }, []);

  const login = (userData, token) => {
    setUser(userData);
    if (token) {
      setAuthToken(token);
      localStorage.setItem("metal_auth", token);
    }
    if (userData?.tipo === 'ADMINISTRADOR') {
      setCurrentPage('admin');
      return;
    }
    setCurrentPage('home');
  };

  const logout = () => {
    setUser(null);
    setAuthToken(null);
    setCart([]);
    setCurrentPage('home');
    setMobileMenuOpen(false);
  };

  const updateUser = (updatedUser) => {
    if (!updatedUser) return;
    setUser(updatedUser);
  };

  const openProduct = (product) => {
    setSelectedProduct(product);
    setCurrentPage('product-detail');
  };

  const openProfile = (profile) => {
    if (!profile || !profile.id) return;
    setSelectedProfile(profile);
    setCurrentPage('profile');
    setMobileMenuOpen(false);
  };

  const openNegotiation = (conversationId) => {
    if (!conversationId) return;
    setSelectedNegotiationId(conversationId);
    setCurrentPage('negotiation');
    setMobileMenuOpen(false);
  };

  const addToCart = (product) => {
    setCart((prev) => {
      const existing = prev.find((item) => item.id === product.id);
      const rawMaxEstoque =
        toNumberOrNull(product?.estoque) ?? toNumberOrNull(existing?.estoque);
      const maxEstoque = rawMaxEstoque ?? 1;
      if (maxEstoque <= 0) {
        toast.warning('Sem estoque disponível para esta peça.');
        return prev;
      }
      if (existing) {
        if (maxEstoque !== null && existing.quantidade >= maxEstoque) {
          toast.warning('Quantidade máxima em estoque atingida.');
          return prev;
        }
        return prev.map((item) =>
          item.id === product.id
            ? { ...item, quantidade: item.quantidade + 1 }
            : item
        );
      }
      return [...prev, { ...product, estoque: maxEstoque, quantidade: 1 }];
    });
  };

  const updateCartQty = (productId, quantidade) => {
    setCart((prev) =>
      prev
        .map((item) =>
          item.id === productId
            ? {
                ...item,
                quantidade: Math.min(
                  Math.max(1, quantidade),
                  toNumberOrNull(item.estoque) ?? Number.MAX_SAFE_INTEGER
                )
              }
            : item
        )
        .filter((item) => item.quantidade > 0)
    );
  };

  const removeFromCart = (productId) => {
    setCart((prev) => prev.filter((item) => item.id !== productId));
  };

  const clearCart = () => setCart([]);

  const value = {
    user,
    authToken,
    cart,
    currentPage,
    setCurrentPage,
    searchQuery,
    setSearchQuery,
    selectedCity,
    setSelectedCity,
    selectedProduct,
    setSelectedProduct,
    selectedProfile,
    setSelectedProfile,
    selectedNegotiationId,
    setSelectedNegotiationId,
    mobileMenuOpen,
    setMobileMenuOpen,
    login,
    logout,
    updateUser,
    openProduct,
    openProfile,
    openNegotiation,
    addToCart,
    updateCartQty,
    removeFromCart,
    clearCart
  };

  return <AppContext.Provider value={value}>{children}</AppContext.Provider>;
};

const AppShell = () => {
  const { currentPage } = useApp();

  return (
    <div className="app">
      <Header />
      <main className="main">
        {currentPage === 'home' && <HomePage />}
        {currentPage === 'products' && <ProductsPage />}
        {currentPage === 'product-detail' && <ProductDetailPage />}
        {currentPage === 'cart' && <CartPage />}
        {currentPage === 'login' && <LoginPage />}
        {currentPage === 'register' && <RegisterPage />}
        {currentPage === 'dashboard' && <DashboardPage />}
        {currentPage === 'profile' && <ProfilePage />}
        {currentPage === 'negotiation' && <NegotiationPage />}
        {currentPage === 'admin' && <AdminPage />}
      </main>
      <Footer />
    </div>
  );
};

const App = () => (
  <ToastProvider>
    <ConfirmProvider>
      <AppProvider>
        <AppShell />
      </AppProvider>
    </ConfirmProvider>
  </ToastProvider>
);

const Header = () => {
  const { user, authToken, cart, setCurrentPage, mobileMenuOpen, setMobileMenuOpen, openNegotiation, searchQuery, setSearchQuery, selectedCity, setSelectedCity } = useApp();
  const [notificationCount, setNotificationCount] = useState(0);
  const [notificationsOpen, setNotificationsOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [notificationsLoading, setNotificationsLoading] = useState(false);
  const cartCount = cart.reduce((sum, item) => sum + item.quantidade, 0);
  const notificationBadge = notificationCount > 9 ? '9+' : notificationCount;

  const loadNotificationCount = useCallback(async () => {
    if (!user?.id || !authToken) return;
    try {
      const data = await fetchNotificacoesContagem(user.id);
      setNotificationCount(Number(data?.total ?? data ?? 0));
    } catch (err) {
      console.error(err);
    }
  }, [user?.id, authToken]);

  const loadNotifications = async () => {
    if (!user?.id || !authToken) return;
    setNotificationsLoading(true);
    try {
      const data = await fetchNotificacoes(user.id);
      setNotifications(data || []);
    } catch (err) {
      console.error(err);
      setNotifications([]);
    } finally {
      setNotificationsLoading(false);
    }
  };

  useEffect(() => {
    if (!user?.id || !authToken) {
      setNotificationCount(0);
      setNotifications([]);
      setNotificationsOpen(false);
      return undefined;
    }

    loadNotificationCount();
    const timer = setInterval(loadNotificationCount, 45000);
    return () => clearInterval(timer);
  }, [user?.id, authToken, loadNotificationCount]);

  const handleUserClick = () => {
    if (user?.tipo === 'ADMINISTRADOR') {
      setCurrentPage('admin');
    } else {
      setCurrentPage('dashboard');
    }
    setMobileMenuOpen(false);
    setNotificationsOpen(false);
  };
  const handleAdminClick = () => {
    setCurrentPage('admin');
    setMobileMenuOpen(false);
    setNotificationsOpen(false);
  };
  const handleHeaderSearch = () => {
    setCurrentPage('products');
    setMobileMenuOpen(false);
    setNotificationsOpen(false);
  };
  const handleHeaderSearchSubmit = (e) => {
    e.preventDefault();
    handleHeaderSearch();
  };
  const handleHeaderCityChange = (e) => {
    const value = e.target.value;
    setSelectedCity(value === 'Todas as cidades de SC' ? '' : value);
    handleHeaderSearch();
  };
  const handleNotificationToggle = () => {
    const nextOpen = !notificationsOpen;
    setNotificationsOpen(nextOpen);
    setMobileMenuOpen(false);
    if (nextOpen) {
      loadNotifications();
    }
  };
  const handleOpenNotification = async (notification) => {
    if (!notification?.conversaId || !user?.id) return;
    try {
      await marcarNegociacaoLida(notification.conversaId, user.id);
    } catch (err) {
      console.error(err);
    }
    setNotificationsOpen(false);
    setNotifications((prev) =>
      prev.filter((item) => item.conversaId !== notification.conversaId)
    );
    setNotificationCount((prev) =>
      Math.max(0, prev - Number(notification.naoLidas || 1))
    );
    openNegotiation(notification.conversaId);
  };

  return (
    <header className="header">
      <div className="container header-content">
        <div className="logo" onClick={() => setCurrentPage('home')}>
          <img src={metalScLogo} alt="Metal-SC" className="logo-icon" />
          <span>Metal-SC</span>
        </div>

        <div className="header-center">
          <nav className={`nav ${mobileMenuOpen ? 'mobile-open' : ''}`}>
            <button className="nav-link" onClick={() => { setCurrentPage('home'); setMobileMenuOpen(false); }}>
              Início
            </button>
            <button className="nav-link" onClick={() => { setCurrentPage('products'); setMobileMenuOpen(false); }}>
              Peças
            </button>
          </nav>

          <form className="header-search" role="search" onSubmit={handleHeaderSearchSubmit}>
            <div className="header-search-field">
              <Search size={17} />
              <input
                type="text"
                placeholder="Buscar peça, marca ou modelo"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>
            <div className="header-city-field">
              <MapPin size={17} />
              <select
                aria-label="Selecionar cidade em Santa Catarina"
                value={selectedCity || 'Todas as cidades de SC'}
                onChange={handleHeaderCityChange}
              >
                <option>Todas as cidades de SC</option>
                <option>Florianópolis</option>
                <option>Joinville</option>
                <option>Blumenau</option>
                <option>São José</option>
                <option>Criciúma</option>
                <option>Chapecó</option>
                <option>Itajaí</option>
                <option>Jaraguá do Sul</option>
                <option>Palhoça</option>
                <option>Lages</option>
                <option>Balneário Camboriú</option>
                <option>Brusque</option>
                <option>Tubarão</option>
                <option>São Bento do Sul</option>
                <option>Caçador</option>
                <option>Concórdia</option>
                <option>Rio do Sul</option>
                <option>Araranguá</option>
                <option>Biguaçu</option>
                <option>Navegantes</option>
              </select>
            </div>
            <button type="submit" className="header-search-submit" aria-label="Buscar peças">
              <Search size={16} />
              <span>Buscar</span>
            </button>
          </form>
        </div>

        <div className="header-actions">
          {user && (
            <div className="notification-wrap">
              <button
                className={`icon-btn notification-btn ${notificationsOpen ? 'active' : ''}`}
                onClick={handleNotificationToggle}
                aria-label="Notificações"
                aria-expanded={notificationsOpen}
              >
                <Bell size={20} />
                {notificationCount > 0 && (
                  <span className="notification-badge">{notificationBadge}</span>
                )}
              </button>

              {notificationsOpen && (
                <div className="notification-dropdown" role="menu" aria-label="Notificações recentes">
                  <div className="notification-dropdown-head">
                    <strong>Notificações</strong>
                    <button type="button" className="ghost-btn small" onClick={loadNotifications}>
                      Atualizar
                    </button>
                  </div>
                  {notificationsLoading ? (
                    <p className="notification-empty">Carregando...</p>
                  ) : notifications.length === 0 ? (
                    <p className="notification-empty">Nenhuma notificação nova</p>
                  ) : (
                    <div className="notification-list">
                      {notifications.map((notification) => (
                        <button
                          key={`${notification.conversaId}-${notification.mensagemId}`}
                          type="button"
                          className="notification-item"
                          onClick={() => handleOpenNotification(notification)}
                        >
                          <span className="notification-item-top">
                            <strong>{notification.pecaNome || 'Negociação'}</strong>
                            {notification.naoLidas > 1 && (
                              <em>{notification.naoLidas}</em>
                            )}
                          </span>
                          <span>{notification.remetenteNome}</span>
                          <small>{notification.trechoMensagem}</small>
                          <span className="notification-link">Ver negociação</span>
                          <time>{formatDateTime(notification.dataEnvio)}</time>
                        </button>
                      ))}
                    </div>
                  )}
                </div>
              )}
            </div>
          )}
          <button className="icon-btn" onClick={() => setCurrentPage('cart')} aria-label="Carrinho">
            <ShoppingCart size={20} />
            {cartCount > 0 && <span className="cart-badge">{cartCount}</span>}
          </button>
          {user?.tipo === 'ADMINISTRADOR' && (
            <button className="icon-btn" onClick={handleAdminClick} aria-label="Administrador">
              <ShieldCheck size={20} />
            </button>
          )}
          {user ? (
            <div className="user-pill" onClick={handleUserClick}>
              <User size={18} />
              <span>{user.nome}</span>
            </div>
          ) : (
            <>
              <button className="ghost-btn" onClick={() => setCurrentPage('login')}>
                Entrar
              </button>
              <button className="cta-btn small" onClick={() => setCurrentPage('register')}>
                Cadastrar
              </button>
            </>
          )}
          <button
            className="icon-btn menu-btn"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            aria-label="Menu"
          >
            {mobileMenuOpen ? <X size={20} /> : <Menu size={20} />}
          </button>
        </div>
      </div>
    </header>
  );
};
const HomePage = () => {
  const { setCurrentPage, openProduct, addToCart, user } = useApp();
  const [featuredProducts, setFeaturedProducts] = useState([]);
  const featuredTrackRef = useRef(null);
  const scrollFeatured = (direction) => {
    const track = featuredTrackRef.current;
    if (!track) return;
    const amount = track.clientWidth * 0.8;
    track.scrollBy({ left: direction === 'left' ? -amount : amount, behavior: 'smooth' });
  };
  const heroStats = [
    { label: 'Anúncios ativos', value: '10k+' },
    { label: 'Revendedores SC', value: '480+' },
    { label: 'Pedidos enviados', value: '32k+' }
  ];
  const featureItems = [
    {
      title: 'Qualidade garantida',
      text: 'Cadastros completos, fotos e dados essenciais para comparar melhor.',
      icon: CheckCircle2
    },
    {
      title: 'Confiança local',
      text: 'Perfis de revendedores, avaliações e atendimento focado em Santa Catarina.',
      icon: ShieldCheck
    },
    {
      title: 'Agilidade',
      text: 'Busca por cidade e estoque para encontrar a peça certa sem perder tempo.',
      icon: Clock3
    }
  ];
  const storySteps = [
    { title: 'Busque e compare', text: 'Filtros por cidade, marca e categoria.', icon: Search },
    { title: 'Negocie na plataforma', text: 'Combine valor, frete e condições dentro da negociação.', icon: MessageSquare },
    { title: 'Retire ou receba', text: 'Acompanhe o pedido até a confirmação do pagamento.', icon: Package }
  ];
  const securitySteps = [
    {
      title: 'Pedido bloqueado',
      text: 'O pedido fica aguardando a negociação antes de liberar qualquer próxima etapa.',
      icon: ShieldCheck
    },
    {
      title: 'Acordo confirmado',
      text: 'Cliente e revendedor aprovam valor e condições antes do pagamento.',
      icon: CheckCircle2
    },
    {
      title: 'Pagamento informado',
      text: 'O cliente informa o pagamento e o status fica pendente de conferência.',
      icon: Clock3
    },
    {
      title: 'Liberação segura',
      text: 'Só depois da confirmação o pedido é liberado ao revendedor.',
      icon: Package
    }
  ];

  const regionalDealers = [
    { cidade: 'Florianópolis', quantidade: 86 },
    { cidade: 'Joinville', quantidade: 74 },
    { cidade: 'Blumenau', quantidade: 58 },
    { cidade: 'Itajaí', quantidade: 42 },
    { cidade: 'Chapecó', quantidade: 39 },
    { cidade: 'Criciúma', quantidade: 34 }
  ];
  const testimonials = [
    {
      nome: 'Rafael Martins',
      cidade: 'Joinville',
      nota: 5,
      comentario: 'Achei um alternador usado com garantia e fechei tudo pelo chat.'
    },
    {
      nome: 'Carla Hoffmann',
      cidade: 'Blumenau',
      nota: 5,
      comentario: 'O filtro por cidade economizou tempo e a retirada foi no mesmo dia.'
    },
    {
      nome: 'Auto Peças Costa',
      cidade: 'Itajaí',
      nota: 4,
      comentario: 'O painel deixou pedidos e negociações bem mais fáceis de acompanhar.'
    },
    {
      nome: 'Marcos Vieira',
      cidade: 'Chapecó',
      nota: 5,
      comentario: 'Consegui comparar preço, estado da peça e reputação antes de comprar.'
    }
  ];
  const handleAnnounceClick = () => {
    if (user?.tipo === 'REVENDEDOR') {
      setCurrentPage('dashboard');
    } else {
      setCurrentPage('register');
    }
  };

  useEffect(() => {
    fetchPecas()
      .then((data) => setFeaturedProducts(data.slice(0, 9)))
      .catch((err) => console.error(err));
  }, []);

  return (
    <div className="home-page">
      <section className="hero">
        <div className="container hero-content">
          <div className="hero-text">
            <div className="hero-pill">Classificados automotivos em SC</div>
            <h1>Encontre a peça certa com rapidez e confiança.</h1>
            <p>
              Compra e venda de peças usadas com revendedores verificados, estoque real
              e entrega segura.
            </p>
            <div className="hero-badges">
              <span className="hero-badge">Peças revisadas</span>
              <span className="hero-badge">Revendedores SC</span>
              <span className="hero-badge">Entrega assistida</span>
            </div>
            <div className="hero-actions">
              <button className="cta-btn" onClick={() => setCurrentPage('products')}>
                Ver peças
              </button>
              {(!user || user?.tipo === 'REVENDEDOR') && (
                <button className="secondary-btn" onClick={handleAnnounceClick}>
                  Anunciar peça
                </button>
              )}
            </div>
          </div>
          <div className="hero-visual">
            <div className="hero-card">
              <div className="hero-card-header">
                <div className="hero-card-icon">
                  <Package size={24} />
                </div>
                <span>Catálogo verificado</span>
              </div>
              <h3>Mais de 10 mil peças prontas para comparar</h3>
              <p>Estoque atualizado diariamente por revendedores locais em Santa Catarina.</p>
              <div className="hero-card-grid">
                <div>
                  <strong>480+</strong>
                  <span>Revendedores</span>
                </div>
                <div>
                  <strong>24h</strong>
                  <span>Atualização média</span>
                </div>
              </div>
              <div className="hero-card-footer">
                <span>Carroceria</span>
                <span>Transmissão</span>
                <span>Freios</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="highlight">
        <div className="container">
          <div className="section-title">
            <h2>Destaques da semana</h2>
            <p>Selecionamos as peças mais procuradas.</p>
          </div>
          <div className="featured-carousel">
            <button
              type="button"
              className="carousel-nav-btn"
              onClick={() => scrollFeatured('left')}
              aria-label="Ver destaques anteriores"
            >
              <ChevronLeft size={18} />
            </button>
            <div className="featured-carousel-track" ref={featuredTrackRef}>
              {featuredProducts.map((product) => (
                <ProductCard
                  key={product.id}
                  product={product}
                  onView={openProduct}
                  onAdd={addToCart}
                />
              ))}
            </div>
            <button
              type="button"
              className="carousel-nav-btn"
              onClick={() => scrollFeatured('right')}
              aria-label="Ver próximos destaques"
            >
              <ChevronRight size={18} />
            </button>
          </div>
        </div>
      </section>

      <section className="hero-stats">
        <div className="container stats-grid">
          {heroStats.map((stat) => (
            <div className="stat-card" key={stat.label}>
              <span className="stat-label">{stat.label}</span>
              <span className="stat-number">{stat.value}</span>
            </div>
          ))}
        </div>
      </section>

      <section className="features">
        <div className="container">
          <div className="section-title">
            <h2>Por que usar Metal-SC</h2>
            <p>Processo simples, seguro e transparente.</p>
          </div>
          <div className="features-grid">
            {featureItems.map((feature) => (
              <div className="feature-card" key={feature.title}>
                <div className="feature-icon" aria-hidden="true">
                  <feature.icon size={20} />
                </div>
                <h3>{feature.title}</h3>
                <p>{feature.text}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="story-section">
        <div className="container">
          <div className="section-title">
            <h2>Como funciona</h2>
            <p>Do anúncio ao envio em poucos passos.</p>
          </div>
          <div className="story-grid">
            {storySteps.map((step, index) => (
              <div className="story-card" key={step.title}>
                <div className="story-header">
                  <span className="story-index">0{index + 1}</span>
                  <div className="story-icon" aria-hidden="true">
                    <step.icon size={18} />
                  </div>
                </div>
                <h3>{step.title}</h3>
                <p>{step.text}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="security-section">
        <div className="container">
          <div className="section-title">
            <h2>Como garantimos sua segurança na compra</h2>
            <p>O pedido só avança quando negociação, pagamento e confirmação estão alinhados.</p>
          </div>
          <div className="security-grid">
            {securitySteps.map((step, index) => (
              <div className="security-card" key={step.title}>
                <div className="security-card-icon" aria-hidden="true">
                  <step.icon size={18} />
                </div>
                <span>0{index + 1}</span>
                <h3>{step.title}</h3>
                <p>{step.text}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="region-section">
        <div className="container">
          <div className="section-title with-icon">
            <MapPinned size={22} />
            <div>
              <h2>Revendedores por região</h2>
              <p>Compre de lojas próximas e encontre peças em cidades estratégicas de SC.</p>
            </div>
          </div>
          <div className="region-grid">
            {regionalDealers.map((region) => (
              <div className="region-card" key={region.cidade}>
                <strong>{region.cidade}</strong>
                <span>{region.quantidade} revendedores</span>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="testimonials-section">
        <div className="container">
          <div className="section-title">
            <h2>Depoimentos</h2>
            <p>Experiências recentes de quem compra e vende peças em Santa Catarina.</p>
          </div>
          <div className="testimonial-grid">
            {testimonials.map((testimonial) => (
              <article className="testimonial-card" key={`${testimonial.nome}-${testimonial.cidade}`}>
                <div className="testimonial-head">
                  <div className="testimonial-avatar" aria-hidden="true">
                    {getInitials(testimonial.nome)}
                  </div>
                  <div>
                    <strong>{testimonial.nome}</strong>
                    <span>{testimonial.cidade}</span>
                  </div>
                </div>
                <div className="testimonial-stars" aria-label={`${testimonial.nota} de 5 estrelas`}>
                  {Array.from({ length: 5 }).map((_, index) => (
                    <Star
                      key={`${testimonial.nome}-${index}`}
                      size={15}
                      fill={index < testimonial.nota ? 'currentColor' : 'none'}
                    />
                  ))}
                </div>
                <p>{testimonial.comentario}</p>
              </article>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
};

const ProductsPage = () => {
  const { openProduct, addToCart, searchQuery, setSearchQuery, selectedCity, setSelectedCity } = useApp();
  const [products, setProducts] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('');
  const [selectedState, setSelectedState] = useState('');
  const [minPrice, setMinPrice] = useState('');
  const [maxPrice, setMaxPrice] = useState('');
  const [loading, setLoading] = useState(true);
  const [showFilters, setShowFilters] = useState(false);

  const scCities = [
    'Florianópolis',
    'Joinville',
    'Blumenau',
    'São José',
    'Criciúma',
    'Chapecó',
    'Itajaí',
    'Jaraguá do Sul',
    'Palhoça',
    'Lages',
    'Balneário Camboriú',
    'Brusque',
    'Tubarão',
    'São Bento do Sul',
    'Caçador',
    'Concórdia',
    'Rio do Sul',
    'Araranguá',
    'Biguaçu',
    'Navegantes'
  ];

  const categories = [
    'Motor',
    'Suspensão',
    'Freios',
    'Elétrica',
    'Câmbio e Transmissão',
    'Arrefecimento',
    'Escapamento',
    'Rodas e Pneus',
    'Ar Condicionado',
    'Iluminação',
    'Interior e Acabamento',
    'Vidros e Retrovisores',
    'Direção',
    'Combustível e Injeção',
    'Carroceria e Lataria',
    'Acessórios'
  ];
  const states = ['NOVO', 'USADO', 'RECONDICIONADO', 'DEFEITUOSO'];
  const estadoLabels = {
    NOVO: 'Novo',
    USADO: 'Usado',
    RECONDICIONADO: 'Recondicionado',
    DEFEITUOSO: 'Defeituoso'
  };

  useEffect(() => {
    setLoading(true);
    fetchPecas()
      .then((data) => {
        setProducts(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setLoading(false);
      });
  }, []);

  const filteredProducts = useMemo(() => {
    return products.filter((product) => {
      const matchesQuery =
        !searchQuery ||
        product.nome?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        product.descricao?.toLowerCase().includes(searchQuery.toLowerCase());
      const matchesCategory = !selectedCategory || product.categoria === selectedCategory;
      const matchesState = !selectedState || product.estado === selectedState;
      const matchesCity =
        !selectedCity ||
        product.endereco?.cidade?.toLowerCase().includes(selectedCity.toLowerCase());
      const matchesUf =
        !product.endereco?.estado ||
        product.endereco.estado.toLowerCase().includes('sc');
      const matchesMinPrice =
        !minPrice || Number(product.preco) >= Number(minPrice);
      const matchesMaxPrice =
        !maxPrice || Number(product.preco) <= Number(maxPrice);

      return (
        matchesQuery &&
        matchesCategory &&
        matchesState &&
        matchesCity &&
        matchesUf &&
        matchesMinPrice &&
        matchesMaxPrice
      );
    });
  }, [products, searchQuery, selectedCategory, selectedState, selectedCity, minPrice, maxPrice]);

  return (
    <div className="products-page container">
      <div className="page-header">
        <div>
          <h1>Catálogo de peças</h1>
          <p>{filteredProducts.length} resultados encontrados</p>
        </div>
        <button className="ghost-btn" onClick={() => setShowFilters((prev) => !prev)}>
          <Filter size={16} /> Filtros
        </button>
      </div>

      <div className={`filters ${showFilters ? 'show' : ''}`}>
        <div className="filter-group">
          <label>Buscar</label>
          <div className="input-icon">
            <Search size={16} />
            <input
              type="text"
              placeholder="Nome ou descricao"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>
        </div>
        <div className="filter-group">
          <label>Categoria</label>
          <select value={selectedCategory} onChange={(e) => setSelectedCategory(e.target.value)}>
            <option value="">Todas</option>
            {categories.map((category) => (
              <option key={category} value={category}>{category}</option>
            ))}
          </select>
        </div>
        <div className="filter-group">
          <label>Estado da peça</label>
          <select value={selectedState} onChange={(e) => setSelectedState(e.target.value)}>
            <option value="">Todos</option>
            {states.map((state) => (
              <option key={state} value={state}>{estadoLabels[state] || state}</option>
            ))}
          </select>
        </div>
        <div className="filter-group">
          <label>Cidade</label>
          <select value={selectedCity} onChange={(e) => setSelectedCity(e.target.value)}>
            <option value="">Todas as cidades de SC</option>
            {scCities.map((city) => (
              <option key={city} value={city}>{city}</option>
            ))}
          </select>
        </div>
        <div className="filter-group">
          <label>Preço mínimo</label>
          <input
            type="number"
            min="0"
            value={minPrice}
            onChange={(e) => setMinPrice(e.target.value)}
            placeholder="0"
          />
        </div>
        <div className="filter-group">
          <label>Preço máximo</label>
          <input
            type="number"
            min="0"
            value={maxPrice}
            onChange={(e) => setMaxPrice(e.target.value)}
            placeholder="10000"
          />
        </div>
      </div>

      {loading ? (
        <div className="empty-state">Carregando peças...</div>
      ) : (
        <div className="product-grid">
          {filteredProducts.map((product) => (
            <ProductCard
              key={product.id}
              product={product}
              onView={openProduct}
              onAdd={addToCart}
            />
          ))}
        </div>
      )}
    </div>
  );
};
const ProductDetailPage = () => {
  const { selectedProduct, addToCart, setCurrentPage, openProfile, user } = useApp();
  const toast = useToast();
  const [currentImageIndex, setCurrentImageIndex] = useState(0);
  const [reportOpen, setReportOpen] = useState(false);
  const [reportReason, setReportReason] = useState('');
  const [reportLoading, setReportLoading] = useState(false);

  if (!selectedProduct) {
    return (
      <div className="container empty-state">
        <Package size={40} />
        <h2>Selecione uma peça no catálogo.</h2>
        <button className="cta-btn" onClick={() => setCurrentPage('products')}>
          Ver catálogo
        </button>
      </div>
    );
  }

  const images = (selectedProduct.imagens || []).map(resolveImageUrl);
  const currentImageUrl = selectedProduct.imagens?.[currentImageIndex] || '';
  const hasImages = images.length > 0;
  const estoqueValue = toNumberOrNull(selectedProduct?.estoque);
  const outOfStock = estoqueValue !== null && estoqueValue <= 0;

  const goPrev = () => {
    setCurrentImageIndex((prev) => (prev === 0 ? images.length - 1 : prev - 1));
  };

  const goNext = () => {
    setCurrentImageIndex((prev) => (prev === images.length - 1 ? 0 : prev + 1));
  };

  const openReport = () => {
    if (!user) {
      toast.warning('Faça login para denunciar uma imagem.');
      setCurrentPage('login');
      return;
    }
    setReportOpen(true);
  };

  const submitReport = async (event) => {
    event.preventDefault();
    if (!currentImageUrl) return;
    setReportLoading(true);
    try {
      await denunciarPecaImagem(selectedProduct.id, {
        usuarioId: user.id,
        imagemUrl: currentImageUrl,
        motivo: reportReason
      });
      setReportOpen(false);
      setReportReason('');
      toast.success('Denúncia enviada para moderação.');
    } catch (err) {
      toast.error(err.message || 'Erro ao enviar denúncia.');
    } finally {
      setReportLoading(false);
    }
  };

  return (
    <div className="container detail-page">
      <button className="ghost-btn" onClick={() => setCurrentPage('products')}>
        Voltar ao catálogo
      </button>
      <div className="detail-card">
        <div className="detail-image">
          {hasImages ? (
            <>
              <img src={images[currentImageIndex]} alt={selectedProduct.nome} />
              {images.length > 1 && (
                <div className="carousel-controls">
                  <button type="button" className="icon-btn" onClick={goPrev}>
                    ‹
                  </button>
                  <button type="button" className="icon-btn" onClick={goNext}>
                    ›
                  </button>
                </div>
              )}
              {images.length > 1 && (
                <div className="carousel-thumbs">
                  {images.map((img, index) => (
                    <button
                      key={img}
                      type="button"
                      className={`thumb ${index === currentImageIndex ? 'active' : ''}`}
                      onClick={() => setCurrentImageIndex(index)}
                    >
                      <img src={img} alt={`Imagem ${index + 1}`} />
                    </button>
                  ))}
                </div>
              )}
              <button type="button" className="image-report-btn" onClick={openReport}>
                <Flag size={16} /> Denunciar imagem
              </button>
            </>
          ) : (
            <div className="image-placeholder">
              <Package size={32} />
            </div>
          )}
        </div>
        {reportOpen && (
          <div className="modal-backdrop" onClick={() => setReportOpen(false)}>
            <form className="report-modal" onSubmit={submitReport} onClick={(event) => event.stopPropagation()}>
              <div className="modal-head">
                <h3>Denunciar imagem</h3>
                <button type="button" className="icon-btn" onClick={() => setReportOpen(false)}>
                  <X size={18} />
                </button>
              </div>
              <img src={images[currentImageIndex]} alt="Imagem denunciada" />
              <label>
                Motivo
                <textarea
                  value={reportReason}
                  onChange={(event) => setReportReason(event.target.value)}
                  placeholder="Explique o problema encontrado"
                  rows="4"
                />
              </label>
              <div className="table-actions">
                <button type="button" className="ghost-btn" onClick={() => setReportOpen(false)}>
                  Cancelar
                </button>
                <button type="submit" className="cta-btn" disabled={reportLoading}>
                  {reportLoading ? 'Enviando...' : 'Enviar denuncia'}
                </button>
              </div>
            </form>
          </div>
        )}
        <div className="detail-info">
          <h1>{selectedProduct.nome}</h1>
          <p className="detail-meta">
            {selectedProduct.marca || 'Marca não informada'} - {selectedProduct.categoria}
          </p>
          {selectedProduct.endereco?.cidade && (
            <p className="detail-meta">
              {selectedProduct.endereco.cidade}{selectedProduct.endereco.estado ? ` - ${selectedProduct.endereco.estado}` : ''}
            </p>
          )}
          <p className="detail-desc">{selectedProduct.descricao || 'Sem descrição.'}</p>
          <div className="detail-price">{formatPrice(selectedProduct.preco)}</div>
          <div className="detail-actions">
            <button
              className="cta-btn"
              onClick={() => addToCart(selectedProduct)}
              disabled={outOfStock}
            >
              {outOfStock ? 'Sem estoque' : 'Adicionar ao carrinho'}
            </button>
            <button className="secondary-btn" onClick={() => setCurrentPage('cart')}>
              Ir para o carrinho
            </button>
          </div>
          {selectedProduct.revendedorId && (
            <div className="detail-seller">
              <span>Revendedor</span>
              <button
                className="ghost-btn small"
                onClick={() =>
                  openProfile({ id: selectedProduct.revendedorId, tipo: 'REVENDEDOR' })
                }
              >
                Ver perfil
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

const CartPage = () => {
  const { cart, user, setCurrentPage, updateCartQty, removeFromCart, clearCart, openNegotiation } = useApp();
  const toast = useToast();
  const [loading, setLoading] = useState(false);

  const total = cart.reduce((sum, item) => sum + item.preco * item.quantidade, 0);

  const handleCheckout = async () => {
    if (!user) {
      toast.warning('Faça login para finalizar a compra.');
      setCurrentPage('login');
      return;
    }

    setLoading(true);
    try {
      const firstItem = cart[0];
      const revendedorId =
        firstItem?.revendedorId || firstItem?.vendedorId || firstItem?.vendedor?.id;

      if (!revendedorId) {
        toast.error('Não foi possível identificar o revendedor do pedido.');
        return;
      }

      const pedido = {
        clienteId: user.id,
        revendedorId,
        itens: cart.map((item) => ({
          pecaId: item.id,
          quantidade: item.quantidade,
          precoUnitario: item.preco
        })),
        valorTotal: total,
        enderecoEntrega: {
          rua: 'Rua Exemplo',
          numero: '123',
          cidade: 'Florianópolis',
          estado: 'SC',
          cep: '88000-000'
        }
      };

      const criado = await createPedido(pedido);
      toast.success('Pedido criado. O chat de negociação foi aberto.');
      clearCart();
      if (criado?.conversaId) {
        openNegotiation(criado.conversaId);
      } else {
        setCurrentPage('dashboard');
      }
    } catch (err) {
      console.error(err);
      toast.error(err.message || 'Erro ao processar pedido.');
    } finally {
      setLoading(false);
    }
  };

  if (cart.length === 0) {
    return (
      <div className="container empty-state">
        <ShoppingCart size={48} />
        <h2>Seu carrinho está vazio</h2>
        <button className="cta-btn" onClick={() => setCurrentPage('products')}>
          Ver peças
        </button>
      </div>
    );
  }

  return (
    <div className="container cart-page">
      <h1>Carrinho</h1>
      <div className="cart-grid">
        <div className="cart-items">
          {cart.map((item) => (
            <div key={item.id} className="cart-item">
              <div>
                <h3>{item.nome}</h3>
                <p>{item.marca || 'Marca nao informada'}</p>
                <div className="item-price">{formatPrice(item.preco)}</div>
              </div>
              <div className="cart-controls">
                <input
                  type="number"
                  min="1"
                  max={toNumberOrNull(item.estoque) ?? undefined}
                  value={item.quantidade}
                  onChange={(e) => updateCartQty(item.id, Number(e.target.value))}
                />
                <button className="ghost-btn" onClick={() => removeFromCart(item.id)}>
                  Remover
                </button>
              </div>
            </div>
          ))}
        </div>
        <div className="cart-summary">
          <h2>Resumo</h2>
          <div className="summary-line">
            <span>Total</span>
            <strong>{formatPrice(total)}</strong>
          </div>
          <button className="cta-btn" onClick={handleCheckout} disabled={loading}>
            {loading ? 'Abrindo chat...' : 'Confirmar no carrinho'}
          </button>
          <span className="meta-note">O pagamento será liberado após aprovação no chat.</span>
        </div>
      </div>
    </div>
  );
};
const LoginPage = () => {
  const { login, setCurrentPage } = useApp();
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (!senha) {
        setError('Senha obrigatoria');
        setLoading(false);
        return;
      }
      const response = await loginUsuario(email, senha);
      const authenticatedUser = response?.user || response;
      const token = response?.token || null;

      if (!token) {
        throw new Error('Token não retornado pelo backend');
      }

      login(authenticatedUser, token);
      setCurrentPage(authenticatedUser?.tipo === 'ADMINISTRADOR' ? 'admin' : 'home');
    } catch (err) {
      setError('Email ou senha inválidos');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container auth-page">
      <div className="auth-card">
        <h2>Entrar</h2>
        <p>Use o email e a senha cadastrados.</p>
        {error && <div className="error-message">{error}</div>}
        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label>Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label>Senha</label>
            <input
              type="password"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
            />
          </div>
          <button className="cta-btn" type="submit" disabled={loading}>
            {loading ? 'Entrando...' : 'Entrar'}
          </button>
        </form>
        <div className="auth-actions">
          <button className="ghost-btn outline" onClick={() => setCurrentPage('register')}>
            Criar conta
          </button>
        </div>
      </div>
    </div>
  );
};

const RegisterPage = () => {
  const { setCurrentPage } = useApp();
  const [formData, setFormData] = useState({
    tipo: 'CLIENTE',
    nome: '',
    email: '',
    senha: '',
    telefone: '',
    cnpj: '',
    nomeLoja: '',
    endereco: {
      rua: '',
      numero: '',
      complemento: '',
      bairro: '',
      cidade: '',
      estado: '',
      cep: ''
    }
  });
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);
  const [showSenha, setShowSenha] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleTelefoneChange = (event) => {
    const somenteDigitos = event.target.value.replace(/\D/g, '').slice(0, 11);
    setFormData((prev) => ({ ...prev, telefone: somenteDigitos }));
  };

  const handleCnpjChange = (event) => {
    const somenteDigitos = event.target.value.replace(/\D/g, '').slice(0, 14);
    setFormData((prev) => ({ ...prev, cnpj: somenteDigitos }));
  };

  const handleEnderecoChange = (event) => {
    const { name, value } = event.target;
    setFormData((prev) => ({
      ...prev,
      endereco: { ...prev.endereco, [name]: value }
    }));
  };

  const selectTipo = (tipo) => {
    setFormData((prev) => ({ ...prev, tipo }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setLoading(true);

    try {
      const payload =
        formData.tipo === 'CLIENTE'
          ? {
              nome: formData.nome,
              email: formData.email,
              senha: formData.senha,
              telefone: formData.telefone,
              tipo: 'CLIENTE',
              endereco: formData.endereco
            }
          : {
              nome: formData.nome,
              email: formData.email,
              senha: formData.senha,
              telefone: formData.telefone,
              tipo: 'REVENDEDOR',
              cnpj: formData.cnpj,
              nomeLoja: formData.nomeLoja
            };

      await registerUsuario(payload, formData.tipo);
      setSuccess(true);
      setTimeout(() => setCurrentPage('login'), 1500);
    } catch (err) {
      setError(err.message || 'Erro ao cadastrar');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container auth-page">
      <div className="auth-card">
        <div className="auth-header">
          <div className="auth-header-icon">
            <ShieldCheck size={18} />
          </div>
          <div>
            <h2>Criar conta</h2>
            <p className="auth-header-sub">Leva menos de 2 minutos</p>
          </div>
        </div>

        {error && <div className="error-message">{error}</div>}
        {success && <div className="success-message">Cadastro realizado!</div>}

        <form onSubmit={handleSubmit} className="auth-form">
          <p className="form-section-label">Tipo de conta</p>
          <div className="account-type-grid">
            <button
              type="button"
              className={`account-type-card${formData.tipo === 'CLIENTE' ? ' active' : ''}`}
              onClick={() => selectTipo('CLIENTE')}
            >
              <User size={20} />
              <span>Cliente</span>
            </button>
            <button
              type="button"
              className={`account-type-card${formData.tipo === 'REVENDEDOR' ? ' active' : ''}`}
              onClick={() => selectTipo('REVENDEDOR')}
            >
              <Store size={20} />
              <span>Revendedor</span>
            </button>
          </div>

          <p className="form-section-label">Dados da conta</p>
          <div className="form-group">
            <label htmlFor="nome">Nome completo</label>
            <input
              id="nome"
              name="nome"
              placeholder="Seu nome completo"
              autoComplete="name"
              value={formData.nome}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              name="email"
              type="email"
              placeholder="exemplo@email.com"
              autoComplete="email"
              value={formData.email}
              onChange={handleChange}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="senha">Senha</label>
            <div className="password-field">
              <input
                id="senha"
                name="senha"
                type={showSenha ? 'text' : 'password'}
                placeholder="Mínimo 8 caracteres"
                autoComplete="new-password"
                value={formData.senha}
                onChange={handleChange}
                minLength={8}
                required
              />
              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowSenha((prev) => !prev)}
                aria-label={showSenha ? 'Ocultar senha' : 'Mostrar senha'}
              >
                {showSenha ? <EyeOff size={16} /> : <Eye size={16} />}
              </button>
            </div>
          </div>
          <div className="form-group">
            <label htmlFor="telefone">Telefone</label>
            <input
              id="telefone"
              name="telefone"
              type="tel"
              inputMode="numeric"
              placeholder="47999999999"
              autoComplete="tel"
              value={formData.telefone}
              onChange={handleTelefoneChange}
              maxLength={11}
            />
          </div>

          {formData.tipo === 'REVENDEDOR' ? (
            <>
              <p className="form-section-label">Dados da loja</p>
              <div className="form-group">
                <label htmlFor="cnpj">CNPJ</label>
                <input
                  id="cnpj"
                  name="cnpj"
                  type="tel"
                  inputMode="numeric"
                  placeholder="00000000000000"
                  autoComplete=""
                  value={formData.cnpj}
                  onChange={handleCnpjChange}
                  maxLength={14}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="nomeLoja">Nome da loja</label>
                <input
                  id="nomeLoja"
                  name="nomeLoja"
                  placeholder="Metal Peças Joinville"
                  value={formData.nomeLoja}
                  onChange={handleChange}
                  required
                />
              </div>
            </>
          ) : (
            <>
              <p className="form-section-label">Endereço</p>
              <div className="form-group">
                <label htmlFor="rua">Rua</label>
                <input
                  id="rua"
                  name="rua"
                  placeholder="Rua das Flores"
                  autoComplete="address-line1"
                  value={formData.endereco.rua}
                  onChange={handleEnderecoChange}
                />
              </div>
              <div className="form-row form-row-2">
                <div className="form-group">
                  <label htmlFor="numero">Número</label>
                  <input
                    id="numero"
                    name="numero"
                    placeholder="123"
                    value={formData.endereco.numero}
                    onChange={handleEnderecoChange}
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="bairro">Bairro</label>
                  <input
                    id="bairro"
                    name="bairro"
                    placeholder="Centro"
                    value={formData.endereco.bairro}
                    onChange={handleEnderecoChange}
                  />
                </div>
              </div>
              <div className="form-row form-row-3">
                <div className="form-group">
                  <label htmlFor="cidade">Cidade</label>
                  <input
                    id="cidade"
                    name="cidade"
                    placeholder="Joinville"
                    value={formData.endereco.cidade}
                    onChange={handleEnderecoChange}
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="estado">UF</label>
                  <input
                    id="estado"
                    name="estado"
                    placeholder="SC"
                    maxLength={2}
                    value={formData.endereco.estado}
                    onChange={handleEnderecoChange}
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="cep">CEP</label>
                  <input
                    id="cep"
                    name="cep"
                    placeholder="89201-000"
                    autoComplete="postal-code"
                    value={formData.endereco.cep}
                    onChange={handleEnderecoChange}
                  />
                </div>
              </div>
            </>
          )}

          <button className="cta-btn" type="submit" disabled={loading}>
            {loading ? 'Salvando...' : 'Criar conta'}
          </button>
        </form>

        <p className="auth-switch">
          Já tem conta?{' '}
          <button type="button" className="auth-switch-link" onClick={() => setCurrentPage('login')}>
            Entrar
          </button>
        </p>
      </div>
    </div>
  );
};
const DashboardPage = () => {
  const { user, setCurrentPage, logout } = useApp();

  if (!user) {
    return (
      <div className="container empty-state">
        <h2>Faça login para acessar o painel</h2>
        <button className="cta-btn" onClick={() => setCurrentPage('login')}>
          Entrar
        </button>
      </div>
    );
  }

  if (user.tipo === 'ADMINISTRADOR') {
    return <AdminPage />;
  }

  return (
    <div className="container dashboard-page">
      <div className="dashboard-header">
        <div>
          <h1>Painel</h1>
          <p>Ola, {user.nome}</p>
        </div>
        <button className="ghost-btn" onClick={logout}>
          <LogOut size={16} /> Sair
        </button>
      </div>
      {user.tipo === 'REVENDEDOR' ? (
        <RevendedorDashboard />
      ) : (
        <ClienteDashboard />
      )}
    </div>
  );
};

const ProfileSettingsPanel = () => {
  const { user, updateUser } = useApp();
  const [formData, setFormData] = useState({
    nome: '',
    telefone: '',
    nomeLoja: '',
    endereco: {
      rua: '',
      numero: '',
      complemento: '',
      bairro: '',
      cidade: '',
      estado: '',
      cep: ''
    }
  });
  const [saving, setSaving] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    if (!user) return;
    setFormData({
      nome: user.nome || '',
      telefone: user.telefone || '',
      nomeLoja: user.nomeLoja || '',
      endereco: {
        rua: user.endereco?.rua || '',
        numero: user.endereco?.numero || '',
        complemento: user.endereco?.complemento || '',
        bairro: user.endereco?.bairro || '',
        cidade: user.endereco?.cidade || '',
        estado: user.endereco?.estado || '',
        cep: user.endereco?.cep || ''
      }
    });
  }, [user]);

  if (!user) return null;

  const isRevendedor = user.tipo === 'REVENDEDOR';

  const handleEnderecoChange = (event) => {
    const { name, value } = event.target;
    setFormData((prev) => ({
      ...prev,
      endereco: { ...prev.endereco, [name]: value }
    }));
  };

  const handleSave = async (event) => {
    event.preventDefault();
    setSaving(true);
    setError('');
    setMessage('');
    try {
      const payload = {
        nome: formData.nome,
        telefone: formData.telefone,
        endereco: formData.endereco
      };
      if (isRevendedor) {
        payload.nomeLoja = formData.nomeLoja;
      }

      const updated = await updatePerfil(user.id, payload);

      updateUser(updated);
      setMessage('Perfil atualizado.');
    } catch (err) {
      setError(err.message || 'Erro ao atualizar perfil');
    } finally {
      setSaving(false);
    }
  };

  const handleUploadFoto = async (event) => {
    const file = event.target.files?.[0];
    if (!file) return;
    setUploading(true);
    setError('');
    setMessage('');
    try {
      const data = await uploadUsuarioFoto(user.id, file);
      const fotoUrl = data?.url;
      if (fotoUrl) {
        updateUser({ ...user, fotoUrl });
        setMessage('Foto atualizada.');
      }
    } catch (err) {
      setError(err.message || 'Erro ao enviar foto');
    } finally {
      setUploading(false);
    }
  };

  return (
    <div className="panel profile-settings">
      <div className="panel-header">
        <h2>Meu perfil</h2>
      </div>
      {error && <div className="error-message">{error}</div>}
      {message && <div className="success-message">{message}</div>}
      <div className="profile-settings-grid">
        <div className="profile-photo">
          <div className="avatar">
            {user.fotoUrl ? (
              <img src={resolveImageUrl(user.fotoUrl)} alt="Foto de perfil" />
            ) : (
              <User size={22} />
            )}
          </div>
          <label className="ghost-btn small">
            {uploading ? 'Enviando...' : 'Trocar foto'}
            <input
              type="file"
              accept="image/*"
              onChange={handleUploadFoto}
              disabled={uploading}
              hidden
            />
          </label>
        </div>

        <form onSubmit={handleSave} className="profile-form">
          <div className="form-group">
            <label>Nome</label>
            <input
              value={formData.nome}
              onChange={(e) => setFormData((prev) => ({ ...prev, nome: e.target.value }))}
              required
            />
          </div>
          <div className="form-group">
            <label>Telefone</label>
            <input
              value={formData.telefone}
              onChange={(e) =>
                setFormData((prev) => ({ ...prev, telefone: e.target.value }))
              }
            />
          </div>
          {isRevendedor && (
            <div className="form-group">
              <label>Nome da loja</label>
              <input
                value={formData.nomeLoja}
                onChange={(e) =>
                  setFormData((prev) => ({ ...prev, nomeLoja: e.target.value }))
                }
              />
            </div>
          )}
          <div className="form-group">
            <label>Rua</label>
            <input
              name="rua"
              value={formData.endereco.rua}
              onChange={handleEnderecoChange}
            />
          </div>
          <div className="form-group">
            <label>Número</label>
            <input
              name="numero"
              value={formData.endereco.numero}
              onChange={handleEnderecoChange}
            />
          </div>
          <div className="form-group">
            <label>Bairro</label>
            <input
              name="bairro"
              value={formData.endereco.bairro}
              onChange={handleEnderecoChange}
            />
          </div>
          <div className="form-group">
            <label>Cidade</label>
            <input
              name="cidade"
              value={formData.endereco.cidade}
              onChange={handleEnderecoChange}
            />
          </div>
          <div className="form-group">
            <label>Estado</label>
            <input
              name="estado"
              value={formData.endereco.estado}
              onChange={handleEnderecoChange}
            />
          </div>
          <div className="form-group">
            <label>CEP</label>
            <input
              name="cep"
              value={formData.endereco.cep}
              onChange={handleEnderecoChange}
            />
          </div>
          <button
            className={`cta-btn ${isRevendedor ? 'profile-save' : ''}`}
            type="submit"
            disabled={saving}
          >
            {saving ? 'Salvando...' : 'Salvar alterações'}
          </button>
        </form>
      </div>
    </div>
  );
};

const ProfilePage = () => {
  const { user, selectedProfile, setCurrentPage } = useApp();
  const confirm = useConfirm();
  const [profile, setProfile] = useState(null);
  const [comentarios, setComentarios] = useState([]);
  const [media, setMedia] = useState(null);
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState('');
  const [formData, setFormData] = useState({ nota: 5, comentario: '' });

  const loadProfile = useCallback(async () => {
    if (!selectedProfile?.id) return;
    setLoading(true);
    setError('');
    try {
      const perfilData = await fetchPerfilById(selectedProfile.id);
      setProfile(perfilData);

      const comentariosData = await fetchComentariosPerfilByAlvo(selectedProfile.id);
      setComentarios(comentariosData || []);

      if (perfilData?.tipo === 'REVENDEDOR') {
        const mediaData = await fetchComentarioPerfilMedia(selectedProfile.id);
        setMedia(mediaData);
      } else {
        setMedia(null);
      }
    } catch (err) {
      console.error(err);
      setError(err.message || 'Erro ao carregar perfil');
    } finally {
      setLoading(false);
    }
  }, [selectedProfile?.id]);

  useEffect(() => {
    loadProfile();
  }, [loadProfile]);

  if (!selectedProfile) {
    return (
      <div className="container empty-state">
        <h2>Perfil não selecionado.</h2>
        <button
          className="cta-btn"
          onClick={() => setCurrentPage(user ? 'dashboard' : 'home')}
        >
          Voltar
        </button>
      </div>
    );
  }

  const profileTipo = profile?.tipo || selectedProfile.tipo;
  const isRevendedor = profileTipo === 'REVENDEDOR';
  const isCliente = profileTipo === 'CLIENTE';

  const canComment =
    user &&
    profile &&
    user.id !== profile.id &&
    ((user.tipo === 'CLIENTE' && isRevendedor) ||
      (user.tipo === 'REVENDEDOR' && isCliente));

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!user || !profile) return;

    const comentarioTexto = (formData.comentario || '').trim();
    const precisaNota = user.tipo === 'CLIENTE' && isRevendedor;

    if (precisaNota && (!formData.nota || formData.nota < 1)) {
      setError('Nota obrigatoria para avaliar revendedor');
      return;
    }

    if (!comentarioTexto && !precisaNota) {
      setError('Comentario obrigatorio');
      return;
    }

    setSending(true);
    setError('');
    try {
      const payload = {
        autorId: user.id,
        alvoId: profile.id,
        comentario: comentarioTexto
      };
      if (precisaNota) {
        payload.nota = Number(formData.nota);
      }

      await createComentarioPerfil(payload);
      setFormData({ nota: 5, comentario: '' });
      await loadProfile();
    } catch (err) {
      console.error(err);
      setError(err.message || 'Erro ao enviar comentário');
    } finally {
      setSending(false);
    }
  };

  const handleDeleteComentario = async (comentarioId) => {
    if (!comentarioId) return;
    const confirmed = await confirm('Excluir este comentário?', { danger: true, confirmLabel: 'Excluir' });
    if (!confirmed) return;
    try {
      await deleteComentarioPerfil(comentarioId);
      await loadProfile();
    } catch (err) {
      console.error(err);
      setError(err.message || 'Erro ao excluir comentário');
    }
  };

  let commentHint = 'Faca login para comentar.';
  if (user && profile && user.id === profile.id) {
    commentHint = 'Você está vendo seu próprio perfil.';
  } else if (user && !canComment) {
    commentHint = 'Seu tipo de usuário não pode comentar este perfil.';
  }

  return (
    <div className="container profile-page">
      <div className="profile-header">
        <div>
          <h1>Perfil</h1>
          <p>{profile?.nome || 'Usuário'}</p>
        </div>
        <button
          className="ghost-btn"
          onClick={() => setCurrentPage(user ? 'dashboard' : 'home')}
        >
          Voltar
        </button>
      </div>

      {error && <div className="error-message">{error}</div>}

      {loading ? (
        <div className="empty-state">Carregando perfil...</div>
      ) : (
        <div className="profile-grid">
          <div className="panel profile-card">
            <div className="profile-title">
              <div className="profile-avatar">
                {profile?.fotoUrl ? (
                  <img src={resolveImageUrl(profile.fotoUrl)} alt="Foto de perfil" />
                ) : (
                  <User size={22} />
                )}
              </div>
              <div>
                <h2>{profile?.nome || 'Usuario'}</h2>
                <span className="profile-badge">
                  {isRevendedor ? 'Revendedor' : 'Cliente'}
                </span>
              </div>
            </div>

            <div className="profile-meta">
              {isRevendedor && profile?.nomeLoja && (
                <div className="profile-line">
                  <span className="meta-chip">Loja: {profile.nomeLoja}</span>
                </div>
              )}
              {profile?.email && (
                <div className="profile-line">
                  <Mail size={16} />
                  <span>{profile.email}</span>
                </div>
              )}
              {profile?.telefone && (
                <div className="profile-line">
                  <Phone size={16} />
                  <span>{profile.telefone}</span>
                </div>
              )}
              {profile?.endereco?.cidade && (
                <div className="profile-line">
                  <MapPin size={16} />
                  <span>
                    {profile.endereco.cidade}
                    {profile.endereco.estado ? ` - ${profile.endereco.estado}` : ''}
                  </span>
                </div>
              )}
            </div>

            <div className="profile-rating">
              {isRevendedor && (
                <span className="meta-chip">
                  Media do perfil:{' '}
                  {media !== null && media !== undefined
                    ? Number(media).toFixed(1)
                    : '0.0'}
                </span>
              )}
              <span className="meta-note">{comentarios.length} comentarios</span>
            </div>
          </div>

          <div className="panel profile-comments">
            <div className="panel-header">
              <h2>Comentarios</h2>
            </div>

            {canComment ? (
              <form className="comment-form" onSubmit={handleSubmit}>
                {user?.tipo === 'CLIENTE' && isRevendedor && (
                  <div className="form-group">
                    <label>Nota</label>
                    <select
                      value={formData.nota}
                      onChange={(e) =>
                        setFormData((prev) => ({
                          ...prev,
                          nota: Number(e.target.value)
                        }))
                      }
                    >
                      <option value={5}>5</option>
                      <option value={4}>4</option>
                      <option value={3}>3</option>
                      <option value={2}>2</option>
                      <option value={1}>1</option>
                    </select>
                  </div>
                )}
                <div className="form-group">
                  <label>Comentario</label>
                  <textarea
                    rows="3"
                    value={formData.comentario}
                    onChange={(e) =>
                      setFormData((prev) => ({ ...prev, comentario: e.target.value }))
                    }
                    placeholder={
                      user?.tipo === 'REVENDEDOR'
                        ? 'Conte como foi a negociação com o cliente'
                        : 'Conte como foi a compra com o revendedor'
                    }
                  />
                </div>
                <button className="cta-btn" type="submit" disabled={sending}>
                  {sending ? 'Enviando...' : 'Enviar comentario'}
                </button>
              </form>
            ) : (
              <p className="meta-note">{commentHint}</p>
            )}

            {comentarios.length === 0 ? (
              <p>Sem comentarios ainda.</p>
            ) : (
              <div className="comment-list">
                {comentarios.map((comentario) => (
                  <div key={comentario.id} className="comment-card">
                    <div className="comment-head">
                      <strong>{comentario.autorNome || 'Usuario'}</strong>
                      {comentario.nota !== null && comentario.nota !== undefined && (
                        <span className="meta-chip">Nota: {comentario.nota}/5</span>
                      )}
                    </div>
                    <p>{comentario.comentario || 'Sem comentario.'}</p>
                    <div className="comment-foot">
                      <span className="meta-note">{formatDate(comentario.data)}</span>
                      {user?.id && comentario.autorId === user.id && (
                        <button
                          className="ghost-btn small"
                          onClick={() => handleDeleteComentario(comentario.id)}
                        >
                          Excluir
                        </button>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

const NegotiationInbox = ({ role }) => {
  const { user, openNegotiation } = useApp();
  const confirm = useConfirm();
  const toast = useToast();
  const [conversas, setConversas] = useState([]);
  const [loading, setLoading] = useState(false);
  const [deletingId, setDeletingId] = useState(null);
  const [reportTarget, setReportTarget] = useState(null);
  const [reportReason, setReportReason] = useState('');
  const [reportLoading, setReportLoading] = useState(false);

  const loadConversas = useCallback(async () => {
    if (!user?.id) return;
    setLoading(true);
    try {
      const data =
        role === 'REVENDEDOR'
          ? await fetchNegociacoesRevendedor(user.id)
          : await fetchNegociacoesCliente(user.id);
      setConversas(data || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [role, user?.id]);

  useEffect(() => {
    loadConversas();
    const timer = setInterval(loadConversas, 5000);
    return () => clearInterval(timer);
  }, [loadConversas]);
  const visibleConversas = useMemo(
    () => conversas.filter((conversa) => conversa.status !== 'CANCELADO'),
    [conversas]
  );

  const handleDeleteConversa = async (event, conversa) => {
    event.stopPropagation();
    if (!user?.id) return;
    const confirmed = await confirm(
      'Excluir este chat da sua lista? Ele só some para você, a outra parte continua vendo a conversa normalmente.',
      { title: 'Excluir conversa', confirmLabel: 'Excluir', danger: true }
    );
    if (!confirmed) return;
    setDeletingId(conversa.id);
    try {
      await excluirNegociacao(conversa.id, user.id);
      setConversas((prev) => prev.filter((item) => item.id !== conversa.id));
      toast.success('Conversa removida da sua lista.');
    } catch (err) {
      toast.error(err.message || 'Erro ao excluir conversa.');
    } finally {
      setDeletingId(null);
    }
  };

  const openReport = (event, conversa) => {
    event.stopPropagation();
    setReportTarget(conversa);
    setReportReason('');
  };

  const closeReport = () => {
    setReportTarget(null);
    setReportReason('');
  };

  const submitReport = async (event) => {
    event.preventDefault();
    if (!reportTarget || !user?.id) return;
    setReportLoading(true);
    try {
      await denunciarNegociacao(reportTarget.id, {
        usuarioId: user.id,
        motivo: reportReason
      });
      toast.success('Denúncia enviada. O administrador vai revisar essa conversa.');
      closeReport();
    } catch (err) {
      toast.error(err.message || 'Erro ao enviar denúncia.');
    } finally {
      setReportLoading(false);
    }
  };

  return (
    <div className="panel negotiation-inbox">
      <div className="panel-header section-panel-header">
        <div>
          <h2>Negociações</h2>
          <p>
            {visibleConversas.length > 0
              ? `${visibleConversas.length} conversa${visibleConversas.length > 1 ? 's' : ''} em andamento`
              : 'Converse com o revendedor antes do pagamento'}
          </p>
        </div>
        <button className="ghost-btn" onClick={loadConversas} disabled={loading}>
          {loading ? 'Atualizando...' : 'Atualizar'}
        </button>
      </div>
      {visibleConversas.length === 0 ? (
        <div className="empty-state compact negotiation-empty">
          <MessageSquare size={34} />
          <p>Nenhuma negociação em andamento.</p>
        </div>
      ) : (
        <div className="negotiation-list">
          {visibleConversas.map((conversa) => {
            const contactName =
              role === 'REVENDEDOR' ? conversa.clienteNome : conversa.revendedorNome;
            const contactLabel = role === 'REVENDEDOR' ? 'Cliente' : 'Revendedor';
            const negotiationValue = getNegotiationValue(conversa);
            const negotiationDate = conversa.atualizadaEm || conversa.criadaEm;

            return (
              <div key={conversa.id} className="negotiation-row-wrapper">
                <button
                  className="negotiation-row"
                  onClick={() => openNegotiation(conversa.id)}
                >
                  <span className="negotiation-main">
                    <strong>{conversa.pecaNome || 'Peça em negociação'}</strong>
                    <small>{contactLabel}: {contactName || 'Não informado'}</small>
                    <span className="negotiation-meta">
                      {negotiationDate && <em>{formatDate(negotiationDate)}</em>}
                      {negotiationValue !== null && <em>{formatPrice(negotiationValue)}</em>}
                    </span>
                  </span>
                  <span className="negotiation-row-side">
                    {conversa.naoLidas > 0 && (
                      <span className="unread-badge">{conversa.naoLidas}</span>
                    )}
                    <span className={`status-pill ${getStatusPillClass(conversa.status)}`}>
                      {formatStatusLabel(conversa.status)}
                    </span>
                  </span>
                </button>
                <span className="negotiation-row-tools">
                  <button
                    type="button"
                    className="icon-btn"
                    title="Denunciar esta conversa"
                    aria-label="Denunciar conversa"
                    onClick={(event) => openReport(event, conversa)}
                  >
                    <Flag size={16} />
                  </button>
                  <button
                    type="button"
                    className="icon-btn"
                    title="Excluir este chat da sua lista"
                    aria-label="Excluir conversa"
                    onClick={(event) => handleDeleteConversa(event, conversa)}
                    disabled={deletingId === conversa.id}
                  >
                    <Trash2 size={16} />
                  </button>
                </span>
              </div>
            );
          })}
        </div>
      )}

      {reportTarget && (
        <div className="modal-backdrop" onClick={closeReport}>
          <form className="report-modal" onSubmit={submitReport} onClick={(event) => event.stopPropagation()}>
            <div className="modal-head">
              <h3>Denunciar conversa</h3>
              <button type="button" className="icon-btn" onClick={closeReport}>
                <X size={18} />
              </button>
            </div>
            <p>
              A denúncia é enviada para o administrador junto com o histórico desta
              conversa com {role === 'REVENDEDOR' ? (reportTarget.clienteNome || 'o cliente') : (reportTarget.revendedorNome || 'o revendedor')}.
            </p>
            <label>
              Motivo
              <textarea
                value={reportReason}
                onChange={(event) => setReportReason(event.target.value)}
                placeholder="Explique o que aconteceu nessa conversa"
                rows="4"
              />
            </label>
            <div className="table-actions">
              <button type="button" className="ghost-btn" onClick={closeReport}>
                Cancelar
              </button>
              <button type="submit" className="cta-btn" disabled={reportLoading}>
                {reportLoading ? 'Enviando...' : 'Enviar denuncia'}
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};

const OrderStageTabs = ({ pedido }) => {
  const paymentComplete =
    pedido.statusPagamento === 'PAGAMENTO_CONFIRMADO' ||
    pedido.status === 'PAGAMENTO_CONFIRMADO';
  const paymentReady =
    pedido.status === 'PAGAMENTO_LIBERADO' ||
    pedido.status === 'PAGAMENTO_PENDENTE' ||
    pedido.status === 'PAGAMENTO_INFORMADO_CLIENTE';

  const stages = [
    {
      key: 'cliente',
      label: 'Cliente',
      detail: pedido.aprovacaoCliente ? 'Aprovou' : 'Pendente',
      complete: Boolean(pedido.aprovacaoCliente)
    },
    {
      key: 'revendedor',
      label: 'Revendedor',
      detail: pedido.aprovacaoRevendedor ? 'Aprovou' : 'Pendente',
      complete: Boolean(pedido.aprovacaoRevendedor)
    },
    {
      key: 'pagamento',
      label: 'Pagamento',
      detail: getPaymentStageLabel(pedido),
      complete: paymentComplete,
      ready: paymentReady
    }
  ];
  const firstOpenStage = stages.findIndex((stage) => !stage.complete);

  return (
    <div className="order-stage-tabs" role="tablist" aria-label="Etapas do pedido">
      {stages.map((stage, index) => {
        const current = !stage.complete && (stage.ready || index === firstOpenStage);
        const statusClass = stage.complete ? 'active done' : current ? 'current' : 'inactive';

        return (
          <span
            key={stage.key}
            className={`order-stage-tab ${statusClass}`}
            role="tab"
            aria-selected={stage.complete || current}
          >
            <span className="order-stage-dot" aria-hidden="true">
              {stage.complete ? <CheckCircle2 size={14} /> : index + 1}
            </span>
            <span>{stage.label}</span>
            <strong>{stage.detail}</strong>
          </span>
        );
      })}
    </div>
  );
};

const NEGOTIATION_STAGE_STATUSES = ['AGUARDANDO_NEGOCIACAO', 'PENDENTE'];

const OrderCard = ({
  pedido,
  partyName,
  partyType,
  onOpenProfile,
  onOpenNegotiation,
  onCancelled,
  action,
  note,
  requiresAction = false,
  actionHint = ''
}) => {
  const { user } = useApp();
  const confirm = useConfirm();
  const toast = useToast();
  const [chatRemoved, setChatRemoved] = useState(false);
  const [deletingChat, setDeletingChat] = useState(false);
  const [reportOpen, setReportOpen] = useState(false);
  const [reportReason, setReportReason] = useState('');
  const [reportLoading, setReportLoading] = useState(false);

  const displayName = partyName || partyType || 'Contato';
  const total = getPedidoTotal(pedido);
  const title = getPedidoTitle(pedido);
  const profileLabel = partyType
    ? `Ver perfil do ${partyType.toLowerCase()}`
    : 'Ver perfil';
  const chatOcultoNoServidor =
    user?.tipo === 'REVENDEDOR'
      ? Boolean(pedido.chatOcultoParaRevendedor)
      : Boolean(pedido.chatOcultoParaCliente);
  const hasChat =
    Boolean(pedido.conversaId) &&
    !chatRemoved &&
    !chatOcultoNoServidor &&
    pedido.status !== 'CANCELADO';
  const isNegotiationStage = NEGOTIATION_STAGE_STATUSES.includes(pedido.status);

  const handleDeleteChat = async () => {
    if (!user?.id || !pedido.conversaId) return;

    if (isNegotiationStage) {
      const confirmed = await confirm(
        'Cancelar esta negociação? O pedido será marcado como cancelado para você e para a outra parte. Essa ação não pode ser desfeita.',
        { title: 'Cancelar pedido', confirmLabel: 'Cancelar pedido', danger: true }
      );
      if (!confirmed) return;
      setDeletingChat(true);
      try {
        await encerrarNegociacao(pedido.conversaId, user.id, 'CANCELADO');
        await cancelarPedido(pedido.id);
        toast.success('Pedido e negociação cancelados.');
        onCancelled?.(pedido.id);
      } catch (err) {
        toast.error(err.message || 'Erro ao cancelar o pedido.');
      } finally {
        setDeletingChat(false);
      }
      return;
    }

    const confirmed = await confirm(
      'Apagar este chat da sua lista? Ele só some para você, a outra parte continua vendo a conversa normalmente.',
      { title: 'Apagar chat', confirmLabel: 'Apagar', danger: true }
    );
    if (!confirmed) return;
    setDeletingChat(true);
    try {
      await excluirNegociacao(pedido.conversaId, user.id);
      setChatRemoved(true);
      toast.success('Chat apagado da sua lista.');
    } catch (err) {
      toast.error(err.message || 'Erro ao apagar o chat.');
    } finally {
      setDeletingChat(false);
    }
  };

  const openReport = () => {
    setReportReason('');
    setReportOpen(true);
  };

  const closeReport = () => {
    setReportOpen(false);
    setReportReason('');
  };

  const submitReport = async (event) => {
    event.preventDefault();
    if (!user?.id || !pedido.conversaId) return;
    setReportLoading(true);
    try {
      await denunciarNegociacao(pedido.conversaId, {
        usuarioId: user.id,
        motivo: reportReason
      });
      toast.success('Denúncia enviada. O administrador vai revisar essa conversa.');
      closeReport();
    } catch (err) {
      toast.error(err.message || 'Erro ao enviar denúncia.');
    } finally {
      setReportLoading(false);
    }
  };

  return (
    <article className={`order-card ${requiresAction ? 'needs-action' : ''}`}>
      <div className="order-card-top">
        <div className="order-party">
          <div className="order-avatar" aria-hidden="true">
            {getInitials(displayName)}
          </div>
          <div>
            <div className="order-title-line">
              <strong>{title}</strong>
              {requiresAction && <span className="action-badge">Ação necessária</span>}
            </div>
            <span>
              {partyType}: {displayName} · Pedido #{pedido.id?.substring(0, 8) || 'novo'} · {formatDate(pedido.dataCriacao) || 'Data não informada'}
            </span>
          </div>
        </div>
        <div className="order-highlight">
          <span className={`status-pill ${getStatusPillClass(pedido.status)}`}>
            {formatStatusLabel(pedido.status)}
          </span>
          <strong>{formatPrice(total)}</strong>
        </div>
      </div>

      {pedido.status === 'CANCELADO' ? (
        <p className="meta-note">Este pedido foi cancelado.</p>
      ) : (
        <OrderStageTabs pedido={pedido} />
      )}

      <div className="order-card-actions">
        <div className="order-actions-left">
          {onOpenProfile && (
            <button className="ghost-btn small" onClick={onOpenProfile}>
              {profileLabel}
            </button>
          )}
          {hasChat && onOpenNegotiation && (
            <button
              className={`ghost-btn small ${requiresAction ? 'soft-emphasis' : ''}`}
              onClick={onOpenNegotiation}
            >
              Abrir chat
            </button>
          )}
          {hasChat && (
            <>
              <button
                type="button"
                className="icon-btn"
                title="Denunciar esta conversa"
                aria-label="Denunciar conversa"
                onClick={openReport}
              >
                <Flag size={16} />
              </button>
              <button
                type="button"
                className="icon-btn"
                title={isNegotiationStage ? 'Cancelar pedido e apagar chat' : 'Apagar este chat da sua lista'}
                aria-label={isNegotiationStage ? 'Cancelar pedido e apagar chat' : 'Apagar chat'}
                onClick={handleDeleteChat}
                disabled={deletingChat}
              >
                <Trash2 size={16} />
              </button>
            </>
          )}
          {(chatRemoved || chatOcultoNoServidor) && Boolean(pedido.conversaId) && (
            <span className="meta-note">Chat apagado da sua lista.</span>
          )}
        </div>
        <div className="order-actions-right">
          {actionHint && <span className="meta-note action-hint">{actionHint}</span>}
          {action || (!actionHint && note && <span className="meta-note">{note}</span>)}
        </div>
      </div>

      {reportOpen && (
        <div className="modal-backdrop" onClick={closeReport}>
          <form className="report-modal" onSubmit={submitReport} onClick={(event) => event.stopPropagation()}>
            <div className="modal-head">
              <h3>Denunciar conversa</h3>
              <button type="button" className="icon-btn" onClick={closeReport}>
                <X size={18} />
              </button>
            </div>
            <p>
              A denúncia é enviada para o administrador junto com o histórico desta
              conversa com {displayName}.
            </p>
            <label>
              Motivo
              <textarea
                value={reportReason}
                onChange={(event) => setReportReason(event.target.value)}
                placeholder="Explique o que aconteceu nessa conversa"
                rows="4"
              />
            </label>
            <div className="table-actions">
              <button type="button" className="ghost-btn" onClick={closeReport}>
                Cancelar
              </button>
              <button type="submit" className="cta-btn" disabled={reportLoading}>
                {reportLoading ? 'Enviando...' : 'Enviar denuncia'}
              </button>
            </div>
          </form>
        </div>
      )}
    </article>
  );
};

const PecaForm = ({
  editingPeca,
  formData,
  setFormData,
  categories,
  states,
  estadoLabels,
  imageFiles,
  existingImages,
  onEnderecoChange,
  onNumberChange,
  onAddFiles,
  onRemoveExistingImage,
  onRemoveNewFile,
  onSubmit
}) => {
  const [dragActive, setDragActive] = useState(false);
  const [cepLoading, setCepLoading] = useState(false);
  const [cepStatus, setCepStatus] = useState('');
  const totalImages = existingImages.length + imageFiles.length;
  const remainingImages = Math.max(3 - totalImages, 0);

  const newImagePreviews = useMemo(
    () =>
      imageFiles.map((file, index) => ({
        file,
        index,
        url: URL.createObjectURL(file)
      })),
    [imageFiles]
  );

  useEffect(() => {
    return () => {
      newImagePreviews.forEach((preview) => URL.revokeObjectURL(preview.url));
    };
  }, [newImagePreviews]);

  const handleCepLookup = async () => {
    const cep = String(formData.endereco.cep || '').replace(/\D/g, '');
    if (cep.length !== 8) {
      setCepStatus('Informe um CEP com 8 dígitos.');
      return;
    }

    setCepLoading(true);
    setCepStatus('');
    try {
      const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
      const data = await response.json();

      if (!response.ok || data.erro) {
        setCepStatus('CEP não encontrado.');
        return;
      }

      setFormData((prev) => ({
        ...prev,
        endereco: {
          ...prev.endereco,
          rua: data.logradouro || prev.endereco.rua,
          bairro: data.bairro || prev.endereco.bairro,
          cidade: data.localidade || prev.endereco.cidade,
          estado: data.uf || prev.endereco.estado,
          cep: data.cep || prev.endereco.cep
        }
      }));
      setCepStatus('Endereço preenchido pelo CEP.');
    } catch (err) {
      setCepStatus('Não foi possível consultar o CEP agora.');
    } finally {
      setCepLoading(false);
    }
  };

  const handleDrop = (event) => {
    event.preventDefault();
    setDragActive(false);
    onAddFiles(event.dataTransfer.files);
  };

  return (
    <form onSubmit={onSubmit} className="part-form">
      <div className="form-section">
        <div className="form-section-head">
          <h3>Informações básicas</h3>
          <p>Dados que ajudam o comprador a reconhecer a peça rapidamente.</p>
        </div>
        <div className="form-section-grid wide">
          <div className="form-group">
            <label>Nome</label>
            <input
              name="nome"
              value={formData.nome}
              onChange={(e) => setFormData({ ...formData, nome: e.target.value })}
              required
            />
          </div>
          <div className="form-group span-2">
            <label>Descricao</label>
            <textarea
              name="descricao"
              rows="3"
              value={formData.descricao}
              onChange={(e) => setFormData({ ...formData, descricao: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label>Categoria</label>
            <select
              name="categoria"
              value={formData.categoria}
              onChange={(e) => setFormData({ ...formData, categoria: e.target.value })}
              required
            >
              <option value="">Selecione</option>
              {categories.map((category) => (
                <option key={category} value={category}>{category}</option>
              ))}
            </select>
          </div>
          <div className="form-group">
            <label>Estado</label>
            <select
              name="estado"
              value={formData.estado}
              onChange={(e) => setFormData({ ...formData, estado: e.target.value })}
            >
              {states.map((state) => (
                <option key={state} value={state}>{estadoLabels?.[state] || state}</option>
              ))}
            </select>
          </div>
          <div className="form-group">
            <label>Marca</label>
            <input
              name="marca"
              value={formData.marca}
              onChange={(e) => setFormData({ ...formData, marca: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label>Modelo do veículo</label>
            <input
              name="modeloVeiculo"
              value={formData.modeloVeiculo}
              onChange={(e) => setFormData({ ...formData, modeloVeiculo: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label>Ano</label>
            <input
              type="number"
              name="ano"
              min="0"
              step="1"
              value={formData.ano}
              onKeyDown={(e) => {
                if (e.key === '-') e.preventDefault();
              }}
              onChange={onNumberChange}
            />
          </div>
        </div>
      </div>

      <div className="form-section">
        <div className="form-section-head">
          <h3>Preço e estoque</h3>
          <p>Valores usados no catálogo e no carrinho.</p>
        </div>
        <div className="form-section-grid compact">
          <div className="form-group">
            <label>Preço</label>
            <input
              type="number"
              name="preco"
              min="0"
              step="0.01"
              value={formData.preco}
              onKeyDown={(e) => {
                if (e.key === '-') e.preventDefault();
              }}
              onChange={onNumberChange}
              required
            />
          </div>
          <div className="form-group">
            <label>Estoque</label>
            <input
              type="number"
              name="estoque"
              min="0"
              step="1"
              value={formData.estoque}
              onKeyDown={(e) => {
                if (e.key === '-') e.preventDefault();
              }}
              onChange={onNumberChange}
              required
            />
          </div>
        </div>
      </div>

      <div className="form-section">
        <div className="form-section-head">
          <h3>Localização</h3>
          <p>Use o CEP para preencher rua, bairro, cidade e UF automaticamente.</p>
        </div>
        <div className="form-section-grid wide">
          <div className="form-group cep-group">
            <label>CEP</label>
            <div className="inline-field">
              <input
                name="cep"
                value={formData.endereco.cep}
                onChange={onEnderecoChange}
                onBlur={handleCepLookup}
              />
              <button type="button" className="ghost-btn small" onClick={handleCepLookup} disabled={cepLoading}>
                {cepLoading ? 'Buscando...' : 'Buscar'}
              </button>
            </div>
            {cepStatus && <small>{cepStatus}</small>}
          </div>
          <div className="form-group">
            <label>Rua</label>
            <input
              name="rua"
              value={formData.endereco.rua}
              onChange={onEnderecoChange}
            />
          </div>
          <div className="form-group">
            <label>Número</label>
            <input
              name="numero"
              value={formData.endereco.numero}
              onChange={onEnderecoChange}
            />
          </div>
          <div className="form-group">
            <label>Bairro</label>
            <input
              name="bairro"
              value={formData.endereco.bairro}
              onChange={onEnderecoChange}
            />
          </div>
          <div className="form-group">
            <label>Cidade</label>
            <input
              name="cidade"
              value={formData.endereco.cidade}
              onChange={onEnderecoChange}
            />
          </div>
          <div className="form-group">
            <label>Estado (UF)</label>
            <input
              name="estado"
              value={formData.endereco.estado}
              onChange={onEnderecoChange}
              maxLength="2"
            />
          </div>
          <div className="form-group span-2">
            <label>Complemento</label>
            <input
              name="complemento"
              value={formData.endereco.complemento}
              onChange={onEnderecoChange}
            />
          </div>
        </div>
      </div>

      <div className="form-section">
        <div className="form-section-head">
          <h3>Fotos</h3>
          <p>Adicione até 3 imagens da peça.</p>
        </div>
        <label
          className={`dropzone ${dragActive ? 'drag-active' : ''}`}
          onDragEnter={(event) => {
            event.preventDefault();
            setDragActive(true);
          }}
          onDragOver={(event) => event.preventDefault()}
          onDragLeave={() => setDragActive(false)}
          onDrop={handleDrop}
        >
          <input
            type="file"
            accept="image/*"
            multiple
            onChange={(event) => {
              onAddFiles(event.target.files);
              event.target.value = '';
            }}
          />
          <UploadCloud size={28} />
          <span>Arraste imagens aqui ou clique para selecionar</span>
          <small>{remainingImages} de 3 espaços disponíveis.</small>
        </label>

        {(existingImages.length > 0 || newImagePreviews.length > 0) && (
          <div className="image-preview-grid">
            {existingImages.map((url) => (
              <div key={url} className="image-preview-card">
                <img src={resolveImageUrl(url)} alt="Imagem da peça" />
                <button
                  type="button"
                  className="icon-btn"
                  onClick={() => onRemoveExistingImage(url)}
                  aria-label="Remover imagem"
                >
                  <X size={14} />
                </button>
              </div>
            ))}
            {newImagePreviews.map((preview) => (
              <div key={`${preview.file.name}-${preview.index}`} className="image-preview-card">
                <img src={preview.url} alt={preview.file.name} />
                <button
                  type="button"
                  className="icon-btn"
                  onClick={() => onRemoveNewFile(preview.index)}
                  aria-label="Remover imagem"
                >
                  <X size={14} />
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="form-actions">
        <button className="cta-btn" type="submit">
          {editingPeca ? 'Salvar alterações' : 'Cadastrar peça'}
        </button>
      </div>
    </form>
  );
};

const NegotiationPage = () => {
  const { user, selectedNegotiationId, setCurrentPage } = useApp();

  if (!user) {
    return (
      <div className="container empty-state">
        <h2>Faça login para acessar a negociação.</h2>
        <button className="cta-btn" onClick={() => setCurrentPage('login')}>
          Entrar
        </button>
      </div>
    );
  }

  if (!selectedNegotiationId) {
    return (
      <div className="container empty-state">
        <h2>Nenhuma negociação selecionada.</h2>
        <button className="cta-btn" onClick={() => setCurrentPage('dashboard')}>
          Voltar ao painel
        </button>
      </div>
    );
  }

  return (
    <div className="container negotiation-page">
      <button className="ghost-btn" onClick={() => setCurrentPage('dashboard')}>
        Voltar ao painel
      </button>
      <NegotiationChat conversaId={selectedNegotiationId} />
    </div>
  );
};

const NegotiationChat = ({ conversaId }) => {
  const { user, setCurrentPage } = useApp();
  const confirm = useConfirm();
  const toast = useToast();
  const [conversa, setConversa] = useState(null);
  const [conteudo, setConteudo] = useState('');
  const [valor, setValor] = useState('');
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState('');
  const messageListRef = useRef(null);
  const lastMessageIdRef = useRef(null);

  const loadConversa = useCallback(async () => {
    if (!conversaId || !user?.id) {
      setLoading(false);
      return;
    }
    try {
      const data = await fetchNegociacao(conversaId, user.id);
      setConversa(data);
      await marcarNegociacaoLida(conversaId, user.id);
    } catch (err) {
      setError(err.message || 'Erro ao carregar negociação');
    } finally {
      setLoading(false);
    }
  }, [conversaId, user?.id]);

  useEffect(() => {
    setLoading(true);
    loadConversa();
    const timer = setInterval(loadConversa, 3000);
    return () => clearInterval(timer);
  }, [loadConversa]);

  useEffect(() => {
    const mensagens = conversa?.mensagens || [];
    const last = mensagens[mensagens.length - 1];
    if (!last || last.id === lastMessageIdRef.current) return;
    lastMessageIdRef.current = last.id;
    if (messageListRef.current) {
      messageListRef.current.scrollTop = messageListRef.current.scrollHeight;
    }
  }, [conversa]);

  const send = async (event) => {
    event.preventDefault();
    if (!conteudo.trim() && !valor) return;
    setSending(true);
    setError('');
    try {
      const payload = {
        remetenteId: user.id,
        conteudo,
        valorProposto: valor ? Number(valor) : null
      };
      if (user.tipo === 'REVENDEDOR' && valor) {
        await enviarContrapropostaNegociacao(conversaId, payload);
      } else {
        await enviarMensagemNegociacao(conversaId, payload);
      }
      setConteudo('');
      setValor('');
      await loadConversa();
    } catch (err) {
      setError(err.message || 'Erro ao enviar mensagem');
    } finally {
      setSending(false);
    }
  };

  const action = async (type) => {
    if (type === 'cancelar') {
      const confirmed = await confirm(
        'Encerrar esta negociação? O chat vai sumir da sua lista de negociações.',
        { title: 'Encerrar negociação', confirmLabel: 'Encerrar', danger: true }
      );
      if (!confirmed) return;
    }
    setSending(true);
    setError('');
    try {
      if (type === 'aprovar') await aprovarNegociacao(conversaId, user.id);
      if (type === 'recusar') await recusarNegociacao(conversaId, user.id);
      if (type === 'cancelar') {
        await encerrarNegociacao(conversaId, user.id, 'CANCELADO');
        toast.info('Negociação encerrada.');
        setCurrentPage('dashboard');
        return;
      }
      await loadConversa();
    } catch (err) {
      setError(err.message || 'Erro ao atualizar negociação');
    } finally {
      setSending(false);
    }
  };

  if (loading) {
    return <div className="empty-state">Carregando negociação...</div>;
  }

  if (!conversa) {
    return <div className="empty-state">Negociação não encontrada.</div>;
  }

  const isCliente = user.tipo !== 'REVENDEDOR';
  const userApproved = isCliente ? Boolean(conversa.aprovacaoCliente) : Boolean(conversa.aprovacaoRevendedor);
  const otherApproved = isCliente ? Boolean(conversa.aprovacaoRevendedor) : Boolean(conversa.aprovacaoCliente);
  const otherName = isCliente ? (conversa.revendedorNome || 'o revendedor') : (conversa.clienteNome || 'o cliente');
  const bothApproved = userApproved && otherApproved;
  const isClosed = conversa.status === 'FECHADO' || conversa.status === 'CANCELADO';
  const locked = isClosed || bothApproved;

  const mensagens = conversa.mensagens || [];
  const lastMessage = mensagens[mensagens.length - 1];
  const waitingOnMe = !locked && lastMessage && lastMessage.remetenteId !== user.id && !userApproved;

  let banner = null;
  if (conversa.status === 'CANCELADO') {
    banner = { tone: 'inactive', text: 'Esta negociação foi cancelada.' };
  } else if (conversa.status === 'FECHADO') {
    banner = { tone: 'active', text: 'Negociação fechada.' };
  } else if (bothApproved) {
    banner = { tone: 'active', text: 'Termos aprovados pelos dois lados. Pagamento liberado no pedido.' };
  } else if (userApproved && !otherApproved) {
    banner = { tone: 'neutral', text: `Você aprovou os termos. Aguardando aprovação de ${otherName}.` };
  } else if (!userApproved && otherApproved) {
    banner = { tone: 'neutral', text: `${otherName} aprovou os termos. Revise e aprove para liberar o pagamento.` };
  } else if (waitingOnMe) {
    banner = { tone: 'neutral', text: 'É a sua vez de responder.' };
  } else if (lastMessage && lastMessage.remetenteId === user.id) {
    banner = { tone: 'neutral', text: `Aguardando resposta de ${otherName}.` };
  }

  return (
    <div className="negotiation-chat panel">
      <div className="negotiation-head">
        <div>
          <h1>{conversa.pecaNome}</h1>
          <div className="negotiation-price-row">
            {conversa.valorFinalAcordado ? (
              <span className="price-current">{formatPrice(conversa.valorFinalAcordado)}</span>
            ) : conversa.valorNegociado && conversa.valorNegociado !== conversa.valorOriginal ? (
              <>
                <span className="price-original">{formatPrice(conversa.valorOriginal)}</span>
                <span className="price-current">{formatPrice(conversa.valorNegociado)}</span>
              </>
            ) : (
              <span className="price-current">{formatPrice(conversa.valorOriginal)}</span>
            )}
          </div>
          <div className="approval-track">
            <div className={`approval-step ${conversa.aprovacaoCliente ? 'done' : ''}`}>
              {conversa.aprovacaoCliente ? <Check size={13} /> : <Clock3 size={13} />}
              Cliente {conversa.aprovacaoCliente ? 'aprovou' : 'pendente'}
            </div>
            <div className={`approval-connector ${bothApproved ? 'done' : ''}`} />
            <div className={`approval-step ${conversa.aprovacaoRevendedor ? 'done' : ''}`}>
              {conversa.aprovacaoRevendedor ? <Check size={13} /> : <Clock3 size={13} />}
              Revendedor {conversa.aprovacaoRevendedor ? 'aprovou' : 'pendente'}
            </div>
          </div>
        </div>
        <span className={`status-pill ${getStatusPillClass(conversa.status)}`}>
          {formatStatusLabel(conversa.status)}
        </span>
      </div>

      {banner && <div className={`negotiation-banner ${banner.tone}`}>{banner.text}</div>}

      {error && <div className="error-message">{error}</div>}

      <div className="message-list" ref={messageListRef}>
        {mensagens.map((mensagem) => {
          if (mensagem.tipo === 'SISTEMA') {
            return (
              <div key={mensagem.id} className="system-message">
                <span>{mensagem.conteudo}</span>
              </div>
            );
          }

          const mine = mensagem.remetenteId === user.id;
          const typeMeta = getMessageTypeMeta(mensagem.tipo);
          return (
            <div key={mensagem.id} className={`message-bubble ${mine ? 'mine' : ''}`}>
              <div className="message-meta">
                <strong>{mine ? 'Você' : mensagem.remetenteNome}</strong>
                <span>{formatDateTime(mensagem.dataEnvio)}</span>
              </div>
              {typeMeta && <span className={`message-tag ${typeMeta.className}`}>{typeMeta.label}</span>}
              {mensagem.conteudo && <p>{mensagem.conteudo}</p>}
              {mensagem.valorProposto !== null && mensagem.valorProposto !== undefined && (
                <span className="proposal-chip">{formatPrice(mensagem.valorProposto)}</span>
              )}
              {mine && (
                <small className="read-receipt">
                  {mensagem.lida ? <CheckCheck size={12} /> : <Check size={12} />}
                  {mensagem.lida ? 'Lida' : 'Enviada'}
                </small>
              )}
            </div>
          );
        })}
      </div>

      <div className="negotiation-actions">
        {userApproved ? (
          <span className="meta-chip approved-chip"><Check size={14} /> Você já aprovou</span>
        ) : (
          <button className="cta-btn small" onClick={() => action('aprovar')} disabled={locked || sending}>
            <Check size={15} /> Aprovar termos
          </button>
        )}
        <button className="ghost-btn small" onClick={() => action('recusar')} disabled={locked || sending}>
          <XCircle size={15} /> Recusar
        </button>
        <button className="ghost-btn small" onClick={() => action('cancelar')} disabled={locked || sending}>
          Encerrar
        </button>
      </div>

      <form className="chat-form" onSubmit={send}>
        <div className="chat-form-value">
          <span className="chat-form-currency">R$</span>
          <input
            type="number"
            min="0"
            step="0.01"
            value={valor}
            onChange={(e) => setValor(e.target.value)}
            placeholder={user.tipo === 'REVENDEDOR' ? 'Contraproposta' : 'Proposta'}
            disabled={locked}
          />
        </div>
        <textarea
          rows="2"
          value={conteudo}
          onChange={(e) => setConteudo(e.target.value)}
          placeholder="Escreva uma mensagem..."
          disabled={locked}
        />
        <button className="cta-btn send-btn" type="submit" disabled={locked || sending}>
          <Send size={16} />
          {sending ? 'Enviando...' : 'Enviar'}
        </button>
      </form>
    </div>
  );
};

const ClienteDashboard = () => {
  const { user, openProfile, openNegotiation } = useApp();
  const toast = useToast();
  const confirm = useConfirm();
  const [activeSection, setActiveSection] = useState('overview');
  const [pedidos, setPedidos] = useState([]);
  const [conversas, setConversas] = useState([]);
  const [loadingPedidos, setLoadingPedidos] = useState(false);
  const [loadingConversas, setLoadingConversas] = useState(false);
  const [payingId, setPayingId] = useState(null);

  const loadPedidosCliente = useCallback(() => {
    if (user?.id) {
      setLoadingPedidos(true);
      fetchPedidosByCliente(user.id)
        .then((data) => setPedidos(data))
        .catch((err) => console.error(err))
        .finally(() => setLoadingPedidos(false));
    } else {
      setPedidos([]);
    }
  }, [user?.id]);

  const loadConversasCliente = useCallback(() => {
    if (user?.id) {
      setLoadingConversas(true);
      fetchNegociacoesCliente(user.id)
        .then((data) => setConversas(data || []))
        .catch((err) => console.error(err))
        .finally(() => setLoadingConversas(false));
    } else {
      setConversas([]);
    }
  }, [user?.id]);

  useEffect(() => {
    loadPedidosCliente();
    loadConversasCliente();
  }, [loadPedidosCliente, loadConversasCliente]);

  const handleInformarPagamento = async (pedidoId) => {
    if (!pedidoId) return;
    const confirmed = await confirm('Marcar pagamento como efetuado?', {
      confirmLabel: 'Marcar como pago'
    });
    if (!confirmed) return;
    setPayingId(pedidoId);
    try {
      await informarPagamentoPedido(pedidoId, user.id);
      loadPedidosCliente();
      toast.success('Pagamento informado. Aguarde a confirmação do revendedor ou admin.');
    } catch (err) {
      toast.error(err.message || 'Erro ao informar pagamento.');
    } finally {
      setPayingId(null);
    }
  };

  const orderedPedidos = useMemo(
    () =>
      pedidos
        .slice()
        .sort((a, b) => {
          const priorityDiff = getClientOrderPriority(a) - getClientOrderPriority(b);
          if (priorityDiff !== 0) return priorityDiff;
          return new Date(b.dataCriacao || 0).getTime() - new Date(a.dataCriacao || 0).getTime();
        }),
    [pedidos]
  );
  const actionCount = orderedPedidos.filter(
    (pedido) => isClientPaymentAction(pedido) || isClientApprovalAction(pedido)
  ).length;
  const pedidosAbertos = pedidos.filter(
    (pedido) => !['CONCLUIDO', 'ENTREGUE', 'CANCELADO'].includes(pedido.status)
  ).length;
  const valorPedidos = pedidos.reduce(
    (sum, pedido) => sum + Number(getPedidoTotal(pedido) || 0),
    0
  );
  const conversasAtivas = conversas.filter((conversa) => conversa.status !== 'CANCELADO');
  const clientTabs = [
    { id: 'overview', label: 'Visão geral', icon: LayoutDashboard },
    { id: 'orders', label: 'Meus pedidos', icon: ClipboardList, count: pedidos.length },
    { id: 'negotiations', label: 'Negociações', icon: MessageSquare, count: conversasAtivas.length },
    { id: 'profile', label: 'Meu perfil', icon: User }
  ];

  const renderOrders = () => (
    <div className="panel dealer-section-panel client-orders-panel">
        <div className="panel-header section-panel-header">
          <div>
            <h2>Meus pedidos</h2>
            <p>
              {actionCount > 0
                ? `${actionCount} pedido${actionCount > 1 ? 's' : ''} precisa${actionCount > 1 ? 'm' : ''} da sua atenção`
                : 'Pedidos em acompanhamento'}
            </p>
          </div>
          <button className="ghost-btn" onClick={loadPedidosCliente} disabled={loadingPedidos}>
            {loadingPedidos ? 'Atualizando...' : 'Atualizar'}
          </button>
        </div>
        {loadingPedidos ? (
          <p>Carregando pedidos...</p>
        ) : pedidos.length === 0 ? (
          <div className="empty-state compact">
            <ClipboardList size={34} />
            <p>Nenhum pedido realizado ainda.</p>
          </div>
        ) : (
          <div className="order-list">
            {orderedPedidos.map((pedido) => {
              const canInformPayment = isClientPaymentAction(pedido);
              const needsApproval = isClientApprovalAction(pedido);
              const requiresAction = canInformPayment || needsApproval;
              const actionHint = canInformPayment
                ? 'Sua vez: informe o pagamento após concluir.'
                : needsApproval
                  ? 'Sua vez: revise a negociação no chat.'
                  : '';

              return (
                <OrderCard
                  key={pedido.id}
                  pedido={pedido}
                  partyName={pedido.revendedorNome || 'Revendedor'}
                  partyType="Revendedor"
                  onOpenProfile={
                    pedido.revendedorId
                      ? () => openProfile({ id: pedido.revendedorId, tipo: 'REVENDEDOR' })
                      : null
                  }
                  onOpenNegotiation={
                    pedido.conversaId ? () => openNegotiation(pedido.conversaId) : null
                  }
                  onCancelled={(pedidoId) =>
                    setPedidos((prev) =>
                      prev.map((item) =>
                        item.id === pedidoId ? { ...item, status: 'CANCELADO' } : item
                      )
                    )
                  }
                  action={
                    canInformPayment ? (
                      <button
                        className="cta-btn small"
                        onClick={() => handleInformarPagamento(pedido.id)}
                        disabled={payingId === pedido.id}
                      >
                        {payingId === pedido.id ? 'Informando...' : 'Pagamento efetuado'}
                      </button>
                    ) : null
                  }
                  requiresAction={requiresAction}
                  actionHint={actionHint}
                />
              );
            })}
          </div>
        )}
    </div>
  );

  const renderOverview = () => (
    <div className="dealer-section-stack">
      <div className="summary-grid dealer-summary-grid client-summary-grid">
        <div className="stat-card dashboard-stat">
          <span className="stat-label">Pedidos</span>
          <strong className="stat-number">{pedidos.length}</strong>
          <p>{pedidosAbertos} em andamento</p>
        </div>
        <div className="stat-card dashboard-stat">
          <span className="stat-label">Ação necessária</span>
          <strong className="stat-number">{actionCount}</strong>
          <p>{actionCount > 0 ? 'Prioridade no topo da lista' : 'Tudo em dia por aqui'}</p>
        </div>
        <div className="stat-card dashboard-stat">
          <span className="stat-label">Negociações</span>
          <strong className="stat-number">{loadingConversas ? '...' : conversasAtivas.length}</strong>
          <p>Conversas abertas com revendedores</p>
        </div>
        <div className="stat-card dashboard-stat">
          <span className="stat-label">Valor em pedidos</span>
          <strong className="stat-number">{formatPrice(valorPedidos)}</strong>
          <p>Total dos pedidos listados</p>
        </div>
      </div>

      <div className="panel quick-actions-panel">
        <div>
          <h2>Ações rápidas</h2>
          <p>Atalhos para acompanhar compras e resolver pendências.</p>
        </div>
        <div className="quick-actions">
          <button
            className={actionCount > 0 ? 'cta-btn' : 'ghost-btn'}
            onClick={() => setActiveSection('orders')}
          >
            <ClipboardList size={16} /> {actionCount > 0 ? 'Resolver pendências' : 'Ver pedidos'}
          </button>
          <button className="ghost-btn" onClick={() => setActiveSection('negotiations')}>
            <MessageSquare size={16} /> Abrir negociações
          </button>
          <button className="ghost-btn" onClick={() => setActiveSection('profile')}>
            <User size={16} /> Editar perfil
          </button>
        </div>
      </div>

      {actionCount > 0 ? renderOrders() : <NegotiationInbox role="CLIENTE" />}
    </div>
  );

  const renderActiveSection = () => {
    if (activeSection === 'orders') return renderOrders();
    if (activeSection === 'negotiations') return <NegotiationInbox role="CLIENTE" />;
    if (activeSection === 'profile') return <ProfileSettingsPanel />;
    return renderOverview();
  };

  return (
    <div className="dealer-dashboard client-dashboard">
      <div className="dealer-tabs" role="tablist" aria-label="Navegação do painel do cliente">
        {clientTabs.map((tab) => {
          const TabIcon = tab.icon;
          return (
            <button
              key={tab.id}
              type="button"
              className={`dealer-tab ${activeSection === tab.id ? 'active' : ''}`}
              onClick={() => setActiveSection(tab.id)}
              role="tab"
              aria-selected={activeSection === tab.id}
            >
              <TabIcon size={17} />
              <span>{tab.label}</span>
              {typeof tab.count === 'number' && <strong>{tab.count}</strong>}
            </button>
          );
        })}
      </div>

      <div className="dealer-content">
        {renderActiveSection()}
      </div>
    </div>
  );
};

const RevendedorDashboard = () => {
  const { user, openProfile, openNegotiation } = useApp();
  const toast = useToast();
  const confirm = useConfirm();
  const [activeSection, setActiveSection] = useState('overview');
  const [pecas, setPecas] = useState([]);
  const [pedidos, setPedidos] = useState([]);
  const [loadingPedidos, setLoadingPedidos] = useState(false);
  const [confirmingId, setConfirmingId] = useState(null);
  const [editingPeca, setEditingPeca] = useState(null);
  const [formData, setFormData] = useState({
    nome: '',
    descricao: '',
    categoria: '',
    preco: '',
    estado: 'USADO',
    ano: '',
    marca: '',
    modeloVeiculo: '',
    estoque: 1,
    endereco: {
      rua: '',
      numero: '',
      complemento: '',
      bairro: '',
      cidade: '',
      estado: '',
      cep: ''
    }
  });
  const [imageFiles, setImageFiles] = useState([]);
  const [existingImages, setExistingImages] = useState([]);

  const categories = [
    'Motor',
    'Suspensão',
    'Freios',
    'Elétrica',
    'Câmbio e Transmissão',
    'Arrefecimento',
    'Escapamento',
    'Rodas e Pneus',
    'Ar Condicionado',
    'Iluminação',
    'Interior e Acabamento',
    'Vidros e Retrovisores',
    'Direção',
    'Combustível e Injeção',
    'Carroceria e Lataria',
    'Acessórios'
  ];
  const states = ['NOVO', 'USADO', 'RECONDICIONADO', 'DEFEITUOSO'];
  const estadoLabels = {
    NOVO: 'Novo',
    USADO: 'Usado',
    RECONDICIONADO: 'Recondicionado',
    DEFEITUOSO: 'Defeituoso'
  };

  const loadPecas = useCallback(() => {
    if (user?.id) {
      fetchPecasByRevendedor(user.id)
        .then((data) => setPecas(data))
        .catch((err) => console.error(err));
    } else {
      setPecas([]);
    }
  }, [user?.id]);

  const loadPedidos = useCallback(async () => {
    if (!user?.id) {
      setPedidos([]);
      return;
    }
    setLoadingPedidos(true);
    try {
      const data = await fetchPedidosByRevendedor(user.id);
      setPedidos(data || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoadingPedidos(false);
    }
  }, [user?.id]);

  useEffect(() => {
    loadPecas();
    loadPedidos();
  }, [loadPecas, loadPedidos]);

  useEffect(() => {
    if (editingPeca) {
      setFormData({
        nome: editingPeca.nome || '',
        descricao: editingPeca.descricao || '',
        categoria: editingPeca.categoria || '',
        preco: editingPeca.preco || '',
        estado: editingPeca.estado || 'USADO',
        ano: editingPeca.ano || '',
        marca: editingPeca.marca || '',
        modeloVeiculo: editingPeca.modeloVeiculo || '',
        estoque: editingPeca.estoque || 1,
        endereco: {
          rua: editingPeca.endereco?.rua || '',
          numero: editingPeca.endereco?.numero || '',
          complemento: editingPeca.endereco?.complemento || '',
          bairro: editingPeca.endereco?.bairro || '',
          cidade: editingPeca.endereco?.cidade || '',
          estado: editingPeca.endereco?.estado || '',
          cep: editingPeca.endereco?.cep || ''
        }
      });
      setExistingImages(editingPeca.imagens || []);
    } else {
      setFormData({
        nome: '',
        descricao: '',
        categoria: '',
        preco: '',
        estado: 'USADO',
        ano: '',
        marca: '',
        modeloVeiculo: '',
        estoque: 1,
        endereco: {
          rua: '',
          numero: '',
          complemento: '',
          bairro: '',
          cidade: '',
          estado: '',
          cep: ''
        }
      });
      setExistingImages([]);
    }
    setImageFiles([]);
  }, [editingPeca]);

  const handleDelete = async (pecaId) => {
    const confirmed = await confirm('Deseja excluir esta peça?', { danger: true, confirmLabel: 'Excluir' });
    if (confirmed) {
      try {
        await deletePeca(pecaId);
        loadPecas();
      } catch (err) {
        console.error(err);
        toast.error('Erro ao excluir a peça.');
      }
    }
  };

  const handleAddImageFiles = (fileList) => {
    const files = Array.from(fileList || []).filter((file) => file.type?.startsWith('image/'));
    const allowed = 3 - existingImages.length - imageFiles.length;
    if (allowed <= 0) {
      toast.warning('Limite de 3 imagens por peça.');
      return;
    }
    if (files.length > allowed) {
      toast.warning('Limite de 3 imagens por peça.');
    }
    setImageFiles((prev) => [...prev, ...files.slice(0, Math.max(allowed, 0))]);
  };

  const handleRemoveExistingImage = async (url) => {
    if (!editingPeca) return;
    try {
      await removePecaImagem(editingPeca.id, url);
      setExistingImages((prev) => prev.filter((img) => img !== url));
    } catch (err) {
      console.error(err);
      toast.error('Erro ao remover a imagem.');
    }
  };

  const handleRemoveNewFile = (index) => {
    setImageFiles((prev) => prev.filter((_, i) => i !== index));
  };

  const handleEnderecoChange = (event) => {
    const { name, value } = event.target;
    setFormData((prev) => ({
      ...prev,
      endereco: { ...prev.endereco, [name]: value }
    }));
  };

  const handleNonNegativeNumberChange = (event) => {
    const { name, value } = event.target;
    if (value.includes('-')) return;
    const parsed = Number(value);
    if (value !== '' && (!Number.isFinite(parsed) || parsed < 0)) return;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      const preco = Number(formData.preco);
      const estoque = Number(formData.estoque);
      const ano = formData.ano ? Number(formData.ano) : null;

      if (preco < 0 || estoque < 0 || (ano !== null && ano < 0)) {
        toast.warning('Preço, estoque e ano não podem ser negativos.');
        return;
      }

      const payload = {
        ...formData,
        preco,
        estoque,
        ano
      };

      const saved = await savePeca(payload, user.id, editingPeca?.id);
      const pecaId = saved?.id || editingPeca?.id;

      if (pecaId && imageFiles.length > 0) {
        for (const file of imageFiles) {
          try {
            await uploadPecaImagem(pecaId, file);
          } catch (uploadErr) {
            console.error(uploadErr);
            toast.error(uploadErr.message || 'Erro ao enviar imagem.');
            break;
          }
        }
      }

      setEditingPeca(null);
      setImageFiles([]);
      loadPecas();
      setActiveSection('catalog');
      toast.success(editingPeca ? 'Peça atualizada!' : 'Peça cadastrada!');
    } catch (err) {
      console.error(err);
      toast.error(err.message || 'Erro de conexão.');
    }
  };

  const handleConfirmarPagamento = async (pedidoId) => {
    if (!pedidoId) return;
    const confirmed = await confirm('Confirmar pagamento deste pedido?', {
      confirmLabel: 'Confirmar pagamento'
    });
    if (!confirmed) return;
    setConfirmingId(pedidoId);
    try {
      await confirmarPagamentoPedido(pedidoId);
      await loadPedidos();
      toast.success('Pagamento confirmado.');
    } catch (err) {
      console.error(err);
      toast.error(err.message || 'Erro ao confirmar pagamento.');
    } finally {
      setConfirmingId(null);
    }
  };

  const startNewPeca = () => {
    setEditingPeca(null);
    setActiveSection('new');
  };

  const startEditPeca = (peca) => {
    setEditingPeca(peca);
    setActiveSection('new');
  };

  const totalEstoque = pecas.reduce((sum, peca) => sum + Number(peca.estoque || 0), 0);
  const pedidosAbertos = pedidos.filter(
    (pedido) => !['CONCLUIDO', 'ENTREGUE', 'CANCELADO'].includes(pedido.status)
  ).length;
  const valorPedidos = pedidos.reduce(
    (sum, pedido) => sum + Number(pedido.valorFinalNegociado || pedido.valorTotal || 0),
    0
  );

  const dealerTabs = [
    { id: 'overview', label: 'Visão geral', icon: LayoutDashboard },
    { id: 'catalog', label: 'Minhas peças', icon: Package, count: pecas.length },
    { id: 'orders', label: 'Pedidos recebidos', icon: ClipboardList, count: pedidos.length },
    { id: 'negotiations', label: 'Negociações', icon: MessageSquare },
    { id: 'new', label: editingPeca ? 'Editar peça' : 'Cadastrar nova peça', icon: ImagePlus }
  ];

  const renderOverview = () => (
    <div className="dealer-section-stack">
      <div className="summary-grid dealer-summary-grid">
        <div className="stat-card dashboard-stat">
          <span className="stat-label">Peças cadastradas</span>
          <strong className="stat-number">{pecas.length}</strong>
          <p>{totalEstoque} unidades em estoque</p>
        </div>
        <div className="stat-card dashboard-stat">
          <span className="stat-label">Pedidos recebidos</span>
          <strong className="stat-number">{pedidos.length}</strong>
          <p>{pedidosAbertos} em andamento</p>
        </div>
        <div className="stat-card dashboard-stat">
          <span className="stat-label">Valor em pedidos</span>
          <strong className="stat-number">{formatPrice(valorPedidos)}</strong>
          <p>Total bruto dos pedidos listados</p>
        </div>
      </div>

      <div className="panel quick-actions-panel">
        <div>
          <h2>Ações rápidas</h2>
          <p>Atalhos para as rotinas mais usadas do painel.</p>
        </div>
        <div className="quick-actions">
          <button className="cta-btn" onClick={startNewPeca}>
            <Plus size={16} /> Cadastrar peça
          </button>
          <button className="ghost-btn" onClick={() => setActiveSection('orders')}>
            <ClipboardList size={16} /> Ver pedidos
          </button>
          <button className="ghost-btn" onClick={() => setActiveSection('negotiations')}>
            <MessageSquare size={16} /> Abrir negociações
          </button>
        </div>
      </div>

      <ProfileSettingsPanel />
    </div>
  );

  const renderCatalog = () => (
    <div className="panel dealer-section-panel">
      <div className="panel-header section-panel-header">
        <div>
          <h2>Minhas peças</h2>
          <p>Gerencie catálogo, estoque e preços publicados.</p>
        </div>
        <button className="ghost-btn" onClick={startNewPeca}>
          <Plus size={16} /> Nova peça
        </button>
      </div>
      {pecas.length === 0 ? (
        <div className="empty-state compact">
          <Package size={34} />
          <p>Nenhuma peça cadastrada.</p>
          <button className="cta-btn small" onClick={startNewPeca}>Cadastrar primeira peça</button>
        </div>
      ) : (
        <div className="table catalog-table">
          {pecas.map((peca) => (
            <div key={peca.id} className="table-row catalog-row">
              <div>
                <strong>{peca.nome}</strong>
                <p>{peca.categoria || 'Sem categoria'} · {peca.modeloVeiculo || 'Modelo não informado'}</p>
              </div>
              <div className="table-actions">
                <span className="catalog-price">{formatPrice(peca.preco)}</span>
                <span className="meta-chip">Estoque: {peca.estoque ?? 0}</span>
                <button className="icon-btn" onClick={() => startEditPeca(peca)} aria-label="Editar peça">
                  <Edit2 size={16} />
                </button>
                <button className="icon-btn" onClick={() => handleDelete(peca.id)} aria-label="Excluir peça">
                  <Trash2 size={16} />
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );

  const pedidosVisiveis = pedidos;

  const renderOrders = () => (
    <div className="panel dealer-section-panel">
      <div className="panel-header section-panel-header">
        <div>
          <h2>Pedidos recebidos</h2>
          <p>Acompanhe aprovação, pagamento e conversa de cada pedido.</p>
        </div>
        <button className="ghost-btn" onClick={loadPedidos} disabled={loadingPedidos}>
          {loadingPedidos ? 'Atualizando...' : 'Atualizar'}
        </button>
      </div>
      {loadingPedidos ? (
        <p>Carregando pedidos...</p>
      ) : pedidosVisiveis.length === 0 ? (
        <div className="empty-state compact">
          <ClipboardList size={34} />
          <p>Nenhum pedido recebido.</p>
        </div>
      ) : (
        <div className="order-list dealer-order-list">
          {pedidosVisiveis.map((pedido) => {
            const canConfirmPayment = pedido.status === 'PAGAMENTO_INFORMADO_CLIENTE';
            const note = canConfirmPayment
              ? ''
              : pedido.status === 'PAGAMENTO_LIBERADO'
                ? 'Aguardando cliente informar pagamento'
                : ['AGUARDANDO_NEGOCIACAO', 'PENDENTE'].includes(pedido.status)
                  ? 'Aguardando negociação'
                  : formatStatusLabel(pedido.status);

            return (
              <OrderCard
                key={pedido.id}
                pedido={pedido}
                partyName={pedido.clienteNome || 'Cliente'}
                partyType="Cliente"
                onOpenProfile={
                  pedido.clienteId ? () => openProfile({ id: pedido.clienteId, tipo: 'CLIENTE' }) : null
                }
                onOpenNegotiation={
                  pedido.conversaId ? () => openNegotiation(pedido.conversaId) : null
                }
                onCancelled={(pedidoId) =>
                  setPedidos((prev) =>
                    prev.map((item) =>
                      item.id === pedidoId ? { ...item, status: 'CANCELADO' } : item
                    )
                  )
                }
                action={
                  canConfirmPayment ? (
                    <button
                      className="cta-btn small"
                      onClick={() => handleConfirmarPagamento(pedido.id)}
                      disabled={confirmingId === pedido.id}
                    >
                      {confirmingId === pedido.id ? 'Confirmando...' : 'Confirmar pagamento'}
                    </button>
                  ) : null
                }
                note={!canConfirmPayment ? note : ''}
              />
            );
          })}
        </div>
      )}
    </div>
  );

  const renderForm = () => (
    <div className="panel dealer-section-panel">
      <div className="panel-header section-panel-header">
        <div>
          <h2>{editingPeca ? 'Editar peça' : 'Cadastrar nova peça'}</h2>
          <p>Organize as informações em etapas para publicar com menos retrabalho.</p>
        </div>
      </div>
      <PecaForm
        editingPeca={editingPeca}
        formData={formData}
        setFormData={setFormData}
        categories={categories}
        states={states}
        estadoLabels={estadoLabels}
        imageFiles={imageFiles}
        existingImages={existingImages}
        onEnderecoChange={handleEnderecoChange}
        onNumberChange={handleNonNegativeNumberChange}
        onAddFiles={handleAddImageFiles}
        onRemoveExistingImage={handleRemoveExistingImage}
        onRemoveNewFile={handleRemoveNewFile}
        onSubmit={handleSubmit}
      />
    </div>
  );

  const renderActiveSection = () => {
    if (activeSection === 'catalog') return renderCatalog();
    if (activeSection === 'orders') return renderOrders();
    if (activeSection === 'negotiations') return <NegotiationInbox role="REVENDEDOR" />;
    if (activeSection === 'new') return renderForm();
    return renderOverview();
  };

  return (
    <div className="dealer-dashboard">
      <div className="dealer-tabs" role="tablist" aria-label="Navegação do painel do revendedor">
        {dealerTabs.map((tab) => {
          const TabIcon = tab.icon;
          return (
            <button
              key={tab.id}
              type="button"
              className={`dealer-tab ${activeSection === tab.id ? 'active' : ''}`}
              onClick={() => setActiveSection(tab.id)}
              role="tab"
              aria-selected={activeSection === tab.id}
            >
              <TabIcon size={17} />
              <span>{tab.label}</span>
              {typeof tab.count === 'number' && <strong>{tab.count}</strong>}
            </button>
          );
        })}
      </div>

      <div className="dealer-content">
        {renderActiveSection()}
      </div>
    </div>
  );
};
const AdminPage = () => {
  const { user, authToken, setCurrentPage, logout } = useApp();
  const toast = useToast();
  const confirm = useConfirm();
  const [dashboard, setDashboard] = useState(null);
  const [usuarios, setUsuarios] = useState([]);
  const [revendedores, setRevendedores] = useState([]);
  const [alertas, setAlertas] = useState([]);
  const [alertStats, setAlertStats] = useState(null);
  const [alertFilters, setAlertFilters] = useState({ usuarioId: '', data: '', tipo: '', status: '' });
  const alertFiltersRef = useRef(alertFilters);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    alertFiltersRef.current = alertFilters;
  }, [alertFilters]);

  const loadData = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const [dash, users, sellers, moderationStats, moderationAlerts] = await Promise.all([
        fetchAdminDashboard(),
        fetchAdminUsuarios(),
        fetchAdminRevendedores(),
        fetchModeracaoStats(),
        fetchModeracaoAlertas(alertFiltersRef.current)
      ]);
      setDashboard(dash);
      setUsuarios(users || []);
      setRevendedores(sellers || []);
      setAlertStats(moderationStats);
      setAlertas(moderationAlerts || []);
    } catch (err) {
      setError(err.message || 'Erro ao carregar dados');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (user?.tipo === "ADMINISTRADOR" && authToken) {
      loadData();
    }
  }, [user?.tipo, authToken, loadData]);

  if (!user) {
    return (
      <div className="container empty-state">
        <h2>Faca login para acessar o painel administrativo</h2>
        <button className="cta-btn" onClick={() => setCurrentPage('login')}>
          Entrar
        </button>
      </div>
    );
  }

  if (user.tipo !== 'ADMINISTRADOR') {
    return (
      <div className="container empty-state">
        <h2>Acesso restrito para administradores.</h2>
      </div>
    );
  }

  const clientes = usuarios.filter((item) => item.tipo === 'CLIENTE');
  const administradores = usuarios.filter((item) => item.tipo === 'ADMINISTRADOR');

  const totalUsuarios = dashboard?.usuarios?.total ?? usuarios.length;
  const totalPecas = dashboard?.pecas?.total ?? 0;
  const totalVendas = dashboard?.vendas?.total ?? 0;
  const divergenciasPagamento = dashboard?.vendas?.divergenciasPagamentoDetalhes || [];

  const handleRemoveUsuario = async (usuario) => {
    if (!usuario?.id) return;
    const confirmed = await confirm(`Remover o usuario ${usuario.nome}?`, { danger: true, confirmLabel: 'Remover' });
    if (!confirmed) return;
    try {
      await deleteAdminUsuario(usuario.id);
      loadData();
    } catch (err) {
      toast.error(err.message || 'Erro ao remover usuário.');
    }
  };

  const handleRemoveRevendedor = async (revendedor) => {
    if (!revendedor?.id) return;
    const confirmed = await confirm(`Remover o revendedor ${revendedor.nome}?`, { danger: true, confirmLabel: 'Remover' });
    if (!confirmed) return;
    try {
      await deleteAdminRevendedor(revendedor.id);
      loadData();
    } catch (err) {
      toast.error(err.message || 'Erro ao remover revendedor.');
    }
  };

  const handleBaixarTaxas = async (revendedor, zerar = false) => {
    if (!revendedor?.id) return;
    let valor = null;
    if (!zerar) {
      const entrada = window.prompt(
        'Valor pago pelo revendedor (ex: 50.00). Deixe vazio para zerar.',
        ''
      );
      if (entrada === null) return;
      const texto = entrada.trim();
      if (texto.length > 0) {
        const parsed = Number(texto.replace(',', '.'));
        if (!Number.isFinite(parsed) || parsed <= 0) {
          toast.warning('Valor inválido.');
          return;
        }
        valor = parsed;
      }
    } else if (!(await confirm(`Zerar todas as taxas de ${revendedor.nome}?`, { confirmLabel: 'Zerar taxas' }))) {
      return;
    }

    try {
      await baixarTaxasRevendedor(revendedor.id, valor);
      loadData();
    } catch (err) {
      toast.error(err.message || 'Erro ao baixar taxas.');
    }
  };

  const handleAtivarPremium = async (revendedor) => {
    if (!revendedor?.id) return;
    const entrada = window.prompt('Quantos dias de premium?', '30');
    if (entrada === null) return;
    const dias = Number(entrada);
    if (!Number.isFinite(dias) || dias <= 0) {
      toast.warning('Dias inválidos.');
      return;
    }
    try {
      await ativarPremiumRevendedor(revendedor.id, Math.round(dias));
      loadData();
    } catch (err) {
      toast.error(err.message || 'Erro ao ativar premium.');
    }
  };

  const handleDesativarPremium = async (revendedor) => {
    if (!revendedor?.id) return;
    const confirmed = await confirm(`Desativar premium de ${revendedor.nome}?`, { confirmLabel: 'Desativar' });
    if (!confirmed) return;
    try {
      await desativarPremiumRevendedor(revendedor.id);
      loadData();
    } catch (err) {
      toast.error(err.message || 'Erro ao desativar premium.');
    }
  };

  const loadAlertas = async () => {
    try {
      const [stats, data] = await Promise.all([
        fetchModeracaoStats(),
        fetchModeracaoAlertas(alertFiltersRef.current)
      ]);
      setAlertStats(stats);
      setAlertas(data || []);
    } catch (err) {
      toast.error(err.message || 'Erro ao carregar alertas.');
    }
  };

  const handleAlertStatus = async (alerta, status) => {
    try {
      await atualizarModeracaoStatus(alerta.id, status);
      await loadAlertas();
    } catch (err) {
      toast.error(err.message || 'Erro ao atualizar alerta.');
    }
  };

  const handleRemoveMensagem = async (alerta) => {
    if (!alerta.mensagemId) return;
    const confirmed = await confirm('Remover a mensagem sinalizada?', { danger: true, confirmLabel: 'Remover' });
    if (!confirmed) return;
    try {
      await removerMensagemModeracao(alerta.mensagemId);
      await handleAlertStatus(alerta, 'RESOLVIDO');
    } catch (err) {
      toast.error(err.message || 'Erro ao remover mensagem.');
    }
  };

  const handleRemoveImagemDenunciada = async (alerta) => {
    if (!alerta.pecaId || !alerta.imagemUrl) return;
    const confirmed = await confirm('Remover a imagem denunciada da peça?', { danger: true, confirmLabel: 'Remover' });
    if (!confirmed) return;
    try {
      await removePecaImagem(alerta.pecaId, alerta.imagemUrl);
      await handleAlertStatus(alerta, 'RESOLVIDO');
    } catch (err) {
      toast.error(err.message || 'Erro ao remover imagem.');
    }
  };

  const handleSuspendUser = async (alerta) => {
    const confirmed = await confirm(`Suspender ${alerta.usuarioNome}?`, { danger: true, confirmLabel: 'Suspender' });
    if (!confirmed) return;
    try {
      await suspenderUsuarioModeracao(alerta.usuarioId);
      await handleAlertStatus(alerta, 'RESOLVIDO');
      await loadData();
    } catch (err) {
      toast.error(err.message || 'Erro ao suspender usuário.');
    }
  };

  const highlightTerm = (message, term) => {
    if (!message || !term) return message || '';
    const lowerMessage = message.toLowerCase();
    const lowerTerm = term.toLowerCase();
    const index = lowerMessage.indexOf(lowerTerm);
    if (index === -1) return message;
    return (
      <>
        {message.slice(0, index)}
        <mark className="flagged-word">{message.slice(index, index + term.length)}</mark>
        {message.slice(index + term.length)}
      </>
    );
  };

  return (
    <div className="container dashboard-page admin-page">
      <div className="dashboard-header">
        <div>
          <h1>Painel administrativo</h1>
          <p>Gerencie usuarios e revendedores da plataforma.</p>
        </div>
        <div className="table-actions">
          <button className="ghost-btn" onClick={loadData} disabled={loading}>
            {loading ? 'Atualizando...' : 'Atualizar'}
          </button>
          <button className="ghost-btn" onClick={logout}>
            <LogOut size={16} /> Sair
          </button>
        </div>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="dashboard-grid admin-stats">
        <div className="panel stat-card">
          <h3>Usuarios</h3>
          <div className="stat-value">{totalUsuarios}</div>
          <p>
            Clientes: {clientes.length} | Revendedores: {revendedores.length}
          </p>
        </div>
        <div className="panel stat-card">
          <h3>Revendedores</h3>
          <div className="stat-value">{revendedores.length}</div>
          <p>Administradores: {administradores.length}</p>
        </div>
        <div className="panel stat-card">
          <h3>Peças</h3>
          <div className="stat-value">{totalPecas}</div>
          <p>Vendas: {totalVendas}</p>
        </div>
      </div>

      <div className="panel stat-card">
        <h3>Divergencias</h3>
        <div className="stat-value">{dashboard?.vendas?.divergenciasPagamento ?? 0}</div>
        <p>Pagamento informado sem confirmacao ha mais de 24h.</p>
      </div>

      {divergenciasPagamento.length > 0 && (
        <div className="panel">
          <div className="panel-header">
            <h2>Divergencias de pagamento</h2>
          </div>
          <div className="table">
            {divergenciasPagamento.map((item) => (
              <div key={item.pedidoId} className="table-row">
                <div>
                  <strong>Pedido #{item.pedidoId?.substring(0, 8)}</strong>
                  <p>{item.clienteNome} / {item.revendedorNome}</p>
                  <span className="meta-note">Informado em {formatDateTime(item.informadoEm)}</span>
                </div>
                <strong>{formatPrice(item.valor)}</strong>
              </div>
            ))}
          </div>
        </div>
      )}

      <div className="panel moderation-panel">
        <div className="panel-header">
          <div>
            <h2>Alertas de moderacao</h2>
            <p>Monitoramento automático do chat de negociação.</p>
          </div>
          <button className="ghost-btn" onClick={loadAlertas}>
            Filtrar
          </button>
        </div>
        <div className="dashboard-grid admin-stats">
          <div className="stat-card">
            <h3>Pendentes</h3>
            <div className="stat-value">{alertStats?.pendentes ?? 0}</div>
          </div>
          <div className="stat-card">
            <h3>Total</h3>
            <div className="stat-value">{alertStats?.total ?? 0}</div>
          </div>
        </div>
        <div className="moderation-filters">
          <input
            value={alertFilters.usuarioId}
            onChange={(e) => setAlertFilters((prev) => ({ ...prev, usuarioId: e.target.value }))}
            placeholder="ID do usuario"
          />
          <input
            type="date"
            value={alertFilters.data}
            onChange={(e) => setAlertFilters((prev) => ({ ...prev, data: e.target.value }))}
          />
          <input
            value={alertFilters.tipo}
            onChange={(e) => setAlertFilters((prev) => ({ ...prev, tipo: e.target.value }))}
            placeholder="Tipo de infração"
          />
          <select
            value={alertFilters.status}
            onChange={(e) => setAlertFilters((prev) => ({ ...prev, status: e.target.value }))}
          >
            <option value="">Todos</option>
            <option value="PENDENTE">Pendente</option>
            <option value="ANALISADO">Analisado</option>
            <option value="RESOLVIDO">Resolvido</option>
          </select>
        </div>
        {alertas.length === 0 ? (
          <p>Nenhum alerta encontrado.</p>
        ) : (
          <div className="moderation-list">
            {alertas.map((alerta) => (
              <div key={alerta.id} className="moderation-card">
                <div className="moderation-card-head">
                  <strong>{alerta.usuarioNome}</strong>
                  <span className="meta-chip">{alerta.usuarioTipo}</span>
                  <span className={`risk-pill risk-${String(alerta.nivelRisco).toLowerCase()}`}>
                    {alerta.nivelRisco}
                  </span>
                  <span className={`status-pill ${getStatusPillClass(alerta.status)}`}>
                    {formatStatusLabel(alerta.status)}
                  </span>
                  {alerta.denunciaManual && (
                    <span className="meta-chip"><Flag size={12} /> Denúncia manual</span>
                  )}
                </div>
                <p>{highlightTerm(alerta.mensagemEnviada, alerta.palavraDetectada)}</p>
                {alerta.imagemUrl && (
                  <div className="moderation-image-report">
                    <img src={resolveImageUrl(alerta.imagemUrl)} alt="Imagem denunciada" />
                    <div>
                      <strong>{alerta.pecaNome || 'Peca denunciada'}</strong>
                      <span>{alerta.imagemUrl}</span>
                    </div>
                  </div>
                )}
                <div className="meta-line">
                  <span className="meta-chip">{alerta.tipoInfracao}</span>
                  <span className="meta-chip">Detectado: {alerta.palavraDetectada}</span>
                  <span className="meta-chip">Infração: {alerta.contadorInfracoesUsuario || 0}</span> 
                  <span className="meta-note">{formatDateTime(alerta.dataHora)}</span>
                </div>
                <div className="table-actions admin-actions">
                  <button className="ghost-btn small" onClick={() => handleAlertStatus(alerta, 'ANALISADO')}>
                    Analisado
                  </button>
                  <button className="ghost-btn small" onClick={() => handleAlertStatus(alerta, 'RESOLVIDO')}>
                    Ignorar falso positivo
                  </button>
                  {alerta.imagemUrl ? (
                    <button className="ghost-btn small" onClick={() => handleRemoveImagemDenunciada(alerta)}>
                      Remover imagem
                    </button>
                  ) : alerta.mensagemId ? (
                    <button className="ghost-btn small" onClick={() => handleRemoveMensagem(alerta)}>
                      Remover mensagem
                    </button>
                  ) : null}
                  <button className="cta-btn small" onClick={() => handleSuspendUser(alerta)}>
                    Suspender usuario
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="dashboard-grid admin-lists">
        <div className="panel">
          <div className="panel-header">
            <h2>Usuarios (Clientes)</h2>
          </div>
          {loading ? (
            <p>Carregando usuarios...</p>
          ) : clientes.length === 0 ? (
            <p>Nenhum cliente cadastrado.</p>
          ) : (
            <div className="table">
              {clientes.map((cliente) => (
                <div key={cliente.id} className="table-row">
                  <div>
                    <strong>{cliente.nome}</strong>
                    <p>{cliente.email}</p>
                    <span className={`status-pill ${cliente.ativo ? 'active' : 'inactive'}`}>
                      {cliente.ativo ? 'Ativo' : 'Inativo'}
                    </span>
                  </div>
                  <div className="table-actions">
                    <button
                      className="icon-btn"
                      onClick={() => handleRemoveUsuario(cliente)}
                      disabled={!cliente.ativo}
                      title="Remover"
                    >
                      <Trash2 size={16} />
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="panel">
          <div className="panel-header">
            <h2>Revendedores</h2>
          </div>
          {loading ? (
            <p>Carregando revendedores...</p>
          ) : revendedores.length === 0 ? (
            <p>Nenhum revendedor cadastrado.</p>
          ) : (
            <div className="table">
              {revendedores.map((revendedor) => {
                const saldoTaxas = toNumberOrNull(revendedor?.saldoTaxas) ?? 0;
                const premiumAte = revendedor?.premiumAte ? new Date(revendedor.premiumAte) : null;
                const premiumAtivo =
                  Boolean(revendedor?.premiumAtivo) &&
                  (!premiumAte || premiumAte.getTime() >= Date.now());
                const premiumLabel = premiumAtivo
                  ? `Premium ativo${premiumAte ? ` ate ${formatDate(premiumAte)}` : ''}`
                  : 'Premium inativo';

                return (
                  <div key={revendedor.id} className="table-row">
                    <div>
                      <strong>{revendedor.nome}</strong>
                      <p>{revendedor.email}</p>
                      <div className="meta-line">
                        <span
                          className={`status-pill ${revendedor.ativo ? 'active' : 'inactive'}`}
                        >
                          {revendedor.ativo ? 'Ativo' : 'Inativo'}
                        </span>
                        <span className={`status-pill ${premiumAtivo ? 'active' : 'inactive'}`}>
                          {premiumLabel}
                        </span>
                        <span className="meta-chip">
                          Taxas: {formatPrice(saldoTaxas)}
                        </span>
                      </div>
                    </div>
                    <div className="table-actions admin-actions">
                      <button
                        className="ghost-btn small"
                        onClick={() => handleBaixarTaxas(revendedor)}
                        title="Dar baixa parcial"
                      >
                        Baixar taxa
                      </button>
                      <button
                        className="ghost-btn small"
                        onClick={() => handleBaixarTaxas(revendedor, true)}
                        title="Zerar taxas"
                      >
                        Zerar taxa
                      </button>
                      <button
                        className="cta-btn small"
                        onClick={() => handleAtivarPremium(revendedor)}
                        title="Ativar premium"
                      >
                        Premium
                      </button>
                      <button
                        className="ghost-btn small"
                        onClick={() => handleDesativarPremium(revendedor)}
                        title="Desativar premium"
                      >
                        Cancelar premium
                      </button>
                      <button
                        className="icon-btn"
                        onClick={() => handleRemoveRevendedor(revendedor)}
                        disabled={!revendedor.ativo}
                        title="Remover"
                      >
                        <Trash2 size={16} />
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

const ProductCard = ({ product, onView, onAdd }) => {
  const imageCount = product.imagens?.length || 0;
  const estoqueValue = toNumberOrNull(product?.estoque);
  const outOfStock = estoqueValue !== null && estoqueValue <= 0;
  return (
  <div className="product-card">
    <div className="product-media" onClick={() => onView(product)}>
      {product.imagens?.[0] ? (
        <>
          <img src={resolveImageUrl(product.imagens[0])} alt={product.nome} />
          {imageCount > 1 && <span className="image-count">+{imageCount - 1}</span>}
        </>
      ) : (
        <div className="image-placeholder">
          <Package size={28} />
        </div>
      )}
    </div>
    <div className="product-body">
      <h3>{product.nome}</h3>
      <p>{product.marca || 'Marca não informada'}</p>
      {product.endereco?.cidade && (
        <p>{product.endereco.cidade}{product.endereco.estado ? ` - ${product.endereco.estado}` : ''}</p>
      )}
      <span className="product-price">{formatPrice(product.preco)}</span>
      <div className="product-actions">
        <button className="ghost-btn" onClick={() => onView(product)}>
          Ver detalhes
        </button>
        <button
          className="cta-btn small"
          onClick={() => onAdd(product)}
          disabled={outOfStock}
        >
          {outOfStock ? 'Sem estoque' : 'Adicionar'}
        </button>
      </div>
    </div>
  </div>
  );
};

const Footer = () => (
  <footer className="footer">
    <div className="container footer-content">
      <div>
        <div className="logo footer-logo">
          <img src={metalScLogo} alt="Metal-SC" className="logo-icon" />
          <span>Metal-SC</span>
        </div>
        <p>Sua fonte de peças automotivas usadas em Santa Catarina.</p>
      </div>
      <div>
        <h4>Contato</h4>
        <div className="footer-list">
          <span><MapPin size={14} /> Florianopolis, SC</span>
          <span><Phone size={14} /> (48) 99999-9999</span>
          <span><Mail size={14} /> contato@metalsc.com.br</span>
        </div>
      </div>
    </div>
  </footer>
);

export default App;
