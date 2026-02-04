const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const request = async (path, options = {}) => {
  const res = await fetch(`${API_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers || {})
    },
    ...options
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

export const fetchUsuarioPorEmail = (email) =>
  request(`/usuarios/email/${encodeURIComponent(email)}`);

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
    body: JSON.stringify(pedido)
  });

export const uploadPecaImagem = async (pecaId, file) => {
  const formData = new FormData();
  formData.append('file', file);

  const res = await fetch(`${API_URL}/pecas/${pecaId}/imagens/upload`, {
    method: 'POST',
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
