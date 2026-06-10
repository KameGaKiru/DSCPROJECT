package br.edu.ifpe.dsc.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

import static org.junit.jupiter.api.Assertions.*;

public class RegistroUsuarioTest extends BaseSeleniumTest {

    private void preencherRegistro(String matricula, String nome, String sobrenome, String funcao, String senha) {
        driver.findElement(By.id("matricula")).clear();
        driver.findElement(By.id("matricula")).sendKeys(matricula);

        driver.findElement(By.id("nome")).clear();
        driver.findElement(By.id("nome")).sendKeys(nome);

        driver.findElement(By.id("sobrenome")).clear();
        driver.findElement(By.id("sobrenome")).sendKeys(sobrenome);

        new Select(driver.findElement(By.id("funcao"))).selectByValue(funcao);

        driver.findElement(By.id("senha")).clear();
        driver.findElement(By.id("senha")).sendKeys(senha);
    }

    private void clicarRegistrar() {
        clicar(By.cssSelector("#registroForm button[type='submit']"));
    }

    @Test
    void deveRegistrarUsuarioComSucesso() {
        abrirPagina("registro.html");

        String matricula = String.valueOf(numeroUnico()).substring(0, 6);

        preencherRegistro(matricula, "Usuario", "Teste", "MOTORISTA", "senha123");

        clicarRegistrar();

        String alerta = capturarAlerta();

        assertTrue(alerta.toLowerCase().contains("sucesso"));
    }

    @Test
    void devePermitirApenasMatriculasNumericas() {
        abrirPagina("registro.html");

        driver.findElement(By.id("matricula")).sendKeys("abc123@@");

        String valor = driver.findElement(By.id("matricula")).getAttribute("value");

        assertEquals("123", valor);
    }

    @Test
    void deveLimitarMatriculaEmNoMaximo12Digitos() {
        abrirPagina("registro.html");

        driver.findElement(By.id("matricula")).sendKeys("12345678901234567890");

        String valor = driver.findElement(By.id("matricula")).getAttribute("value");

        assertTrue(valor.length() <= 12);
        assertEquals("123456789012", valor);
    }

    @Test
    void deveBloquearMatriculaDuplicada() {
        abrirPagina("registro.html");

        preencherRegistro(
                MATRICULA_MOTORISTA,
                "Motorista",
                "Duplicado",
                "MOTORISTA",
                "senha123"
        );

        clicarRegistrar();

        String alerta = capturarAlerta();

        assertTrue(
                alerta.toLowerCase().contains("matr")
                        || alerta.toLowerCase().contains("cadastr")
                        || alerta.toLowerCase().contains("duplic"),
                "Alerta recebido: " + alerta
        );
    }

    @Test
    void deveExibirMensagemQuandoMatriculaJaEstaCadastrada() {
        abrirPagina("registro.html");

        preencherRegistro(
                MATRICULA_MOTORISTA,
                "Usuario",
                "Repetido",
                "MOTORISTA",
                "senha123"
        );

        clicarRegistrar();

        String alerta = capturarAlerta();

        assertTrue(
                alerta.toLowerCase().contains("matr")
                        && (
                            alerta.toLowerCase().contains("registr")
                            || alerta.toLowerCase().contains("cadastr")
                            || alerta.toLowerCase().contains("duplic")
                        ),
                "Alerta recebido: " + alerta
        );
    }

    @Test
    void deveBloquearNomeObrigatorio() {
        abrirPagina("registro.html");

        preencherRegistro(
                String.valueOf(numeroUnico()).substring(0, 6),
                "",
                "Teste",
                "MOTORISTA",
                "senha123"
        );

        clicarRegistrar();

        String alerta = capturarAlerta();

        assertTrue(alerta.toLowerCase().contains("nome"), "Alerta recebido: " + alerta);
    }

    @Test
    void deveBloquearSobrenomeObrigatorio() {
        abrirPagina("registro.html");

        preencherRegistro(
                String.valueOf(numeroUnico()).substring(0, 6),
                "Usuario",
                "",
                "MOTORISTA",
                "senha123"
        );

        clicarRegistrar();

        String alerta = capturarAlerta();

        assertTrue(alerta.toLowerCase().contains("sobrenome"), "Alerta recebido: " + alerta);
    }

    @Test
    void deveBloquearNomeApenasComEspacos() {
        abrirPagina("registro.html");

        preencherRegistro(
                String.valueOf(numeroUnico()).substring(0, 6),
                "     ",
                "Teste",
                "MOTORISTA",
                "senha123"
        );

        clicarRegistrar();

        String alerta = capturarAlerta();

        assertTrue(alerta.toLowerCase().contains("nome"), "Alerta recebido: " + alerta);
    }

    @Test
    void deveBloquearSobrenomeApenasComEspacos() {
        abrirPagina("registro.html");

        preencherRegistro(
                String.valueOf(numeroUnico()).substring(0, 6),
                "Usuario",
                "     ",
                "MOTORISTA",
                "senha123"
        );

        clicarRegistrar();

        String alerta = capturarAlerta();

        assertTrue(alerta.toLowerCase().contains("sobrenome"), "Alerta recebido: " + alerta);
    }

    @Test
    void deveBloquearNomeNumerico() {
        abrirPagina("registro.html");

        preencherRegistro(
                String.valueOf(numeroUnico()).substring(0, 6),
                "12345",
                "Teste",
                "MOTORISTA",
                "senha123"
        );

        clicarRegistrar();

        String alerta = capturarAlerta();

        assertTrue(alerta.toLowerCase().contains("nome"), "Alerta recebido: " + alerta);
    }

    @Test
    void deveBloquearSenhaCurta() {
        abrirPagina("registro.html");

        preencherRegistro(
                String.valueOf(numeroUnico()).substring(0, 6),
                "Usuario",
                "Teste",
                "MOTORISTA",
                "123"
        );

        clicarRegistrar();

        String alerta = capturarAlerta();

        assertTrue(alerta.toLowerCase().contains("senha"), "Alerta recebido: " + alerta);
    }

    @Test
    void deveBloquearSenhaMaiorQue20Caracteres() {
        abrirPagina("registro.html");

        preencherRegistro(
                String.valueOf(numeroUnico()).substring(0, 6),
                "Usuario",
                "Teste",
                "MOTORISTA",
                "1234567890123456789012345"
        );

        String valorSenha = driver.findElement(By.id("senha")).getAttribute("value");

        assertTrue(valorSenha.length() <= 20);
    }

    @Test
    void deveCadastrarUsuarioComFuncaoValida() {
        abrirPagina("registro.html");

        String matricula = String.valueOf(numeroUnico()).substring(0, 6);

        preencherRegistro(
                matricula,
                "Usuario",
                "Funcao",
                "MECANICO",
                "senha123"
        );

        clicarRegistrar();

        String alerta = capturarAlerta();

        assertTrue(alerta.toLowerCase().contains("sucesso"), "Alerta recebido: " + alerta);
    }
}