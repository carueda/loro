package loro;

///////////////////////////////////////////////////
/**
 * La clase de base para cualquier excepci�n que se
 * presente al interactuar con el sistema Loro.
 *
 * @author Carlos Rueda
 */
public class LoroException extends Exception
{
	///////////////////////////////////////////
	/**
	 * Crea una instancia de esta excepci�n.
	 */
	public LoroException(String m)
	{
		super(m);
	}
}