package loroedi;

import loroedi.Preferencias;
import loro.*;
import util.consola.Consola;

import java.io.*;
import java.util.*;


/////////////////////////////////////////////////////////////////////
/**
 * El controlador principal del sistema LoroEDI.
 *
 * @author Carlos Rueda
 * @version 2002-01-22
 */
public class LoroControl
{
	/** El documentador de Loro. */
	IDocumentador documentador;

	/** El compilador de Loro. */
	ICompilador compilador;
	
	// para mantener informacion notificada por
	// ponDirectorioFuentes, pero NO SE HACE NADA AHORA
	// CON ESTA VARIABLE!
	// antes esto se trasladaba al chequeador, pero ahora
	// el nucleo Loro no tiene nada que ver con esto:
	String dirFuentes = "";

	//////////////////////////////////////////////////////////////////
	/**
	 * Construye el control central de LoroEDI.
	 */
	public LoroControl()
	{
		compilador = Loro.obtCompilador();
		documentador = Loro.obtDocumentador();
	}

	///////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la lista de unidades compiladas que sean ejecutables
	 * directamente.
	 */
	public java.util.Enumeration obtCompilados()
	{
		IUnidad[] unidades = compilador.obtAlgoritmos();
		java.util.Vector v = new java.util.Vector();
		for ( int i = 0; i < unidades.length; i++ )
		{
			IUnidad alg = unidades[i];
			if ( Loro.esEjecutableDirectamente(alg) )
			{
				v.addElement(alg);
			}
		}

		return v.elements();
	}
	////////////////////////////////////////////////////////////////////////
	/**
	 * Pone el directorio de ubicacion de fuentes.
	 *
	 * PENDIENTE:
	 * Ademas de actualizar una variable interna, no se hace nada.
	 */
	public void ponDirectorioFuentes(String dir)
	{
		dirFuentes = dir;
	}

	/////////////////////////////////////////////////////////////////
	public void mostrarAPropositoDe()
	{
		javax.swing.JOptionPane.showMessageDialog(
			null,
			obtMensajeAPropositoDe(),
			"A prop�sito de...",
			javax.swing.JOptionPane.INFORMATION_MESSAGE,
			Util.getIcon("img/splash.jpg")
		);
	}

