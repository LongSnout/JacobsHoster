package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dao.AlbergueDAO;
import dao.UsuarioDAO;
import exception.DatabaseException;
import model.Albergue;
import model.Usuario;
import util.HashUtil;

public class DBInit {
	
	//Esta clase crea la base de datos y tablas la primera vez
	//que se inicia la app. Si ya existe, no hace nada.

    private DBInit() {}

    public static void init() {

        try (Connection conn = DBManager.getConnection()) {

            // 1) Comprobar que hay tablas (detección de ruta/DB equivocada)
            if (!existenTablasMinimas(conn)) {
                throw new DatabaseException("La base de datos no tiene las tablas esperadas. ¿Ruta correcta?");
            }
            asegurarColumnasCama(conn);

            // 2) Semilla: albergue
            if (!AlbergueDAO.existeAlbergue(conn)) {
                Albergue a = new Albergue();
                a.setNombre("Albergue (pendiente de configurar)");
                a.setSincronizacionActiva(false);
                AlbergueDAO.insertarAlbergue(conn, a);
            }

            // 3) Semilla: usuario inicial
            if (!hayUsuarios(conn)) {
                Usuario u = new Usuario();
                u.setNombreUsuario("admin");
                u.setRol("GERENTE");
                u.setPasswordHash(HashUtil.sha256("admin"));
                UsuarioDAO.insertar(conn, u);
            }

        } catch (Exception e) {
            throw new DatabaseException("Error inicializando la base de datos: " + e.getMessage(), e);
        }
    }

    private static boolean existenTablasMinimas(Connection conn) throws Exception {
        return existeTabla(conn, "albergue") && existeTabla(conn, "usuario") && existeTabla(conn, "peregrino");
    }

    private static boolean existeTabla(Connection conn, String nombreTabla) throws Exception {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreTabla);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static boolean hayUsuarios(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM usuario";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    private static void asegurarColumnasCama(Connection conn) throws Exception {
        if (!existeColumna(conn, "cama", "numero_cama")) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "ALTER TABLE cama ADD COLUMN numero_cama INTEGER")) {
                ps.executeUpdate();
            }
        }

        if (!existeColumna(conn, "cama", "activa")) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "ALTER TABLE cama ADD COLUMN activa INTEGER NOT NULL DEFAULT 1")) {
                ps.executeUpdate();
            }
        }

        // Rellenar numero_cama si está vacío
        try (PreparedStatement ps = conn.prepareStatement("""
                UPDATE cama
                SET numero_cama = (
                    SELECT COUNT(*)
                    FROM cama c2
                    WHERE c2.numero_habitacion = cama.numero_habitacion
                      AND c2.rowid <= cama.rowid
                )
                WHERE numero_cama IS NULL
                """)) {
            ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement("""
                CREATE UNIQUE INDEX IF NOT EXISTS idx_cama_habitacion_numero_cama
                ON cama (numero_habitacion, numero_cama)
                """)) {
            ps.executeUpdate();
        }

        try (PreparedStatement ps = conn.prepareStatement("""
                CREATE INDEX IF NOT EXISTS idx_cama_habitacion_activa
                ON cama (numero_habitacion, activa)
                """)) {
            ps.executeUpdate();
        }
    }

    private static boolean existeColumna(Connection conn, String tabla, String columna) throws Exception {
        String sql = "PRAGMA table_info(" + tabla + ")";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String nombre = rs.getString("name");
                if (columna.equalsIgnoreCase(nombre)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    
}



