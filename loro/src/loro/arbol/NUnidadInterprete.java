package loro.arbol;

import loro.util.Util;

import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import java.util.Hashtable;

////////////////////////////////////////////////////////////////////
/**
 * La "unidad para el interprete."
 * Esta es la unidad manipulada para efectos de mantener la informacion
 * asociada a una sesion de interpretacion. Basicamente se necesita
 * su funcionalidad para asociar nombres simples a compuestos.
 */
public class NUnidadInterprete extends NUnidad
{
	////////////////////////////////////////////////////////////////////
	/**
	 * Crea una unidad para interprete.
	 */
	public NUnidadInterprete()
	{
		super(null);
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		throw new IllegalStateException("NUnidadInterprete.aceptar(IVisitante) called!");
	}

	////////////////////////////////////////////////////////////////////
	/**
	 * No debe ser llamado.
	 * @throws IllegalStateException
	 */
	public String getTypeString()
	{
		throw new IllegalStateException("NUnidadInterprete.getTypeString() called!");
	}


	///////////////////////////////////////////////////////////
	public String toString()
	{
		return "Unidad de interpretación";
	}
}