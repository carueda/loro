package loro.ijava;

//////////////////////////////////////////////////////////////////////
/**
 * Ambiente para fines de implementacion de algoritmos Loro en Java.
 * Una instancia de esta clase siempre sera el primer argumento
 * para cualquier metodo Java que implemente un algoritmo en Loro.
 * A traves de este argumento podra obtener, mediante el nombre,
 * otros algoritmos y clases Loro, asi como crear instancias,
 * ejecutar algoritmos y otros servicios asociados a los flujos
 * de entrada/salida del programa Loro actualmente en ejecucion.
 *
 * @author Carlos Rueda
 */
public interface LAmbiente 
{
	//////////////////////////////////////////////////////////////
	/**
	 * Adiciona un receptor de eventos sobre este ambiente.
	 *
	 * @param al	El receptor de eventos.
	 */
	public void addAmbienteListener(LAmbienteListener al)
	throws LException;
	//////////////////////////////////////////////////////////////
	/**
	 * Crea una instancia de una clase Loro.
	 *
	 * @param c			La clase del objeto a crear.
	 * @return			El objeto creado.
	 * @throws			LException Si se presenta algun error.
	 */
	public LObjeto crearInstancia(LClase c)
	throws LException;
	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene un algoritmo Loro por su nombre completo.
	 *
	 * @param nombre	Nombre completo del algoritmo.
	 * @return			El algoritmo correspondiente.
	 *                  null si no se encuentra.
	 */
	public LAlgoritmo obtAlgoritmo(String nombre);

	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene una clase Loro por su nombre completo.
	 *
	 * @param nombre	Nombre completo de la clase.
	 * @return			La clase correspondiente.
	 * @throws			LException Si no fue posible obtener
	 *					la clase.
	 */
	public LClase obtClase(String nombre)
	throws LException;
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el manejador de entrada-salida vigente en la
	 * ejecucion del programa Loro actual.
	 */
	public LManejadorES obtManejadorEntradaSalida()
	throws LException;
	//////////////////////////////////////////////////////////////
	/**
	 * Quita un receptor de eventos sobre este ambiente.
	 *
	 * @param al	El receptor de eventos.
	 */
	public void removeAmbienteListener(LAmbienteListener al)
	throws LException;
	//////////////////////////////////////////////////////////////
	/**
	 * Termina la ejecución de un programa Loro.
	 *
	 * @param codigo	Código de terminación.
	 */
	public void terminarEjecucion(int codigo)
	throws LException;
}