package loro.arbol;

import loro.tipo.Tipo;
import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.Rango;

///////////////////////////////////////////////////////////////////
/**
 *
 */
public class NTipoEntero extends NTipo
{
	///////////////////////////////////////////////////////////////////
	/**
	 */
	public NTipoEntero(Rango rango)
	{
		super(rango);
	}

	//////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
}