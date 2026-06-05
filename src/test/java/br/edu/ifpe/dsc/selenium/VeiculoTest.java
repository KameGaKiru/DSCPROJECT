package br.edu.ifpe.dsc.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VeiculoTest extends BaseSeleniumTest {

    @Test
    void deveCadastrarVeiculoComSucesso() {
        autenticarCoordenador();
        abrirPagina("dashboard_coordenador.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("numero")));

        long agora = System.currentTimeMillis();

        String numero = String.valueOf(agora).substring(3, 13);

        String placa = "T"
                + (char) ('A' + (agora % 26))
                + (char) ('A' + ((agora / 26) % 26))
                + "-"
                + String.valueOf(agora).substring(9, 13);

        driver.findElement(By.id("numero")).sendKeys(numero);
        driver.findElement(By.id("placa")).sendKeys(placa);

        new Select(driver.findElement(By.id("marca"))).selectByVisibleText("FIAT");
        new Select(driver.findElement(By.id("tipo"))).selectByVisibleText("VAN");

        clicar(By.cssSelector("#cadVeiculoForm button[type='submit']"));

        String alerta = capturarAlerta();

        System.out.println("ALERTA RECEBIDO: " + alerta);

        assertTrue(
                alerta.toLowerCase().contains("cadastrado")
                || alerta.toLowerCase().contains("sucesso"),
                "Alerta recebido: " + alerta
        );
    }

    @Test
    void deveBloquearPlacaInvalida() {
        autenticarCoordenador();
        abrirPagina("dashboard_coordenador.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("numero")));

        driver.findElement(By.id("numero")).sendKeys("888123");
        driver.findElement(By.id("placa")).sendKeys("ABC-12");

        new Select(driver.findElement(By.id("marca"))).selectByVisibleText("FIAT");
        new Select(driver.findElement(By.id("tipo"))).selectByVisibleText("VAN");

        clicar(By.cssSelector("#cadVeiculoForm button[type='submit']"));

        String alerta = capturarAlerta();

        assertTrue(alerta.toLowerCase().contains("placa"));
    }
}