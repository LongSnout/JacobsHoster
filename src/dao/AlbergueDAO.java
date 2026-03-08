package dao;

import model.Albergue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AlbergueDAO {

    /**
     * Inserta un albergue en la BD.
     * Normalmente solo habrá uno.
     */
    public static void insertarAlbergue(Connection conn, Albergue albergue) throws SQLException {

        String sql = """
            INSERT INTO albergue (
                nombre,
                direccion,
                municipio,
                provincia,
                pais,
                telefono,
                email,
                codigo_establecimiento_mir,
                id_albergue_nube,
                api_key,
                sincronizacion_activa,
                fecha_ultima_sincronizacion,
                api_base_url,
                install_id,
                install_secret,
                install_registered_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, albergue.getNombre());
            ps.setString(2, albergue.getDireccion());
            ps.setString(3, albergue.getMunicipio());
            ps.setString(4, albergue.getProvincia());
            ps.setString(5, albergue.getPais());
            ps.setString(6, albergue.getTelefono());
            ps.setString(7, albergue.getEmail());

            ps.setString(8, albergue.getCodigoEstablecimientoMir());
            ps.setString(9, albergue.getIdAlbergueNube());
            ps.setString(10, albergue.getApiKey());

            // boolean Java → INTEGER SQLite (0/1)
            ps.setInt(11, albergue.isSincronizacionActiva() ? 1 : 0);

            ps.setString(12, albergue.getFechaUltimaSincronizacion());

            // Nuevos campos (emparejado / configuración cloud)
            ps.setString(13, albergue.getApiBaseUrl());
            ps.setString(14, albergue.getInstallId());
            ps.setString(15, albergue.getInstallSecret());
            ps.setString(16, albergue.getInstallRegisteredAt());

            ps.executeUpdate();
        }
    }

    /**
     * Comprueba si ya existe algún albergue en la BD.
     */
    public static boolean existeAlbergue(Connection conn) throws SQLException {

        String sql = "SELECT COUNT(*) FROM albergue";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() && rs.getInt(1) > 0;
        }
    }

    /**
     * Devuelve el primer albergue encontrado (si existe).
     */
    public static Albergue obtenerAlbergue(Connection conn) throws SQLException {

        String sql = "SELECT * FROM albergue LIMIT 1";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return mapearAlbergue(rs);
            }
        }

        return null;
    }

    /**
     * Actualiza los datos "de ficha" del albergue.
     */
    public static void actualizarDatosBasicos(Connection conn, Albergue a) throws SQLException {

        String sql = """
            UPDATE albergue
            SET nombre = ?,
                direccion = ?,
                municipio = ?,
                provincia = ?,
                pais = ?,
                telefono = ?,
                email = ?,
                codigo_establecimiento_mir = ?
            WHERE id_albergue = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getNombre());
            ps.setString(2, a.getDireccion());
            ps.setString(3, a.getMunicipio());
            ps.setString(4, a.getProvincia());
            ps.setString(5, a.getPais());
            ps.setString(6, a.getTelefono());
            ps.setString(7, a.getEmail());
            ps.setString(8, a.getCodigoEstablecimientoMir());
            ps.setInt(9, a.getIdAlbergue());
            ps.executeUpdate();
        }
    }

    /**
     * Actualiza los datos de sincronización (id nube, api key, estado sincronización, fecha última sincronización).
     */
    public static void actualizarSync(Connection conn, Albergue a) throws SQLException {

        String sql = """
            UPDATE albergue
            SET id_albergue_nube = ?,
                api_key = ?,
                sincronizacion_activa = ?,
                fecha_ultima_sincronizacion = ?
            WHERE id_albergue = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, a.getIdAlbergueNube());
            ps.setString(2, a.getApiKey());
            ps.setInt(3, a.isSincronizacionActiva() ? 1 : 0);
            ps.setString(4, a.getFechaUltimaSincronizacion());
            ps.setInt(5, a.getIdAlbergue());
            ps.executeUpdate();
        }
    }

    /**
     * guarda las credenciales de emparejado (install_id, install_secret) y la URL base de la API
     */
    public static void guardarCredencialesInstalacion(Connection conn,
                                                      int idAlbergue,
                                                      String apiBaseUrl,
                                                      String installId,
                                                      String installSecret,
                                                      String installRegisteredAt) throws SQLException {

        String sql = """
            UPDATE albergue
            SET api_base_url = ?,
                install_id = ?,
                install_secret = ?,
                install_registered_at = ?
            WHERE id_albergue = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, apiBaseUrl);
            ps.setString(2, installId);
            ps.setString(3, installSecret);
            ps.setString(4, installRegisteredAt);
            ps.setInt(5, idAlbergue);
            ps.executeUpdate();
        }
    }

    /**
     * Limpia las credenciales de emparejado (install_id, install_secret) y la URL base de la API, desactivando la sincronización.
     */
    public static void limpiarCredencialesInstalacion(Connection conn, int idAlbergue) throws SQLException {

        String sql = """
            UPDATE albergue
            SET api_base_url = NULL,
                install_id = NULL,
                install_secret = NULL,
                install_registered_at = NULL
            WHERE id_albergue = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAlbergue);
            ps.executeUpdate();
        }
    }

    /*
     * Mapear un ResultSet a un objeto Albergue.
     */

    private static Albergue mapearAlbergue(ResultSet rs) throws SQLException {
        Albergue a = new Albergue();

        a.setIdAlbergue(rs.getInt("id_albergue"));
        a.setNombre(rs.getString("nombre"));
        a.setDireccion(rs.getString("direccion"));
        a.setMunicipio(rs.getString("municipio"));
        a.setProvincia(rs.getString("provincia"));
        a.setPais(rs.getString("pais"));
        a.setTelefono(rs.getString("telefono"));
        a.setEmail(rs.getString("email"));

        a.setCodigoEstablecimientoMir(rs.getString("codigo_establecimiento_mir"));
        a.setIdAlbergueNube(rs.getString("id_albergue_nube"));
        a.setApiKey(rs.getString("api_key"));

        a.setSincronizacionActiva(rs.getInt("sincronizacion_activa") == 1);
        a.setFechaUltimaSincronizacion(rs.getString("fecha_ultima_sincronizacion"));

        a.setApiBaseUrl(rs.getString("api_base_url"));
        a.setInstallId(rs.getString("install_id"));
        a.setInstallSecret(rs.getString("install_secret"));
        a.setInstallRegisteredAt(rs.getString("install_registered_at"));

        return a;
    }
    
    public static void resetearAlbergueActual(Connection conn) throws SQLException {

        String sql = """
            UPDATE albergue
            SET nombre = 'SIN CONFIGURAR',
                direccion = NULL,
                municipio = NULL,
                provincia = NULL,
                pais = 'ESP',
                telefono = NULL,
                email = NULL,
                codigo_establecimiento_mir = NULL,
                id_albergue_nube = NULL,
                api_key = NULL,
                sincronizacion_activa = 0,
                fecha_ultima_sincronizacion = NULL,
                api_base_url = NULL,
                install_id = NULL,
                install_secret = NULL,
                install_registered_at = NULL,
                admite_reservas = 0,
                numeracion_camas_activa = 0
            WHERE id_albergue = 1
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
    
    
}


