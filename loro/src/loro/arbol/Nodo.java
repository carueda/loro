package loro.arbol;



import loro.Rango;

////////////////////////////////////////////////////////////////
/**
 * Todo nodo del arbol de derivacion es subclase de esta clase.
 */
public abstract class Nodo implements INodo
{
	/** Rango de este nodo. */
	protected Rango rango;

	///////////////////////////////////////////////////////////////////
	/**
	 * Crea lo basico de un nodo.
	 */
	public Nodo(Rango rango)
	{
		this.rango = rango;
	}

	/////////////////////////////////////////////////////////
	/**
	 * Obtiene el rango asociado a este nodo.
	 *
	 * @return El rango asociado a este nodo.
	 */
	public Rango obtRango()
	{
		return rango;
	}
}