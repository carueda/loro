package loro.ejecucion;

import loro.visitante.VisitanteException;

/////////////////////////////////////////////////////////////////////
/**
 * Exception para el control interno del flujo en un programa Loro.
 *
 * Mediante este mecanismo se reconocen la instrucciones de control:
 * "termine", "continue", "retorne".
 *
 * Esta excepcion es utilizada internamente por el visitante de
 * ejecucion, es decir, es lanzada y manejada internamente.
 *
 * @version 2002-02-20
 */
abstract class ControlException extends VisitanteException
{
	/////////////////////////////////////////////////////////////////////
	/**
	 * Crea un exception de control.
	 */
	protected ControlException()
	{
		super("Excepcion de control");
	}
}