package loro.tools;

import loro.*;

import java.io.*;
import java.util.*;

/////////////////////////////////////////////////////////////////////
/**
 * Intérprete para acciones interactivas.
 *
 * @author Carlos Rueda
 * @version 2002-06-04
 */
public class InterpreteInteractivo
{
	static final String PROMPT         =  " $ ";
	static final String PREFIX_EXPR    =  "=  ";
	static final String PREFIX_INVALID =  "!  ";
	static final String PREFIX_SPECIAL =  "   ";

	static PrintWriter pw;
	static BufferedReader br;

	static boolean execute = true;
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
	static String obtModo(boolean execute)
	{
		return execute ? "Interpretación completa con ejecución"
					   : "Interpretación sin ejecucion (sólo chequeo)"
		;
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


				procesar(text);
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
	static void metaProcesar(String text)
	{
		String msg;

		if ( text.equals(".?") )
		{
			msg =
"El Intérprete Interactivo le permite ejecutar instrucciones Loro\n"+
"de manera inmediata. Escriba una instrucción a continuación del\n"+
"indicador (símbolo " +PROMPT.trim()+ ") y presione Entrar.\n" +
"Algunos ejemplos:\n"+
"  $ lenguaje: cadena;                       // declaración de una variable \n"+
"  $ lenguaje := \"Loro\";                   // asignación a una variable \n"+
"  $ escribirln(\"Programe en \" +lenguaje); // invocación de un algoritmo \n"+
"\n"+
"Hay algunos comandos especiales para el propio intérprete, reconocidos\n"+
"porque empiezan con punto (.):\n"+
"\n"+
"   .?          - Muestra esta ayuda\n" +
"   .vars       - Muestra las variables declaradas hasta el momento\n" +
"   .borrar ID  - Borra la declaración de la variable indicada\n" +
"   .borrarvars - Borra todas las variables declaradas\n" +
"   .??         - Muestra otros comandos avanzados"
			;
		}
		else if ( text.equals(".??") )
		{
			msg =
"Comandos avanzados:\n"+
"\n"+
"   .modo         - Muestra el modo de interpretación actual.\n" +
"                   Hay dos modos de operación:\n" +
"                     - ejecución completa (por defecto)\n" +
"                     - sólo compilación\n" +
"   .cambiarmodo  - Intercambia el modo de interpretación\n"+
"   .verobj nivel - Pone maximo nivel para visualizar objetos\n"+
"   .verarr long  - Pone maxima longitud para visualizar arreglos\n"
			;
		}
		else if ( text.equals(".vars") )
		{
			msg = Loro.getSymbolTable().toString();
		}
		else if ( text.equals(".modo") )
		{
			msg = obtModo(execute);
		}
		else if ( text.equals(".borrarvars") )
		{
			loroii.reiniciar();
			msg = Loro.getSymbolTable().toString();
		}
		else if ( text.startsWith(".borrar") )
		{
			StringTokenizer st = new StringTokenizer(text.substring(".borrar".length()));
			try
			{
				String id = st.nextToken();
				msg = id+ " " +(loroii.quitarID(id)
					? "borrado" 
					: "no declarado"
				);
			}
			catch ( Exception ex )
			{
				msg = "Indique un nombre de variable";
			}
		}
		else if ( text.equals(".cambiarmodo") )
		{
			execute = !execute;
			if ( execute )
			{
				// se acaba de pasar de "solo compilacion" a "ejecucion".
				// Hacer que todas las variables figuren como sin asignacion:
				loroii.ponAsignado(false);
			}
			msg = "Modo cambiado a: " +obtModo(execute);
		}
		else if ( text.startsWith(".verobj") || text.startsWith(".verarr") )
		{
			StringTokenizer st = new StringTokenizer(text);
			try
			{
				st.nextToken(); // ignore comando
				int num = Integer.parseInt(st.nextToken());
				if ( text.startsWith(".verobj") )
				{
					loroii.ponNivelVerObjeto(num);
				}
				else
				{
					loroii.ponLongitudVerArreglo(num);
				}
				msg = "";
			}
			catch ( Exception ex )
			{
				msg = "Indique un valor numerico";
			}
		}
		else
		{
			msg = text+ ": comando no entendido.  Escriba .? para obtener ayuda.";
		}

		pw.println(msg);
	}

	///////////////////////////////////////////////////////////////////////
	static void procesar(String text)
	throws AnalisisException
	{
		if ( text.charAt(0) == '.' )
		{
			metaProcesar(text);
		}
		else
		{
			procesarLoro(text);
		}
	}

	///////////////////////////////////////////////////////////////////////
	static void procesarLoro(String text)
	throws AnalisisException
	{
		try
		{
			if ( execute )
			{
				String res = loroii.ejecutar(text);
				
				if ( res != null )
				{
					pw.println(PREFIX_EXPR + res);
				}
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