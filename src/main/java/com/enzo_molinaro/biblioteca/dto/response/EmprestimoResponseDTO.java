package com.enzo_molinaro.biblioteca.dto.response;

import com.enzo_molinaro.biblioteca.enums.StatusEmprestimo;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmprestimoResponseDTO {
    private Long id;

    // Dados do livro
    private Long livroId;
    private String livroTitulo;
    private String livroIsbn;

    // Dados do usuário
    private Long usuarioId;
    private String usuarioNome;
    private String usuarioEmail;

    // Dados do empréstimo
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;
    private StatusEmprestimo status;
    private BigDecimal valorMulta;
    private String observacoes;
    private Long diasAtraso;
    private Boolean atrasado;
    private LocalDateTime criadoEm;
}