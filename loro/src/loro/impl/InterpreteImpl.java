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
import java.util.StringTokenizer;

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
 * @version 2002-01-28
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
	 * Crea un intérprete para acciones interactivas.
	 *
	 * @param r
	 * @param w
	 * @param tabSimbBase Tabla de símbolos de base.
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
	private Nodo _compilar(String text)
	throws CompilacionException
	{
		////////////////////
		// fase sintactica
		Nodo n = derivador.ponTextoFuente(text).derivarAccionInterprete();
		if ( n != null )
		{
			try
			{
				////////////////////
				// fase semantica:
				n.aceptar(chequeador);
			}
			catch(VisitanteException ex)
			{
				throw new CompilacionException(null, ex.getMessage());
			}
		}
		return n;
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
		Nodo n = _compilar(text);
		
		if ( n == null )
		{
			// un comentario.
			return null;
		}

		if ( n instanceof NUtiliza )
		{
			return null;
		}
			
		try
		{
			////////////////////
			// fase ejecucion:
			ejecutor.reset(tabSimbBase, ui);
			n.aceptar(ejecutor);
			Object o = ejecutor.obtRetorno();

			return valorComillas(n, o);
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