package com.br.infnet.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.List;

public class LivroListPage extends BasePage {

    @FindBy(css = "a[href='/livros/novo']")
    private WebElement novoLivroButton;

    @FindBy(css = "a[href='/emprestimos']")
    private WebElement emprestimosButton;

    @FindBy(css = "a[href='/buscar']")
    private WebElement buscarButton;

    @FindBy(css = ".table tbody tr")
    private List<WebElement> livrosRows;

    @FindBy(css = ".table")
    private WebElement table;

    public LivroListPage(WebDriver driver) {
        super(driver);
    }

    public void clickNovoLivro() {
        waitAndClick(novoLivroButton);
    }

    public void clickEmprestimos() {
        waitAndClick(emprestimosButton);
    }

    public void clickBuscar() {
        waitAndClick(buscarButton);
    }

    public int getQuantidadeLivros() {
        wait.until(ExpectedConditions.visibilityOf(table));
        return livrosRows.size();
    }

    public boolean isLivroPresente(String titulo) {
        return driver.getPageSource().contains(titulo);
    }

    public void clickEditarLivro(int livroId) {
        WebElement editButton = driver.findElement(
                By.cssSelector("a[href='/livros/" + livroId + "/editar']"));
        waitAndClick(editButton);
    }

    public void clickEmprestarLivro(int livroId) {
        WebElement emprestar = driver.findElement(
                By.cssSelector("a[href='/livros/" + livroId + "/emprestar']"));
        waitAndClick(emprestar);
    }

    public void clickRemoverLivro(int livroId) {
        WebElement removeButton = driver.findElement(
                By.cssSelector("form[action='/livros/" + livroId + "/remover'] button"));
        waitAndClick(removeButton);
    }

    public void confirmarRemocao() {
        driver.switchTo().alert().accept();
    }

    public void cancelarRemocao() {
        driver.switchTo().alert().dismiss();
    }
}

