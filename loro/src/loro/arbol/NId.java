package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

//////////////////////////////////////////////////////
/**
 *
 */
public class NId extends NExpresion
{


	boolean esConstante;


	//////////////////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
	/**
	 * Dice si esta expresión es constante.
	 */
	public boolean esConstante()
	{
		return esConstante;
	}


	//////////////////////////////////////////////
	/**
	 * Establece si esta expresión es constante.
	 */
	public void ponEsConstante(boolean es)
	{
		esConstante = es;
	}

	TId tid;

	/**
	 */
	public NId(TId tid)
	{
		super(tid.obtRango());
		this.tid = tid;
	}

	//////////////////////////////////////////////
	/**
	 */
	public TId obtId()
	{
		return tid;
	}

	//////////////////////////////////////////////
	/**
	 */
	public String toString()
	{
		return tid.obtId();
	}
}