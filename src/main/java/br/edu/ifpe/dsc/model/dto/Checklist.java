package br.edu.ifpe.dsc.model.dto;

import java.util.UUID;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "checklist")
public class Checklist {

    @Id
    @GeneratedValue
    private UUID id;

    private String tipo;
    private int km;

    private String observacoes;
    private String observacoesMecanicas;

    private boolean faroisDianteiros;
    private boolean setasDianteiras;
    private boolean faroisTraseiros;
    private boolean setasTraseiras;
    private boolean nivelOleo;
    private boolean nivelAgua;
    private boolean luzesFreio;

    @ManyToOne
    @JoinColumn(name = "motorista_id", nullable = false)
    private Usuario motorista;

    @ManyToOne
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    private LocalDateTime criadoEm = LocalDateTime.now();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getKm() {
        return km;
    }

    public void setKm(int km) {
        this.km = km;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public String getObservacoesMecanicas() {
        return observacoesMecanicas;
    }

    public void setObservacoesMecanicas(String observacoesMecanicas) {
        this.observacoesMecanicas = observacoesMecanicas;
    }

    public boolean isFaroisDianteiros() {
        return faroisDianteiros;
    }

    public void setFaroisDianteiros(boolean faroisDianteiros) {
        this.faroisDianteiros = faroisDianteiros;
    }

    public boolean isSetasDianteiras() {
        return setasDianteiras;
    }

    public void setSetasDianteiras(boolean setasDianteiras) {
        this.setasDianteiras = setasDianteiras;
    }

    public boolean isFaroisTraseiros() {
        return faroisTraseiros;
    }

    public void setFaroisTraseiros(boolean faroisTraseiros) {
        this.faroisTraseiros = faroisTraseiros;
    }

    public boolean isSetasTraseiras() {
        return setasTraseiras;
    }

    public void setSetasTraseiras(boolean setasTraseiras) {
        this.setasTraseiras = setasTraseiras;
    }

    public boolean isNivelOleo() {
        return nivelOleo;
    }

    public void setNivelOleo(boolean nivelOleo) {
        this.nivelOleo = nivelOleo;
    }

    public boolean isNivelAgua() {
        return nivelAgua;
    }

    public void setNivelAgua(boolean nivelAgua) {
        this.nivelAgua = nivelAgua;
    }

    public boolean isLuzesFreio() {
        return luzesFreio;
    }

    public void setLuzesFreio(boolean luzesFreio) {
        this.luzesFreio = luzesFreio;
    }

    public Usuario getMotorista() {
        return motorista;
    }

    public void setMotorista(Usuario motorista) {
        this.motorista = motorista;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
