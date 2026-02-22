package service;

import db.DBManager;
import exception.DatabaseException;
import model.EnvioXML;
import model.Estancia;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import dao.EnvioXMLDAO;
import dao.EstanciaDAO;

import xml.InformeXMLGenerator;
import xml.InformeXMLRWriter;

// Clase provisional.

public class EnvioXMLService {

    /**
     * Genera el parte xml y lo guarda en la carpeta indicada. Además, registra el envío en la base de datos.
     * El XML se genera en "informeXMLGenerator" y se guarda con "informeXMLWriter".
     * El registro del envío se hace con "EnvioXMLDAO".
     * Aún no está terminado
     */
    public static EnvioXML generarYGuardarParteDia(int idAlbergue, LocalDate dia, String carpetaSalida) {

        String fechaISO = dia.toString(); // YYYY-MM-DD
        String timestamp = LocalDateTime.now().toString().replace(":", "-");
        String ruta = carpetaSalida + "/parte_" + fechaISO + "_" + timestamp + ".xml";

        try (Connection conn = DBManager.getConnection()) {


            List<Estancia> estancias = EstanciaDAO.listarPorDia(conn, idAlbergue, fechaISO);

            String xml = InformeXMLGenerator.generarXMLParteDia(idAlbergue, dia, estancias);

            InformeXMLRWriter.guardarEnFichero(xml, ruta);

            EnvioXML envio = new EnvioXML();
            envio.setFechaEnvio(LocalDateTime.now().toString());
            envio.setRutaFicheroXml(ruta);
            envio.setEstadoEnvio("PENDIENTE");

            EnvioXMLDAO.insertar(conn, envio);


            for (Estancia e : estancias) {
                EstanciaDAO.vincularEnvioXml(conn, e.getIdEstancia(), envio.getIdEnvioXml(), xml);
            }

            return envio;

        } catch (SQLException e) {
            throw new DatabaseException("Error generando y guardando XML del día " + dia, e);
        } catch (Exception e) {
            throw new DatabaseException("Error (no SQL) generando o guardando el XML: " + e.getMessage(), e);
        }
    }
}