	////////////////////////////////////////////////////////////
	/**
	 * Obtiene mensaje de descripcion del sistema.
	 */
	public String obtMensajeAPropositoDe()
	{
		return
			"Loro - Sistema Did�ctico de Programaci�n\n"+
			" \n" +
			Info.obtNombre()+ " " +Info.obtVersion()+ " (" +Info.obtBuild()+ ")\n" +
			Loro.obtNombre()+ " " +Loro.obtVersion()+ " (" +Loro.obtBuild()+ ")\n" +
			" \n" +
			"http://loro.sf.net\n" +
			" \n"+
			"Este programa es software libre y puede ser\n"+
			"redistribuido si se desea. Se ofrece con la\n"+
			"esperanza que sea �til, pero sin ning�n tipo\n"+
			"de garant�a. Por favor, lea la licencia de uso.\n"+
			" \n" +
			"Copyright� 1999-2003 Carlos A. Rueda\n" +
			"Universidad Aut�noma de Manizales\n" +
			"Manizales - Colombia\n" +
			" \n"
		;
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Pone nombre para el archivo en edici�n.
	 * Se actualiza preferencia "ultimo archivo editado".
	 *
	 * @param nomArchivo El nombre a poner.
	 */
	public void ponNombreArchivo(String nombreArchivo)
	{
		// actualice preferencia:
		Preferencias.ponPreferencia(Preferencias.RECENT, nombreArchivo);

		// avise al compilador (nombre simple):
		int i = nombreArchivo.lastIndexOf(java.io.File.separatorChar);
		String n;
		if ( i >= 0 )
			n = nombreArchivo.substring(i + 1);
		else
			n = nombreArchivo;

		compilador.ponNombreArchivo(n);
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Invocado cuando el usuario ha decidido salir del editor.
	 * Se invocan LoroEDI.finalizar() y System.exit(0).
	 */
	public void salir()
	{
		try
		{
			LoroEDI.finalizar();
		}
		catch (Throwable ex)
		{
			javax.swing.JOptionPane.showOptionDialog(
				null,
				ex.getMessage(),
				"Error de terminacion",
				javax.swing.JOptionPane.DEFAULT_OPTION,
				javax.swing.JOptionPane.ERROR_MESSAGE,
				null,
				null,
				null
			);
		}
		System.exit(0);
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
		catch ( LoroException ex )
		{
			loro.Loro.log("LoroEDI: "+
				"Error al generar documentaci�n: " +ex.getMessage()
			);
		}
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
	 * Efect�a la ejecuci�n de un objeto compilado
	 * en una consola.
	 */
	public void ejecutarConConsola(IUnidad u, Object[] args)
	{
		if ( u != null )
		{
			HiloAlgoritmo ha = new HiloAlgoritmo(u, args);
			ha.start();
		}
	}

	///////////////////////////////////////////////////////////////////////
	/**
	 * Dice si una unidad es ejecutable.
	 *
	 * @param u La unidad a chequear.
	 */
	public boolean esEjecutable(IUnidad u)
	{
		return Loro.esEjecutableDirectamente(u);
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Genera la documentacion para las unidades de apoyo.
	 */
	void documentarUnidadesDeApoyo(String docDir)
	throws LoroException
	{
		documentador.documentarUnidadesDeApoyo(docDir);
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Verifica la configuraci�n del n�cleo.
	 */
	void verificarConfiguracionNucleo(boolean mostrarMensajeExito)
	{
		List jars = new ArrayList();
		List oros = new ArrayList();
		Loro.verificarConfiguracion(jars, oros);

		int oros_size = oros.size();
		int jars_size = jars.size();
		
		if ( jars_size == 0 && oros_size == 0 )
		{
			if ( mostrarMensajeExito )
			{
				javax.swing.JOptionPane.showOptionDialog(
					null,
					"Verificaci�n completada:\n"+
					  "  Loro no encontr� ning�n problema de compatibilidad\n"+
					  "  con objetos compilados existentes.",
					"Verificaci�n de compatibilidad de versiones...",
					javax.swing.JOptionPane.DEFAULT_OPTION,
					javax.swing.JOptionPane.WARNING_MESSAGE,
					null,
					null,
					null
				);
			}
			return;
		}
		
		StringBuffer mensaje = new StringBuffer();
		
		mensaje.append(
"Con el fin de detectar posibles problemas de compatibilidad con versiones anteriores,\n"+
"Loro acaba de realizar una revisi�n de las unidades y paquetes compilados existentes.\n"
		);
		
		mensaje.append(
"Loro encontr� incompatibilidad en " +oros.size()+ " unidad(es) y " +jars.size()+ " paquete(s);\n"+
"por lo tanto, no podr� acceder correctamente a tales elementos.\n"
		);
		
		mensaje.append(
"Esto es consecuencia de ciertos cambios importantes al interior del sistema.\n"+
"Loro espera eliminar los problemas de compatibilidad para una pr�xima versi�n.\n"
		);
		
		mensaje.append(
"\n"+
"Soluci�n:\n" 
		);
		
		if ( oros_size > 0 )
		{
			mensaje.append(
"Bastar� con recompilar los fuentes correspondientes para actualizar las unidades indicadas.\n"
			);
		}
		
		if ( jars.size() > 0 )
		{
			mensaje.append(
"En el caso de paquetes de extensi�n, deber� consultarse con sus proveedores acerca de\n"+
"actualizaciones que sean compatibles con esta versi�n de Loro.\n"
			);
		}
		
		mensaje.append("\n");
		
		if ( oros_size > 0 )
		{
			// numero de unidades a mostrar:
			int num_oros = Math.min(5, oros_size);
			if ( num_oros == oros_size )
			{
				mensaje.append(
"Las unidades incompatibles son:\n"
				);
			}
			else
			{
				mensaje.append(
"Algunas de las unidades incompatibles son:\n"
				);
			}
			for ( int i = 0; i < num_oros; i++ )
			{
				String name = (String) oros.get(i);
				name = new File(name).getName();
				String que = null;
				String lower = name.toLowerCase();
				if ( lower.endsWith(".e.oro") )
					que = "Especificaci�n";
				else if ( lower.endsWith(".a.oro") )
					que = "Algoritmo";
				else if ( lower.endsWith(".c.oro") )
					que = "Clase";
				else if ( lower.endsWith(".i.oro") )
					que = "Interface";
				else if ( lower.endsWith(".o.oro") )
					que = "Objeto";
				else
					que = "???";
					
				mensaje.append("     - " 
					+que+ ":  " +name.substring(0, name.length()-6)+ "\n"
				);
			}
		}
		
		if ( jars_size > 0 )
		{
			mensaje.append(
"Los paquetes incompatibles son:\n"
			);
			for ( Iterator it = jars.iterator(); it.hasNext(); )
			{
				 String name = (String) it.next();
				 name = new File(name).getName();
				 mensaje.append("     - " +name+ "\n");
			}
		}

		mensaje.append(
"\n"+
"Para m�s informaci�n, por favor consulta la secci�n sobre cambios en esta versi�n\n"+
"que se incluye en la documentaci�n de ayuda general del entorno.\n"
		);
		
		mensaje.append(
"\n"+
"NOTA: UTILIZA EL MENU DE AYUDA SI DESEAS VOLVER A REPETIR ESTA VERIFICACION.\n"
		);
		
		javax.swing.JOptionPane.showOptionDialog(
			null,
			mensaje,
			"Verificaci�n de compatibilidad de versiones...",
			javax.swing.JOptionPane.DEFAULT_OPTION,
			javax.swing.JOptionPane.WARNING_MESSAGE,
			null,
			null,
			null
		);
	}
	
}