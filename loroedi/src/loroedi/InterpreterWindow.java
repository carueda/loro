package loroedi;

import util.jterm.JTerm;
import util.jterm.JTermListener;
import util.jterm.ITextArea;
import util.jterm.taimp.*;

import loroedi.Info.Str;
import loroedi.Preferencias;
import loroedi.gui.GUI;
import loroedi.jedit.JEditTextArea;

import loro.*;

import java.io.*;
import java.awt.event.*;
import java.awt.Toolkit;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Point;
import java.util.StringTokenizer;
import javax.swing.*;
import java.net.URL;


/////////////////////////////////////////////////////////////////////
/**
 * Ventana para interpretación.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class InterpreterWindow extends Thread
implements ActionListener, JTermListener
{
	protected static final String PROMPT         =  " $ ";
	protected static final String PREFIX_EXPR    =  " = ";
	protected static final String PREFIX_INVALID =  " ! ";
	protected static final String PREFIX_SPECIAL =  "   ";

	static private String version =	
		Info.obtNombre()+ " " +Info.obtVersion()+ " (Build " +Info.obtBuild()+ ")\n" +
		Loro.obtNombre()+ " " +Loro.obtVersion()+ " (Build " +Loro.obtBuild()+ ")"
	;

	protected static final String defaultAddTitleRead = Str.get("msg.waiting_input");

	protected JFrame frame = null;    // en donde se pone el interprete

	protected JButton butTerminar;
	protected JButton butCerrar;
	protected JButton butStep;
	protected JButton butStepInto;
	protected JButton butResume;

	protected JEditTextArea ta;
	protected JTerm term;

	protected PrintWriter pw;
	protected BufferedReader br;

	protected IInterprete loroii;

	protected Thread readingThread = null;

	protected ObservadorPP obspp;	

	protected String title;
	private String addTitleRead;

	protected boolean interactive;
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Crea una ventana interprete.
	 *
	 * @param title For the window.
	 * @param hello Message to start with. Can be null.
	 * @param newSymTab See Loro.crearInterprete
	 * @param ejecutorpp step-by-step execution?
	 */
	public InterpreterWindow(
		String title, String hello, 
		boolean newSymTab, 
		boolean ejecutorpp
	)
	{
		super();
		this.title = title;
		addTitleRead = defaultAddTitleRead;

		ta = JETextArea.createJEditTextArea(
			PROMPT,
			PREFIX_SPECIAL,
			PREFIX_INVALID,
			false             // paintInvalid
		);
		
		term = new JTerm((ITextArea) ta);
		term.addJTermListener(this);

		pw = new PrintWriter(term.getWriter());
		br = new BufferedReader(term.getReader());

		obspp = ejecutorpp ? new ObservadorPP(this) : null;
		
		loroii = Loro.crearInterprete(br, pw, newSymTab, obspp);
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
			
			///////////////////////////////////////////////////////////////////////
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
		
		if ( hello != null )
		{
			term.setPrefix(PREFIX_SPECIAL);
			pw.println(hello);
		}
		
		frame = new JFrame(title);
		URL url = getClass().getClassLoader().getResource("img/icon.jpg");
		if ( url != null ) 
			frame.setIconImage(new ImageIcon(url).getImage());
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent _)
			{
				if ( butCerrar.isEnabled() )
				{
					close();
				}
			}
		});

		java.awt.Container content_pane = frame.getContentPane();

		content_pane.add(obtAreaTexto());

		javax.swing.JPanel pan = new javax.swing.JPanel(
			new java.awt.FlowLayout(java.awt.FlowLayout.LEFT)
		);
		javax.swing.JButton but;
		butCerrar = but = new javax.swing.JButton("Cerrar");
		butCerrar.setMnemonic(KeyEvent.VK_C);
		but.setActionCommand("close");
		but.setToolTipText("Cierra esta ventana");
		but.setEnabled(false);
		but.addActionListener(this);
		pan.add(but);
		
		pan.add(new javax.swing.JLabel("        "));
		butTerminar = but = new javax.swing.JButton("Terminar ejecución");
		butTerminar.setMnemonic(KeyEvent.VK_T);
		but.setActionCommand("terminate");
		but.setToolTipText("Termina abruptamente la ejecución en curso");
		but.addActionListener(this);
		but.setEnabled(false);
		pan.add(but);
		
		if ( loroii.isTraceable() )
		{
			pan.add(new javax.swing.JLabel("        "));
			butStep = but = new javax.swing.JButton("Pasar");
			but.setActionCommand("step");
			but.setToolTipText("Siguiente paso en la ejecución");
			but.addActionListener(this);
			but.setEnabled(true);
			pan.add(but);
			
			butStepInto = but = new javax.swing.JButton("Entrar");
			but.setActionCommand("step-into");
			but.setToolTipText("Entra en elemento");
			but.addActionListener(this);
			but.setEnabled(true);
			pan.add(but);
			
			butResume = but = new javax.swing.JButton("Continuar");
			but.setActionCommand("resume");
			but.setToolTipText("Continúa la ejecución");
			but.addActionListener(this);
			but.setEnabled(true);
			pan.add(but);
		}
		
		content_pane.add(pan, "South");
		Preferencias.Util.updateRect(frame, Preferencias.I_RECT);
	}

	
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
		String msg = null;
		try
		{
			throw exc;
		}
		catch(EjecucionException ex)
		{
			if ( ex.esTerminacionInterna() )
			{
				msg = "Ejecución terminada. Código de terminación = " 
					+ex.obtCodigoTerminacionInterna()
				;
			}
			else
			{
				StringWriter sw = new StringWriter();
				ex.printStackTrace(new PrintWriter(sw));
				msg = ex.getMessage() + "\n" +sw.toString();
			}
		}
		catch(CompilacionException ex)
		{
			msg = ex.getMessage();
		}
		catch(InterruptedIOException ex)
		{
			msg = Loro.Str.get("ii.interrupted_io");
		}
		catch(Exception ex)
		{
			StringWriter sw = new StringWriter();
			PrintWriter psw = new PrintWriter(sw);
			psw.println("UNEXPECTED");
			ex.printStackTrace(psw);
			psw.println("This is a BUG!!");
			msg = sw.toString();
		}
		return msg;
	}
	
	///////////////////////////////////////////////////////////////////////
	public void handleException(Exception exc)
	{
		String msg = formatException(exc);
		term.setPrefix(PREFIX_INVALID);
		pw.println(msg);
	}
	
	
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * Simplemente invoca goInteractive().
	 */
	protected void body()
	throws Exception
	{
		goInteractive();
	}
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * Hace un ciclo de ejecución interactiva.
	 * Para terminarlo, debe llamarse endInteractive();
	 */
	protected void goInteractive()
	throws Exception
	{
		interactive = true;
		butCerrar.setEnabled(true);
		while ( interactive )
		{
			try
			{
				String text = prompt();
				if ( text == null )
					break;
				
				text = text.trim();
				if ( text.length() == 0 )
					continue;

				butTerminar.setEnabled(true);
				String res = loroii.procesar(text);
				if ( res != null )
					expression(res);
				
				if ( obspp != null && obspp.getSymbolTableWindow() != null )
					obspp.getSymbolTableWindow().update();
			}
			catch ( Exception ex )
			{
				handleException(ex);
			}
			finally
			{
				butTerminar.setEnabled(false);
				enableTraceableButtons(false);
				GUI.updateSymbolTable();
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * Hace terminar el ciclo interactivo iniciado con goInteractive().
	 * Esto significa completar el comando en curso pero no continuar 
	 * pidiendo más comandos.
	 */
	protected void endInteractive()
	throws Exception
	{
		interactive = false;
	}
	
	///////////////////////////////////////////////////////////////////////
	void enableTraceableButtons(boolean enable)
	{
		if ( loroii.isTraceable() )
		{
			butStep.setEnabled(enable);
			butStepInto.setEnabled(enable);
			butResume.setEnabled(enable);
		}
	}
	

	///////////////////////////////////////////////////////////////////////
	public void run()
	{
		try
		{
			body();
		}
		catch ( Exception ex )
		{
			handleException(ex);
		}
		finally
		{
			term.setPrefix(PREFIX_SPECIAL);
			term.setEditable(false);
			butCerrar.setEnabled(true);
			butTerminar.setEnabled(false);
			enableTraceableButtons(false);
			GUI.updateSymbolTable();
		}
	}

	//////////////////////////////////////////////////////////////////
	protected void terminate()
	{
		loroii.terminarExternamente();
		if ( readingThread != null && readingThread != Thread.currentThread() )
			readingThread.interrupt();
	}
	
	
	//////////////////////////////////////////////////////////////////
	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();
		if ( cmd.equals("close") )
		{
			close();
			return; //  term.requestFocus() innecesario.
		}
		else if ( cmd.equals("terminate") )
		{
			terminate();
		}
		else if ( cmd.equals("resume") )
		{
			obspp.resume();
		}
		else if ( cmd.startsWith("step") )
		{
			try
			{
				obspp.nextStep(cmd.equals("step-into"));
			}
			catch(InterruptedException ex)
			{
				if ( readingThread != null && readingThread != Thread.currentThread() )
				{
					readingThread.interrupt();
				}
			}
		}
		term.requestFocus();
	}

	////////////////////////////////////////////////////////////////////////////
	public void mostrar()
	{
		if ( !frame.isShowing() )
			frame.setVisible(true);

		term.requestFocus();
	}

	///////////////////////////////////////////////////////////////////////
	public ITextArea getTextArea()
	{
		return ta;
	}
	
	///////////////////////////////////////////////////////////////////////
	JComponent obtAreaTexto()
	{
		return ta;
	}

	///////////////////////////////////////////////////////////////////////
	protected void interpret(String text, String ask_enter)
	throws Exception
	{
		term.setPrefix(PROMPT);
		pw.print(text);

		if ( ask_enter != null )
		{
			// don't let the user edit the jterm area:
			term.setEditable(false);
			try
			{
				addTitleRead = "<<presionar Intro para continuar>>";				
				readLine(ask_enter);
			}
			finally
			{
				term.setEditable(true);
				addTitleRead = defaultAddTitleRead;
			}
		}
		else
		{
			pw.println();
		}

		procesarLoro(text);
	}
	
	///////////////////////////////////////////////////////////////////////
	protected void procesarLoro(String text)
	throws AnalisisException
	{
		try
		{
			term.setPrefix(PREFIX_SPECIAL);
			
			// habilite el boton de terminacion si el modo es ejecucion:
			butTerminar.setEnabled(loroii.getExecute());
			enableTraceableButtons(loroii.getExecute());
			
			if ( loroii.getExecute() )
			{
				String res = loroii.ejecutar(text);
				if ( res != null )
				{
					term.setPrefix(PREFIX_EXPR);
					pw.println(res);
				}
			}
			else
			{
				loroii.compilar(text);
			}
		}
		finally
		{
			Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
			butTerminar.setEnabled(false);
			enableTraceableButtons(false);
			if ( obspp != null && obspp.getSymbolTableWindow() != null )
				obspp.getSymbolTableWindow().update();
		}
	}

	/////////////////////////////////////////////////////////////////
	/** For JTermListener */
	public void waitingRead(boolean reading)
	{
		enableTraceableButtons(!reading);
		if ( reading )
		{
			frame.setTitle(title+ " " +addTitleRead);
			term.requestFocus();
		}
		else
		{
			frame.setTitle(title);
		}
	}
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * @param s Initial part to include in the read string.
	 */
	protected String readLine(String s)
	throws Exception
	{
		term.setInitialStringToRead(s);
		butTerminar.setEnabled(true);
		// no queremos control de seguimiento mientras se lee:
		enableTraceableButtons(false);
		readingThread = Thread.currentThread();
		term.requestFocus();
		try
		{
			return br.readLine();
		}
		finally
		{
			frame.setTitle(title);
			enableTraceableButtons(loroii.getExecute());
			readingThread = null;
		}
	}
	
	///////////////////////////////////////////////
	/**
	 * Cierra la ventana. Si el indicador interactive es true,
	 * esto es lo único que sucede. En otro caso (interactive es false),
	 * este objeto se destruye completamente.
	 * No se hace nada si el botón de "Cerrar" está deshabilitado.
	 */
	protected void close()
	{
		if ( !butCerrar.isEnabled() )
			return;
		
		frame.setVisible(false);
		
		if ( interactive )
			return;        // no se hace más. Se puede llamar mostrar() después.
		
		if ( obspp != null )
			obspp.end();
		try
		{
			pw.close();
			br.close();
		}
		catch(Exception e)
		{
		}
		finally
		{
			frame.dispose();
		}
	}
}
