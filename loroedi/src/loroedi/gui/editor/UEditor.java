package loroedi.gui.editor;

import loroedi.Info.Str;
import loroedi.gui.misc.*;
import loroedi.Preferencias;
import loroedi.Util;

import loroedi.jedit.AreaTexto_jedit;
import util.editor.*;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.net.URL;


/////////////////////////////////////////////////////////
/**
 * Editor para una unidad. Bueno, también sirve para editar
 * cualquier tipo de código.
 *
 * @author Carlos Rueda
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
	
	/** Un UEditorListener que no hace nada (null object pattern). */
	protected static UEditorListener nullEditorListener = new UEditorListener()
		{
				public void changed() {} 
				public void save() {} 
				public void closeWindow() {} 
				public void compile() {}
				public void execute(boolean trace) {} 
				public void reload() {} 
				public void viewDoc() {}
		}
	;

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Crea una ventana para edición de una unidad.
	 *
	 * @param title Title
	 * @param modifiable Is the contents modifiable?
	 * @param executable Include "execute" and "execute-trace" actions?
	 * @param doc Include "view-unit-doc-from-editor" action?
	 * @param include_toolbar
	 * @param preferenceKey For window rect on screen.
	 */
	public UEditor(String title, boolean modifiable, boolean executable, boolean doc,
		boolean include_toolbar, String preferenceKey
	)
	{
		this.title = title;
		this.saved = true;
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		URL url = getClass().getClassLoader().getResource("img/icon.jpg");
		if ( url != null ) 
			frame.setIconImage(new ImageIcon(url).getImage());

		// cree el area de texto (aun no tenemos el Editor):
		AreaTexto areaTexto = new AreaTexto_jedit(null);

		// ahora si, cree el Editor (el cual requiere el area de texto!):
		editor = new Editor(frame, areaTexto, this);
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
		Preferencias.Util.updateRect(frame, preferenceKey);

		createActions(modifiable, executable, doc);
		createMenuBarAndToolBar(include_toolbar);
		
		setEditorListener(nullEditorListener);
	}

	////////////////////////////////////////////////////////////////////////////
	private void createActions(boolean modifiable, boolean executable, boolean doc)
	{
		actions = new HashMap();
		Action a;
		actions.put("save",     a=new SaveAction());     a.setEnabled(modifiable);
		actions.put("compile",  a=new CompileAction());  a.setEnabled(modifiable);
		actions.put("execute",  a=new ExecuteAction());  a.setEnabled(executable);
		actions.put("execute-trace",  a=new ExecuteTraceAction());  a.setEnabled(executable);
		actions.put("view-unit-doc-from-editor",  
		                        a=new ViewDocFromEditorAction());  a.setEnabled(doc);
		actions.put("reload",   a=new ReloadAction());   a.setEnabled(modifiable);
		actions.put("close",    new CloseAction());

		actions.put("copy-selection",   new CopyAction());
		actions.put("cut-selection",    a=new CutAction());  a.setEnabled(modifiable);
		actions.put("paste",            a=new PasteAction());   a.setEnabled(modifiable);
		actions.put("select-all",       new SelectAllAction());
		actions.put("find-text",        new FindAction());
		actions.put("find-next",        new FindNextAction());
		actions.put("goto-line",        new GotoLineAction());
		actions.put("incr-fontsize",    new IncreaseFontAction());
		actions.put("decr-fontsize",    new DecreaseFontAction());
		
//		actions.put("print",  new PrintAction());

	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Pone el listener.
	 */
	public void setEditorListener(UEditorListener listener)
	{
		this.listener = listener != null ? listener : nullEditorListener;
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
	 * Obtiene el listener.
	 */
	public UEditorListener getEditorListener()
	{
		return listener;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Crea la barra de menus.
	 */
	private void createMenuBarAndToolBar(boolean include_toolbar)
	{
		JMenuBar mb = new JMenuBar();
		frame.setJMenuBar(mb);
		
		JMenu menu;
		
		// Unidad:
		menu = new JMenu(Str.get("gui.menu_unit"));
		menu.setMnemonic(KeyEvent.VK_U);
		mb.add(menu);
		menu.add((Action) actions.get("save"));
		menu.add((Action) actions.get("compile"));
		menu.add((Action) actions.get("execute"));
		menu.add((Action) actions.get("execute-trace"));
		menu.add((Action) actions.get("view-unit-doc-from-editor"));
		menu.addSeparator();
		menu.add((Action) actions.get("reload"));
		menu.addSeparator();
		menu.add((Action) actions.get("close"));
		
		// Edición:
		menu = new JMenu(Str.get("gui.menu_edit"));
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
		if ( include_toolbar )
		{
			JToolBar tb = new JToolBar();
			tb.setFloatable(false);
			frame.getContentPane().add(tb, "North");
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
	 * Gets the main frame's title.
	 */
	public String getTitle()
	{
		return title;
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
					double p = 0.15;   // 15%
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
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Selecciona todo.
	 */
	public void selectAll()
	{
		editor.obtAreaTexto().selectAll();
	}
	
	////////////////////////////////////////////////////////////////
	/** 
	 * Implementación para EditorListener.
	 * Notifica al UEditorListener en caso de que exista.
	 */
	public void estaModificado()
	{
		listener.changed();
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

	/////////////////////////////////////////////
	public void setSelectionColor(Color color)
	{
		editor.setSelectionColor(color);
	}

	/////////////////////////////////////////////////////////
	class CloseAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public CloseAction()
		{
			super();
			String[] strs = Str.get("gui.action_close_window").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super(null, Util.getIcon("img/Save24.gif"));
			String[] strs = Str.get("gui.action_saved_unit").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_reload_unit").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super(null, Util.getIcon("img/compile.gif"));
			String[] strs = Str.get("gui.action_compile_unit").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
	class ExecuteAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public ExecuteAction()
		{
			super();
			String[] strs = Str.get("gui.action_run").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F9, KeyEvent.CTRL_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			editor.traerAlFrente();
			listener.execute(false);
		}
	}

	/////////////////////////////////////////////////////////
	class ExecuteTraceAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public ExecuteTraceAction()
		{
			super();
			String[] strs = Str.get("gui.action_run_trace").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F9, KeyEvent.SHIFT_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			editor.traerAlFrente();
			listener.execute(true);
		}
	}

	/////////////////////////////////////////////////////////
	class ViewDocFromEditorAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public ViewDocFromEditorAction()
		{
			super(null, Util.getIcon("img/api.gif"));
			String[] strs = Str.get("gui.action_view_doc_unit").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super(null, Util.getIcon("img/Copy24.gif"));
			String[] strs = Str.get("gui.action_copy").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super(null, Util.getIcon("img/Cut24.gif"));
			String[] strs = Str.get("gui.action_cut").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super(null, Util.getIcon("img/Paste24.gif"));
			String[] strs = Str.get("gui.action_paste").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_select_all").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
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
			String[] strs = Str.get("gui.editor_find_string").split("\\|", 2);
			editor.buscar(strs[0], strs[1]);
		}

		/////////////////////////////////////////////////////////
		public FindAction()
		{
			super(null, Util.getIcon("img/Find24.gif"));
			String[] strs = Str.get("gui.action_find").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			String[] strs = Str.get("gui.editor_find_string").split("\\|", 2);
			editor.buscarSiguiente(strs[0], strs[1]);
		}

		/////////////////////////////////////////////////////////
		public FindNextAction()
		{
			super(null, Util.getIcon("img/FindAgain24.gif"));
			String[] strs = Str.get("gui.action_find_next").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			editor.irALinea(Str.get("gui.editor_goto_line"));
		}

		/////////////////////////////////////////////////////////
		public GotoLineAction()
		{
			super();
			String[] strs = Str.get("gui.action_goto_line").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_L));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_MASK));
		}
	}


	/////////////////////////////////////////////////////////
	/**
	 */
	final class DecreaseFontAction extends AbstractAction
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
		public DecreaseFontAction()
		{
			super();
			String[] strs = Str.get("gui.action_decrease_font").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	final class IncreaseFontAction extends AbstractAction
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
		public IncreaseFontAction()
		{
			super();
			String[] strs = Str.get("gui.action_increase_font").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
		}
	}
}

