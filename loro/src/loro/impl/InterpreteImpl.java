package loro.impl;

import loro.Loro.Str;
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
	 * Crea un int�rprete.
	 *
	 * @param r
	 * @param w
	 * @param tabSimbBase Tabla de s�mbolos de base.
	 * @param loroClassLoader
	 */
	public InterpreteImpl(
		Reader r, Writer w, 
		TablaSimbolos tabSimbBase, LoroClassLoader loroClassLoader, 
		IObservadorPP obspp
	)
	{
		super();

		derivador = ParserFactory.getParser();

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
		return eval(text);
	}
	
	///////////////////////////////////////////////////////////////////////
	public String procesar(String text)
	throws AnalisisException
	{
		return eval(text);
	}
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * Ver UtilEjecucion._executeUsrAlgorithm() 
	 */
	public String eval(String text)
	throws AnalisisException
	{
		List list = _compilar(text);
		if ( list == null || list.size() == 0 )
			return null;   // un comentario.
		
		try
		{
			String ret = null;
			for ( Iterator it = list.iterator(); it.hasNext(); )
			{
				ret = null;
				Object obj = it.next();
				if ( obj instanceof Nodo )
				{
					Nodo n = (Nodo) obj;
					if ( !(n instanceof NUtiliza)  &&  execute )
					{
						ui.setSourceCode(text);
						ejecutor.reset(tabSimbBase, ui);
						ejecutor.ejecutarNodo(n);
						if ( n instanceof NExpresion )
						{
							Tipo tipo = ((NExpresion) n).obtTipo();
							ret = UtilValor.valorComillasDeExpresion(
								tipo, 
								ejecutor.obtRetorno()
							);
						}
					}
				}
				else
				{
					String meta = (String) obj;
					pw.println(base_ml.execute("." +meta));
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
		return execute ? Str.get("ii.run_mode")
		               : Str.get("ii.check_mode")
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
		
		String info = Str.get("ii.help_msg");
		
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
			else if ( text.equals(Str.get("ii.help")) )
			{
				msg = getInfo();
			}
			else if ( text.equals(Str.get("ii.who")) )
			{
				msg = tabSimbBase.toString();
			}
			else if ( text.equals(Str.get("ii.mode")) )
			{
				msg = obtModo(execute);
			}
			else if ( text.equals(Str.get("ii.del_vars")) )
			{
				reiniciar();
				msg = tabSimbBase.toString();
			}
			else if ( text.startsWith(Str.get("ii.del")) )
			{
				String cmd_del = Str.get("ii.del");
				StringTokenizer st = new StringTokenizer(text.substring(cmd_del.length()));
				try {
					String id = st.nextToken();
					if ( quitarID(id) )
						msg = Str.get("ii.1_id_deleted", id);
					else
						msg = Str.get("error.1_id_undefined", id);
				}
				catch ( Exception ex ) {
					msg = Str.get("ii.id_expected");
				}
			}
			else if ( text.equals(Str.get("ii.toggle_mode")) )
			{
				execute = !execute;
				if ( execute ) {
					// se acaba de pasar de "solo compilacion" a "ejecucion".
					// Hacer que todas las variables figuren como sin asignacion:
					ponAsignado(false);
				}
				msg = Str.get("ii.1_new_mode", obtModo(execute));
			}
			else if ( text.startsWith(Str.get("ii.object_level")) 
			||        text.startsWith(Str.get("ii.array_level")) )
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
					msg = Str.get("ii.number_expected");
				}
			}
			else if ( text.equals(".gc") )
			{
				Runtime rt = Runtime.getRuntime();
				rt.gc();
				msg = "free memory = " +rt.freeMemory()+ "  " +
					  "total memory = " +rt.totalMemory()
				;
			}
			else {
				msg = Str.get("ii.1_unrecognized_cmd", text)+ "\n"; 
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
			public void handleException(Exception exc)
			{
				String msg = formatException(exc);
				pw.println(" ! " +msg.replaceAll("\n", "\n ! "));
			}
			
			
			///////////////////////////////////////////////////////////////////////
			public String formatException(Exception exc)
			{
				String msg = null;
				try
				{
					throw exc;
				}
				catch(EjecucionException ex)
				{
					if ( ex.esTerminacionInterna() ) {
						msg = Str.get("ii.1_exit_code", ""+ex.obtCodigoTerminacionInterna()); 
					}
					else {
						StringWriter sw = new StringWriter();
						ex.printStackTrace(new PrintWriter(sw));
						msg = ex.getMessage() + "\n" +sw.toString();
					}
				}
				catch(CompilacionException ex)
				{
					msg = ex.getMessage();
				}
				catch(InterruptedIOException ex)
				{
					msg = Str.get("ii.interrupted_io");
				}
				catch(Exception ex)
				{
					StringWriter sw = new StringWriter();
					PrintWriter psw = new PrintWriter(sw);
					psw.println("UNEXPECTED");
					ex.printStackTrace(psw);
					psw.println("This is a BUG!!");
					msg = sw.toString();
				}
				return msg;
			}
		}
		
		///////////////////////////////////////////////////////////////////////
		public IManager getManager()
		{
			return mgr;
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
						mgr.expression(res);
				}
				catch ( Exception ex )
				{
					mgr.handleException(ex);
				}
			}
		}
	}
}