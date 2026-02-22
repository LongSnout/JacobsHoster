package service;

import db.DBManager;
import exception.DatabaseException;
import model.LlegadaRow;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorDatosService {

    public static List<LlegadaRow> obtenerLlegadasDelDia(LocalDate dia) {

        List<LlegadaRow> lista = new ArrayList<>();
        String fecha = dia.toString(); // YYYY-MM-DD

        String sqlPre = """
            SELECT
                id_preregistro,
                id_peregrino,
                rol,
                nombre,
                apellido1,
                apellido2,
                tipo_documento,
                numero_documento,
                nacionalidad,
                sexo,
                telefono1,
                correo,
                pais
            FROM preregistro
            WHERE fecha_prevista_llegada = ?
              AND estado_preregistro = 'PENDIENTE'
            ORDER BY id_preregistro DESC
            """;

        String sqlEst = """
            SELECT
                e.id_estancia,
                e.id_peregrino,
                p.rol,
                p.nombre,
                p.apellido1,
                p.apellido2,
                p.tipo_documento,
                p.numero_documento,
                p.nacionalidad,
                p.sexo,
                p.telefono1,
                p.correo,
                p.pais
            FROM estancia e
            JOIN peregrino p ON p.id_peregrino = e.id_peregrino
            WHERE e.fecha_entrada = ?
              AND e.estado_estancia <> 'CANCELADA'
            ORDER BY e.id_estancia DESC
            """;

        try (Connection conn = DBManager.getConnection()) {

            //Prerregistros (gris)
            try (PreparedStatement ps = conn.prepareStatement(sqlPre)) {
                ps.setString(1, fecha);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        LlegadaRow row = new LlegadaRow();
                        row.setTipoFila(LlegadaRow.TipoFila.PRERREGISTRO);

                        row.setIdPrerregistro(rs.getInt("id_preregistro"));

                        int idP = rs.getInt("id_peregrino");
                        row.setIdPeregrino(rs.wasNull() ? null : idP);

                        row.setRol(rs.getString("rol"));
                        row.setNombre(rs.getString("nombre"));
                        row.setApellido1(rs.getString("apellido1"));
                        row.setApellido2(rs.getString("apellido2"));

                        row.setTipoDocumento(rs.getString("tipo_documento"));
                        row.setNumeroDocumento(rs.getString("numero_documento"));

                        row.setNacionalidad(rs.getString("nacionalidad"));
                        row.setSexo(rs.getString("sexo"));

                        row.setTelefono1(rs.getString("telefono1"));
                        row.setCorreo(rs.getString("correo"));

                        row.setPais(rs.getString("pais"));

                        row.setGris(true);

                        lista.add(row);
                    }
                }
            }

            //Estancias (normal)
            try (PreparedStatement ps = conn.prepareStatement(sqlEst)) {
                ps.setString(1, fecha);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        LlegadaRow row = new LlegadaRow();
                        row.setTipoFila(LlegadaRow.TipoFila.ESTANCIA);

                        row.setIdEstancia(rs.getInt("id_estancia"));
                        row.setIdPeregrino(rs.getInt("id_peregrino"));

                        row.setRol(rs.getString("rol"));
                        row.setNombre(rs.getString("nombre"));
                        row.setApellido1(rs.getString("apellido1"));
                        row.setApellido2(rs.getString("apellido2"));

                        row.setTipoDocumento(rs.getString("tipo_documento"));
                        row.setNumeroDocumento(rs.getString("numero_documento"));

                        row.setNacionalidad(rs.getString("nacionalidad"));
                        row.setSexo(rs.getString("sexo"));

                        row.setTelefono1(rs.getString("telefono1"));
                        row.setCorreo(rs.getString("correo"));

                        row.setPais(rs.getString("pais"));

                        row.setGris(false);

                        lista.add(row);
                    }
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new DatabaseException("Error obteniendo llegadas del día " + fecha, e);
        }
    }
}


