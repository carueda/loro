package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

/**
 * Una expresión binaria de asignación.
 */
public class NAsignacion extends NExprBin
{
	/**
	 * NAsignacion constructor comment.
	 */
	public NAsignacion(NExpresion v, NExpresion e)
	{
		super(v, e, ":=");
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