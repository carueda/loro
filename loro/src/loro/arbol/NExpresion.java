package loro.arbol;

import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.tipo.Tipo;
import loro.Rango;

////////////////////////////////////////////////////////////////
/**
 * La raiz para todas las expresiones en Loro
 */
public abstract class NExpresion extends Nodo
{
	/** El tipo de la expresión. */
	Tipo tipo;

	/** Se escribió parentizada esta expresión? */
	boolean parentizada;


	/**
	 * Dice si esta expresión es constante.
	 * NOTA: Llámese después de chequeo.
	 */
	public boolean esConstante()
	{
		return false;
	}
	/**
	 * Dice si esta expresión se escribio parentizada.
	 */
	public boolean esParentizada()
	{
		return parentizada;
	}
	/**
	 * Obtiene el tipo de esta expresion.
	 */
	public Tipo obtTipo()
	{
		return tipo;
	}
	/**
	 * Pone el tipo para esta expresion.
	 */
	public void ponTipo(Tipo tipo)
	{
		this.tipo = tipo;
	}

	////////////////////////////////////////////////////////////
	/**
	 */
	public NExpresion(Rango rango)
	{
		super(rango);
	}
}