package loro.arbol;

import loro.util.Util;

import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

////////////////////////////////////////////////////////////
/**
 *
 */
public class NLiteralCadena extends NLiteral
{
	String val;



	////////////////////////////////////////////////////////////
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
	public NLiteralCadena(Rango rango, String imagen)
	{
		super(rango, imagen);
		valor = val = Util.procesarCadena(
			  imagen.substring(1, imagen.length() -1)
		);
	}
}