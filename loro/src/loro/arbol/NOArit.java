package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

/**
 *
 */
public class NOArit extends NExprBin
{
	/**
	 */
	public NOArit(NExpresion e, NExpresion f)
	{
		super(e, f, "|");
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