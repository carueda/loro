package loro.arbol;

import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.Rango;

////////////////////////////////////////////////////////////////
/**
 * Declaracion de uno o varios identificadores.
 */
public class NDeclaracion extends NDeclaracionBase
{
	TId id;

	/** En caso se trate de varios ids para el tipo. */
	TId[] ids;

	/////////////////////////////////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}

	/////////////////////////////////////////////////////////////////////////////////
	public String toString()
	{
		return id+ ":" +obtTipo() +(esConstante() ? " constante" : "");
	}

	/////////////////////////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo declaración para varios ids.
	 */
	public NDeclaracion(Rango rango, TId[] ids, NTipo ntipo, boolean cte, NExpresion e)
	{
		super(rango, ntipo, cte, e);
		this.ids = ids;
	}

	/////////////////////////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo declaración simple.
	 */
	public NDeclaracion(Rango rango, TId tid, NTipo ntipo)
	{
		super(rango, ntipo, false, null);
		this.id = tid;
	}

	/////////////////////////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo declaración para un id.
	 */
	public NDeclaracion(Rango rango, TId id, NTipo ntipo, boolean cte, NExpresion e)
	{
		super(rango, ntipo, cte, e);
		this.id = id;
	}

	/////////////////////////////////////////////////////////////////////////////////
	public TId obtId()
	{
		return id;
	}

	/////////////////////////////////////////////////////////////////////////////////
	public TId[] obtIds()
	{
		return ids;
	}
}