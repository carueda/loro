package loro;

/////////////////////////////////////////////////////////////////////
/**
 * Interprete para acciones.
 *
 * Esta interface no es para ser implementada por el cliente.
 *
 * @author Carlos Rueda
 * @version 2002-01-28
 */
public interface IInterprete
{
	//////////////////////////////////////////////////////////////
	/**
	 * Compila un texto.
	 *
	 * @param texto El codigo fuente a compilar.
	 *
	 * @throws CompilacionException Si se presenta algun error en 
	 *               la compilacion.
	 */
	public void compilar(String text)
	throws CompilacionException;
	//////////////////////////////////////////////////////////////
	/**
	 * Interpreta un texto.
	 *
	 * @param texto El codigo fuente a ejecutar.
	 *
	 * @return Una cadena descriptiva del resultado de la ejecucion.
	 */
	public String ejecutar(String text)
	throws AnalisisException;

	//////////////////////////////////////////////////////////////
	/**
	 * Elimina una entrada de la tabla de simbolos.
	 *
	 * @param id    El identificador cuya entrada debe eliminarse.
	 *
	 * @return true sii la entrada ha sido eliminada.
	 */
	public boolean quitarID(String id);
	/////////////////////////////////////////////////////////////////
	/**
	 * Reinicia la tabla de simbolos a vacia.
	 */
	public void reiniciar();
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Hace terminar la ejecucion en curso.
	 */
	public void terminarExternamente();

	/////////////////////////////////////////////////////////////////
	/**
	 * Dice si hay disponibilidad de ejecucion paso-a-paso.
	 */
	public boolean isTraceable();

	/////////////////////////////////////////////////////////////////
	/**
	 * Siguiente paso en la ejecucion en curso.
	 */
	public void nextStep()
	throws InterruptedException;

	/////////////////////////////////////////////////////////////////
	/**
	 * Continuar en la ejecucion en curso.
	 */
	public void resume();

	///////////////////////////////////////////////////////////////////////
	/**
	 * Pone a todas las variables el estado de asignacion indicado.
	 */
	public void ponAsignado(boolean asignado);
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * Pone el nivel de profundidad para efectos de mostrar la composicion
	 * de un objeto.
	 */
	public void ponNivelVerObjeto(int nivelVerObjeto);

	///////////////////////////////////////////////////////////////////////
	/**
	 * Pone la maxima longitud para efectos de mostrar la composicion
	 * de un arreglo
	 */
	public void ponLongitudVerArreglo(int longitudVerArreglo);
}