package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 *
 */
public class NACadena extends NExprUn
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
	 * NACadena constructor comment.
	 * @param e Nodo
	 */
	public NACadena(Rango rango, NExpresion e)
	{
		super(rango, e, "@");
	}
}