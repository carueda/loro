package loro.arbol;

import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.Rango;
import loro.util.Util;

import loro.IUnidad;

//////////////////////////////////////////////////////////////////////
/**
 * Nodo que representa un algoritmo Loro.
 */
public class NAlgoritmo extends NUnidad implements IUnidad.IAlgoritmo
{
	/** Especificacion resuelta por este algoritmo. */
	TNombre espec;

	/** Descripcion de la estrategia seguida. */
	TCadenaDoc estrategia;

	/** Los parametros formales de entrada. */
	NDeclaracion[] pent;

	/** Los parametros formales de salida. */
	NDeclaracion[] psal;

	/** Las acciones del algoritmo. */
	Nodo[] acciones;

	/** Lenguaje en que se implementa este algoritmo. */
	TCadena impLenguaje;

	/** Informacion adicional para el implementador. Puede ser null. */
	TCadena impInfo;

	/** 
	 * Nombre completo de la especificacion.
	 * El TNombre espec puede ser corto: en la fase de compilacion 
	 * este campo sera obtenido.
	 */
	String[] nombreEspec;


	/** La clase a la que pertenece este algoritmo (método). */
	transient NClase clase;

	///////////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo Algoritmo.
	 */
	public NAlgoritmo(
		Rango rango,
		TId tid,
		TNombre tespec,
		TCadenaDoc testrategia,
		NDeclaracion[] ent,
		NDeclaracion[] sal,
		Nodo[] a,
		TCadena timpLenguaje,
		TCadena timpInfo
	)
	{
		super(rango);

		ponId(tid);
		this.espec = tespec;
		this.estrategia = testrategia;

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

		acciones = a;

		this.impLenguaje = timpLenguaje;
		this.impInfo = timpInfo;
	}

	///////////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}

	///////////////////////////////////////////////////////////
	/**
	 * Dice si este algoritmo esta implementado en el lenguaje
	 * dado.
	 * Esto es cierto si:
	 *  - Este algoritmo es regular y leng es "loro" (en este
	 *      caso invoque mejor implementadoEnLoro() por eficiencia).
	 *  - este algoritmo esta implementado en otro lenguaje
	 *      y este coincide con leng.
	 * Las comparaciones ignoran distincion por mayusculas/minusculas.
	 */
	public boolean implementadoEnLenguaje(String leng)
	{
		if ( impLenguaje == null )
			return "loro".equalsIgnoreCase(leng);
		else
			return obtLenguajeImplementacion().equalsIgnoreCase(leng);
	}
	
	///////////////////////////////////////////////////////////
	/**
	 * Dice si este algoritmo esta implementado en Loro, es decir,
	 * si NO tiene la indicacion de ser implementado en otro lenguaje.
	 */
	public boolean implementadoEnLoro()
	{
		return impLenguaje == null;
	}
	///////////////////////////////////////////////////////////
	public Nodo[] obtAcciones()
	{
		return acciones;
	}


	///////////////////////////////////////////////////////////
	/**
	 * Obtiene la informacion adicional de implementacion de este algoritmo.
	 */
	public String obtInfoImplementacion()
	{
		return impInfo != null ? impInfo.obtCadena() : null;
	}
	
	///////////////////////////////////////////////////////////
	/**
	 * Obtiene el lenguaje de implementacion de este algoritmo.
	 */
	public String obtLenguajeImplementacion()
	{
		return impLenguaje.obtCadena();
	}

	///////////////////////////////////////////////////////////
	public NDeclaracion[] obtParametrosEntrada()
	{
		return pent;
	}
	
	///////////////////////////////////////////////////////////
	public NDeclaracion[] obtParametrosSalida()
	{
		return psal;
	}

	///////////////////////////////////////////////////////////
	public String getPrototype()
	{
		StringBuffer sb = new StringBuffer(obtNombreSimpleCadena()+ "(");
		for ( int i = 0; i < pent.length; i++ )
		{
			if ( i > 0 )
				sb.append(",");
			sb.append(pent[i].toString());
		}
		sb.append(")");
		if ( psal.length > 0 )
		{
			sb.append("->");
			for ( int i = 0; i < psal.length; i++ )
			{ 
				if ( i > 0 )
					sb.append(",");
				sb.append(psal[i].toString());
			}
		}

		return sb.toString();
	}

