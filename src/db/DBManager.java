package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import config.AppConfig;

public class DBManager {

    /**
     * Crea una conexión a la base de datos SQLite.
     *
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {

        Connection connection = DriverManager.getConnection(AppConfig.DB_URL);

        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
            stmt.execute("PRAGMA busy_timeout = 5000");
        } catch (SQLException e) {
            System.out.println("No se han podido aplicar los PRAGMA de la DB, pero se abre la conexión de todos modos.");
            e.printStackTrace();
        }

        return connection;
    }
}