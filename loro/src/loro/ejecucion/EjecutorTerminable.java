package loro.ejecucion;

import loro.arbol.*;
import loro.visitante.VisitanteException;
import loro.tabsimb.*;
import loro.IUbicable;

///////////////////////////////////////////////////////////
/**
 * Un ejecutor que puede terminarse externamente.
 *
 * @author Carlos Rueda
 */
public class EjecutorTerminable extends Seguidor
{
	/** Mi hilo de ejecución. */
	Thread myThread;

	/** Ha sido terminado externamente este hilo? */
	boolean terminadoExternamente;

	///////////////////////////////////////////////////////
	/**
	 * Crea un ejecutor terminable.
	 * Se pone la mínima prioridad al hilo actual, con el fin
	 * de permitir una respuesta pronta cuando se intente
	 * interrumpir este hilo.
	 * <p>
	 * <b>NOTA</b>: Segun algunas referencias, este criterio no es muy confiable, 
	 * pero ha mostrado resultados prácticos en los entornos en que se ha probado
	 * hasta el momento. Sigue pendiente hacer una implementación completamente 
	 * confiable en este sentido.
	 */
	public EjecutorTerminable()
	{
		this(null, null);
	}

	///////////////////////////////////////////////////////
	/**
	 * Crea un ejecutor terminable.
	 *
	 * <p>
	 * <b>NOTA</b>: Segun algunas referencias, este criterio no es muy confiable, 
	 * pero ha mostrado resultados prácticos en los entornos en que se ha probado
	 * hasta el momento. Sigue pendiente hacer una implementación completamente 
	 * confiable en este sentido.
	 *
	 * @param tabSimbBase	Tabla de simbolos a tomar como de base.
	 * @param unidadActual	Unidad actual.
	 */
	public EjecutorTerminable(
		TablaSimbolos tabSimbBase, 
		NUnidad unidadActual
	)
	{
		super();
		reset(tabSimbBase, unidadActual);
	}

	///////////////////////////////////////////////////////
	/**
	 * Prepara este ejecutor para nueva visita.
	 * Se pone la mínima prioridad al hilo actual, con el fin
	 * de permitir una respuesta pronta cuando se intente
	 * interrumpir este hilo.
	 *
	 * <p>
	 * <b>NOTA</b>: Segun algunas referencias, este criterio no es muy confiable, 
	 * pero ha mostrado resultados prácticos en los entornos en que se ha probado
	 * hasta el momento. Sigue pendiente hacer una implementación completamente 
	 * confiable en este sentido.
	 *
	 * @param tabSimbBase	Tabla de simbolos a tomar como de base.
	 * @param unidadBase	Unidad de base.
	 */
	public void reset(TablaSimbolos tabSimbBase, NUnidad unidadBase)
	{
		super.reset(tabSimbBase, unidadBase);
		myThread = Thread.currentThread();
		myThread.setPriority(Thread.MIN_PRIORITY);
		terminadoExternamente = false;
	}


	//////////////////////////////////////////////////////////////
	/**
	 * Provoca la terminación del hilo del ejecutor terminable.
	 */
	public synchronized void terminarExternamente()
	{
		terminadoExternamente = true;   //++
		Thread current = Thread.currentThread();
		if ( myThread != current )
		{
			//-- terminadoExternamente = true;
			myThread.interrupt();
		}
	}


	////////////////////////////////////////////////////////////
	/**
	 * Invoca _chequearTerminacionExterna(n) y retorna.
	 */
	protected void _enter(INodo n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
	}
	
	////////////////////////////////////////////////////////////
	/**
	 * No hace nada.
	 */
	protected void _exit(INodo n)
	throws VisitanteException
	{
	}
	
	
	//////////////////////////////////////////////////////
	/**
	 * Retorna true: este ejecutor esta preparado para ser 
	 * terminado externamente. Vea terminarExternamente().
	 */
	public boolean esTerminableExternamente()
	{
		return true;
	}

	//////////////////////////////////////////////////
	/**
	 * Auxiliar para el chequeo de posible terminación externa.
	 * Si asi es, se lanza un nuevo objeto excepcion 
	 * TerminacionExternaException.
	 */
	protected void _chequearTerminacionExterna(IUbicable u)
	throws VisitanteException
	{
		if ( terminadoExternamente )
		{
			throw new TerminacionExternaException(u, pilaEjec);
		}
	}

	/////////////////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para visitar el cuerpo de acciones de una iteración.
	 * Se sobreescribe para chequear posible terminacion externa en
	 * el caso en que el arreglo de acciones es vacío.
	 */
	protected void _visitarAccionesIteracion(Nodo iter, Nodo[] acciones)
	throws VisitanteException
	{
		if ( acciones.length == 0 )
		{
			_chequearTerminacionExterna(iter);
		}

		super._visitarAccionesIteracion(iter, acciones);
	}
}