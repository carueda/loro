package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 *
 */
public class NPlus extends NExprUn
{

	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}

	/**
	 * NPlus constructor comment.
	 * @param e Nodo
	 */
	public NPlus(Rango rango, NExpresion e)
	{
		super(rango, e, "+");
	}
}