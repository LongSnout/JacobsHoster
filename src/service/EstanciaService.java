package service;

import dao.EstanciaDAO;
import dao.PeregrinoDAO;
import db.DBManager;
import exception.DatabaseException;
import model.Estancia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import java.util.List;
import model.Peregrino;

public class EstanciaService {

    /**
     * Crea una estancia hoy para un peregrino.
     */
    public static Estancia crearEstanciaHoy(int idPeregrino, Integer idCama) {

        Estancia e = new Estancia();

        e.setIdEnvioXml(null);
        e.setIdCama(idCama);

        e.setIdAlbergue(config.AppConfig.ID_ALBERGUE);
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
     * Inserta o actualiza una estancia, si la estancia es 0 en la DB es que no existe y se inserta, si es diferente de 0 se actualiza.
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


    
    public static List<Peregrino> listarPeregrinosPresentesPorDia(int idAlbergue, LocalDate fecha) {
        try (Connection conn = DBManager.getConnection()) {
            return PeregrinoDAO.listarPresentesPorDia(conn, idAlbergue, fecha.toString());
        } catch (SQLException e) {
            throw new DatabaseException("Error al listar peregrinos presentes en fecha " + fecha, e);
        }
    }
    
    public static Estancia buscarPorPeregrinoYFecha(int idPeregrino, LocalDate fecha) {
        try (Connection conn = DBManager.getConnection()) {
            return EstanciaDAO.buscarPorPeregrinoYFecha(conn, idPeregrino, fecha.toString());
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Error al buscar estancia para id_peregrino=" + idPeregrino + " en fecha " + fecha,
                    e
            );
        }
    }
    
    public static int contarPlazasOcupadasEnFecha(int idAlbergue, LocalDate fecha) {
        try (Connection conn = DBManager.getConnection()) {
            return EstanciaDAO.contarOcupadasEnFecha(conn, idAlbergue, fecha.toString());
        } catch (SQLException e) {
            throw new DatabaseException("Error al contar plazas ocupadas en fecha " + fecha, e);
        }
    }
    
    
    public static List<Estancia> listarPorRangoFechas(int idAlbergue,
            LocalDate fechaDesde, LocalDate fechaHasta) {
        try (Connection conn = DBManager.getConnection()) {
            return EstanciaDAO.listarPorRangoFechas(conn, idAlbergue,
                fechaDesde.toString(), fechaHasta.toString());
        } catch (SQLException e) {
            throw new DatabaseException("Error listando estancias por rango", e);
        }
    }
    
    
    
    
    
    
    
    /*
     * FUTURO (nube / preregistro / reservas):
     *
     * Cuando se implemente la sincronización con la nube y los peregrinos puedan
     * enviar sus datos por adelantado, habrá que distinguir entre:
     *
     * - PREREGISTRO / PENDIENTE_CONFIRMAR
     * - RESERVADA
     * - ACTIVA
     * - FINALIZADA
     * - CANCELADA
     *
     * Regla prevista:
     *
     * 1) Si el albergue NO acepta reservas:
     *    - los preregistros enviados desde la nube NO descuentan plaza
     *    - solo sirven como ficha previa / pendiente de confirmar
     *
     * 2) Si el albergue SÍ acepta reservas:
     *    - las reservas confirmadas sí descuentan plaza aunque el huésped
     *      aún no haya hecho check-in presencial
     *
     * 3) En la interfaz:
     *    - los preregistros/pedientes deberían mostrarse en gris
     *    - las estancias activas en color normal
     *
     * IMPORTANTE:
     * El cálculo de aforo no debe basarse solo en la lista visual, sino en el
     * estado real de negocio de cada estancia/reserva.
     */
    
}




