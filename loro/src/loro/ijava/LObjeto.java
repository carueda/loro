package loro.ijava;

///////////////////////////////////////////////////////////////
/**
 * Representacion en Java de un objeto Loro.
 * El programador puede asociar un objeto Java a un objeto Loro
 * con el fin de tener informacion adicional (privada) relacionada
 * con el objeto Loro correspondiente.
 * 
 * @author Carlos Rueda
 */
public interface LObjeto 
{
	//////////////////////////////////////////////////////////
	/**
	 * Obtiene la clase de este objeto.
	 */
	public LClase obtClase()
	throws LException;
	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene el objeto java asociado a este objeto Loro.
	 */
	public Object obtObjetoJava()
	throws LException;
	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene el valor de un atributo de este objeto.
	 */
	public Object obtValor(String atr)
	throws LException;
	///////////////////////////////////////////////////////////////
	/**
	 * Pone el objeto java asociado a este objeto Loro.
	 */
	public void ponObjetoJava(Object obj)
	throws LException;
	///////////////////////////////////////////////////////////////
	/**
	 * Pone el valor para un atributo.
	 */
	public void ponValor(String atr, Object valor)
	throws LException;
}