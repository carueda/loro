package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.tipo.TipoClase;

import loro.Rango;

////////////////////////////////////////////////////////
/**
 * SubId: referencia a un atributo en un objeto.
 */
public class NSubId extends NExpresion
{
	NExpresion e;
	TId id;


	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}

	public NExpresion obtExpresion()
	{
		return e;
	}


	/**
	 * Crea una expresión de acceso a atributo.
	 */
	public NSubId(NExpresion exp, TId id)
	{
		super(new Rango(exp.obtRango(), id.obtRango()));
		e = exp;
		this.id = id;
	}

	public TId obtId()
	{
		return id;
	}
}