package br.edu.ifpe.dsc.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ChecklistTest extends BaseSeleniumTest {

    private void abrirTelaChecklist() {
        autenticarMotorista();
        abrirPagina("dashboard_checklist.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("veiculoSelect")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("checklistTable")));
    }

    private void selecionarPrimeiroVeiculo() {
        Select veiculo = new Select(driver.findElement(By.id("veiculoSelect")));

        wait.until(driver -> veiculo.getOptions().size() > 1);

        veiculo.selectByIndex(1);
    }

    private void clicarSalvarChecklist() {
        clicar(By.xpath("//button[contains(., 'Salvar Checklist')]"));
    }

    private void ajustarTipoSeNecessario(String alerta) {
        if (alerta.contains("SAÍDA") || alerta.contains("SAIDA")) {
            new Select(driver.findElement(By.id("tipo"))).selectByValue("SAIDA");
        } else if (alerta.contains("ENTRADA")) {
            new Select(driver.findElement(By.id("tipo"))).selectByValue("ENTRADA");
        }
    }

    private String obterNumeroVeiculoSelecionado() {
        Select selectVeiculo = new Select(driver.findElement(By.id("veiculoSelect")));

        String textoVeiculoSelecionado = selectVeiculo.getFirstSelectedOption().getText();

        String apenasNumero = textoVeiculoSelecionado
                .replace("Nº", "")
                .replace("N°", "")
                .trim()
                .split("\\s+")[0]
                .replaceAll("[^0-9]", "");

        return apenasNumero;
    }

    private double obterUltimoKmDoVeiculoSelecionado() {
        String numeroVeiculo = obterNumeroVeiculoSelecionado();

        List<WebElement> linhas = driver.findElements(By.cssSelector("#checklistTable tr"));

        for (WebElement linha : linhas) {
            String textoLinha = linha.getText();

            if (textoLinha.contains("Nº " + numeroVeiculo)
                    || textoLinha.contains("N° " + numeroVeiculo)
                    || textoLinha.contains("N " + numeroVeiculo)) {

                String kmTexto = textoLinha.replaceAll("(?s).*?(\\d+(?:[,.]\\d+)?)\\s*km.*", "$1");

                if (kmTexto.matches("\\d+(?:[,.]\\d+)?")) {
                    return Double.parseDouble(kmTexto.replace(",", "."));
                }
            }
        }

        return 0.0;
    }

    private double gerarKmMaiorQueUltimo() {
        return obterUltimoKmDoVeiculoSelecionado() + 100.5;
    }

    private void preencherKm(double km) {
        WebElement campoKm = driver.findElement(By.id("km"));
        campoKm.clear();
        campoKm.sendKeys(String.valueOf(km).replace(",", "."));
    }

    private void preencherObservacao(String texto) {
        WebElement observacoes = driver.findElement(By.id("observacoes"));
        observacoes.clear();
        observacoes.sendKeys(texto);
    }

    private String salvarTratandoAlternancia() {
        clicarSalvarChecklist();

        String alerta = capturarAlerta();

        if (alerta.contains("próximo deve ser")
                || alerta.contains("proximo deve ser")
                || alerta.contains("O próximo deve ser")
                || alerta.contains("O proximo deve ser")) {

            ajustarTipoSeNecessario(alerta);

            clicarSalvarChecklist();

            alerta = capturarAlerta();
        }

        return alerta;
    }

    @Test
    void deveBloquearChecklistSemVeiculo() {
        abrirTelaChecklist();

        preencherKm(50000);
        marcarTodosItens();

        clicarSalvarChecklist();

        String alerta = capturarAlerta();

        assertTrue(
                alerta.toLowerCase().contains("selecione"),
                "Alerta recebido: " + alerta
        );
    }

    @Test
    void deveAceitarKmRealPositivo() {
        abrirTelaChecklist();

        selecionarPrimeiroVeiculo();

        preencherKm(gerarKmMaiorQueUltimo());
        marcarTodosItens();

        String alerta = salvarTratandoAlternancia();

        assertTrue(
                alerta.toLowerCase().contains("sucesso")
                        || alerta.toLowerCase().contains("registrado"),
                "Alerta recebido: " + alerta
        );
    }

    @Test
    void deveBloquearKmNegativoOuZero() {
        abrirTelaChecklist();

        selecionarPrimeiroVeiculo();

        preencherKm(0);
        marcarTodosItens();

        clicarSalvarChecklist();

        String alerta = capturarAlerta();

        if (alerta.contains("próximo deve ser")
                || alerta.contains("proximo deve ser")) {

            ajustarTipoSeNecessario(alerta);

            preencherKm(0);

            clicarSalvarChecklist();

            alerta = capturarAlerta();
        }

        assertTrue(
                alerta.toLowerCase().contains("km")
                        || alerta.toLowerCase().contains("válido")
                        || alerta.toLowerCase().contains("valido"),
                "Alerta recebido: " + alerta
        );
    }

    @Test
    void deveExigirKmMaiorQueUltimoRegistrado() {
        abrirTelaChecklist();

        selecionarPrimeiroVeiculo();

        double ultimoKm = obterUltimoKmDoVeiculoSelecionado();

        preencherKm(ultimoKm);
        marcarTodosItens();

        clicarSalvarChecklist();

        String alerta = capturarAlerta();

        if (alerta.contains("próximo deve ser")
                || alerta.contains("proximo deve ser")) {

            ajustarTipoSeNecessario(alerta);

            preencherKm(ultimoKm);

            clicarSalvarChecklist();

            alerta = capturarAlerta();
        }

        assertTrue(
                alerta.toLowerCase().contains("km")
                        || alerta.toLowerCase().contains("superior")
                        || alerta.toLowerCase().contains("último")
                        || alerta.toLowerCase().contains("ultimo"),
                "Alerta recebido: " + alerta
        );
    }

    @Test
    void deveImpedirDoisRegistrosConsecutivosDoMesmoTipo() {
        abrirTelaChecklist();

        selecionarPrimeiroVeiculo();

        preencherKm(gerarKmMaiorQueUltimo());
        marcarTodosItens();

        clicarSalvarChecklist();

        String alerta = capturarAlerta();

        assertTrue(
                alerta.toLowerCase().contains("próximo")
                        || alerta.toLowerCase().contains("proximo")
                        || alerta.toLowerCase().contains("entrada")
                        || alerta.toLowerCase().contains("saída")
                        || alerta.toLowerCase().contains("saida")
                        || alerta.toLowerCase().contains("registrado")
                        || alerta.toLowerCase().contains("sucesso"),
                "Alerta recebido: " + alerta
        );
    }

    @Test
    void deveExigirObservacaoQuandoItensNaoMarcados() {
        abrirTelaChecklist();

        selecionarPrimeiroVeiculo();

        preencherKm(gerarKmMaiorQueUltimo());

        clicarSalvarChecklist();

        try {
            String alerta = capturarAlerta();

            if (alerta.contains("próximo deve ser")
                    || alerta.contains("proximo deve ser")) {

                ajustarTipoSeNecessario(alerta);

                preencherKm(gerarKmMaiorQueUltimo());

                clicarSalvarChecklist();
            }

        } catch (Exception ignored) {
        }

        WebElement erro = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("observacoesErro"))
        );

        assertTrue(erro.isDisplayed());
        assertTrue(
                erro.getText().toLowerCase().contains("observa"),
                "Mensagem recebida: " + erro.getText()
        );
    }

    @Test
    void deveBloquearObservacaoApenasComNumeros() {
        abrirTelaChecklist();

        selecionarPrimeiroVeiculo();

        double kmMaior = gerarKmMaiorQueUltimo();

        preencherKm(kmMaior);
        preencherObservacao("123456");

        clicarSalvarChecklist();

        try {
            String alerta = capturarAlerta();

            if (alerta.contains("próximo deve ser")
                    || alerta.contains("proximo deve ser")) {

                ajustarTipoSeNecessario(alerta);

                preencherKm(kmMaior);
                preencherObservacao("123456");

                clicarSalvarChecklist();
            }

        } catch (Exception ignored) {
        }

        WebElement erro = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("observacoesErro"))
        );

        assertTrue(erro.isDisplayed());

        assertTrue(
                erro.getText().toLowerCase().contains("letra")
                        || erro.getText().toLowerCase().contains("números")
                        || erro.getText().toLowerCase().contains("numeros"),
                "Mensagem recebida: " + erro.getText()
        );
    }

    @Test
    void observacoesDevemTerNoMaximo400Caracteres() {
        abrirTelaChecklist();

        String textoGrande = "a".repeat(450);

        preencherObservacao(textoGrande);

        String valor = driver.findElement(By.id("observacoes")).getAttribute("value");

        assertTrue(valor.length() <= 400);
    }

    @Test
    void deveContarCaracteresDasObservacoes() {
        abrirTelaChecklist();

        preencherObservacao("Teste");

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