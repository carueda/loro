package loro.ejecucion;

//////////////////////////////////////////////////////////
/**
 * Un arreglo cuya base es distinta de cero.
 *
 * @version 11/17/01
 */
final public class ArregloBaseNoCero
{
	/** La base. */
	public final int base;
	
	/** El arreglo. */
	public final Object[] array;

	//////////////////////////////////////////////////////////
	/**
	 * Crea un arreglo cuya base es distinta de cero.
	 */
	public ArregloBaseNoCero(int base, Object[] array) 
	{
		this.base = base;
		this.array = array;
	}
}