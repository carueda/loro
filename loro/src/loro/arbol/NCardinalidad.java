package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 *
 */
public class NCardinalidad extends NExprUn
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
	 * NCardinalidad constructor comment.
	 * @param e Nodo
	 */
	public NCardinalidad(Rango rango, NExpresion e)
	{
		super(rango, e, "#");
	}
}