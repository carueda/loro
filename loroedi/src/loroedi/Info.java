package loroedi;

import java.util.*;
import java.io.*;
import java.util.ResourceBundle;

/////////////////////////////////////////////////////////////////////
/**
 * Informacion sobre el nombre del sistema y version.
 * Estos datos se leen del recurso loroedi/resource/info.properties.
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
	 * Obtiene los datos del recurso loroedi/resource/info.properties.
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
			InputStream is = cl.getResourceAsStream("loroedi/resource/info.properties");
			if ( is == null ) {
				System.err.println(Str.get("info.props_not_found"));
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


	private static ResourceBundle strings = null;
	
	/** Sets up the bundle loro.resource.strings */
	static {
		try {
			strings = ResourceBundle.getBundle("loroedi.resource.strings");
		}
		catch(java.util.MissingResourceException ex) {
			System.err.println(
				"!!!!!! Warning:\n" +
				"!!!!!! Cannot get bundle loroedi.resource.strings.\n" +
				"!!!!!! The system has not been compiled properly.\n"
			);
		}
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
}
