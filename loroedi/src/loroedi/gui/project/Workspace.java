package loroedi.gui.project;

import loroedi.gui.GUI;
import loroedi.gui.project.model.*;
import loroedi.gui.project.unit.*;
import loroedi.gui.editor.UEditor;

import loroedi.InterpreterWindow;
import loroedi.Util;
import loroedi.Info.Str;

import loro.*;

import java.util.*;
import java.util.zip.ZipOutputStream;
import java.io.*;

//////////////////////////////////////////////////
/**
 * Manages the set of available projects.
 *
 * Available projects are:
 * <ul>
 *	<li> Those that are core extensions (read only).
 *	<li> Those found under the project directory (modifiable).
 * </ul>
 *
 * Project names are not case sensitive.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public final class Workspace
{
	private static Workspace instance = null;
	
	//////////////////////////////////////////////////
	/**
	 * Obtiene la instancia de esta clase.
	 *
	 * @throws Exception Si no existe el directorio prs_dir.
	 * @throws IllegalStateException Si la instancia ya está creada.
	 */
	public static Workspace createInstance(String prs_directory)
	throws Exception
	{
		if ( instance != null )
		{
			throw new IllegalStateException();
		}
		instance = new Workspace(prs_directory);
		return instance;
	}
	
	//////////////////////////////////////////////////
	/**
	 * Obtiene la instancia de esta clase.
	 *
	 * @throws IllegalStateException Si la instancia no ya está creada.
	 */
	public static Workspace getInstance()
	{
		if ( instance == null )
		{
			throw new IllegalStateException();
		}
		return instance;
	}
	
	/** List of names of available projects */
	List prjnames;
	
	/**
	 * Mapping from project name (in lower case) to either 
	 * Extension, String, or IProjectModel. 
	 */
	Map name_prj;
	
	/** Directory with projects. */
	File prs_dir;
	
	PMListener pmlistener;


	EDICompiler ediCompiler;
	
	/////////////////////////////////////////////////////////////////
	private Workspace(String prs_directory)
	throws Exception
	{
		pmlistener = new PMListener();
		ediCompiler = new EDICompiler();
		prjnames = new ArrayList();
		name_prj = new HashMap();
		
		// 1- obtenga los proyectos bajo prs_directory
		prs_dir = new File(prs_directory);
		if ( !prs_dir.isDirectory() )
		{
			throw new Exception(prs_dir+ ": Not a directory!");
		}
		File[] files = prs_dir.listFiles();
		for ( int i = 0; i < files.length; i++ )
		{
			if ( files[i].isDirectory() )
			{
				String name = files[i].getName();
				prjnames.add(name);
				// Se asocia el mismo nombre:
				name_prj.put(name.toLowerCase(), name);
			}
		}

		// 2- obtenga las extensiones del núcleo:
		List extensionLoaders = Loro.getExtensionLoaders();
		for ( Iterator it = extensionLoaders.iterator(); it.hasNext(); )
		{
			IOroLoader oroLoader = (IOroLoader) it.next();
			addProjectModelFromLoader(oroLoader);
		}

		// 3- obtenga el mismo cargador de unidades del núcleo:
		IOroLoader oroLoader = Loro.getCoreLoader();
		Extension extinfo = new Extension(oroLoader);
		String name = extinfo.getName(); 
		prjnames.add(name);
		IProjectModel prjm = _loadProjectModelExtension(extinfo);
		name_prj.put(name.toLowerCase(), prjm);
	}
	
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Adiciona un proyecto a la lista de disponibles de acuerdo con
	 * un cargador de unidades.
	 */
	public void addProjectModelFromLoader(IOroLoader oroLoader)
	throws IOException
	{
		Extension extinfo = new Extension(oroLoader);
		String name = extinfo.getName();
		if ( !existsProjectModel(name) )
			prjnames.add(name);
		// Se asocia el extinfo:
		name_prj.put(name.toLowerCase(), extinfo);
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Adiciona un nombre de proyecto a la lista de disponibles.
	 *
	 * @throws RuntimeException Si el directorio correspondiente no existe.
	 */
	public void addProjectModelName(String name)
	{
		File dir = new File(prs_dir, name);
		if ( dir.isDirectory() )
		{
			IProjectModel prjm = _loadProjectModelDirectory(name);
			if ( !existsProjectModel(name) )
				prjnames.add(name);
			name_prj.put(name.toLowerCase(), prjm);
		}
		else
		{
			throw new RuntimeException(dir+ ": not a directory!");
		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la lista de nombres de los proyectos disponibles.
	 */
	public Collection getAvailableProjects()
	{
		return prjnames;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la lista de nombres de todas las especificaciones disponibles.
	 */
	public Collection getAvailableSpecifications()
	{
		List specnames = new ArrayList();
		for ( Iterator it = prjnames.iterator(); it.hasNext(); )
		{
			String prjname = (String) it.next();
			IProjectModel prjm = getProjectModel(prjname);
			for ( Iterator itt = prjm.getPackages().iterator(); itt.hasNext(); )
			{
				IPackageModel pkgm = (IPackageModel) itt.next();
				for ( Iterator it2 = pkgm.getSpecNames().iterator(); it2.hasNext(); )
				{
					String specname = (String) it2.next();
					SpecificationUnit unit = pkgm.getSpecification(specname);
					specnames.add(unit.getQualifiedName());
				}
			}
		}
		return specnames;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Dice si existe un proyecto.
	 */
	public boolean existsProjectModel(String prjname)
	{
		return name_prj.get(prjname.toLowerCase()) != null;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene uno de los modelos disponibles.
	 * Si el nombre no concuerda con alguno de los modelos disponibles,
	 * se genera un RuntimeException.
	 */
	public IProjectModel getProjectModel(String prjname)
	{
		IProjectModel prjm;
		Object obj = name_prj.get(prjname.toLowerCase());
		if ( obj instanceof IProjectModel )
		{
			prjm = (IProjectModel) obj;
		}
		else if ( obj instanceof Extension )
		{
			prjm = _loadProjectModelExtension((Extension) obj);
			name_prj.put(prjname.toLowerCase(), prjm);
		}
		else if ( obj instanceof String )
		{
			prjm = _loadProjectModelDirectory((String) obj);
			name_prj.put(prjname.toLowerCase(), prjm);
		}
		else
		{
			throw new RuntimeException(prjname+ ": unavailable project!");
		}

		prjm.addProjectModelListener(pmlistener);
		
		return prjm;
		
	}
	
	/////////////////////////////////////////////////////////////////
	private IProjectModel _loadProjectModelDirectory(String name)
	{
		File prj_dir = new File(prs_dir, name);
		return loadProjectModelFromDirectory(prj_dir);
	}
	
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la información básica de un proyecto.
	 * @return An IROInfo object describing the project. 
	 */
	public IProjectModel.IROInfo getProjectModelInfo(String prjname)
	{
		Object obj = name_prj.get(prjname.toLowerCase());
		if ( obj instanceof IProjectModel )
		{
			IProjectModel prjm = (IProjectModel) obj;
			return prjm.getInfo();
		}
		else if ( obj instanceof Extension )
		{
			return _loadProjectModelInfoExtension((Extension) obj);
		}
		else if ( obj instanceof String )
		{
			return _loadProjectModelInfoFromDirectory(prjname);
		}
		else
		{
			throw new RuntimeException(prjname+ ": unavailable project!");
		}
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Gets a IROInfo object describing a project from a directory.
	 * @return An IROInfo object describing the project. 
	 */
	private IProjectModel.IROInfo _loadProjectModelInfoFromDirectory(String prjname)
	{
		File prj_dir = new File(prs_dir, prjname);
		Properties props = new Properties();
		File file = new File(prj_dir, "prj.props");
		if ( file.exists() )
		{
			try
			{
				loroedi.Util.loadProperties(file, props);
			}
			catch(IOException ex)
			{
				System.err.println(ex.getMessage());
			}
		}
		String description = "";
		file = new File(prj_dir, "prj.description");
		if ( file.exists() )
		{
			try
			{
				description = loroedi.Util.readFile(file);
			}
			catch(IOException ex)
			{
				System.err.println(ex.getMessage());
			}
		}
		String demo_src = "";
		file = new File(prj_dir, "prj.demo.lsh");
		if ( file.exists() )
		{
			try
			{
				demo_src = loroedi.Util.readFile(file);
			}
			catch(IOException ex)
			{
				System.err.println(ex.getMessage());
			}
			
		}

		return new ReadOnlyInfo(
			prjname,
			props.getProperty("title"),
			props.getProperty("authors"),
			props.getProperty("version"),
			props.getProperty("description.format", "plain"),
			description,
			demo_src
		);
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Carga un proyecto de un directorio dado.
	 */
	public IProjectModel loadProjectModelFromDirectory(File prj_dir)
	{
		String prjname = prj_dir.getName();
		IProjectModel.IROInfo roi = _loadProjectModelInfoFromDirectory(prjname);

		IProjectModel prjm = new ProjectModel(prjname);
		prjm.getInfo().setTitle(roi.getTitle());
		prjm.getInfo().setAuthors(roi.getAuthors());
		prjm.getInfo().setVersion(roi.getVersion());
		prjm.getInfo().setDescriptionFormat(roi.getDescriptionFormat());
		prjm.getInfo().setDescription(roi.getDescription());
		prjm.getInfo().setDemoScript(roi.getDemoScript());
		
		// get packages:
		File pkgs_dir = prj_dir;
		if ( pkgs_dir.isDirectory() )
		{
			// check first for the anonymous package: 
			String pkgname = "";
			File pkg_dir = pkgs_dir; // same location
			File pkg_props = new File(pkg_dir, "pkg.props");
			if ( pkg_props.exists() )   // a real package?
			{
				IPackageModel pkgm = prjm.addPackage(pkgname);
				_setupPackageFromDirectory(pkg_dir, pkg_props, pkgm, prjm);
			}
			
			// now check for named packages:
			List pkgnames = loroedi.Util.listFiles(
				pkgs_dir,
				true,        // boolean names,
				false,       // boolean absolute,
				true,        // boolean inc_dirs,
				false,       // boolean inc_files,
				9999,        // int level,
				null         // List list
			);
			for ( Iterator it = pkgnames.iterator(); it.hasNext(); )
			{
				pkgname = (String) it.next();
				pkg_dir = new File(pkgs_dir, pkgname);
				pkg_props = new File(pkg_dir, "pkg.props");
				if ( pkg_props.exists() )   // a real package?
				{
					pkgname = Util.replace(pkgname, File.separator, "::");
					IPackageModel pkgm = prjm.addPackage(pkgname);
					_setupPackageFromDirectory(pkg_dir, pkg_props, pkgm, prjm);
				}
			}
		}

		prjm.getControlInfo().setModifiable(true);
		
		return prjm;
	}
	
	/////////////////////////////////////////////////////////////////
	private IProjectModel.IROInfo _loadProjectModelInfoExtension(Extension extinfo)
	{
		Properties props = extinfo.getProperties();
		return new ReadOnlyInfo(
			extinfo.getName(),
			props.getProperty("title"),
			props.getProperty("authors"),
			props.getProperty("version"),
			props.getProperty("description.format", "plain"),
			extinfo.getDescription(),
			extinfo.getDemoScript()
		);
	}
	
	/////////////////////////////////////////////////////////////////
	private IProjectModel _loadProjectModelExtension(Extension extinfo)
	{
		IProjectModel.IROInfo roi = _loadProjectModelInfoExtension(extinfo);

		IProjectModel prjm = new ProjectModel(extinfo.getName());
		prjm.getInfo().setTitle(roi.getTitle());
		prjm.getInfo().setAuthors(roi.getAuthors());
		prjm.getInfo().setVersion(roi.getVersion());
		prjm.getInfo().setDescriptionFormat(roi.getDescriptionFormat());
		prjm.getInfo().setDescription(roi.getDescription());
		prjm.getInfo().setDemoScript(roi.getDemoScript());

		prjm.getControlInfo().setModifiable(
			Boolean.valueOf(extinfo.getProperties().getProperty("modifiable")).booleanValue()
		);
		
		IOroLoader oroLoader = extinfo.getOroLoader();
		
		// verifique primero:
		prjm.getControlInfo().setValid(oroLoader.verify(null));
		
		for ( Iterator it = oroLoader.getPackageNames(null).iterator(); it.hasNext(); )
		{
			String pkgname = (String) it.next();
			IPackageModel pkgm = prjm.addPackage(pkgname);
			_setupPackageFromLoader(oroLoader, pkgname, pkgm, prjm);
		}
		
		prjm.setOroLoader(oroLoader);
		return prjm;
	}
	
	/////////////////////////////////////////////////////////////////
	private void _setupPackageFromLoader(
		IOroLoader oroLoader,
		String pkgname, IPackageModel pkgm, IProjectModel prjm
	)
	{
		List list = oroLoader.loadUnitNamesFromPackage(pkgname, null);
		
		List nspecs   = new ArrayList();
		List nalgs    = new ArrayList();
		List nclasses = new ArrayList();
		
		for ( Iterator it = list.iterator(); it.hasNext(); )
		{
			String qname = (String) it.next();
			
			if ( qname.endsWith(".e") )
			{
				nspecs.add(qname);
			}
			else if ( qname.endsWith(".a") )
			{
				nalgs.add(qname);
			}
			else if ( qname.endsWith(".c") )
			{
				nclasses.add(qname);
			}
		}
		
		// specs:
		for ( Iterator it = nspecs.iterator(); it.hasNext(); )
		{
			String name = (String) it.next();
			IUnidad.IEspecificacion ne = (IUnidad.IEspecificacion) oroLoader.getUnit(name);
			String src = oroLoader.getUnitSource(name);
			
			// quite paquete si lo tiene:
			int index = name.lastIndexOf("::");
			if ( index >= 0 )
				name = name.substring(index + 2);
			
			// quite terminación:
			name = name.substring(0, name.length() - 2);
			
			SpecificationUnit spec = pkgm.addSpecification(name);
			spec.setSourceCode(src);
			if ( ne != null )
			{
				spec.setIUnidad(ne);
			}
		}

		// algs:
		for ( Iterator it = nalgs.iterator(); it.hasNext(); )
		{
			String name = (String) it.next();
			IUnidad.IAlgoritmo na = (IUnidad.IAlgoritmo) oroLoader.getUnit(name);
			String src = oroLoader.getUnitSource(name);
			
			// quite paquete si lo tiene:
			int index = name.lastIndexOf("::");
			if ( index >= 0 )
				name = name.substring(index + 2);
			
			// quite terminación:
			name = name.substring(0, name.length() - 2);
			
			String spec_name = null;
			
			if ( na != null )
				spec_name = na.getSpecificationName();
			else
				spec_name = "(UNKNOWN)";

			AlgorithmUnit alg = pkgm.addAlgorithm(name, spec_name);			
			alg.setSourceCode(src);
			if ( na != null )
			{
				alg.setIUnidad(na);
			}
		}

		// classes:
		for ( Iterator it = nclasses.iterator(); it.hasNext(); )
		{
			String name = (String) it.next();
			IUnidad.IClase nc = (IUnidad.IClase) oroLoader.getUnit(name);
			String src = oroLoader.getUnitSource(name);
			
			// quite paquete si lo tiene:
			int index = name.lastIndexOf("::");
			if ( index >= 0 )
				name = name.substring(index + 2);
			
			// quite terminación:
			name = name.substring(0, name.length() - 2);
			
			ClassUnit clazz = pkgm.addClass(name);
			clazz.setSourceCode(src);
			if ( nc != null )
			{
				clazz.setIUnidad(nc);
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Prepara paquete leido de directorio.
	 */
	private void _setupPackageFromDirectory(
		File pkg_dir, File pkg_props, 
		IPackageModel pkgm, IProjectModel prjm
	)
	{
		Properties props = new Properties();
		try
		{
			loroedi.Util.loadProperties(pkg_props, props);
		}
		catch(IOException ex)
		{
			System.err.println(ex.getMessage());
			return;
		}

		// specs:
		for ( int i = 0; ; i++ )
		{
			String key = "spec." +i;
			String name = props.getProperty(key);
			if ( name == null )
			{
				break;
			}
			SpecificationUnit spec = pkgm.addSpecification(name);
			_setSourceAndCompiledUnit(spec, pkg_dir, name+ ".e");
		}

		// algs:
		for ( int i = 0; ; i++ )
		{
			String key = "alg." +i;
			String value = props.getProperty(key);
			if ( value == null )
			{
				break;
			}
			StringTokenizer st = new StringTokenizer(value, ", \t");
			try
			{
				String name = st.nextToken();
				String spec_name = st.nextToken();
				AlgorithmUnit alg = pkgm.addAlgorithm(name, spec_name);
				_setSourceAndCompiledUnit(alg, pkg_dir, name+ ".a");
			}
			catch(NoSuchElementException ex)
			{
				// ignore
			}
		}

		// classes:
		for ( int i = 0; ; i++ )
		{
			String key = "class." +i;
			String name = props.getProperty(key);
			if ( name == null )
			{
				break;
			}
			ClassUnit clazz = pkgm.addClass(name);
			_setSourceAndCompiledUnit(clazz, pkg_dir, name+ ".c");
		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Pone el código fuente y la unidad compilada de una unidad en un paquete.
	 */
	private void _setSourceAndCompiledUnit(
		IProjectUnit unit, 
		File pkg_dir,
		String unitname
	)
	{
		File srcfile = new File(pkg_dir, unitname + ".loro");
		File unitfile = new File(pkg_dir, unitname + ".oro");
		try
		{
			String src = loroedi.Util.readFile(srcfile);
			unit.setSourceCode(src);
		}
		catch(Exception ex)
		{
			Loro.log("Error ocurred while getting source for unit: " 
				+unitname+ ": " +ex.getMessage()
			);
		}
		if ( unitfile.exists() )
		{
			IUnidad comp_unit = Loro.obtUnidadDeArchivo(unitfile.getAbsolutePath());
			unit.setIUnidad(comp_unit);
		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene un proyecto sin nombre y vacío.
	 * Sirve de base para la construcción de nuevos.
	 */
	public IProjectModel getNewProjectModel()
	{
		IProjectModel prjm = new ProjectModel("");
		prjm.addProjectModelListener(pmlistener);
		return prjm;
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Exporta un proyecto.
	 *
	 * @param prjm El modelo del proyecto a exportar.
	 * @param dest Nombre del destino de la exportación. 
	 *             Si este nombre termina en ".lar", entonces la exportación
	 *             es un archivo de extensión Loro. En otro caso, se interpreta
	 *             como el nombre de un directorio.
	 * @param source Incluir código fuente?
	 * @param compiled Incluir código compilado?
	 * @param html Incluir documentación?
	 *
	 * @throws Exception Si algún problema surge.
	 */
	public void exportProjectModel(
		IProjectModel prjm, 
		String dest,
		final boolean source,
		final boolean compiled,
		final boolean html
	)
	throws Exception
	{
		// prepare the filename filter:
		FilenameFilter fnfilter = new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				if ( !source && (name.endsWith(".loro") || name.endsWith(".lsh")) )
					return false;
				if ( !compiled && name.endsWith(".oro") )
					return false;
				if ( !html && name.endsWith(".html") )
					return false;
				return true;
			}
		};
					
					
		// in case destination is an extension:
		ZipOutputStream zos = null;
		
		// in case destination is a directory:
		File dir = null;
		
		// zos and dir are mutually exclusive, of course.
		
		File dest_file = new File(dest);
		
		if ( dest.endsWith(".lar") )
		{
			File parent = dest_file.getParentFile();
			if ( !parent.exists() )
			{
				if ( !parent.mkdirs() )
					throw new Exception("Could not create directory: " +parent);
			}
			
			// export to an extension archive.
			BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(dest)
			);
			zos = new ZipOutputStream(bos);
			zos.setLevel(9);
		}
		else
		{
			dir = new File(dest);
			if ( !dir.isAbsolute() )
				throw new Exception("Directory must be absolute: " +dest);
			if ( !dir.exists() )
			{
				if ( !dir.mkdirs() )
					throw new Exception("Could not create directory: " +dir);
			}
		}




		String prjname = prjm.getInfo().getName();
		if ( prjname.endsWith(".lar") )
		{
			// source is an extension project.
			IOroLoader oroLoader = prjm.getOroLoader();
			if ( oroLoader != null )
			{
				if ( zos != null )
					loro.util.Util.copyExtensionToZip(oroLoader, zos, fnfilter);
				else
					loro.util.Util.copyExtensionToDirectory(oroLoader, dir, fnfilter);
			}
			else
			{
				throw new Exception("extension with no unit loader."); 
			}
		}
		else
		{
			// source is a directory project.
		
			if ( zos != null )
			{
				// export to zos: (see initialization above)
				File prj_dir = new File(prs_dir, prjname);
				loro.util.Util.copyDirectoryToZip(prj_dir, zos, fnfilter);
			}
			else
			{
				// export to a directory.
				
				// por ahora, con _saveProjectModel,  pero debería cambiarse
				// a una simple copia de directorio similar a como se hace
				// hacia una extensión.  PENDING.
				_saveProjectModel(prjm, dir, true);
				
				// verificar si la copia ha sido en el directorio prs_dir,
				// en cuyo caso, se actualiza la lista de proyectos
				// disponibles si es necesario
				File parent = dir.getParentFile();
				if ( prs_dir.equals(parent) )
				{
					String name = dir.getName();
					if ( ! existsProjectModel(name) )
					{
						prjnames.add(name);
						IProjectModel prjm2 = _loadProjectModelDirectory(name);
						name_prj.put(name.toLowerCase(), prjm2);
					}
				}
			}
		}
		
		if ( zos != null )
		{
			zos.close();
		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Compila un proyecto.
	 *
	 * @throws RuntimeException Si el proyecto no es modificable
	 *         o no tiene nombre.
	 */
	public void compileProjectModel(IProjectModel prjm)
	throws Exception
	{
		String prjname = prjm.getInfo().getName();
		File prj_dir = new File(prs_dir, prjname);
		
		List classes = new ArrayList();
		List specs = new ArrayList();
		List algs = new ArrayList();

		for ( Iterator it = prjm.getPackages().iterator(); it.hasNext(); )
		{
			IPackageModel pkgm = (IPackageModel) it.next();
			for ( Iterator itt = pkgm.getClassNames().iterator(); itt.hasNext(); )
			{
				String name = (String) itt.next();
				IProjectUnit unit = pkgm.getClass(name);
				classes.add(unit);
			}
			for ( Iterator itt = pkgm.getSpecNames().iterator(); itt.hasNext(); )
			{
				String name = (String) itt.next();
				IProjectUnit unit = pkgm.getSpecification(name);
				specs.add(unit);
			}
		
			for ( Iterator itt = pkgm.getAlgorithmNames().iterator(); itt.hasNext(); )
			{
				String name = (String) itt.next();
				IProjectUnit unit = pkgm.getAlgorithm(name);
				algs.add(unit);
			}
		}

		// todas las unidades:
		List units = new ArrayList();
		for ( Iterator it = classes.iterator(); it.hasNext(); )
		{
			units.add((IProjectUnit) it.next());
		}
		for ( Iterator it = specs.iterator(); it.hasNext(); )
		{
			units.add((IProjectUnit) it.next());
		}
		for ( Iterator it = algs.iterator(); it.hasNext(); )
		{
			units.add((IProjectUnit) it.next());
		}

		if ( units.size() == 0 )
		{
			// nada que hacer:
			return;
		}
		
		// Algunos preparativos:
		for ( Iterator it = units.iterator(); it.hasNext(); )
		{
			IProjectUnit unit = (IProjectUnit) it.next();
			
			// - invalidación de tabla de símbolos		
			IUnidad pre_u = unit.getIUnidad();
			if ( pre_u != null )
			{
				GUI.invalidateSymbolTable(pre_u.getTypeString());
			}

			// - anular posible compilacion anterior		
			unit.setIUnidad(null);
			
			// - unidad posiblemente en edición:		
			UEditor editor = (UEditor) unit.getUserObject();
			if ( editor != null )
			{
				// limpiar area de mensajes:
				editor.getMessageArea().clear();
				
				// guardar unidad si hay cambios en edición:
				if ( !editor.isSaved() && !GUI.saveUnit(unit) )
				{
					throw new Exception(unit.getStereotypedName()+ 
						": this unit could not be saved!"
					);
				}
			}
		}
		
		// prepare directorio de destino:
		ICompilador compiler = Loro.obtCompilador();
		compiler.ponDirectorioDestino(prj_dir.getAbsolutePath());
		
		// Compilación:
		// Un error de tipo DerivacionException, inmediatamente detiene
		// la compilación.
		// En otro caso, puesto que cada unidad compilada bien no vuelve a 
		// compilarse en el ciclo de pasadas, el único criterio para detener 
		// una compilación con errores es que no se haya hecho ningún progreso
		// de una pasada a la siguiente.
		
		// lista de excepciones pasada previa:
		List prev_excs = null;

		for ( int pass = 1; ; pass++ )
		{
			// lista de excepciones de esta pasada:
			List excs = new ArrayList();
			
			for ( Iterator it = units.iterator(); it.hasNext(); )
			{
				IProjectUnit unit = (IProjectUnit) it.next();
				IUnidad compiled = unit.getIUnidad();
				if ( compiled == null )
				{
					// esta unidad no ha sido compilada exitosamente:
					try
					{
						_compileUnit(unit, prj_dir);
					}
					catch(DerivacionException ex)
					{
						// terminemos inmediatamente:
						throw new UnitCompilationException(ex, unit);
					}
					catch(CompilacionException ex)
					{
						excs.add(new UnitCompilationException(ex, unit));
					}
				}
			}
			if ( excs.size() == 0 )
			{
				// Compilación exitosa :-)
				prev_excs = null;
				break;
			}
			
			if ( prev_excs == null )
			{
				// primera pasada:
				prev_excs = excs;
			}
			else if ( excs.size() >= prev_excs.size() )
			{
				// No hubo progreso :-(
				prev_excs = excs;
				break;
			}
			else
			{
				// Sí hubo progreso: hacer siguiente pasada:
				prev_excs = excs;
			}
		}
		
		if ( prev_excs != null && prev_excs.size() > 0 )
		{
			throw (UnitCompilationException) prev_excs.get(0);
		}
		
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Salvaguarda un proyecto.
	 *
	 * @throws RuntimeException Si el proyecto no es modificable
	 *         o no tiene nombre.
	 * @throws Exception Si no se puede crear el directorio para el proyecto
	 *         cuando éste no existe con anterioridad.
	 */
	public void saveProjectModel(IProjectModel prjm) throws Exception {
		if ( !prjm.getControlInfo().isModifiable() ) {
			throw new RuntimeException("project is unmodifiable");
		}

		IProjectModel.IInfo info = prjm.getInfo();
		String name = info.getName();
		if ( name.trim().length() == 0 ) {
			throw new RuntimeException("unnamed project");
		}
		
		File prj_dir = new File(prs_dir, name);
		_saveProjectModel(prjm, prj_dir, true);
		
		if ( !existsProjectModel(name) )
		{
			// update list of available projects:
			name_prj.put(name.toLowerCase(), prjm);
			prjnames.add(name);
		}
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Salvaguarda las propiedades de proyecto.
	 * Es equivalente a saveProjectModel salvo que no se guardan
	 * los paquetes. Note que aqui se incluye también la descripción.
	 *
	 * @throws RuntimeException Si el proyecto no es modificable
	 *         o no tiene nombre.
	 * @throws Exception Si no se puede crear el directorio para el proyecto
	 *         cuando éste no existe con anterioridad.
	 */
	public void saveProjectModelProperties(IProjectModel prjm) throws Exception {
		if ( !prjm.getControlInfo().isModifiable() ) {
			throw new RuntimeException("project unmodifiable");
		}
		IProjectModel.IInfo info = prjm.getInfo();
		String name = info.getName();
		if ( name.trim().length() == 0 ) {
			throw new RuntimeException("unnamed project");
		}
		
		File prj_dir = new File(prs_dir, name);
		_saveProjectModel(prjm, prj_dir, false);
	}



	/////////////////////////////////////////////////////////////////
	/**
	 * Salvaguarda incondicionalmente un proyecto en un directorio
	 * dado.
	 *
	 * No se tiene en cuenta para nada el nombre que tenga el proyecto
	 * o si el proyecto dice no ser modificable.
	 *
	 * Este método sirve de soporte a saveProjectModel() y a
	 * exportProjectModel().
	 *
	 * @throws RuntimeException Si el proyecto no es modificable
	 * @throws Exception Si no se puede crear el directorio para el proyecto
	 *         cuando éste no existe con anterioridad.
	 */
	private void _saveProjectModel(
		IProjectModel prjm,
		File prj_dir,
		boolean include_pkgs
	)
	throws Exception
	{
		IProjectModel.IInfo info = prjm.getInfo();

		// guarde propiedades y descripción		
		Properties props = new Properties();
		if ( !prj_dir.exists() ) {
			if ( ! prj_dir.mkdirs() )  {
				throw new Exception("Could not create directory " +prj_dir);
			}
		}
		props.setProperty("title", info.getTitle());
		props.setProperty("authors", info.getAuthors());
		props.setProperty("version", info.getVersion());
		props.setProperty("description.format", info.getDescriptionFormat());
		loroedi.Util.storeProperties(
			"Project properties", 
			props,
			new File(prj_dir, "prj.props")
		);
		
		loroedi.Util.writeFile(new File(prj_dir, "prj.description"), 
			info.getDescription()
		);
		
		saveDemoScript(prjm);
		
		if ( include_pkgs )
		{
			// guarde paquetes:
			File pkgs_dir = prj_dir;
			for ( Iterator it = prjm.getPackages().iterator(); it.hasNext(); )
			{
				_savePackageModel((IPackageModel) it.next(), pkgs_dir);
			}
		}
	}
	
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Salvaguarda un paquete.
	 */
	private void _savePackageModel(IPackageModel pkgm, File pkgs_dir)
	throws Exception
	{
		String pkgname = pkgm.getName();
		pkgname = Util.replace(pkgname, "::", File.separator);
		File pkg_dir = new File(pkgs_dir, pkgname);
		File pkg_props = new File(pkg_dir, "pkg.props");

		if ( !pkg_dir.exists() ) {
			if ( ! pkg_dir.mkdirs() )  {
				throw new Exception("Could not create directory " +pkg_dir);
			}
		}

		System.out.println("saving package " +pkgname);
		for ( Iterator it = pkgm.getSpecNames().iterator(); it.hasNext(); ) {
			String name = (String) it.next();
			// save source:
			SpecificationUnit spec = pkgm.getSpecification(name);
			loroedi.Util.writeFile(
				new File(pkg_dir, name+ ".e.loro"), 
				spec.getSourceCode()
			);
		}
		for ( Iterator it = pkgm.getAlgorithmNames().iterator(); it.hasNext(); )
		{
			String name = (String) it.next();
			AlgorithmUnit alg = pkgm.getAlgorithm(name);
			String spec_name = alg.getSpecificationName();
			// save source:
			loroedi.Util.writeFile(
				new File(pkg_dir, name+ ".a.loro"), 
				alg.getSourceCode()
			);
		}
		for ( Iterator it = pkgm.getClassNames().iterator(); it.hasNext(); )
		{
			String name = (String) it.next();
			// save source:
			ClassUnit clazz = pkgm.getClass(name);
			loroedi.Util.writeFile(
				new File(pkg_dir, name+ ".c.loro"), 
				clazz.getSourceCode()
			);
		}
		
		_updatePackageProperties(pkg_props, pkgm);
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Actualiza pkg.props en disco para un paquete.
	 */
	private void _updatePackageProperties(
		File pkg_props, 
		IPackageModel pkgm
	)
	throws Exception
	{
		int ii;
		Properties props = new Properties();
		ii = 0;
		for ( Iterator it = pkgm.getSpecNames().iterator(); it.hasNext(); )
		{
			String name = (String) it.next();
			props.setProperty("spec." +ii, name);
			ii++;
		}
		ii = 0;
		for ( Iterator it = pkgm.getAlgorithmNames().iterator(); it.hasNext(); )
		{
			String name = (String) it.next();
			AlgorithmUnit alg = pkgm.getAlgorithm(name);
			String spec_name = alg.getSpecificationName();
			props.setProperty("alg." +ii, name+ ", " +spec_name);
			ii++;
		}
		ii = 0;
		for ( Iterator it = pkgm.getClassNames().iterator(); it.hasNext(); )
		{
			String name = (String) it.next();
			props.setProperty("class." +ii, name);
			ii++;
		}

		loroedi.Util.storeProperties(
			"Package properties", 
			props,
			pkg_props 
		);
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Importa unidades en un proyecto.
	 * Las unidades se obtienen del código fuente dado.
	 */
	public void importSource(IProjectModel prjm, String source)
	throws Exception
	{
		String prjname = prjm.getInfo().getName();
		File prj_dir = new File(prs_dir, prjname);
		
		ICompilador compiler = Loro.obtCompilador();
		compiler.ponDirectorioDestino(prj_dir.getAbsolutePath());
		
		//IUnidad[] unids = ediCompiler.derivar(source);
		IFuente fuente = ediCompiler.compilar(source);
		String pkgname = fuente.getPackageName();
		IFuente.IUtiliza[] uses = fuente.obtUtilizas();
		
		// prepare "primeras líneas" para cada fuente de unidad:
		StringBuffer sbheader = new StringBuffer();
		if ( pkgname != null )
			sbheader.append(Loro.Str.get("package")+ " " +pkgname+ "\n\n");

		for ( int i = 0; i < uses.length; i++ ) {
			IFuente.IUtiliza use = uses[i];
			sbheader.append(Loro.Str.get("uses")+ " " +use.obtQue()+ " " +use.getName()+ "\n\n");
		}
		String header = sbheader.toString();
		
		if ( pkgname == null )
			pkgname = "";

		IPackageModel pkgm = prjm.addPackage(pkgname);
		IUnidad[] unids = fuente.obtUnidades();
		
		int posIni = 0;
		
		for ( int i = 0; i < unids.length; i++ )
		{
			IUnidad compiled_unit = unids[i];
			String unitname = compiled_unit.obtNombreSimpleCadena();
			IProjectUnit unit;
			if ( compiled_unit instanceof IUnidad.IEspecificacion )
			{
				unit = pkgm.addSpecification(unitname);
			}
			else if ( compiled_unit instanceof IUnidad.IAlgoritmo )
			{
				IUnidad.IAlgoritmo alg = (IUnidad.IAlgoritmo) compiled_unit;
				String spec_name = alg.getSpecificationName();
				unit = pkgm.addAlgorithm(unitname, spec_name);
			}
			else if ( compiled_unit instanceof IUnidad.IClase )
			{
				unit = pkgm.addClass(unitname);
			}
			else
			{
				throw new RuntimeException("impossible!");
			}

			Rango r = compiled_unit.obtRango();
			
			// El header se incluye desde la segunda unidad en adelante.
			// La primera unidad va a contener de todas maneras todo
			// el encabezado ya que posIni inicia en 0:			
			String unitsrc = i == 0 ? "" : header;
			
			unitsrc += source.substring(posIni, r.obtPosFin()) + "\n";
			unit.setSourceCode(unitsrc);
			unit.setIUnidad(compiled_unit);
			
			// actualice posIni para proxima unidad;
			posIni = r.obtPosFin();
		}
		
		// guarde todo para reflejar los codigos fuente para cada unidad.
		saveProjectModel(prjm);

	}	
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Compila una unidad.
	 */
	public void compileUnit(IProjectUnit unit)
	throws Exception
	{
		IPackageModel pkgm = unit.getPackage();
		IProjectModel prjm = pkgm.getModel();
		String prjname = prjm.getInfo().getName();
		File prj_dir = new File(prs_dir, prjname);
		
		ICompilador compiler = Loro.obtCompilador();
		compiler.ponDirectorioDestino(prj_dir.getAbsolutePath());
		
		// - invalidación de tabla de símbolos		
		IUnidad pre_u = unit.getIUnidad();
		if ( pre_u != null )
		{
			GUI.invalidateSymbolTable(pre_u.getTypeString());
		}

		// null es indicador de no compilación:
		unit.setIUnidad(null);
		_compileUnit(unit, prj_dir);
	}	
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Auxiliar para compilar una unidad.
	 */
	private void _compileUnit(IProjectUnit unit, File prj_dir)
	throws Exception
	{
		String unitname = unit.getName();
		IPackageModel pkgm = unit.getPackage();
		String pkgname = pkgm.getName();
		String src = unit.getSourceCode();
		
		IUnidad u;
		if ( unit instanceof SpecificationUnit )
		{
			u = ediCompiler.compileSpecification(pkgname, unitname, src);
		}
		else if ( unit instanceof AlgorithmUnit )
		{
			String specname = ((AlgorithmUnit) unit).getSpecificationName();
			u = ediCompiler.compileAlgorithm(pkgname, unitname, specname, src);
		}
		else if ( unit instanceof ClassUnit )
		{
			u = ediCompiler.compileClass(pkgname, unitname, src);
		}
		else
		{
			throw new RuntimeException("Unexpected, unit.getClass() = " +unit.getClass());
		}
		
		unit.setIUnidad(u);

		// documente:
		GUI.docUnit(unit, prj_dir);
	}
		
	/////////////////////////////////////////////////////////////////
	/**
	 * Salvaguarda una unidad.
	 * El código fuente a guardar es el que tenga asociado la unidad.
	 */
	public void saveUnit(IProjectUnit unit)
	throws Exception
	{
		IPackageModel pkgm = unit.getPackage();
		String pkgname = pkgm.getName();
		pkgname = Util.replace(pkgname, "::", File.separator);

		IProjectModel prjm = pkgm.getModel();
		String prjname = prjm.getInfo().getName();
		File prj_dir = new File(prs_dir, prjname);
		File pkgs_dir = prj_dir;
		
		File pkg_dir = new File(pkgs_dir, pkgname);
		_saveUnit(pkg_dir, unit);
		File pkg_props = new File(pkg_dir, "pkg.props");
		try
		{
			_updatePackageProperties(pkg_props, pkgm);
		}
		catch(Exception ex)
		{
			Loro.log("Error while updating package properties: " +ex.getMessage());
		}
	}
		
	/////////////////////////////////////////////////////////////////
	/**
	 * Salvaguarda una unidad.
	 */
	private void _saveUnit(File pkg_dir, IProjectUnit unit)
	throws Exception
	{
		String src = unit.getSourceCode();
		String name = unit.getName();
		String ext = _getFileExtensionId(unit) + ".loro";
		loroedi.Util.writeFile(
			new File(pkg_dir, name + ext),  
			src != null ? src : "// no source yet"
		);
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Salvaguarda el script de demostración, si existe.
	 * El código fuente a guardar es el que tenga asociado el proyecto.
	 */
	public void saveDemoScript(IProjectModel prjm)
	{
		IProjectModel.IInfo info = prjm.getInfo();
		String demo_script = info.getDemoScript();
		if ( demo_script != null )
		{
			String prjname = info.getName();
			File prj_dir = new File(prs_dir, prjname);
			try
			{
				loroedi.Util.writeFile(new File(prj_dir, "prj.demo.lsh"), demo_script);
			}
			catch(Exception ex)
			{
				Loro.log("Error while updating demo script: " +ex.getMessage());
			}
		}
	}
		
	/////////////////////////////////////////////////////////////////
	/**
	 * Ejecuta una unidad.
	 */
	public void executeAlgorithm(final AlgorithmUnit alg, Object[] args, boolean ejecutorpp)
	{
		IUnidad u = alg.getIUnidad();
		if ( u == null ) {
			throw new NullPointerException("algorithm doesn't have an associated IUnidad");
		}
		
		IPackageModel pkgm = alg.getPackage();
		IProjectModel prjm = pkgm.getModel();
		String prjname = prjm.getInfo().getName();
		File prj_dir = new File(prs_dir, prjname);
		
		// prepare directorio destino, que en este caso es la forma
		// en que se agrega tal directorio para fines de busqueda, mas que
		// para "destino" de nuevo elementos propiamente:
		ICompilador compiler = Loro.obtCompilador();
		compiler.ponDirectorioDestino(prj_dir.getAbsolutePath());
		
		InterpreterWindow iw = 
		new InterpreterWindow(Str.get("gui.1_title_run_algorithm", alg.getQualifiedName()), 
		null, false, ejecutorpp)
		{
			protected void body() throws Exception {
				// A este prefijo se le agrega lo que suministre el usuario 
				// en caso de necesitarse argumentos.
				String initial_part = alg.getQualifiedName()+ "("; 
				IUnidad u = alg.getIUnidad();
				pw.println(PROMPT+ "// " +Str.get("gui.1_title_run_algorithm", u));
				String cmd;
				int num_args = Loro.getNumArguments(u);
				boolean hasReturnValue = Loro.hasReturnValue(u);
				if ( num_args == 1 ) {
					pw.println(PROMPT+ "// " +Str.get("gui.run_algorithm_prompt_arg"));
				}
				else if ( num_args > 1 ) {
					pw.println(PROMPT+ "// " +Str.get("gui.1_run_algorithm_prompt_args", ""+num_args));
				}
				else {
					initial_part += ")";
				}
				if ( hasReturnValue ) {
					pw.println(PROMPT+ "// " +Str.get("gui.run_algorithm_prompt_assignment"));
				}
				
				if ( num_args > 0 || hasReturnValue ) {
					pw.print(PROMPT);
					String text = readLine(initial_part);
					if ( text == null )
						return;
	
					cmd = text;
				}
				else {
					cmd = initial_part;
					pw.println(PROMPT + cmd);
				}
				
				procesarLoro(cmd);
			}
		};
		iw.mostrar();
		iw.start();
	}
		
	/////////////////////////////////////////////////////////////////
	/**
	 * Ejecuta una lista (String) de comandos en una ventana de interpretación.
	 *
	 * @param title   For the window.
	 * @param hello   Message to start with. Can be null.
	 * @param byeOK   Message to show at the end if all OK. Can be null.
	 * @param byeErr  Message to show at the end if any exception. Can be null.
	 * @param cmds    Commands to execute.
	 * @param newSymTab See InterpreterWindow
	 * @param ejecutorpp step-by-step execution?
	 */
	public void executeCommands(
		String title, 
		String hello, 
		final String byeOK, 
		final String byeErr, 
		final List cmds,
		boolean newSymTab,
		boolean ejecutorpp
	) {
		InterpreterWindow iw = new InterpreterWindow(title, hello, newSymTab, ejecutorpp) {
			// updated when terminate() is called
			boolean terminated = false;
			
			//////////////////////////////////////////////////////////////////
			protected void body() throws Exception {
				boolean all_OK = true;
				for ( Iterator iter = cmds.iterator(); iter.hasNext(); ) {
					GUI.SourceSegment ss = (GUI.SourceSegment) iter.next();
					String cmd = ss.src;
					String ask_enter = ss.pause ? "" : null;
					try {
						interpret(cmd, ask_enter);
					}
					catch ( Exception ex ) {
						handleException(ex);
						all_OK = false;
						// Si es terminated y hay más segmentos pendientes, se
						// le pide confirmación al usuario para continuar:
						if ( terminated && iter.hasNext() && !GUI.confirm(frame,
							Str.get("gui.conf_stop_commands")
						))
							break;
							
						terminated = false;
					}
				}
				if ( all_OK && byeOK != null ) {
					term.setPrefix(PREFIX_SPECIAL);
					pw.println(byeOK);
				}
				if ( !all_OK && byeErr != null ) {
					term.setPrefix(PREFIX_SPECIAL);
					pw.println(byeErr);
				}
			}
			//////////////////////////////////////////////////////////////////
			protected void terminate() {
				super.terminate();
				terminated = true;
			}
		};
		iw.getTextArea().setEditable(false);
		iw.mostrar();
		iw.start();
	}
		
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el fragmento de extensión distintivo dependiendo del
	 * tipo de unidad concreto.
	 */
	private String _getFileExtensionId(IProjectUnit unit) {
		if ( unit instanceof SpecificationUnit ) {
			return ".e";
		}
		else if ( unit instanceof AlgorithmUnit ) {
			return ".a";
		}
		else if ( unit instanceof ClassUnit ) {
			return ".c";
		}
		throw new RuntimeException(
			"Expected: SpecificationUnit, AlgorithmUnit, ClassUnit"
		);
	}



	//////////////////////////////////////////////////////////////////////
	/**
	 *
	 */
	static class Extension
	{
		String name;
		Properties props;
		String description;
		String demoSrc;
		IOroLoader oroLoader;
		
		//////////////////////////////////////////////////////////////////////
		/**
		 * Crea un objeto informativo sobre el cargador dado.
		 * Las propiedades se cargan del recurso "prj.props". 
		 * Los valores por defecto para estas propiedades son:
		 * <ul>
		 *	<li> "title" = ""
		 *	<li> "authors" = ""
		 *	<li> "version" = ""
		 *	<li> "modifiable" = "false"
		 * </ul>
		 * La descripción se carga del recurso "prj.description".
		 * El demo se carga del recurso "prj.demo.lsh".
		 */
		Extension(IOroLoader oroLoader)
		throws IOException
		{
			this.oroLoader = oroLoader;
			
			// valores por defecto:
			Properties default_props = new Properties();
			default_props.setProperty("title", "");
			default_props.setProperty("authors", "");
			default_props.setProperty("version", "");
			default_props.setProperty("modifiable", "false");
			default_props.setProperty("description.format", "plain");
			
			props = new Properties(default_props);
			
			name = oroLoader.getName();
			InputStream is = oroLoader.getResourceAsStream("prj.props");
			if ( is != null )
			{
				props.load(is);
				is.close();
			}

			description = "";
			is = oroLoader.getResourceAsStream("prj.description");
			if ( is != null )
			{
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				StringBuffer sb = new StringBuffer();
				String line;
				while ( (line = br.readLine()) != null )
				{
					sb.append(line + "\n");
				}
				is.close();
				description = sb.toString();
			}

			demoSrc = "";
			is = oroLoader.getResourceAsStream("prj.demo.lsh");
			if ( is != null )
			{
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				StringBuffer sb = new StringBuffer();
				String line;
				while ( (line = br.readLine()) != null )
				{
					sb.append(line + "\n");
				}
				is.close();
				demoSrc = sb.toString();
			}
		}
		
		//////////////////////////////////////////////////////////////////////
		public String getName()
		{
			return name;
		}
		
		//////////////////////////////////////////////////////////////////////
		public Properties getProperties()
		{
			return props;
		}
		
		//////////////////////////////////////////////////////////////////////
		public String getDescription()
		{
			return description;
		}
		
		//////////////////////////////////////////////////////////////////////
		public String getDemoScript()
		{
			return demoSrc;
		}
		
		//////////////////////////////////////////////////////////////////////
		public IOroLoader getOroLoader()
		{
			return oroLoader;
		}
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * Borra una unidad. Esto significa borrar de disco los archivos
	 * correspondientes: .oro, .loro, .html .
	 */
	private void _removeUnit(IProjectUnit unit)
	{
		IPackageModel pkgm = unit.getPackage();
		String pkgname = pkgm.getName();
		pkgname = Util.replace(pkgname, "::", File.separator);

		IProjectModel prjm = pkgm.getModel();
		String prjname = prjm.getInfo().getName();
		File prj_dir = new File(prs_dir, prjname);
		File pkgs_dir = prj_dir;
		
		File pkg_dir = new File(pkgs_dir, pkgname);
		String name = unit.getName();
		String ext_id = _getFileExtensionId(unit);
		String[] exts = { ".oro", ".html", ".loro" };
		for ( int i = 0; i < exts.length; i++ ) {
			File file = new File(pkg_dir, name + ext_id + exts[i]);
			if ( file.exists() && ! file.delete() ) {
				Loro.log("Could not delete file: " +file);
			}
		}
		File pkg_props = new File(pkg_dir, "pkg.props");
		try {
			_updatePackageProperties(pkg_props, pkgm);
		}
		catch(Exception ex) {
			Loro.log("Error while updating package properties: " +ex.getMessage());
		}
	}
		
	/////////////////////////////////////////////////////////////////
	/**
	 * Borra un paquete. Esto significa borrar pkg.props del directorio
	 * correspondiente.
	 */
	private void _removePackage(IPackageModel pkgm)
	{
		String pkgname = pkgm.getName();
		pkgname = Util.replace(pkgname, "::", File.separator);

		IProjectModel prjm = pkgm.getModel();
		String prjname = prjm.getInfo().getName();
		File prj_dir = new File(prs_dir, prjname);
		File pkgs_dir = prj_dir;
		
		File pkg_dir = new File(pkgs_dir, pkgname);
		File file = new File(pkg_dir, "pkg.props");
		if ( file.exists() && ! file.delete() ) {
			Loro.log("Could not delete file: " +file);
		}
	}
		

	/////////////////////////////////////////////////////////////////////
	class PMListener implements IProjectModelListener {
		// ProjectListener method:
		public void action(ProjectModelEvent e) {
			switch ( e.getID() ) {
				case ProjectModelEvent.PACKAGE_ADDED:
					IPackageModel pkgm = (IPackageModel) e.getElement();
					IProjectModel prjm = pkgm.getModel();
					String name = prjm.getInfo().getName();
					File prj_dir = new File(prs_dir, name);
					File pkgs_dir = prj_dir;
					try {
						_savePackageModel(pkgm, pkgs_dir);
					}
					catch(Exception ex) {
						Loro.log("Error while saving pakage: " +ex.getMessage());
					}
					break;
					
				case ProjectModelEvent.SPEC_ADDED:
				case ProjectModelEvent.ALGORITHM_ADDED:
				case ProjectModelEvent.CLASS_ADDED:
					try {
						saveUnit((IProjectUnit) e.getElement());
					}
					catch(Exception ex) {
						Loro.log("Error while saving unit: " +ex.getMessage());
					}
					break;

				case ProjectModelEvent.PACKAGE_REMOVED:
					_removePackage((IPackageModel) e.getElement());
					break;
					
				case ProjectModelEvent.SPEC_REMOVED:
				case ProjectModelEvent.ALGORITHM_REMOVED:
				case ProjectModelEvent.CLASS_REMOVED:
					_removeUnit((IProjectUnit) e.getElement());
					break;
			}
		}
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * All set* methods throw UnsupportedOperationException
	 */
	private static class ReadOnlyInfo implements IProjectModel.IROInfo {
		String name;
		String title;
		String authors;
		String version;
		String descriptionFormat;
		String description;
		String demoSrc;
		
		/////////////////////////////////////////////////////////////////
		ReadOnlyInfo(
			String name,
			String title,
			String authors,
			String version,
			String descriptionFormat,
			String description,
			String demoSrc
		)
		{
			this.name = name;
			this.title = title;
			this.authors = authors;
			this.version = version;
			this.descriptionFormat = descriptionFormat;
			this.description = description;
			this.demoSrc = demoSrc;
		}
		
		/////////////////////////////////////////////////////////////////
		public String getName() {
			return name;
		}
		
		/////////////////////////////////////////////////////////////////
		public String getTitle() {
			return title;
		}
	
		/////////////////////////////////////////////////////////////////
		public String getAuthors() {
			return authors;
		}
	
		/////////////////////////////////////////////////////////////////
		public String getVersion() {
			return version;
		}
	
		/////////////////////////////////////////////////////////////////
		public String getDescription() {
			return description;
		}
		
		/////////////////////////////////////////////////////////////////
		public String getDescriptionFormat() {
			return descriptionFormat;
		}

		/////////////////////////////////////////////////////////////////
		public String getDemoScript() {
			return demoSrc;
		}
	}
}

