package loro.ijava;



import java.io.IOException;

/////////////////////////////////////////////////////////////////////
/**
 * Interface del Manejador de entrada-salida estandares para tiempo
 * de ejecucion de un programa Loro.
 *
 * @author Carlos Rueda
 * @version 8/15/01
 */
public interface LManejadorES
{

	//////////////////////////////////////////////////////////////
	/**
	 * Escribe una cadena por la salida estandar.
	 * Si este argumento es null, se imprime "nulo".
	 */
	public void escribir(String s);
	//////////////////////////////////////////////////////////////
	/**
	 * Escribe una cadena por la salida con cambio de linea al final.
	 * Si este argumento es null, se imprime "nulo".
	 */
	public void escribirln(String s);
	//////////////////////////////////////////////////////////////
	/**
	 * Lee un valor booleano: cierto o falso.
	 */
	public boolean leerBooleano()
	throws IOException;
	/////////////////////////////////////////////////////////////
	/**
	 * Lee una cadena.
	 */
	public String leerCadena()
	throws IOException;
	////////////////////////////////////////////////////////////////
	/**
	 * Lee una cadena no vacía. Es decir, se hace un
	 * ciclo de lectura hasta que la cadena no sea vacía.
	 */
	public String leerCadenaNoVacia()
	throws IOException;
	/////////////////////////////////////////////////////////////
	/**
	 * Lee un caracter.
	 */
	public char leerCaracter()
	throws IOException;
	/////////////////////////////////////////////////////////////
	/**
	 * Lee un entero.
	 */
	public int leerEntero()
	throws IOException;
	/////////////////////////////////////////////////////////////
	/**
	 * Lee un real.
	 */
	public double leerReal()
	throws IOException;
}