package loro.arbol;


import loro.visitante.IVisitante;
import loro.visitante.VisitanteException;


import loro.Rango;

/**
 * Raiz de todas las expresiones binarias.
 */
public abstract class NExprBin extends NExpresion
{
	/** La expresión a la izquierda. */
	NExpresion e;

	/** La expresión a la derecha. */
	NExpresion f;

	/** El operador. */
	String op;

	//////////////////////////////////////////////////////////////////
	/**
	 * Crea una expresion binaria.
	 * El rango se calcula con base en los de las expresiones
	 * (porque el operador va en infijo).
	 */
	public NExprBin(NExpresion e, NExpresion f, String op)
	{
		super(new Rango(e.obtRango(), f.obtRango()));
		this.e = e;
		this.f = f;

		this.op = op;
	}
	public NExpresion obtExpresionDer()
	{
		return f;
	}
	public NExpresion obtExpresionIzq()
	{
		return e;
	}
	public String obtOperador()
	{
		return op;
	}

	//////////////////////////////////////////////////////////////////
	/**
	 * Crea una expresion binaria con rango dado.
	 * En algunos casos, este rango tiene que ser dado pues no se puede
	 * obtener solamente de las dos expresiones en juego (o sea, cuando
	 * el operador no van en infijo).
	 */
	public NExprBin(Rango rango, NExpresion e, NExpresion f, String op)
	{
		super(rango);
		this.e = e;
		this.f = f;

		this.op = op;
	}
}