package loro;

//////////////////////////////////////////////////////////////
/**
 * Clase para las excepciones generadas durante la compilaci�n
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
	 * @param rango Rango del texto involucrado con la excepci�n.
	 * @param m Descripi�n de la excepci�n.
	 */
	public CompilacionException(Rango rango, String m)
	{
		super(rango, m);
	}
}