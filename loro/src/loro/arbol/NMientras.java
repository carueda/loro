package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 * NMientras.
 */
public class NMientras extends NIteracion
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
	 * Crea un NMientras.
	 */
	public NMientras(Rango rango, TId tetq, NExpresion e, Nodo[] a)
	{
		super(rango, tetq, a);
		expr = e;
	}
}