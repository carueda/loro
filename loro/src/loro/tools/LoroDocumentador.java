package loro.tools;

import loro.Loro;
import loro.doc.Documentador;
import loro.arbol.*;
import loro.visitante.*;
import loro.util.Util;
import loro.util.ManejadorUnidades;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.*;

///////////////////////////////////////////////////////////////////
/**
 * Generador de documentacion desde linea de comandos.
 *
 * @author Carlos Rueda
 * @version 09/11/01
 * @version 08/30/01
 */
public class LoroDocumentador
{
	////////////////////////////////////////////////////////////////////
	/**
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
		String paths_dir = "";

		try
		{
			Loro.configurar(ext_dir, paths_dir);

		}
		catch (Throwable ex)
		{
			System.out.println(
				ex.getMessage() + "\n" +
				"Error de inicio" + "\n"
			);

			return;
		}

		String dirGuardarGenerado = null;

		int arg = 0;
		while ( arg < args.length && args[arg].startsWith("-") )
		{
			if ( args[arg].equals("-d") )
			{
				if ( ++arg < args.length )
				{
					dirGuardarGenerado = args[arg];
				}
				else
				{
					System.out.println(obtUso());
					_finalizar();
					return;
				}
			}
			else
			{
				System.out.println(obtUso());
				_finalizar();
				return;
			}

			arg++;
		}

		ManejadorUnidades mu = ManejadorUnidades.obtManejadorUnidades();

		List units = new ArrayList();
		for ( ; arg < args.length; arg++ )
		{
			String name = args[arg];
			NUnidad n;
			if ( name.endsWith(".e") || name.endsWith(".a") || name.endsWith(".c") )
			{
				n = mu.obtUnidad(name);
				if ( n == null )
				{
					System.err.println(name+ " : unidad no encontrada");
				}
				else
				{
					units.add(n);
				}
			}
			else if ( name.endsWith(".oro") )
			{
				n = mu.obtUnidadDeArchivo(name);
				if ( n == null )
				{
					System.err.println(name+ " : error al leer unidad de archivo");
				}
				else
				{
					units.add(n);
				}
			}
			else if ( name.endsWith(".lar") )
			{
				try
				{
					int size = units.size();
					Documentador.cargarUnidadesDeZip(name, units);
					System.out.println("Cargadas " +(units.size() - size)+ " unidades de " +name);
				}
				catch ( Exception ex )
				{
					System.err.println(name+ " : " +ex.getMessage());
				}
			}
			else
			{
				// se asume que name corresponde a nombre de paquete:
				mu.getOroLoaderManager().loadUnitsFromPackage(name, units);
			}
		}

		System.out.println("Procesando " +units.size()+ " unidades...");

		String s = Documentador.procesarLista(units, dirGuardarGenerado);
		if ( s != null )
		{
			System.err.println(s);
		}

		_finalizar();
	}


	/////////////////////////////////////////////////////////////////
	public static String obtUso()
	{
		return
_titulo()+ "\n"+
"\n"+
"USO:\n" +
"   lorodoc [-d dir] elemento ...\n" +
"\n"+
"      -d dir    Indica directorio de base para guardar\n"+
"                los archivos HTML generados. Por defecto, el directorio actual.\n"+
"\n"+
"      elemento  Una de las siguientes posibilidades:\n"+
"                  - Nombre completo de una unidad (cualificada con paquete)\n"+
"                    terminado con la extension caracteristica (.e, .a, .c):\n"+
"                    Ej:  loroI::sistema::leerEntero.a\n"+
"                            toma el algoritmo leerEntero del paquete loroI::sistema\n"+
"                  - Nombre de un paquete:\n"+
"                    Ej:  loroI::mat\n"+
"                            toma todas la unidades del paquete loroI::mat\n"+
"                  - Nombre de un archivo .lar:\n"+
"                    Ej:  loroI.lar\n"+
"                            toma TODAS la unidades del archivo loroI.lar\n"+
"                  - Nombre de un archivo terminado en .oro.\n"+
"                    Ej:  mipaquete/subpaquete/UnaClase.c.oro\n"+
"                            toma la unidad guardada en este archivo\n"+
"\n"+
"   lorodoc -version\n"+
"                Muestra version del sistema y termina.\n"
		;
	}



	////////////////////////////////////////////////////////////
	/**
	 * Obtiene el título del Documentator.
	 */
	private static String _titulo()
	{
		return Loro.obtNombre()+ " Versión " +Loro.obtVersion()+ "\n"+
		"Generador de Documentacion";
	}

	////////////////////////////////////////////////////////////////
	private static void _finalizar()
	{
		try
		{
			Loro.cerrar();
		}
		catch (Throwable ex)
		{
			System.out.println(
				ex.getMessage() + "\n" +
				"Error de terminacion"
			);
		}
	}
}