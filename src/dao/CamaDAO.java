package dao;

import model.Cama;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CamaDAO {


	public static void insertar(Connection conn, Cama c) throws SQLException {

	    String sql = """
	            INSERT INTO cama (
	                numero_habitacion,
	                numero_cama,
	                estado,
	                activa
	            ) VALUES (?, ?, ?, ?)
	            """;

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, c.getNumeroHabitacion());
	        ps.setInt(2, c.getNumeroCama());
	        ps.setString(3, c.getEstado());
	        ps.setInt(4, c.isActiva() ? 1 : 0);
	        ps.executeUpdate();
	    }
	}


	public static void actualizarEstado(Connection conn, int idCama, String nuevoEstado) throws SQLException {

		String sql = """
				UPDATE cama
				SET estado = ?
				WHERE id_cama = ?
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, nuevoEstado);
			ps.setInt(2, idCama);
			ps.executeUpdate();
		}
	}


	public static void eliminar(Connection conn, int idCama) throws SQLException {

		String sql = "DELETE FROM cama WHERE id_cama = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, idCama);
			ps.executeUpdate();
		}
	}


	public static Cama obtenerPorId(Connection conn, int idCama) throws SQLException {

		String sql = "SELECT * FROM cama WHERE id_cama = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, idCama);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapear(rs);
				}
			}
		}

		return null;
	}


	public static List<Cama> listarPorHabitacion(Connection conn, int numeroHabitacion) throws SQLException {

	    String sql = """
	            SELECT *
	            FROM cama
	            WHERE numero_habitacion = ?
	              AND activa = 1
	            ORDER BY numero_cama ASC
	            """;

	    List<Cama> lista = new ArrayList<>();

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, numeroHabitacion);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                lista.add(mapear(rs));
	            }
	        }
	    }

	    return lista;
	}



	public static List<Cama> listarTodas(Connection conn) throws SQLException {

	    String sql = """
	            SELECT *
	            FROM cama
	            WHERE activa = 1
	            ORDER BY numero_habitacion ASC, numero_cama ASC
	            """;

	    List<Cama> lista = new ArrayList<>();

	    try (PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            lista.add(mapear(rs));
	        }
	    }

	    return lista;
	}


	private static Cama mapear(ResultSet rs) throws SQLException {

	    Cama c = new Cama();

	    c.setIdCama(rs.getInt("id_cama"));
	    c.setNumeroHabitacion(rs.getInt("numero_habitacion"));
	    c.setNumeroCama(rs.getInt("numero_cama"));
	    c.setEstado(rs.getString("estado"));
	    c.setActiva(rs.getInt("activa") == 1);

	    return c;
	}


	public static void desactivarCamasLibres(Connection conn, int numeroHabitacion, int limite) throws SQLException {

	    String sql = """
	            UPDATE cama
	            SET activa = 0
	            WHERE id_cama IN (
	                SELECT id_cama
	                FROM cama
	                WHERE numero_habitacion = ?
	                  AND activa = 1
	                  AND estado = 'LIBRE'
	                ORDER BY numero_cama DESC
	                LIMIT ?
	            )
	            """;

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, numeroHabitacion);
	        ps.setInt(2, limite);
	        ps.executeUpdate();
	    }
	}

	
	public static int contarTodas(Connection conn) throws SQLException {
	    String sql = "SELECT COUNT(*) FROM cama";

	    try (PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        return rs.next() ? rs.getInt(1) : 0;
	    }
	}
	
	public static int contarDisponiblesParaCapacidad(Connection conn) throws SQLException {
	    String sql = """
	        SELECT COUNT(*)
	        FROM cama
	        WHERE activa = 1
	          AND estado <> 'BLOQUEADA'
	        """;

	    try (PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        return rs.next() ? rs.getInt(1) : 0;
	    }
	}
	
	public static Cama obtenerPorHabitacionYNumeroCama(Connection conn, int numeroHabitacion, int numeroCama) throws SQLException {

	    String sql = """
	        SELECT *
	        FROM cama
	        WHERE numero_habitacion = ?
	          AND numero_cama = ?
	          AND activa = 1
	        """;

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, numeroHabitacion);
	        ps.setInt(2, numeroCama);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return mapear(rs);
	            }
	        }
	    }

	    return null;
	}
	
	public static int obtenerNumeroCamaDentroDeHabitacion(Connection conn, int idCama) throws SQLException {

	    String sql = """
	        SELECT numero_cama
	        FROM cama
	        WHERE id_cama = ?
	        """;

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, idCama);

	        try (ResultSet rs = ps.executeQuery()) {
	            return rs.next() ? rs.getInt("numero_cama") : 0;
	        }
	    }
	}
	
	public static void activarCama(Connection conn, int numeroHabitacion, int numeroCama) throws SQLException {

	    String sql = """
	            UPDATE cama
	            SET activa = 1
	            WHERE numero_habitacion = ?
	              AND numero_cama = ?
	            """;

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, numeroHabitacion);
	        ps.setInt(2, numeroCama);
	        ps.executeUpdate();
	    }
	}

	public static void activarCamasHasta(Connection conn, int numeroHabitacion, int numeroCamaMax) throws SQLException {

	    String sql = """
	            UPDATE cama
	            SET activa = 1
	            WHERE numero_habitacion = ?
	              AND numero_cama <= ?
	            """;

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, numeroHabitacion);
	        ps.setInt(2, numeroCamaMax);
	        ps.executeUpdate();
	    }
	}

	public static void desactivarCamasDesde(Connection conn, int numeroHabitacion, int desdeNumeroCama) throws SQLException {

	    String sql = """
	            UPDATE cama
	            SET activa = 0
	            WHERE numero_habitacion = ?
	              AND numero_cama >= ?
	              AND activa = 1
	              AND estado = 'LIBRE'
	            """;

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, numeroHabitacion);
	        ps.setInt(2, desdeNumeroCama);
	        ps.executeUpdate();
	    }
	}

	public static void desactivarTodasLasCamasDeHabitacion(Connection conn, int numeroHabitacion) throws SQLException {

	    String sql = """
	            UPDATE cama
	            SET activa = 0
	            WHERE numero_habitacion = ?
	              AND activa = 1
	              AND estado = 'LIBRE'
	            """;

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, numeroHabitacion);
	        ps.executeUpdate();
	    }
	}

	public static boolean existeCama(Connection conn, int numeroHabitacion, int numeroCama) throws SQLException {

	    String sql = """
	            SELECT 1
	            FROM cama
	            WHERE numero_habitacion = ?
	              AND numero_cama = ?
	            LIMIT 1
	            """;

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, numeroHabitacion);
	        ps.setInt(2, numeroCama);

	        try (ResultSet rs = ps.executeQuery()) {
	            return rs.next();
	        }
	    }
	}

	public static boolean hayCamasActivasOcupadasDesde(Connection conn, int numeroHabitacion, int desdeNumeroCama) throws SQLException {

	    String sql = """
	            SELECT 1
	            FROM cama
	            WHERE numero_habitacion = ?
	              AND numero_cama >= ?
	              AND activa = 1
	              AND estado = 'OCUPADA'
	            LIMIT 1
	            """;

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, numeroHabitacion);
	        ps.setInt(2, desdeNumeroCama);

	        try (ResultSet rs = ps.executeQuery()) {
	            return rs.next();
	        }
	    }
	}

	public static boolean hayCamasActivasOcupadasEnHabitacion(Connection conn, int numeroHabitacion) throws SQLException {

	    String sql = """
	            SELECT 1
	            FROM cama
	            WHERE numero_habitacion = ?
	              AND activa = 1
	              AND estado = 'OCUPADA'
	            LIMIT 1
	            """;

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, numeroHabitacion);

	        try (ResultSet rs = ps.executeQuery()) {
	            return rs.next();
	        }
	    }
	}
	
}




