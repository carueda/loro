package loro.parsers.javacc;

import loro.derivacion.*;
import loro.arbol.*;
import loro.util.Util;
import loro.Rango;

import java.io.*;
import java.util.*;


//////////////////////////////////////////////////////////////////////
/**
 * Clase auxilar del derivador encargada de crear cada nodo del 
 * arbol de derivacion.
 *
 * @author Carlos Rueda
 */
class ConstructorArbol
{
	//////////////////////////////////////////////////////////////////
	static NACadena crearNACadena(Token ti, NExpresion e)
	{
		Rango rango = obtRango(ti, e);
		return new NACadena(rango, e);
	}
	
	//////////////////////////////////////////////////////////////////
	static NAfirmacion crearNAfirmacion(
		Token ti,
		NExpresion e,
		Token tf
	)
	{
		Rango rango = obtRango(ti, tf);
		String cadena = texto.substring(rango.obtPosIni(), rango.obtPosFin());
		return new NAfirmacion(rango, cadena, e);
	}

	//////////////////////////////////////////////////////////////////
	static NAfirmacion crearNAfirmacion(
		Token ti,
		Token tdoc,
		Token tf
	)
	{
		Rango rango = obtRango(ti, tf);
		String cadena = texto.substring(rango.obtPosIni(), rango.obtPosFin());
		String textCond = tdoc.image;
		return new NAfirmacion(rango, cadena, textCond);
	}

	//////////////////////////////////////////////////////////////////
	static NAsignacion crearNAsignacion(NExpresion v, NExpresion e)
	{
		return new NAsignacion(v, e);
	}
	//////////////////////////////////////////////////////////////////
	static NCardinalidad crearNCardinalidad(Token ti, NExpresion e)
	{
		Rango rango = obtRango(ti, e);
		return new NCardinalidad(rango, e);
	}
	//////////////////////////////////////////////////////////////////
	static NCaso crearNCaso(
		Token ti,
		NExpresion e, Nodo[] a, boolean cfc,
		Token tf
	)
	{
		Rango rango;
		if ( tf != null )
		{
			rango = obtRango(ti, tf);
		}
		else if ( a.length > 0 )
		{
			rango = obtRango(ti, a[a.length - 1]);
		}
		else
		{
			rango = obtRango(ti, e);
		}
		
		return new NCaso(
			rango,
			e, a, cfc
		);
	}


	//////////////////////////////////////////////////////////////////
	static NCondicion crearNCondicion(NExpresion e, NExpresion f, NExpresion g)
	{
		Rango rango = new Rango(e.obtRango(), g.obtRango());
		return new NCondicion(rango, e, f, g);
	}



	//////////////////////////////////////////////////////////////////
	static NCorrDer crearNCorrDer(NExpresion e, NExpresion f)
	{
		return new NCorrDer(e, f);
	}
	//////////////////////////////////////////////////////////////////
	static NCorrDerDer crearNCorrDerDer(NExpresion e, NExpresion f)
	{
		return new NCorrDerDer(e, f);
	}
	//////////////////////////////////////////////////////////////////
	static NCorrIzq crearNCorrIzq(NExpresion e, NExpresion f)
	{
		return new NCorrIzq(e, f);
	}
	//////////////////////////////////////////////////////////////////
	static NCrearArreglo crearNCrearArreglo(
		Token ti,
		NExpresion e,
		NExpresion g,
		NExpresion f
	)
	{
		Rango rango = obtRango(ti, f);
		
		return new NCrearArreglo(
			rango,
			e,
			g,
			f
		);
	}

	//////////////////////////////////////////////////////////////////
	static NExpresionArreglo crearNExpresionArreglo(
		Token ti,
		List list,
		Token tf
	)
	{
		Rango rango = obtRango(ti, tf);
		
		return new NExpresionArreglo(
			rango,
			(NExpresion[]) list.toArray(new NExpresion[list.size()])
		);
	}

	//////////////////////////////////////////////////////////////////
	static NDecision crearNDecision(
		Token ti,
		NExpresion e, Nodo[] as,
		NDecisionSiNoSi[] sinosis,
		Nodo[] an,
		Token tf
	)
	{
		Rango rango = obtRango(ti, tf);
		return new NDecision(
			rango,
			e, as,
			sinosis,
			an
		);
	}
	
