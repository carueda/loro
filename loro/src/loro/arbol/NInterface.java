package loro.arbol;

import loro.Loro.Str;
import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.Rango;

////////////////////////////////////////////////////////////////
/**
 * Una interface Loro.
 */
public class NInterface extends NUnidad
{
	/** Descripción. */
	TCadenaDoc descripcion;

	/** Interfaces extendidas. */
	TNombre[] interfaces;

	/** Operaciones . */
	NEspecificacion[] opers;


	///////////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo interface.
	 */
	public NInterface(
		Rango rango,
		TId id,
		TNombre[] interfaces,
		TCadenaDoc desc,
		NEspecificacion[] opers
	)
	{
		super(rango);
		ponId(id);
		this.interfaces = interfaces;
		this.descripcion = desc;
		this.opers = opers;
		notificar();
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene la lista de las operaciones declaradas para esta interface.
	 */
	public NEspecificacion[] obtOperacionesDeclaradas()
	{
		return opers;
	}

	////////////////////////////////////////////////////////////////
	public String toString()
	{
		return Str.get("interface")+ " " +obtNombreCompletoCadena();
	}

	////////////////////////////////////////////////////////////////////
	/**
	 * Retorna "interface " +obtNombreCompletoCadena();
	 */
	public String getTypeString()
	{
		return Str.get("interface")+ " " +obtNombreCompletoCadena();
	}

	////////////////////////////////////////////////////////////////
	public String obtDescripcion()
	{
		return descripcion == null ? null : descripcion.obtCadena();
	}

	////////////////////////////////////////////////////////////////
	public TNombre[] obtInterfaces()
	{
		return interfaces;
	}
	
	///////////////////////////////////////////////////////////////////
	/**
	 * Notifica a mis operaciones a qué interface pertenecen.
	 */
	protected void notificar()
	{
		for (int i = 0; i < opers.length; i++)
		{
			opers[i].ponInterface(this);
		}
	}

	///////////////////////////////////////////////////////////////////
	/**
	 * Definido para actualizar referencias "transient" asociadas.
	 * Actualmente notificar().
	 */
	private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException
	{
		s.defaultReadObject();
		notificar();
	}
}