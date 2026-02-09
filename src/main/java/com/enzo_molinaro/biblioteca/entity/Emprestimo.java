package com.enzo_molinaro.biblioteca.entity;


import com.enzo_molinaro.biblioteca.enums.StatusEmprestimo;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "emprestimos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "data_emprestimo", nullable = false)
    private LocalDate dataEmprestimo;

    @Column(name = "data_prevista_devolucao", nullable = false)
    private LocalDate dataPrevistaDevolucao;

    @Column(name = "data_devolucao")
    private LocalDate dataDevolucao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private StatusEmprestimo status = StatusEmprestimo.ATIVO;

    @Column(name = "valor_multa", precision = 10, scale = 2)
    private BigDecimal valorMulta;

    @Column(name = "observacoes", length = 500)
    private String observacoes;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    // Métodos de negócio
    public void devolver() {
        this.dataDevolucao = LocalDate.now();
        this.status = StatusEmprestimo.DEVOLVIDO;
        calcularMulta();
    }

    public void renovar(int dias) {
        if (this.status != StatusEmprestimo.ATIVO) {
            throw new IllegalStateException("Apenas empréstimos ativos podem ser renovados");
        }
        if (isAtrasado()) {
            throw new IllegalStateException("Não é possível renovar empréstimo em atraso");
        }
        this.dataPrevistaDevolucao = this.dataPrevistaDevolucao.plusDays(dias);
        this.status = StatusEmprestimo.RENOVADO;
    }

    public boolean isAtrasado() {
        return this.dataDevolucao == null &&
                LocalDate.now().isAfter(this.dataPrevistaDevolucao);
    }

    public long getDiasAtraso() {
        if (!isAtrasado()) return 0;
        return ChronoUnit.DAYS.between(this.dataPrevistaDevolucao, LocalDate.now());
    }

    private void calcularMulta() {
        if (isAtrasado() && this.livro.getValorMultaDiaria() != null) {
            long diasAtraso = ChronoUnit.DAYS.between(
                    this.dataPrevistaDevolucao,
                    this.dataDevolucao
            );
            if (diasAtraso > 0) {
                this.valorMulta = this.livro.getValorMultaDiaria()
                        .multiply(BigDecimal.valueOf(diasAtraso));
            }
        }
    }
}
