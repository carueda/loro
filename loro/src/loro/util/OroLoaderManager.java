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
 * @since 0.8pre1
 * @version 2002-08-25
 * @author Carlos Rueda
 */
public class OroLoaderManager
{
	/** Cargadores considerados como extensiones. */
	private List extensionLoaders;
	
	/** Los File's correspondientes a las extensiones. */
	private List extensionFiles;
	
	/** Cargadores de directorios. */
	private List directoryLoaders;
	
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
		directoryLoaders = new ArrayList();
		extensionLoaders = new ArrayList();
		extensionFiles = new ArrayList();
		loaders = new ArrayList();
		for ( int i = 0; i < files.length; i++ )
		{
			File file = files[i];
			try
			{
				if ( file.isFile() )
				{
					addExtensionToPath(file);
				}
				else if ( file.isDirectory() )
				{
					addDirectoryToPath(file);
				}
			}
			catch(Exception ex)
			{
				logger.log(file+ ": " +ex.getMessage());
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Adiciona una directorio a la ruta de búsqueda.
	 *
	 * @since 0.8pre1
	 *
	 * @param dir El directorio a incluir.
	 */
	public void addDirectoryToPath(File dir)
	{
		IOroLoader ol = new DirectoryOroLoader(dir);
		directoryLoaders.add(ol); 
		loaders.add(ol);
		logger.log("OroLoaderManager: added directory: " +dir.getAbsolutePath());
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Adiciona un archivo zip a la ruta de búsqueda.
	 *
	 * @since 0.8pre1
	 *
	 * @param file El archivo zip a incluir.
	 */
	public void addExtensionToPath(File file)
	throws IOException
	{
		extensionFiles.add(file);
		IOroLoader ol = new ZipFileOroLoader(file);
		extensionLoaders.add(ol);
		loaders.add(ol);
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
	public NUnidad getUnit(String unitname)
	{
		NUnidad n = null;
		for ( Iterator it = loaders.iterator(); it.hasNext(); )
		{
			IOroLoader loader = (IOroLoader) it.next();
			n = loader.getUnit(unitname);
			if ( n != null )
			{
				break;
			}
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

