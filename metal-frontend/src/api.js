const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const getAuthHeader = () => {
  const token = localStorage.getItem('metal_auth');
  return token ? { Authorization: `Basic ${token}` } : {};
};

const request = async (path, options = {}) => {
  const { headers, ...rest } = options;
  const res = await fetch(`${API_URL}${path}`, {
    ...rest,
    headers: {
      'Content-Type': 'application/json',
      ...getAuthHeader(),
      ...(headers || {})
    }
  });

  if (res.status === 204) {
    return null;
  }

  let data = null;
  const contentType = res.headers.get('content-type') || '';
  if (contentType.includes('application/json')) {
    data = await res.json();
  } else {
    data = await res.text();
  }

  if (!res.ok) {
    const message =
      (data && data.error) ||
      (data && data.message) ||
      (typeof data === 'string' && data) ||
      'Erro na requisição';
    throw new Error(message);
  }

  return data;
};

export const fetchPecas = () => request('/pecas');

export const fetchPecasByRevendedor = (revendedorId) =>
  request(`/pecas/revendedor/${revendedorId}`);

export const fetchPedidosByCliente = (clienteId) =>
  request(`/pedidos/cliente/${clienteId}`);

export const fetchPedidosByRevendedor = (revendedorId) =>
  request(`/pedidos/revendedor/${revendedorId}`);

export const confirmarPagamentoPedido = (pedidoId) =>
  request(`/pedidos/${pedidoId}/confirmar-pagamento`, { method: 'POST' });

export const fetchUsuarioPorEmail = (email) =>
  request(`/usuarios/email/${encodeURIComponent(email)}`);

export const loginUsuario = (email, senha) =>
  request('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, senha })
  });

export const registerUsuario = (payload, tipo) => {
  const endpoint = tipo === 'CLIENTE' ? '/clientes' : '/revendedores';
  return request(endpoint, {
    method: 'POST',
    body: JSON.stringify(payload)
  });
};

export const savePeca = (pecaData, revendedorId, pecaId) => {
  const payload = {
    ...pecaData,
    revendedorId
  };
  const method = pecaId ? 'PUT' : 'POST';
  const endpoint = pecaId ? `/pecas/${pecaId}` : '/pecas';
  return request(endpoint, {
    method,
    body: JSON.stringify(payload)
  });
};

export const deletePeca = (pecaId) =>
  request(`/pecas/${pecaId}`, { method: 'DELETE' });

export const createPedido = (pedido) =>
  request('/pedidos', {
    method: 'POST',
    headers: {
      Accept: 'application/json'
    },
    body: JSON.stringify(pedido)
  });

export const uploadPecaImagem = async (pecaId, file) => {
  const formData = new FormData();
  formData.append('file', file);

  const res = await fetch(`${API_URL}/pecas/${pecaId}/imagens/upload`, {
    method: 'POST',
    headers: {
      ...getAuthHeader()
    },
    body: formData
  });

  let data = null;
  const contentType = res.headers.get('content-type') || '';
  if (contentType.includes('application/json')) {
    data = await res.json();
  } else {
    data = await res.text();
  }

  if (!res.ok) {
    const message =
      (data && data.error) ||
      (data && data.message) ||
      (typeof data === 'string' && data) ||
      'Erro ao enviar imagem';
    throw new Error(message);
  }

  return data;
};

export const removePecaImagem = (pecaId, imageUrl) =>
  request(`/pecas/${pecaId}/imagens?url=${encodeURIComponent(imageUrl)}`, {
    method: 'DELETE'
  });

export const fetchAdminDashboard = () => request('/admin/dashboard');

export const fetchAdminUsuarios = () => request('/admin/usuarios');

export const fetchAdminRevendedores = () => request('/admin/revendedores');

export const deleteAdminUsuario = (usuarioId) =>
  request(`/admin/usuarios/${usuarioId}`, { method: 'DELETE' });

export const deleteAdminRevendedor = (revendedorId) =>
  request(`/admin/revendedores/${revendedorId}`, { method: 'DELETE' });

export const baixarTaxasRevendedor = (revendedorId, valor) => {
  const query = valor !== null && valor !== undefined ? `?valor=${encodeURIComponent(valor)}` : '';
  return request(`/admin/revendedores/${revendedorId}/taxas/baixar${query}`, {
    method: 'POST'
  });
};

export const ativarPremiumRevendedor = (revendedorId, dias) => {
  const query = dias ? `?dias=${encodeURIComponent(dias)}` : '';
  return request(`/admin/revendedores/${revendedorId}/premium${query}`, {
    method: 'POST'
  });
};

export const desativarPremiumRevendedor = (revendedorId) =>
  request(`/admin/revendedores/${revendedorId}/premium`, { method: 'DELETE' });
