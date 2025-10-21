package com.br.infnet.selenium.tests;

import com.br.infnet.selenium.base.TestBase;
import com.br.infnet.selenium.pages.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

public class EmprestimoTest extends TestBase {

    @ParameterizedTest
    @ValueSource(ints = {1, 7, 15, 30})
    public void testEmprestimoComDiferentesPrazos(int prazo) {
        LivroListPage listPage = new LivroListPage(driver);

        int livroIndex = prazo == 1 ? 4 : prazo == 7 ? 12 : prazo == 15 ? 14 : 20;
        listPage.clickEmprestarLivro(livroIndex);

        EmprestimoFormPage emprestimoPage = new EmprestimoFormPage(driver);
        emprestimoPage.definirPrazo(prazo);
        emprestimoPage.clickEmprestar();

        assertTrue(driver.getCurrentUrl().contains("/emprestimos"));
    }

    @Test
    public void testEmprestimoLivroJaEmprestado() {
        LivroListPage listPage = new LivroListPage(driver);

        listPage.clickEmprestarLivro(5);

        EmprestimoFormPage emprestimoPage = new EmprestimoFormPage(driver);
        emprestimoPage.definirPrazo(7);
        emprestimoPage.clickEmprestar();

        listPage = new LivroListPage(driver);

        //não dá para clicar no botão pois ele some após o empréstimo
        //tentativa de burlar a UI
        driver.navigate().to(BASE_URL + "/livros/5/emprestar");
        WebElement mensagemErro = driver.findElement(By.className("error-details"));
        assertTrue(mensagemErro.getText().contains("Livro não está disponível para empréstimo"), "Livro não está disponível para empréstimo");
    }


    @Test
    public void testCancelamentoEmprestimo() {
        LivroListPage listPage = new LivroListPage(driver);
        listPage.clickEmprestarLivro(3);

        EmprestimoFormPage emprestimoPage = new EmprestimoFormPage(driver);
        emprestimoPage.clickCancelar();

        assertTrue(driver.getCurrentUrl().contains("/livros"));
    }

    @Test
    public void testNavegacaoEmprestimos() {
        LivroListPage listPage = new LivroListPage(driver);
        listPage.clickEmprestimos();

        assertTrue(driver.getCurrentUrl().contains("/emprestimos"));
    }
}
