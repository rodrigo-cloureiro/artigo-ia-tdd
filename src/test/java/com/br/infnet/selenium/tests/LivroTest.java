package com.br.infnet.selenium.tests;

import com.br.infnet.selenium.base.TestBase;
import com.br.infnet.selenium.pages.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LivroTest extends TestBase {

    @Test
    @Order(1)
    public void testCadastroLivroSucesso() {
        LivroListPage listPage = new LivroListPage(driver);
        listPage.clickNovoLivro();

        LivroFormPage formPage = new LivroFormPage(driver);
        formPage.preencherFormulario("Ilíada", "Homero", "9788563560568");
        formPage.clickSubmit();

        assertTrue(driver.getCurrentUrl().contains("/livros"));
        assertTrue(listPage.isLivroPresente("Ilíada"));
    }

    @ParameterizedTest
    @Order(2)
    @CsvSource({
            "'Grande Sertão: Veredas','João Guimarães Rosa','9788535909760'",
            "'Menino de Engenho','José Lins do Rego','9788535914870'",
            "'A Odisseia','Homero','9788535914887'"
    })
    public void testCadastroMultiplosLivros(String titulo, String autor, String isbn) {
        LivroListPage listPage = new LivroListPage(driver);
        int quantidadeInicial = listPage.getQuantidadeLivros();

        listPage.clickNovoLivro();

        LivroFormPage formPage = new LivroFormPage(driver);
        formPage.preencherFormulario(titulo, autor, isbn);
        formPage.clickSubmit();

        assertEquals(quantidadeInicial + 1, listPage.getQuantidadeLivros());
        assertTrue(listPage.isLivroPresente(titulo));
    }

    @Test
    @Order(3)
    public void testCancelamentoFormulario() {
        LivroListPage listPage = new LivroListPage(driver);
        int quantidadeInicial = listPage.getQuantidadeLivros();

        listPage.clickNovoLivro();

        LivroFormPage formPage = new LivroFormPage(driver);
        formPage.preencherFormulario("Teste", "Autor Teste", "1234567890123");
        formPage.clickCancelar();

        assertTrue(driver.getCurrentUrl().contains("/livros"));
        assertEquals(quantidadeInicial, listPage.getQuantidadeLivros());
    }

    @Test
    @Order(4)
    public void testValidacaoFormulario() {
        LivroListPage listPage = new LivroListPage(driver);
        listPage.clickNovoLivro();

        LivroFormPage formPage = new LivroFormPage(driver);
        formPage.clickSubmit();

        //validação feita na própria página html
        assertTrue(driver.getCurrentUrl().contains("novo"));
    }

    @Test
    @Order(5)
    public void testEdicaoLivroBemSucedida() {
        LivroListPage listPage = new LivroListPage(driver);
        listPage.clickEditarLivro(1);

        LivroFormPage formPage = new LivroFormPage(driver);
        String tituloOriginal = formPage.getTitulo();

        formPage.preencherFormulario(tituloOriginal + " - Editado", "Autor Editado", "9999999999999");
        formPage.clickSubmit();

        assertTrue(listPage.isLivroPresente(tituloOriginal + " - Editado"));
        assertTrue(listPage.isLivroPresente("Autor Editado"));
    }


    @Test
    @Order(6)
    public void testRemocaoLivroComDesistencia() {
        LivroListPage listPage = new LivroListPage(driver);
        int quantidadeInicial = listPage.getQuantidadeLivros();

        listPage.clickRemoverLivro(1);
        listPage.cancelarRemocao();

        // Verificar que o livro ainda está presente
        assertEquals(quantidadeInicial, listPage.getQuantidadeLivros());
    }

    @Test
    @Order(7)
    public void testRemocaoLivroComConfirmacao() {
        LivroListPage listPage = new LivroListPage(driver);
        int quantidadeInicial = listPage.getQuantidadeLivros();

        listPage.clickRemoverLivro(2);
        listPage.confirmarRemocao();

        // Verificar que o livro foi removido
        assertEquals(quantidadeInicial - 1, listPage.getQuantidadeLivros());
    }
}