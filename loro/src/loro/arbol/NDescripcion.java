package loro.arbol;

import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.Rango;

////////////////////////////////////////////////////////////////
/**
 * Una descripcion para una identificador.
 */
public class NDescripcion extends Nodo
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

	/////////////////////////////////////////////////////////////////////////////
	public String obtDescripcion()
	{
		return descripcion.obtCadena();
	}

	/////////////////////////////////////////////////////////////////////////////
	public TId obtId()
	{
		return id;
	}

	/////////////////////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo NDescripcion.
	 */
	public NDescripcion(Rango rango, TId id, TCadenaDoc desc)
	{
		super(rango);
		this.id = id;
		this.descripcion = desc;
	}
}