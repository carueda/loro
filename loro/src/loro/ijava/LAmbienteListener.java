package loro.ijava;

///////////////////////////////////////////////////////////////////////
/**
 * Objeto notificado de algunos eventos asociados
 * a la ejecución del programa Loro en curso.
 */
public interface LAmbienteListener 
{
	///////////////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando está apunto de terminarse la ejecución del
	 * programa.
	 */
	public void terminandoEjecucion();
}