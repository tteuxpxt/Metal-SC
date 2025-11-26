import React, { useState, useEffect, createContext, useContext } from 'react';
import { Search, ShoppingCart, User, LogOut, Plus, Edit2, Trash2, Filter, X, Menu, Heart, Star, MapPin, Phone, Mail, Package, TrendingUp } from 'lucide-react';
import './App.css';

// API Configuration
const API_URL = 'http://localhost:8080/api';

// Context for Auth and Cart
const AppContext = createContext();

const useApp = () => {
  const context = useContext(AppContext);
  if (!context) throw new Error('useApp must be used within AppProvider');
  return context;
};

// Main App Component
const App = () => {
  const [user, setUser] = useState(null);
  const [cart, setCart] = useState([]);
  const [currentPage, setCurrentPage] = useState('home');
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  useEffect(() => {
    const savedUser = localStorage.getItem('metal_user');
    const savedCart = localStorage.getItem('metal_cart');
    if (savedUser) setUser(JSON.parse(savedUser));
    if (savedCart) setCart(JSON.parse(savedCart));
  }, []);

  const login = (userData) => {
    setUser(userData);
    localStorage.setItem('metal_user', JSON.stringify(userData));
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('metal_user');
    setCurrentPage('home');
  };

  const addToCart = (product) => {
    const existing = cart.find(item => item.id === product.id);
    let newCart;
    if (existing) {
      newCart = cart.map(item =>
        item.id === product.id ? { ...item, quantidade: item.quantidade + 1 } : item
      );
    } else {
      newCart = [...cart, { ...product, quantidade: 1 }];
    }
    setCart(newCart);
    localStorage.setItem('metal_cart', JSON.stringify(newCart));
  };

  const updateCartQuantity = (productId, delta) => {
    const newCart = cart.map(item =>
      item.id === productId ? { ...item, quantidade: Math.max(0, item.quantidade + delta) } : item
    ).filter(item => item.quantidade > 0);
    setCart(newCart);
    localStorage.setItem('metal_cart', JSON.stringify(newCart));
  };

  const removeFromCart = (productId) => {
    const newCart = cart.filter(item => item.id !== productId);
    setCart(newCart);
    localStorage.setItem('metal_cart', JSON.stringify(newCart));
  };

  const clearCart = () => {
    setCart([]);
    localStorage.removeItem('metal_cart');
  };

  const value = {
    user,
    cart,
    currentPage,
    setCurrentPage,
    login,
    logout,
    addToCart,
    updateCartQuantity,
    removeFromCart,
    clearCart,
    mobileMenuOpen,
    setMobileMenuOpen
  };

  return (
    <AppContext.Provider value={value}>
      <div className="app">
        <Header />
        <main className="main-content">
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
    </AppContext.Provider>
  );
};

// Header Component
const Header = () => {
  const { user, cart, currentPage, setCurrentPage, logout, mobileMenuOpen, setMobileMenuOpen } = useApp();
  const cartCount = cart.reduce((sum, item) => sum + item.quantidade, 0);

  return (
    <header className="header">
      <div className="header-top">
        <div className="container">
          <div className="header-content">
            <div className="logo" onClick={() => setCurrentPage('home')}>
              <Package size={32} />
              <span>Metal-SC</span>
            </div>

            <button className="mobile-menu-btn" onClick={() => setMobileMenuOpen(!mobileMenuOpen)}>
              <Menu size={24} />
            </button>

            <nav className={`nav ${mobileMenuOpen ? 'mobile-open' : ''}`}>
              <a onClick={() => { setCurrentPage('home'); setMobileMenuOpen(false); }}>Início</a>
              <a onClick={() => { setCurrentPage('products'); setMobileMenuOpen(false); }}>Peças</a>
              {user && user.tipo === 'REVENDEDOR' && (
                <a onClick={() => { setCurrentPage('dashboard'); setMobileMenuOpen(false); }}>Anunciar</a>
              )}
            </nav>

            <div className="header-actions">
              <button className="cart-btn" onClick={() => setCurrentPage('cart')}>
                <ShoppingCart size={22} />
                {cartCount > 0 && <span className="cart-badge">{cartCount}</span>}
              </button>

              {user ? (
                <>
                  <button className="user-btn" onClick={() => setCurrentPage('dashboard')}>
                    <User size={22} />
                    <span className="user-name">{user.nome}</span>
                  </button>
                  <button className="logout-btn" onClick={logout}>
                    <LogOut size={20} />
                  </button>
                </>
              ) : (
                <button className="login-btn" onClick={() => setCurrentPage('login')}>
                  Entrar
                </button>
              )}
            </div>
          </div>
        </div>
      </div>
    </header>
  );
};

