package service;

import dao.CamaDAO;
import dao.HabitacionDAO;
import db.DBManager;
import exception.DatabaseException;
import model.Cama;
import model.Habitacion;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class HabitacionService {

    /**
     * Crea una habitación.
     */
	public static void crearHabitacionConCamas(
	        Connection conn,
	        int numeroHabitacion,
	        int idAlbergue,
	        int numeroCamas
	) throws SQLException {

	    Habitacion h = new Habitacion();
	    h.setNumeroHabitacion(numeroHabitacion);
	    h.setIdAlbergue(idAlbergue);
	    h.setNumeroCamas(numeroCamas);
	    h.setEstadoHabitacion("ACTIVA");

	    HabitacionDAO.insertar(conn, h);

	    for (int i = 1; i <= numeroCamas; i++) {
	        Cama cama = new Cama();
	        cama.setNumeroHabitacion(numeroHabitacion);
	        cama.setNumeroCama(i);
	        cama.setEstado("LIBRE");
	        cama.setActiva(true);
	        CamaDAO.insertar(conn, cama);
	    }
	}

    /**
     * Cambia el número de camas de una habitación.
     * Añade o elimina camas según diferencia. solo borra camas "LIBRE".
     */
	public static void actualizarNumeroCamas(
	        Connection conn,
	        int numeroHabitacion,
	        int nuevoNumeroCamas
	) throws SQLException {

	    List<Cama> camas = CamaDAO.listarPorHabitacion(conn, numeroHabitacion);
	    int camasActuales = camas.size();

	    if (nuevoNumeroCamas > camasActuales) {
	        for (int i = camasActuales + 1; i <= nuevoNumeroCamas; i++) {
	            Cama cama = new Cama();
	            cama.setNumeroHabitacion(numeroHabitacion);
	            cama.setNumeroCama(i);
	            cama.setEstado("LIBRE");
	            cama.setActiva(true);
	            CamaDAO.insertar(conn, cama);
	        }

	    } else if (nuevoNumeroCamas < camasActuales) {
	        int aDesactivar = camasActuales - nuevoNumeroCamas;
	        CamaDAO.desactivarCamasLibres(conn, numeroHabitacion, aDesactivar);
	    }

	    HabitacionDAO.actualizarNumeroCamas(conn, numeroHabitacion, nuevoNumeroCamas);
	}

    /**
     * Lista habitaciones.
     */
    public static List<Habitacion> listarHabitaciones(Connection conn) throws SQLException {
        return HabitacionDAO.listarTodas(conn);
    }
    
    public static void sincronizarConfiguracion(
            Connection conn,
            int idAlbergue,
            java.util.List<Integer> camasPorHabitacion
    ) throws SQLException {

        // 1. Activar / crear habitaciones definidas
        for (int i = 0; i < camasPorHabitacion.size(); i++) {
            int numeroHabitacion = i + 1;
            int numeroCamasDeseadas = camasPorHabitacion.get(i);

            if (numeroCamasDeseadas < 0) {
                throw new SQLException("La habitación " + numeroHabitacion + " no puede tener un número negativo de camas");
            }

            Habitacion habitacion = HabitacionDAO.obtenerPorNumero(conn, numeroHabitacion);

            if (habitacion == null) {
                crearHabitacionConCamas(conn, numeroHabitacion, idAlbergue, numeroCamasDeseadas);
            } else {
                HabitacionDAO.actualizarEstado(conn, numeroHabitacion, "ACTIVA");
                HabitacionDAO.actualizarNumeroCamas(conn, numeroHabitacion, numeroCamasDeseadas);

                // Reactivar o crear camas 1..N
                for (int numCama = 1; numCama <= numeroCamasDeseadas; numCama++) {
                    if (CamaDAO.existeCama(conn, numeroHabitacion, numCama)) {
                        CamaDAO.activarCama(conn, numeroHabitacion, numCama);
                    } else {
                        Cama cama = new Cama();
                        cama.setNumeroHabitacion(numeroHabitacion);
                        cama.setNumeroCama(numCama);
                        cama.setEstado("LIBRE");
                        cama.setActiva(true);
                        CamaDAO.insertar(conn, cama);
                    }
                }

                // Desactivar sobrantes si están libres
                int desde = numeroCamasDeseadas + 1;
                if (CamaDAO.hayCamasActivasOcupadasDesde(conn, numeroHabitacion, desde)) {
                    throw new SQLException("No se puede reducir la habitación " + numeroHabitacion + " porque tiene camas ocupadas");
                }

                CamaDAO.desactivarCamasDesde(conn, numeroHabitacion, desde);
            }
        }

        // 2. Inactivar habitaciones sobrantes
        int primeraSobrante = camasPorHabitacion.size() + 1;
        List<Habitacion> sobrantes = HabitacionDAO.listarDesdeNumero(conn, primeraSobrante);

        for (Habitacion h : sobrantes) {
            if ("ACTIVA".equalsIgnoreCase(h.getEstadoHabitacion())) {

                if (CamaDAO.hayCamasActivasOcupadasEnHabitacion(conn, h.getNumeroHabitacion())) {
                    throw new SQLException("No se puede inactivar la habitación " + h.getNumeroHabitacion() + " porque tiene camas ocupadas");
                }

                HabitacionDAO.actualizarEstado(conn, h.getNumeroHabitacion(), "INACTIVA");
                CamaDAO.desactivarTodasLasCamasDeHabitacion(conn, h.getNumeroHabitacion());
            }
        }
    }
    
    public static List<Habitacion> listarHabitacionesActivas() {
        try (Connection conn = DBManager.getConnection()) {
            return HabitacionDAO.listarActivas(conn);
        } catch (SQLException e) {
            throw new DatabaseException("Error listando habitaciones activas", e);
        }
    }
    
    
}
