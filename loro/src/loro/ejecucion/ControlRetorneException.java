package loro.ejecucion;

import loro.arbol.NExpresion;

//////////////////////////////////////////////////////////////////
/**
 * Exception de control para el mecanismo "retorne".
 */
class ControlRetorneException extends ControlException
{
	/** El arreglo de valores que se retorna. */
	Object res[];
	
	/** Las expresiones correspondientes. */
	NExpresion[] expresiones;

	/////////////////////////////////////////////////////
	/**
	 * Crea un ControlRetorneException.
	 *
	 * @param r Arreglo de valores de retorno.
	 */
	public ControlRetorneException(Object r[], NExpresion[] expresiones)
	{
		super();
		res = r;
		this.expresiones = expresiones;
	}

	/////////////////////////////////////////////////////
	/**
	 * Obtiene el resultado asociado.
	 */
	public Object[] obtResultado()
	{
		return res;
	}

	/////////////////////////////////////////////////////
	/**
	 * Obtiene las expresiones correspondientes al resultado.
	 */
	public NExpresion[] obtExpresiones()
	{
		return expresiones;
	}
}