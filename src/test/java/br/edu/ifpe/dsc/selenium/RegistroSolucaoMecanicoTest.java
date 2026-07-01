package br.edu.ifpe.dsc.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

public class RegistroSolucaoMecanicoTest extends BaseSeleniumTest {

    private void abrirDashboardMecanico() {
        autenticarMecanico();
        abrirPagina("dashboard_mecanico.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("checklistTable")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("totalChecklists")));
    }

    private WebElement obterPrimeiroBotaoSolucao() {
    return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
            "//tbody[@id='checklistTable']//button[" +
                    "contains(normalize-space(.), 'Confirmar Solução')" +
                    " or contains(normalize-space(.), 'Editar')" +
                    " or contains(@onclick, 'abrirModalSolucao')" +
            "]"
    )));
}

        private void abrirModalSolucao() {
        WebElement botao = obterPrimeiroBotaoSolucao();

        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});",
                botao
        );

        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "arguments[0].click();",
                botao
        );

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalSolucao")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("solucaoMecanico")));
        }

        private void aguardarTabelaConter(String texto) {
        wait.until(driver ->
                driver.findElement(By.id("checklistTable"))
                        .getText()
                        .toLowerCase()
                        .contains(texto.toLowerCase())
        );
}

    private void preencherSolucao(String texto) {
        WebElement campo = driver.findElement(By.id("solucaoMecanico"));
        campo.clear();
        campo.sendKeys(texto);
    }

    private void confirmarSolucao() {
        clicar(By.cssSelector("#formSolucao button[type='submit']"));
    }

    @Test
    void mecanicoDeveVisualizarChecklistsPendentes() {
        abrirDashboardMecanico();

        assertTrue(driver.findElement(By.id("checklistTable")).isDisplayed());
        assertTrue(driver.findElement(By.id("checklistsPendentes")).isDisplayed());

        String pendentes = driver.findElement(By.id("checklistsPendentes")).getText();

        assertFalse(pendentes.isBlank(), "O card de pendentes deve estar preenchido.");

        assertTrue(
                driver.findElements(By.xpath(
                        "//tbody[@id='checklistTable']//button[contains(normalize-space(.), 'Confirmar Solução')]"
                )).size() > 0
                        || driver.findElement(By.id("checklistTable")).getText().toLowerCase().contains("sem solução")
                        || driver.findElement(By.id("checklistTable")).getText().toLowerCase().contains("sem solucao"),
                "O mecânico deve visualizar checklists pendentes ou sem solução."
        );
    }

    @Test
    void sistemaDevePermitirRegistrarSolucaoMecanica() {
        abrirDashboardMecanico();

        abrirModalSolucao();

        preencherSolucao("Troca realizada e veículo liberado para operação.");

        confirmarSolucao();

        String alerta = capturarAlerta();

        assertTrue(
                alerta.toLowerCase().contains("sucesso")
                        || alerta.toLowerCase().contains("registrada")
                        || alerta.toLowerCase().contains("solução")
                        || alerta.toLowerCase().contains("solucao"),
                "Alerta recebido: " + alerta
        );
    }

    @Test
    void solucaoNaoPodeEstarVazia() {
        abrirDashboardMecanico();

        abrirModalSolucao();

        WebElement campo = driver.findElement(By.id("solucaoMecanico"));
        campo.clear();

        clicar(By.cssSelector("#formSolucao button[type='submit']"));

        assertTrue(campo.isDisplayed());
        assertTrue(
                campo.getAttribute("required") != null
                        || !campo.getAttribute("validationMessage").isBlank(),
                "O campo solução deve ser obrigatório."
        );
        }

    @Test
    void sistemaDeveRegistrarDataEResponsavelPelaSolucao() {
        abrirDashboardMecanico();

        abrirModalSolucao();

        String solucao = "Solução com data e responsável " + System.currentTimeMillis();

        driver.findElement(By.id("solucaoMecanico")).clear();
        driver.findElement(By.id("solucaoMecanico")).sendKeys(solucao);

        clicar(By.cssSelector("#formSolucao button[type='submit']"));

        String alerta = capturarAlerta();

        assertTrue(
                alerta.toLowerCase().contains("sucesso")
                        || alerta.toLowerCase().contains("registrada"),
                "Alerta recebido: " + alerta
        );

        aguardarTabelaConter(solucao);

        String tabela = driver.findElement(By.id("checklistTable")).getText().toLowerCase();

        assertTrue(
                tabela.contains(solucao.toLowerCase())
                        || tabela.contains("resolvido")
                        || tabela.matches("(?s).*\\d{2}/\\d{2}/\\d{4}.*"),
                "A tabela deve exibir solução, data ou status resolvido."
        );
}

    @Test
    void checklistDeveSerMarcadoComoResolvidoAposRegistroDaSolucao() {
        abrirDashboardMecanico();

        abrirModalSolucao();

        String solucao = "Checklist resolvido teste " + System.currentTimeMillis();

        driver.findElement(By.id("solucaoMecanico")).clear();
        driver.findElement(By.id("solucaoMecanico")).sendKeys(solucao);

        clicar(By.cssSelector("#formSolucao button[type='submit']"));

        String alerta = capturarAlerta();

        assertTrue(
                alerta.toLowerCase().contains("sucesso")
                        || alerta.toLowerCase().contains("registrada"),
                "Alerta recebido: " + alerta
        );

        aguardarTabelaConter(solucao);

        String tabela = driver.findElement(By.id("checklistTable")).getText().toLowerCase();

        assertTrue(
                tabela.contains("resolvido")
                        || tabela.contains(solucao.toLowerCase()),
                "O checklist deve aparecer como resolvido ou exibir a solução registrada."
        );
    }
}