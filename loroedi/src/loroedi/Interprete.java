package loroedi;

import loroedi.Preferencias;
import loroedi.gui.GUI;
import loroedi.jedit.JEditTextArea;
import loro.*;
import util.jterm.JTerm;
import util.jterm.ITextArea;
import util.jterm.taimp.*;

import java.io.*;
import java.awt.event.*;
import java.awt.Rectangle;
import java.awt.FlowLayout;
import java.awt.Container;
import javax.swing.*;
import java.net.URL;


/////////////////////////////////////////////////////////////////////
/**
 * Intérprete para acciones interactivas.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class Interprete
implements ActionListener
{
	static final String PROMPT         =  " $ ";
	static final String PREFIX_EXPR    =  " = ";
	static final String PREFIX_INVALID =  " ! ";
	static final String PREFIX_SPECIAL =  "   ";

	static private String version =	
"Loro - Sistema Didáctico de Programación\n"+
Info.obtNombre()+ " " +Info.obtVersion()+ " (Build " +Info.obtBuild()+ ")\n" +
Loro.obtNombre()+ " " +Loro.obtVersion()+ " (Build " +Loro.obtBuild()+ ")"
	;
	
	static private Interprete instance = null;
	static private JFrame frame = null;    // en donde se pone el interprete

	static private JButton butTerminar;
	static private JButton butCerrar;


	////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la instancia de esta clase.
	 */
	public static Interprete getInstance()
	{
		if ( instance == null )
			_createInstance();

		return instance;
	}


	////////////////////////////////////////////////////////////////////////////
	/**
	 * Crea la instancia unica del Interprete.
	 *
	 * @throws IllegalStateException   Ante un segundo llamado
	 */
	private static void _createInstance()
	{
		if ( instance != null )
			throw new IllegalStateException("Second call to _createInstance()!");

		instance = new Interprete();
		frame = new JFrame("Intérprete Interactivo de Loro");
		URL url = ClassLoader.getSystemClassLoader().getResource("img/icon.jpg");
		if ( url != null ) 
			frame.setIconImage(new ImageIcon(url).getImage());

		Container content_pane = frame.getContentPane();
		content_pane.add(instance.obtAreaTexto());

		JPanel pan = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton but;
		butCerrar = but = new JButton("Cerrar");
		butCerrar.setMnemonic(KeyEvent.VK_C);
		butCerrar.setToolTipText("Cierra esta ventana");
		but.addActionListener(instance);
		pan.add(but);
		pan.add(new JLabel("        "));
		butTerminar = but = new JButton("Terminar ejecución");
		butTerminar.setMnemonic(KeyEvent.VK_T);
		butTerminar.setToolTipText("Termina abruptamente la ejecución en curso");
		but.addActionListener(instance);
		//butTerminar.setEnabled(false); --Por ahora se deja habilitado siempre.
		pan.add(but);
		content_pane.add(pan, "South");

		Preferencias.Util.updateRect(frame, Preferencias.II_RECT);

		// start interaction:
		new Thread(new Runnable()
		{
			public void run()
			{
				instance.ii.run();
			}
		}).start();
	}

	////////////////////////////////////////////////////////////////////////////////
	//	             Instance:                //

	private JEditTextArea ta;
	private JTerm term;

	private PrintWriter pw;
	private BufferedReader br;

	private boolean execute = true;
	private IInterprete loroii;
	private IInterprete.IInteractiveInterpreter ii;


	/////////////////////////////////////////////////////////////////////
	/**
	 * Crea un interprete para acciones interactivas.
	 */
	private Interprete()
	{
		super();

		ta = JETextArea.createJEditTextArea(
			PROMPT,
			PREFIX_SPECIAL,
			PREFIX_INVALID,
			false
		);
		term = new JTerm((ITextArea) ta);

		pw = new PrintWriter(term.getWriter());
		br = new BufferedReader(term.getReader());

		loroii = Loro.crearInterprete(br, pw, false, null);

		ii = loroii.getInteractiveInterpreter();
		final IInterprete.IInteractiveInterpreter.IManager def_mgr = ii.getManager();
		ii.setManager(new IInterprete.IInteractiveInterpreter.IManager()
		{
			///////////////////////////////////////////////////////////////////////
			public String prompt()
			throws IOException
			{
				term.setPrefix(PROMPT);
				String text = br.readLine();
				term.setPrefix(PREFIX_SPECIAL);
				return text;
			}

			///////////////////////////////////////////////////////////////////////
			public void expression(String expr)
			{
				term.setPrefix(PREFIX_EXPR);
				pw.println(expr);
			}

			///////////////////////////////////////////////////////////////////////
			public String formatException(Exception exc)
			{
				return def_mgr.formatException(exc);
			}
			
			///////////////////////////////////////////////////////////////////////
			public void handleException(Exception exc)
			{
				String msg = def_mgr.formatException(exc);
				term.setPrefix(PREFIX_INVALID);
				pw.println(msg);
			}
		});
		
		loroii.setMetaListener(new IInterprete.IMetaListener()
		{
			String info = 
".version      - Muestra información general sobre versión del sistema\n" +
".limpiar      - Limpia la ventana"
			;
			
			///////////////////////////////////////////////////////////////////////
			public String getInfo()
			{
				return info;
			}
			
			public String execute(String meta)
			{
				String res = null;
				if ( meta.equals(".version") )
				{
					res = version;
				}
				else if ( meta.equals(".limpiar") )
				{
					ta.setText("");
					res = "";
				}
				return res;
			}
		});

		term.setPrefix(PREFIX_SPECIAL);
		pw.println(
			loroedi.Info.obtTituloII() + "\n" +
			"Escribe .? para obtener una ayuda"
		);
	}

	//////////////////////////////////////////////////////////////////
	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();
		if ( cmd.equalsIgnoreCase("Cerrar") )
		{
			frame.setVisible(false);
		}
		else if ( cmd.equalsIgnoreCase("Terminar ejecución") )
		{
			loroii.terminarExternamente();
		}

		term.requestFocus();
	}


	////////////////////////////////////////////////////////////////////////////
	public void mostrar()
	{
		if ( !frame.isShowing() )
		{
			frame.setVisible(true);
		}
		term.requestFocus();
	}

	///////////////////////////////////////////////////////////////////////
	JComponent obtAreaTexto()
	{
		return ta;
	}
}
