
import React, { createContext, useContext, useEffect, useMemo, useState } from 'react';
import {
  Edit2,
  Filter,
  LogOut,
  Mail,
  MapPin,
  Menu,
  Package,
  Phone,
  Plus,
  Search,
  ShoppingCart,
  Trash2,
  User,
  X
} from 'lucide-react';
import './App.css';
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
  confirmarPagamentoPedido,
  baixarTaxasRevendedor,
  ativarPremiumRevendedor,
  desativarPremiumRevendedor,
  loginUsuario,
  registerUsuario,
  savePeca,
  uploadPecaImagem,
  removePecaImagem
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

const WHATSAPP_RELEASE_URL =
  process.env.REACT_APP_WHATSAPP_RELEASE_URL ||
  "https://media.tenor.com/zGQLL-kwwEoAAAAM/cat-meme-pee.gif";

const buildWhatsAppLink = (phone, message) => {
  if (!phone) return '';
  const digits = String(phone).replace(/\D/g, '');
  if (!digits) return '';
  let number = digits;
  if (!digits.startsWith('55') && (digits.length === 10 || digits.length === 11)) {
    number = `55${digits}`;
  }
  const text = message ? `?text=${encodeURIComponent(message)}` : '';
  return `https://wa.me/${number}${text}`;
};

