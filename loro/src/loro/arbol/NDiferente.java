package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

/**
 * This type was created in VisualAge.
 */
public class NDiferente extends NExprBin
{
	/**
	 * NDiferente constructor comment.
	 * @param e Nodo
	 * @param f Nodo
	 */
	public NDiferente(NExpresion e, NExpresion f)
	{
		super(e, f, "!=");
	}
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
}