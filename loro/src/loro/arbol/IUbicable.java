package loro.arbol;

import loro.Rango;

//////////////////////////////////////////////////////////////////
/**
 * Un objeto que puede ubicarse en un texto.
 * Cada nodo y terminal de un arbol debe implementar esta interface.
 *
 * @author Carlos Rueda
 * @version 2002/01/08
 */
public interface IUbicable
{

	//////////////////////////////////////////////////////////////////
	/**
	 * Ubicacion de este objeto.
	 */
	public Rango obtRango();
}