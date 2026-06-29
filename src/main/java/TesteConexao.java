import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TesteConexao {

	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3306/mensageria";
		String usuario = "mensageria_app";
		String senha = "mensageria123";

		try (Connection conexao =
					 DriverManager.getConnection(url, usuario, senha)) {

			System.out.println("Conexão realizada com sucesso.");

		} catch (SQLException e) {
			System.err.println("Erro ao conectar ao banco:");
			System.err.println(e.getMessage());
		}
	}
}