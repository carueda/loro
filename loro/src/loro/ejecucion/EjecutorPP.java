package loro.ejecucion;

import loro.IObservadorPP;
import loro.arbol.*;
import loro.visitante.VisitanteException;
import loro.tabsimb.*;

///////////////////////////////////////////////////////////
/**
 * Un ejecutor que puede controlarse paso a paso.
 */
public class EjecutorPP extends EjecutorTerminable 
{
	protected ControlPP controlpp;
	protected IObservadorPP obspp;
	
	///////////////////////////////////////////////////////
	/**
	 */
	public EjecutorPP(TablaSimbolos tabSimbBase, NUnidad unidadActual)
	{
		super(tabSimbBase, unidadActual);
		this.controlpp = new ControlPP();
	}

	//////////////////////////////////////////////////////
	/**
	 * Retorna true.
	 */
	public boolean esPasoAPaso()
	{
		return true;
	}

	//////////////////////////////////////////////////////
	public void ponObservadorPP(IObservadorPP obspp)
	{
		this.obspp = obspp;
	}

	//////////////////////////////////////////////////////
	public void ponSenalPP(int senal)
	throws InterruptedException
	{
		controlpp.ponSenal(senal);
	}

	//////////////////////////////////////////////////////
	/**
	 * @throws UnsupportedOperationException
	 */
	public void resume()
	{
		controlpp.setActive(false);
	}

	//////////////////////////////////////////////////////////////////////////
	/**
	 * Sobreescrito para desactivar el control paso-a-paso.
	 */
	protected EjecucionVisitanteException _crearEjecucionException(IUbicable u, String s)
	{
		controlpp.setActive(false);
		return super._crearEjecucionException(u, s);
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Sobreescrito para desactivar el control paso-a-paso.
	 */
	public synchronized void terminarExternamente()
	{
		controlpp.setActive(false);
		super.terminarExternamente();
		if ( obspp != null )
			obspp.end();
	}


	//////////////////////////////////////////////////
	protected int _obtSenalPP(IUbicable u)
	throws VisitanteException
	{
		try
		{
			return controlpp.obtSenal();
		}
		catch(InterruptedException ex)
		{
			throw new TerminacionExternaException(u, pilaEjec);
		}
	}
	
	////////////////////////////////////////////////////////////
	/**
	 * Llama super._enter(n), pide una señal de control de
	 * seguimiento paso-a-paso, e notifica al observador.
	 */
	protected void _enter(INodo n)
	throws VisitanteException
	{
		super._enter(n);
		int senal = _obtSenalPP(n);
		if ( obspp != null )
		{
			String src = unidadActual.getSourceCode();
			obspp.enter(n, tabSimb, src);
		}				
	}
	
	////////////////////////////////////////////////////////////
	/**
	 * Llama super._exit(n), pide una señal de control de
	 * seguimiento paso-a-paso, e notifica al observador.
	 */
	protected void _exit(INodo n)
	throws VisitanteException
	{
		super._exit(n);
		int senal = _obtSenalPP(n);
		if ( obspp != null )
		{
			String src = unidadActual.getSourceCode();
			obspp.exit(n, tabSimb, src);
		}				
	}
	
	///////////////////////////////////////////////////////////////////
	protected void _visitarCondicionEnEspecificacion(NEspecificacion n, int contexto)
	throws VisitanteException
	{
		_chequearTerminacionExterna(n);
		super._visitarCondicionEnEspecificacion(n, contexto);
	}
	
	//////////////////////////////////////////////////////////////////////
	protected void _pushEvent()
	throws VisitanteException
	{
		if ( obspp != null )
		{
			int senal = _obtSenalPP(unidadActual);
			String src = unidadActual.getSourceCode();
			obspp.push(unidadActual, tabSimb, src);
		}				
	}
	
	//////////////////////////////////////////////////////////////////////
	protected void _popEvent()
	throws VisitanteException
	{
		if ( obspp != null )
		{
			int senal = _obtSenalPP(unidadActual);
			String src = unidadActual.getSourceCode();
			obspp.pop(unidadActual, tabSimb, src);
		}				
	}

}
