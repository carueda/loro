package loro.arbol;

import loro.tipo.Tipo;
import loro.Rango;

////////////////////////////////////////////////////////////////
/**
 * Clase de base para declaraciones.
 * (Factoriza por ahora algunas operaciones de NDeclaracion y NDeclDesc
 * que podrian fusionarse posteriormente.)
 */
abstract class NDeclaracionBase extends Nodo
{
	/** El nodo-tipo de esta declaracion. */
	NTipo ntipo;



	/** Es constante? */
	boolean esConstante;

	/** Expresión para cuando esta declaracion tenga inicialización. */
	NExpresion e;

	////////////////////////////////////////////
	/**
	 * Dice si esta declaración tiene expresión constante.
	 */
	public boolean esConstante()
	{
		return esConstante;
	}
	////////////////////////////////////////////
	/**
	 * Obtiene la expresion de inicializacion.
	 * Puede ser null.
	 */
	public NExpresion obtExpresion()
	{
		return e;
	}

	////////////////////////////////////////////
	/**
	 * Obtiene el tipo real de esta declaracion.
	 * Este es el mismo obtenido con ntipo.obtTipo().
	 */
	public Tipo obtTipo()
	{
		return ntipo.obtTipo();
	}


	
	////////////////////////////////////////////////////////////////
	/**
	 * Dice si esta declaración tiene expresión
	 * para valor inicial.
	 */
	public boolean tieneInicializacion()
	{
		return e != null;
	}

	/////////////////////////////////////////////////////////////////////////////////
	/**
	 * Crea lo basico de un nodo declaración.
	 */
	protected NDeclaracionBase(Rango rango, NTipo t, boolean cte, NExpresion e)
	{
		super(rango);
		this.ntipo = t;
		this.esConstante = cte;
		this.e = e;
	}

	////////////////////////////////////////////
	/**
	 * Obtiene el nodo-tipo indicado para esta declaracion.
	 */
	public NTipo obtNTipo()
	{
		return ntipo;
	}
}