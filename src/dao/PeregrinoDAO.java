package dao;

import model.Peregrino;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class PeregrinoDAO {


    public static void insertar(Connection conn, Peregrino p) throws SQLException {

        String sql = """
            INSERT INTO peregrino (
                rol, nombre, apellido1, apellido2,
                tipo_documento, numero_documento,
                fecha_nacimiento,
                nacionalidad, sexo,
                direccion, direccion_complementaria,
                codigo_municipio, nombre_municipio,
                codigo_postal, pais,
                telefono1, telefono2,
                correo, parentesco
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getRol());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getApellido1());
            ps.setString(4, p.getApellido2());

            ps.setString(5, p.getTipoDocumento());
            ps.setString(6, p.getNumeroDocumento());

            ps.setString(7, p.getFechaNacimiento());

            ps.setString(8, p.getNacionalidad());
            ps.setString(9, p.getSexo());

            ps.setString(10, p.getDireccion());
            ps.setString(11, p.getDireccionComplementaria());

            ps.setString(12, p.getCodigoMunicipio());
            ps.setString(13, p.getNombreMunicipio());

            ps.setString(14, p.getCodigoPostal());
            ps.setString(15, p.getPais());

            ps.setString(16, p.getTelefono1());
            ps.setString(17, p.getTelefono2());

            ps.setString(18, p.getCorreo());
            ps.setString(19, p.getParentesco());

            ps.executeUpdate();

            // Recuperar el id autogenerado (id_peregrino)
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setIdPeregrino(rs.getInt(1));
                }
            }
        }
    }


    public static void update(Connection conn, Peregrino p) throws SQLException {

        String sql = """
            UPDATE peregrino SET
                rol = ?,
                nombre = ?,
                apellido1 = ?,
                apellido2 = ?,
                tipo_documento = ?,
                numero_documento = ?,
                fecha_nacimiento = ?,
                nacionalidad = ?,
                sexo = ?,
                direccion = ?,
                direccion_complementaria = ?,
                codigo_municipio = ?,
                nombre_municipio = ?,
                codigo_postal = ?,
                pais = ?,
                telefono1 = ?,
                telefono2 = ?,
                correo = ?,
                parentesco = ?
            WHERE id_peregrino = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getRol());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getApellido1());
            ps.setString(4, p.getApellido2());

            ps.setString(5, p.getTipoDocumento());
            ps.setString(6, p.getNumeroDocumento());

            ps.setString(7, p.getFechaNacimiento());

            ps.setString(8, p.getNacionalidad());
            ps.setString(9, p.getSexo());

            ps.setString(10, p.getDireccion());
            ps.setString(11, p.getDireccionComplementaria());

            ps.setString(12, p.getCodigoMunicipio());
            ps.setString(13, p.getNombreMunicipio());

            ps.setString(14, p.getCodigoPostal());
            ps.setString(15, p.getPais());

            ps.setString(16, p.getTelefono1());
            ps.setString(17, p.getTelefono2());

            ps.setString(18, p.getCorreo());
            ps.setString(19, p.getParentesco());

            ps.setInt(20, p.getIdPeregrino());

            ps.executeUpdate();
        }
    }


    public static List<Peregrino> listarTodos(Connection conn) throws SQLException {

        String sql = "SELECT * FROM peregrino ORDER BY id_peregrino DESC";
        List<Peregrino> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }

        return lista;
    }


    public static Peregrino obtenerPorId(Connection conn, int idPeregrino) throws SQLException {

        String sql = "SELECT * FROM peregrino WHERE id_peregrino = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPeregrino);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }

        return null;
    }


    public static Peregrino obtenerPorDocumento(Connection conn, String tipoDocumento, String numeroDocumento)
            throws SQLException {

        String sql = """
            SELECT *
            FROM peregrino
            WHERE tipo_documento = ?
              AND numero_documento = ?
            LIMIT 1
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipoDocumento);
            ps.setString(2, numeroDocumento);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }

        return null;
    }


    private static Peregrino mapear(ResultSet rs) throws SQLException {

        Peregrino p = new Peregrino();

        p.setIdPeregrino(rs.getInt("id_peregrino"));
        p.setRol(rs.getString("rol"));

        p.setNombre(rs.getString("nombre"));
        p.setApellido1(rs.getString("apellido1"));
        p.setApellido2(rs.getString("apellido2"));

        p.setTipoDocumento(rs.getString("tipo_documento"));
        p.setNumeroDocumento(rs.getString("numero_documento"));

        p.setFechaNacimiento(rs.getString("fecha_nacimiento"));

        p.setNacionalidad(rs.getString("nacionalidad"));
        p.setSexo(rs.getString("sexo"));

        p.setDireccion(rs.getString("direccion"));
        p.setDireccionComplementaria(rs.getString("direccion_complementaria"));

        p.setCodigoMunicipio(rs.getString("codigo_municipio"));
        p.setNombreMunicipio(rs.getString("nombre_municipio"));

        p.setCodigoPostal(rs.getString("codigo_postal"));
        p.setPais(rs.getString("pais"));

        p.setTelefono1(rs.getString("telefono1"));
        p.setTelefono2(rs.getString("telefono2"));

        p.setCorreo(rs.getString("correo"));
        p.setParentesco(rs.getString("parentesco"));

        return p;
    }
}
