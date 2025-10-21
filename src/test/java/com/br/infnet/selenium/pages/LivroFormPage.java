package com.br.infnet.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LivroFormPage extends BasePage {

    @FindBy(id = "titulo")
    private WebElement tituloInput;

    @FindBy(id = "autor")
    private WebElement autorInput;

    @FindBy(id = "isbn")
    private WebElement isbnInput;

    @FindBy(css = "button[type='submit']")
    private WebElement submitButton;

    @FindBy(css = "a[href='/livros']")
    private WebElement cancelarButton;

    @FindBy(css = ".error")
    private WebElement errorMessage;

    public LivroFormPage(WebDriver driver) {
        super(driver);
    }

    public void preencherFormulario(String titulo, String autor, String isbn) {
        waitAndSendKeys(tituloInput, titulo);
        waitAndSendKeys(autorInput, autor);
        waitAndSendKeys(isbnInput, isbn);
    }

    public void clickSubmit() {
        waitAndClick(submitButton);
    }

    public void clickCancelar() {
        waitAndClick(cancelarButton);
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return errorMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorMessage() {
        return errorMessage.getText();
    }

    public String getTitulo() {
        return tituloInput.getAttribute("value");
    }

    public String getAutor() {
        return autorInput.getAttribute("value");
    }

    public String getIsbn() {
        return isbnInput.getAttribute("value");
    }
}

