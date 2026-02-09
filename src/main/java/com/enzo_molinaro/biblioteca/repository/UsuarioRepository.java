package com.enzo_molinaro.biblioteca.repository;

import com.enzo_molinaro.biblioteca.entity.Usuario;
import com.enzo_molinaro.biblioteca.enums.TipoUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    Page<Usuario> findByAtivoTrue(Pageable pageable);

    List<Usuario> findByTipoAndAtivoTrue(TipoUsuario tipo);

    @Query("SELECT u FROM Usuario u WHERE u.ativo = true AND " +
            "(LOWER(u.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "u.cpf LIKE CONCAT('%', :termo, '%'))")
    Page<Usuario> buscarPorTermo(@Param("termo") String termo, Pageable pageable);

    @Query("SELECT u FROM Usuario u JOIN u.emprestimos e " +
            "WHERE e.dataDevolucao IS NULL " +
            "GROUP BY u HAVING COUNT(e) >= u.limiteEmprestimos")
    List<Usuario> findUsuariosComLimiteAtingido();
}