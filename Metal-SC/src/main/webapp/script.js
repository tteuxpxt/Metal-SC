// script.js - frontend for Metal-SC
const API_URL = 'http://localhost:8080/api';
let currentUser = null;
let cart = [];
let allProducts = [];

/* Utility functions */
function showPage(pageId) {
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    const target = document.getElementById(pageId);
    if (target) target.classList.add('active');

    // small UX: scroll to top
    window.scrollTo(0,0);

    // load dynamic content per page
    if (pageId === 'produtos') loadProductsIfNeeded();
    if (pageId === 'dashboard') loadDashboard();
    if (pageId === 'carrinho') renderCart();
}

/* -------------------- ADMIN (simples) -------------------- */
async function loadAdminDashboard() {
    if (!currentUser || (currentUser.tipo !== 'ADMINISTRADOR' && currentUser.tipo !== 'ADMIN')) {
        console.warn('Acesso admin negado');
        return;
    }
    try {
        const [uRes, pRes, pedRes] = await Promise.all([
            fetch(`${API_URL}/administrador/usuarios`),
            fetch(`${API_URL}/pecas`),
            fetch(`${API_URL}/pedidos`)
        ]);
        const usuarios = uRes.ok ? await uRes.json() : [];
        const pecas = pRes.ok ? await pRes.json() : [];
        const pedidos = pedRes.ok ? await pedRes.json() : [];
        document.getElementById('adminTotalUsers').textContent = usuarios.length;
        document.getElementById('adminTotalPecas').textContent = pecas.length;
        document.getElementById('adminTotalPedidos').textContent = pedidos.length;

        fillAdminUsersTable(usuarios);
        fillAdminPecasTable(pecas);
        await loadAdminRevendedores();
        fillAdminPedidosTable(pedidos);
    } catch (err) {
        console.error('Erro ao carregar admin dashboard', err);
    }
}

function fillAdminUsersTable(users) {
    const tbody = document.querySelector('#adminUsersTable tbody');
    tbody.innerHTML = '';
    users.forEach(u => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${u.id}</td><td>${escapeHtml(u.nome)}</td><td>${escapeHtml(u.email)}</td><td>${u.tipo || ''}</td>
            <td>
                <button class="btn btn-danger" onclick="bloquearUsuario('${u.id}')">Bloquear</button>
            </td>`;
        tbody.appendChild(tr);
    });
}

function fillAdminPecasTable(pecas) {
    const tbody = document.querySelector('#adminPecasTable tbody');
    tbody.innerHTML = '';
    pecas.forEach(p => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${p.id}</td><td>${escapeHtml(p.nome)}</td><td>${escapeHtml(p.categoria)}</td><td>R$ ${Number(p.preco).toFixed(2)}</td>
            <td>
                <button class="btn btn-danger" onclick="removerPecaAdmin('${p.id}')">Remover</button>
            </td>`;
        tbody.appendChild(tr);
    });
}

