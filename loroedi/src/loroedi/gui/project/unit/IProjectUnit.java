package loroedi.gui.project.unit;

import loroedi.gui.project.model.IPackageModel;

import loro.IUnidad;

import javax.swing.*;

//////////////////////////////////////////////////
/**
 * Contiene informaci�n asociada a una unidad Loro para efectos
 * del Entorno Integrado.
 *
 * @author Carlos Rueda
 * @version 2002-08-12
 */
public interface IProjectUnit
{
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el estereotipo de esta unidad.
	 */
	public String getStereotype();

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la letra c�digo que indica el tipo exacto de esta unidad.
	 */
	public String getCode();

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre simple (sin paquete) de esta unidad.
	 */
	public String getName();

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre con paquete de esta unidad.
	 */
	public String getQualifiedName();

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre completo de esta unidad incluyendo su estereotipo,
	 * ejemplo "�clase� abc::de::Foo";
	 */
	public String getStereotypedName();

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el paquete de esta unidad.
	 */
	public IPackageModel getPackage();

	//////////////////////////////////////////////////
	/**
	 * Dice si el c�digo fuente de esta unidad es editable.
	 * Esta condici�n la determina el proyecto al que pertenezca
	 * esta unidad.
	 */
	public boolean isEditable();
	
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el c�digo fuente de esta unidad. 
	 * Puede ser null si tal c�digo no est� disponible.
	 *
	 * @return el c�digo fuente de esta unidad. Puede ser null.
	 */
	public String getSourceCode();
	
	////////////////////////////////////////////////////////////////
	/**
	 * Pone el c�digo fuente de esta unidad.
	 *
	 * @param src el c�digo fuente.
	 */
	public void setSourceCode(String src);
	
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la IUnidad asociada a esta unidad. 
	 *
	 * @return la IUnidad asociada a esta unidad.
	 */
	public IUnidad getIUnidad();
	
	////////////////////////////////////////////////////////////////
	/**
	 * Pone la IUnidad asociada a esta unidad. 
	 *
	 * @param u  la IUnidad a asociar.
	 */
	public void setIUnidad(IUnidad iunidad);
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el objeto de usuario.
	 */
	public Object getUserObject();

	/////////////////////////////////////////////////////////////////
	/**
	 * Pone el objeto de usuario.
	 */
	public void setUserObject(Object obj);

}
