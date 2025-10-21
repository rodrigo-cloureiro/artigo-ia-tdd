package com.br.infnet.service;

public class CalculadoraMulta {
    private static final double MULTA_FIXA = 5.0;
    private static final double MULTA_POR_DIA = 0.5;

    public static double calcular(int diasAtraso) {
        if (diasAtraso <= 0) {
            return 0.0;
        }
        return MULTA_FIXA + (MULTA_POR_DIA * diasAtraso);
    }
}
