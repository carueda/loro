package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 * Caso.
 */
public class NCaso extends NAccion
{
	/** Expresión de control para el caso. null significa
	 * que es de un si_no.
	 */
	NExpresion expr;

	/** Lista de acciones. */
	Nodo[] acciones;

	/** Se indicó fin caso? */
	boolean conFinCaso;

	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
	/**
	 */
	public Nodo[] obtAcciones()
	{
		return acciones;
	}
	/**
	 */
	public NExpresion obtExpresion()
	{
		return expr;
	}

	/**
	 */
	public boolean tieneFinCaso()
	{
		return conFinCaso;
	}

	/**
	 * Crea un NCaso.
	 */
	public NCaso(
		Rango rango,
		NExpresion e, Nodo[] a, boolean cfc
	)
	{
		super(rango);
		expr = e;
		acciones = a;
		conFinCaso = cfc;
	}
}