package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;

import exception.DatabaseException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.Peregrino;
import service.PeregrinoService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;

import model.Estancia;
import service.EstanciaService;


public class MainController {

    @FXML private ListView<Peregrino> lvHuespedes;
    private Estancia estanciaActual;

    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    @FXML private javafx.scene.control.ScrollPane spFicha;

    // Huésped
    @FXML private TextField tfTipoDocumento;
    @FXML private TextField tfNumeroDocumento;
    @FXML private TextField tfNombre;
    @FXML private TextField tfApellido1;
    @FXML private TextField tfApellido2;
    @FXML private TextField tfFechaNacimiento;
    @FXML private TextField tfSexo;
    @FXML private TextField tfNacionalidad;
    @FXML private TextField tfPais;
    @FXML private TextField tfCodigoPostal;
    @FXML private TextField tfDireccion;
    @FXML private TextField tfDireccionComplementaria;
    @FXML private TextField tfCodigoMunicipio;
    @FXML private TextField tfNombreMunicipio;
    @FXML private TextField tfTelefono;
    @FXML private TextField tfTelefono2;
    @FXML private TextField tfCorreo;
    @FXML private TextField tfParentesco;

    // Estancia (por ahora solo defaults)
    @FXML private TextField tfReferencia;
    @FXML private TextField tfFechaEntrada;
    @FXML private TextField tfFechaSalida;

    // (Hay más campos en tu FXML, pero no hacen falta aquí todavía)

