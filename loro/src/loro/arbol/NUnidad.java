package loro.arbol;

import loro.Rango;
import loro.util.Util;
import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.IUnidad;

import java.util.Hashtable;


////////////////////////////////////////////////////////////////////
/**
 * La superclase de todos las "unidades" de Loro.
 *
 * Estos elementos quedan asociados al nombre del archivo fuente de
 * donde se compilaron, asi como al paquete indicado alli.
 *
 * Incluye el mecanismo para resolver nombres simples que
 * aparezcan dentro del código de la unidad en nombres compuestos
 * (con paquete) para la fase de ejecución.
 */
public abstract class NUnidad extends Nodo implements IUnidad
{
	/** 
	 * Versión de Loro al compilarse esta unidad.
	 * El ManejadorUnidades maneja este dato en lectura/escritura de unidades.
	 * @see loro.util.ManejadorUnidades
	 */
	transient protected String version;

	/** 
	 * Nombre del fuente de donde se compiló esta unidad.
	 * El ManejadorUnidades maneja este dato en lectura/escritura de unidades.
	 * @see loro.util.ManejadorUnidades
	 */
	transient protected String nombreFuente;
	
	/** Current time when this unit was compiled. */
	transient protected long millis;

	/** 
	 * Código fuente que produjo esta unidad.
	 */
	protected String src;

	
	/** Paquete al que pertenece esta unidad. */
	protected String[] paquete;

	/**
	 * Lista de asociaciones de nombres simple/compuesto necesaria
	 * para resolver nombres simples dentro del codigo
	 * de esta unidad en los nombres compuestos con paquete.
	 */
	protected Hashtable sc;


	/** Nombre de esta unidad. */
	protected TId id;


	////////////////////////////////////////////////////////////////////
	/**
	 * Inicia la lista de asociaciones de nombres simple/compuesto
	 * en vacio.
	 */
	public void iniciarAsociacionesSimpleCompuesto()
	{
		if ( sc != null )
		{
			sc.clear();
		}
		else
		{
			sc = new Hashtable();
		}
	}

	////////////////////////////////////////////////////////////////////
	/**
	 * Retorna el nombre del paquete. Por ejemplo, "loroI::sistema".
	 * nulo si esta unidad no tiene un paquete explicito asociado.
	 */
	public String obtNombrePaquete()
	{
		if ( paquete != null )
		{
			return Util.obtStringRuta(paquete, "::");
		}
		else
		{
			return null;
		}
	}

	////////////////////////////////////////////////////////////////////
	public String obtNombreCompletoCadena()
	{
		return obtNombreCompletoCadena("::");
	}

	////////////////////////////////////////////////////////////////////
	public String obtNombreCompletoCadena(String sep)
	{
		String pre = "";
		if ( paquete != null )
		{
			pre = Util.obtStringRuta(paquete, sep)
				+ sep;
		}
		return pre + obtNombreSimpleCadena();
	}

	////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre compuesto asociado a un nombre simple.
	 * Si no existe una asociacion para el nombre simple, se
	 * retorna null.
	 */
	public String obtNombreCompuesto(String simple)
	{
		if ( sc != null )
		{
			return (String) sc.get(simple);
		}

		return null;
	}

	////////////////////////////////////////////////////////////////////
	public String obtNombreFuente()
	{
		return nombreFuente;
	}

	////////////////////////////////////////////////////////////////////
	public String obtVersion()
	{
		return version;
	}

	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene el nombre simple de esta unidad.
	 * Este se toma directamente del nodo id (NId) indicado
	 * por lo que se asume que es no null.
	 * En particular, NAlgoritmo sobreescribe este metodo
	 * pues es posible no indicar el nombre del algoritmo
	 * explicitamente.
	 */
	public String obtNombreSimpleCadena()
	{
		return id.obtId();
	}

	////////////////////////////////////////////////////////////////////
	/**
	 * Pone una asociacion nombre-simple/nombre-compuesto a
	 * esta unidad de compilacion.
	 * Si ya existe una asociacion para el nombre simple, se
	 * reemplaza.
	 */
	public void ponNombreCompuesto(String simple, String compuesto)
	{
		if ( sc == null )
		{
			iniciarAsociacionesSimpleCompuesto();
		}

		sc.put(simple, compuesto);
	}

	////////////////////////////////////////////////////////////////////
	public void ponNombreFuente(String nombreFuente)
	{
		this.nombreFuente = nombreFuente;
	}

	////////////////////////////////////////////////////////////////////
	public void ponVersion(String version)
	{
		this.version = version;
	}

	////////////////////////////////////////////////////////////////////
	public long getMillis()
	{
		return millis;
	}

	////////////////////////////////////////////////////////////////////
	public void setMillis(long millis)
	{
		this.millis = millis;
	}


	//////////////////////////////////////////////////
	public boolean esEjecutable()
	{
		return false;
	}

	////////////////////////////////////////////////////////////////////
	public TId obtId()
	{
		return id;
	}

	////////////////////////////////////////////////////////////////////
	public String[] obtNombreCompleto()
	{
		String[] res;
		if ( paquete != null )
		{
			res = new String[paquete.length + 1];
			for ( int k = 0; k < paquete.length; k++ )
			{
				res[k] = paquete[k];
			}
		}
		else
		{
			res = new String[1];
		}

		res[res.length -1] = obtNombreSimpleCadena();

		return res;
	}

	////////////////////////////////////////////////////////////////////
	public String[] obtPaquete()
	{
		return paquete;
	}

	////////////////////////////////////////////////////////////////////
	public void ponId(TId id)
	{
		this.id = id;
	}

	////////////////////////////////////////////////////////////////////
	public void ponPaquete(String[] paquete)
	{
		this.paquete = paquete;
	}


	////////////////////////////////////////////////////////////////////
	/**
	 * Crea la informacion basica de una unidad de compilacion.
	 */
	protected NUnidad(Rango rango)
	{
		super(rango);
	}

	////////////////////////////////////////////////////////////////////
	/**
	 * Pone el código fuente para esta unidad.
	 */
	public void setSourceCode(String src)
	{
		this.src = src;
	}

	////////////////////////////////////////////////////////////////////
	/**
	 * Retorna el código fuente asociado a esta unidad.
	 */
	public String getSourceCode()
	{
		return src;
	}
}