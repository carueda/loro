package loro.ijava;

/////////////////////////////////////////////////////////////
/**
 * Representa un m�todo Loro.
 */
public interface LMethod 
{
	//////////////////////////////////////////////////////////////
	/**
	 * Ejecuta este m�todo sobre el objeto dado con los argumentos dados.
	 */
	public Object invoke(LObjeto lobj, Object[] args)
	throws LException;
}
