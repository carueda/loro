package loro;

import java.io.IOException;

/////////////////////////////////////////////////////////////////////
/**
 * Documentador.
 *
 * Esta interface no es para ser implementada por el cliente.
 *
 * @author Carlos Rueda
 * @version 2002-08-29
 */
public interface IDocumentador
{
	//////////////////////////////////////////////////////
	/**
	 * Genera documentacion para una unidad compilada.
	 *
	 * @param u La unidad a documentar.
	 * @param dir Directorio de destino para la documentacion.
	 *
	 * @throws LoroException Si se presenta algun error durante
	 *            el proceso.
	 */
	public void documentar(IUnidad u, String dir)
	throws LoroException;
	
	/////////////////////////////////////////////////////////////////
	/**
	 * Documenta las unidades de apoyo.
	 *
	 * @param dir Directorio de destino de la documentacion.
	 *
	 * @throws LoroException   Si hay algun problema.
	 */
	public void documentarUnidadesDeApoyo(String dir)
	throws LoroException;

	/////////////////////////////////////////////////////////////////
	/**
	 * Documenta las extensiones.
	 *
	 * @param dir Directorio de destino de la documentacion.
	 *
	 * @throws LoroException   Si hay algun problema.
	 */
	public void documentarExtensiones(String dir)
	throws LoroException, IOException;

}