package loroedi.gui.project.model;

import loroedi.gui.project.unit.*;

import javax.swing.*;

//////////////////////////////////////////////////
/** ~!~
 * @author Carlos Rueda
 */
public class ProjectModelEvent extends java.util.EventObject
{
	public static final int PACKAGE_ADDED      = 1;
	public static final int SPEC_ADDED         = 2;
	public static final int ALGORITHM_ADDED    = 3;
	public static final int CLASS_ADDED        = 4;

	public static final int PACKAGE_REMOVED    = 101;
	public static final int SPEC_REMOVED       = 102;
	public static final int ALGORITHM_REMOVED  = 103;
	public static final int CLASS_REMOVED      = 104;

	/** The affected element. */
	Object element;
	
	/** The event type. */
	int id;
	
	//////////////////////////////////////////////////
	public ProjectModelEvent(Object source, int id, Object element)
	{
		super(source);
		this.id = id;
		this.element = element;
	}
	
	//////////////////////////////////////////////////
	/**
	 * Retorna el tipo de este evento.
	 */
	public int getID()
	{
		return id;
	}
	
	//////////////////////////////////////////////////
	/**
	 * Retorna el elemento asociado.
	 */
	public Object getElement()
	{
		return element;
	}
}
