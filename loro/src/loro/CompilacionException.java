package loro;

//////////////////////////////////////////////////////////////
/**
 * Clase para las excepciones generadas durante la compilación
 * de un programa en Loro. 
 *
 * @author Carlos Rueda
 */
public class CompilacionException extends AnalisisException
{
	///////////////////////////////////////////
	/**
	 * Crea una excepcion de analisis.
	 *
	 * @param rango Rango del texto involucrado con la excepción.
	 * @param m Descripión de la excepción.
	 */
	public CompilacionException(Rango rango, String m)
	{
		super(rango, m);
	}
}