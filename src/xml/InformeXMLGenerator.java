package xml;

import model.Estancia;

import java.time.LocalDate;
import java.util.List;

public class InformeXMLGenerator {

    /**
     * Genera un string XML  con las estancias del día, está sin terminar, es provisional.
     */
    public static String generarXMLParteDia(int idAlbergue, LocalDate dia, List<Estancia> estancias) {

        StringBuilder sb = new StringBuilder();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<parte>\n");
        sb.append("  <albergueId>").append(idAlbergue).append("</albergueId>\n");
        sb.append("  <fecha>").append(dia).append("</fecha>\n");
        sb.append("  <estancias>\n");

        for (Estancia e : estancias) {
            sb.append("    <estancia>\n");
            sb.append("      <idEstancia>").append(e.getIdEstancia()).append("</idEstancia>\n");
            sb.append("      <idPeregrino>").append(e.getIdPeregrino()).append("</idPeregrino>\n");
            sb.append("      <idCama>").append(e.getIdCama() == null ? "" : e.getIdCama()).append("</idCama>\n");
            sb.append("      <entrada>").append(e.getFechaEntrada()).append("</entrada>\n");
            sb.append("      <salidaPrevista>").append(e.getFechaSalidaPrevista() == null ? "" : e.getFechaSalidaPrevista()).append("</salidaPrevista>\n");
            sb.append("      <salidaReal>").append(e.getFechaSalidaReal() == null ? "" : e.getFechaSalidaReal()).append("</salidaReal>\n");
            sb.append("      <estado>").append(e.getEstadoEstancia()).append("</estado>\n");
            sb.append("    </estancia>\n");
        }

        sb.append("  </estancias>\n");
        sb.append("</parte>\n");

        return sb.toString();
    }
}
