package loro.arbol;

import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.Rango;

////////////////////////////////////////////////////////////
/**
 * Nodo para una afirmación.
 *
 * @version 2002-10-06
 */
public class NAfirmacion extends NAccion
{
	/**
	 * La expresión para la afirmación. 
	 * Puede ser null si sólo se indicó una cadena de descripción.
	 */
	NExpresion expresion;

	/**
	 * Cadena de descripción cuando no hay expresión 
	 */
	String docText;

	/** Segmento de texto abarcado por toda la afirmación. */
	String cadena;

	////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo afirmacion.
	 */
	public NAfirmacion(
		Rango rango, String cadena,
		NExpresion e
	)
	{
		super(rango);
		expresion = e;
		this.cadena = cadena;
	}

	////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo afirmacion.
	 */
	public NAfirmacion(
		Rango rango, String cadena,
		String docText
	)
	{
		super(rango);
		this.docText = docText;
		this.cadena = cadena;
	}

	////////////////////////////////////////////////////////////
	/**
	 * Retorna la descripcion literal dada por el programador.
	 * Casos:
	 *   Ha sido dada una expresión y ésta es de tipo NLiteralCadena. 
	 *   ha sido dado una cadena de documentación.
	 *   En otros casos retorna null.
	 * La idea es mantener las comillas de esta descripcion, que no se
	 * tienen a disposicion con obtCadena().
	 */
	public String getLiteralDescription()
	{
		if ( docText != null )
		{
			return docText;
		}
		else if (expresion instanceof NLiteralCadena)
		{
			return ((NLiteralCadena) expresion).obtImagen();
		}
		else
		{
			return null;
		}
	}

	////////////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}

	////////////////////////////////////////////////////////////
	/**
	 * Obtiene el segmento de texto abarcado por toda la afirmación.
	 */
	public String obtCadena()
	{
		return cadena;
	}

	////////////////////////////////////////////////////////////
	/**
	 * Obtiene la expresion de esta afirmacion.
	 * Puede ser null cuando lo que se suministró es una cadena
	 * de documentación.
	 */
	public NExpresion obtExpresion()
	{
		return expresion;
	}

}