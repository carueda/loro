package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 * .
 */
public class NRepita extends NIteracion
{

	NExpresion expr;


	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}

	public NExpresion obtCondicion()
	{
		return expr;
	}

	/**
	 * NRepita constructor comment.
	 */
	public NRepita(Rango rango, TId tetq, Nodo[] a, NExpresion e)
	{
		super(rango, tetq, a);
		expr = e;
	}	
}