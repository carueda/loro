package loroedi.gui.project.model;

import loroedi.Info.Str;
import loroedi.gui.project.unit.*;

import loro.Loro;
import loro.IUnidad;

//////////////////////////////////////////////////
/**
 * Utilerías varias.
 *
 * @author Carlos Rueda
 */
public class MUtil
{
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene plantilla de texto fuente para una especificación tester.
	 */
	public static String getSourceCodeTemplateSpecTest(
		String pkg_name, String spec_name, String tested_spec_name
	)
	{
		String var = "alg";
		StringBuffer sb = new StringBuffer();
		if ( pkg_name != null ) {
			sb.append(Loro.Str.get("package")+ " " +pkg_name+ "\n\n");
		}
		sb.append(Str.get("templ.2_test_spec", spec_name, tested_spec_name));
		return sb.toString();
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene plantilla de texto fuente para un algoritmo tester.
	 */
	public static String getSourceCodeTemplateAlgTest(
		String pkg_name, String spec_name, String tested_spec_name
	)
	{
		String var = "alg";
		StringBuffer sb = new StringBuffer();
		if ( pkg_name != null ) {
			sb.append(Loro.Str.get("package")+ " " +pkg_name+ "\n\n");
		}
		sb.append(Str.get("templ.2_test_algorithm", spec_name, tested_spec_name));
		return sb.toString();
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene plantilla de texto fuente para una especificación.
	 */
	public static String getSourceCodeTemplateSpec(String spec_name, String pkg_name)
	{
		StringBuffer sb = new StringBuffer();
		if ( pkg_name != null )
		{
			sb.append("paquete " +pkg_name+ ";\n" + "\n");
		}
		sb.append(
			"especificación " +spec_name+ "( {{indica las entradas si las hay}} )"  +"\n"+
			"    -> {{indica la salida si la hay}}"                   +"\n"+
			"    descripción"                                           +"\n"+
			"           {{Describe brevemente el problema}}"          +"\n"+
			"    entrada"                                               +"\n"+
			"        {{Describe las entradas, si las hay}}"           +"\n"+
			"    salida"                                                +"\n"+
			"        {{Describe la salida, si la hay}}"               +"\n"+
			"    pre { {{Escribe la precondición}} }"                 +"\n"+
			"    pos { {{Escribe la poscondición}} }"                 +"\n"+
			"fin especificación"
		);
		return sb.toString();
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Pone plantilla de texto fuente para una unidad.
	 * Provisionalmente, estas plantillas están "hard-coded" aquí;
	 * luego se pondrán en recursos correspondientes.
	 */
	public static void setSourceCodeTemplate(IProjectUnit unit)
	{
		IPackageModel pkg = unit.getPackage();
		StringBuffer sb = new StringBuffer();
		boolean anonymous_pkg = pkg.getModel().isAnonymous(pkg);
		
		if ( ! anonymous_pkg )
			sb.append("paquete " +pkg.getName()+ "\n" + "\n");

		String name = unit.getName();
		if ( unit instanceof SpecificationUnit )
		{
			sb.append(
				"especificación " +name+ "( {{indica las entradas si las hay}} )"  +"\n"+
				"    -> {{indica la salida si la hay}}"                   +"\n"+
				"    descripción"                                           +"\n"+
				"           {{Describe brevemente el problema}}"          +"\n"+
				"    entrada"                                               +"\n"+
				"        {{Describe las entradas, si las hay}}"           +"\n"+
				"    salida"                                                +"\n"+
				"        {{Describe la salida, si la hay}}"               +"\n"+
				"    pre { {{Escribe la precondición}} }"                 +"\n"+
				"    pos { {{Escribe la poscondición}} }"                 +"\n"+
				"fin especificación"
			);
		}
		else if ( unit instanceof AlgorithmUnit )
		{
			IProjectModel prj = pkg.getModel();
			AlgorithmUnit alg = (AlgorithmUnit) unit;
			String spec_name = alg.getSpecificationName();
			IUnidad.IEspecificacion ne = null;
			
			SpecificationUnit spec = prj.getAlgorithmSpecification(alg);
			if ( spec != null )
			{
				// encontrada la especificación en el mismo proyecto.
				ne = (IUnidad.IEspecificacion) spec.getIUnidad();
			}
			else
			{
				// la especificación debe estar en otro proyecto del espacio de trabajo.
				ne = Loro.getSpecification(spec_name);
			}
			
			String spec_signature = null;
			if ( ne == null )
			{
				spec_signature = "{{" +"Error: No se encuentra la especificación " +spec_name+ "}}";
			}
			else
			{
				spec_signature = ne.getPrototype();
				String spec_pkg_name = ne.obtNombrePaquete();
				String spec_simple_name = ne.obtNombreSimpleCadena();

				String alg_prefix_pkg  = anonymous_pkg ? "" : pkg.getName()+"::";
				String spec_prefix_pkg = spec_pkg_name == null ? "" : spec_pkg_name+"::";
				
				// si los paquetes son distintos...
				if ( !alg_prefix_pkg.equals(spec_prefix_pkg) )
				{
					if ( Loro.getAutomaticPackageName().equals(spec_pkg_name) )
					{
						// especificación en el paquete automático.
						// Se deja spec_signature como está (que NO incluye paquete).
						// --nada por hacer.
					}
					else
					{
						// Se usa nombre completo para spec_signature:
						if ( spec_pkg_name != null )
							spec_signature = spec_pkg_name + "::" +spec_signature;
					}
					
					// En caso que este algoritmo NO esté en el paquete anónimo pero
					// la especificación si lo esté, entonces hay necesidad de agregar:
					//   utiliza especificación spec_name;
					if ( ! anonymous_pkg
					&&     spec_pkg_name == null )
					{
						sb.append("utiliza especificación " +spec_name+ "\n" + "\n");
					}
				}
				
				
			}
			
			sb.append(
				"algoritmo " +name+ " para " +spec_signature    +"\n"+
				"    descripción"                               +"\n"+
				"           {{Describe brevemente el algoritmo}}"     +"\n"+
				"inicio"                                        +"\n"+
				"    {{Escribe las acciones}}"                +"\n"+
				"fin algoritmo"
			);
		}
		else if ( unit instanceof ClassUnit )
		{
			sb.append(
				"clase " +name                                      +"\n"+
				"    descripción"                                   +"\n"+
				"           {{Describe brevemente esta clase}}"   +"\n"+
				"\n"+
				"    {{Escribe los componentes de esta clase}}"   +"\n"+
				"fin clase"
			);
		}
		
		unit.setSourceCode(sb.toString());
	}
}
