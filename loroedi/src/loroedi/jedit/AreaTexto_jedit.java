package loroedi.jedit;

import java.io.*;
import java.awt.Component;
import java.awt.event.*;

import javax.swing.text.*;
import javax.swing.event.*;


import util.editor.Editor;
import util.editor.AreaTexto;

import java.awt.Font;
import java.awt.Color;

////////////////////////////////////////////////////////
/**
 * Un area de texto implementada sobre JEditTextArea.
 */
public class AreaTexto_jedit extends AreaTexto
{
	JEditTextArea je;


	/////////////////////////////////////////////////////////////////
	/**
	 */
	public Component comoComponent()
	{
		return je;
	}
	/////////////////////////////////////////////////////////////////
	public void copy()
	{
		je.copy();
	}
	/////////////////////////////////////////////////////////////////
	public void cut()
	{
		je.cut();
	}
	/////////////////////////////////////////////////////////////////
	public int getCaretPosition()
	{
		return je.getCaretPosition();
	}

	/////////////////////////////////////////////
	public String getText()
	{
		return je.getText();
	}


	/////////////////////////////////////////////////////////////////
	public void irALinea(int num)
	{
		int count = je.getLineCount();
		if ( num < 1 )
			num = 1;
		if ( num > count )
			num = count;

		int pos = je.getLineStartOffset(num-1);
		je.select(pos, pos);
	}

	/////////////////////////////////////////////////////////////////
	public void paste()
	{
		je.paste();
	}

	/////////////////////////////////////////////
	public void setSelectionColor(Color color)
	{
		je.getPainter().setSelectionColor(color);
	}
	
	/////////////////////////////////////////////////////////////////
	public void select(int start, int end)
	{
		try
		{
			je.select(start, end);
		}
		catch(IllegalArgumentException e)
		{
			// ignore
		}
	}
	/////////////////////////////////////////////////////////////////
	public void selectAll()
	{
		je.selectAll();
	}
	/////////////////////////////////////////////////////////////////
	public void setCaretPosition(int pos)
	{
		je.setCaretPosition(pos);
	}

	/////////////////////////////////////////////
	public void setText(String t)
	{
		je.setText(t);
	}
	/////////////////////////////////////////////
	/**
	 * Hace que el area de texto obtenga el foco
	 * de los eventos.
	 */
	public void traerAlFrente()
	{
		je.requestFocus();
	}



	/////////////////////////////////////////////
	/**
	 *
	 */
	public AreaTexto_jedit(Editor se)
	{
		super(se);
		je = Loro.crearJEditTextArea();
		je.getDocument().addDocumentListener(new MyDocumentListener());
		je.addKeyListener(new MyKeyListener());
		je.setMinimumSize(new java.awt.Dimension(100, 100));
	}

	////////////////////////////////////////////////////////
	/**
	 * Para cumplir con el contrato de AreaTexto por el cual debo
	 * avisarle a mi editor, obtEditor(), cuando se presenten cambios
	 * en el contenido.
	 */
	class MyDocumentListener implements DocumentListener
	{
		public void insertUpdate(DocumentEvent e)
		{
			// oops: no es grave, pero curioso: (2002-02-10)
			// se.textoModificado()
			//  VAJ dice que 'se' no es visible (siendo protected)??
			// Ok. utilizo este metodo:
			obtEditor().textoModificado();
		}
		
		public void removeUpdate(DocumentEvent e)
		{
			obtEditor().textoModificado();
		}
		
		public void changedUpdate(DocumentEvent e) {}
		
	}// class MyDocumentListener

	////////////////////////////////////////////////////////
	/**
	 * Esta es para atender algunos comandos asociados al clipboard.
	 */
	class MyKeyListener extends KeyAdapter
	{
		////////////////////////////////////////////////////////
		public void keyPressed(KeyEvent ke)
		{
			int keyCode = ke.getKeyCode();
			int modifiers = ke.getModifiers();
			boolean attended = false;

			// Atiende Ctrl-Insert (copy); 
			// Shift-Insert (paste), y Shift-Delete (cut)
			if ( (modifiers & KeyEvent.SHIFT_MASK) != 0 )
			{
				if ( keyCode == KeyEvent.VK_INSERT )
				{
					je.paste();
					attended = true;
				}
				else if ( keyCode == KeyEvent.VK_DELETE )
				{
					je.cut();
					attended = true;
				}
			}
			else if( (modifiers & KeyEvent.CTRL_MASK) != 0
			&&  keyCode == KeyEvent.VK_INSERT )
			{
				je.copy();
				attended = true;
			}

			// adicionado para atender Ctrl-C, Alt-C (copy); 
			// Ctrl-V, Alt-V (paste), y Ctrl-X, Alt-X (cut)
			// (2001-07-31)
			else if ( (modifiers & KeyEvent.CTRL_MASK) != 0 
				   || (modifiers & KeyEvent.ALT_MASK) != 0 
			)
			{
				if ( keyCode == KeyEvent.VK_V )
				{
					je.paste();
					attended = true;
				}
				else if ( keyCode == KeyEvent.VK_X )
				{
					je.cut();
					attended = true;
				}
				else if ( keyCode == KeyEvent.VK_C )
				{
					je.copy();
					attended = true;
				}
			}

			if ( attended )
			{
				ke.consume();
			}

		}
	} // class MyKeyListener

	/////////////////////////////////////////////
	public Font getFont()
	{
		return je.getFont();
	}

	/////////////////////////////////////////////
	public void setFont(Font font)
	{
		je.setFont(font);
	}

	////////////////////////////////////////////////////////////////
	public void setEditable(boolean editable)
	{
		je.setEditable(editable);
	}
}