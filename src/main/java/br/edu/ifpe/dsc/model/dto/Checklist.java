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

    @Column(nullable = false)
    private String tipo;

    @Column(nullable = false)
    private int km;

    private boolean faroisDianteiros;
    private boolean setasDianteiras;
    private boolean faroisTraseiros;
    private boolean setasTraseiras;
    private boolean luzesFreio;
    private boolean nivelOleo;
    private boolean nivelAgua;

    @Column(length = 1000)
    private String observacoes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @ManyToOne
    @JoinColumn(name = "motorista_id", nullable = false)
    @JsonIgnoreProperties({"senha", "checklists"})
    private Usuario motorista;

    @ManyToOne
    @JoinColumn(name = "veiculo_id", nullable = false)
    @JsonIgnoreProperties({"checklists"})
    private Veiculo veiculo;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }

    public Long getId() { 
        return id; 
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

    public boolean isLuzesFreio() { 
        return luzesFreio; 

    }

    public void setLuzesFreio(boolean luzesFreio) { 
        this.luzesFreio = luzesFreio; 
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

    public String getObservacoes() { 
        return observacoes; 
    }

    public void setObservacoes(String observacoes) { 
        this.observacoes = observacoes; 
    }

    public LocalDateTime getCriadoEm() { 
        return criadoEm; 
    }

    public void setCriadoEm(LocalDateTime criadoEm) { 
        this.criadoEm = criadoEm; 
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
}