package service;

import db.DBManager;
import exception.DatabaseException;
import model.Albergue;
import model.EnvioXML;
import model.Estancia;
import model.Peregrino;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import dao.AlbergueDAO;
import dao.EnvioXMLDAO;
import dao.EstanciaDAO;
import dao.PeregrinoDAO;

import xml.InformeXMLGenerator;
import xml.InformeXMLRWriter;

// Clase provisional la exportación principal se hace desde MainController ahora, pendiente de arreglar.

public class EnvioXMLService {

    public static EnvioXML generarYGuardarParteDia(int idAlbergue, LocalDate dia, String carpetaSalida) {

        String fechaISO = dia.toString();
        String timestamp = LocalDateTime.now().toString().replace(":", "-");
        String ruta = carpetaSalida + "/parte_" + fechaISO + "_" + timestamp + ".xml";

        try (Connection conn = DBManager.getConnection()) {

            Albergue albergue = AlbergueDAO.obtenerAlbergue(conn);

            List<Estancia> estancias = EstanciaDAO.listarPorDia(conn, idAlbergue, fechaISO);

            List<Peregrino> peregrinos = new ArrayList<>();
            for (Estancia e : estancias) {
                Peregrino p = PeregrinoDAO.obtenerPorId(conn, e.getIdPeregrino());
                if (p != null) peregrinos.add(p);
                else estancias.remove(e);
            }

            String xml = InformeXMLGenerator.generarXMLParteDia(albergue, estancias, peregrinos);

            InformeXMLRWriter.guardarEnFichero(xml, ruta);

            EnvioXML envio = new EnvioXML();
            envio.setFechaEnvio(LocalDateTime.now().toString());
            envio.setRutaFicheroXml(ruta);
            envio.setEstadoEnvio("PENDIENTE");

            EnvioXMLDAO.insertar(conn, envio);

            for (Estancia e : estancias) {
                EstanciaDAO.vincularEnvioXml(conn, e.getIdEstancia(), envio.getIdEnvioXml(), "PENDIENTE");
            }

            return envio;

        } catch (SQLException e) {
            throw new DatabaseException("Error generando y guardando XML del día " + dia, e);
        } catch (Exception e) {
            throw new DatabaseException("Error generando o guardando el XML: " + e.getMessage(), e);
        }
    }
}