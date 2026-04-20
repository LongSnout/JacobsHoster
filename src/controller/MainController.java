package controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;

import exception.DatabaseException;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.Peregrino;
import model.Prerregistro;
import service.PeregrinoService;
import service.PrerregistroService;
import javafx.scene.control.TextArea;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import model.Albergue;
import model.Estancia;
import service.AlbergueService;
import service.CamaService;
import service.EstanciaService;

import javafx.util.Duration;

import model.Producto;
import model.VentaLinea;
import service.ProductoService;
import service.VentaLineaService;
import util.ItemLista;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;

import xml.InformeXMLGenerator;
import xml.InformeXMLRWriter;




public class MainController {

    // ===== Navegación y lista izquierda =====
    @FXML private ListView<ItemLista> lvHuespedes;
    @FXML private Button btnDiaAnterior;
    @FXML private Button btnDiaSiguiente;
    @FXML private javafx.scene.control.DatePicker dpFechaLista;
    @FXML private TextField tfBuscarHuesped;
    @FXML private Label lblPlazas;

    // ===== Botones de acción =====
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    @FXML private Button btnCopiarDatosPrevios;
    @FXML private javafx.scene.control.ScrollPane spFicha;

    // ===== Datos del huésped =====
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
    @FXML private TextField tfRol;
    @FXML private TextField tfSoporteDocumento;

    // ===== Datos de la estancia =====
    @FXML private TextField tfReferencia;
    @FXML private TextField tfFechaEntrada;
    @FXML private TextField tfFechaSalida;
    @FXML private TextField tfFechaContrato;
    @FXML private TextField tfNumHabitaciones;
    @FXML private TextField tfNumPersonas;
    @FXML private TextField tfNumeroHabitacion;
    @FXML private TextField tfNumeroCama;
    @FXML private TextField tfLugarInicioCamino;
    @FXML private TextField tfUltimoAlbergue;
    @FXML private TextField tfCaminoDestino;
    @FXML private javafx.scene.control.CheckBox cbInternet;
    @FXML private TextArea taObservaciones;

    // ===== Datos de pago =====
    @FXML private TextField tfTipoPago;
    @FXML private TextField tfTitular;
    @FXML private TextField tfCaducidadTarjeta;
    @FXML private TextField tfMedioPago;
    @FXML private TextField tfFechaPago;

    // ===== Widget de ventas =====
    @FXML private javafx.scene.layout.VBox vboxProductos;
    @FXML private Label lblTotalVentas;
    private final List<VentaLinea> lineasVentaActuales = new ArrayList<>();
    
 // ===== Pestaña Ventas =====
    @FXML private javafx.scene.control.TabPane tabPane;
    @FXML private DatePicker dpFechaVentas;
    @FXML private Button btnVentasDiaAnterior;
    @FXML private Button btnVentasDiaSiguiente;
    @FXML private ListView<Producto> lvTarifas;
    @FXML private ListView<Producto> lvProductosCatalogo;
    @FXML private TextField tfNombreTarifa;
    @FXML private TextField tfPrecioTarifa;
    @FXML private TextField tfNombreProducto;
    @FXML private TextField tfPrecioProducto;
    @FXML private Label lblResumenEstancias;
    @FXML private Label lblResumenProductos;
    @FXML private Label lblResumenTotal;
    @FXML private javafx.scene.layout.VBox vboxResumenPorTipo;
    
    // ==== Perfiles =====

    @FXML private javafx.scene.control.Menu menuArchivo;
    @FXML private javafx.scene.control.Menu menuEdicion;
    @FXML private javafx.scene.control.Menu menuPerfiles;
    
    
 // ===== Pestaña Informes =====
    @FXML private DatePicker dpIngresosDesde;
    @FXML private DatePicker dpIngresosHasta;
    @FXML private javafx.scene.layout.VBox vboxDesglosIngresos;
    @FXML private Label lblTotalCaja;
    @FXML private DatePicker dpOcupacionDesde;
    @FXML private DatePicker dpOcupacionHasta;
    @FXML private Label lblPeregrinosTotales;
    @FXML private Label lblPlazasTotalesInformes;
    @FXML private Label lblOcupacionMedia;
    @FXML private Label lblDiaMasOcupado;
    @FXML private Label lblAnio;
    @FXML private javafx.scene.control.Button btnAnioAnterior;
    @FXML private javafx.scene.control.Button btnAnioSiguiente;
    @FXML private javafx.scene.chart.PieChart chartSexo;
    @FXML private javafx.scene.chart.BarChart<String, Number> chartPais;
    @FXML private javafx.scene.chart.BarChart<String, Number> chartEdad;
    private int anioActual = java.time.LocalDate.now().getYear();
    
    
    // ===== Estado interno =====
    private Estancia estanciaActual;
    private Peregrino actual;
    private LocalDate fechaLista = LocalDate.now();
    private boolean cargandoFicha = false;
    private String documentoOriginalTipo = "";
    private String documentoOriginalNumero = "";
    private Prerregistro prerregistroActual = null;

    // ===== Datos previos (copiar al siguiente registro) =====
    private String previoNacionalidad      = "";
    private String previoPais              = "";
    private String previoFechaEntrada      = "";
    private String previoFechaSalida       = "";
    private String previoNumHabitaciones   = "";
    private String previoNumPersonas       = "";
    private String previoNumeroHabitacion  = "";
    private String previoLugarInicioCamino = "";
    private String previoUltimoAlbergue    = "";
    private String previoCaminoDestino     = "";
    private String previoTelefono          = "";
    private String previoReferenciaGrupo   = "";

    // ===== Constantes =====
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private static final java.util.Set<String> TIPOS_PAGO_VALIDOS = java.util.Set.of(
            "DESTI", "EFECT", "TARJT", "PLATF", "TRANS", "MOVIL", "TREG", "OTRO"
    );

    private static final java.util.Set<String> PARENTESCOS_VALIDOS = java.util.Set.of(
            "AB", "BA", "BN", "CY", "CD", "HR", "HJ", "PM", "NI", "SB", "SG", "TI", "YN", "TU", "OT"
    );

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
			lvHuespedes.getScene().addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {

				if (event.getCode() == javafx.scene.input.KeyCode.DELETE && lvHuespedes.isFocused()
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

		Platform.runLater(() -> autoResizeTextArea(taObservaciones));

		Platform.runLater(() -> inicializarWidgetVentas());
		
		// Sincronizar fecha ventas con fecha lista
		dpFechaVentas.setValue(fechaLista);
		dpFechaVentas.valueProperty().addListener((obs, oldVal, newVal) -> {
		    if (newVal != null) {
		        fechaLista = newVal;
		        dpFechaLista.setValue(fechaLista);
		        refrescarLista();
		        actualizarResumenVentas();
		    }
		});

		// Cargar catálogo al arrancar
		Platform.runLater(() -> {
		    cargarCatalogoVentas();
		    actualizarResumenVentas();
		});
		
		// Actualizar pestaña Ventas al entrar en ella
		tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
		    if (newTab != null && "Ventas".equals(newTab.getText())) {
		        dpFechaVentas.setValue(fechaLista);
		        cargarCatalogoVentas();
		        actualizarResumenVentas();
		    }
		});
		
