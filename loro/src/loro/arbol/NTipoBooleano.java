package loro.arbol;

import loro.tipo.Tipo;
import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.Rango;

///////////////////////////////////////////////////////////////////
/**
 * Nodo para el tipo booleano.
 */
public class NTipoBooleano extends NTipo
{
	///////////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo para el tipo.
	 */
	public NTipoBooleano(Rango rango)
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