// Home Page
const HomePage = () => {
  const { setCurrentPage } = useApp();
  const [featuredProducts, setFeaturedProducts] = useState([]);

  useEffect(() => {
    fetch(`${API_URL}/pecas`)
      .then(res => res.json())
      .then(data => setFeaturedProducts(data.slice(0, 8)))
      .catch(err => console.error(err));
  }, []);

  return (
    <div className="home-page">
      <section className="hero">
        <div className="container">
          <div className="hero-content">
            <h1>Encontre a peça perfeita para seu veículo</h1>
            <p>Milhares de peças automotivas usadas de qualidade</p>
            <button className="cta-btn" onClick={() => setCurrentPage('products')}>
              Ver todas as peças
            </button>
          </div>
        </div>
      </section>

      <section className="features">
        <div className="container">
          <div className="features-grid">
            <div className="feature-card">
              <div className="feature-icon">🔧</div>
              <h3>Peças Verificadas</h3>
              <p>Todas as peças passam por verificação de qualidade</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">✅</div>
              <h3>Vendedores Confiáveis</h3>
              <p>Revendedores verificados e avaliados</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">🚚</div>
              <h3>Entrega Rápida</h3>
              <p>Receba suas peças com segurança</p>
            </div>
            <div className="feature-card">
              <div className="feature-icon">💰</div>
              <h3>Melhor Preço</h3>
              <p>Compare preços e economize</p>
            </div>
          </div>
        </div>
      </section>

      {featuredProducts.length > 0 && (
        <section className="featured-products">
          <div className="container">
            <h2>Peças em Destaque</h2>
            <div className="products-grid">
              {featuredProducts.map(product => (
                <ProductCard key={product.id} product={product} />
              ))}
            </div>
          </div>
        </section>
      )}
    </div>
  );
};

