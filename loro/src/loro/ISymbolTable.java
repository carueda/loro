package loro;


//////////////////////////////////////////////////////////////////////////
/**
 * Interface para acceder a tabla de s�mbolos.
 *
 * @version 2002-10-02
 */
public interface ISymbolTable
{
	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene los nombres de las variables declaradas.
	 *
	 * @param id    El identificador.
	 *
	 */
	public String[] getVariableNames();
	
	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene una cadena que describe el tipo de un identificador.
	 * Si el identificador no est� definido, retorna null.
	 *
	 * @param id    El identificador.
	 *
	 */
	public String getTypeString(String id);
	
	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene una representaci�n cadena del valor de un identificador.
	 * Si el identificador no est� definido, retorna null.
	 * Si el identificador no tiene asignaci�n, retorna "?".
	 * En otro caso, se retorna la representaci�n del valor de acuerdo
	 * con el lenguaje.
	 *
	 * @param id    El identificador.
	 *
	 */
	public String getValueString(String id);
	
	//////////////////////////////////////////////////////////////
	/**
	 * Elimina la entrada asociada a un identificador.
	 *
	 * @param id    El identificador.
	 *
	 * @return true sii la entrada ha sido eliminada.
	 */
	public boolean borrar(String id);

}
