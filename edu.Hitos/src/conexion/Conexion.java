package conexion;

//Código conexión BD:
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

//Variables importantes
	private static String driver = "com.mysql.cj.jdbc.Driver";
	private static String usuario = "root";
	private static String password = "Kyoko.15";
	private static String url = "jdbc:mysql://localhost:3306/empresa?useSSL=false&serverTimezone=UTC";
	private Connection con = null;

	public Connection getConnection() {
		try {
			con = DriverManager.getConnection(url, usuario, password);
			System.out.println("Conectado a mysql, bienvenido Yukiteru");
		} catch (SQLException e) {
			System.out.println("Error de conexion: " + e.getMessage());
		}
		return con;
	}

	public void close() {
		try {
			if (con != null)
				con.close();
			System.out.println("Conexion cerrada");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}