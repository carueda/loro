package loro.tabsimb;

//////////////////////////////////////////////////////////////
/**
 * Excepcion para errores asociados al manejo de la tabla de
 * simbolos.
 *
 * @version 2002-01-08
 */
public class TSException extends Exception 
{
	//////////////////////////////////////////////////////////////
	/**
	 * Crea una excepcion de tabla de simbolos.
	 */
	public TSException(String s)
	{
		super(s);
	}
}