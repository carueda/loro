package loro;

import loro.ISymbolTable;
import loro.arbol.INodo;

///////////////////////////////////////////////////////////
/**
 * Observador de seguimiento paso-a-paso.
 */
public interface IObservadorPP
{
	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando se está por ingresar al nodo dado.
	 */
	public int enter(INodo n, ISymbolTable symbTab, String src)
	throws InterruptedException;

	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando justo se finaliza la visita a nodo dado.
	 * Si el nodo corresponde a una expresión, entonces el
	 * parámetro result contiene una versión imprimible del resultado
	 * de la expresión. En otro caso, result será null.
	 */
	public int exit(INodo n, ISymbolTable symbTab, String src, String result)
	throws InterruptedException;
	
	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando acaba de ingresarse a un ámbito de unidad.
	 */
	public int push(IUnidad n, ISymbolTable symbTab, String src)
	throws InterruptedException;

	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando acaba de salirse de un ámbito de unidad.
	 */
	public int pop(IUnidad n, ISymbolTable symbTab, String src)
	throws InterruptedException;

	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando el seguimiento termina.
	 */
	public int end();
}
	

