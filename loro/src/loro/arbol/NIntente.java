package loro.arbol;

import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

//////////////////////////////////////////////////////
/**
 * NIntente.
 */
public class NIntente extends NAccion
{
	/** Las acciones . */
	Nodo[] acciones;
	
	/** los atrapes. */
	NAtrape[] cc;
	
	/** Las acciones para el siempre. */
	NAtrape f;
	

	/////////////////////////////////////////////////////
	/**
	 * Crea un NIntente.
	 */
	public NIntente(Rango rango, Nodo[] a, NAtrape[] cc, NAtrape f)
	{
		super(rango);
		acciones = a;
		this.cc = cc;
		this.f = f;
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
	 * Obtiene los atrapes excluido el de "siempre".
	 */
	public NAtrape[] obtAtrapes()
	{
		return cc;
	}

	/////////////////////////////////////////////////////
	/**
	 * Obtiene el siempre.
	 */
	public NAtrape obtSiempre()
	{
		return f;
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
