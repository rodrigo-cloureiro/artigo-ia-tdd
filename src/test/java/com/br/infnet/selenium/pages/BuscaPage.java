package com.br.infnet.selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import java.util.List;

public class BuscaPage extends BasePage {

    @FindBy(id = "tipo")
    private WebElement tipoSelect;

    @FindBy(id = "termo")
    private WebElement termoInput;

    @FindBy(css = "button[type='submit']")
    private WebElement buscarButton;

    @FindBy(css = "a[href='/livros']")
    private WebElement voltarButton;

    @FindBy(css = ".table")
    private WebElement resultadosTable;

    @FindBy(css = "h2")
    private WebElement resultadosTitle;

    public BuscaPage(WebDriver driver) {
        super(driver);
    }

    public void selecionarTipoBusca(String tipo) {
        Select select = new Select(tipoSelect);
        select.selectByValue(tipo);
    }

    public void digitarTermo(String termo) {
        waitAndSendKeys(termoInput, termo);
    }

    public void clickBuscar() {
        waitAndClick(buscarButton);
    }

    public void clickVoltar() {
        waitAndClick(voltarButton);
    }

    public boolean isResultadosDisplayed() {
        try {
            return resultadosTable.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isMensagemNenhumResultado() {
        return driver.getPageSource().contains("Nenhum livro encontrado.");
    }

    @FindBy(css = ".error")
    private WebElement mensagemErro;

    public void waitAndClear(WebElement element) {
        waitForElement(element);
        element.clear();
    }

    public void realizarBusca(String tipo, String termo) {
        selecionarTipoBusca(tipo);
        digitarTermo(termo);
        clickBuscar();
    }

    public boolean isMensagemErroExibida() {
        try {
            return isElementDisplayed(mensagemErro);
        } catch (Exception e) {
            return false;
        }
    }

    public String getMensagemErro() {
        return getText(mensagemErro);
    }

    public int getQuantidadeResultados() {
        try {
            List<WebElement> linhas = resultadosTable.findElements(By.cssSelector("tr"));
            return linhas.size() - 1; // Subtrai 1 para não contar o cabeçalho
        } catch (Exception e) {
            return 0;
        }
    }

    public List<String> getTitulosResultados() {
        return getColumnValues(2);
    }

    public List<String> getAutoresResultados() {
        return getColumnValues(3);
    }

    public List<String> getIdsResultados() {
        return getColumnValues(1);
    }

    public List<String> getStatusResultados() {
        return getColumnValues(5);
    }

    private List<String> getColumnValues(int columnIndex) {
        List<WebElement> cells = resultadosTable.findElements(
                By.cssSelector("tr:not(:first-child) td:nth-child(" + columnIndex + ")")
        );
        return cells.stream().map(WebElement::getText).toList();
    }

    public boolean contemLivroComTitulo(String titulo) {
        return getTitulosResultados().stream()
                .anyMatch(t -> t.toLowerCase().contains(titulo.toLowerCase()));
    }

    public boolean contemLivroComAutor(String autor) {
        return getAutoresResultados().stream()
                .anyMatch(a -> a.toLowerCase().contains(autor.toLowerCase()));
    }

    public boolean contemLivroComId(String id) {
        return getIdsResultados().contains(id);
    }

    public String getTipoSelecionado() {
        Select select = new Select(tipoSelect);
        return select.getFirstSelectedOption().getAttribute("value");
    }

    public String getTermoDigitado() {
        return termoInput.getAttribute("value");
    }

    public boolean isFormularioLimpo() {
        return getTipoSelecionado().isEmpty() && getTermoDigitado().isEmpty();
    }

    public boolean validarCamposObrigatorios() {
        return tipoSelect.getAttribute("required") != null &&
                termoInput.getAttribute("required") != null;
    }

}
