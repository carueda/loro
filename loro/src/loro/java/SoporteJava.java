package loro.java;

import loro.arbol.*;

import loro.tipo.*;

///////////////////////////////////////////////////////////
/**
 * Algunas facilidades para conversion e interface Loro-Java.
 */
public class SoporteJava 
{
	////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el codigo Java del esquema para implementar
	 * el algoritmo dado. 
	 * Si el algoritmo no tiene la indicacion de ser implementado
	 + en Java, se retorna null.
	 */
	public static String obtEsquemaJava(NAlgoritmo alg)
	{
		if ( !alg.implementadoEnLenguaje("java") )
		{
			return null;
		}
			
		StringBuffer sb = new StringBuffer(
"/////////////////////////////////////////////////////////////////////////\n"
		);
		String tipoJava;

		NDeclaracion[] psal = alg.obtParametrosSalida();
		if ( psal.length == 1 )
		{
			NDeclaracion d = psal[0];
			tipoJava = obtTipoInterfaceJava(d.obtTipo());
		}
		else
		{
			tipoJava = "void";
		}

		sb.append("public static " +
			tipoJava+ " " +alg.obtId()+ "(loro.ijava.LAmbiente $amb"
		);

		NDeclaracion[] pent = alg.obtParametrosEntrada();
		if ( pent.length > 0 )
		{
			for (int i = 0; i < pent.length; i++)
			{
				NDeclaracion d = pent[i];
				tipoJava = obtTipoInterfaceJava(d.obtTipo());

				sb.append(",\n\t" +tipoJava+ " " +d.obtId());
			}
		}
		
		sb.append(")\n{\n");
		sb.append("}\n");

		return sb.toString();
	}
	///////////////////////////////////////////////////////////////
	/**
	 * INCOMPLETO
	 * De acuerdo al tipo Loro, retorna la version correspondiente
	 * para interface con Java. 
	 */
	public static String obtTipoInterfaceJava(Tipo t)
	{
		if ( t.esCadena() )
			return "java.lang.String";
		if ( t.esEntero() )
			return "int";
		if ( t.esReal() )
			return "double";
		if ( t.esCaracter() )
			return "char";
		if ( t.esBooleano() )
			return "boolean";
		if ( t.esClase() )
			return "LObjeto";
		if ( t.esArreglo() )
			return "LArreglo";

		throw new RuntimeException("Uy! llamado obtTipoInterfaceJava con " +t);
	}
	///////////////////////////////////////////////////////////////
	/**
	 * INCOMPLETO
	 * De acuerdo al tipo Loro, retorna la versión Java
	 * correspondiente. 
	 */
	public static String obtTipoJava(Tipo t)
	{
		if ( t.esCadena() )
			return "java.lang.String";
		if ( t.esEntero() )
			return "int";
		if ( t.esReal() )
			return "double";
		if ( t.esCaracter() )
			return "char";
		if ( t.esBooleano() )
			return "boolean";
		if ( t.esClase() )
			return ((TipoClase)t).obtNombreSimple();
		if ( t.esArreglo() )
			return obtTipoJava(t.obtTipoElemento()) + "[]";

		return "??";
	}
}