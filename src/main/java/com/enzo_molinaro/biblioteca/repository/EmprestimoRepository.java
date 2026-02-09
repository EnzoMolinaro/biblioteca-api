package com.enzo_molinaro.biblioteca.repository;

import com.enzo_molinaro.biblioteca.entity.Emprestimo;
import com.enzo_molinaro.biblioteca.enums.StatusEmprestimo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    List<Emprestimo> findByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status);

    List<Emprestimo> findByLivroIdAndStatus(Long livroId, StatusEmprestimo status);

    @Query("SELECT e FROM Emprestimo e WHERE e.usuario.id = :usuarioId AND e.dataDevolucao IS NULL")
    List<Emprestimo> findEmprestimosAtivosDoUsuario(@Param("usuarioId") Long usuarioId);

    @Query("SELECT COUNT(e) FROM Emprestimo e WHERE e.usuario.id = :usuarioId AND e.dataDevolucao IS NULL")
    long countEmprestimosAtivosByUsuarioId(@Param("usuarioId") Long usuarioId);

    @Query("SELECT e FROM Emprestimo e WHERE e.dataDevolucao IS NULL " +
            "AND e.dataPrevistaDevolucao < :data")
    List<Emprestimo> findEmprestimosAtrasados(@Param("data") LocalDate data);

    @Query("SELECT e FROM Emprestimo e WHERE e.dataPrevistaDevolucao BETWEEN :dataInicio AND :dataFim")
    List<Emprestimo> findEmprestimosPorPeriodo(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim
    );

    Page<Emprestimo> findByStatus(StatusEmprestimo status, Pageable pageable);

    @Query("SELECT e FROM Emprestimo e " +
            "WHERE e.usuario.id = :usuarioId " +
            "ORDER BY e.dataEmprestimo DESC")
    Page<Emprestimo> findByUsuarioIdOrderByDataEmprestimoDesc(
            @Param("usuarioId") Long usuarioId,
            Pageable pageable
    );

    @Query("SELECT e FROM Emprestimo e JOIN FETCH e.livro JOIN FETCH e.usuario " +
            "WHERE e.id = :id")
    Emprestimo findByIdWithDetails(@Param("id") Long id);

    boolean existsByUsuarioIdAndLivroIdAndDataDevolucaoIsNull(Long usuarioId, Long livroId);
}