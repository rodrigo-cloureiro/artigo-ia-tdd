package com.br.infnet.service;

import com.br.infnet.model.Livro;
import net.jqwik.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class LivroServiceTest {
    private LivroService service;

    @BeforeEach
    void setup() {
        service = new LivroService();
    }

//*----------------------------------TESTES UNITÁRIOS------------------------------------*//

    @Test
    @DisplayName("Deve gerar um ID incremental a partir de zero")
    void testGerarId() {
        int id1 = service.gerarId();
        int id2 = service.gerarId();
        assertEquals(4, id1);
        assertEquals(5, id2);
        assertEquals(id1 + 1, id2);
    }

    @Test
    @DisplayName("Deve cadastrar livro válido no acervo")
    void cadastrarLivroNoAcervo() {
        Livro livro = new Livro(service.gerarId(), "Clean Code", "Robert Martin", "9780132350884");
        service.cadastrarLivroNoAcervo(livro);
        assertEquals(4, service.listarLivrosDoAcervo().size());
        assertEquals(livro, service.buscarLivroPorIDNoAcervo(livro.getId()));
    }

    @Test
    @DisplayName("Não deve cadastrar livro nulo")
    void cadastrarLivroNulo() {
        assertThrows(IllegalArgumentException.class, () -> service.cadastrarLivroNoAcervo(null));
    }

    @Test
    @DisplayName("Deve buscar livro por ID existente")
    void buscarLivroPorIDNoAcervo() {
        Livro livro = new Livro(service.gerarId(), "Teste", "Teste", "1234567890123");
        service.cadastrarLivroNoAcervo(livro);
        Livro encontrado = service.buscarLivroPorIDNoAcervo(livro.getId());
        assertNotNull(encontrado);
        System.out.println("Livro encontrado: " + encontrado);
        assertEquals(livro.getTitulo(), encontrado.getTitulo());
    }

    @Test
    @DisplayName("Deve lançar exceção para ID inexistente (livro não cadastrado no sistema)")
    void buscarLivroPorIDInexistente() {
        assertThrows(NoSuchElementException.class, () -> service.buscarLivroPorIDNoAcervo(999));
    }

    @Test
    @DisplayName("Deve listar todos os livros com o mesmo título")
    void buscarLivroPorTituloNoAcervo() {
        Livro livro1 = new Livro(service.gerarId(), "Java", "AutorA", "1111111111111");
        Livro livro2 = new Livro(service.gerarId(), "Java", "AutorB", "2222222222222");
        service.cadastrarLivroNoAcervo(livro1);
        service.cadastrarLivroNoAcervo(livro2);
        ArrayList<Livro> encontrados = service.buscarLivroPorTituloNoAcervo("Java");
        assertEquals(2, encontrados.size());
    }

    @Test
    @DisplayName("Deve lançar exceção para título nulo ou vazio")
    void buscarLivroPorTituloInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.buscarLivroPorTituloNoAcervo(null);
            service.buscarLivroPorTituloNoAcervo("");
            service.buscarLivroPorTituloNoAcervo("   ");
        });
    }

    @Test
    @DisplayName("Deve listar todos os livros pelo mesmo autor")
    void buscarLivroPorAutorNoAcervo() {
        Livro livro1 = new Livro(service.gerarId(), "Livro1", "Juquinha Baiano", "1111111111111");
        Livro livro2 = new Livro(service.gerarId(), "Livro2", "Juquinha Baiano", "2222222222222");
        service.cadastrarLivroNoAcervo(livro1);
        service.cadastrarLivroNoAcervo(livro2);
        ArrayList<Livro> encontrados = service.buscarLivroPorAutorNoAcervo("Juquinha Baiano");
        assertEquals(2, encontrados.size());
    }

    @Test
    @DisplayName("Deve lançar exceção para autor nulo ou vazio")
    void buscarLivroPorAutorInvalido() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.buscarLivroPorAutorNoAcervo(null);
            service.buscarLivroPorAutorNoAcervo("");
            service.buscarLivroPorAutorNoAcervo("   ");
        });
    }

    @Test
    @DisplayName("Deve atualizar nome de livro existente")
    void atualizarNomeDeLivroDoAcervo() {
        Livro livro = new Livro(service.gerarId(), "Título Original", "Autor", "1234567890123");
        service.cadastrarLivroNoAcervo(livro);

        service.atualizarLivroDoAcervo(livro.getId(), "Novo Título", "Autor", "1234567890123");

        Livro atualizado = service.buscarLivroPorIDNoAcervo(livro.getId());
        assertEquals("Novo Título", atualizado.getTitulo());
    }

    @Test
    @DisplayName("Deve atualizar autor de livro existente")
    void atualizarAutorDeLivroDoAcervo() {
        Livro livro = new Livro(service.gerarId(), "Título", "Autor Original", "1234567890123");
        service.cadastrarLivroNoAcervo(livro);

        service.atualizarLivroDoAcervo(livro.getId(), "Título", "Novo Autor", "1234567890123");

        Livro atualizado = service.buscarLivroPorIDNoAcervo(livro.getId());
        assertEquals("Novo Autor", atualizado.getAutor());
    }

    @Test
    @DisplayName("Deve atualizar ISBN de livro existente")
    void atualizarIsbnDeLivroDoAcervo() {
        Livro livro = new Livro(service.gerarId(), "Título", "Autor", "1234567890123");
        service.cadastrarLivroNoAcervo(livro);

        service.atualizarLivroDoAcervo(livro.getId(), "Título", "Autor", "9876543210987");

        Livro atualizado = service.buscarLivroPorIDNoAcervo(livro.getId());
        assertEquals("9876543210987", atualizado.getIsbn());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar livro não cadastrado")
    void atualizarLivroInexistente() {
        assertThrows(NoSuchElementException.class, () ->
                service.atualizarLivroDoAcervo(999, "Teste", "Teste", "1234567890123"));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar com ISBN duplicado")
    void atualizarLivroComISBNDuplicado() {
        Livro livro1 = new Livro(service.gerarId(), "Livro 1", "Autor A", "1111111111111");
        Livro livro2 = new Livro(service.gerarId(), "Livro 2", "Autor B", "2222222222222");

        service.cadastrarLivroNoAcervo(livro1);
        service.cadastrarLivroNoAcervo(livro2);

        assertThrows(IllegalArgumentException.class, () ->
                service.atualizarLivroDoAcervo(livro2.getId(), "Livro 2", "Autor B", "1111111111111"));
    }

    @Test
    @DisplayName("Deve efetivamente remover livro do acervo")
    void removerLivroDoAcervo() {
        Livro livro = new Livro(service.gerarId(), "Teste", "Teste", "1234567890123");
        service.cadastrarLivroNoAcervo(livro);
        service.removerLivroDoAcervo(livro.getId());
        assertEquals(3, service.listarLivrosDoAcervo().size());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover livro não cadastrado")
    void removerLivroInexistente() {
        assertThrows(NoSuchElementException.class, () -> service.removerLivroDoAcervo(999));
    }

    @Test
    @DisplayName("Deve listar todos os livros cadastrados do acervo")
    void listarLivrosDoAcervo() {
        Livro livro1 = new Livro(service.gerarId(), "Livro1", "AutorA", "1111111111111");
        Livro livro2 = new Livro(service.gerarId(), "Livro2", "AutorB", "2222222222222");
        service.cadastrarLivroNoAcervo(livro1);
        service.cadastrarLivroNoAcervo(livro2);
        ArrayList<Livro> lista = service.listarLivrosDoAcervo();

        assertEquals(5, lista.size());
    }

    @Test
    @DisplayName("Deve emprestar livro disponível")
    void emprestarLivroDisponivel() {
        Livro livro = new Livro(service.gerarId(), "Teste", "Teste", "1234567890123");
        service.cadastrarLivroNoAcervo(livro);
        service.emprestarLivro(livro.getId(), 7);
        System.out.println("Data Empréstimo: " + livro.getDataEmprestimo());
        System.out.println("Data Estimada Devolução: " + livro.getDataEstimadaDevolucao());
        assertFalse(livro.isDisponivel());
        assertNotNull(livro.getDataEmprestimo());
        assertEquals(7, livro.getPrazoDevolucao());
    }

    @Test
    @DisplayName("Deve lançar exceção caso o prazo de devolução seja negativo")
    void emprestarLivroPrazoNegativo() {
        Livro livro = new Livro(service.gerarId(), "Teste", "Teste", "1234567890123");
        service.cadastrarLivroNoAcervo(livro);
        assertThrows(IllegalArgumentException.class, () -> service.emprestarLivro(livro.getId(), -5));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar emprestar livro indisponível")
    void emprestarLivroIndisponivel() {
        Livro livro = new Livro(service.gerarId(), "Teste", "Teste", "1234567890123");
        service.cadastrarLivroNoAcervo(livro);
        service.emprestarLivro(livro.getId(), 7);
        assertThrows(IllegalStateException.class, () -> service.emprestarLivro(livro.getId(), 5));
    }

    @Test
    @DisplayName("Deve devolver livro sem multa")
    void devolverLivroSemMulta() throws MultaPendenteException {
        Livro livro = new Livro(service.gerarId(), "Teste", "Teste", "1234567890123");
        service.cadastrarLivroNoAcervo(livro);
        service.emprestarLivro(livro.getId(), 10);
        //presumindo que a devolução está sendo feita na data estimada, que é menor que 11 dias
        livro.setDataEfetivaDevolucao(livro.getDataEstimadaDevolucao());
        service.devolverLivro(livro.getId());
        assertTrue(livro.isDisponivel());
        assertEquals(0, livro.getMulta());
    }

    @Test
    @DisplayName("Deve lançar exceção ao devolver livro com multa e o livro não deve ser devolvido")
    void devolverLivroComMulta() {
        Livro livro = new Livro(service.gerarId(), "Teste", "Teste", "1234567890123");
        service.cadastrarLivroNoAcervo(livro);
        service.emprestarLivro(livro.getId(), 20);
        livro.setDataEfetivaDevolucao(livro.getDataEmprestimo().plusDays(20));
        assertThrows(MultaPendenteException.class, () -> service.devolverLivro(livro.getId()));
        assertFalse(livro.isDisponivel());
    }

    @Test
    @DisplayName("Deve calcular a multa corretamente após o prazo gratuito")
    void calcularMulta() {
        Livro livro = new Livro(service.gerarId(), "Teste", "Teste", "1234567890123");
        service.cadastrarLivroNoAcervo(livro);
        service.emprestarLivro(livro.getId(), 18);
        livro.setDataEfetivaDevolucao(livro.getDataEmprestimo().plusDays(18));
        double multa = service.calcularMulta(livro.getId());
        //lembrando que a multa é calculada apenas a partir do 11º dia
        assertEquals(9, multa);
    }

//*----------------------------------TESTES PARAMETRIZADOS------------------------------------*//

    @Provide
    Arbitrary<Livro> livros() {
        return Combinators.combine(
                ids(),
                titulo(),
                autor(),
                isbn()
        ).as((id,titulo, autor, isbn) -> {
            LivroService tempService = new LivroService();
            return new Livro(id, titulo, autor, isbn);
        });
    }

    @Provide
    Arbitrary<LocalDate> data() {
        return Combinators.combine(
                Arbitraries.integers().between(2024, 2025),
                Arbitraries.integers().between(1, 12),
                Arbitraries.integers().between(1, 28)
        ).as((ano, mes, dia) -> {
            try {
                return LocalDate.of(ano, mes, dia);
            } catch (Exception e) {
                return LocalDate.of(ano, mes, Math.min(dia, 28));
            }
        });
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



    @Property(tries = 1000)
    void testAdicionarLivrosVariadosAoAcervo(@ForAll("livros") Livro livro) {
        LivroService tempService = new LivroService();
        tempService.cadastrarLivroNoAcervo(livro);
        System.out.println("Livro adicionado: " + livro);
        assertNotNull(tempService.buscarLivroPorIDNoAcervo(livro.getId()));
        assertEquals(4, tempService.listarLivrosDoAcervo().size());
    }

    @Property (tries = 1000)
    void testBuscarLivroPorIdInvalidaDeveLancarExcecao(@ForAll("ids") int id) {
        LivroService tempService = new LivroService();
        assertThrows(NoSuchElementException.class, () -> tempService.buscarLivroPorIDNoAcervo(id));
    }

    @Property()
    void testCalcularMultaComCenariosVariados(@ForAll("livros") Livro livro, @ForAll("data") LocalDate dataEmprestimo) {
        Assume.that(dataEmprestimo.isBefore(LocalDate.of(2025, 7, 1)));

        LivroService tempService = new LivroService();
        tempService.cadastrarLivroNoAcervo(livro);
        tempService.emprestarLivro(livro.getId(), 10);

        livro.setDataEmprestimo(dataEmprestimo);
        livro.setDataEstimadaDevolucao(dataEmprestimo.plusDays(10));
        LocalDate dataDevolucao = dataEmprestimo.plusDays((int)(Math.random() * 20));
        livro.setDataEfetivaDevolucao(dataDevolucao);
        long diasAtraso = livro.getDataEmprestimo().until(dataDevolucao).getDays();

        double multaEsperada;
        if (diasAtraso <= 10) {
            multaEsperada = 0.0;
        } else {
            multaEsperada = 5.0 + 0.5 * (diasAtraso - 10);
        }

        double multaCalculada = tempService.calcularMulta(livro.getId());
        System.out.println("Livro: " + livro.getTitulo() + " | Data Empréstimo: " + dataEmprestimo +
                " | Data Devolução: " + dataDevolucao + " | Dias desde empréstimo: " + diasAtraso +
                " | Multa Calculada: " + multaCalculada);
        assertEquals(multaEsperada, multaCalculada, 0.01);
    }
}