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


	/////////////////////////////////////////////////////////////////////
	/**
	 * Crea un intérprete para acciones interactivas.
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
	
			try
			{
				ejecutor.ponSenalPP(0);
			}
			catch(InterruptedException ex)
			{
			}
			
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
			ejecutor.ponSalidaEstandar(new PrintWriter(w));
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
				Nodo n = (Nodo) it.next();
			
				try
				{
					////////////////////
					// fase semantica:
					chequeador.chequear(n);
				}
				catch ( ChequeadorException se )
				{
					throw new CompilacionException(se.obtRango(), se.getMessage());
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
		String ret = null;
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
				Nodo n = (Nodo) it.next();
			
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
					Object o = ejecutor.obtRetorno();
					ret = valorComillas(n, o);
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
	public void nextStep()
	throws InterruptedException
	{
		ejecutor.ponSenalPP(0);
	}

	///////////////////////////////////////////////////////////////////////
	public void resume()
	{
		ejecutor.resume();
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
}