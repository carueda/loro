package loro.tools;

import loro.*;

import java.io.*;
import java.util.*;

/////////////////////////////////////////////////////////////////////
/**
 * Intérprete para acciones interactivas.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class InterpreteInteractivo
{
	/////////////////////////////////////////////////////////////////////
	public static void main(String[] args)
	throws Exception
	{
		processArgs(args);
		init();
		execute();
		end();
	}
	
	static String version =	
		"Intérprete Interactivo de Loro\n" +
		Loro.obtNombre()+ " versión " +Loro.obtVersion()+ " (" +Loro.obtBuild()+ ")"
	;

	static PrintWriter pw;
	static BufferedReader br;
	static IInterprete loroi;
	static IInterprete.IInteractiveInterpreter ii;

	static String ext_dir = "";
	static String oro_dir = ".";
	static String paths_dir = "";

	/////////////////////////////////////////////////////////////////////
	static void processArgs(String[] args)
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
					System.exit(1);
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
					System.exit(1);
				}
			}
			else if ( args[arg].startsWith("-ayuda") )
			{
				System.out.println(obtUso());
				System.exit(0);
			}
			arg++;
		}
	}

	/////////////////////////////////////////////////////////////////////
	static void init()
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

		loroi = Loro.crearInterprete(br, pw, false, null);
		ii = loroi.getInteractiveInterpreter();
		ii.setManager(new IInterprete.IInteractiveInterpreter.IManager()
		{
			///////////////////////////////////////////////////////////////////////
			public String prompt()
			throws IOException
			{
				pw.print(" ? ");
				pw.flush();
				return br.readLine();
			}

			///////////////////////////////////////////////////////////////////////
			public void expression(String expr)
			{
				pw.println("Expresion = " +expr);
			}

			///////////////////////////////////////////////////////////////////////
			public void exception(String msg)
			{
				pw.println("Exception = " +msg);
			}
		});
		
		loroi.setMetaListener(new IInterprete.IMetaListener()
		{
			String info = 
".version      - Muestra información general sobre versión del sistema\n" +
".salir        - Termina el modo interactivo"
			;
			
			///////////////////////////////////////////////////////////////////////
			public String getInfo()
			{
				return info;
			}
			
			public String execute(String meta)
			{
				String res = null;
				if ( meta.equals(".version") )
				{
					res = version;
				}
				else if ( meta.equals(".salir") )
				{
					ii.end();
					res = "** modo interactivo terminado **";
				}
				return res;
			}
		});
	}

	/////////////////////////////////////////////////////////////////////
	static void execute()
	{
		pw.println(
			version+ "\n" +
			"Escribe .? para obtener una ayuda\n"
		);
		ii.run();
	}
	
	/////////////////////////////////////////////////////////////////////
	static void end()
	throws Exception
	{
		Loro.cerrar();
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