package loro;

import loro.ISymbolTable;

///////////////////////////////////////////////////////////
/**
 * Observador de seguimiento paso-a-paso.
 * @author Carlos Rueda
 * @version $Id$
 */
public interface IObservadorPP
{
	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando se está por ingresar al nodo dado.
	 */
	public int enter(INode n, ISymbolTable symbTab, String src)
	throws InterruptedException;

	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando justo se finaliza la visita a nodo dado.
	 * Si el nodo corresponde a una expresión, entonces el
	 * parámetro result contiene una versión imprimible del resultado
	 * de la expresión. En otro caso, result será null.
	 */
	public int exit(INode n, ISymbolTable symbTab, String src, String result)
	throws InterruptedException;
	
	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando acaba de ingresarse a un ámbito de unidad.
	 */
	public int push(IUnidad u, ISymbolTable symbTab, String src)
	throws InterruptedException;

	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando acaba de salirse de un ámbito de unidad.
	 */
	public int pop(IUnidad u, INode n, ISymbolTable symbTab, String src)
	throws InterruptedException;

	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando el seguimiento termina.
	 */
	public int end();
}
	

