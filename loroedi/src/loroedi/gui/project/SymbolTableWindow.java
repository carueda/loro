package loroedi.gui.project;

import loroedi.Preferencias;

import loro.ISymbolTable;
import loroedi.gui.*;
import loroedi.gui.project.model.*;
import loroedi.gui.project.unit.*;

import loroedi.gui.misc.*;
import loroedi.Preferencias;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.*;
import java.awt.*;



/////////////////////////////////////////////////////////
/**
 * Ventana para acceder a la tabla de símbolos común.
 * Esta es una "singleton".
 * createInstance crea la instancia.
 * getInstance obtiene la instancia.
 *
 * @author Carlos Rueda
 * @version 2002-10-02
 */
public class SymbolTableWindow
{
	private static SymbolTableWindow instance;
	
	/////////////////////////////////////////////////////////
	/**
	 * Crea la instancia única de esta clase. 
	 *
	 * @param symTab La tabla de símbolos a visualizar.
	 */
	public static SymbolTableWindow createInstance(ISymbolTable symTab)
	{
		instance = new SymbolTableWindow(symTab);
		return instance;
	}
	
	/////////////////////////////////////////////////////////
	/**
	 * Obtiene la instancia de esta clase.
	 *
	 * @throws IllegalStateException si no se ha llamado promero createInstance.
	 */
	public static SymbolTableWindow getInstance()
	{
		if ( instance == null )
		{
			throw new IllegalStateException("SymbolTableWindow no creado");
		}
		return instance;
	}
	
	private ISymbolTable symTab;
	private JFrame frame = null;

	private TableModel tableModel;
	private JTable table;
	private int selectedRow;

	/////////////////////////////////////////////////////////
	private SymbolTableWindow(ISymbolTable symTab)
	{
		this.symTab = symTab;
		frame = new JFrame("Declaraciones");
		JPanel cp = new JPanel(new BorderLayout());
		cp.setBorder(BorderFactory.createTitledBorder("Variables declaradas"));
		frame.setContentPane(cp);

		tableModel = new TableModel();
		table = new JTable(tableModel);
		table.setPreferredScrollableViewportSize(new Dimension(300, 100));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		selectedRow = -1;
		frame.getContentPane().add(new JScrollPane(table));

		ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				//Ignore extra messages.
				if (e.getValueIsAdjusting())
				{
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
		b = new JButton("Borrar variable");
		b.setActionCommand("delete-variable");
		b.addActionListener(lis);
		buttons.add(b);
		b = new JButton("Cerrar");
		b.setActionCommand("close");
		b.addActionListener(lis);
		buttons.add(b);

		Rectangle rect = Preferencias.obtRectangulo(Preferencias.SYMTAB_RECT);
		frame.setLocation(rect.x, rect.y);
		frame.setSize(rect.width, rect.height);
		frame.addComponentListener(new ComponentAdapter()
		{
			void common()
			{
				Rectangle rect_ = new Rectangle(frame.getLocationOnScreen(), frame.getSize());
				Preferencias.ponRectangulo(Preferencias.SYMTAB_RECT, rect_);
			}
			public void componentResized(ComponentEvent e){common();}
			public void componentMoved(ComponentEvent e){common();}
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
	 * Actualiza el contenido de la ventana.
	 */
	public void update()
	{
		tableModel.fireTableDataChanged();
	}

	/////////////////////////////////////////////////////////
	private class BListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			String cmd = e.getActionCommand();
			if ( cmd.equals("delete-variable") )
			{
				tableModel.delete_attribute();
			}
			else // "close"
			{
				SymbolTableWindow.this.frame.dispose();
			}
		}

	}

	/////////////////////////////////////////////////////////
	private class TableModel extends AbstractTableModel
	{
		final String[] colnames = { "Variable", "Tipo", "Valor" };

		/////////////////////////////////////////////////////////
		public int getColumnCount()
		{
			return colnames.length;
		}

		/////////////////////////////////////////////////////////
		public String getColumnName(int columnIndex)
		{
			return colnames[columnIndex];
		}

		/////////////////////////////////////////////////////////
		public int getRowCount()
		{
			String[] vars = symTab.getVariableNames();
			return vars.length;
		}

		/////////////////////////////////////////////////////////
		public Object getValueAt(int row, int col)
		{
			String[] vars = symTab.getVariableNames();
			switch ( col )
			{
				case 0:
					return vars[row];
				case 1:
					return symTab.getTypeString(vars[row]);
				case 2:
					return symTab.getValueString(vars[row]);
			}
			throw new InternalError("col=" +col);
		}

		/////////////////////////////////////////////////////////
		/**
		 */
		private void delete_attribute()
		{
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
