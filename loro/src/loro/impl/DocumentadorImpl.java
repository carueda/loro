package loro.impl;

import loro.Loro.Str;
import loro.*;
import loro.util.ManejadorUnidades;

import java.util.Vector;
import java.util.Iterator;
import java.util.List;
import java.io.File;
import java.io.IOException;

/////////////////////////////////////////////////////////////////////
/**
 * Implementacion de IDocumentador.
 *
 * @author Carlos Rueda
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
				Str.get("error.1_generating_doc", ex.getMessage())
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
		mu.getOroLoaderManager().getCoreLoader().loadUnitsFromPackage("*", units);
		
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
		ManejadorUnidades mu = ManejadorUnidades.obtManejadorUnidades();
		List list = mu.getOroLoaderManager().getExtensionFiles();
		for ( Iterator it = list.iterator(); it.hasNext(); )
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