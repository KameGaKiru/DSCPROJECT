package br.edu.ifpe.dsc.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChecklistTest extends BaseSeleniumTest {

    @Test
    void deveBloquearChecklistSemVeiculo() {
        autenticarMotorista();
        abrirPagina("dashboard_checklist.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("km")));

        driver.findElement(By.id("km")).sendKeys("50000");
        marcarTodosItens();

        driver.findElement(By.xpath("//button[contains(., 'Salvar Checklist')]")).click();

        String alerta = capturarAlerta();

        assertTrue(alerta.toLowerCase().contains("selecione"));
    }

    @Test
    void deveExigirObservacaoQuandoItensNaoMarcados() {
        autenticarMotorista();
        abrirPagina("dashboard_checklist.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("veiculoSelect")));

        Select veiculo = new Select(driver.findElement(By.id("veiculoSelect")));
        wait.until(driver -> veiculo.getOptions().size() > 1);
        veiculo.selectByIndex(1);

        driver.findElement(By.id("km")).sendKeys("999999");

        driver.findElement(By.xpath("//button[contains(., 'Salvar Checklist')]")).click();

        try {
            String alerta = capturarAlerta();

            if (alerta.contains("próximo deve ser do tipo SAÍDA")) {
                new Select(driver.findElement(By.id("tipo"))).selectByValue("SAIDA");
            } else if (alerta.contains("próximo deve ser do tipo ENTRADA")) {
                new Select(driver.findElement(By.id("tipo"))).selectByValue("ENTRADA");
            }

            driver.findElement(By.xpath("//button[contains(., 'Salvar Checklist')]")).click();

        } catch (Exception ignored) {
        }

        WebElement erro = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("observacoesErro"))
        );

        assertTrue(erro.isDisplayed());
        assertTrue(erro.getText().toLowerCase().contains("observa"));
    }

    @Test
    void deveContarCaracteresDasObservacoes() {
        autenticarMotorista();
        abrirPagina("dashboard_checklist.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("observacoes")));

        driver.findElement(By.id("observacoes")).sendKeys("Teste");

        assertEquals("5", driver.findElement(By.id("contadorObservacoes")).getText());
    }

    private void marcarTodosItens() {
        List<String> ids = List.of(
                "faroisDianteiros",
                "setasDianteiras",
                "faroisTraseiros",
                "setasTraseiras",
                "luzesFreio",
                "nivelOleo",
                "nivelAgua"
        );

        for (String id : ids) {
            WebElement checkbox = driver.findElement(By.id(id));
            if (!checkbox.isSelected()) {
                checkbox.click();
            }
        }
    }
}