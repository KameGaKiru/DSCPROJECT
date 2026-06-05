package br.edu.ifpe.dsc.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest extends BaseSeleniumTest {

    @Test
    void deveLogarMotoristaComSucesso() {
        abrirPagina("index.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("matricula")));

        driver.findElement(By.id("matricula")).sendKeys(MATRICULA_MOTORISTA);
        driver.findElement(By.id("senha")).sendKeys(SENHA);
        driver.findElement(By.cssSelector("#loginForm button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("dashboard_motorista"));

        assertTrue(driver.getCurrentUrl().contains("dashboard_motorista"));
    }

    @Test
    void deveValidarSenhaCurtaNoLogin() {
        abrirPagina("index.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("matricula")));

        driver.findElement(By.id("matricula")).sendKeys("111111");
        driver.findElement(By.id("senha")).sendKeys("123");
        driver.findElement(By.cssSelector("#loginForm button[type='submit']")).click();

        String alerta = capturarAlerta();

        assertTrue(alerta.toLowerCase().contains("senha"));
    }
}