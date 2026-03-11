package dao;

import model.Habitacion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HabitacionDAO {


	public static void insertar(Connection conn, Habitacion h) throws SQLException {

		String sql = """
				INSERT INTO habitacion (
				    numero_habitacion,
				    id_albergue,
				    numero_camas,
				    estado_habitacion
				) VALUES (?, ?, ?, ?)
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, h.getNumeroHabitacion());
			ps.setInt(2, h.getIdAlbergue());
			ps.setInt(3, h.getNumeroCamas());
			ps.setString(4, h.getEstadoHabitacion());
			ps.executeUpdate();
		}
	}


	public static void actualizar(Connection conn, Habitacion h) throws SQLException {

		String sql = """
				UPDATE habitacion
				SET id_albergue = ?,
				    numero_camas = ?,
				    estado_habitacion = ?
				WHERE numero_habitacion = ?
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, h.getIdAlbergue());
			ps.setInt(2, h.getNumeroCamas());
			ps.setString(3, h.getEstadoHabitacion());
			ps.setInt(4, h.getNumeroHabitacion());
			ps.executeUpdate();
		}
	}


	public static void actualizarNumeroCamas(Connection conn, int numeroHabitacion, int nuevoNumeroCamas)
			throws SQLException {

		String sql = """
				UPDATE habitacion
				SET numero_camas = ?
				WHERE numero_habitacion = ?
				""";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, nuevoNumeroCamas);
			ps.setInt(2, numeroHabitacion);
			ps.executeUpdate();
		}
	}


	public static void eliminar(Connection conn, int numeroHabitacion) throws SQLException {

		String sql = "DELETE FROM habitacion WHERE numero_habitacion = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, numeroHabitacion);
			ps.executeUpdate();
		}
	}


	public static Habitacion obtenerPorNumero(Connection conn, int numeroHabitacion) throws SQLException {

		String sql = "SELECT * FROM habitacion WHERE numero_habitacion = ?";

		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, numeroHabitacion);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return mapear(rs);
				}
			}
		}

		return null;
	}


	public static List<Habitacion> listarTodas(Connection conn) throws SQLException {

		String sql = "SELECT * FROM habitacion ORDER BY numero_habitacion ASC";

		List<Habitacion> lista = new ArrayList<>();

		try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				lista.add(mapear(rs));
			}
		}

		return lista;
	}


	private static Habitacion mapear(ResultSet rs) throws SQLException {

		Habitacion h = new Habitacion();

		h.setNumeroHabitacion(rs.getInt("numero_habitacion"));
		h.setIdAlbergue(rs.getInt("id_albergue"));
		h.setNumeroCamas(rs.getInt("numero_camas"));
		h.setEstadoHabitacion(rs.getString("estado_habitacion"));

		return h;
	}
	
	public static List<Habitacion> listarActivas(Connection conn) throws SQLException {

	    String sql = """
	            SELECT *
	            FROM habitacion
	            WHERE estado_habitacion = 'ACTIVA'
	            ORDER BY numero_habitacion ASC
	            """;

	    List<Habitacion> lista = new ArrayList<>();

	    try (PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            lista.add(mapear(rs));
	        }
	    }

	    return lista;
	}

	public static void actualizarEstado(Connection conn, int numeroHabitacion, String estado) throws SQLException {

	    String sql = """
	            UPDATE habitacion
	            SET estado_habitacion = ?
	            WHERE numero_habitacion = ?
	            """;

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, estado);
	        ps.setInt(2, numeroHabitacion);
	        ps.executeUpdate();
	    }
	}

	public static List<Habitacion> listarDesdeNumero(Connection conn, int numeroHabitacionDesde) throws SQLException {

	    String sql = """
	            SELECT *
	            FROM habitacion
	            WHERE numero_habitacion >= ?
	            ORDER BY numero_habitacion ASC
	            """;

	    List<Habitacion> lista = new ArrayList<>();

	    try (PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, numeroHabitacionDesde);

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                lista.add(mapear(rs));
	            }
	        }
	    }

	    return lista;
	}
}
