package loro;

import loro.impl.*;
import loro.arbol.*;
import loro.compilacion.*;
import loro.derivacion.*;
import loro.ejecucion.*;
import loro.util.*;
import loro.util.Logger;
import loro.tipo.Tipo;
import loro.tabsimb.TablaSimbolos;
import loro.visitante.Descriptor;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.io.*;
import java.util.*;

//////////////////////////////////////////////////////////////
/**
 * Punto de entrada principal al núcleo Loro.
 *
 * @author Carlos Rueda
 */
public final class Loro 
{
	/** para toArray. */
	private static final File[] EMPTY_FILE_ARRAY = new File[0];

	/** Está iniciado el núcleo? */
	private static boolean iniciado = false;

	/** Nombre del sistema (leido de recurso). */
	private static String nombre = null;

	/** Version del sistema (leido de recurso). */
	private static String version;
	
	/** Build del sistema (leido de recurso). */
	private static String build;
	
	/** Cargador de clases para los ejecutores. */
	private static LoroClassLoader loroClassLoader;
	
	/**
	 * Directorio de extensiones. 
	 * De allí se toman en cuenta archivos *.lar y *.jar.
	 */
	private static String ext_dir = null;
	
	/**
	 * Directorio con subdirectorios para búsqueda.
	 * Todo subdirectorio de este directorio es tomado en cuenta 
	 * en la ruta de búsqueda de unidades compiladas.
	 */
	private static String paths_dir = null;
	
	/** El logger. */
	private static Logger logger = null;

	/** El manejador de unidades. */
	private static ManejadorUnidades mu;
	
	/** 
	 * Lista de todos los archivos lar de extensión.
	 */
	private static List extensionFiles;
	

	/** El compilador. */
	private static ICompilador compilador;

	/** El documentador. */
	private static IDocumentador documentador;

	/** Tabla de símbolos de base y común para todos los intérpretes
	 * interactivos que se creen. */
	private static TablaSimbolos tabSimbBase;
	
	
	////////////////////////////////////////////////////////
	// Se instancia solo para acceder al classloader.
	private Loro() {}
	
	////////////////////////////////////////////////////////
	/**
	 * Obtiene los datos del recurso loro/info.properties.
	 * Este servicio no requiere que el núcleo haya sido iniciado.
	 */
	private static void _obtInfo() 
	{
		if ( nombre == null )
		{
			// valores por defecto:
			nombre = "Loro";
			version = "v";
			
			ClassLoader	cl = new Loro().getClass().getClassLoader();
			Properties props = new Properties(); 
			InputStream is = cl.getResourceAsStream("loro/info.properties");
			if ( is == null )
			{
				System.err.println(
"!!!!!! Recurso loro/info.properties no encontrado.\n" +
"!!!!!! El sistema Loro no ha sido compilado correctamente.\n"+
"!!!!!! Se dejan valores de informacion por defecto."
				);
				return;
			}
			try
			{
				props.load(is);
				is.close();
			}
			catch(IOException ex)
			{
				// ignore.
			}
			
			nombre  = props.getProperty("loro.nombre");
			version = props.getProperty("loro.version");
			build   = props.getProperty("loro.build");
		}
	}

