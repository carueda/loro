package loro.ijava;

import loro.LoroException;

/////////////////////////////////////////////////////////////////
/**
 * Excepcion basica para interface Loro-Java
 *
 * @author Carlos Rueda
 */
public class LException extends LoroException
{
	////////////////////////////////////////////////
	/**
	 * Excepcion para api nativa.
	 */
	public LException(String m) 
	{
		super(m);
	}
}