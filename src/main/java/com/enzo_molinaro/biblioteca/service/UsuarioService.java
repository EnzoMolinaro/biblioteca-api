package com.enzo_molinaro.biblioteca.service;

import com.enzo_molinaro.biblioteca.dto.request.UsuarioRequestDTO;
import com.enzo_molinaro.biblioteca.dto.response.UsuarioResponseDTO;
import com.enzo_molinaro.biblioteca.entity.Usuario;
import com.enzo_molinaro.biblioteca.enums.TipoUsuario;
import com.enzo_molinaro.biblioteca.exception.BusinessException;
import com.enzo_molinaro.biblioteca.exception.ResourceNotFoundException;
import com.enzo_molinaro.biblioteca.mapper.UsuarioMapper;
import com.enzo_molinaro.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        log.info("Criando usuário com email: {}", dto.getEmail());

        validarUsuarioUnico(dto.getEmail(), dto.getCpf(), null);

        Usuario usuario = usuarioMapper.toEntity(dto);
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        log.info("Usuário criado com sucesso. ID: {}", usuarioSalvo.getId());
        return usuarioMapper.toResponseDTO(usuarioSalvo);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        log.debug("Buscando usuário por ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));

        return usuarioMapper.toResponseDTO(usuario);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listarTodos(Pageable pageable) {
        log.debug("Listando todos os usuários ativos");
        return usuarioRepository.findByAtivoTrue(pageable)
                .map(usuarioMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> buscar(String termo, Pageable pageable) {
        log.debug("Buscando usuários por termo: {}", termo);
        return usuarioRepository.buscarPorTermo(termo, pageable)
                .map(usuarioMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarPorTipo(TipoUsuario tipo) {
        log.debug("Listando usuários do tipo: {}", tipo);
        return usuarioMapper.toResponseDTOList(
                usuarioRepository.findByTipoAndAtivoTrue(tipo)
        );
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioRequestDTO dto) {
        log.info("Atualizando usuário ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        validarUsuarioUnico(dto.getEmail(), dto.getCpf(), id);

        usuarioMapper.updateEntityFromDTO(dto, usuario);
        Usuario usuarioAtualizado = usuarioRepository.save(usuario);

        log.info("Usuário atualizado com sucesso. ID: {}", id);
        return usuarioMapper.toResponseDTO(usuarioAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando (desativando) usuário ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        long emprestimosAtivos = usuario.getEmprestimosAtivos();
        if (emprestimosAtivos > 0) {
            throw new BusinessException(
                    "Não é possível desativar usuário com empréstimos ativos. " +
                            "Empréstimos pendentes: " + emprestimosAtivos
            );
        }

        usuario.setAtivo(false);
        usuarioRepository.save(usuario);

        log.info("Usuário desativado com sucesso. ID: {}", id);
    }

    @Transactional(readOnly = true)
    public boolean podeEmprestar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return usuario.podeEmprestar();
    }

    private void validarUsuarioUnico(String email, String cpf, Long idExcluir) {
        if (usuarioRepository.existsByEmail(email)) {
            Usuario existente = usuarioRepository.findByEmail(email).orElseThrow();
            if (idExcluir == null || !existente.getId().equals(idExcluir)) {
                throw new BusinessException("Já existe um usuário cadastrado com o email: " + email);
            }
        }

        if (usuarioRepository.existsByCpf(cpf)) {
            Usuario existente = usuarioRepository.findByCpf(cpf).orElseThrow();
            if (idExcluir == null || !existente.getId().equals(idExcluir)) {
                throw new BusinessException("Já existe um usuário cadastrado com o CPF: " + cpf);
            }
        }
    }
}