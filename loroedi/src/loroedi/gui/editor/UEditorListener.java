package loroedi.gui.editor;

import javax.swing.*;

//////////////////////////////////////////////////
/**
 * Interface para notificar ciertos eventos asociados a un UEditor.
 * @author Carlos Rueda
 */
public interface UEditorListener
{
	/////////////////////////////////////////////////////////////////
	/**
	 * UEditor llama este método para avisar que el contenido del archivo 
	 * actual en edición acaba de ser modificado por el usuario.
	 */
	public void changed();

	/////////////////////////////////////////////////////////////////
	/**
	 * UEditor llama este método para atender la acción "view-unit-doc-from-editor".
	 */
	public void viewDoc();

	/////////////////////////////////////////////////////////////////
	/**
	 * UEditor llama este método cuando el usuario lanza la acción "save".
	 */
	public void save();

	/////////////////////////////////////////////////////////////////
	/**
	 * UEditor llama este método cuando el usuario lanza la acción "compile".
	 */
	public void compile();

	/////////////////////////////////////////////////////////////////
	/**
	 * UEditor llama este método cuando el usuario lanza la acción "execute".
	 */
	public void execute();

	/////////////////////////////////////////////////////////////////
	/**
	 * UEditor llama este método cuando el usuario lanza la acción "reload".
	 */
	public void reload();

	/////////////////////////////////////////////////////////////////
	/**
	 * UEditor llama este método para avisar que el usuario quiere cerrar
	 * la ventana.
	 */
	public void closeWindow();

}
