package br.com.mensageria.dao;

import br.com.mensageria.model.Usuario;
import br.com.mensageria.util.ConexaoFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO {

    public int inserir(Usuario usuario) throws SQLException {
        final String sql = "INSERT INTO usuario (nome, telefone, email, status) VALUES (?, ?, ?, ?)";

        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            comando.setString(1, usuario.getNome());
            comando.setString(2, usuario.getTelefone());
            if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
                comando.setNull(3, Types.VARCHAR);
            } else {
                comando.setString(3, usuario.getEmail());
            }
            comando.setString(4, usuario.getStatus());
            comando.executeUpdate();

            try (ResultSet chaves = comando.getGeneratedKeys()) {
                if (chaves.next()) {
                    usuario.setId(chaves.getInt(1));
                    return usuario.getId();
                }
            }
        }

        throw new SQLException("O banco não retornou o ID do usuário inserido.");
    }

    public List<Usuario> listar() throws SQLException {
        final String sql = "SELECT id_usuario, nome, telefone, email, data_cadastro, status " +
                "FROM usuario ORDER BY nome";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql);
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {
                usuarios.add(mapear(resultado));
            }
        }
        return usuarios;
    }

    public Optional<Usuario> buscarPorId(int id) throws SQLException {
        final String sql = "SELECT id_usuario, nome, telefone, email, data_cadastro, status " +
                "FROM usuario WHERE id_usuario = ?";

        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);

            try (ResultSet resultado = comando.executeQuery()) {
                return resultado.next() ? Optional.of(mapear(resultado)) : Optional.empty();
            }
        }
    }

    public boolean atualizar(Usuario usuario) throws SQLException {
        final String sql = "UPDATE usuario SET nome = ?, telefone = ?, email = ?, status = ? " +
                "WHERE id_usuario = ?";

        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, usuario.getNome());
            comando.setString(2, usuario.getTelefone());
            if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
                comando.setNull(3, Types.VARCHAR);
            } else {
                comando.setString(3, usuario.getEmail());
            }
            comando.setString(4, usuario.getStatus());
            comando.setInt(5, usuario.getId());
            return comando.executeUpdate() == 1;
        }
    }

    public boolean excluir(int id) throws SQLException {
        final String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        try (Connection conexao = ConexaoFactory.obterConexao();
             PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setInt(1, id);
            return comando.executeUpdate() == 1;
        }
    }

    private Usuario mapear(ResultSet resultado) throws SQLException {
        Timestamp cadastro = resultado.getTimestamp("data_cadastro");
        return new Usuario(
                resultado.getInt("id_usuario"),
                resultado.getString("nome"),
                resultado.getString("telefone"),
                resultado.getString("email"),
                cadastro == null ? null : cadastro.toLocalDateTime(),
                resultado.getString("status")
        );
    }
}
