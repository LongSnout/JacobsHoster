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
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.Peregrino;
import service.PeregrinoService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javafx.animation.PauseTransition;
import javafx.application.Platform;

import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import model.Albergue;
import model.Estancia;
import service.AlbergueService;
import service.CamaService;
import service.EstanciaService;

import javafx.util.Duration;




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

    // Extras para navegación rápida por fechas
    @FXML private Button btnDiaAnterior;
    @FXML private Button btnDiaSiguiente;
    @FXML private javafx.scene.control.DatePicker dpFechaLista;
    @FXML private TextField tfBuscarHuesped;
    
    
    //Plazas y capacidad
    @FXML private Label lblPlazas;
    @FXML private TextField tfNumHabitaciones;
    @FXML private javafx.scene.control.CheckBox cbInternet;
    @FXML private TextField tfTamGrupo;
    @FXML private TextField tfNumPersonas;
    @FXML private TextField tfNumeroHabitacion;
    @FXML private TextField tfNumeroCama;
    

    private LocalDate fechaLista = LocalDate.now();

    private Peregrino actual;

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    private void initialize() {

        configurarListView();

        fechaLista = LocalDate.now();
        dpFechaLista.setValue(fechaLista);

        refrescarLista();
        actualizarModoBusqueda();

        lvHuespedes.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cargarEnFicha(newVal);
            }
        });

        // Carga municipios para autocompletar
        cargarMunicipiosDesdeCSV();
        activarAutocompletarMunicipio();

        // Carga países para validación
        cargarPaisesDesdeCSV();
        activarAutocompletarPais(tfNacionalidad);
        activarAutocompletarPais(tfPais);

        // Instala validación suave (naranja) en campos clave
        instalarValidacionSuave();

        // Instala normalización flexible de fechas
        activarFechasFlexibles();

        // Autorrelleno por documento al salir del campo
        tfNumeroDocumento.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) {
                intentarAutorrellenarPorDocumento();
            }
        });

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

        dpFechaLista.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fechaLista = newVal;
                refrescarLista();
            }
        });

        tfBuscarHuesped.textProperty().addListener((obs, oldVal, newVal) -> {
            actualizarModoBusqueda();
            refrescarLista();
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
                    setGraphic(null);
                    setStyle("");
                    return;
                }

                String nombre = safe(p.getNombre());
                String a1 = safe(p.getApellido1());
                String doc = (safe(p.getTipoDocumento()) + " " + safe(p.getNumeroDocumento())).trim();

                String label = (nombre + " " + a1).trim();
                if (!doc.isBlank()) label += "  (" + doc + ")";
                if (label.isBlank()) label = "ID " + p.getIdPeregrino();

                boolean invalido = peregrinoTieneErrores(p);
                boolean nuevoHoy = esEntradaHoy(p);

                Text punto = new Text();
                Text texto = new Text(label);

                if (nuevoHoy) {
                    punto.setText("● ");
                    punto.setStyle("-fx-fill: #1f6f3e;");
                } else {
                    punto.setText("");
                }

                if (invalido) {
                    texto.setStyle("-fx-fill: #d97917;");
                } else {
                	texto.setStyle("-fx-fill: -fx-text-inner-color;");
                }

                TextFlow flow = new TextFlow(punto, texto);

                setText(null);
                setGraphic(flow);
                setStyle("");
            }
        });
    }
    
    
    private void refrescarLista() {
        try {
            String filtro = trim(tfBuscarHuesped);

            List<Peregrino> lista;

            if (filtro.isBlank()) {
                lista = EstanciaService.listarPeregrinosPresentesPorDia(1, fechaLista);
            } else {
                lista = PeregrinoService.buscarGlobal(filtro);
            }

            lvHuespedes.setItems(FXCollections.observableArrayList(lista));

        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            lvHuespedes.setItems(FXCollections.observableArrayList());
        }
        
        refrescarIndicadorPlazas();
    }

    /*
    private void refrescarLista() {
        try {
            List<Peregrino> lista = EstanciaService.listarPeregrinosPorDia(1, fechaLista);

            String filtro = trim(tfBuscarHuesped).toUpperCase();

            if (!filtro.isBlank()) {
                lista = lista.stream()
                        .filter(p -> coincideBusqueda(p, filtro))
                        .toList();
            }

            lvHuespedes.setItems(FXCollections.observableArrayList(lista));

        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            lvHuespedes.setItems(FXCollections.observableArrayList());
        }
    } */

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
        cargarHabitacionYCamaEnFicha();
        
        bloquearDocumentoSiExistente(true);
    }

    private void volcarDeFichaAActual() {
        if (actual == null) actual = new Peregrino();

        if (actual.getRol() == null || actual.getRol().isBlank()) {
            actual.setRol("VI");
        }

        // ---- Peregrino ----
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

        // referencia (nunca vacía)
        String ref = trim(tfReferencia);
        if (ref.isBlank()) {
            ref = generarReferenciaEstancia();
            tfReferencia.setText(ref);
        }
        estanciaActual.setReferenciaContrato(ref);

        // fechas ISO desde campos (pueden venir vacías)
        String fe = fechaIsoDesdeCampo(tfFechaEntrada); // yyyy-MM-dd o ""
        String fs = fechaIsoDesdeCampo(tfFechaSalida);  // yyyy-MM-dd o ""

        // ENTRADA: si viene vacía o inválida -> HOY sí o sí (BD = NOT NULL)
        if (fe.isBlank()) {
            LocalDate hoy = LocalDate.now();
            tfFechaEntrada.setText(hoy.format(FECHA_ES));
            fe = hoy.toString(); // ISO
        }

        // SALIDA: si viene vacía o inválida -> por defecto ENTRADA+1
        if (fs.isBlank()) {
            LocalDate entrada = LocalDate.parse(fe);
            LocalDate salida = entrada.plusDays(1);
            tfFechaSalida.setText(salida.format(FECHA_ES));
            fs = salida.toString();
        }

        // CHECK: salida_prevista > entrada (si no, ajusta a entrada + 1)
        try {
            LocalDate entrada = LocalDate.parse(fe);
            LocalDate salida = LocalDate.parse(fs);

            if (!salida.isAfter(entrada)) {
                salida = entrada.plusDays(1);
                tfFechaSalida.setText(salida.format(FECHA_ES));
                fs = salida.toString();
            }
        } catch (Exception ignored) {
            // Si por alguna razón se lía el parseo, no bloqueamos:
            // dejamos al menos la entrada válida y anulamos salida
            fs = "";
            tfFechaSalida.setText("");
        }

        // Guardado a modelo (entrada SIEMPRE con valor; salida puede ser NULL)
        estanciaActual.setFechaEntrada(fe);
        estanciaActual.setFechaSalidaPrevista(fs.isBlank() ? null : fs);

        // defaults mínimos
        if (estanciaActual.getEstadoEstancia() == null || estanciaActual.getEstadoEstancia().isBlank()) {
            estanciaActual.setEstadoEstancia("ACTIVA");
        }
        if (estanciaActual.getNumeroHabitaciones() == 0) {
            estanciaActual.setNumeroHabitaciones(1);
        }
        
     // Gestión de asignación manual de cama/habitación
        String txtHab = trim(tfNumeroHabitacion);
        String txtCama = trim(tfNumeroCama);

        // Caso 1: ambos vacíos -> conservar cama actual si ya existía
        if (txtHab.isBlank() && txtCama.isBlank()) {
            // No tocamos estanciaActual.idCama
        	// FUTURO:
        	//TODO:
        	// Si la estancia es nueva y no tiene cama asignada, aquí entrará la lógica de autoasignación.
        	// Criterios previstos:
        	// - no asignar camas inexistentes
        	// - evitar mezclar hombres con habitación de mujeres
        	// - priorizar grupos juntos
        	// - intentar llenar habitaciones completas si conviene
        	// - opcionalmente minimizar número de habitaciones abiertas
        }
        // Caso 2: ambos rellenos -> intentar reasignar
        else if (!txtHab.isBlank() && !txtCama.isBlank()) {
            try {
                int numeroHabitacion = Integer.parseInt(txtHab);
                int numeroCama = Integer.parseInt(txtCama);

                model.Cama cama = CamaService.obtenerPorHabitacionYNumeroCama(numeroHabitacion, numeroCama);

                if (cama != null) {
                    estanciaActual.setIdCama(cama.getIdCama());
                } else {
                    // Habitación/cama no válida: de momento no machacamos la cama existente
                    // Más adelante aquí se puede marcar error visual
                }

            } catch (NumberFormatException e) {
                // Si meten texto raro, tampoco machacamos la cama existente
            }
        }
        // Caso 3: uno relleno y otro no -> no tocar la cama existente
        else {
            // Incompleto: no hacemos nada para no romper asignaciones previas
            // Más adelante aquí se puede marcar error visual
        }
    }

    private void nuevaFicha() {
        actual = new Peregrino();
        actual.setRol("VI");
        
        estanciaActual = new Estancia();
        estanciaActual.setIdAlbergue(1);
        estanciaActual.setEstadoEstancia("ACTIVA");
        estanciaActual.setNumeroHabitaciones(1);
        
        
        
        bloquearDocumentoSiExistente(false);
        

        
        // Limpia selección
        lvHuespedes.getSelectionModel().clearSelection();

        // Defaults “prácticos”
        tfTipoDocumento.setText("NIF");
        tfSexo.setText("H");
        tfNacionalidad.setText("ESP");
        tfPais.setText("ESP");

        // Referencia única
        String ref = generarReferenciaEstancia();
        tfReferencia.setText(ref);

        // Fechas por defecto (entrada hoy, salida mañana)
        LocalDate hoy = LocalDate.now();
        tfFechaEntrada.setText(hoy.format(FECHA_ES));
        tfFechaSalida.setText(hoy.plusDays(1).format(FECHA_ES));
        
        estanciaActual.setFechaEntrada(hoy.toString());
        estanciaActual.setFechaSalidaPrevista(hoy.plusDays(1).toString());

        // Limpia el resto de campos típicos (si quieres, opcional)
        tfNumeroDocumento.setText("");
        tfNombre.setText("");
        tfApellido1.setText("");
        tfApellido2.setText("");
        tfFechaNacimiento.setText("");
        tfCodigoPostal.setText("");
        tfDireccion.setText("");
        tfDireccionComplementaria.setText("");
        tfCodigoMunicipio.setText("");
        tfNombreMunicipio.setText("");
        tfTelefono.setText("");
        tfTelefono2.setText("");
        tfCorreo.setText("");
        tfParentesco.setText("");
        tfNumeroHabitacion.setText("");
        tfNumeroCama.setText("");

        // Aplica regla municipio tras setear país
        aplicarReglaMunicipio();

        // Scroll arriba + foco
        Platform.runLater(() -> {
            spFicha.setVvalue(0.0);
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

        LocalDate fn = parseFechaFlexible(tfFechaNacimiento.getText());
        if (fn != null && fn.isAfter(LocalDate.now())) {
            marcarError(tfFechaNacimiento, true);
        } else {
            // OJO: esto solo quita error si la fecha existe y no es futura.
            // Si está vacía y quieres que sea "permitida", déjalo así.
            marcarError(tfFechaNacimiento, false);
        }

        // Dispara validaciones suaves (solo pinta naranja)
        validarTipoDocumento();
        validarNumeroDocumentoConTipo();
        validarSexo();
        validarIso3(tfNacionalidad);
        validarIso3(tfPais);
        validarMunicipioSegunPaisSoloCampos();

        // Bloqueo por aforo: solo para estancias nuevas
        LocalDate fechaControl = parseFechaFlexible(tfFechaEntrada.getText());
        if (fechaControl == null) {
            fechaControl = LocalDate.now();
        }

        if (esEstanciaNueva()) {
            int ocupadas = EstanciaService.contarPlazasOcupadasEnFecha(1, fechaControl);
            int totales = CamaService.contarCapacidadTotal();

            if (totales > 0 && ocupadas >= totales) {
                System.out.println("Aforo completo para la fecha " + fechaControl + ": no se pueden añadir más huéspedes.");
                return;
            }
        }

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

        // País: solo reacciona cuando ya es ISO3 (para no fastidiar el autocompletar)
        tfPais.textProperty().addListener((o, a, b) -> {

            String v = trim(tfPais).toUpperCase();

            if (v.matches("^[A-Z]{3}$")) {
                aplicarReglaMunicipio();
                validarMunicipioSegunPaisSoloCampos();
                refrescarLista();
            }
        });

        // País: al perder foco, si escribieron "ESPAÑA" (o parecido), lo convierte a ISO3
        tfPais.focusedProperty().addListener((obs, was, isNow) -> {
            if (!isNow) {
                Pais p = buscarPaisMejorCoincidencia(trim(tfPais));
                if (p != null) {
                    tfPais.setText(p.iso3);
                    aplicarReglaMunicipio();
                    validarMunicipioSegunPaisSoloCampos();
                    refrescarLista();
                }
            }
        });
        
        tfNacionalidad.focusedProperty().addListener((obs, was, isNow) -> {
            if (!isNow) {
                // fuerza autocompletado “silent” al perder foco (igual que País)
                Pais p = buscarPaisMejorCoincidencia(trim(tfNacionalidad));
                if (p != null) {
                    tfNacionalidad.setText(p.iso3);
                    marcarError(tfNacionalidad, false);
                }
            }
        });
        
    }
    
    @FXML
    private void onNuevoAlbergue() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/ui/nuevo_albergue.fxml")
            );

            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Nuevo albergue");
            stage.setScene(scene);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        String original = trim(tf);
        if (original.isBlank()) {
            marcarError(tf, true);
            return false;
        }

        String v = original.toUpperCase();

        // Si NO es ISO3, intentamos resolver por nombre / ISO2 / variantes
        if (!v.matches("^[A-Z]{3}$") && !listaPaises.isEmpty()) {
            String iso = resolverIso3DesdeTexto(original);
            if (iso != null) {
                tf.setText(iso);
                marcarError(tf, false);
                return true;
            }
        }

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
    
    
    
    
    
    
 // ---- Municipios (CSV en /ui/resources) ----

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
    
 // ---- Países (CSV: /ui/resources/CodigoISOPaises.csv) ----
    private static class Pais {
        final String iso3;
        final String iso2;
        final String nombre; // nombre "principal" (tal cual viene, con posibles "/")

        Pais(String iso3, String iso2, String nombre) {
            this.iso3 = iso3;
            this.iso2 = iso2;
            this.nombre = nombre;
        }
    }

    private final List<Pais> listaPaises = new ArrayList<>();

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
    
    private void cargarPaisesDesdeCSV() {
        listaPaises.clear();

        String[] rutas = {
                "/ui/resources/CodigoISOPaises.csv",
                "/ui/resources/codigoisopaises.csv"
        };

        InputStream is = null;
        String rutaUsada = null;

        for (String r : rutas) {
            is = getClass().getResourceAsStream(r);
            if (is != null) {
                rutaUsada = r;
                break;
            }
        }

        if (is == null) {
            System.out.println("No se encontró CodigoISOPaises.csv en el classpath.");
            System.out.println("Rutas probadas:");
            for (String r : rutas) System.out.println("  - " + r);
            return;
        }

        System.out.println("Cargando países desde: " + rutaUsada);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String line = br.readLine(); // cabecera
            if (line == null) return;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isBlank()) continue;

                String[] parts = line.split(";");
                if (parts.length < 3) continue;

                String iso3 = parts[0].trim().toUpperCase();
                String iso2 = parts[1].trim().toUpperCase();
                String nombre = parts[2].trim();

                if (iso3.isBlank()) continue;

                listaPaises.add(new Pais(iso3, iso2, nombre));
            }

            System.out.println("Países cargados: " + listaPaises.size());

        } catch (Exception e) {
            System.out.println("No se pudo cargar CodigoISOPaises.csv: " + e.getMessage());
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
        
     // ✅ Auto-aplicar municipio al perder foco (si escribe nombre parcial o completo)
        tfCodigoMunicipio.focusedProperty().addListener((o, was, isNow) -> {
            if (isNow) return; // solo cuando pierde foco

            // solo España
            String pais = normalizar(tfPais.getText());
            if (!"ESP".equals(pais)) return;

            String raw = trim(tfCodigoMunicipio);
            if (raw.isBlank()) return;

            // si ya es un código válido, no tocamos
            if (raw.matches("^\\d{5}$")) return;

            String query = normalizar(raw);
            if (query.length() < 2) return;

            // provincia preferida desde CP (si está)
            String prov = provinciaDesdeCP(tfCodigoPostal.getText());

            List<Municipio> candidatos = new ArrayList<>();
            if (!prov.isBlank() && municipiosPorProvincia.containsKey(prov)) {
                candidatos.addAll(municipiosPorProvincia.get(prov));
            } else {
                for (List<Municipio> l : municipiosPorProvincia.values()) candidatos.addAll(l);
            }

            List<Municipio> encontrados = buscarMunicipios(candidatos, query);

            if (encontrados.isEmpty()) return;

            // si hay uno solo -> lo aplicamos directo
            if (encontrados.size() == 1) {
                Municipio m = encontrados.get(0);
                tfCodigoMunicipio.setText(m.codigo);
                tfNombreMunicipio.setText(m.nombre);
                marcarError(tfCodigoMunicipio, false);
                marcarError(tfNombreMunicipio, false);
                sugerenciasMunicipio.hide();
                return;
            }

            // si hay varios, intentamos decisión “segura”:
            // - si alguno coincide EXACTO con alguna variante del nombre, elegimos ese
            for (Municipio m : encontrados) {
                for (String v : m.nombre.split("/")) {
                    if (normalizar(v).equals(query)) {
                        tfCodigoMunicipio.setText(m.codigo);
                        tfNombreMunicipio.setText(m.nombre);
                        marcarError(tfCodigoMunicipio, false);
                        marcarError(tfNombreMunicipio, false);
                        sugerenciasMunicipio.hide();
                        return;
                    }
                }
            }

            // si sigue habiendo duda, no inventamos: dejamos el texto tal cual
            // (opcional: podrías re-mostrar sugerencias aquí)
            // sugerenciasMunicipio.show(tfCodigoMunicipio, Side.BOTTOM, 0, 0);
        });
        
     // TAB (y opcional ENTER) para aceptar la primera sugerencia
        tfCodigoMunicipio.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, ev -> {

            if (!sugerenciasMunicipio.isShowing()) return;

            // TAB: aceptar primera sugerencia
            if (ev.getCode() == javafx.scene.input.KeyCode.TAB /*|| ev.getCode() == javafx.scene.input.KeyCode.ENTER*/) {

                if (!sugerenciasMunicipio.getItems().isEmpty()) {
                    // el primero debería ser el mejor (tu ranking ya lo ordena)
                    javafx.scene.control.MenuItem mi = sugerenciasMunicipio.getItems().get(0);

                    if (mi instanceof CustomMenuItem cmi) {
                        // Ejecuta el mismo handler que cuando haces click
                        cmi.fire();
                        ev.consume();
                    }
                }
            }

            // ESC para cerrar sin tocar nada
            if (ev.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                sugerenciasMunicipio.hide();
                ev.consume();
            }
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
    
    private void activarAutocompletarPais(TextField tf) {

        final ContextMenu menu = new ContextMenu(); // 👈 uno por TextField, no compartido

        tf.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;

            if (listaPaises.isEmpty()) {
                menu.hide();
                return;
            }

            String query = normalizar(newVal);

            // si ya parece ISO3, no molestamos
            if (query.matches("^[A-Z]{3}$")) {
                menu.hide();
                return;
            }

            if (query.length() < 2) {
                menu.hide();
                return;
            }

            List<Pais> encontrados = buscarPaises(query);

            if (encontrados.isEmpty()) {
                menu.hide();
                return;
            }

            List<CustomMenuItem> items = new ArrayList<>();
            int limit = Math.min(12, encontrados.size());

            for (int i = 0; i < limit; i++) {
                Pais p = encontrados.get(i);

                String nombreBonito = p.nombre.split("/")[0].trim();

                Label lbl = new Label(nombreBonito + "  (" + p.iso3 + ")");
                lbl.setStyle("-fx-padding: 4 8 4 8;");

                CustomMenuItem it = new CustomMenuItem(lbl, true);
                it.setOnAction(ev -> {
                    tf.setText(p.iso3);
                    marcarError(tf, false);
                    menu.hide();

                    if (tf == tfPais) {
                        aplicarReglaMunicipio();
                        validarMunicipioSegunPaisSoloCampos();
                        refrescarLista();
                    }
                });

                items.add(it);
            }

            menu.getItems().setAll(items);

            if (!menu.isShowing()) {
                menu.show(tf, Side.BOTTOM, 0, 0);
            }
        });

        tf.focusedProperty().addListener((o, was, isNow) -> {
            if (!isNow) {
                menu.hide();

                // 👇 al perder foco: si escribió un nombre, lo intentamos convertir a ISO3
                String iso = resolverIso3DesdeTexto(tf.getText());
                if (iso != null) {
                    tf.setText(iso);
                    marcarError(tf, false);

                    if (tf == tfPais) {
                        aplicarReglaMunicipio();
                        validarMunicipioSegunPaisSoloCampos();
                        refrescarLista();
                    }
                }
            }
        });
    }
    
    private List<Pais> buscarPaises(String queryNorm) {
        List<Pais> empieza = new ArrayList<>();
        List<Pais> contiene = new ArrayList<>();

        for (Pais p : listaPaises) {

            // matchear también por ISO2 e ISO3
            String iso3n = normalizar(p.iso3);
            String iso2n = normalizar(p.iso2);

            boolean match = false;
            boolean start = false;

            if (iso3n.startsWith(queryNorm) || iso2n.startsWith(queryNorm)) {
                match = true;
                start = true;
            } else if (iso3n.contains(queryNorm) || iso2n.contains(queryNorm)) {
                match = true;
            }

            // matchear por nombre y variantes
            if (!match) {
                String[] variantes = p.nombre.split("/");
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
            }

            if (start) empieza.add(p);
            else if (match) contiene.add(p);
        }

        // Orden: empieza > contiene
        List<Pais> res = new ArrayList<>(empieza);
        res.addAll(contiene);

        // (Opcional) quitar duplicados por ISO3, por si tu CSV mete repetidos
        Map<String, Pais> uniq = new LinkedHashMap<>();
        for (Pais p : res) uniq.putIfAbsent(p.iso3, p);

        return new ArrayList<>(uniq.values());
    }
    
    private String resolverIso3DesdeTexto(String texto) {
        if (texto == null) return null;

        String t = texto.trim();
        if (t.isBlank()) return null;

        String tn = normalizar(t);

        // si ya es ISO3
        if (tn.matches("^[A-Z]{3}$")) return tn;

        // si es ISO2
        if (tn.matches("^[A-Z]{2}$")) {
            for (Pais p : listaPaises) {
                if (normalizar(p.iso2).equals(tn)) return p.iso3;
            }
        }

        // match exacto por nombre (o variantes separadas por "/")
        for (Pais p : listaPaises) {
            String[] variantes = p.nombre.split("/");
            for (String v : variantes) {
                if (normalizar(v).equals(tn)) {
                    return p.iso3;
                }
            }
        }

        // si no hay match exacto, no inventamos
        return null;
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

        // 1️⃣ Intentamos formatos con año 4 dígitos primero
        for (DateTimeFormatter f : FORMATOS_ACEPTADOS) {
            try {
                return LocalDate.parse(t, f);
            } catch (Exception ignored) {}
        }

        // 2️⃣ Intentamos formatos con año 2 dígitos (controlando nosotros el siglo)
        try {
            String t2 = t.replace('-', '/');

            // dd/MM/yy o d/M/yy
            if (t2.matches("^\\d{1,2}/\\d{1,2}/\\d{2}$")) {
                String[] parts = t2.split("/");
                int dia = Integer.parseInt(parts[0]);
                int mes = Integer.parseInt(parts[1]);
                int yy  = Integer.parseInt(parts[2]);

                // Regla práctica:
                // si yy <= (añoActual % 100) -> 20yy
                // si yy >  (añoActual % 100) -> 19yy
                int now2 = LocalDate.now().getYear() % 100;
                int year = (yy <= now2) ? (2000 + yy) : (1900 + yy);

                return LocalDate.of(year, mes, dia);
            }
        } catch (Exception ignored) {}

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
        	estanciaActual = EstanciaService.buscarPorPeregrinoYFecha(p.getIdPeregrino(), fechaLista);

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
    
    private Pais buscarPaisExactoPorNombreOIso(String texto) {
        String q = normalizar(texto);
        if (q.isBlank()) return null;

        for (Pais p : listaPaises) {
            if (normalizar(p.iso3).equals(q) || normalizar(p.iso2).equals(q)) return p;

            for (String v : p.nombre.split("/")) {
                if (normalizar(v).equals(q)) return p;
            }
        }
        return null;
    }
    
    private Pais buscarPaisMejorCoincidencia(String texto) {
        String q = normalizar(texto);
        if (q.isBlank()) return null;

        // 🔹 Caso especial clave:
        // Si el usuario escribe más de 3 letras y empieza por un ISO3,
        // lo convertimos directamente (ESPA -> ESP, PORT -> PRT, etc.)
        if (q.length() > 3) {
            for (Pais p : listaPaises) {
                if (q.startsWith(normalizar(p.iso3))) {
                    return p;
                }
            }
        }

        // 1) exacto por ISO o nombre
        Pais exacto = buscarPaisExactoPorNombreOIso(texto);
        if (exacto != null) return exacto;

        // 2) empieza por (en nombre/variantes)
        for (Pais p : listaPaises) {
            if (normalizar(p.iso3).startsWith(q) || normalizar(p.iso2).startsWith(q)) return p;

            for (String v : p.nombre.split("/")) {
                if (normalizar(v).startsWith(q)) return p;
            }
        }

        // 3) contiene (último recurso)
        for (Pais p : listaPaises) {
            if (normalizar(p.iso3).contains(q) || normalizar(p.iso2).contains(q)) return p;

            for (String v : p.nombre.split("/")) {
                if (normalizar(v).contains(q)) return p;
            }
        }

        return null;
    }
    
    private boolean coincideBusqueda(Peregrino p, String filtro) {
        String texto = (
                safe(p.getNombre()) + " " +
                safe(p.getApellido1()) + " " +
                safe(p.getApellido2()) + " " +
                safe(p.getTipoDocumento()) + " " +
                safe(p.getNumeroDocumento())
        ).toUpperCase();

        return texto.contains(filtro);
    }
    
    @FXML
    private void onDiaAnterior() {
        fechaLista = fechaLista.minusDays(1);
        dpFechaLista.setValue(fechaLista);
    }

    @FXML
    private void onDiaSiguiente() {
        fechaLista = fechaLista.plusDays(1);
        dpFechaLista.setValue(fechaLista);
    }
    
    private boolean esEntradaHoy(Peregrino p) {
        try {
            Estancia e = EstanciaService.buscarPorPeregrinoYFecha(p.getIdPeregrino(), fechaLista);
            if (e == null) return false;

            return fechaLista.toString().equals(e.getFechaEntrada());
        } catch (Exception ex) {
            return false;
        }
    }
    
    private void actualizarModoBusqueda() {
        boolean buscando = !trim(tfBuscarHuesped).isBlank();

        btnDiaAnterior.setDisable(buscando);
        dpFechaLista.setDisable(buscando);
        btnDiaSiguiente.setDisable(buscando);

        // opcional: dar sensación visual de "apagado"
        btnDiaAnterior.setOpacity(buscando ? 0.5 : 1.0);
        dpFechaLista.setOpacity(buscando ? 0.5 : 1.0);
        btnDiaSiguiente.setOpacity(buscando ? 0.5 : 1.0);
    }
    
    private void intentarAutorrellenarPorDocumento() {

        String tipo = trim(tfTipoDocumento).toUpperCase();
        String numero = trim(tfNumeroDocumento).toUpperCase();

        if (tipo.isBlank() || numero.isBlank()) return;

        try {
            Peregrino existente = PeregrinoService.obtenerPorDocumento(tipo, numero);

            if (existente == null) {
                bloquearDocumentoSiExistente(false);
                return;
            }

            // Si ya estamos editando ese mismo peregrino, no hacemos nada extra
            if (actual != null && actual.getIdPeregrino() == existente.getIdPeregrino()) {
                bloquearDocumentoSiExistente(true);
                return;
            }

            actual = existente;

            tfNombre.setText(safe(existente.getNombre()));
            tfApellido1.setText(safe(existente.getApellido1()));
            tfApellido2.setText(safe(existente.getApellido2()));
            tfFechaNacimiento.setText(fechaEsDesdeIso(existente.getFechaNacimiento()));
            tfSexo.setText(safe(existente.getSexo()));
            tfNacionalidad.setText(safe(existente.getNacionalidad()));
            tfPais.setText(safe(existente.getPais()));
            tfCodigoPostal.setText(safe(existente.getCodigoPostal()));
            tfDireccion.setText(safe(existente.getDireccion()));
            tfDireccionComplementaria.setText(safe(existente.getDireccionComplementaria()));
            tfCodigoMunicipio.setText(safe(existente.getCodigoMunicipio()));
            tfNombreMunicipio.setText(safe(existente.getNombreMunicipio()));
            tfTelefono.setText(safe(existente.getTelefono1()));
            tfTelefono2.setText(safe(existente.getTelefono2()));
            tfCorreo.setText(safe(existente.getCorreo()));
            tfParentesco.setText(safe(existente.getParentesco()));

            aplicarReglaMunicipio();
            bloquearDocumentoSiExistente(true);
            marcarDocumentoComoAutorrellenado();

        } catch (DatabaseException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void marcarDocumentoComoAutorrellenado() {
        String estiloOk = "-fx-background-color: #dff3e3; -fx-border-color: #1f6f3e;";

        tfTipoDocumento.setStyle(estiloOk);
        tfNumeroDocumento.setStyle(estiloOk);

        PauseTransition pausa = new PauseTransition(javafx.util.Duration.seconds(1.2));
        pausa.setOnFinished(e -> {
            tfTipoDocumento.setStyle("");
            tfNumeroDocumento.setStyle("");
        });
        pausa.play();
    }

    private void bloquearDocumentoSiExistente(boolean bloquear) {
        tfTipoDocumento.setEditable(!bloquear);
        tfNumeroDocumento.setEditable(!bloquear);

        tfTipoDocumento.setFocusTraversable(!bloquear);
        tfNumeroDocumento.setFocusTraversable(!bloquear);
    }
    
    private void refrescarIndicadorPlazas() {
        try {
            int ocupadas = EstanciaService.contarPlazasOcupadasEnFecha(1, fechaLista);
            int totales = CamaService.contarCapacidadTotal();

            lblPlazas.setText("Plazas ocupadas: " + ocupadas + " / " + totales);

            if (totales > 0 && ocupadas >= totales) {
                lblPlazas.setStyle("-fx-text-fill: #b22222;");
            } else if (totales > 0 && ocupadas >= Math.max(1, totales - 3)) {
                lblPlazas.setStyle("-fx-text-fill: #d97917;");
            } else {
                lblPlazas.setStyle("");
            }

        } catch (DatabaseException e) {
            lblPlazas.setText("Plazas ocupadas: -- / --");
            lblPlazas.setStyle("-fx-text-fill: #d97917;");
        }
    }
    
    private boolean esEstanciaNueva() {
        return estanciaActual == null || estanciaActual.getIdEstancia() == 0;
    }
    
    @FXML
    private void onEditarAlbergue() {
        try {
            Albergue actual = AlbergueService.obtenerAlbergue();

            if (actual == null) {
                System.out.println("No existe ningún albergue para editar.");
                return;
            }

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/ui/nuevo_albergue.fxml")
            );

            javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());

            NuevoAlbergueController controller = loader.getController();
            controller.cargarModoEdicion(actual);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Editar albergue");
            stage.setScene(scene);
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void onEliminarAlbergueActual() {
        try {
            Albergue actual = AlbergueService.obtenerAlbergue();

            if (actual == null) {
                System.out.println("No existe ningún albergue para eliminar.");
                return;
            }

            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
            alert.setTitle("Reestablecer albergue");
            alert.setHeaderText("¿Seguro que quieres eliminar la configuración del albergue actual?");
            alert.setContentText("Esta acción vaciará los datos del albergue actual, pero no eliminará peregrinos, estancias ni camas.");

            javafx.scene.control.ButtonType btnNoEliminar =
                    new javafx.scene.control.ButtonType("No reestablecer", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);

            javafx.scene.control.ButtonType btnEliminar =
                    new javafx.scene.control.ButtonType("Restablecer albergue actual", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);

            alert.getButtonTypes().setAll(btnNoEliminar, btnEliminar);

            javafx.scene.control.Button botonVerde = (javafx.scene.control.Button) alert.getDialogPane().lookupButton(btnNoEliminar);
            javafx.scene.control.Button botonNaranja = (javafx.scene.control.Button) alert.getDialogPane().lookupButton(btnEliminar);

            botonVerde.setStyle("-fx-background-color: #71f4b9; -fx-text-fill: black;");
            botonNaranja.setStyle("-fx-background-color: #ffb671; -fx-text-fill: black;");

            java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == btnEliminar) {
            	AlbergueService.resetearAlbergueActual();
            	System.out.println("Datos del albergue actual reseteados correctamente.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void cargarHabitacionYCamaEnFicha() {

        tfNumeroHabitacion.setText("");
        tfNumeroCama.setText("");

        if (estanciaActual == null || estanciaActual.getIdCama() == null) {
            return;
        }

        try {
            model.Cama cama = CamaService.obtenerPorId(estanciaActual.getIdCama());
            if (cama == null) return;

            tfNumeroHabitacion.setText(String.valueOf(cama.getNumeroHabitacion()));

            int numeroCama = CamaService.obtenerNumeroCamaDentroDeHabitacion(cama.getIdCama());
            if (numeroCama > 0) {
                tfNumeroCama.setText(String.valueOf(numeroCama));
            }

        } catch (Exception e) {
            System.out.println("No se pudo cargar habitación/cama en ficha: " + e.getMessage());
        }
    }
    
    
 // FUTURO:
 // Cuando existan preregistros desde la nube, el indicador de plazas deberá
 // decidir si los pendientes/restervas descuentan o no aforo según la
 // configuración del albergue (acepta reservas / no acepta reservas).
    
}