	//////////////////////////////////////////////////////////////////
	static NDecisionSiNoSi crearNDecisionSiNoSi(
		Token ti,
		NExpresion e, Nodo[] as
	)
	{
		Rango rango;
		if ( as.length > 0 )
		{
			rango = obtRango(ti, as[as.length - 1]);
		}
		else
		{
			rango = obtRango(ti, e);
		}
		return new NDecisionSiNoSi(
			rango,
			e, as
		);
	}
	
	//////////////////////////////////////////////////////////////////
	static NDecisionMultiple crearNDecisionMultiple(
		Token ti,
		NExpresion e, NCaso[] c, NCaso sn,
		Token tf
	)
	{
		Rango rango = obtRango(ti, tf);
		return new NDecisionMultiple(
			rango,
			e, c, sn
		);
	}





	//////////////////////////////////////////////////////////////////
	static NDiferente crearNDiferente(NExpresion e, NExpresion f)
	{
		return new NDiferente(e, f);
	}
	//////////////////////////////////////////////////////////////////
	static NDivReal crearNDivReal(NExpresion e, NExpresion f)
	{
		return new NDivReal(e, f);
	}
	//////////////////////////////////////////////////////////////////
	static NEquivalencia crearNEquivalencia(NExpresion e, NExpresion f)
	{
		return new NEquivalencia(e, f);
	}

	//////////////////////////////////////////////////////////////////
	static NEste crearNEste(Token t)
	{
		Rango rango = obtRango(t, t);
		return new NEste(rango);
	}

	//////////////////////////////////////////////////////////////////
	static NFuente crearNFuente(NPaquete paquete, NUtiliza[] autz, Nodo[] n)
	{
		return new NFuente(paquete, autz, n);
	}

	//////////////////////////////////////////////////////////////////
	static NIgual crearNIgual(NExpresion e, NExpresion f)
	{
		return new NIgual(e, f);
	}
	//////////////////////////////////////////////////////////////////
	static NImplicacion crearNImplicacion(NExpresion e, NExpresion f)
	{
		return new NImplicacion(e, f);
	}
	//////////////////////////////////////////////////////////////////
	static NInvocacion crearNInvocacion(NExpresion e, NExpresion[] a, Token tf)
	{
		Rango rango = obtRango(e, tf);
		return new NInvocacion(rango, e, a);
	}
	//////////////////////////////////////////////////////////////////
	static NLiteralBooleano crearNLiteralBooleano(Token t)
	{
		Rango rango = obtRango(t, t);
		return new NLiteralBooleano(rango, t.image);
	}

	//////////////////////////////////////////////////////////////////
	static NLiteralCadena crearNLiteralCadena(Token t)
	{
		Rango rango = obtRango(t, t);
		return new NLiteralCadena(rango, t.image);
	}
	//////////////////////////////////////////////////////////////////
	static NLiteralCaracter crearNLiteralCaracter(Token t)
	{
		Rango rango = obtRango(t, t);
		return new NLiteralCaracter(rango, t.image);
	}
	//////////////////////////////////////////////////////////////////
	static NLiteralEntero crearNLiteralEntero(Token t)
	{
		Rango rango = obtRango(t, t);
		return new NLiteralEntero(rango, t.image);
	}
	//////////////////////////////////////////////////////////////////
	static NLiteralNulo crearNLiteralNulo(Token t)
	{
		Rango rango = obtRango(t, t);
		return new NLiteralNulo(rango, t.image);
	}

	//////////////////////////////////////////////////////////////////
	static NLiteralReal crearNLiteralReal(Token t)
	{
		Rango rango = obtRango(t, t);
		return new NLiteralReal(rango, t.image);
	}

	//////////////////////////////////////////////////////////////////
	static NMas crearNMas(NExpresion e, NExpresion f)
	{
		return new NMas(e, f);
	}
	//////////////////////////////////////////////////////////////////
	static NMayor crearNMayor(NExpresion e, NExpresion f)
	{
		return new NMayor(e, f);
	}
	//////////////////////////////////////////////////////////////////
	static NMayorIgual crearNMayorIgual(NExpresion e, NExpresion f)
	{
		return new NMayorIgual(e, f);
	}
	//////////////////////////////////////////////////////////////////
	static NMenorIgual crearNMenorIgual(NExpresion e, NExpresion f)
	{
		return new NMenorIgual(e, f);
	}

