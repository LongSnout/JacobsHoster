package service;

import dao.EstanciaDAO;
import db.DBManager;
import exception.DatabaseException;
import model.Estancia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class EstanciaService {

    /**
     * Crea una estancia hoy para un peregrino.
     */
    public static Estancia crearEstanciaHoy(int idPeregrino, Integer idCama) {

        Estancia e = new Estancia();

        e.setIdEnvioXml(null);
        e.setIdCama(idCama);

        e.setIdAlbergue(1);
        e.setIdPeregrino(idPeregrino);

        e.setFechaContrato(null);

        // YYYY-MM-DD
        e.setFechaEntrada(LocalDate.now().toString());

        e.setFechaSalidaPrevista(null);
        e.setFechaSalidaReal(null);

        e.setNumeroHabitaciones(1);
        e.setIdGrupo(null);

        e.setInternetIncluido(true);

        e.setReferenciaContrato(null);
        e.setEstadoEstancia("ACTIVA");

        try (Connection conn = DBManager.getConnection()) {
            EstanciaDAO.insertar(conn, e);
            return e;
        } catch (SQLException ex) {
            throw new DatabaseException("Error al crear estancia hoy para id_peregrino=" + idPeregrino, ex);
        }
    }
    
    public static void asignarCama(int idEstancia, int idCama) {

        String sql = "UPDATE estancia SET id_cama = ? WHERE id_estancia = ?";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCama);
            ps.setInt(2, idEstancia);

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new DatabaseException("No existe la estancia con id_estancia=" + idEstancia);
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                    "No se pudo asignar la cama " + idCama + " a la estancia " + idEstancia
                            + ". Motivo: " + e.getMessage(),
                    e
            );
        }
    }

    public static void cerrarEstancia(int idEstancia, String fechaSalidaReal) {

        if (fechaSalidaReal == null || fechaSalidaReal.isBlank()) {
            fechaSalidaReal = LocalDate.now().toString(); // YYYY-MM-DD
        }

        String sql = """
            UPDATE estancia
            SET fecha_salida_real = ?,
                estado_estancia = 'FINALIZADA'
            WHERE id_estancia = ?
            """;

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, fechaSalidaReal);
            ps.setInt(2, idEstancia);

            int filas = ps.executeUpdate();

            if (filas == 0) {
                throw new DatabaseException("No existe la estancia con id_estancia=" + idEstancia);
            }

        } catch (SQLException e) {
            throw new DatabaseException(
                    "No se pudo cerrar la estancia " + idEstancia + ". Motivo: " + e.getMessage(),
                    e
            );
        }
    }
    
    public static Estancia buscarActivaPorPeregrino(int idPeregrino) {

        try (Connection conn = DBManager.getConnection()) {
            return EstanciaDAO.buscarActivaPorPeregrino(conn, idPeregrino);
        } catch (SQLException e) {
            throw new DatabaseException("Error al buscar estancia activa para id_peregrino=" + idPeregrino, e);
        }
    }

    /**
     * Inserta o actualiza una estancia.
     * Si id_estancia == 0 -> insertar.
     * Si id_estancia != 0 -> actualizar.
     */
    public static void guardar(Estancia e) {

        if (e == null) return;

        try (Connection conn = DBManager.getConnection()) {

            if (e.getIdEstancia() == 0) {
                EstanciaDAO.insertar(conn, e);
            } else {
                EstanciaDAO.actualizar(conn, e);
            }

        } catch (SQLException ex) {
            throw new DatabaseException("Error al guardar estancia", ex);
        }
    }
    
    
    
    
}




