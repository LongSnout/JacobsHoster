package xml;

import model.Albergue;
import model.Estancia;
import model.Peregrino;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class RD933Mapper {

    // Formatos requeridos por el Ministerio
    private static final DateTimeFormatter FMT_FECHA      = DateTimeFormatter.ofPattern("yyyy-MM-ddXXX");
    private static final DateTimeFormatter FMT_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private static final ZoneId ZONA                      = ZoneId.of("Europe/Madrid");

    /**
     * Convierte una fecha ISO (yyyy-MM-dd) al formato del Ministerio (yyyy-MM-dd+02:00)
     */
    public static String mapFecha(String fechaIso) {
        if (fechaIso == null || fechaIso.isBlank()) return "";
        try {
            ZonedDateTime zdt = LocalDate.parse(fechaIso).atStartOfDay(ZONA);
            return zdt.format(FMT_FECHA);
        } catch (Exception e) {
            return fechaIso;
        }
    }

    /**
     * Convierte una fecha ISO (yyyy-MM-dd) a fecha-hora con zona (para fechaEntrada/Salida)
     */
    public static String mapFechaHora(String fechaIso) {
        if (fechaIso == null || fechaIso.isBlank()) return "";
        try {
            ZonedDateTime zdt = LocalDate.parse(fechaIso).atStartOfDay(ZONA);
            return zdt.format(FMT_FECHA_HORA);
        } catch (Exception e) {
            return fechaIso;
        }
    }

    /**
     * Devuelve el código de establecimiento del albergue,
     * o un placeholder si no está configurado.
     */
    public static String mapCodigoEstablecimiento(Albergue albergue) {
        if (albergue == null) return "SIN_CONFIGURAR";
        String cod = albergue.getCodigoEstablecimientoMir();
        return (cod != null && !cod.isBlank()) ? cod : "SIN_CONFIGURAR";
    }

    /**
     * Devuelve el valor si no es nulo/vacío, o vacío si lo es.
     * Útil para campos opcionales del XML.
     */
    public static String safe(String valor) {
        return (valor == null) ? "" : valor.trim();
    }

    /**
     * Devuelve el numPersonas de la estancia, mínimo 1.
     */
    public static int mapNumPersonas(Estancia e) {
        // Ahora numPersonasContrato es la referencia de grupo (String)
        // numPersonas en el XML siempre es 1 por peregrino individual
        return 1;
    }

    /**
     * Devuelve el numHabitaciones de la estancia, mínimo 1.
     */
    public static int mapNumHabitaciones(Estancia e) {
        return Math.max(1, e.getNumeroHabitaciones());
    }

    /**
     * Devuelve el tipo de pago, defaulteando a EFECT si está vacío.
     */
    public static String mapTipoPago(Estancia e) {
        String v = safe(e.getTipoPago());
        return v.isBlank() ? "EFECT" : v;
    }

    /**
     * Devuelve el rol del peregrino, defaulteando a VI si está vacío.
     */
    public static String mapRol(Peregrino p) {
        String v = safe(p.getRol());
        return v.isBlank() ? "VI" : v;
    }
}