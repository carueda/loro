package loroedi.gui.project.unit;

import loroedi.gui.project.model.IPackageModel;

//////////////////////////////////////////////////
/**
 *
 * @author Carlos Rueda
 * @version 2002-08-03
 */
public class AlgorithmUnit extends AbstractProjectUnit
{
	String specName;
	
	/////////////////////////////////////////////////////////////////
	public AlgorithmUnit(IPackageModel pkg, String name, String specName)
	{
		super(pkg, name);
		this.specName = specName;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre de la especificación para este algoritmo.
	 */
	public String getSpecificationName()
	{
		return specName;
	}

	/////////////////////////////////////////////////////////////////
	public String getStereotype()
	{
		return "«algoritmo»";
	}
	
	/////////////////////////////////////////////////////////////////
	public String getCode()
	{
		return "a";
	}

}
