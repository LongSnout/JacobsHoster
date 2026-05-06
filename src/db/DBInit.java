package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dao.AlbergueDAO;
import dao.UsuarioDAO;
import exception.DatabaseException;
import model.Albergue;
import model.Usuario;
import util.HashUtil;

public class DBInit {
	
	//Esta clase crea la base de datos y tablas la primera vez
	//que se inicia la app. Si ya existe, solo comprueba que estén las tablas mínimas
	//y crea los datos iniciales si faltan.

    private DBInit() {}

    public static boolean init() {

        try (Connection conn = DBManager.getConnection()) {

            boolean bdNueva = crearTablasSiNoExisten(conn);

            // 1) Comprobar que hay tablas (detección de ruta/DB equivocada)
            if (!existenTablasMinimas(conn)) {
                throw new DatabaseException("La base de datos no tiene las tablas esperadas. ¿Ruta correcta?");
            }

            // 2) Semilla: albergue
            if (!AlbergueDAO.existeAlbergue(conn)) {
                Albergue a = new Albergue();
                a.setNombre("SIN CONFIGURAR");
                a.setSincronizacionActiva(false);
                AlbergueDAO.insertarAlbergue(conn, a);
            }

            // 3) Semilla: usuario inicial
            if (!hayUsuarios(conn)) {
                Usuario u = new Usuario();
                u.setNombreUsuario("admin");
                u.setRol("GERENTE");
                u.setPasswordHash(HashUtil.sha256("admin"));
                UsuarioDAO.insertar(conn, u);
            }

            return bdNueva;

        } catch (Exception e) {
            throw new DatabaseException("Error inicializando la base de datos: " + e.getMessage(), e);
        }
    }

    private static boolean existenTablasMinimas(Connection conn) throws Exception {
        return existeTabla(conn, "albergue") && existeTabla(conn, "usuario") && existeTabla(conn, "peregrino");
    }

