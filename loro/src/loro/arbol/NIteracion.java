package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

////////////////////////////////////////////////////
/**
 * NIteracion. Una iteracion es una accion etiquetable
 * para fines de control del ciclo.
 */
public abstract class NIteracion extends NAccion
{
	/** La etiqueta. */
	TId etq;



	/** Las acciones de esta iteracion. */
	Nodo[] acciones;

	///////////////////////////////////////////////////
	/**
	 * Crea un nodo iteracion.
	 */
	protected NIteracion(Rango rango, TId tetq, Nodo[] a)
	{
		super(rango);
		this.etq = tetq;
		acciones = a;
	}

	/////////////////////////////////////////////////////
	/**
	 * Obtiene las acciones de esta iteracion.
	 */
	public Nodo[] obtAcciones()
	{
		return acciones;
	}

	/////////////////////////////////////////////////////
	public TId obtEtiqueta()
	{
		return etq;
	}
}