package loroedi.gui.project;

import loroedi.gui.project.model.*;
import loroedi.gui.project.unit.*;

import com.jgraph.JGraph;
import com.jgraph.graph.*;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;


/////////////////////////////////////////////////////////
/**
 * Diagrama para un proyecto.
 *
 * @author Carlos Rueda
 * @version 2002-08-13
 */
public class Diagram extends JPanel
{
	protected static final float[] dashPattern = {4, 2};

	/** Font para los «stereotypes» */
	protected static Font stereoFont = new Font("Helvetica", Font.PLAIN, 10);
	
	/** Datos del font para los nombres de paquetes. */
	protected static String pkgFontName = "monospaced";
	protected static int pkgFontStyle = Font.BOLD;
	protected static int pkgFontSize = 12;

	/** Datos del font para los nombres de las unidades. */
	protected static String unitFontName = "Helvetica";
	protected static int unitFontStyle = Font.BOLD;
	protected static int unitFontSize = 14;

	protected static Color spec_bg_color = new Color(204, 255, 255);
	protected static Color alg_bg_color = new Color(168, 251, 181);
	protected static Color class_bg_color = new Color(255, 255, 160);
	

	protected boolean useColors = true;
	
	protected Project project;
	protected JToolBar tb;
	protected JGraph graph;
	protected PMListener pml;
	
	/** Mapping from unit names to Ports */ 
	protected Map ports;
	
	/** Mapping from q_spec_name to Set of alg_names */
	protected Map spec_algnames;
	

