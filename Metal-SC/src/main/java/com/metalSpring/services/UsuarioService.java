package com.metalSpring.services;

import com.metalSpring.model.entity.Usuario;
import com.metalSpring.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(String id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public List<Usuario> buscarPorNome(String nome) {
        return usuarioRepository.findByNomeContainingIgnoreCase(nome);
    }

    @Transactional
    public Usuario criar(Usuario usuario) {
        // Validação de email duplicado
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Email já cadastrado");
        }

        // CORREÇÃO: Criptografar senha ANTES de salvar
        if (usuario.getSenhaHash() != null && !usuario.getSenhaHash().isEmpty()) {
            usuario.setSenhaHash(passwordEncoder.encode(usuario.getSenhaHash()));
        } else {
            throw new RuntimeException("Senha é obrigatória");
        }

        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setAtivo(true); // Define como ativo por padrão

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario atualizar(String id, Usuario usuarioAtualizado) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);

        if (usuarioExistente.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        Usuario usuario = usuarioExistente.get();
        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setTelefone(usuarioAtualizado.getTelefone());
        usuario.setEndereco(usuarioAtualizado.getEndereco());

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void alterarSenha(String id, String senhaAtual, String novaSenha) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        // CORREÇÃO: Validar senha usando PasswordEncoder
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenhaHash())) {
            throw new RuntimeException("Senha atual incorreta");
        }

        // Criptografar nova senha
        usuario.setSenhaHash(passwordEncoder.encode(novaSenha));
        usuarioRepository.save(usuario);
    }

    public boolean validarSenha(String id, String senha) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);

        if (usuario.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        // CORREÇÃO: Usar PasswordEncoder para validar
        return passwordEncoder.matches(senha, usuario.get().getSenhaHash());
    }

    @Transactional
    public void deletar(String id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    public boolean emailExiste(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public Usuario atualizarEndereco(String id, com.metalSpring.model.embeddable.Endereco endereco) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        usuario.setEndereco(endereco);

        return usuarioRepository.save(usuario);
    }
}