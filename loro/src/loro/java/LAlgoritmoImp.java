package loro.java;

import loro.ijava.*;
import loro.arbol.NAlgoritmo;
import loro.EjecucionException;
import loro.ejecucion.LoroEjecutor;

///////////////////////////////////////////////////////////
/**
 * Implementacion de un l-algoritmo.
 */
public class LAlgoritmoImp implements LAlgoritmo
{
	/** El ejecutor. */
	LoroEjecutor ejecutor;
	
	/** El algoritmo verdadero asociado. */
	NAlgoritmo alg;

	//////////////////////////////////////////////////////////////
	/**
	 * Ejecuta este algoritmo sobre los argumentos dados.
	 * El objeto de retorno debera ser promovido al tipo
	 * adecuado correspondiente al tipo de retorno del
	 * algoritmo. Si la declaracion del algoritmo indica que
	 * no hay salida, se retorna null.
	 */
	public Object ejecutar(Object[] args)
	throws LException
	{
		try
		{
//
// PROVISIONAL   2003-04-16
//
// La ejecución por este medio debe lanzarse en un NUEVO objeto
// de ejecución.
//
// Provisionalmente se hace un poco a "fuerza bruta".
// PENDIENTE definir mejor diseño para esto.
//
			
			loro.IEjecutor exe = loro.Loro.crearEjecutorTerminableExternamente();
			loro.ejecucion.ManejadorEntradaSalida mes = (loro.ejecucion.ManejadorEntradaSalida)
				ejecutor.obtManejadorEntradaSalida();
			exe.ponEntradaEstandar(mes.obtEntradaEstandar());
			exe.ponSalidaEstandar(mes.obtSalidaEstandar());
			Object ret = exe.ejecutarAlgoritmo(alg, args);
			return ret;


// codigo anterior: ejecución sobre el mismo ejecutor:
			//return ejecutor.ejecutarAlgoritmo(alg, args);
		}
		catch(EjecucionException ex)
		{
			throw new LException(ex.getMessage());
		}
	}
	////////////////////////////////////////////////////////
	/**
	 *  Obtiene el algoritmo verdadero correspondiente.
	 */
	public NAlgoritmo obtAlgoritmo()
	{
		return alg;
	}
	////////////////////////////////////////////////////////
	/**
	 *  Obtiene una version cadena para el algoritmo.
	 */
	public String toString()
	{
		return alg.toString();
	}

	////////////////////////////////////////////////////////
	/**
	 * Crea un l-algoritmo, o sea, un algoritmo de interfaz con
	 * java asociado al algoritmo loro dado.
	 */
	public LAlgoritmoImp(LoroEjecutor ejecutor, NAlgoritmo alg) 
	{
		this.ejecutor = ejecutor;
		this.alg = alg;
	}
}