package loro.parsers.javacc;

import loro.DerivacionException;
import loro.derivacion.*;
import loro.arbol.*;
import loro.util.Util;

import java.io.*;
import java.util.*;


import loro.Rango;

//////////////////////////////////////////////////////////////////////
/**
 * Implementación de Derivador basada en JavaCC.
 *
 * @author Carlos Rueda
 * @version 2002-08-12
 */
public class DerivadorJavaCC implements IDerivador
{
	/** 
	 * Texto en compilacion. Esta cadena siempre se deja
	 * disponible para despachar adecuadamente la creacion
	 * de errores, en cuanto a rangos comprometidos.
	 */
	String texto;

	
	/** El "parser" javacc propiamente. */
	LoroIParser parser;

	////////////////////////////////////////////////////////////////
	/**
	 * Crea un DerivadorImpl.
	 */
	public DerivadorJavaCC()
	{
		texto = "";
		parser = new LoroIParser(new StringReader(""));
	}
	
	///////////////////////////////////////////////////////////////////////
	public List derivarAccionesInterprete()
	throws DerivacionException
	{
		List list = null;
		
		try
		{
			list = parser.accionesInterprete();
		}
		catch ( ParseException e )
		{
			Token tini;
			Token tfin;

			tini = tfin = e.currentToken.next;

			throw new DerivacionException(
				ConstructorArbol.obtRango(tini, tfin, texto),
				e.getMessage()
			);
			
			
		}
		catch ( TokenMgrError e )
		{
			throw new DerivacionException(
				ConstructorArbol.obtRango(e.lin, e.col, e.lin, e.col, texto),
				e.getMessage()
			);
		}

		return list;
	}
	
	///////////////////////////////////////////////////////////////////////
	public NFuente derivarFuente()
	throws DerivacionException
	{
		NFuente nodo = null;

		try
		{
			nodo = parser.fuente();
			NUnidad[] unidades = (NUnidad[]) nodo.obtUnidades();
			for ( int i = 0; i < unidades.length; i++ )
			{
				// Asociar a cada unidad el código fuente de donde proviene:
				unidades[i].setSourceCode(texto);

// Nota (2003-02-09)				
// código anterior erróneo; se deja por un tiempo para hacer pruebas de 
// dependencias no advertidas en este momento.
//				Rango r = unidades[i].obtRango();
//				int start = r.obtPosIni();
//				int end   = r.obtPosFin() + 1;
//				unidades[i].setSourceCode(texto.substring(start, end));
			}
		}
		catch ( ParseException e )
		{
			Token tini;
			Token tfin;

			tini = tfin = e.currentToken.next;

			throw new DerivacionException(
				ConstructorArbol.obtRango(tini, tfin, texto),
				e.getMessage()
			);
			
			
		}
		catch ( TokenMgrError e )
		{
			throw new DerivacionException(
				ConstructorArbol.obtRango(e.lin, e.col, e.lin, e.col, texto),
				e.getMessage()
			);
		}

		return nodo;
	}

	///////////////////////////////////////////////////////////////////////
	public TId derivarId()
	throws DerivacionException
	{
		TId nodo = null;

		try
		{
			nodo = parser.tidEOF();
		}
		catch ( ParseException e )
		{
			Token tini;
			Token tfin;

			tini = tfin = e.currentToken.next;

			throw new DerivacionException(
				ConstructorArbol.obtRango(tini, tfin, texto),
				e.getMessage()
			);
			
			
		}
		catch ( TokenMgrError e )
		{
			throw new DerivacionException(
				ConstructorArbol.obtRango(e.lin, e.col, e.lin, e.col, texto),
				e.getMessage()
			);
		}

		return nodo;
	}

	///////////////////////////////////////////////////////////////////////
	public TNombre derivarNombre()
	throws DerivacionException
	{
		TNombre nodo = null;

		try
		{
			nodo = parser.tnombreEOF();
		}
		catch ( ParseException e )
		{
			Token tini;
			Token tfin;

			tini = tfin = e.currentToken.next;

			throw new DerivacionException(
				ConstructorArbol.obtRango(tini, tfin, texto),
				e.getMessage()
			);
			
			
		}
		catch ( TokenMgrError e )
		{
			throw new DerivacionException(
				ConstructorArbol.obtRango(e.lin, e.col, e.lin, e.col, texto),
				e.getMessage()
			);
		}

		return nodo;
	}



	///////////////////////////////////////////////////////////////////////
	public String obtTextoFuente()
	{
		return texto;
	}

	///////////////////////////////////////////////////////////////////////
	public IDerivador ponTextoFuente(Reader fuente)
	{
		// leer todo el reader fuente como cadena
		// para tener disponibilidad del texto siempre.
		
		BufferedReader br = new BufferedReader(fuente, 2*4096);
		StringBuffer sb = new StringBuffer();
		int ch;
		try
		{
			while ( (ch = br.read()) != -1 ) 
			{
				sb.append((char) ch);
			}
		}
		catch ( IOException ex )
		{
			throw new RuntimeException("Oops: " +ex.getMessage());
		}

		return ponTextoFuente(sb.toString());
	}

	///////////////////////////////////////////////////////////////////////
	public IDerivador ponTextoFuente(String fuente)
	{
		// workaround para el caso en que texto termine con 
		// comentario ``//'', en donde el parser produce un 
		// error lexico equivocado (bug de javacc):
		// simplemente se agrega un cambio de linea:
		fuente += "\n";

		texto = fuente;

		parser.ReInit(new StringReader(texto));

		ConstructorArbol.ponTexto(texto);
		
		return this;
	}
}