async function loadAdminRevendedores() {
    try {
        const res = await fetch(`${API_URL}/administrador/usuarios`);
        if (!res.ok) return;
        const users = await res.json();
        const revs = users.filter(u => u.tipo === 'REVENDEDOR' && !u.aprovado);
        const tbody = document.querySelector('#adminRevendedoresTable tbody');
        tbody.innerHTML = '';
        revs.forEach(r => {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td>${r.id}</td><td>${escapeHtml(r.nomeLoja || r.nome)}</td><td>${escapeHtml(r.cnpj || '')}</td><td>${escapeHtml(r.email)}</td>
                <td>
                    <button class="btn btn-primary" onclick="aprovarRevendedor('${r.id}')">Aprovar</button>
                </td>`;
            tbody.appendChild(tr);
        });
    } catch (err) {
        console.error(err);
    }
}

function fillAdminPedidosTable(pedidos) {
    const tbody = document.querySelector('#adminPedidosTable tbody');
    tbody.innerHTML = '';
    pedidos.forEach(p => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${p.id}</td><td>${escapeHtml((p.cliente && p.cliente.nome) || p.clienteId || '')}</td><td>R$ ${Number(p.valorTotal).toFixed(2)}</td><td>${p.status || ''}</td>`;
        tbody.appendChild(tr);
    });
}

async function bloquearUsuario(id) {
    if (!confirm('Bloquear este usuário?')) return;
    try {
        const res = await fetch(`${API_URL}/administrador/bloquear/${id}`, { method: 'PUT' });
        if (!res.ok) { alert('Erro ao bloquear'); return; }
        alert('Usuário bloqueado');
        await loadAdminDashboard();
    } catch (err) { console.error(err); alert('Erro'); }
}

async function aprovarRevendedor(id) {
    if (!confirm('Aprovar este revendedor?')) return;
    try {
        const res = await fetch(`${API_URL}/administrador/aprovarRevendedor/${id}`, { method: 'PUT' });
        if (!res.ok) { alert('Erro ao aprovar'); return; }
        alert('Revendedor aprovado');
        await loadAdminDashboard();
    } catch (err) { console.error(err); alert('Erro'); }
}

async function removerPecaAdmin(id) {
    if (!confirm('Remover esta peça permanentemente?')) return;
    try {
        const res = await fetch(`${API_URL}/administrador/removerPeca/${id}`, { method: 'DELETE' });
        if (!res.ok) { alert('Erro ao remover peça'); return; }
        alert('Peça removida');
        await loadAdminDashboard();
        await loadProducts();
    } catch (err) { console.error(err); alert('Erro'); }
}

/* show/hide admin menu depending on role */
function toggleAdminMenu() {
    const btn = document.getElementById('adminBtn');
    if (!btn) return;
    if (currentUser && (currentUser.tipo === 'ADMINISTRADOR' || currentUser.tipo === 'ADMIN')) {
        btn.style.display = 'inline-block';
        document.getElementById('cartBtn').style.display = 'none';
    } else {
        btn.style.display = 'none';
        document.getElementById('cartBtn').style.display = 'inline-block';
    }
}


function showAlert(containerId, message, type='success') {
    const container = document.getElementById(containerId);
    if (!container) return;
    container.innerHTML = `<div class="alert ${type==='success' ? 'alert-success' : 'alert-error'}">${message}</div>`;
    setTimeout(()=>{ if (container) container.innerHTML = ''; }, 4000);
}

function checkAuth() {
    const user = localStorage.getItem('user');
    if (user) {
        currentUser = JSON.parse(user);
        document.getElementById('loginBtn').style.display = 'none';
        document.getElementById('userMenu').style.display = 'inline-block';
        document.getElementById('logoutBtn').style.display = 'inline-block';
    } else {
        currentUser = null;
        document.getElementById('loginBtn').style.display = 'inline-block';
        document.getElementById('userMenu').style.display = 'none';
        document.getElementById('logoutBtn').style.display = 'none';
    }
    toggleAdminMenu(); // <--- add this
}


function toggleRevendedorFields() {
    const t = document.getElementById('userType').value;
    const show = t === 'REVENDEDOR';
    document.getElementById('cnpjField').style.display = show ? 'block' : 'none';
    document.getElementById('lojaField').style.display = show ? 'block' : 'none';
}

/* Auth */
function checkAuth() {
    const user = localStorage.getItem('user');
    if (user) {
        currentUser = JSON.parse(user);
        document.getElementById('loginBtn').style.display = 'none';
        document.getElementById('userMenu').style.display = 'inline-block';
        document.getElementById('logoutBtn').style.display = 'inline-block';
    } else {
        currentUser = null;
        document.getElementById('loginBtn').style.display = 'inline-block';
        document.getElementById('userMenu').style.display = 'none';
        document.getElementById('logoutBtn').style.display = 'none';
    }
}

async function login(e) {
    e.preventDefault();
    document.getElementById('loginAlert').innerHTML = '';
    const email = document.getElementById('loginEmail').value;
    const senha = document.getElementById('loginPassword').value;
    try {
        const res = await fetch(`${API_URL}/usuarios/login`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({email, senha})
        });
        if (!res.ok) { showAlert('loginAlert','Email ou senha inválidos','error'); return; }
        const user = await res.json();
        localStorage.setItem('user', JSON.stringify(user));
        currentUser = user;
        showAlert('loginAlert','Login realizado!','success');
        checkAuth();
        setTimeout(()=> showPage('home'), 800);
    } catch (err) {
        console.error(err);
        showAlert('loginAlert','Erro ao efetuar login','error');
    }
}

