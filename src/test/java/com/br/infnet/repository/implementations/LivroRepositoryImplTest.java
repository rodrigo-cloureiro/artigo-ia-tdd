package com.br.infnet.repository.implementations;

import com.br.infnet.model.Livro;
import com.br.infnet.repository.interfaces.iLivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes do LivroRepositoryImpl")
class LivroRepositoryImplTest {

    private iLivroRepository livroRepository;

    @BeforeEach
    void setUp() {
        livroRepository = new LivroRepositoryImpl();
    }

    @Test
    @DisplayName("Deve salvar um livro com sucesso")
    void testSalvarLivro() {
        Livro livro = new Livro(1, "1984", "George Orwell", "9780451524935");
        livroRepository.salvarLivro(livro);

        List<Livro> livros = livroRepository.listarLivros();
        assertTrue(livros.stream().anyMatch(l ->
                l.getTitulo().equals("1984") &&
                        l.getAutor().equals("George Orwell") &&
                        l.getIsbn().equals("9780451524935")));
    }

    @Test
    @DisplayName("Deve buscar livro por ID existente")
    void testBuscarLivroPorIdExistente() {
        Livro livro = new Livro(1, "Dom Casmurro", "Machado de Assis", "9788525406958");
        livroRepository.salvarLivro(livro);

        List<Livro> livros = livroRepository.listarLivros();
        Livro livroCriado = livros.stream()
                .filter(l -> l.getTitulo().equals("Dom Casmurro"))
                .findFirst().orElse(null);

        assertNotNull(livroCriado);
        Livro resultado = livroRepository.buscarLivroPorId(livroCriado.getId());

        assertNotNull(resultado);
        assertEquals(livroCriado.getId(), resultado.getId());
        assertEquals("Dom Casmurro", resultado.getTitulo());
    }

    @Test
    @DisplayName("Deve retornar null ao buscar livro por ID inexistente")
    void testBuscarLivroPorIdInexistente() {
        Livro resultado = livroRepository.buscarLivroPorId(999);
        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve buscar livro por ISBN existente")
    void testBuscarLivroPorISBNExistente() {
        Livro livro = new Livro(1,"O Cortiço", "Aluísio Azevedo", "9788508133031");
        livroRepository.salvarLivro(livro);
        Livro resultado = livroRepository.buscarLivroPorISBN("9788508133031");
        assertNotNull(resultado);
        assertEquals("O Cortiço", resultado.getTitulo());
    }

    @Test
    @DisplayName("Deve retornar null ao buscar por ISBN inexistente")
    void testBuscarLivroPorISBNInexistente() {
        Livro resultado = livroRepository.buscarLivroPorISBN("9999999999999");
        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve listar todos os livros")
    void testListarLivros() {
        int tamanhoInicial = livroRepository.listarLivros().size();

        livroRepository.salvarLivro(new Livro(1,"Livro 1", "Autor 1", "9781234567890"));
        livroRepository.salvarLivro(new Livro(2, "Livro 2", "Autor 2", "9781234567891"));

        List<Livro> resultado = livroRepository.listarLivros();

        assertNotNull(resultado);
        assertEquals(tamanhoInicial + 2, resultado.size());
    }

    @Test
    @DisplayName("Deve listar livros por título")
    void testListarLivrosPorTitulo() {
        livroRepository.salvarLivro(new Livro(1, "Dom Casmurro", "Machado de Assis", "9788525406958"));
        livroRepository.salvarLivro(new Livro(2, "O Dom", "Outro Autor", "9781234567890"));

        List<Livro> resultado = livroRepository.listarLivrosPorTitulo("dom");

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(l ->
                l.getTitulo().toLowerCase().contains("dom")));
    }

    @Test
    @DisplayName("Deve listar livros por autor")
    void testListarLivrosPorAutor() {
        livroRepository.salvarLivro(new Livro(1, "Dom Casmurro", "Machado de Assis", "9788525406958"));
        livroRepository.salvarLivro(new Livro(2, "O Cortiço", "Aluísio Azevedo", "9788508133031"));

        List<Livro> resultado = livroRepository.listarLivrosPorAutor("machado");

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Machado de Assis", resultado.getFirst().getAutor());
    }

    @Test
    @DisplayName("Deve verificar se ISBN existe")
    void testExisteISBN() {
        livroRepository.salvarLivro(new Livro(1, "Teste", "Autor Teste", "9781234567890"));

        assertTrue(livroRepository.existeISBN("9781234567890"));
        assertFalse(livroRepository.existeISBN("9999999999999"));
    }

    @Test
    @DisplayName("Deve atualizar livro existente")
    void testAtualizarLivro() {
        Livro livro = new Livro(1, "Título Original", "Autor Original", "9781234567890");
        livroRepository.salvarLivro(livro);

        List<Livro> livros = livroRepository.listarLivros();
        Livro livroCriado = livros.stream()
                .filter(l -> l.getIsbn().equals("9781234567890"))
                .findFirst().orElse(null);

        assertNotNull(livroCriado);
        livroCriado.setTitulo("Título Atualizado");
        livroRepository.atualizarLivro(livroCriado);

        Livro livroAtualizado = livroRepository.buscarLivroPorId(livroCriado.getId());
        assertEquals("Título Atualizado", livroAtualizado.getTitulo());
    }

    @Test
    @DisplayName("Deve remover livro existente")
    void testRemoverLivro() {
        Livro livro = new Livro(1, "Livro para deletar", "Autor", "9781234567890");
        livroRepository.salvarLivro(livro);

        List<Livro> livros = livroRepository.listarLivros();
        Livro livroCriado = livros.stream()
                .filter(l -> l.getIsbn().equals("9781234567890"))
                .findFirst().orElse(null);

        assertNotNull(livroCriado);
        livroRepository.removerLivro(livroCriado.getId());

        assertNull(livroRepository.buscarLivroPorId(livroCriado.getId()));
    }

    @Test
    @DisplayName("Deve gerar IDs únicos sequenciais")
    void testGerarProximoId() {
        int id1 = livroRepository.gerarProximoId();
        int id2 = livroRepository.gerarProximoId();

        assertTrue(id2 > id1);
        assertEquals(1, id2 - id1);
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao buscar por título inexistente")
    void testListarLivrosPorTituloInexistente() {
        List<Livro> resultado = livroRepository.listarLivrosPorTitulo("título que não existe");

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao buscar por autor inexistente")
    void testListarLivrosPorAutorInexistente() {

        List<Livro> resultado = livroRepository.listarLivrosPorAutor("autor que não existe");

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