	//////////////////////////////////////////////////////////////////
	static NMenos crearNMenos(NExpresion e, NExpresion f)
	{
		return new NMenos(e, f);
	}

	//////////////////////////////////////////////////////////////////
	static NMod crearNMod(NExpresion e, NExpresion f)
	{
		return new NMod(e, f);
	}
	//////////////////////////////////////////////////////////////////
	static NNeg crearNNeg(Token ti, NExpresion e)
	{
		Rango rango = obtRango(ti, e);
		return new NNeg(rango, e);
	}
	//////////////////////////////////////////////////////////////////
	static NNo crearNNo(Token ti, NExpresion e)
	{
		Rango rango = obtRango(ti, e);
		return new NNo(rango, e);
	}
	//////////////////////////////////////////////////////////////////
	static NNoBit crearNNoBit(Token ti, NExpresion e)
	{
		Rango rango = obtRango(ti, e);
		return new NNoBit(rango, e);
	}

	//////////////////////////////////////////////////////////////////
	static NO crearNO(NExpresion e, NExpresion f)
	{
		return new NO(e, f);
	}
	//////////////////////////////////////////////////////////////////
	static NOArit crearNOArit(NExpresion e, NExpresion f)
	{
		return new NOArit(e, f);
	}
	//////////////////////////////////////////////////////////////////
	static NOExc crearNOExc(NExpresion e, NExpresion f)
	{
		return new NOExc(e, f);
	}


	//////////////////////////////////////////////////////////////////
	static NPlus crearNPlus(Token ti, NExpresion e)
	{
		Rango rango = obtRango(ti, e);
		return new NPlus(rango, e);
	}
	//////////////////////////////////////////////////////////////////
	static NPor crearNPor(NExpresion e, NExpresion f)
	{
		return new NPor(e, f);
	}

	//////////////////////////////////////////////////////////////////
	static NRetorne crearNRetorne(Token t, NExpresion[] es)
	{
		Rango rango;
		if ( es.length > 0 )
		{
			rango = obtRango(t, es[es.length -1]);
		}
		else
		{
			rango = obtRango(t, t);
		}
		
		return new NRetorne(rango, es);
	}

	//////////////////////////////////////////////////////////////////
	static NIntente crearNIntente(Token ti, Nodo[] a, NAtrape[] cc, NAtrape f, Token tf)
	{
		Rango rango = obtRango(ti, tf);
		return new NIntente(rango, a, cc, f);
	}

	//////////////////////////////////////////////////////////////////
	static NAtrape crearNAtrape(Token ti, NDeclaracion d, Nodo[] a, Token tf)
	{
		Rango rango;
		if ( tf != null )
			rango = obtRango(ti, tf);
		else if ( a.length > 0 )
			rango = obtRango(ti, a[a.length - 1]);
		else
			rango = obtRango(ti, d);
			
		return new NAtrape(rango, d, a);
	}

	//////////////////////////////////////////////////////////////////
	static NLance crearNLance(Token t, NExpresion e)
	{
		Rango rango = obtRango(t, e);
		return new NLance(rango, e);
	}

	//////////////////////////////////////////////////////////////////
	static NSubindexacion crearNSubindexacion(
		NExpresion e, NExpresion f, Token tf
	)
	{
		Rango rango = obtRango(e, tf);
		return new NSubindexacion(rango, e, f);
	}


	//////////////////////////////////////////////////////////////////
	static NY crearNY(NExpresion e, NExpresion f)
	{
		return new NY(e, f);
	}
	//////////////////////////////////////////////////////////////////
	static NYArit crearNYArit(NExpresion e, NExpresion f)
	{
		return new NYArit(e, f);
	}





	/** 
	 * Texto en compilacion.
	 */
	static String texto = null;

	//////////////////////////////////////////////////////////////////
	// No instanciable
	private ConstructorArbol() {}

