package loro;

import java.io.Reader;
import java.io.Writer;

//////////////////////////////////////////////////////
/**
 * Un ejecutor de unidades ejecutables.
 *
 * Esta interface no es para ser implementada por el cliente.
 */
public interface IEjecutor 
{
	//////////////////////////////////////////////////////
	/**
	 * Es terminable externamente este ejecutor?
	 */
	public boolean esTerminableExternamente();
	
	//////////////////////////////////////////////////////////////
	/**
	 * Pone la entrada estandar.
	 */
	public void ponEntradaEstandar(Reader r);
	
	//////////////////////////////////////////////////////////////
	/**
	 * Pone la salida estandar.
	 */
	public void ponSalidaEstandar(Writer w);
	
	//////////////////////////////////////////////////////
	/**
	 * Termina externamente este ejecutor.
	 *
	 * @throws UnsupportedOperationException Si este ejecutor
	 *         no puede terminarse externamente.
	 */
	public void terminarExternamente();

	//////////////////////////////////////////////////////
	/**
	 * Puede seguirse paso a paso este ejecutor?
	 */
	public boolean esPasoAPaso();
	
	//////////////////////////////////////////////////////
	/**
	 * Pone observador para ejecución paso-a-paso.
	 * 
	 * POR COMPLEMENTAR.
	 *
	 * @throws UnsupportedOperationException Si este ejecutor
	 *         no puede admite esta operación.
	 */
	public void ponObservadorPP(IObservadorPP obspp);
	
	////////////////////////////////////////////////////////////////////
	/**
	 * Ejecuta una unidad algoritmo con argumentos dados.
	 */
	public Object ejecutarAlgoritmo(IUnidad alg, Object[] args)
	throws EjecucionException;

	/////////////////////////////////////////////////////////////////////
	/**
	 * Para cuando se llame desde línea de comandos.
	 * Este hace preparativos y llama a ejecutarAlgoritmo(alg, Object[]).
	 */
	public void ejecutarAlgoritmoArgumentosCadena(
		IUnidad alg, String[] cadArgs
	)
	throws EjecucionException;
}