package loro.arbol;

import loro.Rango;

////////////////////////////////////////////////////////////////////////
/**
 * Implementacion basica de ITerminal.
 *
 * @author Carlos Rueda
 * @version 2002-01-08
 */
public abstract class Terminal implements ITerminal
{
	/** Rango de este terminal. */
	Rango rango;
	
	////////////////////////////////////////////////////////////
	/**
	 * Crea un terminal.
	 */
	protected Terminal(Rango rango)
	{
		this.rango = rango;
	}
	//////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el rango de ubicacion de este terminal.
	 */
	public Rango obtRango()
	{
		return rango;
	}
}