package loroedi.gui;

import loroedi.gui.project.*;
import loroedi.gui.project.model.*;
import loroedi.gui.project.unit.*;
import loroedi.gui.editor.*;
import loroedi.gui.misc.MessageArea;

import loroedi.LoroEDI;
import loroedi.HiloAlgoritmo;
import loroedi.Util;
import loroedi.Configuracion;
import loroedi.Preferencias;
import loroedi.Interprete;
import loroedi.laf.LookAndFeel;
import loroedi.help.BrowserPanel;
import loroedi.Splash;

import loro.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.util.*;
import java.util.List;
import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;

//////////////////////////////////////////////////
/**
 * Controlador general del entorno integrado.
 */
public class GUI
{
	public static final Font monospaced12_font = new Font("monospaced", Font.PLAIN, 12);
	public static final String UNTITLED_PROJECT = "(proyecto sin nombre)";
	public static final String tester_prefix = "test_";
	
	static Workspace workspace;

	static String prs_dir;
	static String doc_dir;	
	static JFrame docFrame;
	static BrowserPanel browser;
	static boolean docInProjectDirectory;
	
	static Project focusedProject;
	static int numOpen;
	static Actions actions;
	
	/** Mapping from names to ProjectFrame's. */
	static Map openFrames;
	
	/** Mapping from names to UEditor's de scripts de demostración. */
	static Map demoEditors;
	
	static SymbolTableWindow symbolTableWindow;
	
