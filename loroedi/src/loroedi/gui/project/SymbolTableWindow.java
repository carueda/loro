package loroedi.gui.project;

import loroedi.Preferencias;

import loro.ISymbolTable;
import loro.Loro;
import loroedi.gui.*;
import loroedi.gui.project.model.*;
import loroedi.gui.project.unit.*;

import loroedi.gui.misc.*;
import loroedi.Preferencias;
import loroedi.Info.Str;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.*;
import java.awt.*;
import java.net.URL;



/////////////////////////////////////////////////////////
/**
 * Ventana para acceder a una tabla de símbolos.
 * @author Carlos Rueda
 */
public class SymbolTableWindow
{
	private ISymbolTable symTab;
	private JFrame frame = null;
	private boolean closeable;

	private TableModel tableModel;
	private JTable table;
	private int selectedRow;
	private boolean delVar;

	/////////////////////////////////////////////////////////
	public SymbolTableWindow(
		String title,
		ISymbolTable symTab,
		boolean closeable, boolean delVar, String preferenceKey)
	{
		this.symTab = symTab;
		this.closeable = closeable;
		this.delVar = delVar;
		frame = new JFrame(title);
		URL url = getClass().getClassLoader().getResource("img/icon.jpg");
		if ( url != null ) 
			frame.setIconImage(new ImageIcon(url).getImage());
		JPanel cp = new JPanel(new BorderLayout());
		cp.setBorder(BorderFactory.createTitledBorder(Str.get("gui.1_title_namespace", title)));
		frame.setContentPane(cp);

		tableModel = new TableModel();
		table = new JTable(tableModel);
		table.setPreferredScrollableViewportSize(new Dimension(300, 100));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectedRow = -1;
		frame.getContentPane().add(new JScrollPane(table));

		ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				//Ignore extra messages.
				if (e.getValueIsAdjusting())  {
					return;
				}

				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				selectedRow = lsm.isSelectionEmpty() ? -1 : lsm.getMinSelectionIndex();
			}
		});

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
		frame.getContentPane().add(buttons, "South");
		JButton b;
		ActionListener lis = new BListener();
		if ( delVar ) {
			String[] strs = Str.get("but.delete_declaration").split("\\|", 2);
			b = new JButton(strs[0]);
			b.setToolTipText(strs[1]);
			b.setActionCommand("delete-variable");
			b.addActionListener(lis);
			buttons.add(b);
		}
		if ( closeable ) {
			String[] strs = Str.get("but.close").split("\\|", 2);
			b = new JButton(strs[0]);
			b.setToolTipText(strs[1]);
			b.setActionCommand("close");
			b.addActionListener(lis);
			buttons.add(b);
		}

		Preferencias.Util.updateRect(frame, preferenceKey);
	
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent _) {
				if ( SymbolTableWindow.this.closeable ) {
					SymbolTableWindow.this.frame.dispose();
				}
			}
		});
	}

	/////////////////////////////////////////////////////////
	/**
	 * Despliega la ventana.
	 */
	public void show()
	{
		frame.setVisible(true);
	}

	/////////////////////////////////////////////////////////
	/**
	 * Obtiene la ventana.
	 */
	public JFrame getFrame()
	{
		return frame;
	}
	
	/////////////////////////////////////////////////////////
	public void setSymbolTable(ISymbolTable symTab)
	{
		this.symTab = symTab;
		update();
	}
	
	/////////////////////////////////////////////////////////
	/**
	 * Actualiza el contenido de la ventana.
	 */
	public void update()
	{
		tableModel.fireTableDataChanged();
	}

	/////////////////////////////////////////////////////////
	private class BListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if ( cmd.equals("delete-variable") ) {
				tableModel.delete_attribute();
			}
			else { // "close"
				SymbolTableWindow.this.frame.dispose();
			}
		}

	}

	/////////////////////////////////////////////////////////
	private class TableModel extends AbstractTableModel {
		final String[] colnames = { 
			Loro.Str.get("ns.id"), Loro.Str.get("ns.type"), Loro.Str.get("ns.value") 
		};

		/////////////////////////////////////////////////////////
		public int getColumnCount() {
			return colnames.length;
		}

		/////////////////////////////////////////////////////////
		public String getColumnName(int columnIndex) {
			return colnames[columnIndex];
		}

		/////////////////////////////////////////////////////////
		public int getRowCount() {
			String[] vars = symTab.getVariableNames();
			return vars.length;
		}

		/////////////////////////////////////////////////////////
		public Object getValueAt(int row, int col) {
			String[] vars = symTab.getVariableNames();
			try {
				switch ( col ) {
					case 0:
						return vars[row];
					case 1:
						return symTab.getTypeString(vars[row]);
					case 2:
						return symTab.getValueString(vars[row]);
				}
				throw new InternalError("col=" +col);
			}
			catch(ArrayIndexOutOfBoundsException ex) {
				// IGNORE: (this happens sometimes; a Java bug?, synchronization?)
				return null;
			}
		}

		/////////////////////////////////////////////////////////
		/**
		 */
		private void delete_attribute() {
			if ( selectedRow < 0 )
				return;

			java.awt.Toolkit.getDefaultToolkit().beep();
			String[] vars = symTab.getVariableNames();
			String id = vars[selectedRow];
			symTab.borrar(id);
			tableModel.fireTableRowsDeleted(selectedRow, selectedRow);
			selectedRow = -1;
		}
	}
}
