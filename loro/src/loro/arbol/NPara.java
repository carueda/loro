package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

//////////////////////////////////////////////////////
/**
 * NPara.
 */
public class NPara extends NIteracion
{
	TId id;
	NDeclaracion dec;
	NExpresion desde;
	boolean bajando;
	NExpresion paso;
	NExpresion hasta;



	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
	public boolean esBajando()
	{
		return bajando;
	}

	public NDeclaracion obtDeclaracion()
	{
		return dec;
	}
	public NExpresion obtExpresionDesde()
	{
		return desde;
	}
	public NExpresion obtExpresionHasta()
	{
		return hasta;
	}
	public NExpresion obtExpresionPaso()
	{
		return paso;
	}


	/**
	 * Crea un NPara.
	 */
	public NPara(
		Rango rango, 
		TId etq,
		TId i, NDeclaracion d, NExpresion ed, boolean b, NExpresion ep, NExpresion eh, Nodo[] a
	)
	{
		super(rango, etq, a);
		
		if ( (i==null) == (d==null) )
		{
			throw new RuntimeException("Only one of i and d must be non-null.");
		}
		
		id = i;
		dec = d;
		desde = ed;
		bajando = b;
		paso = ep;
		hasta = eh;
		acciones = a;
	}

	public TId obtId()
	{
		return id;
	}
}