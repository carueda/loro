package loro.ejecucion;

import loro.arbol.*;

///////////////////////////////////////////////////////////
/**
 * Señala una detención "externa" del hilo que está ejecutando
 * un programa en Loro.
 */
class TerminacionExternaException extends TerminacionException
{


	///////////////////////////////////////////////////////////
	/**
	 * Crea una TerminacionExternaException.
	 */
	public TerminacionExternaException(IUbicable u, PilaEjecucion pilaEjec)
	{
		super(u, pilaEjec);
	}
}