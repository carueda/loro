package loro.impl;

import loro.ejecucion.*;
import loro.arbol.*;

import loro.*;

/////////////////////////////////////////////////////////////
/**
 * Este objeto interactua con un visitante ejecutor para
 * implementar IEjecutor.
 */
public class EjecutorImpl implements IEjecutor
{
	LoroEjecutor loroEjecutor;

	///////////////////////////////////////////////////
	/**
	 * Crea una implementacion de IEjecutor.
	 *
	 * @param tipoEjecutor Tipo de ejecutor deseado.
	 */
	public EjecutorImpl(int tipoEjecutor, LoroClassLoader loroClassLoader) 
	{
		loroEjecutor = Ejecutores.crearEjecutor(tipoEjecutor);
		loroEjecutor.ponClassLoader(loroClassLoader);
	}

	//////////////////////////////////////////////////////
	public boolean esTerminableExternamente() 
	{
		return loroEjecutor.esTerminableExternamente();
	}
	
	//////////////////////////////////////////////////////
	public void ponEntradaEstandar(java.io.Reader r) 
	{
		loroEjecutor.ponEntradaEstandar(r);
	}
	
	//////////////////////////////////////////////////////
	public void ponSalidaEstandar(java.io.Writer w) 
	{
		loroEjecutor.ponSalidaEstandar(w);
	}
	
	//////////////////////////////////////////////////////
	public void terminarExternamente() 
	{
		loroEjecutor.terminarExternamente();
	}
	
	//////////////////////////////////////////////////////
	public boolean esPasoAPaso() 
	{
		return loroEjecutor.esPasoAPaso();
	}
	
	//////////////////////////////////////////////////////
	public void ponObservadorPP(IObservadorPP obspp)
	{
		loroEjecutor.ponObservadorPP(obspp);
	}

	//////////////////////////////////////////////////////
	public Object ejecutarAlgoritmo(IUnidad u, Object[] args) 
	throws EjecucionException 
	{
		NAlgoritmo alg = (NAlgoritmo) u;
		return loroEjecutor.ejecutarAlgoritmo(alg, args);
	}

	//////////////////////////////////////////////////////
	public void ejecutarAlgoritmoArgumentosCadena(
		IUnidad u, String[] cadArgs
	) 
	throws EjecucionException 
	{
		NAlgoritmo alg = (NAlgoritmo) u;
		loroEjecutor.ejecutarAlgoritmoArgumentosCadena(alg, cadArgs);
	}
}