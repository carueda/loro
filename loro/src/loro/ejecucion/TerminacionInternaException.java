package loro.ejecucion;

import loro.Loro.Str;

//////////////////////////////////////////////////////////////////
/**
 * Exception lanzada cuando se llama el proceso 
 * <tt>loroI::sistema::terminarEjecución(cod: entero)</tt>.
 */
class TerminacionInternaException extends RuntimeException
{
	/** El código de terminación. */
	int codigo;
	///////////////////////////////////////////////////////////
	/**
	 * Crea una exception para indicar terminación del programa
	 * Loro.
	 *
	 * @codigo Código de terminación.
	 */
	public TerminacionInternaException(int codigo)
	{
		super(Str.get("rt.exit_code")+ " = " +codigo);
		this.codigo = codigo;
	}
	/////////////////////////////////////////////////////////
	/**
	 * Obtiene el código de terminación asociado.
	 */
	public int obtCodigo()
	{
		return codigo;
	}
}