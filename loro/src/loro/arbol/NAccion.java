package loro.arbol;



import loro.Rango;

/**
 * Toda accion de Loro debe ser subclase de este nodo.
 */
public abstract class NAccion extends Nodo
{

	///////////////////////////////////////////////////////////////////
	/**
	 * Crea lo basico de un nodo.
	 */
	public NAccion(Rango rango)
	{
		super(rango);
	}
}