    private Peregrino actual;

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    private void initialize() {
    	
        configurarListView();
        refrescarLista();

        lvHuespedes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cargarEnFicha(newVal);
            }
        });

        // Carga municipios para autocompletar
        cargarMunicipiosDesdeCSV();
        activarAutocompletarMunicipio();
        
        // Instala validación suave (naranja) en campos clave
        instalarValidacionSuave();
        
        // Instala normalización flexible de fechas (acepta varios formatos, pero muestra siempre dd/MM/yyyy)
        activarFechasFlexibles();

        // Arranque: ficha nueva

        nuevaFicha();
        
        Platform.runLater(() -> {
            lvHuespedes.getScene().addEventFilter(
                javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {

                    if (event.getCode() == javafx.scene.input.KeyCode.DELETE
                            && lvHuespedes.isFocused()
                            && lvHuespedes.getSelectionModel().getSelectedItem() != null) {

                        onEliminarFicha();
                        event.consume();
                    }
                });
        });
    }

    // --------------------------
    // Navegación / carga / lista
    // --------------------------

    private void configurarListView() {
        lvHuespedes.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Peregrino p, boolean empty) {
                super.updateItem(p, empty);

                if (empty || p == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                String nombre = safe(p.getNombre());
                String a1 = safe(p.getApellido1());
                String doc = (safe(p.getTipoDocumento()) + " " + safe(p.getNumeroDocumento())).trim();

                String label = (nombre + " " + a1).trim();
                if (!doc.isBlank()) label += "  (" + doc + ")";
                setText(label.isBlank() ? ("ID " + p.getIdPeregrino()) : label);

                boolean invalido = peregrinoTieneErrores(p);

                if (invalido) {
                    setStyle("-fx-text-fill: #d97917;"); // naranja elegante
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void refrescarLista() {
        List<Peregrino> lista = PeregrinoService.listarTodos();
        lvHuespedes.setItems(FXCollections.observableArrayList(lista));
    }

    private void cargarEnFicha(Peregrino p) {
        actual = p;

        tfTipoDocumento.setText(safe(p.getTipoDocumento()));
        tfNumeroDocumento.setText(safe(p.getNumeroDocumento()));
        tfNombre.setText(safe(p.getNombre()));
        tfApellido1.setText(safe(p.getApellido1()));
        tfApellido2.setText(safe(p.getApellido2()));
        tfFechaNacimiento.setText(safe(p.getFechaNacimiento()));
        tfSexo.setText(safe(p.getSexo()));
        tfNacionalidad.setText(safe(p.getNacionalidad()));
        tfPais.setText(safe(p.getPais()));
        tfCodigoPostal.setText(safe(p.getCodigoPostal()));
        tfDireccion.setText(safe(p.getDireccion()));
        tfDireccionComplementaria.setText(safe(p.getDireccionComplementaria()));
        tfCodigoMunicipio.setText(safe(p.getCodigoMunicipio()));
        tfNombreMunicipio.setText(safe(p.getNombreMunicipio()));
        tfTelefono.setText(safe(p.getTelefono1()));
        tfTelefono2.setText(safe(p.getTelefono2()));
        tfCorreo.setText(safe(p.getCorreo()));
        tfParentesco.setText(safe(p.getParentesco()));
        tfFechaNacimiento.setText(fechaEsDesdeIso(p.getFechaNacimiento()));

        aplicarReglaMunicipio();
        
        cargarEstanciaDe(p);
    }

    private void volcarDeFichaAActual() {
        if (actual == null) actual = new Peregrino();

        if (actual.getRol() == null || actual.getRol().isBlank()) {
            actual.setRol("VI");
        }

        actual.setTipoDocumento(trim(tfTipoDocumento));
        actual.setNumeroDocumento(trim(tfNumeroDocumento));
        actual.setNombre(trim(tfNombre));
        actual.setApellido1(trim(tfApellido1));
        actual.setApellido2(trim(tfApellido2));
        actual.setFechaNacimiento(fechaIsoDesdeCampo(tfFechaNacimiento));
        actual.setSexo(trim(tfSexo));
        actual.setNacionalidad(trim(tfNacionalidad));
        actual.setPais(trim(tfPais));
        actual.setCodigoPostal(trim(tfCodigoPostal));
        actual.setDireccion(trim(tfDireccion));
        actual.setDireccionComplementaria(trim(tfDireccionComplementaria));
        actual.setCodigoMunicipio(trim(tfCodigoMunicipio));
        actual.setNombreMunicipio(trim(tfNombreMunicipio));
        actual.setTelefono1(trim(tfTelefono));
        actual.setTelefono2(trim(tfTelefono2));
        actual.setCorreo(trim(tfCorreo));
        actual.setParentesco(trim(tfParentesco));

        // ---- Estancia ----
        if (estanciaActual == null) estanciaActual = new Estancia();

        estanciaActual.setIdAlbergue(1);

        // referencia
        String ref = trim(tfReferencia);
        if (ref.isBlank()) ref = generarReferenciaEstancia();
        tfReferencia.setText(ref);
        estanciaActual.setReferenciaContrato(ref);

        // fechas (ISO en BD)
        estanciaActual.setFechaEntrada(fechaIsoDesdeCampo(tfFechaEntrada));
        estanciaActual.setFechaSalidaPrevista(fechaIsoDesdeCampo(tfFechaSalida));

        // defaults mínimos
        if (estanciaActual.getEstadoEstancia() == null || estanciaActual.getEstadoEstancia().isBlank()) {
            estanciaActual.setEstadoEstancia("ACTIVA");
        }
        if (estanciaActual.getNumeroHabitaciones() == 0) {
            estanciaActual.setNumeroHabitaciones(1);
        }
    }

    private void nuevaFicha() {
        actual = new Peregrino();
        actual.setRol("VI");

        cargarEnFicha(actual);
        lvHuespedes.getSelectionModel().clearSelection();

        // Defaults “prácticos”
        tfTipoDocumento.setText("NIF");
        tfSexo.setText("H");
        tfNacionalidad.setText("ESP");
        tfPais.setText("ESP");

        // Defaults fechas (entrada hoy, salida mañana) con hora
        LocalDate hoy = LocalDate.now();
        tfFechaEntrada.setText(hoy.format(FECHA_ES));
        tfFechaSalida.setText(hoy.plusDays(1).format(FECHA_ES));
        
        // Referencia única (timestamp)
        tfReferencia.setText(generarReferenciaEstancia());
        
        estanciaActual = new Estancia();
        estanciaActual.setIdAlbergue(1); // si solo hay uno
        estanciaActual.setReferenciaContrato(generarReferenciaEstancia());

        tfReferencia.setText(estanciaActual.getReferenciaContrato());

        cargarEnFicha(actual);
        lvHuespedes.getSelectionModel().clearSelection();

        aplicarReglaMunicipio();
        
        tfTipoDocumento.requestFocus();
        
        Platform.runLater(() -> {
            spFicha.setVvalue(0.0);      // scroll arriba
            tfTipoDocumento.requestFocus();
        });

    }
    
    // Genera una referencia única para la estancia.
    private String generarReferenciaEstancia() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }

    // --------------------------
    // Botones
    // --------------------------

    @FXML
    private void onGuardarFicha() {

        // Normaliza (pero NO bloquea)
        tfTipoDocumento.setText(trim(tfTipoDocumento).toUpperCase());
        tfNumeroDocumento.setText(trim(tfNumeroDocumento).toUpperCase());
        tfSexo.setText(trim(tfSexo).toUpperCase());
        tfNacionalidad.setText(trim(tfNacionalidad).toUpperCase());
        tfPais.setText(trim(tfPais).toUpperCase());

        // Normaliza fechas (dd/MM/yyyy en pantalla)
        normalizarCampoFecha(tfFechaEntrada);
        normalizarCampoFecha(tfFechaSalida);
        normalizarCampoFecha(tfFechaNacimiento);

        // Dispara validaciones suaves (solo pinta naranja)
        validarTipoDocumento();
        validarNumeroDocumentoConTipo();
        validarSexo();
        validarIso3(tfNacionalidad);
        validarIso3(tfPais);
        validarMunicipioSegunPaisSoloCampos();

        try {
            // 1️⃣ Volcar datos del formulario al modelo
            volcarDeFichaAActual();

            // 2️⃣ Guardar peregrino (para asegurar id real)
            PeregrinoService.guardar(actual);

            // 3️⃣ Enlazar estancia al peregrino recién guardado
            if (estanciaActual == null) {
                estanciaActual = new Estancia();
            }

            estanciaActual.setIdPeregrino(actual.getIdPeregrino());

            // Defaults de seguridad
            if (estanciaActual.getIdAlbergue() == 0) {
                estanciaActual.setIdAlbergue(1);
            }

            if (estanciaActual.getEstadoEstancia() == null 
                    || estanciaActual.getEstadoEstancia().isBlank()) {
                estanciaActual.setEstadoEstancia("ACTIVA");
            }

            if (estanciaActual.getNumeroHabitaciones() == 0) {
                estanciaActual.setNumeroHabitaciones(1);
            }

            // 4️⃣ Guardar estancia (insert o update automático)
            EstanciaService.guardar(estanciaActual);

            // 5️⃣ Refrescar UI
            refrescarLista();
            nuevaFicha();

        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onEliminarFicha() {

        if (actual == null || actual.getIdPeregrino() == 0) {
            nuevaFicha();
            return;
        }

        try {
            // Asumo que ya lo añadiste (como dijiste que funciona eliminar)
            PeregrinoService.eliminar(actual.getIdPeregrino());

            refrescarLista();
            nuevaFicha();

        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    // --------------------------
    // Validación suave (no bloquea)
    // --------------------------

    private void instalarValidacionSuave() {

        instalarValidador(tfTipoDocumento, this::validarTipoDocumento);
        instalarValidador(tfNumeroDocumento, this::validarNumeroDocumentoConTipo);
        instalarValidador(tfSexo, this::validarSexo);

        instalarValidador(tfNacionalidad, () -> validarIso3(tfNacionalidad));
        instalarValidador(tfPais, () -> validarIso3(tfPais));

        instalarValidador(tfCodigoMunicipio, this::validarMunicipioSegunPaisSoloCampos);
        instalarValidador(tfNombreMunicipio, this::validarMunicipioSegunPaisSoloCampos);

        tfPais.textProperty().addListener((o, a, b) -> {
            aplicarReglaMunicipio();
            validarMunicipioSegunPaisSoloCampos();
            refrescarLista();
        });
    }

    private void instalarValidador(TextField tf, Runnable validacion) {
        if (tf == null) return;

        tf.focusedProperty().addListener((obs, was, isNow) -> {
            if (!isNow) {
                validacion.run();
                refrescarLista();
            }
        });
    }

    private boolean validarTipoDocumento() {
        String v = trim(tfTipoDocumento).toUpperCase();

        if ("DNI".equals(v)) {
            v = "NIF";
        }

        tfTipoDocumento.setText(v);

        boolean ok = v.matches("^(NIF|NIE|PAS|OTRO|CIF|CIF_E)$");
        marcarError(tfTipoDocumento, !ok);
        return ok;
    }

    private boolean validarSexo() {
        String v = trim(tfSexo).toUpperCase();
        tfSexo.setText(v);

        boolean ok = v.matches("^(H|M|O)$");
        marcarError(tfSexo, !ok);
        return ok;
    }

    private boolean validarIso3(TextField tf) {
        String v = trim(tf).toUpperCase();
        tf.setText(v);

        boolean ok = v.matches("^[A-Z]{3}$");
        marcarError(tf, !ok);
        return ok;
    }

    private boolean validarNumeroDocumentoConTipo() {
        String tipo = trim(tfTipoDocumento).toUpperCase();
        String num = trim(tfNumeroDocumento).toUpperCase();
        tfNumeroDocumento.setText(num);

        boolean ok = validarNumeroDocumento(tipo, num);
        marcarError(tfNumeroDocumento, !ok);
        return ok;
    }

    private boolean validarNumeroDocumento(String tipoDoc, String numDoc) {
        if (numDoc.isBlank()) return false;

        switch (tipoDoc) {
            case "NIF":
                return numDoc.matches("^\\d{8}[A-Z]$");
            case "NIE":
                return numDoc.matches("^[XYZ]\\d{7}[A-Z]$");
            case "CIF":
            case "CIF_E":
                return numDoc.matches("^[ABCDEFGHJNPQRSUVW]\\d{7}[0-9A-J]$");
            case "PAS":
                return numDoc.matches("^[A-Z0-9]{5,15}$");
            case "OTRO":
                return numDoc.length() >= 3;
            default:
                return false;
        }
    }

    private boolean validarMunicipioSegunPaisSoloCampos() {
        String pais = trim(tfPais).toUpperCase();

        boolean esEsp = "ESP".equals(pais);

        if (esEsp) {
            String cod = trim(tfCodigoMunicipio);
            boolean ok = cod.matches("^\\d{5}$");
            marcarError(tfCodigoMunicipio, !ok);
            marcarError(tfNombreMunicipio, false);
            return ok;
        } else {
            String nom = trim(tfNombreMunicipio);
            boolean ok = !nom.isBlank();
            marcarError(tfNombreMunicipio, !ok);
            marcarError(tfCodigoMunicipio, false);
            return ok;
        }
    }

    private void aplicarReglaMunicipio() {
        String pais = trim(tfPais).toUpperCase();
        boolean esEsp = "ESP".equals(pais);

        // Solo ocultamos el campo (como pediste)
        tfCodigoMunicipio.setVisible(esEsp);
        tfCodigoMunicipio.setManaged(esEsp);

        tfNombreMunicipio.setVisible(!esEsp);
        tfNombreMunicipio.setManaged(!esEsp);
    }

    // --------------------------
    // “Perfil naranja” en lista
    // --------------------------

    private boolean peregrinoTieneErrores(Peregrino p) {

        String tipo = safe(p.getTipoDocumento()).toUpperCase();
        String num = safe(p.getNumeroDocumento()).toUpperCase();
        String sexo = safe(p.getSexo()).toUpperCase();
        String nac = safe(p.getNacionalidad()).toUpperCase();
        String pais = safe(p.getPais()).toUpperCase();

        if (!tipo.matches("^(NIF|NIE|PAS|OTRO|CIF|CIF_E)$")) return true;
        if (!validarNumeroDocumento(tipo, num)) return true;
        if (!sexo.matches("^(H|M|O)$")) return true;
        if (!nac.matches("^[A-Z]{3}$")) return true;
        if (!pais.matches("^[A-Z]{3}$")) return true;

        if ("ESP".equals(pais)) {
            String cod = safe(p.getCodigoMunicipio());
            if (!cod.matches("^\\d{5}$")) return true;
        } else {
            String nom = safe(p.getNombreMunicipio());
            if (nom.isBlank()) return true;
        }

        return false;
    }

    // --------------------------
    // Helpers
    // --------------------------

    private void marcarError(TextField tf, boolean error) {
        if (tf == null) return;
        tf.getStyleClass().remove("field-error");
        if (error) tf.getStyleClass().add("field-error");
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static String trim(TextField tf) {
        return tf == null || tf.getText() == null ? "" : tf.getText().trim();
    }
    
    
    
    
    
    
 // ---- Municipios (CSV en resources) ----

    private static class Municipio {
        final String provincia; // 2 dígitos
        final String codigo;    // 5 dígitos (PPMMM)
        final String nombre;    // texto original

        Municipio(String provincia, String codigo, String nombre) {
            this.provincia = provincia;
            this.codigo = codigo;
            this.nombre = nombre;
        }
    }

    private final Map<String, List<Municipio>> municipiosPorProvincia = new HashMap<>();
    private final ContextMenu sugerenciasMunicipio = new ContextMenu();

    private void cargarMunicipiosDesdeCSV() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream("/ui/resources/municipios.csv"),
                StandardCharsets.UTF_8))) {

            String line = br.readLine(); // cabecera
            if (line == null) return;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isBlank()) continue;

                String[] parts = line.split(";");
                if (parts.length < 3) continue;

                String cpro = parts[0].trim();
                String cmun = parts[1].trim();
                String nombre = parts[2].trim();

                // normalizamos ceros a la izquierda
                cpro = String.format("%02d", Integer.parseInt(cpro));
                cmun = String.format("%03d", Integer.parseInt(cmun));

                String codigo = cpro + cmun;

                Municipio m = new Municipio(cpro, codigo, nombre);

                municipiosPorProvincia.computeIfAbsent(cpro, k -> new ArrayList<>()).add(m);

                // Extra: si el nombre tiene "/", indexamos cada parte para que ambas funcionen
                // (lo usaremos en la búsqueda)
            }

        } catch (Exception e) {
            System.out.println("No se pudo cargar municipios.csv: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String normalizar(String s) {
        if (s == null) return "";
        String t = s.trim().toUpperCase();
        t = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", ""); // quita tildes
        t = t.replaceAll("\\s+", " ");
        return t;
    }

    private static String provinciaDesdeCP(String codigoPostal) {
        if (codigoPostal == null) return "";
        String cp = codigoPostal.trim();
        if (!cp.matches("^\\d{5}$")) return "";
        return cp.substring(0, 2);
    }
    
    
    
    
    
    
    private void activarAutocompletarMunicipio() {

        // escribes aquí (aunque ponga "Código municipio (ESP)")
        tfCodigoMunicipio.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;

            // solo si España
            String pais = normalizar(tfPais.getText());
            if (!"ESP".equals(pais)) {
                sugerenciasMunicipio.hide();
                return;
            }

            String query = normalizar(newVal);
            if (query.length() < 2) {
                sugerenciasMunicipio.hide();
                return;
            }

            // provincia preferida desde CP (si está)
            String prov = provinciaDesdeCP(tfCodigoPostal.getText());

            List<Municipio> candidatos = new ArrayList<>();

            if (!prov.isBlank() && municipiosPorProvincia.containsKey(prov)) {
                candidatos.addAll(municipiosPorProvincia.get(prov));
            } else {
                // sin CP, buscamos en toda España
                for (List<Municipio> l : municipiosPorProvincia.values()) candidatos.addAll(l);
            }

            List<Municipio> encontrados = buscarMunicipios(candidatos, query);

            if (encontrados.isEmpty()) {
                sugerenciasMunicipio.hide();
                return;
            }

            // top 10 sugerencias
            List<CustomMenuItem> items = new ArrayList<>();
            int limit = Math.min(10, encontrados.size());

            for (int i = 0; i < limit; i++) {
                Municipio m = encontrados.get(i);

                Label lbl = new Label(m.nombre + "  (" + m.codigo + ")");
                lbl.setStyle("-fx-padding: 4 8 4 8;");

                CustomMenuItem it = new CustomMenuItem(lbl, true);
                it.setOnAction(ev -> {

                    // aquí ponemos el código INE real
                    tfCodigoMunicipio.setText(m.codigo);

                    // opcional: guardamos el nombre en el otro campo (aunque esté oculto)
                    tfNombreMunicipio.setText(m.nombre);

                    marcarError(tfCodigoMunicipio, false);
                    marcarError(tfNombreMunicipio, false);

                    sugerenciasMunicipio.hide();
                });

                items.add(it);
            }

            sugerenciasMunicipio.getItems().setAll(items);

            if (!sugerenciasMunicipio.isShowing()) {
                sugerenciasMunicipio.show(tfCodigoMunicipio, javafx.geometry.Side.BOTTOM, 0, 0);
            }
        });

        // si pierde foco, ocultamos el menú
        tfCodigoMunicipio.focusedProperty().addListener((o, was, isNow) -> {
            if (!isNow) sugerenciasMunicipio.hide();
        });
    }

    private List<Municipio> buscarMunicipios(List<Municipio> base, String queryNorm) {

        // ranking simple: empieza por... luego contiene...
        List<Municipio> empieza = new ArrayList<>();
        List<Municipio> contiene = new ArrayList<>();

        for (Municipio m : base) {

            // nombres con "/": indexamos cada parte
            String[] variantes = m.nombre.split("/");
            boolean match = false;
            boolean start = false;

            for (String v : variantes) {
                String vn = normalizar(v);
                if (vn.startsWith(queryNorm)) {
                    match = true;
                    start = true;
                    break;
                }
                if (vn.contains(queryNorm)) {
                    match = true;
                }
            }

            if (start) empieza.add(m);
            else if (match) contiene.add(m);
        }

        // empieza primero, luego contiene
        List<Municipio> res = new ArrayList<>(empieza);
        res.addAll(contiene);
        return res;
    }
    
    
    private static final DateTimeFormatter FECHA_ES = DateTimeFormatter.ofPattern("dd/MM/uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private static final DateTimeFormatter[] FORMATOS_ACEPTADOS = new DateTimeFormatter[] {
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ofPattern("d-M-uuuu").withResolverStyle(ResolverStyle.STRICT),
            DateTimeFormatter.ISO_LOCAL_DATE // uuuu-MM-dd
    };

    private LocalDate parseFechaFlexible(String texto) {
        if (texto == null) return null;

        String t = texto.trim();
        if (t.isBlank()) return null;

        for (DateTimeFormatter f : FORMATOS_ACEPTADOS) {
            try {
                return LocalDate.parse(t, f);
            } catch (Exception ignored) {}
        }
        return null;
    }

    private void normalizarCampoFecha(TextField tf) {
        LocalDate d = parseFechaFlexible(tf.getText());
        if (d != null) {
            tf.setText(d.format(FECHA_ES));          // siempre dd/MM/yyyy
            marcarError(tf, false);
        } else {
            // no bloquea, solo marca naranja si escribió algo inválido
            marcarError(tf, !trim(tf).isBlank());
        }
    }
    
    private String fechaIsoDesdeCampo(TextField tf) {
        LocalDate d = parseFechaFlexible(tf.getText());
        return (d == null) ? "" : d.toString(); // yyyy-MM-dd
    }
    
    private void activarFechasFlexibles() {

        // Entrada: al perder foco, normaliza y ajusta salida
        tfFechaEntrada.focusedProperty().addListener((obs, was, isFocused) -> {
            if (!isFocused) {

                LocalDate entradaAntes = parseFechaFlexible(tfFechaEntrada.getText());
                normalizarCampoFecha(tfFechaEntrada);
                LocalDate entrada = parseFechaFlexible(tfFechaEntrada.getText());

                if (entrada != null) {

                    LocalDate salida = parseFechaFlexible(tfFechaSalida.getText());

                    boolean salidaVacia = trim(tfFechaSalida).isBlank();
                    boolean salidaInvalida = (salida == null && !trim(tfFechaSalida).isBlank());

                    boolean eraDefault = false;
                    if (entradaAntes != null && salida != null) {
                        eraDefault = salida.equals(entradaAntes.plusDays(1));
                    }

                    if (salidaVacia || salidaInvalida || eraDefault) {
                        tfFechaSalida.setText(entrada.plusDays(1).format(FECHA_ES));
                        marcarError(tfFechaSalida, false);
                    }
                }
            }
        });

        // Salida: normaliza al perder foco
        tfFechaSalida.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                normalizarCampoFecha(tfFechaSalida);
            }
        });

        // Fecha de nacimiento: solo normaliza
        tfFechaNacimiento.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                normalizarCampoFecha(tfFechaNacimiento);

                LocalDate fn = parseFechaFlexible(tfFechaNacimiento.getText());
                if (fn != null && fn.isAfter(LocalDate.now())) {
                    marcarError(tfFechaNacimiento, true);
                }
            }
        });
    }
    
    private String fechaEsDesdeIso(String iso) {
        if (iso == null || iso.isBlank()) return "";
        try {
            return LocalDate.parse(iso).format(FECHA_ES);
        } catch (Exception e) {
            return iso; // por si viene algo raro
        }
    }
    
    private void cargarEstanciaDe(Peregrino p) {
        try {
            // Necesitas un método así en tu servicio:
            // - "buscarActivaPorPeregrino" o "buscarUltimaPorPeregrino"
        	estanciaActual = EstanciaService.buscarActivaPorPeregrino(p.getIdPeregrino());

            if (estanciaActual == null) {
                estanciaActual = new Estancia();
                estanciaActual.setIdPeregrino(p.getIdPeregrino());
                // idAlbergue obligatorio: si solo hay uno, puedes poner 1.
                estanciaActual.setIdAlbergue(1);
            }

            tfReferencia.setText(safe(estanciaActual.getReferenciaContrato()));
            tfFechaEntrada.setText(fechaEsDesdeIso(estanciaActual.getFechaEntrada()));
            tfFechaSalida.setText(fechaEsDesdeIso(estanciaActual.getFechaSalidaPrevista()));

        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            // Si falla, no bloqueamos: creamos una estancia en blanco
            estanciaActual = new Estancia();
            estanciaActual.setIdPeregrino(p.getIdPeregrino());
            estanciaActual.setIdAlbergue(1);
            tfReferencia.setText("");
            tfFechaEntrada.setText("");
            tfFechaSalida.setText("");
        }
    }
}