package loro.tools;

import loro.arbol.*;
import loro.compilacion.*;
import loro.ejecucion.*;

import loro.util.ManejadorUnidades;
import loro.util.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;


import loro.Loro;
import loro.EjecucionException;

/////////////////////////////////////////////////////////////////////
/**
 * Ejecutor desde linea de comandos.
 * Herramienta loro.
 *
 * @author Carlos Rueda
 */
public class LoroInterprete
{
	////////////////////////////////////////////////////////////////
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

		String nombre;
		String sargs[];

		int arg = 0;
		Logger logger = Logger.createLogger("Interprete");
		ManejadorUnidades mu = ManejadorUnidades.obtManejadorUnidades();

		nombre = args[arg++];
		sargs = new String[args.length - arg];
		System.arraycopy(args, arg, sargs, 0, sargs.length);

		Nodo n = mu.obtAlgoritmo(nombre);
		if ( n == null
		||   !(n instanceof NAlgoritmo) )
		{
			System.err.println(nombre+ " no es algoritmo Loro");
			logger.close();
			return;
		}

		NAlgoritmo alg = (NAlgoritmo) n;

		try
		{
			new LoroEjecutor().ejecutarAlgoritmoArgumentosCadena(alg, sargs);
		}
		catch (EjecucionException e)
		{
			System.out.println("Error de ejecucion: " +e.getMessage());
			e.printStackTrace();
		}
			
		logger.close();
	}
	/////////////////////////////////////////////////////////////////
	public static String obtUso()
	{
		return
_titulo()+ "\n"+
"\n"+
"USO:\n" +
"   loro -ayuda\n"+
"        Despliega esta ayuda y termina.\n" +
"   loro algoritmo [args...]\n" +
"        algoritmo [args...]\n"+
"                   algoritmo a ejecutar y sus argumentos.\n"+
"   loro -version\n"+
"                   Muestra version del sistema y termina.\n"+
"\n"+
	"NOTA: Este mensaje actualizado 2002-01-25 a medias ;-)"
		;
	}


	////////////////////////////////////////////////////////////
	/**
	 * Obtiene el título del intérprete.
	 */
	private static String _titulo()
	{
		return Loro.obtNombre()+ " Versión " +Loro.obtVersion()+ "\n"+
		"Intérprete desde línea de comandos";
	}
}