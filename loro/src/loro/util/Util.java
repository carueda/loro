package loro.util;

import loro.arbol.*;
import loro.Rango;
import loro.IOroLoader;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.*;
import java.io.*;
import java.util.zip.*;


///////////////////////////////////////////////////////////////
/**
 * Algunas utilerias generales
 *
 */
public final class Util
{

	////////////////////////////////////////////////////////
	/**
	 */
	public static void _assert(boolean c, String m)
	{
		if ( !c )
		{
			throw new RuntimeException("Condición fallida: " +m);
		}
	}

	///////////////////////////////////////////////////////////////////
	/**
	 * Es var una variable sem'antica?
	 */
	public static boolean esVarSemantica(String var)
	{
		return var.endsWith("'");
	}


	///////////////////////////////////////////////////////////
	/**
	 * Obtiene la ruta completa correspondiente a la ruta
	 * dada. No pone separador de directorio al final.
	 */
	public static String obtStringRuta(String[] ruta)
	{
		return obtStringRuta(ruta, "::");
	}

	///////////////////////////////////////////////////////////////////
	/**
	 * Procesa las posibles secuencias de escape
	 * y arma una cadena.
	 *
	 * @param s La cadena con secuencia de escape.
	 * @return La cadena procesada.
	 */
	public static String procesarCadena(String s)
	{
		StringBuffer sb = new StringBuffer();
		boolean enEscape = false;
		int enOctal = 0;
		int enUnicode = 0;
		int res = 0;

		for ( int i = 0; i < s.length(); i++ )
		{
			char c = s.charAt(i);

			if ( enOctal > 0 )
			{
				if ( '0' <= c && c <= '7' )
				{
					res = res*8 + c - '0';
					enOctal--;
					if ( enOctal == 0 )
						sb.append((char) res);
					continue;
				}
				else
				{
					sb.append((char) res);
					enOctal = 0;
				}
			}
			else if ( enUnicode > 0 )
			{
				if ( '0' <= c && c <= '9'
				||   'a' <= c && c <= 'f'
				||   'A' <= c && c <= 'F' )
				{
					if ( '0' <= c && c <= '9' )
						res = res*16 + c - '0';
					else if ( 'a' <= c && c <= 'f' )
						res = res*16 + c - 'a' + 10;
					else
						res = res*16 + c - 'A' + 10;

					enUnicode--;

					if ( enUnicode == 0 )
						sb.append((char) res);
					continue;
				}
				else
				{
					sb.append((char) res);
					enUnicode = 0;
				}
			}
			else if ( enEscape )
			{
				enEscape = false;
				switch(c)
				{
					case 'n': res = '\n'; break;
					case 't': res = '\t'; break;
					case 'b': res = '\b'; break;
					case 'r': res = '\r'; break;
					case 'f': res = '\f'; break;
					case '\\': res = '\\'; break;
					case '\'': res = '\''; break;
					case '"': res = '"'; break;

					case 'u':
						enUnicode = 4;
						res = 0;
						continue;

					default:
						if ( '0' <= c && c <= '7' )
						{
							res = c - '0';
							if ( c <= '3' )
								enOctal = 2;	// 2 más posibles
							else
								enOctal = 1;	// 1 más posible

							continue;
						}
						break;
				}
				sb.append((char) res);
				continue;
			}

			// Estado normal.

			if ( c == '\\' )
				enEscape = true;
			else
				sb.append(c);
		}

		if ( enOctal > 0 || enUnicode > 0 )
		{
			//algo queda por agregar
			sb.append((char)res);
		}


		return sb.toString();
	}
	///////////////////////////////////////////////////////////////////
	/**
	 * Procesa la posible secuencia de escape
	 * y arma un caracter.
	 *
	 * @param s La cadena con secuencia de escape.
	 * @return El caracter correspondiente.
	 */
	public static char procesarCaracter(String s)
	{
		String cad = procesarCadena(s);

		return cad.charAt(0);
	}

