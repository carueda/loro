package loro.tipo;

///////////////////////////////////////////////////////////
abstract class TipoBasico extends Tipo
{
	///////////////////////////////////////////////////////////
	public boolean esBasico()
	{
		return true;
	}

	///////////////////////////////////////////////////////////
	TipoBasico()
	{
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Retorna this == t.
	 */
	public final boolean igual(Tipo t)
	{
		return t != null && this.getClass().equals(t.getClass());

		//// pendiente poder hacer:
		//return this == t;
		//// cuando se garantice caracter de singleton para los tipos basicos
		//// despues de lectura de serializables.
	}
}