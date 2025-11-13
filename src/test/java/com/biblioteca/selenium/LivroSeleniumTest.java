package com.biblioteca.selenium;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class LivroSeleniumTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:7070");
    }

    @Test
    void deveCadastrarLivroComSucesso() {
        // Navegar para o formulário de cadastro
        driver.findElement(By.linkText("Novo Livro")).click();

        // Preencher formulário
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("titulo")));

        driver.findElement(By.id("titulo")).sendKeys("Livro Teste Selenium");
        driver.findElement(By.id("autor")).sendKeys("Autor Teste");
        driver.findElement(By.id("isbn")).sendKeys("9788535930999");

        // Submeter formulário
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Verificar redirecionamento e mensagem de sucesso
        wait.until(ExpectedConditions.urlContains("/livros"));
        WebElement alert = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".alert-success")));

        assertTrue(alert.getText().contains("sucesso"));
    }

    @Test
    void deveRejeitarXssNoFormulario() {
        driver.findElement(By.linkText("Novo Livro")).click();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("titulo")));

        // Tentar inserir XSS
        driver.findElement(By.id("titulo")).sendKeys("<script>alert('xss')</script>");
        driver.findElement(By.id("autor")).sendKeys("Autor Válido");
        driver.findElement(By.id("isbn")).sendKeys("9788535930001");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Verificar que permanece no formulário com erro
        WebElement erro = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector(".alert-danger")));

        assertTrue(erro.isDisplayed());
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}