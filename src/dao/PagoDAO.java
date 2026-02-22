package dao;

import model.Pago;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PagoDAO {


    public static void insertar(Connection conn, Pago pago) throws SQLException {

        if (existePagoParaEstancia(conn, pago.getIdEstancia())) {
            throw new SQLException("Ya existe un pago para esta estancia");
        }

        String sql = """
            INSERT INTO pago (
                id_estancia,
                tipo_pago,
                fecha_pago,
                medio_pago,
                titular_pago,
                caducidad_tarjeta
            ) VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pago.getIdEstancia());
            ps.setString(2, pago.getTipoPago());
            ps.setString(3, pago.getFechaPago());
            ps.setString(4, pago.getMedioPago());
            ps.setString(5, pago.getTitularPago());
            ps.setString(6, pago.getCaducidadTarjeta());

            ps.executeUpdate();
        }
    }


    public static boolean existePagoParaEstancia(Connection conn, int idEstancia) throws SQLException {

        String sql = "SELECT COUNT(*) FROM pago WHERE id_estancia = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEstancia);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }


    public static Pago obtenerPorEstancia(Connection conn, int idEstancia) throws SQLException {

        String sql = "SELECT * FROM pago WHERE id_estancia = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEstancia);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }

        return null;
    }


    private static Pago mapear(ResultSet rs) throws SQLException {

        Pago p = new Pago();

        p.setIdPago(rs.getInt("id_pago"));
        p.setIdEstancia(rs.getInt("id_estancia"));

        p.setTipoPago(rs.getString("tipo_pago"));
        p.setFechaPago(rs.getString("fecha_pago"));

        p.setMedioPago(rs.getString("medio_pago"));
        p.setTitularPago(rs.getString("titular_pago"));
        p.setCaducidadTarjeta(rs.getString("caducidad_tarjeta"));

        return p;
    }
}
