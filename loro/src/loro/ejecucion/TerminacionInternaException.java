package loro.ejecucion;

import loro.Loro.Str;

//////////////////////////////////////////////////////////////////
/**
 * Exception lanzada cuando se llama el proceso 
 * <tt>loroI::sistema::terminarEjecuci�n(cod: entero)</tt>.
 */
class TerminacionInternaException extends RuntimeException
{
	/** El c�digo de terminaci�n. */
	int codigo;
	///////////////////////////////////////////////////////////
	/**
	 * Crea una exception para indicar terminaci�n del programa
	 * Loro.
	 *
	 * @codigo C�digo de terminaci�n.
	 */
	public TerminacionInternaException(int codigo)
	{
		super(Str.get("rt.exit_code")+ " = " +codigo);
		this.codigo = codigo;
	}
	/////////////////////////////////////////////////////////
	/**
	 * Obtiene el c�digo de terminaci�n asociado.
	 */
	public int obtCodigo()
	{
		return codigo;
	}
}