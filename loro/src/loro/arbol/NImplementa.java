package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

/////////////////////////////////////////////////////////
/**
 * Nodo para representar el cheque de si un objeto (expresion)
 * implementa una cierta interface.
 */
public class NImplementa extends NExpresion
{
	/** La expresión revisada. */
	NExpresion e;




	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
	////////////////////////////////////////////
	/**
	 * Obtiene la expresion revisada.
	 */
	public NExpresion obtExpresion()
	{
		return e;
	}



	/** El nodo tipo revisado. */
	NTipo ntipoRevisado;



	/////////////////////////////////////////////
	/**
	 * Obtiene el nodo-tipo revisado.
	 */
	public NTipo obtNTipoRevisado()
	{
		return ntipoRevisado;
	}

	/**
	 */
	public NImplementa(Rango rango, NExpresion e, NTipo ntipoRevisado)
	{
		super(rango);
		this.e = e;
		this.ntipoRevisado = ntipoRevisado;
	}
}