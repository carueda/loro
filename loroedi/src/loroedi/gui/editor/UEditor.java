package loroedi.gui.editor;

import loroedi.gui.misc.*;
import loroedi.Preferencias;
import loroedi.Util;

import loroedi.jedit.AreaTexto_jedit;
import util.editor.*;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;


/////////////////////////////////////////////////////////
/**
 * Editor para una unidad.
 *
 * @author Carlos Rueda
 * @version 2002-08-04
 */
public class UEditor implements EditorListener
{
	protected Map actions;

	protected boolean saved;
	
	protected UEditorListener listener;
	protected String title;

	/** La ventana principal. */
	protected JFrame frame;

	/** El editor propiamente. */
	protected Editor editor;
	
	protected MessageArea msgArea;
	protected JToolBar tb;
	

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Crea una ventana para edición de una unidad.
	 */
	public UEditor(String title, boolean modifiable)
	{
		this.title = title;
		this.saved = true;
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		// cree el area de texto (aun no tenemos el Editor):
		AreaTexto areaTexto = new AreaTexto_jedit(null);

		// ahora si, cree el Editor (el cual requiere el area de texto!):
		editor = new Editor(
			frame, 
			areaTexto, 
			this,
			"Fuentes Loro (*.loro)",
			".loro"
		);
		areaTexto.ponEditor(editor);
		areaTexto.setEditable(modifiable);

		msgArea = new MessageArea();
		msgArea.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				if ( e.getKeyCode() == KeyEvent.VK_ENTER )
				{
					editor.traerAlFrente();
				}
			}
		});

		JScrollPane scrollp = new JScrollPane(
			msgArea,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);
		scrollp.setMinimumSize(new java.awt.Dimension(100, 60));
		scrollp.setMaximumSize(new java.awt.Dimension(10000, 100));

		JSplitPane split = getJSplitPane0();

		split.add(editor.comoComponent());
		split.add(scrollp);

		frame.getContentPane().add(split);
		frame.setSize(700, 500);

		createActions(modifiable);
		createMenuBarAndToolBar();
	}

	////////////////////////////////////////////////////////////////////////////
	private void createActions(boolean modifiable)
	{
		actions = new HashMap();
		Action a;
		actions.put("save",     a=new SaveAction());     a.setEnabled(modifiable);
		actions.put("compile",  a=new CompileAction());  a.setEnabled(modifiable);
		actions.put("view-unit-doc-from-editor",  new ViewDocFromEditorAction());
		actions.put("reload",   a=new ReloadAction());   a.setEnabled(modifiable);
		actions.put("close",    new CloseAction());

		actions.put("copy-selection",   new CopyAction());
		actions.put("cut-selection",    a=new CutAction());  a.setEnabled(modifiable);
		actions.put("paste",            a=new PasteAction());   a.setEnabled(modifiable);
		actions.put("select-all",       new SelectAllAction());
		actions.put("find-text",        new FindAction());
		actions.put("find-next",        new FindNextAction());
		actions.put("goto-line",        new GotoLineAction());
		actions.put("incr-fontsize",    new IncrementFontAction());
		actions.put("decr-fontsize",    new DecrementFontAction());
		
//		actions.put("print",  new PrintAction());

	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Pone el listener.
	 */
	public void setEditorListener(UEditorListener listener)
	{
		this.listener = listener;
		frame.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent _)
			{
				UEditor.this.listener.closeWindow();
			}
		});
	}
	
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Crea la barra de menus.
	 */
	private void createMenuBarAndToolBar()
	{
		JMenuBar mb = new JMenuBar();
		frame.setJMenuBar(mb);
		JToolBar tb = new JToolBar();
		tb.setFloatable(false);
		frame.getContentPane().add(tb, "North");
		
		JMenu menu;
		
		// Unidad:
		menu = new JMenu("Unidad");
		menu.setMnemonic(KeyEvent.VK_U);
		mb.add(menu);
		menu.add((Action) actions.get("save"));
		menu.add((Action) actions.get("compile"));
		menu.add((Action) actions.get("view-unit-doc-from-editor"));
		menu.addSeparator();
		menu.add((Action) actions.get("reload"));
		menu.addSeparator();
		menu.add((Action) actions.get("close"));
		
		// Edición:
		menu = new JMenu("Edición");
		menu.setMnemonic(KeyEvent.VK_E);
		mb.add(menu);
		menu.add((Action) actions.get("copy-selection"));
		menu.add((Action) actions.get("cut-selection"));
		menu.add((Action) actions.get("paste"));
		menu.add((Action) actions.get("select-all"));
		menu.addSeparator();
		menu.add((Action) actions.get("find-text"));
		menu.add((Action) actions.get("find-next"));
		menu.addSeparator();
		menu.add((Action) actions.get("goto-line"));
		menu.addSeparator();
		menu.add((Action) actions.get("incr-fontsize"));
		menu.add((Action) actions.get("decr-fontsize"));

		// Toolbar:		
		tb.add((Action) actions.get("save"));
		tb.addSeparator();
		tb.add((Action) actions.get("copy-selection"));
		tb.add((Action) actions.get("cut-selection"));
		tb.add((Action) actions.get("paste"));
		tb.addSeparator();
		tb.add((Action) actions.get("find-text"));
		tb.add((Action) actions.get("find-next"));
		tb.addSeparator();
		tb.add((Action) actions.get("compile"));
		tb.add((Action) actions.get("view-unit-doc-from-editor"));
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la ventana de este editor.
	 */
	public JFrame getFrame()
	{
		return frame;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el area de texto para mensajes.
	 */
	public MessageArea getMessageArea()
	{
		return msgArea;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Muestra la ventana
	 */
	public void display()
	{
		if ( !frame.isShowing() )
		{
			frame.setVisible(true);
		}
		editor.traerAlFrente();
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the main frame's title.
	 */
	public void setTitle(String title)
	{
		this.title = title;
		updateTitle();
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the saved status of the current text contents.
	 */
	public void setSaved(boolean saved)
	{
		this.saved = saved;
		updateTitle();
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Returns the saved status.
	 */
	public boolean isSaved()
	{
		return saved;
	}

	////////////////////////////////////////////////////////////////////////////
	private void updateTitle()
	{
		frame.setTitle(title+ (saved ? "" : " *"));
	}


	////////////////////////////////////////////////////////////////
	/**
	 */
	private JSplitPane getJSplitPane0()
	{
		final JSplitPane split0 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split0.setDividerSize(3);
		split0.setAutoscrolls(false);
		split0.setContinuousLayout(false);
		split0.setMinimumSize(new java.awt.Dimension(100, 100));
		split0.addComponentListener
		(	new ComponentAdapter()
			{
				public void componentResized(ComponentEvent e)
				{
					double h = split0.getSize().getHeight();
					double p = 0.1;
					if ( p * h < 60. )
						p = 60. / h;

					p = 1. -p;
					if ( 0. < p && p < 1.0 )
						split0.setDividerLocation(p);
				}
			}
		);
		return split0;
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * obtiene el texto en edicion.
	 */
	public String getText()
	{
		return editor.getText();
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * pone el texto en edicion.
	 */
	public void setText(String text)
	{
		editor.obtAreaTexto().setText(text);
		editor.setCaretPosition(0);
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Selecciona un trozo.
	 */
	public void select(int start, int end)
	{
		editor.obtAreaTexto().select(start, end);
	}
	
	////////////////////////////////////////////////////////////////
	/** 
	 * Implementación para EditorListener.
	 * Notifica al UEditorListener en caso de que exista.
	 * No hace nada si no hay un listener registrado.
	 */
	public void estaModificado()
	{
		if ( listener != null  )
		{
			listener.changed();
		}
	}

	////////////////////////////////////////////////////////////////////////
	/** Implementación para EditorListener. */
	public void ponDirectorioFuentes(String dir)
	{
		// ignore
	}
	/////////////////////////////////////////////////////////////////
	/** Implementación para EditorListener. */
	public void ponNombreArchivo(String nomArchivo)
	{
		// ignore
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Incrementa o decrementa el tamano del font actual
	 * del editor.
	 * Retorna el tamano resultante.
	 */
	public int changeFontSize(int inc)
	{
		editor.changeFontSize(inc);
		int font_size = editor.obtAreaTexto().getFont().getSize();
		Preferencias.ponPreferencia(Preferencias.EDITOR_FONT_SIZE, "" +font_size);
		return font_size;
	}

	////////////////////////////////////////////////////////////////
	public void setEditable(boolean editable)
	{
		editor.setEditable(editable);
	}
	/////////////////////////////////////////////
	public void setCaretPosition(int pos)
	{
		editor.setCaretPosition(pos);
	}


	/////////////////////////////////////////////////////////
	class CloseAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public CloseAction()
		{
			super("Cerrar");
			putValue(SHORT_DESCRIPTION, "Cierra esta ventana");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			listener.closeWindow();
		}
	}

	/////////////////////////////////////////////////////////
	class SaveAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public SaveAction()
		{
			super("Guardar", Util.getIcon("img/Save24.gif"));
			putValue(SHORT_DESCRIPTION, "Guarda esta unidad");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_G));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			editor.traerAlFrente();
			listener.save();
		}
	}

	/////////////////////////////////////////////////////////
	class ReloadAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public ReloadAction()
		{
			super("Recargar");
			putValue(SHORT_DESCRIPTION, "Recarga el último contenido guardado de esta unidad");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			editor.traerAlFrente();
			listener.reload();
		}
	}

	/////////////////////////////////////////////////////////
	class CompileAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public CompileAction()
		{
			super("Compilar", Util.getIcon("img/compile.gif"));
			putValue(SHORT_DESCRIPTION, "Compila esta unidad");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			editor.traerAlFrente();
			listener.compile();
		}
	}

	/////////////////////////////////////////////////////////
	class ViewDocFromEditorAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public ViewDocFromEditorAction()
		{
			super("Ver documentación", Util.getIcon("img/api.gif"));
			putValue(SHORT_DESCRIPTION, "Muestra la documentación de esta unidad");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F1, KeyEvent.CTRL_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			listener.viewDoc();
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class CopyAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			editor.traerAlFrente();
			editor.copy();
		}

		/////////////////////////////////////////////////////////
		public CopyAction()
		{
			super("Copiar", Util.getIcon("img/Copy24.gif"));
			putValue(SHORT_DESCRIPTION, "Copia el texto seleccionado");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class CutAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			editor.traerAlFrente();
			editor.cut();
		}

		/////////////////////////////////////////////////////////
		public CutAction()
		{
			super("Cortar", Util.getIcon("img/Cut24.gif"));
			putValue(SHORT_DESCRIPTION, "Corta el texto seleccionado");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_U));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK));
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class PasteAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			editor.traerAlFrente();
			editor.paste();
		}

		/////////////////////////////////////////////////////////
		public PasteAction()
		{
			super("Pegar", Util.getIcon("img/Paste24.gif"));
			putValue(SHORT_DESCRIPTION, 
				"Inserta el último texto copiado o cortado");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK));
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class SelectAllAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			editor.traerAlFrente();
			editor.selectAll();
		}

		/////////////////////////////////////////////////////////
		public SelectAllAction()
		{
			super("Seleccionar todo");
			putValue(SHORT_DESCRIPTION, "Selecciona todo el contenido en edición");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_T));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class FindAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			editor.traerAlFrente();
			editor.buscar();
		}

		/////////////////////////////////////////////////////////
		public FindAction()
		{
			super("Buscar...", Util.getIcon("img/Find24.gif"));
			putValue(SHORT_DESCRIPTION, "Busca una secuencia de caracteres en el texto");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_B));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK));
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class FindNextAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			editor.traerAlFrente();
			editor.buscarSiguiente();
		}

		/////////////////////////////////////////////////////////
		public FindNextAction()
		{
			super("Buscar siguiente", Util.getIcon("img/FindAgain24.gif"));
			putValue(SHORT_DESCRIPTION, "Busca de nuevo");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_G));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_MASK));
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class GotoLineAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			editor.traerAlFrente();
			editor.irALinea();
		}

		/////////////////////////////////////////////////////////
		public GotoLineAction()
		{
			super("Ir a línea...");
			putValue(SHORT_DESCRIPTION, "Permite ir a una línea en particular");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_MASK));
		}
	}


	/////////////////////////////////////////////////////////
	/**
	 */
	final class DecrementFontAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			editor.traerAlFrente();
			changeFontSize(-1);
		}

		/////////////////////////////////////////////////////////
		public DecrementFontAction()
		{
			super("Decrementar tamaño letra");
			putValue(SHORT_DESCRIPTION, "Decrementa el tamaño de letra en la ventana de edición");
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class IncrementFontAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			editor.traerAlFrente();
			changeFontSize(+1);
		}

		/////////////////////////////////////////////////////////
		public IncrementFontAction()
		{
			super("Incrementar tamaño letra");
			putValue(SHORT_DESCRIPTION, "Incrementa el tamaño de letra en la ventana de edición");
		}
	}


}
