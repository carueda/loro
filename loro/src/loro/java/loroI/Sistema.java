package loro.java.loroI;

import loro.ijava.*;
import loro.Loro.Str;

/////////////////////////////////////////////////////////////////
/**
 * Implementacion de algoritmos del paquete loroI::sistema.
 *
 * @author Carlos Rueda
 */
public class Sistema {
	/** dormir. */
	public static void dormir(LAmbiente $amb,
		int ms
	)
	throws LException {
		try {
			Thread.sleep(ms);
		}
		catch(InterruptedException e) {
			throw new LException(Str.get("rt.interrupted"));
		}
	}
	/** dormir. */
	public static void dormir(LAmbiente $amb,
		long ms
	)
	throws LException {
		try {
			Thread.sleep(ms);
		}
		catch(InterruptedException e) {
			throw new LException(Str.get("rt.interrupted"));
		}
	}
	/** escribir. */
	public static void escribir(LAmbiente $amb,
		String cad
	)
	throws LException {
		$amb.obtManejadorEntradaSalida().escribir(cad);
	}
	/** escribirln. */
	public static void escribirln(LAmbiente $amb,
		String cad
	)
	throws LException {
		$amb.obtManejadorEntradaSalida().escribirln(cad);
	}
	/** leerBooleano. */
	public static boolean leerBooleano(LAmbiente $amb)
	throws LException, java.io.IOException {
		try {
			return $amb.obtManejadorEntradaSalida().leerBooleano();
		}
		catch(RuntimeException e) {
			throw new LException(e.getMessage());
		}
	}
	/** leerCadena. */
	public static String leerCadena(LAmbiente $amb)
	throws LException, java.io.IOException {
		return $amb.obtManejadorEntradaSalida().leerCadena();
	}
	
	/** leerCaracter. */
	public static char leerCaracter(LAmbiente $amb)
	throws LException, java.io.IOException {
		try {
			return $amb.obtManejadorEntradaSalida().leerCaracter();
		}
		catch(RuntimeException e) {
			throw new LException(e.getMessage());
		}
	}
	
	/** leerEntero. */
	public static int leerEntero(LAmbiente $amb)
	throws LException, java.io.IOException {
		try {
			return $amb.obtManejadorEntradaSalida().leerEntero();
		}
		catch(NumberFormatException e) {
			throw new LException(e.getMessage());
		}
	}
	/** leerReal. */
	public static double leerReal(LAmbiente $amb)
	throws LException, java.io.IOException {
		try {
			return $amb.obtManejadorEntradaSalida().leerReal();
		}
		catch(NumberFormatException e) {
			throw new LException(e.getMessage());
		}
	}
	/** obtAlgoritmo. */
	public static LAlgoritmo obtAlgoritmo(LAmbiente $amb,
		String nombre
	)
	{
		return $amb.obtAlgoritmo(nombre);
	}
	/** obtTiempoEnSegundos. */
	public static int obtTiempoEnSegundos(LAmbiente $amb) {
		return (int) (System.currentTimeMillis() / 1000);
	}
	/** terminarEjecucion. */
	public static void terminarEjecucion(LAmbiente $amb,
		int codigo
	)
	throws LException {
		$amb.terminarEjecucion(codigo);
	}
	/** terminarEjecucion. */
	public static void terminarEjecución(LAmbiente $amb,
		int codigo
	)
	throws LException {
		$amb.terminarEjecucion(codigo);
	}

	/** caracterAMinuscula. */
	public static char caracterAMinuscula(LAmbiente $amb, char c)
	throws LException, java.io.IOException {
		return Character.toLowerCase(c);
	}

	/** caracterAMayuscula. */
	public static char caracterAMayuscula(LAmbiente $amb, char c)
	throws LException, java.io.IOException {
		return Character.toUpperCase(c);
	}
}
