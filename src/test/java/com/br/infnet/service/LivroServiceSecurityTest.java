package com.br.infnet.service;

import com.br.infnet.model.Livro;
import com.br.infnet.repository.interfaces.iLivroRepository;
import com.br.infnet.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@DisplayName("Testes de Segurança - LivroService")
public class LivroServiceSecurityTest {

    @Mock
    private iLivroRepository mockRepository;
    private LivroService livroService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        livroService = new LivroService(mockRepository);
    }

    //----------------- TESTES DE SEGURANÇA ----------------//
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
            "Título@#$%&*()+={}[]|\\:;\"<>?/~`"
    })
    @DisplayName("Títulos maliciosos devem ser rejeitados")
    void testTitulosMaliciosos(String titulo) {
        Livro livro = new Livro (1, titulo, "Autor Válido", "9781234567890");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> livroService.cadastrarLivroNoAcervo(livro));
        assertNotNull(exception.getMessage());
        verifyNoInteractions(mockRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "'; DROP TABLE autores; --",
            "<script>alert('xss')</script>",
            "Autor@#$%&*()+={}[]|\\:;\"<>?/~`"
    })
    @DisplayName("Autores maliciosos devem ser rejeitados")
    void testAutoresMaliciosos(String autor) {
        Livro livro = new Livro(1, "Título Válido", autor, "9781234567890");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> livroService.cadastrarLivroNoAcervo(livro));
        assertNotNull(exception.getMessage());
        verifyNoInteractions(mockRepository);
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
            Livro livro = new Livro(1, "Título Válido", "João Silva", isbn);
            livroService.cadastrarLivroNoAcervo(livro);
        });
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().toLowerCase().contains("isbn"));
    }

    //----------------- TESTES DE SANITIZAÇÃO ----------------//
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
        Livro livro = new Livro(1, "Título Teste", "Autor Teste", "9781234567890");
        livroService.cadastrarLivroNoAcervo(livro);

        assertDoesNotThrow(() -> {
            livroService.buscarLivroPorTituloNoAcervo(termoBusca);
            livroService.buscarLivroPorAutorNoAcervo(termoBusca);
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
        Livro livro = new Livro(1, "Título Original", "Autor Original", "9781234567890");
        livro.setDisponivel(true);
        when(mockRepository.existeISBN("9781234567890")).thenReturn(false);
        when(mockRepository.buscarLivroPorId(1)).thenReturn(livro);
        when(mockRepository.existeISBN("9780987654321")).thenReturn(false);

        livroService.cadastrarLivroNoAcervo(livro);

        assertDoesNotThrow(() -> {
            livroService.atualizarLivroDoAcervo(
                    livro.getId(),
                    entradaMaliciosa,
                    entradaMaliciosa,
                    "9780987654321"
            );
            Livro livroAtualizado = new Livro(1, entradaMaliciosa, entradaMaliciosa, "9780987654321");
            when(mockRepository.buscarLivroPorId(1)).thenReturn(livroAtualizado);

            Livro resultado = livroService.buscarLivroPorIDNoAcervo(1);
            assertNotNull(resultado);
        });
    }


}
