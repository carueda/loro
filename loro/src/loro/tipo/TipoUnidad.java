package loro.tipo;

import loro.util.Util;
import loro.arbol.NUnidad;

///////////////////////////////////////////////////////////
/**
 * Tipo para una unidad.
 */
public abstract class TipoUnidad extends Tipo
{
	/**
	 * Nombre completo de la unidad.
	 */
	String[] nombre;




	//////////////////////////////////////////////////////////////////////
	/**
	 * Retorna null, el valor por defecto para toda unidad.
	 */
	public Object obtValorDefecto()
	{
		return null;
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre completo de la unidad correspondiente.
	 */
	public String[] obtNombreConPaquete()
	{
		return nombre;
	}





	//////////////////////////////////////////////////////////////////////
	/**
	 * Crea un tipo unidad asociado al nombre completo correspondiente.
	 */
	TipoUnidad(String[] nombre)
	{
		this.nombre = nombre;
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre simple dado a esta clase.
	 * Si este tipo es generico, se retorna null.
	 */
	public String obtNombreSimple()
	{
		if ( nombre != null )
		{
			return nombre[nombre.length -1];
		}

		return null;
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Retorna el nombre completo como String de este tipo.
	 */
	public String obtNombreCompletoString()
	{
		return Util.obtStringRuta(nombre);
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Retorna lo mismo que obtNombreCompletoString().
	 */
	public String toString()
	{
		return obtNombreCompletoString();
	}
}