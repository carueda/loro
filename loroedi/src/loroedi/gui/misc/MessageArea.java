package loroedi.gui.misc;

import javax.swing.*;
import java.awt.event.*;


/////////////////////////////////////////////////////////
/**
 * Un simple area de mensajes.
 * Se hace un manejo simple para prevenir una sobrepetici�n de memoria:
 * se pone un l�mite para controlar el tama�o de esta �rea de mensajes.
 *
 * @author Carlos Rueda
 */
public class MessageArea extends JTextArea
{
	/**
	 * Un l�mite para controlar el tama�o de esta �rea de mensajes.
	 * Ayuda a prevenir una sobrepetici�n de memoria.
	 * Actualmente MAX_NO_LINES = 222.
	 */
	public static final int MAX_NO_LINES = 222;
	
	
	////////////////////////////////////////////////////////////////////////////
	public MessageArea()
	{
		super();
		setBackground(new java.awt.Color(244,221,227));
		setFont(new java.awt.Font("monospaced", 0, 12));
		setEditable(false);
	}
	
	////////////////////////////////////////////////////////////////////////////
	public void print(String m)
	{
		if ( getLineCount() > MAX_NO_LINES )
			clear();

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
