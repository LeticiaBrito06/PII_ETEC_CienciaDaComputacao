package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    public static Connection conectar() {
        try {

            String url = System.getenv("DB_URL");
            String user = System.getenv("DB_USER");
            String password = System.getenv("DB_PASSWORD");

            Connection conn = DriverManager.getConnection(url, user, password);

            System.out.println("Conexão realizada com sucesso!");
            return conn;

        } catch (SQLException e) {
            System.out.println("Erro ao conectar com o banco!");
            System.err.println(e.getMessage());
            return null;
        }
    }
}