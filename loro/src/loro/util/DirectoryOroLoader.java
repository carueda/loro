package loro.util;

import loro.IOroLoader;
import loro.arbol.*;
import loro.util.Util;

import java.util.*;
import java.io.*;

///////////////////////////////////////////////////////////////
/**
 * Cargador de las unidades desde un directorio.
 */
class DirectoryOroLoader implements IOroLoader
{
	private File directory;
	private Logger logger;
	
	///////////////////////////////////////////////////////////////
	public DirectoryOroLoader(File directory)
	{
		this.directory = directory;
		logger = Logger.getLogger();
	}
	
	//////////////////////////////////////////////////////////////////////
	public String getName()
	{
		return directory.getName();
	}
	
	/////////////////////////////////////////////////////////////////////
	public NUnidad getUnit(String unitname)
	{
		unitname = Util.replace(unitname, "::", File.separator);
		
		logger.log("DirectoryOroLoader: getUnit: " +unitname);
		
		NUnidad n = null;
		ObjectInputStream in = null;

		File file = new File(directory, unitname+ ".oro");
		try
		{
			in = new ObjectInputStream(new FileInputStream(file));
			logger.log("Encontrado " +unitname+ " en " +directory);
		}
		catch(Exception e)
		{
			logger.log("No Encontrado " +unitname+ " en " +directory);
		}

		if ( in != null )
		{
			// intente leer la unidad:
			try
			{
				n = ManejadorUnidades.leerUnidad(in);
				in.close();
			}
			catch(Exception e)
			{
				logger.log("Leyendo unidad: " +unitname+ " de directory " +directory+ ": " +e.getMessage());
			}
		}

		return n;
	}

	/////////////////////////////////////////////////////////////////////
	public String getUnitSource(String unitname)
	{
		unitname = Util.replace(unitname, "::", File.separator);
		String src = null;
		File file = new File(directory, unitname+ ".loro");
		try
		{
			src = readFile(file);
			logger.log("Encontrado fuente " +unitname+ " en " +directory);
		}
		catch(Exception e)
		{
			// ignore
		}
		return src;
	}

	//////////////////////////////////////////////////////////////////////
	public InputStream getResourceAsStream(String name)
	{
		name = Util.replace(name, "::", File.separator);
		InputStream n = null;
		File file = new File(directory, name);
		try
		{
			n = new FileInputStream(file);
		}
		catch(Exception e)
		{
			// ignore
		}
		return n;
	}

	///////////////////////////////////////////////////////////////
	public List getPackageNames(List list)
	{
		if ( list == null )
			list  = new ArrayList();

		return list;		
	}
	
	///////////////////////////////////////////////////////////////
	public List loadUnitNamesFromPackage(String nombrePaquete, List list)
	{
		if ( list == null )
			list = new ArrayList();

		boolean incSubpkgs = false;
		if ( nombrePaquete.endsWith("*") )
		{
			incSubpkgs = true;
			nombrePaquete = nombrePaquete.substring(0, nombrePaquete.length() -1);
		}
		
		if ( incSubpkgs )
			throw new RuntimeException("No implementado aun");

		
		if ( nombrePaquete.endsWith("::") )
		{
			nombrePaquete = nombrePaquete.substring(0, nombrePaquete.length() -2);
		}
		File dir = new File(directory, Util.replace(nombrePaquete, "::", "/"));
		if ( !dir.isDirectory() )
		{
			logger.log(dir+ " no es directorio");
			return list;		
		}

		String sep = "";
		if ( nombrePaquete.length() > 0 )
			sep = "::";
		
		File[] files = dir.listFiles();
		for ( int i = 0; i < files.length; i++ )
		{
			String name = files[i].getName();
			if ( name.endsWith(".oro") )
			{
				name = nombrePaquete+ sep +name.substring(0, name.length() - 4);
				list.add(name);
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
		boolean recurse = true;
		List oro_list = Util.getFilenames(directory.getAbsolutePath(), recurse);
		for ( Iterator it = oro_list.iterator(); it.hasNext(); )
		{
			String name = (String) it.next();
			if ( ! name.toLowerCase().endsWith(".oro") )
			{
				continue;
			}
			
			File file = new File(directory, name);
			ObjectInputStream in = null;
			try
			{
				in = new ObjectInputStream(new FileInputStream(file));
			}
			catch(Exception e)
			{
				if ( list == null )
					return false;
				
				ok = false;
				list.add(file.getAbsolutePath());
			}
			finally
			{
				if ( in != null )
					try{in.close();} catch(IOException ex){}
			}
		}
		return ok;
	}
	
	///////////////////////////////////////////////////////////////
	public void close()
	throws IOException
	{
		// nada
	}

	///////////////////////////////////////////////////////////////
	public String toString()
	{
		return "DirectoryOroLoader: " +getName();
	}
	
	/////////////////////////////////////////////////////////////////////
	public List getFilenames(FilenameFilter fnfilter)
	{
		return Util.getFilenames(
			directory.getAbsolutePath(),
			true,   // recurse,
			fnfilter
		);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Reads an entire text file.
	 *
	 * @param file        Text file.
	 */
	private static String readFile(File file)
	throws FileNotFoundException, IOException
	{
		BufferedReader br = new BufferedReader(
			new InputStreamReader(new FileInputStream(file))
		);
		StringBuffer sb = new StringBuffer();
		String line;
		while ( (line = br.readLine()) != null )
		{
			sb.append(line+ "\n");
		}
		br.close();
		return sb.toString();
	}

}


