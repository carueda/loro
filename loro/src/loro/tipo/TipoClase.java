package loro.tipo;

import loro.util.Util;
import loro.util.ManejadorUnidades;
import loro.compilacion.ClaseNoEncontradaException;

///////////////////////////////////////////////////////////
/**
 * Tipo clase.
 */
public class TipoClase extends TipoUnidad
{
	//////////////////////////////////////////////////////////////////////
	/**
	 * Dice si a una variable de este tipo se le puede asignar un valor
	 * del tipo dado.
	 * En este caso, si t es el tipo nulo o si t es clase igual o subclase
	 * de este tipo clase.
	 */
	public boolean esAsignable(Tipo t)
	throws ClaseNoEncontradaException
	{
		if ( t.esNulo() )
			return true;
		
		if ( t.esClase() )
		    return Tipos.aKindOf((TipoClase) t, this);
	    
		// si esta clase es la raiz de clases de Loro, i.e., Objeto,
		// entonces t puede ser cadena, o arreglo:
		ManejadorUnidades mu = ManejadorUnidades.obtManejadorUnidades();
		if ( mu.obtNombreClaseRaiz().equals(obtNombreCompletoString()) )
		{
			return t.esCadena() || t.esArreglo();
		}
		
		return false;
	}
	
	//////////////////////////////////////////////////////////
	/**
	 * Dice si un valor de este tipo es convertible al tipo dado.
	 */
	public boolean esConvertibleA(Tipo t)
	{
		try
		{
			if ( t.esClase() ) 
			    return Tipos.aKindOf((TipoClase) t, this) || Tipos.aKindOf(this, (TipoClase) t);
			
			// si esta clase es la raiz de clases de Loro, i.e., Objeto,
			// entonces t puede ser cadena, o arreglo:
			ManejadorUnidades mu = ManejadorUnidades.obtManejadorUnidades();
			if ( mu.obtNombreClaseRaiz().equals(obtNombreCompletoString()) )
			{
				return t.esCadena() || t.esArreglo();
			}
			
			return false;
		}
		catch(ClaseNoEncontradaException ex)
		{
			// ignore
		}
		return false;
	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Retorna true.
	 */
	public boolean esClase()
	{
		return true;
	}
	//////////////////////////////////////////////////////////////////////
	/**
	 * Retorna true.
	 */
	public boolean esObjeto()
	{
		return true;
	}
	

	
	//////////////////////////////////////////////////////////////
	/**
	 * Dice si está pendiente de clase.
	 */
	public boolean estaPendiente()
	{
		return nombre == null;
	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Chequea igualdad de tipos.
	 */
	public boolean igual(Tipo t)
	{
		if ( !t.esClase() )
		{
			return false;
		}

		TipoClase tt = (TipoClase) t;

		// Compare nombres:
		String s = Util.obtStringRuta(nombre);
		String ss = Util.obtStringRuta(tt.nombre);

		return s.equals(ss);
	}


	//////////////////////////////////////////////////////////////////////
	/**
	 * Retorna una descripcion breve de este tipo en uno de los estilos:
	 *
	 *		baz::Foo    nombre conocido
	 *		&lt;clase?&gt;    si no hay nombre (no deberia suceder)
	 *
	 * pendiente: REVISAR ESTO!
	 */
	public String toString()
	{
		return nombre != null ? Util.obtStringRuta(nombre) : "<clase?>";
	}


	//////////////////////////////////////////////////////////////////////
	/**
	 * Crea un tipo clase asociado al nombre dado.
	 */
	TipoClase(String[] nombre)
	{
		super(nombre);
	}


	//////////////////////////////////////////////////////////
	/**
	 * Dice si este tipo clase implementa el tipo interface dado.
	 *
	 * @param t   El tipo a revisar.
	 */
	public boolean implementa(Tipo t)
	throws ClaseNoEncontradaException
	{
		return Tipos.implementa(this, t);
	}
}