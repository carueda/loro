package loro.impl;

import loro.visitante.VisitanteException;
import loro.compilacion.*;
import loro.arbol.Nodo;
import loro.arbol.NExpresion;
import loro.arbol.NUnidadInterprete;
import loro.arbol.NUtiliza;
import loro.ejecucion.*;
import loro.util.*;
import loro.tabsimb.*;
import loro.tipo.Tipo;
import loro.derivacion.*;
import loro.ijava.LException;
import loro.*;

import java.io.*;
import java.util.*;


/////////////////////////////////////////////////////////////////////
/**
 * Interprete para acciones.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class InterpreteImpl implements IInterprete
{
	TablaSimbolos tabSimbBase;
	NUnidadInterprete ui;

	Chequeador chequeador;
	EjecutorTerminable ejecutor;

	boolean execute = true;

	IDerivador derivador;
	
	BufferedReader br = null;
	PrintWriter pw = null;

	IMetaListener base_ml;
	
	boolean interactive;
	IInteractiveInterpreter interactiveInterpreter;


	/////////////////////////////////////////////////////////////////////
	/**
	 * Crea un intérprete.
	 *
	 * @param r
	 * @param w
	 * @param tabSimbBase Tabla de símbolos de base.
	 * @param loroClassLoader
	 */
	public InterpreteImpl(
		Reader r, Writer w, 
		TablaSimbolos tabSimbBase, LoroClassLoader loroClassLoader, 
		IObservadorPP obspp
	)
	{
		super();

		derivador = ManejadorDerivacion.obtDerivador();

		this.tabSimbBase = tabSimbBase;
		ui = new NUnidadInterprete();

		chequeador = new Chequeador(tabSimbBase, ui);

		if ( obspp != null )
		{
			ejecutor = new EjecutorPP(tabSimbBase, ui);
			ejecutor.ponObservadorPP(obspp); 
		}
		else
		{
			ejecutor = new EjecutorTerminable(tabSimbBase, ui);
		}
		
		ejecutor.ponClassLoader(loroClassLoader);
		
		
		if ( r != null )
		{
			br = new BufferedReader(r);
			ejecutor.ponEntradaEstandar(br);
		}
		if ( w != null )
		{
			if ( w instanceof PrintWriter )
				pw = (PrintWriter) w;
			else
				pw = new PrintWriter(w, true);

			ejecutor.ponSalidaEstandar(pw);
		}
		
		base_ml = new MetaListener();
		
		// created on demand
		interactiveInterpreter = null;
	}

	/////////////////////////////////////////////////////////////////
	public boolean isTraceable()
	{
		return ejecutor.esPasoAPaso();
	}

	///////////////////////////////////////////////////////////////////////
	private List _compilar(String text)
	throws CompilacionException
	{
		////////////////////
		// fase sintactica
		List list = derivador.ponTextoFuente(text).derivarAccionesInterprete();
		if ( list != null )
		{
			for ( Iterator it = list.iterator(); it.hasNext(); )
			{
				Object obj = it.next();
				
				if ( obj instanceof Nodo )
				{
					Nodo n = (Nodo) obj;
					try
					{
						chequeador.chequear(n);
					}
					catch ( ChequeadorException se )
					{
						throw new CompilacionException(se.obtRango(), se.getMessage());
					}
				}
				else
				{
					// metacomando.
				}
			}
		}
		return list;
	}

	///////////////////////////////////////////////////////////////////////
	public void compilar(String text)
	throws CompilacionException
	{
		_compilar(text);
	}

	///////////////////////////////////////////////////////////////////////
	public String ejecutar(String text)
	throws AnalisisException
	{
		return (String) eval(text, true);
	}
	
	///////////////////////////////////////////////////////////////////////
	public String procesar(String text)
	throws AnalisisException
	{
		return (String) eval(text, true);
	}
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * Ver UtilEjecucion._executeUsrAlgorithm() 
	 */
	public Object eval(String text, boolean comillas)
	throws AnalisisException
	{
		Object ret = null;
		List list = _compilar(text);
		if ( list == null || list.size() == 0 )
			return null;   // un comentario.
		
		try
		{
			for ( Iterator it = list.iterator(); it.hasNext(); )
			{
				Object obj = it.next();
				if ( obj instanceof Nodo )
				{
					Nodo n = (Nodo) obj;
			
					if ( n instanceof NUtiliza )
					{
						ret = null;
					}
					else if ( execute )
					{
						ui.setSourceCode(text);
						ejecutor.reset(tabSimbBase, ui);
						ejecutor.ejecutarNodo(n);
						ret = ejecutor.obtRetorno();
						if ( comillas )
							ret = valorComillas(n, ret);
					}
				}
				else
				{
					String meta = (String) obj;
					ret = base_ml.execute("." +meta);
					pw.println(ret);
					ret = null;
				}
			}
			
			return ret;
		}
		catch(EjecucionVisitanteException ex)
		{
			throw new EjecucionException(
				ex.obtPilaEjecucion(), ex.obtRango(), ex.getMessage()
			);
		}
		catch(ControlLanceException ex)
		{
			throw new EjecucionException(null, null, 
				ex.getMessage()+ "\n" +ex.obtEstadoPila()
			);
		}
		catch(VisitanteException ex)
		{
			throw new EjecucionException(null, null, ex.getMessage());
		}
		catch(LException ex)
		{
			throw new EjecucionException(null, null, ex.getMessage());
		}
	}

	///////////////////////////////////////////////////////////////////////
	public boolean quitarID(String id)
	{
		return tabSimbBase.borrar(id);
	}

	///////////////////////////////////////////////////////////////////////
	public void reiniciar()
	{
		tabSimbBase.reiniciar();
		ui.iniciarAsociacionesSimpleCompuesto();
	}

	///////////////////////////////////////////////////////////////////////
	public void terminarExternamente()
	{
		ejecutor.terminarExternamente();
	}
	
	///////////////////////////////////////////////////////////////////////
	String valorComillas(Nodo n, Object o)
	throws LException
	{
		String res = null;

		if ( n instanceof NExpresion )
		{
			Tipo tipo = ((NExpresion) n).obtTipo();

			if ( o != null )
			{
				res = UtilValor.comoCadena(o);

				// mire si hay que poner ``quotes'':

				char q = 0;
				if ( tipo.esCaracter() || o instanceof Character )
					q = '\'';
				else if ( tipo.esCadena() || o instanceof String )
					q = '\"';

				if ( q != 0 )
					res = Util.quote(q, res);
			}
			else if ( tipo.esUnit() )
			{
				// no imprimir nada. Ok
			}
			else
			{
				// muestre este nulo:
				res = UtilValor.comoCadena(null);
			}
		}

		return res;
	}

	///////////////////////////////////////////////////////////////////////
	public void ponAsignado(boolean asignado)
	{
		tabSimbBase.ponAsignado(asignado);
	}
	
	///////////////////////////////////////////////////////////////////////
	public void ponNivelVerObjeto(int nivelVerObjeto)
	{
		UtilValor.ponNivelVerObjeto(nivelVerObjeto);
	}

	///////////////////////////////////////////////////////////////////////
	public void ponLongitudVerArreglo(int longitudVerArreglo)
	{
		UtilValor.ponLongitudVerArreglo(longitudVerArreglo);
	}

	//////////////////////////////////////////////////////////////
	public boolean getExecute()
	{
		return execute;
	}

	///////////////////////////////////////////////////////////////////////
	public void setExecute(boolean execute)
	{
		this.execute = execute;
	}

	///////////////////////////////////////////////////////////////////////
	static String obtModo(boolean execute)
	{
		return execute ? "Interpretación completa con ejecución"
		               : "Interpretación sin ejecucion (sólo chequeo)"
		;
	}

	//////////////////////////////////////////////////////////////
	public ISymbolTable getSymbolTable()
	{
		return tabSimbBase;
	}

	///////////////////////////////////////////////////////////////////////
	public void setMetaListener(IMetaListener ml)
	{
		((MetaListener) base_ml).client = ml;
	}
	
	
	///////////////////////////////////////////////////////////////////////
	// Base listener for meta-commands
	private class MetaListener implements IMetaListener
	{
		/** Client listener. */
		IMetaListener client = null;
		
		String info =
"El Intérprete Interactivo permite ejecutar instrucciones en el lenguaje\n"+
"Loro de manera inmediata. Escribe una instrucción a continuación del\n"+
"indicador y presiona Intro.\n" +
"\n"+
"Los siguientes son algunos comandos especiales para el propio intérprete\n"+
"reconocidos porque empiezan con punto (.):\n"+
"\n"+
".?            - Muestra esta ayuda\n" +
".vars         - Muestra las variables declaradas actualmente\n" +
".borrar ID    - Borra la declaración de la variable indicada\n" +
".borrarvars   - Borra todas las variables declaradas\n" +
".verobj nivel - Pone máximo nivel para visualizar objetos\n"+
".verarr long  - Pone máxima longitud para visualizar arreglos\n" +
".modo         - Muestra el modo de interpretación actual.\n" +
"                Hay dos modos de operación:\n" +
"                  - ejecución completa (por defecto)\n" +
"                  - sólo compilación (usar con cuidado)\n" +
".cambiarmodo  - Intercambia el modo de interpretación\n"+
".gc           - Recicla memoria"
		;
		
		///////////////////////////////////////////////////////////////////////
		public String getInfo()
		{
			if ( client != null )
				return info + "\n" + client.getInfo();
			else
				return info;
		}
		
		///////////////////////////////////////////////////////////////////////
		// never returns null.
		public String execute(String text)
		{
			String msg = null;
			
			if ( client != null )
				msg = client.execute(text);
	
			if ( msg != null )
			{
				// procesado por cliente meta-listener.
			}
			else if ( text.equals(".?") )
			{
				msg = getInfo();
			}
			else if ( text.equals(".vars") )
			{
				msg = tabSimbBase.toString();
			}
			else if ( text.equals(".modo") )
			{
				msg = obtModo(execute);
			}
			else if ( text.equals(".borrarvars") )
			{
				reiniciar();
				msg = tabSimbBase.toString();
			}
			else if ( text.startsWith(".borrar") )
			{
				StringTokenizer st = new StringTokenizer(text.substring(".borrar".length()));
				try
				{
					String id = st.nextToken();
					msg = id+ " " +(quitarID(id)
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
					ponAsignado(false);
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
						ponNivelVerObjeto(num);
					else
						ponLongitudVerArreglo(num);
					
					msg = "";
				}
				catch ( Exception ex )
				{
					msg = "Indique un valor numérico";
				}
			}
			else if ( text.equals(".gc") )
			{
				System.gc();
				msg = "free memory = " +Runtime.getRuntime().freeMemory()+ "  " +
					  "total memory = " +Runtime.getRuntime().totalMemory()
				;
			}
			else
			{
				msg = text+ ": meta-comando no entendido.\n" +
					  "Escribe .? para obtener una ayuda\n";
			}
	
			return msg;
		}
	
	}
	
	
	///////////////////////////////////////////////////////////////////////
	public IInteractiveInterpreter getInteractiveInterpreter()
	{
		if ( interactiveInterpreter == null )
		{
			interactive = true;
			interactiveInterpreter = new InteractiveInterpreter();
		}
		
		return interactiveInterpreter;
	}
	
	///////////////////////////////////////////////////////////////////////
	private class InteractiveInterpreter implements IInteractiveInterpreter
	{
		IManager mgr = new Manager();
		
		///////////////////////////////////////////////////////////////////////
		// Default manager for interaction
		private class Manager implements IManager
		{
			///////////////////////////////////////////////////////////////////////
			public String prompt()
			throws IOException
			{
				pw.print(" $ ");
				pw.flush();
				return br.readLine();
			}

			///////////////////////////////////////////////////////////////////////
			public void expression(String expr)
			{
				pw.println(" = " +expr);
			}

			///////////////////////////////////////////////////////////////////////
			public void exception(String msg)
			{
				pw.println(" ! " +msg.replaceAll("\n", "\n ! "));
			}
		}
		
		///////////////////////////////////////////////////////////////////////
		public void setManager(IManager mgr)
		{
			this.mgr = mgr;
		}

		///////////////////////////////////////////////////////////////////////
		public void end()
		{
			interactive = false;
		}
		
		///////////////////////////////////////////////////////////////////////
		public void run()
		{
			while ( interactive )
			{
				String msg = null; // for exception
				
				try
				{
					String text = mgr.prompt();
					if ( text == null )
						break;
					
					text = text.trim();
					if ( text.length() == 0 )
						continue;
	
					String res = procesar(text);
					if ( res != null )
					{
						mgr.expression(res);
						continue;
					}
				}
				catch ( EjecucionException ex )
				{
					if ( ex.esTerminacionInterna() )
					{
						msg = "Ejecución terminada. Código de terminación = " 
							+ex.obtCodigoTerminacionInterna()
						;
					}
					else
					{
						StringWriter sw = new StringWriter();
						ex.printStackTrace(new PrintWriter(sw));
						msg = ex.getMessage() + "\n" +sw.toString();
					}
				}
				catch(CompilacionException ex)
				{
					msg = ex.getMessage() + "\n";
				}
				catch ( InterruptedIOException ex )
				{
					// ignore
				}
				catch(Exception ex)
				{
					StringWriter sw = new StringWriter();
					PrintWriter psw = new PrintWriter(sw);
					psw.println("INESPERADO");
					ex.printStackTrace(psw);
					psw.println("Esta es una anomalía del sistema.");
					msg = sw.toString();
				}
	
				if ( msg != null )
					mgr.exception(msg);
			}
		}
	}
}