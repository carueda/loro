package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 * NSubindexacion.
 */
public class NSubindexacion extends NExprBin
{


	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
	/**
	 * Dice si esta expresión es constante.
	 */
	public boolean esConstante()
	{
		return e.esConstante();
	}

	/**
	 * NSubindexacion.
	 *
	 * @pararm rango
	 * @param e
	 * @param f
	 */
	public NSubindexacion(Rango rango, NExpresion e, NExpresion f)
	{
		super(rango, e, f, "[]");
	}
}