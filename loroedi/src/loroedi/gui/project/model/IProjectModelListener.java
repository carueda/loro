package loroedi.gui.project.model;

import loroedi.gui.project.unit.*;

import javax.swing.*;

//////////////////////////////////////////////////
/** ~!~
 *
 * @author Carlos Rueda
 * @version 2002-07-13
 */
public interface IProjectModelListener
{
	/////////////////////////////////////////////////////////////////
	/**
	 * Avisa la ocurrencia de un evento.
	 */
	public void action(ProjectModelEvent e);

}
