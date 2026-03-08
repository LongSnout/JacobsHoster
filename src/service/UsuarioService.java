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
}