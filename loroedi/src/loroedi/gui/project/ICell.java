package loroedi.gui.project;

//////////////////////////////////////////////////
/**
 * Permite manejar la información asociada a las celdas
 * del diagrama.
 *
 * @author Carlos Rueda
 * @version 2002-08-13
 */
public interface ICell
{
	/////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el objeto de usuario.
	 */
	public Object getUserObject();

	/////////////////////////////////////////////////////////////////
	/**
	 * Pone el objeto de usuario.
	 */
	public void setUserObject(Object obj);

}
