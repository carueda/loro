package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 *
 */
public class NNeg extends NExprUn
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
	 * NNeg constructor comment.
	 * @param e Nodo
	 */
	public NNeg(Rango rango, NExpresion e)
	{
		super(rango, e, "-");
	}
}