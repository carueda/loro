package loro.arbol;

import loro.Rango;

/////////////////////////////////////////////////////
/**
 *
 */
public class TNombre extends Terminal
{
	TId[] nom;
	
	////////////////////////////////////////////////////////////
	/**
	 */
	public TNombre(TId[] nom)
	{
		super(new Rango(nom[0].obtRango(), nom[nom.length -1].obtRango()));
		this.nom = nom;
	}
	/////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre al estilo "p::q::r".
	 */
	public String obtCadena()
	{
		return obtCadena("::");
	}
	/////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre con el separador dado.
	 */
	private String obtCadena(String sep)
	{
		TId[] ids = obtIds();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ids.length; i++)
		{
			if ( i > 0 )
				sb.append(sep);
			sb.append(ids[i].obtId());
		}
		return sb.toString();
	}
	/////////////////////////////////////////////////////
	/**
	 * Obtiene el arreglo de nombres como cadenas.
	 */
	public String[] obtCadenas()
	{
		TId[] ids = obtIds();
		String[] cadenas = new String[ids.length];
		for (int i = 0; i < ids.length; i++)
		{
			cadenas[i] = ids[i].obtId();
		}
		return cadenas;
	}
	/////////////////////////////////////////////////////
	/**
	 *
	 */
	public TId[] obtIds()
	{
		return nom;
	}
	/////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre al estilo "p/q/r".
	 */
	public String obtRuta()
	{
		return obtCadena("/");
	}
	//////////////////////////////////////////////
	/**
	 */
	public String toString()
	{
		return obtCadena();
	}
	
}