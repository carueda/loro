package loro.tipo;

import loro.arbol.*;

import loro.util.Util;
import loro.ejecucion.Objeto;
import loro.util.ManejadorUnidades;
import java.util.*;
import loro.compilacion.ClaseNoEncontradaException;

////////////////////////////////////////////////////
/**
 * Servicios generales sobre el sistema de tipos.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class Tipos 
{
	/*
		2002-06-02
		Note que el manejador de unidades es siempre pedido en cada rutina 
		que lo necesita. Esto permite tener siempre la versión actualizada 
		de dicho manejador ante posibles reinicios del núcleo.
	*/
	
	////////////////////////////////////////////////////////////////
	/**
	 * Dice si una clase es igual o es subclase de otra.
	 */
	public static boolean aKindOf(TipoClase tc1, TipoClase tc2)
	throws ClaseNoEncontradaException
	{
		ManejadorUnidades mu = ManejadorUnidades.obtManejadorUnidades();
		
		// tome el nombre de la super clase:
		String[] n2 = tc2.obtNombreConPaquete();
		String t2 = Util.obtStringRuta(n2);
		NClase c2 = mu.obtClase(t2);

		// tome el nombre de la clase base:
		String[] n1 = tc1.obtNombreConPaquete();
		String t1 = Util.obtStringRuta(n1);
		NClase clase = mu.obtClase(t1);
		
		while ( clase != null )
		{
			if ( t1.equals(t2) )
				return true;

			// intente por super clase:
			clase = mu.obtSuperClase(clase);
			if ( clase != null )
				t1 = clase.obtNombreCompletoCadena();
		}

		return false;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Dice si una clase es igual o es subclase de otra.
	 */
	public static boolean aKindOf(NClase clase, TipoClase tc2)
	throws ClaseNoEncontradaException
	{
		ManejadorUnidades mu = ManejadorUnidades.obtManejadorUnidades();
		
		// tome el nombre de la super clase:
		String[] n2 = tc2.obtNombreConPaquete();
		String t2 = Util.obtStringRuta(n2);
		NClase c2 = mu.obtClase(t2);

		while ( clase != null )
		{
			// tome el nombre de la clase base:
			String t1 = clase.obtNombreCompletoCadena();
			
			if ( t1.equals(t2) )
				return true;

			// intente por super clase:
			clase = mu.obtSuperClase(clase);
		}

		return false;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Dice si un objeto es instancia de un tipo.
	 *
	 * @param object Object chequeado.
	 * @param e_tipo Tipo declarado del objeto
	 * @param tipo Tipo confrontado
	 */
	public static boolean isInstanceOf(Object object, Tipo e_tipo, Tipo tipo)
	throws ClaseNoEncontradaException
	{
		if ( object == null )
			return false;        // como en Java:
		
		ManejadorUnidades mu = ManejadorUnidades.obtManejadorUnidades();

		String t2 = tipo.toString();
		
		if ( object instanceof Objeto )
		{
			// tipo tiene que ser TipoClase:
			if ( !(tipo instanceof TipoClase) )
				return false;
			
			// tome el nombre de la super clase:
			TipoClase tc2 = (TipoClase) tipo;
			
			Objeto obj = (Objeto) object;
			NClase c2 = mu.obtClase(t2);
			NClase clase = obj.obtNClase();
			while ( clase != null )
			{
				// tome el nombre de la clase base:
				String t1 = clase.obtNombreCompletoCadena();
				
				if ( t1.equals(t2) )
					return true;
	
				// intente por super clase:
				clase = mu.obtSuperClase(clase);
			}
			return false;
		}
		else if ( object instanceof String )
		{
			// t2 tiene que ser "cadena" o el nombre de la clase raiz:
			return Tipo.cadena.toString().equals(t2)
			    || mu.obtNombreClaseRaiz().equals(t2);
		}
		else if ( object instanceof Object[] )
		{
			// tienen que ser iguales e_tipo y tipo:
			return e_tipo.igual(tipo);
		}
		else
			throw new Error("Tipo.isInstanceOf: Imposible");
	}
	
	//////////////////////////////////////////////////////////
	/**
	 * Dice si una clase implementa la interface dada.
	 * Esto significa comprobar si el nombre de alguna de las
	 * interfaces implementadas por la clase coincide que el
	 * nombre de la interface dada.
	 *
	 * @throws ClaseNoEncontradaException
	 *              Si algun tipo no se encuentra.
	 */
	public static boolean implementa(NClase clase, TipoInterface ti)
	throws ClaseNoEncontradaException
	{
		ManejadorUnidades mu = ManejadorUnidades.obtManejadorUnidades();
		
		// obtenga los nombres de todas las interfaces implementadas por la clase:
		Set obj_interfs = mu.obtSuperInterfaces(clase);

		// el resultado es true si contiene el nombre de la interface preguntada:
		return obj_interfs.contains(ti.obtNombreCompletoString());

	}

	//////////////////////////////////////////////////////////
	/**
	 * Dice si un objeto implementa la interface dada.
	 * Esto significa comprobar si el nombre de alguna de las
	 * interfaces implementadas por el objeto coincide que el
	 * nombre de la interface dada.
	 *
	 * @throws ClaseNoEncontradaException
	 *              Si algun tipo no se encuentra.
	 */
	public static boolean implementa(Objeto obj, TipoInterface ti)
	throws ClaseNoEncontradaException
	{
		return  obj != null  &&  implementa(obj.obtNClase(), ti);
	}

	//////////////////////////////////////////////////////////
	/**
	 * Dice si una clase implementa la interface dada.
	 * Esto significa comprobar si el nombre de alguna de las
	 * interfaces implementadas por la clase coincide que el
	 * nombre de la interface dada.
	 *
	 * @throws ClaseNoEncontradaException
	 *              Si algun tipo no se encuentra.
	 */
	public static boolean implementa(TipoClase tc, Tipo t)
	throws ClaseNoEncontradaException
	{
		if ( !t.esInterface() )
		{
			return false;
		}

		ManejadorUnidades mu = ManejadorUnidades.obtManejadorUnidades();
		
		NClase clase = mu.obtClase(tc.obtNombreCompletoString());

		return implementa(clase, (TipoInterface) t);

	}

	//////////////////////////////////////////////////////////
	/**
	 * Obtiene la operacion identificada con el nombre dado entre
	 * las interfaces implementadas por la clase dada.
	 *
	 * @param clase La clase a examinar
	 * @param id El nombre de la operacion a buscar
	 *
	 * @return La operacion. null si no se encuentra.
	 *
	 * @throws ClaseNoEncontradaException
	 *              Si algun tipo no se encuentra.
	 */
	public static NEspecificacion obtOperacion(NClase clase, String id)
	throws ClaseNoEncontradaException
	{
		ManejadorUnidades mu = ManejadorUnidades.obtManejadorUnidades();
		
		// obtenga los nombres de todas las interfaces implementadas por la clase:
		Set obj_interfs = mu.obtSuperInterfaces(clase);

		// examine todas estas interfaces:
		for ( Iterator it = obj_interfs.iterator(); it.hasNext(); )
		{
			String nom_interf = (String) it.next();

			NInterface interf = mu.obtInterface(nom_interf);
			if ( interf != null )
			{
				NEspecificacion[] opers = interf.obtOperacionesDeclaradas();
				for ( int k = 0; k < opers.length; k++ )
				{
					String test_id = opers[k].obtId().obtId();
					if ( id.equals(test_id)  )
					{
						return opers[k];
					}
				}
			}
		}

		return null;

	}

	//////////////////////////////////////////////////////////
	/**
	 * Obtiene la operacion identificada con el nombre dado en
	 * la interface dada o en alguna de sus superinterfaces.
	 *
	 * @param ti  El tipo interface examinar.
	 * @param id El nombre de la operacion a buscar
	 *
	 * @return La operacion. null si no se encuentra.
	 *
	 * @throws ClaseNoEncontradaException
	 *              Si algun tipo no se encuentra.
	 */
	public static NEspecificacion obtOperacion(TipoInterface ti, String id)
	throws ClaseNoEncontradaException
	{
		ManejadorUnidades mu = ManejadorUnidades.obtManejadorUnidades();
		
		NInterface interf = mu.obtInterface(ti.obtNombreCompletoString());

		// recoja los nombres de las interfaces en donde buscar:
		Set nom_interfs = mu.obtSuperInterfaces(interf);
		if ( nom_interfs == null  )
		{
			nom_interfs = new HashSet();
		}

		// agregue el propio nombre de la interface dada
		nom_interfs.add(ti.obtNombreCompletoString());

		// examine todas estas interfaces:
		for ( Iterator it = nom_interfs.iterator(); it.hasNext(); )
		{
			String nom_interf = (String) it.next();

			interf = mu.obtInterface(nom_interf);
			NEspecificacion[] opers = interf.obtOperacionesDeclaradas();
			for ( int k = 0; k < opers.length; k++ )
			{
				String test_id = opers[k].obtId().obtId();
				if ( id.equals(test_id)  )
				{
					return opers[k];
				}
			}
		}

		return null;

	}


	//////////////////////////////////////////////////////////
	/**
	 * Dice si el tipo corresponde a un tipo referencia. Concretamente,
	 * una variable de tipo referencia puede tener el valor nulo (null).
	 * La implementación actual retorna la evaluación de
	 * t.obtValorDefecto() == null .
	 */
	public static boolean esReferencia(Tipo t)
	{
		return t.obtValorDefecto() == null ;
	}
}