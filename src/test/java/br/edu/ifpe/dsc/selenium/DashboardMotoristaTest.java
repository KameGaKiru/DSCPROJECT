package br.edu.ifpe.dsc.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardMotoristaTest extends BaseSeleniumTest {

    private void abrirDashboardMotorista() {
        autenticarMotorista();
        abrirPagina("dashboard_motorista.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("totalChecklists")));
    }

    @Test
    void dashboardDeveExibirTotalDeChecklistsDoMotorista() {
        abrirDashboardMotorista();

        assertTrue(driver.findElement(By.id("totalChecklists")).isDisplayed());

        String total = driver.findElement(By.id("totalChecklists")).getText();

        assertFalse(total.isBlank(), "O total de checklists do motorista deve estar preenchido.");
    }

    @Test
    void dashboardDeveExibirRankingDoMotorista() {
        abrirDashboardMotorista();

        assertTrue(driver.findElement(By.id("rankingPosicao")).isDisplayed());

        String ranking = driver.findElement(By.id("rankingPosicao")).getText();

        assertFalse(ranking.isBlank(), "A posição no ranking deve estar preenchida.");
    }

    @Test
    void dashboardDeveExibirResumoDoRanking() {
        abrirDashboardMotorista();

        assertTrue(driver.findElement(By.id("rankingSub")).isDisplayed());

        String resumo = driver.findElement(By.id("rankingSub")).getText();

        assertTrue(
                resumo.toLowerCase().contains("motorista")
                        || resumo.toLowerCase().contains("comparado")
                        || resumo.toLowerCase().contains("entre"),
                "O resumo do ranking deve informar a comparação entre motoristas."
        );
    }

    @Test
    void dashboardDeveExibirListaDeFuncionarios() {
        abrirDashboardMotorista();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("usuariosTable")));

        assertTrue(driver.findElement(By.id("usuariosTable")).isDisplayed());

        String tabela = driver.findElement(By.id("usuariosTable")).getText();

        assertFalse(tabela.isBlank(), "A tabela de funcionários deve exibir dados.");
    }

    @Test
    void dashboardDeveDestacarMotoristaLogadoNaTabela() {
        abrirDashboardMotorista();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("usuariosTable")));

        String tabela = driver.findElement(By.id("usuariosTable")).getText();

        assertTrue(
                tabela.contains(MATRICULA_MOTORISTA),
                "A tabela deve conter a matrícula do motorista logado."
        );
    }

    @Test
    void botaoFazerChecklistDeveRedirecionarParaTelaChecklist() {
        abrirDashboardMotorista();

        clicar(By.id("checklistBtn"));

        wait.until(ExpectedConditions.urlContains("dashboard_checklist.html"));

        assertTrue(driver.getCurrentUrl().contains("dashboard_checklist.html"));
    }

    @Test
    void botaoSairDeveEncerrarSessaoERedirecionarParaLogin() {
        abrirDashboardMotorista();

        clicar(By.id("logoutBtn"));

        wait.until(ExpectedConditions.urlContains("index.html"));

        assertTrue(driver.getCurrentUrl().contains("index.html"));
    }

    @Test
    void usuarioNaoAutenticadoNaoDeveAcessarDashboardMotorista() {
        abrirPagina("dashboard_motorista.html");

        String alerta = capturarAlerta();

        assertTrue(
                alerta.toLowerCase().contains("acesso negado")
                        || alerta.toLowerCase().contains("sessão")
                        || alerta.toLowerCase().contains("sessao"),
                "Alerta recebido: " + alerta
        );

        wait.until(ExpectedConditions.urlContains("index.html"));

        assertTrue(driver.getCurrentUrl().contains("index.html"));
    }
}
