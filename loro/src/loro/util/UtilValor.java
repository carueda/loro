package loro.util;


import loro.ejecucion.ArregloBaseNoCero;
import loro.compilacion.ClaseNoEncontradaException;
import loro.tipo.Tipo;
import loro.ijava.LException;

import loro.ejecucion.Objeto;

import java.util.*;

///////////////////////////////////////////////////////////////
/**
 * Algunas utilerias sobre valores.
 */
public final class UtilValor
{
	/**
	 * Maximo nivel de entrada en los atributos de
	 * un objeto para efectos de obtener su version cadena.
	 */
	private static int maxNivelVerObjeto = 4;

	/**
	 * Maxima longitud para efectos de obtener version cadena de un arreglo.
	 */
	private static int longitudVerArreglo = 20;
	
	
	/**
	 * Para controlar recursión.
	 * Memoria con los objetos ya procesados durante una operación de obtener
	 * la versión cadena. 
	 */
	private static Set processed_objects;


	///////////////////////////////////////////////////////////////////////
	/**
	 * Pone la maxima longitud para efectos de mostrar la composicion
	 * de un arreglo.
	 */
	public static void ponLongitudVerArreglo(int longitudVerArreglo_)
	{
		longitudVerArreglo = longitudVerArreglo_;
	}

	///////////////////////////////////////////////////////////////////////
	/**
	 * Pone el máximo nivel de profundidad para efectos de mostrar la
	 * composición de objetos.
	 * Al entrar a procesar los miembros de un objeto, este nivel se
	 * decrementa en 1. Si al entrar a un objeto el nivel es 0, sólo
	 * se incluye en la cadena un texto del estilo <code>{...}</code>.
	 * Este comienza desde el primer objeto encontrado.
	 */
	public static void ponNivelVerObjeto(int maxNivelVerObjeto_)
	{
		maxNivelVerObjeto = maxNivelVerObjeto_;
	}


	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene una version cadena del objeto dado.
	 * Si es null, se retorna "nulo".
	 * Si es un Boolean, se retorna "cierto" o "falso" de acuerdo con el valor.
	 * Si es Objeto o arreglo, se tiene en cuenta posible recursión.
	 * Si es Objeto, se tiene en cuenta el máximo nivel para ver el objetos.
	 * Si es un arreglo, se tiene en cuenta la longitud para ver arreglos.
	 */
	public static String comoCadena(Object o)
	{
		processed_objects = new HashSet();
		return _auxComoCadena(o, maxNivelVerObjeto);
	}

	///////////////////////////////////////////////////////////////////////
	public static String valorComillasDeExpresion(Tipo tipo, Object o)
	{
		String res = null;

		if ( o != null )
		{
			res = comoCadena(o);

			// mire si hay que poner ``quotes'':
			char q = 0;
			if ( tipo.esCaracter() )
			{
				q = '\'';
			}
			else if ( tipo.esCadena() )
			{
				q = '\"';
			}

			if ( q != 0 )
			{
				res = Util.quote(q, res);
			}
		}
		else if ( tipo.esUnit() )
		{
			// no imprimir nada. Ok
		}
		else
		{
			// muestre este nulo:
			res = "nulo";
		}

		return res;
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para comoCadena(Object).
	 */
	private static String _auxComoCadena(Object o, int nivelVerObjeto)
	{
		if ( o == null )
		{
			return "nulo";
		}
		else if ( o instanceof Boolean )
		{
			boolean b = ((Boolean)o).booleanValue();
			return b ? "cierto" : "falso";
		}
		
		String res;
		if ( o instanceof Objeto )
		{
			res = _comoCadenaObjeto((Objeto) o, nivelVerObjeto);
		}
		else if ( o instanceof ArregloBaseNoCero )
		{
			o = ((ArregloBaseNoCero) o).array;
			res = _comoCadenaArreglo(o, nivelVerObjeto);
		}
		else if ( o.getClass().isArray() )
		{
			res = _comoCadenaArreglo(o, nivelVerObjeto);
		}
		else
		{
			res = o.toString();
		}

		return res;
	}


	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene una version cadena del arreglo dado.
	 */
	private static String _comoCadenaArreglo(Object a, int nivelVerObjeto)
	{
		if ( processed_objects.contains(a) )
			return "[!]";  // recursión detectada!
		
		processed_objects.add(a);
		
		StringBuffer sb = new StringBuffer("[");
		int size = java.lang.reflect.Array.getLength(a);
		
		for ( int i = 0; i < size; i++ )
		{
			if ( i > 0 )
				sb.append(",");
			
			if ( i >= longitudVerArreglo )
			{
				sb.append("...");
				break;
			}
				
			Object val = java.lang.reflect.Array.get(a, i);
			
			String cad;
			if ( val instanceof Objeto )
			{
				cad = _comoCadenaObjeto((Objeto) val, nivelVerObjeto) ;
			}
			else if ( val instanceof String )
			{
				cad = Util.quote('\"', val.toString());
			}
			else if ( val instanceof Character )
			{
				cad = Util.quote('\'', val.toString());
			}
			else
			{
				cad = _auxComoCadena(val, nivelVerObjeto);
			}

			sb.append(cad);
		}
		sb.append("]");

		return sb.toString();
	}

	//////////////////////////////////////////////////////////////
	/**
	 */
	private static String _comoCadenaObjeto(Objeto o, int nivelVerObjeto)
	{
		if ( processed_objects.contains(o) )
			return "{!}";  // recursión detectada!
		
		processed_objects.add(o);
		
		StringBuffer sb = new StringBuffer("{");
		if ( nivelVerObjeto == 0 )
		{
			sb.append("...");
		}
		else
		{
			try
			{
				ManejadorUnidades mu = ManejadorUnidades.obtManejadorUnidades();
				String[] atrs = mu.obtNombresAtributos(o.obtNClase());
				

				for ( int i = 0; i < atrs.length; i++ )
				{
					if ( i > 0 )
						sb.append(",");

					String atr = atrs[i];
					String cad;
					try
					{
						Object val = o.obtValor(atr);
						if ( val instanceof String )
						{
							cad = Util.quote('\"', val.toString());
						}
						else if ( val instanceof Character )
						{
							cad = Util.quote('\'', val.toString());
						}
						else if ( val instanceof Objeto )
						{
							cad = _comoCadenaObjeto((Objeto) val, nivelVerObjeto -1) ;
						}
						else
						{
							cad = _auxComoCadena(val, nivelVerObjeto);
						}
					}
					catch (LException ex)
					{
						cad = "<<" +ex.getMessage()+ ">>";
					}
					
					sb.append(atr+ "=" +cad);
				}
			}
			catch (ClaseNoEncontradaException ex)
			{
				sb.append("!!Clase no encontrada: '" +ex.obtNombre()+ "'");
			}
		}
		sb.append("}");

		return sb.toString();
	}
	
	///////////////////////////////////////////////////////////
	// Clase no instanciable.
	private UtilValor() {}

}