package loroedi.gui.project.model;

import loroedi.gui.project.unit.*;

import loro.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

//////////////////////////////////////////////////
/**
 * Implementación de modelo de paquete.
 *
 * @author Carlos Rueda
 * @version $Id$
 */
public class PackageModel implements IPackageModel
{
	static final String[] EMPTY_STRING_ARRAY = new String[0];
	
	String pkgname;
	Map specs;
	Map algorithms;
	Map classes;
	
	/** Mi proyecto. */
	IProjectModel model;
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Crea un paquete para el modelo dado.
	 */
	public PackageModel(IProjectModel model, String pkgname)
	{
		this.model = model;
		this.pkgname = pkgname;
		specs = new HashMap();
		algorithms = new HashMap();
		classes = new HashMap();
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Retorna lo mismo que <code>model.getControlInfo().isModifiable()</code>, donde
	 * <code>model</code> es el argumento con que se creo este objeto.
	 */
	public boolean isModifiable()
	{
		return model.getControlInfo().isModifiable();
	}
	
	/////////////////////////////////////////////////////////////////
	public String getName()
	{
		return pkgname;
	}
	
	/////////////////////////////////////////////////////////////////
	public IProjectModel getModel()
	{
		return model;
	}
	
	/////////////////////////////////////////////////////////////////
	public Collection getSpecNames()
	{
		return specs.keySet();
	}

	/////////////////////////////////////////////////////////////////
	public Collection getClassNames()
	{
		return classes.keySet();
	}

	/////////////////////////////////////////////////////////////////
	public Collection getAlgorithmNames()
	{
		return algorithms.keySet();
	}

	/////////////////////////////////////////////////////////////////
	public Collection getUnits()
	{
		List list = new ArrayList();
		list.addAll(specs.values());
		list.addAll(algorithms.values());
		list.addAll(classes.values());
		return list;
	}

	////////////////////////////////////////////////////////////////
	public SpecificationUnit addSpecification(String spec_name)
	{
		SpecificationUnit unit = new SpecificationUnit(this, spec_name);
		MUtil.setSourceCodeTemplate(unit);
		specs.put(spec_name, unit);
		model.notifyListeners(
			new ProjectModelEvent(this, ProjectModelEvent.SPEC_ADDED, unit)
		);
		return unit;
	}
	
	////////////////////////////////////////////////////////////////
	public AlgorithmUnit addAlgorithm(String alg_name, String spec_name)
	{
		AlgorithmUnit unit = new AlgorithmUnit(this, alg_name, spec_name);
		MUtil.setSourceCodeTemplate(unit);
		algorithms.put(alg_name, unit);
		model.notifyListeners(
			new ProjectModelEvent(this, ProjectModelEvent.ALGORITHM_ADDED, unit)
		);
		return unit;
	}
	
	////////////////////////////////////////////////////////////////
	public ClassUnit addClass(String class_name)
	{
		ClassUnit unit = new ClassUnit(this, class_name);
		MUtil.setSourceCodeTemplate(unit);
		classes.put(class_name, unit);
		model.notifyListeners(
			new ProjectModelEvent(this, ProjectModelEvent.CLASS_ADDED, unit)
		);
		return unit;
	}
	
	////////////////////////////////////////////////////////////////
	public boolean removeSpecification(SpecificationUnit spec)
	{
		String spec_name = spec.getName();
		if ( spec == specs.get(spec_name) )
		{
			specs.remove(spec_name);
			return true;
		}
		return false;
	}
	
	////////////////////////////////////////////////////////////////
	public boolean removeAlgorithm(AlgorithmUnit alg)
	{
		String alg_name = alg.getName();
		if ( alg == algorithms.get(alg_name) )
		{
			algorithms.remove(alg_name);
			return true;
		}
		return false;
	}
	
	////////////////////////////////////////////////////////////////
	public boolean removeClass(ClassUnit clazz)
	{
		String clazz_name = clazz.getName();
		if ( clazz == classes.get(clazz_name) )
		{
			classes.remove(clazz_name);
			return true;
		}
		return false;
	}
	
	////////////////////////////////////////////////////////////////
	public SpecificationUnit getSpecification(String spec_name)
	{
		SpecificationUnit unit = (SpecificationUnit) specs.get(spec_name);
		return unit;
	}
	
	/////////////////////////////////////////////////////////////////
	public AlgorithmUnit getAlgorithm(String alg_name)
	{
		return (AlgorithmUnit) algorithms.get(alg_name);
	}

	/////////////////////////////////////////////////////////////////
	public ClassUnit getClass(String class_name)
	{
		return (ClassUnit) classes.get(class_name);
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * En esta clase se verifica:
	 * - nombre no vacío y sin espacios intermedios.
	 * - nombre no repetido.
	 */
	public String validateNewSpecificationName(String name)
	{
		String val = validateId(name);
		if ( val != null )
			return val;
		
		if ( specs.keySet().contains(name) )
			return "Nombre de especificación ya existe en este paquete";
		
		return null;
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * En esta clase se verifica:
	 * - nombre válido
	 * - nombre no repetido.
	 */
	public String validateNewAlgorithmName(String name)
	{
		String val = validateId(name);
		if ( val != null )
			return val;

		if ( algorithms.keySet().contains(name) )
			return "Nombre de algoritmo ya existe en este paquete";
		
		return null;
	}

	/////////////////////////////////////////////////////////////////
	/**
	 * En esta clase se verifica:
	 * - nombre no vacío y sin espacios intermedios.
	 * - nombre no repetido.
	 */
	public String validateNewClassName(String name)
	{
		String val = validateId(name);
		if ( val != null )
			return val;

		if ( classes.keySet().contains(name) )
			return "Nombre de clase ya existe en este paquete";
		
		return null;
	}

	/////////////////////////////////////////////////////////////////
	private String validateId(String id)
	{
		id = id.trim();
		if ( id.length() == 0 )
			return "Nombre vacío";
		
		if ( id.indexOf('/') >= 0 )  // posible comentario
			return "Error sintáctico: identificador mal formado.";
		
		ICompilador compilador = Loro.obtCompilador();
		compilador.ponTextoFuente(id);
		try
		{
			compilador.derivarId();
		}
		catch(CompilacionException ex)
		{
			return "Error sintáctico: identificador mal formado.";
		}
		return null;
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Retorna lo mismo que <code>getName()</code> si no es vacío;
	 * en caso contrario retorna "(anónimo)".
	 */
	public String toString()
	{
		String pkgname = getName();
		return pkgname.length() > 0 ? pkgname : "(anónimo)";
	}
}
