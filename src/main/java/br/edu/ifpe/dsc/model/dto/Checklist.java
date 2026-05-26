package br.edu.ifpe.dsc.model.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "checklists")
public class Checklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipo;
    private double km;

    private boolean faroisDianteiros;
    private boolean setasDianteiras;
    private boolean faroisTraseiros;
    private boolean setasTraseiras;
    private boolean luzesFreio;
    private boolean nivelOleo;
    private boolean nivelAgua;

    @Column(length = 1000)
    private String observacoes;

    @Column(length = 1000)
    private String solucaoMecanico;

    private LocalDateTime resolvidoEm;

    @ManyToOne
    @JoinColumn(name = "mecanico_id")
    @JsonIgnoreProperties({"senha", "checklists"})
    private Usuario mecanico;

    private LocalDateTime criadoEm;

    @ManyToOne
    @JoinColumn(name = "motorista_id")
    @JsonIgnoreProperties({"senha", "checklists"})
    private Usuario motorista;

    @ManyToOne
    @JoinColumn(name = "veiculo_id", nullable = true)
    @JsonIgnoreProperties({"checklists"})
    private Veiculo veiculo;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public double getKm() { return km; }
    public void setKm(double km) { this.km = km; }

    public boolean isFaroisDianteiros() { return faroisDianteiros; }
    public void setFaroisDianteiros(boolean v) { this.faroisDianteiros = v; }

    public boolean isSetasDianteiras() { return setasDianteiras; }
    public void setSetasDianteiras(boolean v) { this.setasDianteiras = v; }

    public boolean isFaroisTraseiros() { return faroisTraseiros; }
    public void setFaroisTraseiros(boolean v) { this.faroisTraseiros = v; }

    public boolean isSetasTraseiras() { return setasTraseiras; }
    public void setSetasTraseiras(boolean v) { this.setasTraseiras = v; }

    public boolean isLuzesFreio() { return luzesFreio; }
    public void setLuzesFreio(boolean v) { this.luzesFreio = v; }

    public boolean isNivelOleo() { return nivelOleo; }
    public void setNivelOleo(boolean v) { this.nivelOleo = v; }

    public boolean isNivelAgua() { return nivelAgua; }
    public void setNivelAgua(boolean v) { this.nivelAgua = v; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String o) { this.observacoes = o; }

    public String getSolucaoMecanico() { return solucaoMecanico; }
    public void setSolucaoMecanico(String s) { this.solucaoMecanico = s; }

    public LocalDateTime getResolvidoEm() { return resolvidoEm; }
    public void setResolvidoEm(LocalDateTime r) { this.resolvidoEm = r; }

    public Usuario getMecanico() { return mecanico; }
    public void setMecanico(Usuario mecanico) { this.mecanico = mecanico; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime c) { this.criadoEm = c; }

    public Usuario getMotorista() { return motorista; }
    public void setMotorista(Usuario motorista) { this.motorista = motorista; }

    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }
}