	//////////////////////////////////////////////////////////////////
	static NAlgoritmo crearNAlgoritmo(
		Token ti,
		TId id,
		TNombre espec,
		TCadenaDoc estrategia,
		NDeclaracion[] ent,
		NDeclaracion[] sal,
		Nodo[] a,
		TCadena impLenguaje,
		TCadena impInfo,
		Token tf
	)
	{
		Rango rango = obtRango(ti, tf);
		return new NAlgoritmo(
			rango,
			id,
			espec,
			estrategia,
			ent,
			sal,
			a,
			impLenguaje,
			impInfo
		);
	}

	//////////////////////////////////////////////////////////////////
	static NCiclo crearNCiclo(Token ti, TId etq, Nodo[] a, Token tf)
	{
		Rango rango = obtRango(ti, tf);
		return new NCiclo(rango, etq, a);
	}

	//////////////////////////////////////////////////////////////////
	static NInterface crearNInterface(
		Token ti,
		TId id,	
		TNombre[] interfases,
		TCadenaDoc desc,
		List /*NEspecificacion*/ opers,
		Token tf
	)
	{
		Rango rango = obtRango(ti, tf);
		NEspecificacion[] a_opers = (NEspecificacion[]) opers.toArray(new NEspecificacion[0]);
		return new NInterface(
			rango,
			id,	
			interfases,
			desc,
			a_opers
		);
	}

	//////////////////////////////////////////////////////////////////
	static NClase crearNClase(
		Token ti,
		TId id,	
		TNombre extiende,      // clase extendida
		TNombre[] interfaces,  // interfaces implementadas
		TCadenaDoc desc,
		NDeclDesc[] atrs, 
		NConstructor[] acs,
		NAlgoritmo[] ams,
		Token tf
	)
	{
		boolean defineObjeto = ti.toString().equals("objeto");
		Rango rango = obtRango(ti, tf);
		return new NClase(
			rango,
			defineObjeto,
			id,	
			extiende, 
			interfaces,
			desc,
			atrs, 
			acs,
			ams
		);
	}

	//////////////////////////////////////////////////////////////////
	static NConstructor crearNConstructor(
		Token tini,
		TCadenaDoc desc,
		NDeclaracion[] ent,
		NDescripcion[] dent,
		NAfirmacion pre,
		NAfirmacion pos,
		TCadenaDoc e,
		Nodo[] a,
		Token tfin
	)
	{
		Rango rango = obtRango(tini, tfin);
		return new NConstructor(
			rango,
			desc,
			ent,
			dent,
			pre,
			pos,
			e,
			a
		);
	}

	//////////////////////////////////////////////////////////////////
	static NContinue crearNContinue(Token t, TId etq, NExpresion e)
	{
		Rango rango;
		if ( e != null )
		{
			rango = obtRango(t, e);
		}
		else if ( etq != null )
		{
			rango = obtRango(t, etq);
		}
		else
		{
			rango = obtRango(t);
		}
		return new NContinue(rango, etq, e);
	}

	//////////////////////////////////////////////////////////////////
	static NConvertirTipo crearNConvertirTipo(
		NExpresion e, NTipo t
	)
	{
		Rango rango = obtRango(e, t);
		return new NConvertirTipo(
			rango,
			e, t
		);
	}

	//////////////////////////////////////////////////////////////////
	static NCrearArregloTipoBase crearNCrearArregloTipoBase(NTipo t)
	{
		return new NCrearArregloTipoBase(t);
	}

	//////////////////////////////////////////////////////////////////
	static NCrearObjeto crearNCrearObjeto(
		Token ti, TNombre tnom, NExpresion[] args, Token tf
	)
	{
		Rango rango;
		if ( tf == null ) 
		{
			if ( args.length > 0 )
			{
				rango = obtRango(ti, args[args.length -1]);
			}
			else
			{
				rango = obtRango(ti, tnom);
			}
		} 
		else 
		{
			rango = obtRango(ti, tf);
		}
		
		return new NCrearObjeto(rango, tnom, args);
	}

	//////////////////////////////////////////////////////////////////
	static NCuantificado crearNCuantificado(
		Token ti,
		NDeclaracion[] d, NExpresion con, NExpresion e
	)
	{
		boolean para_todo = ti.image.equals("para_todo");
		Rango rango = obtRango(ti, e);
		
		return new NCuantificado(
			rango,
			para_todo, d, con, e
		);
	}

