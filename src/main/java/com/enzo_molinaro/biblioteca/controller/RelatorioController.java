package com.enzo_molinaro.biblioteca.controller;

import com.enzo_molinaro.biblioteca.dto.response.EmprestimoResponseDTO;
import com.enzo_molinaro.biblioteca.dto.response.RelatorioDTO;
import com.enzo_molinaro.biblioteca.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
@Tag(name = "Relatórios", description = "Endpoints para geração de relatórios e estatísticas")
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/geral")
    @Operation(summary = "Relatório geral",
            description = "Retorna estatísticas gerais do sistema")
    public ResponseEntity<RelatorioDTO> relatorioGeral() {
        RelatorioDTO relatorio = relatorioService.gerarRelatorioGeral();
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/livros-mais-emprestados")
    @Operation(summary = "Livros mais emprestados",
            description = "Retorna ranking dos livros mais emprestados")
    public ResponseEntity<Map<String, Object>> livrosMaisEmprestados(
            @Parameter(description = "Limite de resultados")
            @RequestParam(defaultValue = "10") int limite) {
        Map<String, Object> relatorio = relatorioService.relatorioLivrosMaisEmprestados(limite);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/usuarios-mais-ativos")
    @Operation(summary = "Usuários mais ativos",
            description = "Retorna ranking dos usuários com mais empréstimos")
    public ResponseEntity<Map<String, Object>> usuariosMaisAtivos(
            @Parameter(description = "Limite de resultados")
            @RequestParam(defaultValue = "10") int limite) {
        Map<String, Object> relatorio = relatorioService.relatorioUsuariosMaisAtivos(limite);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/periodo")
    @Operation(summary = "Empréstimos por período",
            description = "Retorna estatísticas de empréstimos em um período específico")
    public ResponseEntity<Map<String, Object>> emprestimosPorPeriodo(
            @Parameter(description = "Data inicial (formato: yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data final (formato: yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        Map<String, Object> relatorio = relatorioService.relatorioEmprestimosPorPeriodo(dataInicio, dataFim);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/proximos-vencimento")
    @Operation(summary = "Próximos do vencimento",
            description = "Retorna empréstimos que vencem nos próximos N dias")
    public ResponseEntity<List<EmprestimoResponseDTO>> proximosDoVencimento(
            @Parameter(description = "Número de dias")
            @RequestParam(defaultValue = "7") int dias) {
        List<EmprestimoResponseDTO> emprestimos =
                relatorioService.relatorioEmprestimosProximosDoVencimento(dias);
        return ResponseEntity.ok(emprestimos);
    }

    @GetMapping("/livros-por-categoria")
    @Operation(summary = "Livros por categoria",
            description = "Retorna quantidade de livros agrupados por categoria")
    public ResponseEntity<Map<String, Long>> livrosPorCategoria() {
        Map<String, Long> relatorio = relatorioService.relatorioLivrosPorCategoria();
        return ResponseEntity.ok(relatorio);
    }
}