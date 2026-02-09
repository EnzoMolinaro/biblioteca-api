package com.enzo_molinaro.biblioteca.service;

import com.enzo_molinaro.biblioteca.dto.request.LivroRequestDTO;
import com.enzo_molinaro.biblioteca.dto.response.LivroResponseDTO;
import com.enzo_molinaro.biblioteca.entity.Livro;
import com.enzo_molinaro.biblioteca.exception.BusinessException;
import com.enzo_molinaro.biblioteca.exception.ResourceNotFoundException;
import com.enzo_molinaro.biblioteca.mapper.LivroMapper;
import com.enzo_molinaro.biblioteca.repository.CategoriaRepository;
import com.enzo_molinaro.biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private LivroMapper livroMapper;

    @InjectMocks
    private LivroService livroService;

    private LivroRequestDTO livroRequestDTO;
    private Livro livro;
    private LivroResponseDTO livroResponseDTO;

    @BeforeEach
    void setUp() {
        livroRequestDTO = LivroRequestDTO.builder()
                .isbn("9788535902778")
                .titulo("Dom Casmurro")
                .autor("Machado de Assis")
                .quantidadeTotal(5)
                .valorMultaDiaria(new BigDecimal("2.00"))
                .build();

        livro = Livro.builder()
                .id(1L)
                .isbn("9788535902778")
                .titulo("Dom Casmurro")
                .autor("Machado de Assis")
                .quantidadeTotal(5)
                .quantidadeDisponivel(5)
                .valorMultaDiaria(new BigDecimal("2.00"))
                .ativo(true)
                .build();

        livroResponseDTO = LivroResponseDTO.builder()
                .id(1L)
                .isbn("9788535902778")
                .titulo("Dom Casmurro")
                .autor("Machado de Assis")
                .build();
    }

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    void deveCriarLivroComSucesso() {
        // Arrange
        when(livroRepository.existsByIsbn(livroRequestDTO.getIsbn())).thenReturn(false);
        when(livroMapper.toEntity(livroRequestDTO)).thenReturn(livro);
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);
        when(livroMapper.toResponseDTO(livro)).thenReturn(livroResponseDTO);

        // Act
        LivroResponseDTO resultado = livroService.criar(livroRequestDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(livroResponseDTO.getTitulo(), resultado.getTitulo());
        verify(livroRepository, times(1)).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar livro com ISBN duplicado")
    void deveLancarExcecaoAoCriarLivroComIsbnDuplicado() {
        // Arrange
        when(livroRepository.existsByIsbn(livroRequestDTO.getIsbn())).thenReturn(true);

        // Act & Assert
        assertThrows(BusinessException.class, () -> livroService.criar(livroRequestDTO));
        verify(livroRepository, never()).save(any(Livro.class));
    }

    @Test
    @DisplayName("Deve buscar livro por ID com sucesso")
    void deveBuscarLivroPorIdComSucesso() {
        // Arrange
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(livroMapper.toResponseDTO(livro)).thenReturn(livroResponseDTO);

        // Act
        LivroResponseDTO resultado = livroService.buscarPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar livro inexistente")
    void deveLancarExcecaoAoBuscarLivroInexistente() {
        // Arrange
        when(livroRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> livroService.buscarPorId(999L));
    }

    @Test
    @DisplayName("Deve deletar (desativar) livro com sucesso")
    void deveDeletarLivroComSucesso() {
        // Arrange
        when(livroRepository.findById(1L)).thenReturn(Optional.of(livro));
        when(livroRepository.save(any(Livro.class))).thenReturn(livro);

        // Act
        livroService.deletar(1L);

        // Assert
        assertFalse(livro.getAtivo());
        verify(livroRepository, times(1)).save(livro);
    }
}