    private static boolean existeTabla(Connection conn, String nombreTabla) throws Exception {
        String sql = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreTabla);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static boolean hayUsuarios(Connection conn) throws Exception {
        String sql = "SELECT COUNT(*) FROM usuario";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    
    
    private static boolean crearTablasSiNoExisten(Connection conn) throws Exception {
        boolean eraVacia = !existenTablasMinimas(conn);

        String[] sqls = {

            // albergue
            """
            CREATE TABLE IF NOT EXISTS albergue (
                id_albergue INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                direccion TEXT,
                municipio TEXT,
                provincia TEXT,
                pais TEXT,
                telefono TEXT,
                email TEXT,
                codigo_establecimiento_mir TEXT,
                id_albergue_nube TEXT,
                api_key TEXT,
                sincronizacion_activa INTEGER NOT NULL DEFAULT 0
                    CHECK (sincronizacion_activa IN (0,1)),
                fecha_ultima_sincronizacion TEXT,
                api_base_url TEXT,
                install_id TEXT,
                install_secret TEXT,
                install_registered_at TEXT,
                admite_reservas INTEGER NOT NULL DEFAULT 0,
                numeracion_camas_activa INTEGER NOT NULL DEFAULT 0,
                hora_apertura TEXT,
                hora_cierre TEXT,
                fecha_apertura_desde TEXT,
                fecha_apertura_hasta TEXT,
                observaciones_apertura TEXT,
                CHECK (fecha_ultima_sincronizacion IS NULL OR length(fecha_ultima_sincronizacion) >= 10),
                CHECK (email IS NULL OR instr(email, '@') > 1)
            )
            """,

            // habitacion
            """
            CREATE TABLE IF NOT EXISTS habitacion (
                numero_habitacion INTEGER PRIMARY KEY,
                id_albergue INTEGER NOT NULL,
                numero_camas INTEGER NOT NULL CHECK (numero_camas >= 0),
                estado_habitacion TEXT NOT NULL DEFAULT 'ACTIVA',
                FOREIGN KEY (id_albergue)
                    REFERENCES albergue(id_albergue)
                    ON DELETE CASCADE
            )
            """,

            // cama
            """
            CREATE TABLE IF NOT EXISTS cama (
                id_cama INTEGER PRIMARY KEY AUTOINCREMENT,
                numero_habitacion INTEGER NOT NULL,
                estado TEXT NOT NULL DEFAULT 'LIBRE',
                numero_cama INTEGER,
                activa INTEGER NOT NULL DEFAULT 1,
                FOREIGN KEY (numero_habitacion)
                    REFERENCES habitacion(numero_habitacion)
                    ON DELETE CASCADE
            )
            """,

            // envio_xml
            """
            CREATE TABLE IF NOT EXISTS envio_xml (
                id_envio_xml INTEGER PRIMARY KEY AUTOINCREMENT,
                fecha_envio TEXT NOT NULL,
                ruta_fichero_xml TEXT,
                estado_envio TEXT NOT NULL DEFAULT 'PENDIENTE'
            )
            """,

            // peregrino
            """
            CREATE TABLE IF NOT EXISTS peregrino (
                id_peregrino INTEGER PRIMARY KEY AUTOINCREMENT,
                rol TEXT DEFAULT 'VI',
                nombre TEXT,
                apellido1 TEXT,
                apellido2 TEXT,
                tipo_documento TEXT,
                numero_documento TEXT,
                fecha_nacimiento TEXT,
                nacionalidad TEXT,
                sexo TEXT,
                direccion TEXT,
                direccion_complementaria TEXT,
                codigo_municipio TEXT,
                nombre_municipio TEXT,
                codigo_postal TEXT,
                pais TEXT,
                telefono1 TEXT,
                telefono2 TEXT,
                correo TEXT,
                parentesco TEXT,
                soporte_documento TEXT
            )
            """,

            // estancia
            """
            CREATE TABLE IF NOT EXISTS estancia (
                id_estancia INTEGER PRIMARY KEY AUTOINCREMENT,
                id_envio_xml INTEGER,
                id_cama INTEGER,
                id_albergue INTEGER NOT NULL,
                id_peregrino INTEGER NOT NULL,
                fecha_contrato TEXT,
                fecha_entrada TEXT NOT NULL,
                fecha_salida_prevista TEXT,
                fecha_salida_real TEXT,
                numero_habitaciones INTEGER NOT NULL DEFAULT 1,
                id_grupo TEXT DEFAULT NULL,
                internet_incluido INTEGER NOT NULL DEFAULT 1
                    CHECK (internet_incluido IN (0,1)),
                referencia_contrato TEXT,
                estado_estancia TEXT NOT NULL DEFAULT 'ACTIVA',
                lugar_inicio_camino TEXT,
                ultimo_albergue TEXT,
                camino_destino TEXT,
                num_personas_contrato TEXT,
                tipo_pago TEXT,
                titular_pago TEXT,
                caducidad_tarjeta TEXT,
                medio_pago TEXT,
                fecha_pago TEXT,
                observaciones TEXT,
                CHECK (fecha_salida_prevista IS NULL OR fecha_salida_prevista > fecha_entrada),
                CHECK (fecha_salida_real IS NULL OR fecha_salida_real > fecha_entrada),
                FOREIGN KEY (id_envio_xml)
                    REFERENCES envio_xml(id_envio_xml)
                    ON DELETE SET NULL,
                FOREIGN KEY (id_cama)
                    REFERENCES cama(id_cama)
                    ON DELETE SET NULL,
                FOREIGN KEY (id_albergue)
                    REFERENCES albergue(id_albergue)
                    ON DELETE CASCADE,
                FOREIGN KEY (id_peregrino)
                    REFERENCES peregrino(id_peregrino)
                    ON DELETE CASCADE
            )
            """,

            // pago
            """
            CREATE TABLE IF NOT EXISTS pago (
                id_pago INTEGER PRIMARY KEY AUTOINCREMENT,
                id_estancia INTEGER NOT NULL,
                tipo_pago TEXT NOT NULL DEFAULT 'EFECTIVO',
                fecha_pago TEXT NOT NULL,
                medio_pago TEXT,
                titular_pago TEXT,
                caducidad_tarjeta TEXT,
                FOREIGN KEY (id_estancia)
                    REFERENCES estancia(id_estancia)
                    ON DELETE CASCADE
            )
            """,

            // preregistro
            """
            CREATE TABLE IF NOT EXISTS preregistro (
                id_preregistro INTEGER PRIMARY KEY AUTOINCREMENT,
                id_albergue INTEGER NOT NULL,
                id_peregrino INTEGER,
                id_preregistro_nube TEXT,
                fecha_envio TEXT,
                fecha_prevista_llegada TEXT,
                estado_preregistro TEXT NOT NULL DEFAULT 'PENDIENTE',
                rol TEXT NOT NULL DEFAULT 'VI',
                nombre TEXT,
                apellido1 TEXT,
                apellido2 TEXT,
                tipo_documento TEXT,
                numero_documento TEXT,
                fecha_nacimiento TEXT,
                nacionalidad TEXT,
                sexo TEXT,
                direccion TEXT,
                direccion_complementaria TEXT,
                codigo_municipio TEXT,
                nombre_municipio TEXT,
                codigo_postal TEXT,
                pais TEXT,
                telefono1 TEXT,
                telefono2 TEXT,
                correo TEXT,
                parentesco TEXT,
                FOREIGN KEY (id_albergue)
                    REFERENCES albergue(id_albergue)
                    ON DELETE CASCADE,
                FOREIGN KEY (id_peregrino)
                    REFERENCES peregrino(id_peregrino)
                    ON DELETE SET NULL
            )
            """,

            // producto
            """
            CREATE TABLE IF NOT EXISTS producto (
                id_producto INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                precio REAL NOT NULL DEFAULT 0.0,
                activo INTEGER NOT NULL DEFAULT 1,
                es_estancia INTEGER NOT NULL DEFAULT 0
            )
            """,

            // usuario
            """
            CREATE TABLE IF NOT EXISTS usuario (
                id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre_usuario TEXT NOT NULL UNIQUE,
                password_hash TEXT NOT NULL,
                rol TEXT NOT NULL DEFAULT 'RECEPCION',
                activo INTEGER NOT NULL DEFAULT 1
            )
            """,

            // venta_linea
            """
            CREATE TABLE IF NOT EXISTS venta_linea (
                id_linea INTEGER PRIMARY KEY AUTOINCREMENT,
                id_estancia INTEGER NOT NULL,
                id_producto INTEGER,
                nombre_snapshot TEXT NOT NULL,
                precio_unitario REAL NOT NULL DEFAULT 0.0,
                cantidad INTEGER NOT NULL DEFAULT 1,
                FOREIGN KEY (id_estancia) REFERENCES estancia(id_estancia),
                FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
            )
            """
        };

        for (String sql : sqls) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.executeUpdate();
            }
        }

