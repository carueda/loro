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
			"A propósito de...",
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
			"Loro - Sistema Didáctico de Programación\n"+
			" \n" +
			Info.obtNombre()+ " " +Info.obtVersion()+ " (" +Info.obtBuild()+ ")\n" +
			Loro.obtNombre()+ " " +Loro.obtVersion()+ " (" +Loro.obtBuild()+ ")\n" +
			" \n" +
			"http://loro.sf.net\n" +
			" \n"+
			"Este programa es software libre y puede ser\n"+
			"redistribuido si se desea. Se ofrece con la\n"+
			"esperanza que sea útil, pero sin ningún tipo\n"+
			"de garantía. Por favor, lea la licencia de uso.\n"+
			" \n" +
			"Copyright© 1999-2003 Carlos A. Rueda\n" +
			"Universidad Autónoma de Manizales\n" +
			"Manizales - Colombia\n" +
			" \n"
		;
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Pone nombre para el archivo en edición.
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
				"Error al generar documentación: " +ex.getMessage()
			);
		}
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
	 * Efectúa la ejecución de un objeto compilado
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
	 * Verifica la configuración del núcleo.
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
					"Verificación completada:\n"+
					  "  Loro no encontró ningún problema de compatibilidad\n"+
					  "  con objetos compilados existentes.",
					"Verificación de compatibilidad de versiones...",
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
"Loro acaba de realizar una revisión de las unidades y paquetes compilados existentes.\n"
		);
		
		mensaje.append(
"Loro encontró incompatibilidad en " +oros.size()+ " unidad(es) y " +jars.size()+ " paquete(s);\n"+
"por lo tanto, no podrá acceder correctamente a tales elementos.\n"
		);
		
		mensaje.append(
"Esto es consecuencia de ciertos cambios importantes al interior del sistema.\n"+
"Loro espera eliminar los problemas de compatibilidad para una próxima versión.\n"
		);
		
		mensaje.append(
"\n"+
"Solución:\n" 
		);
		
		if ( oros_size > 0 )
		{
			mensaje.append(
"Bastará con recompilar los fuentes correspondientes para actualizar las unidades indicadas.\n"
			);
		}
		
		if ( jars.size() > 0 )
		{
			mensaje.append(
"En el caso de paquetes de extensión, deberá consultarse con sus proveedores acerca de\n"+
"actualizaciones que sean compatibles con esta versión de Loro.\n"
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
					que = "Especificación";
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
"Para más información, por favor consulta la sección sobre cambios en esta versión\n"+
"que se incluye en la documentación de ayuda general del entorno.\n"
		);
		
		mensaje.append(
"\n"+
"NOTA: UTILIZA EL MENU DE AYUDA SI DESEAS VOLVER A REPETIR ESTA VERIFICACION.\n"
		);
		
		javax.swing.JOptionPane.showOptionDialog(
			null,
			mensaje,
			"Verificación de compatibilidad de versiones...",
			javax.swing.JOptionPane.DEFAULT_OPTION,
			javax.swing.JOptionPane.WARNING_MESSAGE,
			null,
			null,
			null
		);
	}
	
}