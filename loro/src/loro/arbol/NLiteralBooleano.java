package loro.arbol;

import loro.Loro.Str;
import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.Rango;

/**
 *
 */
public class NLiteralBooleano extends NLiteral
{
	boolean val;


	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
	public boolean obtValorBooleano()
	{
		return val;
	}

	/**
	 * Crea un NLiteralBooleano.
	 */
	public NLiteralBooleano(Rango rango, String imagen)
	{
		super(rango, imagen);
		val = imagen.equals(Str.get("true"));
		valor = new Boolean(val);
	}
}