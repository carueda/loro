package loroedi;

import loro.Loro;

import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Point;

import java.util.Properties;
import java.io.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/////////////////////////////////////////////////////////////////////
/**
 * Manejador de preferencias.
 * Archivo de propiedades: .loro/loroedi.pref
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class Preferencias
{
	/** The name of the last file edited. */
	public static final String RECENT = "loroedi.pref.recent";

	/** Rectangle for the main editor. */
	public static final String EDITOR_RECT = "loroedi.pref.editor.rect";

	/** Rectangle for the help window. */
	public static final String HELP_RECT = "loroedi.pref.help.rect";

	/** Rectangle for the DOC window. */
	public static final String DOC_RECT = "loroedi.pref.doc.rect";

	/** Base DOC directory. */
	public static final String DOC_DIR = "loroedi.pref.doc.dir";

	/** Base ORO directory. */
	public static final String ORO_DIR = "loroedi.pref.oro.dir";

	/** Rectangle for the Interactive Interpreter window. 
	 *  This is the "persistent" interpreter. */
	public static final String II_RECT = "loroedi.pref.ii.rect";

	/** Rectangle for interpreter windows. This is used when
	 *  a specific algorithm or demo is launched. */
	public static final String I_RECT = "loroedi.pref.i.rect";

	/** Rectangle for the demo editor. */
	public static final String DEMO_RECT = "loroedi.pref.demo.rect";

	/** Rectangle for the source trace editor. */
	public static final String SOURCE_TRACE_RECT = "loroedi.pref.srctrace.rect";

	/** Tamano del font en el editor. */
	public static final String EDITOR_FONT_SIZE = "loroedi.pref.editor.fontsize";

	////////////////////////////////////////////////////////////////////
	//       preferencias agregadas para nuevo entorno integrado
	
	/** The name of the last open project. */
	public static final String PRJ_RECENT = "loroedi.pref.prj.recent";

	/** Rectangle for the last open project window. */
	public static final String PRJ_RECT = "loroedi.pref.prj.rect";

	/** Rectangle for the top-level symbol table window. */
	public static final String SYMTAB_RECT = "loroedi.pref.symtab.rect";

	/** Rectangle for the trace symbol table window. */
	public static final String SYMTAB_TRACE_RECT = "loroedi.pref.symtab.trace.rect";

	/** Base projects directory. */
	public static final String PRS_DIR = "loroedi.pref.prs.dir";

	/** Rectangle for dialog window to choose a .lar project. */
	public static final String PRJ_CHOOSE_RECT = "loroedi.pref.prj.choose.rect";

	/** Rectangle for dialog window to choose a .lar project. */
	public static final String PRJ_EXTERN_LAST = "loroedi.pref.prj.extern.last";
	
	////////////////////////////////////////////////////////////////////
	
	
	
	/** Las propiedades. */
	private static Properties props = new Properties();

	/** Loro preferences full name. */
	private static String loro_pref_name;


	/** encabezado para el archivo de configuracion. */
	private static final String header =
		"LoroEDI user preferences"
	;

	//////////////////////////////////////////////////////////////////////////////
	static
	{
		// determine si estamos en windows para precisar el nombre del
		// archivo de preferencias
		boolean is_win =
			System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0;

		// nombre del archivo de configuracion a partir de user.home:
		String name = (is_win ? "_" : ".") + "loro/loroedi.pref";

		// nombre completo del archivo de preferencias:
		loro_pref_name = System.getProperty("user.home")+ "/" + name;
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Carga las preferencias. Si no hay archivo de preferencias (normalmente
	 * porque es la primera vez que se ejecuta este servicio), se crean una
	 * preferencias por defecto. El archivo sera escrito cuando se invoque store().
	 */
	public static void load()
	{
		try
		{
			// load properties:
			BufferedInputStream br =
				new BufferedInputStream(new FileInputStream(loro_pref_name))
			;

			props.load(br);
		}
		catch (IOException ex )
		{
			Loro.log("LoroEDI: " + 
				"Error reading preferences file:\n" +
				"  " +loro_pref_name+ "\n" +
				"Exception: " +ex.getMessage()+ "\n"
			);
		}

		// ORO_DIR:
		String oro_dir = props.getProperty(ORO_DIR);
		if ( oro_dir == null )
		{
			props.setProperty(ORO_DIR, Configuracion.getConfDirectory()+ "/oro");
		}

		// DOC_DIR:
		String doc_dir = props.getProperty(DOC_DIR);
		if ( doc_dir == null )
		{
			props.setProperty(DOC_DIR, Configuracion.getConfDirectory()+ "/doc");
		}

		// Para propiedades asociadas a rectangulos sobre pantalla:
		java.awt.Toolkit tk = java.awt.Toolkit.getDefaultToolkit();
		Dimension d = tk.getScreenSize();

		// EDITOR_RECT
		if ( props.getProperty(EDITOR_RECT) == null )
		{
			Dimension s = new Dimension(700, 500);
			Rectangle rect = new Rectangle(
					(d.width - s.width) / 2, (d.height - s.height) / 2,
					s.width, s.height
			);
			ponRectangulo(EDITOR_RECT, rect);
		}
		
		// EDITOR_FONT_SIZE
		if ( props.getProperty(EDITOR_FONT_SIZE) == null )
		{
			props.setProperty(EDITOR_FONT_SIZE, "14");
		}
		
		// HELP_RECT
		if ( props.getProperty(HELP_RECT) == null )
		{
			Dimension s = new Dimension(700, 500);
			Rectangle rect = new Rectangle(
					(d.width - s.width) / 2, (d.height - s.height) / 2 - 80,
					s.width, s.height
			);
			ponRectangulo(HELP_RECT, rect);
		}
		
		// DOC_RECT
		if ( props.getProperty(DOC_RECT) == null )
		{
			Dimension s = new Dimension(750, 500);
			Rectangle rect = new Rectangle(
					(d.width - s.width) / 2, (d.height - s.height) / 2,
					s.width, s.height
			);
			ponRectangulo(DOC_RECT, rect);
		}
		
		
		// PRJ_RECT
		if ( props.getProperty(PRJ_RECT) == null )
		{
			Dimension s = new Dimension(600, 400);
			Rectangle rect = new Rectangle(
					(d.width - s.width) / 2, (d.height - s.height) / 2,
					s.width, s.height
			);
			ponRectangulo(PRJ_RECT, rect);
		}
		
		// SYMTAB_RECT
		if ( props.getProperty(SYMTAB_RECT) == null )
		{
			Dimension s = new Dimension(300, 150);
			Rectangle rect = new Rectangle(
					(d.width - s.width) / 2, (d.height - s.height) / 2,
					s.width, s.height
			);
			ponRectangulo(SYMTAB_RECT, rect);
		}
		
		// SYMTAB_TRACE_RECT
		if ( props.getProperty(SYMTAB_TRACE_RECT) == null )
		{
			Dimension s = new Dimension(300, 150);
			Rectangle rect = new Rectangle(
					400, 0,
					s.width, s.height
			);
			ponRectangulo(SYMTAB_TRACE_RECT, rect);
		}
		
		// SOURCE_TRACE_RECT
		if ( props.getProperty(SOURCE_TRACE_RECT) == null )
		{
			Dimension s = new Dimension(600, 350);
			Rectangle rect = new Rectangle(
					0, 0,
					s.width, s.height
			);
			ponRectangulo(SOURCE_TRACE_RECT, rect);
		}
		
		// II_RECT
		if ( props.getProperty(II_RECT) == null )
		{
			Dimension s = new Dimension(600, 300);
			Rectangle rect = new Rectangle(
					350, 200,
					s.width, s.height
			);
			ponRectangulo(II_RECT, rect);
		}
		
		// I_RECT
		if ( props.getProperty(I_RECT) == null )
		{
			Dimension s = new Dimension(600, 300);
			Rectangle rect = new Rectangle(
					0, 350,
					s.width, s.height
			);
			ponRectangulo(I_RECT, rect);
		}
		
		// DEMO_RECT
		if ( props.getProperty(DEMO_RECT) == null )
		{
			Dimension s = new Dimension(500, 400);
			Rectangle rect = new Rectangle(
					10, 100,
					s.width, s.height
			);
			ponRectangulo(DEMO_RECT, rect);
		}

		// PRJ_CHOOSE_RECT
		if ( props.getProperty(PRJ_CHOOSE_RECT) == null )
		{
			Dimension s = new Dimension(500, 500);
			Rectangle rect = new Rectangle(
					(d.width - s.width) / 2, (d.height - s.height) / 2,
					s.width, s.height
			);
			ponRectangulo(PRJ_CHOOSE_RECT, rect);
		}

		// PRS_DIR:
		String prs_dir = props.getProperty(PRS_DIR);
		if ( prs_dir == null )
		{
			props.setProperty(PRS_DIR, Configuracion.getConfDirectory()+ "/prs");
		}

	}
	//////////////////////////////////////////////////////////////////////
	/**
	 * Almacena las preferencias actuales.
	 */
	public static void store()
	{
		try
		{
			BufferedOutputStream out =
				new BufferedOutputStream(new FileOutputStream(loro_pref_name))
			;

			props.store(out, header);
			out.close();
		}
		catch (IOException ex )
		{
			Loro.log("LoroEDI: " + 
				"Couldn't save preferences file:\n" +
				"  " +loro_pref_name+ "\n" +
				"Exception: " +ex.getMessage()
			);
		}
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene una preferencia.
	 * Si la propiedad no esta definida, se retorna "".
	 */
	public static String obtPreferencia(String key)
	{
		return (String) props.getProperty(key, "");
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene una preferencia que define un rectangulo.
	 */
	public static Rectangle obtRectangulo(String key)
	{
		String s = (String) props.getProperty(key);
		if ( s != null && s.length() > 0 )
		{
			Rectangle rect = new Rectangle();
			java.util.StringTokenizer st = new java.util.StringTokenizer(s, ", \t");
			try
			{
				rect.x = Integer.parseInt(st.nextToken());
				rect.y = Integer.parseInt(st.nextToken());
				rect.width = Integer.parseInt(st.nextToken());
				rect.height = Integer.parseInt(st.nextToken());
			}
			catch ( Exception ex )
			{
				Loro.log("LoroEDI: " +
					"getRectangle.getRect: key: " +key+ " : " +ex.getMessage()
				);
			}
			return rect;
		}
		else
		{
			throw new RuntimeException("Internal error: obtRectangulo(" +key+ ")");
		}
		
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Pone una preferencia.
	 */
	public static void ponPreferencia(String key, String value)
	{
		props.setProperty(key, value);
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Pone una preferencia que define un rectangulo.
	 */
	public static void ponRectangulo(String key, Rectangle rect)
	{
		props.setProperty(key, rect.x+ "," +rect.y+ "," +rect.width+ "," +rect.height);
	}

	//////////////////////////////////////////////////////////////////////////////
	/**
	 * Convenience methods to manage preferences.
	 */
	public static class Util
	{
		// non-instanceable
		private Util() {}
		
		//////////////////////////////////////////////////////////////////////////////
		/**
		 * Updates a frame rect preference according to location and size events.
		 */
		public static void updateRect(final Window frame, final String preferenceKey)
		{
			Rectangle rect = Preferencias.obtRectangulo(preferenceKey);
			frame.setLocation(rect.x, rect.y);
			frame.setSize(rect.width, rect.height);
			frame.addComponentListener(new ComponentAdapter()
			{
				void common()
				{
					Rectangle rect_ = new Rectangle(frame.getLocationOnScreen(), frame.getSize());
					Preferencias.ponRectangulo(preferenceKey, rect_);
				}
				public void componentResized(ComponentEvent e){common();}
				public void componentMoved(ComponentEvent e){common();}
			});
		}
	}
}