		aplicarRestriccionesPorRol();
		cargarGruposExistentes();
		activarAutocompletarGrupo();
		
		
		// Inicializar fechas de informes
		LocalDate hoy = LocalDate.now();
		LocalDate inicioMes = hoy.withDayOfMonth(1);
		dpIngresosDesde.setValue(inicioMes);
		dpIngresosHasta.setValue(hoy);
		dpOcupacionDesde.setValue(inicioMes);
		dpOcupacionHasta.setValue(hoy);
		lblAnio.setText(String.valueOf(anioActual));
		
		
		// Iniciar servicio de sincronización de prerregistros
		sync.PrerregistroSyncService.iniciar(this::refrescarLista);

	}

	@FXML
	private void onCopiarDatosPrevios() {
		
		

		boolean noHayNada = previoNacionalidad.isBlank() && previoPais.isBlank() && previoFechaEntrada.isBlank()
				&& previoFechaSalida.isBlank() && previoNumHabitaciones.isBlank() && previoNumPersonas.isBlank()
				&& previoNumeroHabitacion.isBlank() && previoLugarInicioCamino.isBlank()
				&& previoUltimoAlbergue.isBlank() && previoCaminoDestino.isBlank() && previoTelefono.isBlank();

		if (noHayNada) {
			return;
		}
		
		tfNacionalidad.setText(previoNacionalidad);
		tfPais.setText(previoPais);
		tfFechaEntrada.setText(previoFechaEntrada);
		tfFechaSalida.setText(previoFechaSalida);
		tfNumHabitaciones.setText(previoNumHabitaciones);
		tfNumPersonas.setText(previoNumPersonas);
		tfNumeroHabitacion.setText(previoNumeroHabitacion);
		tfLugarInicioCamino.setText(previoLugarInicioCamino);
		tfUltimoAlbergue.setText(previoUltimoAlbergue);
		tfCaminoDestino.setText(previoCaminoDestino);
		tfTelefono.setText(previoTelefono);
		tfNumPersonas.setText(previoReferenciaGrupo);

		aplicarReglaMunicipio();

		marcarError(tfNacionalidad, false);
		marcarError(tfPais, false);
		marcarError(tfCodigoMunicipio, false);
		marcarError(tfNombreMunicipio, false);

		Platform.runLater(() -> tfTipoDocumento.requestFocus());
	}

	// --------------------------
	// Navegación / carga / lista
	// --------------------------

	private void configurarListView() {
	    lvHuespedes.setCellFactory(lv -> new ListCell<>() {
	        @Override
	        protected void updateItem(ItemLista item, boolean empty) {
	            super.updateItem(item, empty);

	            if (empty || item == null) {
	                setText(null);
	                setGraphic(null);
	                setStyle("");
	                return;
	            }

	            String label;
	            boolean invalido = false;
	            boolean nuevoHoy = false;
	            boolean esPrerregistro = item.esPrerregistro();

	            if (esPrerregistro) {
	                Prerregistro pr = item.getPrerregistro();
	                String nombre = safe(pr.getNombre());
	                String a1 = safe(pr.getApellido1());
	                String doc = (safe(pr.getTipoDocumento()) + " " + safe(pr.getNumeroDocumento())).trim();
	                label = (nombre + " " + a1).trim();
	                if (!doc.isBlank()) label += "  (" + doc + ")";
	            } else {
	                Peregrino p = item.getPeregrino();
	                String nombre = safe(p.getNombre());
	                String a1 = safe(p.getApellido1());
	                String doc = (safe(p.getTipoDocumento()) + " " + safe(p.getNumeroDocumento())).trim();
	                label = (nombre + " " + a1).trim();
	                if (!doc.isBlank()) label += "  (" + doc + ")";
	                if (label.isBlank()) label = "ID " + p.getIdPeregrino();
	                invalido = peregrinoTieneErrores(p);
	                nuevoHoy = esEntradaHoy(p);
	            }

	            Text punto = new Text();
	            Text texto = new Text(label);

	            if (esPrerregistro) {
	                punto.setText("● ");
	                punto.setStyle("-fx-fill: #c9a800;"); // amarillo
	                texto.setStyle("-fx-fill: #888888;"); // gris
	            } else if (nuevoHoy) {
	                punto.setText("● ");
	                punto.setStyle("-fx-fill: #1f6f3e;"); // verde
	                texto.setStyle("-fx-fill: -fx-text-inner-color;");
	            } else {
	                punto.setText("");
	                if (invalido) {
	                    texto.setStyle("-fx-fill: #d97917;");
	                } else {
	                    texto.setStyle("-fx-fill: -fx-text-inner-color;");
	                }
	            }

	            TextFlow flow = new TextFlow(punto, texto);
	            setText(null);
	            setGraphic(flow);
	            setStyle("");
	        }
	    });
	}



	private void cargarEnFicha(ItemLista item) {
	    if (item.esPrerregistro()) {
	        cargarEnFichaDesdePrerregistro(item.getPrerregistro());
	    } else {
	        cargarEnFichaDesdePeregrino(item.getPeregrino());
	    }
	}

	private void cargarEnFichaDesdePeregrino(Peregrino p) {
	    cargandoFicha = true;
	    try {
	        actual = p;
	        prerregistroActual = null;

	        documentoOriginalTipo = safe(p.getTipoDocumento());
	        documentoOriginalNumero = safe(p.getNumeroDocumento());
	        tfTipoDocumento.setText(safe(p.getTipoDocumento()));
	        tfNumeroDocumento.setText(safe(p.getNumeroDocumento()));
	        tfNombre.setText(safe(p.getNombre()));
	        tfApellido1.setText(safe(p.getApellido1()));
	        tfApellido2.setText(safe(p.getApellido2()));
	        tfFechaNacimiento.setText(fechaEsDesdeIso(p.getFechaNacimiento()));
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
	        tfSoporteDocumento.setText(safe(p.getSoporteDocumento()));
	        tfRol.setText(safe(p.getRol()));

	        aplicarReglaMunicipio();
	        cargarEstanciaDe(p);
	        cargarHabitacionYCamaEnFicha();
	        marcarError(tfCodigoMunicipio, false);
	        marcarError(tfNombreMunicipio, false);

	    } finally {
	        cargandoFicha = false;
	    }
	}

	private void cargarEnFichaDesdePrerregistro(Prerregistro pr) {
	    cargandoFicha = true;
	    try {
	        actual = null;
	        prerregistroActual = pr;

	        documentoOriginalTipo = safe(pr.getTipoDocumento());
	        documentoOriginalNumero = safe(pr.getNumeroDocumento());
	        tfTipoDocumento.setText(safe(pr.getTipoDocumento()));
	        tfNumeroDocumento.setText(safe(pr.getNumeroDocumento()));
	        tfNombre.setText(safe(pr.getNombre()));
	        tfApellido1.setText(safe(pr.getApellido1()));
	        tfApellido2.setText(safe(pr.getApellido2()));
	        tfFechaNacimiento.setText(fechaEsDesdeIso(pr.getFechaNacimiento()));
	        tfSexo.setText(safe(pr.getSexo()));
	        tfNacionalidad.setText(safe(pr.getNacionalidad()));
	        tfPais.setText(safe(pr.getPais()));
	        tfCodigoPostal.setText(safe(pr.getCodigoPostal()));
	        tfDireccion.setText(safe(pr.getDireccion()));
	        tfDireccionComplementaria.setText(safe(pr.getDireccionComplementaria()));
	        tfCodigoMunicipio.setText(safe(pr.getCodigoMunicipio()));
	        tfNombreMunicipio.setText(safe(pr.getNombreMunicipio()));
	        tfTelefono.setText(safe(pr.getTelefono1()));
	        tfTelefono2.setText(safe(pr.getTelefono2()));
	        tfCorreo.setText(safe(pr.getCorreo()));
	        tfParentesco.setText(safe(pr.getParentesco()));
	        tfRol.setText(safe(pr.getRol()));
	        tfSoporteDocumento.setText(""); // no viene en prerregistro

	        aplicarReglaMunicipio();
	        marcarError(tfCodigoMunicipio, false);
	        marcarError(tfNombreMunicipio, false);

	    } finally {
	        cargandoFicha = false;
	    }
	}



	// --------------------------
	// Botones
	// --------------------------

	@FXML
	private void onGuardarFicha() {

		// Normalizar:
		tfTipoDocumento.setText(trim(tfTipoDocumento).toUpperCase());
		tfNumeroDocumento.setText(trim(tfNumeroDocumento).toUpperCase());
		tfSexo.setText(trim(tfSexo).toUpperCase());
		tfNacionalidad.setText(trim(tfNacionalidad).toUpperCase());
		tfPais.setText(trim(tfPais).toUpperCase());

		// Normaliza fechas (dd/MM/yyyy en pantalla)
		normalizarCampoFecha(tfFechaEntrada);
		normalizarCampoFecha(tfFechaSalida);
		normalizarCampoFecha(tfFechaNacimiento);
		normalizarCampoFecha(tfFechaPago);
		normalizarCampoFecha(tfFechaContrato);
		normalizarCampoFecha(tfFechaPago);
		
		tfRol.setText(trim(tfRol).toUpperCase());
		tfParentesco.setText(trim(tfParentesco).toUpperCase());

		LocalDate fn = parseFechaFlexible(tfFechaNacimiento.getText());
		if (fn != null && fn.isAfter(LocalDate.now())) {
			marcarError(tfFechaNacimiento, true);
		} else {
			marcarError(tfFechaNacimiento, false);
		}

		// validaciones suaves (solo pinta naranja)
		validarTipoDocumento();
		validarNumeroDocumentoConTipo();
		validarSexo();
		validarIso3(tfNacionalidad);
		validarIso3(tfPais);
		validarMunicipioSegunPaisSoloCampos();
		validarTipoPago();
		validarParentesco();
		validarCaducidadTarjeta();
		validarRol();

		// Bloqueo por aforo, solo para estancias nuevas
		LocalDate fechaControl = parseFechaFlexible(tfFechaEntrada.getText());
		if (fechaControl == null) {
			fechaControl = LocalDate.now();
		}
		
		if (esEstanciaNueva()) {
			int ocupadas = EstanciaService.contarPlazasOcupadasEnFecha(1, fechaControl);
			int totales = CamaService.contarCapacidadTotal();

			if (totales > 0 && ocupadas >= totales) {
				System.out.println(
						"Aforo completo para la fecha " + fechaControl + ": no se pueden añadir más huéspedes.");
				return;
			}
		}

		try {
			
			// Si cambió el documento, eliminar el peregrino anterior
			String tipoActual = trim(tfTipoDocumento).toUpperCase();
			String numeroActual = trim(tfNumeroDocumento).toUpperCase();

			boolean documentoCambiado = !documentoOriginalTipo.isBlank()
			        && (!tipoActual.equals(documentoOriginalTipo)
			                || !numeroActual.equals(documentoOriginalNumero));

			if (documentoCambiado && actual != null && actual.getIdPeregrino() != 0) {
			    try {
			        PeregrinoService.eliminar(actual.getIdPeregrino());
			        actual = new Peregrino();
			        estanciaActual = new Estancia();
			        estanciaActual.setIdAlbergue(1);
			        estanciaActual.setEstadoEstancia("ACTIVA");
			    } catch (DatabaseException e) {
			        System.out.println("Error al eliminar peregrino anterior: " + e.getMessage());
			        return;
			    }
			}
			
			
			// Volcar datos del formulario al modelo
			volcarDeFichaAActual();

			// Guardar peregrino (para asegurar id real)
			PeregrinoService.guardar(actual);

			// Enlazar estancia al peregrino recién guardado
			if (estanciaActual == null) {
				estanciaActual = new Estancia();
			}

			estanciaActual.setIdPeregrino(actual.getIdPeregrino());

			// Valore mínimos para evitar NOT NULL en BD
			if (estanciaActual.getIdAlbergue() == 0) {
				estanciaActual.setIdAlbergue(1);
			}

			if (estanciaActual.getEstadoEstancia() == null || estanciaActual.getEstadoEstancia().isBlank()) {
				estanciaActual.setEstadoEstancia("ACTIVA");
			}

			if (estanciaActual.getNumeroHabitaciones() == 0) {
				estanciaActual.setNumeroHabitaciones(1);
			}
			
			
			

			// Guardar estancia (insert o update automático)
			
			if (estanciaActual.getIdCama() != null) {
			    boolean disponible = camaDisponible(
			        estanciaActual.getIdCama(),
			        estanciaActual.getFechaEntrada(),
			        estanciaActual.getFechaSalidaPrevista(),
			        estanciaActual.getIdEstancia()
			    );
			    if (!disponible) {
			        mostrarAlerta("Cama ocupada",
			            "La cama asignada ya está ocupada en esas fechas.\n" +
			            "Cambia la cama o deja los campos de habitación y cama vacíos.");
			        return;
			    }
			}
			
			EstanciaService.guardar(estanciaActual);
			
			// Si venía de un prerregistro, marcarlo como ACEPTADO
			if (prerregistroActual != null) {
			    try {
			        PrerregistroService.marcarAceptado(
			            prerregistroActual.getIdPrerregistro()
			        );
			    } catch (Exception e) {
			        System.out.println("No se pudo marcar prerregistro como aceptado: " + e.getMessage());
			    }
			    prerregistroActual = null;
			}

			// Guardar líneas de venta
			VentaLineaService.guardarLineas(estanciaActual.getIdEstancia(), lineasVentaActuales);

			// Resetear widget ventas al estado por defecto
			lineasVentaActuales.clear();
			try {
			    List<Producto> tarifas = ProductoService.listarActivos().stream()
			            .filter(Producto::isEsEstancia).toList();
			    if (!tarifas.isEmpty()) {
			        Producto tarifa = tarifas.get(0);
			        VentaLinea vl = new VentaLinea();
			        vl.setIdProducto(tarifa.getIdProducto());
			        vl.setNombreSnapshot(tarifa.getNombre());
			        vl.setPrecioUnitario(tarifa.getPrecio());
			        vl.setCantidad(1);
			        lineasVentaActuales.add(vl);
			    }
			} catch (Exception e) {
			    System.out.println("No se pudo resetear widget ventas: " + e.getMessage());
			}
			recargarCatalogoProductos();
			
			// Guardar datos previos para posible copia en nuevo registro
			guardarDatosPreviosDesdeFicha();

			// Refrescar UI
			refrescarLista();
			nuevaFicha();

		} catch (DatabaseException e) {
		    System.out.println(e.getMessage());
		    e.printStackTrace();

		    String causaRaiz = e.getCause() != null ? e.getCause().getMessage() : "";

		    String mensajeUsuario;
		    if (causaRaiz.contains("solapa")) {
		        mensajeUsuario = "No se pudo guardar la ficha.\n\n" +
		                "La cama asignada ya está ocupada en esas fechas.\n" +
		                "Cambia la cama o deja los campos de habitación y cama vacíos.";
		    } else if (causaRaiz.contains("UNIQUE")) {
		        mensajeUsuario = "No se pudo guardar la ficha.\n\n" +
		                "Ya existe un registro con ese documento de identidad.";
		    } else {
		        mensajeUsuario = "No se pudo guardar la ficha.\n\n" + e.getMessage();
		    }

		    mostrarAlerta("Error al guardar", mensajeUsuario);
		}
	}
	
	
	private void volcarDeFichaAActual() {
		if (actual == null)
			actual = new Peregrino();

		String rol = trim(tfRol).toUpperCase();
		if (rol.isBlank())
			rol = "VI";
		actual.setRol(rol);

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
		actual.setSoporteDocumento(trim(tfSoporteDocumento));

		// ---- Estancia ----
		if (estanciaActual == null)
			estanciaActual = new Estancia();

		estanciaActual.setIdAlbergue(1);
		estanciaActual.setLugarInicioCamino(trim(tfLugarInicioCamino));
		estanciaActual.setUltimoAlbergue(trim(tfUltimoAlbergue));
		estanciaActual.setCaminoDestino(trim(tfCaminoDestino));
		estanciaActual.setObservaciones(trim(taObservaciones));

		String fechaContrato = fechaIsoDesdeCampo(tfFechaContrato);
		if (fechaContrato.isBlank()) {
			fechaContrato = LocalDate.now().toString();
			tfFechaContrato.setText(LocalDate.now().format(FECHA_ES));
		}
		estanciaActual.setFechaContrato(fechaContrato);

		estanciaActual.setMedioPago(trim(tfMedioPago));

		try {
			int nh = Integer.parseInt(trim(tfNumHabitaciones));
			estanciaActual.setNumeroHabitaciones(nh);
		} catch (Exception e) {
			estanciaActual.setNumeroHabitaciones(1);
		}

		estanciaActual.setInternetIncluido(cbInternet.isSelected());

		estanciaActual.setNumPersonasContrato(null);

		estanciaActual.setTipoPago(trim(tfTipoPago));

		String titular = trim(tfTitular);
		if (titular.isBlank()) {
			titular = construirNombreCompletoHuesped();
			tfTitular.setText(titular);
		}

		estanciaActual.setTitularPago(titular);
		estanciaActual.setCaducidadTarjeta(trim(tfCaducidadTarjeta));
		estanciaActual.setFechaPago(fechaIsoDesdeCampo(tfFechaPago));

		// referencia (nunca vacía)
		String ref = trim(tfReferencia);
		if (ref.isBlank()) {
			ref = generarReferenciaEstancia();
			tfReferencia.setText(ref);
		}
		estanciaActual.setReferenciaContrato(ref);

		// fechas ISO desde campos (pueden venir vacías)
		String fe = fechaIsoDesdeCampo(tfFechaEntrada); // yyyy-MM-dd o ""
		String fs = fechaIsoDesdeCampo(tfFechaSalida); // yyyy-MM-dd o ""

		// ENTRADA
		if (fe.isBlank()) {
			LocalDate hoy = LocalDate.now();
			tfFechaEntrada.setText(hoy.format(FECHA_ES));
			fe = hoy.toString(); // ISO
		}

		// SALIDA
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

		// Guardado a modelo (entrada siempre con valor, salida puede ser NULL)
		estanciaActual.setFechaEntrada(fe);
		estanciaActual.setFechaSalidaPrevista(fs.isBlank() ? null : fs);

		// defaults mínimos
		if (estanciaActual.getEstadoEstancia() == null || estanciaActual.getEstadoEstancia().isBlank()) {
			estanciaActual.setEstadoEstancia("ACTIVA");
		}
		if (estanciaActual.getNumeroHabitaciones() == 0) {
			estanciaActual.setNumeroHabitaciones(1);
		}

		// Gestión de asignación de cama/habitación
		String txtHab = trim(tfNumeroHabitacion);
		String txtCama = trim(tfNumeroCama);

		// Caso 1: ambos vacíos, conservar cama actual si ya existía
		if (txtHab.isBlank() && txtCama.isBlank()) {
			/* No tocamos estanciaActual.idCama
			FUTURO:
			 TODO:
			Si la estancia es nueva y no tiene cama asignada, aquí entrará la lógica de
			autoasignación.
			Criterios previstos:
			no asignar camas inexistentes
			evitar mezclar hombres con habitación de mujeres
			priorizar grupos juntos
			intentar llenar habitaciones completas si conviene
			opcionalmente minimizar número de habitaciones abiertas*/
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
					// Habitación/cama no válida: de momento no machaca la cama existente
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
		cargandoFicha = true;

		try {
			actual = new Peregrino();
			
			//
			documentoOriginalTipo = "";
			documentoOriginalNumero = "";
			
			actual.setRol("VI");

			estanciaActual = new Estancia();
			estanciaActual.setIdAlbergue(1);
			estanciaActual.setEstadoEstancia("ACTIVA");
			estanciaActual.setNumeroHabitaciones(1);

			estanciaActual.setFechaContrato(LocalDate.now().toString());
			estanciaActual.setTipoPago("EFECT");
			estanciaActual.setMedioPago("EFECT");

			bloquearDocumentoSiExistente(false);

			lvHuespedes.getSelectionModel().clearSelection();

			tfTipoDocumento.setText("NIF");
			tfSexo.setText("H");
			tfNacionalidad.setText("ESP");
			tfPais.setText("ESP");

			String ref = generarReferenciaEstancia();
			tfReferencia.setText(ref);

			LocalDate hoy = LocalDate.now();
			tfFechaEntrada.setText(hoy.format(FECHA_ES));
			tfFechaSalida.setText(hoy.plusDays(1).format(FECHA_ES));

			estanciaActual.setFechaEntrada(hoy.toString());
			estanciaActual.setFechaSalidaPrevista(hoy.plusDays(1).toString());

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
			tfLugarInicioCamino.setText("");
			tfUltimoAlbergue.setText("");
			tfCaminoDestino.setText("");
			tfNumHabitaciones.setText("1");
			cbInternet.setSelected(true);
			tfNumPersonas.setText("");
			tfTipoPago.setText("EFECT");
			tfTitular.setText("");
			tfCaducidadTarjeta.setText("");
			tfFechaPago.setText("");
			tfSoporteDocumento.setText("");
			taObservaciones.setText("");

			tfRol.setText("VI");
			tfFechaContrato.setText(LocalDate.now().format(FECHA_ES));
			tfMedioPago.setText("EFECT");

			aplicarReglaMunicipio();

			marcarError(tfCodigoMunicipio, false);
			marcarError(tfNombreMunicipio, false);
			lineasVentaActuales.clear();
			
			// Añadir línea de estancia por defecto si hay tarifa activa
			try {
			    List<Producto> tarifas = ProductoService.listarActivos().stream()
			            .filter(Producto::isEsEstancia).toList();
			    if (!tarifas.isEmpty()) {
			        Producto tarifa = tarifas.get(0);
			        VentaLinea vl = new VentaLinea();
			        vl.setIdProducto(tarifa.getIdProducto());
			        vl.setNombreSnapshot(tarifa.getNombre());
			        vl.setPrecioUnitario(tarifa.getPrecio());
			        vl.setCantidad(1);
			        lineasVentaActuales.add(vl);
			    }
			} catch (Exception e) {
			    System.out.println("No se pudo cargar tarifa por defecto: " + e.getMessage());
			}
			
			refrescarWidgetVentas();

		} finally {
			cargandoFicha = false;
		}

		Platform.runLater(() -> {
			spFicha.setVvalue(0.0);
			tfTipoDocumento.requestFocus();
		});
	}

	
	@FXML
	private void onExportarXMLMinisterio() {
		
		
		
	    try {
	        Albergue albergue = AlbergueService.obtenerAlbergue();
	        
	        // Bloquear si no hay código de establecimiento configurado
	        String codigoMir = albergue != null ? albergue.getCodigoEstablecimientoMir() : null;
			if (codigoMir == null || codigoMir.isBlank() || codigoMir.equals("SIN_CONFIGURAR")) {
			    mostrarAlerta("Configuración incompleta",
			        "Debe configurar el código de establecimiento del albergue antes de exportar.\n\n" +
			        "Puede hacerlo desde Edición → Editar albergue → campo: 'Código MIR'.");
			    return;
			}
			
			

	        // Solo peregrinos que entraron ese día (no los que siguen presentes de días anteriores)
	        List<Estancia> estancias = new ArrayList<>();
	        List<Peregrino> peregrinos = new ArrayList<>();

	        List<Peregrino> todosDelDia = EstanciaService.listarPeregrinosPresentesPorDia(1, fechaLista);

	        for (Peregrino p : todosDelDia) {
	            Estancia e = EstanciaService.buscarPorPeregrinoYFecha(p.getIdPeregrino(), fechaLista);
	            if (e == null) continue;

	            // Filtrar solo los que entraron ese día
	            if (fechaLista.toString().equals(e.getFechaEntrada())) {
	                estancias.add(e);
	                peregrinos.add(p);
	            }
	        }

	        if (peregrinos.isEmpty()) {
	            mostrarAlerta("Sin datos", "No hay peregrinos con entrada el " + fechaLista + ".");
	            return;
	        }

	        
	        
	        // Validar antes de generar
	        List<String> errores = validarParaXML(peregrinos, estancias);
	        if (!errores.isEmpty()) {
	            String mensajeErrores = "Se encontraron los siguientes problemas:\n\n"
	                    + String.join("\n", errores)
	                    + "\n\nCorrige estos datos antes de exportar.";
	            mostrarAlerta("Datos incompletos o incorrectos", mensajeErrores);
	            return;
	        }
	        
	        
	        
	        String xml = InformeXMLGenerator.generarXMLParteDia(albergue, estancias, peregrinos);

	        String nombreFichero = "parte_ministerio_" + fechaLista + ".xml";
	        String ruta = config.AppConfig.XML_OUTPUT_DIR
	                + java.io.File.separator + nombreFichero;

	        InformeXMLRWriter.guardarEnFichero(xml, ruta);

	        mostrarAlerta("Exportación completada",
	                "XML generado con " + peregrinos.size() + " peregrino(s).\n" +
	                "Fichero: " + nombreFichero + "\n" +
	                "Carpeta: " + config.AppConfig.XML_OUTPUT_DIR);

	    } catch (Exception e) {
	        mostrarAlerta("Error", "No se pudo exportar el XML: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	@FXML
	private void onAjustarCarpetaXML() {
	    javafx.stage.DirectoryChooser chooser = new javafx.stage.DirectoryChooser();
	    chooser.setTitle("Seleccionar carpeta para XMLs del Ministerio");

	    java.io.File dirActual = new java.io.File(config.AppConfig.XML_OUTPUT_DIR);
	    if (dirActual.exists()) chooser.setInitialDirectory(dirActual);

	    javafx.stage.Stage stage = (javafx.stage.Stage) tabPane.getScene().getWindow();
	    java.io.File seleccionada = chooser.showDialog(stage);

	    if (seleccionada != null) {
	        config.AppConfig.XML_OUTPUT_DIR = seleccionada.getAbsolutePath();
	        mostrarAlerta("Carpeta actualizada",
	            "Los XMLs se guardarán en:\n" + config.AppConfig.XML_OUTPUT_DIR);
	    }
	}

	private void mostrarAlerta(String titulo, String mensaje) {
	    javafx.scene.control.Alert alert =
	        new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
	    alert.setTitle(titulo);
	    alert.setHeaderText(null);
	    alert.setContentText(mensaje);
	    alert.showAndWait();
	}
	
	
	
	
	// Genera una referencia única para la estancia.
	private String generarReferenciaEstancia() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
	}

	@FXML
	private void onEliminarFicha() {

	    // Si es un prerregistro pendiente, volver a PENDIENTE (o simplemente limpiar ficha)
	    if (prerregistroActual != null) {
	        // No se borra nada, solo se limpia la ficha
	        prerregistroActual = null;
	        nuevaFicha();
	        return;
	    }

	    if (actual == null || actual.getIdPeregrino() == 0) {
	        nuevaFicha();
	        return;
	    }

	    try {
	        PeregrinoService.eliminar(actual.getIdPeregrino());

	        refrescarLista();
	        nuevaFicha();

	    } catch (DatabaseException e) {
	        System.out.println(e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	private void refrescarLista() {
	    try {
	        String filtro = trim(tfBuscarHuesped);

	        List<ItemLista> items = new java.util.ArrayList<>();

	        if (filtro.isBlank()) {
	            // Peregrinos del día
	            EstanciaService.listarPeregrinosPresentesPorDia(1, fechaLista)
	                .stream()
	                .map(ItemLista::new)
	                .forEach(items::add);

	            // Prerregistros pendientes mezclados
	            PrerregistroService.listarPendientes()
	                .stream()
	                .map(ItemLista::new)
	                .forEach(items::add);

	        } else {
	            PeregrinoService.buscarGlobal(filtro)
	                .stream()
	                .map(ItemLista::new)
	                .forEach(items::add);
	        }

	        lvHuespedes.setItems(FXCollections.observableArrayList(items));

	    } catch (DatabaseException e) {
	        System.out.println(e.getMessage());
	        e.printStackTrace();
	        lvHuespedes.setItems(FXCollections.observableArrayList());
	    }

	    refrescarIndicadorPlazas();
	    cargarGruposExistentes();
	}

	/*
	 * private void refrescarLista() { try { List<Peregrino> lista =
	 * EstanciaService.listarPeregrinosPorDia(1, fechaLista);
	 * 
	 * String filtro = trim(tfBuscarHuesped).toUpperCase();
	 * 
	 * if (!filtro.isBlank()) { lista = lista.stream() .filter(p ->
	 * coincideBusqueda(p, filtro)) .toList(); }
	 * 
	 * lvHuespedes.setItems(FXCollections.observableArrayList(lista));
	 * 
	 * } catch (DatabaseException e) { System.out.println(e.getMessage());
	 * e.printStackTrace();
	 * lvHuespedes.setItems(FXCollections.observableArrayList()); } }
	 */

	// ------------------------------------------------------
	// Validación suave (no bloquea)


	private void instalarValidacionSuave() {

		instalarValidador(tfTipoDocumento, this::validarTipoDocumento);
		instalarValidador(tfNumeroDocumento, this::validarNumeroDocumentoConTipo);
		instalarValidador(tfSexo, this::validarSexo);

		instalarValidador(tfNacionalidad, () -> validarIso3(tfNacionalidad));
		instalarValidador(tfPais, () -> validarIso3(tfPais));

		instalarValidador(tfCodigoMunicipio, this::validarMunicipioSegunPaisSoloCampos);
		instalarValidador(tfNombreMunicipio, this::validarMunicipioSegunPaisSoloCampos);
		instalarValidador(tfTipoPago, this::validarTipoPago);

		instalarValidador(tfParentesco, this::validarParentesco);
		instalarValidador(tfCaducidadTarjeta, this::validarCaducidadTarjeta);
		instalarValidador(tfRol, this::validarRol);

		// País: solo reacciona cuando ya es ISO3 (para no fastidiar el autocompletar)
		tfPais.textProperty().addListener((o, a, b) -> {

			String v = trim(tfPais).toUpperCase();

			if (v.matches("^[A-Z]{3}$")) {
				aplicarReglaMunicipio();
				validarMunicipioSegunPaisSoloCampos();
				refrescarLista();
			}
		});

		// País: al perder foco, si escribieron "ESPAÑA" (o parecido), lo convierte a
		// ISO3
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
					getClass().getResource("/ui/nuevo_albergue.fxml"));

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
		if (tf == null)
			return;

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
		if (numDoc.isBlank())
			return false;

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
		if (cargandoFicha) {
			marcarError(tfCodigoMunicipio, false);
			marcarError(tfNombreMunicipio, false);
			return true;
		}

		String pais = trim(tfPais).toUpperCase();
		boolean esEsp = "ESP".equals(pais);

		if (esEsp) {
			String cod = trim(tfCodigoMunicipio);

			if (cod.isBlank()) {
				marcarError(tfCodigoMunicipio, false);
				marcarError(tfNombreMunicipio, false);
				return true;
			}

			boolean ok = cod.matches("^\\d{5}$");
			marcarError(tfCodigoMunicipio, !ok);
			marcarError(tfNombreMunicipio, false);
			return ok;

		} else {
			String nom = trim(tfNombreMunicipio);

			if (nom.isBlank()) {
				marcarError(tfNombreMunicipio, false);
				marcarError(tfCodigoMunicipio, false);
				return true;
			}

			boolean ok = !nom.isBlank();
			marcarError(tfNombreMunicipio, !ok);
			marcarError(tfCodigoMunicipio, false);
			return ok;
		}
	}

	private void aplicarReglaMunicipio() {
		String pais = trim(tfPais).toUpperCase();
		boolean esEsp = "ESP".equals(pais);

		// Solo ocultamos el campo
		tfCodigoMunicipio.setVisible(esEsp);
		tfCodigoMunicipio.setManaged(esEsp);

		tfNombreMunicipio.setVisible(!esEsp);
		tfNombreMunicipio.setManaged(!esEsp);
	}

	// --------------------------
	// Perfil naranja en lista
	// --------------------------

	private boolean peregrinoTieneErrores(Peregrino p) {

		String tipo = safe(p.getTipoDocumento()).toUpperCase();
		String num = safe(p.getNumeroDocumento()).toUpperCase();
		String sexo = safe(p.getSexo()).toUpperCase();
		String nac = safe(p.getNacionalidad()).toUpperCase();
		String pais = safe(p.getPais()).toUpperCase();

		if (!tipo.matches("^(NIF|NIE|PAS|OTRO|CIF|CIF_E)$"))
			return true;
		if (!validarNumeroDocumento(tipo, num))
			return true;
		if (!sexo.matches("^(H|M|O)$"))
			return true;
		if (!nac.matches("^[A-Z]{3}$"))
			return true;
		if (!pais.matches("^[A-Z]{3}$"))
			return true;

		if ("ESP".equals(pais)) {
			String cod = safe(p.getCodigoMunicipio());
			if (!cod.isBlank() && !cod.matches("^\\d{5}$"))
				return true;
		} else {
			String nom = safe(p.getNombreMunicipio());
			if (nom.isBlank())
				return true;
		}

		return false;
	}

	// ------------------------------------
	// Helpers
	// -----------------------------------

	private void marcarError(TextField tf, boolean error) {
		if (tf == null)
			return;
		tf.getStyleClass().remove("field-error");
		if (error)
			tf.getStyleClass().add("field-error");
	}

	private static String safe(String s) {
		return s == null ? "" : s;
	}

	private static String trim(TextField tf) {
		return tf == null || tf.getText() == null ? "" : tf.getText().trim();
	}

	private static String trim(javafx.scene.control.TextArea ta) {
		return ta == null || ta.getText() == null ? "" : ta.getText().trim();
	}

	// ----------- Municipios (CSV en /ui/resources) -------

	private static class Municipio {
		final String provincia; // 2 dígitos
		final String codigo; // 5 dígitos (PPMMM)
		final String nombre; // texto original

		Municipio(String provincia, String codigo, String nombre) {
			this.provincia = provincia;
			this.codigo = codigo;
			this.nombre = nombre;
		}
	}

	// -------- Países (CSV: /ui/resources/CodigoISOPaises.csv) -------
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
				getClass().getResourceAsStream("/ui/resources/municipios.csv"), StandardCharsets.UTF_8))) {

			String line = br.readLine(); // cabecera
			if (line == null)
				return;

			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isBlank())
					continue;

				String[] parts = line.split(";");
				if (parts.length < 3)
					continue;

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

		String[] rutas = { "/ui/resources/CodigoISOPaises.csv", "/ui/resources/codigoisopaises.csv" };

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
			for (String r : rutas)
				System.out.println("  - " + r);
			return;
		}

		//System.out.println("Cargando países desde: " + rutaUsada);

		try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

			String line = br.readLine(); // cabecera
			if (line == null)
				return;

			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isBlank())
					continue;

				String[] parts = line.split(";");
				if (parts.length < 3)
					continue;

				String iso3 = parts[0].trim().toUpperCase();
				String iso2 = parts[1].trim().toUpperCase();
				String nombre = parts[2].trim();

				if (iso3.isBlank())
					continue;

				listaPaises.add(new Pais(iso3, iso2, nombre));
			}

			//System.out.println("Países cargados: " + listaPaises.size());

		} catch (Exception e) {
			System.out.println("No se pudo cargar la tabla de codigos ISO de paises: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static String normalizar(String s) {
		if (s == null)
			return "";
		String t = s.trim().toUpperCase();
		t = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", ""); // quita tildes
		t = t.replaceAll("\\s+", " ");
		return t;
	}

	private static String provinciaDesdeCP(String codigoPostal) {
		if (codigoPostal == null)
			return "";
		String cp = codigoPostal.trim();
		if (!cp.matches("^\\d{5}$"))
			return "";
		return cp.substring(0, 2);
	}

	private void activarAutocompletarMunicipio() {

		// escribes aquí (aunque ponga "Código municipio (ESP)")
		tfCodigoMunicipio.textProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal == null)
				return;

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
				for (List<Municipio> l : municipiosPorProvincia.values())
					candidatos.addAll(l);
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
			if (!isNow)
				sugerenciasMunicipio.hide();
		});

		// ✅ Auto-aplicar municipio al perder foco (si escribe nombre parcial o
		// completo)
		tfCodigoMunicipio.focusedProperty().addListener((o, was, isNow) -> {
			if (isNow)
				return; // solo cuando pierde foco

			// solo España
			String pais = normalizar(tfPais.getText());
			if (!"ESP".equals(pais))
				return;

			String raw = trim(tfCodigoMunicipio);
			if (raw.isBlank())
				return;

			// si ya es un código válido, no tocamos
			if (raw.matches("^\\d{5}$"))
				return;

			String query = normalizar(raw);
			if (query.length() < 2)
				return;

			// provincia preferida desde CP (si está)
			String prov = provinciaDesdeCP(tfCodigoPostal.getText());

			List<Municipio> candidatos = new ArrayList<>();
			if (!prov.isBlank() && municipiosPorProvincia.containsKey(prov)) {
				candidatos.addAll(municipiosPorProvincia.get(prov));
			} else {
				for (List<Municipio> l : municipiosPorProvincia.values())
					candidatos.addAll(l);
			}

			List<Municipio> encontrados = buscarMunicipios(candidatos, query);

			if (encontrados.isEmpty())
				return;

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

		// TAB para aceptar la primera sugerencia
		tfCodigoMunicipio.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, ev -> {

			if (!sugerenciasMunicipio.isShowing())
				return;

			// TAB: aceptar primera sugerencia
			if (ev.getCode() == javafx.scene.input.KeyCode.TAB) {

				if (!sugerenciasMunicipio.getItems().isEmpty()) {
					
					MenuItem mi = sugerenciasMunicipio.getItems().get(0);

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

			// nombres con "/"
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

			if (start)
				empieza.add(m);
			else if (match)
				contiene.add(m);
		}

		// empieza primero luego contiene
		List<Municipio> res = new ArrayList<>(empieza);
		res.addAll(contiene);
		return res;
	}

	private void activarAutocompletarPais(TextField tf) {

		final ContextMenu menu = new ContextMenu(); // uno por TextField, no compartido

		tf.textProperty().addListener((obs, oldVal, newVal) -> {
			if (newVal == null)
				return;

			if (listaPaises.isEmpty()) {
				menu.hide();
				return;
			}

			String query = normalizar(newVal);

			// si ya parece ISO3 no sale nada
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

				// al perder foco, si escribió un nombre, lo intentamos convertir a ISO3
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

			if (start)
				empieza.add(p);
			else if (match)
				contiene.add(p);
		}

		// Orden, empieza > contiene
		List<Pais> res = new ArrayList<>(empieza);
		res.addAll(contiene);

		// quitar duplicados por ISO3, por si el CSV mete repetidos
		Map<String, Pais> uniq = new LinkedHashMap<>();
		for (Pais p : res)
			uniq.putIfAbsent(p.iso3, p);

		return new ArrayList<>(uniq.values());
	}

	private String resolverIso3DesdeTexto(String texto) {
		if (texto == null)
			return null;

		String t = texto.trim();
		if (t.isBlank())
			return null;

		String tn = normalizar(t);

		// si ya es ISO3
		if (tn.matches("^[A-Z]{3}$"))
			return tn;

		// si es ISO2
		if (tn.matches("^[A-Z]{2}$")) {
			for (Pais p : listaPaises) {
				if (normalizar(p.iso2).equals(tn))
					return p.iso3;
			}
		}

		// match exacto por nombre o variantes separadas por "/"
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
		if (texto == null)
			return null;

		String t = texto.trim();
		if (t.isBlank())
			return null;

		// Intentamos formatos con año 4 dígitos primero
		for (DateTimeFormatter f : FORMATOS_ACEPTADOS) {
			try {
				return LocalDate.parse(t, f);
			} catch (Exception ignored) {
			}
		}

		// Intentamos formatos con año 2 dígitos
		try {
			String t2 = t.replace('-', '/');

			// dd/MM/yy o d/M/yy
			if (t2.matches("^\\d{1,2}/\\d{1,2}/\\d{2}$")) {
				String[] parts = t2.split("/");
				int dia = Integer.parseInt(parts[0]);
				int mes = Integer.parseInt(parts[1]);
				int yy = Integer.parseInt(parts[2]);

				int now2 = LocalDate.now().getYear() % 100;
				int year = (yy <= now2) ? (2000 + yy) : (1900 + yy);

				return LocalDate.of(year, mes, dia);
			}
		} catch (Exception ignored) {
		}

		return null;
	}

	private void normalizarCampoFecha(TextField tf) {
		LocalDate d = parseFechaFlexible(tf.getText());
		if (d != null) {
			tf.setText(d.format(FECHA_ES)); // siempre dd/MM/yyyy
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
		if (iso == null || iso.isBlank())
			return "";
		try {
			return LocalDate.parse(iso).format(FECHA_ES);
		} catch (Exception e) {
			return iso; // por si viene algo raro
		}
	}

	private void cargarEstanciaDe(Peregrino p) {
		try {
			// buscamos la estancia que coincida con el peregrino y la fecha de la lista (si
			// hay)
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
			tfLugarInicioCamino.setText(safe(estanciaActual.getLugarInicioCamino()));
			tfUltimoAlbergue.setText(safe(estanciaActual.getUltimoAlbergue()));
			tfCaminoDestino.setText(safe(estanciaActual.getCaminoDestino()));
			tfNumHabitaciones.setText(
					estanciaActual.getNumeroHabitaciones() > 0 ? String.valueOf(estanciaActual.getNumeroHabitaciones())
							: "");
			cbInternet.setSelected(estanciaActual.isInternetIncluido());
			tfFechaContrato.setText(fechaEsDesdeIso(estanciaActual.getFechaContrato()));
			tfMedioPago.setText(safe(estanciaActual.getMedioPago()));

			//String numPersonas = estanciaActual.getNumPersonasContrato();
			tfNumPersonas.setText(safe(estanciaActual.getNumPersonasContrato()));

			tfTipoPago.setText(safe(estanciaActual.getTipoPago()));
			tfTitular.setText(safe(estanciaActual.getTitularPago()));
			tfCaducidadTarjeta.setText(safe(estanciaActual.getCaducidadTarjeta()));
			tfFechaPago.setText(fechaEsDesdeIso(estanciaActual.getFechaPago()));

			taObservaciones.setText(safe(estanciaActual.getObservaciones()));

			// Cargar líneas de venta de esta estancia
			lineasVentaActuales.clear();
			if (estanciaActual.getIdEstancia() != 0) {
			    lineasVentaActuales.addAll(
			        VentaLineaService.listarPorEstancia(estanciaActual.getIdEstancia())
			    );
			}
			recargarCatalogoProductos();

		} catch (DatabaseException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			// Si falla no bloquea, crea una estancia en blanco
			estanciaActual = new Estancia();
			estanciaActual.setIdPeregrino(p.getIdPeregrino());
			estanciaActual.setIdAlbergue(1);
			tfReferencia.setText("");
			tfFechaEntrada.setText("");
			tfFechaSalida.setText("");
			tfLugarInicioCamino.setText("");
			tfUltimoAlbergue.setText("");
			tfCaminoDestino.setText("");
			tfNumHabitaciones.setText("");
			cbInternet.setSelected(false);
			tfNumPersonas.setText("");
			tfTipoPago.setText("");
			tfTitular.setText("");
			tfCaducidadTarjeta.setText("");
			tfFechaPago.setText("");
			tfFechaContrato.setText("");
			tfMedioPago.setText("");
			taObservaciones.setText("");
			lineasVentaActuales.clear();
			refrescarWidgetVentas();
		}
	}

	private Pais buscarPaisExactoPorNombreOIso(String texto) {
		String q = normalizar(texto);
		if (q.isBlank())
			return null;

		for (Pais p : listaPaises) {
			if (normalizar(p.iso3).equals(q) || normalizar(p.iso2).equals(q))
				return p;

			for (String v : p.nombre.split("/")) {
				if (normalizar(v).equals(q))
					return p;
			}
		}
		return null;
	}

	private Pais buscarPaisMejorCoincidencia(String texto) {
		String q = normalizar(texto);
		if (q.isBlank())
			return null;

		// Caso especial clave:
		// Si el usuario escribe más de 3 letras y empieza por un ISO3,
		// lo convertimos directamente
		if (q.length() > 3) {
			for (Pais p : listaPaises) {
				if (q.startsWith(normalizar(p.iso3))) {
					return p;
				}
			}
		}

		// exacto por ISO o nombre
		Pais exacto = buscarPaisExactoPorNombreOIso(texto);
		if (exacto != null)
			return exacto;

		// empieza por (en nombre/variantes)
		for (Pais p : listaPaises) {
			if (normalizar(p.iso3).startsWith(q) || normalizar(p.iso2).startsWith(q))
				return p;

			for (String v : p.nombre.split("/")) {
				if (normalizar(v).startsWith(q))
					return p;
			}
		}

		// contiene
		for (Pais p : listaPaises) {
			if (normalizar(p.iso3).contains(q) || normalizar(p.iso2).contains(q))
				return p;

			for (String v : p.nombre.split("/")) {
				if (normalizar(v).contains(q))
					return p;
			}
		}

		return null;
	}

	private boolean coincideBusqueda(Peregrino p, String filtro) {
		String texto = (safe(p.getNombre()) + " " + safe(p.getApellido1()) + " " + safe(p.getApellido2()) + " "
				+ safe(p.getTipoDocumento()) + " " + safe(p.getNumeroDocumento())).toUpperCase();

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
			if (e == null)
				return false;

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

		btnDiaAnterior.setOpacity(buscando ? 0.5 : 1.0);
		dpFechaLista.setOpacity(buscando ? 0.5 : 1.0);
		btnDiaSiguiente.setOpacity(buscando ? 0.5 : 1.0);
	}

	private void intentarAutorrellenarPorDocumento() {

		String tipo = trim(tfTipoDocumento).toUpperCase();
		String numero = trim(tfNumeroDocumento).toUpperCase();

		if (tipo.isBlank() || numero.isBlank())
			return;

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
			tfSoporteDocumento.setText(safe(existente.getSoporteDocumento()));

			aplicarReglaMunicipio();
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
					getClass().getResource("/ui/nuevo_albergue.fxml"));

			javafx.scene.Scene scene = new javafx.scene.Scene(loader.load(), 620, 780);

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

			javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
					javafx.scene.control.Alert.AlertType.CONFIRMATION);
			alert.setTitle("Reestablecer albergue");
			alert.setHeaderText("¿Seguro que quieres eliminar la configuración del albergue actual?");
			alert.setContentText(
					"Esta acción vaciará los datos del albergue actual, pero no eliminará peregrinos, estancias ni camas.");

			javafx.scene.control.ButtonType btnNoEliminar = new javafx.scene.control.ButtonType("No reestablecer",
					javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);

			javafx.scene.control.ButtonType btnEliminar = new javafx.scene.control.ButtonType(
					"Restablecer albergue actual", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);

			alert.getButtonTypes().setAll(btnNoEliminar, btnEliminar);

			javafx.scene.control.Button botonVerde = (javafx.scene.control.Button) alert.getDialogPane()
					.lookupButton(btnNoEliminar);
			javafx.scene.control.Button botonNaranja = (javafx.scene.control.Button) alert.getDialogPane()
					.lookupButton(btnEliminar);

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
			if (cama == null)
				return;

			tfNumeroHabitacion.setText(String.valueOf(cama.getNumeroHabitacion()));
			tfNumeroCama.setText(String.valueOf(cama.getNumeroCama()));

		} catch (Exception e) {
			System.out.println("No se pudo cargar habitación/cama en ficha: " + e.getMessage());
		}
	}

	private void guardarDatosPreviosDesdeFicha() {
		previoNacionalidad = trim(tfNacionalidad);
		previoPais = trim(tfPais);
		previoFechaEntrada = trim(tfFechaEntrada);
		previoFechaSalida = trim(tfFechaSalida);
		previoNumHabitaciones = trim(tfNumHabitaciones);
		previoNumPersonas = trim(tfNumPersonas);
		previoNumeroHabitacion = trim(tfNumeroHabitacion);
		previoLugarInicioCamino = trim(tfLugarInicioCamino);
		previoUltimoAlbergue = trim(tfUltimoAlbergue);
		previoCaminoDestino = trim(tfCaminoDestino);
		previoTelefono = trim(tfTelefono);
		previoReferenciaGrupo = trim(tfNumPersonas);
	}

	private boolean validarTipoPago() {
		String v = trim(tfTipoPago).toUpperCase();
		tfTipoPago.setText(v);

		if (v.isBlank()) {
			marcarError(tfTipoPago, false);
			return true;
		}

		boolean ok = TIPOS_PAGO_VALIDOS.contains(v);
		marcarError(tfTipoPago, !ok);
		return ok;
	}

	private String construirNombreCompletoHuesped() {
		String nombre = trim(tfNombre);
		String apellido1 = trim(tfApellido1);
		String apellido2 = trim(tfApellido2);

		return (nombre + " " + apellido1 + " " + apellido2).trim().replaceAll("\\s+", " ");
	}

	private boolean validarParentesco() {
		String v = trim(tfParentesco).toUpperCase();
		tfParentesco.setText(v);

		if (v.isBlank()) {
			marcarError(tfParentesco, false);
			return true;
		}

		boolean ok = PARENTESCOS_VALIDOS.contains(v);
		marcarError(tfParentesco, !ok);
		return ok;
	}

	private boolean validarCaducidadTarjeta() {
		String v = trim(tfCaducidadTarjeta);

		if (v.isBlank()) {
			marcarError(tfCaducidadTarjeta, false);
			return true;
		}

		if (!v.matches("^(0[1-9]|1[0-2])/\\d{4}$")) {
			marcarError(tfCaducidadTarjeta, true);
			return false;
		}

		int year = Integer.parseInt(v.substring(3));
		boolean ok = year >= 2023;

		marcarError(tfCaducidadTarjeta, !ok);
		return ok;
	}

	private boolean validarRol() {
		String v = trim(tfRol).toUpperCase();
		if (v.isBlank())
			v = "VI";
		tfRol.setText(v);

		boolean ok = v.matches("^(VI)$");
		marcarError(tfRol, !ok);
		return ok;
	}

	private void autoResizeTextArea(TextArea ta) {

		ta.setPrefRowCount(1);

		ta.textProperty().addListener((obs, oldText, newText) -> {
			int lineas = newText.split("\n", -1).length;

			double alturaLinea = 24;
			double nuevaAltura = (lineas + 1) * alturaLinea;
			double maxAltura = 150;

			ta.setPrefHeight(Math.min(nuevaAltura, maxAltura));
		});
	}

	// --------------------------
	// Widget de ventas
	// --------------------------

	private void inicializarWidgetVentas() {
	    if (vboxProductos == null || lblTotalVentas == null) return;
	    recargarCatalogoProductos();
	}

	private void recargarCatalogoProductos() {
	    if (vboxProductos == null) return;
	    try {
	        List<Producto> productos = ProductoService.listarActivos();
	        vboxProductos.getChildren().clear();

	        boolean primeraEstanciaVista = false;
	        for (Producto p : productos) {
	            int cantidadInicial = 0;
	            if (p.isEsEstancia() && !primeraEstanciaVista) {
	                cantidadInicial = 1;
	                primeraEstanciaVista = true;
	            }

	            // buscar si ya hay una línea activa para este producto
	            for (VentaLinea vl : lineasVentaActuales) {
	                if (vl.getIdProducto() != null && vl.getIdProducto() == p.getIdProducto()) {
	                    cantidadInicial = vl.getCantidad();
	                    break;
	                }
	            }

	            vboxProductos.getChildren().add(crearFilaProducto(p, cantidadInicial));
	        }

	        refrescarWidgetVentas();

	    } catch (Exception e) {
	        System.out.println("Error cargando catálogo de productos: " + e.getMessage());
	    }
	}

	private javafx.scene.layout.HBox crearFilaProducto(Producto p, int cantidadInicial) {

	    javafx.scene.layout.HBox fila = new javafx.scene.layout.HBox(4);
	    fila.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
	    fila.getStyleClass().add(p.isEsEstancia() ? "ventas-fila-estancia" : "ventas-fila");

	    // Nombre y precio
	    javafx.scene.layout.VBox info = new javafx.scene.layout.VBox(1);
	    info.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
	    javafx.scene.layout.HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);

	    Label lblNombre = new Label(p.getNombre());
	    lblNombre.getStyleClass().add(p.isEsEstancia() ? "ventas-nombre-estancia" : "ventas-nombre");
	    lblNombre.setWrapText(true);

	    Label lblPrecio = new Label(String.format("%.2f €", p.getPrecio()));
	    lblPrecio.getStyleClass().add("ventas-precio");

	    info.getChildren().addAll(lblNombre, lblPrecio);

	    // Campo cantidad
	    TextField tfCantidad = new TextField(String.valueOf(cantidadInicial));
	    tfCantidad.getStyleClass().add("ventas-cantidad");
	    tfCantidad.setAlignment(javafx.geometry.Pos.CENTER);

	    // Botón -
	    Button btnMenos = new Button("−");
	    btnMenos.getStyleClass().add("ventas-btn");

	    // Botón +
	    Button btnMas = new Button("+");
	    btnMas.getStyleClass().add("ventas-btn");

	    // Lógica botón +
	    btnMas.setOnAction(e -> {
	        int actual = parseCantidad(tfCantidad.getText());
	        int nueva = actual + 1;
	        tfCantidad.setText(String.valueOf(nueva));
	        actualizarLineaVenta(p, nueva);
	    });

	    // Lógica botón -
	    btnMenos.setOnAction(e -> {
	        int actual = parseCantidad(tfCantidad.getText());
	        int nueva = Math.max(0, actual - 1);
	        tfCantidad.setText(String.valueOf(nueva));
	        actualizarLineaVenta(p, nueva);
	    });

	    // Edición manual del campo
	    tfCantidad.textProperty().addListener((obs, oldVal, newVal) -> {
	        if (!newVal.matches("\\d*")) {
	            tfCantidad.setText(oldVal);
	            return;
	        }
	        int cantidad = parseCantidad(newVal);
	        actualizarLineaVenta(p, cantidad);
	    });

	    fila.getChildren().addAll(info, btnMenos, tfCantidad, btnMas);
	    return fila;
	}

	private void actualizarLineaVenta(Producto p, int cantidad) {
	    // Buscar línea existente
	    for (VentaLinea vl : lineasVentaActuales) {
	        if (vl.getIdProducto() != null && vl.getIdProducto() == p.getIdProducto()) {
	            if (cantidad == 0) {
	                lineasVentaActuales.remove(vl);
	            } else {
	                vl.setCantidad(cantidad);
	            }
	            refrescarWidgetVentas();
	            return;
	        }
	    }
	    // No existe: crear si cantidad > 0
	    if (cantidad > 0) {
	        VentaLinea nueva = new VentaLinea();
	        nueva.setIdProducto(p.getIdProducto());
	        nueva.setNombreSnapshot(p.getNombre());
	        nueva.setPrecioUnitario(p.getPrecio());
	        nueva.setCantidad(cantidad);
	        lineasVentaActuales.add(nueva);
	        refrescarWidgetVentas();
	    }
	}

	private int parseCantidad(String texto) {
	    try {
	        return Math.max(0, Integer.parseInt(texto.trim()));
	    } catch (Exception e) {
	        return 0;
	    }
	}

	private void refrescarWidgetVentas() {
	    if (lblTotalVentas == null) return;

	    double total = lineasVentaActuales.stream()
	            .mapToDouble(VentaLinea::getSubtotal)
	            .sum();

	    lblTotalVentas.setText("Total: " + String.format("%.2f", total) + " €");
	}

	@FXML
	private void onEditarProductos() {
	    System.out.println("Abrir ventana editar productos (pendiente bloque 4)");
	}
	
	// --------------------------
	// Pestaña Ventas
	// --------------------------

	@FXML
	private void onVentasDiaAnterior() {
	    fechaLista = fechaLista.minusDays(1);
	    dpFechaLista.setValue(fechaLista);
	    dpFechaVentas.setValue(fechaLista);
	    actualizarResumenVentas();
	}

	@FXML
	private void onVentasDiaSiguiente() {
	    fechaLista = fechaLista.plusDays(1);
	    dpFechaLista.setValue(fechaLista);
	    dpFechaVentas.setValue(fechaLista);
	    actualizarResumenVentas();
	}

	@FXML
	private void onActualizarVentas() {
	    actualizarResumenVentas();
	}

	@FXML
	private void onNuevaTarifa() {
	    lvTarifas.getSelectionModel().clearSelection();
	    tfNombreTarifa.clear();
	    tfPrecioTarifa.clear();
	    tfNombreTarifa.requestFocus();
	}

	@FXML
	private void onGuardarTarifa() {
	    guardarProductoCatalogo(true);
	}

	@FXML
	private void onDesactivarTarifa() {
	    Producto sel = lvTarifas.getSelectionModel().getSelectedItem();
	    if (sel == null) return;
	    ProductoService.desactivar(sel.getIdProducto());
	    cargarCatalogoVentas();
	    recargarCatalogoProductos();
	}

	@FXML
	private void onNuevoProductoCatalogo() {
	    lvProductosCatalogo.getSelectionModel().clearSelection();
	    tfNombreProducto.clear();
	    tfPrecioProducto.clear();
	    tfNombreProducto.requestFocus();
	}

	@FXML
	private void onGuardarProductoCatalogo() {
	    guardarProductoCatalogo(false);
	}

	@FXML
	private void onDesactivarProductoCatalogo() {
	    Producto sel = lvProductosCatalogo.getSelectionModel().getSelectedItem();
	    if (sel == null) return;
	    ProductoService.desactivar(sel.getIdProducto());
	    cargarCatalogoVentas();
	    recargarCatalogoProductos();
	}

	private void guardarProductoCatalogo(boolean esEstancia) {
	    String nombre = esEstancia ? tfNombreTarifa.getText().trim() : tfNombreProducto.getText().trim();
	    String precioStr = esEstancia ? tfPrecioTarifa.getText().trim() : tfPrecioProducto.getText().trim();

	    if (nombre.isBlank()) return;

	    double precio = 0.0;
	    try {
	        precio = Double.parseDouble(precioStr.replace(",", "."));
	    } catch (Exception e) {
	        return;
	    }

	    Producto sel = esEstancia
	            ? lvTarifas.getSelectionModel().getSelectedItem()
	            : lvProductosCatalogo.getSelectionModel().getSelectedItem();

	    Producto p = (sel != null) ? sel : new Producto();
	    p.setNombre(nombre);
	    p.setPrecio(precio);
	    p.setActivo(true);
	    p.setEsEstancia(esEstancia);

	    ProductoService.guardar(p);
	    cargarCatalogoVentas();
	    recargarCatalogoProductos();
	}

	private void cargarCatalogoVentas() {
	    if (lvTarifas == null || lvProductosCatalogo == null) return;

	    List<Producto> todos = ProductoService.listarTodos();

	    List<Producto> tarifas   = todos.stream().filter(Producto::isEsEstancia).toList();
	    List<Producto> productos = todos.stream().filter(p -> !p.isEsEstancia()).toList();

	    lvTarifas.setItems(javafx.collections.FXCollections.observableArrayList(tarifas));
	    lvProductosCatalogo.setItems(javafx.collections.FXCollections.observableArrayList(productos));

	    // Al seleccionar una tarifa : rellenar campos
	    lvTarifas.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
	        if (newVal != null) {
	            tfNombreTarifa.setText(newVal.getNombre());
	            tfPrecioTarifa.setText(String.format("%.2f", newVal.getPrecio()));
	        }
	    });

	    // Al seleccionar un producto: rellenar campos
	    lvProductosCatalogo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
	        if (newVal != null) {
	            tfNombreProducto.setText(newVal.getNombre());
	            tfPrecioProducto.setText(String.format("%.2f", newVal.getPrecio()));
	        }
	    });
	}

	private void actualizarResumenVentas() {
	    if (lblResumenEstancias == null) return;

	    try {
	        model.ResumenVentas resumen = VentaLineaService.obtenerResumenDia(1, fechaLista);

	        lblResumenEstancias.setText(String.format("Estancias:   %.2f €", resumen.getTotalEstancias()));
	        lblResumenProductos.setText(String.format("Productos:  %.2f €", resumen.getTotalProductos()));
	        lblResumenTotal.setText(String.format("Total:  %.2f €", resumen.getTotalGeneral()));

	        // Desglose por producto
	        vboxResumenPorTipo.getChildren().clear();

	        java.util.List<String[]> desglose = VentaLineaService.obtenerDesgloseDia(1, fechaLista);

	        if (!desglose.isEmpty()) {
	            // Separador estancias
	            boolean hayEstancias = desglose.stream().anyMatch(d -> "1".equals(d[3]));
	            boolean hayProductos = desglose.stream().anyMatch(d -> "0".equals(d[3]));

	            if (hayEstancias) {
	                Label lblTitEst = new Label("— Estancias —");
	                lblTitEst.setStyle("-fx-font-size: 11px; -fx-text-fill: #4a7596; -fx-font-weight: bold;");
	                vboxResumenPorTipo.getChildren().add(lblTitEst);

	                for (String[] fila : desglose) {
	                    if ("1".equals(fila[3])) {
	                        Label lbl = new Label(String.format("%s  ×%s  =  %.2f €",
	                                fila[0], fila[1], Double.parseDouble(fila[2])));
	                        lbl.getStyleClass().add("ventas-resumen-item");
	                        vboxResumenPorTipo.getChildren().add(lbl);
	                    }
	                }
	            }

	            if (hayProductos) {
	                Label lblTitProd = new Label("— Productos —");
	                lblTitProd.setStyle("-fx-font-size: 11px; -fx-text-fill: #4a7596; -fx-font-weight: bold;");
	                vboxResumenPorTipo.getChildren().add(lblTitProd);

	                for (String[] fila : desglose) {
	                    if ("0".equals(fila[3])) {
	                        Label lbl = new Label(String.format("%s  ×%s  =  %.2f €",
	                                fila[0], fila[1], Double.parseDouble(fila[2])));
	                        lbl.getStyleClass().add("ventas-resumen-item");
	                        vboxResumenPorTipo.getChildren().add(lbl);
	                    }
	                }
	            }
	        }

	        // Desglose por tipo de pago
	        if (!resumen.getPorTipoPago().isEmpty()) {
	            Label lblTitPago = new Label("— Por tipo de pago —");
	            lblTitPago.setStyle("-fx-font-size: 11px; -fx-text-fill: #4a7596; -fx-font-weight: bold;");
	            vboxResumenPorTipo.getChildren().add(lblTitPago);

	            resumen.getPorTipoPago().forEach((tipo, total) -> {
	                Label lbl = new Label(tipo + ":  " + String.format("%.2f €", total));
	                lbl.getStyleClass().add("ventas-resumen-item");
	                vboxResumenPorTipo.getChildren().add(lbl);
	            });
	        }

	    } catch (Exception e) {
	        System.out.println("Error actualizando resumen ventas: " + e.getMessage());
	    }
	}
	
	
	private boolean camaDisponible(int idCama, String fechaEntrada, String fechaSalida, int idEstanciaActual) {
	    try (java.sql.Connection conn = db.DBManager.getConnection()) {
	        String sql = """
	            SELECT COUNT(*) FROM estancia
	            WHERE id_cama = ?
	              AND id_estancia <> ?
	              AND estado_estancia <> 'CANCELADA'
	              AND (
	                COALESCE(fecha_salida_real, fecha_salida_prevista) IS NULL
	                OR COALESCE(fecha_salida_real, fecha_salida_prevista) > ?
	              )
	              AND (? IS NULL OR ? > fecha_entrada)
	            """;
	        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setInt(1, idCama);
	            ps.setInt(2, idEstanciaActual);
	            ps.setString(3, fechaEntrada);
	            ps.setString(4, fechaSalida);
	            ps.setString(5, fechaSalida);
	            try (java.sql.ResultSet rs = ps.executeQuery()) {
	                return rs.next() && rs.getInt(1) == 0;
	            }
	        }
	    } catch (Exception e) {
	        return true;
	    }
	}

	
	@FXML
	private void onGestionPerfiles() {
	    try {
	        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
	                getClass().getResource("/ui/gestion_perfiles.fxml"));

	        javafx.scene.Scene scene = new javafx.scene.Scene(loader.load(), 520, 400);

	        javafx.stage.Stage stage = new javafx.stage.Stage();
	        stage.setTitle("Gestión de perfiles");
	        stage.setScene(scene);
	        stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
	        stage.showAndWait();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	private void aplicarRestriccionesPorRol() {
	    boolean esGerente = config.SesionActual.isGerente();

	    menuEdicion.setVisible(esGerente);
	    menuPerfiles.setVisible(esGerente);

	    menuArchivo.getItems().forEach(item -> {
	        if ("Editar productos".equals(item.getText())) {
	            item.setVisible(esGerente);
	        }
	    });
	}
	
	@FXML
	private void onCerrarSesion() {
	    try {
	        config.SesionActual.cerrar();

	        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
	                getClass().getResource("/ui/login.fxml"));

	        javafx.scene.Scene scene = new javafx.scene.Scene(loader.load());

	        javafx.stage.Stage stage = (javafx.stage.Stage) tabPane.getScene().getWindow();
	        stage.setTitle("Jacobs Hoster - Login");
	        stage.setScene(scene);
	        stage.setMaximized(false);
	        stage.show();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	private final List<String> gruposExistentes = new ArrayList<>();

	private void cargarGruposExistentes() {
	    gruposExistentes.clear();
	    try (java.sql.Connection conn = db.DBManager.getConnection()) {
	        String sql = """
	            SELECT DISTINCT num_personas_contrato
	            FROM estancia
	            WHERE num_personas_contrato IS NOT NULL
	              AND num_personas_contrato != ''
	              AND fecha_entrada = ?
	            ORDER BY num_personas_contrato
	            """;
	        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
	            ps.setString(1, fechaLista.toString());
	            try (java.sql.ResultSet rs = ps.executeQuery()) {
	                while (rs.next()) {
	                    String val = rs.getString(1);
	                    if (val != null && !val.isBlank()) {
	                        gruposExistentes.add(val);
	                    }
	                }
	            }
	        }
	    } catch (Exception e) {
	        System.out.println("Error cargando grupos: " + e.getMessage());
	    }
	}

	private void activarAutocompletarGrupo() {
	    ContextMenu menuGrupo = new ContextMenu();

	    tfNumPersonas.textProperty().addListener((obs, oldVal, newVal) -> {
	        if (newVal == null || newVal.isBlank()) {
	            menuGrupo.hide();
	            return;
	        }

	        String query = newVal.trim().toLowerCase();

	        List<String> coincidencias = gruposExistentes.stream()
	            .filter(g -> g.toLowerCase().contains(query))
	            .limit(8)
	            .toList();

	        if (coincidencias.isEmpty()) {
	            menuGrupo.hide();
	            return;
	        }

	        List<CustomMenuItem> items = new ArrayList<>();
	        for (String grupo : coincidencias) {
	            Label lbl = new Label(grupo);
	            lbl.setStyle("-fx-padding: 4 8 4 8;");
	            CustomMenuItem item = new CustomMenuItem(lbl, true);
	            item.setOnAction(ev -> {
	                tfNumPersonas.setText(grupo);
	                menuGrupo.hide();
	            });
	            items.add(item);
	        }

	        menuGrupo.getItems().setAll(items);
	        if (!menuGrupo.isShowing()) {
	            menuGrupo.show(tfNumPersonas, Side.BOTTOM, 0, 0);
	        }
	    });

	    tfNumPersonas.focusedProperty().addListener((o, was, isNow) -> {
	        if (!isNow) menuGrupo.hide();
	    });
	}
	
	private List<String> validarParaXML(List<Peregrino> peregrinos, List<Estancia> estancias) {
	    List<String> errores = new ArrayList<>();

	    java.util.Set<String> tiposDocValidos = java.util.Set.of("NIF", "NIE", "PAS", "OTRO", "CIF", "CIF_E");
	    java.util.Set<String> sexosValidos = java.util.Set.of("H", "M", "O");
	    java.util.Set<String> tiposPagoValidos = java.util.Set.of("DESTI", "EFECT", "TARJT", "PLATF", "TRANS", "MOVIL", "TREG", "OTRO");

	    for (int i = 0; i < peregrinos.size(); i++) {
	        Peregrino p = peregrinos.get(i);
	        Estancia e = estancias.get(i);

	        String id = safe(p.getNombre()) + " " + safe(p.getApellido1());
	        if (id.isBlank()) id = "Peregrino #" + (i + 1);

	        // Campos obligatorios persona
	        if (safe(p.getNombre()).isBlank())
	            errores.add(id + ": falta el nombre");
	        if (safe(p.getApellido1()).isBlank())
	            errores.add(id + ": falta el apellido 1");
	        if (safe(p.getTipoDocumento()).isBlank())
	            errores.add(id + ": falta el tipo de documento");
	        if (safe(p.getNumeroDocumento()).isBlank())
	            errores.add(id + ": falta el número de documento");
	        if (safe(p.getNacionalidad()).isBlank())
	            errores.add(id + ": falta la nacionalidad");
	        if (safe(p.getFechaNacimiento()).isBlank())
	            errores.add(id + ": falta la fecha de nacimiento");

	        // Enumeraciones
	        if (!safe(p.getTipoDocumento()).isBlank()
	                && !tiposDocValidos.contains(p.getTipoDocumento().toUpperCase()))
	            errores.add(id + ": tipo de documento inválido (" + p.getTipoDocumento() + ")");

	        if (!safe(p.getSexo()).isBlank()
	                && !sexosValidos.contains(p.getSexo().toUpperCase()))
	            errores.add(id + ": sexo inválido (" + p.getSexo() + ")");

	        // Municipio
	        if ("ESP".equalsIgnoreCase(p.getPais())) {
	            if (!safe(p.getCodigoMunicipio()).isBlank()
	                    && !p.getCodigoMunicipio().matches("^\\d{5}$"))
	                errores.add(id + ": código de municipio inválido (" + p.getCodigoMunicipio() + ")");
	        }

	        // Fechas coherentes
	        if (!safe(e.getFechaEntrada()).isBlank() && !safe(e.getFechaSalidaPrevista()).isBlank()) {
	            try {
	                java.time.LocalDate entrada = java.time.LocalDate.parse(e.getFechaEntrada());
	                java.time.LocalDate salida = java.time.LocalDate.parse(e.getFechaSalidaPrevista());
	                if (!salida.isAfter(entrada))
	                    errores.add(id + ": la fecha de salida debe ser posterior a la de entrada");
	            } catch (Exception ex) {
	                errores.add(id + ": fechas de estancia inválidas");
	            }
	        }

	        if (!safe(e.getFechaContrato()).isBlank() && !safe(e.getFechaEntrada()).isBlank()) {
	            try {
	                java.time.LocalDate contrato = java.time.LocalDate.parse(e.getFechaContrato());
	                java.time.LocalDate entrada = java.time.LocalDate.parse(e.getFechaEntrada());
	                if (contrato.isAfter(entrada))
	                    errores.add(id + ": la fecha de contrato no puede ser posterior a la de entrada");
	            } catch (Exception ex) {
	                // si no se puede parsear, ya lo detectará el mapper
	            }
	        }

	        // Pago
	        if (!safe(e.getTipoPago()).isBlank()
	                && !tiposPagoValidos.contains(e.getTipoPago().toUpperCase()))
	            errores.add(id + ": tipo de pago inválido (" + e.getTipoPago() + ")");
	    }

	    return errores;
	}
	
	
	@FXML
	private void onActualizarIngresos() {
	    if (dpIngresosDesde.getValue() == null || dpIngresosHasta.getValue() == null) return;

	    try {
	        LocalDate desde = dpIngresosDesde.getValue();
	        LocalDate hasta = dpIngresosHasta.getValue();

	        model.ResumenVentas resumen = VentaLineaService.obtenerResumenRango(1, desde, hasta);
	        java.util.List<String[]> desglose = VentaLineaService.obtenerDesgloseRango(1, desde, hasta);

	        vboxDesglosIngresos.getChildren().clear();

	        // Desglose por producto
	        boolean hayEstancias = desglose.stream().anyMatch(d -> "1".equals(d[3]));
	        boolean hayProductos = desglose.stream().anyMatch(d -> "0".equals(d[3]));

	        if (hayEstancias) {
	            Label lbl = new Label("— Estancias —");
	            lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #4a7596; -fx-font-weight: bold;");
	            vboxDesglosIngresos.getChildren().add(lbl);
	            for (String[] fila : desglose) {
	                if ("1".equals(fila[3])) {
	                    Label l = new Label(String.format("%s  x%s  =  %.2f €",
	                        fila[0], fila[1], Double.parseDouble(fila[2])));
	                    l.getStyleClass().add("ventas-resumen-item");
	                    vboxDesglosIngresos.getChildren().add(l);
	                }
	            }
	        }

	        if (hayProductos) {
	            Label lbl = new Label("— Productos —");
	            lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #4a7596; -fx-font-weight: bold;");
	            vboxDesglosIngresos.getChildren().add(lbl);
	            for (String[] fila : desglose) {
	                if ("0".equals(fila[3])) {
	                    Label l = new Label(String.format("%s  x%s  =  %.2f €",
	                        fila[0], fila[1], Double.parseDouble(fila[2])));
	                    l.getStyleClass().add("ventas-resumen-item");
	                    vboxDesglosIngresos.getChildren().add(l);
	                }
	            }
	        }

	        // Por tipo de pago
	        if (!resumen.getPorTipoPago().isEmpty()) {
	            Label lbl = new Label("— Por tipo de pago —");
	            lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #4a7596; -fx-font-weight: bold;");
	            vboxDesglosIngresos.getChildren().add(lbl);
	            resumen.getPorTipoPago().forEach((tipo, total) -> {
	                Label l = new Label(tipo + ":  " + String.format("%.2f €", total));
	                l.getStyleClass().add("ventas-resumen-item");
	                vboxDesglosIngresos.getChildren().add(l);
	            });
	        }

	        lblTotalCaja.setText(String.format("Total caja:  %.2f €", resumen.getTotalGeneral()));

	    } catch (Exception e) {
	        System.out.println("Error actualizando ingresos: " + e.getMessage());
	    }
	}

	@FXML
	private void onActualizarOcupacion() {
	    if (dpOcupacionDesde.getValue() == null || dpOcupacionHasta.getValue() == null) return;

	    try {
	        LocalDate desde = dpOcupacionDesde.getValue();
	        LocalDate hasta = dpOcupacionHasta.getValue();

	        int totalCamas = CamaService.contarCapacidadTotal();
	        int totalPeregrinos = 0;
	        int totalDias = 0;
	        int sumaOcupacion = 0;
	        int diaMasOcupadoNum = 0;
	        LocalDate diaMasOcupado = null;

	        LocalDate dia = desde;
	        while (!dia.isAfter(hasta)) {
	            int ocupadas = EstanciaService.contarPlazasOcupadasEnFecha(1, dia);
	            totalPeregrinos += ocupadas;
	            sumaOcupacion += ocupadas;
	            totalDias++;

	            if (ocupadas > diaMasOcupadoNum) {
	                diaMasOcupadoNum = ocupadas;
	                diaMasOcupado = dia;
	            }

	            dia = dia.plusDays(1);
	        }

	        int ocupacionMedia = totalDias > 0 && totalCamas > 0
	            ? (int) Math.round((double) sumaOcupacion / totalDias / totalCamas * 100)
	            : 0;

	        lblPeregrinosTotales.setText("Peregrinos totales: " + totalPeregrinos);
	        lblPlazasTotalesInformes.setText("Plazas totales: " + totalCamas);
	        lblOcupacionMedia.setText("Ocupación media: " + ocupacionMedia + "%");
	        lblDiaMasOcupado.setText("Día más ocupado: " +
	            (diaMasOcupado != null ? diaMasOcupado.format(FECHA_ES) + " (" + diaMasOcupadoNum + ")" : "-"));

	    } catch (Exception e) {
	        System.out.println("Error actualizando ocupación: " + e.getMessage());
	    }
	}

	@FXML
	private void onAnioAnterior() {
	    anioActual--;
	    lblAnio.setText(String.valueOf(anioActual));
	    onActualizarEstadisticas();
	}

	@FXML
	private void onAnioSiguiente() {
	    anioActual++;
	    lblAnio.setText(String.valueOf(anioActual));
	    onActualizarEstadisticas();
	}

	@FXML
	private void onActualizarEstadisticas() {
	    try {
	        // Gráfico por sexo
	        java.util.Map<String, Integer> porSexo = PeregrinoService.contarPorSexo(1, anioActual);
	        chartSexo.getData().clear();
	        porSexo.forEach((sexo, cantidad) -> {
	            String etiqueta = "H".equals(sexo) ? "Hombre" : "M".equals(sexo) ? "Mujer" : "Otro";
	            chartSexo.getData().add(new javafx.scene.chart.PieChart.Data(
	                etiqueta + " (" + cantidad + ")", cantidad));
	        });

	     // Gráfico por país
	        java.util.Map<String, Integer> porPais = PeregrinoService.contarPorPais(1, anioActual);
	        chartPais.getData().clear();
	        ((javafx.scene.chart.CategoryAxis) chartPais.getXAxis()).getCategories().clear();
	        javafx.scene.chart.XYChart.Series<String, Number> seriePais = new javafx.scene.chart.XYChart.Series<>();
	        for (java.util.Map.Entry<String, Integer> entry : porPais.entrySet()) {
	            seriePais.getData().add(new javafx.scene.chart.XYChart.Data<>(entry.getKey(), entry.getValue()));
	        }
	        chartPais.getData().add(seriePais);
	        
	        
	        Platform.runLater(() -> {
	            chartPais.getXAxis().setTickLabelRotation(-45);
	            ((CategoryAxis) chartPais.getXAxis()).setTickMarkVisible(true);
	            ((CategoryAxis) chartPais.getXAxis()).setTickLabelsVisible(true);
	        });

	        // Gráfico por edad
	        java.util.Map<String, Integer> porEdad = PeregrinoService.contarPorFranjaEdad(1, anioActual);
	        chartEdad.getData().clear();
	        ((CategoryAxis) chartEdad.getXAxis()).getCategories().clear();
	        javafx.scene.chart.XYChart.Series<String, Number> serieEdad = new javafx.scene.chart.XYChart.Series<>();
	        for (java.util.Map.Entry<String, Integer> entry : porEdad.entrySet()) {
	            serieEdad.getData().add(new javafx.scene.chart.XYChart.Data<>(entry.getKey(), entry.getValue()));
	        }
	        chartEdad.getData().add(serieEdad);
	        chartEdad.getXAxis().setTickLabelRotation(-45);
	        ((CategoryAxis) chartEdad.getXAxis()).setTickMarkVisible(true);
	        ((CategoryAxis) chartEdad.getXAxis()).setTickLabelsVisible(true);

	    } catch (Exception e) {
	        System.out.println("Error actualizando estadísticas: " + e.getMessage());
	    }
	}
	
	@FXML
	private void onContactoEmail() {
	    try {
	        java.awt.Desktop.getDesktop().mail(
	            new java.net.URI("mailto:contacto@snoutserv.com"));
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	// FUTURO:
	// Cuando existan preregistros desde la nube, el indicador de plazas deberá
	// decidir si los pendientes/restervas descuentan o no aforo según la
	// configuración del albergue (acepta reservas / no acepta reservas).

}