package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

/**
 * Una expresi�n binaria de asignaci�n.
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