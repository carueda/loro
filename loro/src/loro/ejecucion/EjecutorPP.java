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
	}


	//////////////////////////////////////////////////
	protected void _chequearTerminacionExterna(IUbicable u)
	throws VisitanteException
	{
		super._chequearTerminacionExterna(u);
		try
		{
			int senal = controlpp.obtSenal();
		}
		catch(InterruptedException ex)
		{
			throw new TerminacionExternaException(u, pilaEjec);
		}
		
		_notifyObserver(u);
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
	{
		System.out.println("EjecutorPP.push: unidadActual=" +unidadActual);
		System.out.println("EjecutorPP.push: tabSimb:\n" +tabSimb);
		_notifyObserver(unidadActual);
	}
	
	//////////////////////////////////////////////////////////////////////
	protected void _popEvent()
	{
		System.out.println("EjecutorPP.pop: unidadActual=" +unidadActual);
		System.out.println("EjecutorPP.pop: tabSimb:\n" +tabSimb);
		_notifyObserver(unidadActual);
	}
	
	//////////////////////////////////////////////////
	protected void _notifyObserver(IUbicable u)
	{
		if ( obspp == null )
			return;
		
		if ( u instanceof NUnidad )
			return;
		
		String src = unidadActual.getSourceCode();
		obspp.ver(u, tabSimb, src);				
	}

}
