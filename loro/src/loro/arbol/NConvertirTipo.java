package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/**
 *
 */
public class NConvertirTipo extends NExprUn
{

	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}

	/** El nodo-tipo al que se convierte la expresion. */
	NTipo ntipo;

	/**
	 */
	public NConvertirTipo(
		Rango rango,
		NExpresion e, NTipo t
	)
	{
		super(rango, e, "tipo(.)");
		this.ntipo = t;
	}

	/////////////////////////////////////////////////////
	/**
	 * Obtiene el tipo al que se convierte la expresion.
	 */
	public NTipo obtNTipo()
	{
		return ntipo;
	}
}