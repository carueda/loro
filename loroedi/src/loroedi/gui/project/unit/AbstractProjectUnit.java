package loroedi.gui.project.unit;

import loroedi.gui.project.model.IPackageModel;
import loro.IUnidad;

//////////////////////////////////////////////////
/**
 *
 * @author Carlos Rueda
 * @version 2002-08-12
 */
public abstract class AbstractProjectUnit implements IProjectUnit
{
	IPackageModel pkg;
	String name;
	String src;
	IUnidad iunidad;
	Object userObj;
	
	/////////////////////////////////////////////////////////////////
	protected AbstractProjectUnit(IPackageModel pkg, String name)
	{
		this.pkg = pkg;
		this.name = name;
	}
	
	/////////////////////////////////////////////////////////////////
	public String getStereotype()
	{
		return "«?»";
	}
	
	/////////////////////////////////////////////////////////////////
	public String getName()
	{
		return name;
	}

	/////////////////////////////////////////////////////////////////
	public String getQualifiedName()
	{
		return pkg.getModel().isAnonymous(pkg) ? getName() : pkg.getName() + "::" + getName();
	}
	
	/////////////////////////////////////////////////////////////////
	public String getStereotypedName()
	{
		return getStereotype()+ " " +getQualifiedName();
	}
	
	/////////////////////////////////////////////////////////////////
	public IPackageModel getPackage()
	{
		return pkg;
	}
	
	////////////////////////////////////////////////////////////////
	public String getSourceCode()
	{
		return src;
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Retorna lo mismo que <code>pkg.isModifiable()</code>, donde
	 * <code>pkg</code> es el argumento con que se creo este objeto.
	 */
	public boolean isEditable()
	{
		return pkg.isModifiable();
	}
	
	////////////////////////////////////////////////////////////////
	public void setSourceCode(String src)
	{
		this.src = src;
	}

	/////////////////////////////////////////////////////////////////
	public IUnidad getIUnidad()
	{
		return iunidad;
	}
	
	////////////////////////////////////////////////////////////////
	public void setIUnidad(IUnidad iunidad)
	{
		this.iunidad = iunidad;
	}

	/////////////////////////////////////////////////////////////////
	public Object getUserObject()
	{
		return userObj;
	}

	/////////////////////////////////////////////////////////////////
	public void setUserObject(Object obj)
	{
		this.userObj = obj;
	}
	
	/////////////////////////////////////////////////////////////////
	public String toString()
	{
		return name;
	}

}
