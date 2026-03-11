package service;

import db.DBManager;
import exception.DatabaseException;
import model.Cama;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class CamaService {

    /**
     * Recalcula el estado de TODAS las camas según las estancias.
     * BLOQUEADA se respeta y no se modifica.
     * OCUPADA si existe estancia ACTIVA que cubre hoy.
     * LIBRE si no.
     */
    public static void refrescarEstados() {

        String hoy = LocalDate.now().toString(); // YYYY-MM-DD

        //Marcar OCUPADA si hay estancia ACTIVA que cubre hoy
        String sqlOcupadas = """
            UPDATE cama
            SET estado = 'OCUPADA'
            WHERE estado <> 'BLOQUEADA'
              AND id_cama IN (
                  SELECT e.id_cama
                  FROM estancia e
                  WHERE e.id_cama IS NOT NULL
                    AND e.estado_estancia = 'ACTIVA'
                    AND e.fecha_entrada <= ?
                    AND (
                        COALESCE(e.fecha_salida_real, e.fecha_salida_prevista) IS NULL
                        OR COALESCE(e.fecha_salida_real, e.fecha_salida_prevista) > ?
                    )
              )
            """;

        //Marcar LIBRE el resto (si no están BLOQUEADAS y no tienen estancia activa hoy)
        String sqlLibres = """
            UPDATE cama
            SET estado = 'LIBRE'
            WHERE estado <> 'BLOQUEADA'
              AND id_cama NOT IN (
                  SELECT e.id_cama
                  FROM estancia e
                  WHERE e.id_cama IS NOT NULL
                    AND e.estado_estancia = 'ACTIVA'
                    AND e.fecha_entrada <= ?
                    AND (
                        COALESCE(e.fecha_salida_real, e.fecha_salida_prevista) IS NULL
                        OR COALESCE(e.fecha_salida_real, e.fecha_salida_prevista) > ?
                    )
              )
            """;

        try (Connection conn = DBManager.getConnection()) {

            // OCUPADAS
            try (PreparedStatement ps = conn.prepareStatement(sqlOcupadas)) {
                ps.setString(1, hoy);
                ps.setString(2, hoy);
                ps.executeUpdate();
            }

            // LIBRES
            try (PreparedStatement ps = conn.prepareStatement(sqlLibres)) {
                ps.setString(1, hoy);
                ps.setString(2, hoy);
                ps.executeUpdate();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error refrescando estados de camas para hoy=" + hoy, e);
        }
    }
    
    public static int contarCapacidadTotal() {
        String sql = """
            SELECT COUNT(*)
            FROM cama
            WHERE estado <> 'BLOQUEADA'
            """;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getInt(1) : 0;

        } catch (SQLException e) {
            throw new DatabaseException("Error contando capacidad total de camas", e);
        }
    }
    
    public static int obtenerNumeroCamaDentroDeHabitacion(int idCama) {
        try (Connection conn = DBManager.getConnection()) {

            Cama camaActual = dao.CamaDAO.obtenerPorId(conn, idCama);
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

        } catch (SQLException e) {
            throw new DatabaseException("Error obteniendo número de cama dentro de la habitación", e);
        }
    }
    
    public static model.Cama obtenerPorId(int idCama) {
        try (Connection conn = DBManager.getConnection()) {
            return dao.CamaDAO.obtenerPorId(conn, idCama);
        } catch (SQLException e) {
            throw new DatabaseException("Error obteniendo cama por id=" + idCama, e);
        }
    }
    
    public static Cama obtenerPorHabitacionYNumeroCama(int numeroHabitacion, int numeroCama) {
        try (Connection conn = DBManager.getConnection()) {
            return dao.CamaDAO.obtenerPorHabitacionYNumeroCama(conn, numeroHabitacion, numeroCama);
        } catch (SQLException e) {
            throw new DatabaseException("Error buscando cama por habitación y número de cama", e);
        }
    }
    
    
}





