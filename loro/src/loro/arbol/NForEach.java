package loro.arbol;

import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.Rango;

//////////////////////////////////////////////////////
/**
 * NForEach.
 */
public class NForEach extends NIteracion
{
	TId id;
	NDeclaracion dec;
	NExpresion en;

	/////////////////////////////////////////////////////////////
	/**
	 * Crea un NForEach.
	 */
	public NForEach(
		Rango rango, 
		TId etq,
		TId i, NDeclaracion d, NExpresion en, Nodo[] a
	)
	{
		super(rango, etq, a);
		
		if ( (i==null) == (d==null) )
		{
			throw new RuntimeException("Only one of i and d must be non-null.");
		}
		
		this.id = i;
		this.dec = d;
		this.en = en;
		this.acciones = a;
	}

	/////////////////////////////////////////////////////////////	
	public NDeclaracion obtDeclaracion()
	{
		return dec;
	}
	
	/////////////////////////////////////////////////////////////	
	public NExpresion obtExpresionEn()
	{
		return en;
	}

	/////////////////////////////////////////////////////////////	
	public TId obtId()
	{
		return id;
	}

	/////////////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
}
