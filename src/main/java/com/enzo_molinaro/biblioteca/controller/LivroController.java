package com.enzo_molinaro.biblioteca.controller;

import com.enzo_molinaro.biblioteca.dto.request.LivroRequestDTO;
import com.enzo_molinaro.biblioteca.dto.response.LivroResponseDTO;
import com.enzo_molinaro.biblioteca.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livros")
@RequiredArgsConstructor
@Tag(name = "Livros", description = "Endpoints para gerenciamento de livros")
public class LivroController {

    private final LivroService livroService;

    @PostMapping
    @Operation(summary = "Cadastrar novo livro", description = "Cria um novo livro no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Livro criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "ISBN já cadastrado")
    })
    public ResponseEntity<LivroResponseDTO> criar(@Valid @RequestBody LivroRequestDTO dto) {
        LivroResponseDTO livro = livroService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(livro);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar livro por ID", description = "Retorna os detalhes de um livro específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livro encontrado"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    public ResponseEntity<LivroResponseDTO> buscarPorId(
            @Parameter(description = "ID do livro") @PathVariable Long id) {
        LivroResponseDTO livro = livroService.buscarPorId(id);
        return ResponseEntity.ok(livro);
    }

    @GetMapping
    @Operation(summary = "Listar todos os livros", description = "Retorna lista paginada de livros ativos")
    public ResponseEntity<Page<LivroResponseDTO>> listarTodos(
            @PageableDefault(size = 10, sort = "titulo", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<LivroResponseDTO> livros = livroService.listarTodos(pageable);
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar livros", description = "Busca livros por título, autor ou ISBN")
    public ResponseEntity<Page<LivroResponseDTO>> buscar(
            @Parameter(description = "Termo de busca") @RequestParam String termo,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<LivroResponseDTO> livros = livroService.buscar(termo, pageable);
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/disponiveis")
    @Operation(summary = "Listar livros disponíveis", description = "Retorna livros com exemplares disponíveis")
    public ResponseEntity<Page<LivroResponseDTO>> listarDisponiveis(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<LivroResponseDTO> livros = livroService.listarDisponiveis(pageable);
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Listar livros por categoria", description = "Retorna livros de uma categoria específica")
    public ResponseEntity<Page<LivroResponseDTO>> listarPorCategoria(
            @Parameter(description = "ID da categoria") @PathVariable Long categoriaId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<LivroResponseDTO> livros = livroService.listarPorCategoria(categoriaId, pageable);
        return ResponseEntity.ok(livros);
    }

    @GetMapping("/autor")
    @Operation(summary = "Buscar por autor", description = "Retorna livros de um autor específico")
    public ResponseEntity<List<LivroResponseDTO>> buscarPorAutor(
            @Parameter(description = "Nome do autor") @RequestParam String autor) {
        List<LivroResponseDTO> livros = livroService.buscarPorAutor(autor);
        return ResponseEntity.ok(livros);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar livro", description = "Atualiza os dados de um livro existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livro atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<LivroResponseDTO> atualizar(
            @Parameter(description = "ID do livro") @PathVariable Long id,
            @Valid @RequestBody LivroRequestDTO dto) {
        LivroResponseDTO livro = livroService.atualizar(id, dto);
        return ResponseEntity.ok(livro);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar livro", description = "Desativa um livro (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Livro deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    public ResponseEntity<Void> deletar(
            @Parameter(description = "ID do livro") @PathVariable Long id) {
        livroService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/disponibilidade")
    @Operation(summary = "Verificar disponibilidade", description = "Verifica se há exemplares disponíveis")
    public ResponseEntity<Boolean> verificarDisponibilidade(
            @Parameter(description = "ID do livro") @PathVariable Long id) {
        boolean disponivel = livroService.isDisponivel(id);
        return ResponseEntity.ok(disponivel);
    }
}