	///////////////////////////////////////////////////////////////////
	/**
	 * Obtiene un string correspondiente a una cadena Loro, o sea,
	 * con las comillas y secuencias de escape.
	 * <p>
	 * Por ejemplo, un cambio de linea se convierte en los caracteres ``\n''.
	 * <p>
	 * En general se convierten: <code> \n   \t   \r   \f   \b </code>
	 * <p>
	 * A continuación, Si q es ', se hace la conversión ' por \'
	 * Si q es ", se hace la conversión " por \"
	 * <p>
	 * finalmente se pone q a ambos extremos.
	 *
	 * @param q  Normalmente uno de  ' o ". 
	 * @param s  La cadena a procesar
	 * @return La cadena procesada.
	 */
	public static String quote(char q, String s)
	{
		// conversiones generales:
		String[] ss = {
			"\n", "\\n",
			"\t", "\\t",
			"\r", "\\r",
			"\f", "\\f",
			"\b", "\\b",
		};
		for ( int i = 0; i < ss.length; i += 2 )
		{
			s = replace(s, ss[i], ss[i + 1]);
		}
		
		// caso caracter:
		if ( q == '\'' )
		{
			s = replace(s, "'", "\\'");
		}
		// caso cadena:
		else if ( q == '"' )
		{
			s = replace(s, "\"", "\\\"");
		}
		// finalmente se pone q a ambos extremos:
		return q + s + q;
	}

	///////////////////////////////////////////////////////////////////
	/**
	 * Formatea una cadena completando con cierto caracter a la derecha
	 * tal que la longitud total sea una dada.
	 * El resultado se agrega al StringBuffer dado.
	 */
	public static void format(Object o, int width, char fill, StringBuffer sb)
	{
		String s = o == null ? "nulo" : o.toString();
		sb.append(s);

		int len = s.length();
		while ( len < width )
		{
			sb.append(fill);
			len++;
		}
	}

	///////////////////////////////////////////////////////////
	// Clase no instanciable.
	private Util() {}

	///////////////////////////////////////////////////////////////////
	/**
	 * Concatena un token a una secuencia de tokens.
	 */
	public static String[] concatenarNombre(String[] ids, String id)
	{
		String[] nruta = new String[ids.length + 1];
		System.arraycopy(ids, 0, nruta, 0, ids.length);
		nruta[nruta.length - 1] = id;
		return nruta;
	}

	///////////////////////////////////////////////////////////////////
	/**
	 * Es var una variable sem'antica?
	 */
	public static boolean esVarSemantica(TId id)
	{
		return id != null && esVarSemantica(id.obtId());
	}

	///////////////////////////////////////////////////////////
	/**
	 * Obtiene la direccion relativa (estilo recurso) dest de acuerdo con base.
	 * Estrategia simple: siempre "retrocede" con "../" la misma cantidad de
	 * deparadores de base; y al final va al destino, sin
	 * importar si se repite algo del recorrido.
	 *
	 * Ejs (El :: significa separacion entre elementos de arreglo):
	 *
	 * <pre>
	 *  base         dest       retorno
	 *  a            x::y::z    x/y/z
	 *  a::b         x::y::z    ../x/y/z
	 *  a::b         a::b::e    ../a/b/e
	 *  a::b::c      a::f::g    ../../a/f/g
	 *  a::b::c::d   a::b       ../../../a/b
	 *  a            b          b
	 * </pre>
	 */
	public static String getRelativeLocation(String[] baseids, String[] destids)
	{
		StringBuffer sb = new StringBuffer();
		for ( int k = 0; k < baseids.length - 1; k++ )
		{
			sb.append("../");
		}

		for ( int k = 0; k < destids.length; k++ )
		{
			sb.append(destids[k]);
			if ( k < destids.length - 1 )
			{
				sb.append("/");
			}
		}

		return sb.toString();
	}

