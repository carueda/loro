package loro.tipo;

import loro.Loro.Str;

///////////////////////////////////////////////////////////
/**
 * Tipo caracter.
 */
class TipoCaracter extends TipoBasico
{
	//////////////////////////////////////////////////////////////////////
	public boolean esCaracter()
	{
		return true;
	}
	
	//////////////////////////////////////////////////////////////////////
	public boolean esConvertibleA(Tipo t)
	{
		return t.esCadena() 
			|| t.esEntero()
			|| t.esCaracter()
		;
	}
	
	//////////////////////////////////////////////////////////////////////
	public boolean esSumable()
	{
		return true;
	}
	

	
	//////////////////////////////////////////////////////////////////////
	public Object obtValorDefecto()
	{
		return defectoCaracter;
	}
	
	//////////////////////////////////////////////////////////////////////
	public String toString()
	{
		return Str.get("character");
	}

	/** Valor por defecto para un caracter Loro: '\u0000' */
	private static final Character defectoCaracter = new Character((char)0);
	/** Mi unica instancia */
	private static final Tipo instancia = new TipoCaracter();

	//////////////////////////////////////////////////////////////////////
	private TipoCaracter() {}

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