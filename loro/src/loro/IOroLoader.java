package loro;

import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import loro.arbol.NUnidad;


///////////////////////////////////////////////////////////////
/**
 * Cargador de unidades compiladas.
 *
 * Un cargador obtiene unidades compiladas y otros recursos al 
 * estilo de un ClassLoader de Java. 
 *
 * @version 2002-08-06
 * @author Carlos Rueda
 */
public interface IOroLoader
{
	//////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre de este cargador.
	 */
	public String getName();

	//////////////////////////////////////////////////////////////////////
	/**
	 * Returns an input stream for reading the specified resource.
	 */
	InputStream getResourceAsStream(String name);

	/////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene una unidad.
	 * Retorna null si no existe o no se puede leer.
	 */
	public NUnidad getUnit(String nombre);
	
	/////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el código fuente de una unidad.
	 */
	public String getUnitSource(String nombre);
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene los nombres de los paquetes de este cargador.
	 */
	public List getPackageNames(List list);

	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene los nombres de todas las unidades de un paquete dado.
	 * Cada nombre puede ser utilizado en getUnit para obtener la
	 * unidad propiamente.
	 * NOTA: Es posible incluir aquí unn nombre de unidad que no
	 * pueda cargarse exitosamente con getUnit.
	 *
	 * @param nombrePaquete    Nombre del paquete en estilo '::'.
	 *                         Si termina en "*", se incluyen subpaquetes.
	 *
	 * @param names            En donde se agregan los nombres cualificados
	 *                         de las unidades encontradas con la
	 *                         terminación característica, ejemplo:
	 *                         foo::baz::unaEspec.e
	 */
	public List loadUnitNamesFromPackage(String nombrePaquete, List names);

	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene todas las unidades VALIDAS de un paquete dado.
	 * Básicamente es una servicio basado en loadUnitNamesFromPackage y
	 * getUnit.
	 *
	 * @param nombrePaquete    Nombre del paquete en estilo '::'.
	 *                         Si termina en "*", se incluyen subpaquetes.
	 *
	 * @param units            En donde se agregan las unidades encontradas.
	 */
	public List loadUnitsFromPackage(String nombrePaquete, List units);

	/////////////////////////////////////////////////////////////////////
	/**
	 * Verifica el cargador.
	 * Retorna true si todo bien. En caso contrario se agregan a la lista
	 * los nombres de todas las unidades o extensiones que producen algún 
	 * error ante intento de cargarse. Si esta lista es vacía, la verificación
	 * se detiene ante el primer error encontrado y se retorna false.
	 */
	public boolean verify(List list);

	///////////////////////////////////////////////////////////////
	/**
	 * Cierra este cargador.
	 */
	public void close()
	throws IOException;



}
