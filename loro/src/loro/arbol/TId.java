package loro.arbol;

import loro.Rango;

/////////////////////////////////////////////////
/**
 * Terminal Id.
 */
public class TId extends Terminal
{
	/** Identificador. */
	String id;
	

	////////////////////////////////////////////////////////////
	/**
	 * Crea un terminal Id.
	 */
	public TId(Rango rango, String id)
	{
		super(rango);
		this.id = id;
	}
	////////////////////////////////////////////////////////////
	/**
	 * Obtiene el identificador.
	 */
	public String obtId()
	{
		return id;
	}
	//////////////////////////////////////////////
	/**
	 */
	public String toString()
	{
		return id;
	}
}