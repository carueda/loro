package loro.doc;

import loro.arbol.NUnidad;

import loro.visitante.VisitanteException;
import loro.util.ManejadorUnidades;



import java.io.IOException;

import java.util.*;

///////////////////////////////////////////////////////////////////
/**
 * Ofrece algunos servicios para generar documentacion.
 *
 * @author Carlos Rueda
 * @version 09/11/01
 */
public class Documentador
{
	////////////////////////////////////////////////////////////////////
	/**
	 * Genera documentacion para todas las unidades en un archivo zip.
	 *
	 * @param zipFilename   Nombre del archivo zip.
	 * @param baseDir    Directorio para poner la documentacion.
	 *
	 * @return           Un mensaje en caso de error; null si todas
	 *                   las unidades se procesaron exitosamente,
	 *
	 * @throws IOException  Si hay problema para abrir archivo.
	 */
	public static String procesarArchivoZip(String zipFilename, String baseDir)
	throws IOException
	{
		ManejadorUnidades mu = ManejadorUnidades.obtManejadorUnidades();
		List units = mu.cargarUnidadesDeZip(zipFilename, null);
		return procesarLista(units, baseDir);
	}

	////////////////////////////////////////////////////////////////////
	/**
	 * Genera documentacion para las unidades en una lista.
	 *
	 * @param units      La unidades (NUnidad).
	 * @param baseDir    Directorio para poner la documentacion.
	 *
	 * @return           Un mensaje en caso de error; null si todas
	 *                   las unidades se procesaron exitosamente,
	 */
	public static String procesarLista(List units, String baseDir)
	{
		StringBuffer sb = null;
		for ( Iterator it = units.iterator(); it.hasNext(); )
		{
			NUnidad n = null;
			try
			{
				n = (NUnidad) it.next();
				VisitanteLoroDoc v = new VisitanteLoroDoc(baseDir);
				n.aceptar(v);
			}
			catch (Exception ex)
			{
				if ( sb == null )
				{
					sb = new StringBuffer();
				}
				sb.append(
					(n != null ? n.obtNombreCompletoCadena()+ ": " : "")
					+ "error: " +ex.getMessage()+ "\n"
				);
			}
		}
		return sb == null ? null : sb.toString();
	}

	////////////////////////////////////////////////////////////////////
	/**
	 * Genera documentacion para todas las unidades en paquete especifico.
	 *
	 * @param  pn        Nombre del paquete.
	 * @param baseDir    Directorio para poner la documentacion.
	 *
	 * @return           Un mensaje en caso de error; null si todas
	 *                   las unidades se procesaron exitosamente,
	 */
	public static String procesarPaquete(String pn, String baseDir)
	{
		ManejadorUnidades mu = ManejadorUnidades.obtManejadorUnidades();
		List units = new ArrayList();
		mu.cargarUnidadesDePaquete(pn, units);
		return procesarLista(units, baseDir);
	}
}