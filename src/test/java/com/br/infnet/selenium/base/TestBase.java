package com.br.infnet.selenium.base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public abstract class TestBase {
    protected WebDriver driver;
    protected static final String BASE_URL = "http://localhost:7000";

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-features=VizDisplayCompositor");
        options.setExperimentalOption("useAutomationExtension", false);

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        driver.manage().window().maximize();

        ((JavascriptExecutor) driver).executeScript("window.setTimeout = function(fn, delay) { return window.originalSetTimeout(fn, delay * 2); };");
        driver.get(BASE_URL + "/livros");
    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        if (driver != null) {
            try {
                ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");

                ((JavascriptExecutor) driver).executeScript("window.sessionStorage.clear();");

                driver.manage().deleteAllCookies();

                ((JavascriptExecutor) driver).executeScript(
                        "if ('caches' in window) {" +
                                "  caches.keys().then(function(names) {" +
                                "    names.forEach(function(name) {" +
                                "      caches.delete(name);" +
                                "    });" +
                                "  });" +
                                "}"
                );

            } catch (Exception e) {
                System.out.println("Erro ao limpar dados do navegador: " + e.getMessage());
            } finally {
                Thread.sleep(5000);
                driver.quit();
            }
        }
    }
}


