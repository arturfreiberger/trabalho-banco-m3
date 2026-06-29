package br.com.mensageria.model;

import java.time.LocalDateTime;

public class Mensagem {

    private Integer id;
    private Integer idUsuario;
    private Integer idConversa;
    private String autor;
    private String conteudo;
    private LocalDateTime dataEnvio;
    private LocalDateTime dataEdicao;
    private String status;

    public Mensagem() {
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Integer getIdUsuario() { return idUsuario; }

    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public Integer getIdConversa() { return idConversa; }

    public void setIdConversa(Integer idConversa) { this.idConversa = idConversa; }

    public String getAutor() { return autor; }

    public void setAutor(String autor) { this.autor = autor; }

    public String getConteudo() { return conteudo; }

    public void setConteudo(String conteudo) { this.conteudo = conteudo; }

    public LocalDateTime getDataEnvio() { return dataEnvio; }

    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }

    public LocalDateTime getDataEdicao() { return dataEdicao; }

    public void setDataEdicao(LocalDateTime dataEdicao) { this.dataEdicao = dataEdicao; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        String texto = "REMOVIDA".equals(status) ? "Mensagem removida" : conteudo;
        return String.format("%d | %s | %s | %s", id, autor, texto, status);
    }
}
