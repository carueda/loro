package loro;

import java.io.Reader;
import java.io.Writer;
import java.util.List;

/////////////////////////////////////////////////////////////////////
/**
 * Interface para acceder a los servicios de compilaci�n del sistema.
 *
 * La compilaci�n como tal puede operar con respecto a un texto fuente
 * espec�fico (proveniente de un String o de un java.io.Reader), 
 * y tambi�n puede operar sobre un directorio completo.
 *
 * Tambi�n ofrece un servicio de "anticompilaci�n" orientado a facilitar
 * tareas de prueba del sistema con respecto a programas que se saben
 * son inv�lidos. 
 *
 * <p>Esta interface no es para ser implementada por el cliente.
 *
 * @see loro.Loro#obtCompilador
 *
 * @author Carlos Rueda
 * @version 2002-09-18
 */
public interface ICompilador
{
	///////////////////////////////////////////////////////////////////////
	/**
	 * Compila el c�digo fuente asociado en espera de la definici�n de
	 * una especificaci�n.
	 *
	 * @see #ponTextoFuente(String)
	 * @see #ponTextoFuente(Reader)
	 *
	 * @param expected_pkgname
	 *             Nombre del paquete (en estilo "::") que debe aparecer indicado en el fuente.
	 * @param expected_unitname
	 *             Nombre de la especificaci�n que debe aparecer indicado en el fuente.
	 *
	 * @return 	La unidad compilada resultante.
	 *
	 * @exception CompilacionException
	 *		Si se presenta alg�n error en la compilaci�n.
	 */
	public IUnidad compileSpecificaction(
		String expected_pkgname, 
		String expected_unitname
	)
	throws CompilacionException;
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * Compila el c�digo fuente asociado en espera de la definici�n de
	 * un algoritmo.
	 *
	 * @see #ponTextoFuente(String)
	 * @see #ponTextoFuente(Reader)
	 *
	 * @param expected_pkgname
	 *             Nombre del paquete (en estilo "::") que debe aparecer indicado en el fuente.
	 * @param expected_unitname
	 *             Nombre del algoritmo que debe aparecer indicado en el fuente.
	 * @param expected_specname
	 *             Nombre de la especificaci�n correspondiente.
	 *             Este par�metro es ignorado si es null.
	 *
	 * @return 	La unidad compilada resultante.
	 *
	 * @exception CompilacionException
	 *		Si se presenta alg�n error en la compilaci�n.
	 */
	public IUnidad compileAlgorithm(
		String expected_pkgname, 
		String expected_unitname,
		String expected_specname
	)
	throws CompilacionException;
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * Compila el c�digo fuente asociado en espera de la definici�n de
	 * una clase.
	 *
	 * @see #ponTextoFuente(String)
	 * @see #ponTextoFuente(Reader)
	 *
	 * @param expected_pkgname
	 *             Nombre del paquete (en estilo "::") que debe aparecer indicado en el fuente.
	 * @param expected_unitname
	 *             Nombre de la clase que debe aparecer indicado en el fuente.
	 *
	 * @return 	La unidad compilada resultante.
	 *
	 * @exception CompilacionException
	 *		Si se presenta alg�n error en la compilaci�n.
	 */
	public IUnidad compileClass(
		String expected_pkgname, 
		String expected_unitname
	)
	throws CompilacionException;
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * Compila el programa fuente asociado.
	 *
	 * @see #ponTextoFuente(String)
	 * @see #ponTextoFuente(Reader)
	 *
	 * @return 	El objeto compilado para el fuente.
	 *
	 * @exception CompilacionException
	 *		Si se presenta alg�n error en la compilaci�n.
	 */
	public IFuente compilarFuente()
	throws CompilacionException;
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * Deriva el programa fuente asociado.
	 * Derivar significa s�lamente hacer el chequeo sint�ctico (construcci�n
	 * del �rbol). Utilice este servicio para conocer informaci�n b�sica
	 * sobre los elementos encontrados en el c�digo fuente.
	 *
	 * @since 0.8pre1
	 *
	 * @see #ponTextoFuente(String)
	 * @see #ponTextoFuente(Reader)
	 *
	 * @return 	El arreglo de unidades derivadas resultante.
	 *
	 * @exception CompilacionException
	 *		Si se presenta alg�n error en la derivaci�n.
	 */
	public IUnidad[] derivarFuente()
	throws CompilacionException;
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * Deriva un identificador:  id  &lt;EOF>
	 * <p> No acepta variable sem�ntica.
	 *
	 * @exception CompilacionException
	 *		Si se presenta alg�n error en la derivaci�n.
	 */
	public void derivarId()
	throws CompilacionException;
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * Deriva un nombre estilo id1::id2::id3  &lt;EOF>
	 * <p> Ning�n id puede ser variable sem�ntica.
	 *
	 * @exception CompilacionException
	 *		Si se presenta alg�n error en la derivaci�n.
	 */
	public void derivarNombre()
	throws CompilacionException;
	
	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene las unidades que son algoritmos generados en la 
	 * ultima compilacion.
	 */
	public IUnidad[] obtAlgoritmos();
	///////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el texto fuente asociado actualmente a este compilador.
	 *
	 * @return El texto fuente asociado actualmente a este compilador.
	 */
	public String obtTextoFuente();
	
