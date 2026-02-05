package com.metalSpring.services;

import com.metalSpring.model.entity.Cliente;
import com.metalSpring.model.enums.UsuarioTipo;
import com.metalSpring.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Optional<Cliente> buscarPorId(String id) {
        return clienteRepository.findById(id);
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    @Transactional
    public Cliente criar(Cliente cliente) {
        
        if (clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        
        cliente.setTipo(UsuarioTipo.CLIENTE);

        
        if (cliente.getSenhaHash() != null && !cliente.getSenhaHash().isEmpty()) {
            cliente.setSenhaHash(passwordEncoder.encode(cliente.getSenhaHash()));
        } else {
            throw new RuntimeException("Senha é obrigatória");
        }

        
        cliente.setDataCadastro(LocalDateTime.now());
        cliente.setAtivo(true);

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente atualizar(String id, Cliente clienteAtualizado) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        
        if (clienteAtualizado.getNome() != null) {
            cliente.setNome(clienteAtualizado.getNome());
        }
        if (clienteAtualizado.getTelefone() != null) {
            cliente.setTelefone(clienteAtualizado.getTelefone());
        }
        if (clienteAtualizado.getEndereco() != null) {
            cliente.setEndereco(clienteAtualizado.getEndereco());
        }

        return clienteRepository.save(cliente);
    }

    @Transactional
    public void deletar(String id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente não encontrado");
        }
        clienteRepository.deleteById(id);
    }

    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Cliente> buscarClientesMaisAtivos() {
        return clienteRepository.findClientesMaisAtivos();
    }

    public boolean emailExiste(String email) {
        return clienteRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public void alterarSenha(String id, String senhaAtual, String novaSenha) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        
        if (!passwordEncoder.matches(senhaAtual, cliente.getSenhaHash())) {
            throw new RuntimeException("Senha atual incorreta");
        }

        
        cliente.setSenhaHash(passwordEncoder.encode(novaSenha));
        clienteRepository.save(cliente);
    }
}