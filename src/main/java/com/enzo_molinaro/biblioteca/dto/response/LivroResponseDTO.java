package com.enzo_molinaro.biblioteca.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivroResponseDTO {
    private Long id;
    private String isbn;
    private String titulo;
    private String autor;
    private String editora;
    private Integer anoPublicacao;
    private Integer quantidadeTotal;
    private Integer quantidadeDisponivel;
    private BigDecimal valorMultaDiaria;
    private String categoriaNome;
    private Boolean ativo;
    private LocalDateTime criadoEm;
}