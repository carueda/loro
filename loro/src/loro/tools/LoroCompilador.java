package loro.tools;

import loro.*;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.util.Comparator;

/////////////////////////////////////////////////////////////////////
/**
 * Ofrece el servicio completo de compilaci�n de archivos fuentes.
 * 
 * compilar() compila una lista de archivos de acuerdo con par�metros
 * de trabajo.
 *
 * anticompilar() anti-compila una lista de archivos de acuerdo con par�metros
 * de trabajo.
 *
 * main() recibe par�metros de la l�nea de comandos e invoca compilar().
 * Pendiente opci�n para anticompilar (esta opci�n est� disponible a trav�s
 * de la ant-task).
 *
 * @version 2002-06-04
 * @author Carlos Rueda
 */
public class LoroCompilador
{
	////////////////////////////////////////////////////////////////
	/**
	 * Lee argumentos de la l�nea de comandos e invoca compilar().
	 * Este m�todo invoca System.exit() para terminar.
	 *
	 * La lista de nombres de los archivos fuentes es primero ordenada
	 * asumiendo que hay consistencia entre el nombre del archivo y la unidad contenida:
	 * ".e.loro" para especificaciones, ".a.loro" para algoritmos y
	 * ".c.loro" para clases; los archivos cuyo nombre no encajen en una de las 
	 * anteriores, se dejan para lo �ltimo.
	 *
	 * Este orden ayuda un poco a lograr compilaciones exitosas pero NO lo 
	 * garantiza. Este manejo es s�lo una aproximaci�n burda mientras se
	 * hace una implementaci�n inteligente de dependencias de compilaci�n.
	 */
	public static void main(String args[])
	{
		if ( args.length == 0 || args[0].equals("-ayuda") )
		{
			System.out.println(obtUso());
			return;
		}
		else if ( args[0].equals("-version") )
		{
			System.out.println(_titulo());
			return;
		}

		String ext_dir = "";
		String oro_dir = "."; // por defecto, el directorio actual

		boolean guardarCompilados = true;

		int arg = 0;
		while ( arg < args.length && args[arg].startsWith("-") )
		{
			if ( args[arg].equals("-d") )
			{
				if ( ++arg < args.length )
				{
					oro_dir = args[arg];
				}
				else
				{
					System.out.println(obtUso());
					return;
				}
			}
			else if ( args[arg].equals("-x") )
			{
				if ( ++arg < args.length )
				{
					ext_dir = args[arg];
				}
				else
				{
					System.out.println(obtUso());
					return;
				}
			}
			else if ( args[arg].equals("-Z") )
			{
				System.out.println(obtUsoAvanzado());
				return;
			}
			else if ( args[arg].equals("-ng") )
			{
				guardarCompilados = false;
			}
			else
			{
				System.out.println("Invalid option " +args[arg]);
				System.exit(1);
			}

			arg++;
		}

		// lista de archivos:
		List list = new ArrayList();
		for ( ; arg < args.length; arg++ )
		{
			String nombre = args[arg];
			list.add(nombre);
		}

		int status = 0;
		try
		{
			status = compilar(list, oro_dir, ext_dir, guardarCompilados);
		}
		catch (LoroException ex)
		{
			System.err.println(ex.getMessage());
			status = 2;
		}
		
		System.exit(status);
	}

	/////////////////////////////////////////////////////////////////
	public static String obtUso()
	{
		return
_titulo()+ "\n"+
"\n"+
"USO:\n" +
"\n"+
"   loroc [-d dir] fuentes ...\n" +
"      -d dir    Establece el directorio de destino para guardar\n"+
"                los archivos compilados. Por defecto, el directorio actual.\n"+
"\n"+
"   loroc -version\n"+
"                Muestra version del sistema y termina.\n"+
"\n"+
"   loroc -ayuda\n"+
"       Despliega esta ayuda y termina.\n"+
"\n"+
"   loroc -Z\n"+
"       Muestra formas de uso avanzadas y termina.\n"
		;
	}

	/////////////////////////////////////////////////////////////////
	static String obtUsoAvanzado()
	{
		return
_titulo()+ "\n"+
"\n"+
"Opcion avanzada:\n" +
"\n"+
"      -ng       Esta opcion indica no guardar archivos compilados.\n"+
"                Usar con cuidado ya que es posible obtener\n"+
"                problemas de compilacion relacionados con no\n"+
"                encontrar unidades referenciadas por otras\n"+
"                unidades.\n"
		;
	}

