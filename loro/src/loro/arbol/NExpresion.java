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
	/** El tipo de la expresi�n. */
	Tipo tipo;

	/** Se escribi� parentizada esta expresi�n? */
	boolean parentizada;


	/**
	 * Dice si esta expresi�n es constante.
	 * NOTA: Ll�mese despu�s de chequeo.
	 */
	public boolean esConstante()
	{
		return false;
	}
	/**
	 * Dice si esta expresi�n se escribio parentizada.
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