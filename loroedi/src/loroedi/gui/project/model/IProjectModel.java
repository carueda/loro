package loroedi.gui.project.model;

import loroedi.gui.project.unit.*;

import loro.IUnidad;

import javax.swing.*;
import java.util.Collection;

//////////////////////////////////////////////////
/**
 * Modelo de proyecto.
 *
 * @author Carlos Rueda
 * @version 2002-07-26
 */
public interface IProjectModel
{
	//////////////////////////////////////////////////
	/**
	 * Info general sobre el proyecto.
	 */
	public interface IInfo
	{
		/////////////////////////////////////////////////////////////////
		/**
		 * Obtiene el nombre de este proyecto.
		 */
		public String getName();
	
		/////////////////////////////////////////////////////////////////
		/**
		 * Establece el nombre de este proyecto.
		 */
		public void setName(String name);
	
		/////////////////////////////////////////////////////////////////
		/**
		 * Obtiene el t�tulo de este proyecto.
		 */
		public String getTitle();
	
		/////////////////////////////////////////////////////////////////
		/**
		 * Establece el t�tulo de este proyecto.
		 */
		public void setTitle(String title);
	
		/////////////////////////////////////////////////////////////////
		/**
		 * Obtiene los autores de este proyecto.
		 */
		public String getAuthors();
	
		/////////////////////////////////////////////////////////////////
		/**
		 * Establece los autores de este proyecto.
		 */
		public void setAuthors(String authors);
	
		/////////////////////////////////////////////////////////////////
		/**
		 * Obtiene la versi�n de este proyecto.
		 */
		public String getVersion();
	
		/////////////////////////////////////////////////////////////////
		/**
		 * Establece la versi�n de este proyecto.
		 */
		public void setVersion(String version);
	
		/////////////////////////////////////////////////////////////////
		/**
		 * Obtiene la descripci�n de este proyecto.
		 */
		public String getDescription();

		/////////////////////////////////////////////////////////////////
		/**
		 * Establece la descripci�n de este proyecto.
		 */
		public void setDescription(String description);
		
		/////////////////////////////////////////////////////////////////
		/**
		 * Obtiene el c�digo de demostraci�n de este proyecto.
		 */
		public String getDemoScript();

		/////////////////////////////////////////////////////////////////
		/**
		 * Establece el c�digo de demostraci�n de este proyecto.
		 */
		public void setDemoScript(String src);

	}

	//////////////////////////////////////////////////
	/**
	 * Info de control sobre el proyecto.
	 * Los indicadores est�n pensados s�lamente para fines de control externo, 
	 * es decir, no influyen internamente en la operatividad de otros m�todos del 
	 * modelo.
	 * Es responsabilidad del cliente establecer/verificar los valores de
	 * estos indicadores para ayudarse en el control que sea necesario.
	 */
	public interface IControlInfo
	{
		/////////////////////////////////////////////////////////////////
		/**
		 * Dice si este proyecto es modificable.
		 */
		public boolean isModifiable();
		
		/////////////////////////////////////////////////////////////////
		/**
		 * Pone el indicador de modificable.
		 */
		public void setModifiable(boolean modifiable);
		
		/////////////////////////////////////////////////////////////////
		/**
		 * Dice si este proyecto es v�lido.
		 */
		public boolean isValid();
		
		/////////////////////////////////////////////////////////////////
		/**
		 * Pone el indicador de validez.
		 */
		public void setValid(boolean valid);
	}
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la informaci�n general sobre este proyecto.
	 */
	public IInfo getInfo();

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la informaci�n de control sobre este proyecto.
	 */
	public IControlInfo getControlInfo();

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene los paquetes de este proyecto.
	 * Cada elemento es de tipo IPackageModel.
	 * Never null.
	 */
	public Collection getPackages();

	////////////////////////////////////////////////////////////////
	/**
	 * Valida si el nombre dado es aceptable como nombre de un nuevo 
	 * paquete en este paquete.
	 *
	 * @param name Nombre a validar.
	 *
	 * @return null si el nombre es v�lido; o un breve mensaje indicando
	 *         por que el nombre es inv�lido
	 */
	public String validateNewPackageName(String name);
	
	////////////////////////////////////////////////////////////////
	/**
	 * Agrega un paquete.
	 *
	 * @param name El nombre para el paquete. Si este par�metro es null
	 *             o es vac�o, entonces se agrega el paquete an�nimo.
	 *
	 * @return     El paquete agregado.
	 */
	public IPackageModel addPackage(String name);
	
	////////////////////////////////////////////////////////////////
	/**
	 * Remueve un paquete.
	 *
	 * @param pkgm  El paquete a remover.
	 *
	 * @return    true si el paquete fue removido.
	 */
	public boolean removePackage(IPackageModel pkgm);
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Dice si el paquete dado es el an�nimo.
	 */
	public boolean isAnonymous(IPackageModel pkg);
	
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene un paquete del proyecto.
	 *
	 * @return el paquete; null si no existe.
	 */
	public IPackageModel getPackage(String name);
	
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la unidad.
	 * Recibe el nombre cualificado de la unidad.
	 *
	 * @return la unidad.
	 */
	public SpecificationUnit getSpecification(String q_spec_name);
	
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la unidad.
	 * Recibe el nombre cualificado de la unidad.
	 *
	 * @return la unidad.
	 */
	public AlgorithmUnit getAlgorithm(String q_alg_name);
	
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene los algoritmos que implementan una especificaci�n.
	 *
	 * @return los algoritmos. Cada elemento es un AlgorithmUnit.
	 */
	public Collection getAlgorithms(SpecificationUnit spec);
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la especificaci�n de un algoritmo.
	 *
	 * @return la unidad.
	 */
	public SpecificationUnit getAlgorithmSpecification(AlgorithmUnit alg);

	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la unidad.
	 * Recibe el nombre cualificado de la unidad.
	 *
	 * @return la unidad.
	 */
	public ClassUnit getClass(String q_class_name);
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene los proyectos de los que depende este proyecto.
	 * Cada elemento es de tipo IProjectModel.
	 * Never null.
	 */
	public Collection getSupportingProjects();

	/////////////////////////////////////////////////////////////////
	/**
	 * Adiciona un proyecto de soporte para este proyecto.
	 */
	public void addSupportingProject(IProjectModel prj);
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Adiciona el listener. No se pueden tener listeners repetidos.
	 */
	public void addProjectModelListener(IProjectModelListener lis);

	/////////////////////////////////////////////////////////////////
	/**
	 * Remueve el listener.
	 */
	public void removeProjectModelListener(IProjectModelListener lis);

	/////////////////////////////////////////////////////////////////
	/**
	 * Notifies all registered listeners about an event.
	 */
	public void notifyListeners(ProjectModelEvent ev);
	
	
}
