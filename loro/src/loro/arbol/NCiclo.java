package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 * .
 */
public class NCiclo extends NIteracion
{




	//////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}


	/**
	 * NCiclo.
	 */
	public NCiclo(Rango rango, TId tetq, Nodo[] a)
	{
		super(rango, tetq, a);
	}
}