	////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre del sistema Loro.
	 * Este servicio puede llamarse en cualquier momento independientemente
	 * de si el núcleo ha sido iniciado o no.
	 */
	public static String obtNombre()
	{
		_obtInfo();
		return nombre;
	}
	////////////////////////////////////////////////////////////
	/**
	 * Obtiene la versión del sistema Loro.
	 * Este servicio puede llamarse en cualquier momento independientemente
	 * de si el núcleo ha sido iniciado o no.
	 */
	public static String obtVersion()
	{
		_obtInfo();
		return version;
	}
	////////////////////////////////////////////////////////////
	/**
	 * Obtiene el "build" del sistema Loro.
	 * Este servicio puede llamarse en cualquier momento independientemente
	 * de si el núcleo ha sido iniciado o no.
	 */
	public static String obtBuild()
	{
		_obtInfo();
		return build;
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Cierra el núcleo Loro.
	 * Después de esta operación NO debería invocarse ningún servicio del
	 * núcleo salvo volver a iniciarlo a través de configurar(). No seguir
	 * esta indicación producirá errores estilo NPE o similares y/o otros
	 * comportamientos anómalos, así que asegúrese de mantener sus
	 * invariantes.
	 *
	 * @throws IllegalStateException  Si el núcleo no se encuentra iniciado.
	 */
	public static void cerrar()
	{
		_verificarIniciado();
		
		_destruirManejadorUnidades();
		compilador = null;
		documentador = null;
		loroClassLoader = null;
		if ( logger != null )
		{
			logger.close();
			logger = null;
		}
		iniciado = false;
	}

	///////////////////////////////////////////////////////////////////
	/**
	 * Establece la configuración de trabajo para el sistema Loro.
	 * Salvo algunos métodos informativos, éste debe ser el primero en llamarse
	 * ANTES de cualquier otro servicio.
	 *
	 * El mecanismo para lograr el efecto de indicar el directorio de destino
	 * para guardar compilados es:
	 *
	 * <pre>
	 *	ICompilador compilador = Loro.obtCompilador();
	 * 	compilador.ponDirectorioDestino(oro_dir);
	 * </pre> 
	 *
	 * Justo a continuación de este método debe llamarse verificarSistema(),
	 * salvo cuando se quiere sólo el sistema básico que permita precisamente 
	 * preparar los elementos de apoyo (por ejemplo, para poder compilar la 
	 * clase raiz del lenguaje Loro).
	 *
	 * @param ext_dir Directorio de extensiones de donde se leen archivos *.jar y *.lar.
	 *                Puede ser null.
	 *
	 * @param paths_dir Directorio con subdirectorios y archivos .lar de búsqueda.
	 *
	 * @throws LoroException  Si la inicialización falla.
	 *
	 * @throws IllegalStateException  Si el núcleo ya se encuentra iniciado.
	 */
	public static void configurar(
		String ext_dir_,
		String paths_dir_
	)
	throws LoroException
	{
		if ( iniciado )
		{
			throw new IllegalStateException("El núcleo ya se encuentra iniciado!");
		}
		
		Logger.createLogger(obtNombre()+ " " +obtVersion()+ " (Build " +obtBuild()+ ")");
		ext_dir = ext_dir_;
		paths_dir = paths_dir_;

		logger = Logger.getLogger();
		
		log("  Extensiones      : " +ext_dir);
		log("  Rutas adicionales: " +paths_dir);

		_crearManejadorUnidades();
		
		if ( ext_dir != null )
		{
			// ponga el cargador de clases:
			// este cargador sera utilizado para los ejecutores:
			loroClassLoader = new LoroClassLoader();
			try
			{
				loroClassLoader.ponDirectorioExtensiones(ext_dir);
			}
			catch(MalformedURLException ex)
			{
				throw new LoroException("Error con el directorio de extensiones: "+
					ex.getMessage()
				);
			}
		}

		compilador = new CompiladorImpl();
		documentador = new DocumentadorImpl();
		
		iniciado = true;
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Extrae el archivo dado xxx.lar en xxx/ 
	 *
	 * @param file El archivo extensión. 
	 * @param force true para obligar la expansión aunque xxx/ ya exista. 
	 * @return El directorio asociado.
	 * @throws Exception En caso de problemas.
	 * @throws RuntimeException Si el nombre del archivo no termina en .lar
	 */
	public static File expandExtensionFile(File file, boolean force)
	throws Exception
	{
		String fullname = file.getAbsolutePath();
		if ( fullname.toLowerCase().endsWith(".lar") )
		{
			File dir = new File(fullname.substring(0, fullname.length() - 4));
			if ( force || !dir.exists() )
			{
				// extract file into dir:
				IOroLoader oroLoader = new ZipFileOroLoader(file);
				Util.copyExtensionToDirectory(oroLoader, dir, null);
			}
			return dir;
		}
		else
		{
			throw new RuntimeException("expandExtensionFile: Not a .lar file");
		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Extrae cada archivo paths_dir/xxx.lar en paths_dir/xxx/ en caso
	 * que paths_dir/xxx/ no exista.
	 * Este es un preparativo preliminar en _crearManejadorUnidades(). 
	 */
	private static void _expandExtensions()
	{
		if ( paths_dir != null )
		{
			File dir_file = new File(paths_dir);
			File[] files = dir_file.listFiles();
			if ( files != null )
			{
				for (int i = 0; i < files.length; i++)
				{
					File file = files[i];
					if ( file.getName().toLowerCase().endsWith(".lar") )
					{
						try
						{
							expandExtensionFile(file, false);
						}
						catch(Exception ex)
						{
							log(file+ ": " +ex.getMessage());
						}
					}
				}
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Se encarga de crear el ManejadorUnidades único con todos los
	 * todos los directorios y archivos *.lar bajo paths_dir/* como 
	 * ruta de búsqueda de unidades compiladas. Los archivo *.lar
	 * son primero expandidos.
	 * También se agregan al final todos los archivos ext_dir/*.lar.
	 */
	private static void _crearManejadorUnidades()
	{
		_expandExtensions();
		
		// lista completa de extensiones y directorios de búsqueda:
		List list = new ArrayList();
		
		// Agregar todos los directorios en paths_dir/* ...
		if ( paths_dir != null )
		{
			File dir_file = new File(paths_dir);
			File[] files = dir_file.listFiles();
			if ( files != null )
			{
				for (int i = 0; i < files.length; i++)
				{
					File file = files[i];
					if ( file.isDirectory() )
						list.add(file);
				}
			}
		}

		// lista de todos los archivos lar de extensión.
		extensionFiles = new ArrayList();

		// Se agregan al final los ext_dir/*.lar ...
		if ( ext_dir != null )
		{
			File dir_file = new File(ext_dir);
			File[] files = dir_file.listFiles();
			if ( files != null )
			{
				for (int i = 0; i < files.length; i++)
				{
					File file = files[i];
					if ( file.getName().toLowerCase().endsWith(".lar") )
					{
						extensionFiles.add(file);
						list.add(file);
					}
				}
			}
		}


		File[] files = (File[]) list.toArray(EMPTY_FILE_ARRAY);

		// ... para crear el manejador de unidades:
		mu = ManejadorUnidades.crearManejadorUnidades(files);

		// generar documentacion en doc_dir para las librerias de extension:
//		if ( a.length > 0 )
//		{
//			 no deberia hacerse siempre pero por ahora lo hacemos :-/
//			
//			log("Generando documentacion para extensiones en: " +doc_dir);
//			for ( int i = 0; i < a.length; i++ )
//			{
//				File file = a[i];
//				try
//				{
//					String err = loro.doc.Documentador.procesarArchivoZip(
//						file.getAbsolutePath(), doc_dir
//					);
//				}
//				catch ( IOException ex )
//				{
//					log(file+ ": " +ex.getMessage());
//				}
//			}
//		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Se encarga de destruir el ManejadorUnidades único
	 */
	private static void _destruirManejadorUnidades()
	{
		ManejadorUnidades.destruirManejadorUnidades();
		mu = null;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Rutina facilitadora para escribir mensajes al logger.
	 * Si el núcleo ha sido iniciado, este método delega en el logger
	 * creado como parte de dicha inicialización; en caso contrario,
	 * simplemente se escribe el mensaje por System.err.
	 */
	public static void log(String msg)
	{
		if ( logger != null )
		{
			logger.log(msg);
		}
		else
		{
			System.err.println(msg);
		}
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la tabla de simbolos común.
	 */
	public static ISymbolTable getSymbolTable()
	{
		_verificarIniciado();
		
		if ( tabSimbBase == null )
			tabSimbBase = new TablaSimbolos();

		return tabSimbBase;
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Crea un interprete para acciones interactivas.
	 *
	 * @param newSymTab Si true, el interprete queda asociado a una tabla de
	 *                  símbolos nueva e independiente de la tabla compartida.
	 *                  En caso contrario el intéprete toma como base una tabla
	 *                  que será compartida por todos los intérpretes instanciados
	 *                  con este parámetro dado en false.
	 *
	 * @param obspp     Si != null, se crea un ejecutor con seguimiento paso-a-paso.
	 *                  See InterpreteImpl.
	 */
	public static IInterprete crearInterprete(Reader r, Writer w, boolean newSymTab, IObservadorPP obspp)
	{
		_verificarIniciado();
		
		if ( newSymTab )
		{
			return new InterpreteImpl(r, w, new TablaSimbolos(), loroClassLoader, obspp);
		}
		else if ( tabSimbBase == null )
		{
			tabSimbBase = new TablaSimbolos();
		}
		
		return new InterpreteImpl(r, w, tabSimbBase, loroClassLoader, obspp);
	}


	///////////////////////////////////////////////////////////////////////
	/**
	 * Dice si la unidad dada se puede invocar directamente.
	 * Este depende ahora que la unidad corresponda a un algoritmo,
	 * y que sus parametros de entrada puedan pedirse
	 * interactivamente al usuario.
	 */
	public static boolean esEjecutableDirectamente(IUnidad u)
	{
		_verificarIniciado();
		
		if ( ! (u instanceof NAlgoritmo) )
		{
			return false;
		}

		NAlgoritmo alg = (NAlgoritmo) u;
		
		NDeclaracion[] pent = alg.obtParametrosEntrada();
		for (int i = 0; i < pent.length; i++)
		{
			Tipo t = pent[i].obtTipo();
			if ( ! t.esBasico() && ! t.esCadena() )
			{
				return false;
			}
		}
		return true;

	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el compilador unico.
	 */
	public static ICompilador obtCompilador()
	{
		_verificarIniciado();
		
		return compilador;
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Crea un ejecutor terminable externamente.
	 */
	public static IEjecutor crearEjecutorTerminableExternamente()
	{
		_verificarIniciado();
		
		return new EjecutorImpl(
			Ejecutores.EJECUTOR_TERMINABLE_EXTERNAMENTE,
			loroClassLoader
		);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el documentador unico.
	 */
	public static IDocumentador obtDocumentador()
	{
		_verificarIniciado();
		
		return documentador;
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la descripcion de la especificacion de un algoritmo.
	 *
	 * @param u La unidad que debe corresponder a un algoritmo.
	 *
	 * @return La descripcion de la especificacion de un algoritmo.
	 *         null si la unidad no es un algoritmo.
	 */
	public static String obtDescripcionEspecificacion(IUnidad u)
	{
		_verificarIniciado();
		
		if ( u instanceof NAlgoritmo )
		{
			NAlgoritmo alg = (NAlgoritmo) u;
			String[] nespec = alg.obtNombreEspecificacion();
			String sespec = loro.util.Util.obtStringRuta(nespec);
			NEspecificacion espec = mu.obtEspecificacion(sespec);
			if ( espec != null )
			{
				return espec.obtDescripcion();
			}
		}

		return null;
	}	

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene una descripción concisa del tipo del nodo dado.
	 *
	 * @param n   El nodo a describir.
	 *
	 * @return La descripción concisa del tipo del nodo dado.
	 */
	public static String getNodeDescription(INodo n)
	{
		_verificarIniciado();
		return Descriptor.getDescription(n);
	}
		
	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene una especificación.
	 *
	 * @param qname Nombre cualificado de la especificación, 
	 *              salvo si se refiere al paquete automático.
	 *
	 * @return La unidad.
	 *         null si no se encuentra.
	 */
	public static IUnidad.IEspecificacion getSpecification(String qname)
	{
		_verificarIniciado();
		
		IUnidad.IEspecificacion u = mu.obtEspecificacion(qname);
		if ( u == null )
		{
			// intente paquete automático
			if ( qname.indexOf(":") < 0 )    // qname debe ser simple
			{
				String auto_pkg = mu.obtNombrePaqueteAutomatico();
				u = mu.obtEspecificacion(auto_pkg+ "::" +qname);
			}
		}
		return u;
	}	

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene un algoritmo.
	 *
	 * @param qname Nombre cualificado del algoritmo,
	 *              salvo si se refiere al paquete automático.
	 *
	 * @return La unidad.
	 *         null si no se encuentra.
	 */
	public static IUnidad.IAlgoritmo getAlgorithm(String qname)
	{
		_verificarIniciado();
		
		IUnidad.IAlgoritmo u = mu.obtAlgoritmo(qname);
		if ( u == null )
		{
			// intente paquete automático
			if ( qname.indexOf(":") < 0 )    // qname debe ser simple
			{
				String auto_pkg = mu.obtNombrePaqueteAutomatico();
				u = mu.obtAlgoritmo(auto_pkg+ "::" +qname);
			}
		}
		return u;
	}	

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene una clase.
	 *
	 * @param qname Nombre cualificado de la clase,
	 *              salvo si se refiere al paquete automático.
	 *
	 * @return La unidad.
	 *         null si no se encuentra.
	 */
	public static IUnidad.IClase getClass(String qname)
	{
		_verificarIniciado();
		
		IUnidad.IClase u = mu.obtClase(qname);
		if ( u == null )
		{
			// intente paquete automático
			if ( qname.indexOf(":") < 0 )    // qname debe ser simple
			{
				String auto_pkg = mu.obtNombrePaqueteAutomatico();
				u = mu.obtClase(auto_pkg+ "::" +qname);
			}
		}
		return u;
	}	

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre del paquete automático.
	 *
	 * @return el nombre del paquete automático.
	 */
	public static String getAutomaticPackageName()
	{
		_verificarIniciado();
		
		return mu.obtNombrePaqueteAutomatico();
	}	

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la estrategia dada a un algoritmo.
	 *
	 * @param u La unidad que debe corresponder a un algoritmo.
	 *
	 * @return La estrategia dada a un algoritmo. 
	 *         null si la unidad no es un algoritmo o si el algoritmo
	 *         no está implementado en Loro.
	 */
	public static String obtEstrategiaAlgoritmo(IUnidad u)
	{
		_verificarIniciado();
		
		if ( u instanceof NAlgoritmo )
		{
			NAlgoritmo alg = (NAlgoritmo) u;
			TCadenaDoc tdoc = alg.obtEstrategia();
			return tdoc != null ? tdoc.obtCadena() : null;
		}

		return null;
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el número de parámetros de entrada de un algoritmo.
	 *
	 * @param u La unidad que debe corresponder a un algoritmo.
	 *
	 * @return el número de parámetros de entrada. 
	 */
	public static int getNumArguments(IUnidad u)
	{
		_verificarIniciado();
		
		if ( u instanceof NAlgoritmo )
		{
			NAlgoritmo alg = (NAlgoritmo) u;
			return alg.obtParametrosEntrada().length;
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Dice si un algoritmo tiene valor de retorno declarado.
	 *
	 * @param u La unidad que debe corresponder a un algoritmo.
	 *
	 * @return Si tiene retorno declarado. 
	 */
	public static boolean hasReturnValue(IUnidad u)
	{
		_verificarIniciado();
		
		if ( u instanceof NAlgoritmo )
		{
			NAlgoritmo alg = (NAlgoritmo) u;
			return alg.obtParametrosSalida().length > 0;
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Verifica que existan los elementos de apoyo requeridos para 
	 * operar normalmente en el uso final del sistema. 
	 * 
	 * La verificación del núcleo consta de:
	 * <ul>
	 *   <li> Que la clase raiz exista.
	 * </ul>
	 *
	 * Este método siempre debe ser llamado justo a continuación de 
	 * configurar(). 
	 *
	 * La excepción a esta regla es cuando precisamente se quiere que 
	 * el sistema básico opere para poder preparar los mismos elementos 
	 * de apoyo (por ejemplo, para poder compilar la misma clase raiz).
	 * En particular, la herramienta de linea de comandos para compilar
	 * no efectua esta verificación.
	 *
	 * @throws LoroException  Si la verificacion falla.
	 */
	public static void verificarNucleo()
	throws LoroException
	{
		_verificarIniciado();
		
		NClase clase_raiz = mu.obtClaseRaiz();
		if ( clase_raiz == null )
		{
			throw new LoroException("No se encuentra la clase raiz: " +
				mu.obtNombreClaseRaiz()+
				"\n"
			);
		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Verifica la configuración dada. 
	 * Antes de llamarse este método debe haberse llamado configurar() 
	 * y opcionalmente verificarNucleo().
	 * 
	 * @param lars Lista en donde se agregarán los nombres completos de
	 *			los archivos LAR que resultan incompatibles con
	 *			la versión actual del núcleo.
	 *
	 * @param oros Lista en donde se agregarán los nombres completos de
	 *			los archivos .oro que resultan incompatibles con
	 *			la versión actual del núcleo.
	 */
	public static void verificarConfiguracion(List lars, List oros)
	{
		_verificarIniciado();
		
		logger.log("%%%% INICIO verificación de configuración");
		mu.verificarUnidadesCompiladas(lars, oros);
		logger.log("%%%% FINAL verificación de configuración");
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Verifica que el núcleo se encuentre dedidamente iniciado.
	 *
	 * @throws IllegalStateException  Si el núcleo no se encuentra iniciado.
	 */
	private static void _verificarIniciado()
	{
		if ( !iniciado )
		{
			throw new IllegalStateException("El núcleo no se encuentra iniciado!");
		}
	}

	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Carga una unidad dado su nombre de archivo completo.
	 */
	public static NUnidad obtUnidadDeArchivo(String nombreArchivo)
	{
		return mu.obtUnidadDeArchivo(nombreArchivo);
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el cargador del núcleo.
	 *
	 * @return el cargador del núcleo.
	 *
	 * @throws IllegalStateException  Si el núcleo no se encuentra iniciado.
	 */
	public static IOroLoader getCoreLoader()
	{
		_verificarIniciado();
		return mu.getCoreLoader();
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene los cargadores de las extensiones activas.
	 * Por cada extensión hay un cargador correspondiente.
	 *
	 * (Nota: En esta lista NO está incluido el cargador del núcleo (apoyo).
	 *  <code>getCoreLoader()</code> lo obtiene.)
	 *
	 * @return Lista con elementos de tipo IOroLoader.
	 *
	 * @throws IllegalStateException  Si el núcleo no se encuentra iniciado.
	 */
	public static List getExtensionLoaders()
	{
		_verificarIniciado();
		return mu.getExtensionLoaders();
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene los objetos File de los cargadores tomados como extensiones.
	 *
	 * @return Lista con elementos de tipo File.
	 */
	public static List getExtensionFiles()
	{
		_verificarIniciado();
		return mu.getExtensionFiles();
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Adiciona una directorio a la ruta de búsqueda.
	 *
	 * @param dir El directorio a incluir.
	 *
	 * @throws IllegalStateException  Si el núcleo no se encuentra iniciado.
	 */
	public static void addDirectoryToPath(File dir)
	{
		_verificarIniciado();
		mu.addDirectoryToPath(dir);
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Adiciona un archivo zip a la ruta de búsqueda.
	 *
	 * @param file El archivo zip a incluir.
	 * @return El cargador asociado.
	 *
	 * @throws IllegalStateException  Si el núcleo no se encuentra iniciado.
	 */
	public static IOroLoader addExtensionToPath(File file)
	{
		_verificarIniciado();
		return mu.addExtensionToPath(file);
	}

}