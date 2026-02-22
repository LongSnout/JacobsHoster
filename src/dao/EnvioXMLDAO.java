package dao;

import model.EnvioXML;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnvioXMLDAO {


    public static void insertar(Connection conn, EnvioXML envio) throws SQLException {

        String sql = """
            INSERT INTO envio_xml (
                fecha_envio,
                ruta_fichero_xml,
                estado_envio
            ) VALUES (?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, envio.getFechaEnvio());
            ps.setString(2, envio.getRutaFicheroXml());
            ps.setString(3, envio.getEstadoEnvio());

            ps.executeUpdate();
        }
    }


    public static EnvioXML obtenerPorId(Connection conn, int idEnvioXml) throws SQLException {

        String sql = "SELECT * FROM envio_xml WHERE id_envio_xml = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEnvioXml);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }

        return null;
    }


    public static void actualizarEstado(Connection conn, int idEnvioXml, String nuevoEstado) throws SQLException {

        String sql = """
            UPDATE envio_xml
            SET estado_envio = ?
            WHERE id_envio_xml = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idEnvioXml);
            ps.executeUpdate();
        }
    }


    public static void actualizarRutaFichero(Connection conn, int idEnvioXml, String nuevaRuta) throws SQLException {

        String sql = """
            UPDATE envio_xml
            SET ruta_fichero_xml = ?
            WHERE id_envio_xml = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevaRuta);
            ps.setInt(2, idEnvioXml);
            ps.executeUpdate();
        }
    }


    private static EnvioXML mapear(ResultSet rs) throws SQLException {

        EnvioXML e = new EnvioXML();

        e.setIdEnvioXml(rs.getInt("id_envio_xml"));
        e.setFechaEnvio(rs.getString("fecha_envio"));
        e.setRutaFicheroXml(rs.getString("ruta_fichero_xml"));
        e.setEstadoEnvio(rs.getString("estado_envio"));

        return e;
    }
}
