package util.editor;



import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.*;

import java.awt.Component;

import java.awt.Font;
import java.awt.Color;

//////////////////////////////////////////////////////////////////////
/**
 * Un editor de archivos texto.
 * Este editor opera sobre un objeto AreaTexto, notifica a un
 * observador EditorListener acerca de eventos de edicion y
 * contiene metodos para operaciones basicas de edicion como 
 * buscar, ir a una linea, guardar, guardar como, y similares.
 *
 * @author Carlos Rueda
 * @version 2002-02-05
 */
public class Editor
{
	/** El nombre del fuente en edición. */
	protected String nombreArchivo;

	/** El área de texto utilizado para la edición. */
	protected AreaTexto textoArchivo;

	/** Directorio base de los fuentes. */
	protected String rutaBase;

	/** ¿Está guardado el contenido actual? */
	protected boolean discoAlDia;

	/** ¿Ha puesto el usuario un nombre? */
	protected boolean conNombre;

	/**
	 * Es por cambio interno (no de usuario) la
	 * actualización del TextArea?
	 * Se vio en alguna JVM que el método setText()
	 * (o sea, cambio interno) provoca el evento. Este
	 * flag ayuda a verificar de qué se trata en cada
	 * caso. Vea textValueChanged(), nuevo(), abrir(), salir().
	 */
	protected boolean cambioInterno;

	/**
	 * Cadena para la opcion de busqueda.
	 */
	protected String cadenaBusqueda;



