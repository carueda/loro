package loroedi;

import loroedi.help.HelpManager;
import loroedi.laf.LookAndFeel;
import loro.*;
import util.editor.*;
import loroedi.EDI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;

import java.util.*;
import javax.swing.*;

/////////////////////////////////////////////////////////////////////
/**
 * VIEJO Entorno de desarrollo Integrado para Loro.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class LoroEDI
{
	static final String prefix_title = "Simple LoroEDI";

	/**
	 * El controlador principal.
	 */
	LoroControl loroControl;

	//////////////////////////////////////////////////////////////////////////////
	/**
	 */
	public LoroEDI(JFrame frame, LoroControl loroControl)
	{
		super();
		this.loroControl = loroControl;
		
		EDI edi = new EDI(prefix_title, frame, loroControl);

		edi.display();
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Programa principal.
	 */
	public static void main(String args[])
	{
		if ( args.length > 0 )
		{
			if ( args[0].equals("-version") )
			{
				System.out.println(Info.obtTituloEDI());
			}
			else // ( args[0].equals("-ayuda") o lo que sea:
			{
				System.out.println(obtUso());
			}
			System.exit(0);
		}

		try
		{
			JFrame frame = new JFrame();
			iniciar(frame);
			
			// Prepare L&F:
			LookAndFeel.setLookAndFeel();

			// Cree controlador central y lance el entorno de desarrollo:
			LoroEDI loroEDI = new LoroEDI(frame, new LoroControl());

			// despache posible primera ejecucion de la version actual del sistema:
			loroEDI.despacharPrimeraVez();
		}
		catch (Exception ex)
		{
			javax.swing.JOptionPane.showOptionDialog(
				null,
				ex.getMessage(),
				"Error de inicio",
				javax.swing.JOptionPane.DEFAULT_OPTION,
				javax.swing.JOptionPane.ERROR_MESSAGE,
				null,
				null,
				null
			);

			System.exit(1);
		}

	}

	////////////////////////////////////////////////////////////
	public javax.swing.Icon obtImagenAPropositoDe()
	{
		return Util.getIcon("img/splash.gif");
	}
	////////////////////////////////////////////////////////////
	/** Implementación para Editable. */
	public String obtMensajeAPropositoDe()
	{
		return
			Info.obtNombre()+ "\n" +
			"Versión " +Info.obtVersion()+ "\n" +
			" \n" +
			"Un Sistema de Programación Didáctico\n"+
			" \n" +
			"http://loro.sf.net\n" +
			" \n" +
			"Copyright© 1999-2002 Carlos Rueda\n" +
			"Universidad Autónoma de Manizales\n" +
			"Manizales - Colombia\n" +
			" \n"+
			"Este programa es software libre y usted puede\n"+
			"redistribuirlo si lo desea. Se ofrece con la\n"+
			"esperanza que sea útil, pero sin ningún tipo\n"+
			"de garantía. Por favor, lea la licencia de uso.\n"+
			" \n"
		;
	}

	/** Implementación para Editable. */
	public String obtTitulo()
	{
		return Info.obtNombre();
	}
	/////////////////////////////////////////////////////////////////
	static String obtUso()
	{
		return
Info.obtTituloEDI()+ "\n" +
"USO:\n" +
"   loroedi\n" +
"       Lanza el entorno de desarrollo.\n"+
"   loroedi -ayuda\n"+
"       Despliega esta ayuda y termina.\n" +
"   loroedi -version\n"+
"       Muestra version del sistema y termina.\n"
		;
	}


	/////////////////////////////////////////////////////////////////
	/**
	 * Pone nombre para el archivo en edición.
	 * Se actualiza preferencia "ultimo archivo editado".
	 *
	 * @param nomArchivo El nombre a poner.
	 */
	public void ponNombreArchivo(String nomArchivo)
	{
		Preferencias.ponPreferencia(Preferencias.RECENT, nomArchivo);
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * En caso de ser la primera ejecución de la versión actual,
	 * realiza las siguientes operaciones:
	 *
	 * <ul>
	 *	<li> Verifica el núcleo de Loro.
	 *	<li> Verifica la configuración dada al núcleo.
	 *	<li> Genera documentación de unidades de apoyo
	 *		  en LoroPreferencias.DOC_DIR.
	 *	<li> Abre la ventana de ayuda general.
	 *	<li> Verifica configuración dada al núcleo de Loro.
	 * </ul>
	 *
	 * @throws Exception   Si hay algun problema.
	 */
	private void despacharPrimeraVez()
	throws Exception
	{
		if ( Configuracion.getVC() == 0 )  // primera ejecución?
		{
			// Verifique el núcleo Loro: 
			Loro.verificarNucleo();
	
			// genere documentacion de unidades de apoyo:
			String docDir = Preferencias.obtPreferencia(Preferencias.DOC_DIR);
			loroControl.documentarUnidadesDeApoyo(docDir);
			
			// Abra la ventana de ayuda:
			loroedi.help.HelpManager.displayHelp();
			
			loroControl.verificarConfiguracionNucleo(false);
		}
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Ejecuta las operaciones finales para la terminación normal de
	 * LoroEDI.
	 *
	 * <ul>
	 *  <li> Almacena las preferencias LoroEDI.
	 *  <li> Almacena la configuración LoroEDI.
	 *  <li> Cierra el núcleo.
	 * </ul>
	 *
	 * @throws Throwable Si se presenta algún problema de finalización.
	 */
	public static void finalizar()
	throws Throwable
	{
		Preferencias.store();
		Configuracion.store();
		Loro.cerrar();
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Ejecuta las operaciones iniciales para que LoroEDI
	 * pueda funcionar normalmente.
	 *
	 * Los pasos realizados son:
	 * <ul>
	 *  <li> Muestra ventana de presentación (splash)
	 *  <li> Carga configuración LoroEDI.
	 *  <li> Carga preferencias LoroEDI.
	 *  <li> Configura el núcleo Loro.
	 * </ul>
	 *
	 * @throws Exception Si se presenta algún problema de inicio.
	 */
	public static void iniciar(JFrame frame)
	throws Exception
	{
		// Presentación:
		Splash.showSplash(frame);

		// Cargue la configuración:
		Configuracion.load();

		// Cargue preferencias:
		Preferencias.load();

		// prepare valores de configuración para Loro:
		String ext_dir = Configuracion.getProperty(Configuracion.DIR)+ "/lib/ext/";
		String paths_dir = Configuracion.getProperty(Configuracion.DIR)+ "/lib/prs/";

		// Inicie sistema Loro:
		Loro.configurar(ext_dir, paths_dir);
		
		String oro_dir = Preferencias.obtPreferencia(Preferencias.ORO_DIR);
		ICompilador compilador = Loro.obtCompilador();
		compilador.ponDirectorioDestino(oro_dir);

		// provisional
		System.setErr(System.out);
	}
}