package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 * NInvocacion.
 */
public class NInvocacion extends NExpresion
{
	/** La funci�n como tal. */
	NExpresion expr;

	/** Los valores con que se hace la invocaci�n. */
	NExpresion[] args;


	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
	public NExpresion[] obtArgumentos()
	{
		return args;
	}
	public NExpresion obtExpresion()
	{
		return expr;
	}

	/**
	 * NInvocacion constructor comment.
	 */
	public NInvocacion(Rango rango, NExpresion e, NExpresion[] a)
	{
		super(rango);
		expr = e;
		args = a;
	}
}