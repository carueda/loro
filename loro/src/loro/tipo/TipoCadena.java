package loro.tipo;

import loro.Loro.Str;
import loro.util.ManejadorUnidades;

///////////////////////////////////////////////////////////
/**
 * Tipo cadena
 */
class TipoCadena extends TipoObjeto
{
	/** Mi unica instancia */
	private static final Tipo instancia = new TipoCadena();

	//////////////////////////////////////////////////////////////////////
	private TipoCadena() {}

	//////////////////////////////////////////////////////////////////////
	static Tipo obtInstancia()
	{
		return instancia;
	}

	///////////////////////////////////////////////////////////
	public boolean esAsignable(Tipo t)
	{
		return igual(t)  ||  t.esNulo();
	}

	//////////////////////////////////////////////////////////////////////
	public boolean esConvertibleA(Tipo t)
	{
		if ( t.esCadena() )
			return true;
		
		if ( t.esClase() )
		{
			TipoClase tc = (TipoClase) t;
			ManejadorUnidades mu = ManejadorUnidades.obtManejadorUnidades();
			String class_name = tc.obtNombreCompletoString();
			if ( mu.obtNombreClaseRaiz().equals(class_name) )
			{
				return true;
			}
		}
		
		return false;
	}
	
	///////////////////////////////////////////////////////////
	/**
	 */
	public boolean esCadena()
	{
		return true;
	}

	///////////////////////////////////////////////////////////
	/**
	 */
	public boolean igual(Tipo t)
	{
		return this == t;
	}

	///////////////////////////////////////////////////////////
	public Tipo obtTipoElemento()
	{
		return Tipo.caracter;
	}

	///////////////////////////////////////////////////////////
	public Object obtValorDefecto()
	{
		return null;
	}

	///////////////////////////////////////////////////////////
	public String toString()
	{
		return Str.get("string");
	}

	//////////////////////////////////////////////////////////////////////
	private Object readResolve()
	throws java.io.ObjectStreamException
	{
		return instancia;
	}
}