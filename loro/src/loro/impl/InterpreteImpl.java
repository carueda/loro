package loro.impl;


import loro.visitante.VisitanteException;
import loro.compilacion.*;
import loro.arbol.Nodo;
import loro.arbol.NExpresion;
import loro.arbol.NUnidadInterprete;
import loro.arbol.NUtiliza;
import loro.ejecucion.*;
import loro.util.Util;

import java.io.*;
import java.util.*;

import loro.tabsimb.*;
import loro.tipo.Tipo;
import loro.derivacion.*;
import loro.ijava.LException;
import loro.*;

import loro.util.UtilValor;

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
	
	PrintWriter pw = null;


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
			ejecutor.ponEntradaEstandar(new BufferedReader(r));
		if ( w != null )
		{
			pw = new PrintWriter(w);
			ejecutor.ponSalidaEstandar(pw);
		}
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
	/**
	 * Ver UtilEjecucion._executeUsrAlgorithm() 
	 */
	public Object eval(String text, boolean comillas)
	throws AnalisisException
	{
		Object ret = null;
		List list = _compilar(text);
		if ( list == null || list.size() == 0 )
		{
			// un comentario.
			return null;
		}
		
		
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
					else
					{
						////////////////////
						// fase ejecucion:
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
					// metacomando.
					String meta = (String) obj;
					System.out.println("meta=" +meta);
					metaProcesar("." +meta);
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
				{
					q = '\'';
				}
				else if ( tipo.esCadena() )
				{
					q = '\"';
				}

				if ( q != 0 )
				{
					res = Util.quote(q, res);
				}
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


	///////////////////////////////////////////////////////////////////////
	private void metaProcesar(String text)
	{
		String msg;

		if ( text.equals(".?") )
		{
			msg =
"El Intérprete Interactivo permite ejecutar instrucciones de\n"+
"manera inmediata. Escribe una instrucción a continuación del\n"+
"indicador y presiona Entrar.\n" +
"\n"+
"Hay algunos comandos especiales para el propio intérprete\n"+
"reconocidos porque empiezan con punto (.):\n"+
"\n"+
"   .?            - Muestra esta ayuda\n" +
"   .limpiar      - Limpia la ventana\n"+
"   .vars         - Muestra las variables declaradas actualmente\n" +
"   .borrar ID    - Borra la declaración de la variable indicada\n" +
"   .borrarvars   - Borra todas las variables declaradas\n" +
"   .verobj nivel - Pone máximo nivel para visualizar objetos\n"+
"   .verarr long  - Pone máxima longitud para visualizar arreglos\n" +
"   .version      - Muestra información general sobre versión del sistema\n" +
"   .??           - Muestra otros comandos avanzados"
			;
		}
		else if ( text.equals(".??") )
		{
			msg =
"Comandos avanzados:\n"+
"   .modo         - Muestra el modo de interpretación actual.\n" +
"                   Hay dos modos de operación:\n" +
"                     - ejecución completa (por defecto)\n" +
"                     - sólo compilación (usar con cuidado)\n" +
"   .cambiarmodo  - Intercambia el modo de interpretación\n"+
"   .gc           - Reciclar memoria\n"
			;
		}
		else if ( text.equals(".vars") )
		{
			msg = tabSimbBase.toString();
			//msg = Loro.getSymbolTable().toString();
		}
		else if ( text.equals(".modo") )
		{
			msg = obtModo(execute);
		}
		else if ( text.equals(".borrarvars") )
		{
			this.reiniciar();
			msg = Loro.getSymbolTable().toString();
		}
		else if ( text.startsWith(".borrar") )
		{
			StringTokenizer st = new StringTokenizer(text.substring(".borrar".length()));
			try
			{
				String id = st.nextToken();
				msg = id+ " " +(this.quitarID(id)
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
				this.ponAsignado(false);
			}
			msg = "Modo cambiado a: " +obtModo(execute);
		}
		else if ( text.equals(".limpiar") )
		{
			//ta.setText("");
			msg = "no se como limpiar";
		}
		else if ( text.equals(".version") )
		{
			msg =
"Loro - Sistema Didáctico de Programación\n"+
//Info.obtNombre()+ " " +Info.obtVersion()+ " (Build " +Info.obtBuild()+ ")\n" +
Loro.obtNombre()+ " " +Loro.obtVersion()+ " (Build " +Loro.obtBuild()+ ")\n"
			;
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
					this.ponNivelVerObjeto(num);
				}
				else
				{
					this.ponLongitudVerArreglo(num);
				}
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
			msg = text+ ": comando no entendido.    .? para obtener ayuda.";
		}

		if ( pw != null )
			pw.println(msg);
	}

	///////////////////////////////////////////////////////////////////////
	static String obtModo(boolean execute)
	{
		return execute ? "Interpretación completa con ejecución"
		               : "Interpretación sin ejecucion (sólo chequeo)"
		;
	}


}