const getWhatsAppReleaseLink = (phone, message) => {
  if (WHATSAPP_RELEASE_URL) {
    return WHATSAPP_RELEASE_URL;
  }
  return buildWhatsAppLink(phone, message);
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
  const [user, setUser] = useState(null);
  const [authToken, setAuthToken] = useState(null);
  const [cart, setCart] = useState([]);
  const [currentPage, setCurrentPage] = useState('home');
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  useEffect(() => {
    const savedUser = localStorage.getItem('metal_user');
    const savedCart = localStorage.getItem('metal_cart');
    const savedAuth = localStorage.getItem('metal_auth');
    if (savedUser) setUser(JSON.parse(savedUser));
    if (savedCart) {
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
    }
    if (savedAuth) setAuthToken(savedAuth);
  }, []);

  useEffect(() => {
    if (user) {
      localStorage.setItem('metal_user', JSON.stringify(user));
    } else {
      localStorage.removeItem('metal_user');
    }
  }, [user]);

  useEffect(() => {
    if (authToken) {
      localStorage.setItem('metal_auth', authToken);
    } else {
      localStorage.removeItem('metal_auth');
    }
  }, [authToken]);

  useEffect(() => {
    localStorage.setItem('metal_cart', JSON.stringify(cart));
  }, [cart]);

  const login = (userData, token) => {
    setUser(userData);
    if (token) {
      setAuthToken(token);
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

  const openProduct = (product) => {
    setSelectedProduct(product);
    setCurrentPage('product-detail');
  };

  const addToCart = (product) => {
    setCart((prev) => {
      const existing = prev.find((item) => item.id === product.id);
      const rawMaxEstoque =
        toNumberOrNull(product?.estoque) ?? toNumberOrNull(existing?.estoque);
      const maxEstoque = rawMaxEstoque ?? 1;
      if (maxEstoque <= 0) {
        alert('Sem estoque disponivel para esta peca.');
        return prev;
      }
      if (existing) {
        if (maxEstoque !== null && existing.quantidade >= maxEstoque) {
          alert('Quantidade maxima em estoque atingida.');
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
    selectedProduct,
    setSelectedProduct,
    mobileMenuOpen,
    setMobileMenuOpen,
    login,
    logout,
    openProduct,
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
        {currentPage === 'admin' && <AdminPage />}
      </main>
      <Footer />
    </div>
  );
};

const App = () => (
  <AppProvider>
    <AppShell />
  </AppProvider>
);

const Header = () => {
  const { user, cart, setCurrentPage, mobileMenuOpen, setMobileMenuOpen } = useApp();
  const cartCount = cart.reduce((sum, item) => sum + item.quantidade, 0);
  const handleAnnounceClick = () => {
    if (user?.tipo === 'REVENDEDOR') {
      setCurrentPage('dashboard');
    } else {
      setCurrentPage('register');
    }
    setMobileMenuOpen(false);
  };
  const handleUserClick = () => {
    if (user?.tipo === 'ADMINISTRADOR') {
      setCurrentPage('admin');
    } else {
      setCurrentPage('dashboard');
    }
    setMobileMenuOpen(false);
  };

  return (
    <header className="header">
      <div className="container header-content">
        <div className="logo" onClick={() => setCurrentPage('home')}>
          <img src={metalScLogo} alt="Metal-SC" className="logo-icon" />
          <span>Metal-SC</span>
        </div>

        <nav className={`nav ${mobileMenuOpen ? 'mobile-open' : ''}`}>
          <button className="nav-link" onClick={() => { setCurrentPage('home'); setMobileMenuOpen(false); }}>
            Inicio
          </button>
          <button className="nav-link" onClick={() => { setCurrentPage('products'); setMobileMenuOpen(false); }}>
            Peças
          </button>
          {(!user || user?.tipo === 'REVENDEDOR') && (
            <button className="nav-link" onClick={handleAnnounceClick}>
              Anunciar peças
            </button>
          )}
        </nav>

        <div className="header-actions">
          <button className="icon-btn" onClick={() => setCurrentPage('cart')}>
            <ShoppingCart size={20} />
            {cartCount > 0 && <span className="cart-badge">{cartCount}</span>}
          </button>
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
  const handleAnnounceClick = () => {
    if (user?.tipo === 'REVENDEDOR') {
      setCurrentPage('dashboard');
    } else {
      setCurrentPage('register');
    }
  };

  useEffect(() => {
    fetchPecas()
      .then((data) => setFeaturedProducts(data.slice(0, 6)))
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
            <div className="hero-search">
              <div className="hero-search-field">
                <Search size={18} />
                <input
                  type="text"
                  placeholder="Buscar peça, marca ou modelo"
                  onFocus={() => setCurrentPage('products')}
                />
              </div>
              <div className="hero-search-field">
                <MapPin size={18} />
                <input
                  type="text"
                  placeholder="Cidade ou região em SC"
                  onFocus={() => setCurrentPage('products')}
                />
              </div>
              <button className="cta-btn" onClick={() => setCurrentPage('products')}>
                Buscar
              </button>
            </div>
          </div>
          <div className="hero-card">
            <Package size={44} />
            <h3>Mais de 10 mil peças em catálogo</h3>
            <p>Atualizado diariamente por revendedores locais.</p>
          </div>
        </div>
      </section>

      <section className="features">
        <div className="container">
          <div className="section-title">
            <h2>Por que usar Metal-SC</h2>
            <p>Processo simples, seguro e transparente.</p>
          </div>
          <div className="features-grid">
            <div className="feature-card">
              <div className="feature-icon">QA</div>
              <h3>Peças verificadas</h3>
              <p>Cadastro revisado para garantir qualidade.</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">OK</div>
              <h3>Revendedores confiáveis</h3>
              <p>Perfis avaliados e histórico de vendas.</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">FAST</div>
              <h3>Entrega rápida</h3>
              <p>Acompanhe o envio e receba com segurança.</p>
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
          <div className="product-grid">
            {featuredProducts.map((product) => (
              <ProductCard
                key={product.id}
                product={product}
                onView={openProduct}
                onAdd={addToCart}
              />
            ))}
          </div>
        </div>
      </section>
    </div>
  );
};

const ProductsPage = () => {
  const { openProduct, addToCart } = useApp();
  const [products, setProducts] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [selectedState, setSelectedState] = useState('');
  const [selectedCity, setSelectedCity] = useState('');
  const [selectedUf, setSelectedUf] = useState('');
  const [minPrice, setMinPrice] = useState('');
  const [maxPrice, setMaxPrice] = useState('');
  const [loading, setLoading] = useState(true);
  const [showFilters, setShowFilters] = useState(false);

  const categories = ['MOTOR', 'SUSPENSAO', 'FREIOS', 'ELETRICA', 'CARROCERIA', 'TRANSMISSAO'];
  const states = ['NOVO', 'USADO', 'RECONDICIONADO', 'DEFEITUOSO'];

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
        !selectedUf ||
        product.endereco?.estado?.toLowerCase().includes(selectedUf.toLowerCase());
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
  }, [products, searchQuery, selectedCategory, selectedState, selectedCity, selectedUf, minPrice, maxPrice]);

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
          <label>Estado da peÃ§a</label>
          <select value={selectedState} onChange={(e) => setSelectedState(e.target.value)}>
            <option value="">Todos</option>
            {states.map((state) => (
              <option key={state} value={state}>{state}</option>
            ))}
          </select>
        </div>
        <div className="filter-group">
          <label>Cidade</label>
          <input
            type="text"
            value={selectedCity}
            onChange={(e) => setSelectedCity(e.target.value)}
            placeholder="Ex: Florianopolis"
          />
        </div>
        <div className="filter-group">
          <label>Estado (UF)</label>
          <input
            type="text"
            value={selectedUf}
            onChange={(e) => setSelectedUf(e.target.value)}
            placeholder="Ex: SC"
          />
        </div>
        <div className="filter-group">
          <label>Preco minimo</label>
          <input
            type="number"
            min="0"
            value={minPrice}
            onChange={(e) => setMinPrice(e.target.value)}
            placeholder="0"
          />
        </div>
        <div className="filter-group">
          <label>Preco maximo</label>
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
  const { selectedProduct, addToCart, setCurrentPage } = useApp();
  const [currentImageIndex, setCurrentImageIndex] = useState(0);

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
  const hasImages = images.length > 0;
  const estoqueValue = toNumberOrNull(selectedProduct?.estoque);
  const outOfStock = estoqueValue !== null && estoqueValue <= 0;

  const goPrev = () => {
    setCurrentImageIndex((prev) => (prev === 0 ? images.length - 1 : prev - 1));
  };

  const goNext = () => {
    setCurrentImageIndex((prev) => (prev === images.length - 1 ? 0 : prev + 1));
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
            </>
          ) : (
            <div className="image-placeholder">
              <Package size={32} />
            </div>
          )}
        </div>
        <div className="detail-info">
          <h1>{selectedProduct.nome}</h1>
          <p className="detail-meta">
            {selectedProduct.marca || 'Marca nao informada'} � {selectedProduct.categoria}
          </p>
          {selectedProduct.endereco?.cidade && (
            <p className="detail-meta">
              {selectedProduct.endereco.cidade}{selectedProduct.endereco.estado ? ` - ${selectedProduct.endereco.estado}` : ''}
            </p>
          )}
          <p className="detail-desc">{selectedProduct.descricao || 'Sem descricao.'}</p>
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
        </div>
      </div>
    </div>
  );
};

const CartPage = () => {
  const { cart, user, setCurrentPage, updateCartQty, removeFromCart, clearCart } = useApp();
  const [loading, setLoading] = useState(false);

  const total = cart.reduce((sum, item) => sum + item.preco * item.quantidade, 0);

  const handleCheckout = async () => {
    if (!user) {
      alert('Faca login para finalizar a compra');
      setCurrentPage('login');
      return;
    }

    setLoading(true);
    try {
      const firstItem = cart[0];
      const revendedorId =
        firstItem?.revendedorId || firstItem?.vendedorId || firstItem?.vendedor?.id;

      if (!revendedorId) {
        alert('Nao foi possivel identificar o revendedor do pedido.');
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
          cidade: 'Florianopolis',
          estado: 'SC',
          cep: '88000-000'
        }
      };

      await createPedido(pedido);
      alert('Pedido realizado com sucesso!');
      clearCart();
      setCurrentPage('dashboard');
    } catch (err) {
      console.error(err);
      alert(err.message || 'Erro ao processar pedido');
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
            {loading ? 'Processando...' : 'Finalizar compra'}
          </button>
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
      const user = await loginUsuario(email, senha);
      const token = btoa(`${email}:${senha}`);
      login(user, token);
      setCurrentPage(user?.tipo === 'ADMINISTRADOR' ? 'admin' : 'home');
    } catch (err) {
      setError('Email ou senha invalidos');
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

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleEnderecoChange = (event) => {
    const { name, value } = event.target;
    setFormData((prev) => ({
      ...prev,
      endereco: { ...prev.endereco, [name]: value }
    }));
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
        <h2>Criar conta</h2>
        {error && <div className="error-message">{error}</div>}
        {success && <div className="success-message">Cadastro realizado!</div>}
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Tipo de conta</label>
            <select name="tipo" value={formData.tipo} onChange={handleChange}>
              <option value="CLIENTE">Cliente</option>
              <option value="REVENDEDOR">Revendedor</option>
            </select>
          </div>
          <div className="form-group">
            <label>Nome</label>
            <input name="nome" value={formData.nome} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>Email</label>
            <input name="email" type="email" value={formData.email} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>Senha</label>
            <input name="senha" type="password" value={formData.senha} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>Telefone</label>
            <input name="telefone" value={formData.telefone} onChange={handleChange} />
          </div>

          {formData.tipo === 'REVENDEDOR' ? (
            <>
              <div className="form-group">
                <label>CNPJ</label>
                <input name="cnpj" value={formData.cnpj} onChange={handleChange} required />
              </div>
              <div className="form-group">
                <label>Nome da loja</label>
                <input name="nomeLoja" value={formData.nomeLoja} onChange={handleChange} required />
              </div>
            </>
          ) : (
            <>
              <div className="form-group">
                <label>Rua</label>
                <input name="rua" value={formData.endereco.rua} onChange={handleEnderecoChange} />
              </div>
              <div className="form-group">
                <label>Numero</label>
                <input name="numero" value={formData.endereco.numero} onChange={handleEnderecoChange} />
              </div>
              <div className="form-group">
                <label>Bairro</label>
                <input name="bairro" value={formData.endereco.bairro} onChange={handleEnderecoChange} />
              </div>
              <div className="form-group">
                <label>Cidade</label>
                <input name="cidade" value={formData.endereco.cidade} onChange={handleEnderecoChange} />
              </div>
              <div className="form-group">
                <label>Estado</label>
                <input name="estado" value={formData.endereco.estado} onChange={handleEnderecoChange} />
              </div>
              <div className="form-group">
                <label>CEP</label>
                <input name="cep" value={formData.endereco.cep} onChange={handleEnderecoChange} />
              </div>
            </>
          )}

          <button className="cta-btn" type="submit" disabled={loading}>
            {loading ? 'Salvando...' : 'Cadastrar'}
          </button>
        </form>
        <button className="ghost-btn" onClick={() => setCurrentPage('login')}>
          Ja tenho conta
        </button>
      </div>
    </div>
  );
};
const DashboardPage = () => {
  const { user, setCurrentPage, logout } = useApp();

  if (!user) {
    return (
      <div className="container empty-state">
        <h2>Faca login para acessar o painel</h2>
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
      {user.tipo === 'REVENDEDOR' ? <RevendedorDashboard /> : <ClienteDashboard />}
    </div>
  );
};

const ClienteDashboard = () => {
  const { user } = useApp();
  const [pedidos, setPedidos] = useState([]);

  useEffect(() => {
    if (user) {
      fetchPedidosByCliente(user.id)
        .then((data) => setPedidos(data))
        .catch((err) => console.error(err));
    }
  }, [user]);

  return (
    <div className="panel">
      <h2>Meus pedidos</h2>
      {pedidos.length === 0 ? (
        <p>Nenhum pedido realizado ainda.</p>
      ) : (
        <div className="order-list">
          {pedidos.map((pedido) => (
            <div key={pedido.id} className="order-card">
              <div>
                <strong>Pedido #{pedido.id?.substring(0, 8)}</strong>
                <p>{formatDate(pedido.dataCriacao)}</p>
                {pedido.status === 'CONFIRMADO' &&
                (pedido.revendedorTelefone || WHATSAPP_RELEASE_URL) ? (
                  <a
                    className="ghost-btn outline small"
                    href={getWhatsAppReleaseLink(
                      pedido.revendedorTelefone,
                      `Ola! Estou falando sobre o pedido ${pedido.id?.substring(0, 8)}.`
                    )}
                    target="_blank"
                    rel="noreferrer"
                  >
                    Liberar WhatsApp
                  </a>
                ) : (
                  <span className="meta-note">
                    WhatsApp liberado após pagamento confirmado
                  </span>
                )}
              </div>
              <div className="order-status">
                <span>{pedido.status}</span>
                <strong>{formatPrice(pedido.valorTotal)}</strong>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

const RevendedorDashboard = () => {
  const { user } = useApp();
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

  const categories = ['MOTOR', 'SUSPENSAO', 'FREIOS', 'ELETRICA', 'CARROCERIA', 'TRANSMISSAO'];
  const states = ['NOVO', 'USADO', 'RECONDICIONADO', 'DEFEITUOSO'];

  const loadPecas = () => {
    if (user) {
      fetchPecasByRevendedor(user.id)
        .then((data) => setPecas(data))
        .catch((err) => console.error(err));
    }
  };

  const loadPedidos = async () => {
    if (!user) return;
    setLoadingPedidos(true);
    try {
      const data = await fetchPedidosByRevendedor(user.id);
      setPedidos(data || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoadingPedidos(false);
    }
  };

  useEffect(() => {
    loadPecas();
    loadPedidos();
  }, [user]);

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
    if (window.confirm('Deseja excluir esta peca?')) {
      try {
        await deletePeca(pecaId);
        loadPecas();
      } catch (err) {
        console.error(err);
        alert('Erro ao excluir a peca.');
      }
    }
  };

  const handleFilesChange = (event) => {
    const files = Array.from(event.target.files || []);
    const allowed = 3 - existingImages.length;
    if (allowed <= 0) {
      alert('Limite de 3 imagens por peca');
      return;
    }
    if (files.length > allowed) {
      alert('Limite de 3 imagens por peca');
    }
    setImageFiles(files.slice(0, Math.max(allowed, 0)));
  };

  const handleRemoveExistingImage = async (url) => {
    if (!editingPeca) return;
    try {
      await removePecaImagem(editingPeca.id, url);
      setExistingImages((prev) => prev.filter((img) => img !== url));
    } catch (err) {
      console.error(err);
      alert('Erro ao remover a imagem.');
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

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      const payload = {
        ...formData,
        preco: Number(formData.preco),
        estoque: Number(formData.estoque),
        ano: formData.ano ? Number(formData.ano) : null
      };

      const saved = await savePeca(payload, user.id, editingPeca?.id);
      const pecaId = saved?.id || editingPeca?.id;

      if (pecaId && imageFiles.length > 0) {
        for (const file of imageFiles) {
          try {
            await uploadPecaImagem(pecaId, file);
          } catch (uploadErr) {
            console.error(uploadErr);
            alert(uploadErr.message || 'Erro ao enviar imagem');
            break;
          }
        }
      }

      setEditingPeca(null);
      setImageFiles([]);
      loadPecas();
      alert(editingPeca ? 'Peca atualizada!' : 'Peca cadastrada!');
    } catch (err) {
      console.error(err);
      alert(err.message || 'Erro de conexao');
    }
  };

  const handleConfirmarPagamento = async (pedidoId) => {
    if (!pedidoId) return;
    if (!window.confirm('Confirmar pagamento deste pedido?')) return;
    setConfirmingId(pedidoId);
    try {
      await confirmarPagamentoPedido(pedidoId);
      await loadPedidos();
      alert('Pagamento confirmado.');
    } catch (err) {
      console.error(err);
      alert(err.message || 'Erro ao confirmar pagamento');
    } finally {
      setConfirmingId(null);
    }
  };

  return (
    <div className="dashboard-grid">
      <div className="panel">
        <div className="panel-header">
          <h2>Minhas peças</h2>
          <button className="ghost-btn" onClick={() => setEditingPeca(null)}>
            <Plus size={16} /> Nova peça
          </button>
        </div>
        {pecas.length === 0 ? (
          <p>Nenhuma peça cadastrada.</p>
        ) : (
          <div className="table">
            {pecas.map((peca) => (
              <div key={peca.id} className="table-row">
                <div>
                  <strong>{peca.nome}</strong>
                  <p>{peca.categoria}</p>
                </div>
                <div className="table-actions">
                  <span>{formatPrice(peca.preco)}</span>
                  <button className="icon-btn" onClick={() => setEditingPeca(peca)}>
                    <Edit2 size={16} />
                  </button>
                  <button className="icon-btn" onClick={() => handleDelete(peca.id)}>
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
          <h2>Pedidos recebidos</h2>
          <button className="ghost-btn" onClick={loadPedidos} disabled={loadingPedidos}>
            {loadingPedidos ? 'Atualizando...' : 'Atualizar'}
          </button>
        </div>
        {loadingPedidos ? (
          <p>Carregando pedidos...</p>
        ) : pedidos.length === 0 ? (
          <p>Nenhum pedido recebido.</p>
        ) : (
          <div className="order-list">
            {pedidos.map((pedido) => {
              const isPendente = pedido.status === 'PENDENTE';
              return (
                <div key={pedido.id} className="order-card">
                  <div>
                    <strong>Pedido #{pedido.id?.substring(0, 8)}</strong>
                    <p>Cliente: {pedido.clienteNome || 'Cliente'}</p>
                    <p>{formatDate(pedido.dataCriacao)}</p>
                  </div>
                  <div className="order-status">
                    <span>{pedido.status}</span>
                    <strong>{formatPrice(pedido.valorTotal)}</strong>
                    {isPendente ? (
                      <button
                        className="cta-btn small"
                        onClick={() => handleConfirmarPagamento(pedido.id)}
                        disabled={confirmingId === pedido.id}
                      >
                        {confirmingId === pedido.id ? 'Confirmando...' : 'Confirmar pagamento'}
                      </button>
                    ) : (
                      <span className="meta-note">Pagamento confirmado</span>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>

      <div className="panel">
        <h2>{editingPeca ? 'Editar peca' : 'Nova peca'}</h2>
        <form onSubmit={handleSubmit} className="form-grid">
          <div className="form-group">
            <label>Nome</label>
            <input
              name="nome"
              value={formData.nome}
              onChange={(e) => setFormData({ ...formData, nome: e.target.value })}
              required
            />
          </div>
          <div className="form-group">
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
                <option key={state} value={state}>{state}</option>
              ))}
            </select>
          </div>
          <div className="form-group">
            <label>Preco</label>
            <input
              type="number"
              name="preco"
              value={formData.preco}
              onChange={(e) => setFormData({ ...formData, preco: e.target.value })}
              required
            />
          </div>
          <div className="form-group">
            <label>Estoque</label>
            <input
              type="number"
              name="estoque"
              value={formData.estoque}
              onChange={(e) => setFormData({ ...formData, estoque: e.target.value })}
              required
            />
          </div>
          <div className="form-group">
            <label>Ano</label>
            <input
              type="number"
              name="ano"
              value={formData.ano}
              onChange={(e) => setFormData({ ...formData, ano: e.target.value })}
            />
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
            <label>Modelo veiculo</label>
            <input
              name="modeloVeiculo"
              value={formData.modeloVeiculo}
              onChange={(e) => setFormData({ ...formData, modeloVeiculo: e.target.value })}
            />
          </div>
          <div className="form-group">
            <label>Rua</label>
            <input
              name="rua"
              value={formData.endereco.rua}
              onChange={handleEnderecoChange}
            />
          </div>
          <div className="form-group">
            <label>Numero</label>
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
            <label>Estado (UF)</label>
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
          <div className="form-group">
            <label>Complemento</label>
            <input
              name="complemento"
              value={formData.endereco.complemento}
              onChange={handleEnderecoChange}
            />
          </div>
          <div className="form-group">
            <label>Fotos da peça</label>
            <input type="file" accept="image/*" multiple onChange={handleFilesChange} />
            <small>Máximo de 3 imagens por peça.</small>
            {(existingImages.length > 0 || imageFiles.length > 0) && (
              <div className="image-list">
                {existingImages.map((url) => (
                  <div key={url} className="image-chip">
                    <img src={resolveImageUrl(url)} alt="Imagem da peça" />
                    <button
                      type="button"
                      className="icon-btn"
                      onClick={() => handleRemoveExistingImage(url)}
                    >
                      <X size={14} />
                    </button>
                  </div>
                ))}
                {imageFiles.map((file, index) => (
                  <div key={`${file.name}-${index}`} className="image-chip">
                    <span>{file.name}</span>
                    <button
                      type="button"
                      className="icon-btn"
                      onClick={() => handleRemoveNewFile(index)}
                    >
                      <X size={14} />
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>
          <button className="cta-btn" type="submit">
            {editingPeca ? 'Salvar alterações' : 'Cadastrar peça'}
          </button>
        </form>
      </div>
    </div>
  );
};
const AdminPage = () => {
  const { user, setCurrentPage, logout } = useApp();
  const [dashboard, setDashboard] = useState(null);
  const [usuarios, setUsuarios] = useState([]);
  const [revendedores, setRevendedores] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadData = async () => {
    setLoading(true);
    setError('');
    try {
      const [dash, users, sellers] = await Promise.all([
        fetchAdminDashboard(),
        fetchAdminUsuarios(),
        fetchAdminRevendedores()
      ]);
      setDashboard(dash);
      setUsuarios(users || []);
      setRevendedores(sellers || []);
    } catch (err) {
      setError(err.message || 'Erro ao carregar dados');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user?.tipo === 'ADMINISTRADOR') {
      loadData();
    }
  }, [user]);

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

  const handleRemoveUsuario = async (usuario) => {
    if (!usuario?.id) return;
    if (!window.confirm(`Remover o usuario ${usuario.nome}?`)) return;
    try {
      await deleteAdminUsuario(usuario.id);
      loadData();
    } catch (err) {
      alert(err.message || 'Erro ao remover usuario');
    }
  };

  const handleRemoveRevendedor = async (revendedor) => {
    if (!revendedor?.id) return;
    if (!window.confirm(`Remover o revendedor ${revendedor.nome}?`)) return;
    try {
      await deleteAdminRevendedor(revendedor.id);
      loadData();
    } catch (err) {
      alert(err.message || 'Erro ao remover revendedor');
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
          alert('Valor invalido.');
          return;
        }
        valor = parsed;
      }
    } else if (!window.confirm(`Zerar todas as taxas de ${revendedor.nome}?`)) {
      return;
    }

    try {
      await baixarTaxasRevendedor(revendedor.id, valor);
      loadData();
    } catch (err) {
      alert(err.message || 'Erro ao baixar taxas');
    }
  };

  const handleAtivarPremium = async (revendedor) => {
    if (!revendedor?.id) return;
    const entrada = window.prompt('Quantos dias de premium?', '30');
    if (entrada === null) return;
    const dias = Number(entrada);
    if (!Number.isFinite(dias) || dias <= 0) {
      alert('Dias invalidos.');
      return;
    }
    try {
      await ativarPremiumRevendedor(revendedor.id, Math.round(dias));
      loadData();
    } catch (err) {
      alert(err.message || 'Erro ao ativar premium');
    }
  };

  const handleDesativarPremium = async (revendedor) => {
    if (!revendedor?.id) return;
    if (!window.confirm(`Desativar premium de ${revendedor.nome}?`)) return;
    try {
      await desativarPremiumRevendedor(revendedor.id);
      loadData();
    } catch (err) {
      alert(err.message || 'Erro ao desativar premium');
    }
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
          <h3>Pecas</h3>
          <div className="stat-value">{totalPecas}</div>
          <p>Vendas: {totalVendas}</p>
        </div>
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
      <p>{product.marca || 'Marca nao informada'}</p>
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


