package com.enzo_molinaro.biblioteca.mapper;

import com.enzo_molinaro.biblioteca.dto.request.LivroRequestDTO;
import com.enzo_molinaro.biblioteca.dto.response.LivroResponseDTO;
import com.enzo_molinaro.biblioteca.entity.Livro;
import org.mapstruct.*;  // ✅ Import correto do MapStruct

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LivroMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "quantidadeDisponivel", source = "quantidadeTotal")
    @Mapping(target = "categoria.id", source = "categoriaId")
    @Mapping(target = "ativo", constant = "true")
    Livro toEntity(LivroRequestDTO dto);

    @Mapping(target = "categoriaNome", source = "categoria.nome")
    LivroResponseDTO toResponseDTO(Livro entity);

    List<LivroResponseDTO> toResponseDTOList(List<Livro> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria.id", source = "categoriaId")
    void updateEntityFromDTO(LivroRequestDTO dto, @MappingTarget Livro entity);
}