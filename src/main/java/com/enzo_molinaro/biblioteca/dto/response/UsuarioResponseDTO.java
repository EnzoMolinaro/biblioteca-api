package com.enzo_molinaro.biblioteca.dto.response;

import com.enzo_molinaro.biblioteca.enums.TipoUsuario;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private TipoUsuario tipo;
    private Boolean ativo;
    private Integer limiteEmprestimos;
    private Long emprestimoAtivos;
    private LocalDateTime criadoEm;
}