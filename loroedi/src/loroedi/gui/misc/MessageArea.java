package loroedi.gui.misc;


import javax.swing.*;
import java.awt.event.*;


/////////////////////////////////////////////////////////
/**
 * Un simple area de mensajes.
 *
 * @author Carlos Rueda
 * @version 2002-08-24
 */
public class MessageArea extends JTextArea
{
	////////////////////////////////////////////////////////////////////////////
	public MessageArea()
	{
		super();
		setBackground(new java.awt.Color(244,221,227));
		setFont(new java.awt.Font("dialog", 0, 12));
		setEditable(false);
	}
	
	////////////////////////////////////////////////////////////////////////////
	public void print(String m)
	{
		append(m);
		setCaretPosition(getText().length());
	}
	
	////////////////////////////////////////////////////////////////////////////
	public void println(String m)
	{
		print(m + "\n");
	}

	////////////////////////////////////////////////////////////////////////////
	public void clear()
	{
		setText("");
	}
}
