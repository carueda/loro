package util.editor;

//////////////////////////////////////////////////
/**
 * Una clase interesada en recibir eventos asociado
 * a un editor, debe implementar esta interface.
 *
 * @author Carlos Rueda
 * @version 2002-01-17
 */
public interface EditorListener
{
	/////////////////////////////////////////////////////////////////
	/**
	 * Avisa que el contenido del archivo actual en
	 * edición acaba de ser modificado por el usuario.
	 */
	public void estaModificado();

	/////////////////////////////////////////////////////////////////
	/**
	 * Avisa en donde se están abriendo y guardando archivos fuentes.
	 *
	 * param dir	El directorio de fuentes.
	 */
	public void ponDirectorioFuentes(String dir);
	/////////////////////////////////////////////////////////////////
	/**
	 * Avisa el nombre del archivo actual en edicion.
	 *
	 * @param nombreArchivo	El nombre del archivo.
	 */
	public void ponNombreArchivo(String nombreArchivo);


}