	/////////////////////////////////////////////////////////////////
	/**
	 * Interactúa con el usuario para abrir un archivo.
	 */
	public void abrir()
	{
		JFileChooser fd = new JFileChooser(rutaBase);

		fd.setFileFilter(fileFilter);
		fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fd.setMultiSelectionEnabled(false);
		fd.setDialogTitle("Abrir...");
		fd.setDialogType(JFileChooser.OPEN_DIALOG);

		int res = fd.showOpenDialog(frame);

		if ( res != JFileChooser.APPROVE_OPTION )
		{
			traerAlFrente();
			return;
		}

		String nomAr = fd.getSelectedFile().getName();
		if ( nomAr == null )
		{
			traerAlFrente();
			return;
		}

		String dir = fd.getCurrentDirectory().getPath();
		if ( dir == null )
			dir = rutaBase;

		if ( dir.endsWith(File.separator) )
			nomAr = dir + nomAr;
		else
			nomAr = dir + File.separator + nomAr;

		abrirArchivo(nomAr);
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Abre el archivo dado.
	 */
	public void abrirArchivo(String nomAr)
	{
		FileInputStream fis;
		try
		{   fis = new FileInputStream(nomAr);
		}
		catch ( FileNotFoundException fnfex )
		{
			JOptionPane.showMessageDialog(null,
				"Archivo '" +nomAr+ "' no encontrado", "Mensaje", JOptionPane.ERROR_MESSAGE
			);
			traerAlFrente();
			return;
		}

		if ( !discoAlDia )
		{
			Object[] options = {
				"Guardar antes de abrir",
				"Ignorar cambios y abrir",
				"Cancelar la operación"
			};

			int sel = javax.swing.JOptionPane.showOptionDialog(
				frame,
				"El archivo:\n" +
				"\n" +
				nombreArchivo+ "\n" +
				"\n" +
				"ha sido modificado.\n"+
				" \n"+
				"¿Qué decide hacer?",
				"Advertencia",
			   javax.swing.JOptionPane.DEFAULT_OPTION,
			   javax.swing.JOptionPane.WARNING_MESSAGE,
				null,
				options, options[0]
			);

			if ( sel == javax.swing.JOptionPane.CLOSED_OPTION
				|| sel == 2 )
			{
				traerAlFrente();
				return;
			}

			if ( sel == 0 )
			{
				guardar();
			}
		}

		BufferedInputStream bis = new BufferedInputStream(fis);
		StringBuffer sb = new StringBuffer("");

		try
		{   
			for ( int cc = bis.read(); cc != -1; cc = bis.read() )
			{
				if ( cc != 13 ) // 1999-04-22
				{
					sb.append("" + (char) cc);
				}
			}
			bis.close();
		}
		catch ( java.io.IOException ex )
		{
		}

		File file = new File(nomAr);
		String dir = file.getParent();

		actualizarTextArea(sb.toString());
		ponNombreArchivo(nomAr);
		ponRutaBase(dir);
		conNombre = true;
		discoAlDia = true;
		traerAlFrente();
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Pone el contenido del TextArea.
	 */
	protected void actualizarTextArea(String texto)
	{
		// Estamos en Linux? (faltaria probar este asunto en otros
		// linux y unix en donde este separador es '/'):
		boolean en_linux = File.separatorChar == '/';

		// Si estamos en Linux, lo siguiente previene que
		// textValueChanged(TextEvent e) no tome el cambio como
		// hecho por el usuario por edicion:
		cambioInterno = en_linux;

		textoArchivo.setText(texto);

		textoArchivo.setCaretPosition(0);  // carueda 2001-02-12
	}


	/////////////////////////////////////////////////////////////////
	/**
	 * Interactúa con el usuario para llevar el cursor hasta donde 
	 * esta una cadena.
	 */
	public void buscar()
	{
		String lin = javax.swing.JOptionPane.showInputDialog(
			frame,
			"Indique la cadena a buscar:",
			"Buscar",
			javax.swing.JOptionPane.QUESTION_MESSAGE
		);
		if ( lin != null )
		{
			int pos = textoArchivo.buscar(lin, textoArchivo.getCaretPosition());
			if ( pos >= 0 )
			{
				traerAlFrente();
				cadenaBusqueda = lin;
			}
			else
			{
				javax.swing.JOptionPane.showMessageDialog(
					null,
					"Cadena '" +lin+ "' no encontrada",
					"Mensaje",
					javax.swing.JOptionPane.INFORMATION_MESSAGE
				);
			}
		}
	}
	/////////////////////////////////////////////////////////////////
	/**
	 * Busca la siguiente aparicion de una cadena previamente buscada.
	 */
	public void buscarSiguiente()
	{
		if ( cadenaBusqueda == null )
		{
			buscar();
			return;
		}

		int pos = textoArchivo.buscar(cadenaBusqueda, textoArchivo.getCaretPosition());
		if ( pos >= 0 )
		{
			traerAlFrente();
		}
		else
		{
			javax.swing.JOptionPane.showMessageDialog(
				null,
				"Cadena '" +cadenaBusqueda+ "' no encontrada",
				"Mensaje",
				javax.swing.JOptionPane.INFORMATION_MESSAGE
			);
		}
	}
	/////////////////////////////////////////////////////////////////
	/**
	 * copy
	 */
	public void copy()
	{
		textoArchivo.copy();
	}










	/////////////////////////////////////////////////////////////////
	/**
	 * cut
	 */
	public void cut()
	{
		textoArchivo.cut();
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * obtiene el texto.  2001-01-29
	 */
	public String getText()
	{
		return textoArchivo.getText();
	}
	/////////////////////////////////////////////////////////////////
	/**
	 * Guarda en disco el contenido actual de la edición.
	 */
	public void guardar()
	{
		if ( ! conNombre )
		{
			guardarComo();
			return;
		}

		FileOutputStream fis;
		try
		{   fis = new FileOutputStream(nombreArchivo);
			BufferedOutputStream bis = new BufferedOutputStream(fis);
			String s = textoArchivo.getText();
			for ( int c = 0; c < s.length(); c++ )
				bis.write(s.charAt(c));
			bis.close();
			discoAlDia = true;
		}
		catch ( java.io.IOException fnfex )
		{
		}
	}
	/////////////////////////////////////////////////////////////////
	/**
	 * Interactúa con el usuario para guardar el contenido
	 * actual con un nombre de archivo especificado.
	 */
	public void guardarComo()
	{
		JFileChooser fd = new JFileChooser(rutaBase);
		//fd.setDirectory(rutaBase);

		File file = new File(nombreArchivo);

		fd.setSelectedFile(file);
		fd.setDialogTitle("Guardar como...");
		fd.setDialogType(JFileChooser.SAVE_DIALOG);

		int res = fd.showSaveDialog(frame);
		if ( res != JFileChooser.APPROVE_OPTION )
			return;

		String nomAr = fd.getSelectedFile().getName();
		if ( nomAr == null )
			return;

		if ( extension != null && nomAr.indexOf('.') < 0 )
			nomAr += extension;

		String dir = fd.getCurrentDirectory().getPath();
		if ( dir == null )
			dir = rutaBase;

		if ( dir.length() > 0 )
		{
			if ( dir.endsWith(File.separator) )
				nomAr = dir + nomAr;
			else
				nomAr = dir + File.separator + nomAr;
		}

		if ( new File(nomAr).exists() )
		{
			Object[] options = {
				"Sobreescribir",
				"Cancelar la operación"
			};

			int sel = javax.swing.JOptionPane.showOptionDialog(
				frame,
				"Ya existe el archivo:\n" +
				"\n" +
				nomAr+ "\n" +
				" \n"+
				"¿Qué decide hacer?",
				"Precaución",
			   javax.swing.JOptionPane.DEFAULT_OPTION,
			   javax.swing.JOptionPane.WARNING_MESSAGE,
				null,
				options, options[0]
			);

			if ( sel != 0 )
			{
				return;
			}
		}

		ponRutaBase(dir);
		ponNombreArchivo(nomAr);
		conNombre = true;
		guardar();
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Interactúa con el usuario para ir a una linea
	 */
	public void irALinea()
	{
		String lin = javax.swing.JOptionPane.showInputDialog(
			frame,
			"Indique el número de la línea:",
			"Ir a línea",
			javax.swing.JOptionPane.QUESTION_MESSAGE
		);
		if ( lin != null )
		{
			try
			{
				int num = Integer.parseInt(lin);
				textoArchivo.irALinea(num);
				traerAlFrente();
			}
			catch(NumberFormatException ex)
			{
			}
		}

	}








	/////////////////////////////////////////////////////////////////
	/**
	 * Pone la ventana de edición en blanco y como nombre
	 * de archivo "sinNombre" más la extensión.
	 */
	public void nuevo()
	{
		if ( !discoAlDia )
		{
			Object[] options = {
				"Guardar y abrir nuevo",
				"Ignorar y abrir nuevo",
				"Cancelar la operación"
			};

			int sel = javax.swing.JOptionPane.showOptionDialog(
				frame,
				"El archivo:\n" +
				"\n" +
				nombreArchivo+ "\n" +
				"\n" +
				"ha sido modificado.\n"+
				" \n"+
				"¿Qué decide hacer?",
				"Advertencia",
			   javax.swing.JOptionPane.DEFAULT_OPTION,
			   javax.swing.JOptionPane.WARNING_MESSAGE,
				null,
				options, options[0]
			);

			if ( sel == javax.swing.JOptionPane.CLOSED_OPTION
				|| sel == 2 )
			{
				return;
			}

			if ( sel == 0 )
			{
				guardar();
			}
		}

		actualizarTextArea("");
		ponNombreArchivo("sinNombre" + (extension == null ? "" : extension));
		traerAlFrente();

		conNombre = false;
		discoAlDia = true;  // aunque no se ha guardado nada
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Responde el nombre del archivo en edición.
	 * @return El nombre.
	 */
	public String obtNombreArchivo()
	{
		return nombreArchivo;
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * paste
	 */
	public void paste()
	{
		textoArchivo.paste();
	}
	/////////////////////////////////////////////////////////////////
	/**
	 * Pone nombre para el archivo en edición.
	 * @param nomArchivo El nombre a poner.
	 */
	public void ponNombreArchivo(String nomArchivo)
	{
		nombreArchivo = nomArchivo;
		edListener.ponNombreArchivo(nombreArchivo);
	}
	/////////////////////////////////////////////////////////////////
	public void ponRutaBase(String dir)
	{
		char sc = java.io.File.separatorChar;
		rutaBase = dir.replace('/', sc).replace('\\', sc);
		edListener.ponDirectorioFuentes(rutaBase);
	}

	/////////////////////////////////////////////
	public void setSelectionColor(Color color)
	{
		textoArchivo.setSelectionColor(color);
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Selecciona un trozo.
	 */
	public void select(int start, int end)
	{
		textoArchivo.select(start, end);
		String s = textoArchivo.getText();
		int len = textoArchivo.getText().length();
		if ( start >= len )
		{
			textoArchivo.setCaretPosition(len);
		}
	}
	/////////////////////////////////////////////////////////////////
	/**
	 * selectAll
	 */
	public void selectAll()
	{
		textoArchivo.selectAll();
	}
	/////////////////////////////////////////////////////////////////
	public void textoModificado()
	{
		if ( cambioInterno )
		{
			// Es cambio interno. Se actualiza para próximo evento.
			// actualizarTextArea(String texto) es quien pone este
			// indicador en true.
			cambioInterno = false;
		}
		else
		{
			// Evento provocado por edición del usuario.
			discoAlDia = false;
			edListener.estaModificado();
		}
	}
	/////////////////////////////////////////////
	/**
	 * Hace que el area de texto obtenga el foco
	 * de los eventos.
	 */
	public void traerAlFrente()
	{
		textoArchivo.traerAlFrente();
	}



	/** Observador de este editor. */
	protected EditorListener edListener;
	/** Extension de los archivos. */
	protected String extension;
	/** Mi contenedor principal utilizado para algunos dialogos. */
	protected JFrame frame;


	////////////////////////////////////////////////////////
	/**
	 * Una implementacion muy basica (seguramente incompleta)
	 * de AreaTexto basada en java.awt.TextArea.
	 * Esta clase es obsoleta: se mantiene para posibles revisiones
	 * de la interface general AreaTexto.
	 *
	 * @author Carlos Rueda
	 */
	static class AreaTextoBasica extends AreaTexto
	{
		/** El area de texto propiamente. */
		java.awt.TextArea ta;
		
		/////////////////////////////////////////////
		/**
		 * Crea un area de texto basica.
		 */
		public AreaTextoBasica(Editor se) 
		{
			super(se);
			ta = new java.awt.TextArea(200, 200);
			ta.setFont(new java.awt.Font("monospaced", java.awt.Font.PLAIN, 14));

			// para avisarle al editor de cambios en el contenido:
			ta.addTextListener(new TextListener()
			{
				/////////////////////////////////////////
				public void textValueChanged(TextEvent e)
				{
					obtEditor().textoModificado();
				}
			});
		}

		/////////////////////////////////////////////
		public Component comoComponent()
		{
			return ta;
		}
		/////////////////////////////////////////////
		public void copy()
		{
			// IGNORADO
		}
		/////////////////////////////////////////////
		public void cut()
		{
			// IGNORADO
		}
		/////////////////////////////////////////////
		public int getCaretPosition()
		{
			return ta.getCaretPosition();
		}
		/////////////////////////////////////////////
		public void setFont(Font font)
		{
			ta.setFont(font);
		}
		/////////////////////////////////////////////
		public Font getFont()
		{
			return ta.getFont();
		}
		/////////////////////////////////////////////
		public String getText() 
		{
			return ta.getText();
		}
		/////////////////////////////////////////////
		public void irALinea(int num)
		{
			// IGNORADO
		}
		/////////////////////////////////////////////
		public void paste()
		{
			// IGNORADO
		}
		
		/////////////////////////////////////////////
		public void setSelectionColor(Color color)
		{
			// IGNORADO
		}
		
		/////////////////////////////////////////////
		public void select(int start, int end)
		{
			ta.select(start, end);
		}
		/////////////////////////////////////////////
		public void selectAll()
		{
			ta.selectAll();
		}
		/////////////////////////////////////////////
		public void setCaretPosition(int pos)
		{
			ta.setCaretPosition(pos);
		}
		/////////////////////////////////////////////
		public void setText(String t)
		{
			ta.setText(t);
		}
		/////////////////////////////////////////////
		public void traerAlFrente()
		{
			ta.requestFocus();
		}

		////////////////////////////////////////////////////////////////
		public void setEditable(boolean editable)
		{
			ta.setEditable(editable);
		}
	}



	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el componente visual de este editor.
	 */
	public Component comoComponent()
	{
		return textoArchivo.comoComponent();
	}	

	/** Descripcion de los archivos. */
	protected String descripcion;
	/** Para dialogo de abrir archivo. */
	protected FileFilter fileFilter;

	/////////////////////////////////////////////////////////////////
	/**
	 * Crea un editor.
	 * Como ruta de base se toma System.getProperty("user.dir").
	 *
	 * @param frame Para dialogos.
	 * @param areaTexto Area de trabajo para edicion. Si es null,
	 *                  se provee una implementacion muy elemental
	 *                  (seguramente incompleta) basada en AWT.
	 * @param edListener Observador de este editor.
	 * @param descripcion Descripcion de los archivos a editar.
	 * @param extension Extension de los archivos a editar.
	 *                  Normalmente inicia con punto.
	 *                  null para indicar que es indiferente.
	 *
	 * @throws NullPointerException si edListener == null.
	 */
	public Editor(
		JFrame frame, 
		AreaTexto areaTexto, 
		EditorListener edListener,
		String descripcion_,
		String extension_
	)
	{
		this.frame = frame;
		this.edListener = edListener;
		if ( edListener == null ) 
		{
			throw new NullPointerException("EditorListener nulo");
		}
		this.descripcion = descripcion_;
		this.extension = extension_;

		fileFilter = new FileFilter()
		{
			public boolean accept(File f)
			{
				return extension == null 
				||     f.isDirectory() 
				||     f.getName().endsWith(".loro");
			}

			public String getDescription()
			{
				return descripcion;
			}
		};
		
		if ( areaTexto == null )
		{
			areaTexto = new AreaTextoBasica(this);
		}
		
		this.textoArchivo = areaTexto;

		discoAlDia = true;

		rutaBase = System.getProperty("user.dir");

		cambioInterno = false;

		cadenaBusqueda = null;

	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el area de texto manipulada.
	 *
	 * @return El area de texto manipulada.
	 */
	public AreaTexto obtAreaTexto()
	{
		return textoArchivo;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Incrementa o decrementa el tamano del font actual
	 * de esta area de texto.
	 */
	public void changeFontSize(int inc)
	{
		textoArchivo.changeFontSize(inc);
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Confirma con el usuario un intento de salir del editor en caso 
	 * que la edición actual presente cambios sin guardar.
	 *
	 * @return true Si se puede proceder con la salida.
	 */
	public boolean confirmarSalir()
	{
		if ( !discoAlDia )
		{
			Object[] options = {
				"Guardar y salir",
				"Ignorar cambios y salir",
				"Cancelar la salida"
			};

			int sel = javax.swing.JOptionPane.showOptionDialog(
				frame,
				"El archivo:\n" +
				"\n" +
				nombreArchivo+ "\n" +
				"\n" +
				"ha sido modificado.\n"+
				" \n"+
				"¿Qué decide hacer?",
				"Advertencia",
				javax.swing.JOptionPane.DEFAULT_OPTION,
				javax.swing.JOptionPane.WARNING_MESSAGE,
				null,
				options, options[0]
			);

			if ( sel == javax.swing.JOptionPane.CLOSED_OPTION
			||   sel == 2 )
			{
				return false;
			}

			if ( sel == 0 )
			{
				guardar();
			}
		}

		return true;
	}

	////////////////////////////////////////////////////////////////
	public void setEditable(boolean editable)
	{
		textoArchivo.setEditable(editable);
	}
	/////////////////////////////////////////////
	public void setCaretPosition(int pos)
	{
		textoArchivo.setCaretPosition(pos);
	}
}