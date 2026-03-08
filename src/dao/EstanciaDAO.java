package dao;

import model.Estancia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class EstanciaDAO {


	public static void insertar(Connection conn, Estancia e) throws SQLException {

	    String sql = """
	        INSERT INTO estancia (
	            id_envio_xml,
	            id_cama,
	            id_albergue,
	            id_peregrino,
	            fecha_contrato,
	            fecha_entrada,
	            fecha_salida_prevista,
	            fecha_salida_real,
	            numero_habitaciones,
	            id_grupo,
	            internet_incluido,
	            referencia_contrato,
	            estado_estancia
	        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
	        """;

	    try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

	        // id_envio_xml (nullable)
	        if (e.getIdEnvioXml() == null) ps.setNull(1, java.sql.Types.INTEGER);
	        else ps.setInt(1, e.getIdEnvioXml());

	        // id_cama (nullable)
	        if (e.getIdCama() == null) ps.setNull(2, java.sql.Types.INTEGER);
	        else ps.setInt(2, e.getIdCama());

	        ps.setInt(3, e.getIdAlbergue());
	        ps.setInt(4, e.getIdPeregrino());

	        ps.setString(5, emptyToNull(e.getFechaContrato()));
	        ps.setString(6, emptyToNull(e.getFechaEntrada()));
	        ps.setString(7, emptyToNull(e.getFechaSalidaPrevista()));
	        ps.setString(8, emptyToNull(e.getFechaSalidaReal()));

	        ps.setInt(9, e.getNumeroHabitaciones());
	        ps.setString(10, e.getIdGrupo());

	        ps.setInt(11, e.isInternetIncluido() ? 1 : 0);

	        ps.setString(12, e.getReferenciaContrato());
	        ps.setString(13, e.getEstadoEstancia());

	        ps.executeUpdate();

	        // id autogenerado (id_estancia)
	        try (ResultSet rs = ps.getGeneratedKeys()) {
	            if (rs.next()) {
	                e.setIdEstancia(rs.getInt(1));
	            }
	        }
	    }
	}



	public static void actualizar(Connection conn, Estancia e) throws SQLException {

	    String sql = """
	        UPDATE estancia
	        SET id_envio_xml = ?,
	            id_cama = ?,
	            id_albergue = ?,
	            id_peregrino = ?,
	            fecha_contrato = ?,
	            fecha_entrada = ?,
	            fecha_salida_prevista = ?,
	            fecha_salida_real = ?,
	            numero_habitaciones = ?,
	            id_grupo = ?,
	            internet_incluido = ?,
	            referencia_contrato = ?,
	            estado_estancia = ?
	        WHERE id_estancia = ?
	        """;

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {

	        if (e.getIdEnvioXml() == null) ps.setNull(1, java.sql.Types.INTEGER);
	        else ps.setInt(1, e.getIdEnvioXml());

	        if (e.getIdCama() == null) ps.setNull(2, java.sql.Types.INTEGER);
	        else ps.setInt(2, e.getIdCama());

	        ps.setInt(3, e.getIdAlbergue());
	        ps.setInt(4, e.getIdPeregrino());

	        ps.setString(5, emptyToNull(e.getFechaContrato()));
	        ps.setString(6, emptyToNull(e.getFechaEntrada()));
	        ps.setString(7, emptyToNull(e.getFechaSalidaPrevista()));
	        ps.setString(8, emptyToNull(e.getFechaSalidaReal()));

	        ps.setInt(9, e.getNumeroHabitaciones());
	        ps.setString(10, emptyToNull(e.getIdGrupo()));

	        ps.setBoolean(11, e.isInternetIncluido());

	        ps.setString(12, emptyToNull(e.getReferenciaContrato()));
	        ps.setString(13, emptyToNull(e.getEstadoEstancia()));

	        ps.setInt(14, e.getIdEstancia());

	        ps.executeUpdate();
	    }
	}

	private static String emptyToNull(String s) {
	    if (s == null) return null;
	    String t = s.trim();
	    return t.isBlank() ? null : t;
	}


    public static Estancia obtenerPorId(Connection conn, int idEstancia) throws SQLException {

        String sql = "SELECT * FROM estancia WHERE id_estancia = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEstancia);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }

        return null;
    }


    public static List<Estancia> listarActivas(Connection conn) throws SQLException {

        String sql = """
            SELECT * FROM estancia
            WHERE estado_estancia = 'ACTIVA'
            ORDER BY fecha_entrada DESC
            """;

        List<Estancia> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) lista.add(mapear(rs));
        }

        return lista;
    }


    public static List<Estancia> listarPorCama(Connection conn, int idCama) throws SQLException {

        String sql = """
            SELECT * FROM estancia
            WHERE id_cama = ?
            ORDER BY fecha_entrada DESC
            """;

        List<Estancia> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCama);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }

        return lista;
    }
    
    


    public static List<Estancia> listarPorPeregrino(Connection conn, int idPeregrino) throws SQLException {

        String sql = """
            SELECT * FROM estancia
            WHERE id_peregrino = ?
            ORDER BY fecha_entrada DESC
            """;

        List<Estancia> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPeregrino);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }

        return lista;
    }


    private static Estancia mapear(ResultSet rs) throws SQLException {

        Estancia e = new Estancia();

        e.setIdEstancia(rs.getInt("id_estancia"));

        // id_envio_xml (nullable)
        int idEnvio = rs.getInt("id_envio_xml");
        if (rs.wasNull()) e.setIdEnvioXml(null);
        else e.setIdEnvioXml(idEnvio);

        // id_cama (nullable)
        int idCama = rs.getInt("id_cama");
        if (rs.wasNull()) e.setIdCama(null);
        else e.setIdCama(idCama);

        e.setIdAlbergue(rs.getInt("id_albergue"));
        e.setIdPeregrino(rs.getInt("id_peregrino"));

        e.setFechaContrato(rs.getString("fecha_contrato"));
        e.setFechaEntrada(rs.getString("fecha_entrada"));
        e.setFechaSalidaPrevista(rs.getString("fecha_salida_prevista"));
        e.setFechaSalidaReal(rs.getString("fecha_salida_real"));

        e.setNumeroHabitaciones(rs.getInt("numero_habitaciones"));
        e.setIdGrupo(rs.getString("id_grupo"));

        e.setInternetIncluido(rs.getInt("internet_incluido") == 1);

        e.setReferenciaContrato(rs.getString("referencia_contrato"));
        e.setEstadoEstancia(rs.getString("estado_estancia"));

        return e;
    }
    
    public static List<Estancia> listarPorDia(Connection conn, int idAlbergue, String fechaEntradaISO) throws SQLException {

        String sql = """
            SELECT *
            FROM estancia
            WHERE id_albergue = ?
              AND fecha_entrada = ?
              AND estado_estancia <> 'CANCELADA'
            ORDER BY id_estancia DESC
            """;

        List<Estancia> lista = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAlbergue);
            ps.setString(2, fechaEntradaISO);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }

        return lista;
    }
    

    public static void vincularEnvioXml(
            Connection conn,
            int idEstancia,
            int idEnvioXml,
            String nuevoEstado
    ) throws SQLException {

        String sql = """
            UPDATE estancia
            SET id_envio_xml = ?,
                estado_estancia = ?
            WHERE id_estancia = ?
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEnvioXml);
            ps.setString(2, nuevoEstado);
            ps.setInt(3, idEstancia);
            ps.executeUpdate();
        }
    }

    public static Estancia buscarActivaPorPeregrino(Connection conn, int idPeregrino) throws SQLException {

        String sql = """
            SELECT *
            FROM estancia
            WHERE id_peregrino = ?
              AND estado_estancia = 'ACTIVA'
            ORDER BY id_estancia DESC
            LIMIT 1
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPeregrino);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs); // ahora te digo mapear()
                }
                return null;
            }
        }
    }
    
    public static Estancia buscarPorPeregrinoYFecha(Connection conn, int idPeregrino, String fechaISO) throws SQLException {

        String sql = """
            SELECT *
            FROM estancia
            WHERE id_peregrino = ?
              AND estado_estancia <> 'CANCELADA'
              AND fecha_entrada <= ?
              AND (
                    (fecha_salida_real IS NOT NULL AND fecha_salida_real >= ?)
                    OR
                    (fecha_salida_real IS NULL AND fecha_salida_prevista IS NOT NULL AND fecha_salida_prevista >= ?)
                    OR
                    (fecha_salida_real IS NULL AND fecha_salida_prevista IS NULL)
                  )
            ORDER BY fecha_entrada DESC, id_estancia DESC
            LIMIT 1
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idPeregrino);
            ps.setString(2, fechaISO);
            ps.setString(3, fechaISO);
            ps.setString(4, fechaISO);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }

        return null;
    }
    
    
}






