package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 *
 */
public class NLiteralReal extends NLiteral
{
	double val;

	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}

	/**
	 * NLiteralReal constructor comment.
	 */
	public NLiteralReal(Rango rango, String imagen)
	{
		super(rango, imagen);
		valor = new Double(imagen);
		val = ((Double)valor).doubleValue();
	}
}