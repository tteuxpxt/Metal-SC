package com.metalSpring.services;

import com.metalSpring.model.entity.ComentarioPerfil;
import com.metalSpring.model.entity.Usuario;
import com.metalSpring.model.enums.UsuarioTipo;
import com.metalSpring.repository.ComentarioPerfilRepository;
import com.metalSpring.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ComentarioPerfilService {

    @Autowired
    private ComentarioPerfilRepository comentarioPerfilRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<ComentarioPerfil> listarTodos() {
        return comentarioPerfilRepository.findAll();
    }

    public Optional<ComentarioPerfil> buscarPorId(String id) {
        return comentarioPerfilRepository.findById(id);
    }

    public List<ComentarioPerfil> buscarPorAlvo(String alvoId) {
        return comentarioPerfilRepository.findByAlvoIdOrderByDataDesc(alvoId);
    }

    public List<ComentarioPerfil> buscarPorAutor(String autorId) {
        return comentarioPerfilRepository.findByAutorIdOrderByDataDesc(autorId);
    }

    public double calcularMediaPorAlvo(String alvoId) {
        Double media = comentarioPerfilRepository.calcularMediaPorAlvo(alvoId);
        return media == null ? 0.0 : media;
    }

    @Transactional
    public ComentarioPerfil criar(String autorId, String alvoId, Integer nota, String comentario) {
        Usuario autor = usuarioRepository.findById(autorId)
                .orElseThrow(() -> new RuntimeException("Autor nao encontrado"));
        Usuario alvo = usuarioRepository.findById(alvoId)
                .orElseThrow(() -> new RuntimeException("Usuario alvo nao encontrado"));

        validarComentario(autor, alvo, nota, comentario);

        ComentarioPerfil comentarioPerfil = new ComentarioPerfil();
        comentarioPerfil.setAutor(autor);
        comentarioPerfil.setAlvo(alvo);
        comentarioPerfil.setNota(nota);
        comentarioPerfil.setComentario(comentario);
        comentarioPerfil.setData(LocalDateTime.now());

        return comentarioPerfilRepository.save(comentarioPerfil);
    }

    @Transactional
    public ComentarioPerfil editar(String id, Integer nota, String comentarioTexto) {
        ComentarioPerfil comentario = comentarioPerfilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comentario nao encontrado"));

        validarEdicao(comentario, nota, comentarioTexto);

        comentario.editar(nota, comentarioTexto);

        return comentarioPerfilRepository.save(comentario);
    }

    @Transactional
    public void excluir(String id) {
        if (!comentarioPerfilRepository.existsById(id)) {
            throw new RuntimeException("Comentario nao encontrado");
        }
        comentarioPerfilRepository.deleteById(id);
    }

    private void validarComentario(Usuario autor, Usuario alvo, Integer nota, String comentario) {
        if (autor.getId().equals(alvo.getId())) {
            throw new RuntimeException("Nao e permitido comentar o proprio perfil");
        }

        boolean comentarioVazio = comentario == null || comentario.trim().isEmpty();
        if (comentarioVazio && nota == null) {
            throw new RuntimeException("Comentario ou nota sao obrigatorios");
        }

        UsuarioTipo tipoAutor = autor.getTipo();
        UsuarioTipo tipoAlvo = alvo.getTipo();

        if (tipoAutor == UsuarioTipo.CLIENTE && tipoAlvo == UsuarioTipo.REVENDEDOR) {
            if (nota == null) {
                throw new RuntimeException("Nota e obrigatoria para avaliar revendedor");
            }
            if (nota < 1 || nota > 5) {
                throw new RuntimeException("Nota deve estar entre 1 e 5");
            }
            return;
        }

        if (tipoAutor == UsuarioTipo.REVENDEDOR && tipoAlvo == UsuarioTipo.CLIENTE) {
            if (nota != null) {
                throw new RuntimeException("Revendedor nao pode atribuir nota ao cliente");
            }
            return;
        }

        throw new RuntimeException("Tipo de usuario invalido para comentario de perfil");
    }

    private void validarEdicao(ComentarioPerfil comentario, Integer nota, String novoComentario) {
        Usuario autor = comentario.getAutor();
        Usuario alvo = comentario.getAlvo();
        UsuarioTipo tipoAutor = autor.getTipo();
        UsuarioTipo tipoAlvo = alvo.getTipo();

        if (tipoAutor == UsuarioTipo.CLIENTE && tipoAlvo == UsuarioTipo.REVENDEDOR) {
            if (nota != null && (nota < 1 || nota > 5)) {
                throw new RuntimeException("Nota deve estar entre 1 e 5");
            }
        } else if (tipoAutor == UsuarioTipo.REVENDEDOR && tipoAlvo == UsuarioTipo.CLIENTE) {
            if (nota != null) {
                throw new RuntimeException("Revendedor nao pode atribuir nota ao cliente");
            }
        } else {
            throw new RuntimeException("Tipo de usuario invalido para comentario de perfil");
        }

        boolean comentarioVazio = novoComentario == null || novoComentario.trim().isEmpty();
        if (comentarioVazio && nota == null) {
            throw new RuntimeException("Comentario ou nota sao obrigatorios");
        }
    }
}
