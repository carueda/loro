package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

////////////////////////////////////////////////
/**
 *
 */
public class NTermine extends NAccion
{
	TId etq;
	NExpresion e;


	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}

	/////////////////////////////////////////////////////
	public NExpresion obtExpresion()
	{
		return e;
	}


	/**
	 * NTermine
	 */
	public NTermine(Rango rango, TId tetq, NExpresion e)
	{
		super(rango);
		this.etq = tetq;
		this.e = e;
	}

	/////////////////////////////////////////////////////
	public TId obtEtiqueta()
	{
		return etq;
	}
}