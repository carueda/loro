package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;


import loro.Rango;

//////////////////////////////////////////////////////////////////////
/**
 * Clase de base para literales.
 */
public abstract class NLiteral extends NExpresion
{

	Object valor;



	//////////////////////////////////////////////////////////////////////
	/**
	 * @return boolean
	 * @param l NLiteral
	 */
	public boolean igual(NLiteral l)
	{
		return imagen.equals(l.imagen);
		//return t.image.equals(l.t.image);
	}

	//////////////////////////////////////////////////////////////////////
	public Object obtValor()
	{
		return valor;
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * @return java.lang.String
	 */
	public String toString()
	{
		return valor.toString();
	}

	String imagen;

	//////////////////////////////////////////////////////////////////////
	/**
	 */
	public NLiteral(Rango rango, String imagen)
	{
		super(rango);
		this.imagen = imagen;
		valor = null;
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene tal cual la apariencia de este literal.
	 */
	public String obtImagen()
	{
		return imagen;
	}
}