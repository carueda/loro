package loro.ejecucion;

import loro.arbol.*;
import loro.visitante.VisitanteException;



import loro.tabsimb.*;

///////////////////////////////////////////////////////////
/**
 * Un ejecutor que puede terminarse externamente.
 *
 * @author Carlos Rueda
 */
public class EjecutorTerminable extends LoroEjecutor
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


	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NACadena n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NAfirmacion n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NAlgoritmo n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NAsignacion n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NCardinalidad n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NCaso n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NCiclo n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NClase n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NCondicion n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NConstructor n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NContinue n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NConvertirTipo n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NCorrDer n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NCorrDerDer n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NCorrIzq n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NCrearArreglo n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NCrearArregloTipoBase n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NCrearObjeto n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NCuantificado n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NDecision n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NDecisionMultiple n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NDeclaracion n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NDescripcion n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NDiferente n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NDivReal n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NEquivalencia n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NEsInstanciaDe n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NEspecificacion n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NFuente n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NId n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NIgual n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NImplicacion n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NInvocacion n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NLiteralBooleano n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NLiteralCadena n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NLiteralCaracter n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NLiteralEntero n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NLiteralNulo n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NLiteralReal n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NMas n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NMayor n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NMayorIgual n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NMenor n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NMenorIgual n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NMenos n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NMientras n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NMod n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NNeg n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NNo n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NNoBit n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NNombre n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NO n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NOArit n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NOExc n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NPaquete n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NPara n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NPlus n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NPor n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NRepita n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NRetorne n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NSubId n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NSubindexacion n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NTermine n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NUtiliza n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NY n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}
	////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NYArit n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}

	////////////////////////////////////////////////////////
	public void visitar(NIntente n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}

	////////////////////////////////////////////////////////
	public void visitar(NLance n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
	}

	//////////////////////////////////////////////////////////////////////
	public void visitar(NAtrape n)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super.visitar(n);
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