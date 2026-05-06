package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import config.AppConfig;

public class DBManager {

    /**
     * Crea una conexión a la base de datos SQLite.
     * @return Devuelve una conexión a la base de datos SQLite.
     */
    public static Connection getConnection() throws SQLException {

        Connection connection = DriverManager.getConnection(AppConfig.DB_URL);

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON"); // Los pragmas son comandos específicos de SQLite para configurar la conexión.
            stmt.execute("PRAGMA busy_timeout = 5000"); // Esto hace que si la base de datos está bloqueada por otra operación, espere hasta 5 segundos antes de lanzar un error.
        } catch (SQLException e) {
            System.out.println("No se han podido aplicar los PRAGMA de la DB, pero se abre la conexión de todos modos.");
            e.printStackTrace();
        }

        return connection;
    }
}