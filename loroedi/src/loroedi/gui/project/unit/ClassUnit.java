package loroedi.gui.project.unit;

import loroedi.gui.project.model.IPackageModel;

//////////////////////////////////////////////////
/**
 *
 * @author Carlos Rueda
 * @version 2002-08-03
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
		return "«clase»";
	}

	/////////////////////////////////////////////////////////////////
	public String getCode()
	{
		return "c";
	}
}