async function register(e) {
    e.preventDefault();
    document.getElementById('registerAlert').innerHTML = '';
    const tipo = document.getElementById('userType').value;
    const payload = {
        nome: document.getElementById('registerName').value,
        email: document.getElementById('registerEmail').value,
        senha: document.getElementById('registerPassword').value,
        telefone: document.getElementById('registerPhone').value,
        tipo: tipo,
        endereco: {rua:'',numero:'',cidade:'',estado:'',cep:''}
    };
    if (tipo === 'REVENDEDOR') {
        payload.cnpj = document.getElementById('registerCnpj').value;
        payload.nomeLoja = document.getElementById('registerLoja').value;
    }
    try {
        const res = await fetch(`${API_URL}/usuarios`, {
            method: 'POST',
            headers: {'Content-Type':'application/json'},
            body: JSON.stringify(payload)
        });
        if (!res.ok) { showAlert('registerAlert','Erro ao cadastrar','error'); return; }
        showAlert('registerAlert','Cadastro realizado! Faça login.','success');
        setTimeout(()=> showPage('login'), 1000);
    } catch (err) {
        console.error(err);
        showAlert('registerAlert','Erro ao cadastrar','error');
    }
}

function logout() {
    localStorage.removeItem('user');
    currentUser = null;
    checkAuth();
    showPage('home');
}

/* Products */
async function loadProducts() {
    try {
        const res = await fetch(`${API_URL}/pecas`);
        if (!res.ok) { console.error('Erro ao buscar pecas'); return; }
        allProducts = await res.json();
        displayProducts(allProducts);
        populateFilters();
    } catch (err) {
        console.error(err);
    }
}

let productsLoaded = false;
function loadProductsIfNeeded() {
    if (!productsLoaded) {
        loadProducts();
        productsLoaded = true;
    } else {
        displayProducts(allProducts);
    }
}

function displayProducts(products) {
    const grid = document.getElementById('productsGrid');
    grid.innerHTML = '';
    products.forEach(p => {
        const card = document.createElement('div');
        card.className = 'product-card';
        card.innerHTML = `
            <div class="product-img">🔧</div>
            <div class="product-info">
                <h3>${escapeHtml(p.nome)}</h3>
                <p class="product-meta">${escapeHtml(p.marca)} ${escapeHtml(p.modeloVeiculo)} (${p.ano || '-'})</p>
                <p class="product-meta">${escapeHtml(p.categoria)}</p>
                <p class="product-price">R$ ${Number(p.preco).toFixed(2)}</p>
                <span class="badge badge-success">${escapeHtml(p.estado || '')}</span>
                <p class="product-meta" style="margin-top: 0.5rem;">Estoque: ${p.estoque || 0}</p>
                <button class="btn btn-primary" style="width: 100%; margin-top: 1rem;">Adicionar ao Carrinho</button>
            </div>
        `;
        const btn = card.querySelector('button');
        btn.addEventListener('click', ()=> addToCart(p.id));
        grid.appendChild(card);
    });
}

function escapeHtml(s) {
    if (s === null || s === undefined) return '';
    return String(s).replaceAll('&','&amp;').replaceAll('<','&lt;').replaceAll('>','&gt;');
}

function populateFilters() {
    const brands = Array.from(new Set(allProducts.map(p => p.marca).filter(Boolean)));
    const brandFilter = document.getElementById('brandFilter');
    brandFilter.innerHTML = '<option value="">Todas as marcas</option>';
    brands.forEach(b => {
        const opt = document.createElement('option'); opt.value = b; opt.textContent = b;
        brandFilter.appendChild(opt);
    });
}

function searchProducts() {
    const q = document.getElementById('searchInput').value.toLowerCase();
    const category = document.getElementById('categoryFilter').value;
    const brand = document.getElementById('brandFilter').value;
    const filtered = allProducts.filter(p => {
        const matchesSearch = !q || (p.nome && p.nome.toLowerCase().includes(q)) || (p.descricao && p.descricao.toLowerCase().includes(q));
        const matchesCategory = !category || p.categoria === category;
        const matchesBrand = !brand || p.marca === brand;
        return matchesSearch && matchesCategory && matchesBrand;
    });
    displayProducts(filtered);
}

/* Cart */
function addToCart(productId) {
    const product = allProducts.find(p => p.id === productId);
    if (!product) { alert('Produto não encontrado'); return; }
    const existing = cart.find(i => i.id === productId);
    if (existing) existing.quantidade += 1;
    else cart.push({...product, quantidade: 1});
    persistCart();
    updateCartUI();
}

function persistCart() {
    localStorage.setItem('msc_cart', JSON.stringify(cart));
}

function loadCart() {
    cart = JSON.parse(localStorage.getItem('msc_cart') || '[]');
}

