package loroedi.gui.project.unit;

import loroedi.gui.project.model.IPackageModel;

//////////////////////////////////////////////////
/**
 *
 * @author Carlos Rueda
 * @version 2002-08-03
 */
public class SpecificationUnit extends AbstractProjectUnit
{
	/////////////////////////////////////////////////////////////////
	public SpecificationUnit(IPackageModel pkg, String name)
	{
		super(pkg, name);
	}

	/////////////////////////////////////////////////////////////////
	public String getStereotype()
	{
		return "«especificación»";
	}

	/////////////////////////////////////////////////////////////////
	public String getCode()
	{
		return "e";
	}
}
