package br.edu.ifpe.dsc.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LogoutTest extends BaseSeleniumTest {

    @Test
    void deveFazerLogoutDoMotorista() {
        autenticarMotorista();
        abrirPagina("dashboard_motorista.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logoutBtn")));

        driver.findElement(By.id("logoutBtn")).click();

        wait.until(ExpectedConditions.urlContains("index.html"));

        assertTrue(driver.getCurrentUrl().contains("index.html"));
    }

    @Test
    void deveFazerLogoutDoCoordenador() {
        autenticarCoordenador();
        abrirPagina("dashboard_coordenador.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logoutBtn")));

        driver.findElement(By.id("logoutBtn")).click();

        wait.until(ExpectedConditions.urlContains("index.html"));

        assertTrue(driver.getCurrentUrl().contains("index.html"));
    }

    @Test
    void deveFazerLogoutDoMecanico() {
        autenticarMecanico();
        abrirPagina("dashboard_mecanico.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logoutBtn")));

        driver.findElement(By.id("logoutBtn")).click();

        wait.until(ExpectedConditions.urlContains("index.html"));

        assertTrue(driver.getCurrentUrl().contains("index.html"));
    }
}