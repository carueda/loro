package loro.compilacion;

import loro.arbol.*;
import loro.visitante.VisitanteException;
import loro.Rango;

//////////////////////////////////////////////////////////////
/**
 * Excepción para errores semánticos.
 */
public class ChequeadorException extends VisitanteException 
{
	/** Ubicacion de este error. */
	IUbicable u;

	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene el rango asociado a este error.
	 *
	 * @return el rango asociado a este error.
	 */
	public Rango obtRango()
	{
		return u.obtRango();
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Crea una excepción semántica.
	 *
	 * @param u Ubicación del texto implicado en el error.
	 * @param s Mensaje del error.
	 */
	public ChequeadorException(IUbicable u, String s)
	{
		super(s);
		this.u = u;
		if ( u == null )
			printStackTrace();
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene la ubicación de este error.
	 *
	 * @return la ubicación de este error.
	 */
	public IUbicable obtUbicable()
	{
		return u;
	}
}