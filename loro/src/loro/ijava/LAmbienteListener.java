package loro.ijava;

///////////////////////////////////////////////////////////////////////
/**
 * Objeto notificado de algunos eventos asociados
 * a la ejecuci�n del programa Loro en curso.
 */
public interface LAmbienteListener 
{
	///////////////////////////////////////////////////////////////////////
	/**
	 * Llamado cuando est� apunto de terminarse la ejecuci�n del
	 * programa.
	 */
	public void terminandoEjecucion();
}