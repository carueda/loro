package loro.compilacion;

import loro.IUbicable;

/////////////////////////////////////////////////////////////
/**
 * Representa particularmente un error de identificador indefinido.
 * Permite el manejo transitorio de este tipo de error durante
 * una pasada de compilación que pudiera corregirse en una
 * subsiguiente pasada.
 */
class IdIndefinidoException extends ChequeadorException 
{

	//////////////////////////////////////////////////////////////
	/**
	 * Crea una IdIndefinidoException.
	 *
	 * @param u Ubicación del texto implicado en el error.
	 * @param s Mensaje del error.
	 */
	public IdIndefinidoException(IUbicable u, String s)
	{
		super(u, s);
	}
}