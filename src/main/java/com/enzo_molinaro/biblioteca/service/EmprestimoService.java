package com.enzo_molinaro.biblioteca.service;

import com.enzo_molinaro.biblioteca.dto.request.EmprestimoRequestDTO;
import com.enzo_molinaro.biblioteca.dto.response.EmprestimoResponseDTO;
import com.enzo_molinaro.biblioteca.entity.Emprestimo;
import com.enzo_molinaro.biblioteca.entity.Livro;
import com.enzo_molinaro.biblioteca.entity.Usuario;
import com.enzo_molinaro.biblioteca.enums.StatusEmprestimo;
import com.enzo_molinaro.biblioteca.exception.BusinessException;
import com.enzo_molinaro.biblioteca.exception.ResourceNotFoundException;
import com.enzo_molinaro.biblioteca.mapper.EmprestimoMapper;
import com.enzo_molinaro.biblioteca.repository.EmprestimoRepository;
import com.enzo_molinaro.biblioteca.repository.LivroRepository;
import com.enzo_molinaro.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmprestimoService {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmprestimoMapper emprestimoMapper;

    @Transactional
    public EmprestimoResponseDTO criar(EmprestimoRequestDTO dto) {
        log.info("Criando empréstimo - Livro ID: {}, Usuário ID: {}",
                dto.getLivroId(), dto.getUsuarioId());

        Livro livro = livroRepository.findById(dto.getLivroId())
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado"));

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        validarEmprestimo(livro, usuario);

        Emprestimo emprestimo = Emprestimo.builder()
                .livro(livro)
                .usuario(usuario)
                .dataEmprestimo(LocalDate.now())
                .dataPrevistaDevolucao(LocalDate.now().plusDays(usuario.getTipo().getDiasEmprestimo()))
                .status(StatusEmprestimo.ATIVO)
                .observacoes(dto.getObservacoes())
                .build();

        livro.emprestar();
        livroRepository.save(livro);

        Emprestimo emprestimoSalvo = emprestimoRepository.save(emprestimo);
        log.info("Empréstimo criado com sucesso. ID: {}", emprestimoSalvo.getId());

        return emprestimoMapper.toResponseDTO(emprestimoSalvo);
    }

    @Transactional(readOnly = true)
    public EmprestimoResponseDTO buscarPorId(Long id) {
        log.debug("Buscando empréstimo por ID: {}", id);

        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo não encontrado com ID: " + id));

        return emprestimoMapper.toResponseDTO(emprestimo);
    }

    @Transactional(readOnly = true)
    public Page<EmprestimoResponseDTO> listarTodos(Pageable pageable) {
        log.debug("Listando todos os empréstimos");
        return emprestimoRepository.findAll(pageable)
                .map(emprestimoMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<EmprestimoResponseDTO> listarPorStatus(StatusEmprestimo status, Pageable pageable) {
        log.debug("Listando empréstimos com status: {}", status);
        return emprestimoRepository.findByStatus(status, pageable)
                .map(emprestimoMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<EmprestimoResponseDTO> listarPorUsuario(Long usuarioId, Pageable pageable) {
        log.debug("Listando empréstimos do usuário ID: {}", usuarioId);

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        return emprestimoRepository.findByUsuarioIdOrderByDataEmprestimoDesc(usuarioId, pageable)
                .map(emprestimoMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<EmprestimoResponseDTO> listarAtrasados() {
        log.debug("Listando empréstimos atrasados");
        List<Emprestimo> emprestimos = emprestimoRepository.findEmprestimosAtrasados(LocalDate.now());
        return emprestimoMapper.toResponseDTOList(emprestimos);
    }

    @Transactional
    public EmprestimoResponseDTO devolver(Long id) {
        log.info("Devolvendo empréstimo ID: {}", id);

        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo não encontrado"));

        if (emprestimo.getDataDevolucao() != null) {
            throw new BusinessException("Este empréstimo já foi devolvido");
        }

        emprestimo.devolver();
        emprestimo.getLivro().devolver();

        livroRepository.save(emprestimo.getLivro());
        Emprestimo emprestimoDevolucao = emprestimoRepository.save(emprestimo);

        log.info("Empréstimo devolvido com sucesso. ID: {}", id);
        if (emprestimo.getValorMulta() != null && emprestimo.getValorMulta().doubleValue() > 0) {
            log.info("Multa gerada: R$ {}", emprestimo.getValorMulta());
        }

        return emprestimoMapper.toResponseDTO(emprestimoDevolucao);
    }

    @Transactional
    public EmprestimoResponseDTO renovar(Long id, int dias) {
        log.info("Renovando empréstimo ID: {} por {} dias", id, dias);

        if (dias <= 0 || dias > 30) {
            throw new BusinessException("Número de dias para renovação deve ser entre 1 e 30");
        }

        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empréstimo não encontrado"));

        emprestimo.renovar(dias);
        Emprestimo emprestimoRenovado = emprestimoRepository.save(emprestimo);

        log.info("Empréstimo renovado com sucesso. Nova data: {}",
                emprestimoRenovado.getDataPrevistaDevolucao());

        return emprestimoMapper.toResponseDTO(emprestimoRenovado);
    }

    private void validarEmprestimo(Livro livro, Usuario usuario) {
        if (!livro.getAtivo()) {
            throw new BusinessException("Livro não está disponível para empréstimo");
        }

        if (livro.getQuantidadeDisponivel() <= 0) {
            throw new BusinessException("Não há exemplares disponíveis deste livro");
        }

        if (!usuario.getAtivo()) {
            throw new BusinessException("Usuário não está ativo");
        }

        if (!usuario.podeEmprestar()) {
            throw new BusinessException(
                    "Usuário atingiu o limite de empréstimos simultâneos (" +
                            usuario.getLimiteEmprestimos() + ")"
            );
        }

        // Verificar se o usuário já tem este livro emprestado
        if (emprestimoRepository.existsByUsuarioIdAndLivroIdAndDataDevolucaoIsNull(
                usuario.getId(), livro.getId())) {
            throw new BusinessException("Usuário já possui este livro emprestado");
        }

        // Verificar se o usuário tem empréstimos atrasados
        long emprestimosAtrasados = emprestimoRepository
                .findEmprestimosAtivosDoUsuario(usuario.getId())
                .stream()
                .filter(Emprestimo::isAtrasado)
                .count();

        if (emprestimosAtrasados > 0) {
            throw new BusinessException(
                    "Usuário possui empréstimos em atraso. Regularize a situação antes de novo empréstimo"
            );
        }
    }
}