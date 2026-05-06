package service;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import config.AppConfig;
import dao.PrerregistroDAO;
import db.DBManager;
import exception.DatabaseException;
import exception.ValidationException;
import model.Prerregistro;

public class PrerregistroService {

    private PrerregistroService() {
        // Constructor privado para evitar instanciación
    }

    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_ACEPTADO  = "ACEPTADO";
    public static final String ESTADO_RECHAZADO = "RECHAZADO";
    public static final String ESTADO_CADUCADO  = "CADUCADO";


    public static List<Prerregistro> listarPendientes() {
        try (Connection conn = DBManager.getConnection()) {
            return PrerregistroDAO.listarPendientesPorFechaLlegada(conn);
        } catch (SQLException e) {
            throw new DatabaseException("Error al listar preregistros pendientes", e);
        }
    }

    public static Prerregistro obtenerPorId(int idPrerregistro) {
        try (Connection conn = DBManager.getConnection()) {
            return PrerregistroDAO.obtenerPorId(conn, idPrerregistro);
        } catch (SQLException e) {
            throw new DatabaseException("Error al obtener preregistro por ID", e);
        }
    }

    /**
     * Inserta un preregistro en local.
     */
    public static void insertar(Prerregistro pr) {
        validarMinimo(pr);
        aplicarDefaults(pr);

        try (Connection conn = DBManager.getConnection()) {
            PrerregistroDAO.insertar(conn, pr);
        } catch (SQLException e) {
            throw new DatabaseException("Error al insertar preregistro", e);
        }
    }

    /**
     * Marca un preregistro como ACEPTADO / RECHAZADO / CADUCADO...
     */
    public static void cambiarEstado(int idPrerregistro, String nuevoEstado) {
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            throw new ValidationException("El nuevo estado no puede ser vacío");
        }

