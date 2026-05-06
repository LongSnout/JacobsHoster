package config;

import java.io.File;

public class AppConfig {

    private AppConfig() {}

    public static final String APP_VERSION = "1.0.6";
    public static final String APP_NAME = "Jacobs Hoster";

    // Ruta base en AppData (Windows)
    private static final String APP_DATA_DIR = System.getenv("APPDATA") + File.separator + "JacobsHoster";

    // URL de la base de datos
    public static final String DB_URL = "jdbc:sqlite:" + APP_DATA_DIR + File.separator + "jacobs_hoster.db";

    // Método para asegurar que la carpeta existe
    public static void initAppDirectories() {
        File dir = new File(APP_DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

	
	
	//Identidad del albergue
	//TODO: esto se saca de un campo de la base de datos, pero por ahora lo dejamos en 1, porque de momento solo hay un albergue
	// En el futuro, si se añaden más albergues, cada uno tendrá su ID único, esto de cara a implementar la sincronización con la nube, para que cada albergue solo sincronice sus datos.
	// o para manejar múltiples albergues desde una misma instalación, quizá por el perfil de gerente o qué sé yo, para ver estadísticas de varios albergues, etc.
	// ahora siempre es 1, está ya metido en la DB.
	
	public static final int ID_ALBERGUE = 1;

	
    
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
    
    public static final String SYNC_KEY = "Pendiente :) "; // al final esta no se usa de momento, o sea que TODO:
    																// ahora se usa la apikey que está más abajo, pero esto queda pendiente para este verano
    																// porque mejora la seguridad y permite que cada albergue reciba solo sus prerregistros, o sea, los
    																// prerregistros de otros albergues no se sincronizan, lo cual es importante para la privacidad de los datos.
    
    
	//Base de datos en la nube
    public static final String API_BASE_URL = "https://api.snoutserv.com";

    public static final String API_TOKEN = "jacobs-api-key-2026"; // Esta es la API Key que se usa provisionalmente (lo dicho arriba), en realidad esta si la usa
    																// solo la app web sería lo suyo, solo para enviar datos a la nube,
    																// no para recibir prerregistros, porque para eso lo suyo es el sistema de emparejado que se
    																// describe arriba, que es más seguro y privado.
    
    
    // Cada cuanto se hace el pull de datos desde la nube (segundos).
    public static final int SYNC_INTERVAL_SECONDS = 30; // Ahora está rapido para probar y mostrar la app y tal, pero luego lo bajaremos a 2 o 3 minutos para no gastarme el servidor,

    
    
    // Ruta donde se guardan los XMLs del Ministerio
    // Se puede cambiar desde la interfaz de usuario en Edición, en la menu bar
    public static String XML_OUTPUT_DIR =
    	    System.getProperty("user.home")
    	    + File.separator
    	    + "Desktop";
	
	
}
