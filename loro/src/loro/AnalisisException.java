package loro;

//////////////////////////////////////////////////////////////
/**
 * La clase de base para cualquier excepción de análisis 
 * (en compilación o ejecución) sobre un programa en Loro.
 *
 * Una excepción de análisis tiene una descripción verbal
 * y la indicación del rango de texto fuente comprometido.
 *
 * @author Carlos Rueda
 */
public abstract class AnalisisException extends LoroException
{
	/** El rango de texto involucrado en esta excepción. */
	protected Rango rango;

	///////////////////////////////////////////
	/**
	 * Crea una excepción de análisis.
	 *
	 * @param rango Rango del texto involucrado con la excepción.
	 * @param m Descripión de la excepción.
	 */
	protected AnalisisException(Rango rango, String m)
	{
		super(m);
		this.rango = rango;
	}
	///////////////////////////////////////////
	/**
	 * Obtiene el rango asociado a esta excepción.
	 *
	 * @return El rango de texto involucrado en esta excepción.
	 */
	public Rango obtRango()
	{
		return rango;
	}
}