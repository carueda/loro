package loro.arbol;

import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.Rango;

///////////////////////////////////////////////////////////////////
/**
 * Un fragmento "si_no_si".
 */
public class NDecisionSiNoSi extends NAccion
{
	NExpresion expr;
	Nodo[] as;

	//////////////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
	
	//////////////////////////////////////////////////////////////
	public Nodo[] obtAcciones()
	{
		return as;
	}
	
	//////////////////////////////////////////////////////////////
	public NExpresion obtCondicion()
	{
		return expr;
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo NDecisionSiNoSi.
	 */
	public NDecisionSiNoSi(
		Rango rango,
		NExpresion e, Nodo[] as
	)
	{
		super(rango);
		expr = e;
		this.as = as;
	}
}