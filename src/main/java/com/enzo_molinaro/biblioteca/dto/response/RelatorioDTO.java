package com.enzo_molinaro.biblioteca.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelatorioDTO {
    private Long totalLivros;
    private Long totalUsuarios;
    private Long totalEmprestimos;
    private Long emprestimosAtivos;
    private Long emprestimosAtrasados;
    private BigDecimal multasAcumuladas;
    private Long livrosIndisponiveis;
    private LocalDate dataGeracao;
}