	static Splash splash;
	
	
	/** Atiende items en el menu "Ventana" */
	static ActionListener selectFromWindowMenu = new ActionListener() 
	{
		public void actionPerformed(ActionEvent ev)
		{
			String str = ev.getActionCommand();
			if ( str.startsWith("p:") )
			{
				String prjname = str.substring(2);
				ProjectFrame frame = (ProjectFrame) openFrames.get(prjname);
				if ( frame != null )
				{
					frame.project.display();
				}
			}
		}
	};
	
	
	/////////////////////////////////////////////////////////////////
	public static void init(String prs_dir_)
	{
		prs_dir = prs_dir_;
		
		actions = new Actions();
		openFrames = new HashMap();
		demoEditors = new HashMap();

		try
		{		
			ProjectFrame frame = new ProjectFrame();
			splash = Splash.showSplash(frame);
		
			Configuracion.load();
			Preferencias.load();
			LookAndFeel.setLookAndFeel();

			_initCore();
			
			Rectangle rect = Preferencias.obtRectangulo(Preferencias.PRJ_RECT);
			frame.init(rect);

			doc_dir = Preferencias.obtPreferencia(Preferencias.DOC_DIR);
			new File(doc_dir).mkdirs();
			
			// Documentar también bajo directorio del respectivo proyecto?
			// **** NOTA: Por ahora se hace así siempre ****
			docInProjectDirectory = true;
			
			splash.status("Iniciando espacio de trabajo...");
			workspace = Workspace.createInstance(prs_dir); 

			numOpen = 0;
			IProjectModel model = null;
			
			String recent = Preferencias.obtPreferencia(Preferencias.PRJ_RECENT);
			if ( recent.length() > 0
			&&   workspace.existsProjectModel(recent) )
			{
				splash.status("Cargando proyecto reciente...");
				model = workspace.getProjectModel(recent);
			}
			else
			{
				// abra un nuevo proyecto en blanco:
				model = workspace.getNewProjectModel();
				
				// lo siguiente pendiente mientras se define comportamiento.
//				_newOrOpenProject(frame);
//				if ( numOpen == 0 )
//				{
//					frame.dispose();
//					quit();
//				}
			}
			
			// Cree el interprete interactivo:
			Interprete.crearInterprete();

			_dispatch(frame, model);
			
			_firstTime();
			
			if ( _isNewAndEmpty(model) )
			{
				focusedProject.getMessageArea().print(
"Proyecto nuevo. Para proceder con su definición, elegir la opción 'Propiedades'\n"+
"en el menú 'Proyecto'.  Si se desea acceder a un proyecto ya existente, elegir\n"+
"opción 'Abrir...' (Ctrl-O) u opción 'Instalar...' (Ctrl-I)."
				);
			}
			else
			{
				focusedProject.getMessageArea().print(
"Proyecto '" +model.getInfo().getName()+ "' abierto."
				);
			}

			// cierre el splash window.			
			splash.status(null);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			JOptionPane.showOptionDialog(
				null,
				ex.getMessage(),
				"Error de inicio",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null,
				null,
				null
			);

			System.exit(1);
		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Inicializa el núcleo.
	 *
	 * @throws Exception Si se presenta algún problema de inicio.
	 */
	static void _initCore()
	throws Exception
	{
		if ( prs_dir == null )
		{
			prs_dir = Preferencias.obtPreferencia(Preferencias.PRS_DIR);
		}
		new File(prs_dir).mkdirs();

		// prepare valores de configuración para Loro:
		splash.status("Configurando núcleo...");
		String ext_dir = Configuracion.getProperty(Configuracion.DIR)+ "/lib/ext/";
		String paths_dir = prs_dir;
		Loro.configurar(ext_dir, paths_dir);

		// pendiente
		String oro_dir = Preferencias.obtPreferencia(Preferencias.ORO_DIR);
		ICompilador compilador = Loro.obtCompilador();
		compilador.ponDirectorioDestino(oro_dir);
		
		splash.status("Preparando entorno...");
		symbolTableWindow = new SymbolTableWindow(
			"top-level",
			Loro.getSymbolTable(),
			true,   // closeable
			true,   // delVar
			Preferencias.SYMTAB_RECT  // preferenceCode
		);
	}
	
	
	/////////////////////////////////////////////////////////////////
	/**
	 * En caso de ser la primera ejecución de la versión actual,
	 * realiza las siguientes operaciones:
	 *
	 * <ul>
	 *	<li> Verifica el núcleo de Loro.
	 *	<li> Verifica la configuración dada al núcleo.
	 *	<li> Genera documentación de unidades de apoyo
	 *		  en LoroPreferencias.DOC_DIR.
	 *	<li> Abre la ventana de ayuda general.
	 *	<li> Verifica configuración dada al núcleo de Loro.
	 * </ul>
	 *
	 * @throws Exception   Si hay algun problema.
	 */
	private static void _firstTime()
	throws Exception
	{
		if ( Configuracion.getVC() > 0 )
			return;
			
		// primera ejecución?

		////////////////////////////////////////
		// Verifique el núcleo Loro: 
		splash.status("Verificando núcleo...");
		Loro.verificarNucleo();

		IDocumentador documentador = Loro.obtDocumentador();

		////////////////////////////////////////			
		// genere documentacion del núcleo en doc_dir:
		documentador.documentarUnidadesDeApoyo(doc_dir);
		
		////////////////////////////////////////
		// genere documentacion de extensiones en doc_dir:
		// lo siguiente genera doc para todas las unidades,
		// incluyendo las del núcleo:
		splash.status("Actualizando documentación...");
		documentador.documentarExtensiones(doc_dir);
		
		// y lo siguiente genera doc para cada proyecto-extensión como tal
		// (no unidades):
		for ( Iterator it = workspace.getAvailableProjects().iterator(); it.hasNext(); )
		{
			 String prjname = (String) it.next();
			 if ( prjname.endsWith(".lar") )
			 {
				IProjectModel prjm = workspace.getProjectModel(prjname);
				_saveProjectDoc(prjm, null);
			 }
		}
		
		////////////////////////////////////////
		// Abra la ventana de ayuda:
		splash.status("Abriendo ayuda...");
		loroedi.help.HelpManager.displayHelp();
		
		//pendiente revisar esto:
		//loroControl.verificarConfiguracionNucleo(false);
	}

	
	
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Adiciona un directorio a la ruta de búsqueda.
	 */
	static void _addDirectoryToPath(String prjname)
	{
		String paths_dir = prs_dir;
		File dir = new File(paths_dir, prjname);
		Loro.addDirectoryToPath(dir);
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Adiciona un archivo extensión a la ruta de búsqueda.
	 */
	static IOroLoader _addExtensionToPath(File file)
	{
		return Loro.addExtensionToPath(file);
	}
	
	////////////////////////////////////////////////////////////////
//	private static void _newOrOpenProject(ProjectFrame frame)
//	{
//		Object[] options = {
//			"Crear nuevo proyecto",
//			"Abrir proyecto existente",
//			"Salir del sistema"
//		};
//
//		int sel = JOptionPane.showOptionDialog(
//			frame,
//			"Parece ser la primera vez que se ejecuta LoroEI\n"
//			+"\n"
//			+"¿Qué deseas hacer:",
//			"¿Qué hago?",
//			JOptionPane.DEFAULT_OPTION,
//			JOptionPane.WARNING_MESSAGE,
//			null,
//			options, options[0]
//		);
//
//		if ( sel == JOptionPane.CLOSED_OPTION || sel == 2 )
//		{
//			return;
//		}
//		if ( sel == 0 )
//		{
//			_newProject(frame);
//		}
//		else if ( sel == 1 )
//		{
//			_openProjectDialog(frame);
//		}
//	}
//


	/////////////////////////////////////////////////////////////////
	/**
	 * Despliega el Intérprete Interactivo general.
	 */
	public static void showII()
	{
		loroedi.Interprete.mostrarInterprete();
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Despliega la tabla de símbolos común.
	 */
	public static void showSymbolTable()
	{
		symbolTableWindow.show();
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Invalida declaraciones de la tabla de símbolos común.
	 * Significa borrar las declaraciones cuyo tipo es igual al tipo
	 * indicado. Este método es llamado desde Workspace._compileUnit.
	 */
	public static void invalidateSymbolTable(String typeString)
	{
		ISymbolTable symbTab = Loro.getSymbolTable();
		String[] ids = symbTab.getVariableNames();
		for ( int i = 0; i < ids.length; i++ )
		{
			if ( symbTab.getTypeString(ids[i]).equals(typeString) )
				symbTab.borrar(ids[i]);
		}
		updateSymbolTable();
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Actualiza la ventana de tabla de símbolos común.
	 */
	public static void updateSymbolTable()
	{
		symbolTableWindow.update();
	}
	
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Prepara la ventana para documentación.
	 */
	static void createBrowserPanel()
	{
		docFrame = new JFrame("Documentación");
		URL url = ClassLoader.getSystemClassLoader().getResource("img/icon.jpg");
		if ( url != null ) 
			docFrame.setIconImage(new ImageIcon(url).getImage());
		docFrame.setSize(500, 450);
		docFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		docFrame.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent _)
			{
				docFrame.setVisible(false);
			}
		});
		Rectangle rect = Preferencias.obtRectangulo(Preferencias.DOC_RECT);
		docFrame.setLocation(rect.x, rect.y);
		docFrame.setSize(rect.width, rect.height);
		docFrame.addComponentListener(new ComponentAdapter()
		{
			void common()
			{
				Rectangle rect_ = new Rectangle(docFrame.getLocationOnScreen(), docFrame.getSize());
				Preferencias.ponRectangulo(Preferencias.DOC_RECT, rect_);
			}
			public void componentResized(ComponentEvent e){common();}
			public void componentMoved(ComponentEvent e){common();}
		});

		try
		{
			browser  = new BrowserPanel(null, null);
			docFrame.getContentPane().add(browser);
		}
		catch(Exception ex)
		{
			JTextArea ta = new JTextArea();
			ta.setText("Error creando área de documentación:\n" +ex.getMessage());
			docFrame.getContentPane().add(ta);
		}
	}


	/////////////////////////////////////////////////////////////////
	/**
	 * Al nombre se hace remplazo de "::" por File.separator y se le
	 * agrega ".html", y se toma relativo a doc_dir.
	 */
	static URL getDocumentURL(String docname)
	throws Exception
	{
		docname = loro.util.Util.replace(docname, "::", File.separator);
		File file = new File(doc_dir, docname + ".html");
		return file.toURL();
	}


	
	/////////////////////////////////////////////////////////////////
	/**
	 * Despliega el documento indicado. 
	 */
	static void showDocumentation(String docname)
	{
		if ( browser == null )
			createBrowserPanel();

		try
		{
			URL url = getDocumentURL(docname);
			URL prev = browser.getPage();
			if ( prev != null && prev.equals(url) )
			{
				browser.refresh();
			}
			else
			{
				browser.setPage(url);
			}
			docFrame.setVisible(true);
		}
		catch(Exception ex)
		{
			browser.setTextHTML("Error:<br>\n" +
				"<pre>\n"+
				ex.getMessage()+ "\n"+
				"<pre>\n"
			);
		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Despliega la documentación del proyecto enfocado.
	 */
	public static void showProjectDocumentation()
	{
		// actualice por posibles adiciones/borrados de elementos:
		_saveProjectDoc(focusedProject.getModel(), null);
		String prjname = focusedProject.getModel().getInfo().getName();
		showDocumentation(prjname+ ".prj");
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Despliega la documentación de una unidad.
	 */
	public static void showUnitDocumentation(IProjectUnit unit)
	{
		String unitname = unit.getQualifiedName();
		showDocumentation(unitname+ "." +unit.getCode());
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Actualiza ventana de documentación si la unidad dada coincide
	 * con el elemento desplegado.
	 */
	public static void refreshDocIfNecessary(IProjectUnit unit)
	{
		if ( browser != null )
		{
			try
			{
				String unitname = unit.getQualifiedName();
				URL url = getDocumentURL(unitname+ "." +unit.getCode());
				URL prev = browser.getPage();
				if ( prev != null && prev.equals(url) )
				{
					browser.refresh();
				}
			}
			catch(Exception ex)
			{
				// ignore
			}
		}
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Edita/muestra las propiedades del proyecto enfocado.
	 */
	public static void editProperties()
	{
		IProjectModel model = focusedProject.getModel();
		IProjectModel.IControlInfo ci = model.getControlInfo();
		if ( _editProperties(model, ci.isModifiable(), ci.isValid(), focusedProject.getFrame()) )
		{
			try
			{
				workspace.saveProjectModelProperties(model);
				File doc2_dir = null;
				if ( docInProjectDirectory )
				{
					doc2_dir = new File(prs_dir +File.separator+ model.getInfo().getName());
				}
				_saveProjectDoc(model, doc2_dir);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				JOptionPane.showOptionDialog(
					focusedProject.getFrame(),
					ex.getMessage(),
					"Error al tratar de guardar propiedades del proyecto",
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE,
					null,
					null,
					null
				);
			}
			focusedProject.updateTitle();
		}
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para editar/mostrar las propiedades de un proyecto.
	 *
	 * @param model El modelo
	 * @param editable true para permitir la edición de las propiedades.
	 *                 No se mira el modelo.
	 * @param frame Ventana de referencia para el diálogo.
	 *
	 * @return true ssi editable == true y la forma fue aceptada y OK. 
	 */
	private static boolean _editProperties(
		IProjectModel model,
		final boolean editable,
		final boolean valid,
		JFrame frame
	)
	{
        final JTextField f_name = new JTextField(50);
		f_name.setEditable(editable);
        final JTextField f_title = new JTextField(50);
		f_title.setEditable(editable);
        final JTextField f_authors = new JTextField(50);
		f_authors.setEditable(editable);
        final JTextField f_version = new JTextField(50);
		f_version.setEditable(editable);
        final JTextArea f_description = new JTextArea(8, 50);
		f_description.setEditable(editable);
		f_description.setFont(monospaced12_font);
		JCheckBox t_htmlDescription = new JCheckBox("Contiene HTML");
		t_htmlDescription.setToolTipText("Indica que la descripción contiene marcadores HTML");
		final JLabel status = new JLabel();
		
		if ( !valid )
		{
			status.setForeground(Color.red);
			status.setText(
"<html>\n"+
"<b>Proyecto incompatible</b><br>\n"+
"Este proyecto corresponde a una extensión cuyos elementos compilados resultan incompatibles<br>\n"+
"con la actual versión de Loro.<br>\n"+ 
"Para obtener una versión actualizada de esta extensión, favor consultar con su proveedor."+
"</html>\n"
			);
		}
		else
		{
			status.setFont(status.getFont().deriveFont(Font.ITALIC));
			if ( !editable )
				status.setText("Las propiedades de este proyecto no se pueden modificar");
		}
		
		final String curr_name = model.getInfo().getName();
		f_name.setText(curr_name);
		if ( curr_name.length() > 0 )
		{
			// ya tiene nombre de antes, así que no debe ser editable
			// (para cambiar este nombre se debe usar alguna opción
			// específica: "Grabar como").
			f_name.setEditable(false);
		}
		
		f_title.setText(model.getInfo().getTitle());
		f_authors.setText(model.getInfo().getAuthors());
		f_version.setText(model.getInfo().getVersion());
		f_description.setText(model.getInfo().getDescription());
		t_htmlDescription.setSelected(
			"html".equalsIgnoreCase(model.getInfo().getDescriptionFormat())
		);
		f_description.setCaretPosition(0);

		// borders:		
		f_name.setBorder(createTitledBorder("Código del proyecto"));
		f_title.setBorder(createTitledBorder("Título"));
		f_authors.setBorder(createTitledBorder("Autor(es)"));
		f_version.setBorder(createTitledBorder("Versión/Fecha"));
		JScrollPane sp = new JScrollPane(f_description);
		Box descr_panel = new Box(BoxLayout.Y_AXIS);
		t_htmlDescription.setAlignmentX(0f);
		descr_panel.add(t_htmlDescription, "North");
		sp.setAlignmentX(0f);
		descr_panel.add(sp);
		
		descr_panel.setBorder(createTitledBorder("Descripción"));
		
        Object[] array = {
			f_name,
			f_title,
			f_authors,
			f_version,
			descr_panel,
			status
		};
		
		String diag_title;
		if ( curr_name.trim().length() == 0 )
		{
			diag_title = "Propiedades para el nuevo proyecto";
		}
		else
		{
			diag_title = "Propiedades del proyecto: \"" +curr_name+ "\"";
		}
        final ProjectDialog form = new ProjectDialog(frame, diag_title, array)
		{
			public boolean dataOk()
			{
				if ( !editable )
				{
					return true;
				}
				String msg = null;
				if ( f_name.isEditable() )
				{
					String prj_name = f_name.getText();
					if ( prj_name.indexOf(' ') >= 0 )
					{
						msg = "No se permite utilizar espacios para el código del proyecto";
						//f_name.setBorder(ProjectDialog.redBorder);
					}
					else if ( prj_name.length() == 0 )
					{
						msg = "Falta un código de identificación para el proyecto";
					}
					else if ( !curr_name.equalsIgnoreCase(prj_name)
					&& workspace.existsProjectModel(prj_name) )
					{
						msg = "Ya existe un proyecto con este código";
					}
				}
				if ( msg != null )
				{
					// nada
				}
				else if ( f_title.getText().trim().length() == 0 )
				{
					msg = "Falta indicar un título para el proyecto";
				}
				else if ( f_description.getText().trim().length() == 0 )
				{
					msg = "Falta dar una descripción al proyecto";
				}
				
				if ( msg == null )
				{
					status.setForeground(Color.gray);
					status.setText("OK");
				}
				else
				{
					status.setForeground(Color.red);
					status.setText(msg);
				}
				return msg == null;
			}
		};
		
		form.activate();
        form.pack();
		form.setLocationRelativeTo(frame);
		form.setVisible(true);
		if ( editable && form.accepted() )
		{
			String prj_name = f_name.getText().trim();
			String title = f_title.getText().trim();
			String authors = f_authors.getText().trim();
			String version = f_version.getText().trim();
			String description = f_description.getText().trim();
			boolean htmlDescription = t_htmlDescription.isSelected();

			model.getInfo().setName(prj_name);			
			model.getInfo().setTitle(title);			
			model.getInfo().setAuthors(authors);			
			model.getInfo().setVersion(version);			
			model.getInfo().setDescription(description);			
			model.getInfo().setDescriptionFormat(
				htmlDescription ? "html" : "plain"
			);			
			return true;
		}
		return false;
	}


	/////////////////////////////////////////////////////////////////
	/**
	 * Crea un nuevo proyecto.
	 *
	 * @return true si la creación procede de acuerdo a requerimientos
	 *         mínimos de definición de un proyecto.
	 */
	public static boolean newProject()
	{
		IProjectModel model = _newProject(focusedProject.getFrame());
		if ( model == null )
		{
			return false;
		}
		
		if ( _isNewAndEmpty(focusedProject.getModel()) )
		{
			// utilice la misma ventana:
			focusedProject.setModel(model);
			openFrames.remove("");
			openFrames.put(model.getInfo().getName(), focusedProject.getFrame());
			_updateWindowMenu();
		}
		else
		{
			Rectangle rect;
			if ( focusedProject != null )
			{
				JFrame base_frame = focusedProject.getFrame();
				rect = new Rectangle(base_frame.getLocation(), base_frame.getSize());
			}
			else
			{
				rect = Preferencias.obtRectangulo(Preferencias.PRJ_RECT);
			}
			rect.translate(10, 10);
			ProjectFrame frame = new ProjectFrame(rect);
			_dispatch(frame, model);
		}
		return true;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para creación de un nuevo proyecto.
	 *
	 * @return el nuevo modelo inicializado de tal manera que puede ser guardado.
	 *         null si lo anterior no se da.
	 */
	private static IProjectModel _newProject(JFrame frame)
	{
		IProjectModel model = workspace.getNewProjectModel();
		IProjectModel.IControlInfo ci = model.getControlInfo();
		if ( _editProperties(model, ci.isModifiable(), ci.isValid(), frame) )
		{
			try
			{
				workspace.saveProjectModel(model);
				File doc2_dir = null;
				if ( docInProjectDirectory )
				{
					doc2_dir = new File(prs_dir +File.separator+ model.getInfo().getName());
				}
				_saveProjectDoc(model, doc2_dir);
				_addDirectoryToPath(model.getInfo().getName());
				return model;
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				JOptionPane.showOptionDialog(
					frame,
					ex.getMessage(),
					"Error al tratar de guardar proyecto",
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE,
					null,
					null,
					null
				);
			}
		}
		return null;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Cierra el proyecto enfocado. 
	 */
	public static void closeProject()
	{
		////PENDING: cerrar lógicamente el proyecto...
		//focusedProject.close()  
		
		openFrames.remove(focusedProject.getModel().getInfo().getName());
		
		if ( numOpen == 1 )
		{
			quit();
		}
		else
		{
			JFrame frame = focusedProject.getFrame();
			frame.dispose();
			numOpen--;
		}
		_updateWindowMenu();
	}
	
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la cadena HTML de documentacion de los paquetes de
	 * un proyecto.
	 */
	private static String _getPackagesDoc(IProjectModel prjm)
	{
		StringBuffer sb = new StringBuffer();
		
		for ( Iterator it = prjm.getPackages().iterator(); it.hasNext(); )
		{
			IPackageModel pkgm = (IPackageModel) it.next();
			
			String pkgname = pkgm.getName();
			if ( pkgname.length() == 0 )
				pkgname = "<i>an&oacute;nimo</i>";
			else
				pkgname = "<code><b>" +pkgname+ "</b></code>";
			
			sb.append("<tr bgcolor=\"#FFCCCC\">\n");
			sb.append("<td>\n");
			sb.append("paquete: " +pkgname+ "\n");
			sb.append("</td>\n");
			sb.append("</tr>\n");
			
			for ( Iterator itt = pkgm.getUnits().iterator(); itt.hasNext(); )
			{
				IProjectUnit unit = (IProjectUnit) itt.next();
				String qname = loro.util.Util.replace(
					unit.getQualifiedName(), "::", "/"
				);
				String code = unit.getCode();
				String href = qname+ "." +code+ ".html";

				sb.append("<tr>\n");
				sb.append("<td>\n");

				sb.append("&nbsp;&nbsp;<a href=\"" +href+ "\">" +
					"<code>" +unit.getStereotype()+ " " +unit.getName()+ "</code>" + 
					"</a>\n"
				);
				sb.append("</td>\n");
				sb.append("</tr>\n");
			}
		}
		return sb.toString();
	}
	
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Salvaguarda la documentación básica de un proyecto en doc_dir.
	 *
	 * @param doc2_dir Si no es null, también se genera en este directorio.
	 */
	private static void _saveProjectDoc(IProjectModel model, File doc2_dir)
	{
		IProjectModel.IInfo info = model.getInfo();
		String prjname = info.getName();
		String title = info.getTitle();
		String authors = info.getAuthors();
		String version = info.getVersion();
		String description = info.getDescription();
		boolean htmlDescription = "html".equalsIgnoreCase(info.getDescriptionFormat());
		String pkgDoc = _getPackagesDoc(model);

		if ( title == null )
			title = "(no hay)";
		if ( authors == null )
			authors = "(no hay)";
		if ( version == null )
			version = "(no hay)";
		if ( description == null )
			description = "(no hay)";
		
		// if description is not in HTML, then replace special symbols:
		if ( !htmlDescription )
		{
			description = 
				"<pre>\n" +
				loro.util.Util.formatHtml(description) +
				"</pre>\n";
		}
		
		// Replace ``@{...}'' tags in description:
		description = loro.util.Util.processInlineTags(description, null);
		
		try
		{
			String dir = Configuracion.getProperty(Configuracion.DIR);
			String templ_filename = dir+ "/doc/templprj.html";
			File file = new File(templ_filename);
			String content;
			if ( file.exists() )
			{
				content = Util.readFile(new File(templ_filename));
				String[] reps = {
					"{{prjname}}", prjname,
					"{{title}}", title,
					"{{authors}}", authors,
					"{{version}}", version,
					"{{description}}", description,
					"{{packages}}", pkgDoc,
				};
				for ( int i = 0; i < reps.length; i += 2 )
				{
					content = loro.util.Util.replace(content, reps[i], reps[i+1]);
				}
			}
			else // no debería suceder, pero...
			{
				content = "<html><head><title" +title+ "</title></head>" +
					"<body><h1>" +title+ "</h1>" +
					"<pre>" +description+ "</pre>" +
					"</body></html>"
				;
			}
			
			// guarde bajo doc_dir:
			Util.writeFile(new File(doc_dir, prjname+ ".prj.html"), content);
			
			if ( doc2_dir != null )
			{
				Util.writeFile(new File(doc2_dir, prjname+ ".prj.html"), content);
			}
			
			if ( browser != null ) // simplemente refresque.
				browser.refresh();
		}
		catch(Exception ex)
		{
			System.out.println(
				"Error al guardar documentación proyecto '" +prjname+ "'\n" +
				ex.getMessage()
			);
		}
		
		// provisionalmente aquí, para pruebas iniciales.
		_saveMainIndexForProjects();
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Actualiza el index.html principal bajo doc_dir con la lista de
	 * proyectos en el espacio de trabajo.
	 * PRIMERA VERSION.
	 */
	private static void _saveMainIndexForProjects()
	{
		StringBuffer sb = new StringBuffer(
			"<html>\n" +
			"<head>\n" +
			"<title>Proyectos Loro</title>\n" +
			"</head>\n" +
			"<body>\n"
		);
		
		sb.append("<b>Proyectos Loro</b>\n");
		
		sb.append("<table>\n");
		for ( Iterator it = workspace.getAvailableProjects().iterator(); it.hasNext(); )
		{
			String prjname = (String) it.next();
			IProjectModel prjm = workspace.getProjectModel(prjname);
			sb.append("<tr>\n");
			sb.append("<td bgcolor=\"#FFCCCC\">\n");
			sb.append("  <a href=\"" +prjname+ ".prj.html"+ "\">" +prjname+ "</a>\n");
			sb.append("</td>\n");
			sb.append("<td bgcolor=\"#FFEEEE\">\n");
			sb.append("  " +prjm.getInfo().getTitle()+ "\n");
			sb.append("</td>\n");
			sb.append("</tr>\n");
		}
		sb.append("</table>\n");
		try
		{
			// guarde bajo doc_dir:
			Util.writeFile(new File(doc_dir, "index.html"), sb.toString());
		}
		catch(Exception ex)
		{
			System.out.println(
				"Error al guardar index.html principal.\n" +
				ex.getMessage()
			);
		}
	}
			
	/////////////////////////////////////////////////////////////////
	/**
	 * Salvaguarda el proyecto enfocado. 
	 */
	public static void saveProject()
	{
		IProjectModel model = focusedProject.getModel(); 
		String name = model.getInfo().getName();
		if ( name.trim().length() == 0 )
		{
			IProjectModel.IControlInfo ci = model.getControlInfo();
			if ( ! _editProperties(model, ci.isModifiable(), ci.isValid(), focusedProject.getFrame()) )
			{
				return;
			}
		}
		
		try
		{
			workspace.saveProjectModel(model);
			File doc2_dir = null;
			if ( docInProjectDirectory )
			{
				doc2_dir = new File(prs_dir +File.separator+ model.getInfo().getName());
			}
			_saveProjectDoc(model, doc2_dir);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			JOptionPane.showOptionDialog(
				focusedProject.getFrame(),
				ex.getMessage(),
				"Error al tratar de guardar proyecto",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null,
				null,
				null
			);
		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Exporta el proyecto enfocado. 
	 */
	public static void exportProject()
	{
		IProjectModel model = focusedProject.getModel();
		
		String prjname = model.getInfo().getName();
		JFrame frame = focusedProject.getFrame();

        final JTextField f_name = new JTextField(30);
		f_name.setEditable(false);
        final JTextField f_title = new JTextField(30);
		f_title.setEditable(false);
        final JTextField f_directory = new JTextField(30);
        final JTextField f_extension = new JTextField(30);
		final JLabel status = new JLabel();
		status.setFont(status.getFont().deriveFont(Font.ITALIC));
		
		f_name.setText(model.getInfo().getName());
		f_title.setText(model.getInfo().getTitle());

		// borders:		
		f_name.setBorder(createTitledBorder("Código del proyecto"));
		f_title.setBorder(createTitledBorder("Título"));
		
		JPanel panel_target = new JPanel(new GridLayout(0, 1));
		panel_target.setBorder(createTitledBorder("Destino"));
		
		final JPanel panel_extension = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JRadioButton radio_extension = new JRadioButton("Proyecto");
		radio_extension.setMnemonic(KeyEvent.VK_P);
		radio_extension.setSelected(true);
		panel_extension.add(radio_extension);
		panel_extension.add(f_extension);
		JButton choose_ext = new JButton("...");
		panel_extension.add(choose_ext);
		
		panel_target.add(panel_extension);

		final JPanel panel_directory = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JRadioButton radio_directory = new JRadioButton("Directorio");
		radio_directory.setMnemonic(KeyEvent.VK_D);
		radio_directory.setSelected(false);
		panel_directory.add(radio_directory);
		panel_directory.add(f_directory);
		JButton choose_dir = new JButton("...");
		panel_directory.add(choose_dir);
		
		panel_target.add(panel_directory);
		
		ButtonGroup group = new ButtonGroup();
        group.add(radio_directory);
        group.add(radio_extension);
		
		JPanel panel_include = new JPanel(new GridLayout(0, 1));
		panel_include.setBorder(createTitledBorder("Incluir en la exportación"));
		
		final JCheckBox check_source = new JCheckBox("Código fuente (*.loro)", true);
		check_source.setMnemonic(KeyEvent.VK_F);
		panel_include.add(check_source);
		
		final JCheckBox check_compiled = new JCheckBox("Código compilado (*.oro)", true);
		check_compiled.setMnemonic(KeyEvent.VK_C);
		check_compiled.setDisplayedMnemonicIndex(7);
		panel_include.add(check_compiled);

		final JCheckBox check_html = new JCheckBox("Documentación (*.html)", true);
		check_html.setMnemonic(KeyEvent.VK_M);
		panel_include.add(check_html);

        Object[] array = {
			f_name,
			f_title,
			panel_target,
			panel_include,
			status
		};
		
		String diag_title = "Exportar proyecto: \"" +model.getInfo().getName()+ "\"";
        final ProjectDialog form = new ProjectDialog(frame, diag_title, array)
		{
			public boolean dataOk()
			{
				String msg = null;
				if ( radio_directory.isSelected() )
				{
					String dir = f_directory.getText().trim();
					if ( dir.length() == 0 )
					{
						msg = "Falta indicar un directorio absoluto de destino";
					}
					else 
					{
						File file = new File(dir);
						if ( !file.isAbsolute() )
						{
							msg = "Debe indicarse un directorio absoluto";
						}
					}
				}
				else if ( radio_extension.isSelected() )
				{
					String ext = f_extension.getText().trim();
					if ( ext.length() == 0 )
					{
						msg = "Falta indicar el nombre del archivo proyecto de destino";
					}
				}
				
				if ( msg == null )
				{
					if ( !check_source.isSelected() && !check_compiled.isSelected()
					&&   !check_html.isSelected() )
					{
						msg = "Nada para incluir en la exportación";
					}
				}
				
				if ( msg == null )
				{
					status.setForeground(Color.gray);
					status.setText("OK");
				}
				else
				{
					status.setForeground(Color.red);
					status.setText(msg);
				}
				return msg == null;
			}
		};
		
		choose_dir.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				radio_directory.setSelected(true);
				String dir = Util.selectDirectory(
					focusedProject.getFrame(),
					"Directorio de destino"
				);
				if ( dir != null )
				{
					f_directory.setText(dir);
				}
			}
		});
		
		choose_ext.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				radio_extension.setSelected(true);
				String ext = Util.selectSaveFile(
					focusedProject.getFrame(),
					"Archivo de extensión de destino",
					JFileChooser.FILES_ONLY
				);
				if ( ext != null )
				{
					if ( !ext.endsWith(".lar") )
						ext += ".lar";
					f_extension.setText(ext);
				}
			}
		});
		
		ActionListener target_radio_listener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				boolean b = e.getSource() == radio_extension;
				panel_extension.setEnabled(b);
				panel_directory.setEnabled(!b);
				form.notifyUpdate(); 
			}
		};
		radio_extension.addActionListener(target_radio_listener);
		radio_directory.addActionListener(target_radio_listener);


		ActionListener target_check_listener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				form.notifyUpdate(); 
			}
		};
		check_source.addActionListener(target_check_listener);
		check_compiled.addActionListener(target_check_listener);
		check_html.addActionListener(target_check_listener);


		form.activate();
        form.pack();
		form.setLocationRelativeTo(frame);
		form.setVisible(true);
		if ( form.accepted() )
		{
			String dest;
			if ( radio_directory.isSelected() )
			{
				dest = f_directory.getText().trim();
				File file = new File(dest);
				if ( file.exists()
				&&   !confirm(frame, 
						"El directorio de destino ya existe:\n"+
						"  " +dest+ "\n"+
						"¿Proceder con la exportación?\n") )
				{
					return;
				}
			}
			else // ( radio_extension.isSelected() )
			{
				dest = f_extension.getText().trim();
				if ( !dest.endsWith(".lar") )
					dest += ".lar";
				
				File file = new File(dest);
				if ( file.exists()
				&&  !confirm(frame, 
						"El archivo de destino ya existe:\n"+
						"  " +dest+ "\n"+
						"¿Proceder con la exportación?\n") )
				{
					return;
				}
			}
				
			try
			{
				workspace.exportProjectModel(
					focusedProject.getModel(), 
					dest,
					check_source.isSelected(),
					check_compiled.isSelected(),
					check_html.isSelected()
				);
				
				message(focusedProject.getFrame(), "Exportación de proyecto exitosa");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				JOptionPane.showOptionDialog(
					focusedProject.getFrame(),
					ex.getMessage(),
					"Error al tratar de exportar proyecto",
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE,
					null,
					null,
					null
				);
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Edita/visualiza una unidad.
	 *
	 * @param unit  La unidad a editar.
	 * @param frame Para posibles ventanas de mensajes.
	 *
	 * @return El editor.
	 */
	public static UEditor editUnit(IProjectUnit unit, JFrame frame)
	{
		UEditor editor = (UEditor) unit.getUserObject();
		if ( editor == null )
		{
			IPackageModel pkg = unit.getPackage();
			String pkgname;
			if ( pkg.getModel().isAnonymous(pkg) )
			{
				pkgname = "";
			}
			else
			{
				pkgname = pkg.getName()+ "::";
			}
			String name = unit.getStereotype()+ " " +pkgname+ unit.getName();
			String src = unit.getSourceCode();
			if ( src == null )
			{
				JOptionPane.showMessageDialog(
					frame,
					"No hay código fuente disponible",
					"Atención",
					JOptionPane.INFORMATION_MESSAGE
				);
				return null;
			}
			boolean modifiable = pkg.getModel().getControlInfo().isModifiable();
			editor = new UEditor(name, modifiable, unit instanceof AlgorithmUnit, true, true,
				Preferencias.EDITOR_RECT
			);
			editor.setText(src);
			editor.setEditable(unit.isEditable());
			editor.setCaretPosition(0);
			// el editor-listener puesto después para no recibir .changed():
			EditorListenerImpl lis = new EditorListenerImpl(unit);
			editor.setEditorListener(lis);
			unit.setUserObject(editor);
		}
		editor.display();
		return editor;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Guarda una unidad.
	 * Si la unidad está en edición, es decir, tiene un editor asociado,
	 * primero se actualiza el código fuente para la unidad tomando el
	 * contenido actual del editor.
	 *
	 * @return true si efectivamente la acción se completó.
	 */
	public static boolean saveUnit(IProjectUnit unit)
	{
		try
		{
			UEditor editor = (UEditor) unit.getUserObject();
			if ( editor != null )
			{
				unit.setSourceCode(editor.getText());
				workspace.saveUnit(unit);
				editor.setSaved(true);
				editor.getMessageArea().setText("Guardado.");
			}
			else
			{
				workspace.saveUnit(unit);
			}
			return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			JOptionPane.showOptionDialog(
				null, //focusedProject.getFrame(),
				ex.getMessage(),
				"Error al tratar de guardar unidad",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null,
				null,
				null
			);
			return false;
		}
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Documenta una unidad.
	 * La documentación se genera en doc_dir. 
	 * Si prj_dir no es null y docInProjectDirectory es true, también se 
	 * genera en prj_dir, lo cual facilita el desarrollo
	 * de los proyectos de demostración de tal manera que queden "completos"
	 * en sus directorios de origen y así puedan "instalarse" o "empaquetarse"
	 * posteriormente.
	 *
	 * @param unit  La unidad.
	 * @param prj_dir Directorio base del proyecto al que pertenece la unidad.
	 */
	public static void docUnit(IProjectUnit unit, File prj_dir)
	throws LoroException
	{
		IUnidad u = unit.getIUnidad();
		IDocumentador documentador = Loro.obtDocumentador();
		documentador.documentar(u, doc_dir);
		if ( prj_dir != null && docInProjectDirectory )
		{
			documentador.documentar(u, prj_dir.getAbsolutePath());
		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Genera toda la documentación de un proyecto en doc_dir.
	 *
	 * @param prjm Proyecto a documentar.
	 */
	public static void docProject(IProjectModel prjm)
	{
		_saveProjectDoc(prjm, null);
		
		// Actualizador documentación de cada unidad:
		IDocumentador documentador = Loro.obtDocumentador();
		for ( Iterator it = prjm.getPackages().iterator(); it.hasNext(); )
		{
			IPackageModel pkgm = (IPackageModel) it.next();
			
			for ( Iterator itt = pkgm.getUnits().iterator(); itt.hasNext(); )
			{
				IProjectUnit unit = (IProjectUnit) itt.next();
				IUnidad u = unit.getIUnidad();
				if ( u != null )
				{
					try
					{
						documentador.documentar(u, doc_dir);
					}
					catch(LoroException ex)
					{
						// ignore
					}
				}
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Ejecución en un hilo de _compileDemo();
	 */
	public static Thread compileDemo()
	{
		Runnable run = new Runnable() 
		{
			public void run() 
			{
				_compileDemo();
			}
		};
		Thread thread = new Thread(run);
		thread.start();
		return thread;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Compila el demo script del proyecto enfocado.
	 * Si el editor asociado a este script existe, se toma el código
	 * fuente del contenido de tal editor; si no, se toma directamente el
	 * código indicado por el proyecto enfocado.
	 * Si hay error y el editor no existe, se crea el editor y se despliega
	 * el mensaje allí.
	 *
	 * @return true si todo bien.
	 */
	public static boolean _compileDemo()
	{
		String name = focusedProject.getModel().getInfo().getName();
		String src;
		
		UEditor editor = (UEditor) demoEditors.get(name);
		
		if ( editor != null )
			src = editor.getText();
		else
			src = focusedProject.getModel().getInfo().getDemoScript();

		if ( src == null )
			return true;
		
		String compiling_msg = "Compilando demo '" +name+ "' ...";
		if ( editor != null )
		{
			editor.getMessageArea().clear();
			editor.getMessageArea().println(compiling_msg);
		}
		
		MessageArea prj_msg = focusedProject.getMessageArea();
		//prj_msg.clear();
		prj_msg.println(" " +compiling_msg);

		try
		{
			IInterprete ii = Loro.crearInterprete(null, null, true, null);
			ii.compilar(src);
			prj_msg.print(" Bien!");
			if ( editor != null )
				editor.getMessageArea().print(" Bien!");
			
			return true;
		}
		catch(CompilacionException ce)
		{
			Rango rango = ce.obtRango();
			
			String res = "[" +rango.obtIniLin()+ "," +rango.obtIniCol()+ "]" +
				" " +ce.getMessage()
			;
			
			prj_msg.println(res);

			if ( editor == null )
			{
				// aquí necesitamos el editor para mostrar el problema:
				editor = createDemoEditor();
			}
			
			if ( rango != null )
			{	
				editor.select(rango.obtPosIni(), rango.obtPosFin());
			}
			editor.display();
			editor.getMessageArea().print(res);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			JOptionPane.showOptionDialog(
				null, //focusedProject.getFrame(),
				ex.getMessage(),
				"Error al tratar de compilar unidad",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null,
				null,
				null
			);
		}
		
		return false;
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Compila una unidad.
	 */
	public static Thread compileUnit(final IProjectUnit unit)
	{
		Runnable run = new Runnable() 
		{
			public void run() 
			{

				UEditor editor = (UEditor) unit.getUserObject();
				if ( editor != null )
				{
					editor.getMessageArea().clear();
					editor.getMessageArea().print("Compilando...");
				}
				MessageArea prj_msg = focusedProject.getMessageArea();
				prj_msg.clear();
				prj_msg.print("Compilando " +unit.getStereotypedName()+ "...");
		
				try
				{
					workspace.compileUnit(unit);
					prj_msg.print(" Bien!");
					if ( editor != null )
					{
						editor.getMessageArea().print(" Bien!");
						refreshDocIfNecessary(unit);
					}
					return;
				}
				catch(CompilacionException ce)
				{
					Rango rango = ce.obtRango();
					
					String res = "[" +rango.obtIniLin()+ "," +rango.obtIniCol()+ "]" +
						" " +ce.getMessage()
					;
					
					prj_msg.println("\n" +unit.getStereotypedName()+ ": " +res);
					
					if ( editor == null )
					{
						// aquí necesitamos el editor para mostrar el problema:
						editor = editUnit(unit, null);
					}
					
					if ( editor != null )
					{
						if ( rango != null )
						{	
							editor.select(rango.obtPosIni(), rango.obtPosFin());
						}
						editor.display();
						editor.getMessageArea().print("\n" +res);
					}
					else
					{
						JOptionPane.showOptionDialog(
							null,
							res,
							"Hubo error en compilación",
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.ERROR_MESSAGE,
							null,
							null,
							null
						);
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					JOptionPane.showOptionDialog(
						null, //focusedProject.getFrame(),
						ex.getMessage(),
						"Error al tratar de compilar unidad",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.ERROR_MESSAGE,
						null,
						null,
						null
					);
				}
			}
		};
		Thread thread = new Thread(run);
		thread.start();
		return thread;
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Compila el proyecto enfocado.
	 */
	public static void compileProject()
	{
		Runnable run = new Runnable() 
		{
			public void run() 
			{
				MessageArea prj_msg = focusedProject.getMessageArea();
				prj_msg.clear();
				prj_msg.print("Compilando proyecto...");
				IProjectModel model = focusedProject.getModel();
				try
				{
					workspace.compileProjectModel(model);
					prj_msg.print(" Bien!");
					if ( browser != null ) // simplemente refresque.
						browser.refresh();
		
					File doc2_dir = null;
					if ( docInProjectDirectory )
					{
						doc2_dir = new File(prs_dir +File.separator+ model.getInfo().getName());
					}
					_saveProjectDoc(model, doc2_dir);
					
					// ahora compile el demo:
					boolean demo_ok = _compileDemo();
					// _compileDemo() abre el editor en caso de error.
					if ( demo_ok )					
						message(focusedProject.getFrame(), "Compilación de proyecto exitosa");
				}
				catch(UnitCompilationException ex)
				{
					CompilacionException ce = ex.getCompilacionException();
					Rango rango = ce.obtRango();
		
					IProjectUnit unit = ex.getUnit();
					
					String res = "[" +rango.obtIniLin()+ "," +rango.obtIniCol()+ "]" +
						" " +ex.getMessage()
					;
		
					prj_msg.println("\n" +unit.getStereotypedName()+ ": " +res);
					
					UEditor editor = (UEditor) unit.getUserObject();
					
					if ( editor == null )
					{
						// aquí necesitamos el editor para mostrar el problema:
						editor = editUnit(unit, null);
					}
					
					if ( editor != null )
					{
						if ( rango != null )
						{	
							editor.select(rango.obtPosIni(), rango.obtPosFin());
						}
						editor.display();
						editor.getMessageArea().setText(res);
					}
					else
					{
						// pero no debería suceder.
						JOptionPane.showOptionDialog(
							null,
							res,
							"Hubo error en compilación",
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.ERROR_MESSAGE,
							null,
							null,
							null
						);
					}
				}
				catch(Exception ex)
				{
					//ex.printStackTrace();
					JOptionPane.showOptionDialog(
						focusedProject.getFrame(),
						ex.getMessage(),
						"Error al compilar proyecto",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.ERROR_MESSAGE,
						null,
						null,
						null
					);
				}
			}
		};
		new Thread(run).start();
	}


	/////////////////////////////////////////////////////////////////
	/**
	 * Prueba los algoritmos del proyecto enfocado.
	 */
	public static void testProject()
	{
		Runnable run = new Runnable() 
		{
			public void run() 
			{
				MessageArea prj_msg = focusedProject.getMessageArea();
				prj_msg.clear();
				prj_msg.print("Tomando algoritmos a probar...\n");
				IProjectModel model = focusedProject.getModel();
				List cmds = new ArrayList();
				int total_algs = 0;
				for ( Iterator it = model.getPackages().iterator(); it.hasNext(); )
				{
					IPackageModel pkgm = (IPackageModel) it.next();
					for ( Iterator it2 = pkgm.getAlgorithmNames().iterator(); it2.hasNext(); )
					{
						total_algs++;
						String alg_name = (String) it2.next();
						AlgorithmUnit tested_alg = pkgm.getAlgorithm(alg_name);
						String cmd = getTestExecutionCmd(tested_alg, false);
						if ( cmd != null )
						{
							cmds.add(
								"// Probando " +tested_alg+ "\n"+
								cmd
							);
						}
					}
				}
				if ( cmds.size() > 0 )
				{
					prj_msg.print("Lanzando ventana para ejecución de pruebas...\n");
					cmds.add("// Pruebas terminadas exitosamente.");
					workspace.executeCommands(
						"Probando proyecto " +focusedProject.getModel().getInfo().getName(),
						"Invocando algoritmo" +(cmds.size() > 0 ? "s" : "")+ " de prueba.\n",
						cmds,
						false,    // newSymTab
						false     // ejecutorpp
					);
				}
				else if ( total_algs > 0 )
				{
					String msg = 
						"No se encontraron pruebas aplicables.\n"+
						"Un algoritmo se puede probar si cuenta con un esquema de\n"+
						"pruebas para su especificación y tanto el algoritmo a probar\n"+
						"como el algoritmo probador están compilados." 
					;
					prj_msg.print(msg+ "\n");
					message(focusedProject.getFrame(), msg);
				}
				else // total_algs == 0 
				{
					String msg = "Este proyecto no tiene algoritmos definidos\n";
						
					prj_msg.print(msg+ "\n");
					message(focusedProject.getFrame(), msg);
				}
			}
		};
		new Thread(run).start();
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Abre el editor del guión de demostración.
	 */
	public static UEditor editDemo()
	{
		String name = focusedProject.getModel().getInfo().getName();
		UEditor editor = (UEditor) demoEditors.get(name);
		if ( editor == null )
		{
			editor = createDemoEditor();
		}
		editor.display();
		return editor;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Crea el editor para el guión de demostración del proyecto enfocado
	 */
	private static UEditor createDemoEditor()
	{
		final IProjectModel prjm = focusedProject.getModel();
		IProjectModel.IInfo info = prjm.getInfo();
		String name = info.getName();
		String src = info.getDemoScript();
		if ( src == null )
			src = "";
		
		boolean modifiable = prjm.getControlInfo().isModifiable();
		final UEditor editor = new UEditor(
			"Demo '" +name+ "'", modifiable, true, false, true,
			Preferencias.DEMO_RECT
		);
		editor.setText(src);
		editor.setCaretPosition(0);
		// el editor-listener puesto después para no recibir .changed():
		editor.setEditorListener(new UEditorListener()
		{
			String saved = editor.getText();
			public void changed() 
			{
				if ( editor.isSaved() )
				{
					// primer cambio.
					editor.getMessageArea().print("\nIniciando edición");
				}
				editor.setSaved(false);
			}
			public void save() 
			{
				IProjectModel.IInfo info = prjm.getInfo();
				info.setDemoScript(editor.getText());
				workspace.saveDemoScript(prjm);
				editor.setSaved(true);
				editor.getMessageArea().setText("Guardado.");
				saved = editor.getText();
			}
			public void closeWindow() 
			{
				editor.getFrame().setVisible(false);
			}
			public void compile() 
			{
				save();
				compileDemo();
			}
			public void execute(boolean trace) 
			{
				if ( !editor.isSaved() )
				{
					save();
				}
				runDemo(trace);
			}
			public void reload() 
			{
				editor.setText(saved);
				editor.setSaved(true);
				editor.getMessageArea().setText("Recargado.");
				editor.setCaretPosition(0);
			}
			public void viewDoc() {}
		});
		
		demoEditors.put(name, editor);
		return editor;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Ejecuta un guión de demostración.
	 */
	public static void runDemo(final boolean ejecutorpp)
	{
		Runnable run = new Runnable() 
		{
			public void run() 
			{
				MessageArea prj_msg = focusedProject.getMessageArea();
				prj_msg.clear();
				IProjectModel model = focusedProject.getModel();
				prj_msg.print("Ejecutando demo '" +model.getInfo().getName()+ "' ...\n");
				String src = model.getInfo().getDemoScript();
				if ( src == null )
				{
					prj_msg.print("No hay código de demo definido\n");
					return;
				}
				BufferedReader br = new BufferedReader(new StringReader(src));
				String line;
				StringBuffer cmd = null;
				List cmds = new ArrayList();
				try
				{
					while ( (line = br.readLine()) != null )
					{
						if ( line.trim().length() == 0 )
						{
							// linea en blanco agrega comando acumulado:
							if ( cmd != null )
							{
								cmds.add(cmd.toString());
								cmd = null;
							}
						}
						else
						{
							// linea normal:
							if ( cmd == null )
								cmd = new StringBuffer(line);
							else
								cmd.append("\n" +line);
						}
					}
				}
				catch(IOException ex)
				{
					// ignore
				}

				if ( cmd != null )
					cmds.add(cmd.toString());
				
				workspace.executeCommands(
					"Ejecución demo '" +model.getInfo().getName()+ "'",
					null,
					cmds,
					true,     // newSymTab
					ejecutorpp
				);
			}
		};
		new Thread(run).start();
	}


	/////////////////////////////////////////////////////////////////
	/**
	 * ejecuta un algoritmo.
	 */
	public static void executeAlgorithm(AlgorithmUnit alg, boolean ejecutorpp)
	{
		IUnidad u = alg.getIUnidad();
		if ( u != null )
		{	
			workspace.executeAlgorithm(alg, null, ejecutorpp);
		}
		else
		{
			message(
				focusedProject.getFrame(),
				"El algoritmo no está compilado"
			);
		}
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre del algoritmo que hace pruebas al algoritmo dado.
	 * El algoritmo probador tiene exactamente el mismo nombre de la especificación,
	 * incluido el paquete correspondiente, SALVO que el nombre simple se prefija con
	 * tester_prefix. Por ejemplo, si la especificación del algoritmo dado es "foo::baz", 
	 * y tester_prefix == "probar_", entonces el algoritmo probador sería "foo::probar_baz".
	 */
	public static String getTesterAlgorithmName(IUnidad.IAlgoritmo tested_u)
	{
		String q_spec_name = tested_u.getSpecificationName();
		String tester_alg_name = q_spec_name;
		int index = tester_alg_name.lastIndexOf("::");
		if ( index >= 0 )
		{
			tester_alg_name = tester_alg_name.substring(0, index + 2)
				+ tester_prefix + tester_alg_name.substring(index + 2)
			;
		}
		else
		{
			tester_alg_name = tester_prefix +tester_alg_name;
		}
		return tester_alg_name;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Crea un comando de ejecución de prueba.
	 */
	public static String getTestExecutionCmd(AlgorithmUnit tested_alg, boolean dialogs)
	{
		MessageArea prj_msg = focusedProject.getMessageArea();
		prj_msg.print("Examinando '" +tested_alg.getQualifiedName()+ "' ...\n");
		IUnidad.IAlgoritmo tested_u = (IUnidad.IAlgoritmo) tested_alg.getIUnidad();
		if ( tested_u == null )
		{
			String msg = "...El algoritmo no está compilado";
			prj_msg.print(msg+ "\n");
			if ( dialogs )
			{
				message(focusedProject.getFrame(), msg);
			}
			return null;
		}
		
		String tester_alg_name = getTesterAlgorithmName(tested_u);
		
		// obtener el algoritmo probador:
		IUnidad.IAlgoritmo tester_u = Loro.getAlgorithm(tester_alg_name);
		if ( tester_u == null )
		{
			String msg = "...No se encuentra el algoritmo probador '" +tester_alg_name+ "'";
			prj_msg.print(msg+ "\n");
			if ( dialogs )
			{
				message(focusedProject.getFrame(), msg);
			}
			return null;
		}
		
		// todo listo para crear el comando de ejecución:
		return tester_u.obtNombreCompletoCadena()+ "(" +tested_u.obtNombreCompletoCadena()+ ")";
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Hace pruebas a un algoritmo.
	 */
	public static void testAlgorithm(AlgorithmUnit tested_alg, boolean dialogs)
	{
		String cmd = getTestExecutionCmd(tested_alg, dialogs);
		if ( cmd != null )
		{
			List cmds = new ArrayList();
			cmds.add(
				"// Probando " +tested_alg+ "\n"+
				cmd
			);
			cmds.add("// Prueba terminada exitosamente.");
			workspace.executeCommands(
				"Probando " +tested_alg.getIUnidad(),
				null,
				cmds,
				false,    // newSymTab
				false     // ejecutorpp
			);
		}
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Termina el sistema.
	 */
	public static void quit()
	{
		try
		{
			if ( focusedProject != null )
			{
				JFrame frame = focusedProject.getFrame();
				java.awt.Toolkit.getDefaultToolkit().beep();
				int sel = JOptionPane.showConfirmDialog(
					frame,
					"¿Finalizar el Entorno Integrado de Loro?",
					"Confirmación",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE
				);
				if ( sel != 0 )
					return;
				
				Preferencias.ponPreferencia(Preferencias.PRJ_RECENT, 
					focusedProject.getModel().getInfo().getName()
				);
				Rectangle rect = new Rectangle(frame.getLocationOnScreen(), frame.getSize());
				Preferencias.ponRectangulo(Preferencias.PRJ_RECT, rect);
			}
			
			LoroEDI.finalizar();
		}
		catch(Throwable ex)
		{
			ex.printStackTrace();
		}
		System.out.println("bye!");
		System.exit(0);
	}
	
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene las acciones.
	 */
	public static Actions getActions()
	{
		return actions;
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el proyecto actualmente enfocado.
	 */
	public static Project getFocusedProject()
	{
		return focusedProject;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Dice si el proyecto dado está abierto.
	 */
	public static boolean alreadyOpen(String prjname)
	{
		return openFrames.get(prjname) != null;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Crea un borde con título.
	 */
	public static Border createTitledBorder(String title)
	{
		return BorderFactory.createTitledBorder(title);
//		EtchedBorder eb = new EtchedBorder(Color.green, Color.white);
//		return BorderFactory.createTitledBorder(eb, title);
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Despacha un proyecto.
	 */
	public static void dispatch(String prjname)
	{
		ProjectFrame frame = (ProjectFrame) openFrames.get(prjname);
		if ( frame != null )
		{
			frame.project.display();
		}
		else
		{
			Rectangle rect;
			if ( focusedProject != null )
			{
				JFrame base_frame = focusedProject.getFrame();
				rect = new Rectangle(base_frame.getLocation(), base_frame.getSize());
			}
			else
			{
				rect = Preferencias.obtRectangulo(Preferencias.PRJ_RECT);
			}
			rect.translate(10, 10);
			frame = new ProjectFrame(rect);
			IProjectModel prjm = workspace.getProjectModel(prjname);
			_dispatch(frame, prjm);
		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Despacha un proyecto.
	 */
	private static void _dispatch(ProjectFrame frame, IProjectModel prjm)
	{
		focusedProject = new Project(frame, prjm);
		frame.project = focusedProject;
		focusedProject.display();
		openFrames.put(focusedProject.getModel().getInfo().getName(), frame);
		numOpen++;
		_updateWindowMenu();
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando cambia el proyecto (ventana) enfocado.
	 */
	private static void _focusedProjectChanged(Project newFocusedProject)
	{
		if ( newFocusedProject != null )
		{
			focusedProject = newFocusedProject;
			actions.setModificationEnabled(focusedProject.getModel().getControlInfo().isModifiable());
		}
	}
	

	////////////////////////////////////////////////////////////////////////////
	private static void _updateWindowMenu()
	{
		Collection prjnames = openFrames.keySet();
		for ( Iterator it = openFrames.values().iterator(); it.hasNext(); )
		{
			ProjectFrame frame = (ProjectFrame) it.next();
			frame.windowMenu.removeAll();
			for ( Iterator itt = prjnames.iterator(); itt.hasNext(); )
			{
				String prjname = (String) itt.next();
				String actionCmd = "p:" +prjname;
				if ( prjname.trim().length() == 0  )
				{
					prjname = UNTITLED_PROJECT;
				}
				JMenuItem mi = new JMenuItem("Proyecto: " +prjname);
				mi.setActionCommand(actionCmd);
				mi.addActionListener(selectFromWindowMenu);
				frame.windowMenu.add(mi);
			}
		}
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Dice si un projecto no tiene nombre y no tiene ningún paquete
	 * definido. Situación que lo hace descartable.
	 */
	private static boolean _isNewAndEmpty(IProjectModel model)
	{
		return
			model.getPackages().size() == 0 &&
			model.getInfo().getName().length() == 0
		;
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * @return true si la apertura procede.
	 */
	public static boolean openProject()
	{
		String prjname = _openProjectDialog(focusedProject.getFrame());
		return prjname == null ? false : _openProject(prjname);
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Abre el proyecto de nombre dado.
	 * @return true si la apertura procede.
	 */
	static boolean _openProject(String prjname)
	{
		if ( _isNewAndEmpty(focusedProject.getModel())
		&&   openFrames.get(prjname) == null )
		{
			// utilice la misma ventana:
			IProjectModel model = workspace.getProjectModel(prjname);
			focusedProject.setModel(model);
			openFrames.remove("");
			openFrames.put(prjname, focusedProject.getFrame());
			_updateWindowMenu();
		}
		dispatch(prjname);
		focusedProject.getMessageArea().clear();
		focusedProject.getMessageArea().println("Proyecto '" +prjname+ "' abierto");
		return true;
	}
	
	
	////////////////////////////////////////////////////////////////
	/**
	 * Instala proyecto con base en URL dado.
	 *
	 * @return el directorio en donde se hizo la instalación.
	 *              null si hay problemas.
	 */
	static File installFromURL(final URL url, final File outfile)
	{
		File dir = null;
		
		DataInputStream in;
		try
		{
			in = new DataInputStream(url.openStream());
		}
		catch(IOException ex)
		{
			JOptionPane.showOptionDialog(
				focusedProject.getFrame(),
				"Error al tratar de leer el archivo indicado.\n"
				+"Favor verificar la localización dada.\n"
				+"\n"
				+ex.getClass()+ ":\n"
				+"   " +ex.getMessage(),
				"Error al tratar de leer el archivo indicado",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null,
				null,
				null
			);
			return null;
		}
		try
		{
			MessageArea prj_msg = focusedProject.getMessageArea();
			prj_msg.clear();
			prj_msg.println("Importando proyecto:");
			
			File path = new File(url.getPath());
			String name = path.getName();
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
				new FileOutputStream(outfile)
			));
	
			byte[] buffer = new byte[1024];
			int read;
			while ( (read = in.read(buffer)) > 0 )
			{
				out.write(buffer, 0, read);
				prj_msg.print(".");
			}
			
			out.close();
			in.close();

			dir = Loro.expandExtensionFile(outfile, true);
			workspace.addProjectModelName(dir.getName());
			_addDirectoryToPath(dir.getName());
			
			prj_msg.println(" Listo");
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			JOptionPane.showOptionDialog(
				focusedProject.getFrame(),
				ex.getMessage(),
				"Error al tratar de instalar proyecto de proveniencia externa",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null,
				null,
				null
			);
		}
		finally
		{
			try{in.close();}catch(Exception ex){}
		}
		
		return dir;
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Permite al usuario instalar un proyecto de los incluidos en el sistema.
	 */
	public static void installProject()
	{
		Runnable run = new Runnable() 
		{
			public void run() 
			{
				_installProject();
			}
		};
		new Thread(run).start(); 
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Permite al usuario instalar un proyecto, bien sea de los 
	 * incluidos en el sistema, o de proveniencia externa.
	 */
	private static void _installProject()
	{
		String private_prs_directory = Configuracion.getProperty(Configuracion.DIR)+ "/lib/prs/";
		List prjnames = new ArrayList();

		File private_prs_dir = new File(private_prs_directory);
		if ( private_prs_dir.isDirectory() )
		{
			File[] files = private_prs_dir.listFiles();
			for ( int i = 0; i < files.length; i++ )
			{
				if ( files[i].isDirectory() )
				{
					String name = files[i].getName(); 
					prjnames.add(name);
				}
			}
		}
		
		final JLabel status = new JLabel();
		status.setFont(status.getFont().deriveFont(Font.ITALIC));
		final JList cb_prjs = new JList((String[]) prjnames.toArray(new String[0]));
		cb_prjs.setVisibleRowCount(5);
		cb_prjs.setSelectedIndex(0); 
		cb_prjs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
        final JTextField f_name = new JTextField(50);
		f_name.setFont(monospaced12_font);
		
		
		JCheckBox chk_open = new JCheckBox("Abrir proyecto al finalizar instalación", null, true); 
		
		JScrollPane sp = new JScrollPane(cb_prjs);
		f_name.setBorder(createTitledBorder("Instalar con el nombre"));
		f_name.setText((String) cb_prjs.getSelectedValue());

		final JPanel panel_inc = new JPanel();
		panel_inc.setLayout(new BoxLayout(panel_inc, BoxLayout.Y_AXIS));
		panel_inc.setBorder(createTitledBorder(""));
		final JRadioButton radio_inc = new JRadioButton("Instalar proyecto incluido:");
		radio_inc.setAlignmentX(0f);
		radio_inc.setMnemonic(KeyEvent.VK_I);
		radio_inc.setSelected(true);
		panel_inc.add(radio_inc);
		sp.setAlignmentX(0f);
		panel_inc.add(sp);

		cb_prjs.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				f_name.setText((String) cb_prjs.getSelectedValue());
				cb_prjs.ensureIndexIsVisible(cb_prjs.getSelectedIndex());
				radio_inc.setSelected(true);
			}
		});

		final JPanel panel_lar = new JPanel();
		panel_lar.setLayout(new BoxLayout(panel_lar, BoxLayout.Y_AXIS));
		panel_lar.setBorder(createTitledBorder(""));
		final JRadioButton radio_lar = new JRadioButton("Instalar proyecto externo:");
		radio_lar.setAlignmentX(0f);
		panel_lar.add(radio_lar);
		JLabel l_header_lar = new JLabel(
			"<html>"+
			"Escribe la ubicación web (URL) o indica el archivo local que contiene<br>\n"+
			"el proyecto a instalar.\n"+
			"</html>"
		);
		l_header_lar.setForeground(Color.gray);
		l_header_lar.setAlignmentX(0f);
		panel_lar.add(l_header_lar);
		final JPanel panel_lar2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel_lar2.setAlignmentX(0f);
        final JTextField f_lar = new JTextField(30);
		f_lar.setText(Preferencias.obtPreferencia(Preferencias.PRJ_EXTERN_LAST));
		panel_lar2.add(f_lar);
		radio_lar.setMnemonic(KeyEvent.VK_E);
		
		JButton choose_lar = new JButton("Local...");
		choose_lar.setMnemonic(KeyEvent.VK_L);
		panel_lar2.add(choose_lar);
		
		JButton browse_for_lar = new JButton("Navegar...");
		browse_for_lar.setMnemonic(KeyEvent.VK_N);
		panel_lar2.add(browse_for_lar);
		
		panel_lar.add(panel_lar2);

		final DocumentListener docListener = new DocumentListener()
		{
			void common()
			{
				String name = "";
				f_name.setText("");
//				radio_lar.setSelected(true);
				String lar = f_lar.getText().trim();
				if ( lar.length() == 0 )
					return;

				if ( lar.toLowerCase().startsWith("http:")
				||   lar.toLowerCase().startsWith("ftp:")
				||   lar.toLowerCase().startsWith("file:") )
				{
					try
					{
						URL url = new URL(lar);
						name = new File(url.getPath()).getName();
					}
					catch (MalformedURLException ex)
					{
					}
				}
				else
				{
					// Se asume como archivo local.
					
					File file = new File(lar);
					if ( file.exists() )
						name = file.getName();
				}
				if ( name.toLowerCase().endsWith(".lar") )
					name = name.substring(0, name.length() -4);
				f_name.setText(name);
			}
			public void insertUpdate(DocumentEvent e) { common(); }
			public void removeUpdate(DocumentEvent e) { common(); }
			public void changedUpdate(DocumentEvent e) { common(); }
		};
		f_lar.getDocument().addDocumentListener(docListener);

		ButtonGroup group = new ButtonGroup();
        group.add(radio_inc);
        group.add(radio_lar);

		JLabel l_header = new JLabel(
			"<html>"+
			"Instalar un proyecto significa ponerlo en el espacio de trabajo para<br>\n"+
			"que pueda ser abierto, probado y posiblemente modificado.<br>\n"+
			"<br>\n"+
			"Aquí puedes elegir entre instalar uno de los proyectos incluidos con<br>\n"+
			"el sistema, o instalar un proyecto de proveniencia externa.<br>\n"+
			"</html>"
		);
		l_header.setForeground(Color.gray);

		
        Object[] array = {
			l_header,
			panel_inc,
			panel_lar,
			f_name,
			chk_open,
			status
		};
		
        final ProjectDialog form = new ProjectDialog(focusedProject.getFrame(), "Instalar proyecto", array)
		{
			public boolean dataOk()
			{
				String prjname = f_name.getText();
				String msg = null;
				boolean ret = true;
				
				if ( radio_inc.isSelected() )
				{
					String from_name = (String) cb_prjs.getSelectedValue();
					
					if ( from_name == null || from_name.trim().length() == 0 )
					{
						msg = "Falta indicar el proyecto a instalar";
						ret = false;
					}
				}
				else if ( radio_lar.isSelected() )
				{
					String lar = f_lar.getText().trim();
					if ( lar.length() == 0 )
					{
						msg = "Falta indicar el archivo";
						ret = false;
					}
					else if ( lar.toLowerCase().startsWith("http:")
					||   lar.toLowerCase().startsWith("ftp:")
					||   lar.toLowerCase().startsWith("file:") )
					{
						try
						{
							URL url = new URL(lar);
						}
						catch (MalformedURLException ex)
						{
							msg = "URL mal formado";
							ret = false;
						}
					}
					else
					{
						// Se asume como archivo local.
						
						File file = new File(lar);
						if ( !file.exists() )
						{
							msg = "El elemento indicado no existe";
							ret = false;
						}
					}
				}
				
				if ( msg == null )
				{
					if ( prjname.trim().length() == 0 )
					{
						msg = "Falta indicar un nombre para instalar el proyecto";
						ret = false;
					}
					else if ( alreadyOpen(prjname) )
					{
						msg = "Hay un proyecto abierto con el nombre '" +prjname+ "'";
						ret = false;
					}
					else if ( new File(new File(prs_dir), prjname).exists() )
					{
						msg = "Ya hay un proyecto instalado con el nombre '" +prjname+ "'";
						setAcceptText("SOBREESCRIBIR");
					}
					else
					{
						setAcceptText("Aceptar");
					}
				}
				
				status.setForeground(ret ? Color.gray : Color.red);
				if ( msg == null )
				{
					msg = "OK";
				}
				status.setText(msg);
				return ret;
			}
		};
		
		choose_lar.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String lar = Util.selectFile(
					focusedProject.getFrame(),
					"Archivo proyecto",
					JFileChooser.FILES_ONLY
				);
				if ( lar != null )
				{
					f_lar.setText(lar);
					radio_lar.setSelected(true);
				}
			}
		});

		browse_for_lar.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				radio_lar.setSelected(true);
				String lar = f_lar.getText().trim();
				
				lar = _chooseProjectInPage(
					focusedProject.getFrame(), 
					lar
				);
				if ( lar != null )
					f_lar.setText(lar);
			}
		});

		ActionListener radio_listener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e) 
			{
				boolean b = e.getSource() == radio_inc;
				panel_inc.setEnabled(b);
				panel_lar.setEnabled(!b);
				form.notifyUpdate(); 
				docListener.changedUpdate(null);
			}
		};
		radio_inc.addActionListener(radio_listener);
		radio_lar.addActionListener(radio_listener);

		form.activate();
        form.pack();
		form.setLocationRelativeTo(focusedProject.getFrame());
		form.setVisible(true);
		if ( form.accepted() )
		{
			String to_name = f_name.getText();
			String from_name;
			
			if ( radio_inc.isSelected() )
			{
				// haga copia del directorio:
				from_name = (String) cb_prjs.getSelectedValue();
				File private_prj_dir = new File(private_prs_directory, from_name);
				
				File prj_dir = new File(new File(prs_dir), to_name);
				try
				{
					prj_dir.mkdirs();
					loroedi.Util.copyDirectory(private_prj_dir, "", prj_dir, true);
					workspace.addProjectModelName(to_name);
					_addDirectoryToPath(to_name);
					
					// ahora copy documentación:
					loroedi.Util.copyDirectory(private_prj_dir, ".html", new File(doc_dir), true);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					JOptionPane.showOptionDialog(
						focusedProject.getFrame(),
						ex.getMessage(),
						"Error al instalar proyecto",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.ERROR_MESSAGE,
						null,
						null,
						null
					);
					return;
				}
				
			}
			else if ( radio_lar.isSelected() )
			{
				URL url;
				
				String lar = f_lar.getText().trim();
				Preferencias.ponPreferencia(Preferencias.PRJ_EXTERN_LAST, lar);
				if ( lar.toLowerCase().startsWith("http:")
				||   lar.toLowerCase().startsWith("ftp:") 
				||   lar.toLowerCase().startsWith("file:") )
				{
					try
					{
						url = new URL(lar);
					}
					catch (MalformedURLException ex)
					{
						// But shouldn't happen.
						return;
					}
				}
				else
				{
					File file = new File(lar);
					try
					{
						url = file.toURL();
					}
					catch (MalformedURLException ex)
					{
						// But shouldn't happen.
						return;
					}
				}
				
				File outfile = new File(new File(prs_dir), to_name+ ".lar");
				File dir = installFromURL(url, outfile);
				if ( dir == null )
					return;
				
				from_name = dir.getName(); 
			}
			else
			{
				throw new RuntimeException("Impossible");
			}
			
			String msg; 
			boolean compile_project = false;
			if ( chk_open.isSelected() )
			{
				_openProject(to_name);
				
				msg = 
					"Instalación completada.\n"+
					"\n"+
					"El proyecto '" +from_name+ "' se ha instalado con el nombre '" +to_name+ "'\n"+
					"y se encuentra ahora abierto.\n"+
					"\n"+
					"Loro procederá a compilar este proyecto a continuación.\n"+
					"\n"
				;
				compile_project = true;
			}
			else
			{
				msg = 
					"Instalación completada.\n"+
					"\n"+
					"El proyecto '" +from_name+ "' se ha instalado con el nombre '" +to_name+ "'.\n"+
					"\n"+
					"Para abrirlo, utiliza la opción 'Abrir...' en el menú 'Proyecto'.\n"+
					"Posiblemente sea necesario compilar el proyecto una vez abierto.\n"+
					"\n"
				;
			}
			message(focusedProject.getFrame(), msg);
			if ( compile_project )
				compileProject();
		}
	}
	
	
	
	////////////////////////////////////////////////////////////////
	/**
	 * @return El URL del proyecto seleccionado por el usuario en 
	 * navegación de página web.
	 */
	private static String _chooseProjectInPage(JFrame frame, String lar)
	{
		if ( lar.length() == 0 )
			return null;

		String larlc = lar.toLowerCase();
		if ( !larlc.startsWith("http:")
		&&   !larlc.startsWith("ftp:")
		&&   !larlc.startsWith("file:") )
			return null;
			
		if ( larlc.endsWith(".lar") )
			return null;
			
		URL page;
		try
		{
			page = new URL(lar);
		}
		catch (MalformedURLException ex)
		{
			return null;
		}
		
		
        final JTextField f_name = new JTextField(50);
		f_name.setEditable(false);
		f_name.setFont(monospaced12_font);
		f_name.setBorder(createTitledBorder("Enlace completo"));
		
		final JLabel status = new JLabel();
		status.setFont(status.getFont().deriveFont(Font.ITALIC));

		BrowserPanel prj_browser = new BrowserPanel(null, null);
		prj_browser.setPage(page);
		prj_browser.setLocationEditable(false);
		prj_browser.setClickListener(new BrowserPanel.IClickListener()
		{
			public boolean click(URL hyperlink)
			{
				String lar = hyperlink.toString();
				if ( lar.endsWith(".lar") )
				{
					f_name.setText(lar);
					return true;
				}
				return false;
			}
		});
		prj_browser.setBorder(createTitledBorder("Elegir enlace del proyecto a instalar"));
		
        Object[] array = {
			prj_browser,
			f_name,
			status
		};
		
        final ProjectDialog form = new ProjectDialog(frame, "Buscar proyecto en página", array)
		{
			public boolean dataOk()
			{
				String msg = null;
				boolean ret = true;
				String lar = f_name.getText();
				if ( lar.length() == 0 )
				{
					msg = "Falta indicar un enlace .lar";
					ret = false;
				}
				status.setForeground(ret ? Color.gray : Color.red);
				if ( msg == null )
					msg = "OK";

				status.setText(msg);
				return ret;
			}
		};
		Preferencias.Util.updateRect(form, Preferencias.PRJ_CHOOSE_RECT);
		form.activate();
		form.setVisible(true);
		if ( form.accepted() )
		{
			return f_name.getText();
		}
		return null;
	}

	
	////////////////////////////////////////////////////////////////
	/**
	 * Permite al usuario importar un archivo fuente en el proyecto enfocado.
	 */
	public static void importFiles()
	{
		if ( focusedProject == null )
		{
			return;
		}
		JFrame frame = focusedProject.getFrame();
		String filename = Util.selectFile(frame, 
			"Archivo fuente a importar", JFileChooser.FILES_ONLY
		);
		if ( filename == null )
		{
			return;
		}
		try
		{
			String source = Util.readFile(new File(filename));
			IProjectModel prjm = focusedProject.getModel();
			workspace.importSource(prjm, source);
		}
		catch(CompilacionException ex)
		{
			Rango r = ex.obtRango();
			JOptionPane.showOptionDialog(
				frame,
				"Error de compilación en el fuente.\n"
				+"\n"
				+"No se puede importar un fuente con errores de compilación."
				+"\n"
				+"[" +r.obtIniLin()+ "," +r.obtIniCol()+ "] " +ex.getMessage()
				,
				"Error de compilación",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null,
				null,
				null
			);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			JOptionPane.showOptionDialog(
				frame,
				ex.getMessage(),
				"Error al importar fuente",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.ERROR_MESSAGE,
				null,
				null,
				null
			);
		}
	}


	////////////////////////////////////////////////////////////////
	/**
	 * @return El nombre del proyecto seleccionado por el usuario.
	 */
	private static String _openProjectDialog(JFrame frame)
	{
		String[] prjnames = (String[]) workspace.getAvailableProjects().toArray(new String[0]);
		final JList cb_prjs = new JList(prjnames); 
		cb_prjs.setSelectedIndex(0); 
		cb_prjs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
		final JTextField f_selected = new JTextField((String) cb_prjs.getSelectedValue());
		f_selected.setEditable(false);
		f_selected.setBorder(createTitledBorder("Abrir"));
		final JLabel status = new JLabel();
		status.setFont(status.getFont().deriveFont(Font.ITALIC));
		
		JScrollPane sp = new JScrollPane(cb_prjs);
		sp.setBorder(createTitledBorder("Proyectos de trabajo"));
		

		cb_prjs.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				String str = (String) cb_prjs.getSelectedValue();
				if ( str == null )
				{
					str = "";
				}
				f_selected.setText(str);
				cb_prjs.ensureIndexIsVisible(cb_prjs.getSelectedIndex());
			}
		});

		JLabel l_header = new JLabel(
			"<html>"+
			"Estos proyectos hacen parte del actual espacio de trabajo.<br>\n"+
			"Si no está aquí el proyecto buscado, intentar por la opción<br>\n"+
			"'Instalar' del menú principal.\n"+
			"</html>"
		);
		l_header.setForeground(Color.gray);

        Object[] array = {
			l_header,
			sp,
			f_selected,
			status
		};
		
        final ProjectDialog form = new ProjectDialog(frame, "Abrir proyecto", array)
		{
			public boolean dataOk()
			{
				String prjname = f_selected.getText();
				String msg = null;
				boolean ret = true;
				if ( prjname.length() == 0 )
				{
					msg = "Falta indicar el proyecto a abrir";
					ret = false;
				}
				if ( alreadyOpen(prjname) )
				{
					msg = "Proyecto actualmente abierto";
				}
				status.setForeground(ret ? Color.gray : Color.red);
				if ( msg == null )
				{
					msg = "OK";
				}
				status.setText(msg);
				return ret;
			}
		};
		
		form.activate();
        form.pack();
		form.setLocationRelativeTo(frame);
		form.setVisible(true);
		if ( form.accepted() )
		{
			String prjname = f_selected.getText();
			return prjname;
		}
		return null;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Returns all specs (names) in the workspace.
	 */
	public static Collection getAllSpecs()
	{
		return workspace.getAvailableSpecifications();
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la lista de nombres de los proyectos disponibles.
	 */
	public static Collection getAvailableProjects()
	{
		return workspace.getAvailableProjects();
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene uno de los modelos disponibles.
	 * Si el nombre no concuerda con alguno de los modelos disponibles,
	 * se genera un RuntimeException.
	 */
	public static IProjectModel getProjectModel(String prjname)
	{
		return workspace.getProjectModel(prjname);
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 */
	public static void reloadProject()
	{
		final Runnable doIt = new Runnable() 
		{	
			public void run() 
			{
				MessageArea prj_msg = focusedProject.getMessageArea();
				prj_msg.clear();
				prj_msg.print("Recargando...");
				IProjectModel model = workspace.getProjectModel(
					focusedProject.getModel().getInfo().getName()
				);
				focusedProject.setModel(model);
				focusedProject.getDiagram().repaint();
				prj_msg.print(" Listo");
			}
		};
		
		new Thread(doIt).start(); 
	}
	
	////////////////////////////////////////////////////////////////////////////
	private static JMenuBar createMenuBar(JMenu windowMenu)
	{
		JMenuBar mb = new JMenuBar();
		mb.add(createMenu(getActions().getProjectGroup()));
		mb.add(createMenu(getActions().getDevelopmentGroup()));
		mb.add(createMenu(getActions().getExecutionGroup()));
		mb.add(windowMenu);
		mb.add(createMenu(getActions().getHelpGroup()));
		return mb;
	}

	////////////////////////////////////////////////////////////////////////////
	private static JMenu createMenu(Actions.Group g)
	{
		JMenu menu = new JMenu(g.name);
		menu.setMnemonic(g.mnemonic);
		for ( Iterator it = g.actions.iterator(); it.hasNext(); )
		{
			Object obj = (Object) it.next();
			if ( obj == null )
			{
				menu.addSeparator();
			}
			else if ( obj instanceof Action )
			{
				menu.add((Action) obj);
			}
			else if ( obj instanceof Actions.Group )
			{
				menu.add(createMenu((Actions.Group) obj));
			}
		}
		return menu;
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Extensión de JFrame para mantener asociación con el proyecto
	 * despachado y el menu "Ventana" correspondiente.
	 */
	private static class ProjectFrame extends JFrame
	{
		Project project;
		JMenu windowMenu;
	
		/////////////////////////////////////////////////////////////////
		/**
		 * Constructor básico; ninguna inicialización; debe llamarse
		 * obj.init() para hace la instancia obj útil.
		 */
		ProjectFrame()
		{
			super();
		}
		
		/////////////////////////////////////////////////////////////////	
		/**
		 * Constructor general.
		 */
		ProjectFrame(Rectangle rect)
		{
			this();
			init(rect);
		}
		
		/////////////////////////////////////////////////////////////////	
		/**
		 * Método pensado exclusivamente para ser llamado sobre una
		 * instancia creada con el constructor básico.
		 */
		void init(Rectangle rect)
		{
			addWindowListener(new WindowAdapter()
			{
				public void windowClosing(WindowEvent _)
				{
					closeProject();
				}
			});
			addWindowFocusListener(new WindowFocusListener()
			{
				public void windowGainedFocus(WindowEvent ev)
				{
					_focusedProjectChanged(project);
				}
				public void windowLostFocus(WindowEvent _) {}
			});
			setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			URL url = getClass().getClassLoader().getResource("img/icon.jpg");
			if ( url != null ) 
				setIconImage(new ImageIcon(url).getImage());

			windowMenu = new JMenu("Ventana");
			JMenuBar mb = createMenuBar(windowMenu); 
			windowMenu.setMnemonic(KeyEvent.VK_V);
			setJMenuBar(mb);
			getContentPane().add(createToolBar(), "North");
			if ( rect != null )
			{
				setLocation(rect.x, rect.y);
				setSize(rect.width, rect.height);
			}
		}
		
		/////////////////////////////////////////////////////////////////
		private JToolBar createToolBar()
		{
			JToolBar tb = new JToolBar();
			tb.setFloatable(false);
			tb.add(actions.getAction("new")).setFocusable(false);
			tb.add(actions.getAction("open")).setFocusable(false);
			tb.addSeparator();
			tb.add(actions.getAction("compile-project")).setFocusable(false);
			tb.add(actions.getAction("show-ii")).setFocusable(false);
			tb.add(actions.getAction("view-prj-doc")).setFocusable(false);
			tb.add(actions.getAction("help")).setFocusable(false);
			tb.addSeparator();
			return tb;
		}
	
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Atiende acciones sobre una editor de unidad.
	 * Por ahora sólo se muestran mensajes de "eco".
	 * Pendiente implementación real.
	 */
	private static class EditorListenerImpl implements UEditorListener
	{
		IProjectUnit unit;
		
		/////////////////////////////////////////////////////////////////
		public EditorListenerImpl(IProjectUnit unit)
		{
			this.unit = unit;
		}
		
		/////////////////////////////////////////////////////////////////
		public void changed()
		{
			UEditor editor = (UEditor) unit.getUserObject();
			if ( editor.isSaved() )
			{
				// primer cambio.
				editor.getMessageArea().print("\nIniciando edición");
			}
			editor.setSaved(false);
		}
	
		/////////////////////////////////////////////////////////////////
		public void viewDoc()
		{
			showUnitDocumentation(unit);
		}
	
		/////////////////////////////////////////////////////////////////
		public void save()
		{
			saveUnit(unit);
		}
	
		/////////////////////////////////////////////////////////////////
		public void compile()
		{
			if ( saveUnit(unit) )
			{
				compileUnit(unit);
			}
		}
	
		/////////////////////////////////////////////////////////////////
		public void execute(boolean trace) 
		{
			if ( unit instanceof AlgorithmUnit )
				executeAlgorithm((AlgorithmUnit) unit, trace);
		}

		/////////////////////////////////////////////////////////////////
		public void reload()
		{
			UEditor editor = (UEditor) unit.getUserObject();
			editor.setText(unit.getSourceCode());
			editor.setSaved(true);
			editor.getMessageArea().setText("Recargado.");
			editor.setCaretPosition(0);
		}
		
		/////////////////////////////////////////////////////////////////
		public void closeWindow()   // PENDING
		{
			UEditor editor = (UEditor) unit.getUserObject();
			editor.getFrame().setVisible(false);
		}
	
	}
	
	/////////////////////////////////////////////////////////////////////
	public static boolean confirm(JFrame frame, String msg)
	{
		// No uso showConfirmDialog porque no encuentro como poner el
		// "No" preenfocado. Así que uso showOptionDialog.
		
		Object[] options = { "Sí", "No" };
		java.awt.Toolkit.getDefaultToolkit().beep();
		return 0 == JOptionPane.showOptionDialog(
			frame,
			msg,
			"Confirmación",
			JOptionPane.DEFAULT_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options, options[1]
		);
	}
	
	/////////////////////////////////////////////////////////////////////
	public static void message(JFrame frame, String msg)
	{
		java.awt.Toolkit.getDefaultToolkit().beep();
		JOptionPane.showMessageDialog(
			frame,
			msg,
			"Mensaje",
			JOptionPane.WARNING_MESSAGE
		);
	}
}
