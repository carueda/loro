package loro;

//////////////////////////////////////////////////////////////////
/**
 * El tipo de exception que puede darse durante la construccion del
 * arbol sintactico.
 *
 * @author Carlos Rueda
 */
public class DerivacionException extends CompilacionException
{
	///////////////////////////////////////////////////////////////////
	/**
	 * Crea un error de derivación.
	 *
	 * @param rango - Rango comprometido.
	 * @param m Mensaje de error
	 */
	public DerivacionException(Rango rango, String m)
	{
		super(rango, m);
/*
System.out.println(
	rango.obtPosIni() + "\n"+
	rango.obtPosFin() + "\n"+
	rango.obtIniLin() + "\n"+
	rango.obtIniCol() + "\n"+
	rango.obtFinLin() + "\n"+
	rango.obtFinCol() + "\n"
);
*/
	}
}