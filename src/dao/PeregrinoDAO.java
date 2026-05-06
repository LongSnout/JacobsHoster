package dao;

import model.Peregrino;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
                correo, parentesco,
                soporte_documento
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
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
            ps.setString(20, p.getSoporteDocumento());

            ps.executeUpdate();

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
                parentesco = ?,
                soporte_documento = ?
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
            ps.setString(20, p.getSoporteDocumento());

            ps.setInt(21, p.getIdPeregrino());

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
        p.setSoporteDocumento(rs.getString("soporte_documento"));

        return p;
    }

    public static void eliminarPorId(Connection conn, int idPeregrino) throws SQLException {

        // 1. Borrar líneas de venta de sus estancias
        String sqlVentas = """
            DELETE FROM venta_linea
            WHERE id_estancia IN (
                SELECT id_estancia FROM estancia WHERE id_peregrino = ?
            )
            """;
        try (PreparedStatement ps = conn.prepareStatement(sqlVentas)) {
            ps.setInt(1, idPeregrino);
            ps.executeUpdate();
        }

        // 2. Borrar estancias
        String sqlEstancias = "DELETE FROM estancia WHERE id_peregrino = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlEstancias)) {
            ps.setInt(1, idPeregrino);
            ps.executeUpdate();
        }

        // 3. Borrar peregrino
        String sqlPeregrino = "DELETE FROM peregrino WHERE id_peregrino = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlPeregrino)) {
            ps.setInt(1, idPeregrino);
            ps.executeUpdate();
        }
    }

    
    /**
     * Lista peregrinos presentes en una fecha concreta.
     * Incluye también a quienes tienen salida prevista ese mismo día,
     * porque todavía pueden aparecer en la gestión diaria.
     */
    public static List<Peregrino> listarPresentesPorDia(Connection conn, int idAlbergue, String fechaISO) throws SQLException {

        String sql = """
            SELECT DISTINCT p.*
            FROM peregrino p
            JOIN estancia e ON e.id_peregrino = p.id_peregrino
            WHERE e.id_albergue = ?
              AND e.estado_estancia <> 'CANCELADA'
              AND e.fecha_entrada <= ?
              AND (
                    (e.fecha_salida_real IS NOT NULL AND e.fecha_salida_real >= ?)
                    OR
                    (e.fecha_salida_real IS NULL AND e.fecha_salida_prevista IS NOT NULL AND e.fecha_salida_prevista >= ?)
                    OR
                    (e.fecha_salida_real IS NULL AND e.fecha_salida_prevista IS NULL)
                  )
            ORDER BY p.apellido1, p.apellido2, p.nombre
            """;

        List<Peregrino> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAlbergue);
            ps.setString(2, fechaISO);
            ps.setString(3, fechaISO);
            ps.setString(4, fechaISO);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }

        return lista;
    }

    public static List<Peregrino> buscarGlobal(Connection conn, String texto) throws SQLException {

        String sql = """
            SELECT *
            FROM peregrino
            WHERE UPPER(COALESCE(nombre, '')) LIKE ?
               OR UPPER(COALESCE(apellido1, '')) LIKE ?
               OR UPPER(COALESCE(apellido2, '')) LIKE ?
               OR UPPER(COALESCE(tipo_documento, '')) LIKE ?
               OR UPPER(COALESCE(numero_documento, '')) LIKE ?
               OR UPPER(COALESCE(telefono1, '')) LIKE ?
               OR UPPER(COALESCE(telefono2, '')) LIKE ?
               OR UPPER(COALESCE(correo, '')) LIKE ?
               OR UPPER(COALESCE(soporte_documento, '')) LIKE ?
            ORDER BY id_peregrino DESC
            """;

        List<Peregrino> lista = new ArrayList<>();
        String patron = "%" + texto.trim().toUpperCase() + "%";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 9; i++) {
                ps.setString(i, patron);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }

        return lista;
    }
    
    public static List<Peregrino> listarPorRangoFechas(Connection conn,
            int idAlbergue, String fechaDesde, String fechaHasta) throws SQLException {

        String sql = """
            SELECT p.* FROM peregrino p
            INNER JOIN estancia e ON e.id_peregrino = p.id_peregrino
            WHERE e.id_albergue = ?
              AND e.estado_estancia <> 'CANCELADA'
              AND e.fecha_entrada >= ?
              AND e.fecha_entrada <= ?
            ORDER BY e.fecha_entrada
            """;

        List<Peregrino> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAlbergue);
            ps.setString(2, fechaDesde);
            ps.setString(3, fechaHasta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public static Map<String, Integer> contarPorSexo(Connection conn,
            int idAlbergue, int anio) throws SQLException {

        String sql = """
            SELECT p.sexo, COUNT(*) as total
            FROM peregrino p
            INNER JOIN estancia e ON e.id_peregrino = p.id_peregrino
            WHERE e.id_albergue = ?
              AND e.estado_estancia <> 'CANCELADA'
              AND strftime('%Y', e.fecha_entrada) = ?
            GROUP BY p.sexo
            ORDER BY total DESC
            """;

        Map<String, Integer> resultado = new LinkedHashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAlbergue);
            ps.setString(2, String.valueOf(anio));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String sexo = rs.getString("sexo");
                    if (sexo == null || sexo.isBlank()) sexo = "?";
                    resultado.put(sexo, rs.getInt("total"));
                }
            }
        }
        return resultado;
    }

    public static Map<String, Integer> contarPorPais(Connection conn,
            int idAlbergue, int anio) throws SQLException {

        String sql = """
            SELECT p.nacionalidad, COUNT(*) as total
            FROM peregrino p
            INNER JOIN estancia e ON e.id_peregrino = p.id_peregrino
            WHERE e.id_albergue = ?
              AND e.estado_estancia <> 'CANCELADA'
              AND strftime('%Y', e.fecha_entrada) = ?
            GROUP BY p.nacionalidad
            ORDER BY total DESC
            """;

        Map<String, Integer> resultado = new LinkedHashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAlbergue);
            ps.setString(2, String.valueOf(anio));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String pais = rs.getString("nacionalidad");
                    if (pais == null || pais.isBlank()) pais = "?";
                    resultado.put(pais, rs.getInt("total"));
                }
            }
        }
        return resultado;
    }

    public static Map<String, Integer> contarPorFranjaEdad(Connection conn,
            int idAlbergue, int anio) throws SQLException {

        String sql = """
            SELECT p.fecha_nacimiento
            FROM peregrino p
            INNER JOIN estancia e ON e.id_peregrino = p.id_peregrino
            WHERE e.id_albergue = ?
              AND e.estado_estancia <> 'CANCELADA'
              AND strftime('%Y', e.fecha_entrada) = ?
              AND p.fecha_nacimiento IS NOT NULL
            """;

        Map<String, Integer> franjas = new LinkedHashMap<>();
        franjas.put("<18", 0);
        franjas.put("18-30", 0);
        franjas.put("31-45", 0);
        franjas.put("46-60", 0);
        franjas.put("61-75", 0);
        franjas.put(">75", 0);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAlbergue);
            ps.setString(2, String.valueOf(anio));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String fnStr = rs.getString("fecha_nacimiento");
                    try {
                        java.time.LocalDate fn = java.time.LocalDate.parse(fnStr);
                        int edad = java.time.Period.between(fn,
                            java.time.LocalDate.of(anio, 12, 31)).getYears();

                        String franja;
                        if (edad < 18) franja = "<18";
                        else if (edad <= 30) franja = "18-30";
                        else if (edad <= 45) franja = "31-45";
                        else if (edad <= 60) franja = "46-60";
                        else if (edad <= 75) franja = "61-75";
                        else franja = ">75";

                        franjas.merge(franja, 1, Integer::sum);
                    } catch (Exception ignored) {}
                }
            }
        }
        return franjas;
    }
    
    
    
    
    
    
    
    
    
}


















