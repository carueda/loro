package loroedi;

import java.util.*;
import java.io.*;

/////////////////////////////////////////////////////////////////////
/**
 * Informacion sobre el nombre del sistema y version.
 * Estos datos se leen del recurso loroedi/info.properties.
 *
 * @author Carlos Rueda
 * @version 2002-03-06
 */
public final class Info
{
	private static String name = null;
	private static String version;
	private static String build;

	////////////////////////////////////////////////////////
	/**
	 * Obtiene los datos del recurso loroedi/info.properties.
	 */
	private static void obtInfo() 
	{
		if ( name == null )
		{
			// valores por defecto:
			name = "LoroEDI";
			version = "v";
			build = "b";
			
			ClassLoader	cl = new Info().getClass().getClassLoader();
			Properties props = new Properties(); 
			InputStream is = cl.getResourceAsStream("loroedi/info.properties");
			if ( is == null )
			{
				System.err.println(
"!!!!!! Recurso loroedi/info.properties no encontrado.\n" +
"!!!!!! El sistema LoroEDI no ha sido compilado correctamente.\n"+
"!!!!!! Se ponen valores de informacion por defecto."
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
			
			name    = props.getProperty("loroedi.name");
			version = props.getProperty("loroedi.version");
			build   = props.getProperty("loroedi.build");
		}
	}

	////////////////////////////////////////////////////////////
	/**
	 * Obtiene el "build" del sistema.
	 */
	public static String obtBuild()
	{
		obtInfo();
		return build;
	}

	////////////////////////////////////////////////////////////
	/**
	 * Obtiene la versión del sistema.
	 */
	public static String obtVersion()
	{
		obtInfo();
		return version;
	}

	////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre del sistema.
	 */
	public static String obtNombre()
	{
		obtInfo();
		return name;
	}


	////////////////////////////////////////////////////////////
	/**
	 * Obtiene el título del Entorno de Desarrollo Integrado.
	 */
	public static String obtTituloEDI()
	{
		obtInfo();
		return obtNombre()+ 
		" Versión " +obtVersion()+
		" (" +obtBuild()+ ")\n"+
		"Entorno de Desarrollo Integrado";
	}

	////////////////////////////////////////////////////////////
	/**
	 * Obtiene el título del intérprete interactivo.
	 */
	public static String obtTituloII()
	{
		obtInfo();
		return obtNombre()+ 
		" Versión " +obtVersion()+
		" (" +obtBuild()+ ")\n"+
		"Intérprete Interactivo";
	}

	////////////////////////////////////////////////////////
	// Solo se instancia una vez para acceder al classloader.
	private Info()	{}
}