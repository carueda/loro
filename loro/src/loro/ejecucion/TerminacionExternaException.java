package loro.ejecucion;

import loro.arbol.*;

///////////////////////////////////////////////////////////
/**
 * Se�ala una detenci�n "externa" del hilo que est� ejecutando
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