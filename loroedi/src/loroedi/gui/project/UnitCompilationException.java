package loroedi.gui.project;

import loroedi.gui.project.unit.IProjectUnit;

import loro.CompilacionException;

//////////////////////////////////////////////////////////////
/**
 * Recoge informaci�n sobre un error de compilaci�n asociado
 * a una unidad.
 *
 * @author Carlos Rueda
 */
public class UnitCompilationException extends Exception
{
	/** El propio error de compilaci�n. */
	CompilacionException ce;
	
	/** La unidad correspondiente. */
	IProjectUnit unit;
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Crea una excepci�n de compilaci�n asociada a una unidad.
	 *
	 * @param rango Rango del texto involucrado con la excepci�n.
	 * @param m Descripi�n de la excepci�n.
	 */
	public UnitCompilationException(CompilacionException ce, IProjectUnit unit)
	{
		super(ce.getMessage());
		this.ce = ce;
		this.unit = unit;
	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la propia excepci�n de compilaci�n.
	 */
	public CompilacionException getCompilacionException()
	{
		return ce;
	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la unidad asociada al error de compilaci�n.
	 */
	public IProjectUnit getUnit()
	{
		return unit;
	}
}
