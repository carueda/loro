package loro.doc;

import loro.arbol.*;

import loro.util.Util;
import loro.util.ManejadorUnidades;
import loro.compilacion.ClaseNoEncontradaException;

import java.io.*;

import loro.visitante.VisitanteProfundidad;
import loro.visitante.VisitanteException;
import loro.tipo.*;

/////////////////////////////////////////////////////////////////////
/**
 * Este visitante genera documentacion para unidades Loro.
 *
 * @author Carlos Rueda
 */
public class VisitanteLoroDoc extends VisitanteProfundidad
{
	/** Base directory. */
	String dir;
	PrintWriter out;

	ManejadorUnidades mu;

	
	/** Interface actual. */
	NInterface interfaceActual = null;

	/** Clase actual. */
	NClase claseActual = null;

	/////////////////////////////////////////////////////////////////////
	/**
	 * Crea un generador de documentacion que escribe
	 * por la salida estándar del sistema.
	 */
	public VisitanteLoroDoc(String dir)
	{
		super();
		this.dir = dir;
		mu = ManejadorUnidades.obtManejadorUnidades();

	}


	/////////////////////////////////////////////////////////////////////
	static String pd(String cad)
	{
		if ( cad.startsWith("\"") )
			cad = cad.substring(1, cad.length() -1);
		else if ( cad.startsWith("''") )
			cad = cad.substring(2, cad.length() -2);

		return cad;
	}


	//////////////////////////////////////////////////////////////////////////
	String hr()
	{
		return "<hr width=\"100%\">\n";
	}
	
	//////////////////////////////////////////////////////////////////////////
	void close()
	{
		out.println(
//			"<div align=\"right\"><font size=\"-1\">" +
//			"<hr width=\"100%\">\n" +
//			"<i>Generado por Loro " +loro.Loro.obtVersion()+ "</i>" +
//			"</font></div>\n" +
			"</body>\n" +
			"</html>\n"
		);
		out.close();
	}

	//////////////////////////////////////////////////////////////////////////
	/**
	 * Replaces an inline tag.
	 * This is assumed to have the form:
	 *	p::q::u.x
	 * where x is one of:  i, o, c, e, a.
	 */
	static String replaceInlineTag(String it, NUnidad n)
	{
		if ( !it.endsWith(".i") 
		&&   !it.endsWith(".o")
		&&   !it.endsWith(".c")
		&&   !it.endsWith(".e")
		&&   !it.endsWith(".a") )
		{
			// don't change it:
			return it;
		}

		int len = it.length();
		String dest = it.substring(0, len - 2);
		char x = it.charAt(len - 1);

		String href = Util.getRelativeLocation(n.obtNombreCompleto(), dest)
			+ "." +x+ ".html"
		;

		// only show the simple name:
		String simple = dest;
		int i;
		if ( (i = simple.lastIndexOf(":")) >= 0 )
		{
			simple = simple.substring(i + 1);
		}

		return "<a href=\"" +href+ "\">" +tt(simple)+ "</a>";
	}

	//////////////////////////////////////////////////////////////////////////
	/**
	* Replaces all ``@{...}'' tags.
	 */
	static String processInlineTags(String s, NUnidad n)
	{
		StringBuffer sb = new StringBuffer();
		int len = "@{".length();
		int i, p = 0;
		while ( (i = s.indexOf("@{", p)) >= 0 )
		{
			// keep first part:
			sb.append(s.substring(p, i));

			// find ending "}"
			int e = s.indexOf("}", i + len);
			if ( e < 0 )
			{
				// malformed inline tag!. Leave remaining as it came
				p = i;
				break;
			}
			String it = s.substring(i + len, e);
			sb.append(replaceInlineTag(it, n));
			p = e + 1;
		}
		sb.append(s.substring(p));
		return sb.toString();
	}

	//////////////////////////////////////////////////////////////////////////
	static String nest(Object s, String tag)
	{
		return "<" +tag+ ">" +s+ "</" +tag+ ">";
	}

	//////////////////////////////////////////////////////////////////////////
	static String tt(Object s)
	{
		return nest(s, "code");
	}

	//////////////////////////////////////////////////////////////////////////
	static String b(Object s)
	{
		return nest(s, "b");
	}

	//////////////////////////////////////////////////////////////////////////
	static String u(Object s)
	{
		return nest(s, "u");
	}

	//////////////////////////////////////////////////////////////////////////
	static String f(String font, Object s)
	{
		return "<font " +font+ ">" +s+ "</font>";
	}

	//////////////////////////////////////////////////////////////////////////
	static String i(Object s)
	{
		return nest(s, "i");
	}

	//////////////////////////////////////////////////////////////////////////
	static String tr(Object s)
	{
		return "<tr valign=\"top\">" +s+ "</tr>";
	}

