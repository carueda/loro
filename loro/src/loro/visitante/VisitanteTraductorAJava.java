package loro.visitante;

import loro.arbol.*;

import loro.java.SoporteJava;

import java.io.PrintWriter;
import java.io.Writer;
import java.io.OutputStream;

import loro.tipo.*;

/////////////////////////////////////////////////////////////////////
/**
 * Este visitante genera código Java.
 * 
 * @author Carlos Rueda
 * @version 0.1 Jun/09/1999 - Muy preliminar
 */
public class VisitanteTraductorAJava
			extends VisitanteProfundidad
{
	PrintWriter out;
	protected Espaciado spc = new Espaciado(3);

	/**
	* Se está describiendo una entrada?
	* Vea visitar(NDescripcion n)
	*/
	boolean descrEntrada = true;
	/**
	 * Crea un generador a fuente Java que escribe
	 * por la salida estándar del sistema.
	 */
	public VisitanteTraductorAJava()
	{
		out = new PrintWriter(System.out, true);
	}
	/**
	 * Crea un generador a fuente Java que escribe
	 * por la salida indicada.
	 */
	public VisitanteTraductorAJava(OutputStream o)
	{
		out = new PrintWriter(o, true);
	}
	/**
	 * Crea un generador a fuente Java que escribe
	 * por la salida indicada.
	 */
	public VisitanteTraductorAJava(Writer o)
	{
		out = new PrintWriter(o, true);
	}
	static String pd(String cad)
	{
		if ( cad.startsWith("\"") )
			cad = cad.substring(1, cad.length() -1);

		return cad;
	}

	public void visitar(NACadena n)
	throws VisitanteException
	{
		if ( n.obtExpresion() instanceof NLiteralNulo )
		{
			// @nulo  -->  "null"
			out.print("\"null\"");
		}
		else if ( n.obtExpresion() instanceof NLiteralCadena )
		{
			// @"algo"  -->  "algo"
			n.obtExpresion().aceptar(this);
		}
		else if ( n.obtTipo().esObjeto() )
		{
			// @obj  -->  obj.toString()
			n.obtExpresion().aceptar(this);
			out.print(".toString()");
		}
		else
		{
			// @x  -->  (""+x)
			out.print("(\"\"+");
			n.obtExpresion().aceptar(this);
			out.print(")");
		}
	}
	/**
	 */
	public void visitar(NAsignacion n)
	throws VisitanteException
	{
		visitarExprBin(n, "=");
	}
	public void visitar(NCardinalidad n)
	throws VisitanteException
	{
		n.obtExpresion().aceptar(this);
		if ( n.obtTipo().esCadena() )
		{
			out.print(".getLength()");
		}
		else // n.obtTipo().esArreglo()
		{
			out.print(".length");
		}
	}
	/**
	 */
	public void visitar(NClase n)
	throws VisitanteException
	{
		out.println("/**");
		spc.updateSpc(+1);
		out.println(spc.spc + pd(n.obtDescripcion() == null ? "" :n.obtDescripcion()));
		spc.updateSpc(-1);
		out.println("*/");
		out.println("public class " +n.obtId()+ " {\n");
		spc.updateSpc(+1);
		out.println(spc.spc+ "// ATTRIBUTES:\n");
		out.print(spc.spc);
		visitarLista(n.obtParametrosEntrada(), ";\n" + spc.spc);
		out.println(";\n");
		out.println(spc.spc+ "// CONSTRUCTORS:\n");
		out.print(spc.spc);
		visitarLista(n.obtConstructores(), "\n" + spc.spc);
		spc.updateSpc(-1);
		out.println("\n} // end class " +n.obtId());
	}
	public void visitar(NCondicion n)
	throws VisitanteException
	{
		visitarConParentesis(n.obtCondicion());
		out.print(" ? ");
		visitarConParentesis(n.obtPrimeraAlternativa());
		out.print(" : ");
		visitarConParentesis(n.obtSegundaAlternativa());
	}
	/**
	 */
	public void visitar(NConstructor n)
	throws VisitanteException
	{
		if ( n.esPorDefecto() )
			return;

		String id = n.obtClase().obtId().toString();
		out.println("/**");
		spc.updateSpc(+1);
		out.println(spc.spc + (n.obtDescripcion() == null ? "" : n.obtDescripcion()) );
		spc.updateSpc(-1);
		out.println(spc.spc + "*/");
		out.print(spc.spc + "public " +id+ "(");
		visitarLista(n.obtParametrosEntrada(), ", ");
		out.println(") {");
		//	visitarLista(n.obtDescripcionesEntrada());
		//	if ( n.obtPrecondicion() != null )
		//		n.obtPrecondicion().aceptar(this);
		//	if ( n.obtPoscondicion() != null )
		//		n.obtPoscondicion().aceptar(this);
		spc.updateSpc(+1);
		out.println(spc.spc+ "// statements");
		out.print(spc.spc);
		visitarLista(n.obtAcciones(), ";\n" + spc);
		out.println(";");
		spc.updateSpc(-1);
		out.println(spc.spc + "} // end constructor " +id);
	}
	public void visitar(NCorrDer n)
	throws VisitanteException
	{
		visitarExprBin(n);
	}
	public void visitar(NCorrDerDer n)
	throws VisitanteException
	{
		visitarExprBin(n);
	}
	public void visitar(NCorrIzq n)
	throws VisitanteException
	{
		visitarExprBin(n);
	}
	public void visitar(NDecision n)
	throws VisitanteException
	{
		out.print("if ( ");
		n.obtCondicion().aceptar(this);
		out.println(" ) {");
		spc.updateSpc(+1);
		out.print(spc.spc);
		visitarLista(n.obtAccionesCierto(), ";\n" + spc);
		out.println(";");
		spc.updateSpc(-1);
		out.println(spc+ "}");
		if ( n.obtAccionesFalso() != null )
		{
			out.println(spc+ "else {");
			spc.updateSpc(+1);
			out.print(spc.spc);
			visitarLista(n.obtAccionesFalso(), ";\n" + spc);
			out.println(";");
			spc.updateSpc(-1);
			out.println(spc+ "}");
		}
	}
	/**
	 * Visita una declaración de uno o más identificadores.
	 */
	public void visitar(NDeclaracion n)
	throws VisitanteException
	{
		if ( n.esConstante() )
			out.print("final ");

		out.print(SoporteJava.obtTipoJava(n.obtTipo())+ " " +n.obtId());
		if ( n.obtExpresion() != null )
		{
			out.print(" = ");
			n.obtExpresion().aceptar(this);
		}
	}
	public void visitar(NDescripcion n)
	throws VisitanteException
	{
		if ( descrEntrada )
			out.print("@param " +n.obtId()+ " ");
		else
			out.print("@return ");

		out.print(pd(n.obtDescripcion()));
	}
	public void visitar(NDiferente n)
	throws VisitanteException
	{
		visitarExprBin(n);
	}
	public void visitar(NDivReal n)
	throws VisitanteException
	{
		visitarExprBin(n);
	}
	public void visitar(NEspecificacion n)
	throws VisitanteException
	{
		out.println("/**");
		spc.updateSpc(+1);
		out.println(spc.spc + "Interface for specification " +n.obtId()+ ".");
		spc.updateSpc(-1);
		out.println("*/");
		out.println("public interface E" +n.obtId()+ " {\n");
		spc.updateSpc(+1);

		Tipo t = null;
		NDeclaracion[] n_psal = n.obtParametrosSalida();
		if ( n_psal.length > 0 )
			t = n_psal[0].obtTipo();

		out.println(spc+ "/**");
		spc.updateSpc(+1);
		out.println(spc.spc + pd(n.obtDescripcion() == null ? "" : n.obtDescripcion()));
		out.print("\n" +spc);
		descrEntrada = true;
		visitarLista(n.obtDescripcionesEntrada(), "\n" +spc);
		if ( t != null )
		{
			out.print("\n" +spc);
			descrEntrada = false;
			visitarLista(n.obtDescripcionesSalida(), "\n" +spc);
		}
		out.println();
		spc.updateSpc(-1);
		out.println(spc + "*/");

		String jtipo = t == null ? "void" : SoporteJava.obtTipoJava(t);

		out.print(spc.spc + "public " +jtipo+ " " +n.obtId()+ "(");
		NDeclaracion[] n_pent = n.obtParametrosEntrada();
		visitarLista(n_pent, ", ");
		out.println(");");

		//	visitarLista(n.dent);
		//	if ( n.pre != null )
		//		n.pre.aceptar(this);
		//	if ( n.pos != null )
		//		n.pos.aceptar(this);


		spc.updateSpc(-1);
		out.println("\n} // fin interface E" +n.obtId());

	}
	/**
	 * Visita un fuente de compilación.
	 */
	public void visitar(NFuente n)
	throws VisitanteException
	{
		out.println("\n// Generated by loroj (" +new java.util.Date()+ ")\n");
		visitarLista((Nodo[]) n.obtUnidades());
	}
	/**
	 */
	public void visitar(NId n)
	throws VisitanteException
	{
		out.print(n.obtId());
	}
	public void visitar(NIgual n)
	throws VisitanteException
	{
		visitarExprBin(n, "==");
	}
	public void visitar(NLiteralBooleano n)
	throws VisitanteException
	{
		out.print(n.obtValorBooleano() ? "true" : "false");
	}
	////////////////////////////////////////////////
	public void visitar(NLiteralCadena n)
	throws VisitanteException
	{
		out.print(n.obtImagen());
	}
	public void visitar(NLiteralEntero n)
	throws VisitanteException
	{
		out.print(n.obtValor());
	}
	public void visitar(NLiteralNulo n)
	throws VisitanteException
	{
		out.print("null");
	}
	public void visitar(NMas n)
	throws VisitanteException
	{
		visitarExprBin(n);
	}
	public void visitar(NMayor n)
	throws VisitanteException
	{
		visitarExprBin(n);
	}
	public void visitar(NMayorIgual n)
	throws VisitanteException
	{
		visitarExprBin(n);
	}
	public void visitar(NMenor n)
	throws VisitanteException
	{
		visitarExprBin(n);
	}
	public void visitar(NMenorIgual n)
	throws VisitanteException
	{
		visitarExprBin(n);
	}
	public void visitar(NMenos n)
	throws VisitanteException
	{
		visitarExprBin(n);
	}
	public void visitar(NMientras n)
	throws VisitanteException
	{
		out.print("while ( ");
		n.obtCondicion().aceptar(this);
		out.println(" ) {");
		spc.updateSpc(+1);
		out.print(spc.spc);
		visitarLista(n.obtAcciones(), ";\n" + spc);
		out.println(";");
		spc.updateSpc(-1);
		out.print(spc+ "}");

	}
	public void visitar(NMod n)
	throws VisitanteException
	{
		visitarExprBin(n);
	}
	public void visitar(NO n)
	throws VisitanteException
	{
		visitarExprBin(n);
	}
	public void visitar(NOArit n)
	throws VisitanteException
	{
		visitarExprBin(n);
	}
	/**
	 * PENDIENTE PROVISIONAL
	 */
	public void visitar(Nodo n)
	throws VisitanteException
	{
		out.print(spc.spc+ "::visitar(Nodo)");
	}
	public void visitar(NOExc n)
	throws VisitanteException
	{
		visitarExprBin(n);
	}
	public void visitar(NPor n)
	throws VisitanteException
	{
		visitarExprBin(n);
	}
	public void visitar(NY n)
	throws VisitanteException
	{
		visitarExprBin(n);
	}
	public void visitar(NYArit n)
	throws VisitanteException
	{
		visitarExprBin(n);
	}
	/**
	 * Pone paréntesis si es necesario a la expresión.
	 * Si ésta ya los tiene, se le dejan.
	 */
	public void visitarConParentesis(NExpresion n)
	throws VisitanteException
	{
		boolean poner = !n.esParentizada()
		                && (n instanceof NExprBin || n instanceof NExprUn);

		if ( poner ) out.print("(");
		n.aceptar(this);
		if ( poner ) out.print(")");
	}
	public void visitarExprBin(NExprBin n)
	throws VisitanteException
	{
		visitarExprBin(n, n.obtOperador());
	}
	/**
	 * Visita una expresión binaria.
	 * Toma incondicionalmente el operador dado
	 * (no el del nodo n).
	 */
	public void visitarExprBin(NExprBin n, String op)
	throws VisitanteException
	{
		if ( n.esParentizada() )
			out.print("(");

		n.obtExpresionIzq().aceptar(this);

		out.print(" " +op+ " ");

		n.obtExpresionDer().aceptar(this);

		if ( n.esParentizada() )
			out.print(")");
	}
	/**
	 * Visita una expresión unaria.
	 */
	public void visitarExprUn(NExprUn n)
	throws VisitanteException
	{
		visitarExprUn(n, n.obtOperador());
	}
	/**
	 * Visita una expresión unaria.
	 * Toma incondicionalmente el operador dado
	 * (no el del nodo n).
	 */
	public void visitarExprUn(NExprUn n, String op)
	throws VisitanteException
	{
		out.print(op);

		if ( n.esParentizada() )
			out.print("(");

		n.obtExpresion().aceptar(this);

		if ( n.esParentizada() )
			out.print(")");
	}
	/**
	 * Auxiliar para manejar espacios separadores.
	 * Se pone el separador dado entre visita y visita
	 * a los nodos (no se pone al final).
	 */
	public void visitarLista(Nodo[] nodos, String sep)
	throws VisitanteException
	{
		for ( int i = 0; i < nodos.length; i++ )
		{
			nodos[i].aceptar(this);
			if ( i < nodos.length -1 )
				out.print(sep);
		}
	}
}