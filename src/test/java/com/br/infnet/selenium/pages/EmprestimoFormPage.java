package com.br.infnet.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class EmprestimoFormPage extends BasePage {

    @FindBy(id = "prazo")
    private WebElement prazoInput;

    @FindBy(css = "button[type='submit']")
    private WebElement emprestarButton;

    @FindBy(css = "a[href='/livros']")
    private WebElement cancelarButton;

    public EmprestimoFormPage(WebDriver driver) {
        super(driver);
    }

    public void definirPrazo(int dias) {
        waitAndSendKeys(prazoInput, String.valueOf(dias));
    }

    public void clickEmprestar() {
        waitAndClick(emprestarButton);
    }

    public void clickCancelar() {
        waitAndClick(cancelarButton);
    }

    public int getPrazoAtual() {
        return Integer.parseInt(prazoInput.getAttribute("value"));
    }
}

