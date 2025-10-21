package com.br.infnet.service;

import com.br.infnet.model.Livro;
import com.br.infnet.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class LivroServiceSecurityTest {
    private LivroService service;

    @BeforeEach
    void setup() {
        service = new LivroService();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "'; DROP TABLE livros; --",
            "' OR '1'='1",
            "<script>alert('xss')</script>",
            "javascript:alert('xss')",
            "../../../etc/passwd",
            "..\\..\\windows\\system32",
            "\u0000\u0001\u0002",
            "UNION SELECT * FROM users",
            "   ",
            "",
            "Título@#$%&*()+={}[]|\\:;\"<>?/~`"
    })
    @DisplayName("Títulos inválidos devem falhar rapidamente")
    void testTitulosInvalidosFailEarly(String titulo) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Livro livro = new Livro(service.gerarId(), titulo, "João Silva", "9781234567890");
            service.cadastrarLivroNoAcervo(livro);
        });


        assertNotNull(exception.getMessage());
        assertFalse(exception.getMessage().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "   ",
            "",
            "'; DROP TABLE autores; --",
            "<script>alert('xss')</script>",
            "Autor@#$%&*()+={}[]|\\:;\"<>?/~`"
    })
    @DisplayName("Autores inválidos devem falhar rapidamente")
    void testAutoresInvalidosFailEarly(String autor) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Livro livro = new Livro(service.gerarId(), "Título Válido", autor, "9781234567890");
            service.cadastrarLivroNoAcervo(livro);
        });

        assertNotNull(exception.getMessage());
        assertFalse(exception.getMessage().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123",
            "12345678901234567890",
            "abc1234567890",
            "978-123-456-789X",
            "'; DROP TABLE isbn; --",
            ""
    })
    @DisplayName("ISBNs inválidos devem falhar rapidamente")
    void testIsbnInvalidosFailEarly(String isbn) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            Livro livro = new Livro(service.gerarId(), "Título Válido", "João Silva", isbn);
            service.cadastrarLivroNoAcervo(livro);
        });
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().toLowerCase().contains("isbn"));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -100, 0, Integer.MIN_VALUE, 366, 1000})
    @DisplayName("Prazos inválidos devem falhar rapidamente com limites claros")
    void testPrazosInvalidosFailEarly(int prazo) {
        Livro livro = new Livro(service.gerarId(), "Título Válido", "João Silva", "9781234567890");
        service.cadastrarLivroNoAcervo(livro);


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.emprestarLivro(livro.getId(), prazo);
        });

        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("prazo") ||
                exception.getMessage().contains("positivo") ||
                exception.getMessage().contains("365"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Título<script>alert('xss')</script>Normal",
            "Título'; DROP TABLE livros; --Válido",
            "Título\u0000\u0001\u0002Limpo",
            "Título../../../etc/passwdSeguro",
            "TítuloUNION SELECT * FROM usersNormal"
    })
    @DisplayName("Títulos com conteúdo malicioso devem ser sanitizados")
    void testTitulosSanitizados(String tituloMalicioso) {
        //Entradas devem ser sanitizadas antes de serem validadas
        assertDoesNotThrow(() -> {
            String tituloSanitizado = SecurityConfig.processarEntrada(tituloMalicioso);

            assertAll(() -> {
                assertFalse(tituloSanitizado.contains("<script>"));
                assertFalse(tituloSanitizado.contains("DROP TABLE"));
                assertFalse(tituloSanitizado.contains("\u0000"));
                assertFalse(tituloSanitizado.contains("../"));
                assertFalse(tituloSanitizado.contains("UNION SELECT"));
            });
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "João<script>alert('hack')</script>Silva",
            "José'; DELETE FROM users; --Santos",
            "Maria\u0000\u0001\u0002Oliveira",
            "Pedro../../../etc/passwdSouza",
            "AnaUNION SELECT passwordCosta"
    })
    @DisplayName("Autores com conteúdo malicioso devem ser sanitizados")
    void testAutoresSanitizados(String autorMalicioso) {
        assertDoesNotThrow(() -> {
            String autorSanitizado = SecurityConfig.processarEntrada(autorMalicioso);

            assertAll(() -> {
            assertFalse(autorSanitizado.contains("<script>"));
            assertFalse(autorSanitizado.contains("DELETE FROM"));
            assertFalse(autorSanitizado.contains("\u0000"));
            assertFalse(autorSanitizado.contains("../"));
            assertFalse(autorSanitizado.contains("UNION SELECT"));
            assertFalse(autorSanitizado.isEmpty());
        });
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "9781234567890<script>alert('xss')</script>",
            "9781234567890'; DROP TABLE books; --",
            "9781234567890\u0000\u0001\u0002",
            "978123456789../../../etc/passwd0"
    })
    @DisplayName("ISBNs com conteúdo malicioso devem ser sanitizados")
    void testIsbnsSanitizados(String isbnMalicioso) {
        assertDoesNotThrow(() -> {
            String isbnSanitizado = SecurityConfig.processarEntrada(isbnMalicioso);

            assertAll(() -> {
                assertFalse(isbnSanitizado.contains("<script>"));
                assertFalse(isbnSanitizado.contains("DROP TABLE"));
                assertFalse(isbnSanitizado.contains("\u0000"));
                assertFalse(isbnSanitizado.contains("../"));
                assertTrue(isbnSanitizado.matches("^\\d+$") || isbnSanitizado.length() >= 10);
            });
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Livro<b>Negrito</b>Título",
            "História & Aventura",
            "Título com \"aspas\" simples",
            "Nome com 'apostrofe' normal",
            "Texto com\ttab\ne\rquebras"
    })
    @DisplayName("Entradas com formatação especial devem ser sanitizadas preservando conteúdo válido")
    void testSanitizacaoFormatacao(String entradaFormatada) {
        assertDoesNotThrow(() -> {
            String entradaSanitizada = SecurityConfig.processarEntrada(entradaFormatada);

            assertAll(() -> {
                assertFalse(entradaSanitizada.contains("<b>"));
                assertFalse(entradaSanitizada.contains("</b>"));
                assertFalse(entradaSanitizada.trim().isEmpty());
                assertFalse(entradaSanitizada.contains("\t"));
                assertFalse(entradaSanitizada.contains("\n"));
                assertFalse(entradaSanitizada.contains("\r"));
            });
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Busca<script>maliciosa</script>",
            "Termo'; SELECT * FROM livros; --",
            "Pesquisa\u0000com\u0001caracteres\u0002inválidos"
    })
    @DisplayName("Termos de busca devem ser sanitizados antes da pesquisa")
    void testBuscaSanitizada(String termoBusca) {
        // Cria livro válido no acervo
        Livro livro = new Livro(service.gerarId(), "Título Teste", "Autor Teste", "9781234567890");
        service.cadastrarLivroNoAcervo(livro);

        // Verifica se busca com termo malicioso não quebra o sistema
        assertDoesNotThrow(() -> {
            service.buscarLivroPorTituloNoAcervo(termoBusca);
            service.buscarLivroPorAutorNoAcervo(termoBusca);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Título Atualizado<script>alert('hack')</script>",
            "Autor'; UPDATE usuarios SET senha='hack'; --",
            "9781234567890\u0000\u0001malicioso"
    })
    @DisplayName("Atualizações de livro devem sanitizar todas as entradas")
    void testAtualizacaoSanitizada(String entradaMaliciosa) {
        // Cria livro válido
        Livro livro = new Livro(service.gerarId(), "Título Original", "Autor Original", "9781234567890");
        service.cadastrarLivroNoAcervo(livro);

        // Verifica se atualização sanitiza entradas maliciosas
        assertDoesNotThrow(() -> {
            service.atualizarLivroDoAcervo(
                    livro.getId(),
                    entradaMaliciosa,
                    entradaMaliciosa,
                    "9780987654321"
            );

            Livro livroAtualizado = service.buscarLivroPorIDNoAcervo(livro.getId());
            assertFalse(livroAtualizado.getTitulo().contains("<script>"));
            assertFalse(livroAtualizado.getAutor().contains("UPDATE"));
        });
    }


}
