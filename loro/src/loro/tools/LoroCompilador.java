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
 * Ofrece el servicio completo de compilación de archivos fuentes.
 * 
 * compilar() compila una lista de archivos de acuerdo con parámetros
 * de trabajo.
 *
 * anticompilar() anti-compila una lista de archivos de acuerdo con parámetros
 * de trabajo.
 *
 * main() recibe parámetros de la línea de comandos e invoca compilar().
 * Pendiente opción para anticompilar (esta opción está disponible a través
 * de la ant-task).
 *
 * @version 2002-06-04
 * @author Carlos Rueda
 */
public class LoroCompilador
{
	////////////////////////////////////////////////////////////////
	/**
	 * Lee argumentos de la línea de comandos e invoca compilar().
	 * Este método invoca System.exit() para terminar.
	 *
	 * La lista de nombres de los archivos fuentes es primero ordenada
	 * asumiendo que hay consistencia entre el nombre del archivo y la unidad contenida:
	 * ".e.loro" para especificaciones, ".a.loro" para algoritmos y
	 * ".c.loro" para clases; los archivos cuyo nombre no encajen en una de las 
	 * anteriores, se dejan para lo último.
	 *
	 * Este orden ayuda un poco a lograr compilaciones exitosas pero NO lo 
	 * garantiza. Este manejo es sólo una aproximación burda mientras se
	 * hace una implementación inteligente de dependencias de compilación.
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
	 * Obtiene el título del Compilador desde línea de comandos.
	 */
	static String _titulo()
	{
		return Loro.obtNombre()+ " Versión " +Loro.obtVersion()+
		" (" +Loro.obtBuild()+ ")" +
		"\n"+
		"Compilador desde línea de comandos";
	}


	////////////////////////////////////////////////////////////////
	/**
	 * Servicio "completo" para compilar una lista de archivos.
	 * Por "completo" se indica que hace toda la interacción necesaria
	 * con el núcleo Loro para inicializarlo, compilar los fuentes y finalizarlo.
	 *
	 * Este servicio está pensado concretamente para el programa de linea 
	 * de comandos (método main de esta clase) y para otras herramientas
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
	 *                 errores de compilación en ciertos fuentes por dependencias
	 *                 de otros fuentes cuyas compilaciones pudieron ser exitosas
	 *                 pero cuyas unidades compiladas no fueron guardadas en disco
	 *                 para posterior referencia.
	 *
	 * @return 0 si todo ha ido perfectamente; 
	 *         1 si algún archivo no pudo ser leido;
	 *         2 si algún archivo tuvo problemas de compilación;
	 *
	 * @throws LoroException
	 *         Si se presenta algún problema de interacción con el núcleo de Loro.
	 */
	public static int compilar(
		List list,
		String oro_dir,
		String ext_dir,
		boolean guardarCompilados
	)
	throws LoroException
	{
		//// inicializar núcleo:
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
		catch(RuntimeException ex) // Contemplado para depuración
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
	 * Por "completo" se indica que hace toda la interacción necesaria
	 * con el núcleo Loro para inicializarlo, compilar los fuentes y finalizarlo.
	 *
	 * Este servicio está pensado concretamente para el programa de linea 
	 * de comandos (método main de esta clase) y para otras herramientas
	 * similares que puedan surgir, como la "ant task" loroc.
	 *
	 * @param list     La lista de nombres (String) de los archivos a compilar.
	 *
	 * @param oro_dir  Directorio destino para los archivos compilados.
	 *
	 * @param ext_dir  Directorio de extensiones.
	 *
	 * @return 0 si todo ha ido perfectamente; 
	 *         1 si algún archivo no pudo ser leido;
	 *         2 si algún archivo tuvo problemas de anticompilación;
	 *
	 * @throws LoroException
	 *         Si se presenta algún problema de interacción con el núcleo de Loro.
	 */
	public static int anticompilar(
		List list,
		String oro_dir,
		String ext_dir
	)
	throws LoroException
	{
		//// inicializar núcleo:
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
		catch(RuntimeException ex) // Contemplado para depuración
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