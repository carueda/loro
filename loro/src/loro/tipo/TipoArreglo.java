package loro.tipo;

import loro.util.ManejadorUnidades;
import loro.compilacion.ClaseNoEncontradaException;

//////////////////////////////////////////////////////////////////
/**
 * Tipo arreglo.
 */
class TipoArreglo extends TipoObjeto
{
	Tipo elemTipo;

	//////////////////////////////////////////////////////////////////////
	TipoArreglo(Tipo et)
	{
		elemTipo = et;
	}
	
	//////////////////////////////////////////////////////////////////////
	public boolean esArreglo()
	{
		return true;
	}
	
	//////////////////////////////////////////////////////////////////////
	public boolean esArreglo(Tipo et)
	{
		return elemTipo.igual(et);
	}

	//////////////////////////////////////////////////////////////////////	
	public boolean esAsignable(Tipo t)
	throws ClaseNoEncontradaException
	{
		return t.esNulo()
			|| ( t.esArreglo() && elemTipo.esAsignable(t.obtTipoElemento()) )
		;
	}
	
	//////////////////////////////////////////////////////////////////////
	public boolean esConvertibleA(Tipo t)
	{
		if ( t.esArreglo() )
			return elemTipo.esConvertibleA(t.obtTipoElemento());
		
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
	
	//////////////////////////////////////////////////////////////////////
	public boolean igual(Tipo t)
	{
		return t instanceof TipoArreglo
			&& elemTipo.igual(t.obtTipoElemento())
		;
	}
	
	//////////////////////////////////////////////////////////////////////
	public Tipo obtTipoElemento()
	{
		return elemTipo;
	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Retorna null, el valor por defecto para todo arreglo.
	 */
	public Object obtValorDefecto()
	{
		return null;
	}
	
	//////////////////////////////////////////////////////////////////////
	public String toString()
	{
		return "[]" +elemTipo;
	}
}