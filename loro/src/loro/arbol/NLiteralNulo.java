package loro.arbol;

import loro.Loro.Str;
import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.Rango;

/**
 *
 */
public class NLiteralNulo extends NLiteral
{
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}

	public String toString()
	{
		return Str.get("null");
	}

	public NLiteralNulo(Rango rango, String imagen)
	{
		super(rango, imagen);
		valor = null;
	}
}