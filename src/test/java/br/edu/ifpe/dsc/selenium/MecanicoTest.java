package br.edu.ifpe.dsc.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MecanicoTest extends BaseSeleniumTest {

    @Test
    void deveCarregarCardsDoMecanico() {
        autenticarMecanico();
        abrirPagina("dashboard_mecanico.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("totalChecklists")));

        assertTrue(driver.findElement(By.id("totalChecklists")).isDisplayed());
        assertTrue(driver.findElement(By.id("checklistsResolvidos")).isDisplayed());
        assertTrue(driver.findElement(By.id("checklistsPendentes")).isDisplayed());
    }

    @Test
    void deveExibirTabelaDeChecklists() {
        autenticarMecanico();
        abrirPagina("dashboard_mecanico.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("checklistTable")));

        assertTrue(driver.findElement(By.id("checklistTable")).isDisplayed());
    }

    @Test
    void deveValidarCampoSolucaoNoModal() {
        autenticarMecanico();
        abrirPagina("dashboard_mecanico.html");

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("modalSolucao")));

        assertTrue(driver.findElement(By.id("modalSolucao")).getAttribute("class").contains("modal"));
        assertTrue(driver.findElement(By.id("solucaoMecanico")).isDisplayed()
                || driver.findElement(By.id("solucaoMecanico")).getAttribute("maxlength").equals("400"));

        assertEquals("400", driver.findElement(By.id("solucaoMecanico")).getAttribute("maxlength"));
    }
}