package loro.arbol;

import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;
import loro.tipo.Tipo;
import loro.Rango;

import loro.compilacion.ClaseNoEncontradaException;

import loro.IUnidad;

////////////////////////////////////////////////////////////////
/**
 * Representa la definición de una clase Loro.
 */
public class NClase extends NUnidad implements IUnidad.IClase
{
	/** 
	 * Es un objeto lo que se define?
	 * En Loro está contemplado definir un objeto directamente (aunque
	 * no está implementado) y para ello se sigue una sintaxis similar a la de 
	 * clase. Este indicador dice si se trata de un objeto concretamente. 
	 */
	boolean defineObjeto;
	
	/** Clase que extiende. */
	TNombre extiende;

	/** Interfaces declaradas que implementa esta clase. */
	TNombre[] interfaces;

	/** Descripción de la clase. */
	TCadenaDoc descripcion;
	
	/** Atributos de la clase. */
	NDeclDesc[] atrs;

	/** Constructores de la clase. */
	NConstructor[] pcons;

	/** Métodos. */
	NAlgoritmo[] metodos;

	/**
	 * Nombres de los atributos.
	 * Calculados por demanda.
	 * @see obtAtributos()
	 */
	transient String[] nomatrs;


	///////////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo clase.
	 * Si acs es null o su longitud es 0, se crea automaticamente
	 * un constructor por defecto. Este constructor se encarga de
	 * ejecutar las inicializaciones para los atributos de la clase,
	 * que a su vez toman sus valores correspondientes por defecto,
	 * o bien toman los valores de sus expresiones de inicializacion
	 * en caso que se hayan indicado.
	 */
	public NClase(
		Rango rango,
		boolean defineObjeto,
		TId id,
		TNombre extiende, 
		TNombre[] interfaces,
		TCadenaDoc desc,
		NDeclDesc[] atrs, 
		NConstructor[] acs,
		NAlgoritmo[] metodos
	)
	{
		super(rango);

		this.defineObjeto = defineObjeto;
		
		ponId(id);
		this.extiende = extiende;
		this.interfaces = interfaces;
		this.descripcion = desc;

		if ( atrs == null )
		{
			atrs = new NDeclDesc[0];
		}

		this.atrs = atrs;

		/////////////////////////////////////////////////////
		// Si no se indican constructores explicitos, cree
		// el constructor por defecto. Mas abajo se les dice
		// a todos los constructores a que clase pertenecen.
		if ( acs == null || acs.length == 0 )
		{
			acs = new NConstructor[1];
			acs[0] = new NConstructor();
		}

		pcons = acs;
		this.metodos = metodos;

		notificar();
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Acepta al visitante.
	 */
	public void aceptar(IVisitante v)
	throws VisitanteException
	{
		v.visitar(this);
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Dice si esta definición corresponde a objeto.
	 */
	public boolean defineObjeto()
	{
		return defineObjeto;
	}

	////////////////////////////////////////////////////////////////
	public boolean esAtributoConstante(String nombreAtributo)
	{
		for ( int i = 0; i < atrs.length; i++ )
		{
			NDeclDesc d = atrs[i];
			if ( d.obtId().obtId().equals(nombreAtributo) )
			{
				return d.esConstante();
			}
		}

		return false;
	}
	
	///////////////////////////////////////////////////////////////
	/**
	 * Obtiene la lista de los nombres de los atributos.
	 */
	public String[] obtAtributos()
	{
		if ( nomatrs == null )
		{
			nomatrs = new String[atrs.length];
			for (int i = 0; i < atrs.length; i++)
			{
				nomatrs[i] = atrs[i].obtId().obtId();
			}
		}

		return nomatrs;
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el constructor correspondiente para
	 * los tipos de los argumentos dados buscando
	 * hasta el límite.
	 * null si no hay uno compatible.
	 */
	public NConstructor obtConstructor(NDeclaracion[] args, int lim)
	{
		for ( int i = 0; i <= lim; i++ )
		{
			if ( pcons[i].aceptaArgumentos(args) )
				return pcons[i];
		}

		return null;
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el constructor correspondiente para
	 * los tipos de los argumentos dados.
	 * null si no hay uno compatible.
	 */
	public NConstructor obtConstructor(NExpresion[] args)
	throws ClaseNoEncontradaException
	{
		//
		// Note que la búsqueda del constructor apropiado para los
		// argumentos es en el orden definido para la clase.
		// Esto debería revisarse para buscar mejor el constructor
		// más particular según los tipos.
		//

		for ( int i = 0; i < pcons.length; i++ )
		{
			if ( pcons[i].aceptaArgumentos(args) )
				return pcons[i];
		}

		return null;
	}

	////////////////////////////////////////////////////////////////
	public TNombre[] obtInterfacesDeclaradas()
	{
		return interfaces;
	}

	////////////////////////////////////////////////////////////////
	public NConstructor[] obtConstructores()
	{
		return pcons;
	}

	////////////////////////////////////////////////////////////////
	public NAlgoritmo[] obtMetodosDeclarados()
	{
		return metodos;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene el constructor que se crea automaticamente cuando no
	 * se indico ninguno explicitamente.
	 * Retorna null si al crear esta clase si se indicaron
	 * constructores explicitos.
	 */
	public NConstructor obtConstructorPorDefecto()
	{
		if ( pcons.length == 1 && pcons[0].esPorDefecto() )
		{
			return pcons[0];
		}

		return null;
	}


	////////////////////////////////////////////////////////////////
	public NDeclDesc[] obtParametrosEntrada()
	{
		return atrs;
	}
	
	////////////////////////////////////////////////////////////////
	public Tipo obtTipoAtributo(String nombreAtributo)
	{
		for ( int i = 0; i < atrs.length; i++ )
		{
			NDeclDesc d = atrs[i];
			if ( d.obtId().obtId().equals(nombreAtributo) )
			{
				return d.obtTipo();
			}
		}

		return null;
	}
	
	////////////////////////////////////////////////////////////////
	public String toString()
	{
		return "clase " +obtNombreCompletoCadena();
	}


	////////////////////////////////////////////////////////////////
	public String obtDescripcion()
	{
		return descripcion.obtCadena();
	}

	////////////////////////////////////////////////////////////////
	public TNombre obtNombreExtiende()
	{
		return extiende;
	}

	////////////////////////////////////////////////////////////////////
	/**
	 * Retorna obtNombreCompletoCadena().
	 */
	public String getTypeString()
	{
		return obtNombreCompletoCadena();
	}

	///////////////////////////////////////////////////////////////////
	/**
	 * Notifica a mis constructores/métodos a qué clase pertenecen.
	 */
	protected void notificar()
	{
		for (int i = 0; i < pcons.length; i++)
		{
			pcons[i].ponClase(this);
		}
		
		for (int i = 0; i < metodos.length; i++)
		{
			metodos[i].ponClase(this);
		}
	}

	///////////////////////////////////////////////////////////////////
	/**
	 * Definido para actualizar referencias "transient" asociadas.
	 * Actualmente notificar().
	 */
	private void readObject(java.io.ObjectInputStream s)
	throws java.io.IOException, ClassNotFoundException
	{
		s.defaultReadObject();
		notificar();
	}
}