package loro.arbol;

import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.Rango;

//////////////////////////////////////////////////////////////////////
/**
 * Expresi�n "�ste".
 */
public class NEste extends NExpresion
{
	//////////////////////////////////////////////////////////////////////
	/**
	 */
	public NEste(Rango rango)
	{
		super(rango);
	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
}
