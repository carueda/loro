package loro.arbol;

import loro.Rango;

/////////////////////////////////////////////////////
/**
 *
 */
public class TCadenaDoc extends Terminal
{
	String doc;
	
	////////////////////////////////////////////////////////////
	/**
	 */
	public TCadenaDoc(Rango rango, String doc)
	{
		super(rango);
		this.doc = doc;
	}
	////////////////////////////////////////////////////////////
	/**
	 * Obtiene la cadena de documentacion.
	 */
	public String obtCadena()
	{
		return doc;
	}
}