	//////////////////////////////////////////////////////////////////
	static NDeclaracion crearNDeclaracion(
		TId[] ids, 
		NTipo t, 
		boolean cte, 
		NExpresion e,
		Token c
	)
	{
		Rango rango;
		if ( e != null )
		{
			rango = obtRango(ids[0], e);
		}
		else
		{
			if ( c != null )
			{
				rango = obtRango(ids[0], c);
			}
			else
			{
				rango = obtRango(ids[0], t);
			}
		}
		
		return new NDeclaracion(rango, ids, t, cte, e);
	}

	//////////////////////////////////////////////////////////////////
	static NDeclaracion crearNDeclaracion(TId id, NTipo t)
	{
		Rango rango = obtRango(id, t);
		return new NDeclaracion(rango, id, t);
	}

	//////////////////////////////////////////////////////////////////
	static NDeclaracion crearNDeclaracion(
		TId id, 
		NTipo t, 
		boolean cte, 
		NExpresion e,
		Token c
	)
	{
		Rango rango;
		if ( e != null )
		{
			rango = obtRango(id, e);
		}
		else
		{
			if ( c != null )
			{
				rango = obtRango(id, c);
			}
			else
			{
				rango = obtRango(id, t);
			}
		}
		
		return new NDeclaracion(rango, id, t, cte, e);
	}

	//////////////////////////////////////////////////////////////////
	static NDeclDesc crearNDeclDesc(
		TId id, 
		NTipo tipo, 
		TCadenaDoc desc, 
		boolean cte, 
		NExpresion e,
		Token c
	)
	{
		Rango rango;
		if ( e != null )
		{
			rango = obtRango(id, e);
		}
		else
		{
			if ( c != null )
			{
				rango = obtRango(id, c);
			}
			else
			{
				rango = obtRango(id, tipo);
			}
		}
		return new NDeclDesc(rango, id, tipo, desc, cte, e);
	}

	//////////////////////////////////////////////////////////////////
	static NDescripcion crearNDescripcion(TId id, TCadenaDoc desc)
	{
		return new NDescripcion(
			new Rango(id.obtRango(), desc.obtRango()),
			id, desc
		);
	}

	//////////////////////////////////////////////////////////////////
	static NEsInstanciaDe crearNEsInstanciaDe(NExpresion e, NTipo ntipoRevisado)
	{
		return new NEsInstanciaDe(
			new Rango(e.obtRango(), ntipoRevisado.obtRango()),
			e, ntipoRevisado
		);
	}

	//////////////////////////////////////////////////////////////////
	static NEspecificacion crearNEspecificacion(
		Token tini,
		TId id, 
		TCadenaDoc desc,
		NDeclaracion[] ent, NDeclaracion[] sal,
		NDescripcion[] dent, NDescripcion[] dsal,
		NAfirmacion pre, NAfirmacion pos,
		Token tfin
	)
	{
		Rango rango = obtRango(tini, tfin);
		return new NEspecificacion(
			rango,
			id, desc,
			ent, sal,
			dent, dsal,
			pre, pos
		);
	}

	//////////////////////////////////////////////////////////////////
	static NId crearNId(TId t)
	{
		return new NId(t);
	}

	//////////////////////////////////////////////////////////////////
	static NMenor crearNMenor(NExpresion e, NExpresion f)
	{
		return new NMenor(e, f);
	}

	//////////////////////////////////////////////////////////////////
	static NMientras crearNMientras(
		Token ti, 
		TId etq, NExpresion e, Nodo[] a, 
		Token tf
	)
	{
		Rango rango = obtRango(ti, tf);
		return new NMientras(rango, etq, e, a);
	}

	//////////////////////////////////////////////////////////////////
	static NNombre crearNNombre(TId[] nom)
	{
		return new NNombre(new TNombre(nom));
	}

	//////////////////////////////////////////////////////////////////
	static NNombre crearNNombre(TNombre nom)
	{
		return new NNombre(nom);
	}

	//////////////////////////////////////////////////////////////////
	static NPaquete crearNPaquete(Token tini, TNombre nom)
	{
		Rango rango = obtRango(tini, nom);
		return new NPaquete(rango, nom);
	}

