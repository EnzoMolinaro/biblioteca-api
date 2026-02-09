package com.enzo_molinaro.biblioteca.enums;

public enum TipoUsuario {
    ESTUDANTE(3, 14),
    PROFESSOR(5, 21),
    FUNCIONARIO(3, 14),
    EXTERNO(2, 7);

    private final int limiteEmprestimos;
    private final int diasEmprestimo;

    TipoUsuario(int limiteEmprestimos, int diasEmprestimo) {
        this.limiteEmprestimos = limiteEmprestimos;
        this.diasEmprestimo = diasEmprestimo;
    }

    public int getLimiteEmprestimos() {
        return limiteEmprestimos;
    }

    public int getDiasEmprestimo() {
        return diasEmprestimo;
    }
}
