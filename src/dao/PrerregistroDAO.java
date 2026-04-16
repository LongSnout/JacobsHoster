package dao;

import model.Prerregistro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PrerregistroDAO {

	
    public static void insertar(Connection conn, Prerregistro pr) throws SQLException {

        String sql = """
            INSERT INTO preregistro (
                id_albergue, id_peregrino,
                id_preregistro_nube,
                fecha_envio, fecha_prevista_llegada,
                estado_preregistro,
                rol, nombre, apellido1, apellido2,
                tipo_documento, numero_documento,
                fecha_nacimiento, nacionalidad, sexo,
                direccion, direccion_complementaria,
                codigo_municipio, nombre_municipio,
                codigo_postal, pais,
                telefono1, telefono2, correo,
                parentesco
            ) VALUES (
                ?, ?,
                ?,
                ?, ?,
                ?,
                ?, ?, ?, ?,
                ?, ?,
                ?, ?, ?,
                ?, ?,
                ?, ?,
                ?, ?,
                ?, ?, ?,
                ?
            )
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pr.getIdAlbergue());

            // id_peregrino puede ser NULL
            if (pr.getIdPeregrino() == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, pr.getIdPeregrino());
            }

            ps.setString(3, pr.getIdPrerregistroNube());

            ps.setString(4, pr.getFechaEnvio());
            ps.setString(5, pr.getFechaPrevistaLlegada());

            ps.setString(6, pr.getEstadoPrerregistro());

            ps.setString(7, pr.getRol());
            ps.setString(8, pr.getNombre());
            ps.setString(9, pr.getApellido1());
            ps.setString(10, pr.getApellido2());

            ps.setString(11, pr.getTipoDocumento());
            ps.setString(12, pr.getNumeroDocumento());

            ps.setString(13, pr.getFechaNacimiento());
            ps.setString(14, pr.getNacionalidad());
            ps.setString(15, pr.getSexo());

            ps.setString(16, pr.getDireccion());
            ps.setString(17, pr.getDireccionComplementaria());

            ps.setString(18, pr.getCodigoMunicipio());
            ps.setString(19, pr.getNombreMunicipio());

            ps.setString(20, pr.getCodigoPostal());
            ps.setString(21, pr.getPais());

            ps.setString(22, pr.getTelefono1());
            ps.setString(23, pr.getTelefono2());
            ps.setString(24, pr.getCorreo());

            ps.setString(25, pr.getParentesco());

            ps.executeUpdate();
        }
    }


    public static Prerregistro obtenerPorId(Connection conn, int idPrerregistro) throws SQLException {

        String sql = "SELECT * FROM preregistro WHERE id_preregistro = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPrerregistro);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }

        return null;
    }


    public static List<Prerregistro> listarPendientesPorFechaLlegada(Connection conn) throws SQLException {

        String sql = """
            SELECT * FROM preregistro
            WHERE estado_preregistro = 'PENDIENTE'
            ORDER BY fecha_prevista_llegada ASC
            """;

        List<Prerregistro> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }

        return lista;
    }


    public static void actualizarEstado(Connection conn, int idPrerregistro, String nuevoEstado) throws SQLException {

        String sql = """
            UPDATE preregistro
            SET estado_preregistro = ?
            WHERE id_preregistro = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idPrerregistro);
            ps.executeUpdate();
        }
    }


    private static Prerregistro mapear(ResultSet rs) throws SQLException {

        Prerregistro pr = new Prerregistro();

        pr.setIdPrerregistro(rs.getInt("id_preregistro"));

        pr.setIdAlbergue(rs.getInt("id_albergue"));

        // id_peregrino puede ser NULL
        int idP = rs.getInt("id_peregrino");
        if (rs.wasNull()) {
            pr.setIdPeregrino(null);
        } else {
            pr.setIdPeregrino(idP);
        }

        pr.setIdPrerregistroNube(rs.getString("id_preregistro_nube"));

        pr.setFechaEnvio(rs.getString("fecha_envio"));
        pr.setFechaPrevistaLlegada(rs.getString("fecha_prevista_llegada"));

        pr.setEstadoPrerregistro(rs.getString("estado_preregistro"));

        pr.setRol(rs.getString("rol"));
        pr.setNombre(rs.getString("nombre"));
        pr.setApellido1(rs.getString("apellido1"));
        pr.setApellido2(rs.getString("apellido2"));

        pr.setTipoDocumento(rs.getString("tipo_documento"));
        pr.setNumeroDocumento(rs.getString("numero_documento"));

        pr.setFechaNacimiento(rs.getString("fecha_nacimiento"));
        pr.setNacionalidad(rs.getString("nacionalidad"));
        pr.setSexo(rs.getString("sexo"));

        pr.setDireccion(rs.getString("direccion"));
        pr.setDireccionComplementaria(rs.getString("direccion_complementaria"));

        pr.setCodigoMunicipio(rs.getString("codigo_municipio"));
        pr.setNombreMunicipio(rs.getString("nombre_municipio"));

        pr.setCodigoPostal(rs.getString("codigo_postal"));
        pr.setPais(rs.getString("pais"));

        pr.setTelefono1(rs.getString("telefono1"));
        pr.setTelefono2(rs.getString("telefono2"));
        pr.setCorreo(rs.getString("correo"));

        pr.setParentesco(rs.getString("parentesco"));

        return pr;
    }
    
    
    public static boolean existePorIdNube(Connection conn, String idNube) throws SQLException {
        String sql = "SELECT COUNT(*) FROM preregistro WHERE id_preregistro_nube = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idNube);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    
    
    
    
}




