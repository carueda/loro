package loro.java;

import loro.ijava.*;
import loro.arbol.NClase;

///////////////////////////////////////////////////////////
/**
 * Implementacion de una l-clase.
 */
public class LClaseImp implements LClase
{
	/** La clase verdadera asociada. */
	NClase clase;
	////////////////////////////////////////////////////////
	/**
	 * Crea una l-clase, o sea, una clase de interfaz con
	 * java asociada a la clase Loro dada.
	 */
	public LClaseImp(NClase clase) 
	{
		this.clase = clase;
	}
	////////////////////////////////////////////////////////
	/**
	 *  Obtiene la clase verdadera correspondiente.
	 */
	public NClase obtClase()
	{
		return clase;
	}
	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene la lista de los nombres de los atributos.
	 */
	public String[] obtNombresAtributos()
	throws LException
	{
		return clase.obtAtributos();
	}
}