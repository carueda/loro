package loro;



//////////////////////////////////////////////////////
/**
 * Un fuente resultante de una compilaci�n.
 * La estructura de un fuente es:
 *
 * <ul>
 *	<li> indicaci�n de paquete (opcional)
 *	<li> instrucciones 'utiliza' (0 � m�s)
 *	<li> definici�n de unidades (1 � m�s)
 * </ul>
 *
 * Esta interface no es para ser implementada por el cliente.
 *
 * @since 0.8pre1
 *
 * @version 2002-08-22
 * @author Carlos Rueda
 */
public interface IFuente 
{
	///////////////////////////////////////////////////////////////
	/**
	 * Retorna el nombre del paquete. Por ejemplo, "loroI::sistema".
	 * nulo si esta unidad no tiene un paquete explicito asociado.
	 */
	public String getPackageName();
	
	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene la lista de instrucciones 'utiliza'.
	 */
	public IUtiliza[] obtUtilizas();
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la unidades del fuente.
	 */
	public IUnidad[] obtUnidades();
	
	//////////////////////////////////////////////////////
	/**
	 * Representa una instrucci�n 'utiliza'.
	 */
	public interface IUtiliza
	{
		/////////////////////////////////////////////////////////
		/**
		 * Obtiene lo que se utiliza: "algoritmo", "especificacion", "clase".
		 */
		public String obtQue();
		
		/////////////////////////////////////////////////////////
		/**
		 * Obtiene el nombre cualificado del elemento que se utiliza.
		 */
		public String getName();
	}
	
}
