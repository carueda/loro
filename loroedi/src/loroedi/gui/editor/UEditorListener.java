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
	 * UEditor llama este m�todo para avisar que el contenido del archivo 
	 * actual en edici�n acaba de ser modificado por el usuario.
	 */
	public void changed();

	/////////////////////////////////////////////////////////////////
	/**
	 * UEditor llama este m�todo para atender la acci�n "view-unit-doc-from-editor".
	 */
	public void viewDoc();

	/////////////////////////////////////////////////////////////////
	/**
	 * UEditor llama este m�todo cuando el usuario lanza la acci�n "save".
	 */
	public void save();

	/////////////////////////////////////////////////////////////////
	/**
	 * UEditor llama este m�todo cuando el usuario lanza la acci�n "compile".
	 */
	public void compile();

	/////////////////////////////////////////////////////////////////
	/**
	 * UEditor llama este m�todo cuando el usuario lanza la acci�n "execute".
	 */
	public void execute();

	/////////////////////////////////////////////////////////////////
	/**
	 * UEditor llama este m�todo cuando el usuario lanza la acci�n "reload".
	 */
	public void reload();

	/////////////////////////////////////////////////////////////////
	/**
	 * UEditor llama este m�todo para avisar que el usuario quiere cerrar
	 * la ventana.
	 */
	public void closeWindow();

}