function updateCartUI() {
    loadCart();
    document.getElementById('cartCount').textContent = cart.reduce((s,i)=> s + i.quantidade, 0);
    renderCart();
}

function renderCart() {
    loadCart();
    const container = document.getElementById('cartItems');
    if (!container) return;
    if (cart.length === 0) {
        container.innerHTML = '<p>Carrinho vazio</p>';
        document.getElementById('cartSubtotal').textContent = 'R$ 0,00';
        document.getElementById('cartTotal').textContent = 'R$ 0,00';
        return;
    }
    container.innerHTML = '';
    let subtotal = 0;
    cart.forEach(item => {
        subtotal += Number(item.preco) * item.quantidade;
        const div = document.createElement('div');
        div.className = 'cart-item';
        div.innerHTML = `
            <div class="product-img" style="width:100px;height:100px;flex-shrink:0;">🔧</div>
            <div style="flex:1;">
                <h3>${escapeHtml(item.nome)}</h3>
                <p>${escapeHtml(item.marca)} ${escapeHtml(item.modeloVeiculo)} • ${escapeHtml(item.categoria)}</p>
                <p>R$ ${Number(item.preco).toFixed(2)} x ${item.quantidade} = R$ ${(Number(item.preco)*item.quantidade).toFixed(2)}</p>
                <div style="display:flex; gap:8px; margin-top:8px;">
                    <button class="btn" onclick="changeQty('${item.id}', -1)">-</button>
                    <button class="btn" onclick="changeQty('${item.id}', 1)">+</button>
                    <button class="btn btn-danger" onclick="removeFromCart('${item.id}')">Remover</button>
                </div>
            </div>
        `;
        container.appendChild(div);
    });
    document.getElementById('cartSubtotal').textContent = 'R$ ' + subtotal.toFixed(2);
    // For now no extra fees
    document.getElementById('cartTotal').textContent = 'R$ ' + subtotal.toFixed(2);
}

function changeQty(productId, delta) {
    const idx = cart.findIndex(i => i.id === productId);
    if (idx === -1) return;
    cart[idx].quantidade += delta;
    if (cart[idx].quantidade <= 0) cart.splice(idx,1);
    persistCart();
    updateCartUI();
}

function removeFromCart(productId) {
    cart = cart.filter(i => i.id !== productId);
    persistCart();
    updateCartUI();
}

/* Checkout */
async function checkout() {
    if (!currentUser) { alert('Faça login para finalizar a compra'); showPage('login'); return; }
    if (cart.length === 0) { alert('Carrinho vazio'); return; }
    const itens = cart.map(i => ({pecaId: i.id, quantidade: i.quantidade, precoUnitario: i.preco}));
    const pedido = {
        clienteId: currentUser.id,
        itens: itens,
        valorTotal: itens.reduce((s,it)=> s + it.precoUnitario*it.quantidade,0),
        enderecoEntrega: {rua:'Rua Exemplo',numero:'s/n',cidade:'Cidade',estado:'Estado',cep:''}
    };
    try {
        const res = await fetch(`${API_URL}/pedidos`, {
            method: 'POST',
            headers: {'Content-Type':'application/json'},
            body: JSON.stringify(pedido)
        });
        if (!res.ok) { alert('Erro ao criar pedido'); return; }
        const data = await res.json();
        alert('Pedido criado com sucesso! ID: ' + (data.id || '---'));
        cart = [];
        persistCart();
        updateCartUI();
        showPage('home');
    } catch (err) {
        console.error(err);
        alert('Erro ao finalizar pedido');
    }
}

/* Revendedor flows */
function showAddPecaModal() {
    if (!currentUser || currentUser.tipo !== 'REVENDEDOR') { alert('Faça login como revendedor'); return; }
    document.getElementById('addPecaModal').classList.add('active');
}
function closeModal(id) {
    document.getElementById(id).classList.remove('active');
}

