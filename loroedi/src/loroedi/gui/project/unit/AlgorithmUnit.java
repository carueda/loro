package loroedi.gui.project.unit;

import loroedi.gui.project.model.IPackageModel;
import loro.Loro;

//////////////////////////////////////////////////
/**
 * @author Carlos Rueda
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
		return "«" +Loro.Str.get("algorithm")+ "»";
	}
	
	/////////////////////////////////////////////////////////////////
	public String getCode()
	{
		return "a";
	}

}
