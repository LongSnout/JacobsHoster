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
				    estado
				) VALUES (?, ?)
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, c.getNumeroHabitacion());
			ps.setString(2, c.getEstado());
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
				SELECT * FROM cama
				WHERE numero_habitacion = ?
				ORDER BY id_cama ASC
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

		String sql = "SELECT * FROM cama ORDER BY numero_habitacion ASC, id_cama ASC";

		List<Cama> lista = new ArrayList<>();

		try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

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
		c.setEstado(rs.getString("estado"));

		return c;
	}


	public static void eliminarCamasLibres(Connection conn, int numeroHabitacion, int limite) throws SQLException {

		String sql = """
				DELETE FROM cama
				WHERE id_cama IN (
				    SELECT id_cama
				    FROM cama
				    WHERE numero_habitacion = ?
				      AND estado = 'LIBRE'
				    ORDER BY id_cama DESC
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
	        WHERE estado <> 'BLOQUEADA'
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
	        ORDER BY id_cama ASC
	        LIMIT 1 OFFSET ?
	        """;

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, numeroHabitacion);
	        ps.setInt(2, numeroCama - 1);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                return mapear(rs);
	            }
	        }
	    }

	    return null;
	}
	
	public static int obtenerNumeroCamaDentroDeHabitacion(Connection conn, int idCama) throws SQLException {

	    Cama camaActual = obtenerPorId(conn, idCama);
	    if (camaActual == null) return 0;

	    String sql = """
	        SELECT COUNT(*)
	        FROM cama
	        WHERE numero_habitacion = ?
	          AND id_cama <= ?
	        """;

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, camaActual.getNumeroHabitacion());
	        ps.setInt(2, idCama);

	        try (ResultSet rs = ps.executeQuery()) {
	            return rs.next() ? rs.getInt(1) : 0;
	        }
	    }
	}
	
	
	
}




