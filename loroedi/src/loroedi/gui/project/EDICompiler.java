package loroedi.gui.project;

import loroedi.Preferencias;
import loro.*;

//////////////////////////////////////////////////
/**
 * Ayudante para compilaci�n/documentaci�n.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
class EDICompiler
{
	/** El documentador de Loro. */
	IDocumentador documentador;

	/** El compilador de Loro. */
	ICompilador compilador;
	
	//////////////////////////////////////////////////////////////////
	/**
	 * Construye un ayudante.
	 */
	public EDICompiler()
	{
		compilador = Loro.obtCompilador();
		documentador = Loro.obtDocumentador();
	}

	///////////////////////////////////////////////////////////////////////
	/**
	 * Efect�a la compilaci�n de un fuente.
	 *
	 * @param fuente El programa fuente.
	 *
	 * @return El resultado de la compilaci�n.
	 *
	 * @exception CompilacionException Si se presenta alg�n error.
	 */
	public IFuente compilar(String fuente)
	throws CompilacionException
	{
		compilador.ponTextoFuente(fuente);
		IFuente comp_fuente = compilador.compilarFuente();
		IUnidad[] unidades = comp_fuente.obtUnidades();

		// genere documentacion de una vez:
		_generarDocumentacion(unidades);
		
		return comp_fuente;
	}

	///////////////////////////////////////////////////////////////////////
	/**
	 * Efect�a la compilaci�n de un fuente en espera de la definici�n de
	 * una especificaci�n.
	 *
	 * @param pkgname   Nombre esperado de paquete (en estilo "::").
	 *                  Si es vac�o, se toma como el paquete an�nimo.
	 * @param unitname  Nombre esperado de la unidad.
	 * @param fuente    El c�digo fuente.
	 *
	 * @return El resultado de la compilaci�n.
	 *
	 * @exception CompilacionException Si se presenta alg�n error.
	 */
	public IUnidad compileSpecification(
		String pkgname,
		String unitname,
		String fuente
	)
	throws CompilacionException
	{
		compilador.ponTextoFuente(fuente);
		IUnidad u = compilador.compileSpecificaction(pkgname, unitname);
		return u;
	}

	///////////////////////////////////////////////////////////////////////
	/**
	 * Efect�a la compilaci�n de un fuente en espera de la definici�n de
	 * un algoritmo.
	 *
	 * @param pkgname   Nombre esperado de paquete (en estilo "::").
	 *                  Si es vac�o, se toma como el paquete an�nimo.
	 * @param unitname  Nombre esperado de la unidad.
	 * @param specname  Nombre esperado de la especificaci�n para el algoritmo.
	 * @param fuente    El c�digo fuente.
	 *
	 * @return El resultado de la compilaci�n.
	 *
	 * @exception CompilacionException Si se presenta alg�n error.
	 */
	public IUnidad compileAlgorithm(
		String pkgname,
		String unitname,
		String specname,
		String fuente
	)
	throws CompilacionException
	{
		compilador.ponTextoFuente(fuente);
		IUnidad u = compilador.compileAlgorithm(pkgname, unitname, specname);
		return u;
	}

	///////////////////////////////////////////////////////////////////////
	/**
	 * Efect�a la compilaci�n de un fuente en espera de la definici�n de
	 * una clase.
	 *
	 * @param pkgname   Nombre esperado de paquete (en estilo "::").
	 *                  Si es vac�o, se toma como el paquete an�nimo.
	 * @param unitname  Nombre esperado de la unidad.
	 * @param fuente    El c�digo fuente.
	 *
	 * @return El resultado de la compilaci�n.
	 *
	 * @exception CompilacionException Si se presenta alg�n error.
	 */
	public IUnidad compileClass(
		String pkgname,
		String unitname,
		String fuente
	)
	throws CompilacionException
	{
		compilador.ponTextoFuente(fuente);
		IUnidad u = compilador.compileClass(pkgname, unitname);
		return u;
	}

	///////////////////////////////////////////////////////////////////////
	/**
	 * Efect�a la derivaci�n (fase sint�ctica) de un fuente.
	 *
	 * @param fuente El programa fuente.
	 *
	 * @return El resultado de la derivaci�n.
	 *
	 * @exception DerivacionException Si se presenta alg�n error.
	 */
	public IUnidad[] derivar(String fuente)
	throws CompilacionException
	{
		compilador.ponTextoFuente(fuente);
		IUnidad[] unidades = compilador.derivarFuente();
		return unidades;
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Invocado desde compilar(). Genera documentacion en 
	 * Preferencias.obtPreferencia(Preferencias.DOC_DIR).
	 */
	private void _generarDocumentacion(IUnidad[] unidades)
	{
		try
		{
			String directory = Preferencias.obtPreferencia(Preferencias.DOC_DIR);
			for (int i = 0; i < unidades.length; i++)
			{
				documentador.documentar(unidades[i], directory);
			}
		}
		catch ( LoroException ex ) {
			Loro.log("LoroEDI: Error generating documentation: " +ex.getMessage());
		}
	}

}
