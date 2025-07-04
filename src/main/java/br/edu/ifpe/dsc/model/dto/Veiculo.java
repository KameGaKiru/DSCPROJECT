package br.edu.ifpe.dsc.model.dto;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String placa;
    private String modelo;
    private String tipo;
    private int numero;

    public Veiculo() {}

    public Veiculo(String placa, String modelo, String tipo, int numero) {
        this.placa = placa;
        this.modelo = modelo;
        this.tipo = tipo;
        this.numero = numero;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }
}