async function addPeca(e) {
    e.preventDefault();
    const payload = {
        nome: document.getElementById('pecaNome').value,
        descricao: document.getElementById('pecaDescricao').value,
        categoria: document.getElementById('pecaCategoria').value,
        preco: Number(document.getElementById('pecaPreço').value),
        marca: document.getElementById('pecaMarca').value,
        modeloVeiculo: document.getElementById('pecaModelo').value,
        ano: Number(document.getElementById('pecaAno').value),
        estoque: Number(document.getElementById('pecaEstoque').value),
        estado: document.getElementById('pecaEstado').value
    };
    try {
        // simplified route: POST /revendedores/{id}/pecas if revendedor, else /pecas
        const url = currentUser && currentUser.tipo === 'REVENDEDOR' ? `${API_URL}/revendedores/${currentUser.id}/pecas` : `${API_URL}/pecas`;
        const res = await fetch(url, {
            method: 'POST',
            headers: {'Content-Type':'application/json'},
            body: JSON.stringify(payload)
        });
        if (!res.ok) { alert('Erro ao salvar peça'); return; }
        closeModal('addPecaModal');
        await loadProducts();
        alert('Peça adicionada');
    } catch (err) {
        console.error(err);
        alert('Erro ao adicionar peça');
    }
}

async function loadDashboard() {
    checkAuth();
    // Hide both dashboards
    document.getElementById('clientDashboard').style.display = 'none';
    document.getElementById('revendedorDashboard').style.display = 'none';
    if (!currentUser) { showAlert('loginAlert','Faça login para ver o painel','error'); showPage('login'); return; }

    if (currentUser.tipo === 'CLIENTE') {
        document.getElementById('clientDashboard').style.display = 'block';
        // load orders
        try {
            const res = await fetch(`${API_URL}/pedidos/cliente/${currentUser.id}`);
            if (res.ok) {
                const pedidos = await res.json();
                const tbody = document.querySelector('#ordersTable tbody');
                tbody.innerHTML = '';
                pedidos.forEach(p => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `<td>${p.id}</td><td>${new Date(p.dataCriacao || p.data).toLocaleString()}</td><td>R$ ${Number(p.valorTotal).toFixed(2)}</td><td>${p.status || ''}</td>`;
                    tbody.appendChild(tr);
                });
            }
        } catch (err) { console.error(err); }
    } else if (currentUser.tipo === 'REVENDEDOR') {
        document.getElementById('revendedorDashboard').style.display = 'block';
        // load revendedor pieces
        try {
            const res = await fetch(`${API_URL}/revendedores/${currentUser.id}/pecas`);
            if (res.ok) {
                const pecas = await res.json();
                document.getElementById('totalPecas').textContent = pecas.length;
                const tbody = document.querySelector('#pecasTable tbody');
                tbody.innerHTML = '';
                pecas.forEach(p => {
                    const tr = document.createElement('tr');
                    tr.innerHTML = `<td>${escapeHtml(p.nome)}</td><td>${escapeHtml(p.categoria)}</td><td>R$ ${Number(p.preco).toFixed(2)}</td><td>${p.estoque}</td>
                    <td>
                        <button onclick="editPeca('${p.id}')" class="btn btn-secondary">Editar</button>
                        <button onclick="deletePeca('${p.id}')" class="btn btn-danger">Excluir</button>
                    </td>`;
                    tbody.appendChild(tr);
                });
            }
        } catch (err) { console.error(err); }
    } else {
        // unknown type: try to show counts
        document.getElementById('clientDashboard').style.display = 'block';
    }
}

async function editPeca(id) {
    try {
        const res = await fetch(`${API_URL}/pecas/${id}`);
        if (!res.ok) { alert('Erro ao buscar peça'); return; }
        const p = await res.json();
        // populate modal fields then open
        document.getElementById('pecaNome').value = p.nome || '';
        document.getElementById('pecaDescricao').value = p.descricao || '';
        document.getElementById('pecaCategoria').value = p.categoria || '';
        document.getElementById('pecaPreço').value = p.preco || '';
        document.getElementById('pecaMarca').value = p.marca || '';
        document.getElementById('pecaModelo').value = p.modeloVeiculo || '';
        document.getElementById('pecaAno').value = p.ano || '';
        document.getElementById('pecaEstoque').value = p.estoque || '';
        document.getElementById('pecaEstado').value = p.estado || 'USADO_BOM';
        // For simplicity this reuses add modal; backend update route not implemented here
        showAddPecaModal();
    } catch (err) { console.error(err); }
}

async function deletePeca(id) {
    if (!confirm('Deseja excluir essa peça?')) return;
    try {
        const res = await fetch(`${API_URL}/pecas/${id}`, { method: 'DELETE' });
        if (!res.ok) { alert('Erro ao excluir'); return; }
        alert('Peça removida');
        await loadProducts();
        await loadDashboard();
    } catch (err) { console.error(err); }
}

/* Init on load */
document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    loadCart();
    updateCartUI();
    loadProducts();
});
