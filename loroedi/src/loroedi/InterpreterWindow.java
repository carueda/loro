package loroedi;

import util.jterm.JTerm;
import util.jterm.JTermListener;
import util.jterm.ITextArea;
import util.jterm.taimp.*;

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
 */
public abstract class InterpreterWindow extends Thread
implements ActionListener, JTermListener
{
	protected static final String PROMPT         =  " $ ";
	protected static final String PREFIX_EXPR    =  "=  ";
	protected static final String PREFIX_INVALID =  "!  ";
	protected static final String PREFIX_SPECIAL =  "   ";

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

	/////////////////////////////////////////////////////////////////////
	/**
	 * Crea una ventana interprete.
	 *
	 * @param title For the window.
	 * @param hello Message to start with. Can be null.
	 * @param newSymTab See Loro.crearInterprete
	 * @param ejecutorpp step-by-step execution?
	 * @param editable Text area editable?
	 */
	public InterpreterWindow(
		String title, String hello, 
		boolean newSymTab, 
		boolean ejecutorpp,
		boolean editable
	)
	{
		super();
		this.title = title;

		ta = JETextArea.createJEditTextArea(
			PROMPT,
			PREFIX_SPECIAL,
			PREFIX_INVALID,
			false
		);
		ta.setEditable(editable);
		
		term = new JTerm((ITextArea) ta);
		term.addJTermListener(this);

		pw = new PrintWriter(term.getWriter());
		br = new BufferedReader(term.getReader());

		obspp = ejecutorpp ? new ObservadorPP(this) : null;
		
		loroii = Loro.crearInterprete(br, pw, newSymTab, obspp);

		if ( hello != null )
		{
			term.setPrefix(PREFIX_SPECIAL);
			pw.print(hello);
			term.setPrefix(null);
			pw.println();
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
		but.setActionCommand("close");
		but.setToolTipText("Cierra esta ventana");
		but.setEnabled(false);
		but.addActionListener(this);
		pan.add(but);
		
		pan.add(new javax.swing.JLabel("        "));
		butTerminar = but = new javax.swing.JButton("Terminar ejecución");
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
	protected abstract void body()
	throws Exception;
	
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
	protected void handleException(Exception exc)
	{
		String msg = null;
		try
		{
			throw exc;
		}
		catch ( EjecucionException ex )
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
		catch ( InterruptedIOException ex )
		{
			term.setPrefix(null);
			pw.println();
			msg = "Ejecución interrumpida en operación de entrada/salida.";
			//ex.printStackTrace();
		}
		catch(Exception ex)
		{
			StringWriter sw = new StringWriter();
			PrintWriter psw = new PrintWriter(sw);
			psw.println("INESPERADO");
			ex.printStackTrace(psw);
			psw.println(
"Esta es una anomalía del sistema.  Consultar por favor la\n"+
"ayuda general (F1) para saber cómo proceder en esta situación."
			);
			msg = sw.toString();
		}

		if ( msg != null )
		{
			term.setPrefix(PREFIX_INVALID);
			pw.print(msg);
			term.setPrefix(null);
			pw.println();
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
			butCerrar.setEnabled(true);
			butTerminar.setEnabled(false);
			enableTraceableButtons(false);
			GUI.updateSymbolTable();
		}
	}

	//////////////////////////////////////////////////////////////////
	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand();
		if ( cmd.equals("close") )
		{
			close();
		}
		else if ( cmd.equals("terminate") )
		{
			loroii.terminarExternamente();
			if ( readingThread != null && readingThread != Thread.currentThread() )
			{
				readingThread.interrupt();
			}
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



	///////////////////////////////////////////////////////////////////////
	void metaProcesar(String text)
	{
		String msg;

		if ( text.equals(".?") )
		{
			msg =
"El Intérprete Interactivo permite ejecutar instrucciones de\n"+
"manera inmediata. Escribe una instrucción a continuación del\n"+
"indicador (símbolo " +PROMPT.trim()+ ") y presiona Entrar.\n" +
"\n"+
"Hay algunos comandos especiales para el propio intérprete\n"+
"reconocidos porque empiezan con punto (.):\n"+
"\n"+
"   .?            - Muestra esta ayuda\n" +
"   .limpiar      - Limpia la ventana\n"+
"   .vars         - Muestra las variables declaradas actualmente\n" +
"   .borrar ID    - Borra la declaración de la variable indicada\n" +
"   .borrarvars   - Borra todas las variables declaradas\n" +
"   .verobj nivel - Pone máximo nivel para visualizar objetos\n"+
"   .verarr long  - Pone máxima longitud para visualizar arreglos\n" +
"   .version      - Muestra información general sobre versión del sistema\n" +
"   .??           - Muestra otros comandos avanzados"
			;
		}
		else if ( text.equals(".??") )
		{
			msg =
"Comandos avanzados:\n"+
"   .modo         - Muestra el modo de interpretación actual.\n" +
"                   Hay dos modos de operación:\n" +
"                     - ejecución completa (por defecto)\n" +
"                     - sólo compilación (usar con cuidado)\n" +
"   .cambiarmodo  - Intercambia el modo de interpretación\n"+
"   .gc           - Reciclar memoria\n"
			;
		}
		else if ( text.equals(".vars") )
		{
			msg = Loro.getSymbolTable().toString();
		}
		else if ( text.equals(".borrarvars") )
		{
			loroii.reiniciar();
			msg = Loro.getSymbolTable().toString();
		}
		else if ( text.startsWith(".borrar") )
		{
			StringTokenizer st = new StringTokenizer(text.substring(".borrar".length()));
			try
			{
				String id = st.nextToken();
				msg = id+ " " +(loroii.quitarID(id)
					? "borrado" 
					: "no declarado"
				);
			}
			catch ( Exception ex )
			{
				msg = "Indique un nombre de variable";
			}
		}
		else if ( text.equals(".limpiar") )
		{
			ta.setText("");
			msg = "";
		}
		else if ( text.equals(".version") )
		{
			msg =
"Loro - Sistema Didáctico de Programación\n"+
Info.obtNombre()+ " " +Info.obtVersion()+ " (Build " +Info.obtBuild()+ ")\n" +
Loro.obtNombre()+ " " +Loro.obtVersion()+ " (Build " +Loro.obtBuild()+ ")\n"
			;
		}
		else if ( text.startsWith(".verobj") || text.startsWith(".verarr") )
		{
			StringTokenizer st = new StringTokenizer(text);
			try
			{
				st.nextToken(); // ignore comando
				int num = Integer.parseInt(st.nextToken());
				if ( text.startsWith(".verobj") )
				{
					loroii.ponNivelVerObjeto(num);
				}
				else
				{
					loroii.ponLongitudVerArreglo(num);
				}
				msg = "";
			}
			catch ( Exception ex )
			{
				msg = "Indique un valor numérico";
			}
		}
		else if ( text.equals(".gc") )
		{
			System.gc();
			msg = "free memory = " +Runtime.getRuntime().freeMemory()+ "  " +
			      "total memory = " +Runtime.getRuntime().totalMemory()
			;
		}
		else
		{
			msg = text+ ": comando no entendido.    .? para obtener ayuda.";
		}

		term.setPrefix(PREFIX_SPECIAL);
		pw.print(msg);
		term.setPrefix(null);
		pw.println();
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
	public ITextArea getTextArea()
	{
		return ta;
	}
	
	///////////////////////////////////////////////////////////////////////
	JComponent obtAreaTexto()
	{
		return ta;
		//return new JScrollPane(ta);
	}

	///////////////////////////////////////////////////////////////////////
	void procesar(String text)
	throws AnalisisException
	{
		if ( text.charAt(0) == '.' )
		{
			metaProcesar(text);
		}
		else
		{
			procesarLoro(text);
		}
	}

	///////////////////////////////////////////////////////////////////////
	protected void interpret(String text, String ask_enter)
	throws Exception
	{
		term.setPrefix(PROMPT);
		pw.print(text);
		term.setPrefix(null);

		if ( ask_enter != null )
			readLine(ask_enter);
		else
			pw.println();

		procesar(text);
	}
	
	/////////////////////////////////////////////////////////////////
	/** For JTermListener */
	public void waitingRead(boolean reading)
	{
		enableTraceableButtons(!reading);
		if ( reading )
		{
			frame.setTitle(title+ " <<ESPERANDO ENTRADA POR TECLADO>>");
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
		butTerminar.setEnabled(loroii.getExecute());
		// no queremos control de seguimiento mientras se lee:
		enableTraceableButtons(false);
		frame.setTitle(title+ " <<ESPERANDO ENTRADA POR TECLADO>>");
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
	
	///////////////////////////////////////////////////////////////////////
	protected void procesarLoro(String text)
	throws AnalisisException
	{
		boolean one_line_more = true;
		try
		{
			// en caso que el codigo escriba cosas:
			term.setPrefix(PREFIX_SPECIAL);
			//pw.println();

			// habilite el boton de terminacion si el modo es ejecucion:
			butTerminar.setEnabled(loroii.getExecute());
			enableTraceableButtons(loroii.getExecute());
			
			if ( loroii.getExecute() )
			{
				String res = loroii.ejecutar(text);
				Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
				butTerminar.setEnabled(false);
				enableTraceableButtons(false);
				
				if ( term.somethingWritten() )
				{
					// cancele efecto de PREFIX_SPECIAL:
					term.setPrefix(null);
					pw.println();
					one_line_more = false;
				}

				if ( res != null )
				{
					term.setPrefix(PREFIX_EXPR);
					pw.print(res);
					term.setPrefix(null);
					pw.println();
					one_line_more = false;
				}
			}
			else
			{
				loroii.compilar(text);
			}
		}
		finally
		{
			term.setPrefix(null);
			if ( one_line_more )
				pw.println();
			Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
			butTerminar.setEnabled(false);
			enableTraceableButtons(false);
		}
	}
	
	
	///////////////////////////////////////////////
	protected void close()
	{
		if ( butCerrar.isEnabled() )
		{
			if ( obspp != null )
				obspp.end();
			try
			{
				frame.setVisible(false);
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
}
