package loro.tabsimb;

import loro.Loro.Str;
import loro.tipo.Tipo;

/////////////////////////////////////////////////////////////////////
/**
 * Esta clase define una entrada para la tabla de símbolos.
 */
public class EntradaTabla
{
	/** El identificador. */
	private String id;

	/** El tipo. */
	private Tipo tipo;

	/** El valor. */
	private Object valor;

	/** Es constante el valor asociado a esta entrada? */
	private boolean esConstante;

	/** Tiene asignacion esta entrada? */
	private boolean asignado;


	//////////////////////////////////////////////////////
	/**
	 * Dice si esta entrada tiene valor constante.
	 */
	public boolean esConstante()
	{
		return esConstante;
	}

	//////////////////////////////////////////////////////
	/**
	 * Obtiene el tipo de esta entrada.
	 */
	public Tipo obtTipo()
	{
		return tipo;
	}
	//////////////////////////////////////////////////////
	/**
	 * Retorna el valor actual de esta entrada.
	 */
	public Object obtValor()
	{
		return valor;
	}
	//////////////////////////////////////////////////////
	/**
	 * Establece si esta entrada tiene valor constante.
	 */
	public void ponConstante(boolean b)
	{
		esConstante = b;
	}
	//////////////////////////////////////////////////////
	/**
	 * Pone valor a esta entrada. Esto implica que esta
	 * entrada tiene asignacion.
	 */
	public void ponValor(Object val)
	{
		valor = val;
		asignado = true;
	}
	//////////////////////////////////////////////////////
	/**
	 * Pone el estado de asignacion.
	 */
	public void ponAsignado(boolean asignado)
	{
		this.asignado = asignado;
	}
	//////////////////////////////////////////////////////
	/**
	 *
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("id=" +id+ "; ");
		sb.append("typw=" +tipo+ "; ");
		sb.append("assigned=" +asignado+ "; ");
		sb.append("value=" +(valor==null? "" : valor.toString())+ "; ");
		sb.append(esConstante ? " Constant" : "" );
		sb.append("\n" );
		return sb.toString();
	}
	//////////////////////////////////////////////////////////////
	/**
	 * Dice si el identificador tiene asignacion.
	 */
	public boolean obtAsignado()
	{
		return asignado;
	}

	//////////////////////////////////////////////////////
	/**
	 * Crea una entrada para un símbolo.
	 */
	public EntradaTabla(String id, Tipo t)
	{
		this.id = id;
		tipo = t;
		valor = null;
		asignado = false;
	}

	//////////////////////////////////////////////////////
	/**
	 * Obtiene el id de esta entrada.
	 */
	public String obtId()
	{
		return id;
	}
}