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
import java.util.zip.ZipException;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.io.*;
import java.net.URL;
import java.net.JarURLConnection;


///////////////////////////////////////////////////////////////
/**
 * Cargador de unidades desde un archivo zip.
 */
public class ZipFileOroLoader implements IOroLoader
{
	private ZipFile zf;
	private String name;
	private Logger logger;
	
	private Map cache;


	///////////////////////////////////////////////////////////////
	public ZipFileOroLoader(File file)
	throws IOException
	{
		this.zf = new ZipFile(file);
		name = file.getName();
		logger = Logger.getLogger();
		cache = new HashMap();
	}
	
	//////////////////////////////////////////////////////////////////////
	public String getName()
	{
		return name;
	}

	/////////////////////////////////////////////////////////////////////
	public NUnidad getUnit(String nombre)
	{
		NUnidad n = (NUnidad) cache.get(nombre);
		if ( n != null )
		{
			logger.log("........ Encontrado en cache zip");
			return n;
		}

		String slashname = Util.replace(nombre, "::", "/");
		logger.log("ZipFileOroLoader: getEntry " +slashname);
		ZipEntry en = zf.getEntry(slashname + ".oro");
		if ( en == null )
		{
			return null;
		}

		try
		{
			ObjectInputStream in = new ObjectInputStream(zf.getInputStream(en));
			n = ManejadorUnidades.leerUnidad(in);
			in.close();
			cache.put(nombre, n);
		}
		catch(Exception e)
		{
			// ignore.
		}
		
		return n;
	}
	
	/////////////////////////////////////////////////////////////////////
	public String getUnitSource(String unitname)
	{
		unitname += ".loro";
		logger.log("ZipFileOroLoader: getUnitSource: " +unitname);
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
		name = Util.replace(name, "::", File.separator);
		name = name.replace(File.separatorChar, '/');
		InputStream n = null;
		ZipEntry en = zf.getEntry(name);
		if ( en != null )
		{
			try
			{
				n = zf.getInputStream(en);
			}
			catch(Exception e)
			{
				// ignore
			}
		}
		return n;
	}

	///////////////////////////////////////////////////////////////
	public List getPackageNames(List list)
	{
		if ( list == null )
			list  = new ArrayList();
		for ( Enumeration enum = zf.entries(); enum.hasMoreElements(); )
		{
			ZipEntry entry = (ZipEntry) enum.nextElement();
			String name = entry.getName();
			if ( name.endsWith(".oro") )
			{
				int index = name.lastIndexOf("/");
				if ( index >= 0 )
				{
					String pkgname = name.substring(0, index);
					pkgname = Util.replace(pkgname, "/", "::");
					if ( !list.contains(pkgname) )
					{
						list.add(pkgname);
					}
				}
			}
		}
		return list;		
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
		nombrePaquete = Util.replace(nombrePaquete, "::", "/");
		for ( Enumeration enum = zf.entries(); enum.hasMoreElements(); )
		{
			ZipEntry entry = (ZipEntry) enum.nextElement();
			String name = entry.getName();
			if ( name.startsWith(nombrePaquete)
			&&   name.endsWith(".oro") )
			{
				name = name.substring(0, name.length() - 4);
				if ( incSubpkgs )   
				{
					// no importa si tiene subpaquete
					list.add(Util.replace(name, "/", "::"));
					continue;
				}
				// vea que que no tenga subpaquete: 
				String suffix = name.substring(nombrePaquete.length());
				if ( suffix.startsWith("/") )
					suffix = suffix.substring(1);
				
				if ( suffix.indexOf("/") < 0 )  
				{
					list.add(Util.replace(name, "/", "::"));
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
		boolean ok = true;
		for ( Enumeration enum = zf.entries(); enum.hasMoreElements(); )
		{
			ZipEntry entry = (ZipEntry) enum.nextElement();
			String name = entry.getName();
			if ( ! name.endsWith(".oro") )
			{
				continue;
			}
			try
			{
				ObjectInputStream in =
					new ObjectInputStream(zf.getInputStream(entry))
				;
	
				NUnidad n = ManejadorUnidades.leerUnidad(in);
				in.close();
			}
			catch(Exception e)
			{
				if ( list == null )
					return false;
		
				ok = false;
				list.add(name);
			}
		}
		return ok;
	}
	
	///////////////////////////////////////////////////////////////
	public void close()
	throws IOException
	{
		zf.close();
	}


	///////////////////////////////////////////////////////////////
	public String toString()
	{
		return "ZipFileOroLoader: " +getName();
	}
	
	
	/////////////////////////////////////////////////////////////////////
	public List getFilenames(FilenameFilter fnfilter)
	{
		List list  = new ArrayList();
		for ( Enumeration enum = zf.entries(); enum.hasMoreElements(); )
		{
			ZipEntry entry = (ZipEntry) enum.nextElement();
			if ( !entry.isDirectory() )
			{
				String name = entry.getName();
				if ( fnfilter == null
				||   fnfilter.accept(null, name) )
				{
					list.add(name);
				}
			}
		}
		return list;		
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
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		String line;
		while ( (line = br.readLine()) != null )
		{
			sb.append(line+ "\n");
		}
		return sb.toString();
	}
}


