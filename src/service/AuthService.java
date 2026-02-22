package service;

import java.sql.Connection;

import dao.UsuarioDAO;
import db.DBManager;
import exception.AuthException;
import model.Usuario;
import util.HashUtil;

public class AuthService {

    private static Usuario usuarioActual;

    private AuthService() {}

    public static Usuario login(String nombreUsuario, String passwordEnClaro) {

        if (nombreUsuario == null || nombreUsuario.isBlank()
                || passwordEnClaro == null || passwordEnClaro.isBlank()) {
            throw new AuthException("Usuario y contraseña obligatorios");
        }

        try (Connection conn = DBManager.getConnection()) {

            Usuario u = UsuarioDAO.obtenerPorNombreUsuario(conn, nombreUsuario);

            if (u == null) {
                throw new AuthException("Usuario o contraseña incorrectos");
            }

            String hashIntro = HashUtil.sha256(passwordEnClaro);

            if (!hashIntro.equals(u.getPasswordHash())) {
                throw new AuthException("Usuario o contraseña incorrectos");
            }

            usuarioActual = u;
            return u;

        } catch (AuthException e) {
            throw e; // re-lanzar tal cual
        } catch (Exception e) {
            // fallo técnico: BD, driver, etc.
            throw new AuthException("Error interno al iniciar sesión");
        }
    }

    public static void logout() {
        usuarioActual = null;
    }

    public static Usuario getUsuarioActual() {
        return usuarioActual;
    }
}


	
	

