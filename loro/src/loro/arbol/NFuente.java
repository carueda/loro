package loro.arbol;

import loro.IFuente;
import loro.IUnidad;
import loro.Rango;
import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

////////////////////////////////////////////////
/**
 * Fuente que puede contener varias unidades declaradas.
 */
public class NFuente extends Nodo implements IFuente
{
	NPaquete paquete;
	NUtiliza[] autz;
	NUnidad[] unidades;

	//////////////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo NFuente.
	 */
	public NFuente(NPaquete paquete, NUtiliza[] autz, Nodo[] n)
	{
		super(null); // rango null momentaneamente...
		this.paquete = paquete;
		this.autz = autz;

		unidades = new NUnidad[n.length];
		for (int i = 0; i < n.length; i++)
		{
			unidades[i] = (NUnidad) n[i];
		}

		Rango ini;
		if ( paquete != null )
		{
			ini = paquete.obtRango();
		}
		else if ( autz != null && autz.length > 0 )
		{
			ini = autz[autz.length -1].obtRango();
		}
		else
		{
			ini = unidades[0].obtRango();
		}

		Rango fin = unidades[0].obtRango();

		// ... listo mi rango:
		rango = new Rango(ini, fin);

	}
	
	//////////////////////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}

	//////////////////////////////////////////////////////////////////////
	public NPaquete obtPaquete()
	{
		return paquete;
	}
	
	//////////////////////////////////////////////////////////////////////
	public IFuente.IUtiliza[] obtUtilizas()
	{
		return autz;
	}

	//////////////////////////////////////////////////////////////////////
	public IUnidad[] obtUnidades()
	{
		return unidades;
	}

	///////////////////////////////////////////////////////////////
	public String getPackageName()
	{
		return paquete == null ? null : paquete.obtNPaquete().obtCadena();
	}
}