	////////////////////////////////////////////////////////////
	/**
	 * Obtiene el t�tulo del Compilador desde l�nea de comandos.
	 */
	static String _titulo()
	{
		return Loro.obtNombre()+ " Versi�n " +Loro.obtVersion()+
		" (" +Loro.obtBuild()+ ")" +
		"\n"+
		"Compilador desde l�nea de comandos";
	}


	////////////////////////////////////////////////////////////////
	/**
	 * Servicio "completo" para compilar una lista de archivos.
	 * Por "completo" se indica que hace toda la interacci�n necesaria
	 * con el n�cleo Loro para inicializarlo, compilar los fuentes y finalizarlo.
	 *
	 * Este servicio est� pensado concretamente para el programa de linea 
	 * de comandos (m�todo main de esta clase) y para otras herramientas
	 * similares que puedan surgir, como la "ant task" loroc.
	 *
	 * @param list     La lista de nombres (String) de los archivos a compilar.
	 *
	 * @param oro_dir  Directorio destino para los archivos compilados.
	 *
	 * @param ext_dir  Directorio de extensiones.
	 *
	 * @param guardarCompilados Guardar los compilados en disco?
	 *                 Usese con cuidado pues es posible que se presenten
	 *                 errores de compilaci�n en ciertos fuentes por dependencias
	 *                 de otros fuentes cuyas compilaciones pudieron ser exitosas
	 *                 pero cuyas unidades compiladas no fueron guardadas en disco
	 *                 para posterior referencia.
	 *
	 * @return 0 si todo ha ido perfectamente; 
	 *         1 si alg�n archivo no pudo ser leido;
	 *         2 si alg�n archivo tuvo problemas de compilaci�n;
	 *
	 * @throws LoroException
	 *         Si se presenta alg�n problema de interacci�n con el n�cleo de Loro.
	 */
	public static int compilar(
		List list,
		String oro_dir,
		String ext_dir,
		boolean guardarCompilados
	)
	throws LoroException
	{
		//// inicializar n�cleo:
		String paths_dir = "";
		Loro.configurar(ext_dir, paths_dir);
		ICompilador compilador = Loro.obtCompilador();
		compilador.ponDirectorioDestino(oro_dir);
		compilador.ponGuardarCompilados(guardarCompilados);

		Writer errores = new PrintWriter(System.err, true);
		int status = 0;
		try
		{
			int compilados = compilador.compilarListaArchivos(list, null, errores);
			status = list.size() - compilados;
		}
		catch(RuntimeException ex) // Contemplado para depuraci�n
		{
			System.err.println(ex.getMessage());
			ex.printStackTrace(System.err);
			throw new LoroException("INESPERADO: " +ex.getMessage());
		}
		finally
		{
			//// finalizar el nucleo:
			Loro.cerrar();
		}
		
		return status;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Servicio "completo" para anticompilar una lista de archivos.
	 * Por "completo" se indica que hace toda la interacci�n necesaria
	 * con el n�cleo Loro para inicializarlo, compilar los fuentes y finalizarlo.
	 *
	 * Este servicio est� pensado concretamente para el programa de linea 
	 * de comandos (m�todo main de esta clase) y para otras herramientas
	 * similares que puedan surgir, como la "ant task" loroc.
	 *
	 * @param list     La lista de nombres (String) de los archivos a compilar.
	 *
	 * @param oro_dir  Directorio destino para los archivos compilados.
	 *
	 * @param ext_dir  Directorio de extensiones.
	 *
	 * @return 0 si todo ha ido perfectamente; 
	 *         1 si alg�n archivo no pudo ser leido;
	 *         2 si alg�n archivo tuvo problemas de anticompilaci�n;
	 *
	 * @throws LoroException
	 *         Si se presenta alg�n problema de interacci�n con el n�cleo de Loro.
	 */
	public static int anticompilar(
		List list,
		String oro_dir,
		String ext_dir
	)
	throws LoroException
	{
		//// inicializar n�cleo:
		String paths_dir = "";
		Loro.configurar(ext_dir, paths_dir);
		ICompilador compilador = Loro.obtCompilador();
		compilador.ponDirectorioDestino(oro_dir);

		Writer errores = new PrintWriter(System.err, true);
		int status = 0;
		try
		{
			int anticompilados = compilador.anticompilarListaArchivos(list, errores);
			status = list.size() - anticompilados;
		}
		catch(RuntimeException ex) // Contemplado para depuraci�n
		{
			System.err.println(ex.getMessage());
			ex.printStackTrace(System.err);
			throw new LoroException("INESPERADO: " +ex.getMessage());
		}
		finally
		{
			//// finalizar el nucleo:
			Loro.cerrar();
		}
		
		return status;
	}
}