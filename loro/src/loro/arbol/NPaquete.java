package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 */
public class NPaquete extends Nodo
{
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}


	/** Nombre del pquete. */
	TNombre nombre;

	/////////////////////////////////////////////////
	/**
	 * Crea un NPaquete.
	 */
	public NPaquete(Rango rango, TNombre nom)
	{
		super(rango);
		this.nombre = nom;
	}

	//////////////////////////////////////////////////////
	/**
	 */
	public TNombre obtNPaquete()
	{
		return nombre;
	}
}