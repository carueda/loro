package loro.ejecucion;



import loro.arbol.TId;

///////////////////////////////////////////////////////
/**
 * Excepcion de control para indicar una accion "termine" 
 * o una accion "continue".
 */
class ControlInteracionException extends ControlException
{
	/** Es un "termine"? */
	boolean termine;
	
	/** La etiqueta. */
	TId etq;

	//////////////////////////////////////////////
	/**
	 * Dice si es un "termine"; en caso contrario 
	 * es un "continue".
	 */
	public boolean esTermine()
	{
		return termine;
	}


	////////////////////////////////////////////////////////////////
	/**
	 * Crea un ControlInteracionException.
	 */
	public ControlInteracionException(boolean termine, TId etq)
	{
		super();
		this.termine = termine;
		this.etq = etq;
	}

	//////////////////////////////////////////////
	/**
	 * Obtiene la etiqueta asociada.
	 */
	public TId obtEtiqueta()
	{
		return etq;
	}
}