package loro.ejecucion;

import loro.ijava.LManejadorES;
import java.io.Reader;
import java.io.Writer;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.EOFException;
import java.io.InterruptedIOException;

/////////////////////////////////////////////////////////////////////
/**
 * Manejador de entrada-salida estandares.
 *
 * @author Carlos Rueda
 */
public class ManejadorEntradaSalida implements LManejadorES
{
	/**
	 * Flujo para entrada de datos.
	 */
	BufferedReader br;

	/**
	 * Flujo para salida de datos.
	 */
	PrintWriter pw;

	///////////////////////////////////////////////////////////////////
	/**
	 * Constructor EntradaSalida.
	 * Asocia la entrada y la salida estandares a las correspondientes
	 * estandares de Java, es decir, System.in y System.out.
	 * Utilice pon{Entrada,Salida}Estandar para cambiar estos flujos.
	 */
	public ManejadorEntradaSalida() 
	{
		br = new BufferedReader(new InputStreamReader(System.in));
		pw = new PrintWriter(System.out);
	}
	//////////////////////////////////////////////////////////////
	/**
	 * Escribe una cadena por la salida.
	 * Si este argumento es null, se imprime "nulo".
	 */
	public void escribir(String s)
	{
		pw.print(s != null ? s : "nulo");
		pw.flush();
	}
	//////////////////////////////////////////////////////////////
	/**
	 * Escribe una cadena por la salida con cambio de linea al final.
	 * Si este argumento es null, se imprime "nulo".
	 */
	public void escribirln(String s)
	{
		pw.println(s != null ? s : "nulo");
		pw.flush();
	}
	//////////////////////////////////////////////////////////////
	/**
	 * Lee un valor booleano: cierto o falso.
	 */
	public boolean leerBooleano()
	throws IOException
	{
		String res = leerCadenaNoVacia();
		if ( res != null )
		{
			res = res.trim();
			if ( res.equalsIgnoreCase("cierto") )
				return true;
			else if ( res.equalsIgnoreCase("falso") )
				return false;
		}
		throw new RuntimeException(
			"leerBooleano(): Formato inválido para el valor (se espera cierto o falso)"
		);
	}
	/////////////////////////////////////////////////////////////
	/**
	 * Lee una cadena.
	 */
	public String leerCadena()
	throws IOException
	{
		String s = null;
		try
		{
			s = leerLinea();
		}
		catch(EOFException ex)
		{
			throw new RuntimeException("Fin de archivo inesperado"); 
		}
/*		catch(InterruptedIOException ex)
		{
			// interrupcion se toma como terminacion externa:
			throw new loro.ejecucion.EjecucionTerminadaExternamenteException();
		}
		catch(IOException ex)
		{
			throw new RuntimeException("Error en lectura (" +ex.getMessage()+ ")"); 
		}*/
		
		return s;
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Lee una cadena no vacía. Es decir, se hace un
	 * ciclo de lectura hasta que la cadena no sea vacía.
	 * Utilidad para subclases.
	 */
	public String leerCadenaNoVacia()
	throws IOException
	{
		String s;
		do
		{
			s = leerCadena();
		}
		while ( s != null && s.length() == 0 );

		return s;
	}
	/////////////////////////////////////////////////////////////
	/**
	 * Lee un caracter.
	 */
	public char leerCaracter()
	throws IOException
	{
		String res = leerCadenaNoVacia();
		if ( res == null )
		{
			throw new RuntimeException(
				"leerCaracter(): Formato inválido para el valor"
			);
		}
			
		return res.charAt(0);
	}
	/////////////////////////////////////////////////////////////
	/**
	 * Lee un entero.
	 */
	public int leerEntero()
	throws IOException
	{
		String res = leerCadenaNoVacia();
		try
		{
			if ( res == null )
				throw new NumberFormatException();

			return Integer.parseInt(res.trim());
		}
		catch (NumberFormatException e)
		{
			throw new NumberFormatException(
				"leerEntero(): Formato inválido para el número"
			);
		}
	}
	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene una linea de la entrada.
	 */
	public String leerLinea()
	throws IOException
	{
		return br.readLine();
	}
	/////////////////////////////////////////////////////////////
	/**
	 * Lee un real.
	 */
	public double leerReal()
	throws IOException
	{
		String res = leerCadenaNoVacia();
		try
		{
			if ( res == null )
				throw new NumberFormatException();

			return new Double(res.trim()).doubleValue();
		}
		catch (NumberFormatException e)
		{
			throw new NumberFormatException(
				"leerReal(): Formato inválido para el número"
			);
		}
	}
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la entrada estandar.
	 */
	public BufferedReader obtEntradaEstandar()
	{
		return br;
	}
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la salida estandar.
	 */
	public PrintWriter obtSalidaEstandar()
	{
		return pw;
	}
	//////////////////////////////////////////////////////////////
	/**
	 * Pone la entrada estandar por el flujo indicado.
	 */
	public void ponEntradaEstandar(Reader r)
	{
		br = new BufferedReader(r);
	}
	//////////////////////////////////////////////////////////////
	/**
	 * Pone la salida estandar por el flujo indicado.
	 */
	public void ponSalidaEstandar(Writer w)
	{
		if ( w instanceof PrintWriter )
			pw = (PrintWriter) w;
		else
			pw = new PrintWriter(w, true);
	}
}