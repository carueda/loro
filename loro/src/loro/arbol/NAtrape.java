package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

//////////////////////////////////////////////////////
/**
 * NAtrape
 */
public class NAtrape extends NAccion
{
	/**
	 * Declaracion en donde se atrapa el objeto lanzado. 
	 * Es null cuando se trata de un "siempre".
	 */
	NDeclaracion dec;

	/** Las acciones de este atrape. */
	Nodo[] acciones;

	
	/////////////////////////////////////////////////////
	/**
	 * Crea un NAtrape.
	 */
	public NAtrape(Rango rango, NDeclaracion d, Nodo[] a)
	{
		super(rango);
		
		dec = d;
		acciones = a;
	}

	/////////////////////////////////////////////////////
	/**
	 * Obtiene la declaracion en donde se atrapa el objeto lanzado. 
	 * Es null cuando se trata de un "siempre"
	 */
	public NDeclaracion obtDeclaracion()
	{
		return dec;
	}

	/////////////////////////////////////////////////////
	/**
	 * Obtiene las acciones de este atrape.
	 */
	public Nodo[] obtAcciones()
	{
		return acciones;
	}

	/////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
}
