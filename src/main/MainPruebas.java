package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import db.DBInit;
import db.DBManager;
import exception.AuthException;
import model.Peregrino;
import model.Usuario;
import service.AuthService;
import service.PeregrinoService;

public class MainPruebas {

    public static void main(String[] args) {
/*
        System.out.println("Iniciando prueba de conexión a la BD...");

        try (Connection connection = DBManager.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;"
             )) {

            System.out.println("Conexión establecida correctamente.");
            System.out.println("Tablas encontradas en la base de datos:");

            while (rs.next()) {
                String nombreTabla = rs.getString("name");
                System.out.println(" - " + nombreTabla);
            }

        } catch (SQLException e) {
            System.out.println("ERROR al acceder a la base de datos");
            e.printStackTrace();
        }

        System.out.println("Fin de la prueba.");
        
        
        
        
        
        
        
        System.out.println("=== PRUEBA LOGIN (DEBUG) ===");

        // 1) Asegura semillas (admin) en ESA BD
        DBInit.init();

        // 2) Ver qué hay realmente en la tabla usuario
        try (Connection conn = DBManager.getConnection()) {

            System.out.println("Conectado OK. Dump usuarios:");

            String sql = "SELECT id_usuario, nombre_usuario, password_hash, rol FROM usuario";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                int count = 0;
                while (rs.next()) {
                    count++;
                    System.out.println(" - id=" + rs.getInt("id_usuario")
                            + " user=" + rs.getString("nombre_usuario")
                            + " rol=" + rs.getString("rol")
                            + " hash=" + rs.getString("password_hash"));
                }

                if (count == 0) {
                    System.out.println(" (tabla usuario VACÍA)");
                }
            }

        } catch (Exception e) {
            System.out.println("Error leyendo tabla usuario: " + e.getMessage());
            e.printStackTrace();
        }

        // 3) Probar login
        try {
            Usuario u = AuthService.login("admin", "admin");
            System.out.println("Login correcto: " + u.getNombreUsuario() + " (" + u.getRol() + ")");
        } catch (AuthException e) {
            System.out.println("Login fallido: " + e.getMessage());
        }

        System.out.println("=== FIN PRUEBA ===");
        */
        
    	System.out.println("=== PRUEBA AUTOGUARDADO PEREGRINO ===");

        Peregrino p = new Peregrino();
        p.setRol("VI");

        // 1) Guardado inicial (INSERT)
        p.setNombre("Juan");
        System.out.println("Guardando 1ª vez (debería INSERT)...");
        PeregrinoService.guardar(p);

        System.out.println("ID asignado: " + p.getIdPeregrino());

        // 2) Guardado parcial (UPDATE)
        p.setApellido1("Pérez");
        System.out.println("Guardando 2ª vez (debería UPDATE)...");
        PeregrinoService.guardar(p);

        // 3) Otro update
        p.setTipoDocumento("DNI");
        p.setNumeroDocumento("12345678A");
        System.out.println("Guardando 3ª vez (UPDATE)...");
        PeregrinoService.guardar(p);

        System.out.println("=== FIN PRUEBA ===");
        
        
        
        
        
        
        
        
        
        
        
        
        
    }
}
