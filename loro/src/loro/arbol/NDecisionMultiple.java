package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 * NDecisionMultiple.
 */
public class NDecisionMultiple extends NAccion
{
	/** Expresión de control del según. */
	NExpresion expr;

	/** Lista de casos. */
	NCaso[] casos;

	/** Caso para el si_no. */
	NCaso si_no;


	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
	/** Obtiene la lista de casos. */
	public NCaso[] obtCasos()
	{
		return casos;
	}
	/** Obtien el caso para el si_no. */
	public NCaso obtCasoSiNo()
	{
		return si_no;
	}
	/** Obtiene la expresión de control del según. */
	public NExpresion obtExpresion()
	{
		return expr;
	}

	/**
	 * Crea una NDecisionMultiple.
	 */
	public NDecisionMultiple(
		Rango rango,
		NExpresion e, NCaso[] c, NCaso sn
	)
	{
		super(rango);
		expr = e;
		casos = c;
		si_no = sn;
	}
}