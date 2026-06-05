package br.edu.ifpe.dsc.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DashboardTest extends BaseSeleniumTest {

    @Test
    void deveCarregarDashboardMotorista() {
        autenticarMotorista();
        abrirPagina("dashboard_motorista.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("totalChecklists")));

        assertTrue(driver.findElement(By.id("totalChecklists")).isDisplayed());
        assertTrue(driver.findElement(By.id("rankingPosicao")).isDisplayed());
        assertTrue(driver.findElement(By.id("usuariosTable")).isDisplayed());
    }

    @Test
    void deveCarregarDashboardCoordenador() {
        autenticarCoordenador();
        abrirPagina("dashboard_coordenador.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("totalVeiculos")));

        assertTrue(driver.findElement(By.id("totalVeiculos")).isDisplayed());
        assertTrue(driver.findElement(By.id("rankingTable")).isDisplayed());
        assertTrue(driver.findElement(By.id("veiculosTable")).isDisplayed());
    }

    @Test
    void deveCarregarDashboardMecanico() {
        autenticarMecanico();
        abrirPagina("dashboard_mecanico.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("checklistTable")));

        assertTrue(driver.findElement(By.id("totalChecklists")).isDisplayed());
        assertTrue(driver.findElement(By.id("checklistsResolvidos")).isDisplayed());
        assertTrue(driver.findElement(By.id("checklistsPendentes")).isDisplayed());
    }
}