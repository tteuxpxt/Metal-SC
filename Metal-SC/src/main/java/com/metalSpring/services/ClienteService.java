package com.metalSpring.services;

import com.metalSpring.model.entity.Cliente;
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

        cliente.setSenhaHash(passwordEncoder.encode(cliente.getSenhaHash()));
        cliente.setDataCadastro(LocalDateTime.now());

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente atualizar(String id, Cliente clienteAtualizado) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        cliente.setNome(clienteAtualizado.getNome());
        cliente.setTelefone(clienteAtualizado.getTelefone());
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
}