package loro.ejecucion;

import loro.arbol.NExpresion;
import loro.util.UtilValor;

import java.io.*;

//////////////////////////////////////////////////////////////////
/**
 * Exception de control para el mecanismo "lance".
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class ControlLanceException extends ControlException
{
	/** El valor que se retorna. */
	Object res;
	
	/** La expresion correspondiente. */
	NExpresion expresion;
	
	/** Una instantánea del estado de la pula de ejecución. */
	String stackTrace;

	/////////////////////////////////////////////////////
	/**
	 * Crea un ControlLanceException.
	 *
	 * @param r El valor
	 * @param expresion la expresion
	 * @param pilaEjec La pila de ejecución
	 */
	public ControlLanceException(Object r, NExpresion expresion, PilaEjecucion pilaEjec)
	{
		super("Se generó excepción: " +
			UtilValor.valorComillasDeExpresion(expresion.obtTipo(), r)
		);
		res = r;
		this.expresion = expresion;
		StringWriter sw = new StringWriter();
		pilaEjec.mostrar(new PrintWriter(sw));
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
