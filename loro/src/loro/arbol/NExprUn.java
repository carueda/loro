package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 * Raiz de todas las expresiones unarias.
 */
public abstract class NExprUn extends NExpresion
{
	/** La expresión operada.*/
	NExpresion e;

	/** El operador. */
	String op;

	public NExpresion obtExpresion()
	{
		return e;
	}
	public String obtOperador()
	{
		return op;
	}

	/**
	 * NExprUn constructor comment.
	 */
	public NExprUn(Rango rango, NExpresion e, String op)
	{
		super(rango);
		this.e = e;

		this.op = op;
	}
}