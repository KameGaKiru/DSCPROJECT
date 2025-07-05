package br.edu.ifpe.dsc.model.dto;

import java.util.UUID;

import br.edu.ifpe.dsc.model.FuncaoUsuario;
import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String matricula;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String sobrenome;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuncaoUsuario funcao;

    public Usuario() {}

    public Usuario(String matricula, String nome, String sobrenome, String email, FuncaoUsuario funcao) {
        this.matricula = matricula;
        this.nome = nome;
        this.sobrenome = sobrenome;
        setEmail(email); 
        this.funcao = funcao;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && email.endsWith("@dsc.com")) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("O e-mail deve terminar com @dsc.com");
        }
    }

    public FuncaoUsuario getFuncao() {
        return funcao;
    }

    public void setFuncao(FuncaoUsuario funcao) {
        this.funcao = funcao;
    }
}
