package loro;

import loro.ISymbolTable;
import loro.arbol.IUbicable;

///////////////////////////////////////////////////////////
/**
 * Observador de seguimiento paso-a-paso.
 */
public interface IObservadorPP
{
	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando esta por ingresarse al elemento ubicable dado.
	 */
	public int enter(IUbicable u, ISymbolTable symbTab, String src);

	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando justo se finaliza la visita al elemento ubicable dado.
	 */
	public int exit(IUbicable u, ISymbolTable symbTab, String src);
	
	//////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando el seguimiento termina.
	 */
	public int end();
}
	

