package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 *
 */
public class NNoBit extends NExprUn
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
	 * NNoBit constructor comment.
	 * @param e Nodo
	 */
	public NNoBit(Rango rango, NExpresion e)
	{
		super(rango, e, "~");
	}
}