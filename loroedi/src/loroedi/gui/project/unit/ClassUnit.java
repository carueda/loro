package loroedi.gui.project.unit;

import loroedi.gui.project.model.IPackageModel;
import loro.Loro;

//////////////////////////////////////////////////
/**
 * @author Carlos Rueda
 */
public class ClassUnit extends AbstractProjectUnit
{
	/////////////////////////////////////////////////////////////////
	public ClassUnit(IPackageModel pkg, String name)
	{
		super(pkg, name);
	}

	/////////////////////////////////////////////////////////////////
	public String getStereotype()
	{
		return "«" +Loro.Str.get("class")+ "»";
	}

	/////////////////////////////////////////////////////////////////
	public String getCode()
	{
		return "c";
	}
}
