package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 * NCuantificado.
 */
public class NCuantificado extends NExpresion
{
	/** Es un para_todo? */
	boolean paraTodo;

	/** Lista de declaraciones. */
	NDeclaracion[] d;

	/** Expresión "con". */
	NExpresion con;

	/** Expresión propia. */
	NExpresion e;


	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
	/** Es un para_todo? */
	public boolean esParaTodo()
	{
		return paraTodo;
	}
	/** Lista de declaraciones. */
	public NDeclaracion[] obtDeclaraciones()
	{
		return d;
	}
	/** La Expresion principal. */
	public NExpresion obtExpresion()
	{
		return e;
	}
	/** Expresión "con". */
	public NExpresion obtExpresionCon()
	{
		return con;
	}

	/**
	 * NCuantificado.
	 */
	public NCuantificado(
		Rango rango,
		boolean para_todo, NDeclaracion[] d, NExpresion con, NExpresion e
	)
	{
		super(rango);
		paraTodo = para_todo;
		this.d = d;
		this.con = con;
		this.e = e;
	}
}