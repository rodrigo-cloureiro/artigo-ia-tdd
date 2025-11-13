package br.com.biblioteca.selenium;

import br.com.biblioteca.Application;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.javalin.Javalin;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Testes de Interface Web (Selenium)")
public class SeleniumTest {

    private static Javalin app;
    private static WebDriver driver;
    private static final String BASE_URL = "http://localhost:7070";
    private static WebDriverWait wait;

    @BeforeAll
    static void setup() {
        // Configura o Chrome Headless (Requer que o ChromeDriver esteja no PATH)
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Inicia a aplicação Javalin (assumindo que Application.main a inicia)
        // Nota: Em um ambiente real, você inicializaria o Javalin diretamente aqui
        // Para simplificar, assumimos que o main() inicia a app
        app = Javalin.create().start(7070);
        // Você precisaria de um Javalin start/stop controlado para testes.
        // Para este exemplo, faremos um start/stop simplificado.
        try {
            Application.main(new String[]{}); // Inicializa a app completa para o teste
        } catch (Exception e) {
            // Ignora se o servidor já estiver rodando
        }
    }

    @AfterAll
    static void teardown() {
        if (driver != null) {
            driver.quit();
        }
        if (app != null) {
            app.stop();
        }
    }

    @Test
    @Order(1)
    @DisplayName("1. Deve adicionar um novo livro com sucesso")
    void testAddBook() {
        driver.get(BASE_URL + "/new");

        driver.findElement(By.name("title")).sendKeys("A Casa do Dragão");
        driver.findElement(By.name("author")).sendKeys("George R. R. Martin");
        driver.findElement(By.name("isbn")).sendKeys("978-8556510484");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Verifica se o livro está na lista (deve retornar para index.html)
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/"));

        WebElement newBookRow = driver.findElement(By.xpath("//td[contains(text(), 'A Casa do Dragão')]"));
        assertNotNull(newBookRow, "Novo livro não encontrado na tabela.");
    }

    @Test
    @Order(2)
    @DisplayName("2. Deve realizar o empréstimo de um livro disponível")
    void testLendBook() {
        driver.get(BASE_URL);

        // Encontra o botão "Emprestar" para o livro "A Casa do Dragão"
        WebElement lendButton = driver.findElement(By.xpath(
                "//td[contains(text(), 'A Casa do Dragão')]/following-sibling::td//a[contains(text(), 'Emprestar')]"
        ));
        lendButton.click();

        // Preenche o formulário de empréstimo
        wait.until(ExpectedConditions.urlContains("/lend/"));
        driver.findElement(By.name("borrowerName")).sendKeys("Maria Testadora");
        driver.findElement(By.name("daysToLend")).clear();
        driver.findElement(By.name("daysToLend")).sendKeys("7");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Deve redirecionar para a lista de empréstimos
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/loans"));

        WebElement loanEntry = driver.findElement(By.xpath("//td[contains(text(), 'Maria Testadora')]"));
        assertNotNull(loanEntry, "Empréstimo não encontrado na lista.");
    }

    @Test
    @Order(3)
    @DisplayName("3. Deve bloquear a exclusão de um livro emprestado")
    void testBlockDelete() {
        driver.get(BASE_URL);

        // O livro "A Casa do Dragão" deve ter o botão "Excluir" desabilitado
        WebElement deleteButton = driver.findElement(By.xpath(
                "//td[contains(text(), 'A Casa do Dragão')]/following-sibling::td//button[contains(text(), 'Excluir')]"
        ));

        assertTrue(deleteButton.isEnabled() == false, "Botão Excluir deveria estar desabilitado.");

        // Tenta excluir um livro disponível (ex: Duna - ID 6 do CSV)
        WebElement availableDeleteButton = driver.findElement(By.xpath(
                "//td[contains(text(), 'Duna')]/following-sibling::td//form/button"
        ));

        availableDeleteButton.click(); // Confirmação JS é ignorada pelo click()

        // Verifica se Duna foi removido (deve permanecer se o DAO não for limpo, mas a exclusão deve ocorrer)
        // Para este teste, vamos apenas garantir que a página não deu erro 500
        wait.until(ExpectedConditions.urlToBe(BASE_URL + "/"));
        assertFalse(driver.getPageSource().contains("Duna"), "Livro Duna deveria ter sido excluído.");
    }

    @Test
    @Order(4)
    @DisplayName("4. Deve processar devolução e remover da lista de empréstimos")
    void testReturnBook() {
        driver.get(BASE_URL + "/loans");

        // Encontra o botão "Devolver" para "Maria Testadora"
        WebElement returnButton = driver.findElement(By.xpath(
                "//td[contains(text(), 'Maria Testadora')]/following-sibling::td//a[contains(text(), 'Devolver')]"
        ));
        returnButton.click();

        // Confirma a devolução (mesmo que com multa, o botão confirma o pagamento)
        wait.until(ExpectedConditions.urlContains("/return/"));
        driver.findElement(By.cssSelector("button.btn-success")).click();

        // Deve retornar para a lista de empréstimos e estar vazia ou sem o item
        wait.until(ExpectedConditions.urlContains(BASE_URL + "/loans"));
        assertFalse(driver.getPageSource().contains("Maria Testadora"), "Empréstimo de Maria Testadora deveria ter sido removido.");
    }
}