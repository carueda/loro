package loro.ejecucion;

import loro.visitante.VisitanteException;

/////////////////////////////////////////////////////////////////////
/**
 * Exception para el control interno del flujo en un programa Loro.
 *
 * Mediante este mecanismo se reconocen la instrucciones de control:
 * "termine", "continue", "retorne", "lance".
 *
 * Esta excepcion es utilizada internamente por el visitante de
 * ejecucion, es decir, es lanzada y manejada internamente.
 */
abstract class ControlException extends VisitanteException
{
	/////////////////////////////////////////////////////////////////////
	/**
	 * Crea un exception de control.
	 */
	protected ControlException(String m) {
		super(m);
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Crea un exception de control.
	 */
	protected ControlException() {
		this("ControlException");
	}
}