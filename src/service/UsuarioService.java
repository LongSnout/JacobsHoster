package service;

import dao.UsuarioDAO;
import db.DBManager;
import exception.DatabaseException;
import model.Usuario;
import util.HashUtil;

import java.sql.Connection;
import java.sql.SQLException;

public class UsuarioService {

    private UsuarioService() {
    }

    public static void crearUsuario(String nombreUsuario, String passwordEnClaro, String rol) {

        if (nombreUsuario == null || nombreUsuario.isBlank()) {
            throw new DatabaseException("El nombre de usuario es obligatorio");
        }

        if (passwordEnClaro == null || passwordEnClaro.isBlank()) {
            throw new DatabaseException("La contraseña es obligatoria");
        }

        if (rol == null || rol.isBlank()) {
            throw new DatabaseException("El rol es obligatorio");
        }

        try (Connection conn = DBManager.getConnection()) {

            if (UsuarioDAO.existeNombreUsuario(conn, nombreUsuario.trim())) {
                throw new DatabaseException("El nombre de usuario ya existe");
            }

            Usuario u = new Usuario();
            u.setNombreUsuario(nombreUsuario.trim());
            u.setPasswordHash(HashUtil.sha256(passwordEnClaro));
            u.setRol(rol.trim().toUpperCase());

            UsuarioDAO.insertar(conn, u);

        } catch (DatabaseException e) {
            throw e;
        } catch (SQLException e) {
            throw new DatabaseException("Error creando usuario", e);
        }
    }
    
    
    public static void actualizarCredencialesUsuario(int idUsuario, String nuevoNombreUsuario, String nuevaPasswordEnClaro) {

        if (nuevoNombreUsuario == null || nuevoNombreUsuario.isBlank()) {
            throw new DatabaseException("El nombre de usuario es obligatorio");
        }

        if (nuevaPasswordEnClaro == null || nuevaPasswordEnClaro.isBlank()) {
            throw new DatabaseException("La contraseña es obligatoria");
        }

        try (Connection conn = DBManager.getConnection()) {

            Usuario existente = UsuarioDAO.obtenerPorNombreUsuario(conn, nuevoNombreUsuario.trim());

            if (existente != null && existente.getIdUsuario() != idUsuario) {
                throw new DatabaseException("Ese nombre de usuario ya existe");
            }

            String nuevoHash = HashUtil.sha256(nuevaPasswordEnClaro);

            UsuarioDAO.actualizarCredenciales(conn, idUsuario, nuevoNombreUsuario.trim(), nuevoHash);

        } catch (DatabaseException e) {
            throw e;
        } catch (SQLException e) {
            throw new DatabaseException("Error actualizando credenciales del usuario", e);
        }
    }
    
    public static Usuario obtenerPrimerGerente() {
        try (Connection conn = DBManager.getConnection()) {
            return UsuarioDAO.obtenerPrimerGerente(conn);
        } catch (SQLException e) {
            throw new DatabaseException("Error obteniendo el gerente actual", e);
        }
    }
    
    /*
    public static Usuario obtenerPrimerGerente() {
        try (Connection conn = DBManager.getConnection()) {
            Usuario u = UsuarioDAO.obtenerPorNombreUsuario(conn, "admin");
            if (u != null && "GERENTE".equalsIgnoreCase(u.getRol())) {
                return u;
            }

            // Si más adelante haces varios usuarios, aquí convendría un método DAO
            // para buscar el primer usuario con rol GERENTE.
            return null;

        } catch (SQLException e) {
            throw new DatabaseException("Error obteniendo gerente", e);
        }
    }
    */
    
    public static void actualizarNombreUsuario(int idUsuario, String nuevoNombreUsuario) {

        if (nuevoNombreUsuario == null || nuevoNombreUsuario.isBlank()) {
            throw new DatabaseException("El nombre de usuario es obligatorio");
        }

        try (Connection conn = DBManager.getConnection()) {

            Usuario existente = UsuarioDAO.obtenerPorNombreUsuario(conn, nuevoNombreUsuario.trim());

            if (existente != null && existente.getIdUsuario() != idUsuario) {
                throw new DatabaseException("Ese nombre de usuario ya existe");
            }

            Usuario actual = UsuarioDAO.obtenerPorId(conn, idUsuario);
            if (actual == null) {
                throw new DatabaseException("No existe el usuario a actualizar");
            }

            UsuarioDAO.actualizarCredenciales(
                    conn,
                    idUsuario,
                    nuevoNombreUsuario.trim(),
                    actual.getPasswordHash()
            );

        } catch (DatabaseException e) {
            throw e;
        } catch (SQLException e) {
            throw new DatabaseException("Error actualizando nombre de usuario", e);
        }
    }
    
    
}


