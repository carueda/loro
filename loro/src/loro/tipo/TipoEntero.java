package loro.tipo;

import loro.Loro.Str;

///////////////////////////////////////////////////////////
/**
 * Tipo entero.
 */
class TipoEntero extends TipoBasico
{
	//////////////////////////////////////////////////////////////////////
	public boolean esAsignable(Tipo t)
	{
		return t.esEntero() || t.esCaracter();
	}
	
	//////////////////////////////////////////////////////////////////////
	public boolean esConvertibleA(Tipo t)
	{
		return t.esCadena() 
		    || t.esReal() 
			|| t.esBooleano() 
			|| t.esEntero()
			|| t.esCaracter()
		;
	}
	
	//////////////////////////////////////////////////////////////////////
	public boolean esEntero()
	{
		return true;
	}
	
	//////////////////////////////////////////////////////////////////////
	public boolean esNumerico()
	{
		return true;
	}
	
	//////////////////////////////////////////////////////////////////////
	public boolean esSumable()
	{
		return true;
	}
	

	
	//////////////////////////////////////////////////////////////////////
	public Object obtValorDefecto()
	{
		return defectoEntero;
	}
	
	//////////////////////////////////////////////////////////////////////
	public String toString()
	{
		return Str.get("integer");
	}

	/** Valor por defecto para un entero Loro: 0 */
	private static final Integer defectoEntero = new Integer(0);
	/** Mi unica instancia */
	private static final Tipo instancia = new TipoEntero();

	//////////////////////////////////////////////////////////////////////
	private TipoEntero() {}

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