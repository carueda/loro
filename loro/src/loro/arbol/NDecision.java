package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

//////////////////////////////////////////////////////////////////////
/**
 * Un nodo de decision.
 */
public class NDecision extends NAccion
{
	NExpresion expr;
	Nodo[] as;
	NDecisionSiNoSi[] sinosis;
	Nodo[] an;


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
	public Nodo[] obtAccionesCierto()
	{
		return as;
	}
	
	//////////////////////////////////////////////////////////////////////
	public NDecisionSiNoSi[] obtSiNoSis()
	{
		return sinosis;
	}
	
	//////////////////////////////////////////////////////////////////////
	public Nodo[] obtAccionesFalso()
	{
		return an;
	}
	
	//////////////////////////////////////////////////////////////////////
	public NExpresion obtCondicion()
	{
		return expr;
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo NDecision.
	 */
	public NDecision(
		Rango rango,
		NExpresion e, Nodo[] as,
		NDecisionSiNoSi[] sinosis,
		Nodo[] an
	)
	{
		super(rango);
		expr = e;
		this.as = as;
		this.sinosis = sinosis;
		this.an = an;
	}
}