package loro.visitante;

//////////////////////////////////////////////////
/**
 * Excepcion producida durante una visita.
 */
public abstract class VisitanteException extends Exception 
{

	//////////////////////////////////////////////////
	/**
	 * Crea una excepcion de visita con mensaje.
	 */
	public VisitanteException(String s)
	{
		super(s);
	}
}