package loroedi.gui;

import loroedi.gui.GUI;
import loroedi.gui.project.model.*;
import loroedi.gui.project.unit.*;

import loroedi.gui.misc.NotImplementedAction;
import loroedi.gui.editor.*;
import loroedi.Util;
import loroedi.Info;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;


/////////////////////////////////////////////////////////
/** 
 * Acciones para proyecto.
 *
 * @author Carlos Rueda
 * @version 2002-07-26
 */
public class Actions
{
	protected Group project_group;
	protected Group develop_group;
	protected Group execution_group;
	protected Group help_group;
	protected Map name_action;

	/////////////////////////////////////////////////////////////////
	public Actions()
	{
		createActions();
	}
	
	/////////////////////////////////////////////////////////////////
	protected void createActions()
	{
		Group g;
		name_action = new HashMap();
		
		project_group = new Group("Proyecto", KeyEvent.VK_P, new ArrayList());
		addAction("new", new NewAction(), project_group.actions);
		addAction("open", new OpenAction(), project_group.actions);
		addAction("install", new InstallAction(), project_group.actions);
		project_group.actions.add(null);
		addAction("project-properties", new ProjectPropertiesAction(), project_group.actions);
//		project_group.actions.add(null);
//		addAction("save", new SaveAction(), project_group.actions);
//		addAction("save-as", new SaveAsAction(), project_group.actions);
		project_group.actions.add(null);
		addAction("reload", new ReloadAction(), project_group.actions);
		project_group.actions.add(null);
		addAction("import", new ImportAction(), project_group.actions);
		addAction("export", new ExportAction(), project_group.actions);
		project_group.actions.add(null);
		addAction("close-project", new CloseProjectAction(), project_group.actions);
		project_group.actions.add(null);
		addAction("quit", new QuitAction(), project_group.actions);
		//addAction("print", new PrintAction());

		
		develop_group = new Group("Desarrollo", KeyEvent.VK_D, new ArrayList());
		g = new Group("Agregar", KeyEvent.VK_A, new ArrayList());
			addAction("new-pkg", new NewPackageAction(), g.actions);
			addAction("new-spec", new NewSpecificationAction(), g.actions);
			addAction("new-algorithm", new NewAlgorithmAction(), g.actions);
			addAction("new-class", new NewClassAction(), g.actions);
		develop_group.actions.add(g);
		develop_group.actions.add(null);
		addAction("compile-project", new CompileProjectAction(), develop_group.actions);
		develop_group.actions.add(null);
		addAction("view-prj-doc", new ViewProjectDocAction(), develop_group.actions);
		develop_group.actions.add(null);
		Group diag_g = new Group("Diagrama", KeyEvent.VK_G, new ArrayList());
		develop_group.actions.add(diag_g);
			g = new Group("Tamaño", KeyEvent.VK_T, new ArrayList());
				addAction("zoom-in", new ZoomInAction(), g.actions);
				addAction("zoom-out", new ZoomOutAction(), g.actions);
				addAction("actual-size", new ActualSizeAction(), g.actions);
				//addAction("print-diagram", new PrintDiagramAction(), g.actions);
		diag_g.actions.add(g);

		execution_group = new Group("Ejecución", KeyEvent.VK_E, new ArrayList());
		addAction("show-ii", new ShowIIAction(), execution_group.actions);
		execution_group.actions.add(null);
		addAction("test-project", new TestProjectAction(), execution_group.actions);
		addAction("show-variables", new ShowSymbolTableAction(), execution_group.actions);
		
		help_group = new Group("Ayuda", KeyEvent.VK_Y, new ArrayList());
		addAction("help", new HelpAction(), help_group.actions);
		help_group.actions.add(null);
		addAction("about", new AboutAction(), help_group.actions);
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Gets an action.
	 */
	public Action getAction(String name)
	{
		return (Action) name_action.get(name);
	}
	/////////////////////////////////////////////////////////////////
	/**
	 * Enables/disables modification actions.
	 */
	public void setModificationEnabled(boolean enabled)
	{
//		getAction("save").setEnabled(enabled);
//		getAction("save-as").setEnabled(enabled);
		getAction("import").setEnabled(enabled);
		getAction("new-pkg").setEnabled(enabled);
		getAction("new-spec").setEnabled(enabled);
		getAction("new-algorithm").setEnabled(enabled);
		getAction("new-class").setEnabled(enabled);
		getAction("compile-project").setEnabled(enabled);
	}
	
	/////////////////////////////////////////////////////////////////
	protected void addAction(String name, Action action, List list)
	{
		name_action.put(name, action);
		list.add(action);
	}
	
	/////////////////////////////////////////////////////////////////
	public Group getProjectGroup()
	{
		return project_group;
	}

	/////////////////////////////////////////////////////////////////
	public Group getExecutionGroup()
	{
		return execution_group;
	}

	/////////////////////////////////////////////////////////////////
	public Group getDevelopmentGroup()
	{
		return develop_group;
	}

	/////////////////////////////////////////////////////////////////
	public Group getHelpGroup()
	{
		return help_group;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene las acciones para un cierto objeto.
	 */
	public List getActions(Object obj)
	{
		List list = new ArrayList();
		if ( obj instanceof IProjectUnit )
		{
			IProjectUnit unit = (IProjectUnit) obj;
			list.add(new EditViewAction());
			list.add(new ViewUnitDocAction());
			list.add(null);
			if ( unit instanceof SpecificationUnit )
			{
				list.add(new NewAlgorithmFromSpecificationAction());
				list.add(new CreateTestForSpecificationAction());
			}
			else if ( unit instanceof AlgorithmUnit )
			{
				list.add(new ExecuteAlgorithmAction());
				list.add(new TestAlgorithmAction());
			}
			else if ( unit instanceof ClassUnit )
			{
				list.add(new NewInstanceAction());
			}
			list.add(null);
			//list.add(new CopyUnitAction());
			//list.add(new MoveUnitAction());
			list.add(new DeleteUnitAction());
		}
		else if ( obj instanceof IPackageModel )
		{
			IPackageModel pkgm = (IPackageModel) obj;
			list.add(new DeletePackageAction());
		}
		return list;
	}
	
	/////////////////////////////////////////////////////////
    public static class Group 
	{
		public String name;
		public int mnemonic;
		public List actions;
		
		Group(String name, int mnemonic, List actions)
		{
			this.name = name;
			this.mnemonic = mnemonic;
			this.actions = actions;
		}
	}
	
	/////////////////////////////////////////////////////////
    class ShowIIAction extends AbstractAction 
	{
		public ShowIIAction() 
		{
			super("Intéprete Interactivo", Util.getIcon("img/ii.gif"));
			putValue(SHORT_DESCRIPTION, "Despliega el Intérprete Interactivo");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.ALT_MASK));
		}

        public void actionPerformed(ActionEvent e) 
		{
			GUI.showII();
		}
	}