        // Triggers anti solapamiento de camas
        crearTriggersSiNoExisten(conn);
        
        return eraVacia;
        
    }

    /**
	 * Crea triggers para evitar que se asignen estancias solapadas a la misma cama.
	 * El trigger se activa antes de insertar o actualizar una estancia, y comprueba si hay otra estancia
	 * para la misma cama que se solape en las fechas. Si es así, aborta la operación con un error.
	 */
    private static void crearTriggersSiNoExisten(Connection conn) throws Exception {

        String triggerInsert = """
            CREATE TRIGGER IF NOT EXISTS trg_estancia_no_solape_insert
            BEFORE INSERT ON estancia
            WHEN NEW.id_cama IS NOT NULL AND NEW.estado_estancia <> 'CANCELADA'
            BEGIN
                SELECT CASE
                    WHEN EXISTS (
                        SELECT 1 FROM estancia e
                        WHERE e.id_cama = NEW.id_cama
                          AND e.estado_estancia <> 'CANCELADA'
                          AND (
                              COALESCE(e.fecha_salida_real, e.fecha_salida_prevista) IS NULL
                              OR COALESCE(e.fecha_salida_real, e.fecha_salida_prevista) > NEW.fecha_entrada
                          )
                          AND (
                              COALESCE(NEW.fecha_salida_real, NEW.fecha_salida_prevista) IS NULL
                              OR COALESCE(NEW.fecha_salida_real, NEW.fecha_salida_prevista) > e.fecha_entrada
                          )
                    )
                    THEN RAISE(ABORT, 'La cama ya tiene una estancia que se solapa en esas fechas')
                END;
            END
            """;

        String triggerUpdate = """
            CREATE TRIGGER IF NOT EXISTS trg_estancia_no_solape_update
            BEFORE UPDATE OF id_cama, fecha_entrada, fecha_salida_prevista, fecha_salida_real, estado_estancia ON estancia
            WHEN NEW.id_cama IS NOT NULL AND NEW.estado_estancia <> 'CANCELADA'
            BEGIN
                SELECT CASE
                    WHEN EXISTS (
                        SELECT 1 FROM estancia e
                        WHERE e.id_cama = NEW.id_cama
                          AND e.id_estancia <> NEW.id_estancia
                          AND e.estado_estancia <> 'CANCELADA'
                          AND (
                              COALESCE(e.fecha_salida_real, e.fecha_salida_prevista) IS NULL
                              OR COALESCE(e.fecha_salida_real, e.fecha_salida_prevista) > NEW.fecha_entrada
                          )
                          AND (
                              COALESCE(NEW.fecha_salida_real, NEW.fecha_salida_prevista) IS NULL
                              OR COALESCE(NEW.fecha_salida_real, NEW.fecha_salida_prevista) > e.fecha_entrada
                          )
                    )
                    THEN RAISE(ABORT, 'La cama ya tiene una estancia que se solapa en esas fechas')
                END;
            END
            """;

        try (PreparedStatement ps = conn.prepareStatement(triggerInsert)) {
            ps.executeUpdate();
        }
        try (PreparedStatement ps = conn.prepareStatement(triggerUpdate)) {
            ps.executeUpdate();
        }
        
        
        
        
    }
    
    
    
    
}



