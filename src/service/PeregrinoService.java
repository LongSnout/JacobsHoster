package service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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

    
    
    
    
}