	//////////////////////////////////////////////////////////////////
	static NPara crearNPara(
		Token ti, 
		TId etq,
		TId i, NDeclaracion d, NExpresion ed, boolean b, 
		NExpresion ep, NExpresion eh, Nodo[] a,
		Token tf
	)
	{
		Rango rango = obtRango(ti, tf);
		return new NPara(
			rango, 
			etq,
			i, d, ed, b, ep, eh, a
		);
	}

	//////////////////////////////////////////////////////////////////
	static NRepita crearNRepita(Token ti, TId etq, Nodo[] a, NExpresion e)
	{
		Rango rango = obtRango(ti, e);
		return new NRepita(rango, etq, a, e);
	}

	//////////////////////////////////////////////////////////////////
	static NSubId crearNSubId(NExpresion exp, TId ti)
	{
		return new NSubId(exp, ti);
	}

	//////////////////////////////////////////////////////////////////
	static NTermine crearNTermine(Token t, TId etq, NExpresion e)
	{
		Rango rango;
		if ( e != null )
		{
			rango = obtRango(t, e);
		}
		else if ( etq != null )
		{
			rango = obtRango(t, etq);
		}
		else
		{
			rango = obtRango(t);
		}
		
		return new NTermine(rango, etq, e);
	}

	//////////////////////////////////////////////////////////////////
	static NTipo crearNTipoArreglo(Token ti, NTipo et)
	{
		Rango rango = obtRango(ti, et);
		return new NTipoArreglo(rango, et);
	}

	//////////////////////////////////////////////////////////////////
	static NTipo crearNTipoBasico(Token t)
	{
		Rango rango = obtRango(t, t);
		String nombre = t.image;
		
		if ( nombre.equals("entero") )
		{
			return new NTipoEntero(rango);
		}
		else if ( nombre.equals("booleano") )
		{
			return new NTipoBooleano(rango);
		}
		else if ( nombre.equals("caracter") )
		{
			return new NTipoCaracter(rango);
		}
		else if ( nombre.equals("real") )
		{
			return new NTipoReal(rango);
		}
		else if ( nombre.equals("cadena") )
		{
			return new NTipoCadena(rango);
		}
		throw new Error("Tipo " +nombre+ " no esperado!");
	}

	//////////////////////////////////////////////////////////////////
	static NTipo crearNTipoClase(Token ti, TNombre nom)
	{
		Rango rango;
		if ( ti == null )
		{
			rango = nom.obtRango();
		}
		else
		{
			rango = new Rango(obtRango(ti), nom.obtRango());
		}
		return new NTipoClase(rango, nom);
	}

	//////////////////////////////////////////////////////////////////
	static NTipo crearNTipoEspecificacion(Token ti, TNombre nom)
	{
		Rango rango = obtRango(ti);
		if ( nom != null )
		{
			rango = new Rango(rango, nom.obtRango());
		}
		return new NTipoEspecificacion(rango, nom);
	}

	//////////////////////////////////////////////////////////////////
	static NUtiliza crearNUtiliza(Token tini, Token que, TNombre nom)
	{
		Rango rango = obtRango(tini, nom);
		return new NUtiliza(rango, que.image, nom);
	}

	//////////////////////////////////////////////////////////////////
	static TCadena crearTCadena(Token t, String str)
	{
		Rango rango = obtRango(t, t);
		
		return new TCadena(rango, str);
	}

	//////////////////////////////////////////////////////////////////
	static TCadenaDoc crearTCadenaDoc(Token t)
	{
		Rango rango = obtRango(t, t);
		
		return new TCadenaDoc(rango, t.image);
	}

	//////////////////////////////////////////////////////////////////
	static TId crearTId(Token t)
	{
		Rango rango = obtRango(t, t);
		
		return new TId(rango, t.image);
	}

	//////////////////////////////////////////////////////////////////
	static TNombre crearTNombre(Vector v)
	{
		TId[] a = new TId[v.size()];
		v.copyInto(a);

		return new TNombre(a);
	}















	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene el rango entre un token inicial y un token final.
	 */
	private static Rango obtRango(Token tini, Token tfin)
	{
		return new Rango(obtRango(tini), obtRango(tfin));
	}

