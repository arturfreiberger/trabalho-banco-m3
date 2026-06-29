package br.com.mensageria.dao;

import br.com.mensageria.model.Conversa;
import br.com.mensageria.util.ConexaoFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConversaDAO {

    public int inserirComParticipantes(Conversa conversa, List<Integer> participantes) throws SQLException {
        final String sqlConversa = "INSERT INTO conversa (titulo, tipo) VALUES (?, ?)";
        final String sqlParticipante = "INSERT INTO participante " +
                "(id_usuario, id_conversa, funcao) VALUES (?, ?, ?)";

        if (participantes == null || participantes.isEmpty()) {
            throw new IllegalArgumentException("A conversa precisa de pelo menos um participante.");
        }
        if ("PRIVADA".equals(conversa.getTipo()) && participantes.size() != 2) {
            throw new IllegalArgumentException("Uma conversa privada deve possuir exatamente dois participantes.");
        }

        try (Connection conexao = ConexaoFactory.obterConexao()) {
            conexao.setAutoCommit(false);
            try {
                try (PreparedStatement comando = conexao.prepareStatement(
                        sqlConversa, Statement.RETURN_GENERATED_KEYS)) {
                    if (conversa.getTitulo() == null || conversa.getTitulo().isBlank()) {
                        comando.setNull(1, Types.VARCHAR);
                    } else {
                        comando.setString(1, conversa.getTitulo());
                    }
                    comando.setString(2, conversa.getTipo());
                    comando.executeUpdate();

                    try (ResultSet chaves = comando.getGeneratedKeys()) {
                        if (!chaves.next()) {
                            throw new SQLException("O banco não retornou o ID da conversa.");
                        }
                        conversa.setId(chaves.getInt(1));
                    }
                }

                try (PreparedStatement comando = conexao.prepareStatement(sqlParticipante)) {
                    for (int i = 0; i < participantes.size(); i++) {
                        comando.setInt(1, participantes.get(i));
                        comando.setInt(2, conversa.getId());
                        comando.setString(3, i == 0 ? "ADMINISTRADOR" : "MEMBRO");
                        comando.addBatch();
                    }
                    comando.executeBatch();
                }

                conexao.commit();
                return conversa.getId();
            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            } finally {
                conexao.setAutoCommit(true);
            }
        }
    }

    public List<Conversa> listar() throws SQLException {
        final String sql = "SELECT id_conversa, titulo, tipo, data_criacao FROM conversa ORDER BY id_conversa";
        List<Conversa> conversas = new ArrayList<>();

        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                conversas.add(mapear(resultado));
            }
        }
        return conversas;
    }

    public Optional<Conversa> buscarPorId(int id) throws SQLException {
        final String sql = "SELECT id_conversa, titulo, tipo, data_criacao " +
                "FROM conversa WHERE id_conversa = ?";
        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        }
    }

    public List<String> listarParticipantes(int idConversa) throws SQLException {
        final String sql = "SELECT u.id_usuario, u.nome, p.funcao " +
                "FROM participante p JOIN usuario u ON u.id_usuario = p.id_usuario " +
                "WHERE p.id_conversa = ? ORDER BY p.data_entrada, u.nome";
        List<String> participantes = new ArrayList<>();

        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idConversa);
            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    participantes.add(String.format("%d | %s | %s",
                            resultado.getInt("id_usuario"),
                            resultado.getString("nome"),
                            resultado.getString("funcao")));
                }
            }
        }
        return participantes;
    }

    public boolean atualizar(Conversa conversa) throws SQLException {
        final String sql = "UPDATE conversa SET titulo = ?, tipo = ? WHERE id_conversa = ?";
        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            if (conversa.getTitulo() == null || conversa.getTitulo().isBlank()) {
                comando.setNull(1, Types.VARCHAR);
            } else {
                comando.setString(1, conversa.getTitulo());
            }
            comando.setString(2, conversa.getTipo());
            comando.setInt(3, conversa.getId());
            return comando.executeUpdate() == 1;
        }
    }

    public void adicionarParticipante(int idConversa, int idUsuario, String funcao) throws SQLException {
        final String sql = "INSERT INTO participante (id_usuario, id_conversa, funcao) VALUES (?, ?, ?)";
        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idUsuario);
            comando.setInt(2, idConversa);
            comando.setString(3, funcao);
            comando.executeUpdate();
        }
    }

    public boolean removerParticipante(int idConversa, int idUsuario) throws SQLException {
        final String sql = "DELETE FROM participante WHERE id_usuario = ? AND id_conversa = ?";
        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, idUsuario);
            comando.setInt(2, idConversa);
            return comando.executeUpdate() == 1;
        }
    }

    public boolean excluir(int idConversa) throws SQLException {
        final String sqlMensagens = "DELETE FROM mensagem WHERE id_conversa = ?";
        final String sqlConversa = "DELETE FROM conversa WHERE id_conversa = ?";

        try (Connection conexao = ConexaoFactory.obterConexao()) {
            conexao.setAutoCommit(false);
            try {
                try (PreparedStatement comando = conexao.prepareStatement(sqlMensagens)) {
                    comando.setInt(1, idConversa);
                    comando.executeUpdate();
                }
                int linhas;
                try (PreparedStatement comando = conexao.prepareStatement(sqlConversa)) {
                    comando.setInt(1, idConversa);
                    linhas = comando.executeUpdate();
                }
                conexao.commit();
                return linhas == 1;
            } catch (SQLException e) {
                conexao.rollback();
                throw e;
            } finally {
                conexao.setAutoCommit(true);
            }
        }
    }

    private Conversa mapear(ResultSet resultado) throws SQLException {
        Timestamp criacao = resultado.getTimestamp("data_criacao");
        return new Conversa(
                resultado.getInt("id_conversa"),
                resultado.getString("titulo"),
                resultado.getString("tipo"),
                criacao == null ? null : criacao.toLocalDateTime()
        );
    }
}
