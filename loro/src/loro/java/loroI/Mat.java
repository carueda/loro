package loro.java.loroI;

import loro.ijava.*;

/////////////////////////////////////////////////////////////////
/**
 * Implementacion de algoritmos del paquete loroI::mat.
 */
public class Mat
{
	//////////////////////////////////////////////////////////////
	/**
	 * Valor absoluto.
	 */
	public static double abs(LAmbiente $amb,
		double val
	)
	{
		return Math.abs(val);
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Coseno.
	 */
	public static double cos(LAmbiente $amb,
		double val
	)
	{
		return Math.cos(val);
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Funcion exp().
	 */
	public static double exp(LAmbiente $amb,
		double val
	)
	{
		return Math.exp(val);
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 * Logaritmo.
	 */
	public static double log(LAmbiente $amb,
		double val
	)
	{
		return Math.log(val);
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 * Maximo entre enteros.
	 */
	public static int maxEntero(LAmbiente $amb,
		int v1, int v2
	)
	{
		return Math.max(v1, v2);
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 * Maximo entre reales.
	 */
	public static double maxReal(LAmbiente $amb,
		double v1, double v2
	)
	{
		return Math.max(v1, v2);
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 * Minimo entre enteros.
	 */
	public static int minEntero(LAmbiente $amb,
		int v1, int v2
	)
	{
		return Math.min(v1, v2);
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 * Minimo entre reales.
	 */
	public static double minReal(LAmbiente $amb,
		double v1, double v2
	)
	{
		return Math.min(v1, v2);
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 * obtRealAleatorio.
	 */
	public static double obtRealAleatorio(LAmbiente $amb)
	{
		return Math.random();
	}
	//////////////////////////////////////////////////////////////
	/**
	 * Piso.
	 */
	public static double piso(LAmbiente $amb,
		double val
	)
	{
		return Math.floor(val);
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 * Elevacion a potencia entera.
	 */
	public static double pot(LAmbiente $amb,
		double base, int n
	)
	{
		return Math.pow(base, n);
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Elevacion a potencia real.
	 */
	public static double potr(LAmbiente $amb,
		double base, double p
	)
	{
		return Math.pow(base, p);
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Raiz cuadrada.
	 */
	public static double raiz2(LAmbiente $amb,
		double val
	)
	{
		return Math.sqrt(val);
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 * Redondeo.
	 */
	public static double redondear(LAmbiente $amb,
		double val
	)
	{
		return Math.round(val);
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 * Seno.
	 */
	public static double sen(LAmbiente $amb,
		double val
	)
	{
		return Math.sin(val);
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 * Tangente.
	 */
	public static double tan(LAmbiente $amb,
		double val
	)
	{
		return Math.tan(val);
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 * Techo.
	 */
	public static double techo(LAmbiente $amb,
		double val
	)
	{
		return Math.ceil(val);
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 * PI.
	 */
	public static double PI(LAmbiente $amb)
	{
		return Math.PI;
	}
	
	//////////////////////////////////////////////////////////////
	/**
	 * E.
	 */
	public static double E(LAmbiente $amb)
	{
		return Math.E;
	}
}