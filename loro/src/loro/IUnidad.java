package loro;



//////////////////////////////////////////////////////
/**
 * Una unidad resultante de una compilacion.
 *
 * Esta interface no es para ser implementada por el cliente.
 *
 * @version 2002-08-20
 * @author Carlos Rueda
 */
public interface IUnidad 
{
	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre simple de esta unidad.
	 */
	public String obtNombreSimpleCadena();
	
	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre completo de esta unidad.
	 */
	public String obtNombreCompletoCadena();
	
	////////////////////////////////////////////////////////////////////
	/**
	 * Retorna el nombre del paquete. Por ejemplo, "loroI::sistema".
	 * nulo si esta unidad no tiene un paquete explicito asociado.
	 */
	public String obtNombrePaquete();
	
	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene el momento de tiempo (System.currentTimeMillis)
	 * en que fue compilada esta unidad.
	 */
	public long getMillis();

	//////////////////////////////////////////////////
	/**
	 * Obtiene el rango de texto que cubre esta unidad
	 * sobre su texto fuente.
	 */
	public Rango obtRango();
	
	
	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene una cadena que describe el tipo asociado a esta
	 * unidad.
	 * Esta cadena puede utilizarse para efectos de comparación de
	 * tipos.
	 *
	 * @param id    El identificador.
	 *
	 */
	public String getTypeString();
	
	//////////////////////////////////////////////////////
	/**
	 * Unidad especificación.
	 */
	public interface IEspecificacion extends IUnidad
	{
	}
	
	//////////////////////////////////////////////////////
	/**
	 * Unidad algoritmo.
	 */
	public interface IAlgoritmo extends IUnidad
	{
		///////////////////////////////////////////////////////////
		/**
		 * Obtiene el nombre completo de la especificacion satisfecha
		 * por este algoritmo.
		 */
		public String getSpecificationName();
	}

	//////////////////////////////////////////////////////
	/**
	 * Unidad clase.
	 */
	public interface IClase extends IUnidad
	{
	}
}