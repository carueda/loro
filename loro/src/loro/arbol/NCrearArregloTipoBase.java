package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

/**
 * NCrearArregloTipoBase.
 */
public class NCrearArregloTipoBase extends NExpresion
{



	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}

	NTipo ntipo;

	public NCrearArregloTipoBase(NTipo t)
	{
		super(t.obtRango());
		this.ntipo = t;
	}

	////////////////////////////////////////////////////
	/**
	 */
	public NTipo obtNTipo()
	{
		return ntipo;
	}
}