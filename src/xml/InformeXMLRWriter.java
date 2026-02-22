package xml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class InformeXMLRWriter {

    public static void guardarEnFichero(String xml, String rutaFichero) throws IOException {

        Path ruta = Path.of(rutaFichero);

        // Crear carpetas si no existen
        if (ruta.getParent() != null) {
            Files.createDirectories(ruta.getParent());
        }

        Files.writeString(ruta, xml, StandardCharsets.UTF_8);
    }
}
