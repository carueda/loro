package loro.ejecucion;

import loro.arbol.*;
import loro.visitante.VisitanteException;
import loro.Rango;

import java.io.PrintWriter;

/////////////////////////////////////////////////////////////////////
/**
 * El tipo de exception que se lanza al presentarse algun error en 
 * tiempo de ejecucion.
 *
 * 2000-06-12 - inicio
 * 2001-09-18 - Revision si t != null en getMessage.
 * 2002-01-13 - manejo sobre un objeto Ubicable.
 */
public class EjecucionVisitanteException extends VisitanteException
{
	/** La pila de ambientes cuando se genera la exception. */
	PilaEjecucion pilaEjec;

	/////////////////////////////////////////////////////////////////////
	public void printStackTrace()
	{
		if ( pilaEjec != null )
		{
			pilaEjec.mostrar();
		}
	}
	/////////////////////////////////////////////////////////////////////
	public void printStackTrace(PrintWriter pw)
	{
		if ( pilaEjec != null )
		{
			pilaEjec.mostrar(pw);
		}
	}

	/** Ubicacion de este error. */
	IUbicable u;



	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene la pila de ejecucion.
	 */
	public PilaEjecucion obtPilaEjecucion()
	{
		return pilaEjec;
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene el rango de esta excepcion.
	 */
	public Rango obtRango()
	{
		return u != null ? u.obtRango() : null;
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 */
	public EjecucionVisitanteException(IUbicable u, PilaEjecucion pilaEjec, String s)
	{
		super(s);
		this.u = u;
		this.pilaEjec = pilaEjec;
	}
}