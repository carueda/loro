package loroedi.gui.project.model;

import loroedi.gui.project.unit.*;
import loro.util.Logger;

import loro.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

//////////////////////////////////////////////////
/**
 * Implementación de IProjectModel.
 * El paquete anónimo se identifica con el nombre vacío, "". 
 *
 * @author Carlos Rueda
 */
public class ProjectModel implements IProjectModel
{
	static final IPackageModel[] EMPTY_PACKAGE_ARRAY = new IPackageModel[0];
	static final String[] EMPTY_STRING_ARRAY = new String[0];
	
	protected IInfo info;
	protected IControlInfo controlInfo;
	
	/*** Nombre de este proyecto. */
	protected String name;
	
	/** Título de este proyecto. */
	protected String title;
	
	/** Autores de este proyecto. */
	protected String authors;
	
	/** Versión de este proyecto. */
	protected String version;
	
	/** Descripción de este proyecto. */
	protected String description;
	
	/** Código de demostración de este proyecto. */
	protected String demoSrc;
	
	/** Mis paquetes. */
	protected List pkgs;

	/** Mapping from name to IPackage. */
	protected Map pkgmap;
	
	/** Mis listeners. */
	protected Set listeners;
	
	/** Proyectos de los que dependo. */
	protected List supportingProjects;
	
	/** El cargador de unidades asociado. */
	protected IOroLoader oroLoader;
	
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Crea un modelo de projecto vacío y modificable de nombre dado.
	 *
	 * @param name Nombre para el proyecto.
	 */
	public ProjectModel(String name)
	{
		this.name = name;
		info = new Info();
		controlInfo = new ControlInfo();
		pkgs = new ArrayList();
		pkgmap = new HashMap();
		listeners = new HashSet();
		supportingProjects = new ArrayList();
	}
	
	/////////////////////////////////////////////////////////////////
	public boolean isAnonymous(IPackageModel pkg)
	{
		return pkg.getName().length() == 0;
	}
	
	/////////////////////////////////////////////////////////////////
	class ControlInfo implements IControlInfo
	{
		/*** Es modificable este proyecto? */
		protected boolean modifiable = true;
		
		/*** Es válido este proyecto? */
		protected boolean valid = true;
		
		/////////////////////////////////////////////////////////////////
		public boolean isModifiable()
		{
			return modifiable;
		}
		
		/////////////////////////////////////////////////////////////////
		public void setModifiable(boolean modifiable)
		{
			this.modifiable = modifiable;
		}

		/////////////////////////////////////////////////////////////////
		public boolean isValid()
		{
			return valid;
		}
		
		/////////////////////////////////////////////////////////////////
		public void setValid(boolean valid)
		{
			this.valid = valid;
		}
	}
	
	/////////////////////////////////////////////////////////////////
	class Info implements IInfo
	{
		/////////////////////////////////////////////////////////////////
		public String getName()
		{
			return name;
		}
		
		/////////////////////////////////////////////////////////////////
		public void setName(String name)
		{
			ProjectModel.this.name = name;
		}
		
		/////////////////////////////////////////////////////////////////
		public String getTitle()
		{
			return title;
		}
	
		/////////////////////////////////////////////////////////////////
		public void setTitle(String title)
		{
			ProjectModel.this.title = title;
		}
	
		/////////////////////////////////////////////////////////////////
		public String getAuthors()
		{
			return authors;
		}
	
		/////////////////////////////////////////////////////////////////
		public void setAuthors(String authors)
		{
			ProjectModel.this.authors = authors;
		}
	
		/////////////////////////////////////////////////////////////////
		public String getVersion()
		{
			return version;
		}
	
		/////////////////////////////////////////////////////////////////
		public void setVersion(String version)
		{
			ProjectModel.this.version = version;
		}
	
		/////////////////////////////////////////////////////////////////
		public String getDescription()
		{
			return description;
		}
		
		/////////////////////////////////////////////////////////////////
		public void setDescription(String description)
		{
			ProjectModel.this.description = description;
		}
		
		/////////////////////////////////////////////////////////////////
		public String getDemoScript()
		{
			return demoSrc;
		}
		
		/////////////////////////////////////////////////////////////////
		public void setDemoScript(String src)
		{
			ProjectModel.this.demoSrc = src;
		}
	}
	
	/////////////////////////////////////////////////////////////////
	public IInfo getInfo()
	{
		return info;
	}
	
	/////////////////////////////////////////////////////////////////
	public IControlInfo getControlInfo()
	{
		return controlInfo;
	}
	
	/////////////////////////////////////////////////////////////////
	public void addSupportingProject(IProjectModel prj)
	{
		supportingProjects.add(prj);
	}

	/////////////////////////////////////////////////////////////////
	public Collection getPackages()
	{
		return pkgs;
	}

	/////////////////////////////////////////////////////////////////
	public String validateNewPackageName(String name)
	{
		String val = validateName(name);
		if ( val != null )
		{
			return val;
		}

		if ( pkgmap.keySet().contains(name) )
		{
			return "nombre ya existe";
		}
		
		return null;
	}
	
	/////////////////////////////////////////////////////////////////
	private String validateName(String name)
	{
		name = name.trim();
		if ( name.length() == 0 )
			return "Nombre vacío";
		
		if ( name.indexOf('/') >= 0 )  // posible comentario
			return "Error sintáctico: nombre mal formado.";
		
		ICompilador compilador = Loro.obtCompilador();
		compilador.ponTextoFuente(name);
		try
		{
			compilador.derivarNombre();
		}
		catch(CompilacionException ex)
		{
			return "Error sintáctico: nombre mal formado.";
			
			// No es necesario mostrar todo el mensaje:
			//return ex.getMessage();
		}
		return null;
	}
	
