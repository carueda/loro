package loro.ejecucion;

import loro.arbol.NExpresion;

//////////////////////////////////////////////////////////////////
/**
 * Exception de control para el mecanismo "lance".
 */
public class ControlLanceException extends ControlException
{
	/** El valor que se retorna. */
	Object res;
	
	/** La expresion correspondiente. */
	NExpresion expresion;
	
	/** Una instant�nea del estado de la pula de ejecuci�n. */
	String stackTrace;

	/////////////////////////////////////////////////////
	/**
	 * Crea un ControlLanceException.
	 *
	 * @param r El valor
	 * @param expresion la expresion
	 * @param pilaEjec La pila de ejecuci�n
	 */
	public ControlLanceException(Object r, NExpresion expresion, PilaEjecucion pilaEjec)
	{
		super("Se gener� excepci�n: " +r);
		res = r;
		this.expresion = expresion;
		java.io.StringWriter sw = new java.io.StringWriter();
		pilaEjec.mostrar(new java.io.PrintWriter(sw));
		stackTrace = sw.toString();
	}
	
	
	
	/////////////////////////////////////////////////////
	/**
	 * Obtiene el estado de la pila en el momento del lanzamiento.
	 * Version preliminar como cadena.
	 */
	public String obtEstadoPila()
	{
		return stackTrace;
	}

	/////////////////////////////////////////////////////
	/**
	 * Obtiene el resultado asociado.
	 */
	public Object obtResultado()
	{
		return res;
	}

	/////////////////////////////////////////////////////
	/**
	 * Obtiene la expresion correspondiente al resultado.
	 */
	public NExpresion obtExpresion()
	{
		return expresion;
	}
}
