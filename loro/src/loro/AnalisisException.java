package loro;

//////////////////////////////////////////////////////////////
/**
 * La clase de base para cualquier excepci�n de an�lisis 
 * (en compilaci�n o ejecuci�n) sobre un programa en Loro.
 *
 * Una excepci�n de an�lisis tiene una descripci�n verbal
 * y la indicaci�n del rango de texto fuente comprometido.
 *
 * @author Carlos Rueda
 */
public abstract class AnalisisException extends LoroException
{
	/** El rango de texto involucrado en esta excepci�n. */
	protected Rango rango;

	///////////////////////////////////////////
	/**
	 * Crea una excepci�n de an�lisis.
	 *
	 * @param rango Rango del texto involucrado con la excepci�n.
	 * @param m Descripi�n de la excepci�n.
	 */
	protected AnalisisException(Rango rango, String m)
	{
		super(m);
		this.rango = rango;
	}
	///////////////////////////////////////////
	/**
	 * Obtiene el rango asociado a esta excepci�n.
	 *
	 * @return El rango de texto involucrado en esta excepci�n.
	 */
	public Rango obtRango()
	{
		return rango;
	}
}