	////////////////////////////////////////////////////////////////
	public IPackageModel getPackage(String name)
	{
		IPackageModel pkg = (IPackageModel) pkgmap.get(name);
		return pkg;
	}

	////////////////////////////////////////////////////////////////
	public IPackageModel addPackage(String name)
	{
		// see if package already exists:
		IPackageModel pkg = (IPackageModel) pkgmap.get(name);
		if ( pkg == null )
		{
			pkg = new PackageModel(this, name);
			pkgs.add(pkg);
			pkgmap.put(pkg.getName(), pkg);
			notifyListeners(new ProjectModelEvent(this, ProjectModelEvent.PACKAGE_ADDED, pkg));
		}
		return pkg;
	}

	////////////////////////////////////////////////////////////////
	public boolean removePackage(IPackageModel pkgm)
	{
		String pkg_name = pkgm.getName();
		if ( pkgm == pkgmap.get(pkg_name) )
		{
			pkgmap.remove(pkg_name);
			pkgs.remove(pkgm);
			return true;
		}
		return false;
	}

	////////////////////////////////////////////////////////////////
	public Collection getAlgorithms(SpecificationUnit spec)
	{
		String q_spec_name = spec.getQualifiedName();
		List list = new ArrayList();
		for ( Iterator it = pkgs.iterator(); it.hasNext(); )
		{
			IPackageModel pkg = (IPackageModel) it.next();
			for ( Iterator itt = pkg.getAlgorithmNames().iterator(); itt.hasNext(); )
			{
				String alg_name = (String) itt.next();
				AlgorithmUnit alg = pkg.getAlgorithm(alg_name);
				String qsn = alg.getSpecificationName();
				if ( q_spec_name.equals(qsn) )
				{
					list.add(alg);
				}
			}
		}
		return list;
	}
	
	/////////////////////////////////////////////////////////////////
	public SpecificationUnit getSpecification(String q_spec_name)
	{
		int index = q_spec_name.lastIndexOf("::");
		if ( index < 0 )  // nombre simple
		{
			// busque en el paquete anónimo, si existe:
			IPackageModel pkg = (IPackageModel) pkgmap.get("");
			if ( pkg != null )
			{
				return pkg.getSpecification(q_spec_name);
			}
		}
		else
		{
			// primero obtenga el paquete:
			String pkg_name = q_spec_name.substring(0, index);
			IPackageModel pkg = (IPackageModel) pkgmap.get(pkg_name);
			if ( pkg != null )
			{
				// busque el nombre simple:  +2 por "::"
				String spec_name = q_spec_name.substring(index + 2);
				return pkg.getSpecification(spec_name);
			}
		}
		return null;
	}
	
	/////////////////////////////////////////////////////////////////
	public SpecificationUnit getAlgorithmSpecification(AlgorithmUnit alg)
	{
		String spec_name = alg.getSpecificationName();
		return spec_name == null ? null : getSpecification(spec_name);
	}
	
	/////////////////////////////////////////////////////////////////
	public AlgorithmUnit getAlgorithm(String q_alg_name)
	{
		int index = q_alg_name.lastIndexOf("::");
		if ( index < 0 )  // nombre simple
		{
			// busque en el paquete anónimo, si existe:
			IPackageModel pkg = (IPackageModel) pkgmap.get("");
			if ( pkg != null )
			{
				return pkg.getAlgorithm(q_alg_name);
			}
		}
		else
		{
			// primero obtenga el paquete:
			String pkg_name = q_alg_name.substring(0, index);
			IPackageModel pkg = (IPackageModel) pkgmap.get(pkg_name);
			if ( pkg != null )
			{
				// busque el nombre simple:  +2 por "::"
				String name = q_alg_name.substring(index + 2);
				return pkg.getAlgorithm(name);
			}
		}
		return null;
	}
	
	////////////////////////////////////////////////////////////////
	public ClassUnit getClass(String q_class_name)
	{
		int index = q_class_name.lastIndexOf("::");
		if ( index < 0 )  // nombre simple
		{
			// busque en el paquete anónimo, si existe:
			IPackageModel pkg = (IPackageModel) pkgmap.get("");
			if ( pkg != null )
			{
				return pkg.getClass(q_class_name);
			}
		}
		else
		{
			// primero obtenga el paquete:
			String pkg_name = q_class_name.substring(0, index);
			IPackageModel pkg = (IPackageModel) pkgmap.get(pkg_name);
			if ( pkg != null )
			{
				// busque el nombre simple:  +2 por "::"
				String name = q_class_name.substring(index + 2);
				return pkg.getClass(name);
			}
		}
		return null;
	}

	/////////////////////////////////////////////////////////////////
	public Collection getSupportingProjects()
	{
		return supportingProjects;
	}

	/////////////////////////////////////////////////////////////////
	public void addProjectModelListener(IProjectModelListener lis)
	{
		listeners.add(lis);
	}
	
	/////////////////////////////////////////////////////////////////
	public void removeProjectModelListener(IProjectModelListener lis)
	{
		listeners.remove(lis);
	}
	
	/////////////////////////////////////////////////////////////////
	public void notifyListeners(ProjectModelEvent ev)
	{
		Set s_col = Collections.synchronizedSet(listeners);
		synchronized(s_col) 
		{
			for ( Iterator it = s_col.iterator(); it.hasNext(); )
			{
				IProjectModelListener lis = (IProjectModelListener) it.next();
				lis.action(ev);
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////
	public String toString()
	{
		return name;
	}

	/////////////////////////////////////////////////////////////////
	public IOroLoader getOroLoader()
	{
		return oroLoader;
	}
	
	/////////////////////////////////////////////////////////////////
	public void setOroLoader(IOroLoader oroLoader)
	{
		this.oroLoader = oroLoader;
	}
}
