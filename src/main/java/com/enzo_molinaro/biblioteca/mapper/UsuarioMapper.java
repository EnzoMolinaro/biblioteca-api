package com.enzo_molinaro.biblioteca.mapper;

import com.enzo_molinaro.biblioteca.dto.request.UsuarioRequestDTO;
import com.enzo_molinaro.biblioteca.dto.response.UsuarioResponseDTO;
import com.enzo_molinaro.biblioteca.entity.Usuario;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", constant = "true")
    @Mapping(target = "limiteEmprestimos", expression = "java(dto.getTipo().getLimiteEmprestimos())")
    Usuario toEntity(UsuarioRequestDTO dto);

    @Mapping(target = "emprestimoAtivos", expression = "java(entity.getEmprestimosAtivos())")
    UsuarioResponseDTO toResponseDTO(Usuario entity);

    List<UsuarioResponseDTO> toResponseDTOList(List<Usuario> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "limiteEmprestimos", expression = "java(dto.getTipo().getLimiteEmprestimos())")
    void updateEntityFromDTO(UsuarioRequestDTO dto, @MappingTarget Usuario entity);
}