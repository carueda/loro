package loro.arbol;

import loro.Rango;

/////////////////////////////////////////////////////
/**
 *
 */
public class TCadena extends Terminal
{
	String cadena;
	
	////////////////////////////////////////////////////////////
	/**
	 */
	public TCadena(Rango rango, String cadena)
	{
		super(rango);
		this.cadena = cadena;
	}
	////////////////////////////////////////////////////////////
	/**
	 * Obtiene la cadena.
	 */
	public String obtCadena()
	{
		return cadena;
	}
}