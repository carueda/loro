package loro.util;

import loro.IOroLoader;
import loro.ijava.LManejadorES;
import loro.arbol.*;
import loro.util.Util;
import loro.compilacion.ClaseNoEncontradaException;
import loro.Loro;

import java.util.*;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.io.*;
import java.net.URL;
import java.net.JarURLConnection;
import java.nio.charset.Charset;


///////////////////////////////////////////////////////////////
/**
 * Cargador de las unidades del núcleo de Loro.
 * El recurso que contiene este núcleo se incluye en el mismo
 * archivo que contiene las clases. 
 * @author Carlos Rueda
 * @version $Id$
 */
class CoreOroLoader implements IOroLoader
{
	/** Ruta completa al recurso de apoyo. */
	public static final String RESOURCE_PATH = "recurso/loroI.lar";
	
	/** Nombre del recurso de apoyo. */
	private static final String RESOURCE_NAME = "loroI.lar";
	
	private Logger logger;
	private List unitnames;
	private List pkgnames;
	private Map cache;
	
	///////////////////////////////////////////////////////////////
	/**
	 * Crea un cargador de unidades para el núcleo.
	 * Este objeto es creado aunque no se detecte el recurso núcleo
	 * correspondiente como forma de permitir la misma compilación de
	 * dicho núcleo.
	 */
	public CoreOroLoader()
	{
		logger = Logger.getLogger();
		pkgnames = new ArrayList();
		unitnames = new ArrayList();
		cache = new HashMap();
		
		logger.log("Abriendo recurso núcleo");
		try
		{
			ZipInputStream zis = _getZipInputStream();
			ZipEntry entry;
			while ( (entry = zis.getNextEntry()) != null )
			{
				String nombre_original = entry.getName();
				
				if ( !nombre_original.endsWith(".oro") )
					continue;
				
				nombre_original = nombre_original.substring(0,
					nombre_original.length() - 4
				);
				
				// intente leer la unidad:
				NUnidad n = null;
				ObjectInputStream ois = new ObjectInputStream(zis);
				n = ManejadorUnidades.leerUnidad(ois);
	
				if ( n != null )
				{
					String nombre = Util.replace(nombre_original, "/", "::");
					cache.put(nombre, n);
					unitnames.add(nombre);
					int index = nombre.lastIndexOf("::");
					if ( index >= 0 )
					{
						String pkgname = nombre.substring(0, index);
						if ( !pkgnames.contains(pkgname) )
						{
							pkgnames.add(pkgname);
						}
					}
				}
				zis.closeEntry();
			}
			zis.close();
		}
		catch(Exception ex)
		{
			String msg = "Advertencia: No se encuentra el recurso de apoyo "
				+RESOURCE_PATH;
			;
			System.out.println(msg);
			logger.log(msg);
		}
	}
	

	//////////////////////////////////////////////////////////////////////
	public String getName()
	{
		return RESOURCE_NAME;
	}
	
	/////////////////////////////////////////////////////////////////////
	public NUnidad getUnit(String nombre)
	{
		logger.log("CoreOroLoader: buscando en cache : [" +nombre+ "]");
		NUnidad n = (NUnidad) cache.get(nombre);
		return n;
	}

	/////////////////////////////////////////////////////////////////////
	public String getUnitSource(String unitname)
	{
		unitname = Util.replace(unitname, "::", "/") + ".loro";
		String src = null;
		InputStream is = getResourceAsStream(unitname);
		if ( is != null )
		{
			try
			{
				src = readInputStream(is);
				logger.log("Encontrado fuente " +unitname+ " en " +getName());
				is.close();
			}
			catch(Exception e)
			{
				// ignore
			}
		}
		return src;
	}
	
