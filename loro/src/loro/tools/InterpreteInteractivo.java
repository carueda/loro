package loro.tools;

import loro.*;

import java.io.*;
import java.util.*;

/////////////////////////////////////////////////////////////////////
/**
 * Intérprete para acciones interactivas.
 *
 * @author Carlos Rueda
 */
public class InterpreteInteractivo
{
	static final String PROMPT         =  " $ ";
	static final String PREFIX_EXPR    =  "=  ";
	static final String PREFIX_INVALID =  "!  ";
	static final String PREFIX_SPECIAL =  "   ";

	static PrintWriter pw;
	static BufferedReader br;

	static IInterprete loroii;

	// prepare valores de configuracion para Loro:
	static String ext_dir = "";
	static String oro_dir = ".";
	static String paths_dir = "";

	/////////////////////////////////////////////////////////////////////
	public static void main(String[] args)
	throws Exception
	{
		int arg = 0;
		while ( arg < args.length && args[arg].startsWith("-") )
		{
			if ( args[arg].equals("-oro") )
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
			else if ( args[arg].equals("-ext") )
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
			else if ( args[arg].startsWith("-ayuda") )
			{
				System.out.println(obtUso());
				return;
			}
			arg++;
		}
		iniciar();
		ejecutar();
		finalizar();
	}
	
	/////////////////////////////////////////////////////////////////////
	static void finalizar()
	throws Exception
	{
		Loro.cerrar();
	}
	
	/////////////////////////////////////////////////////////////////////
	static void iniciar()
	throws Exception
	{
		// Inicie sistema Loro:
		Loro.configurar(ext_dir, paths_dir);
		ICompilador compilador = Loro.obtCompilador();
		compilador.ponDirectorioDestino(oro_dir);

		// Verifique el núcleo Loro:
		Loro.verificarNucleo();

		// provisional
		System.setErr(System.out);


		pw = new PrintWriter(System.out, true);
		br = new BufferedReader(new InputStreamReader(System.in));

		loroii = Loro.crearInterprete(br, pw, false, null);

		pw.println(
			Loro.obtNombre()+ " " +Loro.obtVersion()+ " (" +Loro.obtBuild()+ ")\n" +
			"Escriba .? para obtener una ayuda"
		);
	}

	///////////////////////////////////////////////////////////////////////
	static void ejecutar()
	{
		while ( true )
		{
			String msg = null;

			try
			{
				pw.print(PROMPT);
				pw.flush();
				String text = br.readLine();
				if ( text == null )
				{
					break;
				}
				text = text.trim();
				if ( text.length() == 0 )
				{
					continue;
				}


				procesarLoro(text);
			}
			catch ( EjecucionException ex )
			{
				StringWriter sw = new StringWriter();
					ex.printStackTrace(new PrintWriter(sw));
					msg = ex.getMessage() + "\n" +sw.toString();
			}
			catch(CompilacionException ex)
			{
				msg = ex.getMessage();
			}
			catch(Exception ex)
			{
				StringWriter sw = new StringWriter();
				PrintWriter psw = new PrintWriter(sw);
				psw.println("INESPERADO");
				ex.printStackTrace(psw);
				psw.println(
"Esta es una anomalía del sistema. Por favor, consulte la ayuda general (F1)\n"+
"para saber si se trata de un problema ya reconocido o es nuevo, en cuyo caso\n"+
"encontrará instrucciones sobre cómo notificarlo y así tenerlo en cuenta para\n"+
"corregirlo en una próxima versión. Gracias."
				);
				msg = sw.toString();
			}

			if ( msg != null )
			{
				pw.println(PREFIX_INVALID + msg);
			}
		}
	}

	///////////////////////////////////////////////////////////////////////
	static void procesarLoro(String text)
	throws AnalisisException
	{
		try
		{
			if ( loroii.getExecute() )
			{
				String res = loroii.ejecutar(text);
				if ( res != null )
					pw.println(PREFIX_EXPR + res);
			}
			else
			{
				loroii.compilar(text);
			}
		}
		finally
		{
			pw.println();
		}
	}
	
	/////////////////////////////////////////////////////////////////
	public static String obtUso()
	{
		return
_titulo()+ "\n"+
"\n"+
"USO:\n" +
"\n"+
"   loroii -ayuda\n"+
"       Despliega esta ayuda y termina.\n"+
"\n"+
"   loroii [opciones]\n" +
"   opciones:\n" +
"      -ext dir  Establece directorio de extensiones\n"+
"      -oro dir  Establece directorio para leer compilados\n"
		;
	}

	////////////////////////////////////////////////////////////
	/**
	 * Obtiene el título de este programa.
	 */
	static String _titulo()
	{
		return Loro.obtNombre()+ 
		" Versión " +Loro.obtVersion()+
		" (" +Loro.obtBuild()+ ")" +
		"\n"+
		"Intérprete Interactivo";
	}

}