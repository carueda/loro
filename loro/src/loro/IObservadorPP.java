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
	 * Llamado cuando esta por ingresarse al nodo dado.
	 */
	public int enter(INodo n, ISymbolTable symbTab, String src);

	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando justo se finaliza la visita a nodo dado.
	 */
	public int exit(INodo n, ISymbolTable symbTab, String src);
	
	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando acaba de ingresarse a un ámbito de unidad.
	 */
	public int push(IUnidad n, ISymbolTable symbTab, String src);

	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando acaba de salirse de un ámbito de unidad.
	 */
	public int pop(IUnidad n, ISymbolTable symbTab, String src);

	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando el seguimiento termina.
	 */
	public int end();
}
	

