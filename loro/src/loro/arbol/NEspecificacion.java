package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

import loro.IUnidad;

//////////////////////////////////////////////////////////////////////
/**
 * Nodo que representa una especificacion en Loro.
 */
public class NEspecificacion extends NUnidad implements IUnidad.IEspecificacion
{
	TCadenaDoc descripcion;

	NDeclaracion[] pent = null;
	NDeclaracion[] psal = null;
	NDescripcion[] dent;
	NDescripcion[] dsal;
	NAfirmacion pre;
	NAfirmacion pos;

	/** La interface a la que pertenece esta operación (especificación)). */
	transient NInterface interf;


	///////////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo especificación.
	 */
	public NEspecificacion(
		Rango rango,
		TId tid, 
		TCadenaDoc tdesc,
		NDeclaracion[] ent, NDeclaracion[] sal,
		NDescripcion[] dent, NDescripcion[] dsal,
		NAfirmacion pre, NAfirmacion pos
	)
	{
		super(rango);

		ponId(tid);
		this.descripcion = tdesc;

		if ( ent == null )
		{
			ent = new NDeclaracion[0];
		}
		pent = ent;

		if ( sal == null )
		{
			sal = new NDeclaracion[0];
		}
		psal = sal;

		if ( dent == null )
		{
			dent = new NDescripcion[0];
		}
		this.dent = dent;

		if ( dsal == null )
		{
			dsal = new NDescripcion[0];
		}
		this.dsal = dsal;

		this.pre = pre;
		this.pos = pos;
	}

	///////////////////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}

	///////////////////////////////////////////////////////////////////
	/**
	 * Dice si esta especificacion es igual a otra.
	 * Dos especificaciones son iguales si sus
	 * nombre completos (con paquete) son iguales.
	 */
	public boolean equals(Object otro)
	{
		if ( !(otro instanceof NEspecificacion) )
			return false;

		NEspecificacion x = (NEspecificacion) otro;

		String mi = obtNombreCompletoCadena();
		String tu = x.obtNombreCompletoCadena();

		return mi.equals(tu);
	}

	///////////////////////////////////////////////////////////////////
	public NDescripcion[] obtDescripcionesEntrada()
	{
		return dent;
	}

	///////////////////////////////////////////////////////////////////
	public NDescripcion[] obtDescripcionesSalida()
	{
		return dsal;
	}

	///////////////////////////////////////////////////////////////////
	public NDeclaracion[] obtParametrosEntrada()
	{
		return pent;
	}

	///////////////////////////////////////////////////////////////////
	public NDeclaracion[] obtParametrosSalida()
	{
		return psal;
	}

	///////////////////////////////////////////////////////////////////
	public NAfirmacion obtPoscondicion()
	{
		return pos;
	}

	///////////////////////////////////////////////////////////////////
	public NAfirmacion obtPrecondicion()
	{
		return pre;
	}

	///////////////////////////////////////////////////////////////////
	public String toString()
	{
		StringBuffer sb = new StringBuffer("especificación " +obtNombreCompletoCadena()+ "(");
		for ( int i = 0; i < pent.length; i++ ) {
			if ( i > 0 )
				sb.append(",");
			sb.append(pent[i].toString());
		}
		sb.append(")");
		if ( psal.length > 0 ) {
			sb.append("->");
			for ( int i = 0; i < psal.length; i++ ) {
				if ( i > 0 )
					sb.append(",");
				sb.append(psal[i].toString());
			}
		}

		return sb.toString();
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la descripcion de esta especificacion.
	 */
	public String obtDescripcion()
	{
		return descripcion.obtCadena();
	}

	////////////////////////////////////////////////////////////////////
	/**
	 * Retorna "algoritmo para " +obtNombreCompletoCadena().
	 */
	public String getTypeString()
	{
		return "algoritmo para " +obtNombreCompletoCadena();
	}


	////////////////////////////////////////////////////////////////
	/**
	 * Pone la interface a la que pertenece esta especificación.
	 * Este método es llamado desde NInterface.
	 * (Note que tiene visibilidad "package.")
	 */
	void ponInterface(NInterface interf)
	{
		this.interf = interf;
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la interface (posiblemente null) a la que pertenece esta especificación.
	 */
	public NInterface obtInterface()
	{
		return interf;
	}

	////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre compuesto asociado a un nombre simple.
	 * Si no hay asociacion en mi propia lista de asociaciones,
	 * se intenta con la interface en caso que yo sea una operación.
	 */
	public String obtNombreCompuesto(String simple)
	{
		String ncomp = super.obtNombreCompuesto(simple);

		if ( ncomp == null && interf != null )
			ncomp = interf.obtNombreCompuesto(simple);
		
		return ncomp;
	}
}