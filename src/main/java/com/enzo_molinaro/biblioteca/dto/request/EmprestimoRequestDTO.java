package com.enzo_molinaro.biblioteca.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmprestimoRequestDTO {

    @NotNull(message = "ID do livro é obrigatório")
    private Long livroId;

    @NotNull(message = "ID do usuário é obrigatório")
    private Long usuarioId;

    @Size(max = 500)
    private String observacoes;
}