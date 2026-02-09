package com.enzo_molinaro.biblioteca.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LivroRequestDTO {

    @NotBlank(message = "ISBN é obrigatório")
    @Pattern(regexp = "\\d{13}", message = "ISBN deve conter 13 dígitos")
    private String isbn;

    @NotBlank(message = "Título é obrigatório")
    @Size(min = 1, max = 200, message = "Título deve ter entre 1 e 200 caracteres")
    private String titulo;

    @NotBlank(message = "Autor é obrigatório")
    private String autor;

    @Size(max = 100)
    private String editora;

    @Min(value = 1000, message = "Ano deve ser maior que 1000")
    @Max(value = 2100, message = "Ano deve ser menor que 2100")
    private Integer anoPublicacao;

    @NotNull(message = "Quantidade total é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser pelo menos 1")
    private Integer quantidadeTotal;

    @DecimalMin(value = "0.0", message = "Valor da multa não pode ser negativo")
    private BigDecimal valorMultaDiaria;

    private Long categoriaId;
}