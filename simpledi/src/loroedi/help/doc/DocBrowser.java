package loroedi.help.doc;

import loroedi.Preferencias;
import loroedi.help.BrowserPanel;

import javax.swing.*;
import javax.swing.tree.*;
import java.net.URL;
import java.net.URLConnection;
import java.awt.event.*;
import java.io.IOException;
import java.io.File;
import java.awt.Toolkit;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Point;
import java.net.URL;

/////////////////////////////////////////////////////////
/**
 */
public class DocBrowser extends ComponentAdapter
implements ActionListener
{
	private JFrame frame;
	private JTree tree;
	private JSplitPane splitPane1;

	private BrowserPanel bp;

	private JComponent msjTextArea;
	
	private String doc_dir;
	private String search_extension;


	////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor
	 */
	public DocBrowser(String doc_dir, String search_extension)
	{
		this(doc_dir, DocNodeFactory.getJTree(doc_dir, search_extension));
	}
	
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Constructor
	 */
	public DocBrowser(String doc_dir, JTree tree)
	{
		this.doc_dir = doc_dir;
		this.tree = tree;
		frame = new JFrame("Documentación");
		URL url = getClass().getClassLoader().getResource("img/icon.jpg");
		if ( url != null ) 
			frame.setIconImage(new ImageIcon(url).getImage());
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent _)
			{
				frame.setVisible(false);
			}
		});
		//frame.setJMenuBar(getJFrame1JMenuBar());

		JPanel cp = new JPanel(new java.awt.BorderLayout());
		frame.setContentPane(cp);

		splitPane1 = getJSplitPane1();

		JPanel left_panel = new JPanel(new java.awt.BorderLayout());

		splitPane1.add(left_panel, "left");
		splitPane1.add(getBrowserPanel(), "right");
		splitPane1.setDividerLocation(200);

		JScrollPane sp = new JScrollPane(
			tree,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
		);
		sp.setMinimumSize(new java.awt.Dimension(100, 100));
		left_panel.add(sp);

		JButton refresh = new JButton(loroedi.Util.getIcon("img/Refresh24.gif"));
		refresh.setActionCommand("refresh");
		refresh.setToolTipText("Actualiza la documentación por recientes compilaciones");
		refresh.addActionListener(this);
		left_panel.add(refresh, "North");

		cp.add(splitPane1, "Center");

		Rectangle rect = Preferencias.obtRectangulo(Preferencias.DOC_RECT);
		frame.setLocation(rect.x, rect.y);
		frame.setSize(rect.width, rect.height);
		frame.addComponentListener(new ComponentAdapter()
		{
			void common()
			{
				Rectangle rect_ = new Rectangle(frame.getLocationOnScreen(), frame.getSize());
				Preferencias.ponRectangulo(Preferencias.DOC_RECT, rect_);
			}
			public void componentResized(ComponentEvent e){common();}
			public void componentMoved(ComponentEvent e){common();}
		});


		MouseListener ml = new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				int selRow = DocBrowser.this.tree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = DocBrowser.this.tree.getPathForLocation(e.getX(), e.getY());
				if (selRow != -1 )	//&& e.getClickCount() == 1 )
				{
					DocNode dt = (DocNode) selPath.getLastPathComponent();
					if ( dt.name instanceof File )
					{
						File file = (File) dt.name;
						try
						{
							if ( file.isFile() )
							{
								// to force update
								bp.setTextHTML("");

								bp.setPage(file.toURL());
							}
						}
						catch ( Exception ex )
						{
							System.out.println("Exception: " +ex.getMessage());
						}
					}
					else if ( dt.isLeaf() && dt.name instanceof String )
					{
						String name = (String) dt.name;
						try
						{
							File file = new File(DocBrowser.this.doc_dir, name + ".html");
							bp.setPage(file.toURL());
							//bp.setTextHTML(name);
						}
						catch ( Exception ex )
						{
							System.out.println("Exception: " +ex.getMessage());
						}
					}
					else
					{
						bp.setTextHTML("<b>" +dt.name+ "</b>");
					}
				}
			}
		};
		tree.addMouseListener(ml);
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * To attend "refresh"
	 */
	public void actionPerformed(ActionEvent e)
	{
		TreePath tp = tree.getSelectionPath();
		setDirectory(Preferencias.obtPreferencia(Preferencias.DOC_DIR));
		if ( tp != null )
		{
			tree.setSelectionPath(tp);
			tree.expandPath(tp);
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the directory for the tree an updates the visualization.
	 */
	public void setDirectory(String directory)
	{
//		DocNode dn = DocNodeFactory.createDocNode(directory, search_extension);
//		setNodeTree(dn)
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Sets the tree structure to visualize.
	 */
	public void setNodeTree(DocNode dn)
	{
		DocNodeFactory.setDocNode(tree, dn);
		
		tree.repaint();
	}

	////////////////////////////////////////////////////////////////////////////
	public void display()
	{
		if ( !frame.isShowing() )
		{
			frame.setVisible(true);
		}
		frame.toFront();
	}

	////////////////////////////////////////////////////////////////////////////
	public void componentResized(java.awt.event.ComponentEvent e)
	{
		splitPane1.setDividerLocation(.3);
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 */
	public javax.swing.JComponent getBrowserPanel()
	{
		if (bp == null)
		{
			bp = new BrowserPanel(null, null);
			bp.setTextHTML("<i>(Seleccione un elemento para visualizar su documentacion)</i>");
		}
		return bp;
	}

	////////////////////////////////////////////////////////////////////////////
	/**
	 */
	public JSplitPane getJSplitPane1()
	{
			JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			sp.setDividerSize(6);
			sp.setAutoscrolls(false);
			sp.setContinuousLayout(false);
			sp.setMinimumSize(new java.awt.Dimension(50, 200));
			//sp.setPreferredSize(new java.awt.Dimension(500, 700));
			sp.setDividerLocation(.3);
			return sp;
	}


	////////////////////////////////////////////////////////////////////////////
	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args)
	{
		DocBrowser docBrowser = new DocBrowser(args[0], ".html");
		docBrowser.display();
	}
}
