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
 * English JavaCC-based implementation of IDerivador.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class EN_JavaCCParser extends BaseParser {
	/** The "real" parser. */
	private LoroParser parser;

	/** Creates a javacc-based parser for english lexical elements */
	public EN_JavaCCParser() {
		super();
		parser = new LoroParser(new StringReader(""));
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
		//parser = new LoroParser(new StringReader(texto));
		ConstructorArbol.ponTexto(texto);
		return this;
	}
	
	private static String[] keywords1 = {
		"class",
		"interface",
		"object",
		"specification",
		"implementation",
		"algorithm",
		"break",
		"new",
		"case",
		"continue",
		"constructor",
		"else",
		"elseif",
		"do",
		"for",
		"from",
		"until",
		"to",
		"downto",
		"by",
		"begin",
		"end",
		"loop",
		"repeat",
		"if",
		"then",
		"instanceof",
		"as",
		"in",
		"exists",
		"forall",
		"global",
		"return",
		"finally",
		"switch",
		"while",
		"throw",
		"try",
		"catch",
		"extends",
		"implements",
		"operation",
		"method",
//			"throws",
		"char",
		"string",
		"int",
		"real",
		"boolean",
	};
	private static String[] keywords2 = {
		"package",
		"uses",
	};
	private static String[] keywords3 = {
		"description",
		"pre",
		"post",
		"input",
		"output",
		"constant",
		"this",
		"super",
	};
	private static String[] literals = {
		"null",
		"true",
		"false",
	};
	
	public static class MetaInfo implements ILanguageInfo {
		public String[] getKeywords1() { return keywords1; }
		public String[] getKeywords2() { return keywords2; }
		public String[] getKeywords3() { return keywords3; }
		public String[] getLiterals()  { return literals; }
	}
}

