package loro.ijava;

/////////////////////////////////////////////////////////////
/**
 * Representa un método Loro.
 */
public interface LMethod 
{
	//////////////////////////////////////////////////////////////
	/**
	 * Ejecuta este método sobre el objeto dado con los argumentos dados.
	 */
	public Object invoke(LObjeto lobj, Object[] args)
	throws LException;
}
