package com.enzo_molinaro.biblioteca.mapper;

import com.enzo_molinaro.biblioteca.dto.request.EmprestimoRequestDTO;
import com.enzo_molinaro.biblioteca.dto.response.EmprestimoResponseDTO;
import com.enzo_molinaro.biblioteca.entity.Emprestimo;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmprestimoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "livro.id", source = "livroId")
    @Mapping(target = "usuario.id", source = "usuarioId")
    Emprestimo toEntity(EmprestimoRequestDTO dto);

    @Mapping(target = "livroTitulo", source = "livro.titulo")
    @Mapping(target = "livroIsbn", source = "livro.isbn")
    @Mapping(target = "usuarioNome", source = "usuario.nome")
    @Mapping(target = "usuarioEmail", source = "usuario.email")
    @Mapping(target = "diasAtraso", expression = "java(entity.getDiasAtraso())")
    @Mapping(target = "atrasado", expression = "java(entity.isAtrasado())")
    EmprestimoResponseDTO toResponseDTO(Emprestimo entity);

    List<EmprestimoResponseDTO> toResponseDTOList(List<Emprestimo> entities);
}