package loro.compilacion;

///////////////////////////////////////////////////////////////
/**
 * Exception para cuando no se encuentra una clase Loro.
 *
 * @author Carlos Rueda
 */
public class ClaseNoEncontradaException extends Exception
{
	///////////////////////////////////////////////////////////////
	/**
	 * Crea una exception de clase no encontrada.
	 *
	 * @param nombre Nombre de la clase que no pudo encontrarse.
	 *               obtNombre() retorna este nombre.
	 */
	public ClaseNoEncontradaException(String nombre)
	{
		super(nombre);
	}

	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre de la clase que no pudo encontrarse.
	 *
	 * @return el nombre de la clase que no pudo encontrarse.
	 */
	public String obtNombre()
	{
		return getMessage();
	}
}