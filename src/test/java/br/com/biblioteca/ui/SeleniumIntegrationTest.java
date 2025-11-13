package br.com.biblioteca.ui;

import br.com.biblioteca.App;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SeleniumIntegrationTest {

    private WebDriver driver;
    private Process serverProcess;

    @BeforeAll
    void setupClass() throws Exception {
        // start app in same JVM: call main in a thread
        new Thread(() -> App.main(new String[]{})).start();
        // setup webdriver
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--headless=new");
        opts.addArguments("--no-sandbox");
        opts.addArguments("--disable-dev-shm-usage");
        driver = new ChromeDriver(opts);

        // wait a bit for server start (simple)
        Thread.sleep(1500);
    }

    @AfterAll
    void teardown() {
        if (driver != null) driver.quit();
    }

    @Test
    void openHomeAndCheckTitle() {
        driver.get("http://localhost:7000/");
        String title = driver.getTitle();
        assertThat(title).contains("Biblioteca");
    }

    @Test
    void createBookViaForm() {
        driver.get("http://localhost:7000/books/new");
        driver.findElement(By.name("title")).sendKeys("Selenium Book");
        driver.findElement(By.name("author")).sendKeys("Tester");
        driver.findElement(By.name("isbn")).sendKeys("9999999999999");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        // after redirect to /, check book appears in list by ISBN text
        driver.get("http://localhost:7000/");
        assertThat(driver.getPageSource()).contains("9999999999999");
    }
}
