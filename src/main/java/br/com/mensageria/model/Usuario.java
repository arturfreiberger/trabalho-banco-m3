package br.com.mensageria.model;

import java.time.LocalDateTime;

public class Usuario {

    private Integer id;
    private String nome;
    private String telefone;
    private String email;
    private LocalDateTime dataCadastro;
    private String status;

    public Usuario() {
    }

    public Usuario(Integer id, String nome, String telefone, String email,
                   LocalDateTime dataCadastro, String status) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
        this.dataCadastro = dataCadastro;
        this.status = status;
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }

    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }

    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("%d | %s | %s | %s | %s",
                id, nome, telefone, email == null ? "sem e-mail" : email, status);
    }
}
