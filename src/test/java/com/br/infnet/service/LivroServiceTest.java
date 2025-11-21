package com.br.infnet.service;

import com.br.infnet.model.Livro;
import com.br.infnet.repository.interfaces.iLivroRepository;
import net.jqwik.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LivroServiceTest {
    private iLivroRepository mockRepository;
    private LivroService livroService;

    @BeforeEach
    public void setup() {
        mockRepository = mock(iLivroRepository.class);
        livroService = new LivroService(mockRepository);
    }

    //----------------------------------TESTES CADASTRO------------------------------------//
    @Test
    @DisplayName("Deve cadastrar livro válido no acervo")
    void cadastrarLivroNoAcervo() {
        Livro livro = new Livro(1, "Clean Code", "Robert Martin", "9780132350884");
        when(mockRepository.existeISBN(livro.getIsbn())).thenReturn(false);
        livroService.cadastrarLivroNoAcervo(livro);
        verify(mockRepository, times(1)).salvarLivro(livro);
    }

    @Test
    @DisplayName("Não deve cadastrar livro nulo")
    void cadastrarLivroNulo() {
        Livro livro = null;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> livroService.cadastrarLivroNoAcervo(livro));
        assertEquals("Livro não pode ser nulo", exception.getMessage());
        verify(mockRepository, never()).salvarLivro(any(Livro.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar livro com ISBN duplicado")
    void naoDeveCadastrarLivroComISBNDuplicado() {
        Livro livro = new Livro(1, "Titulo", "Autor", "1234567890123");
        when(mockRepository.existeISBN("1234567890123")).thenReturn(true);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> livroService.cadastrarLivroNoAcervo(livro));
        assertEquals("Já existe um livro cadastrado com este ISBN", exception.getMessage());
        verify(mockRepository, never()).salvarLivro(any(Livro.class));
    }

    //----------------------------------TESTES LISTAR------------------------------------//
    @Test
    @DisplayName("Deve buscar livro por ID existente")
    void buscarLivroPorIDNoAcervo() {
        Livro livro = new Livro(1, "Teste", "Teste", "1234567890123");
        when(mockRepository.buscarLivroPorId(1)).thenReturn(livro);
        Livro encontrado = livroService.buscarLivroPorIDNoAcervo(1);
        assertNotNull(encontrado);
        assertEquals("Teste", encontrado.getTitulo());
        verify(mockRepository).buscarLivroPorId(1);
    }

    @Test
    @DisplayName("Deve listar todos os livros cadastrados do acervo")
    void listarLivrosDoAcervo() {
        Livro livro1 = new Livro(1, "Livro1", "AutorA", "1111111111111");
        Livro livro2 = new Livro(2, "Livro2", "AutorB", "2222222222222");
        when(mockRepository.listarLivros()).thenReturn(List.of(livro1, livro2));
        List<Livro> lista = livroService.listarLivrosDoAcervo();
        assertEquals(2, lista.size());
        verify(mockRepository).listarLivros();
    }

    @Test
    @DisplayName("Deve lançar exceção para ID inexistente (livro não cadastrado no sistema)")
    void buscarLivroPorIDInexistente() {
        when(mockRepository.buscarLivroPorId(999)).thenReturn(null);
        assertThrows(NoSuchElementException.class, () -> livroService.buscarLivroPorIDNoAcervo(999));
    }

    @Test
    @DisplayName("Deve listar todos os livros com o mesmo título")
    void buscarLivrosPeloMesmoTituloNoAcervo() {
        Livro livro1 = new Livro(1, "Java", "AutorA", "1111111111111");
        Livro livro2 = new Livro(2, "Java", "AutorB", "2222222222222");
        when(mockRepository.listarLivrosPorTitulo("Java")).thenReturn(List.of(livro1, livro2));
        List<Livro> encontrados = livroService.buscarLivroPorTituloNoAcervo("Java");
        assertEquals(2, encontrados.size());
        verify(mockRepository).listarLivrosPorTitulo("Java");
    }

    @Test
    @DisplayName("Deve lançar exceção para título nulo ou vazio")
    void buscarLivroPorTituloInvalido() {
        assertThrows(IllegalArgumentException.class, () -> livroService.buscarLivroPorTituloNoAcervo(null));
        assertThrows(IllegalArgumentException.class, () -> livroService.buscarLivroPorTituloNoAcervo(""));
        assertThrows(IllegalArgumentException.class, () -> livroService.buscarLivroPorTituloNoAcervo("   "));
        verifyNoInteractions(mockRepository);
    }

    @Test
    @DisplayName("Deve listar todos os livros pelo mesmo autor")
    void buscarLivroPorAutorNoAcervo() {
        Livro livro1 = new Livro(1, "Livro1", "Juquinha Baiano", "1111111111111");
        Livro livro2 = new Livro(2, "Livro2", "Juquinha Baiano", "2222222222222");
        when(mockRepository.listarLivrosPorAutor("Juquinha Baiano")).thenReturn(List.of(livro1, livro2));
        List<Livro> encontrados = livroService.buscarLivroPorAutorNoAcervo("Juquinha Baiano");
        assertEquals(2, encontrados.size());
        verify(mockRepository).listarLivrosPorAutor("Juquinha Baiano");
    }

    @Test
    @DisplayName("Deve lançar exceção para autor nulo ou vazio")
    void buscarLivroPorAutorInvalido() {
        assertThrows(IllegalArgumentException.class, () -> livroService.buscarLivroPorAutorNoAcervo(null));
        assertThrows(IllegalArgumentException.class, () -> livroService.buscarLivroPorAutorNoAcervo(""));
        assertThrows(IllegalArgumentException.class, () -> livroService.buscarLivroPorAutorNoAcervo("   "));
        verifyNoInteractions(mockRepository);
    }

    //----------------------------------TESTES ATUALIZAÇÃO------------------------------------//
    @Test
    @DisplayName("Deve atualizar nome de livro existente")
    void atualizarNomeDeLivroDoAcervo() {
        Livro livro = new Livro(1, "Título Original", "Autor", "1234567890123");
        livro.setDisponivel(true);
        when(mockRepository.buscarLivroPorId(1)).thenReturn(livro);
        when(mockRepository.existeISBN("1234567890123")).thenReturn(false);
        livroService.atualizarLivroDoAcervo(1, "Novo Título", "Autor", "1234567890123");
        verify(mockRepository).atualizarLivro(any(Livro.class));
    }

    @Test
    @DisplayName("Deve atualizar autor de livro existente")
    void atualizarAutorDeLivroDoAcervo() {
        Livro livro = new Livro(1, "Título", "Autor Original", "1234567890123");
        livro.setDisponivel(true);
        when(mockRepository.existeISBN("1234567890123")).thenReturn(false);
        when(mockRepository.buscarLivroPorId(1)).thenReturn(livro);
        livroService.atualizarLivroDoAcervo(1, "Título", "Novo Autor", "1234567890123");
        verify(mockRepository).atualizarLivro(any(Livro.class));
    }

    @Test
    @DisplayName("Deve atualizar ISBN de livro existente")
    void atualizarIsbnDeLivroDoAcervo() {
        Livro livro = new Livro(1, "Título", "Autor", "1234567890123");
        livro.setDisponivel(true);
        when(mockRepository.buscarLivroPorId(1)).thenReturn(livro);
        when(mockRepository.existeISBN("9876543210987")).thenReturn(false);
        livroService.atualizarLivroDoAcervo(1, "Título", "Autor", "9876543210987");
        verify(mockRepository).atualizarLivro(any(Livro.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar livro não cadastrado")
    void atualizarLivroInexistente() {
        when(mockRepository.buscarLivroPorId(999)).thenReturn(null);
        assertThrows(NoSuchElementException.class, () -> livroService.atualizarLivroDoAcervo(999, "Teste", "Teste", "1234567890123"));
        verify(mockRepository, never()).atualizarLivro(any(Livro.class));
    }

    @Test
    @DisplayName("Não deve permitir atualizar livro emprestado")
    void naoDeveAtualizarLivroEmprestado() {
        Livro livroEmprestado = new Livro(1, "Título", "Autor", "1234567890123");
        livroEmprestado.setDisponivel(false);
        when(mockRepository.buscarLivroPorId(1)).thenReturn(livroEmprestado);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> livroService.atualizarLivroDoAcervo(1, "Novo Título", "Novo Autor", "9876543210987"));
        assertEquals("Livro está emprestado e não pode ser atualizado", exception.getMessage());
        verify(mockRepository).buscarLivroPorId(1);
        verify(mockRepository, never()).atualizarLivro(any(Livro.class));
    }

    @Test
    @DisplayName("Deve atualizar livro disponível com sucesso")
    void atualizarLivroDisponivel() {
        Livro livroExistente = new Livro(1, "Título Original", "Autor Original", "1234567890123");
        livroExistente.setDisponivel(true);
        when(mockRepository.buscarLivroPorId(1)).thenReturn(livroExistente);
        when(mockRepository.buscarLivroPorISBN("1234567890123")).thenReturn(livroExistente);
        livroService.atualizarLivroDoAcervo(1, "Título Atualizado", "Autor Atualizado", "1234567890123");
        verify(mockRepository).atualizarLivro(any(Livro.class));
    }

    //----------------------------------TESTES REMOÇÃO------------------------------------//
    @Test
    @DisplayName("Deve efetivamente remover livro do acervo")
    void removerLivroDoAcervo() {
        Livro livro = new Livro(1, "Teste", "Teste", "1234567890123");
        livro.setDisponivel(true);
        when (mockRepository.buscarLivroPorId(1)).thenReturn(livro);
        livroService.removerLivroDoAcervo(1);
        verify(mockRepository).removerLivro(1);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover livro não cadastrado")
    void removerLivroInexistente() {
        assertThrows(NoSuchElementException.class, () -> livroService.removerLivroDoAcervo(999));
        verify(mockRepository, never()).removerLivro(anyInt());
    }

    //*----------------------------------TESTES PARAMETRIZADOS------------------------------------*//
    @Provide
    Arbitrary<Livro> livros() {
        return Combinators.combine(
                ids(),
                titulo(),
                autor(),
                isbn()
        ).as((id, titulo, autor, isbn) -> id != null ? new Livro(id, titulo, autor, isbn) : null);
    }

    @Provide
    Arbitrary<Integer> ids() {
        return Arbitraries.integers().between(4, 1000);
    }

    @Provide
    Arbitrary<String> isbn() {
        return Arbitraries.strings().withCharRange('0', '9')
                .ofLength(13);
    }

    @Provide
    Arbitrary<String> titulo() {
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .withCharRange('A', 'Z')
                .withCharRange('0', '9')
                .withChars('á', 'à', 'ã', 'â', 'é', 'ê', 'í', 'ó', 'ô', 'õ', 'ú', 'ç')
                .withChars('Á', 'À', 'Ã', 'Â', 'É', 'Ê', 'Í', 'Ó', 'Ô', 'Õ', 'Ú', 'Ç')
                .withChars('-', '.', '!', '?', ',', ';', ':', '(', ')', '[', ']', '"', '\'')
                .withChars(' ')
                .ofMinLength(3)
                .ofMaxLength(100)
                .filter(s -> s.trim().length() >= 3);
    }

    @Provide
    Arbitrary<String> autor() {
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .withCharRange('A', 'Z')
                .withChars('à', 'á', 'â', 'ã', 'ä', 'å', 'æ', 'ç', 'è', 'é', 'ê', 'ë', 'ì', 'í', 'î', 'ï')
                .withChars('ð', 'ñ', 'ò', 'ó', 'ô', 'õ', 'ö', 'ø', 'ù', 'ú', 'û', 'ü', 'ý', 'þ', 'ÿ')
                .withChars('À', 'Á', 'Â', 'Ã', 'Ä', 'Å', 'Æ', 'Ç', 'È', 'É', 'Ê', 'Ë', 'Ì', 'Í', 'Î', 'Ï')
                .withChars('Ð', 'Ñ', 'Ò', 'Ó', 'Ô', 'Õ', 'Ö', 'Ø', 'Ù', 'Ú', 'Û', 'Ü', 'Ý', 'Þ')
                .withChars('-', '.', ',', ' ')
                .ofMinLength(3)
                .ofMaxLength(50)
                .filter (s -> s.trim().length() >= 3);
    }

    @Property(tries = 50)
    void testCadastrarLivrosValidos(@ForAll("livros") Livro livro) {
        iLivroRepository localMockRepository = mock(iLivroRepository.class);
        LivroService localLivroService = new LivroService(localMockRepository);

        when(localMockRepository.existeISBN(livro.getIsbn())).thenReturn(false);
        assertDoesNotThrow(() -> localLivroService.cadastrarLivroNoAcervo(livro));
        verify(localMockRepository, times(1)).salvarLivro(livro);
    }

    @Property(tries = 50)
    void testBuscarLivroPorIdInexistente(@ForAll("ids") int id) {
        iLivroRepository localMockRepository = mock(iLivroRepository.class);
        LivroService localLivroService = new LivroService(localMockRepository);

        when(localMockRepository.buscarLivroPorId(id)).thenReturn(null);
        assertThrows(NoSuchElementException.class, () ->
                localLivroService.buscarLivroPorIDNoAcervo(id)
        );
    }
}
