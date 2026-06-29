package br.com.mensageria.model;

import java.time.LocalDateTime;

public class Conversa {

    private Integer id;
    private String titulo;
    private String tipo;
    private LocalDateTime dataCriacao;

    public Conversa() {
    }

    public Conversa(Integer id, String titulo, String tipo, LocalDateTime dataCriacao) {
        this.id = id;
        this.titulo = titulo;
        this.tipo = tipo;
        this.dataCriacao = dataCriacao;
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getTitulo() { return titulo; }

    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getTipo() { return tipo; }

    public void setTipo(String tipo) { this.tipo = tipo; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }

    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    @Override
    public String toString() {
        String nome = titulo == null || titulo.isBlank() ? "Conversa privada" : titulo;
        return String.format("%d | %s | %s", id, nome, tipo);
    }
}
