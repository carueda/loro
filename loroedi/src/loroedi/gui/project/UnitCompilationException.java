package loroedi.gui.project;

import loroedi.gui.project.unit.IProjectUnit;

import loro.CompilacionException;

//////////////////////////////////////////////////////////////
/**
 * Recoge información sobre un error de compilación asociado
 * a una unidad.
 *
 * @author Carlos Rueda
 */
public class UnitCompilationException extends Exception
{
	/** El propio error de compilación. */
	CompilacionException ce;
	
	/** La unidad correspondiente. */
	IProjectUnit unit;
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Crea una excepción de compilación asociada a una unidad.
	 *
	 * @param rango Rango del texto involucrado con la excepción.
	 * @param m Descripión de la excepción.
	 */
	public UnitCompilationException(CompilacionException ce, IProjectUnit unit)
	{
		super(ce.getMessage());
		this.ce = ce;
		this.unit = unit;
	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la propia excepción de compilación.
	 */
	public CompilacionException getCompilacionException()
	{
		return ce;
	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la unidad asociada al error de compilación.
	 */
	public IProjectUnit getUnit()
	{
		return unit;
	}
}
