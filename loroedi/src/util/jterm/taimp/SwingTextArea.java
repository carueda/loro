package util.jterm.taimp;

import util.jterm.ITextArea;

import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
import java.awt.Color;

//////////////////////////////////////////////////////////////
/**
 * A Swing based implementation of ITextArea.
 *
 * @author Carlos Rueda
 * @version Jan/13/2000
 * @version 2000-01-17
 * @version 2001-08-28 New constructor with rows and cols.
 */
public class SwingTextArea extends JTextArea implements ITextArea
{
	//////////////////////////////////////////////////////////////
	public SwingTextArea(int rows, int cols)
	{
		super("", rows, cols);
		setBackground(new Color(229, 229, 229));
		setForeground(Color.blue);
		setCaretColor(Color.red);

	}

	//////////////////////////////////////////////////////////////
	public SwingTextArea()
	{
		super();
		setBackground(Color.black);
		setForeground(Color.lightGray);
		setCaretColor(Color.white);
	}
}