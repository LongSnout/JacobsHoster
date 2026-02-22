package service;

import dao.CamaDAO;
import dao.HabitacionDAO;
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

        //Crear habitación
        Habitacion h = new Habitacion();
        h.setNumeroHabitacion(numeroHabitacion);
        h.setIdAlbergue(idAlbergue);
        h.setNumeroCamas(numeroCamas);
        h.setEstadoHabitacion("ACTIVA");

        HabitacionDAO.insertar(conn, h);

        //Crear camas asociadas
        for (int i = 0; i < numeroCamas; i++) {
            Cama cama = new Cama();
            cama.setNumeroHabitacion(numeroHabitacion);
            cama.setEstado("LIBRE");
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
            // Añadir camas
            int aCrear = nuevoNumeroCamas - camasActuales;
            for (int i = 0; i < aCrear; i++) {
                Cama cama = new Cama();
                cama.setNumeroHabitacion(numeroHabitacion);
                cama.setEstado("LIBRE");
                CamaDAO.insertar(conn, cama);
            }

        } else if (nuevoNumeroCamas < camasActuales) {
            // Eliminar camas libres
            int aEliminar = camasActuales - nuevoNumeroCamas;
            CamaDAO.eliminarCamasLibres(conn, numeroHabitacion, aEliminar);
        }

        // Actualizar contador en habitación
        HabitacionDAO.actualizarNumeroCamas(conn, numeroHabitacion, nuevoNumeroCamas);
    }

    /**
     * Lista habitaciones.
     */
    public static List<Habitacion> listarHabitaciones(Connection conn) throws SQLException {
        return HabitacionDAO.listarTodas(conn);
    }
}
