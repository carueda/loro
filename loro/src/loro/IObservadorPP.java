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
	 * Llamado cuando se est� por ingresar al nodo dado.
	 */
	public int enter(INodo n, ISymbolTable symbTab, String src)
	throws InterruptedException;

	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando justo se finaliza la visita a nodo dado.
	 * Si el nodo corresponde a una expresi�n, entonces el
	 * par�metro result contiene una versi�n imprimible del resultado
	 * de la expresi�n. En otro caso, result ser� null.
	 */
	public int exit(INodo n, ISymbolTable symbTab, String src, String result)
	throws InterruptedException;
	
	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando acaba de ingresarse a un �mbito de unidad.
	 */
	public int push(IUnidad n, ISymbolTable symbTab, String src)
	throws InterruptedException;

	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando acaba de salirse de un �mbito de unidad.
	 */
	public int pop(IUnidad n, ISymbolTable symbTab, String src)
	throws InterruptedException;

	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando el seguimiento termina.
	 */
	public int end();
}
	

