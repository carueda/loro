package loroedi.jedit;

import javax.swing.text.Segment;

///////////////////////////////////////////////////////////////////
/**
 * Loro token marker.
 *
 * @author Carlos Rueda
 * @version 0.1 2001-01-29 (based on JavaTokenMarker by Slava Pestov)
 */
public class LoroTokenMarker extends CTokenMarker
{
	// private members
	private static KeywordMap keywords;

	///////////////////////////////////////////////////////////////////
	public LoroTokenMarker()
	{
		super(false,getKeywords());
	}

	///////////////////////////////////////////////////////////////////
	public LoroTokenMarker(
		String prompt,
		String prefix_special,
		String prefix_invalid
	)
	{
		super(false,getKeywords());
		this.prompt = prompt;
		if ( prompt != null )
		{
			prompt_len = prompt.length();
		}
		this.prefix_special = prefix_special;
		if ( prefix_special != null )
		{
			prefix_special_len = prefix_special.length();
		}
		this.prefix_invalid = prefix_invalid;
		if ( prefix_invalid != null )
		{
			prefix_invalid_len = prefix_invalid.length();
		}
	}

	///////////////////////////////////////////////////////////////////
	public static KeywordMap getKeywords()
	{
		if(keywords == null)
		{
			keywords = new KeywordMap(false);
			keywords.add("clase",Token.KEYWORD1);
			keywords.add("interfaz",Token.KEYWORD1);
			keywords.add("interface",Token.KEYWORD1);
			keywords.add("objeto",Token.KEYWORD1);
			keywords.add("especificacion",Token.KEYWORD1);
			keywords.add("especificación",Token.KEYWORD1);
			keywords.add("implementacion",Token.KEYWORD1);
			keywords.add("implementación",Token.KEYWORD1);
			keywords.add("algoritmo",Token.KEYWORD1);
			keywords.add("termine",Token.KEYWORD1);
			keywords.add("crear",Token.KEYWORD1);
			keywords.add("caso",Token.KEYWORD1);
			keywords.add("continue",Token.KEYWORD1);
			keywords.add("constructor",Token.KEYWORD1);
			keywords.add("si_no",Token.KEYWORD1);
			keywords.add("si_no_si",Token.KEYWORD1);
			keywords.add("haga",Token.KEYWORD1);
			keywords.add("para",Token.KEYWORD1);
			keywords.add("desde",Token.KEYWORD1);
			keywords.add("hasta",Token.KEYWORD1);
			keywords.add("bajando",Token.KEYWORD1);
			keywords.add("paso",Token.KEYWORD1);
			keywords.add("inicio",Token.KEYWORD1);
			keywords.add("fin",Token.KEYWORD1);
			keywords.add("ciclo",Token.KEYWORD1);
			keywords.add("repita",Token.KEYWORD1);
			keywords.add("si",Token.KEYWORD1);
			keywords.add("entonces",Token.KEYWORD1);
			keywords.add("es_instancia_de",Token.KEYWORD1);
			keywords.add("como",Token.KEYWORD1);
			keywords.add("en",Token.KEYWORD1);
			keywords.add("existe",Token.KEYWORD1);
			keywords.add("para_todo",Token.KEYWORD1);
			keywords.add("global",Token.KEYWORD1);
			keywords.add("retorne",Token.KEYWORD1);
			keywords.add("siempre",Token.KEYWORD1);
			keywords.add("segun",Token.KEYWORD1);
			keywords.add("según",Token.KEYWORD1);
			keywords.add("mientras",Token.KEYWORD1);
			keywords.add("lance",Token.KEYWORD1);
			keywords.add("intente",Token.KEYWORD1);
			keywords.add("atrape",Token.KEYWORD1);
			keywords.add("extiende",Token.KEYWORD1);
			keywords.add("implementa",Token.KEYWORD1);
			keywords.add("operacion",Token.KEYWORD1);
			keywords.add("operación",Token.KEYWORD1);
			keywords.add("metodo",Token.KEYWORD1);
			keywords.add("método",Token.KEYWORD1);
//			keywords.add("lanza",Token.KEYWORD1);
			keywords.add("caracter",Token.KEYWORD1);
			keywords.add("cadena",Token.KEYWORD1);
			keywords.add("entero",Token.KEYWORD1);
			keywords.add("real",Token.KEYWORD1);
			keywords.add("booleano",Token.KEYWORD1);

			keywords.add("nada",Token.KEYWORD3);
			keywords.add("descripcion",Token.KEYWORD3);
			keywords.add("descripción",Token.KEYWORD3);
			keywords.add("estrategia",Token.KEYWORD3);
			keywords.add("pre",Token.KEYWORD3);
			keywords.add("pos",Token.KEYWORD3);
			keywords.add("entrada",Token.KEYWORD3);
			keywords.add("salida",Token.KEYWORD3);

			keywords.add("paquete",Token.KEYWORD2);
			keywords.add("utiliza",Token.KEYWORD2);

			keywords.add("constante",Token.KEYWORD1);
			keywords.add("este",Token.KEYWORD1);
			keywords.add("éste",Token.KEYWORD1);
			keywords.add("super",Token.KEYWORD1);
			keywords.add("súper",Token.KEYWORD1);

			keywords.add("nulo",Token.LITERAL2);
			keywords.add("cierto",Token.LITERAL2);
			keywords.add("falso",Token.LITERAL2);
		}
		return keywords;
	}
}