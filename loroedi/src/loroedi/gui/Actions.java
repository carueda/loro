package loroedi.gui;

import loroedi.gui.GUI;
import loroedi.gui.project.model.*;
import loroedi.gui.project.unit.*;

import loroedi.gui.misc.NotImplementedAction;
import loroedi.gui.editor.*;
import loroedi.Util;
import loroedi.Info;
import loroedi.Info.Str;

import loro.Loro;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/** 
 * Actions.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class Actions {
	protected Group project_group;
	protected Group develop_group;
	protected Group execution_group;
	protected Group help_group;
	protected Map name_action;

	public Actions() {
		createActions();
	}
	
	protected void createActions() {
		Group g;
		name_action = new HashMap();
		String[] strs;

		strs = Str.get("gui.menu_project").split("\\|", 2);
		project_group = new Group(strs[0], strs[1], new ArrayList());
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

		
		strs = Str.get("gui.menu_dev").split("\\|", 2);
		develop_group = new Group(strs[0], strs[1], new ArrayList());
		strs = Str.get("gui.menu_add").split("\\|", 2);
		g = new Group(strs[0], strs[1], new ArrayList());
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
		strs = Str.get("gui.menu_diagram").split("\\|", 2);
		Group diag_g = new Group(strs[0], strs[1], new ArrayList());
		//addAction("print-diagram", new PrintDiagramAction(), diag_g.actions);
		develop_group.actions.add(diag_g);
			strs = Str.get("gui.menu_diagram_size").split("\\|", 2);
			g = new Group(strs[0], strs[1], new ArrayList());
				addAction("zoom-in", new ZoomInAction(), g.actions);
				addAction("zoom-out", new ZoomOutAction(), g.actions);
				addAction("actual-size", new ActualSizeAction(), g.actions);
		diag_g.actions.add(g);
		develop_group.actions.add(null);
		addAction("edit-demo", new EditDemoAction(), develop_group.actions);

		strs = Str.get("gui.menu_run").split("\\|", 2);
		execution_group = new Group(strs[0], strs[1], new ArrayList());
		addAction("show-ii", new ShowIIAction(), execution_group.actions);
		execution_group.actions.add(null);
		addAction("test-project", new TestProjectAction(), execution_group.actions);
		addAction("show-variables", new ShowSymbolTableAction(), execution_group.actions);
		execution_group.actions.add(null);
		addAction("run-demo", new RunDemoAction(), execution_group.actions);
		addAction("run-trace-demo", new RunTraceDemoAction(), execution_group.actions);
		
		strs = Str.get("gui.menu_help").split("\\|", 2);
		help_group = new Group(strs[0], strs[1], new ArrayList());
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
				list.add(new ExecuteTraceAlgorithmAction());
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
    public static class Group  {
		public String name;
		public int mnemonic;
		public List actions;
		
		Group(String name, int mnemonic, List actions) {
			this.name = name;
			this.mnemonic = mnemonic;
			this.actions = actions;
		}
		Group(String name, String mnemonic, List actions) {
			this.name = name;
			this.actions = actions;
			this.mnemonic = mnemonic.charAt(0);
		}
	}
	
	/////////////////////////////////////////////////////////
    class ShowIIAction extends AbstractAction 
	{
		public ShowIIAction() 
		{
			super(null, Util.getIcon("img/ii.gif"));
			String[] strs = Str.get("gui.action_ii").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_namespace").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_diagram_zoom_in").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_diagram_zoom_out").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_diagram_normal").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_view_unit_doc").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_edit_view_unit").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super("Copy");
			putValue(SHORT_DESCRIPTION, "Allows to make a copy of this unit");
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
			super("Move");
			putValue(SHORT_DESCRIPTION, "Allows to move this unit to other package");
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
			super();
			String[] strs = Str.get("gui.action_delete_unit").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_delete_package").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super(null, Util.getIcon("img/New24.gif"));
			String[] strs = Str.get("gui.action_new_prj").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super(null, Util.getIcon("img/Open24.gif"));
			String[] strs = Str.get("gui.action_open_prj").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_install_prj").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_reload_prj").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_close_prj").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_quit").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.quit();
		}
	}

	/////////////////////////////////////////////////////////
	class PrintDiagramAction extends AbstractAction {
		public PrintDiagramAction() {
			super();
			String[] strs = Str.get("gui.action_print_diagram").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK));
		}
		public void actionPerformed(ActionEvent e) {
			GUI.printDiagram();
		}
	}

	/////////////////////////////////////////////////////////
	class ImportAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public ImportAction()
		{
			super();
			String[] strs = Str.get("gui.action_import_file").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_export_prj").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_props_prj").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super("Save");
			putValue(SHORT_DESCRIPTION, "Saves current state of project");
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
			super("Save as...");
			putValue(SHORT_DESCRIPTION, "Saves this project with a different name");
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
			super();
			String[] strs = Str.get("gui.action_new_pkg").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_new_spec").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_new_algorithm").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_new_algorithm_from_spec").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_create_test_for_spec").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_run_algorithm").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F9, KeyEvent.CTRL_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.getFocusedProject().executeAlgorithm(false);
		}
	}
	
	/////////////////////////////////////////////////////////
	class ExecuteTraceAlgorithmAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public ExecuteTraceAlgorithmAction()
		{
			super();
			String[] strs = Str.get("gui.action_run_trace_algorithm").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F9, KeyEvent.SHIFT_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.getFocusedProject().executeAlgorithm(true);
		}
	}
	
	/////////////////////////////////////////////////////////
	class TestAlgorithmAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public TestAlgorithmAction()
		{
			super();
			String[] strs = Str.get("gui.action_test_algorithm").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_new_instance").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super();
			String[] strs = Str.get("gui.action_new_class").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super(null, Util.getIcon("img/compile.gif"));
			String[] strs = Str.get("gui.action_compile_prj").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super(null, Util.getIcon("img/compile.gif"));
			String[] strs = Str.get("gui.action_test_prj").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_T, KeyEvent.CTRL_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.testProject();
		}
	}

	
	/////////////////////////////////////////////////////////
	class EditDemoAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public EditDemoAction()
		{
			super();
			String[] strs = Str.get("gui.action_edit_demo").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
			putValue(MNEMONIC_KEY, new Integer(KeyEvent.VK_M));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.editProjectDemo();
		}
	}

	/////////////////////////////////////////////////////////
	class RunDemoAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public RunDemoAction()
		{
			super();
			String[] strs = Str.get("gui.action_run_demo").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.runProjectDemo(false);
		}
	}

	/////////////////////////////////////////////////////////
	class RunTraceDemoAction extends AbstractAction
	{
		/////////////////////////////////////////////////////////
		public RunTraceDemoAction()
		{
			super();
			String[] strs = Str.get("gui.action_run_trace_demo").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
			putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.SHIFT_MASK));
		}
	
		/////////////////////////////////////////////////////////
		public void actionPerformed(ActionEvent e)
		{
			GUI.runProjectDemo(true);
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
			super(null, Util.getIcon("img/api.gif"));
			String[] strs = Str.get("gui.action_view_prj_doc").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			super(null, Util.getIcon("img/Help24.gif"));
			String[] strs = Str.get("gui.action_help").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
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
			String about = Str.get("gui.3_about_msg", 
				Info.obtNombre()+ " " +Info.obtVersion()+ " (" +Info.obtBuild()+ ")",
				Loro.obtNombre()+ " " +Loro.obtVersion()+ " (" +Loro.obtBuild()+ ")",
				"http://loro.sf.net"
			);
			
			javax.swing.JOptionPane.showMessageDialog(
				null,
				about,
				Str.get("gui.about"),
				javax.swing.JOptionPane.INFORMATION_MESSAGE,
				Util.getIcon("img/splash.jpg")
			);
		}

		/////////////////////////////////////////////////////////
		public AboutAction()
		{
			super();
			String[] strs = Str.get("gui.action_about").split("\\|", 2);
			putValue(NAME, strs[0]);
			putValue(SHORT_DESCRIPTION, strs[1]);
		}
	}
}
