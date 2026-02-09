package com.enzo_molinaro.biblioteca.repository;

import com.enzo_molinaro.biblioteca.entity.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    Optional<Livro> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    Page<Livro> findByAtivoTrue(Pageable pageable);

    @Query("SELECT l FROM Livro l WHERE l.ativo = true AND " +
            "(LOWER(l.titulo) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "LOWER(l.autor) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "LOWER(l.isbn) LIKE LOWER(CONCAT('%', :termo, '%')))")
    Page<Livro> buscarPorTermo(@Param("termo") String termo, Pageable pageable);

    @Query("SELECT l FROM Livro l WHERE l.categoria.id = :categoriaId AND l.ativo = true")
    Page<Livro> findByCategoriaId(@Param("categoriaId") Long categoriaId, Pageable pageable);

    @Query("SELECT l FROM Livro l WHERE l.quantidadeDisponivel > 0 AND l.ativo = true")
    Page<Livro> findDisponiveis(Pageable pageable);

    @Query("SELECT l FROM Livro l WHERE l.quantidadeDisponivel = 0 AND l.ativo = true")
    List<Livro> findIndisponiveis();

    List<Livro> findByAutorContainingIgnoreCaseAndAtivoTrue(String autor);
}