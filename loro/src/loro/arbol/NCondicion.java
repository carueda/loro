package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 * La expresi�n condicional ?:
 */
public class NCondicion extends NExpresion
{
	/** La condici�n. */
	NExpresion e;

	/** La primera alternativa. */
	NExpresion f;

	/** La segunda alternativa. */
	NExpresion g;


	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
	/** La condici�n. */
	public NExpresion obtCondicion()
	{
		return e;
	}
	/** La primera alternativa. */
	public NExpresion obtPrimeraAlternativa()
	{
		return f;
	}
	/** La segunda alternativa. */
	public NExpresion obtSegundaAlternativa()
	{
		return g;
	}

	////////////////////////////////////////////////////////////////////////
	/**
	 * NCondicion.
	 */
	public NCondicion(Rango rango, NExpresion e, NExpresion f, NExpresion g)
	{
		super(rango);
		this.e = e;
		this.f = f;
		this.g = g;
	}
}