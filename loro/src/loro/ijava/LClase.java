package loro.ijava;

//////////////////////////////////////////////////////////
/**
 * Representacion en Java de una clase en Loro.
 * Todavia en version preliminar.
 *
 * @author Carlos Rueda
 */
public interface LClase
{
	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene la lista de los nombres de los atributos.
	 */
	public String[] obtNombresAtributos()
	throws LException;
}