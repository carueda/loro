package loro.ejecucion;

import loro.arbol.*;

import java.io.PrintWriter;


/////////////////////////////////////////////////////////////////////
/**
 * Exception que indica una terminacion sea interna o externa.
 */
class TerminacionException extends EjecucionVisitanteException
{
	boolean esInterna;
	int codigoTerminacionInterna;



	////////////////////////////////////////////////////////////////
	/**
	 * Dice si esta terminacion es interna.
	 */
	public boolean esInterna()
	{
		return esInterna;
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el codigo de terminacion, que tiene sentido si
	 * se trata efectivamente de una terminacion interna.
	 */
	public int obtCodigoTerminacionInterna()
	{
		return codigoTerminacionInterna;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Crea una excepcion de terminacion externa.
	 */
	public TerminacionException(IUbicable u, PilaEjecucion pilaEjec)
	{
		super(u, pilaEjec, "Terminacion externa");
		this.esInterna = false;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Crea una excepcion de terminacion interna.
	 */
	public TerminacionException(IUbicable u, PilaEjecucion pilaEjec, int codigoTerminacionInterna)
	{
		super(u, pilaEjec, "Terminacion interna");
		this.esInterna = true;
		this.codigoTerminacionInterna = codigoTerminacionInterna;
	}
}