package br.edu.ifpe.dsc.model.dto;

import java.util.UUID;

import br.edu.ifpe.dsc.model.MarcaVeiculo;
import br.edu.ifpe.dsc.model.TipoVeiculo;
import jakarta.persistence.*;

@Entity
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private int numero; 

    @Column(unique = true, nullable = false)
    private String placa;

    
    @Enumerated(EnumType.STRING)
    private MarcaVeiculo marca;

    @Enumerated(EnumType.STRING)
    private TipoVeiculo tipo;

    public Veiculo() {}

    public Veiculo(String placa, MarcaVeiculo marca, TipoVeiculo tipo, int numero) {
        this.placa = placa;
        this.marca = marca;
        this.tipo = tipo;
        this.numero = numero;
    }

    public UUID getId() {
        return id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public MarcaVeiculo getMarca() {
        return marca;
    }

    public void setMarca(MarcaVeiculo marca) {
        this.marca = marca;
    }

    public TipoVeiculo getTipo() {
        return tipo;
    }

    public void setTipo(TipoVeiculo tipo) {
        this.tipo = tipo;
    }
}