package br.edu.ifpe.dsc.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

public abstract class BaseSeleniumTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    protected static final String BASE_URL = "http://localhost:8080";

    protected static final String MATRICULA_MOTORISTA = "111111";
    protected static final String MATRICULA_COORDENADOR = "222222";
    protected static final String MATRICULA_MECANICO = "333333";
    protected static final String SENHA = "senha123";

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        options.addArguments(
            "--start-maximized"
        );

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    protected void abrirPagina(String pagina) {
        driver.get(BASE_URL + "/" + pagina);
    }

    protected void autenticarComo(String matricula, String nome, String funcao) {
        abrirPagina("index.html");

        String token = Base64.getEncoder()
                .encodeToString((matricula + ":" + SENHA).getBytes(StandardCharsets.UTF_8));

        String usuarioJson = """
            {
              "matricula": "%s",
              "nome": "%s",
              "sobrenome": "Teste",
              "funcao": "%s"
            }
            """.formatted(matricula, nome, funcao);

        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript("localStorage.setItem('authHeader', arguments[0]);", "Basic " + token);
        js.executeScript("localStorage.setItem('usuario', arguments[0]);", usuarioJson);
    }

    protected void autenticarMotorista() {
        autenticarComo(MATRICULA_MOTORISTA, "Motorista", "MOTORISTA");
    }

    protected void autenticarCoordenador() {
        autenticarComo(MATRICULA_COORDENADOR, "Coordenador", "COORDENADOR");
    }

    protected void autenticarMecanico() {
        autenticarComo(MATRICULA_MECANICO, "Mecanico", "MECANICO");
    }

    protected String capturarAlerta() {
        wait.until(ExpectedConditions.alertIsPresent());
        String texto = driver.switchTo().alert().getText();
        driver.switchTo().alert().accept();
        return texto;
    }

    protected long numeroUnico() {
        return System.currentTimeMillis() % 1_000_000_000L;
    }
    protected void clicar(By by) {
    WebElement elemento = wait.until(
            ExpectedConditions.presenceOfElementLocated(by)
    );

    ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block: 'center'});",
            elemento
    );

    wait.until(ExpectedConditions.elementToBeClickable(elemento));

    ((JavascriptExecutor) driver).executeScript(
            "arguments[0].click();",
            elemento
    );
}
}