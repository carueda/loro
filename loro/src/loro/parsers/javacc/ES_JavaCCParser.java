package loro.parsers.javacc;

import loro.ILanguageInfo;
import loro.DerivacionException;
import loro.derivacion.*;
import loro.arbol.*;
import loro.util.Util;

import java.io.*;
import java.util.*;

import loro.Rango;

//////////////////////////////////////////////////////////////////////
/**
 * Spanish JavaCC-based implementation of IDerivador.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class ES_JavaCCParser extends BaseParser {
	/** The "real" parser. */
	private ParserEs parser;

	/** Creates a javacc-based parser for spanish lexical elements */
	public ES_JavaCCParser() {
		super();
		parser = new ParserEs(new StringReader(""));
	}
	
	public List derivarAccionesInterprete() throws DerivacionException {
		List list = null;
		try {
			list = parser.accionesInterprete();
		}
		catch ( ParseException e ) {
			Token tini;
			Token tfin;
			tini = tfin = e.currentToken.next;
			throw new DerivacionException(
				ConstructorArbol.obtRango(tini, tfin, texto),
				e.getMessage()
			);
		}
		catch ( TokenMgrError e ) {
			throw new DerivacionException(
				ConstructorArbol.obtRango(e.lin, e.col, e.lin, e.col, texto),
				e.getMessage()
			);
		}
		return list;
	}
	
	public NFuente derivarFuente() throws DerivacionException {
		NFuente nodo = null;
		try {
			nodo = parser.fuente();
			NUnidad[] unidades = (NUnidad[]) nodo.obtUnidades();
			for ( int i = 0; i < unidades.length; i++ ) {
				// give each unit its source code:
				unidades[i].setSourceCode(texto);
			}
		}
		catch ( ParseException e ) {
			Token tini;
			Token tfin;
			tini = tfin = e.currentToken.next;
			throw new DerivacionException(
				ConstructorArbol.obtRango(tini, tfin, texto),
				e.getMessage()
			);
		}
		catch ( TokenMgrError e ) {
			throw new DerivacionException(
				ConstructorArbol.obtRango(e.lin, e.col, e.lin, e.col, texto),
				e.getMessage()
			);
		}
		return nodo;
	}

	public TId derivarId() throws DerivacionException {
		TId nodo = null;
		try {
			nodo = parser.tidEOF();
		}
		catch ( ParseException e ) {
			Token tini;
			Token tfin;
			tini = tfin = e.currentToken.next;
			throw new DerivacionException(
				ConstructorArbol.obtRango(tini, tfin, texto),
				e.getMessage()
			);
		}
		catch ( TokenMgrError e ) {
			throw new DerivacionException(
				ConstructorArbol.obtRango(e.lin, e.col, e.lin, e.col, texto),
				e.getMessage()
			);
		}
		return nodo;
	}

	public TNombre derivarNombre() throws DerivacionException {
		TNombre nodo = null;
		try {
			nodo = parser.tnombreEOF();
		}
		catch ( ParseException e ) {
			Token tini;
			Token tfin;
			tini = tfin = e.currentToken.next;
			throw new DerivacionException(
				ConstructorArbol.obtRango(tini, tfin, texto),
				e.getMessage()
			);
		}
		catch ( TokenMgrError e ) {
			throw new DerivacionException(
				ConstructorArbol.obtRango(e.lin, e.col, e.lin, e.col, texto),
				e.getMessage()
			);
		}
		return nodo;
	}

	public IDerivador ponTextoFuente(String fuente) {
		// workaround para el caso en que texto termine con 
		// comentario ``//'', en donde el parser produce un 
		// error lexico equivocado (bug de javacc):
		// simplemente se agrega un cambio de linea:
		fuente += "\n";
		texto = fuente;
		parser.ReInit(new StringReader(texto));
		//parser = new ParserEs(new StringReader(texto));
		ConstructorArbol.ponTexto(texto);
		return this;
	}


	private static String[] keywords1 = {
		"clase",
		"interfaz",
		"interface",
		"objeto",
		"especificacion",
		"especificación",
		"implementacion",
		"implementación",
		"algoritmo",
		"termine",
		"crear",
		"caso",
		"continue",
		"constructor",
		"si_no",
		"si_no_si",
		"haga",
		"para",
		"desde",
		"hasta",
		"bajando",
		"paso",
		"inicio",
		"fin",
		"ciclo",
		"repita",
		"si",
		"entonces",
		"es_instancia_de",
		"como",
		"en",
		"existe",
		"para_todo",
		"global",
		"retorne",
		"siempre",
		"segun",
		"según",
		"mientras",
		"lance",
		"intente",
		"atrape",
		"extiende",
		"implementa",
		"operacion",
		"operación",
		"metodo",
		"método",
//			"lanza",
		"caracter",
		"cadena",
		"entero",
		"real",
		"booleano",
	};
	private static String[] keywords2 = {
		"paquete",
		"utiliza",
	};
	private static String[] keywords3 = {
		"descripcion",
		"descripción",
		"estrategia",
		"pre",
		"pos",
		"entrada",
		"salida",
		"constante",
		"este",
		"éste",
		"super",
		"súper",
	};
	private static String[] literals = {
		"nulo",
		"cierto",
		"falso",
	};
	
	public static class MetaInfo implements ILanguageInfo {
		public String[] getKeywords1() { return keywords1; }
		public String[] getKeywords2() { return keywords2; }
		public String[] getKeywords3() { return keywords3; }
		public String[] getLiterals()  { return literals; }
	}
}

