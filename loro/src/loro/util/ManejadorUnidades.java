package loro.util;

import loro.IOroLoader;
import loro.ijava.LManejadorES;
import loro.arbol.*;
import loro.util.Util;
import loro.compilacion.ClaseNoEncontradaException;
import loro.Loro;

import java.util.*;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


///////////////////////////////////////////////////////////////
/**
 * A "singleton" class to manage compiled units.
 *
 * Gestiona el almacenamiento, recuperación y versión de unidades compiladas.
 *
 * Debe llamarse el método estático crearManejadorUnidades para disponer de la
 * instancia de trabajo. Una vez terminadas las operaciones con el manejador, 
 * puede (debe) llamarse destruirManejadorUnidades; después del cual podría 
 * llamarse nuevamente a crearManejadorUnidades.
 *
 * @author Carlos Rueda
 */
public class ManejadorUnidades {
	/** The only instance. */
	private static ManejadorUnidades mu = null;


	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Crea el ManejadorUnidades único. 
	 *
	 * @throws IllegalStateException  Si ya existe un manejador de unidades.
	 */
	public static ManejadorUnidades crearManejadorUnidades(File[] files)
	{
		if ( mu != null ) {
			throw new IllegalStateException("UnitManager already created!");
		}

		mu = new ManejadorUnidades(files);
		return mu;
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Destruye el ManejadorUnidades único.
	 *
	 * <b>Nota</b>: El objeto previamente existente quedará inservible después
	 * de la invocación de esta rutina. Muy posiblemente se generarán errores NPE
	 * si se intenta interactuar a través de una referencia al objeto destruido, 
	 * cuyos atributos se dejan en null como mecanismo de control.
	 *
	 * Si se desea disponer nuevamente de un objeto válido, deberá volverse a hacer
	 * la creación del manejador. 
	 *
	 * @throws IllegalStateException  Si NO existe un manejador de unidades válido.
	 */
	public static void destruirManejadorUnidades()
	{
		if ( mu == null ) {
			throw new IllegalStateException("No existing UnitManager!");
		}

		// invalidemos el objeto actual:
		mu.logger = null;
		mu.dirGuardar = null;
		mu.cache = null;
		
		mu.oroLoaderMan = null;
		
		// quedamos oficialmente sin instancia:
		mu = null;
	}


	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el manejador de unidades unico.
	 *
	 * @throws IllegalStateException
	 *		Si no hay una instancia válida creada con crearManejadorUnidades(File[])
	 */
	public static ManejadorUnidades obtManejadorUnidades()
	{
		if ( mu == null ) {
			throw new IllegalStateException("No existing UnitManager!");
		}

		return mu;
	}

	///////////////////////////////////////////////////////////////////////
	//
	// Instance:
	//
	///////////////////////////////////////////////////////////////////////

	/**
	 * Se guardan los compilados en disco?
	 */
	boolean guardarCompilados;

	/**
	 * Para registro de eventos.
	 */
	private Logger logger;

	/**
	 * Directorio para guardar unidades compiladas.
	 */
	private String dirGuardar;

	/**
	 * Las unidades actualmente en memoria. Aqui se ponen todas
	 * las unidades compiladas y las que se van encontrando (por
	 * demanda) en la ruta de busqueda (que no sean archivos zip)
	 * y el directorio para guardar compilados.
	 * Esta memoria se limpia cuando se invoca reiniciar().
	 */
	private Map cache;

	/**
	 * El manejador general de carga de unidades.
	 */
	private OroLoaderManager oroLoaderMan;

	//////////////////////////////////////////////////////////////////////
	/**
	 * Crea el manejador de unidades de compilacion.
	 *
	 * Pone la ruta para busqueda de unidades compiladas con base en el
	 * argumento files.
	 */
	private ManejadorUnidades(File[] files)
	{
		cache = new HashMap();
		logger = Logger.getLogger();
		
		oroLoaderMan = new OroLoaderManager(files);

		dirGuardar = "";
		ponDirGuardarCompilado(dirGuardar);
		guardarCompilados = true;
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el manejador de cargadores de unidades.
	 */
	public OroLoaderManager getOroLoaderManager()
	{
		return oroLoaderMan;
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Carga una unidad del oroLoaderMan.
	 * En caso que la unidad se haya encontrado, se pone en el cache.
	 */
	private NUnidad _cargarUnidad(String nombre)
	{
		NUnidad n = oroLoaderMan.getUnit(nombre);
		if ( null != n )
			cache.put(nombre, n);

		return n;
	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Borra, si existe, el archivo correspondiente a una unidad compilada.
	 * A este nombre se le prefija obtDirGuardarCompilado() y se le
	 * agrega ".oro". 
	 */
	private void _borrarUnidad(String nombre)
	{
		String nombre_completo = obtDirGuardarCompilado() + nombre + ".oro";
		File file = new File(nombre_completo);
		if ( file.exists() )
		{
			file.delete();
		}
	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Guardar una unidad con un nombre.
	 * A este nombre se le prefija obtDirGuardarCompilado() y se le
	 * agrega ".oro". 
	 */
	private void _guardarUnidad(String nombre, NUnidad n)
	throws IOException
	{
		n.ponVersion(Loro.obtVersion());

		if ( !guardarCompilados )
			return;

		String nombre_completo = obtDirGuardarCompilado() + nombre + ".oro";
		int i = nombre_completo.lastIndexOf(File.separatorChar);
		if ( i >= 0 )
		{
			new File(nombre_completo.substring(0, i)).mkdirs();
		}

		ObjectOutputStream out = new ObjectOutputStream(
			new FileOutputStream(nombre_completo)
		);
		escribirUnidad(out, n);
		out.close();
	}
	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la unidad de nombre dado.
	 * Este nombre debe tener la extension correspondiente
	 * ".e", ".a" o ".c".
	 */
	public NUnidad obtUnidad(String nombre)
	{
		NUnidad n;

		logger.log("obtUnidad: " +nombre);
		
		// mire si esta en memoria:
		n = (NUnidad) cache.get(nombre);
		if ( n != null )
		{
			logger.log("... FOUND IN CACHE");
			return n;
		}

		logger.log("... Not found in caches");

		// intente cargar de disco:
		return _cargarUnidad(nombre);
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el algoritmo de nombre dado.
	 */
	public NAlgoritmo obtAlgoritmo(String nombre)
	{
		return (NAlgoritmo) obtUnidad(nombre + ".a");
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la clase de nombre dado.
	 *
	 * @return La clase de Loro.
	 *         null si tal clase no se encuentra.
	 */
	public NClase obtClase(String nombre)
	{
		NClase clase = (NClase) obtUnidad(nombre + ".c");
		return clase;
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la interface de nombre dado.
	 *
	 * @return La interface de Loro.
	 *         null si tal interface no se encuentra.
	 */
	public NInterface obtInterface(String nombre)
	{
		NInterface interface_ = (NInterface) obtUnidad(nombre + ".i");
		return interface_;
	}

	///////////////////////////////////////////////////////////
	/**
	 * Obtiene el directorio para guardar compilados.
	 */
	public String obtDirGuardarCompilado()
	{
		return dirGuardar;
	}
	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la especificacion de nombre dado.
	 */
	public NEspecificacion obtEspecificacion(String nombre)
	{
		logger.log("obtEspecificacion: " +nombre);
		return (NEspecificacion) obtUnidad(nombre + ".e");
	}
	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la especificacion de un algoritmo. Si el algoritmo no
	 * tiene especificacion se retorna null.
	 * 2001-09-18
	 */
	public NEspecificacion obtEspecificacionDeAlgoritmo(NAlgoritmo alg)
	{
		String[] e = alg.obtNombreEspecificacion();
		if ( e == null )
		{
			return null;
		}
		String nombre = Util.obtStringRuta(e);
		return obtEspecificacion(nombre);
	}
	///////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el indicador de guardar compilados en disco.
	 */
	public boolean obtGuardarCompilados()
	{
		return guardarCompilados;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Borra el archivo correspondiente a un algoritmo compilado,
	 * en caso de que exista.
	 */
	public void borrarAlgoritmo(NAlgoritmo n)
	{
		_borrarUnidad(n.obtNombreCompletoCadena(File.separator) + ".a");
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Borra el archivo correspondiente a una clase compilada, 
	 * en caso de que exista.
	 */
	public void borrarClase(NClase n)
	{
		_borrarUnidad(n.obtNombreCompletoCadena(File.separator) + ".c");
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Borra el archivo correspondiente a una especificación compilada, 
	 * en caso de que exista.
	 */
	public void borrarEspecificacion(NEspecificacion n)
	{
		_borrarUnidad(n.obtNombreCompletoCadena(File.separator) + ".e");
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Borra el archivo correspondiente a una especificación compilada, 
	 * en caso de que exista.
	 */
	public void borrarInterface(NInterface n)
	{
		_borrarUnidad(n.obtNombreCompletoCadena(File.separator) + ".i");
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Pone un algoritmo. Esto significa guardar esta unidad en disco y tenerla
	 * en cache para proximas referencias.
	 */
	public void ponAlgoritmo(NAlgoritmo n)
	throws IOException
	{
		cache.put(n.obtNombreCompletoCadena() + ".a", n);
		_guardarUnidad(n.obtNombreCompletoCadena(File.separator) + ".a", n);
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Pone una clase. Esto significa guardar esta unidad en disco y tenerla
	 * en cache para proximas referencias.
	 */
	public void ponClase(NClase n)
	throws IOException
	{
		cache.put(n.obtNombreCompletoCadena() + ".c", n);
		_guardarUnidad(n.obtNombreCompletoCadena(File.separator) + ".c", n);
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Pone una interface. Esto significa guardar esta unidad en disco y tenerla
	 * en cache para proximas referencias.
	 */
	public void ponInterface(NInterface n)
	throws IOException
	{
		cache.put(n.obtNombreCompletoCadena() + ".i", n);
		_guardarUnidad(n.obtNombreCompletoCadena(File.separator) + ".i", n);
	}

	///////////////////////////////////////////////////////////
	/**
	 * Pone el directorio para guardar unidades compiladas.
	 *
	 * @throws NullPointerException If argument is null.
	 */
	public void ponDirGuardarCompilado(String rb)
	{
		if ( rb == null )
			throw new NullPointerException();
		
		char sc = java.io.File.separatorChar;
		rb = rb.replace('/', sc).replace('\\', sc);

		dirGuardar = rb;
		if ( dirGuardar.length() > 0 && !dirGuardar.endsWith(File.separator) )
			dirGuardar += File.separator;
		
		File dir = new File(dirGuardar);
		oroLoaderMan.addDirectoryToPath(dir);
		oroLoaderMan.setFirstDirectory(dir);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Pone una especificacion. Esto significa guardar esta unidad en disco y tenerla
	 * en cache para proximas referencias.
	 */
	public void ponEspecificacion(NEspecificacion n)
	throws IOException
	{
		cache.put(n.obtNombreCompletoCadena() + ".e", n);
		_guardarUnidad(n.obtNombreCompletoCadena(File.separator) + ".e", n);
	}
	///////////////////////////////////////////////////////////////
	/**
	 * Pone el indicador de guardar compilados en disco.
	 */
	public void ponGuardarCompilados(boolean g)
	{
		guardarCompilados = g;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Reinicia el manejador.
	 * Se quitan las unidades guardadas o recuperadas de la memoria.
	 */
	public void reiniciar()
	{
		cache.clear();
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Carga una unidad dado su nombre de archivo completo.
	 *
	 * NO SE MODIFICA NINGUNO DE LOS CACHES.
	 */
	public NUnidad obtUnidadDeArchivo(String nombreArchivo)
	{
		logger.log("Reading unit from : " +nombreArchivo);
		
		File file = new File(nombreArchivo);
		if ( !file.isFile() )
		{
			return null;
		}

		ObjectInputStream in = null;
		try
		{
			in = new ObjectInputStream(new FileInputStream(file));
		}
		catch(Exception e)
		{
			return null;
		}

		NUnidad n = null;
		try
		{
			n = leerUnidad(in);
			in.close();
		}
		catch(Exception ex)
		{
			logger.log("Error reading unit from: " +nombreArchivo+ ": " +ex.getMessage());
		}

		return n;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la superclase de una clase.
	 *
	 * @param clase La clase a la que se le va a encontrar la superclase.
	 *
	 * @return      La superclase.
	 *              Si la clase corresponde a la clase raiz de Loro, se retorna null.
	 *
	 * @throws ClaseNoEncontradaException
	 *              Si la superclase no se encuentra.
	 *              (Debio haber sido borrada despues de haber sido compilada.)
	 *
	 */
	public NClase obtSuperClase(NClase clase) throws ClaseNoEncontradaException {
		// revise si la clase es la raiz:
		String root_name = Loro.getLanguageInfo().getRootClassName();
		String nombre = clase.obtNombreCompletoCadena();
		if ( root_name.equals(nombre) ) {
			return null;
		}

		TNombre extiende = clase.obtNombreExtiende();
		if ( extiende == null ) {
			// no se indico 'extiende', por lo tanto es la raiz:
			NClase root = obtClase(root_name);
			if ( root == null ) {
				throw new ClaseNoEncontradaException(root_name);
			}
			return root;
		}

		// Busque super con base en el 'extiende'.
		// si este 'extiende' es simple, o sea sin paquete, se intenta
		// posible asociacion simple/compuesto; despues en el mismo
		// paquete de la clase; despues en el paquete sin nombre;
		// despues en el paquete automatico.
		// Si el 'extiende' incluye paquete, se busca directamente con
		// dicho nombre.

		String nombreCompuesto;
		TId[] ids = extiende.obtIds();
		if ( ids.length == 1 ) {
			TId id = ids[0];
			nombreCompuesto = clase.obtNombreCompuesto("c" +id);
			if ( nombreCompuesto == null ) {
				// Intente por el paquete de esta misma clase:
				String[] paquete = clase.obtPaquete();
				if ( paquete != null ) {
					nombreCompuesto = Util.obtStringRuta(paquete)+ "::" +id.obtId();
				}
				else {
					// Intente por el paquete sin nombre:
					nombreCompuesto = id.obtId();
				}
			}
		}
		else {
			nombreCompuesto = extiende.obtCadena();
		}

		clase = mu.obtClase(nombreCompuesto);

		if ( clase == null && ids.length == 1 ) {
			// intente por el paquete visible automaticamente:
			String auto_pkg = Loro.getLanguageInfo().getAutomaticPackageName();
			nombreCompuesto = auto_pkg+ "::" +ids[0];
			clase = mu.obtClase(nombreCompuesto);
		}

		if ( clase == null ) {
			throw new ClaseNoEncontradaException(extiende.obtCadena());
		}

		return clase;
	}


	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene todos los atributos de una clase.
	 *
	 * @param clase La clase a la que se le van a encontrar sus atributos.
	 *
	 * @return      La lista con los atributos en el orden de declaracion
	 *              desde la clase raiz.
	 *
	 * @throws      ClaseNoEncontradaException
	 *              Si alguna clase ascendente no se encuentra.
	 */
	public NDeclDesc[] obtAtributos(NClase clase)
	throws ClaseNoEncontradaException
	{
		Stack ascendencia = new Stack();

		while ( clase != null )
		{
			ascendencia.push(clase);
			clase = obtSuperClase(clase);
		}

		Vector atrs_v = new Vector();
		while ( !ascendencia.isEmpty() )
		{
			clase = (NClase) ascendencia.pop();

			NDeclDesc[] pent = clase.obtParametrosEntrada();

			for ( int i = 0; i < pent.length; i++ )
			{
				NDeclDesc d = pent[i];
				atrs_v.addElement(d);
			}
		}
		NDeclDesc[] atrs = new NDeclDesc[atrs_v.size()];
		atrs_v.copyInto(atrs);
		return atrs;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene Los nombres de todos los atributos de una clase.
	 *
	 * @param clase La clase a la que se le van a encontrar sus atributos.
	 *
	 * @return      La lista con los nombres (String) de los atributos.
	 *
	 * @throws      ClaseNoEncontradaException
	 *              Si alguna clase ascendente no se encuentra.
	 */
	public String[] obtNombresAtributos(NClase clase)
	throws ClaseNoEncontradaException
	{
		NDeclDesc[] atrs = obtAtributos(clase);
		String[] nom_atrs = new String[atrs.length];
		for ( int i = 0; i < atrs.length; i++ )
		{
			nom_atrs[i] = atrs[i].obtId().obtId();
		}
		return nom_atrs;
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Escribe una unidad en el ObjectOutputStream dado.
	 * Hace manejo de version de la unidad.
	 */
	private void escribirUnidad(ObjectOutputStream out, NUnidad n)
	throws IOException
	{
		String version        = n.obtVersion(); 
		String nombreFuente   = n.obtNombreFuente();
		String nombreCompleto = n.obtNombreCompletoCadena();
		long millis = System.currentTimeMillis();

		out.writeObject(version);
		out.writeObject(nombreFuente);
		out.writeObject(nombreCompleto);
		out.writeLong(millis);
		
		out.writeObject(n);
	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Lee una unidad del ObjectInputStream dado.
	 * Hace manejo de version de la unidad.
	 */
	static NUnidad leerUnidad(ObjectInputStream ois)
	throws Exception
	{
		String version = (String) ois.readObject();
		String nombreFuente = (String) ois.readObject();
		String nombreCompleto = (String) ois.readObject();
		long millis = ois.readLong();
		
		NUnidad n = (NUnidad) ois.readObject();
		
		n.ponVersion(version);
		n.ponNombreFuente(nombreFuente);
		n.setMillis(millis);
		
		return n;
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene los nombres de las superinterfaces de una interface.
	 * Si se trata de la interface raiz, se retorna inmediatamente sin hacer nada.
	 *
	 * @param interf La interface a la que se le van a encontrar las superinterfaces.
	 *
	 * @param set   Aqui se agregaran los nombres.
	 *
	 * @throws ClaseNoEncontradaException
	 *              Si alguna superinterface no se encuentra.
	 *              (Debio haber sido borrada despues de haber sido compilada.)
	 */
	private void _obtSuperInterfaces(NInterface interf, Set set)
	throws ClaseNoEncontradaException {
		// revise si la interface es la raiz:
		String root_interface_name = Loro.getLanguageInfo().getRootInterfaceName();
		String nombre = interf.obtNombreCompletoCadena();
		if ( root_interface_name.equals(nombre) ) {
			return;
		}

		TNombre[] extiendes = interf.obtInterfaces();
		if ( extiendes.length == 0 ) {
			// no se indico 'extiende', por lo tanto implicitamente es la interface raiz:
			NInterface raiz = obtInterface(root_interface_name);
			if ( raiz == null ) {
				throw new ClaseNoEncontradaException(root_interface_name);
			}
			
			set.add(root_interface_name);
			
			return;
		}

		for (int i = 0; i < extiendes.length; i++) {
			TNombre extiende = extiendes[i];
			
			// si este 'extiende' es simple, o sea sin paquete, se intenta
			// posible asociacion simple/compuesto; despues en el mismo
			// paquete de la clase; despues en el paquete sin nombre;
			// despues en el paquete automatico.
			// Si el 'extiende' incluye paquete, se busca directamente con
			// dicho nombre.

			String nombreCompuesto;
			TId[] ids = extiende.obtIds();
			if ( ids.length == 1 ) {
				TId id = ids[0];
				nombreCompuesto = interf.obtNombreCompuesto("c" +id);
				if ( nombreCompuesto == null ) {
					// Intente por el paquete de esta misma clase:
					String[] paquete = interf.obtPaquete();
					if ( paquete != null ) {
						nombreCompuesto = Util.obtStringRuta(paquete)+ "::" +id.obtId();
					}
					else {
						// Intente por el paquete sin nombre:
						nombreCompuesto = id.obtId();
					}
				}
			}
			else {
				nombreCompuesto = extiende.obtCadena();
			}

			interf = mu.obtInterface(nombreCompuesto);

			if ( interf == null && ids.length == 1 ) {
				// intente por el paquete visible automaticamente:
				String auto_pkg = Loro.getLanguageInfo().getAutomaticPackageName();
				nombreCompuesto = auto_pkg+ "::" +ids[0];
				interf = mu.obtInterface(nombreCompuesto);
			}

			if ( interf == null ) {
				throw new ClaseNoEncontradaException(extiende.obtCadena());
			}
			set.add(nombreCompuesto);
		}
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene los nombres de todas las interfaces implementadas por una clase.
	 *
	 * @param clase La clase a la que se le van a encontrar las superinterfaces.
	 *
	 * @return      El conjunto de los String's correspondientes a los nombres de las 
	 *              superinterfaces.
	 *              Si la clase dada corresponde a la clase raiz de Loro,
	 *              se retorna la interface raiz.
	 *
	 * @throws ClaseNoEncontradaException
	 *              Si alguna superinterface no se encuentra.
	 *              (Debio haber sido borrada despues de haber sido compilada.)
	 *
	 */
	public Set obtSuperInterfaces(NClase clase)
	throws ClaseNoEncontradaException
	{
		Set set = new HashSet();

		// toda clase implementa por lo menos la interface raiz:
		String root_interface_name = Loro.getLanguageInfo().getRootInterfaceName();
		set.add(root_interface_name);

		TNombre[] obj_interfs = clase.obtInterfacesDeclaradas();
		for (int i = 0; i < obj_interfs.length; i++) {
			TNombre tnom = obj_interfs[i];
			
			String nombreCompuesto;
			TId[] ids = tnom.obtIds();
			if ( ids.length == 1 )
			{
				TId id = ids[0];
				nombreCompuesto = clase.obtNombreCompuesto("i" +id);
				if ( nombreCompuesto == null )
				{
					// Intente por el paquete de esta misma clase:
					String[] paquete = clase.obtPaquete();
					if ( paquete != null )
					{
						nombreCompuesto = Util.obtStringRuta(paquete)+ "::" +id.obtId();
					}
					else
					{
						// Intente por el paquete sin nombre:
						nombreCompuesto = id.obtId();
					}
				}
			}
			else
			{
				nombreCompuesto = tnom.obtCadena();
			}

			NInterface interf = mu.obtInterface(nombreCompuesto);

			if ( interf == null && ids.length == 1 ) {
				// intente por el paquete visible automaticamente:
				String auto_pkg = Loro.getLanguageInfo().getAutomaticPackageName();
				nombreCompuesto = auto_pkg+ "::" +ids[0];
				interf = mu.obtInterface(nombreCompuesto);
			}

			if ( interf == null )
			{
				throw new ClaseNoEncontradaException(tnom.obtCadena());
			}

			set.add(nombreCompuesto);

			// y recursivamente agregue sus superinterfaces:
			_obtSuperInterfaces(interf, set);
		}
		

		return set;

	}

	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene los nombres de las superinterfaces de una interface.
	 *
	 * @param interf La interface a la que se le van a encontrar las superinterfaces.
	 *
	 * @return      El conjunto de los String's correspondientes a los nombres de las 
	 *              superinterfaces.
	 *              Si la interface dada corresponde a la interface raiz de Loro,
	 *              se retorna null.
	 *
	 * @throws ClaseNoEncontradaException
	 *              Si alguna superinterace no se encuentra.
	 *              (Debio haber sido borrada despues de haber sido compilada.)
	 *
	 */
	public Set obtSuperInterfaces(NInterface interf)
	throws ClaseNoEncontradaException {
		// revise si la interface es la raiz:
		String nombre = interf.obtNombreCompletoCadena();
		String root_interface_name = Loro.getLanguageInfo().getRootInterfaceName();
		if ( root_interface_name.equals(nombre) ) {
			return null;
		}
		Set set = new HashSet();
		_obtSuperInterfaces(interf, set);
		return set;
	}
}
