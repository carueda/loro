package loro.tipo;

import loro.util.Util;


import loro.arbol.NEspecificacion;
import loro.compilacion.ClaseNoEncontradaException;

///////////////////////////////////////////////////////////
/**
 * El tipo central de Loro para objetos.
 *
 * 2002-05-24
 */
public class TipoInterface extends TipoUnidad
{
	

	



	

	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Dice si a una variable de este tipo se le puede asignar un valor
	 * del tipo dado.
	 */
	public boolean esAsignable(Tipo t)
	throws ClaseNoEncontradaException
	{
		if ( t.esNulo() )
		{
			return true;
		}

		if ( t.esClase() )
		{
			return t.implementa(this);
		}

		if ( !t.esInterface() )
		{
			return false;
		}

		TipoInterface tt = (TipoInterface) t;

		// provisionalmente:
		return igual(tt);
			// pendiente: revisar jerarquia de tipos
	}
	
	//////////////////////////////////////////////////////////
	/**
	 * Dice si un valor de este tipo es convertible al tipo dado.
	 * NOTA: retorna <code>t.esCadena() || igual(t)</code>
	 */
	public boolean esConvertibleA(Tipo t)
	{
		return t.esCadena() || igual(t);
	}
	


	

	

	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Este tipo es igual al dado solo si:
	 * <ul>
	 *	<li> el dado es una interface.
	 *	<li> los nombres son iguales.
	 * </ul>
	 */
	public boolean igual(Tipo t)
	{
		if ( !t.esInterface() )
		{
			return false;
		}

		TipoInterface tt = (TipoInterface) t;

		String s = obtNombreCompletoString();
		String ss = tt.obtNombreCompletoString();

		return s.equals(ss);
	}









	//////////////////////////////////////////////////////////////////////
	/**
	 * Crea un tipo interface asociado al nombre dado.
	 *
	 * @throws NullPointerException
	 */
	TipoInterface(String[] nombre)
	{
		super(nombre);
	}



	//////////////////////////////////////////////////////////
	/**
	 * Retorna true.
	 */
	public boolean esInterface()
	{
		return true;
	}

	//////////////////////////////////////////////////////////
	/**
	 * Obtiene la operacion de nombre dado.
	 */
	public NEspecificacion obtOperacion(String id)
	throws ClaseNoEncontradaException
	{
		NEspecificacion oper = Tipos.obtOperacion(this, id);
		return oper;
	}
}