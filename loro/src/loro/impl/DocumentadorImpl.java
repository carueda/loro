package loro.impl;

import loro.*;
import loro.util.ManejadorUnidades;

import java.util.Vector;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;

/////////////////////////////////////////////////////////////////////
/**
 * Implementacion de IDocumentador.
 *
 * @author Carlos Rueda
 * @version 2002-08-29
 */
public class DocumentadorImpl implements IDocumentador
{
	
	//////////////////////////////////////////////////////
	public void documentar(IUnidad u, String dir)
	throws LoroException
	{
		loro.arbol.NUnidad n = (loro.arbol.NUnidad) u;
		
		try
		{
			loro.visitante.IVisitante doc = new loro.doc.VisitanteLoroDoc(dir);
			n.aceptar(doc);
		}
		catch ( Exception ex )
		{
			throw new LoroException(
				"Error al generar documentacion: " +ex.getMessage()
			);
		}
	}
	/////////////////////////////////////////////////////////////////
	public void documentarUnidadesDeApoyo(String dir)
	throws LoroException
	{
		// cargue las unidades de apoyo:
		ManejadorUnidades mu = ManejadorUnidades.obtManejadorUnidades();
		Vector units = new Vector();
		mu.cargarUnidadesDeApoyo(units);
		
		// genere la documentacion
		String s = loro.doc.Documentador.procesarLista(units, dir);
		if ( s != null )
		{
			// problemas:
			throw new LoroException(s);
		}
	}
	
	/////////////////////////////////////////////////////////////////
	public void documentarExtensiones(String dir)
	throws LoroException, IOException
	{
		// cargue las unidades de apoyo:
		ManejadorUnidades mu = ManejadorUnidades.obtManejadorUnidades();
		for ( Iterator it = mu.getExtensionFiles().iterator(); it.hasNext(); )
		{
			File file = (File) it.next();
			String s = loro.doc.Documentador.procesarArchivoZip(file.getAbsolutePath(), dir);
			if ( s != null )
			{
				// problemas:
				throw new LoroException(s);
			}
		}
	}
}