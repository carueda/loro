package loroedi;

import java.util.Properties;
import java.io.*;

/////////////////////////////////////////////////////////////////////
/**
 * Manejador de la configuracion global de Loro asociada a la version
 * actual.
 * Archivo de propiedades: .loro/loroedi.conf
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class Configuracion
{
	/** Base para nombres de propiedades asociadas a la version actual. */
	private static final String BASE = "loroedi." +Info.obtVersion();

	/** Directorio de instalacion de la version actual. */
	public static final String DIR = BASE+ ".dir";

	/** Contador de ejecuciones completas de la version actual. */
	public static final String VC = BASE+ ".n";

	/** Directorio utilizado para informacion general de configuracion. */
	private static String confDirectory;

	/** Loro configuration file full name. */
	private static String loro_conf_name;


	/** Las propiedades. */
	private static Properties props = new Properties();

	/** encabezado para el archivo de configuracion. */
	private static final String header =
		"LoroEDI Configuration " +Info.obtVersion()+ " -- DO NOT EDIT!"
	;

	static
	{
		// determine si estamos en windows para precisar el nombre del
		// directorio:
		boolean is_win =
			System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0;

		// nombre del directorio (user.home/.loro):
		confDirectory = System.getProperty("user.home")+ "/" +(is_win ? "_" : ".")+ "loro";

		// nombre completo del archivo de configuracion:
		loro_conf_name = confDirectory+ "/loroedi.conf";
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el directorio de base para guardar configuraciones y similares.
	 */
	public static String getConfDirectory()
	{
		return confDirectory;
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene una propiedad de Loro.
	 * Si la propiedad no esta definida, se retorna "".
	 */
	public static String getProperty(String key)
	{
		return props.getProperty(key, "");
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Crea las propiedades de LoroEDI.
	 *
	 * Para esto, lee el archivo user.home/.loro/loroedi.conf (si estamos
	 * en windows es user.home/_loro/loroedi.conf).
	 * Si es necesario (ej. primera ejecucion), se crea.
	 *
	 * La propiedades a establecer son: donde [CV] es codigo de la version actual:
	 *  loroedi.[CV].dir  - directorio de instalacion
	 *  loroedi.[CV].n    - numero de ejecuciones completas de la version actual.
	 *
	 * @throws Exception  Problema para cargar/crear el archivo de configuracion.
	 */
	public static void load() throws Exception {
		File conf_file = new File(loro_conf_name);
		if ( !conf_file.exists() ) {
			try {
				File conf_dir = new File(confDirectory);
				if ( !conf_dir.exists() ) {
					conf_dir.mkdirs();
					if ( !conf_dir.isDirectory() ) {
						throw new Exception(
							"No se pudo crear el directorio para las propiedades de Loro:\n" +
							"  " +conf_dir+ "\n" +
							"\n"
						);
					}
				}
				// creacion solo con header
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(conf_file));
				props.store(out, header);
				out.close();
			}
			catch (IOException ex ) {
				throw new Exception(
					"No se pudo crear el archivo de propiedades de Loro:\n" +
					"  " +loro_conf_name+ "\n" +
					"\n" +
					"El problema ha sido:\n" +
					"  " +ex.getMessage()+ "\n"
				);
			}
		}
	
		try {
			// load properties:
			BufferedInputStream br =
				new BufferedInputStream(new FileInputStream(conf_file))
			;
			props.load(br);
		}
		catch (IOException ex ) {
			throw new Exception(
				"No se pudo cargar el archivo de propiedades de LoroEDI:\n" +
				"  " +loro_conf_name+ "\n" +
				"\n" +
				"El problema ha sido:\n" +
				"  " +ex.getMessage()+ "\n" +
				"\n" +
				"Se recomienda hacer una reintalacion de Loro\n"
			);
		}
		
		// LOROEDIDIR system property:
		String loro_dir = System.getProperty("LOROEDIDIR");
		if ( loro_dir == null ) {
			throw new Exception(
				"loroedi.jar deberia haberse llamado con LOROEDIDIR definido.\n"+
				"Esto no deberia ocurrir. Por favor notificar esta anomalia."
			);
		}
		System.out.println("Setting property " +DIR+ "=[" +loro_dir+ "]");
		props.setProperty(DIR, loro_dir);

		// Manejo del numero de ejecuciones de la version actual del sistema:
		String num = props.getProperty(VC);
		if ( num == null )
		{
			// primera vez: ponga esta propiedad en cero.  Vea store().
			props.setProperty(VC, "0");
		}
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Retorna el numero de ejecuciones completas de la version actual.
	 */
	public static int getVC()
	{
		int n = 0;
		try
		{
			n = Integer.parseInt(props.getProperty(VC, "0"));
		}
		catch ( Exception ex )
		{
			// ignore
		}
		return n;
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Almacena las propiedades actuales de Loro.
	 *
	 * @throws Exception		Problema para almacenar en el archivo de configuracion.
	 */
	public static void store()
	throws Exception
	{
		try
		{
			BufferedOutputStream out =
				new BufferedOutputStream(new FileOutputStream(loro_conf_name))
			;

			// Manejo del numero de ejecuciones de la version actual del sistema:
			int n = 0;
			try
			{
				n = Integer.parseInt(props.getProperty(VC, "0"));
			}
			catch ( Exception ex )
			{
				// ignore
			}
			// incremente contador y asigne:
			props.setProperty(VC, "" + (++n));

			props.store(out, header);
			out.close();
		}
		catch (IOException ex )
		{
			throw new Exception(
				"No se pudo almacenar en el archivo de propiedades de loro:\n" +
				"  " +loro_conf_name+ "\n" +
				"\n" +
				"El problema ha sido:\n" +
				"  " +ex.getMessage()
			);
		}
	}
}