package loro.java.loroI;

import loro.ijava.*;

import java.lang.reflect.Array;

/////////////////////////////////////////////////////////////////
/**
 * Para pruebas de implementacion de algoritmos Loro.
 */
public class Prueba
{
	//////////////////////////////////////////////////////////////
	/**
	 * crearPersonaDesdeJava.
	 */
	public static LObjeto crearPersonaDesdeJava(LAmbiente $amb)
	throws LException
	{
		LClase clase = $amb.obtClase("Persona");
		LObjeto p = $amb.crearInstancia(clase);
		p.ponValor("nombre", "Euclides");
		p.ponValor("edad", new Integer(2500));

		return p;
	}
	//////////////////////////////////////////////////////////////
	/**
	 * ejecutarAlgoritmoLoro.
	 */
	public static void ejecutarAlgoritmoLoro(LAmbiente $amb,
		String nombre
	)
	throws LException
	{
		LAlgoritmo alg = $amb.obtAlgoritmo(nombre);
		Object[] args = {};
		Object res = alg.ejecutar(args);
		if ( res != null )
		{
			$amb.obtManejadorEntradaSalida().
				escribirln("Res = " +res);
		}
	}
	//////////////////////////////////////////////////////////////
	/**
	 * intercjava.
	 */
	public static void intercjava(LAmbiente $amb,
		Object[] a, int i, int j
	)
	throws LException
	{
		$amb.obtManejadorEntradaSalida().escribirln("intercambiando...");
		Object temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}
	//////////////////////////////////////////////////////////////
	/**
	 * intercjava.
	 */
	public static void intercjava_(LAmbiente $amb,
		Object aa, int i, int j
	)
	throws LException
	{
		$amb.obtManejadorEntradaSalida().escribirln("intercambiando...");

		Object[] a = (Object[]) aa;
		Object temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	/*
		Object temp = Array.get(a, i);
		Array.set(a, i, Array.get(a, j));
		Array.set(a, j, temp);*/
	}

	//////////////////////////////////////////////////////////////
	/**
	 * intercjava.
	 */
	public static String replace(LAmbiente $amb,
		String cad, char viejo, char nuevo
	)
	throws LException
	{
		return cad.replace(viejo, nuevo);
	}

	//////////////////////////////////////////////////////////////
	/**
	 * intjava.
	 */
	public static void intjava(LAmbiente $amb,
		Object[] a
	)
	throws LException
	{
		$amb.obtManejadorEntradaSalida().escribirln("Por Object[]");
		for (int i = 0; i < a.length; i++)
		{
			$amb.obtManejadorEntradaSalida().escribirln("" +a[i]);
		}
	}
	//////////////////////////////////////////////////////////////
	/**
	 * intjava.
	 */
	public static void intjava(LAmbiente $amb,
		String[] a
	)
	throws LException
	{
		$amb.obtManejadorEntradaSalida().escribirln("Por String[]");
		for (int i = 0; i < a.length; i++)
		{
			$amb.obtManejadorEntradaSalida().escribirln(a[i]);
		}
	}
	//////////////////////////////////////////////////////////////
	/**
	 * llenarMatriz.
	 */
	public static void llenarMatriz(LAmbiente $amb,
		Object[] matriz
	)
	throws LException
	{
		$amb.obtManejadorEntradaSalida().escribirln("Por Object[]");
		for (int i = 0; i < matriz.length; i++)
		{
			Object[] fila = (Object[]) matriz[i];
			for (int j = 0; j < fila.length; j++)
			{
				fila[j] = new Integer(((int) (100*Math.random())));
			}
		}
	}
	//////////////////////////////////////////////////////////////
	/**
	 * objjava.
	 */
	public static LObjeto objjava(LAmbiente $amb,
		LObjeto p
	)
	throws LException
	{
		$amb.obtManejadorEntradaSalida()
			.escribirln("Nombre: " +p.obtValor("nombre")+ "\n" +
						"Edad  : " +p.obtValor("edad")
		);
		p.ponValor("nombre", "Hobbes");
		p.ponValor("edad", new Integer(9));

		return p;
	}
	//////////////////////////////////////////////////////////////
	/**
	 * obtDirectorio.
	 */
	public static String[] obtDirectorio(LAmbiente $amb,
		String nombreDir
	)
	{
		java.io.File file = new java.io.File(nombreDir);
		return file.list();
	}
	//////////////////////////////////////////////////////////////
	/**
	 * obtMatriz.
	 */
	public static int[][] obtMatriz(LAmbiente $amb,
		int m, int n
	)
	{
		int[][] matriz =  new int[m][n];

		return matriz;
	}
	//////////////////////////////////////////////////////////////
	/**
	 * registrar.
	 */
	public static void registrar(LAmbiente $amb,
		LAlgoritmo lalg, String nombre
	)
	throws LException
	{
		$amb.obtManejadorEntradaSalida().
			escribirln("lalg = " +lalg);
	}
}