	///////////////////////////////////////////////////////////
	/**
	 */
	public String toString()
	{
		if ( clase == null )
		{
			String pkg = obtNombrePaquete();
			if ( pkg == null )
				pkg = "";
			else
				pkg += "::";
			
			return "algoritmo " +pkg + getPrototype();
		}
		else
		{
			return
				"método " +clase.obtNombreCompletoCadena()+ "." 
				+getPrototype()
			;
		}
	}

	//////////////////////////////////////////////////
	public boolean esEjecutable()
	{
		return true;
	}

	///////////////////////////////////////////////////////////
	/**
	 * Dice si este algoritmo es para la especificacion dada.
	 */
	public boolean esParaEspecificacion(String[] nombreEspec)
	{
		if ( nombreEspec == null )
		{
			return false;
		}

		if ( this.nombreEspec.length != nombreEspec.length )
		{
			return false;
		}

		for (int i = 0; i < nombreEspec.length; i++)
		{
			if ( ! this.nombreEspec[i].equals(nombreEspec[i]) )
			{
				return false;
			}
		}

		return true;
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la estrategia de este algoritmo.
	 * Note que puede ser null si esta implementado en otro lenguaje.
	 */
	public TCadenaDoc obtEstrategia()
	{
		return estrategia;
	}

	///////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre completo de la especificacion satisfecha
	 * por este algoritmo.
	 */
	public String[] obtNombreEspecificacion()
	{
		return nombreEspec;
	}

	///////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre completo de la especificacion satisfecha
	 * por este algoritmo.
	 */
	public String getSpecificationName()
	{
		return Util.obtStringRuta(nombreEspec);
	}

	////////////////////////////////////////////////////////////////////
	/**
	 * Retorna "algoritmo para " +getSpecificationName().
	 */
	public String getTypeString()
	{
		return "algoritmo para " +getSpecificationName();
	}

	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre simple de esta unidad.
	 * Sobreescrito pues es posible no indicar el nombre del algoritmo
	 * explicitamente, en cuyo cado se retorna en nombre simple
	 * de la especificacion.
	 */
	public String obtNombreSimpleCadena()
	{
		if ( id != null )
		{
			return id.obtId();
		}
		else
		{
			// tome de la especificacion:
			TId[] ids = espec.obtIds();
			return ids[ids.length -1].obtId();
		}
	}

	///////////////////////////////////////////////////////////
	/**
	 * Obtiene el tnombre de la especificacion dada
	 * para este algoritmo.
	 */
	public TNombre obtTNombreEspecificacion()
	{
		return espec;
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Pone el nombre completo de mi especificacion.
	 */
	public void ponNombreEspecificacion(String[] nombreEspec)
	{
		this.nombreEspec = nombreEspec;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Pone la clase a la que pertenece este algoritmo.
	 * Este método es llamado desde NClase.
	 * (Note que tiene visibilidad "package.")
	 */
	void ponClase(NClase clase)
	{
		this.clase = clase;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la clase (posiblemente null) a la que pertenece este algoritmo.
	 */
	public NClase obtClase()
	{
		return clase;
	}

	////////////////////////////////////////////////////////////////////
	/**
	 * Si soy método, retorno el fuente asociado a mi clase.
	 */
	public String obtNombreFuente()
	{
		return clase == null ? super.obtNombreFuente() : clase.obtNombreFuente();
	}

	////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre compuesto asociado a un nombre simple.
	 * Si no hay asociacion en mi propia lista de asociaciones,
	 * se intenta con la clase en caso que yo sea un método.
	 */
	public String obtNombreCompuesto(String simple)
	{
		String ncomp = super.obtNombreCompuesto(simple);

		if ( ncomp == null && clase != null )
			ncomp = clase.obtNombreCompuesto(simple);
		
		return ncomp;
	}

}