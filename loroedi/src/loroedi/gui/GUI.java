package loroedi.gui;

import loroedi.gui.project.*;
import loroedi.gui.project.model.*;
import loroedi.gui.project.unit.*;
import loroedi.gui.editor.*;
import loroedi.gui.misc.MessageArea;

import loroedi.Info.Str;
import loroedi.Util;
import loroedi.Configuracion;
import loroedi.Preferencias;
import loroedi.InterpreterWindow;
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

import javax.swing.Timer;
 
//////////////////////////////////////////////////
/**
 * Controlador general del entorno integrado.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class GUI
{
	public static final Font monospaced12_font = new Font("monospaced", Font.PLAIN, 12);
	public static final String UNTITLED_PROJECT = Str.get("gui.untitled_prj");
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
	
	static InterpreterWindow interactiveInterpreter;
	
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
			
			splash.status(Str.get("gui.starting_workspace"));
			workspace = Workspace.createInstance(prs_dir); 

			numOpen = 0;
			IProjectModel model = null;
			
			String recent = Preferencias.obtPreferencia(Preferencias.PRJ_RECENT);
			if ( recent.length() > 0
			&&   workspace.existsProjectModel(recent) )
			{
				splash.status(Str.get("gui.loading_recent_prj"));
				model = workspace.getProjectModel(recent);
			}
			else
			{
				// abra un nuevo proyecto en blanco:
				model = workspace.getNewProjectModel();
				
				// lo siguiente pendiente mientras se define comportamiento.
				//_newOrOpenProject(frame);
				//if ( numOpen == 0 )
				//{
				//	frame.dispose();
				//	quit();
				//}
			}
			
			_dispatch(frame, model);
			
			_firstTime();
			
			if ( _isNewAndEmpty(model) ) {
				focusedProject.getMessageArea().print(Str.get("gui.msg_new_prj"));
			}
			else {
				focusedProject.getMessageArea().print(
					Str.get("gui.1_msg_prj_open", model.getInfo().getName())
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
				"An error occurred",
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
		if ( prs_dir == null ) {
			prs_dir = Preferencias.obtPreferencia(Preferencias.PRS_DIR);
		}
		new File(prs_dir).mkdirs();

		// prepare valores de configuración para Loro:
		splash.status(Str.get("gui.msg_init_core"));
		String ext_dir = Configuracion.getProperty(Configuracion.DIR)+ "/lib/ext/";
		String paths_dir = prs_dir;
		Loro.configurar(ext_dir, paths_dir);

		// pendiente
		String oro_dir = Preferencias.obtPreferencia(Preferencias.ORO_DIR);
		ICompilador compilador = Loro.obtCompilador();
		compilador.ponDirectorioDestino(oro_dir);
		
		splash.status(Str.get("gui.msg_setup_env"));
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
		splash.status(Str.get("gui.msg_verify_core"));
		Loro.verificarNucleo();

		IDocumentador documentador = Loro.obtDocumentador();

		////////////////////////////////////////			
		// genere documentacion del núcleo en doc_dir:
		documentador.documentarUnidadesDeApoyo(doc_dir);
		
		////////////////////////////////////////
		// genere documentacion de extensiones en doc_dir:
		// lo siguiente genera doc para todas las unidades,
		// incluyendo las del núcleo:
		splash.status(Str.get("gui.msg_update_doc"));
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
	 * Despliega el Intérprete Interactivo único.
	 */
	public static void showII()
	{
		if ( interactiveInterpreter == null )
		{
			interactiveInterpreter = new InterpreterWindow(
				Str.get("ii.tit"),
				loroedi.Info.obtTituloII()+ "\n"
				+Str.get("ii.hello"),
				false,                                //newSymTab
				false                                 //ejecutorpp
			);
			interactiveInterpreter.start();
		}
		interactiveInterpreter.mostrar();
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
		docFrame = new JFrame(Str.get("gui.tit_doc"));
		URL url = ClassLoader.getSystemClassLoader().getResource("img/icon.jpg");
		if ( url != null ) 
			docFrame.setIconImage(new ImageIcon(url).getImage());
		docFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		docFrame.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent _)
			{
				docFrame.setVisible(false);
			}
		});
		Preferencias.Util.updateRect(docFrame, Preferencias.DOC_RECT);

		URL home_url = null;
		try
		{
			home_url = new File(doc_dir, "index.html").toURL();
		}
		catch(MalformedURLException e)
		{
			// ignore.
		}
		browser  = new BrowserPanel(home_url, null);
		docFrame.getContentPane().add(browser);
		if ( home_url != null )
			browser.setPage(home_url);
		
		// para revisar si es archivo local y existencia:
		browser.setClickListener(new BrowserPanel.IClickListener()
		{
			public boolean click(URL hyperlink)
			{
				if ( hyperlink.getProtocol().equalsIgnoreCase("file")
				&&  !new File(hyperlink.getPath()).exists() )
				{
					message(docFrame, Str.get("gui.msg_link_not_found"));
					return true;
				}
				return false;
			}
		});
	}

	
	/////////////////////////////////////////////////////////////////
	/**
	 * Despliega el documento indicado. 
	 */
	private static void showDocumentation(String basename, String ext)
	{
		String docname = basename+ "." +ext;
		String filename = Util.replace(basename, "::", File.separator);
		filename += "." +ext+ ".html";
		File file = new File(doc_dir, filename);
		if ( !file.exists() ) {
			message(focusedProject.getFrame(), Str.get("gui.msg_link_not_found"));
			return;
		}
		
		if ( browser == null )
			createBrowserPanel();

		docFrame.setVisible(true);
		try
		{
			URL url = file.toURL();
			URL prev = browser.getPage();
			if ( prev != null && prev.equals(url) )
				browser.refresh();
			else
				browser.setPage(url);
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
		showDocumentation(prjname, "prj");
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Despliega la documentación de una unidad.
	 */
	public static void showUnitDocumentation(IProjectUnit unit)
	{
		String unitname = unit.getQualifiedName();
		showDocumentation(unitname, unit.getCode());
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
				String filename = Util.replace(unitname, "::", File.separator);
				filename += "." +unit.getCode()+ ".html";
				URL url = new File(doc_dir, filename).toURL();
				URL prev = browser.getPage();
				if ( prev != null && prev.equals(url) )
					browser.refresh();
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
					"Error while saving project properties",
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
		String[] strs = Str.get("gui.contains_html").split("\\|", 2);
		JCheckBox t_htmlDescription = new JCheckBox(strs[0]);
		t_htmlDescription.setToolTipText(strs[1]);
		final JLabel status = new JLabel();
		
		if ( !valid ) {
			status.setForeground(Color.red);
			status.setText("incompatible project");
		}
		else{
			status.setFont(status.getFont().deriveFont(Font.ITALIC));
			if ( !editable )
				status.setText(Str.get("gui.msg_read_only_props"));
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
		f_name.setBorder(createTitledBorder(Str.get("gui.prj_code")));
		f_title.setBorder(createTitledBorder(Str.get("gui.prj_tit")));
		f_authors.setBorder(createTitledBorder(Str.get("gui.prj_author")));
		f_version.setBorder(createTitledBorder(Str.get("gui.prj_version_date")));
		JScrollPane sp = new JScrollPane(f_description);
		Box descr_panel = new Box(BoxLayout.Y_AXIS);
		t_htmlDescription.setAlignmentX(0f);
		descr_panel.add(t_htmlDescription, "North");
		sp.setAlignmentX(0f);
		descr_panel.add(sp);
		
		descr_panel.setBorder(createTitledBorder(Str.get("gui.prj_description")));
		
        Object[] array = {
			f_name,
			f_title,
			f_authors,
			f_version,
			descr_panel,
			status
		};
		
		String diag_title;
		if ( curr_name.trim().length() == 0 ) {
			diag_title = Str.get("gui.prj_new_props");
		}
		else {
			diag_title = Str.get("gui.1_prj_props", curr_name);
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
						msg = Str.get("gui.spaces_not_allowed");
						//f_name.setBorder(ProjectDialog.redBorder);
					}
					else if ( prj_name.length() == 0 )
					{
						msg = Str.get("gui.code_expected"); 
					}
					else if ( !curr_name.equalsIgnoreCase(prj_name)
					&& workspace.existsProjectModel(prj_name) )
					{
						msg = Str.get("gui.code_already_used"); 
					}
				}
				if ( msg != null )
				{
					// nada
				}
				else if ( f_title.getText().trim().length() == 0 )
				{
					msg = Str.get("gui.tit_expected"); 
				}
				else if ( f_description.getText().trim().length() == 0 )
				{
					msg = Str.get("gui.description_expected"); 
				}
				
				if ( msg == null )
				{
					status.setForeground(Color.gray);
					status.setText(Str.get("gui.msg_ok"));
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
					"Error occured while saving project",
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
				pkgname = "<i>" +Loro.Str.get("html.anonymous")+ "</i>";
			else
				pkgname = "<code><b>" +pkgname+ "</b></code>";
			
			sb.append("<tr bgcolor=\"#FFCCCC\">\n");
			sb.append("<td>\n");
			sb.append(Loro.Str.get("html.package")+ ": " +pkgname+ "\n");
			sb.append("</td>\n");
			sb.append("</tr>\n");
			
			for ( Iterator itt = pkgm.getUnits().iterator(); itt.hasNext(); )
			{
				IProjectUnit unit = (IProjectUnit) itt.next();
				String qname = Util.replace(
					unit.getQualifiedName(), "::", "/"
				);
				String code = unit.getCode();
				String href = qname+ "." +code+ ".html";

				sb.append("<tr>\n");
				sb.append("<td>\n");

				String stereo = loro.util.Util.formatHtml(unit.getStereotype());
				sb.append("&nbsp;&nbsp;<a href=\"" +href+ "\">" +
					"<code>" +stereo+ " " +unit.getName()+ "</code>" + 
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

		String str_missing = Str.get("gui.prj_missing_prop");
		if ( title == null )
			title = str_missing;
		if ( authors == null )
			authors = str_missing;
		if ( version == null )
			version = str_missing;
		if ( description == null )
			description = str_missing;
		
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
					content = Util.replace(content, reps[i], reps[i+1]);
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
				"Error while saving project documentation '" +prjname+ "'\n" +
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
		String loro_projects = Str.get("gui.html_loro_projects");
		StringBuffer sb = new StringBuffer(
			"<html>\n" +
			"<head>\n" +
			"<title>" +loro_projects+ "</title>\n" +
			"</head>\n" +
			"<body>\n"
		);
		
		sb.append("<b>" +loro_projects+ "</b>\n");
		
		sb.append("<table>\n");
		for ( Iterator it = workspace.getAvailableProjects().iterator(); it.hasNext(); )
		{
			String prjname = (String) it.next();
			IProjectModel.IROInfo info = workspace.getProjectModelInfo(prjname);
			String version = info.getVersion();
			String title = info.getTitle();
			if ( version == null ) 
				version = "?";
			if ( title == null ) 
				title = "?";
			
			sb.append("<tr>\n");
			sb.append("<td bgcolor=\"#FFCCCC\">\n");
			sb.append("  <a href=\"" +prjname+ ".prj.html"+ "\">" +prjname+ "</a>\n");
			sb.append("</td>\n");
			sb.append("<td bgcolor=\"#FFCCCC\">\n");
			sb.append("  " +version+ "\n");
			sb.append("</td>\n");
			sb.append("<td bgcolor=\"#FFEEEE\">\n");
			sb.append("  " +title+ "\n");
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
				"Error while saving main index.html.\n" +
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
				"Error while saving project",
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
		f_name.setBorder(createTitledBorder(Str.get("gui.prj_code")));
		f_title.setBorder(createTitledBorder(Str.get("gui.prj_tit")));
		
		JPanel panel_target = new JPanel(new GridLayout(0, 1));
		panel_target.setBorder(createTitledBorder(Str.get("gui.prj_export_target")));
		
		final JPanel panel_extension = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JRadioButton radio_extension = new JRadioButton(Str.get("gui.prj_export_to_project"));
		radio_extension.setMnemonic(KeyEvent.VK_P);
		radio_extension.setSelected(true);
		panel_extension.add(radio_extension);
		panel_extension.add(f_extension);
		JButton choose_ext = new JButton("...");
		panel_extension.add(choose_ext);
		
		panel_target.add(panel_extension);

		final JPanel panel_directory = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JRadioButton radio_directory = new JRadioButton(Str.get("gui.prj_export_to_directory"));
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
		panel_include.setBorder(createTitledBorder(Str.get("gui.prj_export_include")));
		
		final JCheckBox check_source = new JCheckBox(Str.get("gui.prj_export_include_src"), true);
		check_source.setMnemonic(KeyEvent.VK_F);
		panel_include.add(check_source);
		
		final JCheckBox check_compiled = new JCheckBox(Str.get("gui.prj_export_include_compiled"), true);
		check_compiled.setMnemonic(KeyEvent.VK_C);
		check_compiled.setDisplayedMnemonicIndex(7);
		panel_include.add(check_compiled);

		final JCheckBox check_html = new JCheckBox(Str.get("gui.prj_export_include_doc"), true);
		check_html.setMnemonic(KeyEvent.VK_M);
		panel_include.add(check_html);

        Object[] array = {
			f_name,
			f_title,
			panel_target,
			panel_include,
			status
		};
		
		String diag_title = Str.get("gui.1_prj_export_tit", model.getInfo().getName()); 
        final ProjectDialog form = new ProjectDialog(frame, diag_title, array)
		{
			public boolean dataOk()
			{
				String msg = null;
				if ( radio_directory.isSelected() )
				{
					String dir = f_directory.getText().trim();
					if ( dir.length() == 0 ) {
						msg = Str.get("gui.prj_export_missing_dir"); 
					}
					else {
						File file = new File(dir);
						if ( !file.isAbsolute() ) {
							msg = Str.get("gui.prj_export_missing_abs_dir");
						}
					}
				}
				else if ( radio_extension.isSelected() )
				{
					String ext = f_extension.getText().trim();
					if ( ext.length() == 0 ) {
						msg = Str.get("gui.prj_export_missing_prj_name");
					}
				}
				
				if ( msg == null ) {
					if ( !check_source.isSelected() && !check_compiled.isSelected()
					&&   !check_html.isSelected() ) {
						msg = Str.get("gui.prj_export_nothing");
					}
				}
				
				if ( msg == null )
				{
					status.setForeground(Color.gray);
					status.setText(Str.get("gui.msg_ok"));
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
					Str.get("gui.prj_export_to_directory")
				);
				if ( dir != null ) {
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
					Str.get("gui.prj_export_to_project"),
					JFileChooser.FILES_ONLY
				);
				if ( ext != null ) {
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
				&&   !confirm(frame, Str.get("gui.1_prj_export_conf_dir_existing",dest) ) )
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
				&&   !confirm(frame, Str.get("gui.1_prj_export_conf_file_existing",dest) ) )
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
				
				message(focusedProject.getFrame(), Str.get("gui.1_prj_export_ok")); 
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				JOptionPane.showOptionDialog(
					focusedProject.getFrame(),
					ex.getMessage(),
					"Error exporting project",
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
					Str.get("gui.msg_no_src_code"),
					"",
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
				editor.getMessageArea().setText(Str.get("gui.unit_saved"));
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
				"Error saving unit",
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
	 * Ejecución de _compileDemo(editor) en un hilo.
	 */
	private static Thread compileDemo(final UEditor editor)
	{
		Runnable run = new Runnable() 
		{
			public void run() 
			{
				_compileDemo(editor);
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
	private static boolean _compileProjectDemo()
	{
		MessageArea prj_msg = focusedProject.getMessageArea();		
		IProjectModel prjm = focusedProject.getModel();
		String name = prjm.getInfo().getName();
		String compiling_msg = Str.get("gui.1_msg_compiling_demo"); 
		
		boolean ok = false;
		UEditor editor = (UEditor) demoEditors.get(name);
		if ( editor != null )
		{
			editor.getMessageArea().clear();
			editor.getMessageArea().println(compiling_msg);
			prj_msg.print("  " +Str.get("gui.msg_demo")+ ": ");
			ok = _compileDemo(editor);
		}
		else
		{
			String src = focusedProject.getModel().getInfo().getDemoScript();
			if ( src == null || src.trim().length() == 0 )
				return true;  // inmediatamente, sin mensajes.
			
			prj_msg.print("  " +Str.get("gui.msg_demo")+ ": ");
			int[] offsets = new int[2];
			try
			{
				_compileDemoSource(src, offsets);
				ok = true;
			}
			catch(CompilacionException ce)
			{
				// aquí necesitamos el editor para mostrar el problema:
				editor = createDemoEditor(prjm);
				String msg = _handleDemoCompilacionException(editor, ce, offsets);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				JOptionPane.showOptionDialog(
					null, //focusedProject.getFrame(),
					ex.getMessage(),
					"Error compiling script",
					JOptionPane.DEFAULT_OPTION,
					JOptionPane.ERROR_MESSAGE,
					null,
					null,
					null
				);
			}
		}
		prj_msg.print(ok ? Str.get("gui.msg_compiling_ok") : Str.get("gui.msg_compiling_error"));
		
		return ok;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Compila el código demo asociado al editor dado..
	 *
	 * @return true si todo bien.
	 */
	private static boolean _compileDemo(UEditor editor)
	{
		DemoEditorListener el = (DemoEditorListener) editor.getEditorListener();
		el.save();

		MessageArea msgArea = editor.getMessageArea();
		msgArea.clear();
		msgArea.println(Str.get("gui.1_msg_compiling_demo", ""));
		
		String src = editor.getText();
		int[] offsets = new int[2];
		try
		{
			_compileDemoSource(src, offsets);
			msgArea.print(" " +Str.get("gui.msg_compiling_ok"));
			return true;
		}
		catch(CompilacionException ce)
		{
			_handleDemoCompilacionException(editor, ce, offsets);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			JOptionPane.showOptionDialog(
				null,
				ex.getMessage(),
				"Error compiling unit",
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
	 * Compila el código demo dado.
	 */
	private static void _compileDemoSource(String src, int[] offset)
	throws CompilacionException
	{
		offset[0] = 0;
		IInterprete ii = Loro.crearInterprete(null, null, true, null);
		
		List cmds = new ArrayList();
		_createCommands(src, cmds);
		for ( Iterator it = cmds.iterator(); it.hasNext(); )
		{
			SourceSegment cmdsrc = (SourceSegment) it.next();
			offset[0] = cmdsrc.offset;
			offset[1] = cmdsrc.line;
			String cmd = cmdsrc.src;
			ii.compilar(cmd);
		}
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Despacha un error de compilación de demo.
	 * Retorna el mensaje de error.
	 *
	 * @return true si todo bien.
	 */
	private static String _handleDemoCompilacionException(
		UEditor editor, CompilacionException ce, int[] offsets
	)
	{
		MessageArea msgArea = editor.getMessageArea();
		Rango rango = ce.obtRango();
		String msg = "[" +(offsets[1] + rango.obtIniLin())+ "," +rango.obtIniCol()+ "]" +
			" " +ce.getMessage()
		;
		msgArea.println(msg);
		editor.select(offsets[0] + rango.obtPosIni(), offsets[0] + rango.obtPosFin());
		editor.display();
		return msg;
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
					editor.getMessageArea().print(Str.get("gui.1_msg_compiling_unit", ""));
				}
				MessageArea prj_msg = focusedProject.getMessageArea();
				prj_msg.clear();
				prj_msg.print(Str.get("gui.1_msg_compiling_unit", unit.getStereotypedName()));
		
				try
				{
					workspace.compileUnit(unit);
					prj_msg.print(" " +Str.get("gui.msg_compiling_ok"));
					if ( editor != null )
					{
						editor.getMessageArea().print(" " +Str.get("gui.msg_compiling_ok"));
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
							"Error compiling",
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
						"Error compiling unit",
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
				_compileProject();
			}
		};
		new Thread(run).start();
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para compilar el proyecto enfocado.
	 */
	private static void _compileProject()
	{
		MessageArea prj_msg = focusedProject.getMessageArea();
		prj_msg.clear();
		prj_msg.println(Str.get("gui.1_msg_compiling_prj", ""));
		IProjectModel model = focusedProject.getModel();
		try
		{
			prj_msg.print("  " +Str.get("gui.1_msg_compiling_prj_units")+ ": ");
			workspace.compileProjectModel(model);
			prj_msg.println(Str.get("gui.msg_compiling_ok"));
			if ( browser != null ) // simplemente refresque.
				browser.refresh();

			File doc2_dir = null;
			if ( docInProjectDirectory )
				doc2_dir = new File(prs_dir +File.separator+ model.getInfo().getName());
			_saveProjectDoc(model, doc2_dir);
			
			// ahora compile el demo: (allí se abre el editor en caso de error.)
			boolean demo_ok = _compileProjectDemo();
			if ( demo_ok )
				message(focusedProject.getFrame(), Str.get("gui.msg_compiling_ok"));
		}
		catch(UnitCompilationException ex)
		{
			CompilacionException ce = ex.getCompilacionException();
			Rango rango = ce.obtRango();

			IProjectUnit unit = ex.getUnit();
			
			String res = "[" +rango.obtIniLin()+ "," +rango.obtIniCol()+ "]" +
				" " +ex.getMessage()
			;

			prj_msg.println(Str.get("gui.msg_compiling_error"));
			prj_msg.println(unit.getStereotypedName()+ ": " +res);
			
			UEditor editor = (UEditor) unit.getUserObject();
			
			if ( editor == null )
			{
				// aquí necesitamos el editor para mostrar el problema:
				editor = editUnit(unit, null);
			}
			
			if ( editor != null )
			{
				if ( rango != null )
					editor.select(rango.obtPosIni(), rango.obtPosFin());
				editor.display();
				editor.getMessageArea().setText(res);
			}
			else
			{
				// pero no debería suceder.
				JOptionPane.showOptionDialog(
					null,
					res,
					"Error compiling",
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
				"Error compiling project",
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
				prj_msg.print(Str.get("gui.msg_test_taking_algs")+ "\n");
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
							cmds.add(new SourceSegment(0, 0,
									"// " +Str.get("gui.1_msg_testing_alg", tested_alg)+ "\n"+
									cmd, false
								)
							);
						}
					}
				}
				if ( cmds.size() > 0 )
				{
					prj_msg.print(Str.get("gui.1_msg_test_launching_window")+ "\n");
					String prj_name = focusedProject.getModel().getInfo().getName();
					workspace.executeCommands(
						Str.get("gui.1_msg_testing_prj", prj_name),
						Str.get("gui.msg_call_testing_algs")+ "\n",
						Str.get("gui.msg_call_tests_ok"),      // byeOK
						Str.get("gui.msg_call_tests_error"),   // byeErr
						cmds,
						false,    // newSymTab
						false     // ejecutorpp
					);
				}
				else if ( total_algs > 0 )
				{
					String msg = Str.get("gui.msg_no_tests_available");
					prj_msg.print(msg+ "\n");
					message(focusedProject.getFrame(), msg);
				}
				else // total_algs == 0 
				{
					String msg = Str.get("gui.msg_prj_with_no_algs")+ "\n";
						
					prj_msg.print(msg+ "\n");
					message(focusedProject.getFrame(), msg);
				}
			}
		};
		new Thread(run).start();
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Abre el editor del guión de demostración del proyecto enfocado.
	 */
	public static UEditor editProjectDemo()
	{
		IProjectModel prjm = focusedProject.getModel();
		String name = prjm.getInfo().getName();
		UEditor editor = (UEditor) demoEditors.get(name);
		if ( editor == null )
			editor = createDemoEditor(prjm);
		editor.display();
		return editor;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Crea el editor para el guión de demostración del proyecto dado.
	 */
	private static UEditor createDemoEditor(IProjectModel prjm)
	{
		IProjectModel.IInfo info = prjm.getInfo();
		String name = info.getName();
		String src = info.getDemoScript();
		if ( src == null )
			src = "";
		
		boolean modifiable = prjm.getControlInfo().isModifiable();
		UEditor editor = new UEditor(
			Str.get("gui.1_edit_demo_tit", name), 
			modifiable, true, false, true,
			Preferencias.DEMO_RECT
		);
		editor.setText(src);
		editor.setCaretPosition(0);
		// el editor-listener puesto después para no recibir .changed():
		editor.setEditorListener(new DemoEditorListener(prjm, editor));
		
		demoEditors.put(name, editor);
		return editor;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Ejecuta el guión de demostración del proyecto enfocado.
	 */
	public static void runProjectDemo(boolean ejecutorpp)
	{
		MessageArea prj_msg = focusedProject.getMessageArea();
		prj_msg.clear();
		IProjectModel model = focusedProject.getModel();
		String src = model.getInfo().getDemoScript();
		if ( src != null && src.trim().length() > 0 )
		{
			String title = Str.get("gui.1_edit_demo_tit", model.getInfo().getName()); 
			prj_msg.print(title+ " ...\n");
			runDemo(src, title, ejecutorpp);
		}
		else
			prj_msg.print(Str.get("gui.no_demo_to_run"));
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Ejecuta un guión de demostración.
	 */
	public static void runDemo(final String src, final String title, final boolean ejecutorpp)
	{
		Runnable run = new Runnable() 
		{
			public void run() 
			{
				List cmds = new ArrayList();
				_createCommands(src, cmds);
				workspace.executeCommands(
					title,
					null,
					null,
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
	 * Genera la lista de segmentos definidos en el guión dado.
	 * Cada línea se descompone en dos partes:<br>
	 *  before  "//." after <br>
	 * Se toma el separador final "//."  Si no está, la línea se toma
	 * completamente y no se hace ningún manejo especial.
	 * En otro caso, la parte before se toma y la parte after se lee
	 * como un indicador especial para el manejo de segmentos:
	 * <ul>
	 *  <li> //.$    para finalizar segmento fuente
	 *  <li> //.p    para finalizar segmento fuente y además provocar "pausa"
	 *  <li> En otro caso, la parte se ignora.
	 * </ul>
	 */
	private static void _createCommands(String src, List segments)
	{
		// separador en línea para manejo especial:
		// (por lo pronto, se define aquí localmente)
		String prefix_sep = "//.";
		
		int src_len = src.length();
		StringBuffer segment = null;
		boolean firstLine = true;

		StringBuffer linebuf = new StringBuffer();
		int pos_ini = 0;
		int line_ini = 0;
		int lineno = 0;
		for ( int pos = 0; pos < src_len; pos++ )
		{
			// get next line:
			linebuf.setLength(0);
			for (; pos < src_len; pos++ )
			{
				if ( src.charAt(pos) == '\n' )
					break;
				linebuf.append(src.charAt(pos));
			}
			String line = linebuf.toString();
			lineno++;

			String special = "";
			int idx = line.lastIndexOf(prefix_sep);
			if ( idx >= 0 )
			{
				// Tome después-de y antes-de prefix_sep en special y line:
				special = line.substring(idx + prefix_sep.length());
				line = line.substring(0, idx);
			}
			String linetrim = line.trim();

			if ( segment == null ) // new  segment?
				segment = new StringBuffer(line);
			else
				segment.append("\n" +line);
			
			boolean pause = special.startsWith("p");
			boolean end_segment = pause || special.startsWith("$");
			if ( end_segment ) 
			{
				segments.add(new SourceSegment(pos_ini, line_ini, segment.toString(), pause));
				segment = null;
				pos_ini = pos + 1;
				line_ini = lineno;
			}
		}
	
		if ( segment != null )
			segments.add(new SourceSegment(pos_ini, line_ini, segment.toString(), false));
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Un segmento de acciones dentro de un texto fuente.
	 */
	public static class SourceSegment
	{
		/** Posicion de inicio dentro del fuente. */
		public int offset;
		
		/** Línea de inicio dentro del fuente. */
		public int line;
		
		/** Código fuente del propio comando. */
		public String src;
		
		/** Hacer pausa antes de procesar comando?. */
		public boolean pause;
		
		SourceSegment(int offset, int line, String src, boolean pause)
		{
			this.line = line;
			this.offset = offset;
			this.src = src;
			this.pause = pause;
		}
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
			message(focusedProject.getFrame(), Str.get("gui.msg_alg_not_compiled"));
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
		prj_msg.print("'" +tested_alg.getQualifiedName()+ "' ...\n");
		IUnidad.IAlgoritmo tested_u = (IUnidad.IAlgoritmo) tested_alg.getIUnidad();
		if ( tested_u == null )
		{
			String msg = "..." +Str.get("gui.msg_alg_not_compiled");
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
			String msg = "..." +Str.get("gui.1_msg_tester_alg_not_found", tester_alg_name); 
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
			cmds.add(new SourceSegment(0, 0, "// " +Str.get("gui.1_msg_testing_alg", tested_alg.getIUnidad())+ "\n"+cmd, false));

			workspace.executeCommands(
				Str.get("gui.1_msg_testing_alg", tested_alg.getIUnidad()),
				Str.get("gui.msg_call_testing_algs")+ "\n",
				Str.get("gui.msg_call_tests_ok"),      // byeOK
				Str.get("gui.msg_call_tests_error"),   // byeErr
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
					Str.get("gui.conf_quit"),
					"",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.INFORMATION_MESSAGE
				);
				if ( sel != 0 )
					return;
				
				Preferencias.ponPreferencia(Preferencias.PRJ_RECENT, 
					focusedProject.getModel().getInfo().getName()
				);
				Rectangle rect = new Rectangle(frame.getLocationOnScreen(), frame.getSize());
				Preferencias.ponRectangulo(Preferencias.PRJ_RECT, rect);
			}
			
			Preferencias.store();
			Configuracion.store();
			Loro.cerrar();
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
	private static void _updateWindowMenu() {
		Collection prjnames = openFrames.keySet();
		for ( Iterator it = openFrames.values().iterator(); it.hasNext(); ) {
			ProjectFrame frame = (ProjectFrame) it.next();
			frame.windowMenu.removeAll();
			for ( Iterator itt = prjnames.iterator(); itt.hasNext(); ) {
				String prjname = (String) itt.next();
				String actionCmd = "p:" +prjname;
				if ( prjname.trim().length() == 0  ) {
					prjname = UNTITLED_PROJECT;
				}
				JMenuItem mi = new JMenuItem(Str.get("gui.1_msg_project", prjname));
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
	 * Abre un diálogo para abrir un proyecto del espacio de trabajo.
	 */
	public static void openProject()
	{
		final String prjname = _openProjectDialog(focusedProject.getFrame());
		if ( prjname != null )
		{
			progressRun(focusedProject.getFrame(), 
				Str.get("gui.1_msg_opening_project", prjname), 
				new Runnable()  {
					public void run()  {
						_openProject(prjname);
					}
				}
			);
			focusedProject.getFrame().toFront();
		}
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
		focusedProject.getMessageArea().println(Str.get("gui.1_msg_prj_open", prjname));
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
		try {
			in = new DataInputStream(url.openStream());
		}
		catch(IOException ex)
		{
			JOptionPane.showOptionDialog(
				focusedProject.getFrame(),
				Str.get("gui.cannot_read_file")+ "\n"
				+ex.getClass()+ ":\n"
				+"   " +ex.getMessage(),
				Str.get("gui.cannot_read_file"),
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
			prj_msg.println(Str.get("gui.msg_importing_prj"));
			
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
			
			prj_msg.println(" " +Str.get("gui.msg_done"));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			JOptionPane.showOptionDialog(
				focusedProject.getFrame(),
				ex.getMessage(),
				"Error occured while trying to install project",
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
		
		
		JCheckBox chk_open = new JCheckBox(
			Str.get("gui.msg_open_prj_after_install"), 
			null, true
		); 
		
		JScrollPane sp = new JScrollPane(cb_prjs);
		f_name.setBorder(createTitledBorder(Str.get("gui.msg_install_prj_with_name")));
		f_name.setText((String) cb_prjs.getSelectedValue());

		final JPanel panel_inc = new JPanel();
		panel_inc.setLayout(new BoxLayout(panel_inc, BoxLayout.Y_AXIS));
		panel_inc.setBorder(createTitledBorder(""));
		final JRadioButton radio_inc = new JRadioButton(Str.get("gui.msg_install_builtin_prj")+ ":");
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
		final JRadioButton radio_lar = new JRadioButton(Str.get("gui.msg_install_external_prj")+ ":");
		
		radio_lar.setAlignmentX(0f);
		panel_lar.add(radio_lar);
		JLabel l_header_lar = new JLabel(Str.get("gui.msg_install_prompt_prj"));
		l_header_lar.setForeground(Color.gray);
		l_header_lar.setAlignmentX(0f);
		panel_lar.add(l_header_lar);
		final JPanel panel_lar2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel_lar2.setAlignmentX(0f);
        final JTextField f_lar = new JTextField(30);
		f_lar.setText(Preferencias.obtPreferencia(Preferencias.PRJ_EXTERN_LAST));
		panel_lar2.add(f_lar);
		radio_lar.setMnemonic(KeyEvent.VK_E);
		
		String[] strs;
		
		strs = Str.get("but.local_file").split("\\|", 2);
		JButton choose_lar = new JButton(strs[0]);
		choose_lar.setToolTipText(strs[1]);
		choose_lar.setMnemonic(KeyEvent.VK_L);
		panel_lar2.add(choose_lar);
		
		strs = Str.get("but.browse").split("\\|", 2);
		JButton browse_for_lar = new JButton(strs[0]);
		browse_for_lar.setToolTipText(strs[1]);
		browse_for_lar.setMnemonic(KeyEvent.VK_N);
		panel_lar2.add(browse_for_lar);
		
		panel_lar.add(panel_lar2);

		final DocumentListener docListener = new DocumentListener() {
			void common() {
				String name = "";
				f_name.setText("");
				//radio_lar.setSelected(true);
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

		JLabel l_header = new JLabel(Str.get("gui.msg_install_help_prj"));
		l_header.setForeground(Color.gray);

		
        Object[] array = {
			l_header,
			panel_inc,
			panel_lar,
			f_name,
			chk_open,
			status
		};
		
        final ProjectDialog form = new ProjectDialog(focusedProject.getFrame(), Str.get("gui.tit_install_prj"), array)
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
						msg = Str.get("gui.install_missing_prj_name"); 
						ret = false;
					}
				}
				else if ( radio_lar.isSelected() )
				{
					String lar = f_lar.getText().trim();
					if ( lar.length() == 0 )
					{
						msg = Str.get("gui.install_missing_file"); 
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
							msg = Str.get("gui.install_malformed_url");
							ret = false;
						}
					}
					else
					{
						// Se asume como archivo local.
						
						File file = new File(lar);
						if ( !file.exists() )
						{
							msg = Str.get("gui.install_file_not_found");
							ret = false;
						}
					}
				}
				
				if ( msg == null )
				{
					if ( prjname.trim().length() == 0 )
					{
						msg = Str.get("gui.install_missing_prj_name");
						ret = false;
					}
					else if ( alreadyOpen(prjname) )
					{
						msg = Str.get("gui.1_install_prj_open", prjname);
						ret = false;
					}
					else if ( new File(new File(prs_dir), prjname).exists() )
					{
						msg = Str.get("gui.1_install_prj_overwrite", prjname);
						setAcceptText(Str.get("gui.install_overwrite"));
					}
					else
					{
						setAcceptText(Str.get("gui.install_ok"));
					}
				}
				
				status.setForeground(ret ? Color.gray : Color.red);
				if ( msg == null )
				{
					msg = Str.get("gui.install_ok");
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
					Str.get("gui.tit_install_prj"),
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
						"Error installing project",
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
				msg = Str.get("gui.2_install_completed_compile", from_name, to_name)+ "\n\n";
				compile_project = true;
			}
			else
			{
				msg = Str.get("gui.2_install_completed", from_name, to_name)+ "\n\n";
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
		f_name.setBorder(createTitledBorder(Str.get("gui.install_complete_link")));
		
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
		prj_browser.setBorder(createTitledBorder(Str.get("gui.install_choose_link")));
		
        Object[] array = {
			prj_browser,
			f_name,
			status
		};
		
        final ProjectDialog form = new ProjectDialog(frame, Str.get("gui.install_choose_link"), array)
		{
			public boolean dataOk()
			{
				String msg = null;
				boolean ret = true;
				String lar = f_name.getText();
				if ( lar.length() == 0 )
				{
					msg = Str.get("gui.install_choose_link");
					ret = false;
				}
				status.setForeground(ret ? Color.gray : Color.red);
				if ( msg == null )
					msg = Str.get("gui.msg_ok");

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
			Str.get("gui.import_choose_src_file"), 
			JFileChooser.FILES_ONLY
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
				Str.get("gui.import_src_file_compile_error")
				+"\n"
				+"[" +r.obtIniLin()+ "," +r.obtIniCol()+ "] " +ex.getMessage()
				,
				"",
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
				"Error importing source",
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
		f_selected.setBorder(createTitledBorder(Str.get("gui.prj_open")));
		final JLabel status = new JLabel();
		status.setFont(status.getFont().deriveFont(Font.ITALIC));
		
		JScrollPane sp = new JScrollPane(cb_prjs);
		sp.setBorder(createTitledBorder(Str.get("gui.prj_in_workspace")));
		

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

		JLabel l_header = new JLabel(Str.get("gui.msg_open_help_prj"));
		l_header.setForeground(Color.gray);

        Object[] array = {
			l_header,
			sp,
			f_selected,
			status
		};
		
        final ProjectDialog form = new ProjectDialog(frame, Str.get("gui.prj_open"), array)
		{
			public boolean dataOk()
			{
				String prjname = f_selected.getText();
				String msg = null;
				boolean ret = true;
				if ( prjname.length() == 0 )
				{
					msg = Str.get("gui.open_missing_prj_name");
					ret = false;
				}
				if ( alreadyOpen(prjname) )
				{
					msg = Str.get("gui.open_prj_already_open");
					ret = false;
				}
				status.setForeground(ret ? Color.gray : Color.red);
				if ( msg == null )
				{
					msg = Str.get("gui.msg_ok");
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
		progressRun(focusedProject.getFrame(), Str.get("gui.prj_reloading"), new Runnable() 
		{	
			public void run() 
			{
				MessageArea prj_msg = focusedProject.getMessageArea();
				prj_msg.clear();
				prj_msg.print(Str.get("gui.prj_reloading")+ "...");
				IProjectModel model = workspace.getProjectModel(
					focusedProject.getModel().getInfo().getName()
				);
				focusedProject.setModel(model);
				focusedProject.getDiagram().repaint();
				prj_msg.print(" " +Str.get("gui.msg_done"));
			}
		});
		
		//new Thread(doIt).start(); 
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

			String[] strs = Str.get("gui.menu_window").split("\\|", 2);
			windowMenu = new JMenu(strs[0]);
			windowMenu.setMnemonic(strs[1].charAt(0));
			JMenuBar mb = createMenuBar(windowMenu); 
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
				editor.getMessageArea().print("\n" +Str.get("gui.msg_starting_edit"));
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
			editor.getMessageArea().setText(Str.get("gui.msg_edit_reloaded"));
			editor.setCaretPosition(0);
		}
		
		/////////////////////////////////////////////////////////////////
		public void closeWindow()   // PENDING
		{
			UEditor editor = (UEditor) unit.getUserObject();
			editor.getFrame().setVisible(false);
		}
	
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Atiende acciones sobre un editor de guión de demostración.
	 */
	private static class DemoEditorListener implements UEditorListener
	{
		IProjectModel prjm;
		UEditor editor;
		String saved;
		
		DemoEditorListener(IProjectModel prjm, UEditor editor)
		{
			this.prjm = prjm;
			this.editor = editor;
			saved = editor.getText();
		}
		
		public void changed() 
		{
			if ( editor.isSaved() )
			{
				// primer cambio.
				editor.getMessageArea().print("\n" +Str.get("gui.msg_starting_edit"));
			}
			editor.setSaved(false);
		}
		public void save() 
		{
			IProjectModel.IInfo info = prjm.getInfo();
			info.setDemoScript(editor.getText());
			workspace.saveDemoScript(prjm);
			editor.setSaved(true);
			editor.getMessageArea().setText(Str.get("gui.msg_demo_saved"));
			saved = editor.getText();
		}
		public void closeWindow() 
		{
			editor.getFrame().setVisible(false);
		}
		public void compile() 
		{
			if ( !editor.isSaved() )
				save();
			compileDemo(editor);
		}
		public void execute(boolean trace) 
		{
			if ( !editor.isSaved() )
				save();
			if ( saved.trim().length() > 0 )
			{
				String title = Str.get("gui.msg_demo_run");
				editor.getMessageArea().setText(title+ " ...\n");
				runDemo(saved, title, trace);
			}
			else
				editor.getMessageArea().setText(Str.get("gui.no_demo_to_run"));
		}
		public void reload() 
		{
			editor.setText(saved);
			editor.setSaved(true);
			editor.getMessageArea().setText(Str.get("gui.msg_edit_reloaded"));
			editor.setCaretPosition(0);
		}
		public void viewDoc() {}
	}
	
	
	
	/////////////////////////////////////////////////////////////////////
	public static boolean confirm(JFrame frame, String msg)
	{
		// No uso showConfirmDialog porque no encuentro como poner el
		// "No" preenfocado. Así que uso showOptionDialog.
		
		Object[] options = { Str.get("gui.conf_yes"), Str.get("gui.conf_no") };
		java.awt.Toolkit.getDefaultToolkit().beep();
		return 0 == JOptionPane.showOptionDialog(
			frame,
			msg,
			"",
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
			"",
			JOptionPane.WARNING_MESSAGE
		);
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * The version that has been used for quite some time.  
	 * See below for a SwingWorker-based implementation.
	 */
	public static void progressRun__BAK(Window owner, final String msg, final Runnable runnable)
	{
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		progressBar.setString(msg);
		
		JDialog dialog2;
		if ( owner == null || owner instanceof Frame )
			dialog2 = new JDialog((Frame) owner, msg, true);
		else if ( owner instanceof Dialog )
			dialog2 = new JDialog((Dialog) owner, msg, true);
		else
			throw new Error("Frame or Dialog expected");
		
		final JDialog dialog = dialog2;
		JPanel panel = new JPanel();
		panel.add(progressBar);
		dialog.getContentPane().add(progressBar);

		final Runnable run = new Runnable() {
			public void run() {
				try { 
					runnable.run();
				}
				finally {
					dialog.dispose();
				}
			}
		};
		dialog.pack();
		dialog.setLocationRelativeTo(focusedProject.getFrame());

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Thread(run).start(); 
				dialog.setVisible(true);
			}
		});
	}

	/** An attempt to improve the responsiveness of the gui
	 * by using the SwingWorker class.
	 * UNDER TESTING
	 * Maybe someone out there would be willing to help out!
	 */
	public static void progressRun(Window owner, final String msg, final Runnable runnable)
	{
		final JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		progressBar.setString(msg);
		
		JDialog dialog2;
		if ( owner == null || owner instanceof Frame )
			dialog2 = new JDialog((Frame) owner, msg, true);
		else if ( owner instanceof Dialog )
			dialog2 = new JDialog((Dialog) owner, msg, true);
		else
			throw new Error("Frame or Dialog expected");
		
		final JDialog dialog = dialog2;
		JPanel panel = new JPanel();
		panel.add(progressBar);
		dialog.getContentPane().add(progressBar);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				dialog.pack();
				dialog.setLocationRelativeTo(focusedProject.getFrame());
				dialog.setVisible(true);
			}
		});
		
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				try {
					runnable.run();
				}
				finally {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							dialog.dispose();
						}
					});
				}
				return null;
			}
			public void finished() {
				dialog.dispose();
			}
		};
		worker.start();		
	}
}

