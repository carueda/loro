package loro.derivacion;

//////////////////////////////////////////////////////////////////////
/**
 * Clase 'factory' encargada de obtener el derivador de trabajo.
 *
 * @author Carlos Rueda
 */
public final class ManejadorDerivacion
{
	/** La �nica instancia de derivador ofrecida por esta clase. */
	private static IDerivador derivador;
	//////////////////////////////////////////////////////////////////////
	// No instanciable.
	private ManejadorDerivacion() {}


	//////////////////////////////////////////////////////////////////////
	/**
	 * Se encarga de obtener el derivador de trabajo.
	 * M�ltiples llamadas arrojan el mismo objeto derivador.
	 */
	public static IDerivador obtDerivador()
	{
		if ( derivador == null )
		{
			// Ahora, el derivador disponible est� sobre JavaCC.
			derivador = new loro.parsers.javacc.DerivadorJavaCC();
		}

		return derivador;
	}
}