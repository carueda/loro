package loro.java.loroI;

import loro.ijava.*;

/////////////////////////////////////////////////////////////////
/**
 * Implementacion de algoritmos del paquete loroI::entsal.
 *
 * @author Carlos Rueda
 * @version 2000-07-30
 */
public class EntSal 
{
	//////////////////////////////////////////////////////////////
	/**
	 * existeArchivo.
	 */
	public static boolean esArchivo(LAmbiente $amb,
		String nombreArchivo
	)
	{
		java.io.File file = new java.io.File(nombreArchivo);
		return file.exists();
	}
	//////////////////////////////////////////////////////////////
	/**
	 * esDirectorio.
	 */
	public static boolean esDirectorio(LAmbiente $amb,
		String ruta
	)
	{
		java.io.File file = new java.io.File(ruta);
		return file.isDirectory();
	}
	//////////////////////////////////////////////////////////////
	/**
	 * obtDirectorio.
	 */
	public static String[] obtDirectorio(LAmbiente $amb,
		String nombreDir
	)
	{
		java.io.File file = new java.io.File(nombreDir);
		return file.list();
	}
}