// Products Page
const ProductsPage = () => {
  const [products, setProducts] = useState([]);
  const [filteredProducts, setFilteredProducts] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [selectedBrand, setSelectedBrand] = useState('');
  const [priceRange, setPriceRange] = useState([0, 10000]);
  const [showFilters, setShowFilters] = useState(false);
  const [loading, setLoading] = useState(true);

  const categories = ['MOTOR', 'SUSPENSAO', 'FREIOS', 'ELETRICA', 'CARROCERIA', 'TRANSMISSAO'];

  useEffect(() => {
    setLoading(true);
    fetch(`${API_URL}/pecas`)
      .then(res => res.json())
      .then(data => {
        setProducts(data);
        setFilteredProducts(data);
        setLoading(false);
      })
      .catch(err => {
        console.error(err);
        setLoading(false);
      });
  }, []);

  useEffect(() => {
    let filtered = products;

    if (searchQuery) {
      filtered = filtered.filter(p =>
        p.nome.toLowerCase().includes(searchQuery.toLowerCase()) ||
        p.descricao?.toLowerCase().includes(searchQuery.toLowerCase())
      );
    }

    if (selectedCategory) {
      filtered = filtered.filter(p => p.categoria === selectedCategory);
    }

    if (selectedBrand) {
      filtered = filtered.filter(p => p.marca === selectedBrand);
    }

    filtered = filtered.filter(p => p.preco >= priceRange[0] && p.preco <= priceRange[1]);

    setFilteredProducts(filtered);
  }, [searchQuery, selectedCategory, selectedBrand, priceRange, products]);

  const brands = [...new Set(products.map(p => p.marca).filter(Boolean))];

  return (
    <div className="products-page">
      <div className="container">
        <div className="products-header">
          <h1>Todas as Peças</h1>
          <button className="filter-toggle-btn" onClick={() => setShowFilters(!showFilters)}>
            <Filter size={20} />
            Filtros
          </button>
        </div>

        <div className="search-bar">
          <Search size={20} />
          <input
            type="text"
            placeholder="Buscar peças, marcas, modelos..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>

        <div className="products-content">
          <aside className={`filters ${showFilters ? 'show' : ''}`}>
            <div className="filter-header">
              <h3>Filtros</h3>
              <button onClick={() => setShowFilters(false)}>
                <X size={20} />
              </button>
            </div>

            <div className="filter-section">
              <h4>Categoria</h4>
              <select value={selectedCategory} onChange={(e) => setSelectedCategory(e.target.value)}>
                <option value="">Todas</option>
                {categories.map(cat => (
                  <option key={cat} value={cat}>{cat}</option>
                ))}
              </select>
            </div>

            <div className="filter-section">
              <h4>Marca</h4>
              <select value={selectedBrand} onChange={(e) => setSelectedBrand(e.target.value)}>
                <option value="">Todas</option>
                {brands.map(brand => (
                  <option key={brand} value={brand}>{brand}</option>
                ))}
              </select>
            </div>

            <div className="filter-section">
              <h4>Preço</h4>
              <div className="price-inputs">
                <input
                  type="number"
                  placeholder="Mín"
                  value={priceRange[0]}
                  onChange={(e) => setPriceRange([+e.target.value, priceRange[1]])}
                />
                <span>até</span>
                <input
                  type="number"
                  placeholder="Máx"
                  value={priceRange[1]}
                  onChange={(e) => setPriceRange([priceRange[0], +e.target.value])}
                />
              </div>
            </div>

            <button className="clear-filters" onClick={() => {
              setSelectedCategory('');
              setSelectedBrand('');
              setPriceRange([0, 10000]);
              setSearchQuery('');
            }}>
              Limpar Filtros
            </button>
          </aside>

          <div className="products-list">
            <p className="results-count">{filteredProducts.length} peças encontradas</p>
            {loading ? (
              <div className="loading">Carregando...</div>
            ) : (
              <div className="products-grid">
                {filteredProducts.map(product => (
                  <ProductCard key={product.id} product={product} />
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

// Product Card Component
const ProductCard = ({ product }) => {
  const { addToCart } = useApp();
  const [saved, setSaved] = useState(false);

  return (
    <div className="product-card">
      <div className="product-image">
        <div className="product-badge">{product.estado || 'USADO'}</div>
        <button className="save-btn" onClick={() => setSaved(!saved)}>
          <Heart size={20} fill={saved ? '#ff6b6b' : 'none'} />
        </button>
        <div className="image-placeholder">🔧</div>
      </div>
      <div className="product-info">
        <h3>{product.nome}</h3>
        <p className="product-meta">
          {product.marca} {product.modeloVeiculo} • {product.ano}
        </p>
        <p className="product-category">{product.categoria}</p>
        <div className="product-footer">
          <div className="product-price">
            <span className="price">R$ {product.preco?.toFixed(2)}</span>
            <span className="stock">Estoque: {product.estoque}</span>
          </div>
          <button className="add-to-cart-btn" onClick={() => addToCart(product)}>
            <ShoppingCart size={18} />
            Adicionar
          </button>
        </div>
      </div>
    </div>
  );
};

// Cart Page
const CartPage = () => {
  const { cart, updateCartQuantity, removeFromCart, clearCart, user, setCurrentPage } = useApp();
  const [loading, setLoading] = useState(false);

  const subtotal = cart.reduce((sum, item) => sum + (item.preco * item.quantidade), 0);
  const shipping = subtotal > 200 ? 0 : 30;
  const total = subtotal + shipping;

  const handleCheckout = async () => {
    if (!user) {
      alert('Faça login para finalizar a compra');
      setCurrentPage('login');
      return;
    }

    setLoading(true);
    try {
      const pedido = {
        clienteId: user.id,
        itens: cart.map(item => ({
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

      const res = await fetch(`${API_URL}/pedidos`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(pedido)
      });

      if (res.ok) {
        alert('Pedido realizado com sucesso!');
        clearCart();
        setCurrentPage('dashboard');
      } else {
        alert('Erro ao processar pedido');
      }
    } catch (err) {
      console.error(err);
      alert('Erro ao processar pedido');
    } finally {
      setLoading(false);
    }
  };

  if (cart.length === 0) {
    return (
      <div className="empty-cart">
        <div className="container">
          <ShoppingCart size={64} />
          <h2>Seu carrinho está vazio</h2>
          <p>Adicione produtos para continuar</p>
          <button className="cta-btn" onClick={() => setCurrentPage('products')}>
            Ver Peças
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="cart-page">
      <div className="container">
        <h1>Meu Carrinho</h1>
        <div className="cart-content">
          <div className="cart-items">
            {cart.map(item => (
              <div key={item.id} className="cart-item">
                <div className="item-image">🔧</div>
                <div className="item-details">
                  <h3>{item.nome}</h3>
                  <p>{item.marca} {item.modeloVeiculo}</p>
                  <p className="item-price">R$ {item.preco?.toFixed(2)}</p>
                </div>
                <div className="item-quantity">
                  <button onClick={() => updateCartQuantity(item.id, -1)}>-</button>
                  <span>{item.quantidade}</span>
                  <button onClick={() => updateCartQuantity(item.id, 1)}>+</button>
                </div>
                <div className="item-total">
                  R$ {(item.preco * item.quantidade).toFixed(2)}
                </div>
                <button className="remove-btn" onClick={() => removeFromCart(item.id)}>
                  <Trash2 size={18} />
                </button>
              </div>
            ))}
          </div>

          <div className="cart-summary">
            <h3>Resumo do Pedido</h3>
            <div className="summary-row">
              <span>Subtotal</span>
              <span>R$ {subtotal.toFixed(2)}</span>
            </div>
            <div className="summary-row">
              <span>Frete</span>
              <span>{shipping === 0 ? 'GRÁTIS' : `R$ ${shipping.toFixed(2)}`}</span>
            </div>
            <div className="summary-total">
              <span>Total</span>
              <span>R$ {total.toFixed(2)}</span>
            </div>
            <button
              className="checkout-btn"
              onClick={handleCheckout}
              disabled={loading}
            >
              {loading ? 'Processando...' : 'Finalizar Compra'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

// Login Page
const LoginPage = () => {
  const { login, setCurrentPage } = useApp();
  const [email, setEmail] = useState('');
  const [senha, setSenha] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const res = await fetch(`${API_URL}/usuarios/email/${email}`);
      if (res.ok) {
        const user = await res.json();
        login(user);
        setCurrentPage('home');
      } else {
        setError('Email ou senha inválidos');
      }
    } catch (err) {
      setError('Erro ao fazer login');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="container">
        <div className="auth-card">
          <h2>Entrar</h2>
          {error && <div className="error-message">{error}</div>}
          <form onSubmit={handleSubmit}>
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
                required
              />
            </div>
            <button type="submit" className="submit-btn" disabled={loading}>
              {loading ? 'Entrando...' : 'Entrar'}
            </button>
          </form>
          <p className="auth-link">
            Não tem conta? <a onClick={() => setCurrentPage('register')}>Cadastre-se</a>
          </p>
        </div>
      </div>
    </div>
  );
};

// Register Page
const RegisterPage = () => {
  const { setCurrentPage } = useApp();
  const [formData, setFormData] = useState({
    nome: '',
    email: '',
    senha: '',
    telefone: '',
    tipo: 'CLIENTE',
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

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleEnderecoChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      endereco: {
        ...prev.endereco,
        [name]: value
      }
    }));
  };

 const handleSubmit = async (e) => {
   e.preventDefault();
   setError('');
   setLoading(true);

   try {
     const endpoint = formData.tipo === 'CLIENTE'
       ? `${API_URL}/clientes`
       : `${API_URL}/revendedores`;

     // ✅ Payload com TIPO incluído
     const payload = formData.tipo === 'CLIENTE'
       ? {
           nome: formData.nome,
           email: formData.email,
           senha: formData.senha,
           telefone: formData.telefone,
           tipo: 'CLIENTE',  // ✅ IMPORTANTE
           endereco: formData.endereco
         }
       : {
           nome: formData.nome,
           email: formData.email,
           senha: formData.senha,
           telefone: formData.telefone,
           tipo: 'REVENDEDOR',  // ✅ IMPORTANTE
           cnpj: formData.cnpj,
           nomeLoja: formData.nomeLoja
         };

     console.log('📤 Enviando para:', endpoint);
     console.log('📦 Payload:', JSON.stringify(payload, null, 2));

     const res = await fetch(endpoint, {
       method: 'POST',
       headers: { 'Content-Type': 'application/json' },
       body: JSON.stringify(payload)
     });

     const responseData = await res.json();
     console.log('📥 Resposta:', responseData);

     if (res.ok) {
       setSuccess(true);
       setTimeout(() => setCurrentPage('login'), 2000);
     } else {
       setError(responseData.error || responseData.message || 'Erro ao cadastrar');
     }
   } catch (err) {
     console.error('❌ Erro:', err);
     setError('Erro de conexão. Verifique se o backend está rodando.');
   } finally {
     setLoading(false);
   }
 };

  return (
    <div className="auth-page">
      <div className="container">
        <div className="auth-card" style={{ maxWidth: '600px' }}>
          <h2>Cadastrar</h2>
          {error && <div className="error-message" style={{
            backgroundColor: '#fee',
            padding: '1rem',
            borderRadius: '4px',
            marginBottom: '1rem',
            color: '#c00'
          }}>{error}</div>}
          {success && <div className="success-message" style={{
            backgroundColor: '#efe',
            padding: '1rem',
            borderRadius: '4px',
            marginBottom: '1rem',
            color: '#0a0'
          }}>Cadastro realizado! Redirecionando...</div>}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Tipo de Usuário *</label>
              <select
                name="tipo"
                value={formData.tipo}
                onChange={handleChange}
                required
              >
                <option value="CLIENTE">Cliente</option>
                <option value="REVENDEDOR">Revendedor</option>
              </select>
            </div>

            <div className="form-group">
              <label>Nome Completo *</label>
              <input
                type="text"
                name="nome"
                value={formData.nome}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label>Email *</label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label>Telefone *</label>
              <input
                type="tel"
                name="telefone"
                value={formData.telefone}
                onChange={handleChange}
                placeholder="(48) 99999-9999"
                required
              />
            </div>

            <div className="form-group">
              <label>Senha *</label>
              <input
                type="password"
                name="senha"
                value={formData.senha}
                onChange={handleChange}
                minLength="6"
                required
              />
              <small style={{ color: '#666', fontSize: '0.85rem' }}>
                Mínimo de 6 caracteres
              </small>
            </div>

            {/* CAMPOS ESPECÍFICOS PARA REVENDEDOR */}
            {formData.tipo === 'REVENDEDOR' && (
              <>
                <div className="form-group">
                  <label>CNPJ *</label>
                  <input
                    type="text"
                    name="cnpj"
                    value={formData.cnpj}
                    onChange={handleChange}
                    placeholder="00.000.000/0000-00"
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Nome da Loja *</label>
                  <input
                    type="text"
                    name="nomeLoja"
                    value={formData.nomeLoja}
                    onChange={handleChange}
                    required
                  />
                </div>
              </>
            )}

            {/* CAMPOS ESPECÍFICOS PARA CLIENTE */}
            {formData.tipo === 'CLIENTE' && (
              <>
                <h3 style={{ marginTop: '1.5rem', marginBottom: '1rem' }}>
                  Endereço (Opcional)
                </h3>

                <div className="form-row" style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '1rem' }}>
                  <div className="form-group">
                    <label>Rua</label>
                    <input
                      type="text"
                      name="rua"
                      value={formData.endereco.rua}
                      onChange={handleEnderecoChange}
                    />
                  </div>
                  <div className="form-group">
                    <label>Número</label>
                    <input
                      type="text"
                      name="numero"
                      value={formData.endereco.numero}
                      onChange={handleEnderecoChange}
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label>Complemento</label>
                  <input
                    type="text"
                    name="complemento"
                    value={formData.endereco.complemento}
                    onChange={handleEnderecoChange}
                    placeholder="Apto, Bloco, etc."
                  />
                </div>

                <div className="form-row" style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                  <div className="form-group">
                    <label>Bairro</label>
                    <input
                      type="text"
                      name="bairro"
                      value={formData.endereco.bairro}
                      onChange={handleEnderecoChange}
                    />
                  </div>
                  <div className="form-group">
                    <label>CEP</label>
                    <input
                      type="text"
                      name="cep"
                      value={formData.endereco.cep}
                      onChange={handleEnderecoChange}
                      placeholder="88000-000"
                    />
                  </div>
                </div>

                <div className="form-row" style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '1rem' }}>
                  <div className="form-group">
                    <label>Cidade</label>
                    <input
                      type="text"
                      name="cidade"
                      value={formData.endereco.cidade}
                      onChange={handleEnderecoChange}
                    />
                  </div>
                  <div className="form-group">
                    <label>Estado</label>
                    <select
                      name="estado"
                      value={formData.endereco.estado}
                      onChange={handleEnderecoChange}
                    >
                      <option value="">Selecione</option>
                      <option value="SC">SC</option>
                      <option value="PR">PR</option>
                      <option value="RS">RS</option>
                      <option value="SP">SP</option>
                      {/* Adicione outros estados conforme necessário */}
                    </select>
                  </div>
                </div>
              </>
            )}

            <button
              type="submit"
              className="submit-btn"
              disabled={loading}
              style={{ marginTop: '1.5rem', width: '100%' }}
            >
              {loading ? 'Cadastrando...' : 'Cadastrar'}
            </button>
          </form>

          <p className="auth-link" style={{ marginTop: '1rem', textAlign: 'center' }}>
            Já tem conta? <a onClick={() => setCurrentPage('login')} style={{
              color: 'var(--primary)',
              cursor: 'pointer',
              textDecoration: 'underline'
            }}>Entre aqui</a>
          </p>
        </div>
      </div>
    </div>
  );
};

// Dashboard Page
const DashboardPage = () => {
  const { user } = useApp();

  if (!user) {
    return (
      <div className="container">
        <h2>Faça login para acessar o painel</h2>
      </div>
    );
  }

  return user.tipo === 'REVENDEDOR' ? <RevendedorDashboard /> : <ClienteDashboard />;
};

// Cliente Dashboard
const ClienteDashboard = () => {
  const { user } = useApp();
  const [pedidos, setPedidos] = useState([]);

  useEffect(() => {
    if (user) {
      fetch(`${API_URL}/pedidos/cliente/${user.id}`)
        .then(res => res.json())
        .then(data => setPedidos(data))
        .catch(err => console.error(err));
    }
  }, [user]);

  return (
    <div className="dashboard-page">
      <div className="container">
        <h1>Meus Pedidos</h1>
        <div className="orders-list">
          {pedidos.length === 0 ? (
            <p>Nenhum pedido realizado ainda</p>
          ) : (
            pedidos.map(pedido => (
              <div key={pedido.id} className="order-card">
                <div className="order-header">
                  <span className="order-id">Pedido #{pedido.id?.substring(0, 8)}</span>
                  <span className={`order-status status-${pedido.status?.toLowerCase()}`}>
                    {pedido.status}
                  </span>
                </div>
                <div className="order-details">
                  <p>Data: {new Date(pedido.dataCriacao).toLocaleDateString()}</p>
                  <p className="order-total">Total: R$ {pedido.valorTotal?.toFixed(2)}</p>
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
};

// Revendedor Dashboard
const RevendedorDashboard = () => {
  const { user } = useApp();
  const [pecas, setPecas] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [editingPeca, setEditingPeca] = useState(null);

  useEffect(() => {
    loadPecas();
  }, [user]);

  const loadPecas = () => {
    if (user) {
      fetch(`${API_URL}/pecas/revendedor/${user.id}`)
        .then(res => res.json())
        .then(data => setPecas(data))
        .catch(err => console.error(err));
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Deseja excluir esta peça?')) {
      try {
        await fetch(`${API_URL}/pecas/${id}`, { method: 'DELETE' });
        loadPecas();
              } catch (err) {
                console.error('Erro ao excluir peça:', err);
                alert('Erro ao excluir a peça.');
              }
            }
          };

          const handleEdit = (peca) => {
            setEditingPeca(peca);
            setShowModal(true);
          };

          const handleAddNew = () => {
            setEditingPeca(null);
            setShowModal(true);
          };

          const handleSave = async (pecaData) => {
            const method = editingPeca ? 'PUT' : 'POST';
            const url = editingPeca
              ? `${API_URL}/pecas/${editingPeca.id}`
              : `${API_URL}/pecas`;

            try {
              const payload = {
                ...pecaData,
                revendedorId: user.id
              };

              const res = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
              });

              if (res.ok) {
                setShowModal(false);
                loadPecas();
                alert(editingPeca ? 'Peça atualizada!' : 'Peça cadastrada!');
              } else {
                alert('Erro ao salvar peça');
              }
            } catch (err) {
              console.error(err);
              alert('Erro de conexão');
            }
          };

          return (
            <div className="dashboard-page">
              <div className="container">
                <div className="dashboard-header">
                  <h1>Painel do Revendedor</h1>
                  <button className="cta-btn" onClick={handleAddNew}>
                    <Plus size={20} /> Nova Peça
                  </button>
                </div>

                <div className="stats-cards">
                  <div className="stat-card">
                    <h3>Total de Peças</h3>
                    <p className="stat-value">{pecas.length}</p>
                  </div>
                  <div className="stat-card">
                    <h3>Valor em Estoque</h3>
                    <p className="stat-value">
                      R$ {pecas.reduce((acc, p) => acc + (p.preco * p.estoque), 0).toFixed(2)}
                    </p>
                  </div>
                </div>

                <div className="table-responsive">
                  <table className="data-table">
                    <thead>
                      <tr>
                        <th>Peça</th>
                        <th>Carro</th>
                        <th>Categoria</th>
                        <th>Preço</th>
                        <th>Estoque</th>
                        <th>Ações</th>
                      </tr>
                    </thead>
                    <tbody>
                      {pecas.map(peca => (
                        <tr key={peca.id}>
                          <td>{peca.nome}</td>
                          <td>{peca.modeloVeiculo} ({peca.ano})</td>
                          <td>{peca.categoria}</td>
                          <td>R$ {peca.preco.toFixed(2)}</td>
                          <td>{peca.estoque}</td>
                          <td>
                            <div className="action-buttons">
                              <button className="icon-btn edit" onClick={() => handleEdit(peca)}>
                                <Edit2 size={18} />
                              </button>
                              <button className="icon-btn delete" onClick={() => handleDelete(peca.id)}>
                                <Trash2 size={18} />
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>

              {showModal && (
                <PecaModal
                  peca={editingPeca}
                  onClose={() => setShowModal(false)}
                  onSave={handleSave}
                />
              )}
            </div>
          );
        };

        // Componente Modal para Adicionar/Editar Peça
        const PecaModal = ({ peca, onClose, onSave }) => {
          const [formData, setFormData] = useState({
            nome: '',
            descricao: '',
            marca: '',
            modeloVeiculo: '',
            ano: new Date().getFullYear(),
            categoria: 'MOTOR',
            preco: 0,
            estoque: 1,
            estado: 'USADO'
          });

          useEffect(() => {
            if (peca) {
              setFormData(peca);
            }
          }, [peca]);

          const handleChange = (e) => {
            const { name, value } = e.target;
            setFormData(prev => ({
              ...prev,
              [name]: name === 'preco' || name === 'estoque' || name === 'ano' ? Number(value) : value
            }));
          };

          const handleSubmit = (e) => {
            e.preventDefault();
            onSave(formData);
          };

          return (
            <div className="modal-overlay">
              <div className="modal-content">
                <div className="modal-header">
                  <h2>{peca ? 'Editar Peça' : 'Nova Peça'}</h2>
                  <button className="close-btn" onClick={onClose}><X size={24} /></button>
                </div>
                <form onSubmit={handleSubmit}>
                  <div className="form-grid">
                    <div className="form-group">
                      <label>Nome da Peça</label>
                      <input name="nome" value={formData.nome} onChange={handleChange} required />
                    </div>
                    <div className="form-group">
                      <label>Categoria</label>
                      <select name="categoria" value={formData.categoria} onChange={handleChange}>
                        <option value="MOTOR">Motor</option>
                        <option value="SUSPENSAO">Suspensão</option>
                        <option value="FREIOS">Freios</option>
                        <option value="ELETRICA">Elétrica</option>
                        <option value="CARROCERIA">Carroceria</option>
                        <option value="TRANSMISSAO">Transmissão</option>
                      </select>
                    </div>
                    <div className="form-group">
                      <label>Marca da Peça</label>
                      <input name="marca" value={formData.marca} onChange={handleChange} required />
                    </div>
                    <div className="form-group">
                      <label>Modelo do Veículo</label>
                      <input name="modeloVeiculo" value={formData.modeloVeiculo} onChange={handleChange} required />
                    </div>
                    <div className="form-group">
                      <label>Ano do Veículo</label>
                      <input type="number" name="ano" value={formData.ano} onChange={handleChange} required />
                    </div>
                    <div className="form-group">
                      <label>Estado</label>
                      <select name="estado" value={formData.estado} onChange={handleChange}>
                        <option value="NOVO">Novo</option>
                        <option value="USADO">Usado</option>
                        <option value="REMANUFATURADO">Remanufaturado</option>
                      </select>
                    </div>
                    <div className="form-group">
                      <label>Preço (R$)</label>
                      <input type="number" step="0.01" name="preco" value={formData.preco} onChange={handleChange} required />
                    </div>
                    <div className="form-group">
                      <label>Estoque</label>
                      <input type="number" name="estoque" value={formData.estoque} onChange={handleChange} required />
                    </div>
                    <div className="form-group full-width">
                      <label>Descrição</label>
                      <textarea name="descricao" value={formData.descricao} onChange={handleChange} rows="3"></textarea>
                    </div>
                  </div>
                  <div className="modal-actions">
                    <button type="button" className="cancel-btn" onClick={onClose}>Cancelar</button>
                    <button type="submit" className="save-btn">Salvar</button>
                  </div>
                </form>
              </div>
            </div>
          );
        };

        // Admin Page (Placeholder Simples)
        const AdminPage = () => {
          return (
            <div className="container">
              <h1>Painel Administrativo</h1>
              <p>Área restrita para gerenciamento do sistema.</p>
              <div className="stats-grid">
                <div className="stat-card"><h3>Usuários</h3><p>Loading...</p></div>
                <div className="stat-card"><h3>Vendas</h3><p>Loading...</p></div>
              </div>
            </div>
          );
        };

        // Product Detail Page
        const ProductDetailPage = () => {
          // Nota: Em uma app real, usaríamos URL params (ex: react-router-dom useParams)
          // Como estamos usando state simples, vamos simular pegando do localStorage ou state global
          // Para este exemplo, vou mostrar uma mensagem genérica ou voltar para produtos
          const { setCurrentPage, addToCart } = useApp();

          return (
            <div className="product-detail-page">
              <div className="container">
                <button className="back-btn" onClick={() => setCurrentPage('products')}>
                  ← Voltar para peças
                </button>
                <div className="detail-content">
                   {/* Placeholder de detalhes - Idealmente receberia o ID do produto */}
                   <div className="detail-grid">
                     <div className="detail-image">
                       <div className="image-placeholder-large">🔧</div>
                     </div>
                     <div className="detail-info">
                       <h1>Detalhes da Peça</h1>
                       <p className="detail-desc">Selecione uma peça na lista para ver os detalhes completos.</p>
                       <button className="cta-btn" onClick={() => setCurrentPage('products')}>
                         Ver Catálogo
                       </button>
                     </div>
                   </div>
                </div>
              </div>
            </div>
          );
        };

        // Footer Component
        const Footer = () => {
          return (
            <footer className="footer">
              <div className="container">
                <div className="footer-content">
                  <div className="footer-section">
                    <div className="logo-white">
                      <Package size={24} /> <span>Metal-SC</span>
                    </div>
                    <p>Sua fonte confiável de peças automotivas usadas em Santa Catarina.</p>
                  </div>

                  <div className="footer-section">
                    <h4>Links Rápidos</h4>
                    <ul>
                      <li><a href="#">Início</a></li>
                      <li><a href="#">Peças</a></li>
                      <li><a href="#">Sobre Nós</a></li>
                      <li><a href="#">Contato</a></li>
                    </ul>
                  </div>

                  <div className="footer-section">
                    <h4>Contato</h4>
                    <ul>
                      <li><MapPin size={16} /> Florianópolis, SC</li>
                      <li><Phone size={16} /> (48) 99999-9999</li>
                      <li><Mail size={16} /> contato@metalsc.com.br</li>
                    </ul>
                  </div>

                  <div className="footer-section">
                    <h4>Redes Sociais</h4>
                    <div className="social-icons">
                       {/* Ícones placeholder */}
                       <span>Instagram</span>
                       <span>Facebook</span>
                    </div>
                  </div>
                </div>
                <div className="footer-bottom">
                  <p>&copy; 2024 Metal-SC Comércio de Peças. Todos os direitos reservados.</p>
                </div>
              </div>
            </footer>
          );
        };

        export default App;