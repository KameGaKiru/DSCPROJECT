package br.edu.ifpe.dsc.model.dto;

import java.time.LocalDateTime;

public class MensagemChat {

    private String remetenteMatricula;
    private String remetenteNome;
    private String remetenteFuncao;

    private String destinatarioMatricula;

    private String conteudo;
    private TipoMensagemChat tipo;
    private LocalDateTime enviadaEm;

    public MensagemChat() {
    }

    public String getRemetenteMatricula() {
        return remetenteMatricula;
    }

    public void setRemetenteMatricula(String remetenteMatricula) {
        this.remetenteMatricula = remetenteMatricula;
    }

    public String getRemetenteNome() {
        return remetenteNome;
    }

    public void setRemetenteNome(String remetenteNome) {
        this.remetenteNome = remetenteNome;
    }

    public String getRemetenteFuncao() {
        return remetenteFuncao;
    }

    public void setRemetenteFuncao(String remetenteFuncao) {
        this.remetenteFuncao = remetenteFuncao;
    }

    public String getDestinatarioMatricula() {
        return destinatarioMatricula;
    }

    public void setDestinatarioMatricula(String destinatarioMatricula) {
        this.destinatarioMatricula = destinatarioMatricula;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public TipoMensagemChat getTipo() {
        return tipo;
    }

    public void setTipo(TipoMensagemChat tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getEnviadaEm() {
        return enviadaEm;
    }

    public void setEnviadaEm(LocalDateTime enviadaEm) {
        this.enviadaEm = enviadaEm;
    }
    
}
