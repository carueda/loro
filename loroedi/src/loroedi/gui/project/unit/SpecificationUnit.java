package loroedi.gui.project.unit;

import loroedi.gui.project.model.IPackageModel;
import loro.Loro;

//////////////////////////////////////////////////
/**
 * @author Carlos Rueda
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
		return "«" +Loro.Str.get("specification")+ "»";
	}

	/////////////////////////////////////////////////////////////////
	public String getCode()
	{
		return "e";
	}
}
