package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

//////////////////////////////////////////////////////////////////////
/**
 *
 */
public class NLiteralEntero extends NLiteral
{
	int val;



	//////////////////////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * NLiteralEntero constructor comment.
	 */
	public NLiteralEntero(Rango rango, String imagen)
	{
		super(rango, imagen);
		try
		{
			val = Integer.parseInt(imagen);
			valor = new Integer(val);
		}
		catch ( NumberFormatException ex )
		{
			// Un int de Java no puede contener este numero:
			// No se genera un ParseException: mas bien se deja como
			// error semantico;  Vea Chequeador.visitar(NLiteralEntero):
			valor = ex;
		}
	}
}