package loro.arbol;

import loro.tipo.Tipo;
import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.Rango;

///////////////////////////////////////////////////////////////////
/**
 *
 */
public class NTipoReal extends NTipo
{
	///////////////////////////////////////////////////////////////////
	/**
	 */
	public NTipoReal(Rango rango)
	{
		super(rango);
	}

	///////////////////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
}