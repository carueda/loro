package loro.tipo;

import loro.compilacion.ClaseNoEncontradaException;

///////////////////////////////////////////////////////////
public abstract class Tipo implements java.io.Serializable
{
	public static final Tipo entero   = TipoEntero.obtInstancia();
	public static final Tipo real     = TipoReal.obtInstancia();
	public static final Tipo caracter = TipoCaracter.obtInstancia();
	public static final Tipo booleano = TipoBooleano.obtInstancia();
	public static final Tipo cadena   = TipoCadena.obtInstancia();
	public static final Tipo nulo     = TipoNulo.obtInstancia();
	public static final Tipo unit     = TipoUnit.obtInstancia();

	//////////////////////////////////////////////////////////
	/**
	 * Obtiene tipo arreglo con respecto al tipo de elemento dado.
	 */
	public static Tipo arreglo(Tipo et)
	{
		return new TipoArreglo(et);
	}
	
	////////////////////////////////////////////
	/**
	 * Dice si este tipo es algoritmo.
	 */
	public boolean esAlgoritmo()
	{
		return false;
	}
	
	//////////////////////////////////////////////////////////
	public boolean esArreglo()
	{
		return false;
	}
	
	//////////////////////////////////////////////////////////
	public boolean esArreglo(Tipo et)
	{
		return false;
	}
	
	//////////////////////////////////////////////////////////
	public boolean esAsignable(Tipo t)
	throws ClaseNoEncontradaException
	{
		return igual(t);
	}

	//////////////////////////////////////////////////////////
	/**
	 * 2001-09-20
	 * Dice si a este tipo se le puede asignar el valor dado.
	 * Aqui se retorna true siempre ya que es la situacion
	 * mas general: notese que en tiempo de compilacion se
	 * validan la mayoria de los casos de asignacion (incluyendo
	 * paso de parametros). Pero los tipos "referencia" deben hacer
	 * una reimplementacion apropiada.
	 */
	public boolean esAsignableValor(Object v)
	{
		return true;
	}

	//////////////////////////////////////////////////////////
	public boolean esBasico()
	{
		return false;
	}
	
	//////////////////////////////////////////////////////////
	public boolean esBooleano()
	{
		return false;
	}
	
	//////////////////////////////////////////////////////////
	public boolean esCadena()
	{
		return false;
	}
	
	//////////////////////////////////////////////////////////
	public boolean esCaracter()
	{
		return false;
	}
	
	//////////////////////////////////////////////////////////
	public boolean esClase()
	{
		return false;
	}
	
	//////////////////////////////////////////////////////////
	/**
	 */
	public boolean esComparableCon(Tipo t)
	{
		return esNumerico() && t.esNumerico()
			   ||   esCaracter() && t.esCaracter()
			   ||   esCadena() && t.esCadena() ;
	}
	
	//////////////////////////////////////////////////////////
	/**
	 * Dice si este tipo es convertible al tipo dado.
	 * En esta clase esta operacion opera asi: 
	 * Si el tipo objetivo es cadena, se retorna true;
	 * si no, se retorna el resultado de invocar igual(t). 
	 */
	public boolean esConvertibleA(Tipo t)
	{
		return t.esCadena() || igual(t);
	}
	
	//////////////////////////////////////////////////////////
	public boolean esEntero()
	{
		return false;
	}
	
	//////////////////////////////////////////////////////////
	public boolean esEspecificacion()
	{
		return false;
	}

	//////////////////////////////////////////////////////////
	public boolean esInvocable()
	{
		return false;
	}
	
	//////////////////////////////////////////////////////////
	public boolean esNulo()
	{
		return false;
	}
	
	//////////////////////////////////////////////////////////
	public boolean esNumerico()
	{
		return false;
	}
	
	//////////////////////////////////////////////////////////
	public boolean esObjeto()
	{
		return false;
	}
	
	//////////////////////////////////////////////////////////
	public boolean esReal()
	{
		return false;
	}
	
	//////////////////////////////////////////////////////////
	public boolean esSumable()
	{
		return false;
	}
	
	//////////////////////////////////////////////////////////
	public boolean esUnit()
	{
		return false;
	}
	
	//////////////////////////////////////////////////////////
	/**
	 * Dice si este tipo es igual a uno dado.
	 */
	public abstract boolean igual(Tipo t);
	
	//////////////////////////////////////////////////////////
	public int obtNumArgs()
	{
		throw new RuntimeException("obtNumArgs() called!");
	}
	
	//////////////////////////////////////////////////////////
	/**
	 */
	public int obtNumSals()
	{
		throw new RuntimeException("obtNumSals() called!");
	}
	
	//////////////////////////////////////////////////////////
	public Tipo obtTipoArg(int i)
	{
		throw new RuntimeException("obtTipoArg() called!");
	}
	
	//////////////////////////////////////////////////////////
	public Tipo obtTipoElemento()
	{
		throw new RuntimeException("obtTipoElement() called!");
	}
	
	//////////////////////////////////////////////////////////
	/**
	 */
	public Tipo obtTipoSal(int i)
	{
		throw new RuntimeException("obtTipoSal() called!");
	}
	
	//////////////////////////////////////////////////////////
	/**
	 * Obtiene el valor por defecto para este tipo.
	 */
	abstract public Object obtValorDefecto();

	//////////////////////////////////////////////////////////
	/**
	 * Constructor basico.
	 */
	Tipo()
	{
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene un tipo clase asociado al nombre dado.
	 */
	public static TipoClase clase(String[] nombre)
	{
		return new TipoClase(nombre);
	}

	//////////////////////////////////////////////////////////
	/**
	 * Dice si este tipo corresponde a una interface.
	 * La implementacion aqui retorna false.
	 */
	public boolean esInterface()
	{
		return false;
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene un tipo especificacion asociado al nombre dado.
	 */
	public static TipoEspecificacion especificacion(String[] nombre)
	{
		return new TipoEspecificacion(nombre);
	}

	//////////////////////////////////////////////////////////////
	/**
	 * Obtiene un tipo especificacion para un operacion perteneciente
	 * a un interface.
	 *
	 * @param ti El tipo de la interface a la cual pertenecera esta operacion.
	 * @parm nombre Nombre de la operacion.
	 */
	public static TipoEspecificacion especificacion(TipoInterface ti, String nombre)
	{
		return new TipoEspecificacion(ti, nombre);
	}	

	//////////////////////////////////////////////////////////
	/**
	 * Dice si este tipo clase implementa el tipo interface dado.
	 *
	 * @param t   El tipo a revisar.
	 */
	public boolean implementa(Tipo t)
	throws ClaseNoEncontradaException
	{
		return false;
	}

	//////////////////////////////////////////////////////////////////////
	/**
	 * Obtiene un tipo interface asociado al nombre dado.
	 */
	public static TipoInterface interface_(String[] nombre)
	{
		return new TipoInterface(nombre);
	}
}