package loro.ejecucion;

import loro.IObservadorPP;
import loro.arbol.*;
import loro.visitante.VisitanteException;
import loro.tabsimb.*;
import loro.util.UtilValor;

///////////////////////////////////////////////////////////
/**
 * Un ejecutor que paso-a-paso notifica a un IObservadorPP.
 */
public class EjecutorPP extends EjecutorTerminable 
{
	protected IObservadorPP obspp;
	
	///////////////////////////////////////////////////////
	/**
	 * Crea un ejecutor paso-a-paso.
	 * Normalmente se asociará un IObservadorPP al objeto creado,
	 * @see #ponObservadorPP
	 */
	public EjecutorPP(TablaSimbolos tabSimbBase, NUnidad unidadActual)
	{
		super(tabSimbBase, unidadActual);
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

	//////////////////////////////////////////////////////////////
	/**
	 * Sobreescrito para notificar al observador.
	 */
	public synchronized void terminarExternamente()
	{
		if ( obspp != null )
			obspp.end();
		super.terminarExternamente();
	}


	////////////////////////////////////////////////////////////
	/**
	 * Llama super._enter(n), pide una señal de control de
	 * seguimiento paso-a-paso, y notifica al observador.
	 */
	protected void _enter(INodo n)
	throws VisitanteException
	{
		super._enter(n);
		if ( obspp != null )
		{
			String src;
			if ( n instanceof NUnidad )
			{
				// n se convertirá eventualmente en la "unidadActual";
				// en particular, esto sucede con la visita a NAlgoritmo
				// en este momento [2003-02-13].
				src = ((NUnidad) n).getSourceCode();
			}
			else
				src = unidadActual.getSourceCode();
			
			try
			{
				obspp.enter(n, tabSimb, src);
			}
			catch(InterruptedException ex)
			{
				throw new TerminacionExternaException(n, pilaEjec);
			}
		}				
	}
	
	////////////////////////////////////////////////////////////
	/**
	 * Llama super._exit(n) y notifica al observador.
	 */
	protected void _exit(INodo n)
	throws VisitanteException
	{
		super._exit(n);
		if ( obspp != null )
		{
			String src = unidadActual.getSourceCode();
			String result = null;
			if ( n instanceof NExpresion )
			{
				NExpresion nexp = (NExpresion) n;
				result = retorno != null 
					? UtilValor.valorComillasDeExpresion(nexp.obtTipo(), retorno)
					: "?"
				;
			}
			try
			{
				obspp.exit(n, tabSimb, src, result); 
			}
			catch(InterruptedException ex)
			{
				throw new TerminacionExternaException(n, pilaEjec);
			}
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
			String src = unidadActual.getSourceCode();
			try
			{
				obspp.push(unidadActual, tabSimb, src);
			}
			catch(InterruptedException ex)
			{
				throw new TerminacionExternaException(unidadActual, pilaEjec);
			}
		}				
	}
	
	//////////////////////////////////////////////////////////////////////
	protected void _popEvent()
	throws VisitanteException
	{
		if ( obspp != null )
		{
			String src = unidadActual.getSourceCode();
			try
			{
				INodo curr_node = pilaEjec.peekNode();
				obspp.pop(unidadActual, curr_node, tabSimb, src);
			}
			catch(InterruptedException ex)
			{
				throw new TerminacionExternaException(unidadActual, pilaEjec);
			}
		}				
	}

}
