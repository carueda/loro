package util.consola;

import util.jterm.JTerm;
import util.jterm.ITextArea;
import util.jterm.JTermListener;
import util.jterm.taimp.*;
import loroedi.jedit.JEditTextArea;

import java.io.Reader;
import java.io.Writer;
import java.awt.Color;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.border.TitledBorder;
import javax.swing.JPopupMenu;
import javax.swing.JButton;

import loro.IEjecutor;

////////////////////////////////////////////////////////////
/**
 * Ventana con consola versión JTerm.
 *
 * @author Carlos Rueda
 * @version 0.1 (2001-02-23)
 */
public class ConsolaJTerm extends JFrame
implements Consola, ActionListener, JTermListener
{
	/** El objeto JTerm asociado. */
	JTerm jterm;

	/** El titulo de la zona de consola. */
	TitledBorder border;

	/** El color original del titulo. */
	Color borderColor;

	/** El ejecutor. */
	IEjecutor ejecutor;

	/**
	 * La ejecución ya ha terminado?
	 * Permite dejar cerrar la ventana.
	 */
	boolean ejecucionTerminada;


	JPopupMenu popup;
	JButton butTerminar;
	JButton butCerrar;
	JButton butLimpiar;
	
	///////////////////////////////////////////////
	/**
	 */
	public ConsolaJTerm()
	{
		this(new SwingTextArea(30, 100));
		
//		final String PREFIX_SPECIAL =  " ";
//		this(JETextArea.createJEditTextArea(
//				null, // prompt,
//				PREFIX_SPECIAL, // prefix_special,
//				null, // prefix_invalid,
//				false // paintInvalid
//			)
//		);
//		jterm.setPrefix(PREFIX_SPECIAL);
		
	}
	///////////////////////////////////////////////
	/**
	 */
	public ConsolaJTerm(ITextArea ta)
	//public ConsolaJTerm(SwingTextArea ta)
	{
		super();
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener
		(
			new java.awt.event.WindowAdapter()
			{
				public void windowClosing(java.awt.event.WindowEvent e)
				{
					if ( ejecucionTerminada )
						cerrar();
				}
			}
		);

		java.awt.Container content_pane = getContentPane();

		javax.swing.JPanel pan = new javax.swing.JPanel(
			new java.awt.FlowLayout(java.awt.FlowLayout.LEFT)
		);
		javax.swing.JButton but;
		butLimpiar = but = new javax.swing.JButton("Limpiar");
		but.addActionListener(this);
		pan.add(but);
		butCerrar = but = new javax.swing.JButton("Cerrar");
		butCerrar.setEnabled(false);
		but.addActionListener(this);
		pan.add(but);
		pan.add(new javax.swing.JLabel("        "));
		butTerminar = but = new javax.swing.JButton("Terminar ejecución");
		but.addActionListener(this);
		pan.add(but);
		content_pane.add(pan, "North");

		// Consola:
		jterm = new JTerm(ta);
		jterm.addJTermListener(this);
		javax.swing.JScrollPane sp = null;
		if ( ta instanceof JEditTextArea )
		{
			sp = new javax.swing.JScrollPane((JEditTextArea) ta);
		}
		else if ( ta instanceof SwingTextArea )
		{
			sp = new javax.swing.JScrollPane((SwingTextArea) ta);
		}
		
		border = new TitledBorder("Consola");
		sp.setBorder(border);
		borderColor = border.getTitleColor();
		content_pane.add(sp);

		crearJPopupMenu();

		ejecucionTerminada = false;

		java.awt.event.MouseListener ml =
			new java.awt.event.MouseAdapter()
			{
				public void mousePressed(java.awt.event.MouseEvent e)
				{
					showPopup(e);
				}
				public void mouseReleased(java.awt.event.MouseEvent e)
				{
					showPopup(e);
				}
				private void showPopup(java.awt.event.MouseEvent e)
				{
					if (e.isPopupTrigger())
					{
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		;
		ta.addMouseListener(ml);
		javax.swing.JLabel l = new javax.swing.JLabel("clic-derecho para cambiar letra");
		l.setFont(new java.awt.Font("dialog", java.awt.Font.ITALIC, 12));
		l.addMouseListener(ml);
		content_pane.add(l, "South");

		//setSize(800, 500);
		pack();
	}
	////////////////////////////////////////////////
	/**
	 * Despliega esta consola.
	 */
	public void abrir()
	{
		setVisible(true);
		requestFocus();
		jterm.requestFocus();
	}
	//////////////////////////////////////////////////////////////////
	public void actionPerformed(java.awt.event.ActionEvent e)
	{
		String cmd = e.getActionCommand();
		if ( cmd.equalsIgnoreCase("Limpiar") )
		{
			jterm.clear();
		}
		else if ( cmd.equalsIgnoreCase("Cerrar") )
		{
			if ( ejecucionTerminada )
				cerrar();
		}
		else if ( cmd.equalsIgnoreCase("Terminar ejecución") )
		{
			if ( ejecutor.esTerminableExternamente() )
			{
				ejecutor.terminarExternamente();
			}
		}
		else if ( cmd.startsWith("Incrementar tamaño letra") )
		{
			jterm.incrementFontSize();
		}
		else if ( cmd.startsWith("Decrementar tamaño letra") )
		{
			jterm.decrementFontSize();
		}
		else if ( cmd.startsWith("Negrilla") )
		{
			jterm.toggleBoldFont();
		}
		else if ( cmd.startsWith("Itálica") )
		{
			jterm.toggleItalicFont();
		}
		jterm.requestFocus();
	}
	///////////////////////////////////////////////
	public void cerrar()
	{
		try
		{
			jterm.getReader().close();
			jterm.getWriter().close();
		}
		catch(Exception e)
		{
		}
		dispose();
	}
	//////////////////////////////////////////////////////////////////
	private void crearJPopupMenu()
	{
		popup = new javax.swing.JPopupMenu();

		javax.swing.JMenuItem menuItem =
		new javax.swing.JMenuItem("Incrementar tamaño letra (F11)");
		menuItem.addActionListener(this);
		popup.add(menuItem);

		menuItem = new javax.swing.JMenuItem("Decrementar tamaño letra (F12)");
		menuItem.addActionListener(this);
		popup.add(menuItem);

		popup.addSeparator();

		menuItem = new javax.swing.JMenuItem("Negrilla (F9)");
		menuItem.addActionListener(this);
		popup.add(menuItem);

		menuItem = new javax.swing.JMenuItem("Itálica (F10)");
		menuItem.addActionListener(this);
		popup.add(menuItem);
	}
	/////////////////////////////////////////////////////////////////
	/**
	 */
	public void ejecucionTerminada()
	{
		ejecucionTerminada = true;
		//setTitle("Ejecución terminada");
		butCerrar.setEnabled(true);
		butTerminar.setEnabled(false);

	}
	///////////////////////////////////////////////
	/**
	 */
	public java.io.Reader obtReader()
	{
		return jterm.getReader();
	}
	///////////////////////////////////////////////
	/**
	 */
	public java.io.Writer obtWriter()
	{
		return jterm.getWriter();
	}

	///////////////////////////////////////////////
	public void reset()
	{
		jterm.reset();
	}
	/////////////////////////////////////////////////////////////////
	/**
	 * Called with true when entering in the method
	 * JTermReader.read(char[],int,int); with false when exiting.
	 */
	public void waitingRead(boolean reading)
	{
		border.setTitle("Consola" + (reading ? " (esperando entrada...)" : ""));
		border.setTitleColor(reading ? Color.red : borderColor);
		butLimpiar.setEnabled(!reading);
		repaint();
	}

	public void ponEjecutor(IEjecutor ejecutor)
	{
		this.ejecutor = ejecutor;
	}
}