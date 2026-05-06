package dao;

import model.Albergue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AlbergueDAO {

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
                install_registered_at,
                hora_apertura,
                hora_cierre,
                fecha_apertura_desde,
                fecha_apertura_hasta,
                observaciones_apertura
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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

            ps.setInt(11, albergue.isSincronizacionActiva() ? 1 : 0);

            ps.setString(12, albergue.getFechaUltimaSincronizacion());

            ps.setString(13, albergue.getApiBaseUrl());
            ps.setString(14, albergue.getInstallId());
            ps.setString(15, albergue.getInstallSecret());
            ps.setString(16, albergue.getInstallRegisteredAt());

            ps.setString(17, albergue.getHoraApertura());
            ps.setString(18, albergue.getHoraCierre());
            ps.setString(19, albergue.getFechaAperturaDesde());
            ps.setString(20, albergue.getFechaAperturaHasta());
            ps.setString(21, albergue.getObservacionesApertura());

            ps.executeUpdate();
        }
    }

    public static boolean existeAlbergue(Connection conn) throws SQLException {

        String sql = "SELECT COUNT(*) FROM albergue";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() && rs.getInt(1) > 0;
        }
    }

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
                codigo_establecimiento_mir = ?,
                hora_apertura = ?,
                hora_cierre = ?,
                fecha_apertura_desde = ?,
                fecha_apertura_hasta = ?,
                observaciones_apertura = ?
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
            ps.setString(9, a.getHoraApertura());
            ps.setString(10, a.getHoraCierre());
            ps.setString(11, a.getFechaAperturaDesde());
            ps.setString(12, a.getFechaAperturaHasta());
            ps.setString(13, a.getObservacionesApertura());
            ps.setInt(14, a.getIdAlbergue());
            ps.executeUpdate();
        }
    }

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

        a.setHoraApertura(rs.getString("hora_apertura"));
        a.setHoraCierre(rs.getString("hora_cierre"));
        a.setFechaAperturaDesde(rs.getString("fecha_apertura_desde"));
        a.setFechaAperturaHasta(rs.getString("fecha_apertura_hasta"));
        a.setObservacionesApertura(rs.getString("observaciones_apertura"));

        return a;
    }

    // Método para resetear el albergue a valores por defecto que de momento creo que no se usa en ningún sitio, al menos no en la UI, pero lo dejo por si acaso en el futuro...
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
                numeracion_camas_activa = 0,
                hora_apertura = NULL,
                hora_cierre = NULL,
                fecha_apertura_desde = NULL,
                fecha_apertura_hasta = NULL,
                observaciones_apertura = NULL
            WHERE id_albergue = 1
            """;
        						// de momento usamos id_albergue = 1 porque ahora solo se usa un albergue por instalación de app, pero es provisional
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
}