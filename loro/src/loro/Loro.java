package loro;

import loro.impl.*;
import loro.arbol.*;
import loro.compilacion.*;
import loro.derivacion.*;
import loro.ejecucion.*;
import loro.util.*;
import loro.tipo.Tipo;
import loro.tabsimb.TablaSimbolos;
import loro.visitante.Descriptor;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.io.*;
import java.util.*;
import java.util.ResourceBundle;
import java.util.Locale;

//////////////////////////////////////////////////////////////
/**
 * Punto de entrada principal (facade) al n�cleo Loro.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public final class Loro 
{
	private static final String INFO_PROPS_PATH = "loro/resource/info.properties";
	
	/** para toArray. */
	private static final File[] EMPTY_FILE_ARRAY = new File[0];

	/** Est� iniciado el n�cleo? */
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
	 * De all� se toman en cuenta archivos *.lar y *.jar.
	 */
	private static String ext_dir = null;
	
	/**
	 * Directorio con subdirectorios para b�squeda.
	 * Todo subdirectorio de este directorio es tomado en cuenta 
	 * en la ruta de b�squeda de unidades compiladas.
	 */
	private static String paths_dir = null;
	
	/** El logger. */
	private static Logger logger = null;

	/** El manejador de unidades. */
	private static ManejadorUnidades mu;
	
	/** 
	 * Lista de todos los archivos lar de extensi�n.
	 */
	private static List extensionFiles;
	

	/** El compilador. */
	private static ICompilador compilador;

	/** El documentador. */
	private static IDocumentador documentador;

	/** Tabla de s�mbolos de base y com�n para todos los int�rpretes
	 * interactivos que se creen. */
	private static TablaSimbolos tabSimbBase;
	
	
	////////////////////////////////////////////////////////
	// Se instancia solo para acceder al classloader.
	private Loro() {}
	
	
	private static Locale locale = null;
	private static ResourceBundle strings = null;
	
	/** Returns the locale used by the Loro system. */
	public static Locale getLocale() {
		return locale;
	}
	
	/** Sets the locale. 
	 This method can be called at any time (before or after initialization). 
	 The language keyword set is also affected. */
	public static void setLocale(Locale locale_) {
		try {
			strings = ResourceBundle.getBundle("loro.resource.strings", locale_);
			locale = locale_;
			if ( locale.toString().startsWith("es") )
				ParserFactory.setCurrentCode(ParserFactory.SP);
			else
				ParserFactory.setCurrentCode(ParserFactory.EN);
			log("Locale set to: " +locale);
		}
		catch(java.util.MissingResourceException ex) {
			System.err.println(
				"!!!!!! Warning:\n" +
				"!!!!!! Cannot get loro.resource.strings bundle.\n" +
				"!!!!!! The system has not been compiled properly.\n"+
				"!!!!!! locale = " +locale_+ "\n"
			);
		}
	}

	/** Sets the locale.
	 Only makes the call <code>setLocale(new Locale(slocale))</code> */
	public static void setLocale(String slocale) {
		setLocale(new Locale(slocale));
	}

	public static abstract class Str {
		/** gets a string from the locale bundle. */
		public static String get(String id)  {
			if ( strings != null ) 
				return strings.getString(id);
			else
				return id;
		}
		
		/** gets a string from the locale bundle. */
		public static String get(String id, Object arg0)  {
			return get(id, new Object[] { arg0 });
		}
		
		/** gets a string from the locale bundle. */
		public static String get(String id, Object arg0, Object arg1)  {
			return get(id, new Object[] { arg0, arg1 });
		}
		
		/** gets a string from the locale bundle. */
		public static String get(String id, Object arg0, Object arg1, Object arg2)  {
			return get(id, new Object[] { arg0, arg1, arg2 });
		}
		
		/** gets a string from the locale bundle. */
		public static String get(String id, Object arg0, Object arg1, Object arg2, Object arg3)  {
			return get(id, new Object[] { arg0, arg1, arg2, arg3 });
		}
		
		/** gets a string from the locale bundle. */
		public static String get(String id, Object[] args)  {
			String str = null;
			if ( strings != null )
				str = strings.getString(id);
			if ( str != null ) {
				for ( int i = 0; i < args.length; i++ ) {
					String tag = "{" +i+ "}";
					str = Util.replace(str, tag, args[i].toString());
				}
			}
			else {
				str = id+ ":args";
				for ( int i = 0; i < args.length; i++ ) {
					String tag = "{" +i+ "}";
					str += ":" +tag+ "=" +args[i].toString();
				}
			}
			return str;
		}
	}
	
	
	////////////////////////////////////////////////////////
	/**
	 * Obtiene los datos del recurso loro/resource/info.properties.
	 * Este servicio no requiere que el n�cleo haya sido iniciado.
	 */
	private static void _obtInfo()  {
		if ( nombre == null ) {
			// default values:
			nombre = "Loro";
			version = "v";
			
			ClassLoader	cl = new Loro().getClass().getClassLoader();
			Properties props = new Properties(); 
			InputStream is = cl.getResourceAsStream(INFO_PROPS_PATH);
			if ( is == null ) {
				System.err.println("resource " +INFO_PROPS_PATH+ " NOT FOUND!");
				return;
			}
			try {
				props.load(is);
				is.close();
			}
			catch(IOException ex) {
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
	 * de si el n�cleo ha sido iniciado o no.
	 */
	public static String obtNombre()
	{
		_obtInfo();
		return nombre;
	}
	////////////////////////////////////////////////////////////
	/**
	 * Obtiene la versi�n del sistema Loro.
	 * Este servicio puede llamarse en cualquier momento independientemente
	 * de si el n�cleo ha sido iniciado o no.
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
	 * de si el n�cleo ha sido iniciado o no.
	 */
	public static String obtBuild()
	{
		_obtInfo();
		return build;
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Cierra el n�cleo Loro.
	 * Despu�s de esta operaci�n NO deber�a invocarse ning�n servicio del
	 * n�cleo salvo volver a iniciarlo a trav�s de configurar(). No seguir
	 * esta indicaci�n producir� errores estilo NPE o similares y/o otros
	 * comportamientos an�malos, as� que aseg�rese de mantener sus
	 * invariantes.
	 *
	 * @throws IllegalStateException  Si el n�cleo no se encuentra iniciado.
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
	 * Establece la configuraci�n de trabajo para el sistema Loro.
	 * Salvo algunos m�todos informativos, �ste debe ser el primero en llamarse
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
	 * Justo a continuaci�n de este m�todo debe llamarse verificarSistema(),
	 * salvo cuando se quiere s�lo el sistema b�sico que permita precisamente 
	 * preparar los elementos de apoyo (por ejemplo, para poder compilar la 
	 * clase raiz del lenguaje Loro).
	 *
	 * @param ext_dir Directorio de extensiones de donde se leen archivos *.jar y *.lar.
	 *                Puede ser null.
	 *
	 * @param paths_dir Directorio con subdirectorios y archivos .lar de b�squeda.
	 *
	 * @throws LoroException  Si la inicializaci�n falla.
	 * @throws IllegalStateException  Si el n�cleo ya se encuentra iniciado.
	 */
	public static void configurar(String ext_dir_, String paths_dir_) throws LoroException {
		if ( iniciado ) {
			throw new IllegalStateException("core already inited!");
		}
		
		Logger.createLogger(obtNombre()+ " " +obtVersion()+ " (Build " +obtBuild()+ ")");

		if ( ParserFactory.getCurrentCode() == null ) {
			// setLocale has not been called previously. 
			// Then set language according to default locale:
			setLocale(Locale.getDefault());
		}
		
		ext_dir = ext_dir_;
		paths_dir = paths_dir_;

		logger = Logger.getLogger();
		
		log("extensions: " +ext_dir);
		log("extended paths: " +paths_dir);

		_crearManejadorUnidades();
		
		if ( ext_dir != null ) {
			// ponga el cargador de clases:
			// este cargador sera utilizado para los ejecutores:
			loroClassLoader = new LoroClassLoader();
			try {
				loroClassLoader.ponDirectorioExtensiones(ext_dir);
			}
			catch(MalformedURLException ex) {
				throw new LoroException("error extension directory: "+ex.getMessage());
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
	 * @param file El archivo extensi�n. 
	 * @param force true para obligar la expansi�n aunque xxx/ ya exista. 
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
	 * Se encarga de crear el ManejadorUnidades �nico con todos los
	 * todos los directorios y archivos *.lar bajo paths_dir/* como 
	 * ruta de b�squeda de unidades compiladas. Los archivo *.lar
	 * son primero expandidos.
	 * Tambi�n se agregan al final todos los archivos ext_dir/*.lar.
	 */
	private static void _crearManejadorUnidades()
	{
		_expandExtensions();
		
		// lista completa de extensiones y directorios de b�squeda:
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

		// lista de todos los archivos lar de extensi�n.
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
		//if ( a.length > 0 )
		//{
		//	 no deberia hacerse siempre pero por ahora lo hacemos :-/
		//	
		//	log("Generando documentacion para extensiones en: " +doc_dir);
		//	for ( int i = 0; i < a.length; i++ )
		//	{
		//		File file = a[i];
		//		try
		//		{
		//			String err = loro.doc.Documentador.procesarArchivoZip(
		//				file.getAbsolutePath(), doc_dir
		//			);
		//		}
		//		catch ( IOException ex )
		//		{
		//			log(file+ ": " +ex.getMessage());
		//		}
		//	}
		//}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Se encarga de destruir el ManejadorUnidades �nico
	 */
	private static void _destruirManejadorUnidades()
	{
		ManejadorUnidades.destruirManejadorUnidades();
		mu = null;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Rutina facilitadora para escribir mensajes al logger.
	 * Si el n�cleo ha sido iniciado, este m�todo delega en el logger
	 * creado como parte de dicha inicializaci�n; en caso contrario,
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
	 * Obtiene la tabla de simbolos com�n.
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
	 *                  s�mbolos nueva e independiente de la tabla compartida.
	 *                  En caso contrario el int�prete toma como base una tabla
	 *                  que ser� compartida por todos los int�rpretes instanciados
	 *                  con este par�metro dado en false.
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
	 * Gets metainfo about current programming language.
	 */
	public static ILanguageInfo getLanguageInfo() {
		_verificarIniciado();
		return ParserFactory.getLanguageInfo();
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
			if ( espec != null ) {
				String s = espec.obtDescripcion();
				return s == null ? "" : s;
			}
		}

		return null;
	}	

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene una descripci�n concisa del tipo del nodo dado.
	 *
	 * @param n   El nodo a describir.
	 *
	 * @return La descripci�n concisa del tipo del nodo dado.
	 */
	public static String getNodeDescription(INode n)
	{
		_verificarIniciado();
		return Descriptor.getDescription((INodo) n);
	}
		
	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene una especificaci�n.
	 *
	 * @param qname Nombre cualificado de la especificaci�n, 
	 *              salvo si se refiere al paquete autom�tico.
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
			// intente paquete autom�tico
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
	 *              salvo si se refiere al paquete autom�tico.
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
			// intente paquete autom�tico
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
	 *              salvo si se refiere al paquete autom�tico.
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
			// intente paquete autom�tico
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
	 * Obtiene el nombre del paquete autom�tico.
	 *
	 * @return el nombre del paquete autom�tico.
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
	 *         no est� implementado en Loro.
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
	 * Obtiene el n�mero de par�metros de entrada de un algoritmo.
	 *
	 * @param u La unidad que debe corresponder a un algoritmo.
	 *
	 * @return el n�mero de par�metros de entrada. 
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
	 * La verificaci�n del n�cleo consta de:
	 * <ul>
	 *   <li> Que la clase raiz exista.
	 * </ul>
	 *
	 * Este m�todo siempre debe ser llamado justo a continuaci�n de 
	 * configurar(). 
	 *
	 * La excepci�n a esta regla es cuando precisamente se quiere que 
	 * el sistema b�sico opere para poder preparar los mismos elementos 
	 * de apoyo (por ejemplo, para poder compilar la misma clase raiz).
	 * En particular, la herramienta de linea de comandos para compilar
	 * no efectua esta verificaci�n.
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
			throw new LoroException(Str.get("error.root_class_not_found")+ ": " +
				mu.obtNombreClaseRaiz()+
				"\n"
			);
		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Verifica la configuraci�n dada. 
	 * Antes de llamarse este m�todo debe haberse llamado configurar() 
	 * y opcionalmente verificarNucleo().
	 * 
	 * @param lars Lista en donde se agregar�n los nombres completos de
	 *			los archivos LAR que resultan incompatibles con
	 *			la versi�n actual del n�cleo.
	 *
	 * @param oros Lista en donde se agregar�n los nombres completos de
	 *			los archivos .oro que resultan incompatibles con
	 *			la versi�n actual del n�cleo.
	 */
	public static void verificarConfiguracion(List lars, List oros) {
		_verificarIniciado();
		logger.log("%%%% " +Str.get("begin_verify_configuration"));
		// PENDIENTE separar lars de oros...
		mu.getOroLoaderManager().verify(oros);
		logger.log("%%%% " +Str.get("end_verify_configuration"));
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Verifica que el n�cleo se encuentre dedidamente iniciado.
	 *
	 * @throws IllegalStateException  Si el n�cleo no se encuentra iniciado.
	 */
	private static void _verificarIniciado() {
		if ( !iniciado ) {
			throw new IllegalStateException("Core not yet inited!");
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
	 * Obtiene el cargador del n�cleo.
	 *
	 * @return el cargador del n�cleo.
	 *
	 * @throws IllegalStateException  Si el n�cleo no se encuentra iniciado.
	 */
	public static IOroLoader getCoreLoader()
	{
		_verificarIniciado();
		return mu.getOroLoaderManager().getCoreLoader();
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene los cargadores de las extensiones activas.
	 * Por cada extensi�n hay un cargador correspondiente.
	 *
	 * (Nota: En esta lista NO est� incluido el cargador del n�cleo (apoyo).
	 *  <code>getCoreLoader()</code> lo obtiene.)
	 *
	 * @return Lista con elementos de tipo IOroLoader.
	 *
	 * @throws IllegalStateException  Si el n�cleo no se encuentra iniciado.
	 */
	public static List getExtensionLoaders()
	{
		_verificarIniciado();
		return mu.getOroLoaderManager().getExtensionLoaders();
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
		return mu.getOroLoaderManager().getExtensionFiles();
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Adiciona una directorio a la ruta de b�squeda.
	 *
	 * @param dir El directorio a incluir.
	 *
	 * @throws IllegalStateException  Si el n�cleo no se encuentra iniciado.
	 */
	public static void addDirectoryToPath(File dir)
	{
		_verificarIniciado();
		mu.getOroLoaderManager().addDirectoryToPath(dir);
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Adiciona un archivo zip a la ruta de b�squeda.
	 *
	 * @param file El archivo zip a incluir.
	 * @return El cargador asociado.
	 *
	 * @throws IllegalStateException  Si el n�cleo no se encuentra iniciado.
	 */
	public static IOroLoader addExtensionToPath(File file)
	{
		_verificarIniciado();
		return mu.getOroLoaderManager().addExtensionToPath(file);
	}

}