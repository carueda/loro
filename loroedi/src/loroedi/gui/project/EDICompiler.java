package loroedi.gui.project;

import loroedi.Preferencias;
import loro.*;

//////////////////////////////////////////////////
/**
 * Ayudante para compilación/documentación.
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
	 * Efectúa la compilación de un fuente.
	 *
	 * @param fuente El programa fuente.
	 *
	 * @return El resultado de la compilación.
	 *
	 * @exception CompilacionException Si se presenta algún error.
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
	 * Efectúa la compilación de un fuente en espera de la definición de
	 * una especificación.
	 *
	 * @param pkgname   Nombre esperado de paquete (en estilo "::").
	 *                  Si es vacío, se toma como el paquete anónimo.
	 * @param unitname  Nombre esperado de la unidad.
	 * @param fuente    El código fuente.
	 *
	 * @return El resultado de la compilación.
	 *
	 * @exception CompilacionException Si se presenta algún error.
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
	 * Efectúa la compilación de un fuente en espera de la definición de
	 * un algoritmo.
	 *
	 * @param pkgname   Nombre esperado de paquete (en estilo "::").
	 *                  Si es vacío, se toma como el paquete anónimo.
	 * @param unitname  Nombre esperado de la unidad.
	 * @param specname  Nombre esperado de la especificación para el algoritmo.
	 * @param fuente    El código fuente.
	 *
	 * @return El resultado de la compilación.
	 *
	 * @exception CompilacionException Si se presenta algún error.
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
	 * Efectúa la compilación de un fuente en espera de la definición de
	 * una clase.
	 *
	 * @param pkgname   Nombre esperado de paquete (en estilo "::").
	 *                  Si es vacío, se toma como el paquete anónimo.
	 * @param unitname  Nombre esperado de la unidad.
	 * @param fuente    El código fuente.
	 *
	 * @return El resultado de la compilación.
	 *
	 * @exception CompilacionException Si se presenta algún error.
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
	 * Efectúa la derivación (fase sintáctica) de un fuente.
	 *
	 * @param fuente El programa fuente.
	 *
	 * @return El resultado de la derivación.
	 *
	 * @exception DerivacionException Si se presenta algún error.
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
