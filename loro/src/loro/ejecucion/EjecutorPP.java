package loro.ejecucion;

import loro.IObservadorPP;
import loro.Rango;
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

	//////////////////////////////////////////////////
	protected void _chequearTerminacionExterna(IUbicable u)
	throws VisitanteException
	{
		if ( terminadoExternamente )
			throw new TerminacionExternaException(u, pilaEjec);

		try
		{
			int senal = controlpp.obtSenal();
			if ( obspp != null )
			{
				System.out.println("unidadActual=" +unidadActual);
				String src = unidadActual.getSourceCode();
				if ( src != null )
				{
					System.out.println("{" +src+ "}");
					Rango r = u.obtRango();
					try
					{
						System.out.println("[" +src.substring(r.obtPosIni(), r.obtPosFin())+ "]");
					}
					catch(StringIndexOutOfBoundsException ex)
					{
						ex.printStackTrace();
					}
				}
				
				obspp.ver(u);
			}
		}
		catch(InterruptedException ex)
		{
			throw new TerminacionExternaException(u, pilaEjec);
		}
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
}
