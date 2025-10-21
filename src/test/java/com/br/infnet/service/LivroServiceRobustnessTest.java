package com.br.infnet.service;

import com.br.infnet.model.Livro;
import net.jqwik.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class LivroServiceRobustnessTest {
    private LivroService service;

    @BeforeEach
    void setup() {
        service = new LivroService();
    }

    @Property(tries = 500)
    void testTitulosMaliciosos(@ForAll("titulosMaliciosos") String titulo) {
        service = new LivroService();
        assertDoesNotThrow(() -> {
            try {
                service.buscarLivroPorTituloNoAcervo(titulo);
            } catch (IllegalArgumentException e) {
                assertTrue(e.getMessage().contains("inválido"));
            }
        });
    }

    @Property(tries = 500)
    void testISBNsInvalidos(@ForAll("isbnsMaliciosos") String isbn) {
        service = new LivroService();
        assertThrows(IllegalArgumentException.class, () -> {
            try {
                Livro livro = new Livro(service.gerarId(), "Teste", "Autor", isbn);
                service.cadastrarLivroNoAcervo(livro);
            } catch (IllegalArgumentException e) {
                throw e;
            }
        });
    }

    // TESTES DE SOBRECARGA
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    @DisplayName("Sistema deve responder dentro do tempo limite sob alta carga")
    void testAltaCargaCadastro() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            final int index = i;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    Livro livro = new Livro(
                            service.gerarId(),
                            "Livro " + index,
                            "Autor " + index,
                            String.format("978%010d", index)
                    );
                    service.cadastrarLivroNoAcervo(livro);
                } catch (Exception e) {
                    System.err.println("Erro em thread " + index + ": " + e.getMessage());
                }
            }, executor);
            futures.add(future);
        }
        assertDoesNotThrow(() -> {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        });
        executor.shutdown();
    }

    // TESTES DE LIMITES EXTREMOS
    @ParameterizedTest
    @ValueSource(ints = {Integer.MAX_VALUE, Integer.MIN_VALUE, -1, 0})
    @DisplayName("Testes com IDs extremos")
    void testIDsExtremos(int id) {
        assertDoesNotThrow(() -> {
            try {
                service.buscarLivroPorIDNoAcervo(id);
                fail("Deveria lançar exceção para ID inválido: " + id);
            } catch (NoSuchElementException | IllegalArgumentException e) {
                assertNotNull(e.getMessage());
                assertFalse(e.getMessage().isEmpty());
            }
        });
    }

    @Test
    @DisplayName("Memória deve ser liberada após operações em massa")
    void testLiberacaoMemoria() {
        Runtime runtime = Runtime.getRuntime();
        long memoriaInicial = runtime.totalMemory() - runtime.freeMemory();

        //Adicionando muitos livros ao acervo
        for (int i = 0; i < 1000; i++) {
            Livro livro = new Livro(
                    service.gerarId(),
                    "Livro " + i,
                    "Autor",
                    String.format("978%010d", i)
            );
            service.cadastrarLivroNoAcervo(livro);
        }

        // Remover todos
        ArrayList<Livro> livros = service.listarLivrosDoAcervo();
        for (Livro livro : livros) {
            if (livro.getId() > 3) { // Preservar livros do CSV
                service.removerLivroDoAcervo(livro.getId());
            }
        }

        System.gc();

        long memoriaFinal = runtime.totalMemory() - runtime.freeMemory();
        long diferenca = memoriaFinal - memoriaInicial;

        //Memory leak deve ser menor que 50MB
        assertTrue(diferenca < 50_000_000, "Possível vazamento de memória: " + diferenca + " bytes");
    }

    // PROVIDERS PARA FUZZ TESTING
    @Provide
    Arbitrary<String> titulosMaliciosos() {
        return Arbitraries.oneOf(
                Arbitraries.strings().ofLength(10000),
                Arbitraries.strings().withChars('<', '>', '&', '"', '\'', '\n', '\r', '\t'),
                Arbitraries.of("'; DROP TABLE livros; --", "' OR '1'='1", "UNION SELECT * FROM users"),
                Arbitraries.of("<script>alert('xss')</script>", "javascript:alert('xss')", "<img src=x onerror=alert('xss')>"),
                Arbitraries.of(null, "", "   ", "\n\n\n"),
                Arbitraries.strings().withChars('\u0000', '\uFFFF', '\u202E'),
                Arbitraries.of("../../../etc/passwd", "..\\..\\windows\\system32")
        );
    }

    @Provide
    Arbitrary<String> isbnsMaliciosos() {
        return Arbitraries.oneOf(
                Arbitraries.strings().withCharRange('0', '9').ofLength(50),
                Arbitraries.strings().withChars('a', 'z', '!', '@', '#'),
                Arbitraries.of(null, "", "   "),
                Arbitraries.of("123", "978-0-123-45678-9-0", "ISBN978123456789"),
                Arbitraries.strings().ofLength(1000).withCharRange('0', '9'),
                Arbitraries.of("'; DROP TABLE livros; --", "' OR '1'='1", "UNION SELECT * FROM users"),
                Arbitraries.of("<script>alert('xss')</script>", "javascript:alert('xss')", "<img src=x onerror=alert('xss')>"),
                Arbitraries.of(null, "", "   ", "\n\n\n"),
                Arbitraries.strings().withChars('\u0000', '\uFFFF', '\u202E'),
                Arbitraries.of("../../../etc/passwd", "..\\..\\windows\\system32")
        );
    }
}
