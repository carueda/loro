package loro.arbol;

import loro.tipo.Tipo;
import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.Rango;

///////////////////////////////////////////////////////////////////
/**
 *
 */
public class NTipoEspecificacion extends NTipo
{
	TNombre nom;

	///////////////////////////////////////////////////////////////////
	/**
	 */
	public NTipoEspecificacion(Rango rango, TNombre nom)
	{
		super(rango);
		this.nom = nom;
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

	///////////////////////////////////////////////////////////////////
	public TNombre obtTNombre()
	{
		return nom;
	}
}