package loroedi.gui.misc;

import javax.swing.*;
import java.awt.event.*;

/////////////////////////////////////////////////////////
/**
 * Convenience action.
 */
public class NotImplementedAction extends AbstractAction
{
	/////////////////////////////////////////////////////////
	public NotImplementedAction(String name)
	{
		super(name);
		putValue(SHORT_DESCRIPTION, "Not yet implemented");
	}

	/////////////////////////////////////////////////////////
	public void actionPerformed(ActionEvent e)
	{
		java.awt.Toolkit.getDefaultToolkit().beep();
		JOptionPane.showMessageDialog(
			null,
			"This option is pending",
			"Oops",
			JOptionPane.INFORMATION_MESSAGE
		);
	}
}

