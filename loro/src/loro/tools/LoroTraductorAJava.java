package loro.tools;

import loro.Loro;
import loro.derivacion.*;
import loro.DerivacionException;
import loro.arbol.*;
import loro.compilacion.*;
import loro.visitante.*;
import loro.util.Util;

import java.io.*;

///////////////////////////////////////////////////////////////////
/**
 * Traductor a código Java.
 *
 * @author Carlos Rueda
 * @version muy preliminar
 */
public class LoroTraductorAJava
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

		IDerivador derivador = ParserFactory.getParser();

		System.setErr(System.out);

		int arg = 0;
		int primerFuente = arg;
		for ( ; arg < args.length; arg++ )
		{
			String nombre = args[arg];
			System.out.println(nombre);
			IVisitante v = new Chequeador();
			try
			{
				FileInputStream fis = new FileInputStream(nombre);
				Reader reader = new BufferedReader(new InputStreamReader(fis));
				derivador.ponTextoFuente(reader);
				NFuente prg = derivador.derivarFuente();

				prg.aceptar(v);

				v = new VisitanteTraductorAJava();
				prg.aceptar(v);
			}
			catch(DerivacionException ex)
			{
				System.out.println(ex.getMessage());
			}
			catch(VisitanteException ex)
			{
				throw new RuntimeException("No deberia suceder: VisitanteException no ParseException");
			}
			catch(FileNotFoundException ex)
			{
				System.out.println("Archivo no encontrado");
			}
		}
	}
	/////////////////////////////////////////////////////////////////
	public static String obtUso()
	{
		return
_titulo()+ "\n"+
"\n"+
"USO:\n" +
"   loroj fuente.loro ...\n" +
"   loroj -version\n"+
"                Muestra version del sistema y termina.\n"
		;
	}

	////////////////////////////////////////////////////////////
	/**
	 * Obtiene el título del traductor a Java.
	 */
	private static String _titulo()
	{
		return Loro.obtNombre()+ " Versión " +Loro.obtVersion()+ "\n"+
		"Traductor al lenguaje Java (en versión muy preliminar!)";
	}
}