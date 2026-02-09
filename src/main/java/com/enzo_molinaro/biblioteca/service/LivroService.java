package com.enzo_molinaro.biblioteca.service;

import com.enzo_molinaro.biblioteca.dto.request.LivroRequestDTO;
import com.enzo_molinaro.biblioteca.dto.response.LivroResponseDTO;
import com.enzo_molinaro.biblioteca.entity.Categoria;
import com.enzo_molinaro.biblioteca.entity.Livro;
import com.enzo_molinaro.biblioteca.exception.BusinessException;
import com.enzo_molinaro.biblioteca.exception.ResourceNotFoundException;
import com.enzo_molinaro.biblioteca.mapper.LivroMapper;
import com.enzo_molinaro.biblioteca.repository.CategoriaRepository;
import com.enzo_molinaro.biblioteca.repository.LivroRepository;
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
public class LivroService {

    private final LivroRepository livroRepository;
    private final CategoriaRepository categoriaRepository;
    private final LivroMapper livroMapper;

    @Transactional
    public LivroResponseDTO criar(LivroRequestDTO dto) {
        log.info("Criando livro com ISBN: {}", dto.getIsbn());

        if (livroRepository.existsByIsbn(dto.getIsbn())) {
            throw new BusinessException("Já existe um livro cadastrado com o ISBN: " + dto.getIsbn());
        }

        Livro livro = livroMapper.toEntity(dto);

        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
            livro.setCategoria(categoria);
        }

        Livro livroSalvo = livroRepository.save(livro);
        log.info("Livro criado com sucesso. ID: {}", livroSalvo.getId());

        return livroMapper.toResponseDTO(livroSalvo);
    }

    @Transactional(readOnly = true)
    public LivroResponseDTO buscarPorId(Long id) {
        log.debug("Buscando livro por ID: {}", id);

        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado com ID: " + id));

        return livroMapper.toResponseDTO(livro);
    }

    @Transactional(readOnly = true)
    public Page<LivroResponseDTO> listarTodos(Pageable pageable) {
        log.debug("Listando todos os livros ativos");
        return livroRepository.findByAtivoTrue(pageable)
                .map(livroMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<LivroResponseDTO> buscar(String termo, Pageable pageable) {
        log.debug("Buscando livros por termo: {}", termo);
        return livroRepository.buscarPorTermo(termo, pageable)
                .map(livroMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<LivroResponseDTO> listarDisponiveis(Pageable pageable) {
        log.debug("Listando livros disponíveis");
        return livroRepository.findDisponiveis(pageable)
                .map(livroMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<LivroResponseDTO> listarPorCategoria(Long categoriaId, Pageable pageable) {
        log.debug("Listando livros da categoria ID: {}", categoriaId);

        if (!categoriaRepository.existsById(categoriaId)) {
            throw new ResourceNotFoundException("Categoria não encontrada");
        }

        return livroRepository.findByCategoriaId(categoriaId, pageable)
                .map(livroMapper::toResponseDTO);
    }

    @Transactional
    public LivroResponseDTO atualizar(Long id, LivroRequestDTO dto) {
        log.info("Atualizando livro ID: {}", id);

        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado"));

        if (!livro.getIsbn().equals(dto.getIsbn()) && livroRepository.existsByIsbn(dto.getIsbn())) {
            throw new BusinessException("Já existe outro livro com o ISBN: " + dto.getIsbn());
        }

        Integer quantidadeAntiga = livro.getQuantidadeTotal();

        livroMapper.updateEntityFromDTO(dto, livro);

        // Ajustar quantidade disponível se a total foi alterada
        if (!quantidadeAntiga.equals(dto.getQuantidadeTotal())) {
            int diferenca = dto.getQuantidadeTotal() - quantidadeAntiga;
            livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() + diferenca);

            if (livro.getQuantidadeDisponivel() < 0) {
                throw new BusinessException("Quantidade disponível não pode ser negativa");
            }
        }

        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
            livro.setCategoria(categoria);
        }

        Livro livroAtualizado = livroRepository.save(livro);
        log.info("Livro atualizado com sucesso. ID: {}", id);

        return livroMapper.toResponseDTO(livroAtualizado);
    }

    @Transactional
    public void deletar(Long id) {
        log.info("Deletando (desativando) livro ID: {}", id);

        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado"));

        livro.setAtivo(false);
        livroRepository.save(livro);

        log.info("Livro desativado com sucesso. ID: {}", id);
    }

    @Transactional(readOnly = true)
    public List<LivroResponseDTO> buscarPorAutor(String autor) {
        log.debug("Buscando livros do autor: {}", autor);
        List<Livro> livros = livroRepository.findByAutorContainingIgnoreCaseAndAtivoTrue(autor);
        return livroMapper.toResponseDTOList(livros);
    }

    @Transactional(readOnly = true)
    public boolean isDisponivel(Long id) {
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livro não encontrado"));
        return livro.getQuantidadeDisponivel() > 0;
    }
}