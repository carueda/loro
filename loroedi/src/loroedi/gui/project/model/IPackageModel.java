package loroedi.gui.project.model;

import loroedi.gui.project.unit.*;

import loro.IUnidad;

import javax.swing.*;
import java.util.Collection;

//////////////////////////////////////////////////
/** ~!~
 * @version 2002-07-13
 */
public interface IPackageModel
{
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre de este paquete.
	 */
	public String getName();

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el proyecto al que pertenece este paquete.
	 */
	public IProjectModel getModel();

	//////////////////////////////////////////////////
	/**
	 * Dice si este paquete es modificable.
	 * Esta condición la determina el proyecto al que perteneza
	 * este paquete.
	 */
	public boolean isModifiable();
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene todas la unidades de este paquete.
	 * Cada elemento es un IProjectUnit.
	 * Never null.
	 */
	public Collection getUnits();
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene los nombres de las especificaciones de este paquete.
	 * Never null.
	 */
	public Collection getSpecNames();

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene los nombres de las clases de este paquete.
	 * Never null.
	 */
	public Collection getClassNames();

	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene los nombres de los algoritmos de este paquete.
	 * Never null.
	 */
	public Collection getAlgorithmNames();

	////////////////////////////////////////////////////////////////
	/**
	 * Agrega una especificacion al paquete.
	 */
	public SpecificationUnit addSpecification(String name);
	
	////////////////////////////////////////////////////////////////
	/**
	 * Remueve una especificacion del paquete.
	 */
	public boolean removeSpecification(SpecificationUnit spec);
	
	////////////////////////////////////////////////////////////////
	/**
	 * Agrega un algoritmo al paquete.
	 */
	public AlgorithmUnit addAlgorithm(String alg_name, String spec_name);

	////////////////////////////////////////////////////////////////
	/**
	 * Remueve un algoritmo del paquete.
	 */
	public boolean removeAlgorithm(AlgorithmUnit alg);

	////////////////////////////////////////////////////////////////
	/**
	 * Agrega una clase al paquete.
	 */
	public ClassUnit addClass(String class_name);

	////////////////////////////////////////////////////////////////
	/**
	 * Remueve una clase del paquete.
	 */
	public boolean removeClass(ClassUnit clazz);

	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la unidad.
	 *
	 * @return la unidad.
	 */
	public SpecificationUnit getSpecification(String spec_name);
	
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la unidad.
	 *
	 * @return la unidad.
	 */
	public AlgorithmUnit getAlgorithm(String alg_name);

	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la unidad.
	 *
	 * @return la unidad.
	 */
	public ClassUnit getClass(String class_name);

	////////////////////////////////////////////////////////////////
	/**
	 * Valida si el nombre dado es aceptable como nombre de una nueva 
	 * especificación en este paquete.
	 *
	 * @param name Nombre a validar.
	 *
	 * @return null si el nombre es válido; o un breve mensaje indicando
	 *         por que el nombre es inválido
	 */
	public String validateNewSpecificationName(String name);
	
	////////////////////////////////////////////////////////////////
	/**
	 * Valida si el nombre dado es aceptable como nombre de un nuevo 
	 * algoritmo en este paquete.
	 *
	 * @param alg_name Nombre de algoritmo a validar.
	 *
	 * @return null si el nombre es válido; o un breve mensaje indicando
	 *         por que el nombre es inválido
	 */
	public String validateNewAlgorithmName(String alg_name);

	////////////////////////////////////////////////////////////////
	/**
	 * Valida si el nombre dado es aceptable como nombre de una nueva 
	 * clase en este paquete.
	 *
	 * @param class_name Nombre de clase a validar.
	 *
	 * @return null si el nombre es válido; o un breve mensaje indicando
	 *         por que el nombre es inválido
	 */
	public String validateNewClassName(String class_name);
}
