package loroedi.gui.project.model;

import loroedi.gui.project.unit.*;

import javax.swing.*;

//////////////////////////////////////////////////
/** ~!~
 *
 * @author Carlos Rueda
 */
public interface IProjectModelListener {
	/////////////////////////////////////////////////////////////////
	/**
	 * Avisa la ocurrencia de un evento.
	 */
	public void action(ProjectModelEvent e);

}
