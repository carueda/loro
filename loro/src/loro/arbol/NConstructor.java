package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;

import loro.Rango;

import loro.compilacion.ClaseNoEncontradaException;

////////////////////////////////////////////////////////////////
/**
 * Constructor de objetos de una clase.
 */
public class NConstructor extends Nodo
{
	NDeclaracion[] pent = null;
	NDescripcion[] dent;
	NAfirmacion pre;
	NAfirmacion pos;

	TCadenaDoc estrategia;
	Nodo[] acciones;

	/** La clase a la que pertenece este constructor. */
	transient NClase clase;

	/** Es por defecto (no se indico ninguno en la clase)? */
	boolean esPorDefecto;

	////////////////////////////////////////////////////////////////
	/**
	 * Constructor exclusivo para crear el constructor por defecto
	 * para una cierta clase.
	 */
	NConstructor()
	{
		this(
			null,					// rango
			null, 					// descripcion
			new NDeclaracion[0], 	// NDeclaracion[] ent
			new NDescripcion[0],	// NDescripcion[] dent
			null, 					// NExpresion pre
			null,					// NExpresion pos
			null, 					// estrategia
			new Nodo[0]				// Nodo[] acciones
		);

		esPorDefecto = true;
	}

	////////////////////////////////////////////////////////////////
	/**
	 * Dice si los tipos dados por las declaraciones
	 * son compatibles para usar este constructor.
	 */
	public boolean aceptaArgumentos(NDeclaracion[] args)
	{
		if ( pent.length != args.length )
			return false;

		for ( int i = 0; i < pent.length; i++ )
		{
			if ( !pent[i].obtTipo().igual(args[i].obtTipo()) )
				return false;
		}

		return true;
	}
	////////////////////////////////////////////////////////////////
	/**
	 * Dice si los argumentos dados son compatibles
	 * para usar este constructor.
	 */
	public boolean aceptaArgumentos(NExpresion[] args)
	throws ClaseNoEncontradaException
	{
		if ( pent.length != args.length )
			return false;

		for ( int i = 0; i < pent.length; i++ )
		{
			if ( !pent[i].obtTipo().esAsignable(args[i].obtTipo()) )
				return false;
		}

		return true;
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
	 * Dice si este constructor se creo por defecto.
	 */
	public boolean esPorDefecto()
	{
		return esPorDefecto;
	}
	////////////////////////////////////////////////////////////////
	public Nodo[] obtAcciones()
	{
		return acciones;
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Obtiene la clase a la que pertenece este constructor.
	 */
	public NClase obtClase()
	{
		return clase;
	}

	////////////////////////////////////////////////////////////////
	public NDescripcion[] obtDescripcionesEntrada()
	{
		return dent;
	}
	////////////////////////////////////////////////////////////////
	public NDeclaracion[] obtParametrosEntrada()
	{
		return pent;
	}
	////////////////////////////////////////////////////////////////
	public NAfirmacion obtPoscondicion()
	{
		return pos;
	}
	////////////////////////////////////////////////////////////////
	public NAfirmacion obtPrecondicion()
	{
		return pre;
	}
	
	////////////////////////////////////////////////////////////////
	/**
	 * Pone la clase a la que pertenece este constructor.
	 * Este método es llamado desde NClase.
	 * (Note que tiene visibilidad "package.")
	 */
	void ponClase(NClase clase)
	{
		this.clase = clase;
	}

	TCadenaDoc descripcion;

	////////////////////////////////////////////////////////////////
	/**
	 * Crea un nodo constructor.
	 */
	public NConstructor(
		Rango rango,
		TCadenaDoc desc,
		NDeclaracion[] ent,
		NDescripcion[] dent,
		NAfirmacion pre,
		NAfirmacion pos,
		TCadenaDoc e,
		Nodo[] a
	)
	{
		super(rango);

		this.descripcion = desc;

		if ( ent == null )
		{
			ent = new NDeclaracion[0];
		}
		pent = ent;

		if ( dent == null )
		{
			dent = new NDescripcion[0];
		}
		this.dent = dent;

		this.pre = pre;
		this.pos = pos;

		this.estrategia = e;
		this.acciones = a;

		esPorDefecto = false;
	}

	////////////////////////////////////////////////////////////////
	public String obtDescripcion()
	{
		return descripcion.obtCadena();
	}
}