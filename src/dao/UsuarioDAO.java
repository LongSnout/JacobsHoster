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

        return u;
    }
}
