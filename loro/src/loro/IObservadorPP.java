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
	public int ver(IUbicable u, ISymbolTable symbTab, String src);
}
	