	/////////////////////////////////////////////////////////
    class ShowSymbolTableAction extends AbstractAction 
	{
		public ShowSymbolTableAction() 
		{
			super("Variables declaradas");
			putValue(SHORT_DESCRIPTION, "Muestra la tabla de variables declaradas en el entorno");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_V));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK));
		}

        public void actionPerformed(ActionEvent e) 
		{
			GUI.showSymbolTable();
		}
	}

	/////////////////////////////////////////////////////////
    class ZoomInAction extends AbstractAction 
	{
		public ZoomInAction() 
		{
			super("Ampliar");
			putValue(SHORT_DESCRIPTION, "Amplía el tamaño del diagrama");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
		}

        public void actionPerformed(ActionEvent e) 
		{
			GUI.getFocusedProject().getDiagram().zoom(1.2);
		}
	}

	/////////////////////////////////////////////////////////
    class ZoomOutAction extends AbstractAction 
	{
		public ZoomOutAction() 
		{
			super("Reducir");
			putValue(SHORT_DESCRIPTION, "Reduce el tamaño del diagrama");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
		}

        public void actionPerformed(ActionEvent e) 
		{
			GUI.getFocusedProject().getDiagram().zoom(0.8);
		}
	}

	/////////////////////////////////////////////////////////
    class ActualSizeAction extends AbstractAction 
	{
		public ActualSizeAction() 
		{
			super("Normal");
			putValue(SHORT_DESCRIPTION, "Pone el tamaño real del diagrama");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
		}

        public void actionPerformed(ActionEvent e) 
		{
			GUI.getFocusedProject().getDiagram().zoom(-1);
		}
	}

	/////////////////////////////////////////////////////////
	class ViewUnitDocAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public ViewUnitDocAction()
		{
			super("Ver documentación");
			putValue(SHORT_DESCRIPTION, "Muestra la documentación de esta unidad");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F1, KeyEvent.CTRL_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.getFocusedProject().viewDoc();
		}
	}

	/////////////////////////////////////////////////////////
	class EditViewAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public EditViewAction()
		{
			super("Editar/Ver fuente");
			putValue(SHORT_DESCRIPTION, "Edita o visualiza el código fuente de esta unidad");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.getFocusedProject().edit();
		}
	}

	/////////////////////////////////////////////////////////
	class CopyUnitAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public CopyUnitAction()
		{
			super("Copiar");
			putValue(SHORT_DESCRIPTION, "Permite hacer una copia de esta unidad");
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			//GUI.getFocusedProject().copyUnit();
		}
	}

	/////////////////////////////////////////////////////////
	class MoveUnitAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public MoveUnitAction()
		{
			super("Mover");
			putValue(SHORT_DESCRIPTION, "Permite mover esta unidad a otro paquete");
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			//GUI.getFocusedProject().moveUnit();
		}
	}

	/////////////////////////////////////////////////////////
	class DeleteUnitAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public DeleteUnitAction()
		{
			super("Remover");
			putValue(SHORT_DESCRIPTION, "Elimina esta unidad");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.getFocusedProject().deleteUnit();
		}
	}

	/////////////////////////////////////////////////////////
	class DeletePackageAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public DeletePackageAction()
		{
			super("Remover");
			putValue(SHORT_DESCRIPTION, "Elimina este paquete");
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.getFocusedProject().deletePackage();
		}
	}

	/////////////////////////////////////////////////////////
	class NewAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public NewAction()
		{
			super("Nuevo...", Util.getIcon("img/New24.gif"));
			putValue(SHORT_DESCRIPTION, "Crea un nuevo proyecto");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_N));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.newProject();
		}
	}

	/////////////////////////////////////////////////////////
	class OpenAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public OpenAction()
		{
			super("Abrir...", Util.getIcon("img/Open24.gif"));
			putValue(SHORT_DESCRIPTION, "Abre un proyecto existente");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.openProject();
		}
	}

	/////////////////////////////////////////////////////////
	class InstallAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public InstallAction()
		{
			super("Instalar...");
			putValue(SHORT_DESCRIPTION, "Instalación de proyectos incluidos en el sistema");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.installProject();
		}
	}

	/////////////////////////////////////////////////////////
	class ReloadAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public ReloadAction()
		{
			super("Recargar");
			putValue(SHORT_DESCRIPTION, "Recarga este proyecto");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_R));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.reloadProject();
		}
	}

	/////////////////////////////////////////////////////////
	class CloseProjectAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public CloseProjectAction()
		{
			super("Cerrar");
			putValue(SHORT_DESCRIPTION, "Cierra este proyecto");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.closeProject();
		}
	}

	/////////////////////////////////////////////////////////
	class QuitAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public QuitAction()
		{
			super("Salir");
			putValue(SHORT_DESCRIPTION, "Salir del entorno integrado");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_S));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.quit();
		}
	}

	/////////////////////////////////////////////////////////
	class PrintDiagramAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public PrintDiagramAction()
		{
			super("Imprimir");
			putValue(SHORT_DESCRIPTION, "Imprime el diagrama");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("PRINT DIAGRAM");
		}
	}

	/////////////////////////////////////////////////////////
	class ImportAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public ImportAction()
		{
			super("Importar...");
			putValue(SHORT_DESCRIPTION, "Importar...");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_M));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.importFiles();
		}
	}

	/////////////////////////////////////////////////////////
	class ExportAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public ExportAction()
		{
			super("Exportar...");
			putValue(SHORT_DESCRIPTION, "Exporta el proyecto");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.exportProject();
		}
	}
	
	/////////////////////////////////////////////////////////
	class ProjectPropertiesAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public ProjectPropertiesAction()
		{
			super("Propiedades");
			putValue(SHORT_DESCRIPTION, "Muestra/edita las propiedades del proyecto");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.editProperties();
		}
	}

	/////////////////////////////////////////////////////////
	class SaveAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public SaveAction()
		{
			super("Guardar");
			putValue(SHORT_DESCRIPTION, "Guarda el estado actual de este proyecto");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_G));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.saveProject();
		}
	}

	/////////////////////////////////////////////////////////
	class SaveAsAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public SaveAsAction()
		{
			super("Guardar como...");
			putValue(SHORT_DESCRIPTION, "Guarda este proyecti con otro nombre");
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			System.out.println("SAVE AS");
		}
	}

	/////////////////////////////////////////////////////////
	class NewPackageAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public NewPackageAction()
		{
			super("Paquete");
			putValue(SHORT_DESCRIPTION, "Crea un nuevo paquete");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_P));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.getFocusedProject().createPackage();
		}
	}

	/////////////////////////////////////////////////////////
	class NewSpecificationAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public NewSpecificationAction()
		{
			super("Especificación");
			putValue(SHORT_DESCRIPTION, "Crea una nueva especificación");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.getFocusedProject().createSpecification();
		}
	}

	/////////////////////////////////////////////////////////
	class NewAlgorithmAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public NewAlgorithmAction()
		{
			super("Algoritmo");
			putValue(SHORT_DESCRIPTION, "Crea un nuevo algoritmo");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_A));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.getFocusedProject().createAlgorithm();
		}
	}
	
	/////////////////////////////////////////////////////////
	class NewAlgorithmFromSpecificationAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public NewAlgorithmFromSpecificationAction()
		{
			super("Crear algoritmo");
			putValue(SHORT_DESCRIPTION, "Crea un nuevo algoritmo para esta especificación");
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.getFocusedProject().createAlgorithmFromSpecification();
		}
	}
	
	/////////////////////////////////////////////////////////
	class CreateTestForSpecificationAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public CreateTestForSpecificationAction()
		{
			super("Crear esquema para pruebas");
			putValue(SHORT_DESCRIPTION, 
				"Crea un probador de algoritmos para esta especificación"
			);
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.getFocusedProject().createTestForSpecification();
		}
	}
	
	/////////////////////////////////////////////////////////
	class ExecuteAlgorithmAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public ExecuteAlgorithmAction()
		{
			super("Ejecutar");
			putValue(SHORT_DESCRIPTION, "Ejecuta este algoritmo");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F9, KeyEvent.CTRL_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.getFocusedProject().executeAlgorithm();
		}
	}
	
	/////////////////////////////////////////////////////////
	class TestAlgorithmAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public TestAlgorithmAction()
		{
			super("Probar");
			putValue(SHORT_DESCRIPTION, "Hace pruebas a este algoritmo");
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.getFocusedProject().testAlgorithm();
		}
	}

	/////////////////////////////////////////////////////////
	class NewInstanceAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public NewInstanceAction()
		{
			super("Crear instancia (no implementado aún)");
			putValue(SHORT_DESCRIPTION, "Crea una instancia de esta clase");
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			//GUI.getFocusedProject().newInstance();
		}
	}

	/////////////////////////////////////////////////////////
	class NewClassAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public NewClassAction()
		{
			super("Clase");
			putValue(SHORT_DESCRIPTION, "Crea una nueva clase");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.getFocusedProject().createClass();
		}
	}

	/////////////////////////////////////////////////////////
	class CompileProjectAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public CompileProjectAction()
		{
			super("Compilar proyecto", Util.getIcon("img/compile.gif"));
			putValue(SHORT_DESCRIPTION, "Compila este proyecto en su totalidad");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.compileProject();
		}
	}

	/////////////////////////////////////////////////////////
	class TestProjectAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public TestProjectAction()
		{
			super("Probar proyecto", Util.getIcon("img/compile.gif"));
			putValue(SHORT_DESCRIPTION, "Hace pruebas a los algoritmos de este proyecto");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.testProject();
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	class ViewProjectDocAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			GUI.showProjectDocumentation();
		}

		/////////////////////////////////////////////////////////
		public ViewProjectDocAction()
		{
			super("Documentación", Util.getIcon("img/api.gif"));
			putValue(SHORT_DESCRIPTION, "Visualiza la documentación de este proyecto");
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_D));
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK));
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	class HelpAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			loroedi.help.HelpManager.displayHelp();
		}

		/////////////////////////////////////////////////////////
		public HelpAction()
		{
			super("Ayuda Loro", Util.getIcon("img/Help24.gif"));
			putValue(SHORT_DESCRIPTION, "Información general sobre el sistema");
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		}
	}

	/////////////////////////////////////////////////////////
	/**
	 */
	class AboutAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		/**
		 * Ejecuta esta accion.
		 */
		public void actionPerformed(ActionEvent e)
		{
			String about = 
				Info.obtNombre()+ "   " + "Versión " +Info.obtVersion()+ "\n" +
				"Un Sistema de Programación Didáctico\n"+
				" \n" +
				"http://loro.sf.net\n" +
				" \n"+
				"Este programa es software libre y puede ser\n"+
				"redistribuirdo si se desea. Se ofrece con la\n"+
				"esperanza que sea útil, pero sin ningún tipo\n"+
				"de garantía. Por favor, lea la licencia de uso.\n"+
				" \n" +
				"Copyright© 1999-2002 Carlos Rueda\n" +
				"Universidad Autónoma de Manizales\n" +
				"Manizales - Colombia\n" +
				" \n"
			;
			
			javax.swing.JOptionPane.showMessageDialog(
				null,
				about,
				"A propósito de...",
				javax.swing.JOptionPane.INFORMATION_MESSAGE,
				Util.getIcon("img/splash.gif")
			);
		}

		/////////////////////////////////////////////////////////
		public AboutAction()
		{
			super("A propósito de ...");
			putValue(SHORT_DESCRIPTION, "Muestra identificación y versión del sistema");
		}
	}

}
