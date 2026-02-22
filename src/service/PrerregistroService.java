package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import dao.PrerregistroDAO;
import db.DBManager;
import exception.DatabaseException;
import exception.ValidationException;
import model.Prerregistro;

public class PrerregistroService {

    private PrerregistroService() {
        // Constructor privado para evitar instanciación
    }

    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_ACEPTADO  = "ACEPTADO";
    public static final String ESTADO_RECHAZADO = "RECHAZADO";
    public static final String ESTADO_CADUCADO  = "CADUCADO";


    public static List<Prerregistro> listarPendientes() {
        try (Connection conn = DBManager.getConnection()) {
            return PrerregistroDAO.listarPendientesPorFechaLlegada(conn);
        } catch (SQLException e) {
            throw new DatabaseException("Error al listar preregistros pendientes", e);
        }
    }

    public static Prerregistro obtenerPorId(int idPrerregistro) {
        try (Connection conn = DBManager.getConnection()) {
            return PrerregistroDAO.obtenerPorId(conn, idPrerregistro);
        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener preregistro por ID", e);
        }
    }

    /**
     * Inserta un preregistro en local.
     */
    public static void insertar(Prerregistro pr) {
        validarMinimo(pr);
        aplicarDefaults(pr);

        try (Connection conn = DBManager.getConnection()) {
            PrerregistroDAO.insertar(conn, pr);
        } catch (SQLException e) {
            throw new DatabaseException("Error al insertar preregistro", e);
        }
    }

    /**
     * Marca un preregistro como ACEPTADO / RECHAZADO / CADUCADO...
     */
    public static void cambiarEstado(int idPrerregistro, String nuevoEstado) {
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            throw new ValidationException("El nuevo estado no puede ser vacío");
        }

        try (Connection conn = DBManager.getConnection()) {
            PrerregistroDAO.actualizarEstado(conn, idPrerregistro, nuevoEstado);
        } catch (SQLException e) {
            throw new DatabaseException("Error al actualizar estado de preregistro", e);
        }
    }

    public static void marcarAceptado(int idPrerregistro) {
        cambiarEstado(idPrerregistro, ESTADO_ACEPTADO);
    }

    public static void marcarRechazado(int idPrerregistro) {
        cambiarEstado(idPrerregistro, ESTADO_RECHAZADO);
    }

    public static void marcarCaducado(int idPrerregistro) {
        cambiarEstado(idPrerregistro, ESTADO_CADUCADO);
    }


    public static void recibirDesdeNube() {

    }



    private static void validarMinimo(Prerregistro pr) {
        if (pr == null) throw new ValidationException("El preregistro no puede ser null");

        if (pr.getTipoDocumento() == null || pr.getTipoDocumento().isBlank()) {
            throw new ValidationException("tipoDocumento es obligatorio en preregistro");
        }
        if (pr.getNumeroDocumento() == null || pr.getNumeroDocumento().isBlank()) {
            throw new ValidationException("numeroDocumento es obligatorio en preregistro");
        }

        if (pr.getNombre() == null || pr.getNombre().isBlank()) {
            throw new ValidationException("nombre es obligatorio en preregistro");
        }
        if (pr.getApellido1() == null || pr.getApellido1().isBlank()) {
            throw new ValidationException("apellido1 es obligatorio en preregistro");
        }
    }

    private static void aplicarDefaults(Prerregistro pr) {


        if (pr.getIdAlbergue() == 0) {
            pr.setIdAlbergue(1);
        }

        if (pr.getEstadoPrerregistro() == null || pr.getEstadoPrerregistro().isBlank()) {
            pr.setEstadoPrerregistro(ESTADO_PENDIENTE);
        }


        if (pr.getRol() == null || pr.getRol().isBlank()) {
            pr.setRol("VI");
        }


        if (pr.getFechaEnvio() == null || pr.getFechaEnvio().isBlank()) {
            pr.setFechaEnvio(LocalDate.now().toString());
        }


        if (pr.getFechaPrevistaLlegada() == null || pr.getFechaPrevistaLlegada().isBlank()) {
            pr.setFechaPrevistaLlegada(LocalDate.now().toString());
        }


        pr.setTipoDocumento(pr.getTipoDocumento().trim());
        pr.setNumeroDocumento(pr.getNumeroDocumento().trim());
    }
}
