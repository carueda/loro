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
		if ( pkg_name != null ) {
			sb.append(Loro.Str.get("package")+ " " +pkg_name+ "\n\n");
		}
		sb.append(Str.get("templ.1_spec", spec_name));
		return sb.toString();
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Pone plantilla de texto fuente para una unidad.
	 */
	public static void setSourceCodeTemplate(IProjectUnit unit)
	{
		IPackageModel pkg = unit.getPackage();
		StringBuffer sb = new StringBuffer();
		boolean anonymous_pkg = pkg.getModel().isAnonymous(pkg);
		
		if ( ! anonymous_pkg )
			sb.append(Loro.Str.get("package")+ " " +pkg.getName()+ "\n\n");

		String name = unit.getName();
		if ( unit instanceof SpecificationUnit ) {
			sb.append(Str.get("templ.1_spec", name));
		}
		else if ( unit instanceof AlgorithmUnit ) {
			IProjectModel prj = pkg.getModel();
			AlgorithmUnit alg = (AlgorithmUnit) unit;
			String spec_name = alg.getSpecificationName();
			IUnidad.IEspecificacion ne = null;
			
			SpecificationUnit spec = prj.getAlgorithmSpecification(alg);
			if ( spec != null ) {
				// encontrada la especificación en el mismo proyecto.
				ne = (IUnidad.IEspecificacion) spec.getIUnidad();
			}
			else {
				// la especificación debe estar en otro proyecto del espacio de trabajo.
				ne = Loro.getSpecification(spec_name);
			}
			
			String spec_signature = null;
			if ( ne == null ) {
				spec_signature = "{{Error: " +spec_name+ " : spec not found}}";
			}
			else {
				spec_signature = ne.getPrototype();
				String spec_pkg_name = ne.obtNombrePaquete();
				String spec_simple_name = ne.obtNombreSimpleCadena();

				String alg_prefix_pkg  = anonymous_pkg ? "" : pkg.getName()+"::";
				String spec_prefix_pkg = spec_pkg_name == null ? "" : spec_pkg_name+"::";
				
				// si los paquetes son distintos...
				if ( !alg_prefix_pkg.equals(spec_prefix_pkg) ) {
					if ( Loro.getLanguageInfo().getAutomaticPackageName().equals(spec_pkg_name) ) {
						// especificación en el paquete automático.
						// Se deja spec_signature como está (que NO incluye paquete).
						// --nada por hacer.
					}
					else {
						// Se usa nombre completo para spec_signature:
						if ( spec_pkg_name != null )
							spec_signature = spec_pkg_name + "::" +spec_signature;
					}
					
					// En caso que este algoritmo NO esté en el paquete anónimo pero
					// la especificación si lo esté, entonces hay necesidad de agregar:
					//   utiliza especificación spec_name;
					if ( ! anonymous_pkg  &&  spec_pkg_name == null ) {
						sb.append(Loro.Str.get("uses")+ " " 
							+Loro.Str.get("specification")+ " " +spec_name+ "\n\n");
					}
				}
				
				
			}
			sb.append(Str.get("templ.2_algorithm", name, spec_signature));
		}
		else if ( unit instanceof ClassUnit ) {
			sb.append(Str.get("templ.1_class", name));
		}
		
		unit.setSourceCode(sb.toString());
	}
}
