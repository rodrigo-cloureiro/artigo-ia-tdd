package com.biblioteca.service;

import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LivroServiceTest {

    private LivroRepository repository;
    private LivroService service;

    @BeforeEach
    void setUp() {
        repository = mock(LivroRepository.class);
        EmprestimoService emprestimoService = mock(EmprestimoService.class);
        service = new LivroService(repository, emprestimoService);
    }

    @Test
    @DisplayName("Deve cadastrar livro com sucesso")
    void cadastrarLivroComSucesso() {
        Livro livro = new Livro("Título", "Autor", "9788535930001");
        when(repository.salvar(livro)).thenReturn(livro);

        Livro resultado = service.cadastrarLivro(livro);

        assertNotNull(resultado);
        verify(repository).salvar(livro);
    }

    @Test
    @DisplayName("Deve buscar livro por ID")
    void buscarLivroPorId() {
        Long id = 1L;
        Livro livro = new Livro("Título", "Autor", "9788535930001");
        when(repository.buscarPorId(id)).thenReturn(java.util.Optional.of(livro));

        var resultado = service.buscarPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals(livro, resultado.get());
    }

    @Test
    @DisplayName("Deve retornar vazio quando livro não encontrado por ID")
    void buscarLivroPorIdNaoEncontrado() {
        Long id = 999L;
        when(repository.buscarPorId(id)).thenReturn(java.util.Optional.empty());

        var resultado = service.buscarPorId(id);

        assertTrue(resultado.isEmpty());
    }
}