	///////////////////////////////////////////////////////////
	/**
	 * Pone el texto.
	 */
	static void ponTexto(String texto_)
	{
		texto = texto_;
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene el rango entre dos elementos ubicables.
	 */
	private static Rango obtRango(IUbicable u, IUbicable w)
	{
		return new Rango(u.obtRango(), w.obtRango());
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene el rango entre un elemento ubicable inicial y un token
	 * final.
	 */
	private static Rango obtRango(IUbicable u, Token token)
	{
		return new Rango(u.obtRango(), obtRango(token));
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene el rango entre un token inicial y un elemento
	 * ubicable final.
	 */
	private static Rango obtRango(Token token, IUbicable u)
	{
		return new Rango(obtRango(token), u.obtRango());
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene el rango de un token.
	 */
	private static Rango obtRango(Token tok)
	{
		return obtRango(texto, tok);
	}

	//////////////////////////////////////////////////////////////////
	static NImplementa crearNImplementa(NExpresion e, NTipo ntipoRevisado)
	{
		return new NImplementa(
			new Rango(e.obtRango(), ntipoRevisado.obtRango()),
			e, ntipoRevisado
		);
	}

	//////////////////////////////////////////////////////////////////
	static NTipo crearNTipoInterface(Token ti, TNombre nom)
	{
		Rango rango;
		if ( ti == null )
		{
			rango = nom.obtRango();
		}
		else
		{
			rango = new Rango(obtRango(ti), nom.obtRango());
		}
		return new NTipoInterface(rango, nom);
	}

	/////////////////////////////////////////////////////////////////
	/**
	 *
	 */
	private static int obtPos(
		String texto,
		int deltaLinea, int deltaColumna, int pos
	)
	{
//		try
//		{
			// Ir hasta la línea:
			for ( int lin = 1; lin < deltaLinea; pos++)
			{	
				if ( texto.charAt(pos) == '\n' )
				{
					lin++;
				}
			}
			// Ir hasta la columna:
			for ( int i = 1; i < deltaColumna; i++, pos++ )
			{
/* *****************************************************************
2002-06-05
Ya no se considera el caso '\t'.
Ver SimpleCharStream.UpdateLineColumn()

El siguiente codigo funcionaba bastante bien pero NO en algunos casos:

				// Explicación del siguiente ajuste: JavaCC no toma
				// el \t como una sola posición. Entonces, el avance 
				// de pos está correcto en +1 pero reducimos el 
				// faltante en columnas.
				if ( texto.charAt(pos) == '\t' )
				{
					deltaColumna -= 7;
				}
***************************************************************** */
			}
/*		}
		catch(StringIndexOutOfBoundsException ex)
		{
			// pos quedará mal pero no importa.
			// En realidad se solucionó un problema en el Editor
			// al cargar un archivo: al parecer los ^M que pudiera
			// tener el archivo contaban por líneas para JavaCC 
			// así que se exageraba. abr/2/1999
		}
*/		
		return pos;
	}

	//////////////////////////////////////////////////////////////
	static Rango obtRango(
		int iniLin, int iniCol,
		int finLin, int finCol,
		String texto
	)
	{
/***
		int posIni = obtPos(texto, iniLin, iniCol, 0);
		int col = finLin == iniLin ? finCol - iniCol +1: finCol;
		int posFin = 1 + obtPos(texto, finLin - iniLin +1, col, posIni);
***/
		int posIni = obtPos(texto, iniLin, iniCol, 0);
		int posFin = obtPos(texto, finLin, finCol, 0) + 1;
		
		return new Rango(posIni, posFin, iniLin, iniCol, finLin, finCol);

	}

	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene el rango de un token con respecto a un texto.
	 * Note que la visibilidad de este servicio es "package"
	 * ya que es utilizado por el DerivadorJavaCC.
	 */
	static Rango obtRango(String texto, Token t)
	{
		return obtRango(
			t.beginLine,
			t.beginColumn,
			t.endLine,
			t.endColumn,
			texto
		);
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene el rango entre un token inicial y un token final
	 * con respecto a un texto dado.
	 * Note que la visibilidad de este servicio es "package"
	 * ya que es utilizado por el DerivadorJavaCC.
	 */
	static Rango obtRango(Token tini, Token tfin, String texto)
	{
		return new Rango(obtRango(tini), obtRango(tfin));
	}
}