package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

/**
 */
public class NEquivalencia extends NExprBin
{
	/**
	 * NEquivalencia constructor comment.
	 * @param e Nodo
	 * @param f Nodo
	 */
	public NEquivalencia(NExpresion e, NExpresion f)
	{
		super(e, f, "<=>");
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