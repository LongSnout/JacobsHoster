package dao;

import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {


    public static void insertar(Connection conn, Usuario u) throws SQLException {

        String sql = """
            INSERT INTO usuario (
                nombre_usuario,
                password_hash,
                rol
            ) VALUES (?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getRol());
            ps.executeUpdate();
        }
    }


    public static Usuario obtenerPorNombreUsuario(Connection conn, String nombreUsuario) throws SQLException {

        String sql = "SELECT * FROM usuario WHERE nombre_usuario = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setNombreUsuario(rs.getString("nombre_usuario"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    u.setRol(rs.getString("rol"));
                    return u;
                }
            }
        }

        return null;
    }


    public static boolean existeNombreUsuario(Connection conn, String nombreUsuario) throws SQLException {

        String sql = "SELECT COUNT(*) FROM usuario WHERE nombre_usuario = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }


    public static void actualizarRol(Connection conn, int idUsuario, String nuevoRol) throws SQLException {

        String sql = """
            UPDATE usuario
            SET rol = ?
            WHERE id_usuario = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoRol);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }


    private static Usuario mapear(ResultSet rs) throws SQLException {

        Usuario u = new Usuario();

        u.setIdUsuario(rs.getInt("id_usuario"));
        u.setNombreUsuario(rs.getString("nombre_usuario"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRol(rs.getString("rol"));
        u.setActivo(rs.getInt("activo") == 1);

        return u;
    }
    
    public static void actualizarCredenciales(Connection conn, int idUsuario, String nuevoNombreUsuario, String nuevoPasswordHash) throws SQLException {

        String sql = """
            UPDATE usuario
            SET nombre_usuario = ?,
                password_hash = ?
            WHERE id_usuario = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoNombreUsuario);
            ps.setString(2, nuevoPasswordHash);
            ps.setInt(3, idUsuario);
            ps.executeUpdate();
        }
    }
    
    public static Usuario obtenerPrimerGerente(Connection conn) throws SQLException {

        String sql = """
            SELECT *
            FROM usuario
            WHERE rol = 'GERENTE'
            ORDER BY id_usuario ASC
            LIMIT 1
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return mapear(rs);
            }
        }

        return null;
    }
    
    public static Usuario obtenerPorId(Connection conn, int idUsuario) throws SQLException {

        String sql = "SELECT * FROM usuario WHERE id_usuario = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }

        return null;
    }
    
    public static java.util.List<Usuario> listarTodos(Connection conn) throws SQLException {

        String sql = "SELECT * FROM usuario ORDER BY rol, nombre_usuario";

        java.util.List<Usuario> lista = new java.util.ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = mapear(rs);
                u.setActivo(rs.getInt("activo") == 1);
                lista.add(u);
            }
        }

        return lista;
    }

    public static void actualizarActivo(Connection conn, int idUsuario, boolean activo) throws SQLException {

        String sql = "UPDATE usuario SET activo = ? WHERE id_usuario = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, activo ? 1 : 0);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }
    
}