	////////////////////////////////////////////////////////////////////////////
	/**
	 * Crea un diagrama vacío.
	 */
	public Diagram(Project project)
	{
		super(new GridLayout(1,1));
		this.project = project;
		GraphModel graphModel = new DefaultGraphModel();
		graph = new DJGraph(graphModel);
		graph.setCloneable(false);
		graph.setAntiAliased(true);
		add(new JScrollPane(graph));
		graph.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				Object cell = graph.getFirstCellForLocation(e.getX(), e.getY());
				if ( cell instanceof ICell )
				{
					Diagram.this.project.click((ICell) cell, e);
				}
			}
		});
		graph.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e) 
			{
				Object[] cells = graph.getSelectionCells();
				if ( cells.length == 1 && cells[0] instanceof ICell )
				{
					Diagram.this.project.keyPressed((ICell) cells[0], e);
				}
			}
		});
		
		ports = new HashMap();
		spec_algnames = new HashMap();
		
		pml = new PMListener();
		
		IProjectModel model = project.getModel();
		if ( model != null )
		{
			setupModel(model);
		}
	}

	
	////////////////////////////////////////////////////////////////////////////
	/**
	 * Reestablece el modelo a desplegar en el diagrama.
	 */
	public void reset() 
	{
		ports = new HashMap();
		spec_algnames = new HashMap();
		graph.setModel(new DefaultGraphModel());
		setupModel(project.getModel());
	}
	
	////////////////////////////////////////////////////////////////////////////
	void setupModel(IProjectModel model) 
	{
		// para comparar unidades de acuerdo con el nombre simple:
		Comparator cmp = new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				IProjectUnit u1 = (IProjectUnit) o1;
				IProjectUnit u2 = (IProjectUnit) o2;
				
				String n1 = u1.getName();
				String n2 = u2.getName();
				return n1.compareTo(n2);
			}
		};
			
		model.addProjectModelListener(pml);
		
		for ( Iterator it = model.getPackages().iterator(); it.hasNext(); )
		{
			IPackageModel pkg = (IPackageModel) it.next();
			pml.packageAdded(pkg);

			////////////////////////////////////////////////////////////////////////
			// specs:
			List specs = new ArrayList();
			for ( Iterator it2 = pkg.getSpecNames().iterator(); it2.hasNext(); )
			{
				String spec_name = (String) it2.next();
				SpecificationUnit spec = pkg.getSpecification(spec_name);
				specs.add(spec);
			}
			// ordene las specs por orden alfabético:
			Collections.sort(specs, cmp);
			for ( Iterator it2 = specs.iterator(); it2.hasNext(); )
			{
				SpecificationUnit spec = (SpecificationUnit) it2.next();
				pml.specificationAdded(spec);
			}

			////////////////////////////////////////////////////////////////////////			
			// algs:
			List algs = new ArrayList();
			for ( Iterator it2 = pkg.getAlgorithmNames().iterator(); it2.hasNext(); )
			{
				String alg_name = (String) it2.next();
				AlgorithmUnit alg = pkg.getAlgorithm(alg_name);
				algs.add(alg);
			}
			// ordene los algoritmos para que queden consecutivos los que implementan
			// la misma especificación:
			// Nótese que no se revisa si la especificación pertenece efectivamente
			// a este mismo proyecto (esto no es crítico para el efecto deseado).
			Collections.sort(algs, new Comparator()
			{
				public int compare(Object o1, Object o2)
				{
					AlgorithmUnit alg1 = (AlgorithmUnit) o1;
					AlgorithmUnit alg2 = (AlgorithmUnit) o2;
					
					String specname1 = alg1.getSpecificationName();
					String specname2 = alg2.getSpecificationName();
					if ( specname1.equals(specname2) )
					{
						// los algoritmos son para la misma especificación:
						// simplemente ordene de manera lexicográfica:
						return alg1.getName().compareTo(alg2.getName());
					}
					else
					{
						// especificaciones distintas: utilice los nombres de
						// dichas especificaciones para la decisión:
						return specname1.compareTo(specname2);
					}
				}
			});
			// ahora sí ponga los algoritmos en el diagrama:			
			for ( Iterator it2 = algs.iterator(); it2.hasNext(); )
			{
				AlgorithmUnit alg = (AlgorithmUnit) it2.next();
				pml.algorithmAdded(alg);
			}

			
			////////////////////////////////////////////////////////////////////////
			// classes:
			List classes = new ArrayList();
			for ( Iterator it2 = pkg.getClassNames().iterator(); it2.hasNext(); )
			{
				String class_name = (String) it2.next();
				ClassUnit clazz = pkg.getClass(class_name);
				classes.add(clazz);
			}
			// ordene las specs por orden alfabético:
			Collections.sort(classes, cmp);
			for ( Iterator it2 = classes.iterator(); it2.hasNext(); )
			{
				ClassUnit clazz = (ClassUnit) it2.next();
				pml.classAdded(clazz);
			}
		}
		
		graph.scrollPointToVisible(new Point(0, 0));
	}
	
	// Insert a new Vertex at point
	public Port insertVertex(Object obj, Point point) 
	{
		DCell vertex = new DCell(obj);
		DefaultPort port = new DefaultPort();
		vertex.add(port);
		point = graph.snap(new Point(point));
		Map map = GraphConstants.createMap();
		GraphConstants.setEditable(map, false);
		GraphConstants.setBorderColor(map, Color.black);
		GraphConstants.setBackground(map, Color.white);
		GraphConstants.setOpaque(map, true);
		if ( obj instanceof IPackageModel )
		{
			GraphConstants.setFontName(map, pkgFontName);
			GraphConstants.setFontStyle(map, pkgFontStyle);
			GraphConstants.setFontSize(map, pkgFontSize);
			
			GraphConstants.setBounds(map, 
				new Rectangle(point, new Dimension(1600,200))
			);
			GraphConstants.setAutoSize(map, false);
			GraphConstants.setSizeable(map, false);
			GraphConstants.setMoveable(map, false);
			
			GraphConstants.setHorizontalAlignment(map, JLabel.LEFT);
			GraphConstants.setVerticalAlignment(map, JLabel.TOP);
			
			Hashtable attributes = new Hashtable();
			attributes.put(vertex, map);
			graph.getModel().insert(new Object[]{vertex}, null, null, attributes);
			graph.setSelectionCell(vertex);
			graph.scrollCellToVisible(vertex);
			return port;
		}
		
		if ( useColors ) 
		{
			if ( obj instanceof SpecificationUnit )
			{
				GraphConstants.setBackground(map, spec_bg_color);
			}
			else if ( obj instanceof AlgorithmUnit )
			{
				GraphConstants.setBackground(map, alg_bg_color);
			}
			else if ( obj instanceof ClassUnit )
			{
				GraphConstants.setBackground(map, class_bg_color);
			}
		}
		
		GraphConstants.setFontName(map, unitFontName);
		GraphConstants.setFontStyle(map, unitFontStyle);
		GraphConstants.setFontSize(map, unitFontSize);
		GraphConstants.setVerticalAlignment(map, JLabel.BOTTOM);

		Dimension dim = DiagramUtil.getSize(
			DiagramUtil.getStereo(obj),
			stereoFont,
			obj.toString(),
			GraphConstants.getFont(map)
		);
		GraphConstants.setBounds(map, new Rectangle(point, dim));
		GraphConstants.setAutoSize(map, false);
		GraphConstants.setSizeable(map, false);
		Hashtable attributes = new Hashtable();
		attributes.put(vertex, map);
		graph.getModel().insert(new Object[]{vertex}, null, null, attributes);
		graph.setSelectionCell(vertex);
		graph.scrollCellToVisible(vertex);
		return port;
	}
	
	// Insert a new Edge between source and target
	public void connect(Port source, Port target) 
	{
		ConnectionSet cs = new ConnectionSet();
		DefaultEdge edge = new DefaultEdge();
		cs.connect(edge, source, target);
		Map map = GraphConstants.createMap();
		GraphConstants.setLineEnd(map, GraphConstants.TECHNICAL);
		GraphConstants.setLineStyle(map, GraphConstants.BEZIER);
		GraphConstants.setEditable(map, false);
		GraphConstants.setDisconnectable(map, false);
		GraphConstants.setDashPattern(map, dashPattern);
		Hashtable attributes = new Hashtable();
		attributes.put(edge, map);
		graph.getModel().insert(new Object[]{edge}, cs, null, attributes);
	}

	////////////////////////////////////////////////////////////
	Point findPointForPackage(IPackageModel pkg) 
	{
		Object[] roots = graph.getRoots();
		if ( roots != null )
		{
			for ( int i = 0; i < roots.length; i++ )
			{
				Object cell = roots[i];
				if ( cell instanceof DCell
				&&  ((DCell) cell).getUserObject() instanceof IPackageModel ) 
				{
					IPackageModel pkg2 = (IPackageModel) ((DCell) cell).getUserObject();
					if ( pkg.getName().equals(pkg2.getName()) )
					{
						Rectangle rect = graph.getCellBounds(cell); 
						return rect.getLocation();
					}
				}
			}
		}
		return new Point(0, 0); // shouldn't happen, but...
	}
	
	////////////////////////////////////////////////////////////
	Point findPointForNewPackage(Point p, String title) 
	{
		Object[] roots = graph.getRoots();
		int y = 10;
		if ( roots != null )
		{
			Rectangle rect = new Rectangle();
			for ( int i = 0; i < roots.length; i++ )
			{
				Object cell = roots[i];
				if ( cell instanceof DCell
				&&  ((DCell) cell).getUserObject() instanceof IPackageModel ) 
				{
					rect.add(graph.getCellBounds(cell));
				}
			}
			y += rect.y + rect.height;
		}
		return new Point(p.x + 10, p.y + y);
	}
	
	////////////////////////////////////////////////////////////
	Point findPointForNewSpecification(Point p, String title) 
	{
		Object[] roots = graph.getRoots();
		int x = 20;
		if ( roots != null )
		{
			Rectangle rect = new Rectangle();
			for ( int i = 0; i < roots.length; i++ )
			{
				Object cell = roots[i];
				if ( cell instanceof DCell
				&&   ((DCell) cell).getUserObject() instanceof SpecificationUnit )
				{
					Rectangle r2 = graph.getCellBounds(cell);
					if ( p.y + 25 == r2.y )
					{
						rect.add(r2);
					}
				}
			}
			x += rect.x + rect.width;
		}
		return new Point(p.x + x, p.y + 25);
	}

	////////////////////////////////////////////////////////////
	Point findPointForNewAlgorithm(Point p, String title) 
	{
		Object[] roots = graph.getRoots();
		int x = 20;
		if ( roots != null )
		{
			Rectangle rect = new Rectangle();
			for ( int i = 0; i < roots.length; i++ )
			{
				Object cell = roots[i];
				if ( cell instanceof DCell
				&&  ((DCell) cell).getUserObject() instanceof AlgorithmUnit ) 
				{
					Rectangle r2 = graph.getCellBounds(cell);
					if ( p.y + 85 == r2.y )
					{
						rect.add(r2);
					}
				}
			}
			x += rect.x + rect.width;
		}
		return new Point(p.x + x, p.y + 85);
	}
	
	////////////////////////////////////////////////////////////
	Point findPointForNewClass(Point p, String title) 
	{
		Object[] roots = graph.getRoots();
		int x = 20;
		if ( roots != null )
		{
			Rectangle rect = new Rectangle();
			for ( int i = 0; i < roots.length; i++ )
			{
				Object cell = roots[i];
				if ( cell instanceof DCell
				&&   ((DCell) cell).getUserObject() instanceof ClassUnit )
				{
					Rectangle r2 = graph.getCellBounds(cell);
					if ( p.y + 145 == r2.y )
					{
						rect.add(r2);
					}
				}
			}
			x += rect.x + rect.width;
		}
		return new Point(p.x + x, p.y + 145);
	}

	////////////////////////////////////////////////////////////
	/**
	 * Busca la celda cuyo objeto de usuario es igual (==) al 
	 * objeto dado.
	 */
	public ICell findCell(Object userObject) 
	{
		Object[] roots = graph.getRoots();
		if ( roots != null )
		{
			for ( int i = 0; i < roots.length; i++ )
			{
				Object cell = roots[i];
				if ( cell instanceof ICell
				&&  ((ICell) cell).getUserObject() == userObject ) 
				{
					return (ICell) cell;
				}
			}
		}
		return null;
	}

	////////////////////////////////////////////////////////////
	public void zoom(double z) 
	{
		if ( z > 0.0 )
		{
			graph.setScale(graph.getScale()*z);
		}
		else
		{
			graph.setScale(1);
		}
		if (graph.getSelectionCell() != null)
			graph.scrollCellToVisible(graph.getSelectionCell());
	}
	
	
	////////////////////////////////////////////////////////////
	/**
	 * Elimina una celda del diagrama así como sus arcos adyacentes.
	 */
	public void removeCell(ICell cell) 
	{
		GraphModel graphModel = graph.getModel();
		Object[] cells = new Object[]{ cell };
		Set edges = DefaultGraphModel.getEdges(graphModel, cells);
		graphModel.remove(cells);
		graphModel.remove(edges.toArray());
	}

	/////////////////////////////////////////////////////////////////////
	class DCell extends DefaultGraphCell implements ICell
	{
		public DCell()
		{
			this(null);
		}
		public DCell(Object userObject)
		{
			super(userObject);
		}
	}
	
	static DRenderer my_renderer = new DRenderer();
	
	/////////////////////////////////////////////////////////////////////
	static class DRenderer extends VertexRenderer
	{
		public void paint(Graphics gg)
		{
			super.paint(gg);
			
			DCell cell = (DCell) view.getCell();
			Object obj = cell.getUserObject();

			if ( obj instanceof IPackageModel )
			{
				return;
			}
			
			Graphics2D g = (Graphics2D) gg;
			
			String stereo = DiagramUtil.getStereo(obj);
			
			FontRenderContext frc = g.getFontRenderContext();
			double stereo_height = new TextLayout("H", stereoFont, frc)
				.getBounds().getHeight()
			;
			TextLayout stereo_layout = new TextLayout(stereo, stereoFont, frc);
			Rectangle2D stereo_bounds = stereo_layout.getBounds();

			Rectangle r = view.getBounds();
			double x = (r.width - stereo_bounds.getWidth()) / 2;
			double y = stereo_height + 4;
			
			g.setColor(Color.black);
			stereo_layout.draw(g, (float) x, (float) y);
			
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	class DJGraph extends JGraph
	{
		/////////////////////////////////////////////////////////////////
		DJGraph(GraphModel graphModel)
		{
			super(graphModel);
		}
		
		/////////////////////////////////////////////////////////////////
		protected VertexView createVertexView(Object v, CellMapper cm)
		{
			if ( v instanceof DCell )
			{
				return new DView(v, cm);
			}
			else
			{
				return super.createVertexView(v, cm);
			}
		}
	
		/////////////////////////////////////////////////////////////////////
		class DView extends VertexView
		{
			// para memorizar mi posición vertical
			int mem_y = -1;
			
			/////////////////////////////////////////////////////////////////
			public DView(Object v, CellMapper cm)
			{
				super(v, DJGraph.this, cm);
			}
			
			/////////////////////////////////////////////////////////////////
			public CellViewRenderer getRenderer()
			{
				return my_renderer;
			}
			
			/////////////////////////////////////////////////////////////////
			/**
			 * Sobreescrito para restrigir posición vertical
			 */
			public void update() 
			{
				super.update();
				bounds = GraphConstants.getBounds(attributes);
				
				if ( bounds.y > 0 )
				{
					// bounds.y > 0 significa una asignación explícita.
					
					if ( mem_y < 0 )
					{
						// memorizar mi posición.
						mem_y = bounds.y;
					}
					else if ( bounds.y != mem_y )
					{
						// obligamos que la posición vertical se mantenga:
						bounds.y = mem_y;
						GraphConstants.setBounds(attributes, bounds);
					}
				}
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////////
	class PMListener implements IProjectModelListener
	{
		/////////////////////////////////////////////////////////////////
		// ProjectListener method:
		public void action(ProjectModelEvent e)
		{
			switch ( e.getID() )
			{
				case ProjectModelEvent.PACKAGE_ADDED:
					packageAdded((IPackageModel) e.getElement());
					break;
				case ProjectModelEvent.SPEC_ADDED:
					specificationAdded((SpecificationUnit) e.getElement());
					break;
				case ProjectModelEvent.ALGORITHM_ADDED:
					algorithmAdded((AlgorithmUnit) e.getElement());
					break;
				case ProjectModelEvent.CLASS_ADDED:
					classAdded((ClassUnit) e.getElement());
					break;

				case ProjectModelEvent.PACKAGE_REMOVED:
					packageRemoved((IPackageModel) e.getElement());
					break;
					
				case ProjectModelEvent.SPEC_REMOVED:
				case ProjectModelEvent.ALGORITHM_REMOVED:
				case ProjectModelEvent.CLASS_REMOVED:
					ICell cell = findCell(e.getElement());
					if ( cell != null )
					{
						removeCell(cell);
					}
					break;
			}
		}
		
		/////////////////////////////////////////////////////////////////
		void packageAdded(IPackageModel pkg)
		{
			Point point = findPointForNewPackage(new Point(0, 0), pkg.getName());
			insertVertex(pkg, point);
		}
		
		////////////////////////////////////////////////////////////////////////////
		void packageRemoved(IPackageModel pkg) 
		{
			reset();
		}
		
		/////////////////////////////////////////////////////////////////
		void specificationAdded(SpecificationUnit unit)
		{
			Point pkg_point = findPointForPackage(unit.getPackage());
			String spec_name = unit.getName();
			Point point = findPointForNewSpecification(pkg_point, spec_name);
			Port spec_port = insertVertex(unit, point);
			String q_spec_name = unit.getQualifiedName();
			ports.put("e-" +q_spec_name, spec_port);
			
			// find possible algorithms for this spec:
			Set algnames = (Set) spec_algnames.get(q_spec_name);
			if ( algnames != null )
			{
				for ( Iterator it = algnames.iterator(); it.hasNext(); )
				{
					String algname = (String) it.next();
					// pending connection
					Port alg_port = (Port) ports.get("a-" +algname);
					if ( alg_port != null )
					{
						// se había insertado primero el algoritmo
						connect(alg_port, spec_port);
					}
				}
			}
		}
	
		/////////////////////////////////////////////////////////////////
		void algorithmAdded(AlgorithmUnit unit)
		{
			Point pkg_point = findPointForPackage(unit.getPackage());
			String alg_name = unit.getName();
			Point point = findPointForNewAlgorithm(pkg_point, alg_name);
			Port alg_port = insertVertex(unit, point);
			ports.put("a-" +alg_name, alg_port);
			String q_spec_name = unit.getSpecificationName();
			Port spec_port = (Port) ports.get("e-" +q_spec_name);
			if ( spec_port != null )
			{
				connect(alg_port, spec_port);
			}
			// si spec_port fuera null, es porque la especificación es de 
			// otro proyecto o aún no se ha insertado en éste.
			
			// tenga en cuenta esta asociación q_spec_name-alg_name 
			Set algnames = (Set) spec_algnames.get(q_spec_name);
			if ( algnames == null )
			{
				algnames = new HashSet();
				spec_algnames.put(q_spec_name, algnames);
			}
			algnames.add(alg_name);
		}
	
		/////////////////////////////////////////////////////////////////
		void classAdded(ClassUnit unit)
		{
			Point pkg_point = findPointForPackage(unit.getPackage());
			String class_name = unit.getName();
			Point point = findPointForNewClass(pkg_point, class_name);
			Port port = insertVertex(unit, point);
			ports.put("c-" +class_name, port);
		}
	}

}
