package loro.util;

import loro.IOroLoader;
import loro.arbol.*;

import java.util.*;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.io.*;


///////////////////////////////////////////////////////////////
/**
 * Maneja una lista de objetos IOroLoader.
 *
 * @author Carlos Rueda
 */
public class OroLoaderManager
{
	/** Cargador de unidades del núcleo. */
	private CoreOroLoader coreOroLoader;
	
	/** Cargadores considerados como extensiones. */
	private List extensionLoaders;
	
	/** Los File's correspondientes a las extensiones. */
	private List extensionFiles;
	
	/** Cargadores desde directorios. */
	private List directoryLoaders;
	
	/**
	 * Los File's correspondientes a los directorios.
	 * El mapping es: nombre_canónico --> File
	 */
	private Map directoryFiles;
	
	/** Todos los cargadores. */
	private List loaders;
	
	private Logger logger;

	
	///////////////////////////////////////////////////////////////
	/**
	 * Crea una lista de cargadores.
	 */
	public OroLoaderManager(File[] files)
	{
		logger = Logger.getLogger();
		
		coreOroLoader = new CoreOroLoader();
		
		directoryLoaders = new ArrayList();
		extensionLoaders = new ArrayList();
		extensionFiles = new ArrayList();
		directoryFiles = new HashMap();
		loaders = new ArrayList();
		for ( int i = 0; i < files.length; i++ )
		{
			File file = files[i];
			if ( file.isDirectory() )
			{
				addDirectoryToPath(file);
			}
			else if ( file.isFile() )
			{
				addExtensionToPath(file);
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Adiciona una directorio a la ruta de búsqueda. 
	 * Si el directorio ya está en tal ruta, no se hace nada.
	 * Para esta verificación se utiliza el nombre canónico.
	 *
	 * @param dir El directorio a incluir.
	 */
	public void addDirectoryToPath(File dir)
	{
		String canonical_path;
		
		try
		{
			canonical_path = dir.getCanonicalPath();
		}
		catch(IOException ex)
		{
			// pero no debería suceder.
			logger.log("getCanonicalPath(): " +ex.getMessage());
			return;
		}
		
		if ( directoryFiles.get(canonical_path) == null )
		{
			IOroLoader ol = new DirectoryOroLoader(dir);
			directoryLoaders.add(ol); 
			directoryFiles.put(canonical_path, ol);
			loaders.add(ol);
			logger.log("OroLoaderManager: added directory: " +dir.getAbsolutePath());
		}
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Establece el directorio dado como el primero a examinar para búsquedas.
	 * Este orden puede alterar el resultado de getUnit cuando diversas
	 * unidades tienen el mismo nombre en distintos directorios.
	 *
	 * Si el directorio NO está en la ruta, no se hace nada.
	 * Para esta verificación se utiliza el nombre canónico.
	 *
	 * @param dir El directorio a buscar primero.
	 */
	public void setFirstDirectory(File dir)
	{
		String canonical_path;
		
		try
		{
			canonical_path = dir.getCanonicalPath();
		}
		catch(IOException ex)
		{
			// pero no debería suceder.
			logger.log("getCanonicalPath(): " +ex.getMessage());
			return;
		}

		IOroLoader ol = (IOroLoader) directoryFiles.get(canonical_path);
		if ( ol != null )
		{
			boolean existing = loaders.remove(ol);
			Util._assert(existing, "existing in loaders");
			loaders.add(0, ol);
		}
	}
		
	/////////////////////////////////////////////////////////////////
	/**
	 * Adiciona un archivo zip a la ruta de búsqueda.
	 *
	 * @param file El archivo zip a incluir.
	 * @return El cargador asociado. null si hay error.
	 */
	public IOroLoader addExtensionToPath(File file)
	{
		IOroLoader ol = null;
		try
		{
			ol = new ZipFileOroLoader(file);
			extensionFiles.add(file);
			extensionLoaders.add(ol);
			loaders.add(ol);
			logger.log("OroLoaderManager: added archive: " +file);
		}
		catch(IOException ex)
		{
			logger.log(file+ ": " +ex.getMessage());
		}
		return ol;
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene los cargador del núcleo.
	 */
	public IOroLoader getCoreLoader()
	{
		return coreOroLoader;
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene los cargadores que se toman como extensiones, estos
	 * son los archivos Zip.
	 *
	 * @return Lista con elementos de tipo IOroLoader.
	 */
	public List getExtensionLoaders()
	{
		return extensionLoaders;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene los objetos File de los cargadores tomados como extensiones.
	 *
	 * @return Lista con elementos de tipo File.
	 */
	public List getExtensionFiles()
	{
		return extensionFiles;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene todos los cargadores de este manejador.
	 *
	 * @return Lista con elementos de tipo IOroLoader.
	 */
	public List getDirectoryLoaders()
	{
		return directoryLoaders;
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene una unidad. Se asume que el nombre incluye la
	 * indicacion del tipo de nodo a leer; ejs:
	 *		unaEspecificacion.e
	 *		unAlgoritmo.a
	 *		unaClase.c
	 *
	 * La busqueda se hace en el siguiente orden: <br>
	 *	- cargador de unidades del núcleo<br>
	 *	- lista de cargadores<br>
	 */
	public NUnidad getUnit(String unitname)
	{
		NUnidad n = coreOroLoader.getUnit(unitname);
		if ( n != null )
			return n;
		
		for ( Iterator it = loaders.iterator(); it.hasNext(); )
		{
			IOroLoader loader = (IOroLoader) it.next();
			n = loader.getUnit(unitname);
			if ( n != null )
				break;
		}
		return n;
	}
	
	///////////////////////////////////////////////////////////////
	public List loadUnitNamesFromPackage(String nombrePaquete, List list)
	{
		if ( list == null )
			list  = new ArrayList();

		for ( Iterator it = loaders.iterator(); it.hasNext(); )
		{
			IOroLoader loader = (IOroLoader) it.next();
			loader.loadUnitNamesFromPackage(nombrePaquete, list);
		}
		
		return list;
	}
	
	///////////////////////////////////////////////////////////////
	public List loadUnitsFromPackage(String nombrePaquete, List list)
	{
		if ( list == null )
			list  = new ArrayList();

		list = coreOroLoader.loadUnitsFromPackage(nombrePaquete, list);
		for ( Iterator it = loaders.iterator(); it.hasNext(); )
		{
			IOroLoader loader = (IOroLoader) it.next();
			loader.loadUnitsFromPackage(nombrePaquete, list);
		}
		
		return list;
	}
	
	///////////////////////////////////////////////////////////////
	public boolean verify(List list)
	{
		boolean ok = true; 
		for ( Iterator it = loaders.iterator(); it.hasNext(); )
		{
			IOroLoader loader = (IOroLoader) it.next();
			ok = ok && loader.verify(list);
			if ( !ok && list == null )
				return false;
		}
		return ok;
	}

	///////////////////////////////////////////////////////////////
	public void close()
	throws IOException
	{
		for ( Iterator it = loaders.iterator(); it.hasNext(); )
		{
			IOroLoader loader = (IOroLoader) it.next();
			loader.close();
		}
	}
	
	///////////////////////////////////////////////////////////////
	public String toString()
	{
		StringBuffer sb = new StringBuffer("OroLoaderManager: ");
		for ( Iterator it = loaders.iterator(); it.hasNext(); )
		{
			IOroLoader loader = (IOroLoader) it.next();
			sb.append("\t" +loader+ "\n");
		}
		return sb.toString();
	}
}