	//////////////////////////////////////////////////////////////////////
	public InputStream getResourceAsStream(String name)
	{
		logger.log("Buscando recurso " +name);
		try
		{
			boolean found = false;
			ZipInputStream zis = _getZipInputStream();
			ZipEntry entry;
			while ( (entry = zis.getNextEntry()) != null )
			{
				if ( entry.getName().equals(name) )
				{
					found = true;
					break;
				}
				else
				{
					zis.closeEntry();
				}
			}
			
			InputStream is = null;  // a retornar
			if ( found )
			{
				is = zis;
			}
			else
			{
				zis.close();
			}
			return is;
		}
		catch(Exception ex)
		{
			throw new RuntimeException("CoreOroLoader.getResourceAsStream: " +ex.getMessage());
		}
	}

	//////////////////////////////////////////////////////////////////////
	public List getPackageNames(List list)
	{
		return pkgnames;
	}

	///////////////////////////////////////////////////////////////
	public List loadUnitNamesFromPackage(String nombrePaquete, List list)
	{
		if ( list == null )
			list  = new ArrayList();

		boolean incSubpkgs = false;
		if ( nombrePaquete.endsWith("*") )
		{
			incSubpkgs = true;
			nombrePaquete = nombrePaquete.substring(0, nombrePaquete.length() -1);
		}
		for ( Iterator it = unitnames.iterator(); it.hasNext(); )
		{
			String nombre = (String) it.next();
			if ( nombre.startsWith(nombrePaquete) )
			{
				if ( incSubpkgs )   
				{
					// no importa si tiene subpaquete
					list.add(nombre);
					continue;
				}

				// vea que que no tenga subpaquete: 
				String suffix = nombre.substring(nombrePaquete.length());
				if ( suffix.startsWith("::") )
					suffix = suffix.substring(2);
				
				if ( suffix.indexOf(":") < 0 )  
				{
					list.add(nombre);
				}
			}
		}
		
		return list;
	}
	
	///////////////////////////////////////////////////////////////
	public List loadUnitsFromPackage(String nombrePaquete, List units)
	{
		if ( units == null )
			units  = new ArrayList();
		
		List names = loadUnitNamesFromPackage(nombrePaquete, null);
		for ( Iterator it = names.iterator(); it.hasNext(); )
		{
			String name = (String) it.next();
			NUnidad uni = getUnit(name);
			if ( uni != null )
			{
				units.add(uni);
			}
		}
		return units;
	}
	
	///////////////////////////////////////////////////////////////
	public boolean verify(List list)
	{
		// Se toma como operación exitosa.
		return true;
	}
	
	///////////////////////////////////////////////////////////////
	public void close()
	throws IOException
	{
		// nothing to do
	}
	
	///////////////////////////////////////////////////////////////
	public String toString()
	{
		return "CoreOroLoader: " +getName();
	}

	/////////////////////////////////////////////////////////////////////
	public List getFilenames(FilenameFilter fnfilter)
	{
		try
		{
			List list  = new ArrayList();
			ZipInputStream zis = _getZipInputStream();
			ZipEntry entry;
			while ( (entry = zis.getNextEntry()) != null )
			{
				if ( !entry.isDirectory() )
				{
					String name = entry.getName();
					if ( fnfilter == null
					||   fnfilter.accept(null, name) )
					{
						list.add(name);
					}
				}			
				zis.closeEntry();
			}
			zis.close();
			return list;
		}
		catch(Exception ex)
		{
			throw new RuntimeException("CoreOroLoader.getFilenames: " +ex.getMessage());
		}
	}
	
	///////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para obtener el flujo hacia el recurso núcleo.
	 */
	private ZipInputStream _getZipInputStream()
	throws Exception
	{
		ClassLoader cl = getClass().getClassLoader();
		InputStream is = cl.getResourceAsStream(RESOURCE_PATH);
		return new ZipInputStream(is);
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Reads an entire text file.
	 *
	 * @param file        Text file.
	 */
	private static String readInputStream(InputStream is)
	throws IOException
	{
		Charset cs = Charset.forName("ISO-8859-1");
		BufferedReader br = new BufferedReader(new InputStreamReader(is, cs));
		StringBuffer sb = new StringBuffer();
		String line;
		while ( (line = br.readLine()) != null )
		{
			sb.append(line+ "\n");
		}
		return sb.toString();
	}
}

