package loro.arbol;

import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.Rango;

//////////////////////////////////////////////////////////////////////
/**
 * Nodo de creacion de arreglo.
 */
public class NCrearArreglo extends NExprBin
{
	/**
	 * Limite superior rango de indexacion.
	 * Puede ser null.
	*/
	NExpresion g;

	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la expresion para el limite superior.
	 * null si no fue establecido explicitamente.
	 */
	public NExpresion obtExpresionLimiteSuperior()
	{
		return g;
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

	//////////////////////////////////////////////////////////////////////
	public NCrearArreglo(
		Rango rango,
		NExpresion e,
		NExpresion g,
		NExpresion f
	)
	{
		super(rango, e, f, "crear");
		this.g = g;
	}
}