package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

/**
 */
public class NCorrIzq extends NExprBin
{
	/**
	 * NCorrIzq constructor comment.
	 * @param e Nodo
	 * @param f Nodo
	 */
	public NCorrIzq(NExpresion e, NExpresion f)
	{
		super(e, f, "<<");
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