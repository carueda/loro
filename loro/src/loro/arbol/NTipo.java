package loro.arbol;

import loro.tipo.Tipo;
import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.Rango;

///////////////////////////////////////////////////////////
/**
 * Nodo para tipo.
 */
public abstract class NTipo extends Nodo
{
	/** El tipo real de este nodo-tipo. */
	Tipo tipo;

	///////////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo para un tipo.
	 */
	protected NTipo(Rango rango)
	{
		super(rango);
	}
	
	////////////////////////////////////////////
	/**
	 * Obtiene el tipo real de este ntipo.
	 * Este es el mismo puesto con ponTipo() o
	 * null si ponTipo() no ha sido llamado.
	 */
	public Tipo obtTipo()
	{
		return tipo;
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Pone el tipo real para este ntipo.
	 */
	public void ponTipo(Tipo tipo)
	{
		this.tipo = tipo;
	}
}