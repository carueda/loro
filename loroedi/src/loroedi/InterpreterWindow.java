package loroedi;

import util.jterm.JTerm;
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


/////////////////////////////////////////////////////////////////////
/**
 * Ventana para interpretación.
 *
 * @author Carlos Rueda
 * @version 2002-10-02
 */
public abstract class InterpreterWindow extends Thread
implements ActionListener
{
	protected static final String PROMPT         =  " $ ";
	protected static final String PREFIX_EXPR    =  "=  ";
	protected static final String PREFIX_INVALID =  "!  ";
	protected static final String PREFIX_SPECIAL =  "   ";

	protected JFrame frame = null;    // en donde se pone el interprete

	JButton butTerminar;
	JButton butCerrar;

	JEditTextArea ta;
	protected JTerm term;

	protected PrintWriter pw;
	protected BufferedReader br;

	boolean execute = true;
	IInterprete loroii;

	Thread readingThread = null;	


	/////////////////////////////////////////////////////////////////////
	/**
	 * Crea una ventana interprete.
	 *
	 * @param title For the window.
	 * @param hello Message to start with. Can be null.
	 * @param newSymTab See Loro.crearInterprete
	 */
	public InterpreterWindow(String title, String hello, boolean newSymTab)
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

		loroii = Loro.crearInterprete(br, pw, newSymTab);

		if ( hello != null )
		{
			term.setPrefix(PREFIX_SPECIAL);
			pw.print(hello);
			term.setPrefix(null);
			pw.println();
		}
		
		frame = new JFrame(title);
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
		butCerrar.setToolTipText("Cierra esta ventana");
		butCerrar.setEnabled(false);
		but.addActionListener(this);
		pan.add(but);
		pan.add(new javax.swing.JLabel("        "));
		butTerminar = but = new javax.swing.JButton("Terminar ejecución");
		butTerminar.setToolTipText("Termina abruptamente la ejecución en curso");
		but.addActionListener(this);
		butTerminar.setEnabled(false);
		pan.add(but);
		content_pane.add(pan, "South");

		Rectangle rect = Preferencias.obtRectangulo(Preferencias.II_RECT);
		frame.setLocation(rect.x, rect.y);
		frame.setSize(rect.width, rect.height);

		frame.addComponentListener(new ComponentAdapter()
		{
			void common()
			{
				Rectangle rect_ = new Rectangle(frame.getLocationOnScreen(), frame.getSize());
				Preferencias.ponRectangulo(Preferencias.II_RECT, rect_);
			}
			public void componentResized(ComponentEvent e){common();}
			public void componentMoved(ComponentEvent e){common();}
		});
	}

	///////////////////////////////////////////////////////////////////////
	protected abstract void body()
	throws Exception;
	
	
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
			GUI.updateSymbolTable();
		}
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
			if ( readingThread != null && readingThread != Thread.currentThread() )
			{
				readingThread.interrupt();
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
		else if ( text.equals(".modo") )
		{
			msg = obtModo(execute);
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
		else if ( text.equals(".cambiarmodo") )
		{
			execute = !execute;
			if ( execute )
			{
				// se acaba de pasar de "solo compilacion" a "ejecucion".
				// Hacer que todas las variables figuren como sin asignacion:
				loroii.ponAsignado(false);
			}
			msg = "Modo cambiado a: " +obtModo(execute);
		}
		else if ( text.equals(".limpiar") )
		{
			ta.setText("");
			msg = "";
		}
		else if ( text.equals(".version") )
		{
			msg =
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
	protected void interpret(String text)
	throws Exception
	{
		term.setPrefix(PROMPT);
		pw.print(text);
		term.setPrefix(null);
		pw.println();
		procesarLoro(text);
	}
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * @param s Initial part to include in the read string.
	 */
	protected String readLine(String s)
	throws Exception
	{
		term.setInitialStringToRead(s);
		butTerminar.setEnabled(execute);
		readingThread = Thread.currentThread();
		try
		{
			return br.readLine();
		}
		finally
		{
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
			butTerminar.setEnabled(execute);
			
			if ( execute )
			{
				String res = loroii.ejecutar(text);
				Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
				butTerminar.setEnabled(false);
				
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
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////
	static String obtModo(boolean execute)
	{
		return execute ? "Interpretación completa con ejecución"
					   : "Interpretación sin ejecucion (sólo chequeo)"
		;
	}

	///////////////////////////////////////////////
	protected void close()
	{
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
