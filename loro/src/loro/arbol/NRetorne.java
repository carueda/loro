package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 *
 */
public class NRetorne extends NAccion
{
	NExpresion[] expresiones;


	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
	public NExpresion[] obtExpresiones()
	{
		return expresiones;
	}


	/**
	 * NRetorne constructor comment.
	 */
	public NRetorne(Rango rango, NExpresion[] es)
	{
		super(rango);
		expresiones = es;
	}
}