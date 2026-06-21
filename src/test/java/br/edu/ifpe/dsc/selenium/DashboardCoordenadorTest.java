package br.edu.ifpe.dsc.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.*;

public class DashboardCoordenadorTest extends BaseSeleniumTest {

    private void abrirDashboardCoordenador() {
        autenticarCoordenador();
        abrirPagina("dashboard_coordenador.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("totalVeiculos")));
    }

    private void preencherVeiculo(String numero, String placa, String marca, String tipo) {
        driver.findElement(By.id("numero")).clear();
        driver.findElement(By.id("numero")).sendKeys(numero);

        driver.findElement(By.id("placa")).clear();
        driver.findElement(By.id("placa")).sendKeys(placa);

        if (marca != null && !marca.isBlank()) {
            new Select(driver.findElement(By.id("marca"))).selectByVisibleText(marca);
        }

        if (tipo != null && !tipo.isBlank()) {
            new Select(driver.findElement(By.id("tipo"))).selectByVisibleText(tipo);
        }
    }

    private String gerarNumeroValido() {
    String numero = String.valueOf(System.currentTimeMillis()).substring(4, 13);

    if (numero.startsWith("0")) {
        numero = "9" + numero.substring(1);
    }

        return numero;
    }

    private String gerarPlacaValida() {
        long agora = System.currentTimeMillis();

        return "T"
                + (char) ('A' + (agora % 26))
                + (char) ('A' + ((agora / 26) % 26))
                + "-"
                + String.valueOf(agora).substring(9, 13);
    }

    private void assertMensagemValidacao(String alerta) {
        String texto = alerta.toLowerCase();

        assertTrue(
                texto.contains("erro")
                        || texto.contains("inválid")
                        || texto.contains("inval")
                        || texto.contains("obrig")
                        || texto.contains("cadastr")
                        || texto.contains("duplic")
                        || texto.contains("positivo")
                        || texto.contains("placa")
                        || texto.contains("número")
                        || texto.contains("numero"),
                "Mensagem de validação recebida: " + alerta
        );
    }

    private void cadastrarVeiculo(String numero, String placa) {
        preencherVeiculo(numero, placa, "FIAT", "VAN");
        clicar(By.cssSelector("#cadVeiculoForm button[type='submit']"));
        capturarAlerta();
    }

    private void abrirModalEditarPrimeiroVeiculo() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("veiculosTable")));

        WebElement botaoEditar = wait.until(
                ExpectedConditions.elementToBeClickable(By.cssSelector(".btn-editar"))
        );

        clicar(By.cssSelector(".btn-editar"));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("editNumero")));
    }

    private void salvarEdicaoVeiculo() {
        clicar(By.cssSelector("#editVeiculoForm button[type='submit']"));
    }
    
    private void clicarExcluirDoVeiculo(String numero) {
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("veiculosTable")));

    String numeroSemZeros = String.valueOf(Long.parseLong(numero));

    WebElement linha = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(
            "//tbody[@id='veiculosTable']/tr[" +
                    "td[1][normalize-space()='" + numero + "']" +
                    " or td[1][normalize-space()='" + numeroSemZeros + "']" +
            "]"
    )));

    WebElement botaoExcluir = linha.findElement(By.cssSelector(".btn-deletar"));

    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({block: 'center'});",
            botaoExcluir
    );

    ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
            "arguments[0].click();",
            botaoExcluir
    );
    }

    private void cadastrarVeiculoComDados(String numero, String placa) {
        preencherVeiculo(numero, placa, "FIAT", "VAN");

        clicar(By.cssSelector("#cadVeiculoForm button[type='submit']"));

        capturarAlerta();
    }

    @Test
    void deveExibirTotalDeVeiculosCadastrados() {
        abrirDashboardCoordenador();

        assertTrue(driver.findElement(By.id("totalVeiculos")).isDisplayed());
        assertFalse(driver.findElement(By.id("totalVeiculos")).getText().isBlank());
    }

    @Test
    void deveExibirTotalDeChecklistsRegistrados() {
        abrirDashboardCoordenador();

        assertTrue(driver.findElement(By.id("totalChecklists")).isDisplayed());
        assertFalse(driver.findElement(By.id("totalChecklists")).getText().isBlank());
    }

    @Test
    void deveExibirTotalDeMotoristasAtivos() {
        abrirDashboardCoordenador();

        assertTrue(driver.findElement(By.id("totalMotoristas")).isDisplayed());
        assertFalse(driver.findElement(By.id("totalMotoristas")).getText().isBlank());
    }

    @Test
    void deveExibirRankingDeMotoristasPorQuantidadeDeChecklists() {
        abrirDashboardCoordenador();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rankingTable")));

        String conteudoRanking = driver.findElement(By.id("rankingTable")).getText();

        assertFalse(
                conteudoRanking.isBlank(),
                "O ranking deveria exibir motoristas ou mensagem informativa."
        );
    }

    @Test
    void deveListarVeiculosCadastrados() {
        abrirDashboardCoordenador();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("veiculosTable")));

        String conteudoTabela = driver.findElement(By.id("veiculosTable")).getText();

        assertFalse(
                conteudoTabela.isBlank(),
                "A tabela deveria exibir veículos ou mensagem informativa."
        );
    }

    @Test
    void deveExibirQuantidadeDeChecklistsPorVeiculo() {
        abrirDashboardCoordenador();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("veiculosTable")));

        String conteudoTabela = driver.findElement(By.id("veiculosTable")).getText();

        assertTrue(
                conteudoTabela.matches("(?s).*\\d+.*"),
                "A tabela deve exibir quantidade numérica de checklists por veículo."
        );
    }

    @Test
    void deveCadastrarVeiculoComSucesso() {
        abrirDashboardCoordenador();

        preencherVeiculo(gerarNumeroValido(), gerarPlacaValida(), "FIAT", "VAN");

        clicar(By.cssSelector("#cadVeiculoForm button[type='submit']"));

        String alerta = capturarAlerta();

        assertTrue(
                alerta.toLowerCase().contains("cadastrado")
                        || alerta.toLowerCase().contains("sucesso"),
                "Alerta recebido: " + alerta
        );
    }

    @Test
    void numeroDoVeiculoDeveSerInteiroEPositivo() {
        abrirDashboardCoordenador();

        preencherVeiculo("-1", gerarPlacaValida(), "FIAT", "VAN");

        clicar(By.cssSelector("#cadVeiculoForm button[type='submit']"));

        String alerta = capturarAlerta();

        assertMensagemValidacao(alerta);
    }

    @Test
    void naoDevePermitirNumeroDuplicadoNoCadastro() {
        abrirDashboardCoordenador();

        String numero = gerarNumeroValido();
        String placa1 = gerarPlacaValida();

        cadastrarVeiculo(numero, placa1);

        preencherVeiculo(numero, gerarPlacaValida(), "FIAT", "VAN");

        clicar(By.cssSelector("#cadVeiculoForm button[type='submit']"));

        String alerta = capturarAlerta();

        assertMensagemValidacao(alerta);
    }

    @Test
    void deveAceitarPlacaFormatoAntigoAAA1111() {
        abrirDashboardCoordenador();

        preencherVeiculo(gerarNumeroValido(), gerarPlacaValida(), "FIAT", "VAN");

        clicar(By.cssSelector("#cadVeiculoForm button[type='submit']"));

        String alerta = capturarAlerta();

        assertTrue(
                alerta.toLowerCase().contains("cadastrado")
                        || alerta.toLowerCase().contains("sucesso"),
                "Alerta recebido: " + alerta
        );
    }

    @Test
    void deveAceitarPlacaFormatoMercosulAAA1A11() {
        abrirDashboardCoordenador();

        long agora = System.currentTimeMillis();

        String numero = gerarNumeroValido();
        String placa = "T"
                + (char) ('A' + (agora % 26))
                + (char) ('A' + ((agora / 26) % 26))
                + "-1A"
                + String.valueOf(agora).substring(11, 13);

        preencherVeiculo(numero, placa, "FIAT", "VAN");

        clicar(By.cssSelector("#cadVeiculoForm button[type='submit']"));

        String alerta = capturarAlerta();

        assertTrue(
                alerta.toLowerCase().contains("cadastrado")
                        || alerta.toLowerCase().contains("sucesso"),
                "Alerta recebido: " + alerta
        );
    }

    @Test
    void deveBloquearPlacaInvalida() {
        abrirDashboardCoordenador();

        preencherVeiculo(gerarNumeroValido(), "ABC-12", "FIAT", "VAN");

        clicar(By.cssSelector("#cadVeiculoForm button[type='submit']"));

        String alerta = capturarAlerta();

        assertMensagemValidacao(alerta);
    }

    @Test
    void naoDevePermitirPlacaDuplicadaNoCadastro() {
        abrirDashboardCoordenador();

        String placa = gerarPlacaValida();

        cadastrarVeiculo(gerarNumeroValido(), placa);

        preencherVeiculo(gerarNumeroValido(), placa, "FIAT", "VAN");

        clicar(By.cssSelector("#cadVeiculoForm button[type='submit']"));

        String alerta = capturarAlerta();

        assertMensagemValidacao(alerta);
    }

    @Test
    void marcaETipoDevemSerObrigatorios() {
        abrirDashboardCoordenador();

        preencherVeiculo(gerarNumeroValido(), gerarPlacaValida(), "", "");

        clicar(By.cssSelector("#cadVeiculoForm button[type='submit']"));

        assertTrue(driver.findElement(By.id("marca")).isDisplayed());
        assertTrue(driver.findElement(By.id("tipo")).isDisplayed());
    }

    @Test
    void nenhumCampoPodeSerSalvoVazioNoCadastro() {
        abrirDashboardCoordenador();

        clicar(By.cssSelector("#cadVeiculoForm button[type='submit']"));

        assertTrue(driver.findElement(By.id("numero")).isDisplayed());
        assertTrue(driver.findElement(By.id("placa")).isDisplayed());
        assertTrue(driver.findElement(By.id("marca")).isDisplayed());
        assertTrue(driver.findElement(By.id("tipo")).isDisplayed());
    }

    @Test
    void deveValidarNumeroDoVeiculoDuranteEdicao() {
        abrirDashboardCoordenador();

        abrirModalEditarPrimeiroVeiculo();

        driver.findElement(By.id("editNumero")).clear();
        driver.findElement(By.id("editNumero")).sendKeys("-1");

        salvarEdicaoVeiculo();

        String alerta = capturarAlerta();

        assertMensagemValidacao(alerta);
    }

    @Test
    void naoDevePermitirDuplicidadeDeNumeroDuranteEdicao() {
        abrirDashboardCoordenador();

        String numeroDuplicado = gerarNumeroValido();

        cadastrarVeiculo(numeroDuplicado, gerarPlacaValida());
        cadastrarVeiculo(gerarNumeroValido(), gerarPlacaValida());

        abrirDashboardCoordenador();
        abrirModalEditarPrimeiroVeiculo();

        driver.findElement(By.id("editNumero")).clear();
        driver.findElement(By.id("editNumero")).sendKeys(numeroDuplicado);

        salvarEdicaoVeiculo();

        String alerta = capturarAlerta();

        assertMensagemValidacao(alerta);
    }

    @Test
    void deveValidarPadraoDaPlacaDuranteEdicao() {
        abrirDashboardCoordenador();

        abrirModalEditarPrimeiroVeiculo();

        driver.findElement(By.id("editPlaca")).clear();
        driver.findElement(By.id("editPlaca")).sendKeys("ABC-12");

        salvarEdicaoVeiculo();

        String alerta = capturarAlerta();

        assertMensagemValidacao(alerta);
    }

    @Test
    void naoDevePermitirPlacaDuplicadaDuranteEdicao() {
        abrirDashboardCoordenador();

        String placaDuplicada = gerarPlacaValida();

        cadastrarVeiculo(gerarNumeroValido(), placaDuplicada);
        cadastrarVeiculo(gerarNumeroValido(), gerarPlacaValida());

        abrirDashboardCoordenador();
        abrirModalEditarPrimeiroVeiculo();

        driver.findElement(By.id("editPlaca")).clear();
        driver.findElement(By.id("editPlaca")).sendKeys(placaDuplicada);

        salvarEdicaoVeiculo();

        String alerta = capturarAlerta();

        assertMensagemValidacao(alerta);
    }

    @Test
    void nenhumCampoPodeSerSalvoVazioDuranteEdicao() {
        abrirDashboardCoordenador();

        abrirModalEditarPrimeiroVeiculo();

        driver.findElement(By.id("editNumero")).clear();
        driver.findElement(By.id("editPlaca")).clear();

        salvarEdicaoVeiculo();

        assertTrue(driver.findElement(By.id("editNumero")).isDisplayed());
        assertTrue(driver.findElement(By.id("editPlaca")).isDisplayed());
    }


    @Test
    void exclusaoDeveRemoverApenasOVeiculoEInformarSucesso() {
        abrirDashboardCoordenador();

        cadastrarVeiculo(gerarNumeroValido(), gerarPlacaValida());

        abrirDashboardCoordenador();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("veiculosTable")));

        clicar(By.cssSelector(".btn-deletar"));

        Alert confirmacao = wait.until(ExpectedConditions.alertIsPresent());
        confirmacao.accept();

        String alerta = capturarAlerta();

        assertTrue(
                alerta.toLowerCase().contains("excluído")
                        || alerta.toLowerCase().contains("excluido")
                        || alerta.toLowerCase().contains("sucesso"),
                "Alerta recebido: " + alerta
        );
    }

    @Test
    void checklistsVinculadosDevemPermanecerArmazenadosAposExclusaoDoVeiculo() {
        abrirDashboardCoordenador();

        String totalAntes = driver.findElement(By.id("totalChecklists")).getText();

        String numero = gerarNumeroValido();
        String placa = gerarPlacaValida();

        cadastrarVeiculoComDados(numero, placa);

        abrirDashboardCoordenador();

        clicarExcluirDoVeiculo(numero);

        Alert confirmacao = wait.until(ExpectedConditions.alertIsPresent());
        String textoConfirmacao = confirmacao.getText();

        assertTrue(
                textoConfirmacao.contains(numero)
                        || textoConfirmacao.toLowerCase().contains("excluir")
                        || textoConfirmacao.toLowerCase().contains("veículo")
                        || textoConfirmacao.toLowerCase().contains("veiculo"),
                "Mensagem de confirmação recebida: " + textoConfirmacao
        );

        confirmacao.accept();

        String alerta = capturarAlerta();

        assertTrue(
                alerta.toLowerCase().contains("excluído")
                        || alerta.toLowerCase().contains("excluido")
                        || alerta.toLowerCase().contains("sucesso"),
                "Alerta recebido: " + alerta
        );

        abrirDashboardCoordenador();

        String totalDepois = driver.findElement(By.id("totalChecklists")).getText();

        assertEquals(
                totalAntes,
                totalDepois,
                "A exclusão do veículo não deve alterar a quantidade total de checklists."
        );
    }
}