package loro.compilacion;

import loro.IUbicable;
import loro.Rango;

////////////////////////////////////////////////////////////////
/**
 * Implementación de conveniencia de la interface IUbicable
 * para permitir formas adicionales de indicar ubicaciones.
 */
class Ubicacion implements IUbicable 
{
	/** Mi rango completo. */
	Rango rango;

	////////////////////////////////////////////////////////////////
	/**
	 * Crea una ubicación correspondiente al rango cubierto por
	 * un arreglo de objetos ubicables.
	 */
	Ubicacion(IUbicable[] ubicables)
	{
		rango = new Rango(
			ubicables[0].obtRango(),
			ubicables[ubicables.length -1].obtRango()
		);
	}

	//////////////////////////////////////////////////////////////////
	public Rango obtRango()
	{
		return rango;
	}
}