package loro.ejecucion;

import loro.Loro.Str;
import loro.arbol.*;
import loro.IUbicable;

import java.io.PrintWriter;


/////////////////////////////////////////////////////////////////////
/**
 * Exception que indica una terminacion sea interna o externa.
 * @version $Id$
 */
class TerminacionException extends EjecucionVisitanteException {
	boolean esInterna;
	int codigoTerminacionInterna;

	////////////////////////////////////////////////////////////////
	/**
	 * Dice si esta terminacion es interna.
	 */
	public boolean esInterna() {
		return esInterna;
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el codigo de terminacion, que tiene sentido si
	 * se trata efectivamente de una terminacion interna.
	 */
	public int obtCodigoTerminacionInterna() {
		return codigoTerminacionInterna;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Crea una excepcion de terminacion externa.
	 */
	public TerminacionException(IUbicable u, PilaEjecucion pilaEjec) {
		super(u, pilaEjec, Str.get("extern.termination"));
		this.esInterna = false;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Crea una excepcion de terminacion interna.
	 */
	public TerminacionException(IUbicable u, PilaEjecucion pilaEjec, int codigoTerminacionInterna) {
		super(u, pilaEjec, Str.get("intern.termination"));
		this.esInterna = true;
		this.codigoTerminacionInterna = codigoTerminacionInterna;
	}
}