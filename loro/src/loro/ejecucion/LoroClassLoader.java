package loro.ejecucion;

import loro.Loro;
import loro.util.Util;

import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.io.IOException;
import java.util.*;

/////////////////////////////////////////////////////////////////////
/**
 * Cargador de clases Java de soporte para programas en Loro.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class LoroClassLoader
{
	/**
	 * El classloader para las posibles clases Java de soporte
	 * para paquetes de extension.
	 */
	private URLClassLoader classLoader = null;
	

	/////////////////////////////////////////////////////////////////
	/**
	 * Crea un cargador de classes básico.
	 */
	public LoroClassLoader()
	{
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Pone el directorio de extensiones.
	 *
	 * NOTA: Este metodo debe llamarse una sola vez ya que no es
	 * acumulativo (o sea, solo se contempla UN directorio de extension).
	 *
	 * @param dir_ext Todos los ".lar y *.jar encontrados en este directorio
	 *                son tomados en cuenta.
	 */
	public void ponDirectorioExtensiones(String dir_ext)
	throws MalformedURLException
	{
		Loro.log("Creando classloader en " +dir_ext+ "...");

		File dir_file = new File(dir_ext);
		File[] files = dir_file.listFiles();
		List list = new ArrayList();
		if ( files != null )
		{
			for (int i = 0; i < files.length; i++)
			{
				File file = files[i];

				if ( file.getName().toLowerCase().endsWith(".lar")
				||   file.getName().toLowerCase().endsWith(".jar") )
				{
					Loro.log("Incluyendo: " +file.getAbsoluteFile());
					try
					{
						URL url = file.getAbsoluteFile().toURL();
						list.add(url);
					}
					catch (MalformedURLException ex)
					{
						Loro.log("   MalformedURLException: " +ex.getMessage());
					}
				}
			}
		}

		if ( list.size() == 0 )
		{
			Loro.log("Sin elementos de busqueda para el classloader.");
		}
		else
		{
			URL[] a = (URL[]) list.toArray(EMPTY_URL_ARRAY);
			classLoader = new URLClassLoader(a);
			Loro.log("classloader creado con " +a.length+ " elementos de búsqueda.");
		}

		Loro.log("Finalizado classloader.");
	}

	///////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la clase Java segun el nombre dado.
	 * Primero intenta con Class.forName; si no se resuelve, intenta
	 * con el classLoader en caso que lo haya.
	 *
	 * @param class_name
	 *      Nombre completo de la clase a obtener.
	 *
	 * @throws ClassNotFoundException
	 *      Si no se encuentra la clase.
	 */
	public Class getClass(String class_name)
	throws ClassNotFoundException
	{
		Class clazz = null;

		try
		{
			clazz = Class.forName(class_name);

			Loro.log("Clase " +class_name+ " obtenida de Class.forName");
			return clazz;
		}
		catch ( ClassNotFoundException ex )
		{
			if ( classLoader == null )
				throw ex;

			// intente entonces con el classloader...
		}

		try
		{
			clazz = classLoader.loadClass(class_name);
			Loro.log("Clase " +class_name+ " obtenida del classloader");
		}
		catch ( ClassNotFoundException ex )
		{
			Loro.log("Clase " +class_name+ " NO encontrada");
			throw ex;
		}

		return clazz;
	}

	
	/** Para toArray */
	private static final URL[] EMPTY_URL_ARRAY = new URL[0];
}