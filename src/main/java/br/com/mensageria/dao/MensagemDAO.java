package br.com.mensageria.dao;

import br.com.mensageria.model.Mensagem;
import br.com.mensageria.util.ConexaoFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MensagemDAO {

    public int inserir(Mensagem mensagem) throws SQLException {
        final String sql = "INSERT INTO mensagem (id_usuario, id_conversa, conteudo) VALUES (?, ?, ?)";
        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            comando.setInt(1, mensagem.getIdUsuario());
            comando.setInt(2, mensagem.getIdConversa());
            comando.setString(3, mensagem.getConteudo());
            comando.executeUpdate();

            try (ResultSet chaves = comando.getGeneratedKeys()) {
                if (chaves.next()) {
                    mensagem.setId(chaves.getInt(1));
                    return mensagem.getId();
                }
            }
        }
        throw new SQLException("O banco não retornou o ID da mensagem.");
    }

    public List<Mensagem> listarPorConversa(int idConversa) throws SQLException {
        final String sql = "SELECT m.id_mensagem, m.id_usuario, m.id_conversa, u.nome AS autor, " +
                "m.conteudo, m.data_envio, m.data_edicao, m.status " +
                "FROM mensagem m JOIN usuario u ON u.id_usuario = m.id_usuario " +
                "WHERE m.id_conversa = ? ORDER BY m.data_envio, m.id_mensagem";
        List<Mensagem> mensagens = new ArrayList<>();

        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idConversa);
            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    mensagens.add(mapear(resultado));
                }
            }
        }
        return mensagens;
    }

    public Optional<Mensagem> buscarPorId(int id) throws SQLException {
        final String sql = "SELECT m.id_mensagem, m.id_usuario, m.id_conversa, u.nome AS autor, " +
                "m.conteudo, m.data_envio, m.data_edicao, m.status " +
                "FROM mensagem m JOIN usuario u ON u.id_usuario = m.id_usuario " +
                "WHERE m.id_mensagem = ?";
        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        }
    }

    public boolean atualizarConteudo(int id, String conteudo) throws SQLException {
        final String sql = "UPDATE mensagem SET conteudo = ?, data_edicao = CURRENT_TIMESTAMP, " +
                "status = 'EDITADA' WHERE id_mensagem = ? AND status <> 'REMOVIDA'";
        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, conteudo);
            comando.setInt(2, id);
            return comando.executeUpdate() == 1;
        }
    }

    public boolean removerLogicamente(int id) throws SQLException {
        final String sql = "UPDATE mensagem SET conteudo = 'Mensagem removida', " +
                "data_edicao = CURRENT_TIMESTAMP, status = 'REMOVIDA' WHERE id_mensagem = ?";
        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            return comando.executeUpdate() == 1;
        }
    }

    public boolean excluirFisicamente(int id) throws SQLException {
        final String sql = "DELETE FROM mensagem WHERE id_mensagem = ?";
        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            return comando.executeUpdate() == 1;
        }
    }

    private Mensagem mapear(ResultSet resultado) throws SQLException {
        Mensagem mensagem = new Mensagem();
        mensagem.setId(resultado.getInt("id_mensagem"));
        mensagem.setIdUsuario(resultado.getInt("id_usuario"));
        mensagem.setIdConversa(resultado.getInt("id_conversa"));
        mensagem.setAutor(resultado.getString("autor"));
        mensagem.setConteudo(resultado.getString("conteudo"));
        Timestamp envio = resultado.getTimestamp("data_envio");
        Timestamp edicao = resultado.getTimestamp("data_edicao");
        mensagem.setDataEnvio(envio == null ? null : envio.toLocalDateTime());
        mensagem.setDataEdicao(edicao == null ? null : edicao.toLocalDateTime());
        mensagem.setStatus(resultado.getString("status"));
        return mensagem;
    }
}
