package loro.arbol;

import loro.util.Util;

import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 *
 */
public class NLiteralCaracter extends NLiteral
{
	char val;

	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}

	///////////////////////////////////////////////////////////////
	/**
	 */
	public NLiteralCaracter(Rango rango, String imagen)
	{
		super(rango, imagen);
		val = Util.procesarCaracter(
				  imagen.substring(1, imagen.length() -1)
		);
		valor = new Character(val);
	}
}