        try (Connection conn = DBManager.getConnection()) {
            PrerregistroDAO.actualizarEstado(conn, idPrerregistro, nuevoEstado);
        } catch (SQLException e) {
            throw new DatabaseException("Error al actualizar estado de preregistro", e);
        }
    }

    public static void marcarAceptado(int idPrerregistro) {
        cambiarEstado(idPrerregistro, ESTADO_ACEPTADO);
    }

    public static void marcarRechazado(int idPrerregistro) {
        cambiarEstado(idPrerregistro, ESTADO_RECHAZADO);
    }

    public static void marcarCaducado(int idPrerregistro) {
        cambiarEstado(idPrerregistro, ESTADO_CADUCADO);
    }


    
    /*
     * Recibe los preregistros desde la nube y los inserta en local, evitando duplicados por idPrerregistroNube.
     * Devuelve el número de preregistros nuevos insertados en local.
     * Si ocurre cualquier error (API no responde, JSON mal formado, error DB...) se captura la excepción, se muestra un mensaje de error y se devuelve 0.
     * El estado de los preregistros recibidos siempre se establece como PENDIENTE en local, independientemente del estado que tengan en la nube.
     * Las fechas se reciben como string en formato ISO (yyyy-MM-dd) o como array de Jackson, pero se guardan como string ISO en local.
     */
    public static int recibirDesdeNube() {
        try {
            URL url = new java.net.URI(
                AppConfig.API_BASE_URL + "/api/prerregistros"
            ).toURL();

            java.net.HttpURLConnection http = (java.net.HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setRequestProperty("X-API-KEY", config.AppConfig.API_TOKEN);
            http.setConnectTimeout(10_000);
            http.setReadTimeout(15_000);

            int status = http.getResponseCode();
            if (status != 200) {
                System.err.println("[Sync] API respondió con código: " + status);
                return 0;
            }

            String json;
            try (java.io.InputStream is = http.getInputStream();
                 java.io.BufferedReader br = new java.io.BufferedReader(
                     new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String linea;
                while ((linea = br.readLine()) != null) sb.append(linea);
                json = sb.toString();
            }

            org.json.JSONArray array = new org.json.JSONArray(json);
            int insertados = 0;

            try (Connection conn = DBManager.getConnection()) {
            	for (int i = 0; i < array.length(); i++) {
            	    org.json.JSONObject obj = array.getJSONObject(i);

            	    System.out.println("[Sync] JSON prerregistro recibido: " + obj.toString());
            	    System.out.println("[Sync] fechaPrevistaLlegada recibida: "
            	        + obj.optString("fechaPrevistaLlegada", "NO VIENE"));

            	    String idNube = obj.optString("idPrerregistroNube", null);
                    if (idNube == null || idNube.isBlank()) continue;

                    // Evitar duplicados
                    if (PrerregistroDAO.existePorIdNube(conn, idNube)) {
                        System.out.println("[Sync] Prerregistro ya existe en local. Confirmando en nube: " + idNube);
                        confirmarRecibidoEnNube(idNube);
                        continue;
                    }

                    Prerregistro pr = new Prerregistro();
                    pr.setIdPrerregistroNube(idNube);
                    pr.setIdAlbergue(config.AppConfig.ID_ALBERGUE);
                    pr.setEstadoPrerregistro(ESTADO_PENDIENTE); // siempre PENDIENTE en local

                 // Las fechas se esperan en formato ISO como texto desde la API. Si no vienen, se asigna la fecha actual
                    pr.setFechaEnvio(obj.optString("fechaEnvio", LocalDate.now().toString()));
                    pr.setFechaPrevistaLlegada(obj.optString("fechaPrevistaLlegada", LocalDate.now().toString()));

                    pr.setRol(obj.optString("rol", "VI"));
                    pr.setNombre(obj.optString("nombre", ""));
                    pr.setApellido1(obj.optString("apellido1", ""));
                    pr.setApellido2(obj.optString("apellido2", null));

                    pr.setTipoDocumento(obj.optString("tipoDocumento", ""));
                    pr.setNumeroDocumento(obj.optString("numeroDocumento", ""));

                    pr.setFechaNacimiento(obj.optString("fechaNacimiento", null));
                    pr.setNacionalidad(obj.optString("nacionalidad", null));
                    pr.setSexo(obj.optString("sexo", null));

                    pr.setDireccion(obj.optString("direccion", null));
                    pr.setDireccionComplementaria(obj.optString("direccionComplementaria", null));
                    pr.setCodigoMunicipio(obj.optString("codigoMunicipio", null));
                    pr.setNombreMunicipio(obj.optString("nombreMunicipio", null));
                    pr.setCodigoPostal(obj.optString("codigoPostal", null));
                    pr.setPais(obj.optString("pais", null));

                    pr.setTelefono1(obj.optString("telefono1", null));
                    pr.setTelefono2(obj.optString("telefono2", null));
                    pr.setCorreo(obj.optString("correo", null));
                    pr.setParentesco(obj.optString("parentesco", null));

                    PrerregistroDAO.insertar(conn, pr);
                    insertados++;
                    
                    confirmarRecibidoEnNube(idNube);
                    
                }
            }

            System.out.println("[Sync] Prerregistros nuevos recibidos: " + insertados);
            return insertados;

        } catch (Exception e) {
            System.err.println("[Sync] Error al recibir desde nube: " + e.getMessage());
            return 0;
        }
    }
    
    
    private static boolean confirmarRecibidoEnNube(String idPrerregistroNube) {
        try {
            URL url = new java.net.URI(
                AppConfig.API_BASE_URL + "/api/prerregistros/" + idPrerregistroNube + "/procesado"
            ).toURL();

            java.net.HttpURLConnection http = (java.net.HttpURLConnection) url.openConnection();
            http.setRequestMethod("PUT");
            http.setRequestProperty("X-API-KEY", config.AppConfig.API_TOKEN);
            http.setConnectTimeout(10_000);
            http.setReadTimeout(15_000);

            int status = http.getResponseCode();

            if (status == 200) {
                System.out.println("[Sync] Confirmado en nube: " + idPrerregistroNube);
                return true;
            } else {
                System.err.println("[Sync] No se pudo confirmar en nube. Código: " + status);
                return false;
            }

        } catch (Exception e) {
            System.err.println("[Sync] Error confirmando en nube: " + e.getMessage());
            return false;
        }
    }



    private static void validarMinimo(Prerregistro pr) {
        if (pr == null) throw new ValidationException("El preregistro no puede ser null");

        if (pr.getTipoDocumento() == null || pr.getTipoDocumento().isBlank()) {
            throw new ValidationException("tipoDocumento es obligatorio en preregistro");
        }
        if (pr.getNumeroDocumento() == null || pr.getNumeroDocumento().isBlank()) {
            throw new ValidationException("numeroDocumento es obligatorio en preregistro");
        }

        if (pr.getNombre() == null || pr.getNombre().isBlank()) {
            throw new ValidationException("nombre es obligatorio en preregistro");
        }
        if (pr.getApellido1() == null || pr.getApellido1().isBlank()) {
            throw new ValidationException("apellido1 es obligatorio en preregistro");
        }
    }

    private static void aplicarDefaults(Prerregistro pr) {


        if (pr.getIdAlbergue() == 0) {
            pr.setIdAlbergue(config.AppConfig.ID_ALBERGUE);
        }

        if (pr.getEstadoPrerregistro() == null || pr.getEstadoPrerregistro().isBlank()) {
            pr.setEstadoPrerregistro(ESTADO_PENDIENTE);
        }


        if (pr.getRol() == null || pr.getRol().isBlank()) {
            pr.setRol("VI");
        }


        if (pr.getFechaEnvio() == null || pr.getFechaEnvio().isBlank()) {
            pr.setFechaEnvio(LocalDate.now().toString());
        }


        if (pr.getFechaPrevistaLlegada() == null || pr.getFechaPrevistaLlegada().isBlank()) {
            pr.setFechaPrevistaLlegada(LocalDate.now().toString());
        }


        pr.setTipoDocumento(pr.getTipoDocumento().trim());
        pr.setNumeroDocumento(pr.getNumeroDocumento().trim());
    }
    
    
    public static Prerregistro obtenerPorDocumento(String tipoDocumento, String numeroDocumento) {
        try (Connection conn = DBManager.getConnection()) {
            return PrerregistroDAO.obtenerPorDocumento(conn, tipoDocumento, numeroDocumento);
        } catch (SQLException e) {
            throw new DatabaseException("Error al buscar prerregistro por documento", e);
        }
    }
    
    
    
    
}









