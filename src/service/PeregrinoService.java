package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import dao.PeregrinoDAO;
import db.DBManager;
import exception.DatabaseException;
import model.Peregrino;

public class PeregrinoService {

    private PeregrinoService() {
    	// Constructor privado para evitar instanciación
        
    }

    /**
     * Guarda un peregrino:
     * Pensado para autoguardado progresivo
     * Si el peregrino no tiene id (idPeregrino=0) se inserta, si ya tiene id se actualiza
     */
    public static void guardar(Peregrino p) {

        try (Connection conn = DBManager.getConnection()) {

            if (p.getIdPeregrino() == 0) {
                PeregrinoDAO.insertar(conn, p);
            } else {
                PeregrinoDAO.update(conn, p);
            }

        } catch (SQLException e) {
            throw new DatabaseException("Error al guardar peregrino", e);
        }
    }
    
    public static List<Peregrino> listarTodos() {

        try (Connection conn = DBManager.getConnection()) {
            return PeregrinoDAO.listarTodos(conn);
        } catch (SQLException e) {
            throw new DatabaseException("Error al listar peregrinos", e);
        }
    }
    
    public static Peregrino obtenerPorId(int idPeregrino) {

        try (Connection conn = DBManager.getConnection()) {
            return PeregrinoDAO.obtenerPorId(conn, idPeregrino);
        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener peregrino por id: " + idPeregrino, e);
        }
    }

    public static Peregrino obtenerPorDocumento(String tipoDocumento, String numeroDocumento) {

        try (Connection conn = DBManager.getConnection()) {
            return PeregrinoDAO.obtenerPorDocumento(conn, tipoDocumento, numeroDocumento);
        } catch (SQLException e) {
            throw new DatabaseException(
                    "Error al buscar peregrino por documento: "
                    + tipoDocumento + " " + numeroDocumento, e
            );
        }
    }
    
    public static void eliminar(int idPeregrino) {

        try (Connection conn = DBManager.getConnection()) {
            PeregrinoDAO.eliminarPorId(conn, idPeregrino);
        } catch (SQLException e) {
            throw new DatabaseException("Error al eliminar peregrino id=" + idPeregrino, e);
        }
    }

    public static List<Peregrino> buscarGlobal(String texto) {
        try (Connection conn = DBManager.getConnection()) {
            return PeregrinoDAO.buscarGlobal(conn, texto);
        } catch (SQLException e) {
            throw new DatabaseException("Error al buscar peregrinos: " + texto, e);
        }
    }
    
    public static List<Peregrino> listarPorRangoFechas(int idAlbergue,
            LocalDate fechaDesde, LocalDate fechaHasta) {
        try (Connection conn = DBManager.getConnection()) {
            return PeregrinoDAO.listarPorRangoFechas(conn, idAlbergue,
                fechaDesde.toString(), fechaHasta.toString());
        } catch (SQLException e) {
            throw new DatabaseException("Error listando peregrinos por rango", e);
        }
    }

    public static Map<String, Integer> contarPorSexo(int idAlbergue, int anio) {
        try (Connection conn = DBManager.getConnection()) {
            return PeregrinoDAO.contarPorSexo(conn, idAlbergue, anio);
        } catch (SQLException e) {
            throw new DatabaseException("Error contando por sexo", e);
        }
    }

    public static Map<String, Integer> contarPorPais(int idAlbergue, int anio) {
        try (Connection conn = DBManager.getConnection()) {
            return PeregrinoDAO.contarPorPais(conn, idAlbergue, anio);
        } catch (SQLException e) {
            throw new DatabaseException("Error contando por país", e);
        }
    }

    public static Map<String, Integer> contarPorFranjaEdad(int idAlbergue, int anio) {
        try (Connection conn = DBManager.getConnection()) {
            return PeregrinoDAO.contarPorFranjaEdad(conn, idAlbergue, anio);
        } catch (SQLException e) {
            throw new DatabaseException("Error contando por franja de edad", e);
        }
    }
    
    
    
    
}






