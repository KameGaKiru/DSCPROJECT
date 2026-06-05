package br.edu.ifpe.dsc.model.dto;

import br.edu.ifpe.dsc.model.MarcaVeiculo;
import br.edu.ifpe.dsc.model.TipoVeiculo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "veiculos")
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Positive(message = "O número do veículo deve ser positivo.")
    @Max(value = 9999999999L,
        message = "O número do veículo deve ter no máximo 10 dígitos.")
    @Column(unique = true, nullable = false)
    private int numero;

    @NotNull(message = "A placa é obrigatória.")
    @Pattern(
        regexp = "^[A-Z]{3}-([0-9]{4}|[0-9][A-Z][0-9]{2})$",
        message = "Placa inválida. Utilize AAA-1234 ou AAA-1A23."
    )
    @Column(unique = true, nullable = false)
    private String placa;

    @NotNull(message = "A marca é obrigatória.")
    @Enumerated(EnumType.STRING)
    private MarcaVeiculo marca;

    @NotNull(message = "O tipo é obrigatório.")
    @Enumerated(EnumType.STRING)
    private TipoVeiculo tipo;

public Veiculo() {}

public Veiculo(String placa, MarcaVeiculo marca, TipoVeiculo tipo, int numero) {
    this.placa = placa;
    this.marca = marca;
    this.tipo = tipo;
    this.numero = numero;
}

public Long getId() {
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