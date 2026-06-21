package br.edu.ifpe.dsc.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

public class LoginTest extends BaseSeleniumTest {

    private void preencherLogin(String matricula, String senha) {
        driver.findElement(By.id("matricula")).clear();
        driver.findElement(By.id("matricula")).sendKeys(matricula);

        driver.findElement(By.id("senha")).clear();
        driver.findElement(By.id("senha")).sendKeys(senha);
    }

    private void clicarEntrar() {
        clicar(By.cssSelector("#loginForm button[type='submit']"));
    }

    @Test
    void devePermitirApenasMatriculaNumerica() {
        abrirPagina("index.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("matricula")));

        driver.findElement(By.id("matricula")).sendKeys("abc123@@456");

        String valor = driver.findElement(By.id("matricula")).getAttribute("value");

        assertEquals("123456", valor);
    }

    @Test
    void deveLimitarMatriculaEm12Digitos() {
        abrirPagina("index.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("matricula")));

        driver.findElement(By.id("matricula")).sendKeys("123456789012345");

        String valor = driver.findElement(By.id("matricula")).getAttribute("value");

        assertEquals("123456789012", valor);
        assertTrue(valor.length() <= 12);
    }

    @Test
    void deveRemoverEspacosDaSenha() {
        abrirPagina("index.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

        driver.findElement(By.id("senha")).sendKeys("sen ha 123");

        String valor = driver.findElement(By.id("senha")).getAttribute("value");

        assertEquals("senha123", valor);
    }

    @Test
    void deveLimitarSenhaEm20Caracteres() {
        abrirPagina("index.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("senha")));

        driver.findElement(By.id("senha")).sendKeys("1234567890123456789012345");

        String valor = driver.findElement(By.id("senha")).getAttribute("value");

        assertTrue(valor.length() <= 20);
    }

    @Test
    void deveValidarMatriculaESenhaObrigatorias() {
        abrirPagina("index.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("matricula")));

        clicarEntrar();

        assertTrue(driver.findElement(By.id("matricula")).isDisplayed());
        assertTrue(driver.findElement(By.id("senha")).isDisplayed());
    }

    @Test
    void deveValidarSenhaCurtaNoLogin() {
        abrirPagina("index.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("matricula")));

        preencherLogin(MATRICULA_MOTORISTA, "123");

        clicarEntrar();

        String alerta = capturarAlerta();

        assertTrue(alerta.toLowerCase().contains("senha"));
    }

    @Test
    void deveImpedirLoginComCredenciaisInvalidas() {
        abrirPagina("index.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("matricula")));

        preencherLogin("999999", "senha123");

        clicarEntrar();

        String alerta = capturarAlerta();

        assertTrue(alerta.toLowerCase().contains("inválid")
                || alerta.toLowerCase().contains("matrícula")
                || alerta.toLowerCase().contains("senha"));
    }

    @Test
    void deveExibirMensagemParaMatriculaNaoEncontrada() {
        abrirPagina("index.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("matricula")));

        preencherLogin("999999", SENHA);

        clicarEntrar();

        String alerta = capturarAlerta();

        assertTrue(alerta.toLowerCase().contains("inválid")
                || alerta.toLowerCase().contains("matrícula"));
    }

    @Test
    void deveExibirMensagemParaSenhaIncorreta() {
        abrirPagina("index.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("matricula")));

        preencherLogin(MATRICULA_MOTORISTA, "senhaerrada");

        clicarEntrar();

        String alerta = capturarAlerta();

        assertTrue(alerta.toLowerCase().contains("inválid")
                || alerta.toLowerCase().contains("senha"));
    }

    @Test
    void deveRedirecionarMotoristaParaDashboardCorrespondente() {
        abrirPagina("index.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("matricula")));

        preencherLogin(MATRICULA_MOTORISTA, SENHA);

        clicarEntrar();

        wait.until(ExpectedConditions.urlContains("dashboard_motorista"));

        assertTrue(driver.getCurrentUrl().contains("dashboard_motorista"));
    }

    @Test
    void deveRedirecionarCoordenadorParaDashboardCorrespondente() {
        abrirPagina("index.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("matricula")));

        preencherLogin(MATRICULA_COORDENADOR, SENHA);

        clicarEntrar();

        wait.until(ExpectedConditions.urlContains("dashboard_coordenador"));

        assertTrue(driver.getCurrentUrl().contains("dashboard_coordenador"));
    }

    @Test
    void deveRedirecionarMecanicoParaDashboardCorrespondente() {
        abrirPagina("index.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("matricula")));

        preencherLogin(MATRICULA_MECANICO, SENHA);

        clicarEntrar();

        wait.until(ExpectedConditions.urlContains("dashboard_mecanico"));

        assertTrue(driver.getCurrentUrl().contains("dashboard_mecanico"));
    }
}