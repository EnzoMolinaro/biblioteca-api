package com.enzo_molinaro.biblioteca.service;

import com.enzo_molinaro.biblioteca.dto.response.EmprestimoResponseDTO;
import com.enzo_molinaro.biblioteca.dto.response.LivroResponseDTO;
import com.enzo_molinaro.biblioteca.dto.response.RelatorioDTO;
import com.enzo_molinaro.biblioteca.entity.Emprestimo;
import com.enzo_molinaro.biblioteca.entity.Livro;
import com.enzo_molinaro.biblioteca.mapper.EmprestimoMapper;
import com.enzo_molinaro.biblioteca.mapper.LivroMapper;
import com.enzo_molinaro.biblioteca.repository.EmprestimoRepository;
import com.enzo_molinaro.biblioteca.repository.LivroRepository;
import com.enzo_molinaro.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelatorioService {

    private final EmprestimoRepository emprestimoRepository;
    private final LivroRepository livroRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmprestimoMapper emprestimoMapper;
    private final LivroMapper livroMapper;

    @Transactional(readOnly = true)
    public RelatorioDTO gerarRelatorioGeral() {
        log.info("Gerando relatório geral do sistema");

        long totalLivros = livroRepository.count();
        long totalUsuarios = usuarioRepository.count();
        long totalEmprestimos = emprestimoRepository.count();

        List<Emprestimo> emprestimosAtivos = emprestimoRepository
                .findEmprestimosAtivosDoUsuario(null);

        long emprestimosAtivos_count = emprestimoRepository
                .findByStatus(com.enzo_molinaro.biblioteca.enums.StatusEmprestimo.ATIVO,
                        org.springframework.data.domain.Pageable.unpaged())
                .getTotalElements();

        List<Emprestimo> emprestimosAtrasados = emprestimoRepository
                .findEmprestimosAtrasados(LocalDate.now());

        BigDecimal multasAcumuladas = emprestimosAtrasados.stream()
                .map(Emprestimo::getValorMulta)
                .filter(multa -> multa != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Livro> livrosIndisponiveis = livroRepository.findIndisponiveis();

        return RelatorioDTO.builder()
                .totalLivros(totalLivros)
                .totalUsuarios(totalUsuarios)
                .totalEmprestimos(totalEmprestimos)
                .emprestimosAtivos(emprestimosAtivos_count)
                .emprestimosAtrasados((long) emprestimosAtrasados.size())
                .multasAcumuladas(multasAcumuladas)
                .livrosIndisponiveis((long) livrosIndisponiveis.size())
                .dataGeracao(LocalDate.now())
                .build();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> relatorioLivrosMaisEmprestados(int limite) {
        log.info("Gerando relatório de livros mais emprestados - Limite: {}", limite);

        List<Object[]> resultado = emprestimoRepository
                .findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        e -> e.getLivro().getId(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limite)
                .map(entry -> new Object[]{
                        livroRepository.findById(entry.getKey()).orElse(null),
                        entry.getValue()
                })
                .collect(Collectors.toList());

        List<Map<String, Object>> livrosMaisEmprestados = resultado.stream()
                .filter(obj -> obj[0] != null)
                .map(obj -> {
                    Livro livro = (Livro) obj[0];
                    Long quantidade = (Long) obj[1];

                    Map<String, Object> item = new HashMap<>();
                    item.put("livro", livroMapper.toResponseDTO(livro));
                    item.put("quantidadeEmprestimos", quantidade);
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("livrosMaisEmprestados", livrosMaisEmprestados);
        relatorio.put("dataGeracao", LocalDate.now());

        return relatorio;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> relatorioUsuariosMaisAtivos(int limite) {
        log.info("Gerando relatório de usuários mais ativos - Limite: {}", limite);

        List<Object[]> resultado = emprestimoRepository
                .findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        e -> e.getUsuario().getId(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limite)
                .map(entry -> new Object[]{
                        usuarioRepository.findById(entry.getKey()).orElse(null),
                        entry.getValue()
                })
                .collect(Collectors.toList());

        List<Map<String, Object>> usuariosMaisAtivos = resultado.stream()
                .filter(obj -> obj[0] != null)
                .map(obj -> {
                    var usuario = obj[0];
                    Long quantidade = (Long) obj[1];

                    Map<String, Object> item = new HashMap<>();
                    item.put("usuario", usuario);
                    item.put("quantidadeEmprestimos", quantidade);
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("usuariosMaisAtivos", usuariosMaisAtivos);
        relatorio.put("dataGeracao", LocalDate.now());

        return relatorio;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> relatorioEmprestimosPorPeriodo(
            LocalDate dataInicio, LocalDate dataFim) {

        log.info("Gerando relatório de empréstimos por período: {} a {}", dataInicio, dataFim);

        List<Emprestimo> emprestimos = emprestimoRepository
                .findEmprestimosPorPeriodo(dataInicio, dataFim);

        long totalEmprestimos = emprestimos.size();
        long devolvidosNoPrazo = emprestimos.stream()
                .filter(e -> e.getDataDevolucao() != null &&
                        !e.getDataDevolucao().isAfter(e.getDataPrevistaDevolucao()))
                .count();

        long devolvidosComAtraso = emprestimos.stream()
                .filter(e -> e.getDataDevolucao() != null &&
                        e.getDataDevolucao().isAfter(e.getDataPrevistaDevolucao()))
                .count();

        long aindaEmprestados = emprestimos.stream()
                .filter(e -> e.getDataDevolucao() == null)
                .count();

        BigDecimal multasGeradas = emprestimos.stream()
                .map(Emprestimo::getValorMulta)
                .filter(multa -> multa != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("periodo", Map.of("inicio", dataInicio, "fim", dataFim));
        relatorio.put("totalEmprestimos", totalEmprestimos);
        relatorio.put("devolvidosNoPrazo", devolvidosNoPrazo);
        relatorio.put("devolvidosComAtraso", devolvidosComAtraso);
        relatorio.put("aindaEmprestados", aindaEmprestados);
        relatorio.put("multasGeradas", multasGeradas);
        relatorio.put("dataGeracao", LocalDate.now());

        return relatorio;
    }

    @Transactional(readOnly = true)
    public List<EmprestimoResponseDTO> relatorioEmprestimosProximosDoVencimento(int dias) {
        log.info("Gerando relatório de empréstimos próximos do vencimento - {} dias", dias);

        LocalDate dataLimite = LocalDate.now().plusDays(dias);

        List<Emprestimo> emprestimos = emprestimoRepository
                .findEmprestimosPorPeriodo(LocalDate.now(), dataLimite)
                .stream()
                .filter(e -> e.getDataDevolucao() == null)
                .collect(Collectors.toList());

        return emprestimoMapper.toResponseDTOList(emprestimos);
    }

    @Transactional(readOnly = true)
    public Map<String, Long> relatorioLivrosPorCategoria() {
        log.info("Gerando relatório de livros por categoria");

        return livroRepository.findAll()
                .stream()
                .filter(livro -> livro.getCategoria() != null)
                .collect(Collectors.groupingBy(
                        livro -> livro.getCategoria().getNome(),
                        Collectors.counting()
                ));
    }
}