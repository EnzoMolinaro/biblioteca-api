package com.enzo_molinaro.biblioteca.controller;

import com.enzo_molinaro.biblioteca.dto.request.EmprestimoRequestDTO;
import com.enzo_molinaro.biblioteca.dto.response.EmprestimoResponseDTO;
import com.enzo_molinaro.biblioteca.enums.StatusEmprestimo;
import com.enzo_molinaro.biblioteca.service.EmprestimoService;
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
@RequestMapping("/api/emprestimos")
@RequiredArgsConstructor
@Tag(name = "Empréstimos", description = "Endpoints para gerenciamento de empréstimos")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    @PostMapping
    @Operation(summary = "Criar empréstimo", description = "Registra um novo empréstimo de livro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empréstimo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou regra de negócio violada"),
            @ApiResponse(responseCode = "404", description = "Livro ou usuário não encontrado")
    })
    public ResponseEntity<EmprestimoResponseDTO> criar(@Valid @RequestBody EmprestimoRequestDTO dto) {
        EmprestimoResponseDTO emprestimo = emprestimoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(emprestimo);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar empréstimo por ID", description = "Retorna os detalhes de um empréstimo específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empréstimo encontrado"),
            @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado")
    })
    public ResponseEntity<EmprestimoResponseDTO> buscarPorId(
            @Parameter(description = "ID do empréstimo") @PathVariable Long id) {
        EmprestimoResponseDTO emprestimo = emprestimoService.buscarPorId(id);
        return ResponseEntity.ok(emprestimo);
    }

    @GetMapping
    @Operation(summary = "Listar todos os empréstimos", description = "Retorna lista paginada de empréstimos")
    public ResponseEntity<Page<EmprestimoResponseDTO>> listarTodos(
            @PageableDefault(size = 10, sort = "dataEmprestimo", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<EmprestimoResponseDTO> emprestimos = emprestimoService.listarTodos(pageable);
        return ResponseEntity.ok(emprestimos);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar por status", description = "Retorna empréstimos com status específico")
    public ResponseEntity<Page<EmprestimoResponseDTO>> listarPorStatus(
            @Parameter(description = "Status do empréstimo") @PathVariable StatusEmprestimo status,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<EmprestimoResponseDTO> emprestimos = emprestimoService.listarPorStatus(status, pageable);
        return ResponseEntity.ok(emprestimos);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar por usuário", description = "Retorna empréstimos de um usuário específico")
    public ResponseEntity<Page<EmprestimoResponseDTO>> listarPorUsuario(
            @Parameter(description = "ID do usuário") @PathVariable Long usuarioId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<EmprestimoResponseDTO> emprestimos = emprestimoService.listarPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(emprestimos);
    }

    @GetMapping("/atrasados")
    @Operation(summary = "Listar empréstimos atrasados", description = "Retorna todos os empréstimos em atraso")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarAtrasados() {
        List<EmprestimoResponseDTO> emprestimos = emprestimoService.listarAtrasados();
        return ResponseEntity.ok(emprestimos);
    }

    @PatchMapping("/{id}/devolver")
    @Operation(summary = "Devolver livro", description = "Registra a devolução de um empréstimo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Devolução registrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado"),
            @ApiResponse(responseCode = "400", description = "Empréstimo já devolvido")
    })
    public ResponseEntity<EmprestimoResponseDTO> devolver(
            @Parameter(description = "ID do empréstimo") @PathVariable Long id) {
        EmprestimoResponseDTO emprestimo = emprestimoService.devolver(id);
        return ResponseEntity.ok(emprestimo);
    }

    @PatchMapping("/{id}/renovar")
    @Operation(summary = "Renovar empréstimo", description = "Renova o prazo de devolução de um empréstimo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empréstimo renovado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Empréstimo não encontrado"),
            @ApiResponse(responseCode = "400", description = "Renovação não permitida")
    })
    public ResponseEntity<EmprestimoResponseDTO> renovar(
            @Parameter(description = "ID do empréstimo") @PathVariable Long id,
            @Parameter(description = "Número de dias para renovação") @RequestParam(defaultValue = "7") int dias) {
        EmprestimoResponseDTO emprestimo = emprestimoService.renovar(id, dias);
        return ResponseEntity.ok(emprestimo);
    }
}