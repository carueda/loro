package loro.arbol;

import loro.tipo.Tipo;
import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.Rango;

///////////////////////////////////////////////////////////////////
/**
 * Nodo para tipo cadena.
 */
public class NTipoCadena extends NTipo
{
	///////////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo para el tipo.
	 */
	public NTipoCadena(Rango rango)
	{
		super(rango);
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
}