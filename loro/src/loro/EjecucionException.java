package loro;

import loro.ejecucion.PilaEjecucion;

//////////////////////////////////////////////////////////////
/**
 * Clase para las excepciones generadas durante la ejecución
 * de un programa en Loro. 
 *
 * Esto incluye cualquier error en la lógica de la ejecución, 
 * normalmente inesperados (como por ejemplo, subindizar un
 * arreglo por fuera de su rango).
 *
 * También se considera como excepción la terminación prematura de
 * un programa. En este caso se tienen dos modalidades (mutuamente 
 * exclusivas):
 *
 * <ul>
 *	<li><b>Externa</b>: 
 *		Es la terminación producida por una interrupción al hilo en 
 *		que se lleva a cabo la ejecución del programa.
 *	</li>
 *
 *	<li><b>Interna</b>:
 *		Es la terminación producida por la ejecución de una invocación
 *		al proceso incorporado terminarEjecucion().
 *	</li>
 * </ul>
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class EjecucionException extends AnalisisException {
	/** Codigo de la terminacion interna */
	int codigoTerminacionInterna;
	/** Es terminacion externa? */
	boolean esTerminacionExterna;
	/** Es terminacion interna? */
	boolean esTerminacionInterna;
	/** La pila de ambientes cuando se genera la exception. */
	PilaEjecucion pilaEjec;

	///////////////////////////////////////////
	/**
	 * Crea una excepcion de ejecucion por terminacion externa.
	 */
	public EjecucionException(PilaEjecucion pilaEjec, Rango rango) {
		super(rango, Loro.Str.get("Extern.termination"));
		this.pilaEjec = pilaEjec;
		this.esTerminacionExterna = true;
	}

	//////////////////////////////////////////////////////////
	/**
	 * Crea una excepcion de ejecucion por terminacion interna.
	 */
	public EjecucionException(
		PilaEjecucion pilaEjec,
		Rango rango, int codigoTerminacionInterna
	) {
		super(rango, Loro.Str.get("Extern.termination"));
		this.pilaEjec = pilaEjec;
		this.esTerminacionInterna = true;
		this.codigoTerminacionInterna = codigoTerminacionInterna;
		this.esTerminacionExterna = false;
	}

	///////////////////////////////////////////
	/**
	 * Crea una excepcion de ejecucion.
	 */
	public EjecucionException(PilaEjecucion pilaEjec, Rango rango, String m) {
		super(rango, m);
		this.pilaEjec = pilaEjec;
		this.esTerminacionExterna = false;
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Es terminacion externa?
	 */
	public boolean esTerminacionExterna() {
		return esTerminacionExterna;
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Es terminacion interna?
	 */
	public boolean esTerminacionInterna() {
		return esTerminacionInterna;
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene el codigo de la terminacion interna.
	 * Asegurese antes que efectivamente se trate de una
	 * terminacion interna.
	 */
	public int obtCodigoTerminacionInterna() {
		return codigoTerminacionInterna;
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Muestra el estado de la pila de ejecución de Loro en el momento
	 * que se produjo esta excepción.
	 */
	public void printStackTrace() {
		if ( pilaEjec != null ) {
			pilaEjec.mostrar();
		}
	}

	/////////////////////////////////////////////////////////////////////
	public void printStackTrace(java.io.PrintWriter pw) {
		if ( pilaEjec != null ) {
			pilaEjec.mostrar(pw);
		}
	}
}
