package loroedi.gui.project;

import loro.Loro;
import loro.IUnidad;

import loroedi.Info.Str;
import loroedi.gui.*;
import loroedi.gui.project.model.*;
import loroedi.gui.project.unit.*;
import loroedi.gui.misc.*;
import loroedi.gui.editor.*;
import loroedi.Preferencias;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.net.URL;
import java.io.File;


/////////////////////////////////////////////////////////
/**
 * GUI for projects.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class Project extends JPanel
{
	static final IPackageModel[] EMPTY_PACKAGE_ARRAY = new IPackageModel[0];
	
	protected static final int SPEC_TYPE = 0;
	protected static final int ALGORITHM_TYPE = 1;
	protected static final int CLASS_TYPE = 2;
	
	protected IProjectModel model;
	protected JFrame frame;
	protected Diagram diagram;
	protected JLabel etq;
	protected JToolBar tb;
	
	protected ICell selectedCell;
	
	protected MessageArea msgArea;
	
	protected JSplitPane split0;
	
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Crea una nueva ventana para gestión de un proyecto.
	 */
	public Project(JFrame frame, IProjectModel model)
	{
		super(new GridLayout(1,1));
		this.model = model;
		this.frame = frame;
		updateTitle();
		diagram = new Diagram(this);
		
		createJSplitPane0();
		split0.setTopComponent(diagram);
		split0.setBottomComponent(new JScrollPane(msgArea = new MessageArea()));
		updateDividerLocation();
		this.add(split0);
		frame.getContentPane().add(this);
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Establece el modelo a despachar.
	 */
	public void setModel(IProjectModel model)
	{
		this.model = model;
		updateTitle();
		JLabel label = new JLabel(Str.get("gui.msg_please_wait"));
		label.setVerticalAlignment(JLabel.NORTH);
		try
		{
			split0.setTopComponent(label);
			updateDividerLocation();
			diagram.reset();
		}
		finally
		{
			split0.setTopComponent(diagram);
			updateDividerLocation();
		}
	}
	
	/////////////////////////////////////////////////////////
	protected void click(ICell cell, MouseEvent e)
	{
		selectedCell = cell;
		if ( e.getClickCount() == 2 )
		{
			edit();
		}
		else if ( (e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0 )
		{
			JPopupMenu popup = getPopupMenu(cell);
			Component c = (Component) e.getSource();
			popup.show(c, e.getX(), e.getY());
			return;
		}
	}

	////////////////////////////////////////////////////////////////////////////
	protected void keyPressed(ICell cell, KeyEvent e) 
	{
		int kc = e.getKeyCode();
		int md = e.getModifiers();
		if ( kc == KeyEvent.VK_DELETE )
		{
			Object obj = cell.getUserObject();
			if ( obj instanceof IProjectUnit )
			{
				_deleteUnit((IProjectUnit) obj);
			}
			else if ( obj instanceof IPackageModel )
			{
				_deletePackage((IPackageModel) obj);
			}
		}
		else if ( kc == KeyEvent.VK_ENTER )
		{
			Object obj = cell.getUserObject();
			if ( obj instanceof IProjectUnit )
			{
				GUI.editUnit((IProjectUnit) obj, frame);
			}
		}
		else if ( kc == KeyEvent.VK_F9 && (md & KeyEvent.CTRL_MASK) == KeyEvent.CTRL_MASK )
		{
			Object obj = cell.getUserObject();
			if ( obj instanceof AlgorithmUnit )
			{
				GUI.executeAlgorithm((AlgorithmUnit) obj, false);
			}
		}
		else if ( kc == KeyEvent.VK_F9 && (md & KeyEvent.SHIFT_MASK) == KeyEvent.SHIFT_MASK )
		{
			Object obj = cell.getUserObject();
			if ( obj instanceof AlgorithmUnit )
			{
				GUI.executeAlgorithm((AlgorithmUnit) obj, true);
			}
		}
		else if ( kc == KeyEvent.VK_F1 && (md & KeyEvent.CTRL_MASK) == KeyEvent.CTRL_MASK )
		{
			Object obj = cell.getUserObject();
			if ( obj instanceof IProjectUnit )
			{
				GUI.showUnitDocumentation((IProjectUnit) obj);
			}
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el modelo.
	 */
	public IProjectModel getModel()
	{
		return model;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el diagrama.
	 */
	public Diagram getDiagram()
	{
		return diagram;
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
		frame.toFront();
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Edita la unidad seleccionada.
	 */
	public void edit()
	{
		Object obj = selectedCell.getUserObject();
		if ( obj instanceof IProjectUnit )
		{
			GUI.editUnit((IProjectUnit) obj, frame);
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Borrar el objeto seleccionado.
	 */
	public void deleteUnit()
	{
		Object obj = selectedCell.getUserObject();
		if ( obj instanceof IProjectUnit )
		{
			_deleteUnit((IProjectUnit) obj);
		}
	}
	
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Borra el algoritmo dado.
	 */
	private boolean _deleteAlgorithm(AlgorithmUnit alg) {
		StringBuffer msg = new StringBuffer(
			alg.getStereotype()+ " " +alg.getQualifiedName()+ "\n"+
			"\n"+
			Str.get("gui.prj_conf_delete_algorithm")+ "\n"
		);
		if ( GUI.confirm(frame, msg.toString()) ) {
			IPackageModel pkgm = alg.getPackage();
			if ( pkgm.removeAlgorithm(alg) ) {
				model.notifyListeners(
					new ProjectModelEvent(this, ProjectModelEvent.ALGORITHM_REMOVED, alg)
				);
				return true;
			}
		}
		return false;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Borra la clase dada.
	 */
	private boolean _deleteClass(ClassUnit clazz)
	{
		StringBuffer msg = new StringBuffer(
			clazz.getStereotype()+ " " +clazz.getQualifiedName()+ "\n"+
			"\n"+
			Str.get("gui.prj_conf_delete_class")+ "\n"
		);
		if ( GUI.confirm(frame, msg.toString()) ) {
			IPackageModel pkgm = clazz.getPackage();
			if ( pkgm.removeClass(clazz) ) {
				model.notifyListeners(
					new ProjectModelEvent(this, ProjectModelEvent.CLASS_REMOVED, clazz)
				);
				return true;
			}
		}
		return false;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Borra la especificación dada siempre que no tenga algoritmos de implementación
	 * dependientes en este proyecto.
	 */
	private boolean _deleteSpecification(SpecificationUnit spec)
	{
		StringBuffer msg = new StringBuffer(
			spec.getStereotype()+ " " +spec.getQualifiedName()+ "\n"+
			"\n"
		);
		
		Collection algs = model.getAlgorithms(spec);
		if ( algs.size() > 0 ) {
			msg.append(Str.get("gui.prj_spec_has_algorithms") +"\n");
			GUI.message(frame, msg.toString());
			return false;
		}

		msg.append(Str.get("gui.prj_conf_delete_spec")+ "\n");
		if ( GUI.confirm(frame, msg.toString()) ) {
			IPackageModel pkgm = spec.getPackage();
			if ( pkgm.removeSpecification(spec) ) {
				model.notifyListeners(
					new ProjectModelEvent(this, ProjectModelEvent.SPEC_REMOVED, spec)
				);
				return true;
			}
		}
		return false;
	}
	
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Borra la unidad dada.
	 */
	private void _deleteUnit(IProjectUnit unit) {
		if ( !model.getControlInfo().isModifiable() ) {
			GUI.message(frame, Str.get("gui.msg_project_is_read_only"));
			return;
		}
		
		boolean deleted = false;
		if ( unit instanceof SpecificationUnit ) {
			deleted = _deleteSpecification((SpecificationUnit) unit);
		}
		else if ( unit instanceof AlgorithmUnit ) {
			deleted = _deleteAlgorithm((AlgorithmUnit) unit);
		}
		else if ( unit instanceof ClassUnit ) {
			deleted = _deleteClass((ClassUnit) unit);
		}
		if ( deleted ) {
			msgArea.clear();
			msgArea.print(Str.get("gui.1_prj_msg_unit_deleted", unit.getStereotypedName()));
			UEditor editor = (UEditor) unit.getUserObject();
			if ( editor != null ) {
				editor.getFrame().dispose();
				unit.setUserObject(null);
			}
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Remueve el paquete seleccionado.
	 */
	public void deletePackage() {
		Object obj = selectedCell.getUserObject();
		if ( obj instanceof IPackageModel ) {
			_deletePackage((IPackageModel) obj);
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Remueve el paquete dado siempre que se encuentre vacío.
	 */
	private void _deletePackage(IPackageModel pkgm) {
		if ( !model.getControlInfo().isModifiable() ) {
			GUI.message(frame, Str.get("gui.msg_project_is_read_only"));
			return;
		}

		String pkgname = pkgm.getName();
		String shown_pkgname = pkgname.length() > 0 ? pkgname : "(" +Loro.Str.get("anonymous")+ ")"; 
	
		StringBuffer msg = new StringBuffer(
			Loro.Str.get("package")+ ": " +shown_pkgname+ "\n"+
			"\n"
		);
		if ( pkgm.getSpecNames().size() > 0
		||   pkgm.getAlgorithmNames().size() > 0
		||   pkgm.getClassNames().size() > 0 )
		{
			msg.append(Str.get("gui.prj_pkg_not_empty")+ "\n");
			GUI.message(frame, msg.toString());
			return;
		}
		
		msg.append(Str.get("gui.prj_conf_delete_pkg")+ "\n");
		if ( !GUI.confirm(frame, msg.toString()) )
			return;

		IProjectModel prjm = pkgm.getModel();
		if ( prjm.removePackage(pkgm) ) {
			msgArea.clear();
			msgArea.print(Str.get("gui.1_prj_msg_pkg_deleted", shown_pkgname));

			model.notifyListeners(
				new ProjectModelEvent(this, ProjectModelEvent.PACKAGE_REMOVED, pkgm)
			);
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Visualiza la documentación para el objecto seleccionado.
	 */
	public void viewDoc() {
		Object obj = selectedCell.getUserObject();
		if ( obj instanceof IProjectUnit ) {
			IProjectUnit unit = (IProjectUnit) obj;
			GUI.showUnitDocumentation(unit);
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Actualiza el título de la ventana según el nombre del modelo.
	 * Si tal nombre es vacío, se pone GUI.UNTITLED_PROJECT.
	 * El título se completa según el proyecto sea válido y/o modificable.
	 */
	public void updateTitle() {
		String title = model.getInfo().getName();
		if ( title.trim().length() == 0 ) {
			title = GUI.UNTITLED_PROJECT;
		}
		String suffix = "";
		if ( !model.getControlInfo().isValid() )
			suffix += " (incompatible!)";   // pending

		if ( model.getControlInfo().isModifiable() )
			frame.setTitle(Str.get("gui.2_title_project", title, suffix));
		else
			frame.setTitle(Str.get("gui.2_title_project_read_only", title, suffix));
	}

	////////////////////////////////////////////////////////////////
	/**
	 */
	protected void updateDividerLocation()
	{
		double h = split0.getSize().getHeight();
		double p = 0.1;
		if ( p * h < 60. )
			p = 60. / h;

		p = 1. -p;
		if ( 0. < p && p < 1.0 )
			split0.setDividerLocation(p);
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 */
	private void createJSplitPane0()
	{
		split0 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split0.setDividerSize(3);
		split0.setAutoscrolls(false);
		split0.setContinuousLayout(false);
		split0.setMinimumSize(new java.awt.Dimension(100, 100));
		split0.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				updateDividerLocation();
			}
		});
	}

	////////////////////////////////////////////////////////////////
	/**
	 */
	public JPopupMenu getPopupMenu(ICell cell) {
		Object obj = cell.getUserObject();
		JPopupMenu popupGroup = new JPopupMenu();
		JLabel label = new JLabel();
		label.setForeground(Color.blue);
		label.setFont(new Font("monospaced", Font.PLAIN, 12));
		if ( obj instanceof IProjectUnit ) {
			IProjectUnit unit = (IProjectUnit) obj;
			label.setText(unit.getStereotypedName());
		}
		else if ( obj instanceof IPackageModel ) {
			IPackageModel pkgm = (IPackageModel) obj;
			String pkgname = pkgm.getName();
			if ( pkgname.length() == 0 )
				pkgname = "(" +Loro.Str.get("anonymous")+ ")";
			label.setText(Loro.Str.get("package")+ " " +pkgname);
		}
		popupGroup.add(label);
		popupGroup.addSeparator();
		
		List list = GUI.getActions().getActions(obj);
		for ( Iterator it = list.iterator(); it.hasNext(); ) {
			Action action = (Action) it.next();
			if ( action == null )
				popupGroup.addSeparator();
			else
				popupGroup.add(action);
		}
		return popupGroup;
	}
	

	////////////////////////////////////////////////////////////////
	/**
	 * Crea una pareja especificación/algoritmo para probar algoritmos
	 * que implementen la especificación seleccionada.
	 */
	public void createTestForSpecification()
	{
		if ( !model.getControlInfo().isModifiable() ) {
			GUI.message(frame, Str.get("gui.msg_project_is_read_only"));
			return;
		}
		Object obj = selectedCell.getUserObject();
		if ( !(obj instanceof SpecificationUnit) )
			return;

		SpecificationUnit spec = (SpecificationUnit) obj;
		
		if (  spec.getIUnidad() == null ) {
			GUI.message(frame, Str.get("gui.msg_spec_not_compiled"));
			return;
		}
		
		// La spec y el algoritmo probador se ponen en el mismo paquete de la especificación.
		// Se utiliza el mismo nombre simple pero prefijado con GUI.tester_prefix. 
		// Por ejemplo, si la especificación es "foo::baz", entonces tanto la nueva spec como
		// el algoritmo probador toman el nombre "foo::test_baz" (asumiendo que
		// GUI.tester_prefix == "test_". 
		
		String spec_name = spec.getName();
		
		
		// confirme con el usuario si parece que va a crear un probador para un probador:
		if ( spec_name.startsWith(GUI.tester_prefix)
		&&   !GUI.confirm(frame,  Str.get("gui.1_conf_test_test", GUI.tester_prefix)+ "\n")
		) {
				return;
		}
		
		// tester_name se usa tanto para la espec como para el algoritmo.
		String tester_name = GUI.tester_prefix +spec_name;
		IPackageModel pkg = spec.getPackage();
		IProjectModel prjm = pkg.getModel();
		
		// compruebe que ya no exista la pareja spec/alg de pruebas:
		if ( pkg.getSpecification(tester_name) != null ) {
			GUI.message(frame, Str.get("gui.1_msg_spec_exists", tester_name));
			return;
		}
		if ( pkg.getAlgorithm(tester_name) != null ) {
			GUI.message(frame, Str.get("gui.1_msg_algorithm_exists", tester_name));
			return;
		}

		msgArea.clear();
		msgArea.print(Str.get("gui.1_msg_creating_test_scheme", spec_name)+ "\n");
		
		String pkg_name = prjm.isAnonymous(pkg) ? null : pkg.getName();
		
		// agregue spec:
		SpecificationUnit tester_spec = pkg.addSpecification(tester_name);
		tester_spec.setSourceCode(
			MUtil.getSourceCodeTemplateSpecTest(pkg_name, tester_name, spec_name)
		);
		msgArea.print("... " +Str.get("gui.1_msg_test_adding_spec", tester_spec.getName())+ "\n");
		// guardar el código fuente:
		if ( ! GUI.saveUnit(tester_spec) ) {
			return;
		}
		// ahora compile:
		Thread thread = GUI.compileUnit(tester_spec);
		try {
			thread.join();
		}
		catch(InterruptedException ex) {
			msgArea.print("compilation of spec interrupted!");
		}
		
		if ( tester_spec.getIUnidad() == null ) {
			// compilación fallida!?  No se espera que esto suceda.
			msgArea.print("Compilation failed! (?)");
			return;
			
		}
		// agregue algoritmo:
		String q_spec_name = tester_spec.getQualifiedName();
		msgArea.print("... " +Str.get("gui.1_msg_test_adding_algorithm", tester_name)+ "\n");
		AlgorithmUnit tester_alg = pkg.addAlgorithm(tester_name, q_spec_name);
		tester_alg.setSourceCode(
			MUtil.getSourceCodeTemplateAlgTest(pkg_name, tester_name, spec_name)
		);
		// Avise al usuario lo sucedido y lo que debería completar:
		GUI.message(
			frame, 
			Str.get("gui.2_msg_test_scheme_ready", tester_name, spec_name)
		);
		GUI.editUnit(tester_alg, frame);
		
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 */
	public void createPackage() {
		final JTextField textField = new JTextField(50);
		textField.setFont(GUI.monospaced12_font);
		final JLabel status = new JLabel();
		status.setFont(status.getFont().deriveFont(Font.ITALIC));
		
		textField.setBorder(GUI.createTitledBorder(Str.get("gui.prj_pkg_name")));

		Object[] array = {
			textField,
			status
		};

        ProjectDialog form = new ProjectDialog(frame, Str.get("gui.prj_title_create_pkg"), array) {
			public boolean dataOk() {
				status.setForeground(Color.gray);
				String name = textField.getText().trim();
				String msg = null;
				if ( name.length() == 0 ) {
					IPackageModel pkg = model.getPackage(name);
					if ( pkg == null ) {
						status.setText(Str.get("gui.prj_msg_blank_create_anonymous"));
						return true;
					}
					msg = Str.get("gui.prj_msg_anonymous_exists");
				}
				else {
					msg = model.validateNewPackageName(name);
				}
				
				if ( msg == null ) {
					status.setText(Str.get("gui.prj_msg_name_is_valid"));
				}
				else {
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
		if ( form.accepted() ) {
			String name = textField.getText().trim();
			model.addPackage(name);
			msgArea.clear();
			msgArea.print(Str.get("gui.1_prj_msg_pkg_added", name));
		}
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 */
	public void createSpecification()
	{
		IPackageModel[] pkgs = (IPackageModel[]) model.getPackages().toArray(EMPTY_PACKAGE_ARRAY);
		if ( pkgs.length == 0 )
		{
			JOptionPane.showMessageDialog(
				frame,
				Str.get("gui.prj_msg_no_pkgs"),
				"",
				JOptionPane.INFORMATION_MESSAGE
			);
			return;
		}
		final JComboBox cb_pkgs = new JComboBox(pkgs); 
		cb_pkgs.setFont(GUI.monospaced12_font);
		final JTextField textField = new JTextField(50);
		textField.setFont(GUI.monospaced12_font);
		final JLabel status = new JLabel();
		status.setFont(status.getFont().deriveFont(Font.ITALIC));
		
		cb_pkgs.setBorder(GUI.createTitledBorder(Str.get("gui.prj_in_pkg")));
		textField.setBorder(GUI.createTitledBorder(Str.get("gui.prj_spec_name")));

		Object[] array = {
			cb_pkgs,
			textField,
			status
		};

        ProjectDialog form = new ProjectDialog(frame, Str.get("gui.prj_title_create_spec"), array) {
			public boolean dataOk() {
				IPackageModel pkg = (IPackageModel) cb_pkgs.getSelectedItem();
				String spec_name = textField.getText();
				String msg = pkg.validateNewSpecificationName(spec_name);
				if ( msg == null ) {
					status.setForeground(Color.gray);
					status.setText(Str.get("gui.prj_msg_name_is_valid"));
				}
				else {
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
		if ( form.accepted() ) {
			IPackageModel pkg = (IPackageModel) cb_pkgs.getSelectedItem();
			String name = textField.getText().trim();
			IProjectUnit unit = pkg.addSpecification(name);
			msgArea.clear();
			msgArea.print(Str.get("gui.1_prj_msg_unit_added", unit.getStereotypedName()));
		}
	}

	////////////////////////////////////////////////////////////////
	/**
	 */
	Collection getSpecNamesFromDependencies() {
		List list = new ArrayList();
		for ( Iterator it = model.getSupportingProjects().iterator(); it.hasNext(); ) {
			IProjectModel m2 = (IProjectModel) it.next();
			for ( Iterator itt = m2.getPackages().iterator(); itt.hasNext(); ) {
				IPackageModel pkg = (IPackageModel) itt.next();
				for ( Iterator ittt = pkg.getSpecNames().iterator(); ittt.hasNext(); ) {
					String spec_name = (String) ittt.next();
					list.add(spec_name);
				}
			}
		}
		return list;
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 */
	public void createAlgorithm() {
		createAlgorithm("");
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 */
	public void createAlgorithmFromSpecification() {
		if ( !model.getControlInfo().isModifiable() ) {
			GUI.message(frame, Str.get("gui.msg_project_is_read_only"));
			return;
		}
		Object obj = selectedCell.getUserObject();
		if ( obj instanceof SpecificationUnit ) {
			SpecificationUnit spec = (SpecificationUnit) obj;
			createAlgorithm(spec.getQualifiedName());
		}
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 */
	private void createAlgorithm(String default_spec_name) {
		IPackageModel[] pkgs = (IPackageModel[]) model.getPackages().toArray(EMPTY_PACKAGE_ARRAY);
		if ( pkgs.length == 0 ) {
			JOptionPane.showMessageDialog(
				frame,
				Str.get("gui.prj_msg_no_pkgs"),
				"",
				JOptionPane.INFORMATION_MESSAGE
			);
			return;
		}
		final JComboBox cb_pkgs = new JComboBox(pkgs); 
		cb_pkgs.setFont(GUI.monospaced12_font);
        final JTextField f_alg_name = new JTextField(50);
		f_alg_name.setFont(GUI.monospaced12_font);
        final JTextField f_spec_name = new JTextField(50);
		f_spec_name.setFont(GUI.monospaced12_font);
		f_spec_name.setText(default_spec_name);
		final JLabel status = new JLabel();
		String[] strs = Str.get("but.choose").split("\\|", 2);
		final JButton choose = new JButton(strs[0]);
		choose.setMnemonic(KeyEvent.VK_E);
		status.setFont(status.getFont().deriveFont(Font.ITALIC));
		
		cb_pkgs.setBorder(GUI.createTitledBorder(Str.get("gui.prj_in_pkg")));
		f_alg_name.setBorder(GUI.createTitledBorder(Str.get("gui.prj_algorithm_name")));
		
		JPanel panel_spec = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel_spec.add(f_spec_name);
		panel_spec.add(choose);
		panel_spec.setBorder(GUI.createTitledBorder(Str.get("gui.prj_for_spec")));

        Object[] array = {
			cb_pkgs,
			f_alg_name,
			panel_spec,
			status
		};
		
		//final Collection supp_specs = getSpecNamesFromDependencies();
		
		//// Se evita este llamado hasta que se haga una implementación más eficiente
		//final Collection supp_specs = GUI.getAllSpecs();

        final ProjectDialog form = new ProjectDialog(frame, Str.get("gui.prj_title_create_algorithm"), array) {
			public boolean dataOk() {
				IPackageModel pkg = (IPackageModel) cb_pkgs.getSelectedItem();
				String alg_name = f_alg_name.getText();
				String spec_name = f_spec_name.getText();
				String msg = null;

				if ( (msg = pkg.validateNewAlgorithmName(alg_name)) == null ) {
					IUnidad.IEspecificacion u = Loro.getSpecification(spec_name);
					if ( u == null ) {
						msg = Str.get("gui.prj_spec_not_found_compiled");
						// no existe o no está compilada. Precisar:
						//if (  supp_specs.contains(spec_name) )   // existe?
						//	msg = "Especificación no compilada";
						//else
						//	msg = "Especificación inexistente";
					}
				}
			
				if ( msg == null ) {
					status.setForeground(Color.gray);
					status.setText(Str.get("gui.ok"));
					return true;
				}
				else {
					status.setForeground(Color.red);
					status.setText(msg);
					return false;
				}
			}
		};
		
		choose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPopupMenu popup = chooseSpecification(form, new ActionListener()  {
					public void actionPerformed(ActionEvent e) {
						String str = e.getActionCommand();
						f_spec_name.setText(str);
						form.toFront();
						f_spec_name.requestFocus();
					}
				});
				popup.addPopupMenuListener(new PopupMenuListener() {
					public void popupMenuCanceled(PopupMenuEvent e){}
					public void popupMenuWillBecomeVisible(PopupMenuEvent e){}
					public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
						form.toFront();
						f_spec_name.requestFocus();
					}
				});
				
				popup.show(form, form.getSize().width / 2, 20);
				form.requestFocus();
			}
		});
		
		form.activate();
        form.pack();
		form.setLocationRelativeTo(frame);
		form.setVisible(true);
		if ( form.accepted() ) {
			IPackageModel pkg = (IPackageModel) cb_pkgs.getSelectedItem();
			String alg_name = f_alg_name.getText().trim();
			String spec_name = f_spec_name.getText().trim();
			IProjectUnit unit = pkg.addAlgorithm(alg_name, spec_name);
			msgArea.clear();
			msgArea.print(Str.get("gui.1_prj_msg_unit_added", unit.getStereotypedName()));
		}
	}


	////////////////////////////////////////////////////////////////
	/**
	 * Lanza un popup menu para que el usuario elija una especificación.
	 */
	protected JPopupMenu chooseSpecification(final Dialog owner, final ActionListener al) {
		final JPopupMenu popup = new JPopupMenu();
		GUI.progressRun(owner, Str.get("gui.generating_spec_list"), new Runnable() {
			public void run() {
				_prepareChooseSpecification(popup, al);
			}
		});
		return popup;
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene un popup menu para que el usuario elija una especificación.
	 */
	protected JPopupMenu _prepareChooseSpecification(JPopupMenu popup, ActionListener al) {
		JMenu submenu;
		Collection strings;
		JLabel empty_lbl = new JLabel(" " +Str.get("gui.empty"));
		empty_lbl.setForeground(Color.gray);

		JMenuItem mi;
		JLabel lbl = new JLabel(Str.get("gui.available_specs"), SwingConstants.CENTER);
		lbl.setForeground(Color.blue);
		popup.add(lbl);
		popup.addSeparator();
		
		Collection prjnames = GUI.getAvailableProjects();
		List prjms = new ArrayList();
		prjms.add(model); // este de primero
		for ( Iterator it = prjnames.iterator(); it.hasNext(); ) {
			String prjname = (String) it.next();
			IProjectModel prjm = GUI.getProjectModel(prjname);
			if ( prjm != model )
				prjms.add(prjm);
		}
		
		for ( Iterator it = prjms.iterator(); it.hasNext(); ) {
			IProjectModel prjm = (IProjectModel) it.next();
			Collection qnames = getAllSpecsInProject(prjm);
			
			submenu = new JMenu(Str.get("gui.1_prj_in_prj", prjm.getInfo().getName()));
			popup.add(submenu);
			
			if ( qnames.size() == 0 ) {
				submenu.add(empty_lbl);
			}
			else {
				for ( Iterator itt = qnames.iterator(); itt.hasNext(); ) {
					String qname = (String) itt.next();
					mi = new JMenuItem(qname);
					mi.setFont(GUI.monospaced12_font);
					mi.addActionListener(al);
					submenu.add(mi);
				}
			}
		}
		return popup;
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Returns the qualified names of all specs in the given project.
	 */
	static Collection getAllSpecsInProject(IProjectModel model) {
		List list = new ArrayList();
		for ( Iterator it = model.getPackages().iterator(); it.hasNext(); ) {
			IPackageModel pkg = (IPackageModel) it.next();
			for ( Iterator itt = pkg.getSpecNames().iterator(); itt.hasNext(); ) {
				String spec_name = (String) itt.next();
				String qname = model.isAnonymous(pkg) ? spec_name : pkg.getName() + "::" + spec_name;
				list.add(qname);
			}
		}
		return list;
	}
	
	
	////////////////////////////////////////////////////////////////
	/**
	 */
	public void createClass() {
		IPackageModel[] pkgs = (IPackageModel[]) model.getPackages().toArray(EMPTY_PACKAGE_ARRAY);
		if ( pkgs.length == 0 ) {
			JOptionPane.showMessageDialog(
				frame,
				Str.get("gui.prj_msg_no_pkgs"),
				"",
				JOptionPane.INFORMATION_MESSAGE
			);
			return;
		}
		final JComboBox cb_pkgs = new JComboBox(pkgs); 
		cb_pkgs.setFont(GUI.monospaced12_font);
		final JTextField textField = new JTextField(50);
		textField.setFont(GUI.monospaced12_font);
		final JLabel status = new JLabel();
		status.setFont(status.getFont().deriveFont(Font.ITALIC));
		
		cb_pkgs.setBorder(GUI.createTitledBorder(Str.get("gui.prj_in_pkg")));
		textField.setBorder(GUI.createTitledBorder(Str.get("gui.prj_class_name")));

		Object[] array = {
			cb_pkgs,
			textField,
			status
		};

        ProjectDialog form = new ProjectDialog(frame, Str.get("gui.prj_title_create_class"), array) {
			public boolean dataOk() {
				IPackageModel pkg = (IPackageModel) cb_pkgs.getSelectedItem();
				String name = textField.getText();
				String msg = pkg.validateNewClassName(name);
				if ( msg == null ) {
					status.setForeground(Color.gray);
					status.setText(Str.get("gui.prj_msg_name_is_valid"));
				}
				else {
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
		if ( form.accepted() ) {
			IPackageModel pkg = (IPackageModel) cb_pkgs.getSelectedItem();
			String name = textField.getText().trim();
			IProjectUnit unit = pkg.addClass(name);
			msgArea.clear();
			msgArea.print(Str.get("gui.1_prj_msg_unit_added", unit.getStereotypedName()));
		}
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 */
	public void executeAlgorithm(boolean ejecutorpp) {
		Object obj = selectedCell.getUserObject();
		if ( obj instanceof AlgorithmUnit ) {
			AlgorithmUnit alg = (AlgorithmUnit) obj;
			GUI.executeAlgorithm(alg, ejecutorpp);
		}
	}

	////////////////////////////////////////////////////////////////
	/**
	 */
	public void testAlgorithm() {
		Object obj = selectedCell.getUserObject();
		if ( obj instanceof AlgorithmUnit ) {
			AlgorithmUnit alg = (AlgorithmUnit) obj;
			GUI.testAlgorithm(alg, true);
		}
	}
}
