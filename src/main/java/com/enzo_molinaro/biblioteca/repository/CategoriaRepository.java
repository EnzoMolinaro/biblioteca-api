package com.enzo_molinaro.biblioteca.repository;

import com.enzo_molinaro.biblioteca.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNome(String nome);

    boolean existsByNome(String nome);

    @Query("SELECT c FROM Categoria c LEFT JOIN FETCH c.livros")
    List<Categoria> findAllWithLivros();

    @Query("SELECT c FROM Categoria c WHERE SIZE(c.livros) > 0")
    List<Categoria> findCategoriasComLivros();
}