	///////////////////////////////////////////////////////////
	/**
	 * Obtiene la direccion relativa de dest de acuerdo con base.
	 * Ver documentacion de getRelativeLocation(Token[] base, Token[] dest).
	 */
	public static String getRelativeLocation(String[] baseids, String sdest)
	//
	// Este metodo fue implementado siguiendo la misma estrategia de
	// getRelativeLocation(Token[] base, Token[] dest), pero con ayuda
	// de un StringTokenizer para procesar sdest (que es String aqui).
	//
	{
		StringBuffer sb = new StringBuffer();
		for ( int k = 0; k < baseids.length - 1; k++ )
		{
			sb.append("../");
		}

		StringTokenizer st = new StringTokenizer(sdest, ":");
		while ( st.hasMoreTokens()  )
		{
			sb.append(st.nextToken());
			if ( st.hasMoreTokens() )
			{
				sb.append("/");
			}
		}

		return sb.toString();
	}

	///////////////////////////////////////////////////////////
	/**
	 * Dice si los dos nombre son iguales.
	 */
	public static boolean nombresIguales(String[] n1, String[] n2)
	{
		return obtStringRuta(n1).equals(obtStringRuta(n1));
	}


	///////////////////////////////////////////////////////////
	/**
	 * Obtiene la ruta completa correspondiente a la ruta
	 * dada. No pone separador de directorio al final.
	 */
	public static String obtStringRuta(String[] ids, String sep)
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ids.length; i++)
		{
			if ( i > 0 )
				sb.append(sep);
			sb.append(ids[i]);
		}
		return sb.toString();
	}

	///////////////////////////////////////////////////////
	/**
	 * Si e corresponde a variable semántica, retorna
	 * su termino; si no, retorna null.
	 */
	public static TId obtVarSemantica(NExpresion e)
	{
		if ( !(e instanceof NId) )
		{
			return null;
		}

		TId id = ((NId) e).obtId();
		if ( id.obtId().endsWith("'") )
		{
			return id;
		}

		return null;
	}

	///////////////////////////////////////////////////////////
	/**
	 * Replace in 's' all occurrences of 'from' to 'to'.
	 */
	public static String replace(String s, String from, String to)
	{
		StringBuffer sb = new StringBuffer();
		int len = from.length();
		int i, p = 0;
		while ( (i = s.indexOf(from, p)) >= 0 )
		{
			sb.append(s.substring(p, i) + to);
			p = i + len;
		}
		sb.append(s.substring(p));
		return sb.toString();
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Gets the list of names of all files in a directory.
	 *
	 * @param directory  The directory
	 * @param recurse    Recurse into subdirectories?
	 *
	 * @return The list of filenames (String's).
	 */
	public static List getFilenames(String directory, boolean recurse)
	{
		return getFilenames(directory, recurse, null);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Gets the list of names of all files in a directory accordinf to a
	 * filename filter.
	 *
	 * @param directory  The directory
	 * @param recurse    Recurse into subdirectories?
	 * @param fnfilter   The filename filter. Can be null.
	 *
	 * @return The list of filenames (String's).
	 */
	public static List getFilenames(
		String directory,
		boolean recurse,
		FilenameFilter fnfilter
	)
	{
		List list = new ArrayList();
		File file = new File(directory);
		int level = recurse ? Integer.MAX_VALUE : 1;
		addFilenames(list, file, "", level, fnfilter);
		return list;
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Adds filenames to a list.
	 *
	 * @param list The list.
	 * @param file The file to start with.
	 * @param level The level of recursion. Must be >= 1
	 * @param fnfilter   The filename filter. Can be null.
	 */
	private static void addFilenames(
		List list, 
		File file, 
		String basename, 
		int level,
		FilenameFilter fnfilter
	)
	{
		if ( file.isDirectory() )
		{
			if ( level > 0 )
			{
				File[] dir = file.listFiles();
				for ( int i = 0; i < dir.length; i++ )
				{
					if ( dir[i].isDirectory() )
					{
						addFilenames(list, dir[i], 
							basename+ dir[i].getName()+ "/", 
							level - 1,
							fnfilter
						);
					}
					else
					{
						if ( fnfilter == null
						||   fnfilter.accept(file, dir[i].getName()) )
						{
							list.add(basename+ dir[i].getName());
						}
					}
				}
			}
		}
		else
		{
			String name2 = basename+ file.getName();
			File file2 = new File(name2);
			if ( fnfilter == null
			||   fnfilter.accept(file2.getParentFile(), file2.getName()) )
			{
				list.add(name2);
			}
		}
	}


	///////////////////////////////////////////////////////////////
	/**
	 * Dice si dos valores son iguales, teniendo en cuenta si
	 * son null.
	 */
	public static boolean valoresIguales(Object e_val, Object f_val)
	{
		// Si solo uno de los dos es null, falso:
		if ( e_val == null ^ f_val == null )
			return false;

		// Si ambos null, cierto:
		if ( e_val == null )
			return true;

		// Utilice equals:
		return e_val.equals(f_val);
	}


    ////////////////////////////////////////////////////////////////////////////
    public static void copyDirectoryToZip(
		File basedir, 
		ZipOutputStream zos,
		FilenameFilter fnfilter
	)
    throws Exception
    {
        List list = getFilenames(basedir.getAbsolutePath(), true, fnfilter);

        for ( Iterator it = list.iterator(); it.hasNext(); )
        {
            String filename = (String) it.next();
            copyFileToZip(
                filename,
                basedir.getAbsolutePath()+ "/" +filename,
                zos
            );
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    public static void copyExtensionToZip(
		IOroLoader oroLoader, 
		ZipOutputStream zos,
		FilenameFilter fnfilter
	)
    throws Exception
    {
        List list = oroLoader.getFilenames(fnfilter);

        for ( Iterator it = list.iterator(); it.hasNext(); )
        {
            String name = (String) it.next();
			InputStream is = oroLoader.getResourceAsStream(name);
            copyStreamToZip(
                name,
                is,
                zos
            );
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    public static void copyExtensionToDirectory(
		IOroLoader oroLoader, 
		File dir,
		FilenameFilter fnfilter
	)
    throws Exception
    {
        List list = oroLoader.getFilenames(fnfilter);

        for ( Iterator it = list.iterator(); it.hasNext(); )
        {
            String name = (String) it.next();
			InputStream is = oroLoader.getResourceAsStream(name);
			File dest = new File(dir, name);
			File parent = dest.getParentFile();
			if ( !parent.exists() )
				parent.mkdirs();
			OutputStream os = new FileOutputStream(dest); 
            copyStreamToStream(is, os);
			os.close();
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    public static void copyFileToZip(
		String entryName,
		String filename,
		ZipOutputStream zos
	)
    throws Exception
    {
        ZipEntry entry = new ZipEntry(entryName);
        zos.putNextEntry(entry);
		FileInputStream fis = new FileInputStream(filename);
		copyStreamToStream(new FileInputStream(filename), zos);
		fis.close();
        zos.closeEntry();
    }

    ////////////////////////////////////////////////////////////////////////////
    public static void copyStreamToZip(
		String entryName,
		InputStream is,
		ZipOutputStream zos
	)
    throws Exception
    {
        ZipEntry entry = new ZipEntry(entryName);
        zos.putNextEntry(entry);
		copyStreamToStream(is, zos);
        zos.closeEntry();
    }
	
    ////////////////////////////////////////////////////////////////////////////
    private static void copyStreamToStream(InputStream is, OutputStream os)
    throws Exception
    {
        BufferedInputStream bis = new BufferedInputStream(is);
        int avail = bis.available();
        byte[] buffer = new byte[avail];
        bis.read(buffer, 0, avail);
        os.write(buffer, 0, avail);
    }
}