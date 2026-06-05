package br.edu.ifpe.dsc.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.*;

public class RegistroUsuarioTest extends BaseSeleniumTest {

    @Test
    void deveRegistrarUsuarioComSucesso() {
        abrirPagina("registro.html");

        String matricula = String.valueOf(numeroUnico()).substring(0, 6);

        driver.findElement(By.id("matricula")).sendKeys(matricula);
        driver.findElement(By.id("nome")).sendKeys("Usuario");
        driver.findElement(By.id("sobrenome")).sendKeys("Teste");
        new Select(driver.findElement(By.id("funcao"))).selectByValue("MOTORISTA");
        driver.findElement(By.id("senha")).sendKeys("senha123");

        driver.findElement(By.cssSelector("#registroForm button[type='submit']")).click();

        String alerta = capturarAlerta();

        assertTrue(alerta.toLowerCase().contains("sucesso"));
    }

    @Test
    void deveBloquearMatriculaDuplicada() {
        abrirPagina("registro.html");

        driver.findElement(By.id("matricula")).sendKeys(MATRICULA_MOTORISTA);
        driver.findElement(By.id("nome")).sendKeys("Motorista");
        driver.findElement(By.id("sobrenome")).sendKeys("Duplicado");
        new Select(driver.findElement(By.id("funcao"))).selectByValue("MOTORISTA");
        driver.findElement(By.id("senha")).sendKeys("senha123");

        driver.findElement(By.cssSelector("#registroForm button[type='submit']")).click();

        String alerta = capturarAlerta();

        assertTrue(alerta.toLowerCase().contains("matr")
                || alerta.toLowerCase().contains("erro"));
    }

    @Test
    void deveBloquearNomeNumerico() {
        abrirPagina("registro.html");

        driver.findElement(By.id("matricula")).sendKeys("987654");
        driver.findElement(By.id("nome")).sendKeys("12345");
        driver.findElement(By.id("sobrenome")).sendKeys("Teste");
        driver.findElement(By.id("senha")).sendKeys("senha123");

        driver.findElement(By.cssSelector("#registroForm button[type='submit']")).click();

        String alerta = capturarAlerta();

        assertTrue(alerta.toLowerCase().contains("nome"));
    }

    @Test
    void deveBloquearSenhaCurta() {
        abrirPagina("registro.html");

        driver.findElement(By.id("matricula")).sendKeys("987653");
        driver.findElement(By.id("nome")).sendKeys("Usuario");
        driver.findElement(By.id("sobrenome")).sendKeys("Teste");
        driver.findElement(By.id("senha")).sendKeys("123");

        driver.findElement(By.cssSelector("#registroForm button[type='submit']")).click();

        String alerta = capturarAlerta();

        assertTrue(alerta.contains("senha deve ter entre 6 e 20"));
    }
}