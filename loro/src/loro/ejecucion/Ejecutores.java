package loro.ejecucion;



////////////////////////////////////////////////////////
/**
 * Proveedor de ejecutores.
 */
public class Ejecutores
{
	////////////////////////////////////////////////////////
	// No instanciable
	private Ejecutores() {}



	/** Tipo para ejecutor basico (no terminable). */
	public static final int EJECUTOR_BASICO = 0;
	/** Tipo para ejecutor terminable externamente. */
	public static final int EJECUTOR_TERMINABLE_EXTERNAMENTE = 2;

	///////////////////////////////////////////////////
	/**
	 * Crea un ejecutor.
	 *
	 * @param tipoEjecutor Tipo de ejecutor deseado.
	 *
	 * @throws IllegalArgumentException Si el tipo deseado es invalido.
	 */
	public static LoroEjecutor crearEjecutor(int tipoEjecutor)
	{
		switch ( tipoEjecutor )
		{
			case EJECUTOR_BASICO:
				return new LoroEjecutor();

			case EJECUTOR_TERMINABLE_EXTERNAMENTE:
				return new EjecutorTerminable();

			default:
				throw new IllegalArgumentException();
		}
	}
}