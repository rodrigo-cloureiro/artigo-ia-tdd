package com.br.infnet.service;

import com.br.infnet.model.Livro;
import com.br.infnet.repository.interfaces.iLivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@DisplayName("Testes de Cenários de Falha - LivroService")
public class LivroServiceFailureTest {
        @Mock
        private iLivroRepository mockRepository;

        private LivroService livroService;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            livroService = new LivroService(mockRepository);
        }

        @Test
        @DisplayName("Deve lançar exceção quando repositório falha ao buscar")
        void testFalhaNaBusca() {
            when(mockRepository.buscarLivroPorId(1))
                    .thenThrow(new RuntimeException("Falha na conexão"));

            assertThrows(RuntimeException.class, () -> livroService.buscarLivroPorIDNoAcervo(1));
        }

        @Test
        @DisplayName("Deve lançar exceção quando repositório falha ao salvar")
        void testFalhaNoSalvamento() {
            Livro livro = new Livro(1, "Teste", "Teste", "1234567890123");

            when(mockRepository.existeISBN("1234567890123")).thenReturn(false);
            doThrow(new RuntimeException("Falha ao salvar livro no banco"))
                    .when(mockRepository).salvarLivro(livro);

            assertThrows(RuntimeException.class, () -> livroService.cadastrarLivroNoAcervo(livro));
        }
    }
