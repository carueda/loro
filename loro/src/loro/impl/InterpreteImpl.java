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
 *
 * @author Carlos Rueda
 * @version 2002-10-22
 */
public class InterpreteImpl implements IInterprete
{
	PrintWriter pw;
	BufferedReader br;


	TablaSimbolos tabSimbBase;
	NUnidadInterprete ui;

	Chequeador chequeador;
	EjecutorTerminable ejecutor;

	boolean execute = true;

	IDerivador derivador;


	/////////////////////////////////////////////////////////////////////
	/**
	 * Crea un int�rprete para acciones interactivas.
	 *
	 * @param r
	 * @param w
	 * @param tabSimbBase Tabla de s�mbolos de base.
	 * @param loroClassLoader
	 */
	public InterpreteImpl(Reader r, Writer w, TablaSimbolos tabSimbBase, LoroClassLoader loroClassLoader)
	{
		super();

		derivador = ManejadorDerivacion.obtDerivador();

		pw = new PrintWriter(w);
		br = new BufferedReader(r);

		this.tabSimbBase = tabSimbBase;
		ui = new NUnidadInterprete();

		chequeador = new Chequeador(tabSimbBase, ui);
		ejecutor = new EjecutorTerminable(tabSimbBase, ui);
		ejecutor.ponClassLoader(loroClassLoader);

		ejecutor.ponEntradaEstandar(br);
		ejecutor.ponSalidaEstandar(pw);
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
			try
			{
				for ( Iterator it = list.iterator(); it.hasNext(); )
				{
					Nodo n = (Nodo) it.next();
				
					////////////////////
					// fase semantica:
					n.aceptar(chequeador);
				}
			}
			catch(VisitanteException ex)
			{
				throw new CompilacionException(null, ex.getMessage());
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
}