package loro.util;

import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.EOFException;
import java.io.File;
import java.util.Properties;


///////////////////////////////////////////////////////////////
/**
 * Registro de seguimiento
 *
 * @author Carlos Rueda
 */
public class Logger
{
	/** La unica instancia de esta clase. */
	private static Logger logger = null;

	/** En donde se escriben los mensajes. */
	private PrintWriter pw;
	/////////////////////////////////////////////////////////////
	/**
	 * Crea un manejador para seguimiento.
	 */
	private Logger(String prg)
	{
		this.prg = prg;
		pw = null;

		String nombre_log = System.getProperty("loro.log");
		if ( nombre_log == null || nombre_log.length() == 0 )
			return;

		// Intente abrir el archivo para log:
		try
		{
			if ( nombre_log.equals("System.out") )
			{
				pw = new PrintWriter(System.out);
			}
			else if ( nombre_log.equals("System.err") )
			{
				pw = new PrintWriter(System.err);
			}
			else
			{
				pw = new PrintWriter(
					new FileOutputStream(nombre_log), true
				);
			}
		}
		catch(Exception e)
		{
			System.err.println(
				"Advertencia: No se pudo abrir archivo para log: "
				+nombre_log+
				": " +e.getClass()+ ": " + e.getMessage()
			);
			return;
		}

		log("@@@@ INICIO " +prg+ " " +(new java.util.Date()));

		log("***********Propiedades del sistema:");
		Properties props = System.getProperties();
		props.list(pw);
		log("***********Fin propiedades del sistema");
	}
	/////////////////////////////////////////////////////////////
	/**
	 * Cierra el archivo de registro.
	 */
	public void close()
	{
		if ( pw == null )
			return;

		log("@@@@ FIN " +prg+ " " +(new java.util.Date()));
		pw.close();
		pw = null;
	}
	/////////////////////////////////////////////////////////////
	/**
	 * Crea el manejador de log para esta ejecucion.
	 */
	public static Logger createLogger(String prg)
	{
		if ( logger == null )
			logger = new Logger(prg);

		return logger;
	}
	/////////////////////////////////////////////////////////////
	/**
	 * Obtiene el manejador de log para esta ejecucion.
	 * Debe llamarse despues de crearLogger(String prg) para saber ya
	 * el nombre del programa en ejecucion.
	 */
	public static Logger getLogger()
	{
		if ( logger == null )
			logger = new Logger("??");

		return logger;
	}
	/////////////////////////////////////////////////////////////
	/**
	 * Escribe un mensaje en archivo de registro.
	 */
	public void log(String m)
	{
		if ( pw == null )
			return;

		//pw.println("[" +new java.util.Date().+ "] " +m);
		pw.println(m);
		pw.flush();
	}

	/** Nombre del programa en ejecucion. */
	private String prg;
}