	//////////////////////////////////////////////////////////////////////////
	static String td(Object s)
	{
		return "<td>" +s+ "</td>";
	}

	//////////////////////////////////////////////////////////////////////////
	static String br()
	{
		return "<br>\n";
	}

	//////////////////////////////////////////////////////////////////////////
	// indent
	static String indent()
	{
		return "<blockquote>\n";
	}

	//////////////////////////////////////////////////////////////////////////
	// unindent
	static String unindent()
	{
		return "</blockquote>\n";
	}

	//////////////////////////////////////////////////////////////////////////
	// spaces
	static String s(int i)
	{
		StringBuffer sb = new StringBuffer();
		while ( i-- > 0 )
		{
			sb.append("&nbsp;");
		}
		return sb.toString();
	}

	/////////////////////////////////////////////////////////////////////
	void createFile(NUnidad n, String title, String ext)
	throws VisitanteException
	{
		String filename = Util.obtStringRuta(n.obtNombreCompleto(), "/");
		if ( dir != null )
		{
			filename = dir + "/" + filename+ ext;
		}

		File file = new File(filename);
		if ( !file.exists() )
		{
			// quiza haya que crear ruta tambien:
			File dirfile = file.getParentFile();
			if ( dirfile != null )
			{
				dirfile.mkdirs();
			}
		}

		try
		{
			out = new PrintWriter(new FileOutputStream(filename), true);
		}
		catch(IOException ex)
		{
			throw new DocumentadorException(
				"Error al crear archivo " +filename+ ": "+
				ex.getMessage()
			);
		}

		String p_name = n.obtNombrePaquete();

		out.println(
			"<html>\n" +
			"<head>\n" +
			"<!-- Generado por lorodoc en " +new java.util.Date()+ "-->\n" +
			"<!-- Loro versión " +loro.Loro.obtVersion()+ "-->\n" +
			"<!-- Fuente de esta unidad: " +n.obtNombreFuente()+ " -->\n"+
			"<!-- user.name: " +System.getProperty("user.name")+ " -->\n"+
			"<title>\n" +
			title +
			"</title>\n" +
			"</head>\n" +
			"<body>\n"
		);

		out.println(
			f("size=\"-1\"",
				"Paquete: "
				+tt(p_name == null ? i("an&oacute;nimo") : b(p_name))
			)
		);
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 */
	public static String formatType(Tipo t, NUnidad n)
	{
		if ( t instanceof TipoEspecificacion )
		{
			TipoEspecificacion tt = (TipoEspecificacion) t;
			String[] tks = tt.obtNombreEspecificacion();
			if ( tks != null )
			{
				String sespec = Util.obtStringRuta(tks);
				String href = Util.getRelativeLocation(n.obtNombreCompleto(), tks) + ".e.html";
				return "<a href=\"" +href+ "\">" +b(t)+ "</a>";
			}
		}
		else if ( t instanceof TipoClase )
		{
			TipoClase tt = (TipoClase) t;
			String[] tks = tt.obtNombreConPaquete();
			if ( tks != null )
			{
				String s = Util.obtStringRuta(tks);
				String href = Util.getRelativeLocation(n.obtNombreCompleto(), tks) + ".c.html";
				return "<a href=\"" +href+ "\">" +b(t)+ "</a>";
			}
		}
		else if ( t.esArreglo() )
		{
			return "[]" + formatType(t.obtTipoElemento(), n);
		}

		return t.toString();
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Muestra el contenido de una afirmacion.
	 */
	static String afir(NAfirmacion a, NUnidad n)
	{
		if ( a == null )
		{
			return tt("cierto");
		}
		else
		{
			String l = a.getLiteralDescription();
			return l != null ? processInlineTags(l, n) : tt(a.obtCadena());
		}
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NAlgoritmo n)
	throws VisitanteException
	{
		String name = n.obtNombreSimpleCadena();
		NDeclaracion[] pent = n.obtParametrosEntrada();
		NDeclaracion[] psal = n.obtParametrosSalida();
		
		String label;
		if ( claseActual == null )
		{
			createFile(n, "Algoritmo " +name, ".a.html");
			label = "algoritmo";
		}
		else
		{
			out.println(hr() +br());
			label = "m&eacute;todo";
		}

		out.println("<table BORDER=0 BGCOLOR=\"#FFEEEE\">");
		
		out.println("<tr bgcolor=\"#FFCCCC\">");
		out.println("<td>");
		out.println("<code>");
		out.println(label+ " " +b(name+ "(" +listArgNames(pent)+ ")"));
		if ( psal.length > 0 )
		{
			out.print(b(" -> " +listArgNames(psal)));
		}
		String[] tespec = n.obtNombreEspecificacion();
		if ( tespec != null )
		{
			out.println(br());
			String sespec = Util.obtStringRuta(tespec);
			out.print(" para la especificaci&oacute;n ");
			String href = Util.getRelativeLocation(n.obtNombreCompleto(), tespec) + ".e.html";
			out.println("<a href=\"" +href+ "\">" +b(sespec)+ "</a>");
		}
		out.println("</code>");
		out.println("</td>");
		out.println("</tr>");

		out.println("<tr><td>");
		out.println(indent());
		TCadenaDoc tstrat = n.obtEstrategia();
		if ( tstrat != null )
		{
			String strat = pd(tstrat.obtCadena());
			out.println(
				//b("Estrategia:")+ br() +
				processInlineTags(strat, n)
			);
		}
		else
		{
			out.println(
				"(" +i(label+ " implementado en " +n.obtLenguajeImplementacion())+ ")"
			);
		}
		out.println(unindent());
		out.println("</td></tr>");

		if ( claseActual == null )
			close();
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 */
	static String listArgNames(NDeclaracion[] p)
	{
		StringBuffer sb = new StringBuffer();
		for ( int i = 0; i < p.length; i++ )
		{
			NDeclaracion dec = p[i];
			if ( i > 0 )
			{
				sb.append(", ");
			}
			sb.append(dec.obtId());
		}
		return sb.toString();
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 */
	static String listArgsAsTable(String label, NDeclaracion[] p, NDescripcion[] d, NUnidad n)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<table border=\"0\" bgcolor=\"#FFDDDD\" width=\"100%\">\n");
		sb.append(listArgsAsRows(label, p, d, n));
		sb.append("</table>\n");
		return sb.toString();
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 */
	static String listArgsAsRows(String label, NDeclaracion[] p, NDescripcion[] d, NUnidad n)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<tr bgcolor=\"#FFCCCC\">\n");
		sb.append(
				td(i(label)) + 
				td(tt(":")) +
				td(i("Tipo")) +
				td(tt(":")) +
				td(i("Descripci&oacute;n"))
		);
		sb.append("</tr>\n");
		if ( p.length > 0 )
		{
			for ( int i = 0; i < p.length; i++ )
			{
				NDeclaracion dec = p[i];
				String desc = "";
				if ( d != null )
				{
					NDescripcion des = d[i];
					desc = td(processInlineTags(pd(des.obtDescripcion().toString()), n));
				}
	
				sb.append(
					tr(
						td(tt(b(dec.obtId()))) + 
						td(tt(":")) +
						td(tt(formatType(dec.obtTipo(), n))) +
						td(tt(":")) +
						desc
					)
				);
				sb.append("\n");
			}
		}
		else
		{
			sb.append(tr(td(i("(No hay)"))));
		}

		return sb.toString();
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 */
	static String listAtrsAsTable(String label, NDeclDesc[] p, NUnidad n)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<table border=\"0\" bgcolor=\"#FFDDDD\" width=\"100%\">\n");
		sb.append(listAtrsAsTable(label, p, n));
		sb.append("</table>\n");
		return sb.toString();
	}
	
