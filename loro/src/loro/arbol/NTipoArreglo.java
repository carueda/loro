package loro.arbol;

import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

//////////////////////////////////////////////////////////////////////
/**
 * Nodo para un tipo arreglo.
 */
public class NTipoArreglo extends NTipo
{
	/*** Nodo que representa el tipo de los elementos. */
	NTipo elem;

	////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo para un tipo arreglo.
	 */
	public NTipoArreglo(Rango rango, NTipo elem)
	{
		super(rango);
		this.elem = elem;
	}

	////////////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}

	//////////////////////////////////////////////
	/**
	 * Obtiene el nodo del tipo de los elementos.
	 */
	public NTipo obtNTipoElemento()
	{
		return elem;
	}
}