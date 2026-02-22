package config;

public class AppConfig {


	
	private AppConfig() {}
	
	public static final String APP_VERSION = "1.0.0";
	public static final int CONFIG_VERSION = 1;
	
	//base de datos local.
	public static final String DB_URL = "jdbc:sqlite:data/jacobs_hoster.db";
	
	
	//Identidad del albergue
	//TODO: esto debería cargarse desde la base de datos una vez creada.
	
	public static final int ID_ALBERGUE = 1;

    public static final String CODIGO_ESTABLECIMIENTO = "AST-ALB-001";
	
    
    //clave de sincronización con la nube, cada albergue tendrá la suya.
    // TODO: Revisar esta histira más adelante, hay que pensar en la seguridad
    	// para que nadie pueda acceder a los datos de otro albergue.
    	// Es la clave que identifica al albergue de cara a la API.
    
    	/* TODO: Idea de seguridad para la clave de sincronización
Flujo:
En la app de escritorio: “Conectar este PC al albergue”
El usuario mete su email de albergue.
El servidor comprueba que ese email está en la lista blanca.
El servidor genera un código de emparejado de un solo uso (o enlace con código).
Envías email con un botón: “Autorizar este PC”.
Cuando lo autoriza, el servidor emite:
install_id (id de instalación)
install_secret (secreto de instalación)
y opcionalmente un refresh_token para sacar tokens cortos
La app guarda eso localmente (en tu SQLite o en un keystore).
A partir de ahí, la app se autentica con:
install_id + install_secret
y el servidor solo entrega preregistros del albergue asociado a esa instalación.
Ventaja brutal
Si alguien se baja la app y “cambia el token”:
no puede, porque no tiene una instalación emparejada
y sin acceso al email del albergue no puede emparejar una nueva
Y si un albergue pierde un PC:
revocas esa instalación y listo.
    	 */
    
    public static final String SYNC_KEY = "CAMBIAR_EN_PRODUCCION";
    
	//Base de datos en la nube
    
    // TODO
    public static final String API_BASE_URL = "https://tudominio/api";

    public static final String API_TOKEN = "CAMBIAR_EN_PRODUCCION";
    
    
    // Cada cuanto se hace el pull de datos desde la nube (segundos).
    public static final int SYNC_INTERVAL_SECONDS = 10;

    // Cuántos días mantener preregistros pendientes antes de caducar
    public static final int PRERREGISTRO_CADUCIDAD_DIAS = 3;
    
	
	
}
