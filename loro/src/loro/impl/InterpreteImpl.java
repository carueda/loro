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
	PrefixWriter prefixw;

	String version =	
		"Int�rprete Interactivo de Loro\n" +
		Loro.obtNombre()+ " versi�n " +Loro.obtVersion()+ " (" +Loro.obtBuild()+ ")"
	;

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
			prefixw = new PrefixWriter(pw);
			pw = new PrintWriter(prefixw);

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
						n.aceptar(ejecutor);
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
				if ( tipo.esCaracter() )
					q = '\'';
				else if ( tipo.esCadena() )
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
		return execute ? "Interpretaci�n completa con ejecuci�n"
		               : "Interpretaci�n sin ejecucion (s�lo chequeo)"
		;
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
".?            - Muestra esta ayuda\n" +
".vars         - Muestra las variables declaradas actualmente\n" +
".borrar ID    - Borra la declaraci�n de la variable indicada\n" +
".borrarvars   - Borra todas las variables declaradas\n" +
".verobj nivel - Pone m�ximo nivel para visualizar objetos\n"+
".verarr long  - Pone m�xima longitud para visualizar arreglos\n" +
".version      - Muestra informaci�n general sobre versi�n del sistema\n" +
".modo         - Muestra el modo de interpretaci�n actual.\n" +
"                Hay dos modos de operaci�n:\n" +
"                  - ejecuci�n completa (por defecto)\n" +
"                  - s�lo compilaci�n (usar con cuidado)\n" +
".cambiarmodo  - Intercambia el modo de interpretaci�n\n"+
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
			else if ( text.equals(".version") )
			{
				msg = version;
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
					msg = "Indique un valor num�rico";
				}
			}
			else if ( text.equals(".salir") )
			{
				interactive = false;
				msg = "** modo interactivo terminado **";
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
		String prompt         =  " $ ";
		String prefix_expr    =  "=  ";
		String prefix_invalid =  "!  ";
		String prefix_special =  "   ";
		
		///////////////////////////////////////////////////////////////////////
		public void setPrompt(String prompt)
		{
			this.prompt = prompt;
		}

		///////////////////////////////////////////////////////////////////////
		public void setPrefixes(String expr, String invalid, String special)
		{
			this.prefix_expr    = expr;
			this.prefix_invalid = invalid;
			this.prefix_special = special;
		}

		///////////////////////////////////////////////////////////////////////
		public void end()
		{
			interactive = false;
		}
		
		///////////////////////////////////////////////////////////////////////
		public void run()
		{
			String prefix = prefix_special;
			
			if ( interactive )
			{
				prefixw.setPrefix(prefix);
				pw.println(
					prefix +
					version+ "\n" +
					"Escribe .? para obtener una ayuda\n"
				);
			}
			
			while ( interactive )
			{
				String res = null;  // normal output
				
				prefix = null;
				prefixw.setPrefix(prefix);
				pw.print(prompt);
				pw.flush();
	
				try
				{
					String text = br.readLine();
					if ( text == null )
						break;
					text = text.trim();
					if ( text.length() == 0 )
						continue;
	
					prefixw.setPrefix(prefix_special);
					pw.print(prefix_special);
					res = procesar(text);
					prefix = prefix_expr;
				}
				catch ( EjecucionException ex )
				{
					prefix = prefix_invalid;
					if ( ex.esTerminacionInterna() )
					{
						res = "Ejecuci�n terminada. C�digo de terminaci�n = " 
							+ex.obtCodigoTerminacionInterna()
						;
					}
					else
					{
						StringWriter sw = new StringWriter();
						ex.printStackTrace(new PrintWriter(sw));
						res = ex.getMessage() + "\n" +sw.toString();
					}
				}
				catch(CompilacionException ex)
				{
					prefix = prefix_invalid;
					res = ex.getMessage() + "\n";
				}
				catch ( InterruptedIOException ex )
				{
					// ignore
				}
				catch(Exception ex)
				{
					prefix = prefix_invalid;
					StringWriter sw = new StringWriter();
					PrintWriter psw = new PrintWriter(sw);
					psw.println("INESPERADO");
					ex.printStackTrace(psw);
					psw.println("Esta es una anomal�a del sistema.");
					res = sw.toString();
				}
	
				prefixw.setPrefix(null);
				pw.println();
				prefixw.setPrefix(prefix);
				if ( res != null )
					pw.println(res);
			}
		}
	}
}