	///////////////////////////////////////////////////////////////
	/**
	 * Pone el indicador de guardar compilados en disco.
	 */
	public void ponGuardarCompilados(boolean g);
	
	//////////////////////////////////////////////////////////////
	/**
	 * Pone el directorio de destino para compilaciones.
	 *
	 * Por defecto, este compilador toma el directorio actual de
	 * trabajo, propiedad "user.dir".
	 *
	 * @param dir El directorio de destino.
	 *
	 * @throws NullPointerException Si dir es null.
	 */
	public void ponDirectorioDestino(String dir);

	//////////////////////////////////////////////////////////////
	/**
	 * Pone el nombre de archivo fuente para efectos de asociarlo 
	 * a las unidades que resulten de subsiguientes compilaciones.
	 * Ningun otro manejo se hace con este nombre.
	 *
	 * @param nombreArchivo El nombre del archivo fuente a asociar.
	 */
	public void ponNombreArchivo(String nombreArchivo);

	///////////////////////////////////////////////////////////////////////
	/**
	 * Establece fuente a examinar.
	 *
	 * @param fuente 	El programa fuente.
	 *
	 * @return Este compilador.
	 */
	public ICompilador ponTextoFuente(Reader fuente);
	///////////////////////////////////////////////////////////////////////
	/**
	 * Establece fuente a examinar.
	 *
	 * @param fuente 	El programa fuente.
	 *
	 * @return Este compilador.
	 */
	public ICompilador ponTextoFuente(String fuente);


	///////////////////////////////////////////////////////////////////////
	/**
	 * Compila los archivos fuentes dados en una lista.
	 *
	 * @param archivos Los nombres (String) de los archivos fuentes a compilar.
	 * @param unidades A esta lista se agregan todas las unidades compiladas.
	 *                 Puede ser null.
	 * @param errores Para escribir posibles errores tanto de compilaci�n, como
	 *                otros (archivo no encontrado, por ejemplo).
	 *
	 * @return 	N�mero de archivos compilados exitosamente.
	 */
	public int compilarListaArchivos(List archivos, List unidades, Writer errores);
	
	///////////////////////////////////////////////////////////////////////
	/**
	 * Anti-compila los archivos fuentes dados. Este servicio est� orientado a
	 * apoyar tareas de pruebas del sistema.
	 *
	 * Anti-compilar significa compilar en espera de alg�n error de compilaci�n.
	 * Ahora mismo, no es posible indicar cu�l tipo de error concretamente.
	 * Si alg�n archivo compila exitosamente, se escribe un mensaje de aviso
	 * por writer indicado.
	 *
	 * @param archivos Los nombres (String) de los archivos fuentes a anticompilar.
	 * @param errores Para escribir posibles errores NO asociados a compilaci�n.
	 *                Los errores de compilaci�n NO se reportan, simplemente se lleva 
	 *                un conteo de los archivos que producen problemas de compilaci�n.
	 *
	 * @return 	N�mero de archivos anticompilados, es decir, archivos que generan
	 *          alg�n error de compilaci�n.
	 */
	public int anticompilarListaArchivos(List archivos, Writer errores);
}