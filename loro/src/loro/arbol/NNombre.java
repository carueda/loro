package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

//////////////////////////////////////////////////
/**
 *
 */
public class NNombre extends NExpresion
{



	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}


	TNombre nombre;

	/**
	 */
	public NNombre(TNombre tnom)
	{
		super(tnom.obtRango());
		this.nombre = tnom;
	}

	public TNombre obtNombre()
	{
		return nombre;
	}
}