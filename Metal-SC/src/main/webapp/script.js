 const BASE_URL = 'http://localhost:8080/produtos'; // URL base da API

        // Função para Coletar Dados do Formulário
        function getProdutoFromForm() {
            const nome = document.getElementById('nome').value;
            const preco = document.getElementById('preco').value;
            const quantidade = document.getElementById('quantidade').value;
            const status = document.getElementById('status').value;
            return { nome, preco: parseFloat(preco), quantidade: parseInt(quantidade), status };
        }

        async function listarTodos() {
            const response = await fetch(BASE_URL);
            const produtos = await response.json();
            const lista = document.getElementById('lista-produtos');
            const total = document.getElementById('total-produtos');
            lista.innerHTML = '';
            produtos.forEach(produto => {
                const item = document.createElement('li');
                item.textContent = `ID: ${produto.id}, Nome: ${produto.nome}, Preço: ${produto.preco}, Quantidade: ${produto.quantidade}, Status: ${produto.status}`;
                lista.appendChild(item);
            });
            total.textContent = `Total de produtos: ${produtos.length}`;
        }

        async function salvar() {
            const produto = getProdutoFromForm();
            await fetch(BASE_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(produto)
            });
            listarTodos();
        }

       async function atualizar() {
           const id = document.getElementById('id').value;
           if (!id) {
               alert("Informe o ID do produto para atualizar!");
               return;
           }

           const produto = getProdutoFromForm();

           try {
               const response = await fetch(`${BASE_URL}/${id}`, {
                   method: 'PUT',
                   headers: { 'Content-Type': 'application/json' },
                   body: JSON.stringify(produto)
               });

               if (!response.ok) {
                   const errorText = await response.text();
                   throw new Error(`Erro ao atualizar (${response.status}): ${errorText}`);
               }

               listarTodos();
           } catch (error) {
               console.error("Erro na atualização:", error);
               alert("Falha ao atualizar o produto.");
           }
       }


        async function deletar() {
            const id = document.getElementById('id').value;
            await fetch(`${BASE_URL}/${id}`, { method: 'DELETE' });
            listarTodos();
        }

        function consultar() {
            const id = document.getElementById('id').value;

            fetch(`${BASE_URL}/${id}`)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Erro ao buscar o produto: ' + response.status);
                    }
                    return response.json();
                })
                .then(produto => {
                    document.getElementById('nome').value = produto.nome || '';
                    document.getElementById('preco').value = produto.preco || '';
                    document.getElementById('quantidade').value = produto.quantidade || '';
                    document.getElementById('status').value = produto.status || '';
                })
                .catch(error => {
                    console.error('Erro ao consultar produto:', error);
                    alert('Produto não encontrado!');
                });
        }

        function limparCampos() {
            document.getElementById('id').value = '';
            document.getElementById('nome').value = '';
            document.getElementById('preco').value = '';
            document.getElementById('quantidade').value = '';
            document.getElementById('status').value = '';
        }

        document.addEventListener('DOMContentLoaded', listarTodos);