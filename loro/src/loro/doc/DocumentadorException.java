package loro.doc;

import loro.visitante.VisitanteException;

////////////////////////////////////////////////////////////
/**
 * Excepcion de visita durante generacion de documentacion.
 *
 * @author Carlos Rueda
 */
public class DocumentadorException extends VisitanteException 
{
	///////////////////////////////////////////////////
	/**
	 * Crea una DocumentadorException.
	 *
	 * @param s Mensaje de error.
	 */
	public DocumentadorException(String s) 
	{
		super(s);
	}
}