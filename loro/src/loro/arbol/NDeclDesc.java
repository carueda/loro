package loro.arbol;

import loro.Loro.Str;
import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.Rango;

////////////////////////////////////////////////////////////////
/**
 * NDeclDesc.
 */
public class NDeclDesc extends NDeclaracionBase
{
	TId id;
	TCadenaDoc descripcion;

	////////////////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}

	////////////////////////////////////////////////////////////////
	public String toString() {
		String constant = " " +Str.get("constant");
		return id+ ":" +obtTipo() + (esConstante() ? constant : "")+ " " +descripcion;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo NDeclDesc.
	 */
	public NDeclDesc(
		Rango rango, TId tid, NTipo ntipo, 
		TCadenaDoc desc, boolean cte, NExpresion e
	)
	{
		super(rango, ntipo, cte, e);
		this.id = tid;
		this.descripcion = desc;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la descripcion.
	 */
	public String obtDescripcion()
	{
		return descripcion.obtCadena();
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el id.
	 */
	public TId obtId()
	{
		return id;
	}
}