	/////////////////////////////////////////////////////////////////////
	/**
	 */
	static String listAtrsAsRows(String label, NDeclDesc[] p, NUnidad n)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<tr bgcolor=\"#FFCCCC\">\n");
		sb.append(
				td(i(label)) + 
				td(tt(":")) +
				td(i("Tipo")) +
				td(tt(":")) +
				td(i("Descripci&oacute;n"))
		);
		sb.append("</tr>\n");
		for ( int i = 0; i < p.length; i++ )
		{
			NDeclDesc d = p[i];
			sb.append(
				tr(
					td(tt(b(d.obtId()))) + 
					td(tt(":")) +
					td(tt(formatType(d.obtTipo(), n))) +
					td(tt(":")) +
					td(processInlineTags(pd(d.obtDescripcion().toString()), n))
				)
			);
			sb.append("\n");
		}
		return sb.toString();
	}
	/////////////////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NEspecificacion n)
	throws VisitanteException
	{
		String name = n.obtNombreSimpleCadena();
		NDeclaracion[] pent = n.obtParametrosEntrada();
		NDeclaracion[] psal = n.obtParametrosSalida();
		NAfirmacion pre = n.obtPrecondicion();
		NAfirmacion pos = n.obtPoscondicion();

		String label;
		if ( interfaceActual == null )
		{
			createFile(n, "Especificaci&oacute;n " +name, ".e.html");
			label = "especificaci&oacute;n";
		}
		else
		{
			out.println(hr() +br());
			label = "operaci&oacute;n";
		}

		out.println("<table BORDER=0 BGCOLOR=\"#FFEEEE\">");
		
		out.println("<tr bgcolor=\"#FFCCCC\">");
		out.println("<td>");
		out.println("<code>");
		out.println(label+ " " +b(name+ "(" +listArgNames(pent)+ ")"));
		if ( psal.length > 0 )
		{
			out.print(b(" -> " +listArgNames(psal)));
		}
		out.println("</code>");
		out.println("</td>");
		out.println("</tr>");
		
		out.println("<tr><td>");
		out.println(indent());
		out.println(
			processInlineTags(pd(n.obtDescripcion().toString()), n)+ br()
		);
		out.println(unindent());
		out.println("</td></tr>");

		out.println("<tr><td>");
		out.println("<table border=\"0\" bgcolor=\"#FFDDDD\" width=\"100%\">\n");

		// INPUT
		NDescripcion[] dent = n.obtDescripcionesEntrada();
		out.println(listArgsAsRows(u("Entrada"), pent, dent, n));

		// vertical space
		out.println("<tr bgcolor=\"#FFEEEE\"></tr>");
		
		// OUTPUT
		NDescripcion[] dsal = n.obtDescripcionesSalida();
		out.println(listArgsAsRows(u("Salida"), psal, dsal, n));

		out.println("</table>\n");
		out.println("</td></tr>");

		// vertical space
		out.println("<tr bgcolor=\"#FFEEEE\"></tr>");

		out.println("<tr><td>");

		out.println("<table border=\"0\" bgcolor=\"#FFDDDD\" width=\"100%\">\n");

		out.println("<tr bgcolor=\"#FFCCCC\">");
		out.println(td(u("Precondici&oacute;n:")));
		out.println("</tr>");
		out.println(tr(td(afir(pre, n))));

		// vertical space
		out.println("<tr bgcolor=\"#FFEEEE\"></tr>");

		out.println("<tr bgcolor=\"#FFCCCC\">");
		out.println(td(u("Poscondici&oacute;n:")));
		out.println("</tr>");
		out.println(tr(td(afir(pos, n))));
		out.println("</table>\n");

		out.println("</td></tr>");

		if ( interfaceActual == null )
			close();
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NClase n)
	throws VisitanteException
	{
		String name = n.obtNombreSimpleCadena();
		NDeclDesc[] pent = n.obtParametrosEntrada();

		createFile(n, "Clase " +name, ".c.html");

		out.println("<table BORDER=0 BGCOLOR=\"#FFEEEE\">");
		
		out.println("<tr bgcolor=\"#FFCCCC\">");
		out.println("<td>");
		out.println("<code>");
		out.println("clase " +b(name));
		try
		{
			NClase superClass = mu.obtSuperClase(n);
			if ( superClass != null )
			{
				String[] text = superClass.obtNombreCompleto();
				out.println(br());
				String sext = Util.obtStringRuta(text);
				out.print(" extiende ");
				String href = Util.getRelativeLocation(n.obtNombreCompleto(), sext) + ".c.html";
				out.println("<a href=\"" +href+ "\">" +b(sext)+ "</a>");
			}
		}
		catch (ClaseNoEncontradaException ex)
		{
			out.print(" !!superclase no encontrada!! ");
		}
		out.println("</code>");
		out.println("</td>");
		out.println("</tr>");

		out.println("<tr><td>");
		out.println(indent());

		out.println(
			processInlineTags(pd(n.obtDescripcion().toString()), n)+ br()
		);
		out.println(unindent());
		out.println("</td></tr>");

		out.println("<tr><td>");
		out.println("<table border=\"0\" bgcolor=\"#FFDDDD\" width=\"100%\">\n");

		// ATTRIBUTES
		out.println(listAtrsAsRows(u("Atributo"), pent, n));

		out.println("</table>\n");
		out.println("</td></tr>");

		// ... métodos:             P E N D I N G 
		claseActual = n;
		visitarLista(n.obtMetodosDeclarados());
		claseActual = null;

		close();
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 */
	public void visitar(NInterface n)
	throws VisitanteException
	{
		String name = n.obtNombreSimpleCadena();

		createFile(n, "Interface " +name, ".i.html");

		out.println("<code>");
		out.println("interface " +b(name));
		out.println("</code>");

		out.println(indent());

		out.println(
			//b("Descripci&oacute;n:") +br()+
			processInlineTags(pd(n.obtDescripcion().toString()), n)+ br()
		);

		out.println(br());

		// ... operaciones:
		interfaceActual = n;
		visitarLista(n.obtOperacionesDeclaradas());
		interfaceActual = null;
		
		
		out.println(unindent());

		close();
	}

	/////////////////////////////////////////////////////////////////////
	/**
	 * Visita un fuente de compilación.
	 */
	public void visitar(NFuente n)
	throws VisitanteException
	{
		visitarLista((Nodo[]) n.obtUnidades());
	}

}