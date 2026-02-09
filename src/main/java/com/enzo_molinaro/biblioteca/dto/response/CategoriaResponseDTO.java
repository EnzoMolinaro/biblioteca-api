package com.enzo_molinaro.biblioteca.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaResponseDTO {
    private Long id;
    private String nome;
    private String descricao;
    private Long quantidadeLivros;
}