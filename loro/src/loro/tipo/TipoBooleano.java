package loro.tipo;

import loro.Loro.Str;


///////////////////////////////////////////////////////////
/**
 * Tipo booleano.
 */
class TipoBooleano extends TipoBasico
{
	//////////////////////////////////////////////////////////////////////
	public boolean esBooleano()
	{
		return true;
	}
	
	//////////////////////////////////////////////////////////////////////
	public boolean esConvertibleA(Tipo t)
	{
		return t.esCadena() 
			|| t.esEntero()
		;
	}
	

	
	//////////////////////////////////////////////////////////////////////
	public Object obtValorDefecto()
	{
		return defectoBooleano;
	}
	
	//////////////////////////////////////////////////////////////////////
	public String toString()
	{
		return Str.get("boolean");
	}

	/** Valor por defecto para un booleano Loro: falso */
	private static final Boolean defectoBooleano = Boolean.FALSE;
	/** Mi unica instancia */
	private static final Tipo instancia = new TipoBooleano();

	//////////////////////////////////////////////////////////////////////
	private TipoBooleano() {}

	//////////////////////////////////////////////////////////////////////
	static Tipo obtInstancia()
	{
		return instancia;
	}

	//////////////////////////////////////////////////////////////////////
	private Object readResolve()
	throws java.io.ObjectStreamException
	{
		return instancia;
	}
}