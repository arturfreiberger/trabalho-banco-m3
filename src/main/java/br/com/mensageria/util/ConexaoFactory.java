package br.com.mensageria.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConexaoFactory {
    private static final String ARQUIVO = "/database.properties";
    private static final Properties PROPRIEDADES = carregarPropriedades();

    private ConexaoFactory() {
    }

    private static Properties carregarPropriedades() {
        Properties propriedades = new Properties();

        try (InputStream entrada = ConexaoFactory.class.getResourceAsStream(ARQUIVO)) {
            if (entrada == null) {
                throw new IllegalStateException(
                        "Arquivo database.properties não encontrado em src/main/resources. " +
                        "Copie database.properties.example e informe as credenciais do banco."
                );
            }

            propriedades.load(entrada);
            return propriedades;
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler database.properties.", e);
        }
    }

    public static Connection obterConexao() throws SQLException {
        String url = PROPRIEDADES.getProperty("db.url");
        String usuario = PROPRIEDADES.getProperty("db.user");
        String senha = PROPRIEDADES.getProperty("db.password");

        if (url == null || usuario == null || senha == null) {
            throw new IllegalStateException("As propriedades db.url, db.user e db.password são obrigatórias.");
        }

        return DriverManager.getConnection(url, usuario, senha);
    }
}
