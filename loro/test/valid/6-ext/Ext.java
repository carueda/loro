package ext;

import loro.ijava.*;

public class Ext
{
	public static void ext(LAmbiente $amb)
	throws LException
	{
		$amb.obtManejadorEntradaSalida()
		.escribirln("Hola desde ext.Ext en Java");
	}
}
