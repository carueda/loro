package loro.ejecucion;

import loro.arbol.*;
import loro.util.ManejadorUnidades;
import loro.util.UtilValor;
import loro.ijava.LException;


///////////////////////////////////////////////////////////////
/**
 * Algunas ayudas para ejecución.
 */
final class UtilEjecucion 
{
	static final String PREFIX = "::::";
	
	///////////////////////////////////////////////////////////
	// Clase no instanciable.
	private UtilEjecucion() {}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Muestra los argumentos de entrada para ejecutar un algoritmo.
	 */
	static void _showAlgorithmArguments(
		NAlgoritmo alg,
		Object[] args,
		ManejadorUnidades mu,
		ManejadorEntradaSalida mes
	)
	{
		NDeclaracion[] pent = alg.obtParametrosEntrada();

		mes.escribir(
PREFIX+ " Ejecutando " +alg+ "\n"
		);
		if ( args.length > 0 )
		{
			mes.escribir(
PREFIX+ " Con argumento" +(args.length > 1 ? "s" : "")+ ":\n");
			for ( int i = 0; i < args.length; i++ )
			{
				mes.escribir(
PREFIX+ "   "
				);
				
				NDeclaracion dec = pent[i];
				mes.escribir(dec.obtId()+ " = " +UtilValor.comoCadena(args[i])+ "\n");
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Muestra el resultado de ejecutar un algoritmo.
	 */
	static void _showAlgorithmOutput(
		NAlgoritmo alg,
		Object res,
		ManejadorUnidades mu,
		ManejadorEntradaSalida mes
	)
	{
		NDescripcion[] dsal = null;
		NEspecificacion espec = mu.obtEspecificacionDeAlgoritmo(alg);
		if ( espec != null )
		{
			NDescripcion des = espec.obtDescripcionesSalida()[0];
			mes.escribir(des.obtDescripcion()+ " " +des.obtId()+ " = ");
		}
		mes.escribir(UtilValor.comoCadena(res)+ "\n");
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Pide los argumentos de entrada para ejecutar un algoritmo.
	 */
	static String[] _pedirArgumentosParaAlgoritmo(
		NAlgoritmo alg,
		ManejadorUnidades mu,
		ManejadorEntradaSalida mes,
		PilaEjecucion pilaEjec
	)
	throws EjecucionVisitanteException, LException
	{
		NDeclaracion[] pent = alg.obtParametrosEntrada();
		NDescripcion[] dent = null;
		NEspecificacion espec = mu.obtEspecificacionDeAlgoritmo(alg);
		if ( espec != null )
		{
			dent = espec.obtDescripcionesEntrada();
		}

		int n = pent.length;
		String[] cadArgs = new String[n];

		try
		{
			mes.escribir(
PREFIX+ " Para ejecutar:\n"+
PREFIX+ "    " +alg+ "\n"
			);
			if ( n == 1 )
			{
				mes.escribir(
PREFIX+ " se requiere una entrada.\n"+
PREFIX+ " A continuación, se solicitará esta información.\n"+
PREFIX+ " Tener en cuenta el tipo esperado para esta entrada.\n"
				);
			}
			else
			{
				mes.escribir(
PREFIX+ " se requieren " +n+ " entradas.\n"+
PREFIX+ " A continuación, se solicitará esta información.\n"+
PREFIX+ " Tener en cuenta los tipos esperados para estas entradas.\n"
				);
			}
			
			for ( int i = 0; i < n; i++ )
			{
				if ( dent != null )
				{
					NDescripcion des = dent[i];
					mes.escribir(UtilValor.comoCadena(des.obtDescripcion())+ " ");
				}

				NDeclaracion dec = pent[i];
				mes.escribir(dec.obtId()+ " := ");
				
				cadArgs[i] = mes.leerCadena();
			}
		}
		catch(java.io.IOException ex)
		{
			throw new TerminacionException(alg, pilaEjec);
		}

		return cadArgs;
	}
}