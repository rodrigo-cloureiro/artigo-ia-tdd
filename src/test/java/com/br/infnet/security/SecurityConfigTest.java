package com.br.infnet.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("Testes da SecurityConfig")
public class SecurityConfigTest {

    @ParameterizedTest
    @ValueSource(strings = {"<script>alert('xss')</script>", "'; DROP TABLE--"})
    void testProcessarEntrada(String entrada) {
        String resultado = SecurityConfig.processarEntrada(entrada);

        assertFalse(resultado.contains("<script>"));
        assertFalse(resultado.contains("DROP TABLE"));
    }
}
