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
import java.net.URL;


/////////////////////////////////////////////////////////////////////
/**
 * Interprete para acciones interactivas.
 * PENDIENTE: factorizar con InterpreterWindow.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class Interprete
implements ActionListener
{
	static final String PROMPT         =  " $ ";
	static final String PREFIX_EXPR    =  "=  ";
	static final String PREFIX_INVALID =  "!  ";
	static final String PREFIX_SPECIAL =  "   ";

	static private String version =	
"Loro - Sistema Didáctico de Programación\n"+
Info.obtNombre()+ " " +Info.obtVersion()+ " (Build " +Info.obtBuild()+ ")\n" +
Loro.obtNombre()+ " " +Loro.obtVersion()+ " (Build " +Loro.obtBuild()+ ")\n"
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
		{
			throw new IllegalStateException(
				"Second call to _createInstance()!"
			);
		}

		instance = new Interprete();
		frame = new JFrame("Intérprete Interactivo de Loro");
		URL url = ClassLoader.getSystemClassLoader().getResource("img/icon.jpg");
		if ( url != null ) 
			frame.setIconImage(new ImageIcon(url).getImage());
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent _)
			{
				frame.setVisible(false);
			}
		});

		java.awt.Container content_pane = frame.getContentPane();

		content_pane.add(instance.obtAreaTexto());

		javax.swing.JPanel pan = new javax.swing.JPanel(
			new java.awt.FlowLayout(java.awt.FlowLayout.LEFT)
		);
		javax.swing.JButton but;
		butCerrar = but = new javax.swing.JButton("Cerrar");
		butCerrar.setMnemonic(KeyEvent.VK_C);
		butCerrar.setToolTipText("Cierra esta ventana");
		but.addActionListener(instance);
		pan.add(but);
		pan.add(new javax.swing.JLabel("        "));
		butTerminar = but = new javax.swing.JButton("Terminar ejecución");
		butTerminar.setMnemonic(KeyEvent.VK_T);
		butTerminar.setToolTipText("Termina abruptamente la ejecución en curso");
		but.addActionListener(instance);
		//butTerminar.setEnabled(false);
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

		//instance.start();
		new Thread(new Runnable()
		{
			public void run()
			{
				instance.ii.run();
			}
		}).start();
	}

	

	//
	// Instance:
	//

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

//{{{
		ii = loroii.getInteractiveInterpreter();
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
			public void exception(String msg)
			{
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
//}}}

		
		term.setPrefix(PREFIX_SPECIAL);
		pw.println(
			loroedi.Info.obtTituloII() + "\n" +
			"Escribe .? para obtener una ayuda"
		);
	}


	///////////////////////////////////////////////////////////////////////
	static String obtModo(boolean execute)
	{
		return execute ? "Interpretación completa con ejecución"
					   : "Interpretación sin ejecucion (sólo chequeo)"
		;
	}

	///////////////////////////////////////////////////////////////////////
	public void run()
	{
		while ( true )
		{
			String msg = null;

			try
			{
				term.setPrefix(PROMPT);
				String text = br.readLine();
				if ( text == null )
					break;
				text = text.trim();
				if ( text.length() == 0 )
					continue;

				procesar(text);
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
				/** ignore:    2002-02-12
					Esta InterruptedIOException viene por lectura
			 		de mi propio run(); el siguiente mensaje salia mal
		 			en la ventana (mal en cuanto a los prefijos del jterm
		 			para manejo de coloreamiento).

		 			(Ver en DEVNOTES.txt 2002-02-12 comentario sobre 
		 			EjecutorTerminable)
		 			
				msg = "Ejecución interrumpida en operacion de entrada/salida.";
				//ex.printStackTrace();
				
			**/
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
				pw.println(msg);
			}
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
				if ( loroii.quitarID(id) )
				{
					msg = id+ " borrado";
				}
				else
				{
					msg = id+ " no declarado";
				}
				
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
		pw.println(msg);
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
		term.setPrefix(PREFIX_SPECIAL);
		if ( text.charAt(0) == '.' )
			metaProcesar(text);
		else
			procesarLoro(text);
	}

	///////////////////////////////////////////////////////////////////////
	void procesarLoro(String text)
	throws AnalisisException
	{
		try
		{
			// habilite el boton de terminacion si el modo es ejecucion:
			butTerminar.setEnabled(execute);
			
			if ( execute )
			{
				String res = loroii.ejecutar(text);
				Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
				butTerminar.setEnabled(false);
				
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
		}
	}
}