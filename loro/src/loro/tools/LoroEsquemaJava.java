package loro.tools;

import loro.arbol.*;
import loro.java.SoporteJava;
import loro.util.ManejadorUnidades;
import loro.util.Logger;

import java.io.PrintWriter;


import loro.Loro;

///////////////////////////////////////////////////////////////
/**
 * Generador de Esquema de Implementacion en Java.
 * Herramienta loroij.
 *
 * @author Carlos Rueda
 */
public class LoroEsquemaJava
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

		Logger logger = Logger.createLogger("EsquemaJava");

		PrintWriter pw = new PrintWriter(System.out);
		ManejadorUnidades mu = ManejadorUnidades.obtManejadorUnidades();
		
		for ( int arg = 0; arg < args.length; arg++ )
		{
			String nombre = args[arg];

			NAlgoritmo alg = mu.obtAlgoritmo(nombre);
			if ( alg == null )
			{
				System.err.println(nombre+ " no es algoritmo Loro");
				continue;
			}

			if ( !alg.implementadoEnLenguaje("java") )
			{
				System.err.println(nombre+ 
					" no es algoritmo declarado con implementacion en Java."
				);
				continue;
			}

			pw.println(SoporteJava.obtEsquemaJava(alg));
			pw.flush();
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
"   loroij -ayuda\n"+
"       Despliega esta ayuda y termina.\n" +
"   loroij algoritmo ...\n" +
"       algoritmo ...\n"+
"              algoritmo a implementar en Java.\n"+
"   loroij -version\n"+
"              Muestra version del sistema y termina.\n"
		;
	}

	////////////////////////////////////////////////////////////
	/**
	 * Obtiene el título del Generador de Esquema para Implementación en Java.
	 */
	private static String _titulo()
	{
		return Loro.obtNombre()+ " Versión " +Loro.obtVersion()+ "\n"+
		"Generador de Esquema para Implementación en Java";
	}
}