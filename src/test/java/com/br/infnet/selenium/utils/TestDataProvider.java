package com.br.infnet.selenium.utils;

import org.junit.jupiter.params.provider.Arguments;
import java.util.stream.Stream;

public class TestDataProvider {

    public static Stream<Arguments> livrosValidos() {
        return Stream.of(
                Arguments.of("Dom Casmurro", "Machado de Assis", "9788525406360"),
                Arguments.of("O Cortiço", "Aluísio Azevedo", "9788594318602"),
                Arguments.of("1984", "George Orwell", "9788535914849")
        );
    }

    public static Stream<Arguments> dadosBusca() {
        return Stream.of(
                Arguments.of("titulo", "Dom Casmurro"),
                Arguments.of("autor", "Machado"),
                Arguments.of("id", "1")
        );
    }
}
