package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 *
 */
public class NLance extends NAccion
{
	NExpresion e;

	////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo para acción lance.
	 */
	public NLance(Rango rango, NExpresion e)
	{
		super(rango);
		this.e = e;
	}

	////////////////////////////////////////////////////////////
	public NExpresion obtExpresion()
	{
		return e;
	}

	////////////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}

}
